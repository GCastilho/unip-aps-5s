$(() => {
    //configurações da scrollbar
    $('.left-section, .message').mCustomScrollbar({
        scrollInertia: 85
    });
    $('.left-section').mCustomScrollbar({
        height: 0
    });
});

//função para mudar o posicionamento do scrollbar
let scrollUpdate = () => {
    $(() => {$('.message').mCustomScrollbar("scrollTo", "bottom",{scrollInertia: 0})})
};

