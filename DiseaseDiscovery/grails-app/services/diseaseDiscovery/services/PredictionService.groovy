package diseaseDiscovery.services

class PredictionService {
	def wekaService
	def grailsApplication
	
//    def makePrediction(symptoms, weights) {
//		def folder = grailsApplication.config['classifierPath']
//		def separator = grailsApplication.config['dirSeparator']
//		def predictions = [:] 
//		def individualPredictions = [:]
//		
//		weights.each{ model, weight ->
//			if (weight > 0) {
//				def prediction = wekaService.makePrediction(symptoms, "${folder}${separator}${model}.model")
//				
//				individualPredictions[model] = [prediction, weight]
//				
//				if(predictions[prediction])
//					predictions[prediction] += weight
//				else
//					predictions[prediction] = weight
//			}
//		}
//		
////		println "pred: $predictions"
////		println "all: $individualPredictions"
////		println "max: ${predictions.max{it.value}.key} with weight ${predictions.max{it.value}.value}"
//		
//		return ['prediction' : predictions.max{it.value}, 'all' : individualPredictions]
//    }
	
	def makePrediction(symptoms, weights) {
		println "Making prediction..."
		def predictions = [:]
		def individualPredictions = [:]
		def diseases = [] as Set
		
		def sUser = symptoms.size()
		
		symptoms.each{
			diseases += it.diseases()
		}
		
		diseases.each{
			def sympts = it.symptoms()
			def n = ((sympts + symptoms) - sympts.intersect(symptoms)).size() // Get # of differences between the input and the disease's symptoms
			def sDisease = sympts.size()
			
			predictions[it] = 1 - n/Math.max(sUser, sDisease)
		}
		
		predictions = predictions.sort { a, b -> b.value <=> a.value } // Sort the map
		
		println "Prediction done."
		return ['prediction' : predictions.max{it.value}, 'all' : predictions]
	}
}
