<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="pop" uri="/pop-tags" %>

<pop:Permission ename="viewDevice">
    <!DOCTYPE html>
    <html>

    <head>
        <meta charset="utf-8">
        <meta name="renderer" content="webkit">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        <meta name="viewport" content="width=center-width, initial-scale=1, maximum-scale=1">
        <title>设备列表</title>
        <script src="../../resources/js/base.js"></script>
        <link rel="stylesheet" href="../../resources/css/base.css">
        <link rel="stylesheet" href="../../resources/css/normal.css">
        <link rel="stylesheet" href="../../resources/plugins/jqTable/css/jqTable.css">
    </head>

    <body>
    <div class="container">
        <div class="nav-addr">
            <img src="../../resources/images/navbar/basicinfo.png"> 基本信息管理
            <img src="../../resources/images/navbar/nav_right_12.png"> 设备信息同步
        </div>
        <div class="search-zone">
            <label for="search_text" class="search-type" id="search_type">设备类型</label>
            <select class="search-text" id="search_text">
                <option value=1>车载终端</option>
                <option value=2>锁</option>
                <option value=3>出入库读卡器</option>
                <option value=4>手持机</option>
            </select>
            <input type="button" class="search-btn button" id="search_btn" value="查询">
            <pop:Permission ename="syncDevice">
                <input type="button" class="button" id="add" value="同步">
                <script type="text/javascript">
                    $("#add").click(function () {
                        layer.confirm('是否确认同步设备信息？', {
                            icon: 0,
                            title: ['同步设备', 'font-size:14px;color:#ffffff;background:#478de4;']
                        }, function (index2) {
                            layer.close(index2);
                            var index = layer.load();
                            $.post("../../manage/device/sync.do",
                                function (data) {
                                    layer.close(index);
                                    if (data.msg == "success") {
                                        layer.msg('同步成功！', {icon: 1, time: 1000});
                                        showList("", 1);
                                    } else {
                                        layer.alert('同步失败！' + data.e, {icon: 5});
                                    }
                                },
                                "json"
                            ).error(function (XMLHttpRequest, textStatus, errorThrown) {
                                layer.close(index);
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
                        });
                    });
                </script>
            </pop:Permission>
        </div>
        <div class="data-zone">
            <div class="table-wrap">
                <div class="table-box c-table c-table--border">
                    <table cellspacing="0" cellpadding="0" border="0" role="c-table" data-height="200">
                        <colgroup>
                            <col name="" width="">
                            <col name="" width="">
                            <col name="" width="">
                            <col name="" width="">
                            <col name="" width="">
                            <col name="" width="">
                            <col name="" width="">
                        </colgroup>
                    </table>
                </div>
            </div>
            <div class="page">
                <span>第</span>
                <select id="page_id">
                    <option value="1">1</option>
                </select>
                <span id="page_info">页(几条数据)/共几页(共几条数据)</span>
                <select id="page_size">
                    <option value="25">25条/页</option>
                    <option value="50">50条/页</option>
                    <option value="100">100条/页</option>
                    <option value="150">150条/页</option>
                </select>
                <input type="hidden" id="dtype">
            </div>
        </div>
    </div>
    </body>

    </html>
    <!-- 数据模版 -->
    <script id="table-tpl" type="text/html">
        <table cellspacing="0" cellpadding="0" border="0" class="" role="c-table" data-height="200">
            <colgroup>
                <col name="" width="">
                <col name="" width="">
                <col name="" width="">
                <col name="" width="">
                <col name="" width="">
                <col name="" width="">
                <col name="" width="">
            </colgroup>
            <thead>
            <tr>
                <th class="">
                    <div class="cell">设备ID</div>
                </th>
                <th class="">
                    <div class="cell">设备类型</div>
                </th>
                <th class="">
                    <div class="cell">设备版本</div>
                </th>
                <th class="">
                    <div class="cell">设备型号</div>
                </th>
                <th class="">
                    <div class="cell">出厂时间</div>
                </th>
                <th class="">
                    <div class="cell">交付时间</div>
                </th>
                <th class="">
                    <div class="cell">备注</div>
                </th>
            </tr>
            </thead>
            <tbody>
            {{each data}}
            <tr>
                <td class="">
                    <div class="cell">{{$value.deviceId}}</div>
                </td>
                <td class="">
                    <div class="cell">{{$value.type}}</div>
                </td>
                <td class="">
                    <div class="cell">{{$value.version}}</div>
                </td>
                <td class="">
                    <div class="cell">{{$value.model}}</div>
                </td>
                <td class="">
                    <div class="cell">{{$value.produce}}</div>
                </td>
                <td class="">
                    <div class="cell">{{$value.delivery}}</div>
                </td>
                <td class="">
                    <div class="cell">{{$value.remark}}</div>
                </td>
            </tr>
            {{/each}}
            </tbody>
        </table>
    </script>
    <script src="../../resources/plugins/jquery-1.8.3.min.js"></script>
    <script src="../../resources/plugins/layer/layer.js"></script>
    <!--开源模版引擎： art-template@4.12.2 for browser | https://github.com/aui/art-template -->
    <script src="../../resources/plugins/jqTable/js/plugins/artTemplate.js"></script>
    <!-- 必要插件：固定列滚动需要用到，鼠标滚动兼容多浏览器 -->
    <script src="../../resources/plugins/jqTable/js/jquery.mousewheel.min.js"></script>
    <!-- 表格插件 -->
    <script src="../../resources/plugins/jqTable/js/zipJs/jqTable.all.min.js"></script>
    <script type="text/javascript">
        function showList(deviceType, pageId) {
            var rows = $("#page_size").val();
            var startRow = (pageId - 1) * rows;
            var loadLayer = layer.load();
            $.post(
                "../../manage/device/ajaxFindForPage.do",
                encodeURI("type=" + deviceType + "&pageId=" + pageId + "&startRow=" + startRow + "&rows=" + rows),
                function (gridPage) {
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
                    var dataCount = gridPage.currentRows;
                    $("#page_info").html("页(" + dataCount + "条数据)/共" + pageCount + "页(共" + gridPage.records + "条数据)");
                    $("#dtype").val(gridPage.t.type);
                    var devices = gridPage.dataList;
                    var jsonData = [];
                    for (var i = 0; i < dataCount; i++) {
                        var device = devices[i];
                        jsonData.push({
                            deviceId: device.deviceId,
                            type: device.typeName,
                            version: stringifyVer(device.ver),
                            model: device.model,
                            produce: device.produce,
                            delivery: device.delivery,
                            remark: device.remark
                        });
                    }
                    // 更新表格数据
                    var tableHtml = template('table-tpl', {data: jsonData});
                    $('.c-table').eq(0).data('table').updateHtml(tableHtml);
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

        $(function () {
            $(window).resize(function () {
                var height = $(window).height() - 133;
                $('.table-box').data('height', height);
                // $(window).trigger('resize');
            }).resize();

            $('[role="c-table"]').jqTable();
            showList("", 1);

            $("#search_btn").click(function () {
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

            $("#page_id").change(function () {
                refreshPage();
            });

            $("#page_size").change(function () {
                var deviceType = $("#dtype").val();
                if (isNull(deviceType)) {
                    deviceType = -2;
                }
                showList(deviceType, 1);
            });
        });
    </script>
</pop:Permission>