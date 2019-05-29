//função para gerar as mensagens e adicionar ao HTML
function putMessage(data, newMessage = false) {
	console.log(data);

	let chatBox = document.getElementById('chatBox');

	let htm = '<div class="msg-left-sub">' +
		'<div class="msg-desc">' + data.message + '</div>' +
		'<small>' +
		(new Date(data.timestamp)).getHours()+':'+String((new Date(data.timestamp)).getMinutes()).padStart(2, '0') +
		'</small>' +
		'</div>' +
		'</li>';

	newMessage ?
		chatBox.innerHTML += '<li class="msg-' + (data.sender === me ? 'right' : 'left') + '">' + htm:
		chatBox.innerHTML = '<li class="msg-' + (data.sender === me ? 'right' : 'left') + '">'
		+ htm + chatBox.innerHTML;

}

function chatList(user) {
    $(() => {
        $('.chatList').append(
            '<li user="'+user+'" onclick="openChat(\''+user+'\')">' +
            '<div user="'+user+'" class="listContainer">' +
            '<div user="'+user+'" class="desc">' +
            '<h5 user="'+user+'">'+user+'</h5>' +
            '</div>' +
            '</div>' +
            '</li>'
        );
    })
};

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
};