$(function() {
    // 必填字段添加星号标识
	$('input[required]').each(function(i, e) {
    	$(e).closest('tr').children().first().prepend('<span style="color:red">* </span>');
    });
	
    var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
    var mode = $("#mode").val();
    var id = $("#id").val();
    if (isNull(id)) {
        id = "";
    }

    function findPermissions(mode, roleId) {
        var permissions = null;
        $.ajax({
            type: "get",
            async: false, //不异步，先执行完ajax，再干别的
            url: "../../manage/role/findPermissions.do",
            data: encodeURI("mode=" + mode + "&roleId=" + roleId),
            dataType: "json",
            success: function(response) {
                permissions = response;
            },
            error: function(XMLHttpRequest, textStatus, errorThrown) {  //#3这个error函数调试时非常有用，如果解析不正确，将会弹出错误框
                if (XMLHttpRequest.readyState == 4) {
                    var http_status = XMLHttpRequest.status;
                    if (http_status == 0 || http_status > 600) {
                        location.reload(true);
                    } else if (http_status == 200) {
                        if (textStatus == "parsererror") {
                            layer.alert("应答数据格式解析错误！")
                        } else {
                            layer.alert("http response error: " + textStatus)
                        }
                    } else {
                        layer.alert("http connection error: status[" + http_status + "], " + XMLHttpRequest.statusText)
                    }
                }
            }
        });
        return permissions;
    }

    var setting = {
        check: {
            enable: true
        },
        data: {
            key: {
                name: "cname"
            },
            simpleData: {
                enable: true,
                pIdKey: "parentId"
            }
        },
    };
    if (mode == "view") {
        setting = {
            data: {
                key: {
                    name: "cname"
                },
                simpleData: {
                    enable: true,
                    pIdKey: "parentId"
                }
            },
        };
    }
    var zNodes = findPermissions(mode, id);
    if (isNull(zNodes)) {
        $(".tree-box").html("未分配权限。");
    } else {
        $.fn.zTree.init($("#treeDemo"), setting, zNodes); //初始化树
    }

    var success_zh_text = "修改成功！";
    var error_zh_text = "修改失败！";

    $("#cancel").click(function() {
        parent.layer.close(index);
    });

    $("#confirm").click(function() {
        var name = $.trim($("#name").val());
        var app = $.trim($("#app").val());
        var remark = $.trim($("#remark").val());
        var permissionIds = "";
        var treeObj = $.fn.zTree.getZTreeObj("treeDemo"),
            nodes = treeObj.getCheckedNodes(true);
        for (var i = 0, len = nodes.length; i < len; i++) {
            permissionIds += nodes[i].id + ",";
        }
        permissionIds = permissionIds.substring(0, permissionIds.length - 1);
        var url = "../../manage/role/update.do";
        var param = "id=" + id + "&name=" + name + "&isApp=" + app + "&remark=" + remark + "&permissionIds=" + permissionIds;

        if ("add" == mode) {
            url = "../../manage/role/add.do";
            param = "name=" + name + "&isApp=" + app + "&remark=" + remark + "&permissionIds=" + permissionIds;

            success_zh_text = "添加成功！";
            error_zh_text = "添加失败！";

            if (isNull(name)) {
                layer.alert('角色名称不能为空！', { icon: 0 }, function(index2) {
                    layer.close(index2);
                    $("#name").select();
                });
                return;
            } else {
                var ajaxFlag = true; //ajax执行结果标记，用于判断是否中断整个程序（return若放在ajax回调函数中，则无法跳出ajax）
                $.ajax({
                    type: "post",
                    async: false, //不异步，先执行完ajax，再干别的
                    url: "../../manage/role/isExist.do",
                    data: encodeURI("name=" + name),
                    dataType: "json",
                    success: function(response) {
                        if (response == true || response == "true") {
                            ajaxFlag = false;
                            layer.alert('角色名称已存在！', { icon: 5 }, function(index2) {
                                layer.close(index2);
                                $("#name").select();
                            });
                        }
                    },
                    error: function(XMLHttpRequest, textStatus, errorThrown) {  //#3这个error函数调试时非常有用，如果解析不正确，将会弹出错误框
                        if (XMLHttpRequest.readyState == 4) {
                            var http_status = XMLHttpRequest.status;
                            if (http_status == 0 || http_status > 600) {
                                location.reload(true);
                            } else if (http_status == 200) {
                                if (textStatus == "parsererror") {
                                    layer.alert("应答数据格式解析错误！")
                                } else {
                                    layer.alert("http response error: " + textStatus)
                                }
                            } else {
                                layer.alert("http connection error: status[" + http_status + "], " + XMLHttpRequest.statusText)
                            }
                        }
                    }
                });
                if (!ajaxFlag) {
                    return;
                }
            }
        }

        var loadLayer = layer.load();
        $.post(url, encodeURI(param),
            function(data) {
                layer.close(loadLayer);
                if ("error" == data.msg) {
                    if ("系统内置角色不允许修改！" == data.e) {
                        layer.msg(data.e, { icon: 1, time: 1000 }, function() {
                            parent.layer.close(index);
                        });
                    } else {
                        layer.alert(error_zh_text + "<br>" + data.e, {icon: 2});
                    }
                } else {
                    layer.msg(success_zh_text, { icon: 1, time: 1000 }, function() {
                        parent.layer.close(index);
                    });
                }
            },
            "json"
        ).error(function (XMLHttpRequest, textStatus, errorThrown) {
            layer.close(loadLayer);
            if (XMLHttpRequest.readyState == 4) {
                var http_status = XMLHttpRequest.status;
                if (http_status == 0 || http_status > 600) {
                    location.reload(true);
                } else if (http_status == 200) {
                    if (textStatus == "parsererror") {
                        layer.alert("应答数据格式解析错误！")
                    } else {
                        layer.alert("http response error: " + textStatus)
                    }
                } else {
                    layer.alert("http connection error: status[" + http_status + "], " + XMLHttpRequest.statusText)
                }
            }
        });

    });
});