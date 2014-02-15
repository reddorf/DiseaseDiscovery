package com

class Disease {
	static hasMany = [symptomDisease : SymptomDisease]
	
	long id
	String name
	
    static constraints = {
		id nullable: false
		name blank: false, nullable: false
    }
	
	def symptoms() {
		return symptomDisease.collect{ it.symptom }
	}
}
