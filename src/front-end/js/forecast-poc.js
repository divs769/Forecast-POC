var chart;

var series = [];
$(document).ready(function(){
    chart =  new Highcharts.Chart({
    chart: {
      type: 'line',
      renderTo: 'container'
    },
    title: {
      text: 'Forecast Demand'
    },
    subtitle: {
      text: 'POC'
    },
    credits: {
      enabled: false
    },
    xAxis: {
      type: 'datetime',
      labels: {
        format: '{value: %e %b %y}'
      }
    },
    yAxis: {
      title: {
        text: 'Stock'
      }
    },
    plotOptions: {
      series: {
        cursor: 'pointer',
        point: {
          events: {
            click: function (e) {
              hs.htmlExpand(null, {
                pageOrigin: {
                  x: e.pageX || e.clientX,
                  y: e.pageY || e.clientY
                },
                headingText: this.series.name,
                maincontentText: Highcharts.dateFormat('%A, %b %e, %Y', this.x) + ':<br/> ' +
                this.y + ' visits',
                width: 200
              });
            }
          }
        },
        marker: {
          lineWidth: 1
        }
      }
    },

  });
    var hierarchyType = "lineNumber";
    
    $('#multiselect').multiselect({
           buttonWidth: '400px'
    });

   $('.modelsSelct').hide();
   $('#collapseBtn').hide();
    $('#searchBtn').click(function(){
        $('#collapseBtn').show();
        var weeks = $('#weeksSpinner').val().trim();
        var searchByValue = $('#searchByInput').val();
        var startDate = $("input[name='date']")[0].value
        console.log(weeks, searchByValue, startDate)
        loadBackEndData(weeks, searchByValue, startDate, hierarchyType, function() {
        var availableModels = getAvailableModels();
        var sales =getSalesData();
        if(availableModels.length > 0 || sales.length > 0){
          $('#productDets').show();
          $('#salesForecastImg').hide();
          var options = [];
          for (i = 0; i < availableModels.length; i++) {
            options[i] = '<option value="' + availableModels[i].name + '">' + availableModels[i].name + '</option>';
          }
          deleteOldCharts();
          $("#modelSelect").append(options);
          $('#modelSelect').multiselect('rebuild');
      
          for(i=0;i<sales.length;i++){    
            series.push({name: sales[i].name, data: sales[i].values})
          }
          markBestModel(series, availableModels)

          refreshGraph(series);
        }else{
          alert("Sorry, "+getHierarchyValue(hierarchyType)+" not found.")
        }
  
        });
      });
    
    
    

    refreshBtnFn();
 // When the user clicks on div, open the popup
    $('#historyTable').click(function() {
       var sales =getSalesData();
        for (var i = 0; i < sales[0].values.length; i++) {
            drawRow(sales[0].values[i]);
        }


        function drawRow(rowData) {
            var row = $("<tr />")
            $("#historyData").append(row); //this will append tr element to table... keep its reference for a while since we will add cels into it
            row.append($("<td>" +Highcharts.dateFormat('%b %e %Y', rowData.x)  + "</td>"));
            row.append($("<td>" + rowData.y + "</td>"));
          
        }

    var popup = document.getElementById("myPopup");
    popup.classList.toggle("show");
  });

    $('#searchTypeSelect').change(function() {
      var id = $(this).val()
      /*if(hierarchyType != id){
        $("#searchByInput").val("")
      }*/
      hierarchyType = id
      //$('#header-searchInput').attr('placeholder', "Enter the "+getHierarchyValue(id))
    });

    //Initialize the Weeks spinner
    $('.spinner .btn:first-of-type').on('click', function () {
      $('.spinner input').val(parseInt($('.spinner input').val(), 10) + 1);
    });
    $('.spinner .btn:last-of-type').on('click', function () {
        if(parseInt($('.spinner input').val()) > 1){
           $('.spinner input').val(parseInt($('.spinner input').val(), 10) - 1);
        }
    });

    // Initialize the model dropdown
    $('#modelSelect').multiselect();
});

function refreshBtnFn(){
   $("#refreshBtn").click(function(){
      loadGraphData(series);
      refreshGraph(series);
    });
  }

function markBestModel(series, availableModelaData){
  if(availableModelaData.length > 0){
    series.push({name: availableModelaData[0].name, data: availableModelaData[0].values, error: availableModelaData[0].error});
    $("ul.multiselect-container > li > a > label.checkbox > input[type='checkbox']").first().click();
  }
  
}

