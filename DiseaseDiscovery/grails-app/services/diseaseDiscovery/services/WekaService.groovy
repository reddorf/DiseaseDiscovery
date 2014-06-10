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
import weka.core.converters.ConverterUtils.DataSink;
class WekaService {
	def DATASET_REPETITIONS = 2 // TODO: find useful number
	def NOISE = 10 // % of noise to use
	def MAX_COINCIDENCE = 50 // maximum % of coincidence with an actual symptom list when generating other diseases
	def random = new Random()
	
	def makePrediction(symptoms, modelPath){
		println "Making prediction using $modelPath..."
		def model = SerializationHelper.read(modelPath)
		if(!model) return false
		println "\t>Model loaded"
		def data = getPredictionDataset(symptoms)
		
		data.setClassIndex(0)
		
		println "\t>Data loaded"
//		println data.numInstances()
//		println data
		
		def prediction = new Instances(data)
		def label = model.classifyInstance(data.instance(0))
		prediction.instance(0).setClassValue(label)	
		
		//println prediction.toString()
		println "$label -> ${data.classAttribute().value((int) label)}"
		println "Prediction finished."
		
		return data.classAttribute().value((int) label)
		
//		def pred = model.classifyInstance(data.instance(0));
//		def dist = model.distributionForInstance(data.instance(0));
//		println data.classAttribute().value((int) pred)
//		println "--"
//		println Utils.arrayToString(dist)
		
	}
	
	private defineTrainingDataset(){
		println "  >Fetching data..."
		
		def header = getHeader(1)
		def dataset =  new Instances("DISEASES", header, 0)
		
		getInstances(dataset, 1, MAX_COINCIDENCE, 0).each{
			dataset.add(it)
		}
		
		dataset.setClassIndex(0)
		dataset.randomize(dataset.getRandomNumberGenerator(99999))
		
		println "  >Data fetched."
		return dataset
	}
	
	private defineTestingDataset() {
		println "  >Fetching data..."
		
		def header = getHeader(DATASET_REPETITIONS)
		def dataset =  new Instances("DISEASES", header, 0)
		
		getInstances(dataset, 5, MAX_COINCIDENCE, NOISE).each{
			dataset.add(it)
		}
		
		dataset.setClassIndex(0)
		dataset.randomize(dataset.getRandomNumberGenerator(99999))
		
		println "  >Data fetched."
		return dataset
	}
	
	private getHeader(dataReps = 0){
		dataReps = dataReps ?: 1
		def patientNum = Disease.count() * dataReps
		def attributes = new FastVector()
		
		def diseaseLabels = new FastVector()
		FastVector symptomLabels = new FastVector()
		symptomLabels.addElement("y")
		symptomLabels.addElement("n")
		
		Disease.getAll().each{ disease ->
			diseaseLabels.addElement(disease.id as String)
		}
		
		// Random diseases
		def lastDisease = Disease.count() + 1 
		(lastDisease..(lastDisease + (patientNum * 0.10) - 1)).each{
			diseaseLabels.addElement((it as int) as String)
		}
		
		attributes.addElement(new Attribute("disease_id", diseaseLabels))
		
		Symptom.getAll().each{ symptom ->
			attributes.addElement(new Attribute("s${symptom.id}", symptomLabels))
		}
		
		println "\t>Header built."
		return attributes
	}
	
	private getInstances(dataset, dataReps = 0, maxCoincidence = 0, noiseLevel){
		dataReps = dataReps ?: 1
		def actualInstances = dataReps * Disease.count()
		def falseInstances = actualInstances*0.10 as int
		
		def symptomsList = Symptom.getAll()
		
		def instances = []
		def j=1
		def set = Disease.executeQuery('from Disease order by rand()', [max: actualInstances])
		println "\t> Set of diseases fetched"
		dataReps.each{ 
			set.each{ disease -> 
				def values = new double[dataset.numAttributes()]
				def symptoms = disease.symptoms()
	
				values[0] = dataset.attribute("disease_id").indexOfValue(disease.id as String)
				def i = 1
				symptomsList.each{ symptom -> 
					if(symptom in symptoms){
						values[i] = new Random().nextInt(101) > noiseLevel ? dataset.attribute("s${symptom.id}").indexOfValue("y") : dataset.attribute("s${symptom.id}").indexOfValue("n")
					} else {
						values[i] = new Random().nextInt(101) > noiseLevel ? dataset.attribute("s${symptom.id}").indexOfValue("n") : dataset.attribute("s${symptom.id}").indexOfValue("y")
					}
					
					i++
				}	
				//println "disease $disease done - $j of ${actualInstances}"
				j++
				
				instances << new Instance(1.0, values)		
			}
		}
		
		println "Creating random instances"
		def lastDisease = (Disease.count() + 1) as int
		(1..falseInstances).each{
			def goodSet = false
			def instance
			
			def values = new double[dataset.numAttributes()]
			while(!goodSet) {
				values[0] = dataset.attribute("disease_id").indexOfValue(lastDisease as String)
				def i = 1
				symptomsList.each{
					values[i] = new Random().nextBoolean() ? dataset.attribute("s${it.id}").indexOfValue("y") : dataset.attribute("s${it.id}").indexOfValue("n")
					i++
				}
				if(!similar(instances, values, maxCoincidence)) {
					goodSet = true
				} else {
					values = new double[dataset.numAttributes()]
				}
			}	
			
			//println "random disease $lastDisease done - $it of ${falseInstances}"
			
			instances << new Instance(1.0, values)
			lastDisease++	
		}
		
		println "\t>Dataset body created."
		return instances
	}
	
	private similar(instances, values, maxCoincidence){
		def checkArray
		def i
		def equalsCount

		return !instances.find{ instance ->
			i = 0
			equalsCount = 0

			instance.toDoubleArray().each {
				equalsCount += it == values[i] ? 1 : 0
				i++
			}	
			
			if (equalsCount / instance.numAttributes() * 100 < maxCoincidence)
				return true
			else 
				return false
		}
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
	
	private goodToGo(){
		return Disease.count() > 1 && Symptom.count() > 1 && SymptomDisease.count() > 1
	}
}
