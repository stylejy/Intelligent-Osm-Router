var numOfVisit;
var maxRadius;
var prefShopping;
var prefParks;
var prefPubs;


document.getElementById("pref-submit").addEventListener("click", controller);

function controller() {
    var numOfVisitElem = document.getElementById("numOfVisit");
    if (numOfVisitElem.value > 0 && numOfVisitElem.value != null) {
        numOfVisit = numOfVisitElem.value;
    } else {
        alert("Please type an suitable number for num of places!!");
    }

    var maxRadiusElem = document.getElementById("maxRadius");
    if (maxRadiusElem.value > 0 && maxRadiusElem.value != null) {
        maxRadius = maxRadiusElem.value;
    } else {
        alert("Please type an suitable number for the maximum radius!!");
    }

    var prefShoppingElem = document.getElementById("shopping");
    if (prefShoppingElem.value != null) {
        prefShopping = prefShoppingElem.value;
    } else {
        alert("Please choose your preference for the shopping!!");
    }

    var prefParksElem = document.getElementById("parks");
    if (prefParksElem.value != null) {
        prefParks = prefParksElem.value;
    } else {
        alert("Please choose your preference for the park visit!!");
    }

    var prefPubsElem = document.getElementById("pubs");
    if (prefPubsElem.value != null) {
        prefPubs = prefPubsElem.value;
    } else {
        alert("Please choose your preference for the pub visit!!");
    }

    checkReady();
}

function checkReady() {
    if (numOfVisit != null && maxRadius != null && prefShopping != null && prefParks != null && prefPubs != null) {
        sendPrefsToServer(numOfVisit, maxRadius, prefShopping, prefParks, prefPubs);
    }
}

function sendPrefsToServer(numOfVisit, maxRadius, prefShopping, prefParks, prefPubs) {
    var request = new XMLHttpRequest();
    request.open("POST", "/prefsetting?numOfVisit=" + numOfVisit +
        "&maxRadius=" + maxRadius +
        "&prefShopping=" + prefShopping +
        "&prefParks=" + prefParks +
        "&prefPubs=" + prefPubs, true);
    request.send();
    window.location.href = "/pref";
}