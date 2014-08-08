package diseaseDiscovery.services

class PredictionService {
	def wekaService
	def grailsApplication
	
    def makePrediction(symptoms, weights) {
		def folder = grailsApplication.config['classifierPath']
		def separator = grailsApplication.config['dirSeparator']
		def predictions = [:] 
		def individualPredictions = [:]
		
		weights.each{ model, weight ->
			if (weight > 0) {
				def prediction = wekaService.makePrediction(symptoms, "${folder}${separator}${model}.model")
				
				individualPredictions[model] = [prediction, weight]
				
				if(predictions[prediction])
					predictions[prediction] += weight
				else
					predictions[prediction] = weight
			}
		}
		
//		println "pred: $predictions"
//		println "all: $individualPredictions"
//		println "max: ${predictions.max{it.value}.key} with weight ${predictions.max{it.value}.value}"
		

		return ['prediction' : predictions.max{it.value}, 'all' : individualPredictions]
    }
}
