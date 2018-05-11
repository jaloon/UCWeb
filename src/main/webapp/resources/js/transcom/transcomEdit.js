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
                    }
                });
                if (!ajaxFlag) {
                    return;
                }
            }
        }

        if (isNull(address)) {
            layer.alert('地址不能为空！', { icon: 0 }, function(index2) {
                layer.close(index2);
                $("#address").select();
            });
            return;
        }

        if (isNull(director)) {
            layer.alert('负责人不能为空！', { icon: 2 }, function(index2) {
                layer.close(index2);
                $("#director").select();
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
        );

    });
});