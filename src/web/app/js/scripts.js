$(function() {
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
            $.getJSON("js/data.json", (data) => {
                for(let i = 0; i < data.length; i++) {
                    chatGenerator(data[i]);
                }
            });
        } else {
            let buffer = JSON.parse(localStorage.getItem('key'));
            for(let i = 0; i < buffer.length; i++) {
                chatGenerator(buffer[i]);
            }
        }
    };

    //função para gerar as mensagens e adicionar ao HTML
    let chatGenerator = (data) => {
        console.log(data);
        if (data.id === "1") {
            $('.chat').append("<li class=\"msg-right\">" +
                "                        <div class=\"msg-left-sub\">" +
                "                            <div class=\"msg-desc\">" + data.message +
                "                            </div>" +
                "                            <small>"+data.time+"</small>" +
                "                        </div>" +
                "                    </li>");
            scrollUpdate();
        } else {
            $('.chat').append("<li class=\"msg-left\">" +
                "                        <div class=\"msg-left-sub\">" +
                "                            <div class=\"msg-desc\">" + data.message +
                "                            </div>" +
                "                            <small>"+data.time+"</small>" +
                "                        </div>" +
                "                    </li>");
            scrollUpdate();
        }
    };

    //função de envio
    let send = () =>{
        if ($('#inputMessage').val() !== "") {
            if (localStorage.getItem('key') === null) {
                $.getJSON("js/data.json", (data) => {
                    let dt = new Date();
                    data.push({
                        id: "1",
                        message: $('#inputMessage').val(),
                        time: dt.getHours() + ":" + String(dt.getMinutes()).padStart(2, '0')
                    });
                    console.log(data);
                    localStorage.setItem('key', JSON.stringify(data));
                    $('.chat').html(() => {
                        chatGenerator(data[data.length - 1]);
                    });
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
                $('.chat').html(() => {
                    chatGenerator(data[data.length - 1]);
                });
            }
            scrollUpdate();
            $('#inputMessage').val('');
        }
    };

    $('.btn-send').click(() => {
        send();
    });

    $('#inputMessage').keypress((e) => {
        if (e.which === 13) {
            send();
        }
    });

    //função para teste, usada para limpar o armazenamento local
    $('#test').click(() => {
        localStorage.clear();
    });
});