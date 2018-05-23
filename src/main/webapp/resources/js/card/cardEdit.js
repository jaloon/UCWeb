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
        var cid = $("#cid").val();
        var type = $("#type").val();
        var director = $.trim($("#director").val());
        var phone = $.trim($("#phone").val());
        var identity = $.trim($("#identity").val());
        var remark = $.trim($("#remark").val());
        var url = "../../manage/card/update.do";
        var param = "id=" + id + "&cardId=" + cid + "&type=" + type + "&director=" + director + "&phone=" + phone + "&identityCard=" + identity + "&remark=" + remark;

        if ("add" == mode) {
            url = "../../manage/card/add.do";
            param = "cardId=" + cid + "&type=" + type + "&director=" + director + "&phone=" + phone + "&identityCard=" + identity + "&remark=" + remark;
            success_zh_text = "添加成功！";
            error_zh_text = "添加失败！";

            if (isNull(cid)) {
                layer.alert('卡ID不能为空！', { icon: 0 }, function(index2) {
                    layer.close(index2);
                    $("#cid").select();
                });
                return;
            } else {
                var ajaxFlag = true; //ajax执行结果标记，用于判断是否中断整个程序（return若放在ajax回调函数中，则无法跳出ajax）
                $.ajax({
                    type: "post",
                    async: false, //不异步，先执行完ajax，再干别的
                    url: "../../manage/card/isExist.do",
                    data: encodeURI("cardId=" + cid),
                    dataType: "json",
                    success: function(response) {
                        if (response == true || response == "true") {
                            ajaxFlag = false;
                            layer.alert('卡ID已存在！', { icon: 5 }, function(index2) {
                                layer.close(index2);
                                $("#cid").select();
                            });
                        }
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
                if (!ajaxFlag) {
                    return;
                }
            }
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

        if (!isIdCard(identity)) {
            layer.alert('身份证号不正确！', { icon: 5 }, function(index2) {
                layer.close(index2);
                $("#identity").select();
            });
            return;
        }


        $.post(url, encodeURI(param),
            function(data) {
                // switch (data.id) {
                //     case 0: // 操作成功
                //         layer.msg(success_zh_text, {icon: 1, time: 500}, function () {
                //             parent.layer.close(index);
                //         });
                //         break;
                //     case 1: // 登录异常
                //
                //         break;
                //     default: // 操作失败
                //         layer.msg(error_zh_text, {icon: 2, time: 500});
                //         break;
                // }
                if ("error" == data.msg) {
                    layer.msg(error_zh_text, {icon: 2, time: 500});
                } else {
                    layer.msg(success_zh_text, { icon: 1, time: 500 }, function() {
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