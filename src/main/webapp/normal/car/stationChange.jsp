<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/functions' prefix='fn' %>
<%@ taglib prefix="pop" uri="/pop-tags" %>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=center-width, initial-scale=1, maximum-scale=1">
    <title>车辆远程换站</title>
    <script src="../../resources/js/base.js"></script>
    <link rel="stylesheet" href="../../resources/css/base.css ">
    <link rel="stylesheet" href="../../resources/css/cfgEdit.css ">
    <link rel="stylesheet" href="../../resources/plugins/iziToast/iziToast.min.css"/>
    <%--<link rel="stylesheet" href="../../normal/car/remote/iztoast.css"/>--%>
    <script src="../../resources/plugins/jquery-1.8.3.min.js"></script>
    <%--<script src="../../normal/car/remote/izitoast.js"></script>--%>
    <script src="../../resources/plugins/iziToast/iziToast.min.js"></script>
    <script src="../../resources/plugins/layer/layer.js"></script>
    <script src="../../resources/plugins/verify.js"></script>
    <script src="../../resources/plugins/SelectBox.min.js"></script>
    <style>
        input[readonly] {
            background: #e6e7e9;
        }

        .iziToast-wrapper {
             width: 362px;
             height: 60px;
         }
    </style>
</head>

<body>
<div class="container">
    <div class="info-zone">
        <div class="base-info">
            <div class="info-title">
                车辆信息
            </div>
            <table class="base-table">
                <tr>
                    <td>车牌号</td>
                    <td>
                        <input type="text" class="editInfo" id="car_no" value="${carNumber}" readonly>
                    </td>
                </tr>
            </table>
        </div>
        <div class="sub-info">
            <div class="info-title">
                配送信息
            </div>
            <table class="sub-table" id="change_info">
                <tr>
                    <td>配送单号</td>
                    <td>仓号</td>
                    <td>加油站</td>
                </tr>
                <c:forEach var="distribution" items="${distributions}" varStatus="status">
                    <tr>
                        <td style="display:none">${distribution.carId}</td>
                        <td style="display:none">${distribution.transportId}</td>
                        <td>${distribution.invoice}</td>
                        <td>${distribution.storeId}</td>
                        <td style="display:none">${distribution.oildepotId}</td>
                        <td style="display:none">${distribution.gasstationId}</td>
                        <td>
                            <select>
                                <c:forEach var="station" items="${gasStations}" varStatus="varStatus">
                                    <c:choose>
                                        <c:when test="${distribution.gasstationId==station.id}">
                                            <option value="${station.id}" selected>${station.name}</option>
                                        </c:when>
                                        <c:otherwise>
                                            <option value="${station.id}">${station.name}</option>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>
                            </select>
                        </td>
                    </tr>
                </c:forEach>

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
    var iziPos = {
        bottomRight: "bottomRight",
        bottomLeft: "bottomLeft",
        topRight: "topRight",
        topLeft: "topLeft",
        topCenter: "topCenter",
        bottomCenter: "bottomCenter",
        center: "center"
    };

    $(function () {
        $('.sub-info tr td:first-child').width(150);
        $('.sub-info tr td:nth-child(2)').width(50);
        var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
        $("#cancel").click(function () {
            parent.layer.close(index);
        });
        $("#confirm").click(function () {
            var changedFlag = true;
            var len = $("#change_info").find("tr").length;
            for (var i = 1; i < len; i++) {
                var tr = $("#change_info").find("tr").eq(i);
                var transportId = tr.children().eq(1).html(),
                    invoice = tr.children().eq(2).html(),
                    gasstationId = tr.children().eq(5).html(),
                    changedGasstationId = tr.children().last().find("select").val();
                if (gasstationId == changedGasstationId) {
                    changedFlag = false;
                    iziToast.warning({
                        title: 'Warning',
                        message: '配送单：' + invoice + '，未换站！',
                        timeout: 3000,
                        position: iziPos.topLeft
                    });
                    // layer.msg('配送单：' + invoice + '，未换站！', {icon: 0});
                    continue;
                }
                (function (invoice, i) {
                    $.post("../../manage/remote/asyn_change_station_request",
                        encodeURI("transport_id=" + transportId + "&changed_station_id=" + changedGasstationId +
                            "&token=" + generateUUID()),
                        function (data) {
                            if (data.id > 0) {
                                iziToast.error({
                                    title: 'Error',
                                    message: '配送单：' + invoice + '，换站请求发送失败！' + data.msg,
                                    timeout: 3000,
                                    position: iziPos.topLeft,
                                    onClose: function(){
                                        if (i == len - 1 && changedFlag) {
                                            parent.layer.close(index);
                                        }
                                    }
                                });
                                // layer.msg('配送单：' + invoice + '，换站请求发送失败！<br>' + data.msg, {icon: 2});
                            } else {
                                iziToast.success({
                                    title: 'Success',
                                    message: '配送单：' + invoice + '，换站请求发送成功！',
                                    timeout: 3000,
                                    position: iziPos.topLeft,
                                    onClose: function(){
                                        if (i == len - 1 && changedFlag) {
                                            parent.layer.close(index);
                                        }
                                    }
                                });
                                // layer.msg('配送单：' + invoice + '，换站请求发送成功！', {icon: 1});
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
                        return;
                    });
                })(invoice, i)
            }
        });
    });
</script>