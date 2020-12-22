document.addEventListener("DOMContentLoaded", function (e) {
    var dns = document.getElementsByClassName("docketnumber");

    for(var element of dns) {
        var string = element.innerHTML;
        string = string.replace("[", "");
        string = string.replace("]", "");
        element.innerHTML = string;

        //Set URL
        var a = element.parentElement.parentElement.parentElement;
        a.href = "http://localhost:8080/decision?az=" + string; 
    }

    var norms = document.getElementsByClassName("norms");
    for(var el of norms) {
        var string = el.innerHTML;
        string = string.replace("[", "");
        string = string.replace("]", "");
        el.innerHTML = string;
    }

});