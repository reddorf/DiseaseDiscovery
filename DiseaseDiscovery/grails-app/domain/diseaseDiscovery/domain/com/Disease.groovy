package diseaseDiscovery.domain.com

class Disease {
	static hasMany = [symptomDisease : SymptomDisease]
	
	long id
	String name
	
    static constraints = {
		id nullable: false
		name blank: false, nullable: false
    }
	
	// Helper methods
	def symptoms() {
		return symptomDisease.collect{ it.symptom }
	}
	
	def addSymptom(Symptom symptom, freq = null) { 
		SymptomDisease.link(this, symptom, freq) 
		return symptoms() 
	}
	
	def removeSymptom(Symptom symptom) { 
		SymptomDisease.unlink(this, symptom) 
		return symptoms() 
	}
}
