package diseaseDiscovery.services

class PostResponseService {
	
    def getResponse(instance) {
		def resp = new PostResponse(object: instance)
		
		if (!instance) {
		  resp.success = false
		  resp.message = "There was an error"
		} else {
		  resp.success = true
		  resp.message = "Success"
		}
		return resp
    }
}

class PostResponse {
	boolean success
	String message
	String html
	def object
//	def errors = [:]
  }