function loadGraphData(series) {
  var selectedIds = [];

  $('#modelSelect :selected').each(function(i, selectedElement) {
    selectedIds.push($(selectedElement).val());
  });
  models = getModel(selectedIds);
  for(i=series.length - 1; i > 0; i--){
    if(!findElemArray(series[i], models)){
      series.splice(i, 1);
    }
  }
  for(i=0; i<models.length; i++){   
      if(!findElemArray(models[i], series)){
        series.push({name: models[i].name, data: models[i].values, error: models[i].error})
      }
      
  }
  return series;
}
function findElemArray(element, arr){
  return arr.find(function(elem) {
      return element.name == elem.name;
    })
}
function refreshGraph(series) {
    
    var seriesLength = chart.series.length;
    for(var i = seriesLength - 1; i > -1; i--) {
        chart.series[i].remove();
    }
    for (i = 0; i < series.length; i++){
      console.log(series[i])
      chart.addSeries(series[i])
    }
    chart.redraw();
    updateErrorCharts(series)
  }

  function deleteOldCharts(){
    series = []
    var seriesLength = chart.series.length;
    for(var i = seriesLength - 1; i > -1; i--) {
        chart.series[i].remove();
    }
    $("#containers > div").remove()
    $('#modelSelect').children().remove()
  }

  function updateErrorCharts(series){
    $("#containers > div").each(function(i, selectedElement) {
      if(!findElemArray(selectedElement.id, series)){
        selectedElement.remove();
      }
    });
    var ids = $('#containers > div').map(function(){
      return $(this).attr('id');
      }).get();
    for(i = 1; i < series.length; i++){
      if(!findElemArray(series[i], ids)){
        createErrorChart(series[i]);
      }
    }
  }

  function createErrorChart(model){
      var color;
      if(model.error != null){
         if(model.error <= 30){
          color = 'rgb(140, 227, 5)'; //green
        }else if(model.error <= 60){
          color = '#f7f75c'; //yellow
        }else{
          color = '#f75454'; //red
        }
         // The Error gauge
        $("#containers").append('<div id="'+model.name+'" style="padding-right: 40px; text-align:center; float: center; font-weight: bold;">'+
          '<div>' + model.name +' error </div>' +
          '<div style="border-radius: 50%; width: 100px; height: 100px; background-color:'+color+';">' +
            '<span style="position: relative; top: 37%;">' +model.error.toFixed(2)+"%" + '</span></div>' +
        '</div>')
      }
     
  }

  var modelData = [];
  var salesData =[];
  var hierarchyTypes = {"lineNumber": "line number", "product" : "product name" , "category" : "category name"}
  
  function loadBackEndData(weeks, hierarchyValue, startDate, hierarchyType, callback){
      var datePath = ""
      if(hierarchyValue == ""){
          alert("Please enter the "+hierarchyTypes[hierarchyType])
      }else{
           if(startDate !== ""){
              datePath += "/" + startDate
          }
          $.ajax({
          dataType: 'json',
          headers: {
              'X-Hello': 'World',
              Accept:"application/json",
              "Access-Control-Allow-Origin": "*"
          },
          type:'GET',
          url:'http://localhost:8080/forecast/'+weeks+'/'+hierarchyType+'/'+hierarchyValue+datePath,
          success: function(data)
          {
              console.log(data)
              modelData.length = data.forecastings.length
              for (i = 0; i < data.forecastings.length; i++) { 
                  var forecastingList = [];
                  var foreCastingName = 'ForeCasting using '+data.forecastings[i].name;
                      for(x=0; x < data.forecastings[i].forecastedValues.length; x++){
                          forecastingList.push({x: getNewDate(data.forecastings[i].forecastedValues[x].date), 
                              y: data.forecastings[i].forecastedValues[x].stock});
                      }
                  modelData[i] = {id: i+1, name: data.forecastings[i].name,
                      error: data.forecastings[i].error,
                      values:forecastingList
                 };
               
              }
              var historyDataList = [];
              for(j=0;j<data.historicData.length;j++){
                      historyDataList.push({x: getNewDate(data.historicData[j].date), y: data.historicData[j].stock});
              }
              if(historyDataList.length > 0){
                  salesData[0] = {id: i+1, name: 'Sales Data',
                      values:historyDataList};
              }else{
                  salesData.length = 0
              }
              
                      
             callback();
          },
          error: function(data)
          {
              alert("error");
          }
      });
      }
  }
      function getSalesData(){
          return salesData;
      }
  
  function getNewDate(date){
      return new Date(date[0], date[1] - 1, date[2]);
  }
  
  function getHierarchyValue(id){
      return hierarchyTypes[id]
  }
  
  function getAvailableModels(){
     return modelData;
  } 
  function getModel(names) {
      return modelData.filter(modelElem => names.indexOf(modelElem.name) !== -1)
  }
/*
  var gaugeOptions = {

    chart: {
        type: 'solidgauge'
    },

    title: null,

    pane: {
        center: ['50%', '85%'],
        size: '140%',
        startAngle: -90,
        endAngle: 90,
        background: {
            backgroundColor: (Highcharts.theme && Highcharts.theme.background2) || '#EEE',
            innerRadius: '60%',
            outerRadius: '100%',
            shape: 'arc'
        }
    },

    tooltip: {
        enabled: false
    },
    // the value axis
    yAxis: {
        stops: [
            [0.9, '#DF5353'], // red
            [0.5, '#DDDF0D'], // yellow
            [0.1, '#55BF3B'], // green
        ],
        lineWidth: 0,
        minorTickInterval: null,
        tickAmount: 2,
        title: {
            y: -70
        },
        labels: {
            y: 16
        }
    },

    plotOptions: {
        solidgauge: {
            dataLabels: {
                y: 5,
                borderWidth: 0,
                useHTML: true
            }
        }
    }
};
*/