<!DOCTYPE html>
<head>
	<meta name="layout" content="main">
	<title>Disease Discovery</title>
	
	<g:javascript src="symptomList.js"/>
	<g:javascript>
		$(document).ready(function() {
			setup("${createLink(controller: 'Symptom', action: 'getSymptoms')}",
				  "${createLink(controller: 'SymptomDisease', action: 'makePrediction')}");
		});
	</g:javascript>
</head>
<body>
	<div class="row">
		<div class="col-lg-6">
	    	<div class="input-group">
	 		 	<input type="text" class="form-control" name="symptom" id="symptom_name" 
		  			   value="" placeholder="Enter symptom..." />
	  			<input type="hidden" id="symptom_id" name="symptom_id" value="" />
	      		<span class="input-group-btn">
	        		<button class="btn btn-default" id="btn_addSymptom"	
	        				type="button">
	        			Add symptom
	        		</button>
	      		</span>
	    	</div>
		</div>
	</div>

	<div class="panel panel-primary">
		<div class="panel-heading">
 		 	<h3 class="panel-title">Symptoms list</h3>
       		<button class="btn btn-default" id="btn_submitSymptoms"	
       				type="button">
       			Submit symptoms
       		</button>
		</div>
		<form id="elementList">
			<ul id="elements" class="list-group"></ul>
		</form>
	</div>
	
	<div class="panel panel-primary">
		<div class="panel-heading">
 		 	<h3 class="panel-title">Disease</h3>
		</div>
		<div id="disease"></div>
	</div>
</body>

