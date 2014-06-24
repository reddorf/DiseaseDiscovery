package diseaseDiscovery.controllers.com

import org.springframework.dao.DataIntegrityViolationException

import diseaseDiscovery.domain.com.SymptomDisease;
import diseaseDiscovery.domain.com.Symptom
import diseaseDiscovery.domain.com.Disease
import grails.converters.JSON

class SymptomDiseaseController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

	def medicalInformationService
	def wekaService
	def postResponseService
	def weightService
	def predictionService
	
    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [symptomDiseaseInstanceList: SymptomDisease.list(params), symptomDiseaseInstanceTotal: SymptomDisease.count()]
    }

    def create() {
        [symptomDiseaseInstance: new SymptomDisease(params)]
    }

    def save() {
        def symptomDiseaseInstance = new SymptomDisease(params)
        if (!symptomDiseaseInstance.save(flush: true)) {
            render(view: "create", model: [symptomDiseaseInstance: symptomDiseaseInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'symptomDisease.label', default: 'SymptomDisease'), symptomDiseaseInstance.id])
        redirect(action: "show", id: symptomDiseaseInstance.id)
    }

    def show(Long id) {
        def symptomDiseaseInstance = SymptomDisease.get(id)
        if (!symptomDiseaseInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'symptomDisease.label', default: 'SymptomDisease'), id])
            redirect(action: "list")
            return
        }

        [symptomDiseaseInstance: symptomDiseaseInstance]
    }

    def edit(Long id) {
        def symptomDiseaseInstance = SymptomDisease.get(id)
        if (!symptomDiseaseInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'symptomDisease.label', default: 'SymptomDisease'), id])
            redirect(action: "list")
            return
        }

        [symptomDiseaseInstance: symptomDiseaseInstance]
    }

    def update(Long id, Long version) {
        def symptomDiseaseInstance = SymptomDisease.get(id)
        if (!symptomDiseaseInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'symptomDisease.label', default: 'SymptomDisease'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (symptomDiseaseInstance.version > version) {
                symptomDiseaseInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'symptomDisease.label', default: 'SymptomDisease')] as Object[],
                          "Another user has updated this SymptomDisease while you were editing")
                render(view: "edit", model: [symptomDiseaseInstance: symptomDiseaseInstance])
                return
            }
        }

        symptomDiseaseInstance.properties = params

        if (!symptomDiseaseInstance.save(flush: true)) {
            render(view: "edit", model: [symptomDiseaseInstance: symptomDiseaseInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'symptomDisease.label', default: 'SymptomDisease'), symptomDiseaseInstance.id])
        redirect(action: "show", id: symptomDiseaseInstance.id)
    }

    def delete(Long id) {
        def symptomDiseaseInstance = SymptomDisease.get(id)
        if (!symptomDiseaseInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'symptomDisease.label', default: 'SymptomDisease'), id])
            redirect(action: "list")
            return
        }

        try {
            symptomDiseaseInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'symptomDisease.label', default: 'SymptomDisease'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'symptomDisease.label', default: 'SymptomDisease'), id])
            redirect(action: "show", id: id)
        }
    }
	
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
		preds.all.each{ key, value ->
			preds.all[key] = [Disease.get(value[0]), value[1]]
		}
//		def diseases = [:]
//		preds.all.each{ key, value ->
//			def dis = Disease.get(value[0].toLong()).name
//			if (diseases[dis])
//				diseases[dis] += value[1]
//			else
//				diseases[dis] = value[1]
//		}
//		
		//render postResponseService.getResponse(Disease.get(preds.prediction)) as JSON
		render (template: 'prediction', 
				model: ['predictedDisease' : Disease.get(preds.prediction.key), 
						'predictedWeight'  : preds.prediction.value,
						'modelInfo' : preds.all
						/*'diseases' : diseases, classified: predictions*/])
	}
	
	def path(){
		def folder = new File("..")
		def list = []

		folder.eachFile() { file->
			list << file.getPath().replaceFirst(~/\.[^\.]+$/, '')
		}
		
		render list
	}
	
	def createTrainingSet() {
		wekaService.makeTrainingSet()
		
		render "ieps"
	}
	
	def createTestingSet() {
		wekaService.makeTestingSet()
		
		render "done"
	}
}
