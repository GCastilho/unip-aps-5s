$(() => {

    let ws = new WebSocket("ws://"+window.location.hostname+":8080/");

    //função para carregar as mensagens ao iniciar a pagina
    window.onload = () => {

    };

    ws.onopen = () => {
        ws.send(JSON.stringify({
            command: "greetings",
            sessionID: document.cookie.slice("sessionID=".length)
        }));
        ws.send(JSON.stringify({command: "getUserList"}));
    };

    ws.onclose = function() {
        alert("Closed!");
    };

    ws.onerror = function(err) {
        alert("Error: " + err);
    };

    ws.onmessage = (evt) => {

        if (evt.data.indexOf('{') === -1) {
            $('.chat').html(() => {
                messageLeft({
                    message: evt.data,
                    timestamp: (new Date()).getTime()
                });
            });
        } else {
            let data = JSON.parse(evt.data);
            if (data.status === 'ok') {
                console.log('Mensagem enviada');
            } else if (data.status === 'error') {
                console.log('erro');
                console.log(data);
            } else {
                console.log(data);
            }
        }
    };

    $('ul').click((e) => {
        $('.headRight-title').text($(e.target).attr('user'));
        $('.chat').empty();
        //messageHistory(); função em implementação
    });

    //função de envio
    let send = () =>{
        if ($('#inputMessage').val() !== "") {
            let data = {
                command: "send",
                receiver: "user",
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