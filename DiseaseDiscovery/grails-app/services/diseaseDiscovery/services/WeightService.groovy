package diseaseDiscovery.services

import diseaseDiscovery.domain.com.ClassifierWeight

class WeightService {

    def getDefaultWeights() {
		def weightedList = [:]
		def fileNames = getFileNames()
		def totalNames = fileNames.size()
		def missing = 0
		def current = 0
		def total = 0
		
		fileNames.each{
			def clas = ClassifierWeight.findByClassifier(it)
			if(clas){
				weightedList[it] = clas.weight/100
				total += clas.weight
				current++
			}
			else {
				weightedList[it] = 0.0f
				missing++
			}
		}
		
		if(total < 100.0f){
			fileNames.each{
				if(weightedList[it] > 0){
					weightedList[it] += ((100.0f - total)/current)/100
				}
			}
		}
		
		println weightedList
		return weightedList
    }
	
	private getFileNames() {
		def folder = new File("classifiers")
		def list = []

		folder.eachFile() { file->
			list << file.getName().replaceFirst(~/\.[^\.]+$/, '')
		}
		
		return list
	}
}
