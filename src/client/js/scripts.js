let cor = () => {
    document.getElementsByClassName('.main-section').style.backgroundColor = "red"
};

$(() => {
    $('button').click(() => {
        $('.main-section').css('backgroundColor', 'blue')
    });
    $('.btn-send').click(() => {
        $('.main-section').css('backgroundColor', 'red')
    })
});

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
    })
});