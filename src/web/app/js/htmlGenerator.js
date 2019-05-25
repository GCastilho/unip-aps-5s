//função para gerar as mensagens e adicionar ao HTML
let chatGenerator = (data) => {
    $(() => {
        console.log(data);
        if (data.side === "1") {
            $('.chat').append(
                "<li class=\"msg-right\">" +
                "<div class=\"msg-left-sub\">" +
                "<div class=\"msg-desc\">" + data.message +
                "</div>" +
                "<small>"+data.time+"</small>" +
                "</div>" +
                "</li>");

            scrollUpdate();
        } else {
            $('.chat').append(
                "<li class=\"msg-left\">" +
                "<div class=\"msg-left-sub\">" +
                "<div class=\"msg-desc\">" + data.message +
                "</div>" +
                "<small>"+data.time+"</small>" +
                "</div>" +
                "</li>");

            scrollUpdate();
        }
    })
};