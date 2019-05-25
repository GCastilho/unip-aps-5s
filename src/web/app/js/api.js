$(() => {

    var ws = new WebSocket("ws://127.0.0.1:8080/");

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

    ws.onmessage = (evt) => {
        alert("Message: " + evt.data);
        let data = evt.data;
        let localData = JSON.parse(localStorage.getItem('key'));

        if (data.sessionId === document.cookie.slice(10)) {
            localData.push({
                side: "1",
                message: data.message,
                time: data.time
            });

            localStorage.clear();
            localStorage.setItem('key', JSON.stringify(localData));

            $('.chat').html(() => {
                chatGenerator(localData[localData.length - 1]);
            });
        } else {
            localData.push({
                side: "2",
                message: data.message,
                time: data.time
            });

            localStorage.clear();
            localStorage.setItem('key', JSON.stringify(localData));

            $('.chat').html(() => {
                chatGenerator(localData[localData.length - 1]);
            });
        }
    };

    //função de envio
    let send = () =>{
        if ($('#inputMessage').val() !== "") {

            ws.send(JSON.stringify({
                sessionID: document.cookie.slice("SessionID=".length),
                receiver: "username",
                message: $('#inputMessage').val(),
                timestamp: (new Date()).getHours() + ":" + String((new Date()).getMinutes()).padStart(2, '0')
            }));

            /*$.ajax({
                url: "/app?command=send",
                type: 'get',
                dataType: 'json',
                data: {
                    sessionId: document.cookie.slice(10),
                    message: $('#inputMessage').val(),
                    time: (new Date()).getHours() + ":" + String((new Date()).getMinutes()).padStart(2, '0')
                },
                success: (data) => {
                    console.log(data);
                }
            });

            if (localStorage.getItem('key') === null) {
                $.getJSON("js/data.json", (data) => {
                    let dt = new Date();

                    data.push({
                        side: "1",
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

                console.log(Date.now());
                console.log(document.cookie.slice(10));

                data.push({
                    side: "1",
                    message: $('#inputMessage').val(),
                    time: dt.getHours() + ":" + String(dt.getMinutes()).padStart(2, '0')
                });

                localStorage.clear();
                localStorage.setItem('key', JSON.stringify(data));

                $('.chat').html(() => {
                    chatGenerator(data[data.length - 1]);
                });
            }
            scrollUpdate();*/
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