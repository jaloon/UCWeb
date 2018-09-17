<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/functions' prefix='fn' %>
<%@ taglib prefix="pop" uri="/pop-tags" %>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=center-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>车辆远程控制</title>
    <link rel="stylesheet" href="../../resources/css/base.css ">
    <link rel="stylesheet" href="../../resources/css/cfgEdit.css ">
    <script src="../../resources/plugins/jquery-1.8.3.min.js"></script>
    <script src="../../resources/plugins/layer/layer.js"></script>
    <script src="../../resources/plugins/verify.js"></script>
    <script src="../../resources/plugins/SelectBox.min.js"></script>
    <script src="../../resources/js/base.js"></script>
    <c:if test="${mode==5|| mode == 7}">
        <style>
            <c:if test="${mode == 7}">
            .store-ids-item {
                height: 18px;
                width: 18px;
                margin: 9px 0px;
            }

            .store-ids-text {
                margin: 10px 15px 10px 5px;
                position: relative;
                bottom: 4px;
            }
            </c:if>
            .check-table {
                font-size: 14px;
                color: #445065;
                text-align: center;
            }

            .check-table tr {
                height: 30px;
            }

            .check-table tr td:first-child {
                width: 40px;
            }

            .check-table tr td:nth-child(2) {
                text-align: left;
                padding-left: 10px;
            }

            .lock-check {
                height: 16px;
                width: 16px;
            }
            
            #lockIds {
                cursor: pointer;
            }
        </style>
    </c:if>
</head>

<body>
<div class="container">
    <div class="info-zone">
        <div class="base-info">
            <div class="info-title">
                <c:if test="${mode==1}">进油库</c:if>
                <c:if test="${mode==2}">出油库</c:if>
                <c:if test="${mode==3}">进加油站</c:if>
                <c:if test="${mode==4}">出加油站</c:if>
                <c:if test="${mode==5}">进入应急</c:if>
                <c:if test="${mode==6}">取消应急</c:if>
                <c:if test="${mode==8}">待进油区</c:if>
                <c:if test="${mode==9}">进油区</c:if>
                <c:if test="${mode==10}">出油区</c:if>
                <c:if test="${mode==7}">远程状态变更</c:if>
            </div>
            <table class="base-table">
                <tr>
                    <td>车牌号码</td>
                    <td>
                        <input type="text" class="editInfo" id="carno" value="${carStatus.carNumber}" readonly>
                    </td>
                </tr>
                <tr>
                    <td>在线状态</td>
                    <td>
                        <c:if test="${carStatus.online==null}">
                            <input type="text" class="editInfo" id="online" value="未知" readonly>
                        </c:if>
                        <c:if test="${carStatus.online==0}">
                            <input type="text" class="editInfo" id="online" value="车载终端离线" readonly>
                        </c:if>
                        <c:if test="${carStatus.online==1}">
                            <input type="text" class="editInfo" id="online" value="车载终端在线" readonly>
                        </c:if>
                    </td>
                </tr>
                <tr>
                    <td>车辆状态</td>
                    <td>
                        <input type="text" class="editInfo" id="status" value="${carStatus.statusName}" readonly>
                        <c:if test="${mode != 7}">
                            <input type="hidden" id="changeStatus" value="${carStatus.status}">
                        </c:if>
                    </td>
                </tr>

                <c:if test="${mode == 1 || mode == 2 || mode == 8 || mode == 9 || mode == 10}">
                    <tr>
                        <td>油库名称</td>
                        <td>
                            <input type="hidden" id="stationType" value="1">
                            <select class="editInfo" id="stationId">
                                <c:forEach var="station" items="${depots}" varStatus="varStatus">
                                    <option value="${station.id}">${station.name}</option>
                                </c:forEach>
                            </select>
                        </td>
                    </tr>
                </c:if>
                <c:if test="${mode==3 || mode==4}">
                    <tr>
                        <td>加油站名称</td>
                        <td>
                            <input type="hidden" id="stationType" value="2">
                            <select class="editInfo" id="stationId">
                                <c:forEach var="station" items="${stations}" varStatus="varStatus">
                                    <option value="${station.id}">${station.name}</option>
                                </c:forEach>
                            </select>
                        </td>
                    </tr>
                </c:if>
                <c:if test="${mode==5 || mode==6}">
                    <tr>
                        <td>站点类型</td>
                        <td>
                            <select class="editInfo" id="stationType">
                                <option value="1">油库</option>
                                <option value="2">加油站</option>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <td>站点名称</td>
                        <td>
                            <select class="editInfo" id="stationId"></select>
                        </td>
                    </tr>
                    <c:if test="${mode==5}">
                    <tr id="lock_tr">
                        <td>锁列表</td>
                        <td>
                            <input type="text" class="editInfo" id="lockIds" readonly>
                        </td>
                    </tr>
                    </c:if>
                </c:if>
                <c:if test="${mode==7}">
                    <tr>
                        <td>变更状态</td>
                        <td>
                            <select class="editInfo" id="changeStatus">
                                <option value=1>在油库</option>
                                <option value=2>在途中</option>
                                <option value=3>在加油站</option>
                                <option value=4>返程中</option>
                                <option value=5>应急</option>
                                <option value=6>待入油区</option>
                                <option value=7>在油区</option>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <td>站点类型</td>
                        <td>
                            <select class="editInfo" id="stationType">
                                <option value="1">油库</option>
                                <option value="2">加油站</option>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <td>站点名称</td>
                        <td>
                            <select class="editInfo" id="stationId"></select>
                        </td>
                    </tr>
                    <tr id="store_tr" style="display: none;">
                        <td>仓号列表</td>
                        <td>
                            <c:forEach var="storeId" begin="1" end="${storeNum}" step="1">
                                <input type="checkbox" class="store-ids-item" value="${storeId}">
                                <span class="store-ids-text">${storeId}</span>
                            </c:forEach>
                        </td>
                    </tr>
                    <tr id="lock_tr" style="display: none;">
                        <td>锁列表</td>
                        <td>
                            <input type="text" class="editInfo" id="lockIds" readonly>
                        </td>
                    </tr>
                </c:if>

            </table>
        </div>
    </div>
    <div class="oper-zone">
        <input type="button" id="cancel" value="取消">
        <input type="button" id="confirm" value="确认">
    </div>
