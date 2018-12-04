/**
 * 车辆状态
 * @param {number} status 车辆状态值
 * @returns {string} 车辆状态
 */
function parseCarStatus(status) {
    switch (status) {
        case 1:
            return "在油库 - 解封";
        case 2:
            return "在途中 - 施封";
        case 3:
            return "在加油站 - 解封";
        case 4:
            return "返程中 - 施封";
        case 5:
            return "应急 - 解封";
        case 6:
            return "油区外[待进道闸] - 施封";
        case 7:
            return "在油区[已进道闸] - 解封";
        default:
            return "未知(" + status + ")";
    }
}

/**
 * 施解封类型
 * @param type
 * @returns {string}
 */
function parseSealType(type) {
    switch (type) {
        case 1:
            return "进油库 -> 解封";
        case 2:
            return "出油库 -> 施封";
        case 3:
            return "进加油站 -> 解封";
        case 4:
            return "出加油站 -> 施封";
        case 5:
            return "进入应急 -> 解封";
        case 6:
            return "取消应急 -> 施封";
        case 7:
            return "状态强制变更";
        case 8:
            return "油区外[待进道闸] -> 施封";
        case 9:
            return "进油区[进道闸] -> 解封";
        case 10:
            return "出油区[出道闸] -> 施封";
        default:
            return "未知(" + type + ")";
    }
}

/**
 * 认证类型
 * @param {number} authtype 认证类型值
 * @returns {string} 认证类型
 */
function parseAuthType(authtype) {
    switch (authtype) {
        case 0:
            return "其他方式";
        case 1:
            return "出入库读卡器";
        case 2:
            return "出入库卡";
        case 3:
            return "加油站"; //手持机
        case 4:
            return "加油站"; //普通卡
        case 5:
            return "应急卡";
        case 6:
            return "远程操作";
        case 7:
            return "操作员";
        default:
            return "未知(" + authtype + ")";
    }
}

function showSealBMap(id) {
    layer.open({
        type: 2,
        title: ['车辆施解封记录查询', 'font-size:14px;color:#ffffff;background:#478de4;'],
        // shadeClose: true,
        shade: 0.6,
        area: ['800px', '560px'],
        content: '../../manage/statistics/dispatch.do?' + encodeURI('mode=seal&id=' + id)
    });
}

function showLockBMap(id) {
    layer.open({
        type: 2,
        title: ['锁动作记录查询', 'font-size:14px;color:#ffffff;background:#478de4;'],
        // shadeClose: true,
        shade: 0.6,
        area: ['800px', '560px'],
        content: '../../manage/statistics/dispatch.do?' + encodeURI('mode=lock&id=' + id)
    });
}

