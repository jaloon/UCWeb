var getDriver;
var deleteNewTr;
var deleteTr;
var confirmTr;
var changeDriverName;
var addTr;

$(function () {
    getDriver = function () {
        var drivers = null;
        $.ajax({
            type: "get",
            async: false, //不异步，先执行完ajax，再干别的
            url: "../../manage/driver/findFreeDrivers.do",
            dataType: "json",
            success: function (response) {
                drivers = response;
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
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
        return drivers;
    };

    var drivers = getDriver();

    deleteNewTr = function (obj) {
        $(obj).closest('tr').remove();
        var serialNo = 0;
        $(".serialNo").each(function () {
            serialNo += 1;
            $(this).text(serialNo);
        });
    };

    deleteTr = function (obj, driverId) {
        deleteNewTr(obj);
        $.getJSON("../../manage/driver/getDriverById.do", encodeURI("id=" + driverId),
            function (data) {
                var len = drivers.length;
                if ((len > 0 && !drivers.isContain(data)) || len == 0) {
                    drivers.push(data);
                }
            }
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
    };

    confirmTr = function (obj) {
        var tr = $(obj).closest('tr');
        var name = tr.children().eq(2);
        var select = name.children().first();
        var driverId = select.val();
        var driverName = select.find('option:selected').text();
        tr.children().eq(1).addClass("driverIds");
        name.empty();
        name.html(driverName);
        var oper = tr.children().last();
        oper.empty();
        oper.html("<img alt=\"删除\" title=\"删除\" src=\"../../resources/images/operate/delete.png\" onclick=\"deleteTr(this," + driverId + ")\">");
        for (var i = 0, len = drivers.length; i < len; i++) {
            var driver = drivers[i];
            if (driver.name == driverName) {
                drivers.removeByValue(driver);
                break;
            }
        }
    };

    changeDriverName = function (obj, driverId) {
        $.getJSON("../../manage/driver/getDriverById.do", encodeURI("id=" + driverId),
            function (data) {
                var tr = $(obj).closest('tr');
                var id = tr.children().eq(1);
                var phone = tr.children().eq(3);
                var address = tr.children().eq(4);
                id.html(driverId);
                phone.html(data.phone);
                address.html(data.address);
            }
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
    };

    addTr = function () {
        var index = $("#driver_info").find("tr").length - 1;
        var preTr = $("#driver_info").children().eq(0).children().eq(index - 1);
        if (preTr.children().eq(2).children().length > 0) {
            confirmTr(preTr.children().last());
        }
        if (drivers.length > 0) {
            var driver = drivers[0];
            var trHtml = "<tr><td class=\"serialNo\">" + index + "</td>" +
                "<td style=\"display:none\">" + driver.id + "</td>" +
                "<td><select onchange=\"changeDriverName(this,this.value)\">";
            for (var i = 0, len = drivers.length; i < len; i++) {
                trHtml += "<option value=" + drivers[i].id + ">" + drivers[i].name + "</option>";
            }
            trHtml += "</select></td>" +
                "<td>" + driver.phone + "</td>" +
                "<td>" + driver.address + "</td>" +
                "<td>" +
                "<img alt=\"取消\" title=\"取消\" src=\"../../resources/images/operate/cancel.png\" onclick=\"deleteNewTr(this)\">&emsp;" +
                "<img alt=\"确认\" title=\"确认\" src=\"../../resources/images/operate/confirm.png\" onclick=\"confirmTr(this)\">" +
                "</td></tr>";
            $("#driver_info tr:eq(-2)").after(trHtml);
        } else {
            layer.alert('没有多余的司机可供分配了，<br>请前往司机管理添加司机！', {icon: 5});
        }
    };

    // 必填字段添加星号标识
    $('input[required]').each(function (i, e) {
        $(e).closest('tr').children().first().prepend('<span style="color:red">* </span>');
    });

    var index = parent.layer.getFrameIndex(window.name); //获取窗口索引

    var success_zh_text = "修改成功！";
    var error_zh_text = "修改失败！";
    var mode = $("#mode").val();

    $("#cancel").click(function () {
        parent.layer.close(index);
    });

    $("#confirm").click(function () {
        var cid = trimAll($("#cid").val());
        var company = $("#company").val();
        var type = $("#type").val();
        var sim = $.trim($("#sim").val());
        var transcard = $("#transcard").val();
        if (transcard === "") {
            transcard = $("#transcard").next().next().next().val();
            if (transcard === "") {
                transcard = '0';
            }
        }
        var store = $.trim($("#store").val());
        var remark = $.trim($("#remark").val());
        var driverIds = "";
        $(".driverIds").each(function () {
            driverIds += $(this).text() + ",";
        });
        driverIds = driverIds.substring(0, driverIds.length - 1);
        var url, param;
        if ("edit" == mode) {
            var id = $("#id").val();
            var locks = [];
            $(".locks").each(function () {
                var lockTds = $(this).children();
                locks.push({
                    id: parseInt(lockTds.first().html(), 10),
                    remark: lockTds.last().children().first().val()
                });
            });
            url = "../../manage/car/update.do";
            param = "id=" + id + "&carNumber=" + encodeURIComponent(cid) + "&transCompany.id=" + company
                + "&type=" + type + "&sim=" + sim + "&transportCard.transportCardId=" + transcard
                + "&storeNum=" + store + "&remark=" + remark + "&driverIds=" + driverIds
                + "&locksJson=" + encodeURIComponent(JSON.stringify(locks));
        } else if ("add" == mode) {
            url = "../../manage/car/add.do";
            param = "carNumber=" + encodeURIComponent(cid) + "&transCompany.id=" + company + "&type=" + type
                + "&transportCard.transportCardId=" + transcard + "&storeNum=" + store + "&remark=" + remark
                + "&driverIds=" + driverIds;

            success_zh_text = "添加成功！";
            error_zh_text = "添加失败！";

            if (!isCarNo(cid)) {
                layer.alert('车牌号不正确，请输入一个完整的车牌号！', {icon: 2}, function (index2) {
                    layer.close(index2);
                    $("#cid").select();
                });
                return;
            } else {
                var ajaxFlag = true; //ajax执行结果标记，用于判断是否中断整个程序（return若放在ajax回调函数中，则无法跳出ajax）
                $.ajax({
                    type: "post",
                    async: false, //不异步，先执行完ajax，再干别的
                    url: "../../manage/car/getCarByNo.do",
                    data: encodeURI("carNo=" + cid),
                    dataType: "json",
                    success: function (response) {
                        if (!isNull(response)) {
                            ajaxFlag = false;
                            layer.alert('车牌号已存在！', {icon: 5}, function (index2) {
                                layer.close(index2);
                                $("#officialId").select();
                            });
                        }
                    },
                    error: function (XMLHttpRequest, textStatus, errorThrown) {
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
        }


        if (isNull(store)) {
            layer.alert('仓数不能为空！', {icon: 2}, function (index2) {
                layer.close(index2);
                $("#store").select();
            });
            return;
        }

        var loadLayer = layer.load();
        $.post(url, param,
            function (data) {
                layer.close(loadLayer);
                if ("error" == data.msg) {
                    layer.alert(error_zh_text + "<br>" + data.e, {icon: 2});
                } else {
                    layer.msg(success_zh_text, {icon: 1, time: 500}, function () {
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