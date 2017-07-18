/**
 * Created by stylejy on 17/07/2017.
 */
// create a red polyline from an array of LatLng points

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


//var path = document.getElementById("jsontest").value;
