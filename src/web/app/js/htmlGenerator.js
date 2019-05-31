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
}

function upload_file() {
	let form = document.createElement("form");
	form.setAttribute('method', 'POST');
	form.setAttribute('enctype', "multipart/form-data");
	form.setAttribute('target', "mf");
	form.setAttribute('action', window.location + '../file');

	let dinput = document.createElement("input");
	dinput.setAttribute("name","receiver");
	dinput.setAttribute("value",receiver);
	form.appendChild(dinput);

	dinput = document.createElement("input");
	dinput.setAttribute("name","file");
	dinput.setAttribute("type","file");
	form.appendChild(dinput);
	dinput.click();

	form.submit();
}