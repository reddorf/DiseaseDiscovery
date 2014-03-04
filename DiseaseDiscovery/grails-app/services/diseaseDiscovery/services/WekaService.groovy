package diseaseDiscovery.services

import diseaseDiscovery.domain.com.Disease
import diseaseDiscovery.domain.com.Symptom
import diseaseDiscovery.domain.com.SymptomDisease

import weka.core.Instances
import weka.core.Attribute
import weka.core.FastVector
import weka.classifiers.bayes.NaiveBayes
import weka.core.Instance
import weka.core.Utils
import weka.classifiers.Evaluation
import weka.core.SerializationHelper
import weka.gui.graphvisualizer.GraphVisualizer

class WekaService {

	def createModel(graph = false){
		println "Creating WEKA model."
		
		if(!goodToGo()) {
			println "Dataset not usable. Aborting."
			return null
		}
		
		def data = defineDataset()
		def model = getModel(data)
		
		println "WEKA model created"
		
		saveModel(model)

		return model
	}
	
	private initializeWeka(){
//		String[] options = Utils.splitOptions("-c 1")
		
		def model = new NaiveBayes()
//		model.setOptions(options)
		
		return model
	}
	
	private defineDataset(){
		println "  >Fetching data..."
		
		def header = getHeader()
		def dataset =  new Instances("DISEASES", header, 0)
		
		getInstances(dataset).each{
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
		
		return attributes
	}
	
	private getInstances(dataset){
		def instances = []
		Disease.getAll().each{ disease ->
			def values = new double[dataset.numAttributes()]
			def symptoms = disease.symptoms()
			
			values[0] = dataset.attribute("disease_id").indexOfValue(disease.id as String)
			def i = 1
			Symptom.getAll().each{ symptom ->
				if(symptom in symptoms){
					values[i] = dataset.attribute("s${symptom.id}").indexOfValue("y")
				} else {
					values[i] = dataset.attribute("s${symptom.id}").indexOfValue("n")
				}
				
				i++
			}	
			
			instances << new Instance(1.0, values)		
		}
		
		return instances
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
			
			model = initializeWeka()
			model.buildClassifier(trainSet)
			
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
