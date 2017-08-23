var coordinates;
var marker;
var isSourceFixed = false;
var isMarkerCreated = false;


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
    document.getElementById("getpath").innerHTML = '<button type=button id=getPathBtn class="btn btn-primary btn-xs">->Get the path!</button>'
    document.getElementById("getPathBtn").addEventListener("click", getPath);
}

function markerController() {
    if (isMarkerCreated == false) {
        marker = L.marker([coordinates.lat, coordinates.lng]);
        marker.addTo(mymap);
        isMarkerCreated = true;
    } else if (isSourceFixed == false) {
        marker.setLatLng([coordinates.lat, coordinates.lng]);
    }
}

function sendLatlngToServer(lat, lng) {
    var request = new XMLHttpRequest();
    request.open("GET", "/overpass?lat=" + lat + "&lng=" + lng, true);
    request.send();
}

function getPath() {
    //To get path raw data
    var request = new XMLHttpRequest();
    request.open("GET", "/pathforprefs", true);
    request.onreadystatechange = function() {
        if (request.readyState == 4) {
            if (request.status == 200 || request.status == 0) {
                var jsontext = request.responseText;
                //alert(jsontext);
                var result = JSON.parse(jsontext);

                for (var i in result) {
                    var polyline = L.polyline(result[i], {color: getRandomColour(), opacity: 0.5}).addTo(mymap);
                }
                // zoom the map to the polyline
                mymap.fitBounds(polyline.getBounds());

                getPlaces();
            }
        }

    };
    request.send();
}

function getRandomColour() {
    var color = '#';
    var base = '0123456789ABCDEF'.split('');
    for (var i = 0; i < 6; i++ ) {
        color += base[Math.round(Math.random() * 15)];
    }
    return color;
}

function getPlaces() {
    var request = new XMLHttpRequest();
    request.open("GET", "/sortedplaces", true);
    request.onreadystatechange = function() {
        if (request.readyState == 4) {
            if (request.status == 200 || request.status == 0) {
                var jsontext = request.responseText;
                var results = JSON.parse(jsontext);

                for (var i in results.place) {
                    var lat = results.place[i].lat;
                    var lon = results.place[i].lon;
                    var pref_type = results.place[i].pref_type;
                    var name = results.place[i].name;
                    var seq = Number(i)+1;
                    marker = L.marker([lat, lon]);
                    marker.addTo(mymap);
                    marker.bindPopup("<h3> Place " + seq + "</h3><br><h5> Pref Type : " + pref_type + "<br>Name : " + name + "</h5>").openPopup();
                }
            }
        }

    };
    request.send();
}