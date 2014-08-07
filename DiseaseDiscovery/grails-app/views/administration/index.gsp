<!DOCTYPE html>
<head>
	<meta name="layout" content="main">
	<title>Disease Discovery</title>
	
	<g:javascript src="admin.js"/>
	<g:javascript>
		$(document).ready(function() {
			setup(
				"${createLink(controller: 'Main', action: 'getFiles')}",
				"${createLink(controller: 'Administration', action: 'saveWeights')}");
		});
	</g:javascript>
</head>
<body>

	<div id="alert"></div>
	
	<ul id="main-tabs" class="nav nav-tabs">
		<li style="float: right"><a href="${createLink(controller: 'Symptom', action: 'index')}">Symptoms</a></li>
		<li style="float: right"><a href="${createLink(controller: 'Disease', action: 'index')}">Diseases</a></li>
	</ul>
	<div class="panel panel-primary">
		<div class="panel-heading">

				<ul id="sliders"></ul>
				<button class="btn btn-default" id="btn_defaultWeights"	type="button">
		       		Default weights
		       	</button>
		       	<button class="btn btn-default" id="btn_saveWeights"	type="button">
		       		Save weights
		       	</button>

		</div>
	</div>
</body>

