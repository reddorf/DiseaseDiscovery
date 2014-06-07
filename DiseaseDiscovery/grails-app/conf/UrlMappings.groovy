class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		/*"/"(view:"/index")*/
		/*"/"(view:"/home")*/
		"/"(controller:"main", action:"home")
		"500"(view:'/error')
	}
}
