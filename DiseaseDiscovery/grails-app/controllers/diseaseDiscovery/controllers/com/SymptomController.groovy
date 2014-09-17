package diseaseDiscovery.controllers.com

import org.springframework.security.access.annotation.Secured
import org.springframework.dao.DataIntegrityViolationException

import diseaseDiscovery.domain.com.Symptom;
import grails.converters.JSON


class SymptomController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

	@Secured(['ROLE_ADMIN'])
    def index() {
        redirect(action: "list", params: params)
    }

	@Secured(['ROLE_ADMIN'])
    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [symptomInstanceList: Symptom.list(params), symptomInstanceTotal: Symptom.count()]
    }

	@Secured(['ROLE_ADMIN'])
    def create() {
        [symptomInstance: new Symptom(params)]
    }

	@Secured(['ROLE_ADMIN'])
    def save() {
        def symptomInstance = new Symptom(params)
        if (!symptomInstance.save(flush: true)) {
            render(view: "create", model: [symptomInstance: symptomInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'symptom.label', default: 'Symptom'), symptomInstance.id])
        redirect(action: "show", id: symptomInstance.id)
    }

	@Secured(['ROLE_ADMIN'])
    def show(Long id) {
        def symptomInstance = Symptom.get(id)
        if (!symptomInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'symptom.label', default: 'Symptom'), id])
            redirect(action: "list")
            return
        }

        [symptomInstance: symptomInstance]
    }

	@Secured(['ROLE_ADMIN'])
    def edit(Long id) {
        def symptomInstance = Symptom.get(id)
        if (!symptomInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'symptom.label', default: 'Symptom'), id])
            redirect(action: "list")
            return
        }

        [symptomInstance: symptomInstance]
    }

	@Secured(['ROLE_ADMIN'])
    def update(Long id, Long version) {
        def symptomInstance = Symptom.get(id)
        if (!symptomInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'symptom.label', default: 'Symptom'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (symptomInstance.version > version) {
                symptomInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'symptom.label', default: 'Symptom')] as Object[],
                          "Another user has updated this Symptom while you were editing")
                render(view: "edit", model: [symptomInstance: symptomInstance])
                return
            }
        }

        symptomInstance.properties = params

        if (!symptomInstance.save(flush: true)) {
            render(view: "edit", model: [symptomInstance: symptomInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'symptom.label', default: 'Symptom'), symptomInstance.id])
        redirect(action: "show", id: symptomInstance.id)
    }

	@Secured(['ROLE_ADMIN'])
    def delete(Long id) {
        def symptomInstance = Symptom.get(id)
        if (!symptomInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'symptom.label', default: 'Symptom'), id])
            redirect(action: "list")
            return
        }

        try {
            symptomInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'symptom.label', default: 'Symptom'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'symptom.label', default: 'Symptom'), id])
            redirect(action: "show", id: id)
        }
    }
	
	def getSymptoms(){
		println "sip"
		render Symptom.list() as JSON
	}
}
