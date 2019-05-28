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
		    if (data.status === 'ok') {
	            let command = new Map();

	            command.set('response', () => {
		            let requested = new Map();

		            requested.set('send', () => {
			            if (data.sended) {
				            console.log('Mensagem enviada');
			            } else {
				            console.log('Erro ao enviar mensagem');
			            }
		            });

		            requested.set('greetings', () => {
		            	console.log("Greetings ok!");
		            });

		            requested.set('getUserList', () => {
			            data.userList.forEach(user => chatList(user));
		            });

		            if (requested.has(data.response)) {
			            requested.get(data.response)();
		            } else {
			            console.log('Unrecognized requested response: ' + data.requested);
		            }
	            });

	            command.set('newMessage', () => {
		            console.log("implemente algo que lide com novas mensagens!!!");
	            });

	            if (command.has(data.command)) {
		            command.get(data.command)();
	            }  else {
		            console.log('Unrecognized command response: ' + data.command);
	            }
            } else if (data.status === 'error') {
	            console.log("Server has returned an error: " + data.info);
            } else {
	            console.log("Bad response: " + data);
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