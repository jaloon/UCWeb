'use strict'
$(function() {
    $(window).resize(function() {
        $("#table-cont").height($(window).height() - 134);
    }).resize();

    var tableCont = document.querySelector('#table-cont')
    /**
     * scroll handle
     * @param {event} e -- scroll event
     */
    function scrollHandle (e){
        // console.log(this)
        var scrollTop = this.scrollTop;
        this.querySelector('.table-head').style.transform = 'translateY(' + scrollTop + 'px)';
    }

    tableCont.addEventListener('scroll',scrollHandle)

});