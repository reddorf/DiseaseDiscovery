package diseaseDiscovery.domain.com

class Disease {
	static hasMany = [symptomDisease : SymptomDisease]
	
	long id
	String name
	
    static constraints = {
		id nullable: false
		name blank: false, nullable: false
    }
	
	static mapping = {
		sort name: "asc"
	}
	
	// Helper methods
	def symptoms() {
		return symptomDisease.collect{ it.symptom }?.sort {it.name}
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
