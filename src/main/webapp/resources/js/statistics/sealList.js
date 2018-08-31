/**
 * 车辆状态
 * @param {number} status 车辆状态值
 * @returns {string} 车辆状态
 */
function parseCarStatus(status) {
    switch (status) {
        case 1:
            return "在油库";
        case 2:
            return "在途";
        case 3:
            return "在加油站";
        case 4:
            return "返程";
        case 5:
            return "应急";
        case 6:
            return "油区外";
        case 7:
            return "在油区";
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
            return "进油库";
        case 2:
            return "出油库";
        case 3:
            return "进加油站";
        case 4:
            return "出加油站";
        case 5:
            return "进入应急";
        case 6:
            return "取消应急";
        case 7:
            return "状态强制变更";
        case 8:
            return "待进油区";
        case 9:
            return "进油区";
        case 10:
            return "出油区";
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
            return "手持机";
        case 4:
            return "普通卡";
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

function showBMap(id) {
    layer.open({
        type: 2,
        title: ['车辆施解封记录查询', 'font-size:14px;color:#ffffff;background:#478de4;'],
        // shadeClose: true,
        shade: 0.6,
        area: ['800px', '560px'],
        content: '../../manage/statistics/dispatch.do?' + encodeURI('mode=seal&id=' + id)
    });
}
$(function() {
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
        value: new Date(new Date().setDate(new Date().getDate() - 3)), //3天前
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

    function showList(carNumber, type, begin, end, pageId) {
        var rows = $("#page_size").val();
        var startRow = (pageId - 1) * rows;
        var loadLayer = layer.load();
        $.post(
            "../../manage/statistics/findSealRecordsForPage.do",
            encodeURI("carNumber=" + carNumber + "&type=" + type + "&begin=" + begin + "&end=" + end + "&pageId=" + pageId + "&startRow=" + startRow + "&rows=" + rows),
            function(gridPage) {
                layer.close(loadLayer);
                var pageCount = gridPage.total;
                if (pageCount > 1) {
                    var pageOpts = "";
                    for (var i = 1; i <= pageCount; i++) {
                        pageOpts += "<option value=" + i + ">" + i + "</option>";
                    }
                    $("#page_id").html(pageOpts);
                    $("#page_id").val(gridPage.page);
                } else {
                    $("#page_id").html("<option value='1'>1</option>");
                }
                $("#page_info").html("页(" + gridPage.currentRows + "条数据)/共" + pageCount + "页(共" + gridPage.records + "条数据)");
                $("#qcar").val(gridPage.t.carNumber);
                $("#qtype").val(gridPage.t.type);
                $("#qbegin").val(gridPage.t.begin);
                $("#qend").val(gridPage.t.end);
                $(".table-body").html("");
                var seals = gridPage.dataList;
                var tableData = "<table width='100%'>";
                for (var i = 0; i < gridPage.currentRows; i++) {
                    var seal = seals[i];
                    var coordFlag = seal.longitude == undefined || seal.latitude == undefined;
                    tableData += (coordFlag == true ? "<tr>" : "<tr ondblclick=\"showBMap(" + seal.id + ")\">") +
                        "<td class=\"inout-id\">" + seal.id + "</td>" +
                        "<td class=\"inout-car\">" + seal.carNumber + "</td>" +
                        "<td class=\"inout-time\">" + seal.createDate + "</td>";
                    if (coordFlag) {
                        tableData += "<td class=\"inout-gps\">数据库记录异常</td>";
                        tableData += "<td class=\"inout-coordinate\">数据库记录异常</td>";
                    } else {
                        tableData += "<td class=\"inout-gps\">" + (seal.coorValid ? "有效" : "无效") + "</td>";
                        tableData += "<td class=\"inout-coordinate\"><a href=\"javascript:showBMap(" + seal.id + ")\">("
                            + seal.longitude + ", " + seal.latitude + ")</a></td>";
                    }
                    tableData += "<td class=\"inout-velocity\">" + (seal.velocity == undefined ? "数据库记录异常" : seal.velocity) + "</td>" +
                        "<td class=\"inout-aspect\">" + angle2aspect(seal.angle) + "</td>" +
                        "<td class=\"inout-prestatus\">" + parseCarStatus(seal.prestatus) + "</td>" +
                        "<td class=\"inout-status\">" + parseCarStatus(seal.status) + "</td>" +
                        "<td class=\"inout-type\">" + parseSealType(seal.type) + "</td>" +
                        "<td class=\"inout-station\">" + seal.station + "</td>" +
                        "<td class=\"inout-authtype\">" + parseAuthType(seal.authtype) + "</td>" +
                        "<td class=\"inout-authid\">" + seal.authid + "</td>" +
                        "<td class=\"inout-alarm\">" + (seal.alarm == undefined ? "数据库记录异常" : seal.alarm) + "</td>" +
                        "</tr>";
                }
                tableData += "</table>";
                tableData = tableData.replace(/>null</g, "><");
                $(".table-body").html(tableData);
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

    showList("", "", "", "", 1);

    $("#search_btn").click(function() {
        var carNumber = $("#text_car").val();
        var type = $("#text_type").val();
        var begin = $("#text_begin").val();
        var end = $("#text_end").val();
        showList(carNumber, type, begin, end, 1);
    });

    function refreshPage(pageId) {

        var carNumber = $("#qcar").val();
        if (isNull(carNumber)) {
            carNumber = "";
        }
        var type = $("#qtype").val();
        if (isNull(type)) {
            type = "";
        }
        var begin = $("#qbegin").val();
        if (isNull(begin)) {
            begin = "";
        }
        var end = $("#qend").val();
        if (isNull(end)) {
            end = "";
        }
        showList(carNumber, type, begin, end, pageId);
    }

    $("#page_id").change(function() {
        var pageId = $("#page_id").val();
        if (isNull(pageId)) {
            pageId = 1;
        }
        refreshPage(pageId);
    });

    $("#page_size").change(function() {
        refreshPage(1);
    });
});