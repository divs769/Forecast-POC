var chart;
var endpoint = 'http://fore-fore-1x6uuyrlvli7e.7fpq3x5t7a.eu-west-1.elasticbeanstalk.com/'
//.var endpoint='http://localhost:5000';
var series = [];
var markedModels = []
var lastRefresh = []
$(document).ready(function () {
  configureXEditable();

  // Initialize the model dropdown
  $('#modelSelect').multiselect({
    includeSelectAllOption: false,
    buttonWidth: '200px'
  });

  chart = new Highcharts.Chart({
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

  $('#searchBtn').click(function () {
    var weeks = $('#weeksSpinner').val().trim();
    var searchByValue = $('#searchByInput').val();
    var startDate = $("input[name='date']")[0].value
    console.log(weeks, searchByValue, startDate)
    loadBackEndData(weeks, searchByValue, startDate, hierarchyType, function () {
      var availableModels = getAvailableModels();
      var sales = getSalesData();

      if (availableModels.length > 0 || sales.length > 0) {
        deleteOldCharts();
        updateModelSelect(availableModels);

        for (i = 0; i < sales.length; i++) {
          series.push({ name: sales[i].name, data: sales[i].values })
        }
        if(markedModels.length == 0){
          markBestModel(series, availableModels)
        }else{
          markedPreviouslyMarkedModels(series, availableModels)
          markedModels = []
        }
        lastRefresh = getSelectedModels()
        
        showModelSelectForm(true);
        loadAndShowProductImg(searchByValue);

        createDataModals(lastRefresh)
        refreshGraph(series);
      } else {
        alert("Sorry, " + getHierarchyValue(hierarchyType) + " not found.")
      }

    });
  });

  function configureXEditable() {
    $.fn.editableform.buttons =
    '<button type="submit" class="btn btn-primary btn-sm editable-submit">'+
      '<i class="fa fa-fw fa-check"></i>'+
    '</button>'+
    '<button type="button" class="btn btn-default btn-sm editable-cancel">'+
      '<i class="fa fa-fw fa-times"></i>'+
    '</button>';
  }

  function loadAndShowProductImg(product) {
    $("#productImageCard").show();
  }

  function updateModelSelect(models) {
    var options = [];
    var modelSelect = $("#modelSelect");
    for (i = 0; i < models.length; i++) {
      options.push({ label: models[i].name, value: models[i].name });
    }
    modelSelect.multiselect('dataprovider', options);
    modelSelect.multiselect('refresh');
  }

  //Add listener to the collapse to change the icon according to it's state
  $('.collapse').on('shown.bs.collapse', function () {
    $(this)
      .parent()
      .find(".fa-search-plus")
      .removeClass("fa-search-plus")
      .addClass("fa-search-minus");
  }).on('hidden.bs.collapse', function () {
    $(this)
      .parent()
      .find(".fa-search-minus")
      .removeClass("fa-search-minus")
      .addClass("fa-search-plus");
  });


  refreshBtnFn();


  function drawRow(rowData) {
    var row = $("<tr />")
    $("#historyData").append(row); //this will append tr element to table... keep its reference for a while since we will add cels into it
    row.append($("<td>" + Highcharts.dateFormat('%b %e %Y', rowData.x) + "</td>"));
    row.append($("<td>" + rowData.y + "</td>"));

  }


  $('#searchTypeSelect').change(function () {
    var id = $(this).val()
    hierarchyType = id
  });

  //Initialize the Weeks spinner
  $('.spinner .btn:first-of-type').on('click', function () {
    $('.spinner input').val(parseInt($('.spinner input').val(), 10) + 1);
  });
  $('.spinner .btn:last-of-type').on('click', function () {
    if (parseInt($('.spinner input').val()) > 1) {
      $('.spinner input').val(parseInt($('.spinner input').val(), 10) - 1);
    }
  });

  showModelSelectForm(false)
});

function showModelSelectForm(show) {
  var modelSelectForm = $("#modelSelectForm");
  if (!show) {
    modelSelectForm.hide();
  } else {
    modelSelectForm.show();
  }
}

function createEditModelTable(model) {
  var tbody = $('<tbody></tbody>');
  for (var i = 0; i < model.values.length; i++) {
    var row = '<tr>';
    row += "<td>" + Highcharts.dateFormat('%d/%m/%Y', model.values[i].x) + "</td>";
    var editableValue = '<a href="#" id="value-'+model.values[i].y+'" data-type="number" data-url="/post" data-title="Enter value">' + model.values[i].y + '</a>'
    row += "<td class='edit'>" + editableValue + "</td>";
    row += '</tr>'
    tbody.append(row);
  }
  var table = $('<table class="table editModelTable"></table>');
  table.append('<thead><tr><th>Date</th><th>Total Stock</th></tr></thead>');
  table.append(tbody);
  return table;
}

function createDataModalForModels(model) {
  var clonedModel = model.clonedModel != undefined
  var dialogId = model.name.split(' ').join('_') + "Modal";
  var table = createEditModelTable(model);

  var tableContainer = $('<div class="table-responsive"></div>');
  tableContainer.append(table);

  var modalHeader = $('<div class="modal-header"></div>');
  var modalHeaderContainer = $('<div class="container"></div>');
  var originModelName = model.name
  var newModelName = model.name + ' Copy'
  var reasonForChange = ''
  if(clonedModel){
    originModelName = model.clonedModel
    newModelName = model.name
    reasonForChange = model.comment
  }
  var originModelNameDiv = $('<div class="row"><h4>Origin</h4><input class="originModelName form-control" style="font-size: 18px" type="text" value="' + originModelName + '" disabled></input></div>');
  var newModelNameDiv = $('<div class="row"><h4>Model name</h4><input class="modelName form-control" style="font-size: 18px" type="text" value="' + newModelName + '"></input></div>');
  modalHeaderContainer.append(originModelNameDiv);
  modalHeaderContainer.append(newModelNameDiv);

  modalHeader.append(modalHeaderContainer);

  var modalBody = $('<div class="modal-body" ></div>');
  
  var divRowTable = $('<div class="row" style="height: 350px; overflow-y: auto"></div>');
  divRowTable.append(tableContainer);
  
  var divRowReasonChange = $('<div class="row" style="margin-top: 10px"></div>');
  var reasonForChangeTextArea = $('<div class="card w-100"><div class="card-header">Reason for Change</div><div class="card-body"><textarea class="reasonChange form-control" id="reasonForChange" rows="3">'+reasonForChange+'</textarea></div></div>');
  divRowReasonChange.append(reasonForChangeTextArea);

  modalBody.append(divRowReasonChange);
  modalBody.append(divRowTable);
  
  var modalFooter;
  
  var closeButton = '<button type="button" class="btn btn-danger" data-dismiss="modal">Close</button></div>'
  if(clonedModel){
    var editButton = '<div class="modal-footer"><button type="button" class="btn btn-default editModelBtn">Edit</button>'
    modalFooter = $(editButton+closeButton);
  }else{
    var saveButton = '<div class="modal-footer"><button type="button" class="btn btn-default saveModelBtn">Save</button>'
    modalFooter = $(saveButton+closeButton);
  }
  
  var modalContent = $('<div class="modal-content" style="padding: 15px"></div>');
  modalContent.append(modalHeader);
  modalContent.append(modalBody);
  modalContent.append(modalFooter);

  var modalDialog = $('<div class="modal-dialog modal-lg" ></div>');
  modalDialog.append(modalContent);

  var modal = $('<div class="modal fade" id="' + dialogId + '" role="dialog" ></div>');
  modal.append(modalDialog);

  $('#editModelContainer').append(modal);

  //Making all the table cells with edit class editable
  $('.editModelTable').editable({
    selector: 'a',
    mode: 'inline',
  });
 
  $('#'+dialogId).find('.editModelBtn').click(function(e) {
    if($("#"+dialogId).is(':visible')){
      editModel(dialogId, model.id, function(){
         $("#"+dialogId).modal('hide');
         markedModels = lastRefresh
         var newName = $('#'+dialogId).find('.modelName').val()
         if(newName != model.name){
          var index = markedModels.indexOf(model.name)
          if(index != -1){
            markedModels[i] = newName
          }
         }
         $('#searchBtn').click()
      });
    }
    
  });

  $('#'+dialogId).find('.saveModelBtn').click(function(e) {
    if($("#"+dialogId).is(':visible')){
      console.log('Saving and closing ', dialogId);
      saveModel(dialogId, function(){
         $("#"+dialogId).modal('hide');
        markedModels = lastRefresh
        markedModels.push($('#'+dialogId).find('.modelName').val())
        $('#searchBtn').click()
      });
    }
  });
}

function createModel(dialogId, id){
  var originModelName = $('#'+dialogId).find('.originModelName').val();//$('#originModelName').val();
  var modelName = $('#'+dialogId).find('.modelName').val();//$('#modelName').val();
  var reasonChange = $('#'+dialogId).find('.reasonChange').val();//$('#reasonChange').val();
  var itemValue = $('#searchByInput').val();

  var values = new Array();
  $('#'+dialogId).find('.editModelTable tbody tr').each(function(){
    var rowValue = {};
    $(this).find('td').each(function(column, td) {
      if (column != 1){
        rowValue.date = $(this).text()
      } else {
        rowValue.stock = $(this).find('a').text();
      }  
    });
    values.push(rowValue);
  });

  return {
    id: id,
    item: {
        hierarchyType: hierarchyTypeEnums[$('#searchTypeSelect').val()],
        item: itemValue
    },
    clonedModel: originModelName,
    name: modelName,
    comment: reasonChange,
    values: values 
  }
}

var hierarchyTypeEnums = { "lineNumber": "LINE_NUMBER", "product": "PRODUCT", "category": "CATEGORY" }
function saveModel(dialogId, callback) {
  var newModel = createModel(dialogId)
  console.log('New model', newModel);

   $.ajax({
    headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
    },
    crossDomain: true,
    dataType: 'json',
    type: 'post',
    url: endpoint + '/model',
    data: JSON.stringify(newModel),
    success: function (data) {
      console.log(data)
      callback();
    },
    error: function (error) {
      console.log(error);
      alert(error.responseText);
    }
  });
}

function editModel(dialogId, id, callback) {
  var newModel = createModel(dialogId, id)
  console.log('New model', newModel);

   $.ajax({
    headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
    },
    crossDomain: true,
    type: 'put',
    url: endpoint + '/model',
    data: JSON.stringify(newModel),
    success: function (data) {
      console.log(data)
      callback();
    },
    error: function (error) {
      console.log(error);
      alert(error.responseText);
    }
  });
}

