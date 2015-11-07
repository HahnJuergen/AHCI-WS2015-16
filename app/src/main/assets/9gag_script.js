var siteId;
var vidStarted = false;

jQuery(document).ready(function($) {

	setTimeout(function() {
        siteId = window.location.pathname.substring("/gag/".length);
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
                if(clickWasOnMeme(e)) {
                    if(vidStarted) {
                        $("#" + siteId + " .badge-post-media").trigger("click");
                        vidStarted = false;
                    } else {
                        $("#" + siteId + " .badge-post-cover-image").trigger("click");
                        vidStarted = true;
                    }

                } else {
                    if(confirm("{url:\"" + window.location + "\", text:\"Sie können die Webseite in der App nur ansehen. Im Browser öffnen?\"}")) {};
                }

                return false;
            });


    }, 100);
}

function clickWasOnMeme(click) {
    var possibleImages;

    if(vidStarted) {
      possibleImages = $("#" + siteId + " .badge-post-media");
    } else {
      possibleImages = $("#" + siteId + " .badge-post-cover-image");
    }
    var correctImg;
    for(var i = 0; i < possibleImages.length; i++) {
       if(possibleImages.eq(i).height() == 0) continue;
       correctImg = possibleImages.eq(i);
    }

    if(correctImg == undefined || correctImg == null) return false;

    var boundingRect = correctImg[0].getBoundingClientRect();

    if(click.clientX  >= boundingRect.left && click.clientX <= boundingRect.right &&
        click.clientY >= boundingRect.top && click.clientY <= boundingRect.bottom) {


        return true;
    }

    return false;
}