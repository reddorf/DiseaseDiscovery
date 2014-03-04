<!DOCTYPE html>
<head>
	<meta name="layout" content="main">
	<title>Disease Discovery</title>
	
	<g:javascript src="autocomplete.js"/>
	<g:javascript>
		$(document).ready(function() {
			setupSymptom("${createLink(controller: 'Symptom', action: 'getSymptoms')}");
		});
	</g:javascript>
</head>
<body>
	<input style="float: right; margin: 0px 10px 10px 0px;" type="text" 
		   name="symptom" id="symptom_name" 
		   value="" placeholder="Enter symptom..." />
	<input type="hidden" id="symptom_id" name="symptom_id" value="" />
</body>