function getSelectedModels(){
  var selectedIds = [];
  $('#modelSelect :selected').each(function (i, selectedElement) {
    selectedIds.push($(selectedElement).val());
  });
  return selectedIds
}

function createDataModals(selectedIds){
  $('#editModelContainer').children().each(function(i, elem){
    elem.remove()
  })
  models = getModel(selectedIds);
  for (i = 0; i < models.length; i++) {
    createDataModalForModels(models[i]);
  }
}

function refreshBtnFn() {
  $("#refreshBtn").click(function () {
    var selectedIds = getSelectedModels()
    lastRefresh = selectedIds
    createDataModals(selectedIds)
    loadGraphData(series);
    refreshGraph(series);
  });
}

function markedPreviouslyMarkedModels(series, availableModelData) {
  var modelsToMark = availableModelData.filter(model => markedModels.indexOf(model.name) !== -1)
  for(let modelToMark of modelsToMark){
    series.push({ name: modelToMark.name, data: modelToMark.values, error: modelToMark.error });
  }
   $("ul.multiselect-container > li > a > label.checkbox > input[type='checkbox']").each(function(i, elem){
    if(markedModels.indexOf(elem.value) !== -1){
      elem.click()
    }
  });

}

function markBestModel(series, availableModelaData) {
  if (availableModelaData.length > 0) {
    series.push({ name: availableModelaData[0].name, data: availableModelaData[0].values, error: availableModelaData[0].error });
    $("ul.multiselect-container > li > a > label.checkbox > input[type='checkbox']").first().click();
  }

}

