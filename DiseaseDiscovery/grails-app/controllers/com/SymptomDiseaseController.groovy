package com

import org.springframework.dao.DataIntegrityViolationException

class SymptomDiseaseController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

	def medicalInformationService
	def wekaService
	
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
	
	def makePrediction(){
		wekaService.getFile()
	}
}
