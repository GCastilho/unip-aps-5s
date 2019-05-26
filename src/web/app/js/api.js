$(() => {

    let ws = new WebSocket("ws://"+window.location.hostname+":8080/");

    //função para carregar as mensagens ao iniciar a pagina
    window.onload = () => {
        if (localStorage.getItem('key') === null) {
            $.getJSON("js/data.json", (data) => {
                for(let i = 0; i < data.length; i++) {
                    messageLeft(data[i]);
                }
            });
        } else {
            let buffer = JSON.parse(localStorage.getItem('key'));

            for(let i = 0; i < buffer.length; i++) {
                messageLeft(buffer[i]);
            }
        }
    };

    ws.onopen = () => {
        let greetings = {
            command: "greetings",
            sessionID: document.cookie.slice("SessionID=".length)
        };
        ws.send(JSON.stringify(greetings));
    };

    ws.onclose = function() {
        alert("Closed!");
    };

    ws.onerror = function(err) {
        alert("Error: " + err);
    };

    ws.onmessage = (evt) => {
        let data = evt.data;

        if ((data.indexOf('{') === -1) && (data !== "Hello Webbrowser")) {
            $('.chat').html(() => {
                messageLeft({
                    message: data,
                    time: (new Date()).getTime()
                });
            });
        } else {
            console.log("Message: " + data);
        }
    };

    //função de envio
    let send = () =>{
        if ($('#inputMessage').val() !== "") {
            let data = {
                command: "send",
                receiver: "root",
                message: $('#inputMessage').val(),
                timestamp: (new Date()).getTime()
            }
            ws.send(JSON.stringify(data));
            messageRight(data);
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
});