$(function () {
    $.getJSON("../../../manage/car/selectCars.do", "scope=0&comlimit=1",
        function (data, textStatus, jqXHR) {
            var selectObj = $('#text_car');
            selectObj.append(data.com);
            var cars = data.car;
            var groupObj;
            for (var i = 0, len = cars.length; i < len; i++) {
                var car = cars[i];
                groupObj = $("#com_" + car.comId);
                groupObj.append("<option value = '" + car.carNumber + "'>" + car.carNumber + "</option>");
            }
            selectObj.comboSelect();
            $("#hidden_car").show();
            selectObj.hide();
            selectObj.closest(".combo-select").css({
                position: 'absolute',
                'z-index': 100000,
                left: '100px',
                top: '30px',
                width: '266px',
                height: '34px',
                'font-size': '16px',
                "margin-bottom": "0px"
            });
            selectObj.siblings(".combo-input").height(10);
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

    laydate.render({
        elem: '#text_begin',
        type: 'datetime',
        // value: '2017-10-01 00:00:00',
        // value: new Date(new Date().setDate(new Date().getDate() - 3)), //3天前
        value: new Date(new Date().setHours(0, 0, 0, 0)), //当天零点
        min: -90,
        max: new Date().getTime()
    });
    laydate.render({
        elem: '#text_end',
        type: 'datetime',
        value: new Date(),
        min: -90, //指定日期最小值为90天前
        max: new Date().getTime() //new Date().getTime()获取当前时间的时间戳，指定日期最大值为当前时间
    });

    function showSealList(carNumber, type, begin, end, pageId) {
        var rows = $("#page_size_seal").val();
        var startRow = (pageId - 1) * rows;
        var loadLayer = layer.load();
        $.post(
            "../../manage/statistics/findSealRecordsForPage.do",
            encodeURI("carNumber=" + carNumber + "&type=" + type + "&begin=" + begin + "&end=" + end
                + "&pageId=" + pageId + "&startRow=" + startRow + "&rows=" + rows),
            function (gridPage) {
                layer.close(loadLayer);
                var pageCount = gridPage.total;
                if (pageCount > 1) {
                    var pageOpts = "";
                    for (var i = 1; i <= pageCount; i++) {
                        pageOpts += "<option value=" + i + ">" + i + "</option>";
                    }
                    $("#page_id_seal").html(pageOpts);
                    $("#page_id_seal").val(gridPage.page);
                } else {
                    $("#page_id_seal").html("<option value='1'>1</option>");
                }
                var dataCount = gridPage.currentRows;
                $("#page_info_seal").html("页(" + dataCount + "条数据)/共" + pageCount + "页(共" + gridPage.records + "条数据)");
                $("#qcar_seal").val(gridPage.t.carNumber);
                $("#qtype_seal").val(gridPage.t.type);
                $("#qbegin_seal").val(gridPage.t.begin);
                $("#qend_seal").val(gridPage.t.end);
                var seals = gridPage.dataList;
                var jsonData = [];
                for (var i = 0; i < dataCount; i++) {
                    var seal = seals[i];
                    var coordFlag = seal.longitude != undefined && seal.latitude != undefined;
                    var gps = "-", coordinate = "-";
                    if (coordFlag) {
                        gps = seal.coorValid ? "有效" : "无效";
                        coordinate = seal.longitude + ", " + seal.latitude;
                    }
                    jsonData.push({
                        id: seal.id,
                        carNo: seal.carNumber,
                        time: seal.createDate,
                        coordFlag: coordFlag,
                        gps: gps,
                        coordinate: coordinate,
                        velocity: seal.velocity === undefined ? "-" : seal.velocity,
                        aspect: angle2aspect(seal.angle),
                        prestatus: parseCarStatus(seal.prestatus),
                        status: parseCarStatus(seal.status),
                        type: parseSealType(seal.type),
                        station: seal.station,
                        authtype: parseAuthType(seal.authtype),
                        authid: seal.authid,
                        operator: seal.operator,
                        alarm: seal.alarm == undefined ? "-" : seal.alarm
                    });
                }
                // 更新表格数据
                var tableHtml = template('seal-table-tpl', {data: jsonData});
                $('.c-table').eq(0).data('table').updateHtml(tableHtml);
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
    }

    function showLockList(carNumber, type, begin, end, pageId) {
        var rows = $("#page_size_lock").val();
        var startRow = (pageId - 1) * rows;
        var loadLayer = layer.load();
        $.post(
            "../../manage/statistics/findLockRecordsForPage.do",
            encodeURI("carNumber=" + carNumber + "&status=" + type + "&begin=" + begin + "&end=" + end
                + "&pageId=" + pageId + "&startRow=" + startRow + "&rows=" + rows),
            function (gridPage) {
                layer.close(loadLayer);
                var pageCount = gridPage.total;
                if (pageCount > 1) {
                    var pageOpts = "";
                    for (var i = 1; i <= pageCount; i++) {
                        pageOpts += "<option value=" + i + ">" + i + "</option>";
                    }
                    $("#page_id_lock").html(pageOpts);
                    $("#page_id_lock").val(gridPage.page);
                } else {
                    $("#page_id_lock").html("<option value='1'>1</option>");
                }
                var dataCount = gridPage.currentRows;
                $("#page_info_lock").html("页(" + dataCount + "条数据)/共" + pageCount + "页(共" + gridPage.records + "条数据)");
                $("#qcar_lock").val(gridPage.t.carNumber);
                $("#qtype_lock").val(gridPage.t.status);
                $("#qbegin_lock").val(gridPage.t.begin);
                $("#qend_lock").val(gridPage.t.end);
                var locks = gridPage.dataList;
                var jsonData = [];
                for (var i = 0; i < dataCount; i++) {
                    var lock = locks[i];
                    var coordFlag = lock.longitude != undefined && lock.latitude != undefined;
                    var gps = "-", coordinate = "-";
                    if (coordFlag) {
                        gps = lock.coorValid ? "有效" : "无效";
                        coordinate = lock.longitude + ", " + lock.latitude;
                    }
                    jsonData.push({
                        id: lock.id,
                        carNo: lock.carNumber,
                        // station: lock.station,
                        coordFlag: coordFlag,
                        gps: gps,
                        coordinate: coordinate,
                        velocity: lock.velocity === undefined ? "-" : lock.velocity,
                        aspect: angle2aspect(lock.angle),
                        dev: lock.lockId,
                        store: "仓" + lock.storeId + "-" + lock.seatName + "-" + lock.seatIndex,
                        status: lock.statusName,
                        time: lock.createDate,
                        station: lock.station,
                        report: lock.changeReportTime,
                        alarm: lock.alarm == undefined ? "-" : lock.alarm
                    });
                }
                // 更新表格数据
                var tableHtml = template('lock-table-tpl', {data: jsonData});
                $('.c-table').eq(1).data('table').updateHtml(tableHtml);
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
    }

    $(window).resize(function () {
        var contentH = ($(window).height() - 77) / 2;
        $(".content").height(contentH);
        $('.table-box').data('height', contentH - 108);
        // $(window).trigger('resize');
    }).resize();

    $('[role="c-table"]').jqTable();
    showSealList("", "", "", "", 1);
    showLockList("", "", "", "", 1);

    $("#search_btn_seal").click(function () {
        var carNumber = $("#text_car").val();
        var type = $("#text_type_seal").val();
        var begin = $("#text_begin").val();
        var end = $("#text_end").val();
        showSealList(carNumber, type, begin, end, 1);
    });

    $("#search_btn_lock").click(function () {
        var carNumber = $("#text_car").val();
        var type = $("#text_type_lock").val();
        var begin = $("#text_begin").val();
        var end = $("#text_end").val();
        showLockList(carNumber, type, begin, end, 1);
    });

    function refreshSeal(pageId) {
        var carNumber = $("#qcar_seal").val();
        if (isNull(carNumber)) {
            carNumber = "";
        }
        var type = $("#qtype_seal").val();
        if (isNull(type)) {
            type = "";
        }
        var begin = $("#qbegin_seal").val();
        if (isNull(begin)) {
            begin = "";
        }
        var end = $("#qend_seal").val();
        if (isNull(end)) {
            end = "";
        }
        showSealList(carNumber, type, begin, end, pageId);
    }

    $("#page_id_seal").change(function () {
        var pageId = $("#page_id_seal").val();
        if (isNull(pageId)) {
            pageId = 1;
        }
        refreshSeal(pageId);
    });

    $("#page_size_seal").change(function () {
        refreshSeal(1);
    });

    function refreshLock(pageId) {
        var carNumber = $("#qcar_lock").val();
        if (isNull(carNumber)) {
            carNumber = "";
        }
        var type = $("#qtype_lock").val();
        if (isNull(type)) {
            type = "";
        }
        var begin = $("#qbegin_lock").val();
        if (isNull(begin)) {
            begin = "";
        }
        var end = $("#qend_lock").val();
        if (isNull(end)) {
            end = "";
        }
        showLockList(carNumber, type, begin, end, pageId);
    }

    $("#page_id_lock").change(function () {
        var pageId = $("#page_id_lock").val();
        if (isNull(pageId)) {
            pageId = 1;
        }
        refreshLock(pageId);
    });

    $("#page_size_lock").change(function () {
        refreshLock(1);
    });
});