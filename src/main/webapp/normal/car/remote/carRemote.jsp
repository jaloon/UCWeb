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
                            <select class="editInfo" id="stationId">
                                <c:forEach var="station" items="${stations}" varStatus="varStatus">
                                    <option value="${station.id}">${station.name}</option>
                                </c:forEach>
                            </select>
                        </td>
                    </tr>
                </c:if>
                <c:if test="${mode==5 || mode==6}">
                    <input type="hidden" id="stationId" value="0">
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
                                <option value="0">未知</option>
                                <option value="1">油库</option>
                                <option value="2">加油站</option>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <td>站点名称</td>
                        <td>
                            <select class="editInfo" id="stationId">
                                <option value=0>未知</option>
                            </select>
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
</body>

</html>
<script>
    $(function () {
        <c:if test="${mode==7}">
        //  $("#select_id option[value='3']").remove(); //删除值为3的option
        $("#changeStatus option[value=${carStatus.status}]").remove();
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
                $("#stationId").append(depotHtml);
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
        $("#stationType").change(function () {
            $("#stationId").empty();
            if ($("#stationType").val() == 0) {
                $("#stationId").append("<option value=0>未知</option>");
            } else if ($("#stationType").val() == 1) {
                $("#stationId").append(depotHtml);
            } else {
                $("#stationId").append(stationHtml);
            }
        });
        </c:if>
        var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
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
            var stationId = $("#stationId").val();
            var status = $("#changeStatus").val();
            <c:if test="${mode==7}">
            var stationType = $("#stationType").val();
            </c:if>
            <c:if test="${mode!=7}">
            var stationType = 0;
            </c:if>
            $.post("../../manage/remote/asyn_remote_control_request",
                encodeURI("control_type=${mode}&car_number=${carStatus.carNumber}&station_id=" + stationId +
                    "&token=" + generateUUID() + "&status=" + status + "&station_type=" + stationType),
                function (data) {
                    if (data.id > 0) {
                        layer.msg(data.msg, {
                            icon: 2,
                            time: 500
                        }, function () {
                            parent.layer.close(index);
                        });
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
                if (XMLHttpRequest.readyState == 4 && XMLHttpRequest.status == 200 && textStatus == "parsererror") {
                    layer.confirm('登录失效，是否刷新页面重新登录？', {
                        icon: 0,
                        title: ['登录失效', 'font-size:14px;color:#ffffff;background:#478de4;']
                    }, function () {
                        location.reload(true);
                    });
                }
            });
            </c:if>
        });
    });
</script>