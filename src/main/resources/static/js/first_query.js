

document.addEventListener("DOMContentLoaded", function (e) {

    var totalAmountOfDecisionsInDatabase;

    var result;
    var request = new XMLHttpRequest();
    request.open("GET", "http://localhost:8080/getDBSize", false);
    request.setRequestHeader('Access-Control-Allow-Origin', '*');
    request.onreadystatechange = function () {
        if (request.readyState == 4 && request.status == 200) {
            result = request.responseText;
        }
        else {
            return null;
        }
    }

    request.send();
    totalAmountOfDecisionsInDatabase = JSON.parse(result);
    console.log(totalAmountOfDecisionsInDatabase);
    console.log(document.getElementById("dbAmount"));
    document.getElementById("dbAmount").innerHTML = "Zurzeit sind " + totalAmountOfDecisionsInDatabase + " Gerichtsentscheidungen in der Datenbank.";

});


function searchAZ() {
    var value = document.getElementsByClassName("search-feld")[0].value;
    var url = "http://localhost:8080/decision?az=" + value;
    document.location.replace(url);

}

function searchAZByEnter() {
    if(event.key === 'Enter') {
        searchAZ();
    }
    
}