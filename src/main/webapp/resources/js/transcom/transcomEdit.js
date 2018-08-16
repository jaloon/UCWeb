$(function() {
    // 必填字段添加星号标识
	$('input[required]').each(function(i, e) {
    	$(e).closest('tr').children().first().prepend('<span style="color:red">* </span>');
    });
	
    var index = parent.layer.getFrameIndex(window.name); //获取窗口索引

    var success_zh_text = "修改成功！";
    var error_zh_text = "修改失败！";
    var mode = $("#mode").val();

    $("#cancel").click(function() {
        parent.layer.close(index);
    });

    $("#confirm").click(function() {
        var id = $("#id").val();
        var name = $.trim($("#name").val());
        var address = $.trim($("#address").val());
        var director = $.trim($("#director").val());
        var phone = $.trim($("#phone").val());
        var superior = $("#superior").val();
        var remark = $.trim($("#remark").val());
        var url = "../../manage/transcom/update.do";
        var param = "id=" + id + "&name=" + name + "&address=" + address + "&director=" + director + "&phone=" + phone + "&superior.id=" + superior + "&remark=" + remark;

        if ("add" == mode) {
            url = "../../manage/transcom/add.do";
            param = "name=" + name + "&address=" + address + "&director=" + director + "&phone=" + phone + "&superior.id=" + superior + "&remark=" + remark;
            success_zh_text = "添加成功！";
            error_zh_text = "添加失败！";
            if (isNull(name)) {
                layer.alert('运输公司名称不能为空！', { icon: 0 }, function(index2) {
                    layer.close(index2);
                    $("#name").select();
                });
                return;
            } else {
                var ajaxFlag = true; //ajax执行结果标记，用于判断是否中断整个程序（return若放在ajax回调函数中，则无法跳出ajax）
                $.ajax({
                    type: "post",
                    async: false, //不异步，先执行完ajax，再干别的
                    url: "../../manage/transcom/isExist.do",
                    data: encodeURI("name=" + name),
                    dataType: "json",
                    success: function(response) {
                        if (response == true || response == "true") {
                            ajaxFlag = false;
                            layer.alert('运输公司名称已存在！', { icon: 5 }, function(index2) {
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

        // if (isNull(address)) {
        //     layer.alert('地址不能为空！', { icon: 0 }, function(index2) {
        //         layer.close(index2);
        //         $("#address").select();
        //     });
        //     return;
        // }
        //
        // if (isNull(director)) {
        //     layer.alert('负责人不能为空！', { icon: 2 }, function(index2) {
        //         layer.close(index2);
        //         $("#director").select();
        //     });
        //     return;
        // }
        //
        // if (!isPhone(phone)) {
        //     layer.alert('电话号码不正确！', { icon: 5 }, function(index2) {
        //         layer.close(index2);
        //         $("#phone").select();
        //     });
        //     return;
        // }

        var loadLayer = layer.load();
        $.post(url, encodeURI(param),
            function(data) {
                layer.close(loadLayer);
                if ("error" == data.msg) {
                    layer.alert(error_zh_text + "<br>" + data.e, {icon: 2});
                } else {
                    layer.msg(success_zh_text, { icon: 1, time: 500 }, function() {
                        parent.layer.close(index);
                    });
                }
            },
            "json"
        ).error(function (XMLHttpRequest, textStatus, errorThrown) {
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