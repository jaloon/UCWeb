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
        var hid = $("#hid").val();
        var gasstation = $("#gasstation").val();
        var director = $.trim($("#director").val());
        var phone = $.trim($("#phone").val());
        var identity = $.trim($("#identity").val());
        var remark = $.trim($("#remark").val());
        var url = "../../manage/handset/update.do";
        var param = "id=" + id + "&deviceId=" + hid + "&gasStation.id=" + gasstation + "&director=" + director +
            "&phone=" + phone + "&identityCard=" + identity + "&remark=" + remark;

        if ("add" == mode) {
            url = "../../manage/handset/add.do";
            param = "&deviceId=" + hid + "&gasStation.id=" + gasstation + "&director=" + director +
                "&phone=" + phone + "&identityCard=" + identity + "&remark=" + remark;
            success_zh_text = "添加成功！";
            error_zh_text = "添加失败！"
        }

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
        //
        // if (!isIdCard(identity)) {
        //     layer.alert('身份证号不正确！', { icon: 5 }, function(index2) {
        //         layer.close(index2);
        //         $("#identity").select();
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