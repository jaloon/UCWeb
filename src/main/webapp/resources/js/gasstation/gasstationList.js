var dispatch;
var refreshPage;
$(function() {
    dispatch = function(mode, id) {
        var title = "添加加油站";
        var h = "604px";
        if ("edit" == mode) {
            title = "修改加油站信息";
            h = "654px";
        }
        if ("view" == mode) {
            title = "查看加油站信息";
            h = "603px";
        }
        layer.open({
            type: 2,
            title: ['加油站管理（' + title + '）', 'font-size:14px;color:#ffffff;background:#478de4;'],
            shadeClose: true,
            shade: 0.8,
            resize: false,
            area: ['540px', h],
            content: '../../manage/gasstation/dispatch.do?' + encodeURI('mode=' + mode + '&id=' + id), //iframe的url
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
        var gasstationName = $("#search_text").val();
        if (isNull(gasstationName)) {
            layer.alert('请输入要查询的加油站名称！', { icon: 6 }, function(index2) {
                layer.close(index2);
                $("#search_text").select();
            });
        }
        showList(gasstationName, 1);
    });

    refreshPage = function () {
        var pageId = $("#page_id").val();
        if (isNull(pageId)) {
            pageId = 1;
        }
        var gasstationName = $("#gsname").val();
        if (isNull(gasstationName)) {
            gasstationName = "";
        }
        showList(gasstationName, pageId);
    };

    $("#page_id").change(function() {
        refreshPage();
    });

    $("#page_size").change(function() {
        var gasstationName = $("#gsname").val();
        if (isNull(gasstationName)) {
            gasstationName = "";
        }
        showList(gasstationName, 1);
    });
});