package diseaseDiscovery.services

import diseaseDiscovery.domain.com.Disease
import diseaseDiscovery.domain.com.Symptom
import diseaseDiscovery.domain.com.SymptomDisease

import weka.core.Instances
import weka.core.Attribute
import weka.core.FastVector
import weka.classifiers.bayes.NaiveBayesUpdateable
import weka.core.Instance
import weka.core.Utils
import weka.classifiers.Evaluation
import weka.core.SerializationHelper
import weka.gui.graphvisualizer.GraphVisualizer
import weka.core.SerializationHelper

class WekaService {
	def MAX_INSTANCES = 5 // TODO: find useful number
	def NOISE = 10 // TODO: find % of noise to use
//	def HEAP_SIZE = 2048
	
	def createModel(graph = false){
		println "Creating WEKA model."
		
		if(!goodToGo()) {
			println "Dataset not usable. Aborting."
			return null
		}
		
		def data = defineTrainingDataset()
		def model = getModel(data)
		
		println "WEKA model created"
		
		saveModel(model)

		return model
	}
	
	def makePrediction(symptoms){
		println "Making prediction..."
		def model = SerializationHelper.read("bayes.model")
		if(!model) return false
		println "\t>Model loaded"
		def data = getPredictionDataset(symptoms)
		
		data.setClassIndex(0)
		
		println "\t>Data loaded"
		println data.numInstances()
		println data
		def prediction = new Instances(data)
		def label = model.classifyInstance(data.instance(0))
		prediction.instance(0).setClassValue(label)	
		
		println prediction.toString()
		println "$label -> ${data.classAttribute().value((int) label)}"
		println "Prediction finished."
		
		return data.classAttribute().value((int) label)
		
//		def pred = model.classifyInstance(data.instance(0));
//		def dist = model.distributionForInstance(data.instance(0));
//		println data.classAttribute().value((int) pred)
//		println "--"
//		println Utils.arrayToString(dist)
		
	}
	
	
	private initializeWeka(){
//		def options = Utils.splitOptions("-Xmx${HEAP_SIZE}m")
		
		def model = new NaiveBayesUpdateable()
//		model.setOptions(options)
		
		return model
	}
	
	private defineTrainingDataset(){
		println "  >Fetching data..."
		
		def header = getHeader()
		def dataset =  new Instances("DISEASES", header, 0)
		
		getTrainingInstances(dataset, MAX_INSTANCES).each{
			dataset.add(it)
		}
		
		dataset.setClassIndex(0)
		dataset.randomize(dataset.getRandomNumberGenerator(99999))
		
		println "  >Data fetched."
		return dataset
	}
	
	private getHeader(){
		def attributes = new FastVector()
		
		def diseaseLabels = new FastVector()
		FastVector symptomLabels = new FastVector()
		symptomLabels.addElement("y")
		symptomLabels.addElement("n")
		
		Disease.getAll().each{ disease ->
			diseaseLabels.addElement(disease.id as String)
		}
		attributes.addElement(new Attribute("disease_id", diseaseLabels))
		
		Symptom.getAll().each{ symptom ->
			attributes.addElement(new Attribute("s${symptom.id}", symptomLabels))
		}
		
		println "\t>Header built."
		return attributes
	}
	
	private getTrainingInstances(dataset, maxInstances = 0){
		maxInstances = maxInstances ?: Disease.count()
		def actualInstances =  maxInstances*0.90
		def falseInstances = maxInstances*0.10
		
		def instances = []

		def set = Disease.executeQuery('from Disease order by rand()', [max: actualInstances])
		println "\t> Set of diseases fetched"
		def j=1
		set.each{ disease -> // Get random number of rows
			def values = new double[dataset.numAttributes()]
			def symptoms = disease.symptoms()

			values[0] = dataset.attribute("disease_id").indexOfValue(disease.id as String)
			def i = 1
			Symptom.getAll().each{ symptom -> 
				if(symptom in symptoms){
					values[i] = new Random().nextInt(101) > NOISE ? dataset.attribute("s${symptom.id}").indexOfValue("y") : dataset.attribute("s${symptom.id}").indexOfValue("n")
				} else {
					values[i] = new Random().nextInt(101) > NOISE ? dataset.attribute("s${symptom.id}").indexOfValue("n") : dataset.attribute("s${symptom.id}").indexOfValue("y")
				}
				
				i++
			}	
			j++
			println "disease $disease done - $j of $actualInstances"
			instances << new Instance(1.0, values)		
		}
		
		// TODO: Add random instances
		
		println "\t>Dataset body created."
		return instances
	}
	
	private getPredictionDataset(symptoms){
		def data = getHeader()
		def dataset =  new Instances("DISEASE PREDICTION", header, 0)
		 
		def values = new double[dataset.numAttributes()]
		values[0] = Instance.missingValue()
		def i = 1
		Symptom.getAll().each{ symptom ->
			if(symptom in symptoms){
				values[i] = dataset.attribute("s${symptom.id}").indexOfValue("y")
			} else {
				values[i] = dataset.attribute("s${symptom.id}").indexOfValue("n")
			}
			
			i++
		}
		
		dataset.add(new Instance(1.0, values))
		
		return dataset
	}
	
	private getModel(data){
		println "  >Building model..."
		
		def folds = data.numInstances() < 10 ? data.numInstances() : 10
		
		def bestModel
		def bestEvaluation
		def model
		def evaluator
		def randData = new Instances(data)
		randData.randomize(data.getRandomNumberGenerator(99999))	
		
		for(def i = 0; i < folds; i++){
			def trainSet = randData.trainCV(folds, i)
			def validationSet = randData.testCV(folds, i)
			trainSet.setClassIndex(0)
			validationSet.setClassIndex(0)
			
			model = initializeWeka()
//			model.buildClassifier(trainSet)
			
			
			
			def header = getHeader()
			def dataset =  new Instances("DISEASES", header, 0)
			dataset.setClassIndex(0)
			model.buildClassifier(dataset)
			println "sssssss"
			def current
			def currentInstance = 0
			while ((current = trainSet.instance(currentInstance))){
				model.updateClassifier(current)
				currentInstance++
				println currentInstance
			}
			println "aaa"
			
				
			evaluator = new Evaluation(trainSet)
			evaluator.evaluateModel(model, validationSet)
			
			if(bestEvaluation?.avgCost() > evaluator.avgCost() || (!bestEvaluation && !bestModel)){
				bestModel = model
				bestEvaluation = evaluator
			}
		}

		println "  >Model built."
//		println bestModel.toString()
		return bestModel
	}
	
	private saveModel(model){
		SerializationHelper.write("bayes.model", model)
	}
	
	private goodToGo(){
		return Disease.count() > 1 && Symptom.count() > 1 && SymptomDisease.count() > 1
	}
}
