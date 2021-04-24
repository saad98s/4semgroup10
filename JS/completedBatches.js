var HttpClient = function () {
  this.get = function (aUrl, aCallback) {
    var anHttpRequest = new XMLHttpRequest();
    anHttpRequest.onreadystatechange = function () {
      if (anHttpRequest.readyState == 4 && anHttpRequest.status == 200)
        aCallback(anHttpRequest.responseText);
    };

    anHttpRequest.open("GET", aUrl, true);
    anHttpRequest.send(null);
  };
};

function clearList() {
  document.getElementById("batchIDList").innerHTML = "Batch ID - ";
  document.getElementById("productTypeList").innerHTML = "Product type - ";
  document.getElementById("machineSpeedList").innerHTML = "Machine speed - ";
  document.getElementById("amountOfProductsList").innerHTML =
    "Amount of products - ";
  document.getElementById("acceptableProductsList").innerHTML =
    "Acceptable products - ";
  document.getElementById("defectProductsList").innerHTML =
    "Defect products - ";
  document.getElementById("oeeList").innerHTML = "OEE of batch - ";
  document.getElementById("errorList").innerHTML = "";
  clearGraphs();
}

var client = new HttpClient();

function updateReport() {
  clearList();
  setTimeout(getBatch, 100);
}

function getBatch() {
  var s = "";
  var simpleDataString;
  var timeString = "";
  var timeStringArray;
  var tempString = "";
  var humidityString = "";
  var errorString = "";
  var errorStringArray;

  var requestedBatchID = document.getElementById("batchID").value;
  client.get(
    "http://localhost:8080/DB/getBatchReport?batchID=" + requestedBatchID,
    function (response) {
      console.log(response);
      s = response.split("[");
      console.log(s);

      //Handling of simple data
      simpleDataString = s[0].split(",");
      console.log(simpleDataString);
      document.getElementById("batchIDList").innerHTML =
        "Batch ID : " + parseInt(simpleDataString[0]);
      document.getElementById("productTypeList").innerHTML =
        "Product type : " + parseInt(simpleDataString[1]);
      document.getElementById("machineSpeedList").innerHTML =
        "Machine speed : " + parseInt(simpleDataString[2]);
      document.getElementById("amountOfProductsList").innerHTML =
        "Amount of products : " + parseInt(simpleDataString[3]);
      document.getElementById("acceptableProductsList").innerHTML =
        "Acceptable products : " + simpleDataString[4];
      document.getElementById("defectProductsList").innerHTML =
        "Defect products : " + simpleDataString[5];
      document.getElementById("oeeList").innerHTML =
        "OEE of batch : " + simpleDataString[6];

      //Handling of values for "Time spent in states"
      timeString = s[1].substring(0, s[1].length - 2);
      timeString = timeString.replace(/\s/g, "");
      console.log("Time: " + timeString);
      document.getElementById("hiddenString").value = timeString + ";";
      timeStringArray = timeString.split(",");

      //Handling of values for "Temperature"
      tempString = s[2].substring(0, s[2].length - 2);
      tempString = tempString.replace(/\s/g, "");
      console.log("Temperature: " + tempString);
      document.getElementById("hiddenString").value += tempString + ";";
      tempStringArray = tempString.split(",");

      //Handling of values for "Humidity"
      humidityString = s[3].substring(0, s[3].length - 2);
      humidityString = humidityString.replace(/\s/g, "");
      console.log("Humidity: " + humidityString);
      document.getElementById("hiddenString").value += humidityString;
      humidityStringArray = humidityString.split(",");

      //Handling of Error log
      errorString = s[4].substring(0, s[4].length - 1);
      console.log("Errors: " + errorString);
      errorStringArray = errorString.split(",");
      for (let errorI = 0; errorI < errorStringArray.length; errorI++) {
        document.getElementById("errorList").innerHTML +=
          "<li>" + errorStringArray[errorI] + "</li>";
      }
      makeGraphs();
    }
  );
}
