
var modelData = [];
var salesData =[];

var modelList = [];
function loadBackEndData(callback,weeks,lineNumber){
    $.ajax(
    {
        dataType: 'json',
        headers: {
            'X-Hello': 'World',
            Accept:"application/json",
            "Access-Control-Allow-Origin": "*"
        },
        type:'GET',
        url:'http://localhost:8080/forecast/'+weeks+'/'+lineNumber,
        success: function(data)
        {
            for (i = 0; i < data.forecastings.length; i++) { 
                modelList.push({id: i+1, name: data.forecastings[i].name}); 
                var forecastingList = [];
                var foreCastingName = 'ForeCasting using '+data.forecastings[i].name;
                    for(x=0; x < data.forecastings[i].forecastedValues.length; x++){
                        forecastingList.push({x: getNewDate(data.forecastings[i].forecastedValues[x].date), 
                            y: data.forecastings[i].forecastedValues[x].stockValue});
                    }
                modelData.push({id: i+1, name: data.forecastings[i].name,
                    error: data.forecastings[i].error,
                    values:forecastingList
               });
             
            }
            var historyDataList = [];
            for(j=0;j<data.historicData.length;j++){
                    historyDataList.push({x: getNewDate(data.historicData[j].date), y: data.historicData[j].stockValue});
            }
            salesData.push({id: i+1, name: 'Sales Data',
                    values:historyDataList});
                    
           callback();
        },
        error: function(data)
        {
            alert("error");
        }
    });
}
    function getSalesData(){
        return salesData;
    }

function getNewDate(date){
    return new Date(date[0], date[1] - 1, date[2]);
}

function getAvailableModels(){
    return modelList;
} 
function getAvailableModelDatas(){
    return modelData;
}
function getModel(id) {
    var modelDetailsList = [];
    for (i = 0; i < modelData.length; i++) {
        for (x = 0; x < id.length; x++) {
            console.log("Comparing models ", modelData[i].id, id[x])
            if (modelData[i].id.toString() === id[x].toString()) {
                modelDetailsList.push(modelData[i]);
            }
        }
    }
    console.log("Model Details List", modelDetailsList)
    return modelDetailsList;
}