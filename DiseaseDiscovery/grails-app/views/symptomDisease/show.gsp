
<%@ page import="diseaseDiscovery.domain.com.SymptomDisease" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'symptomDisease.label', default: 'SymptomDisease')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-symptomDisease" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-symptomDisease" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list symptomDisease">
			
				<g:if test="${symptomDiseaseInstance?.symptomFreq}">
				<li class="fieldcontain">
					<span id="symptomFreq-label" class="property-label"><g:message code="symptomDisease.symptomFreq.label" default="Symptom Freq" /></span>
					
						<span class="property-value" aria-labelledby="symptomFreq-label"><g:fieldValue bean="${symptomDiseaseInstance}" field="symptomFreq"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${symptomDiseaseInstance?.disease}">
				<li class="fieldcontain">
					<span id="disease-label" class="property-label"><g:message code="symptomDisease.disease.label" default="Disease" /></span>
					
						<span class="property-value" aria-labelledby="disease-label"><g:link controller="disease" action="show" id="${symptomDiseaseInstance?.disease?.id}">${symptomDiseaseInstance?.disease?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${symptomDiseaseInstance?.symptom}">
				<li class="fieldcontain">
					<span id="symptom-label" class="property-label"><g:message code="symptomDisease.symptom.label" default="Symptom" /></span>
					
						<span class="property-value" aria-labelledby="symptom-label"><g:link controller="symptom" action="show" id="${symptomDiseaseInstance?.symptom?.id}">${symptomDiseaseInstance?.symptom?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${symptomDiseaseInstance?.id}" />
					<g:link class="edit" action="edit" id="${symptomDiseaseInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
