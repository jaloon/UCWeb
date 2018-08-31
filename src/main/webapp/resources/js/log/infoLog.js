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

    function showList(account, name, type, begin, end, pageId) {
        var rows = $("#page_size").val();
        var startRow = (pageId - 1) * rows;
        var loadLayer = layer.load();
        $.post(
            "../../manage/log/ajaxFindInfoLogsForPage.do",
            encodeURI("user.account=" + account + "&user.name=" + name + "&type=" + type + "&begin=" + begin + "&end=" + end + "&pageId=" + pageId + "&startRow=" + startRow + "&rows=" + rows),
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
                $("#olaccount").val(gridPage.t.user.account);
                $("#olname").val(gridPage.t.user.name);
                $("#oltype").val(gridPage.t.type);
                $("#olbegin").val(gridPage.t.begin);
                $("#olend").val(gridPage.t.end);
                $(".table-body").html("");
                var logs = gridPage.dataList;
                var tableData = "<table width='100%'>";
                for (var i = 0; i < gridPage.currentRows; i++) {
                    var log = logs[i];
                    tableData += "<tr>" +
                        "<td class=\"log-id\">" + log.id + "</td>" +
                        "<td class=\"log-account\">" + log.user.account + "</td>" +
                        "<td class=\"log-name\">" + log.user.name + "</td>" +
                        "<td class=\"log-type\">" + toHexId(log.type) + "</td>" +
                        "<td class=\"log-description\">" + log.description + "</td>" +
                        "<td class=\"log-time\">" + log.createDate + "</td>" +
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

    showList("", "", "", "", "", 1);

    $("#search_btn").click(function() {
        var account = "";
        var name = "";
        var type_user = $("#type_user").val();
        if (type_user == 1) {
            account = trimAll($("#text_user").val());
        } else {
            name = trimAll($("#text_user").val());

        }
        var type = $("#text_type").val();
        if (!isNull(type)){
            type = parseInt(type, 16);
        }
        var begin = $("#text_begin").val();
        var end = $("#text_end").val();
        showList(account, name, type, begin, end, 1);
    });

    function refreshPage(pageId) {

        var account = $("#olaccount").val();
        if (isNull(account)) {
            account = "";
        }
        var name = $("#olname").val();
        if (isNull(name)) {
            name = "";
        }
        var type = $("#oltype").val();
        if (isNull(type)) {
            type = "";
        }
        var begin = $("#olbegin").val();
        if (isNull(begin)) {
            begin = "";
        }
        var end = $("#olend").val();
        if (isNull(end)) {
            end = "";
        }
        showList(account, name, type, begin, end, pageId);
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