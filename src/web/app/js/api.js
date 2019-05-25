$(() => {

    let ws = new WebSocket("ws://"+window.location.hostname+":8080/");

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

    ws.onopen = () => {
        alert('test');
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
        console.log("Message: " + evt.data);
        let data = JSON.parse(evt.data);
        console.log(data.receiver);
        console.log((new Date).getTime());
        if (data.receiver === undefined){
        } else if (data.receiver === document.cookie.slice("SessionID=".length)) {
            $('.chat').html(() => {
                chatGenerator({
                    side: "1",
                    message: data.message,
                    time: data.timestamp
                });
            });
        } else {
            $('.chat').html(() => {
                chatGenerator({
                    side: "2",
                    message: data.message,
                    time: data.timestamp
                });
            });
        }
    };

    //função de envio
    let send = () =>{
        if ($('#inputMessage').val() !== "") {
            ws.send(JSON.stringify({
                command: "send",
                receiver: document.cookie.slice("SessionID=".length),
                message: $('#inputMessage').val(),
                timestamp: (new Date()).getTime()
            }));
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