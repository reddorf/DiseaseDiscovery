package com

class Symptom {
	static hasMany = [symptomDisease : SymptomDisease]
	
	long id
	String name
	
    static constraints = {
		id nullable: false
		name blank: false, nullable: false
    }
	
	def diseases() {
		return symptomDisease.collect{ it.disease }
	}
}
