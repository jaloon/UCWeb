var dispatch;
var refreshPage;
$(function() {
    dispatch = function(mode, id) {
        var title = "添加角色";
        var h = "435px";
        if ("edit" == mode) {
            if (id < 8) {
                layer.alert('系统内置角色，不可修改！', { icon: 6 });
                return;
            }
            title = "修改角色信息";
        }
        if ("view" == mode) {
            title = "查看角色信息";
            h = "381px";
        }
        layer.open({
            type: 2,
            title: ['角色管理（' + title + '）', 'font-size:14px;color:#ffffff;background:#478de4;'],
            shadeClose: true,
            shade: 0.8,
            resize: false,
            area: ['540px', h],
            content: '../../manage/role/dispatch.do?' + encodeURI('mode=' + mode + '&id=' + id), //iframe的url
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
        name = $.trim($("#search_text").val());
        if (isNull(name)) {
            layer.alert('请输入要查询的角色名称！', { icon: 6 }, function(index2) {
                layer.close(index2);
                $("#search_text").select();
            });
        }
        showList(name, 1);
    });

    refreshPage = function () {
        var pageId = $("#page_id").val();
        if (isNull(pageId)) {
            pageId = 1;
        }
        var name = $("#rname").val();
        if (isNull(name)) {
            name = "";
        }
        showList(name, pageId);
    };

    $("#page_id").change(function() {
        refreshPage();
    });

    $("#page_size").change(function() {
        var name = $("#rname").val();
        if (isNull(name)) {
            name = "";
        }
        showList(name, 1);
    });
});