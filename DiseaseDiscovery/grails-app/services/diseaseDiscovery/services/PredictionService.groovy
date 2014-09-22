package diseaseDiscovery.services

class PredictionService {
	def wekaService
	def grailsApplication
	
	def makePrediction(symptoms) {
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
			
			predictions[it] = Math.max((1 - n/Math.max(sUser, sDisease)), 0.0)
		}
		
		predictions = predictions.sort { a, b -> b.value <=> a.value } // Sort the map
		
		println "Prediction done."
		return ['prediction' : predictions.max{it.value}, 'all' : predictions]
	}
}
