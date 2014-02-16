
<%@ page import="com.Disease" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'disease.label', default: 'Disease')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-disease" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-disease" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list disease">
			
				<g:if test="${diseaseInstance?.name}">
				<li class="fieldcontain">
					<span id="name-label" class="property-label"><g:message code="disease.name.label" default="Name" /></span>
					
						<span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${diseaseInstance}" field="name"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${diseaseInstance?.symptomDisease}">
				<li class="fieldcontain">
					<span id="symptomDisease-label" class="property-label"><g:message code="disease.symptomDisease.label" default="Symptom Disease" /></span>
					
						<g:each in="${diseaseInstance.symptomDisease}" var="s">
						<span class="property-value" aria-labelledby="symptomDisease-label"><g:link controller="symptomDisease" action="show" id="${s.id}">${s?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${diseaseInstance?.id}" />
					<g:link class="edit" action="edit" id="${diseaseInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
