var invalid = false;
function deleteTr(obj) {
    $(obj).closest('tr').remove();
    var serialNo = 0;
    $(".serialNo").each(function() {
        serialNo += 1;
        $(this).text(serialNo);
    });
    if (invalid) {
        invalid = false;
    }
}

function confirmTr(obj) {
    if (invalid) {
        deleteTr(obj);
        return;
    }
    var tr = $(obj).closest('tr');
    var trChildren = tr.children();
    var type = trChildren.eq(1);
    var cardType = type.children().first().find('option:selected').text();
    type.html(cardType);
    var id = tr.children().eq(2);
    var cardId = id.children().first().val();
    id.html(cardId);
    id.addClass("cardIds");
    var oper = tr.children().last();
    oper.html("<img alt=\"删除\" title=\"删除\" src=\"../../resources/images/operate/delete.png\" onclick=\"deleteTr(this)\">");
}

function changeCardId(obj) {
    var type = obj.value;
    var tr = $(obj).closest('tr');
    var td = tr.children().eq(2);
    td.empty();
    td.html(getCardId(type));
}

function getCardId(cardType) {
    var tdHtml = "<select>";
    $.ajax({
        type: "get",
        async: false, //不异步，先执行完ajax，再干别的
        url: "../../manage/card/findUnusedCard.do",
        data: encodeURI("cardType=" + cardType),
        dataType: "json",
        success: function(cardIds) {
            var len = cardIds.length;
            if (len == 0) {
                tdHtml = "无可配置的卡！"
                layer.alert(tdHtml, { icon: 0 });
                invalid = true;
                return;
            }
            var validCount = 0;
            for (var i = 0; i < len; i++) {
                cardId = cardIds[i];
                var flag = true;
                $(".cardIds").each(function() {
                    var existingId = $(this).text();
                    if (cardId == existingId) {
                        flag = false;
                        return;
                    }
                });
                if (flag) {
                    validCount++;
                    tdHtml += "<option value=" + cardId + ">" + cardId + "</option>";
                }
            }
            if (validCount == 0) {
                tdHtml = "无可配置的卡！"
                layer.alert(tdHtml, { icon: 0 });
                invalid = true;
                return;
            }
            tdHtml += "</select>";
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
    return tdHtml;
}

function addTr() {
    var index = $("#card_info").find("tr").length - 1;
    var preTr = $("#card_info").children().eq(0).children().eq(index - 1);
    if (invalid) {
        index--;
    }
    var preTrLastChildren = preTr.children().last();
    if (preTrLastChildren.children().length > 1) {
        confirmTr(preTrLastChildren);
    }
    var emergencyCards = getCardId(1);
    var trHtml = "<tr><td class=\"serialNo\">" + index + "</td>" +
        "<td><select onchange=\"changeCardId(this)\">" +
        "<option value=1>应急卡</option>" +
        // "<option value=2>入库卡</option>" +
        // "<option value=3>出库卡</option>" +
        // "<option value=4>出入库卡</option>" +
        "<option value=5>普通卡</option>" +
        // "<option value=6>管理卡</option>" +
        "</select></td>" +
        "<td>" + emergencyCards + "</td><td>" +
        "<img alt=\"取消\" title=\"取消\" src=\"../../resources/images/operate/cancel.png\" onclick=\"deleteTr(this)\">&emsp;" +
        "<img alt=\"确认\" title=\"确认\" src=\"../../resources/images/operate/confirm.png\" onclick=\"confirmTr(this)\">" +
        "</td></tr>";
    ////table#card_info的倒数第二行
    $("#card_info tr:eq(-2)").after(trHtml);
}


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
        var officialId = $.trim($("#officialId").val());
        var name = $.trim($("#name").val());
        var abbr = $.trim($("#abbr").val());
        var director = $.trim($("#director").val());
        var phone = $.trim($("#phone").val());
        var address = $.trim($("#address").val());
        var company = $.trim($("#company").val());
        var longitude = $.trim($("#longitude").val());
        var latitude = $.trim($("#latitude").val());
        var radius = $.trim($("#radius").val());
        var region = $.trim($("#region").val());
        var remark = $.trim($("#remark").val());
        var handset = $("#handset").val();
        var cardIds = "";
        $(".cardIds").each(function() {
            cardIds += $(this).text() + ",";
        });
        cardIds = cardIds.substring(0, cardIds.length - 1);
        var url = "../../manage/gasstation/update.do";
        var param = "id=" + id + "&officialId=" + officialId + "&name=" + name + "&abbr=" + abbr + "&director=" + director +
            "&phone=" + phone + "&address=" + address + "&company=" + company + "&longitude=" + longitude +
            "&latitude=" + latitude + "&radius=" + radius + "&coverRegion=" + region + "&remark=" + remark +
            "&handset=" + handset + "&cardIds=" + cardIds;

        if ("add" == mode) {
            url = "../../manage/gasstation/add.do";
            param = "officialId=" + officialId + "&name=" + name + "&abbr=" + abbr + "&director=" + director + "&phone=" + phone +
                "&address=" + address + "&company=" + company + "&longitude=" + longitude + "&latitude=" + latitude +
                "&radius=" + radius + "&coverRegion=" + region + "&remark=" + remark;
            success_zh_text = "添加成功！";
            error_zh_text = "添加失败！";

            if (isNull(officialId)) {
                layer.alert('加油站编号不能为空！', { icon: 0 }, function(index2) {
                    layer.close(index2);
                    $("#officialId").select();
                });
                return;
            } else {
                var ajaxFlag = true; //ajax执行结果标记，用于判断是否中断整个程序（return若放在ajax回调函数中，则无法跳出ajax）
                $.ajax({
                    type: "post",
                    async: false, //不异步，先执行完ajax，再干别的
                    url: "../../manage/gasstation/isExist.do",
                    data: encodeURI("officialId=" + officialId),
                    dataType: "json",
                    success: function(response) {
                        if (response == true || response == "true") {
                            ajaxFlag = false;
                            layer.alert('加油站编号已存在！', { icon: 5 }, function(index2) {
                                layer.close(index2);
                                $("#officialId").select();
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
            if (isNull(name)) {
                layer.alert('加油站名称不能为空！', { icon: 0 }, function(index2) {
                    layer.close(index2);
                    $("#name").select();
                });
                return;
            } else {
                var ajaxFlag = true; //ajax执行结果标记，用于判断是否中断整个程序（return若放在ajax回调函数中，则无法跳出ajax）
                $.ajax({
                    type: "post",
                    async: false, //不异步，先执行完ajax，再干别的
                    url: "../../manage/gasstation/isExist.do",
                    data: encodeURI("name=" + name),
                    dataType: "json",
                    success: function(response) {
                        if (response == true || response == "true") {
                            ajaxFlag = false;
                            layer.alert('加油站名称已存在！', { icon: 5 }, function(index2) {
                                layer.close(index2);
                                $("#name").select();
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

        if (isNull(address)) {
            layer.alert('地址不能为空！', { icon: 0 }, function(index2) {
                layer.close(index2);
                $("#address").select();
            });
            return;
        }

        if (isNull(company)) {
            layer.alert('所属公司不能为空！', { icon: 0 }, function(index2) {
                layer.close(index2);
                $("#company").select();
            });
            return;
        }

        if (!isLng(longitude)) {
            layer.alert('经度不符规则！', { icon: 0 }, function(index2) {
                layer.close(index2);
                $("#longitude").select();
            });
            return;
        }

        if (!isLat(latitude)) {
            layer.alert('纬度不符规则！', { icon: 0 }, function(index2) {
                layer.close(index2);
                $("#latitude").select();
            });
            return;
        }

        if (!isNonnegativeInteger(radius)) {
            layer.alert('施解封半径必须为非负整数！', { icon: 0 }, function(index2) {
                layer.close(index2);
                $("#radius").select();
            });
            return;
        }

        // $.post(url, encodeURI(param),
        //     function(data) {
        //         if ("error" == data.msg) {
        //             layer.msg(error_zh_text, { icon: 2, time: 500 });
        //         } else {
        //             layer.msg(success_zh_text, { icon: 1, time: 500 }, function() {
        //                 parent.layer.close(index);
        //             });
        //         }
        //     },
        //     "json"
        // ).error(function (XMLHttpRequest, textStatus, errorThrown) {
        //     if (XMLHttpRequest.readyState == 4 && XMLHttpRequest.status == 200 && textStatus == "parsererror") {
        //         layer.confirm('登录失效，是否刷新页面重新登录？', {
        //             icon: 0,
        //             title: ['登录失效', 'font-size:14px;color:#ffffff;background:#478de4;']
        //         }, function() {
        //             location.reload(true);
        //         });
        //     }
        // });

    });
});