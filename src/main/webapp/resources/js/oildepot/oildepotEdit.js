var reader_select_html = "<tr><td><select>";
$.ajax({
    type: "get",
    async: false, //不异步，先执行完ajax，再干别的
    url: "../../manage/oildepot/findUnusedReaders.do",
    dataType: "json",
    success: function(response) {
        response.forEach(function(reader) {
        	reader_select_html += "<option value=" + reader.devId + ">" + reader.devId + "</option>";
        });
        reader_select_html += "</select></td>" +
            // "<td style='display:none;'></td>" +
            "<td><select>" +
            "<option value=1>入库读卡器</option>" +
            "<option value=2>出库读卡器</option>" +
            "<option value=3>出入库读卡器</option>" +
            "</select></td>" +
            // "<td style='display:none;'></td>" +
            "<td><select>" +
            "<option value=0>未指定</option>" +
            "<option value=1>入库道闸</option>" +
            "<option value=2>出库道闸</option>" +
            "<option value=3>出入库道闸</option>" +
            "</select></td><td>" +
            "<img alt=\"取消\" title=\"取消\" src=\"../../resources/images/operate/cancel.png\" onclick=\"deleteTr(this)\">&emsp;" +
            "<img alt=\"确认\" title=\"确认\" src=\"../../resources/images/operate/confirm.png\" onclick=\"confirmReaderTr(this)\">" +
            "</td></tr>";
    },
    error: function(XMLHttpRequest, textStatus, errorThrown) {
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

function addReaderTr() {
    var index = $("#reader_info").find("tr").length - 1;
    var preTr = $("#reader_info").children().eq(0).children().eq(index - 1);
    var preTrLastChildren = preTr.children().last();
    if (preTrLastChildren.children().length > 1) {
        confirmReaderTr(preTrLastChildren);
    }
    $("#reader_info tr:eq(-2)").after(reader_select_html);
}

function confirmReaderTr(obj) {
    var tr = $(obj).closest('tr');
    tr.addClass("readerTrs");
    var trChildren = tr.children();
    var td_id = trChildren.first();
    var readerId = td_id.children().first().val();
    var reg = new RegExp("<option value=" + readerId + ">" + readerId + "</option>", "g");
    reader_select_html = reader_select_html.replace(reg, "");
    td_id.html(readerId);
    // var td_type = trChildren.eq(2);
    // var obj_type = td_type.children().first();
    // var readerType = obj_type.val();
    // var typeName = obj_type.find("option:selected").text();
    // trChildren.eq(1).html(readerType);
    // td_type.html(typeName);

    // var td_barrier = trChildren.eq(4);
    // var obj_barrier = td_barrier.children().first();
    // var barrier = obj_barrier.val();
    // var barrierName = obj_barrier.find("option:selected").text();
    // trChildren.eq(3).html(barrier);
    // td_barrier.html(barrierName);
    var oper = trChildren.last();
    oper.html("<img alt=\"删除\" title=\"删除\" src=\"../../resources/images/operate/delete.png\" onclick=\"deleteTr(this)\">");
}

var invalid = false;

function deleteTr(obj, type) {
    $(obj).closest('tr').remove();
    if (type > 0) {
        var serialNo = 0;
        $(".serialNo").each(function() {
            serialNo += 1;
            $(this).text(serialNo);
        });
    }
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
    var oper = trChildren.last();
    oper.html("<img alt=\"删除\" title=\"删除\" src=\"../../resources/images/operate/delete.png\" onclick=\"deleteTr(this,1)\">");
}

function changeCardId(obj) {
    var type = obj.value;
    var td = $(obj).closest('tr').children().eq(2);
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
            invalid = false;
            tdHtml += "</select>";
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
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
    var inCards = getCardId(2);
    var trHtml = "<tr><td class=\"serialNo\">" + index + "</td>" +
        "<td><select onchange=\"changeCardId(this)\">" +
        "<option value=2>入库卡</option>" +
        "<option value=3>出库卡</option>" +
        "<option value=4>出入库卡</option>" +
        "<option value=1>应急卡</option>" +
        // "<option value=5>普通卡</option>" +
        // "<option value=6>管理卡</option>" +
        "</select></td>" +
        "<td>" + inCards + "</td><td>" +
        "<img alt=\"取消\" title=\"取消\" src=\"../../resources/images/operate/cancel.png\" onclick=\"deleteTr(this)\">&emsp;" +
        "<img alt=\"确认\" title=\"确认\" src=\"../../resources/images/operate/confirm.png\" onclick=\"confirmTr(this)\">" +
        "</td></tr>";
    $("#card_info tr:eq(-2)").after(trHtml);
}


$(function() {
	// 必填字段添加星号标识
    $('input[required]').each(function() {
    	$(this).closest('tr').children().first().prepend('<span style="color:red">* </span>');
    });
    
    var index = parent.layer.getFrameIndex(window.name); //获取窗口索引

    var success_zh_text = "修改成功！";
    var error_zh_text = "修改失败！";
    var mode = $("#mode").val();

    $("#cancel").click(function() {
        parent.layer.close(index);
    });

    $("#confirm").click(function() {
        var id = parseInt($("#id").val(), 10);
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
        var url, param;

        if ("edit" == mode) {
            var cardIds = "";
            $(".cardIds").each(function() {
                cardIds += $(this).text() + ",";
            });
            var readers = [];
            // var barrierCount = parseInt($("#barrierCount").val(), 10);
            var barrierCount = 0;
            $(".readerTrs").each(function() {
                var barrier = parseInt($(this).children().eq(2).children().first().val(), 10);
                if (barrier > 0) {
                    barrierCount++;
                }
                var reader = {
                    oilDepotId: id,
                    devId: parseInt($(this).children().first().html(), 10),
                    type: parseInt($(this).children().eq(1).children().first().val(), 10),
                    barrier: barrier
                };
                readers.push(reader);
            });

            if (barrierCount < 1 && readers.length > 0) {
                layer.alert('每个油库至少要有一个读卡器用于道闸转发通知！', { icon: 2 });
                return;
            }

            if (barrierCount > 2) {
                layer.alert('每个油库最多只能有两个读卡器分别用于出库道闸和入库道闸转发通知！', { icon: 2 });
                return;
            }

            cardIds = cardIds.substring(0, cardIds.length - 1);

            url = "../../manage/oildepot/update.do";
            param = "id=" + id + "&officialId=" + officialId + "&name=" + name + "&abbr=" + abbr + "&director=" + director +
                "&phone=" + phone + "&address=" + address + "&company=" + company + "&longitude=" + longitude +
                "&latitude=" + latitude + "&radius=" + radius + "&coverRegion=" + region + "&remark=" + remark +
                "&cardIds=" + cardIds + "&readersJson=" + encodeURIComponent(JSON.stringify(readers));

        }

        if ("add" == mode) {
            url = "../../manage/oildepot/add.do";
            param = "officialId=" + officialId + "&name=" + name + "&abbr=" + abbr + "&director=" + director + "&phone=" + phone +
                "&address=" + address + "&company=" + company + "&longitude=" + longitude + "&latitude=" + latitude +
                "&radius=" + radius + "&coverRegion=" + region + "&remark=" + remark;

            success_zh_text = "添加成功！";
            error_zh_text = "添加失败！";

            if (isNull(officialId)) {
                layer.alert('油库编号不能为空！', { icon: 0 }, function(index2) {
                    layer.close(index2);
                    $("#officialId").select();
                });
                return;
            }
            if (isNull(name)) {
                layer.alert('油库名称不能为空！', { icon: 0 }, function(index2) {
                    layer.close(index2);
                    $("#name").select();
                });
                return;
            }
            if (isNull(abbr)) {
                layer.alert('油库简称不能为空！', { icon: 0 }, function(index2) {
                    layer.close(index2);
                    $("#abbr").select();
                });
                return;
            }
            var ajaxFlag = true; //ajax执行结果标记，用于判断是否中断整个程序（return若放在ajax回调函数中，则无法跳出ajax）
            $.ajax({
                type: "post",
                async: false, //不异步，先执行完ajax，再干别的
                url: "../../manage/oildepot/isExist.do",
                data: encodeURI("officialId=" + officialId + "&name=" + name + "&abbr=" + abbr),
                dataType: "json",
                success: function(response) {
                    if (response == true || response == "true") {
                        ajaxFlag = false;
                        layer.alert('油库已存在！', { icon: 5 }, function(index2) {
                            layer.close(index2);
                            $("#officialId").select();
                        });
                    }
                },
                error: function(XMLHttpRequest, textStatus, errorThrown) {
                    ajaxFlag = false;
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
        // if (isNull(address)) {
        //     layer.alert('地址不能为空！', { icon: 0 }, function(index2) {
        //         layer.close(index2);
        //         $("#address").select();
        //     });
        //     return;
        // }
        //
        // if (isNull(company)) {
        //     layer.alert('所属公司不能为空！', { icon: 0 }, function(index2) {
        //         layer.close(index2);
        //         $("#company").select();
        //     });
        //     return;
        // }

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

        var loadLayer = layer.load();
        $.post(url, param,
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