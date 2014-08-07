var getComponentURL;

function setup(ajaxClassifiersURL, ajaxSaveWeightsURL){
	setModelSliders(ajaxClassifiersURL);
	$("#btn_defaultWeights").click(function(){setModelSliders(ajaxClassifiersURL)});
	if(!$("#sliders").html()){
		$("#alert").html('<div class="alert alert-danger alert-dismissable fade in">' +
			     '<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>' +
			     '<strong> ERROR: </strong> There are no classifiers. You can upload them in [FOLDER HERE].' +
			     '</div>');
	}
	
	$("#btn_saveWeights").click(function(){setWeights(ajaxSaveWeightsURL)});
}

function setWeights(URL){
	var weights = [];
	$('[id^="name_"]').each(function(){
		weights.push({
			'classifier' : this.innerHTML,
			'weight' : $("#val_" + this.innerHTML).html()
		});
	});
	$.when(
			$.ajax({
			type: "POST",
			url: URL,
			data: {'weights': JSON.stringify(weights)},
			traditional: true,
			dataType: "html",
			success : function(response){
				//alert(response.disease);
			}
		})
	).then(function(response){
		$("#alert").html('<div class="alert alert-success alert-dismissable fade in">' +
			     '<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>' +
			     '<strong> SUCCESS: </strong> New weights saved.' +
			     '</div>');
	});
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

function removeDisabledLinks(){
	$("li[class=disabled]").each(function(i, el){
		$(el).find('a').removeAttr('data-toggle')/*.click(function (e) {
			  e.preventDefault();
		})*/;
	});
}