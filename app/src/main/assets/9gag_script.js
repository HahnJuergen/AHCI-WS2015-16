jQuery(document).ready(function($) {
	setTimeout(function() {
        $("body").append("<div id='memeview_overlay'></div>");
        setTimeout(function() {
            $("#memeview_overlay").on("click", function(e) {
                if(confirm("{url:\"" + window.location + "\", text:\"Sie können die Webseite in der App nur ansehen. Im Browser öffnen?\"}")) {
                }
                return false;
            });
        }, 100);

	}, 500);
});
