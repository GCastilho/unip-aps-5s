//função para gerar as mensagens e adicionar ao HTML
let messageRight = (data) => {
    $(() => {
        console.log(data);
        $('.chat').append(
            '<li class="msg-right">' +
            '<div class="msg-left-sub">' +
            '<div class="msg-desc">' + data.message +
            '</div>' +
            '<small>'+(new Date(data.timestamp)).getHours()+':'+(new Date(data.timestamp)).getMinutes()+'</small>' +
            '</div>' +
            '</li>');

        scrollUpdate();
    })
};

let messageLeft = (data) => {
    $(() => {
        console.log(data);
        $('.chat').append(
            '<li class="msg-left">' +
            '<div class="msg-left-sub">' +
            '<div class="msg-desc">' + data.message +
            '</div>' +
            '<small>'+(new Date(data.timestamp)).getHours()+':'+(new Date(data.timestamp)).getMinutes()+'</small>' +
            '</div>' +
            '</li>');

        scrollUpdate();
    })
};

let chatList = (data) => {
    $(() => {
        console.log(data);
        $('.chatList').append(
            '<li user="'+data+'">' +
            '<div user="'+data+'" class="listContainer">' +
            '<div user="'+data+'" class="desc">' +
            '<h5 user="'+data+'">'+data+'</h5>' +
            '</div>' +
            '</div>' +
            '</li>'
        );
    })
};