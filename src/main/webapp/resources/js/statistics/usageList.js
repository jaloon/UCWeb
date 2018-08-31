function showBMap(id) {
    layer.open({
        type: 2,
        title: ['卡及设备使用记录查询', 'font-size:14px;color:#ffffff;background:#478de4;'],
        // shadeClose: true,
        shade: 0.6,
        area: ['800px', '560px'],
        content: '../../manage/statistics/dispatch.do?' + encodeURI('mode=usage&id=' + id)
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
            "../../manage/statistics/findUsageRecordsForPage.do",
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
                var usages = gridPage.dataList;
                var tableData = "<table width='100%'>";
                for (var i = 0; i < gridPage.currentRows; i++) {
                    var usage = usages[i];
                    var coordFlag = usage.longitude == undefined || usage.latitude == undefined;
                    tableData += (coordFlag == true ? "<tr>" : "<tr ondblclick=\"showBMap(" + usage.id + ")\">") +
                        "<td class=\"usage-id\">" + usage.id + "</td>" +
                        "<td class=\"usage-car\">" + usage.carNumber + "</td>" +
                        "<td class=\"usage-time\">" + usage.createDate + "</td>";
                    if (coordFlag) {
                        tableData += "<td class=\"usage-gps\">数据库记录异常</td>";
                        tableData += "<td class=\"usage-coordinate\">数据库记录异常</td>";
                    } else {
                        tableData += "<td class=\"usage-gps\">" + (usage.coorValid ? "有效" : "无效") + "</td>";
                        tableData += "<td class=\"usage-coordinate\"><a href=\"javascript:showBMap(" + usage.id + ")\">("
                            + usage.longitude + ", " + usage.latitude + ")</a></td>";
                    }
                    tableData += "<td class=\"usage-velocity\">" + (usage.velocity == undefined ? "数据库记录异常" : usage.velocity) + "</td>" +
                        "<td class=\"usage-aspect\">" + angle2aspect(usage.angle) + "</td>" +
                        "<td class=\"usage-type\">" + usage.typeName + "</td>" +
                        "<td class=\"usage-dev\">" + usage.devId + "</td>" +
                        "<td class=\"usage-alarm\">" + (usage.alarm == undefined ? "数据库记录异常" : usage.alarm) + "</td>" +
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

    function getHexId(id, type) {
        var hexId = id.toString(16).toUpperCase();
        if (type == 1 || type == 3) { // 设备ID 4个字节
            if (hexId.length < 8) {
                for (var i = 0, len = 8 - hexId.length; i < len; i++) {
                    hexId = "0" + hexId;
                }
            }
        } else { // 卡ID 8个字节
            if (hexId.length < 16) {
                for (var i = 0, len = 16 - hexId.length; i < len; i++) {
                    hexId = "0" + hexId;
                }
            }
        }
        return hexId;
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