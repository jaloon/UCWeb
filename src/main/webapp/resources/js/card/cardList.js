var dispatch;
var refreshPage;
$(function() {
    dispatch = function(mode, id) {
        var title = "添加卡";
        var h = "376px";
        if ("edit" == mode) {
            title = "修改卡信息";
            h = "420px";
        }
        if ("view" == mode) {
            title = "查看卡信息";
            h = "369px";
        }
        layer.open({
            type: 2,
            title: ['卡管理（' + title + '）', 'font-size:14px;color:#ffffff;background:#478de4;'],
            shadeClose: true,
            shade: 0.8,
            resize: false,
            area: ['540px', h],
            content: '../../manage/card/dispatch.do?' + encodeURI('mode=' + mode + '&id=' + id), //iframe的url
            end: function() {
                if (mode != "view") {
                    refreshPage();
                }
            }
        });
        // 阻止事件冒泡到DOM树上
        event.stopPropagation();
    };

    showList("", 1);

    $("#search_btn").click(function() {
        var cardType = $("#search_text").val();
        showList(cardType, 1);
    });

    refreshPage = function () {
        var pageId = $("#page_id").val();
        if (isNull(pageId)) {
            pageId = 1;
        }
        var cardType = $("#ctype").val();
        if (isNull(cardType)) {
            cardType = -2;
        }
        showList(cardType, pageId);
    };

    $("#page_id").change(function() {
        refreshPage();
    });

    $("#page_size").change(function() {
        var cardType = $("#ctype").val();
        if (isNull(cardType)) {
            cardType = -2;
        }
        showList(cardType, 1);
    });
});