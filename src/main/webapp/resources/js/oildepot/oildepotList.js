var dispatch;
var refreshPage;
$(function() {
    dispatch = function(mode, id) {
        var title = "添加油库";
        var h = "604px";
        if ("edit" == mode) {
            title = "修改油库信息";
            h = "664px";
        }
        if ("view" == mode) {
            title = "查看油库信息";
            h = "613px";
        }
        layer.open({
            type: 2,
            title: ['油库管理（' + title + '）', 'font-size:14px;color:#ffffff;background:#478de4;'],
            shadeClose: true,
            shade: 0.8,
            resize: false,
            area: ['540px', h],
            content: '../../manage/oildepot/dispatch.do?' + encodeURI('mode=' + mode + '&id=' + id), //iframe的url
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
        var oildepotName = $("#search_text").val();
        if (isNull(oildepotName)) {
            layer.alert('请输入要查询的油库名称！', { icon: 6 }, function(index2) {
                layer.close(index2);
                $("#search_text").select();
            });
            return;
        }
        showList(oildepotName, 1);
    });

    refreshPage = function () {
        var pageId = $("#page_id").val();
        if (isNull(pageId)) {
            pageId = 1;
        }
        var oildepotName = $("#odname").val();
        if (isNull(oildepotName)) {
            oildepotName = "";
        }
        showList(oildepotName, pageId);
    };

    $("#page_id").change(function() {
        refreshPage();
    });

    $("#page_size").change(function() {
        var oildepotName = $("#odname").val();
        if (isNull(oildepotName)) {
            oildepotName = "";
        }
        showList(oildepotName, 1);
    });
});