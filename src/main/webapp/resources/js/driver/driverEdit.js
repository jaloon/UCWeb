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
        var phone = $.trim($("#phone").val());
        var identity = $.trim($("#identity").val());
        var address = $.trim($("#address").val());
        var remark = $.trim($("#remark").val());
        var url = "../../manage/driver/update.do";
        var param = "id=" + id + "&name=" + name + "&phone=" + phone + "&identityCard=" + identity + "&address=" + address + "&remark=" + remark;

        if ("add" == mode) {
            url = "../../manage/driver/add.do";
            param = "name=" + name + "&phone=" + phone + "&identityCard=" + identity + "&address=" + address + "&remark=" + remark;
            success_zh_text = "添加成功！";
            error_zh_text = "添加失败！"
        }

        if (isNull(name)) {
            layer.alert('姓名不能为空！', { icon: 2 }, function(index2) {
                layer.close(index2);
                $("#name").select();
            });
            return;
        }

        if (!isPhone(phone)) {
            layer.alert('电话号码不正确！', { icon: 5 }, function(index2) {
                layer.close(index2);
                $("#phone").select();
            });
            return;
        }

        if (!isIdCard(identity)) {
            layer.alert('身份证号不正确！', { icon: 5 }, function(index2) {
                layer.close(index2);
                $("#identity").select();
            });
            return;
        }

        if (isNull(address)) {
            layer.alert('地址不能为空！', { icon: 0 }, function(index2) {
                layer.close(index2);
                $("#address").select();
            });
            return;
        }

        $.post(url, encodeURI(param),
            function(data) {
                if ("error" == data.msg) {
                    layer.msg(error_zh_text, { icon: 2, time: 500 });
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