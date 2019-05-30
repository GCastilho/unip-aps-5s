var receiver;

var ws = new WebSocket("ws://"+window.location.hostname+":8080/");

var me;

var lastMessageId;

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
	let inputMessage = $('#inputMessage');
	if (inputMessage.val() !== "") {
		let data = {
			command: "send",
			receiver,
			message: inputMessage.val(),
			timestamp: (new Date()).getTime()
		};
		ws.send(JSON.stringify(data));
		data.sender = me;
		putMessage(data, true);
		scrollUpdate();
		inputMessage.val('');
	}
}

ws.onopen = function() {
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

ws.onmessage = function(evt) {
	try {
		let data = JSON.parse(evt.data);
		if (data.status === 'ok') {
			let command = new Map();

			command.set('getUserList', () => {
				data.userList.forEach(user => {
					if (user !== me) chatList(user)
				});
			});

			command.set('getMessages', () => {
				data.messageList.forEach(message => {
					lastMessageId = message._id.$oid;
					putMessage(message)});
			});

			command.set('newMessage', () => {
				putMessage(data, true);
				scrollUpdate();
			});

			if (command.has(data.command)) {
				command.get(data.command)();
			} else if (data.info !== undefined) {
				console.log('INFO: ' + data.info);
			}  else {
				console.log('Unrecognized command response: ' + data.command);
			}
		} else if (data.status === 'error') {
			console.log('Server has returned an error: ' + data.info);
			alert('Error: ' + data.info);
		} else {
			console.log('Bad response: ' + data);
		}
	} catch (e) {
		console.log('Error while parsing input: ' + evt.data);
	}
};