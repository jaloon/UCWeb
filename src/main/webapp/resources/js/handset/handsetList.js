var dispatch;
var refreshPage;
$(function() {
    dispatch = function(mode, id) {
        var title = "添加手持机";
        var h = "343px";
        if ("edit" == mode) {
            var h = "380px";
            title = "修改手持机信息";
        }
        if ("view" == mode) {
            title = "查看手持机信息";
            h = "320px";
        }
        layer.open({
            type: 2,
            title: ['手持机管理（' + title + '）', 'font-size:14px;color:#ffffff;background:#478de4;'],
            // shadeClose: true,
            shade: 0.8,
            resize: false,
            area: ['540px', h],
            content: '../../manage/handset/dispatch.do?' + encodeURI('mode=' + mode + '&id=' + id), //iframe的url
            end: function() {
                if (mode != "view") {
                    refreshPage();
                }
            }
        });
        // 阻止事件冒泡到DOM树上
        event.stopPropagation();
    };

    showList("", 1);

    $.getJSON("../../manage/handset/getGasStationList.do",
        function(data) {
            if (data == null || data == undefined || data.length == 0) {
                $("#search_text").replaceWith("<input type='text' class='search-text' id='search_text' style='color: #d80e0e;' value='无加油站信息' readonly>");
                return;
            }
            var len = data.length;
            var stationHtml = "";
            for (var i = 0; i < len; i++) {
                var gasStation = data[i];
                stationHtml += "<option value=" + gasStation.id + ">" + gasStation.name + "</option>";
            }
            $("#search_text").append(stationHtml);
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

    $("#search_btn").click(function() {
        var gasStationId = $("#search_text").val();
        if (isNull(gasStationId)) {
            gasStationId = "";
        }
        showList(gasStationId, 1);
    });

    refreshPage = function () {
        var pageId = $("#page_id").val();
        if (isNull(pageId)) {
            pageId = 1;
        }
        var gasStationId = $("#hgsid").val();
        if (isNull(gasStationId)) {
            gasStationId = -2;
        }
        showList(gasStationId, pageId);
    };

    $("#page_id").change(function() {
        refreshPage();
    });

    $("#page_size").change(function() {
        var gasStationId = $("#hgsid").val();
        if (isNull(gasStationId)) {
            gasStationId = -2;
        }
        showList(gasStationId, 1);
    });
});