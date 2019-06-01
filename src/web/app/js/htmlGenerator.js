//função para gerar as mensagens e adicionar ao HTML
function putMessage(data, isNewMessage = false) {
	let chatBox = document.getElementById('chatBox');

	let date = new Date((data.timestamp["$numberLong"] !== undefined) ? (data.timestamp["$numberLong"]/1000)-(1000*60*60*4) : data.timestamp);

	let htm = '<div id="'+lastMessageId+'" class="msg-left-sub">' +
		'<div class="msg-desc">' + data.message +
		(data.fileExtension > 0 ? '<img src="'+window.location+'/files?'+lastMessageId+'&'+receiver+'">' : "") +
		'</div>' +
		'<small>' +
		date.getHours() + ':' + String(date.getMinutes()).padStart(2, '0') +
		'</small>' +
		'</div>' +
		'</li>';

	if (isNewMessage) {
		chatBox.innerHTML += '<li class="msg-' + (data.sender === me ? 'right' : 'left') + '">' + htm;
	} else {
		chatBox.innerHTML = '<li class="msg-' + (data.sender === me ? 'right' : 'left') + '">'
			+ htm + chatBox.innerHTML;
	}
}

function chatList(name, isGroup = false) {
    $(() => {
        $('.chatList').append(
            '<li user="'+name+'" onclick="openChat(\''+name+'\')">' +
            '<div user="'+name+'" class="listContainer">' +
            '<div user="'+name+'" class="desc">' +
            '<h5 user="'+name+'">'+(isGroup ? name.slice(0, name.length-41) : name)+'</h5>' +
            '</div>' +
            '</div>' +
            '</li>'
        );
    })
}

function openChat(user) {
	receiver = user;

	let md = document.getElementById('chatBox');
	md.innerHTML = '';  //Limpa HTML

	let userTitle = document.getElementById('current-user');
	userTitle.innerText = receiver;

	ws.send(JSON.stringify({
		command: 'getMessages',
		receiver,
	}));
	setTimeout(function(){scrollUpdate()},100);
}

function upload_file() {
	let form = document.createElement("form");
	form.setAttribute('method', 'POST');
	form.setAttribute('enctype', "multipart/form-data");
	form.setAttribute('target', "mf");
	form.setAttribute('action', window.location + '../file');

	let dinput = document.createElement("input");
	dinput.setAttribute("name","receiver");
	dinput.setAttribute("value", receiver);
	form.appendChild(dinput);

	dinput = document.createElement("input");
	dinput.setAttribute("name","file");
	dinput.setAttribute("type","file");
	form.appendChild(dinput);
	dinput.click();

	form.submit();
}