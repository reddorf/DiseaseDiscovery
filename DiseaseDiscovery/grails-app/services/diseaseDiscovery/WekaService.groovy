package diseaseDiscovery

import com.Disease
import com.Symptom
import com.SymptomDisease

import weka.core.Instance
import weka.core.Instances
import weka.core.Attribute
import weka.core.FastVector
import weka.classifiers.bayes.NaiveBayes
import weka.core.Instance
import weka.core.Utils

class WekaService {

	def createModel(){
		println "Creating WEKA model."
		
		println "  >Fetching data..."
		def model = initializeWeka()
		def data = defineDataset()
		println "  >Data fetched. Building model..."
		
		model.buildClassifier(data)
		
		println "WEKA model created"
	}
	
	private initializeWeka(){
//		String[] options = Utils.splitOptions("-c 1")
		
		def model = new NaiveBayes()
//		model.setOptions(options)
		
		return model
	}
	
	private defineDataset(){
		def header = getHeader()
		def dataset =  new Instances("DISEASES", header, 0)
		
		getInstances(dataset).each{
			dataset.add(it)
		}
		
		dataset.setClassIndex(0)
		return dataset//dataset.randomize(dataset.getRandomNumberGenerator(99999))
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
}
