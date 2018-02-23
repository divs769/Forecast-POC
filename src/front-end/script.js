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
    $('.date').datepicker({
      format: 'dd/mm/yyyy',
      autoclose: true
    });

    $('#multiselect').multiselect({
           buttonWidth: '400px'
    });

    $(document).on('click', '.number-spinner button', function () {    
        var btn = $(this),
          oldValue = btn.closest('.number-spinner').find('input').val().trim(),
          newVal = 0;
        
        if (btn.attr('data-dir') == 'up') {
          newVal = parseInt(oldValue) + 1;
        } else {
          if (oldValue > 1) {
            newVal = parseInt(oldValue) - 1;
          } else {
            newVal = 1;
          }
        }
        btn.closest('.number-spinner').find('input').val(newVal);
    });

   
    $('#searchBtn').click(function(){
        var weeks = $('.number-spinner button').closest('.number-spinner').find('input').val().trim();
        var lineNumber = $('#header-searchInput').val();
        var startDate = $("input[name='date']")[0].value
        loadBackEndData(function() {
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
  
        },weeks,lineNumber,startDate,hierarchyType);
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

    $('.dropdown-item').click(function(event) {
      var id = event.target.id
      if(hierarchyType != id){
        $("#header-searchInput").val("")
      }
      hierarchyType = id
      $('#header-searchInput').attr('placeholder', "Enter the "+getHierarchyValue(id))
    });
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