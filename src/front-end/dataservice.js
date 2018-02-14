
var model1 = {
    id: "1",
    name: "naive",
    timeFrame: ['Jan/2017', 'Feb/2017', 'Mar/2017', 'Apr/2017', 'May/2017', 'Jun/2017', 'Jul/2017', 'Aug/2017', 'Sep/2017', 'Oct/2017', 'Nov/2017', 'Dec/2017'],
    values: [
        {
            name: 'Forecast 2018',
            data: [17.0, 26.9,19.5, 24.5, 38.4, 11.5,25.2, 46.5, 53.3, 28.3, 33.9, 19.6]
        },
        {
            name: 'Sales 2017',
            data: [3.9, 4.2, 5.7, 8.5, 11.9, 15.2, 17.0, 16.6, 14.2, 10.3, 6.6, 4.8]
        }
    ],
    accuracy: 83
};

var model2 = {
    id: "2",
    name: "average",
    timeFrame: ['Jan/2017', 'Feb/2017', 'Mar/2017', 'Apr/2017', 'May/2017', 'Jun/2017', 'Jul/2017', 'Aug/2017', 'Sep/2017', 'Oct/2017', 'Nov/2017', 'Dec/2017'],
    values: [
                {
                    name: 'Forecast 2018',
                    data: [7.0, 6.9, 9.5, 14.5, 18.4, 21.5, 25.2, 26.5, 23.3, 18.3, 13.9, 9.6]
                },
                {
                    name: 'Sales 2017',
                    data: [3.9, 4.2, 5.7, 8.5, 11.9, 15.2, 17.0, 16.6, 14.2, 10.3, 6.6, 4.8]
                }
            ],
    accuracy: 20
};

var modelData = [model1, model2];
var forecastingList = [];
var historyDataList = [];
var modelList = [];
$.ajax(
    {
        dataType: 'json',
        headers: {
            'X-Hello': 'World',
            Accept:"application/json",
            "Access-Control-Allow-Origin": "*"
        },
        type:'GET',
        url:'http://localhost:8080/forecast/4',
        success: function(data)
        {
            console.log(modelList.length);
            for (i = 0; i < data.forecastings.length; i++) { 
                modelList.push({id: i+1, name: data.forecastings[i].name}); 
            }
            getAvailableModels()  ;
            console.log(modelList.length);
        },
        error: function(data)
        {
            alert("error");
        }
    });

function getAvailableModels(){
    return modelList;
} 

function getModel(id) {
    var modelDetailsList = [];
    console.log(id);
    for (i = 0; i < modelData.length; i++) {
        for (x = 0; x < id.length; x++) {
            console.log("Comparing models ", modelData[i].id, id[x])
            if (modelData[i].id === id[x]) {
                console.log("Adding model")
                modelDetailsList.push(modelData[i]);
            }
        }
    }
    console.log("Model Details List", modelDetailsList)
    return modelDetailsList;
}