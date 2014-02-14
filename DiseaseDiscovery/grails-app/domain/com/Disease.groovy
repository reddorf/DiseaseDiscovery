package com

class Disease {
	static hasMany = [symptom:Symptom]
	
	long id
	String name
	
    static constraints = {
		id nullable: false
		name blank: false, nullable: false
    }
}
