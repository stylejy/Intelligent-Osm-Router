/**
 * Created by stylejy on 16/07/2017.
 */

var minLat = Number(document.getElementById("leafletInit").getAttribute("data-minLat"));
var minLon = Number(document.getElementById("leafletInit").getAttribute("data-minLon"));
var maxLat = Number(document.getElementById("leafletInit").getAttribute("data-maxLat"));
var maxLon = Number(document.getElementById("leafletInit").getAttribute("data-maxLon"));

var attribution = 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors',
    mapBox = L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
        maxZoom: 20,
        minZoom: 16,
        attribution: attribution,
        id: 'mapbox.streets',
        accessToken: 'pk.eyJ1Ijoic3R5bGVqeSIsImEiOiJjajU3NWhzaXUxcjMwMnlseWt2NDRtcGk3In0.Lxh3yrZYpsMnUOmV9Kp50w'}),
    bounds = new L.LatLngBounds(new L.LatLng(minLat, minLon), new L.LatLng(maxLat, maxLon));

var mymap = new L.Map('leaflet', {
    center: bounds.getCenter(),
    zoom: 16,
    layers: [mapBox],
    maxBounds: bounds
});

var latlngs = L.rectangle(bounds).getLatLngs();
L.polyline(latlngs[0].concat(latlngs[0][0]), {color: 'blue', opacity: 0.9}).addTo(mymap);
map.setMaxBounds(bounds);
