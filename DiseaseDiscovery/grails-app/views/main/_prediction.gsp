<g:hiddenField name="predicted_disease" value="${predictedDisease.name}"/>
<g:hiddenField name="good_prediction" value="${modelInfo[predictedDisease] > 0.5}"/>

<div class="row">
	<h4 class="col-md-offset-8 col-md-3">
		Match: ${predictedWeight}
	</h4>
</div>

<br/><p><h4>Classifier results:</h4></p>
<table class="table table-striped table-bordered table-hover">
	<thead>	
		<tr>
			<th>Predicted Disease</th>
			<th>Disease Score</th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${modelInfo}" status="i" var="inst">
			<g:if test="${inst.value > 0.0}">
				<tr>
					<td>
						<a href="#" id="${inst.key.name}" onclick="showInList('${inst.key.id}', '${inst.key.name}');" style="color:black">${inst.key.name}</a>
					</td>
					<td>
						${inst.value}
					</td>
				</tr>
			</g:if>
		</g:each>
	</tbody>
</table>
	
