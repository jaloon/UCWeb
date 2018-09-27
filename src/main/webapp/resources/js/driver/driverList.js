var dispatch;
var refreshPage;
$(function() {
    dispatch = function(mode, id) {
        var title = "添加司机";
        var h = "308px";
        if ("edit" == mode) {
            title = "修改司机信息";
        }
        if ("view" == mode) {
            title = "查看司机信息";
            h = "266px";
        }
        layer.open({
            type: 2,
            title: ['司机管理（' + title + '）', 'font-size:14px;color:#ffffff;background:#478de4;'],
            // shadeClose: true,
            shade: 0.8,
            resize: false,
            area: ['540px', h],
            content: '../../manage/driver/dispatch.do?' + encodeURI('mode=' + mode + '&id=' + id), //iframe的url
            end: function() {
                if (mode != "view") {
                    refreshPage();
                }
            }
        });
        // 阻止事件冒泡到DOM树上
        stopPropagation(event);
    };

    showList("", 1);


    $("#search_btn").click(function() {
        var driverName = trimAll($("#search_text").val());
        showList(driverName, 1);
    });

    refreshPage = function () {
        var pageId = $("#page_id").val();
        if (isNull(pageId)) {
            pageId = 1;
        }
        var driverName = $("#dname").val();
        if (isNull(driverName)) {
            driverName = "";
        }
        showList(driverName, pageId);
    };

    $("#page_id").change(function() {
        refreshPage();
    });

    $("#page_size").change(function() {
        var driverName = $("#dname").val();
        if (isNull(driverName)) {
            driverName = "";
        }
        showList(driverName, 1);
    });
});