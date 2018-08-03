$(function() {
    // 必填字段添加星号标识
	$('input[required]').each(function(i, e) {
    	$(e).closest('tr').children().first().prepend('<span style="color:red">* </span>');
    });
	
    var index = parent.layer.getFrameIndex(window.name); //获取窗口索引

    var success_zh_text = "修改成功！";
    var error_zh_text = "修改失败！";
    var mode = $("#mode").val();

    $("#confirm").click(function() {
        if ("pwd" == mode) {
            var id = $("#id").val();
            var oldPwd = $.trim($("#oldPwd").val());
            var pwd = $.trim($("#pwd").val());
            var repwd = $.trim($("#repwd").val());
            var url = "../../manage/user/updatePwd.do";
            var param = "id=" + id + "&password=" + pwd + "&oldPwd=" + oldPwd;
            if (isNull(oldPwd) || isNull(pwd) || isNull(repwd)) {
                layer.alert('密码不能为空！', { icon: 5 });
                return;
            }
            if (pwd != repwd) {
                layer.alert('两次输入新密码不一致！', { icon: 5 });
                return;
            }
            $.post(url, encodeURI(param),
                function(data) {
                    if ("error" == data.msg) {
                        if ("oldPwdError" == data.e) {
                            layer.alert('原密码不正确！', { icon: 2 }, function(index2) {
                                layer.close(index2);
                                $("#oldPwd").select();
                            });
                        } else {
                            layer.msg(error_zh_text, { icon: 2, time: 500 });
                        }
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
        } else {
            var id = $("#id").val();
            var account = $.trim($("#account").val());
            var roleId = $("#roleId").val();
            var appRoleId = $("#appRoleId").val();
            var comId = $("#comId").val();
            var name = $.trim($("#name").val());
            var phone = $.trim($("#phone").val());
            var identityCard = $.trim($("#identityCard").val());
            var remark = $.trim($("#remark").val());
            var url = "../../manage/user/update.do";
            var param = "id=" + id + "&account=" + account + "&role.id=" + roleId + "&appRole.id=" + appRoleId + "&comId="
                + comId + "&name=" + name + "&phone=" + phone + "&identityCard=" + identityCard + "&remark=" + remark;

            if ("add" == mode) {
                account = $.trim($("#account").val());
                url = "../../manage/user/add.do";
                param = "account=" + account + "&role.id=" + roleId + "&appRole.id=" + appRoleId + "&comId=" + comId +
                    "&name=" + name + "&phone=" + phone + "&identityCard=" + identityCard + "&remark=" + remark;
                success_zh_text = "添加成功！";
                error_zh_text = "添加失败！";
                if (!isAccount(account)) {
                    layer.alert('账号不符规则！', { icon: 5 }, function(index2) {
                        layer.close(index2);
                        $("#account").select();
                    });
                    return;
                } else {
                    var ajaxFlag = true; //ajax执行结果标记，用于判断是否中断整个程序（return若放在ajax回调函数中，则无法跳出ajax）
                    $.ajax({
                        type: "post",
                        async: false, //不异步，先执行完ajax，再干别的
                        url: "../../manage/user/isExist.do",
                        data: encodeURI("account=" + account),
                        dataType: "json",
                        success: function(response) {
                            if (response == true || response == "true") {
                                ajaxFlag = false;
                                layer.alert('账号已存在！', { icon: 5 }, function(index2) {
                                    layer.close(index2);
                                    $("#account").select();
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

            if (isNull(name)) {
                layer.alert('姓名不能为空！', { icon: 0 }, function(index2) {
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

            if (!isIdCard(identityCard)) {
                layer.alert('身份证号不正确！', { icon: 5 }, function(index2) {
                    layer.close(index2);
                    // $("#identityCard").focus();
                    $("#identityCard").select();
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

        }
    });

    $("#cancel").click(function() {
        parent.layer.close(index);
    });
});