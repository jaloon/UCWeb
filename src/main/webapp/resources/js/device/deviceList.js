var showList;

$(function() {
	showList = function (deviceType, pageId) {
        var rows = $("#page_size").val();
        var startRow = (pageId - 1) * rows;
        var loadLayer = layer.load();
        $.post(
            "../../manage/device/ajaxFindForPage.do",
            encodeURI("type=" + deviceType + "&pageId=" + pageId + "&startRow=" + startRow + "&rows=" + rows),
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
                $("#dtype").val(gridPage.t.type);
                $(".table-body").html("");
                var devices = gridPage.dataList;
                var tableData = "<table width='100%'>";
                for (var i = 0; i < gridPage.currentRows; i++) {
                    var device = devices[i];
                    tableData += "<tr>" +
                        "<td class=\"device-id\">" + device.deviceId + "</td>" +
                        "<td class=\"device-type\">" + device.typeName + "</td>" +
                        "<td class=\"device-ver\">" + stringifyVer(device.ver) + "</td>" +
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
