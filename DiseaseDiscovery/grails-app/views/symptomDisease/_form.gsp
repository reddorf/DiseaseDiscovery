<%@ page import="com.SymptomDisease" %>



<div class="fieldcontain ${hasErrors(bean: symptomDiseaseInstance, field: 'disease', 'error')} required">
	<label for="disease">
		<g:message code="symptomDisease.disease.label" default="Disease" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="disease" name="disease.id" from="${com.Disease.list()}" optionKey="id" required="" value="${symptomDiseaseInstance?.disease?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: symptomDiseaseInstance, field: 'symptom', 'error')} required">
	<label for="symptom">
		<g:message code="symptomDisease.symptom.label" default="Symptom" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="symptom" name="symptom.id" from="${com.Symptom.list()}" optionKey="id" required="" value="${symptomDiseaseInstance?.symptom?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: symptomDiseaseInstance, field: 'symptomFreq', 'error')} required">
	<label for="symptomFreq">
		<g:message code="symptomDisease.symptomFreq.label" default="Symptom Freq" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="symptomFreq" value="${fieldValue(bean: symptomDiseaseInstance, field: 'symptomFreq')}" required=""/>
</div>

