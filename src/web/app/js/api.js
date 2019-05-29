var receiver;

var ws = new WebSocket("ws://"+window.location.hostname+":8080/");

var me;

window.onload = () => {
	me = localStorage.getItem('username');

	$('.btn-send').click(() => {
		send();
	});

	$('#inputMessage').keypress((e) => {
		if (e.which === 13) {
			send();
		}
	});

};

function send() {
	if ($('#inputMessage').val() !== "") {
		let data = {
			command: "send",
			receiver,
			message: $('#inputMessage').val(),
			timestamp: (new Date()).getTime()
		};
		ws.send(JSON.stringify(data));
		data.sender = me;
		putMessage(data, true);
		$('#inputMessage').val('');
	}
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

				requested.set('getMessages', () => {
					data.messageList.forEach(message => putMessage(message));
				});

				requested.set('send', () => {
					// não implementado
				});

				if (requested.has(data.response)) {
					requested.get(data.response)();
				} else {
					console.log('Unrecognized requested response: ' + data.response);
				}
			});

			command.set('newMessage', () => {
				putMessage(data, true);
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