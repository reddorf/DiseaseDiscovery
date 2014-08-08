package diseaseDiscovery.controllers.com
import org.springframework.security.access.annotation.Secured
import grails.converters.JSON
import diseaseDiscovery.domain.com.ClassifierWeight

@Secured(['ROLE_ADMIN'])
class AdministrationController {

    def index() { 
		
	}
	
	def saveWeights(){
		def cList = []
		JSON.parse(params?.weights).each{
			//weights[it.classifier] = it.weight.toFloat()///100
			
			def clas = ClassifierWeight.findByClassifier(it.classifier)
			if(clas){
				clas.weight = it.weight.toDouble()
			}
			else {
				new ClassifierWeight(classifier: it.classifier, weight: it.weight.toDouble()).save(flush: true)
			}
			
			cList << clas.classifier
		}
		
		ClassifierWeight.all.each{
			if(!it.classifier in cList){
				it.delete(flush:true)
			}
		}
		
		render "ok"
	}
}
