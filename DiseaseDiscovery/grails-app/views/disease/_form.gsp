<%@ page import="com.Disease" %>



<div class="fieldcontain ${hasErrors(bean: diseaseInstance, field: 'name', 'error')} required">
	<label for="name">
		<g:message code="disease.name.label" default="Name" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="name" required="" value="${diseaseInstance?.name}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: diseaseInstance, field: 'symptomDisease', 'error')} ">
	<label for="symptomDisease">
		<g:message code="disease.symptomDisease.label" default="Symptom Disease" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${diseaseInstance?.symptomDisease?}" var="s">
    <li><g:link controller="symptomDisease" action="show" id="${s.id}">${s?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="symptomDisease" action="create" params="['disease.id': diseaseInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'symptomDisease.label', default: 'SymptomDisease')])}</g:link>
</li>
</ul>

</div>

