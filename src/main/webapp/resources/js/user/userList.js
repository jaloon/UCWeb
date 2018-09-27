var dispatch;
var refreshPage;
$(function() {
    dispatch = function(mode, id) {
        var title = "添加操作员";
        var h = "415px";
        if ("edit" == mode) {
            title = "修改操作员信息";
            h = "451px";
        }
        if ("view" == mode) {
            title = "查看操作员信息";
            h = "399px";
        }
        layer.open({
            type: 2,
            title: ['操作员管理（' + title + '）', 'font-size:14px;color:#ffffff;background:#478de4;'],
            // shadeClose: true,
            shade: 0.8,
            resize: false,
            area: ['540px', h],
            content: '../../manage/user/dispatch.do?' + encodeURI('mode=' + mode + '&id=' + id), //iframe的url
            end: function() {
                if (mode != "view") {
                    refreshPage();
                }
            }
        });
        // 阻止事件冒泡到DOM树上
        stopPropagation(event);
    };


    showList("", "", 1);

    $("#search_btn").click(function() {
        var type = $("#search_type").val();
        if (type == 0) {
            layer.alert('请选择查询类型！', { icon: 6 });
        } else {
            var account = "";
            var name = "";
            if (type == 1) {
                account = $.trim($("#search_text").val());
                if (isNull(account)) {
                    layer.alert('请输入要查询的账号！', { icon: 6 }, function(index2) {
                        layer.close(index2);
                        $("#search_text").select();
                    });
                    return;
                }
            } else {
                name = $.trim($("#search_text").val());
                if (isNull(name)) {
                    layer.alert('请输入要查询的姓名！', { icon: 6 }, function(index2) {
                        layer.close(index2);
                        $("#search_text").select();
                    });
                    return;
                }
            }
            showList(account, name, 1);
        }
    });

    refreshPage = function () {
        var pageId = $("#page_id").val();
        if (isNull(pageId)) {
            pageId = 1;
        }
        var account = $("#uaccount").val();
        if (isNull(account)) {
            account = "";
        }
        var name = $("#uname").val();
        if (isNull(name)) {
            name = "";
        }
        showList(account, name, pageId);
    };

    $("#page_id").change(function() {
        refreshPage();
    });

    $("#page_size").change(function() {
        var account = $("#uaccount").val();
        if (isNull(account)) {
            account = "";
        }
        var name = $("#uname").val();
        if (isNull(name)) {
            name = "";
        }
        showList(account, name, 1);
    });
});