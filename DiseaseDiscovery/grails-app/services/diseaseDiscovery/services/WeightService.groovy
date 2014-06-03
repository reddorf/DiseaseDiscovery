package diseaseDiscovery.services

class WeightService {

    def getDefaultWeights() {
		def weightedList = [:]
		def fileNames = getFileNames()
		def totalNames = fileNames.size()
		fileNames.each{
			weightedList[it] = 1/totalNames
		}
		
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