</div>
<div id="lock_box" style="display: none;">
    <table class="check-table"></table>
</div>
</body>

</html>
<script>
    $(function () {
        var index = parent.layer.getFrameIndex(window.name); //获取窗口索引

        <c:if test="${mode==5 || mode==6}">
        var depotHtml = "", stationHtml = "";
        $.getJSON("../../manage/oildepot/getAllOilDepotsAndGasStations.do",
            function (data, textStatus, jqXHR) {
                var depots = data.depots,
                    stations = data.stations;
                if (depots != undefined && depots != null && depots.length > 0) {
                    for (var i = 0; i < depots.length; i++) {
                        depotHtml += "<option value=" + depots[i].id + ">" + depots[i].name + "</option>"
                    }
                } else {
                    depotHtml = "<option value=0>未知</option>";
                }
                if (stations != undefined && stations != null && stations.length > 0) {
                    for (var i = 0; i < stations.length; i++) {
                        stationHtml += "<option value=" + stations[i].id + ">" + stations[i].name + "</option>"
                    }
                } else {
                    stationHtml = "<option value=0>未知</option>";
                }
                $("#stationId").html(depotHtml);
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
        $("#stationType").change(function () {
            if ($("#stationType").val() == 0) {
                $("#stationId").html("<option value=0>未知</option>");
            } else if ($("#stationType").val() == 1) {
                $("#stationId").html(depotHtml);
            } else {
                $("#stationId").html(stationHtml);
            }
        });
        <c:if test="${mode==5}">
        function parseLock(lock) {
            return  "仓" + lock.store_id + "-" + (lock.seat == 1 ? "上仓锁-" : "下仓锁-") + lock.seat_index;
        }

        var lock_html = "";
        var lockNum = 0;
        $.getJSON("../../manage/car/findlocksByCarNo.do?carNumber=${carStatus.carNumber}",
            function (data, textStatus, jqXHR) {
                var len = data.length;
                if (len == 0) {
                    layer.msg("车辆${carStatus.carNumber}未绑定锁，请查证后再操作！", {
                        icon: 2,
                        time: 1500
                    }, function () {
                        parent.layer.close(index);
                    });
                    return;
                }
                for (var i = 0; i < len; i++) {
                    var lock = data[i];
                    if (lock.is_has_bind == 1 && lock.bind_status == 2 && lock.lock_device_id > 0) {
                        lock_html += "<tr><td><input type='checkbox' class='lock-check' value='"
                            + lock.lock_device_id + "'></td><td>" + parseLock(lock) + "</td></tr>";
                        lockNum++;
                    }
                }
                if (lock_html.length == 0) {
                    layer.msg("车辆${carStatus.carNumber}未绑定锁，请查证后再操作！", {
                        icon: 2,
                        time: 1500
                    }, function () {
                        parent.layer.close(index);
                    });
                }
                $(".check-table").html(lock_html);
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
        var lockSelectedNum = 0;
        $("#lock_tr").click(function () {
            layer.open({
                type: 1,
                title: ['选择锁', 'font-size:14px;color:#ffffff;background:#478de4;'],
                // area: ['500px', '500px'],
                content: $('#lock_box'),
                end: function () {
                    lockSelectedNum = 0;
                    var lockids = "";
                    $(".lock-check:checked").each(function () {
                        lockids += "," + $(this).val();
                        lockSelectedNum++;
                    });
                    if (lockids.length > 0) {
                        $("#lockIds").val(lockids.slice(1));
                    } else {
                        $("#lockIds").val("");
                    }
                }
            });
        });
        </c:if>
        </c:if>

        <c:if test="${mode==7}">
        var lock_html = "";
        //  $("#select_id option[value='3']").remove(); //删除值为3的option
        $("#changeStatus option[value=${carStatus.status}]").remove();
        var depotHtml = "", stationHtml = "";
        var lockNum = 0;

        function statusChange() {
            var status = $("#changeStatus").val();
            if (status == 1 || status > 5) {
                $("#store_tr").hide();
                $("#lock_tr").hide();
                $("#stationType").val(1);
                $("#stationType").prop("disabled", true);
                $("#stationId").html(depotHtml);
            } else if (status == 3) { // 3 在加油站
                $("#lock_tr").hide();
                $("#store_tr").show();
                $("#stationType").val(2);
                $("#stationType").prop("disabled", true);
                $("#stationId").html(stationHtml);
            } else if (status == 5) { // 5 应急
                $("#store_tr").hide();
                $("#lock_tr").show();
                $("#stationType").val(1);
                $("#stationType").prop("disabled", false);
                $("#stationId").html(depotHtml);
                if (lock_html.length == 0) {
                    $.getJSON("../../manage/car/findlocksByCarNo.do?carNumber=${carStatus.carNumber}",
                        function (data, textStatus, jqXHR) {
                            var len = data.length;
                            if (len == 0) {
                                layer.msg("车辆${carStatus.carNumber}未绑定锁，请查证后再操作！", {
                                    icon: 2,
                                    time: 1500
                                }, function () {
                                    parent.layer.close(index);
                                });
                                return;
                            }
                            lockNum = 0;
                            for (var i = 0; i < len; i++) {
                                var lock = data[i];
                                if (lock.is_has_bind == 1 && lock.bind_status == 2 /*&& lock.is_allowed_open == 2*/) {
                                    lock_html += "<tr><td><input type='checkbox' class='lock-check' value='"
                                        + lock.lock_device_id + "'></td><td>" + parseLock(lock) + "</td></tr>";
                                    lockNum++;
                                }
                            }
                            if (lock_html.length == 0) {
                                layer.msg("车辆${carStatus.carNumber}未绑定锁，请查证后再操作！", {
                                    icon: 2,
                                    time: 1500
                                }, function () {
                                    parent.layer.close(index);
                                });
                            }
                            $(".check-table").html(lock_html);
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
                } else {
                    $(".check-table").html(lock_html);
                }
            } else {
                $("#store_tr").hide();
                $("#lock_tr").hide();
                $("#stationType").val(1);
                $("#stationType").prop("disabled", false);
                $("#stationId").html(depotHtml);
            }
        }

        $.getJSON("../../manage/oildepot/getAllOilDepotsAndGasStations.do",
            function (data, textStatus, jqXHR) {
                var depots = data.depots,
                    stations = data.stations;
                if (depots != undefined && depots != null && depots.length > 0) {
                    for (var i = 0; i < depots.length; i++) {
                        depotHtml += "<option value=" + depots[i].id + ">" + depots[i].name + "</option>"
                    }
                } else {
                    depotHtml = "<option value=0>未知</option>";
                }
                if (stations != undefined && stations != null && stations.length > 0) {
                    for (var i = 0; i < stations.length; i++) {
                        stationHtml += "<option value=" + stations[i].id + ">" + stations[i].name + "</option>"
                    }
                } else {
                    stationHtml = "<option value=0>未知</option>";
                }
                statusChange();
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
        $("#stationType").change(function () {
            if ($("#stationType").val() == 0) {
                $("#stationId").html("<option value=0>未知</option>");
            } else if ($("#stationType").val() == 1) {
                $("#stationId").html(depotHtml);
            } else {
                $("#stationId").html(stationHtml);
            }
        });

        function parseLock(lock) {
            return  "仓" + lock.store_id + "-" + (lock.seat == 1 ? "上仓锁-" : "下仓锁-") + lock.seat_index;
        }

        $("#changeStatus").change(statusChange);

        var lockSelectedNum = 0;
        $("#lock_tr").click(function () {
            layer.open({
                type: 1,
                title: ['选择锁', 'font-size:14px;color:#ffffff;background:#478de4;'],
                // area: ['500px', '500px'],
                content: $('#lock_box'),
                end: function () {
                    lockSelectedNum = 0;
                    var lockids = "";
                    $(".lock-check:checked").each(function () {
                        lockids += "," + $(this).val();
                        lockSelectedNum++;
                    });
                    if (lockids.length > 0) {
                        $("#lockIds").val(lockids.slice(1));
                    } else {
                        $("#lockIds").val("");
                    }
                }
            });
        });

        </c:if>

        $("#cancel").click(function () {
            parent.layer.close(index);
        });

        $("#confirm").click(function () {
            <c:if test="${carStatus.online==null}">
            layer.msg("车辆在线状态数据异常，请稍后再试！", {
                icon: 0,
                time: 1500
            }, function () {
                parent.layer.close(index);
            });
            </c:if>
            <c:if test="${carStatus.online==0}">
            layer.msg("车载终端离线，请稍后再试！", {
                icon: 0,
                time: 1500
            }, function () {
                parent.layer.close(index);
            });
            </c:if>
            <c:if test="${carStatus.online==1}">
            var stationType = $("#stationType").val();
            var stationId = $("#stationId").val();

            <c:if test="${mode==7}">
            var status = $("#changeStatus").val();
            var storeIds = 0;
            if (status == 3) {
                $(".store-ids-item:checked").each(function () {
                    storeIds |= 1 << ($(this).val() - 1);
                });
                if (storeIds == 0) {
                    layer.alert('状态变更为在加油站时至少要选择一个仓！', {icon: 2}, function (index2) {
                        layer.close(index2);
                        $(".store-ids-item")[0].select();
                    });
                    return;
                }
            }
            var lockIds = $("#lockIds").val();
            if (status == 5 && lockSelectedNum == 0) {
                layer.alert('状态变更为应急时至少要选择一把锁！', {icon: 2}, function (index2) {
                    layer.close(index2);
                    $("#lock_tr").click();
                });
                return;
            }
            if (lockNum == lockSelectedNum) {
                lockIds = "";
            }
            var url = "../../manage/remote/asyn_status_alter_request";
            var param = encodeURI("car_number=${carStatus.carNumber}&station_type=" + stationType
                + "&station_id=" + stationId + "&status=" + status + "&store_ids=" + storeIds
                + "&lock_ids=" + lockIds + "&token=" + generateUUID());
            </c:if>
            <c:if test="${mode!=7}">
            var lockIds = $("#lockIds").val();
            <c:if test="${mode==5}">
            if (lockSelectedNum == 0) {
                layer.alert('应急操作时至少要选择一把锁！', {icon: 2}, function (index2) {
                    layer.close(index2);
                    $("#lock_tr").click();
                });
                return;
            }
            if (lockNum == lockSelectedNum) {
                lockIds = "";
            }
            </c:if>
            var url = "../../manage/remote/asyn_remote_inout_request";
            var param = encodeURI("control_type=${mode}&car_number=${carStatus.carNumber}&station_type=" + stationType
                + "&station_id=" + stationId + "&lock_ids=" + lockIds + "&token=" + generateUUID());
            </c:if>
            var loadIndex = layer.load();
            $.post(url, param,
                function (data) {
                    layer.close(loadIndex);
                    if (data.id > 0) {
                        layer.alert(data.msg, { icon: 2});
                    } else {
                        layer.msg("车辆远程控制指令发送成功！", {
                            icon: 1,
                            time: 500
                        }, function () {
                            parent.layer.close(index);
                        });
                    }
                },
                "json"
            ).error(function (XMLHttpRequest, textStatus, errorThrown) {
                layer.close(loadIndex);
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
            </c:if>
        });
    });
</script>