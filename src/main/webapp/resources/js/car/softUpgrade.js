var add = false;
var fileLayer;
var liObj;

function openFileLayer() {
    var title = "修改";
    if (add) {
        title = "添加";
    }
    fileLayer = layer.open({
        type: 1,
        title: [title + '升级文件', 'font-size:14px;color:#ffffff;background:#478de4;'],
        area: ['500px', '263px'],
        content: $('.file'),
        end: clearFileDiv()
    });
}

function clearFileDiv() {
    $("#file_type").val("");
    $("#file_name").val("");
    $("#file_size").val("");
    $("#file_crc").val("");
}

function addFile() {
    add = true;
    openFileLayer();
}

function delFile(obj) {
    $(obj).closest("li").remove();
}

function alterFile(obj) {
    add = false;
    liObj = $(obj).closest("li");
    openFileLayer();
    $("#file_type").val(liObj.children().eq(2).html());
    $("#file_name").val(liObj.children().eq(1).html());
    $("#file_size").val(liObj.children().eq(3).html());
    $("#file_crc").val(liObj.children().eq(4).html());
}

function aDown(obj) {
    $(obj).css({
        "color": "#ff0000",
        "border-bottom-color": "#ff0000"
    });
}

function aUp(obj) {
    $(obj).css({
        "color": "#551A8B",
        "border-bottom-color": "#551A8B"
    });
}

