package com

class Symptom {
	static hasMany = [disease : Disease]
	static belongsTo = Disease
	
	long id
	String name
	
    static constraints = {
		id nullable: false
		name blank: false, nullable: false
    }
}
