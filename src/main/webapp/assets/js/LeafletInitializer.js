/**
 * Created by stylejy on 16/07/2017.
 */
var mymap = L.map('leaflet').setView([51.505, -0.09], 13);

L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
    attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>',
    maxZoom: 18,
    id: 'mapbox.streets',
    accessToken: 'pk.eyJ1Ijoic3R5bGVqeSIsImEiOiJjajU3NWhzaXUxcjMwMnlseWt2NDRtcGk3In0.Lxh3yrZYpsMnUOmV9Kp50w'
}).addTo(mymap);

//To get path raw data
var request = new XMLHttpRequest();

request.onreadystatechange = function() {
    jsontext = request.responseText

    //alert(jsontext);
    path = jsontext;

    var str = path;
    var result = JSON.parse(str);

    var latlngs = [result];
    //alert("latlng" + latlngs);
    var polyline = L.polyline(latlngs, {color: 'green', opacity: 0.5}).addTo(mymap);
// zoom the map to the polyline
    mymap.fitBounds(polyline.getBounds());
}

request.open("GET", "/path", true);
request.send();
