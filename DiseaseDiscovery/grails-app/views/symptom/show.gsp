
<%@ page import="diseaseDiscovery.domain.com.Symptom" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'symptom.label', default: 'Symptom')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-symptom" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-symptom" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list symptom">
			
				<g:if test="${symptomInstance?.name}">
				<li class="fieldcontain">
					<span id="name-label" class="property-label"><g:message code="symptom.name.label" default="Name" /></span>
					
						<span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${symptomInstance}" field="name"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${symptomInstance?.symptomDisease}">
				<li class="fieldcontain">
					<span id="symptomDisease-label" class="property-label"><g:message code="symptom.symptomDisease.label" default="Symptom Disease" /></span>
					
						<g:each in="${symptomInstance.symptomDisease}" var="s">
						<span class="property-value" aria-labelledby="symptomDisease-label"><g:link controller="symptomDisease" action="show" id="${s.id}">${s?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${symptomInstance?.id}" />
					<g:link class="edit" action="edit" id="${symptomInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
