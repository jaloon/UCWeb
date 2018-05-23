var dispatch;
var refreshPage;
$(function() {
    dispatch = function(mode, id) {
        var title = "添加手持机";
        var h = "357px";
        if ("edit" == mode) {
            title = "修改手持机信息";
            // h = "395px";
        }
        if ("view" == mode) {
            title = "查看手持机信息";
            h = "306px";
        }
        layer.open({
            type: 2,
            title: ['手持机管理（' + title + '）', 'font-size:14px;color:#ffffff;background:#478de4;'],
            shadeClose: true,
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
            var gasStations = eval(data);
            var len = gasStations.length;
            for (var i = 0; i < len; i++) {
                var gasStation = gasStations[i];
                $("#search_text").append("<option value=" + gasStation.id + ">" + gasStation.name + "</option>");
            }
        }
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