<table class="table table-striped table-bordered table-hover padded">
	<thead>	
		<tr>
			<th>${sympts ? 'Diseases with ' + symptom.name + ' as symtom' : 'Symptoms of disease ' + disease.name}</th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${instanceList}" status="i" var="instance">
			<tr>
				<td>
					<div class="col-lg-8">${instance.name}</div>
					<button class="col-md-offset-2 col-lg-2 btn btn-default" onclick="search('${instance.name}')"
        				type="button">
        				Search in NIH
        			</button>
				</td>
			</tr>
		</g:each>
	</tbody>
</table>