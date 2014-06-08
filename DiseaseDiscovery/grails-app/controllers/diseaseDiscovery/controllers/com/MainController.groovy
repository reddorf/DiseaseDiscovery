package diseaseDiscovery.controllers.com

import diseaseDiscovery.domain.com.Disease
import diseaseDiscovery.domain.com.Symptom

class MainController {

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
}
