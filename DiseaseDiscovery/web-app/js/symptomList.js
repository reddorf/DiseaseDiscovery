var getComponentURL;

function setup(dataGetterLink, ajaxGetDiseaseURL, ajaxModelURL, diseasesURL, symptomsURL, getStuffURL){
	getComponentURL = getStuffURL;
	
	setupAutocomplete(dataGetterLink);
	setModelSliders(ajaxModelURL);
	
	$("#autocomplete_match").change(function(){
		setupAutocomplete(dataGetterLink);
	});
	
	$("#btn_addSymptom").click(function(){
		if($("#symptom_name").val() && $("#symptom_id").val()){
			addSymptomToList($("#symptom_name").val(), $("#symptom_id").val());
		}
	});
	
	$("#btn_submitSymptoms").click(function(){
		if(!$("#elements").children().length) {
			$("#alert").html('<div class="alert alert-warning alert-dismissable fade in">' +
				     '<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>' +
				     '<strong> WARNING: </strong> The symptom list is empty.' +
				     '</div>');
		} else {
			// TODO: ADD SPINNER
			$("#loading").show();
			$("#prediction_title").hide();
			$("#btn_submitSymptoms").prop('disabled', true);
			$("#prediction_dropdown").html("");
			$("#prediction_title").html('Predicted Disease');
			$("#disease").html("");
			
			var sympts = [];
			
			$("#elements").children().each(function() {
				sympts.push($(this).attr('id'));
			});
			
			$.when(
					$.ajax({
					type: "POST",
					url: ajaxGetDiseaseURL,
					data: {'symptoms' : sympts},
					traditional: true,
					dataType: "html",
					success : function(response){
						//alert(response.disease);
					}
				})
			).then(function(response){
				$("#loading").hide();
				$("#prediction_title").show();
				$("#btn_submitSymptoms").prop('disabled', false);
				//if(response.success){
					//$("#disease").html(response.object.name);
					$("#prediction_dropdown").html(response);
					$("#prediction_title").html('<a data-toggle="collapse" data-parent="#accordion" href="#prediction_dropdown" style="color: #000000;">Predicted Disease</a>');
					//$("#disease").html($("#predicted_disease").prop("value"));
					if($("#good_prediction").prop("value") == "true"){
						$("#disease").html($("#predicted_disease").prop("value"));
						$("#warning-icon").html('');
					}
					else {
						//$("#disease").html("<span style='color:red'>"+$("#predicted_disease").prop("value")+"</span>");
						$("#warning-icon").html("<span class='glyphicon glyphicon-warning-sign'></span>");
						$("#warning-icon").tooltip({'selector': '',
												    'placement': 'top',
												    'container':'body'});
						$("#disease").html(/*"<span style='color:red'>"+*/$("#predicted_disease").prop("value")/*+"</span>"*/);
					}
					
	//			}
	//			else {
	//				alert("An error occurred");
	//			}
			});
		}
	});
	
	$('#letter-tabs a[data-toggle="tab"]').on('show.bs.tab', function (e) {
		var target = $(e.target).attr("href") // activated tab

		$.ajax({
	        type: "GET",
	        url: target.charAt(1) == 'd' ? diseasesURL : symptomsURL,
	        data: {letter: target.substring(3)},
	        dataType: "html",
	        async: false,
	        success : function(response) {
	        	//alert(response);
	        	$(target).html(response);
	        },
	        error: function(resp){
	        	console.log("Error getting tab content");
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
                //source: data,
            	source: $("#autocomplete_match").val() == 'contains' ? data :
            			function(req, response) {
		                    var re = $.ui.autocomplete.escapeRegex(req.term);
		                    var matcher = new RegExp("^" + re, "i");
		                    response($.grep(data, function(item) {
		                        return matcher.test(item.value);
		                    }));
            	},
                select: function (event, ui){
                    $('#symptom_id').val(ui.item.id);
                }
            });
        }
    });
}

function addSymptomToList(name, id){
	if($("#elements").find("#" + id).length) {
		$("#alert").html('<div class="alert alert-warning alert-dismissable fade in">' +
			     '<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>' +
			     '<strong>' + name + '</strong> is already in the list.' +
			     '</div>');
	} else {
		$("#elements").append('<li id=\'' + id + '\'class="input-group listed-symptom">' +  // list-group-item
				  '<span class=\"form-control\">' + name + '</span>' +
				  '<hiddenfield value=\'' + id + '\'/>' +
			  	  '<span class=\"input-group-btn\">' +
					  '<button class=\"btn btn-default\" id=\"btn_deleteSymptom\" type=\"button\" + onclick=\"deleteSymptomFromList(' + id + ')\">' +
					  '<span class="glyphicon glyphicon-remove"></span>Delete' +
					  '</button>' +
				  '</span>' +
		          '</li>');
		$("#symptom_name").val("");
		
		$("#alert").html('<div class="alert alert-success alert-dismissable fade in">' +
					     '<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>' +
					     '<strong>' + name + '</strong>  added to the list.' +
					     '</div>');
	}
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
        				"<span id='name_"+name+"'>" + name + "</span><div style='vertical-align:middle;'><div class='slider col-md-10'>"+val+"</div><span id='val_"+name+"' class='value col-md-2'></span></div>" +
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

function getComponents(id, type) {
	//alert(id + " --- " + type);
	$.ajax({
		type: "GET",
		url: getComponentURL,
		async: false,
		dataType: "html",
		data: {'id' : id, 'type' : type},
        success : function(response) {
        	//alert(JSON.stringify(response, null, 4));
        	$("#body_" + id).html(response);
        	$("#toggle_" + id).prop('onclick', null);
        }
	});
}

function search(name){
	window.open("http://search2.google.cit.nih.gov/search?q=" + name + "&btnG.x=0&btnG.y=0&client=NIHNEW_frontend&proxystylesheet=NIHNEW_frontend&output=xml_no_dtd&getfields=*&proxyreload=1&btnG.x=0&btnG.y=0&sort=date%3AD%3AL%3Ad1&oe=UTF-8&ie=UTF-8&ud=1&exclude_apps=1&site=NIH_Master");
}