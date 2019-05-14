$('.left-section, .message').mCustomScrollbar({
    scrollInertia: 85
});

let updateScroll = () => {
    var element = document.getElementsByClassName("message");
    element.scrollTop = element.scrollHeight;
};

var historic = () => {
    $('.chat').append("<li class=\"msg-left\">" +
        "                        <div class=\"msg-left-sub\">" +
        "                            <img src=\"./../img/man03.png\">" +
        "                            <div class=\"msg-desc\">" +
        "                                Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod" +
        "                                tempor incididunt ut labore et dolore magna aliqua." +
        "                            </div>" +
        "                            <small>05:01 am</small>" +
        "                        </div>" +
        "                    </li>")
    updateScroll();
    $('.message').mCustomScrollbar("scrollTo", "bottom",{
        scrollInertia: 0,

    });
};

$(() => {
    $('.btn-send').click(() => {
        if ($('#inputMessage').val() !== "") {
            let dt = new Date();
            $('.chat').append("<li class=\"msg-right\">" +
                "                        <div class=\"msg-left-sub\">" +
                "                            <img src=\"./../img/man04.png\">" +
                "                            <div class=\"msg-desc\">" + $('#inputMessage').val() +
                "                            </div>" +
                "                            <small>"+dt.getHours()+":" +String(dt.getMinutes()).padStart(2, '0')+"</small>" +
                "                        </div>" +
                "                    </li>");
        }
        historic();
        updateScroll();
        $('.message').mCustomScrollbar("scrollTo", "bottom",{
            scrollInertia: 0,

        });
    });
});