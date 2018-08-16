function showBMap(id) {
    layer.open({
        type: 2,
        title: ['锁动作记录查询', 'font-size:14px;color:#ffffff;background:#478de4;'],
        // shadeClose: true,
        shade: 0.6,
        area: ['800px', '560px'],
        content: '../../manage/statistics/dispatch.do?' + encodeURI('mode=lock&id=' + id)
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

        $.post(
            "../../manage/statistics/findLockRecordsForPage.do",
            encodeURI("carNumber=" + carNumber + "&status=" + type + "&begin=" + begin + "&end=" + end + "&pageId=" + pageId + "&startRow=" + startRow + "&rows=" + rows),
            function(data) {
                var gridPage = eval(data);

                var maxIndex = $("#page_id option:last").index(); //获取Select最大的索引值
                var len = maxIndex + 1 - gridPage.total;
                if (len > 0) {
                    for (var i = gridPage.total > 0 ? gridPage.total : 1; i < maxIndex + 1; i++) {
                        $("#page_id option:last").remove(); //删除Select中索引值最大Option(最后一个)
                    }
                } else if (len < 0) {
                    for (var i = maxIndex + 2; i <= gridPage.total; i++) {
                        $("#page_id").append("<option value=" + i + ">" + i + "</option>"); //为Select追加一个Option下拉项
                    }
                }
                $("#page_id").val(gridPage.page);

                $("#page_info").html("页(" + gridPage.currentRows + "条数据)/共" + gridPage.total + "页(共" + gridPage.records + "条数据)");
                $("#qcar").val(gridPage.t.carNumber);
                $("#qtype").val(gridPage.t.status);
                $("#qbegin").val(gridPage.t.begin);
                $("#qend").val(gridPage.t.end);
                $(".table-body").html("");
                var locks = gridPage.dataList;
                var tableData = "<table width='100%'>";
                for (var i = 0; i < gridPage.currentRows; i++) {
                    var lock = locks[i];
                    var coordFlag = lock.longitude == undefined || lock.latitude == undefined;
                    tableData += (coordFlag == true ? "<tr>" : "<tr ondblclick=\"showBMap(" + lock.id + ")\">") +
                        "<td class=\"lock-id\">" + lock.id + "</td>" +
                        "<td class=\"lock-car\">" + lock.carNumber + "</td>" +
                        "<td class=\"lock-time\">" + lock.createDate + "</td>";
                    if (coordFlag) {
                        tableData += "<td class=\"lock-coordinate\">数据库记录异常</td>";
                    } else {
                        tableData += "<td class=\"lock-coordinate\"><a href=\"javascript:showBMap(" + lock.id + ")\">("
                            + lock.longitude + ", " + lock.latitude + ")</a></td>";
                    }
                    tableData += "<td class=\"lock-velocity\">" + (lock.velocity == undefined ? "数据库记录异常" : lock.velocity) + "</td>" +
                        "<td class=\"lock-aspect\">" + angle2aspect(lock.angle) + "</td>" +
                        "<td class=\"lock-dev\">" + lock.lockId + "</td>" +
                        "<td class=\"lock-store\">" + lock.storeId + "</td>" +
                        "<td class=\"lock-seat\">" + lock.seatName + "</td>" +
                        "<td class=\"lock-index\">" + lock.seatIndex + "</td>" +
                        "<td class=\"lock-status\">" + lock.statusName + "</td>" +
                        "<td class=\"lock-alarm\">" + (lock.alarm == undefined ? "数据库记录异常" : lock.alarm) + "</td>" +
                        "</tr>";
                }
                tableData += "</table>";
                tableData = tableData.replace(/>null</g, "><");
                $(".table-body").html(tableData);
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