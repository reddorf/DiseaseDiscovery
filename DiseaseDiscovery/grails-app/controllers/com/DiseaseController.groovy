package com

import org.springframework.dao.DataIntegrityViolationException

class DiseaseController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [diseaseInstanceList: Disease.list(params), diseaseInstanceTotal: Disease.count()]
    }

    def create() {
        [diseaseInstance: new Disease(params)]
    }

    def save() {
        def diseaseInstance = new Disease(params)
        if (!diseaseInstance.save(flush: true)) {
            render(view: "create", model: [diseaseInstance: diseaseInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'disease.label', default: 'Disease'), diseaseInstance.id])
        redirect(action: "show", id: diseaseInstance.id)
    }

    def show(Long id) {
        def diseaseInstance = Disease.get(id)
        if (!diseaseInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'disease.label', default: 'Disease'), id])
            redirect(action: "list")
            return
        }

        [diseaseInstance: diseaseInstance]
    }

    def edit(Long id) {
        def diseaseInstance = Disease.get(id)
        if (!diseaseInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'disease.label', default: 'Disease'), id])
            redirect(action: "list")
            return
        }

        [diseaseInstance: diseaseInstance]
    }

    def update(Long id, Long version) {
        def diseaseInstance = Disease.get(id)
        if (!diseaseInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'disease.label', default: 'Disease'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (diseaseInstance.version > version) {
                diseaseInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'disease.label', default: 'Disease')] as Object[],
                          "Another user has updated this Disease while you were editing")
                render(view: "edit", model: [diseaseInstance: diseaseInstance])
                return
            }
        }

        diseaseInstance.properties = params

        if (!diseaseInstance.save(flush: true)) {
            render(view: "edit", model: [diseaseInstance: diseaseInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'disease.label', default: 'Disease'), diseaseInstance.id])
        redirect(action: "show", id: diseaseInstance.id)
    }

    def delete(Long id) {
        def diseaseInstance = Disease.get(id)
        if (!diseaseInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'disease.label', default: 'Disease'), id])
            redirect(action: "list")
            return
        }

        try {
            diseaseInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'disease.label', default: 'Disease'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'disease.label', default: 'Disease'), id])
            redirect(action: "show", id: id)
        }
    }
}
