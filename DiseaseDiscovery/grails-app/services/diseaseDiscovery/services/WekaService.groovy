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
import weka.core.converters.ConverterUtils.DataSink


class WekaService {
	def DATASET_REPETITIONS = 3 // TODO: find useful number
	def NOISE = 10 // % of noise to use
	def MAX_COINCIDENCE = 50 // maximum % of coincidence with an actual symptom list when generating other diseases
	def random = new Random()
	
	def makePrediction(symptoms, modelPath){
		println "Making prediction using $modelPath..."
		def model = SerializationHelper.read(modelPath)
		if(!model) return false
		println "\t>Model loaded"
		def data = getPredictionDataset(symptoms)
		
		data.setClassIndex(data.numAttributes() - 1)
		
		println "\t>Data loaded"
//		println data.numInstances()
//		println data
		
		def prediction = new Instances(data)
		def label = model.classifyInstance(data.instance(0))
		prediction.instance(0).setClassValue(label)	
		
		//println prediction.toString()
		//println "$label -> ${data.classAttribute().value((int) label)}"
		println "Prediction finished."
		
		//return data.classAttribute().value((int) label)	
		return label	
	}
	
	def makeTrainingSet() {
		def data = defineTrainingDataset()
		
		DataSink.write("train_malalt_final_5rep_10noise_50maxcoinc.arff", data);
		println "Training set saved."
	}
	
	def makeTestingSet(){
		def data = defineTestingDataset()
		
		DataSink.write("test_malalt_final_3rep_10noise_50maxcoinc.arff", data);
		println "Training set saved."
	}
	
	
	private defineTrainingDataset(){
		println "  >Fetching data..."
		
		def header = getHeader(1)
		def dataset =  new Instances("DISEASES", header, 0)
		
		getInstances(dataset,5, MAX_COINCIDENCE, NOISE).each{
			dataset.add(it)
		}
		
		//dataset.setClassIndex(0)
		dataset.randomize(dataset.getRandomNumberGenerator(99999))
		
		println "  >Data fetched."
		return dataset
	}
	
	private defineTestingDataset() {
		println "  >Fetching data..."
		
		def header = getHeader(DATASET_REPETITIONS)
		def dataset =  new Instances("DISEASES", header, 0)
		
		getInstances(dataset, 3, MAX_COINCIDENCE, NOISE).each{
			dataset.add(it)
		}
		
		//dataset.setClassIndex(0)
		dataset.randomize(dataset.getRandomNumberGenerator(99999))
		
		println "  >Data fetched."
		return dataset
	}
	
	private getHeader(dataReps = 0){
		dataReps = dataReps ?: 1
		def patientNum = Disease.count() //* dataReps
		def attributes = new FastVector()
		
		def diseaseLabels = new FastVector()
		FastVector symptomLabels = new FastVector()
		symptomLabels.addElement("y")
		symptomLabels.addElement("n")
		
		Disease.getAll().each{ disease ->
			if(disease.symptoms()) diseaseLabels.addElement(disease.id as String)
		}
		
		// Random diseases
		def lastDisease = 1 + (Disease.createCriteria().get{
			projections {
				max "id"
			}
		} as Long)
		
		(lastDisease..(lastDisease + (patientNum * 0.10) - 1)).each{
			diseaseLabels.addElement((it as int) as String)
		}
		
//		attributes.addElement(new Attribute("disease_id", diseaseLabels))
		
		Symptom.getAll().each{ symptom ->
			attributes.addElement(new Attribute("s${symptom.id}", symptomLabels))
		}
		
		attributes.addElement(new Attribute("disease_id", diseaseLabels))
		
		println "\t>Header built."
		return attributes
	}
	
	private getInstances(dataset, dataReps = 0, maxCoincidence = 0, noiseLevel){
		dataReps = dataReps ?: 1
		def actualInstances = dataReps * Disease.count() as int
		def falseInstances = Disease.count() * 0.10 as int
		
		def symptomsList = Symptom.getAll()
		
		def instances = []
		def j=1
		def set = []//Disease.executeQuery('from Disease order by rand()', [max: actualInstances])
		Disease.getAll().each{
			/*if(it.symptoms())
				set << it
			else
				println "disease $it.name not used: has no symptoms"	*/set << it
		}
		
		println "\t> Set of diseases fetched: using ${set.size()} of ${Disease.count()}"
		(1..dataReps).each{ 
			set.each{ disease -> 
				def values = new double[dataset.numAttributes()]
				def symptoms = disease.symptoms()
	
				def i = 0
				symptomsList.each{ symptom -> 
					if(symptom in symptoms){
						values[i] = new Random().nextInt(101) > noiseLevel ? dataset.attribute("s${symptom.id}").indexOfValue("y") : dataset.attribute("s${symptom.id}").indexOfValue("n")
					} else {
						values[i] = new Random().nextInt(101) > noiseLevel ? dataset.attribute("s${symptom.id}").indexOfValue("n") : dataset.attribute("s${symptom.id}").indexOfValue("y")
					}
					
					i++
				}	
				values[i] = dataset.attribute("disease_id").indexOfValue(disease.id as String)
				println "disease $disease.id done - $j of ${dataReps*set.size()}"
				j++
				
				instances << new Instance(1.0, values)		
			}
		}
		
		println "Creating random instances"
		(1..dataReps).each{ lap ->
			println "set $lap of $dataReps"
			def lastDisease = 1 + (Disease.createCriteria().get{
				projections {
					max "id"
				}
			} as Long)
			(1..falseInstances).each{ 
				def goodSet = false
				def instance
				
				def values = new double[dataset.numAttributes()]
				while(!goodSet) {
					//values[0] = dataset.attribute("disease_id").indexOfValue(lastDisease as String)
					def i = 0
					
					symptomsList.each{
						values[i] = new Random().nextBoolean() ? dataset.attribute("s${it.id}").indexOfValue("y") : dataset.attribute("s${it.id}").indexOfValue("n")
						i++
					}
					values[i] = dataset.attribute("disease_id").indexOfValue(lastDisease as String)
					
					if(!similar(instances, values, maxCoincidence)) {
						goodSet = true
						println "ok"
					} else {
						values = new double[dataset.numAttributes()]
						println "too similar"
					}
				}	
				
				println "random disease $lastDisease done - $it of ${falseInstances} (iteration number $lap of $dataReps)"
				
				instances << new Instance(1.0, values)
				lastDisease++	
			}
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
		
		def i = 0
		Symptom.getAll().each{ symptom ->
			if(symptom in symptoms){
				values[i] = dataset.attribute("s${symptom.id}").indexOfValue("y")
			} else {
				values[i] = dataset.attribute("s${symptom.id}").indexOfValue("n")
			}
			
			i++
		}
		values[i] = Instance.missingValue()
		
		dataset.add(new Instance(1.0, values))
		
		return dataset
	}
	
	private goodToGo(){
		return Disease.count() > 1 && Symptom.count() > 1 && SymptomDisease.count() > 1
	}
}
