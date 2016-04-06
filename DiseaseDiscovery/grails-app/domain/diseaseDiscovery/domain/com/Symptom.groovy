package diseaseDiscovery.domain.com

class Symptom {
	static hasMany = [symptomDisease : SymptomDisease]
	
	long id
	String name
	
    static constraints = {
		id nullable: false
		name blank: false, nullable: false
    }
	
	static mapping = {
		sort name: "desc"
	}
	
	def diseases() {
		return symptomDisease.collect{ it.disease }?.sort {it.name}
	}
	
	// Helper methods
	def addDisease(Disease disease, freq = null) {
		SymptomDisease.link(this, disease, freq)
		return diseases()
	}
	
	def removeDisease(Disease disease) {
		SymptomDisease.unlink(this, disease)
		return diseases()
	}
}
