package diseaseDiscovery.controllers.com

import org.springframework.security.access.annotation.Secured

import diseaseDiscovery.domain.com.SymptomDisease
import diseaseDiscovery.domain.com.Symptom
import diseaseDiscovery.domain.com.Disease
import grails.converters.JSON

class MainController {

	def medicalInformationService
	def wekaService
	def postResponseService
	def weightService
	def predictionService
	
    def home() {
		def diseaseLetters = []
		def symptomLetters = [] 
		("A".."Z").each{
			if(Disease.findByNameIlike("${it}%"))
				diseaseLetters << it
			if(Symptom.findByNameIlike("${it}%"))
				symptomLetters << it
				
		}
		
		[diseaseLetters: diseaseLetters, symptomLetters: symptomLetters]
	}
	
	@Secured(['ROLE_ADMIN'])
	def populateDB(){
		medicalInformationService.populateDB()
	}
	
	def getFiles() {
		render weightService.getDefaultWeights() as JSON
	}
	
	def getDiseasesByLetter(){
		def dis = Disease.findAllByNameIlike("${params.letter}%")
		
		render (template: 'infotable', model: [instanceList: dis, instanceTotal: dis.size(), searchButton: true])
	}
	
	def getSymptomsByLetter(){
		def sympt = Symptom.findAllByNameIlike("${params.letter}%")
		
		render (template: 'infotable', model: [instanceList: sympt, instanceTotal: sympt.size(), addButton: true, searchButton: true])
	}
	
	def getComponents(){
		def ret
		
		if(params.type == Disease.class.toString()){
			ret = Disease.get(params.id).symptoms()
		}
		else if (params.type == Symptom.class.toString()){
			ret = Symptom.get(params.id).diseases()
		}
		
		ret ? render(template: 'dropdown', model: [instanceList: ret,
												   sympts: params.type == Symptom.class.toString(),
												   disease: Disease.findById(params.id),
												   symptom: Symptom.findById(params.id)]) : render ("There are no instances under this element")
	}
	
	def makePrediction(){
		def weights = [:]
		JSON.parse(params.weights).each{
			weights[it.classifier] = it.weight.toFloat()///100
		}

		def symptIds = params.list('symptoms')
		def symptoms = []
		symptIds.each{
			symptoms << Symptom.get(it)
		}

		def preds = predictionService.makePrediction(symptoms, weights)
//		preds.all.each{ key, value ->
//			preds.all[key] = [Disease.get(value[0]), value[1]]
//		}
//
//		render (template: 'prediction',
//				model: ['predictedDisease' : Disease.get(preds.prediction.key),
//						'predictedWeight'  : preds.prediction.value,
//						'modelInfo' : preds.all
//						/*'diseases' : diseases, classified: predictions*/])
		render (template: 'prediction',
							model: ['predictedDisease' : preds.prediction.key,
									'predictedWeight'  : preds.prediction.value,
									'modelInfo' : preds.all
									/*'diseases' : diseases, classified: predictions*/])
	}
	
	@Secured(['ROLE_ADMIN'])
	def path(){
		def folder = new File("..")
		def list = []

		folder.eachFile() { file->
			list << file.getPath().replaceFirst(~/\.[^\.]+$/, '')
		}
		
		render list
	}
	
	@Secured(['ROLE_ADMIN'])
	def createTrainingSet() {
		wekaService.makeTrainingSet()
		
		render "ieps"
	}
	
	@Secured(['ROLE_ADMIN'])
	def createTestingSet() {
		wekaService.makeTestingSet()
		
		render "done"
	}
	
	@Secured(['ROLE_ADMIN'])
	def getWords(){
		def l = []
		Symptom.list().each{
			l << it.name
		}
		Disease.list().each {
			l << it.name
		}
		Collections.shuffle(l)
		
		render l.toString()
	}
	
	@Secured(['ROLE_ADMIN'])
	def getEmptyCount(){
		def set = []
		Disease.getAll().each{
			if(!it.symptoms() )
				set << it
		}
		
		render (["size: " : set.size, "total" : Disease.count, "count" : set.size / Disease.count * 100])
	}
	
	@Secured(['ROLE_ADMIN'])
	def deleteEmpty(){
		def num = Disease.count
		def set = []
		
		println "Getting diseases..."
		
		Disease.getAll().each{
			if(!it.symptoms() )
				set << it
		}
		
		println "Deleting diseases..."
		set.each{
			it.delete(flush:true)
		}
		
		println "Diseases deleted."
		render "Deleted ${num-Disease.count} instances"
	}
	
}
