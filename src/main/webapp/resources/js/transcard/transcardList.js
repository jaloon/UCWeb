var dispatch;
var refreshPage;
$(function() {
    dispatch = function(mode, id) {
        var title = "添加配送卡";
        var h = "205px";
        if ("edit" == mode) {
            title = "修改司机信息";
            h = "243px";
        }
        if ("view" == mode) {
            title = "查看配送卡信息";
            h = "191px";
        }
        layer.open({
            type: 2,
            title: ['配送卡管理（' + title + '）', 'font-size:14px;color:#ffffff;background:#478de4;'],
            shadeClose: true,
            shade: 0.8,
            resize: false,
            area: ['540px', h],
            content: '../../manage/transcard/dispatch.do?' + encodeURI('mode=' + mode + '&id=' + id), //iframe的url
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
        var transcardId = trimAll($("#search_text").val());
        showList(transcardId, 1);
    });

    refreshPage = function () {
        var pageId = $("#page_id").val();
        if (isNull(pageId)) {
            pageId = 1;
        }
        var transcardId = $("#tcid").val();
        if (isNull(transcardId)) {
            transcardId = "";
        }
        showList(transcardId, pageId)
    };

    $("#page_id").change(function() {
        refreshPage();
    });

    $("#page_size").change(function() {
        var transcardId = $("#tcid").val();
        if (isNull(transcardId)) {
            transcardId = "";
        }
        showList(transcardId, 1);
    });
});