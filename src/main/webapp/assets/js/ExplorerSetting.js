document.getElementById("explorer-submit").addEventListener("click", controller);

function controller() {
    if (document.getElementById("depth").value > 0 && document.getElementById("depth").value != null) {
        sendDepthToServer(document.getElementById("depth").value);
    } else {
        alert("Please type an suitable number!!");
    }
}


function sendDepthToServer(depth) {
    var request = new XMLHttpRequest();
    request.open("POST", "/explorersetting?depth=" + depth, true);
    request.send();
    window.location.href = "/explorer";
}