function onMapClick(e) {
    document.getElementById("source").innerHTML = e.latlng;
}

mymap.on('click', onMapClick);