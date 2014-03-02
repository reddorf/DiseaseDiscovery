package diseaseDiscovery

import com.Disease
import com.Symptom
import com.SymptomDisease

class WekaService {

	def getFile() {
		def file = createInputFile()
		
		println file
		
		return file	
	}
	
    def createInputFile() {
		def header = createHeader()
		def body = createBody()
		
		// TODO: save file with header and body
		return header + body
    }
	
	def createHeader() {
		def relation = "@relation DISEASES\n"
		def diseases = "@attribute disease_id {"
		def symptoms = ""
		
		Disease.getAll().each{ disease ->
			diseases += disease.id + ", " 
		}
		diseases = diseases[0..-3] + "}\n"
		
		Symptom.getAll().each{ symptom ->
			symptoms += "@attribute s${symptom.id} {y,n}\n"
		}
		
		return relation + diseases + symptoms
	}
	
	def createBody() {
		def data = "@data\n"
		
		return data
	}
}
