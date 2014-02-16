<%@ page import="com.Symptom" %>



<div class="fieldcontain ${hasErrors(bean: symptomInstance, field: 'name', 'error')} required">
	<label for="name">
		<g:message code="symptom.name.label" default="Name" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="name" required="" value="${symptomInstance?.name}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: symptomInstance, field: 'symptomDisease', 'error')} ">
	<label for="symptomDisease">
		<g:message code="symptom.symptomDisease.label" default="Symptom Disease" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${symptomInstance?.symptomDisease?}" var="s">
    <li><g:link controller="symptomDisease" action="show" id="${s.id}">${s?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="symptomDisease" action="create" params="['symptom.id': symptomInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'symptomDisease.label', default: 'SymptomDisease')])}</g:link>
</li>
</ul>

</div>