function loadGraphData(series) {
  var selectedIds = [];

  $('#modelSelect :selected').each(function (i, selectedElement) {
    selectedIds.push($(selectedElement).val());
  });
  models = getModel(selectedIds);
  for (i = series.length - 1; i > 0; i--) {
    if (!findElemArray(series[i], models)) {
      series.splice(i, 1);
    }
  }
  for (i = 0; i < models.length; i++) {
    if (!findElemArray(models[i], series)) {
      series.push({ name: models[i].name, data: models[i].values, error: models[i].error })
    }

  }
  return series;
}
function findElemArray(element, arr) {
  return arr.find(function (elem) {
    return element.name == elem.name;
  })
}
function refreshGraph(series) {

  var seriesLength = chart.series.length;
  for (var i = seriesLength - 1; i > -1; i--) {
    chart.series[i].remove();
  }
  for (i = 0; i < series.length; i++) {
    console.log(series[i])
    chart.addSeries(series[i])
  }
  chart.redraw();
  updateErrorCharts(series)
}

function deleteOldCharts() {
  series = []
  var seriesLength = chart.series.length;
  for (var i = seriesLength - 1; i > -1; i--) {
    chart.series[i].remove();
  }
  $("#containers > div").remove()
  $('#modelSelect').children().remove()
}

