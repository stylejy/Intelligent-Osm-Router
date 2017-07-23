function mapHeight() {
    //Map height is window height - (top navbar height - bottom controller bar height)
    var mapHeight = window.outerHeight - 175
    document.getElementById("leaflet").style.height = mapHeight - 74;
}