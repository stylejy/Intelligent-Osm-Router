var coordinates;
var marker = [];

var isSourceFixed = false;
var isTargetFixed = false;
var isMarkerCreated = [false, false];

function onMapClick(e) {
    coordinates = e.latlng;
    markerController();
    choosePoints();
}

mymap.on('click', onMapClick);

function choosePoints() {
    if (isSourceFixed == false) {
        document.getElementById("source").innerHTML = 'Chosen Source Point -> ' + shapeCoordinates()
            + selectPointButton("sourceBtn");
        document.getElementById("sourceBtn").addEventListener("click", clickSourceButton);
    } else if (isTargetFixed == false) {
        document.getElementById("target").innerHTML = 'Chosen Target Point -> ' + shapeCoordinates() + selectPointButton("targetBtn");
        document.getElementById("targetBtn").addEventListener("click", clickTargetButton);
    }
}

function selectPointButton(element) {
    return '&nbsp&nbsp&nbsp<button type=button id="' + element + '" class="btn btn-success btn-xs">Select Point</button>';
}

function shapeCoordinates() {
    return '( Latitude: ' + coordinates.lat + ' )&nbsp&nbsp&nbsp( Longitude: ' + coordinates.lng + ' )';
}

function clickSourceButton() {
    isSourceFixed = true;
    document.getElementById("source").innerHTML
        = shapeCoordinates() + '&nbsp&nbsp&nbsp<button type=button id=changeSource class="btn btn-warning btn-xs">Source Point Fixed</button>';
    sendLatlngToServer(coordinates.lat, coordinates.lng);
}

function clickTargetButton() {
    isTargetFixed = true;
    document.getElementById("target").innerHTML
        = shapeCoordinates() + '&nbsp&nbsp&nbsp<button type=button id=changeTarget class="btn btn-warning btn-xs">Target Point Point</button>';
    sendLatlngToServer(coordinates.lat, coordinates.lng);
}

function markerController() {
    var switcher;

    if (isSourceFixed == false || isTargetFixed == false) {
        if (isSourceFixed == false) {
            switcher = 0;
        } else {
            switcher = 1;
        }

        if (isMarkerCreated[switcher] == false) {
            marker[switcher] = L.marker([coordinates.lat, coordinates.lng]);
            marker[switcher].addTo(mymap);
            isMarkerCreated[switcher] = true;
        } else {
            marker[switcher].setLatLng([coordinates.lat, coordinates.lng]);
        }
        if (switcher == 0) {
            marker[switcher].bindPopup("<b>For the source point</b><br>Press Select Point button!!").openPopup();
        } else {
            marker[switcher].bindPopup("<b>For the target point</b><br>Press Select Point button!!").openPopup();
        }
    }
}

function sendLatlngToServer(lat, lng) {
    var request = new XMLHttpRequest();
    request.open("GET", "/overpass?lat=" + lat + "&lng=" + lng, true);
    request.send();
}