function updateErrorCharts(series) {
  $("#containers > div").each(function (i, selectedElement) {
    if (!findElemArray(selectedElement.id, series)) {
      selectedElement.remove();
    }
  });
  var ids = $('#containers > div').map(function () {
    return $(this).attr('id');
  }).get();
  for (i = 1; i < series.length; i++) {
    if (!findElemArray(series[i], ids)) {
      createErrorChart(series[i]);
    }
  }
}

function createErrorChart(model) {
  var color;
  if (model.error != null) {
    if (model.error <= 30) {
      color = 'rgb(140, 227, 5)'; //green
    } else if (model.error <= 60) {
      color = '#f7f75c'; //yellow
    } else {
      color = '#f75454'; //red
    }
    var modelNameWithoutSpaces = model.name.split(' ').join('_')
    // The Error gauge
    $("#containers").append('<div id="' + modelNameWithoutSpaces + '" style="padding-right: 40px; text-align:center; float: center; font-weight: bold;">' +
      '<div>' + model.name + ' error </div>' +
      '<div data-toggle="modal" data-target="#' + modelNameWithoutSpaces + 'Modal" style="cursor: hand; border-radius: 50%; width: 100px; height: 100px; background-color:' + color + ';">' +
      '<span style="position: relative; top: 37%;">' + model.error.toFixed(2) + "%" + '</span></div>' +
      // '<div><button type="button" class="btn btn-info btn-lg" data-toggle="modal" data-target="#' + model.name.trim() + 'Modal"><i class="fa fa-edit"></button></div>' +
      '</div>')
  }

}

var modelData = [];
var salesData = [];
var hierarchyTypes = { "lineNumber": "line number", "product": "product name", "category": "category name" }

function loadBackEndData(weeks, hierarchyValue, startDate, hierarchyType, callback) {
  var datePath = ""
  if (hierarchyValue == "") {
    alert("Please enter the " + hierarchyTypes[hierarchyType])
  } else {
    if (startDate !== "") {
      datePath += "/" + startDate
    }
    $.ajax({
      dataType: 'json',
      headers: {
        'X-Hello': 'World',
        Accept: "application/json",
        "Access-ContrloadBackEndDataol-Allow-Origin": "*"
      },
      type: 'GET',
      url: endpoint + '/forecast/' + weeks + '/' + hierarchyType + '/' + hierarchyValue + datePath,
      success: function (data) {
        console.log(data)
        modelData.length = data.forecastings.length
        for (i = 0; i < data.forecastings.length; i++) {
          var forecastingList = [];
          var foreCastingName = 'ForeCasting using ' + data.forecastings[i].name;
          for (x = 0; x < data.forecastings[i].forecastedValues.length; x++) {
            forecastingList.push({
              x: getNewDate(data.forecastings[i].forecastedValues[x].date),
              y: data.forecastings[i].forecastedValues[x].stock
            });
          }
          modelData[i] = {
            id: data.forecastings[i].id, 
            name: data.forecastings[i].name,
            error: data.forecastings[i].error,
            values: forecastingList,
            clonedModel: data.forecastings[i].clonedModel,
            comment: data.forecastings[i].comment
          };

        }
        var historyDataList = [];
        for (j = 0; j < data.historicData.length; j++) {
          historyDataList.push({ x: getNewDate(data.historicData[j].date), y: data.historicData[j].stock });
        }
        if (historyDataList.length > 0) {
          salesData[0] = {
            id: i + 1, name: 'Sales Data',
            values: historyDataList
          };
        } else {
          salesData.length = 0
        }


        callback();
      },
      error: function (data) {
        alert("error");
      }
    });
  }
}
function getSalesData() {
  return salesData;
}

function getNewDate(date) {
  return new Date(date[0], date[1] - 1, date[2]);
}

function getHierarchyValue(id) {
  return hierarchyTypes[id]
}

function getAvailableModels() {
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