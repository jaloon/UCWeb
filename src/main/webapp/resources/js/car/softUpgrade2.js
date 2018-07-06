var carCount = 0;
var upgradeIndex;
var fileLayer;
var type;

function openFileLayer(title) {
    var height;
    if (type == 1) {
        height = '181px';
    } else if (type == 2) {
        height = '253px';
    } else if (type == 3) {
        height = '289px';
    }
    fileLayer = layer.open({
        type: 1,
        title: ['升级文件' + title, 'font-size:14px;color:#ffffff;background:#478de4;'],
        area: ['500px', height],
        content: $('.file'),
        end: function () {
            console.log('close file layer')
        }
    });
}

function parseFileType(type) {
    switch (type) {
        case 2:
            return "内核";
        case 3:
            return "文件系统";
        case 4:
            return "设备树";
        case 100:
            return "APP";
        default:
            return "未知";
    }
}

$(function () {
    $.getJSON("../../../manage/car/findBindedVehicleTree.do",
        function (data, textStatus, jqXHR) {
            carCount += data.length;

            if (isNull(data) || carCount == 0) {
                layer.alert('车辆数据异常，当前无可用车辆，请稍后重试！', {icon: 0}, function (index2) {
                    layer.close(index2);
                    parent.layer.close(index);
                });
                return;
            }
            var setting = {
                check: {
                    enable: true
                },
                data: {
                    simpleData: {
                        enable: true,
                        idKey: "id",
                        pIdKey: "pId",
                        rootPId: 0
                    }
                },
            };
            $.fn.zTree.init($("#treeDemo"), setting, data); //初始化树
        }
    ).error(function (XMLHttpRequest, textStatus, errorThrown) {
        if (XMLHttpRequest.readyState == 4 && XMLHttpRequest.status == 200 && textStatus == "parsererror") {
            layer.confirm('登录失效，是否刷新页面重新登录？', {
                icon: 0,
                title: ['登录失效', 'font-size:14px;color:#ffffff;background:#478de4;']
            }, function () {
                location.reload(true);
            });
        }
    });

    var carLayer;
    var terminalIds;
    $("#cars").click(function () {
        if (carCount == 0) {
            layer.alert('车辆数据异常，当前无可用车辆，请稍后重试！', {icon: 0}, function (index2) {
                layer.close(index2);
                parent.layer.close(index);
            });
            return;
        }
        var carnos = $("#cars").val();
        var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
        if (isNull(carnos)) {
            // 取消当前所有被选中节点的选中状态
            // treeObj.cancelSelectedNode(); // 此方法无效
            treeObj.checkAllNodes(false);
        } else {
            // 展开所有选中节点
            // var nodes = treeObj.getSelectedNodes(); // 此方法无效
            var nodes = treeObj.getCheckedNodes(true);
            nodes.forEach(function (el) {
                treeObj.expandNode(el, true, false, false, false);
            });
        }
        carLayer = layer.open({
            type: 1,
            title: ['升级车辆', 'font-size:14px;color:#ffffff;background:#478de4;'],
            area: ['500px', '350px'],
            content: $('.cartree'),
            end: function () {
                // 折叠全部节点
                var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
                treeObj.expandAll(false);
            }
        });
    });

    $("#car_cancel").click(function () {
        layer.close(carLayer);
    });

    $("#car_confirm").click(function () {
        var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
        // var nodes = treeObj.getSelectedNodes(); // 此方法无效
        var nodes = treeObj.getCheckedNodes(true);
        var carnos = "";
        terminalIds = "";
        var selectedCarCount = 0;
        nodes.forEach(function (el) {
            if (el.id >= 0x01000001) {
                carnos += ", " + el.name;
                terminalIds += "," + el.id;
                selectedCarCount++;
            }
        });
        if (selectedCarCount > 25) {
            layer.alert('一次最多升级25辆车！当前待升级车辆数目：' + selectedCarCount, {icon: 0}, function (index2) {
                carnos = "";
                terminalIds = "";
                $("#cars").val(carnos);
                treeObj.checkAllNodes(false);
                layer.close(index2);
            });
            return;
        }
        carnos = carnos.slice(2);
        terminalIds = terminalIds.slice(1);
        $("#cars").val(carnos);
        layer.close(carLayer);
    });

    $("#type").change(function () {
        if ($("#type").val() == 2) {
            $("#match").closest("td").html("<input type='text' class='editInfo' value='否' readonly><input type='hidden' id='match' value='0'>");
        } else {
            $("#match").closest("td").html("<select class='editInfo' id='match'><option value=1>是</option><option value=0>否</option></select>");
        }
    });

    var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
    $("#cancel").click(function () {
        parent.layer.close(index);
    });

    $("#confirm").click(function () {
        var path = $("#path").val();
        if (isNull(path)) {
            layer.alert('FTP路径不能为空！', {icon: 2}, function (index2) {
                layer.close(index2);
                $("#path").select();
            });
            return;
        }
        if (path.endsWith("/")) {
            layer.alert('FTP路径不能以【 / 】结尾！', {icon: 2}, function (index2) {
                layer.close(index2);
                $("#path").select();
            });
            return;
        }
        if (!isFtpPath(path)) {
            layer.alert('FTP路径格式不正确！', {icon: 2}, function (index2) {
                layer.close(index2);
                $("#path").select();
            });
            return;
        }
        if (isNull(terminalIds)) {
            layer.alert('升级车辆不能为空！', {icon: 2}, function (index2) {
                layer.close(index2);
                $("#cars").click();
            });
            return;
        }

        type = $("#type").val();
        var match = $("#match").val();

        var loadIndex = layer.load();
        $.post("../../../manage/remote/terminal_upgrade_file_info",
            "terminal_ids=" + terminalIds + "&ftp_path=" + path + "&upgrade_type=" + type + "&match_ver=" + match +
            "&token=" + generateUUID(),
            function (data) {
                if (data.id > 0) {
                    layer.close(loadIndex);
                    layer.msg(data.msg, {
                        icon: 2,
                        time: 500
                    });
                } else {
                    var msg = data.msg;
                    upgradeIndex = msg.index;
                    var ver = msg.ver;
                    var title = "";
                    if (!isNull(ver)) {
                        title = "（ 版本：" + ver + " ）";
                    }
                    var files = msg.files;
                    var tbodyHtml = "<tbody>";
                    for (var i = 0; i < files.length; i++) {
                        var file = files[i];
                        tbodyHtml += "<tr><td class='file-type'>" + parseFileType(file.type) + "</td>" +
                            "<td class='file-name'>" + file.name + "</td>" +
                            "<td class='file-size'>" + file.size + "</td>" +
                            "<td class='file-crc32'>" + file.crc32 + "</td></tr>";
                    }
                    tbodyHtml += "</tbody>";
                    $(".file tbody").replaceWith(tbodyHtml);
                    layer.close(loadIndex);
                    layer.msg('请确认升级文件信息！', {
                        icon: 1,
                        time: 500
                    }, function () {
                        openFileLayer(title);
                    });
                }
            },
            "json"
        ).error(function (XMLHttpRequest, textStatus, errorThrown) {
            if (XMLHttpRequest.readyState == 4 && XMLHttpRequest.status == 200 && textStatus == "parsererror") {
                layer.confirm('登录失效，是否刷新页面重新登录？', {
                    icon: 0,
                    title: ['登录失效', 'font-size:14px;color:#ffffff;background:#478de4;']
                }, function () {
                    location.reload(true);
                });
            }
        });
    });

    $("#file_cancel").click(function () {
        layer.close(fileLayer);
    });

    $("#file_confirm").click(function () {
        $.post("../../../manage/remote/asyn_terminal_upgrade_request",
            "index=" + upgradeIndex + "&token=" + generateUUID(),
            function (data) {
                if (data.id > 0) {
                    layer.msg(data.msg, {
                        icon: 2,
                        time: 5000
                    }, function () {
                        layer.close(fileLayer);
                    });
                } else {
                    layer.msg('请求发送成功！', {
                        icon: 1,
                        time: 500
                    }, function () {
                        layer.close(fileLayer);
                        parent.layer.close(index);
                    });
                }
            },
            "json"
        ).error(function (XMLHttpRequest, textStatus, errorThrown) {
            if (XMLHttpRequest.readyState == 4 && XMLHttpRequest.status == 200 && textStatus == "parsererror") {
                layer.confirm('登录失效，是否刷新页面重新登录？', {
                    icon: 0,
                    title: ['登录失效', 'font-size:14px;color:#ffffff;background:#478de4;']
                }, function () {
                    location.reload(true);
                });
            }
        });
    });
});