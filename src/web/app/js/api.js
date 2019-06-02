var receiver;

var ws = new WebSocket("ws://"+window.location.hostname+":8080/");

var me;

var lastMessageId;

var groupName;

var groupNameList;

window.onload = () => {
	me = localStorage.getItem('username');

	let modal = document.getElementById("new-group");

	let btn = document.getElementById("btn-add-group");

	let span = document.getElementsByClassName("close")[0];

	let cancelBtn = document.getElementById("cancel");

	let saveBtn = document.getElementById("save");

	let user = document.getElementById('current-session');
	user.innerText = 'Bem vindo, '+me+'!';

	$('.btn-send').click(() => {
		send();
	});

	$('#inputMessage').keypress((e) => {
		if (e.which === 13) {
			send();
		}
	});
	$('#btnEmoji').lsxEmojiPicker({
		width: 220,
		height: 200,
		twemoji: true,
		onSelect: function(emoji){
			document.getElementById("inputMessage").value += String.fromCodePoint(emoji.value.replace(/&#/i, '0'));
		}
	});

	btn.onclick = function() {
		modal.style.display = "block";
	};

	cancelBtn.onclick = function() {
		modal.style.display = "none";
	};

	saveBtn.onclick = function() {
		groupNameList = [me];
		groupName = document.getElementById("group-name");
		var names = document.getElementsByName("user");
		names.forEach(name => {
			if (name.checked === true) {
				groupNameList.push(name.value);
				name.checked = false;
			}
		});
		console.log(groupName.value);
		console.log(groupNameList);
		ws.send(JSON.stringify({
			command: 'addNewGroup',
			groupName: groupName.value,
			users: groupNameList
		}));
		groupName.value = '';
		modal.style.display = "none";
	};

	span.onclick = function() {
		modal.style.display = "none";
	};

	window.onclick = function(event) {
		if (event.target === modal) {
			modal.style.display = "none";
		}
	};
};

function send() {
	let inputMessage = $('#inputMessage');
	if (inputMessage.val() !== "") {
		let data = {
			command: "send",
			receiver,
			message: inputMessage.val(),
			timestamp: new Date().getTime()
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
	ws.send(JSON.stringify({command: "getChatList"}));
};

ws.onclose = function() {
	log_out();
};

ws.onerror = function(err) {
	alert("Error: " + err);
};

ws.onmessage = function(evt) {
	try {
		let data = JSON.parse(evt.data);
		if (data.status === 'ok') {
			let command = new Map();

			command.set('getChatList', () => {
				data.groupsList.forEach(group => {
					chatList(group)
				});
				data.userList.forEach(user => {
					if (user !== me) chatList(user)
				});
			});

			command.set("newUser", () => {
				chatList(data.user);
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

			command.set('newGroup', () => {
				chatList(data.groupID);
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

function log_out() {
	document.cookie = 'sessionID=; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
	window.location.pathname = '';
}