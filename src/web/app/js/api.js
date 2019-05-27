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
        try {
            let data = JSON.parse(evt.data);
            switch (data.status) {
	            case 'ok':
	            	switch (data.command) {
			            //Quando vc envia qqer comando, o server responde um comando 'response'
			            case 'response':
				            switch (data.requested) {
					            case 'send':
						            if (data.sended) {
							            console.log('Mensagem enviada');
						            } else {
							            console.log('Erro ao enviar mensagem');
						            }
						            break;
					            case 'getUserList':
						            console.log('Vc requisitou a user list, tem q implementar isso');
						        break;
				            }
				            break;
			            case 'newMessage':
				            console.log("implemente algo que lide com novas mensagens!!!");
				            break;
			            default:
				            console.log('Unrecognized command response: ' + data.command);
				            break;
		            }
		            break;
	            case 'error':
		            console.log("Server has returned an error: " + data.info);
		            break;
	            default:
		            console.log("Bad response: " + data);
		            break;
            }
        } catch (e) {
            console.log("error while parsing input: " + evt.data);
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