<!DOCTYPE html>
<head>
	<meta name="layout" content="main">
	<title>Disease Discovery</title>
	
	<g:javascript src="symptomList.js"/>
	<g:javascript>
		$(document).ready(function() {
			setup(
					"${createLink(controller: 'Symptom', action: 'getSymptoms')}",
				  	"${createLink(controller: 'SymptomDisease', action: 'makePrediction')}",
				  	"${createLink(controller: 'SymptomDisease', action: 'getFiles')}"
				  );
		});
	</g:javascript>
</head>
<body>
<%--	<div class="row">--%>
<%--		<div class="col-lg-6">--%>
<%--	    	<div class="input-group">--%>
<%--	 		 	<input type="text" class="form-control" name="symptom" id="symptom_name" --%>
<%--		  			   value="" placeholder="Enter symptom..." />--%>
<%--	  			<input type="hidden" id="symptom_id" name="symptom_id" value="" />--%>
<%--	      		<span class="input-group-btn">--%>
<%--	        		<button class="btn btn-default" id="btn_addSymptom"	--%>
<%--	        				type="button">--%>
<%--	        			Add symptom--%>
<%--	        		</button>--%>
<%--	      		</span>--%>
<%--	    	</div>--%>
<%--		</div>--%>
<%--	</div>--%>

	<div class="panel panel-primary">
		<div class="panel-heading">
 		 	
 		 	<div class="row">
 		 		<h3 class="panel-title col-lg-6">Symptoms List</h3>
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
		</div>
		<form id="elementList">
			<ul id="elements" class="list-group padded"></ul>
		</form>
<%--	</div>--%>
<%--	--%>
<%--	--%>
<%--	<div class="panel panel-default">--%>
    	<div class="panel-footer">
    		<div class="row">
    			<div class="col-md-9">
					<h4 class="panel-title">
				        <a data-toggle="collapse" data-parent="#accordion" href="#collapseOne">
				          Advanced
				        </a>
					</h4>
				</div>
				<div class="col-md-2">
					<button class="btn btn-default" id="btn_submitSymptoms"	type="button">
		       			Submit Symptoms
		       		</button>
		       	</div>
			</div>
    	</div>
    	<div id="collapseOne" class="panel-collapse collapse ">
			<div id="modelList" class="panel-body">
				<ul id="sliders"></ul>
				<button class="btn btn-default" id="btn_defaultWeights"	type="button">
		       		Default weights
		       	</button>
			</div>
		</div>
	</div>
	
	
	<div class="panel panel-primary">
		<div class="panel-heading">
			<div class="row">
				<div class="col-md-4">
	 		 		<h3 class="panel-title">Predicted Disease</h3>
	 		 	</div>
	 		 	<h4 class="col-md-offset-4 col-md-4">
	 		 		<div id="disease"></div>
	 		 	</h4>
	 		</div>
		</div>
	</div>
</body>

