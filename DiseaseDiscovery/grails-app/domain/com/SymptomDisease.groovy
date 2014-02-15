package com

class SymptomDisease {

	Disease disease
	Symptom symptom
	float symptomFreq
	
    static constraints = {
    }
	
	static SymptomDisease link(disease, symptom, freq) {
		def rel = SymptomDisease.findByDiseaseAndSymptom(disease, symptom)
		if (!rel)
		{
			rel = new SymptomDisease(symptomFreq: freq)
			disease?.addToSymptomDisease(rel)
			symptom?.addToSymptomDisease(rel)
			rel.save()
		}
		
		return rel
	}
	
	static void unlink(disease, symptom) {
		def rel = SymptomDisease.findByDiseaseAndSymptom(disease, symptom)
		if (rel)
		{
			disease?.removeFromSymptomDisease(rel)
			symptom?.removeFromSymptomDisease(rel)
			rel.delete()
		}
	}
}
