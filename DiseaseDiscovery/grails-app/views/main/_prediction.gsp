<g:hiddenField name="predicted_disease" value="${predictedDisease.name}"/>

<div class="row">
	<h4 class="col-md-offset-8 col-md-3">
		Match: ${predictedWeight}
	</h4>
</div>

<br/><p><h4>Classifier results:</h4></p>
<table class="table table-striped table-bordered table-hover">
	<thead>	
		<tr>
			<th>Model</th>
			<th>Predicted Disease</th>
			<th>Model Weight</th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${modelInfo}" status="i" var="inst">
			<tr>
				<td>
					${inst.key}
				</td>
				<td>
					${inst.value[0].name}
				</td>
				<td>
					${inst.value[1]}
				</td>
			</tr>
		</g:each>
	</tbody>
</table>
	