$(function() {
    $("#add").click(addFile);

    $("#file_cancel").click(function() {
        layer.close(fileLayer);
    });

    $("#file_confirm").click(function() {
        var file_type = $("#file_type").val();
        var file_name = $("#file_name").val();
        var file_size = $("#file_size").val();
        var file_crc = $("#file_crc").val();
        if (isNull(file_type)) {
            layer.alert('文件类型不能为空！', { icon: 2 }, function(index2) {
                layer.close(index2);
                $("#file_type").select();
            });
            return;
        }
        if (!isInteger(file_type)) {
            layer.alert('文件类型必需为数字！', { icon: 2 }, function(index2) {
                layer.close(index2);
                $("#file_type").select();
            });
            return;
        }
        if (isNull(file_name)) {
            layer.alert('文件名不能为空！', { icon: 2 }, function(index2) {
                layer.close(index2);
                $("#file_name").select();
            });
            return;
        }
        if (isNull(file_size)) {
            layer.alert('文件大小不能为空！', { icon: 2 }, function(index2) {
                layer.close(index2);
                $("#file_size").select();
            });
            return;
        }
        if (!isInteger(file_size)) {
            layer.alert('文件大小必需为数字！', { icon: 2 }, function(index2) {
                layer.close(index2);
                $("#file_size").select();
            });
            return;
        }
        if (isNull(file_crc)) {
            layer.alert('文件CRC32不能为空！', { icon: 2 }, function(index2) {
                layer.close(index2);
                $("#file_crc").select();
            });
            return;
        }
        if (!isHEX(file_crc, 8)) {
            layer.alert('文件CRC32不是有效的CRC码，请查证！', { icon: 2 }, function(index2) {
                layer.close(index2);
                $("#file_crc").select();
            });
            return;
        }
        var liHtml = "<li><img alt=\"删除\" title=\"删除\" src=\"../../../resources/images/operate/delete.png\" class=\"del\" onclick=\"delFile(this)\">\n" +
            "<span class=\"fname\" onclick=\"alterFile(this)\" onmousedown=\"aDown(this)\" onmouseup=\"aUp(this)\">" + file_name + "</span>" +
            "<span style=\"display: none;\">" + file_type + "</span>" +
            "<span style=\"display: none;\">" + file_size + "</span>" +
            "<span style=\"display: none;\">" + file_crc + "</span></li>";
        if (add) {
            $("#files li:eq(-1)").before(liHtml);
            // 用js控制div的滚动条,让它在内容更新时自动滚动到底部
            $(".file-box").scrollTop($(".file-box")[0].scrollHeight);
        } else {
            liObj.replaceWith(liHtml);
        }
        layer.close(fileLayer);
    });

    function findBindedVehicleTree() {
        var cars = null;
        $.ajax({
            type: "get",
            async: false, //不异步，先执行完ajax，再干别的
            url: "../../../manage/car/findBindedVehicleTree.do",
            dataType: "json",
            success: function(response) {
                cars = response;
            },
            error: function(XMLHttpRequest, textStatus, errorThrown) {  //#3这个error函数调试时非常有用，如果解析不正确，将会弹出错误框
                if (XMLHttpRequest.readyState == 4 && XMLHttpRequest.status == 200 && textStatus == "parsererror") {
                    layer.confirm('登录失效，是否刷新页面重新登录？', {
                        icon: 0,
                        title: ['登录失效', 'font-size:14px;color:#ffffff;background:#478de4;']
                    }, function() {
                        location.reload(true);
                    });
                }
            }
        });
        // cars = JSON.parse("[{\"name\":\"桂B42133\",\"pId\":1,\"id\":16777228},{\"name\":\"桂A12348\",\"pId\":1,\"id\":16777219},{\"name\":\"中石油南宁运输公司\",\"pId\":0,\"id\":1}]");
        return cars;
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
    var zNodes = findBindedVehicleTree();
    if (isNull(zNodes)) {
        $(".tree-box").html("无可用车辆。");
    } else {
        $.fn.zTree.init($("#treeDemo"), setting, zNodes); //初始化树
    }
    var carLayer;
    var terminalIds;
    $("#cars").click(function() {
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
            nodes.forEach(function(el) {
                treeObj.expandNode(el, true, false, false, false);
            });
        }
        carLayer = layer.open({
            type: 1,
            title: ['升级车辆', 'font-size:14px;color:#ffffff;background:#478de4;'],
            area: ['500px', '350px'],
            content: $('.cartree'),
            end: function() {
                // 折叠全部节点
                var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
                treeObj.expandAll(false);
            }
        });
    });

    $("#car_cancel").click(function() {
        layer.close(carLayer);
    });

    $("#car_confirm").click(function() {
        var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
        // var nodes = treeObj.getSelectedNodes(); // 此方法无效
        var nodes = treeObj.getCheckedNodes(true);
        var carnos = "";
        terminalIds = "";
        nodes.forEach(function(el) {
            if (el.id >= 0x01000001) {
                carnos += el.name + ", ";
                terminalIds += el.id + ",";
            }
        });
        carnos = carnos.substring(0, carnos.length - 2);
        terminalIds = terminalIds.substring(0, terminalIds.length - 1);
        $("#cars").val(carnos);
        layer.close(carLayer);
    });

    var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
    $("#cancel").click(function() {
        parent.layer.close(index);
    });

    $("#confirm").click(function() {
        var path = $("#path").val();
        if (isNull(path)) {
            layer.alert('FTP路径不能为空！', { icon: 2 }, function(index2) {
                layer.close(index2);
                $("#path").select();
            });
            return;
        }
        if (!isFtpPath(path)) {
            layer.alert('FTP路径格式不正确！', { icon: 2 }, function(index2) {
                layer.close(index2);
                $("#path").select();
            });
            return;
        }
        if (isNull(terminalIds)) {
            layer.alert('升级车辆不能为空！', { icon: 2 }, function(index2) {
                layer.close(index2);
                $("#cars").click();
            });
            return;
        }
        var fileNum = $("#files").children().length - 1;
        if (fileNum == 0) {
            layer.alert('升级文件不能为空！', { icon: 2 }, function(index2) {
                layer.close(index2);
                $("#add").click();
            });
            return;
        }
        var files = [];
        for (var i = 0; i < fileNum; i++) {
            var li = $("#files").children().eq(i);
            files.push({
                type: parseInt(li.children().eq(2).html(), 10),
                name: li.children().eq(1).html(),
                size: parseInt(li.children().eq(3).html(), 10),
                crc32: li.children().eq(4).html()
            });
        }
        var upgrade_files = encodeURIComponent(JSON.stringify(files));
        var loadIndex = layer.load();
        $.post("../../../manage/remote/asyn_terminal_upgrade_request",
            "terminal_ids=" + terminalIds + "&ftp_path=" + path + "&upgrade_files=" + upgrade_files +
            "&token=" + generateUUID(),
            function(data) {
                layer.close(loadIndex);
                if (data.id > 0) {
                    layer.msg(data.msg, {
                        icon: 2,
                        time: 500
                    });
                } else {
                    layer.msg('请求发送成功！', {
                        icon: 1,
                        time: 500
                    }, function() {
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
                }, function() {
                    location.reload(true);
                });
            }
        });
    });
});