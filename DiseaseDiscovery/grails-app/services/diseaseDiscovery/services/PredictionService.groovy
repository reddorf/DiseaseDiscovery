package diseaseDiscovery.services

class PredictionService {
	def wekaService
	
    def makePrediction(symptoms, weights) {
		def predictions = [:] 
		def individualPredictions = [:]
		
		weights.each{ model, weight ->
			if (weight > 0) {
				def prediction = wekaService.makePrediction(symptoms, "classifiers\\${model}.model")
				
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
