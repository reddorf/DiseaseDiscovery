function setupSymptom(dataGetterLink) {
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
                    $('#symptom_id').val(ui.symptom.id);
                }
            });
        }
    });
}