document.addEventListener("DOMContentLoaded", function (e) {

    list_container = document.getElementById("container-fluid");

    // Wird zu Beginn aufgerufen, um alle Werke aus der Datenbasis anzuzeigen
    displayAllClusters();
});


function displayAllClusters() {
    var output;
    var url = "/getCl";

    
    var request = new XMLHttpRequest();
    request.open("GET", url, false);
    request.setRequestHeader('Access-Control-Allow-Origin', '*');


    request.onreadystatechange = function () {
        if (request.readyState == 4 && request.status == 200) {
            output = request.responseText;
        }
        else {
            return null;
        }
    }
    request.send();
    
    
    var array = JSON.parse(output);

    console.log(array);
    
    //var array = ["graph_cluster_0.json", "graph_cluster_10.json", "graph_cluster_12.json"]

    var unique = Array.from(new Set(array));
    //array = array.sort();

    list_container = document.getElementById("view-container");

    for(var cluster of array) {
        var name = cluster.split(".")[0];
        var id = name.split("_")[2];
        console.log(name);
        console.log(id);
        var li = document.createElement("li");
        var a = document.createElement("a");
        var image_div = document.createElement("div");
        var caption_div = document.createElement("div");
        var inner_caption_div = document.createElement("div");
        li.appendChild(a);
        a.appendChild(image_div);
        caption_div.appendChild(inner_caption_div);
        a.appendChild(caption_div);
        inner_caption_div.appendChild(document.createTextNode(name));
        a.setAttribute("class", "tile-wrapper");
        a.setAttribute("onclick", "openCluster(" + id + ")");
        image_div.setAttribute("class", "cluster-image");
        caption_div.setAttribute("class", "tile-caption");
        inner_caption_div.setAttribute("class", "inner-tile-caption");
        list_container.appendChild(li);
    }
}

function openCluster(id) {
    window.location = '/cluster?id=' + id;
}