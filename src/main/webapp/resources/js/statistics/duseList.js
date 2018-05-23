function showBMap(id) {
    layer.open({
        type: 2,
        title: ['设备使用记录查询', 'font-size:14px;color:#ffffff;background:#478de4;'],
        shadeClose: true,
        shade: 0.6,
        area: ['800px', '560px'],
        content: '../../manage/statistics/dispatch.do?' + encodeURI('mode=use&id=' + id)
    });
    // 阻止事件冒泡到DOM树上
    event.stopPropagation();
}
$(function() {
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
            "../../manage/statistics/findDeviceRecordsForPage.do",
            encodeURI("carNumber=" + carNumber + "&type=" + type + "&begin=" + begin + "&end=" + end + "&pageId=" + pageId + "&startRow=" + startRow + "&rows=" + rows),
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
                $("#qtype").val(gridPage.t.type);
                $("#qbegin").val(gridPage.t.begin);
                $("#qend").val(gridPage.t.end);
                $(".table-body").html("");
                var duses = gridPage.dataList;
                var tableData = "<table width='100%'>";
                for (var i = 0; i < gridPage.currentRows; i++) {
                    var duse = duses[i];
                    var coordFlag = duse.longitude == undefined || duse.latitude == undefined;
                    tableData += (coordFlag == true ? "<tr>" : "<tr ondblclick=\"showBMap(" + duse.id + ")\">") +
                        "<td class=\"duse-id\">" + duse.id + "</td>" +
                        "<td class=\"duse-car\">" + duse.carNumber + "</td>" +
                        "<td class=\"duse-time\">" + duse.createDate + "</td>";
                    if (coordFlag) {
                        tableData += "<td class=\"duse-coordinate\">数据库记录异常</td>";
                    } else {
                        tableData += "<td class=\"duse-coordinate\"><a href=\"javascript:showBMap(" + duse.id + ")\">("
                            + duse.longitude + ", " + duse.latitude + ")</a></td>";
                    }
                    tableData += "<td class=\"duse-velocity\">" + (duse.velocity == undefined ? "数据库记录异常" : duse.velocity) + "</td>" +
                        "<td class=\"duse-aspect\">" + angle2aspect(duse.angle) + "</td>" +
                        "<td class=\"duse-dev\">" + duse.devId + "</td>" +
                        "<td class=\"duse-type\">" + duse.typeName + "</td>" +
                        "<td class=\"duse-alarm\">" + duse.alarm + "</td>" +
                        "<td class=\"duse-lock\">" + duse.lockStatus + "</td>" +
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
                }, function() {
                    location.reload(true);
                });
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