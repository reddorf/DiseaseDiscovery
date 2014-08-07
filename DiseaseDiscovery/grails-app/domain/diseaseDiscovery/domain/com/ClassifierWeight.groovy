package diseaseDiscovery.domain.com

class ClassifierWeight {

	String classifier
	float weight
	
    static constraints = {
		weight min: 0.0f, max: 100.0f
    }
}
