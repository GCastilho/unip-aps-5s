//configurações da scrollbar
$('.left-section, .message').mCustomScrollbar({
    scrollInertia: 85
});

//função para mudar o posicionamento do scrollbar
let scrollUpdate = () => {
    $('.message').mCustomScrollbar("scrollTo", "bottom",{
        scrollInertia: 0
    });
};

//função para carregar as mensagens ao iniciar a pagina
window.onload = () => {
    if (localStorage.getItem('key') === null) {
        $.getJSON("../js/data.json", (data) => {
            chatGenerator(data);
        });
    } else {
        chatGenerator(JSON.parse(localStorage.getItem('key')));
    }
};

//função para gerar as mensagens e adicionar ao HTML
let chatGenerator = (data) => {
    console.log(data);
    for(var i = 0; i < data.length; i++) {
        if (data[i].id === "1") {
            $('.chat').append("<li class=\"msg-right\">" +
                "                        <div class=\"msg-left-sub\">" +
                "                            <img src=\"./../img/man04.png\">" +
                "                            <div class=\"msg-desc\">" + data[i].message +
                "                            </div>" +
                "                            <small>"+data[i].time+"</small>" +
                "                        </div>" +
                "                    </li>");
            scrollUpdate();
        } else {
            $('.chat').append("<li class=\"msg-left\">" +
                "                        <div class=\"msg-left-sub\">" +
                "                            <img src=\"./../img/man03.png\">" +
                "                            <div class=\"msg-desc\">" + data[i].message +
                "                            </div>" +
                "                            <small>"+data[i].time+"</small>" +
                "                        </div>" +
                "                    </li>");
            scrollUpdate();
        }
    }
};

$(() => {
    //função de envio
    $('.btn-send').click(() => {
        if ($('#inputMessage').val() !== "") {
            if (localStorage.getItem('key') === null) {
                $.getJSON("../js/data.json", (data) => {
                    let dt = new Date();
                    data.push({
                        id: "1",
                        message: $('#inputMessage').val(),
                        time: dt.getHours() + ":" + String(dt.getMinutes()).padStart(2, '0')
                    });
                    localStorage.setItem('key', JSON.stringify(data));
                });
            } else {
                let data = JSON.parse(localStorage.getItem('key'));
                let dt = new Date();
                data.push({
                    id: "1",
                    message: $('#inputMessage').val(),
                    time: dt.getHours() + ":" + String(dt.getMinutes()).padStart(2, '0')
                });
                localStorage.clear();
                localStorage.setItem('key', JSON.stringify(data));
            }
        scrollUpdate();
    }});

    //função para teste, usada para limpar o armazenamento local
    $('.teste').click(() => {
        localStorage.clear();
    });
});