var showList;
$(function() {
	showList = function (deviceType, pageId) {
        var rows = $("#page_size").val();
        var startRow = (pageId - 1) * rows;

        $.post(
            "../../manage/device/ajaxFindForPage.do",
            encodeURI("type=" + deviceType + "&pageId=" + pageId + "&startRow=" + startRow + "&rows=" + rows),
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
                $("#dtype").val(gridPage.t.type);
                $(".table-body").html("");
                var devices = gridPage.dataList;
                var tableData = "<table width='100%'>";
                for (var i = 0; i < gridPage.currentRows; i++) {
                    var device = devices[i];
                    tableData += "<tr>" +
                        "<td class=\"device-id\">" + device.deviceId + "</td>" +
                        "<td class=\"device-type\">" + device.typeName + "</td>" +
                        "<td class=\"device-model\">" + device.model + "</td>" +
                        "<td class=\"device-produce\">" + device.produce + "</td>" +
                        "<td class=\"device-delivery\">" + device.delivery + "</td>" +
                        "<td class=\"device-remark\">" + device.remark + "</td>" +
                        "</tr>";
                }
                tableData += "</table>";
                tableData = tableData.replace(/>null</g, "><");
                $(".table-body").html(tableData);
            },
            "json"
        );
    };

    showList("", 1);

    $("#search_btn").click(function() {
        deviceType = $("#search_text").val();
        if (isNull(deviceType)) {
            deviceType = -2;
        }
        showList(deviceType, 1);
    });

    function refreshPage() {
        var pageId = $("#page_id").val();
        if (isNull(pageId)) {
            pageId = 1;
        }
        var deviceType = $("#dtype").val();
        if (isNull(deviceType)) {
            deviceType = -2;
        }
        showList(deviceType, pageId);
    }

    $("#page_id").change(function() {
        refreshPage();
    });

    $("#page_size").change(function() {
        var deviceType = $("#dtype").val();
        if (isNull(deviceType)) {
            deviceType = -2;
        }
        showList(deviceType, 1);
    });
});
