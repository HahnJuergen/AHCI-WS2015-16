var siteId;
var vidStarted = false;

jQuery(document).ready(function($) {

	setTimeout(function() {
        siteId = window.location.pathname.substring("/lol/".length);
        if(siteId.indexOf("/") > 0) {
            siteId = siteId.substring(0, siteId.indexOf("/"));
        }

        setupGeneralOverlay();


	}, 500);
});


function setupGeneralOverlay() {
    $("body").append("<div id='memeview_overlay'></div>");
       setTimeout(function() {
        $("#memeview_overlay").on("click", function(e) {
            if(confirm("{url:\"" + window.location + "\", text:\"Sie können die Webseite in der App nur ansehen. Im Browser öffnen?\"}")) {};
            return false;
        });
    }, 100);
}
