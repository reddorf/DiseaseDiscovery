<table class="table table-striped table-bordered table-hover">
	<tbody>
		<g:each in="${instanceList}" status="i" var="instance">
			<tr>
				<td>
					<div class="col-lg-10">${instance.name}</div>
					<g:if test="${addButton}">
						<button class="col-lg-2 btn btn-default" onclick="addSymptomToList('${instance.name}', ${instance.id})"
	        				type="button">
	        				Use in prediction
	        			</button>
					</g:if>		
					<g:if test="${searchButton}">
						<button class="col-lg-2 btn btn-default" onclick="search('${instance.name}')"
	        				type="button">
	        				Search in NIH
	        			</button>
					</g:if>			
				</td>
			</tr>
		</g:each>
	</tbody>
</table>
<%--<div class="pagination">--%>
<%--	<g:paginate total="${instanceTotal}" />--%>
<%--</div>--%>