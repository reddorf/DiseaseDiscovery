function setup(dataGetterLink, ajaxGetDiseaseURL, ajaxModelURL){
	setupAutocomplete(dataGetterLink);
	setModelSliders(ajaxModelURL);
	
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
	
	$("#btn_defaultWeights").click(function(){setModelSliders(ajaxModelURL)});
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
	$("#" + id).remove()
	//checkElementNumber();
}

function setModelSliders(modelURL){
	$.ajax({
		type: "GET",
		url: modelURL,
		async: false,
		dataType: "json",
        success : function(response) {
        	$("#sliders").html("");
        	$.each(response, function(name, val) {
        		$("#sliders").append(
        				"<div class='row'>" +
        				"<span>" + name + "</span><div style='vertical-align:middle;'><div class='slider col-md-10'>"+val+"</div><span class='value col-md-2'></span></div>" +
        				"</div>"
        				);
        	});
        }
	});
	
	var availableTotal = 1;
	var sliders = $("#sliders .slider");
	sliders.each(function() {
	    var init_value = parseFloat($(this).text());
	    $(this).siblings('.value').text((init_value*100).toFixed(3));

	    $(this).empty().slider({
	        value: init_value,
	        min: 0,
	        max: availableTotal,
	        range: "max",
	        step: 0.0001,
	        animate: 0,
	        slide: function(event, ui) {
	            
	            // Update display to current value
	            $(this).siblings('.value').text((ui.value*100).toFixed(3));

	            // Get current total
	            var total = 0;

	            sliders.not(this).each(function() {
	                total += $(this).slider("option", "value");
	            });

	            // Need to do this because apparently jQ UI
	            // does not update value until this event completes
	            total += ui.value;

	            var delta = availableTotal - total;
	            
	            // Update each slider
	            sliders.not(this).each(function() {
	                var t = $(this),
	                    value = t.slider("option", "value");

	                var new_value = value + (delta/2) ;
	                
	                if (new_value < 0 || ui.value == 1) 
	                    new_value = 0;
	                if (new_value > 1) 
	                    new_value = 1;

	                t.siblings('.value').text((new_value*100).toFixed(3));
	                t.slider('value', new_value);
	            });
	        }
	    });
	});
}

function checkElementNumber() {
	
}

function removeDisabledLinks(){
	$("li[class=disabled]").each(function(i, el){
		$(el).find('a').removeAttr('data-toggle')/*.click(function (e) {
			  e.preventDefault();
		})*/;
	});
}