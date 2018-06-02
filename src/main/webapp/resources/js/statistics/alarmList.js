function showBMap(id) {
    layer.open({
        type: 2,
        title: ['报警记录查询', 'font-size:14px;color:#ffffff;background:#478de4;'],
        shadeClose: true,
        shade: 0.6,
        area: ['800px', '560px'],
        content: '../../manage/statistics/dispatch.do?' + encodeURI('mode=alarm&id=' + id)
    });
}

$(function () {
    $.getJSON("../../../manage/car/selectCars.do", "scope=0",
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
                width: '160px',
                height: '34px',
                'font-size': '16px',
                "margin-bottom": "0px"
            });
            selectObj.siblings(".combo-input").height(10);
        }
    ).error(function (XMLHttpRequest, textStatus, errorThrown) {
        if (XMLHttpRequest.readyState == 4 && XMLHttpRequest.status == 200 && textStatus == "parsererror") {
            layer.confirm('登录失效，是否刷新页面重新登录？', {
                icon: 0,
                title: ['登录失效', 'font-size:14px;color:#ffffff;background:#478de4;']
            }, function () {
                location.reload(true);
            });
        }
    });

    $("#text_dev").change(function () {
        $("#text_type").empty();
        $("#text_type").append("<option value=''>所有报警</option>");
        var dev = $("#text_dev").val();
        if (dev == 1) {
            $("#text_type").append("<option value=1>未施封越界</option>");
            $("#text_type").append("<option value=2>时钟电池报警</option>");
        } else if (dev == 2) {
            $("#text_type").append("<option value=1>通讯异常报警</option>");
            $("#text_type").append("<option value=2>电池低电压报警</option>");
            $("#text_type").append("<option value=3>异常开锁报警</option>");
            $("#text_type").append("<option value=4>进入应急</option>");
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

    function showList(carNumber, dev, type, begin, end, pageId) {
        var rows = $("#page_size").val();
        var startRow = (pageId - 1) * rows;

        $.post(
            "../../manage/statistics/findAlarmRecordsForPage.do",
            encodeURI("carNumber=" + carNumber + "&deviceType=" + dev + "&type=" + type + "&begin=" + begin + "&end=" + end + "&pageId=" + pageId + "&startRow=" + startRow + "&rows=" + rows),
            function (data) {
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
                $("#qdev").val(gridPage.t.deviceType);
                $("#qtype").val(gridPage.t.type);
                $("#qbegin").val(gridPage.t.begin);
                $("#qend").val(gridPage.t.end);
                $(".table-body").html("");
                var alarms = gridPage.dataList;
                var tableData = "<table width='100%'>";
                for (var i = 0; i < gridPage.currentRows; i++) {
                    var alarm = alarms[i];
                    var coordFlag = alarm.longitude == undefined || alarm.latitude == undefined;
                    tableData += (coordFlag == true ? "<tr>" : "<tr ondblclick=\"showBMap(" + alarm.id + ")\">") +
                        "<td class=\"alarm-id\">" + alarm.id + "</td>" +
                        "<td class=\"alarm-car\">" + alarm.carNumber + "</td>" +
                        "<td class=\"alarm-time\">" + alarm.createDate + "</td>" +
                        "<td class=\"alarm-station\">" + alarm.station + "</td>";
                    if (coordFlag) {
                        tableData += "<td class=\"alarm-coordinate\">数据库记录异常</td>";
                    } else {
                        tableData += "<td class=\"alarm-coordinate\"><a href=\"javascript:showBMap(" + alarm.id + ")\">("
                            + alarm.longitude + ", " + alarm.latitude + ")</a></td>";
                    }
                    tableData += "<td class=\"alarm-velocity\">" + (alarm.velocity == undefined ? "数据库记录异常" : alarm.velocity) + "</td>" +
                        "<td class=\"alarm-aspect\">" + angle2aspect(alarm.angle) + "</td>" +
                        "<td class=\"alarm-dev\">" + (alarm.deviceType == 1 ? "车载终端（" : "锁（") + alarm.deviceId + "）</td>" +
                        "<td class=\"alarm-type\">" + alarm.typeName + "</td>" +
                        "<td class=\"alarm-status\">" + (alarm.status == undefined ? "数据库记录异常" : alarm.status) + "</td>" +
                        "<td class=\"alarm-lock\">" + (alarm.lockStatus == undefined ? "数据库记录异常" : alarm.lockStatus) + "</td>" +
                        "</tr>";
                }
                tableData += "</table>";
                tableData = tableData.replace(/>null</g, "><");
                $(".table-body").html(tableData);
            },
            "json"
        ).error(function (XMLHttpRequest, textStatus, errorThrown) {
            if (XMLHttpRequest.readyState == 4 && XMLHttpRequest.status == 200 && textStatus == "parsererror") {
                layer.confirm('登录失效，是否刷新页面重新登录？', {
                    icon: 0,
                    title: ['登录失效', 'font-size:14px;color:#ffffff;background:#478de4;']
                }, function () {
                    location.reload(true);
                });
            }
        });
    }

    showList("", "", "", "", "", 1);

    $("#search_btn").click(function () {
        var carNumber = $("#text_car").val();
        var dev = $("#text_dev").val();
        var type = $("#text_type").val();
        var begin = $("#text_begin").val();
        var end = $("#text_end").val();
        showList(carNumber, dev, type, begin, end, 1);
    });

    function refreshPage(pageId) {

        var carNumber = $("#qcar").val();
        if (isNull(carNumber)) {
            carNumber = "";
        }
        var dev = $("#qdev").val();
        if (isNull(dev) || dev == 0) {
            dev = "";
        }
        var type = $("#qtype").val();
        if (isNull(type) || type == 0) {
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
        showList(carNumber, dev, type, begin, end, pageId);
    }

    $("#page_id").change(function () {
        var pageId = $("#page_id").val();
        if (isNull(pageId)) {
            pageId = 1;
        }
        refreshPage(pageId);
    });

    $("#page_size").change(function () {
        refreshPage(1);
    });
});