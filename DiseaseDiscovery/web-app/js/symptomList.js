function setup(dataGetterLink, ajaxGetDiseaseURL){
	setupAutocomplete(dataGetterLink);
	
	$("#btn_addSymptom").click(function(){
		if($("#symptom_name").val() && $("#symptom_id").val()){
			addSymptomToList($("#symptom_name").val(), $("#symptom_id").val());
		}
	});
	
	$("#btn_submitSymptoms").click(function(){
		// TODO: SUBMIT
		sympts = [];
		$("#elements").children().each(function() {
			sympts.push($(this).attr('id'));
		});

//		$.ajax({
//			type: "POST",
//			url: ajaxGetDiseaseURL,
//			data: {'symptoms' : sympts},
//			traditional: true,
//			//dataType: "json",
//			success : function(response){
//				alert(response.disease);
//			}
//		});
		$.when(
				$.ajax({
				type: "POST",
				url: ajaxGetDiseaseURL,
				data: {'symptoms' : sympts},
				traditional: true,
				//dataType: "json",
				success : function(response){
					//alert(response.disease);
				}
			})
		).then(function(response){
			if(response.success){
				$("#disease").html(response.object.name);
//				alert(response.object.name);
			}
			else {
				alert("An error occurred");
			}
		});
	});
}

function setupAutocomplete(dataGetterLink) {
    $.ajax({
        type: "GET",
        url: dataGetterLink,
        dataType: "json",
        success : function(response) {
            var data =
                $.map(response, function(symptom){
                    return{
                        id: symptom.id,
                        value: symptom.name
                    }
                });
 
            $("#symptom_name").autocomplete({
                source: data,
                select: function (event, ui){
                    $('#symptom_id').val(ui.item.id);
                }
            });
        }
    });
}

function addSymptomToList(name, id){
	$("#elements").append('<li id=\'' + id + '\'class="input-group">' +  // list-group-item
								'<span class=\"form-control\">' + name + '</span>' +
								'<hiddenfield value=\'' + id + '\'/>' +
								'<span class=\"input-group-btn\">' +
									'<button class=\"btn btn-default\" id=\"btn_deleteSymptom\" type=\"button\" + onclick=\"deleteSymptomFromList(' + id + ')\">' +
									'Delete' +
									'</button>' +
								'</span>' +
						  '</li>');
	$("#symptom_name").val("");
}

function deleteSymptomFromList(id){
	$("#sympt." + id).remove()
	//checkElementNumber();
}

function checkElementNumber() {
	
}