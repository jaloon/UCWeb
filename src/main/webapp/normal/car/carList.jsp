<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="pop" uri="/pop-tags" %>
<pop:Permission ename="viewCar">
    <!DOCTYPE html>
    <html>

    <head>
        <meta charset="utf-8">
        <meta name="renderer" content="webkit">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        <meta name="viewport" content="width=center-width, initial-scale=1, maximum-scale=1">
        <title>车辆列表</title>
        <script src="../../resources/js/base.js"></script>
        <link rel="stylesheet" href="../../resources/css/base.css">
        <link rel="stylesheet" href="../../resources/css/normal.css">
        <link rel="stylesheet" href="../../resources/plugins/jqTable/css/jqTable.css">
    </head>

    <body>
    <div class="container">
        <div class="nav-addr">
            <img src="../../resources/images/navbar/car.png"> 车辆管理
            <img src="../../resources/images/navbar/nav_right_12.png"> 车辆信息管理
        </div>
        <div class="search-zone">
            <select class="search-type" id="search_type">
                <option value="0">查询类型</option>
                <option value="1">车牌号码</option>
                <option value="2">所属公司</option>
            </select>
            <input type="text" class="search-text" id="search_text">
            <input type="button" class="search-btn button" id="search_btn" value="查询">
            <pop:Permission ename="editCar">
                <input type="button" class="button" id="add" value="添加" onclick="dispatch('add',0)">
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
                <input type="hidden" id="cnum">
                <input type="hidden" id="ccom">
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
                <col name="" width="">
                <col name="" width="">
                <col name="" width="">
            </colgroup>
            <thead>
            <tr>
                <th class="">
                    <div class="cell">车牌号码</div>
                </th>
                <th class="">
                    <div class="cell">所属公司</div>
                </th>
                <th class="">
                    <div class="cell">车辆类型</div>
                </th>
                <th class="">
                    <div class="cell">终端编号</div>
                </th>
                <th class="">
                    <div class="cell">终端型号</div>
                </th>
                <th class="">
                    <div class="cell">SIM卡号</div>
                </th>
                <th class="">
                    <div class="cell">配送卡ID</div>
                </th>
                <th class="">
                    <div class="cell">仓数</div>
                </th>
                <th class="">
                    <div class="cell">备注</div>
                </th>
                <th class="">
                    <div class="cell">操作</div>
                </th>
            </tr>
            </thead>
            <tbody>
            {{each data}}
            <tr onclick="dispatch('edit',{{$value.id}})">
                <td class="">
                    <div class="cell">{{$value.carNo}}</div>
                </td>
                <td class="">
                    <div class="cell">{{$value.carCom}}</div>
                </td>
                <td class="">
                    <div class="cell">{{$value.type}}</div>
                </td>
                <td class="">
                    <div class="cell">{{$value.deviceId}}</div>
                </td>
                <td class="">
                    <div class="cell">{{$value.model}}</div>
                </td>
                <td class="">
                    <div class="cell">{{$value.sim}}</div>
                </td>
                <td class="">
                    <div class="cell">{{$value.transcard}}</div>
                </td>
                <td class="">
                    <div class="cell">{{$value.storeNum}}</div>
                </td>
                <td class="">
                    <div class="cell">{{$value.remark}}</div>
                </td>
                <td class="">
                    <div class="cell">
                        <img class="edit-btn" src="../../resources/images/operate/view.png" alt="查看" title="查看"
                             onclick="dispatch('view',{{$value.id}})">
                        <pop:Permission ename="editCar">
                            <img class="edit-btn" src="../../resources/images/operate/edit.png" alt="编辑" title="编辑"
                                 onclick="dispatch('edit',{{$value.id}})">
                            <img class="edit-btn" src="../../resources/images/operate/delete.png" alt="删除" title="删除"
                                 onclick="deleteCar({{$value.id}}, '{{$value.carNumber}}')">
                        </pop:Permission>
                    </div>
                </td>
            </tr>
            {{/each}}
            </tbody>
        </table>
    </script>
    <script src="../../resources/plugins/jquery-1.8.3.min.js"></script>
    <%--<script src="http://www.jq22.com/jquery/jquery-1.10.2.js"></script>--%>
    <script src="../../resources/plugins/layer/layer.js"></script>
    <!--开源模版引擎： art-template@4.12.2 for browser | https://github.com/aui/art-template -->
    <script src="../../resources/plugins/jqTable/js/plugins/artTemplate.js"></script>
    <!-- 必要插件：固定列滚动需要用到，鼠标滚动兼容多浏览器 -->
    <script src="../../resources/plugins/jqTable/js/jquery.mousewheel.min.js"></script>
    <!-- 表格插件 -->
    <script src="../../resources/plugins/jqTable/js/zipJs/jqTable.all.min.js"></script>
    <script src="../../resources/plugins/verify.js"></script>
    <script type="text/javascript">

        <pop:Permission ename="editCar">

        function deleteCar(id, carNumber) {
            layer.confirm('删除后不可撤销，是否确认删除？', {
                icon: 0,
                title: ['删除', 'font-size:14px;color:#ffffff;background:#478de4;']
            }, function () {
                $.post("../../manage/car/delete.do", encodeURI("id=" + id + "&carNumber=" + carNumber),
                    function (data) {
                        if ("success" == data.msg) {
                            layer.msg('删除成功！', {icon: 1, time: 500}, function () {
                                refreshPage();
                            });
                        } else {
                            layer.msg('删除失败！', {icon: 2, time: 500});
                        }
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
            });
            // 阻止事件冒泡到DOM树上
            stopPropagation(event);
        }

        </pop:Permission>

        function dispatch(mode, id) {
            var title = "添加车辆";
            var h = "420px";
            if ("edit" == mode) {
                title = "修改车辆信息";
                h = "534px";
            }
            if ("view" == mode) {
                title = "查看车辆信息";
                h = "483px";
            }
            layer.open({
                type: 2,
                title: ['车辆信息管理（' + title + '）', 'font-size:14px;color:#ffffff;background:#478de4;'],
                // shadeClose: true,
                shade: 0.8,
                resize: false,
                area: ['540px', h],
                content: '../../manage/car/dispatch.do?' + encodeURI('mode=' + mode + '&id=' + id), //iframe的url
                end: function () {
                    if (mode != "view") {
                        refreshPage();
                    }
                }
            });
            // 阻止事件冒泡到DOM树上
            stopPropagation(event);
        }

        function refreshPage() {
            var pageId = $("#page_id").val();
            if (isNull(pageId)) {
                pageId = 1;
            }
            var carNo = $("#cnum").val();
            if (isNull(carNo)) {
                carNo = "";
            }
            var company = $("#ccom").val();
            if (isNull(company)) {
                company = "";
            }
            showList(carNo, company, pageId);
        }

        function showList(carNo, company, pageId) {
            var rows = $("#page_size").val();
            var startRow = (pageId - 1) * rows;
            var loadLayer = layer.load();
            $.post(
                "../../manage/car/ajaxFindForPage.do",
                encodeURI("carNumber=" + carNo + "&transCompany.id=" + company + "&pageId=" + pageId
                    + "&startRow=" + startRow + "&rows=" + rows),
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
                    $("#cnum").val(gridPage.t.carNumber);
                    $("#ccom").val(gridPage.t.transCompany.id);
                    if (dataCount == 0) {
                        layer.msg("当前权限范围内无匹配车辆！", {icon: 2});
                    }
                    var cars = gridPage.dataList;
                    var jsonData = [];
                    for (var i = 0; i < dataCount; i++) {
                        var car = cars[i];
                        var carCom = car.transCompany.name === undefined ? "" : car.transCompany.name;
                        var deviceId = "", model = "";
                        if (!isNull(car.vehicleDevice)
                            && !isNull(car.vehicleDevice.deviceId)
                            && car.vehicleDevice.deviceId > 0) {
                            deviceId = car.vehicleDevice.deviceId;
                            model = car.vehicleDevice.model === undefined ? "" : car.vehicleDevice.model;
                        }
                        var transcard = "";
                        if (!isNull(car.transportCard) && car.transportCard.transportCardId > 0) {
                            transcard = car.transportCard.transportCardId;
                        }
                        jsonData.push({
                            id: car.id,
                            carNo: car.carNumber,
                            carCom: carCom,
                            type: car.typeName,
                            deviceId: deviceId,
                            model: model,
                            sim: car.sim,
                            transcard: transcard,
                            storeNum: car.storeNum,
                            remark: car.remark
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
                    } else if (http_status == 406) {
                        layer.alert('权限不足，请重新选择权限范围内的车辆进行查询！', {
                            icon: 5,
                            title: ['警告', 'font-size:14px;color:#ffffff;background:#478de4;']
                        }, function (index2) {
                            layer.close(index2);
                            $("#search_text").select();
                        });
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
            showList("", "", 1);

            $("#search_type").change(function () {
                var type = $("#search_type").val();
                if (type < 2) {
                    $("#search_text").replaceWith("<input type=\"text\" class=\"search-text\" id=\"search_text\">");
                } else {
                    $("#search_text").replaceWith("<select class=\"search-text\" id=\"search_text\"></select>");
                    $.getJSON("../../manage/transcom/getCompanyList.do",
                        function (data) {
                            if (data == null || data == undefined || data.length == 0) {
                                return;
                            }
                            var len = data.length;
                            var comHtml = "";
                            for (var i = 0; i < len; i++) {
                                var company = data[i];
                                comHtml += "<option value=" + company.id + ">" + company.name + "</option>";
                            }
                            $("#search_text").append(comHtml);
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
                }
            });

            $("#search_btn").click(function () {
                var type = $("#search_type").val();
                if (type == 0) {
                    layer.alert('请选择查询类型！', {icon: 6});
                } else {
                    var carNo = "";
                    var company = "";
                    if (type == 1) {
                        carNo = trimAll($("#search_text").val());
                        if (!isCarNo(carNo)) {
                            layer.alert('车牌号不正确，请输入一个完整的车牌号！', {icon: 2}, function (index2) {
                                layer.close(index2);
                                $("#search_text").select();
                            });
                            return;
                        }
                    } else {
                        company = $("#search_text").val();
                    }
                    showList(carNo, company, 1);
                }
            });

            $("#page_id").change(function () {
                refreshPage();
            });

            $("#page_size").change(function () {
                var carNo = $("#cnum").val();
                if (isNull(carNo)) {
                    carNo = "";
                }
                var company = $("#ccom").val();
                if (isNull(company)) {
                    company = "";
                }
                showList(carNo, company, 1);
            });
        });
    </script>
</pop:Permission>