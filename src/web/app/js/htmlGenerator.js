//função para gerar as mensagens e adicionar ao HTML
function putMessage(data, isNewMessage = false) {
	let chatBox = document.getElementById('chatBox');

	let date = new Date((data.timestamp["$numberLong"] !== undefined) ? (data.timestamp["$numberLong"]/1000)-(1000*60*60*4) : data.timestamp);

	let htm = '<div id="'+lastMessageId+'" class="msg-left-sub">' +
		'<div class="msg-desc">' + data.message +
		(data.fileExtension > 0 ? '<img src="'+ window.location +'/files/'+ lastMessageId + '">' : "") +
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

function chatList(name) {
    $(() => {
        $('.chatList').append(
            '<li user="'+name+'" onclick="openChat(\''+name+'\')">' +
            '<div user="'+name+'" class="listContainer">' +
            '<div user="'+name+'" class="desc">' +
            '<h5 user="'+name+'">'+(name.length > 20 ? name.slice(0, name.length-41) : name)+'</h5>' +
            '</div>' +
            '</div>' +
            '</li>'
        );
    })
}

function modalUserList(name) {
	$(() => {$('.modal-body').append('<input type="checkbox" name="user" value="'+name+'">'+name+'<br>')})
}

function openChat(user) {
	receiver = user;

	let md = document.getElementById('chatBox');
	md.innerHTML = '';  //Limpa HTML

	let userTitle = document.getElementById('current-user');
	userTitle.innerText = (receiver.length > 20 ? receiver.substr(0,receiver.length -41):receiver);

	ws.send(JSON.stringify({
		command: 'getMessages',
		receiver,
	}));
	setTimeout(function(){scrollUpdate()},100);
};