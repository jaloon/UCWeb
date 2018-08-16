<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri='http://java.sun.com/jsp/jstl/functions' prefix='fn'%>
<%@ taglib prefix="pop" uri="/pop-tags" %>
<pop:Permission ename="viewHandset">
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=center-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>手持机管理</title>
    <link rel="stylesheet" href="../../resources/css/base.css ">
    <link rel="stylesheet" href="../../resources/css/baseEdit.css ">
    <script src="../../resources/plugins/jquery-1.8.3.min.js"></script>
    <script src="../../resources/plugins/layer/layer.js"></script>
    <script src="../../resources/plugins/verify.js"></script>
    <script src="../../resources/js/base.js"></script>
    <script src="../../resources/js/handset/handsetEdit.js"></script>
    <style>
        select.editInfo {
            width: 398px;
            height: 28px;
        }
    </style>
    <script type="text/javascript">
        $(function() {
        	<c:if test="${mode!='add'}">
                $("#ver").val(stringifyVer(${handset.ver}));
            </c:if>
            <c:if test="${mode=='add'}">
                $.getJSON("../../manage/handset/findUnaddHandset.do",
                    function(data) {
                        var len = data.length;
                        for (var i = 0; i < len; i++) {
                            var hid = data[i];
                            $("#hid").append("<option value=" + hid + ">" + hid + "</option>");
                        }
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
            </c:if>
        });
    </script>
</head>

<body>
    <div class="container">
        <input type="hidden" id="mode" value="${mode}">
        <c:if test="${mode=='view'}">
            <table>
                <tr>
                    <td>手持机ID:</td>
                    <td>
                        <input type="text" class="editInfo" value="${handset.deviceId}" readonly>
                    </td>
                </tr>
                <tr>
                    <td>版本号:</td>
                    <td>
                        <input type="text" class="editInfo" id="ver" value="${handset.ver}" readonly>
                    </td>
                </tr>
                <tr>
                    <td>加油站:</td>
                    <td>
                        <input type="text" class="editInfo" value="${handset.gasStation.name}" readonly>
                    </td>
                </tr>
                <tr>
                    <td>负责人:</td>
                    <td>
                        <input type="text" class="editInfo" value="${handset.director}" readonly>
                    </td>
                </tr>
                <tr>
                    <td>联系电话:</td>
                    <td>
                        <input type="text" class="editInfo" value="${handset.phone}" readonly>
                    </td>
                </tr>
                <tr>
                    <td>身份证号:</td>
                    <td>
                        <input type="tel" class="editInfo" value="${handset.identityCard}" readonly>
                    </td>
                </tr>
                <tr>
                    <td>备注:</td>
                    <td>
                        <input type="text" class="editInfo" id="remark" value="${handset.remark}" readonly>
                    </td>
                </tr>
            </table>
        </c:if>
        <pop:Permission ename="editHandset">
        <c:if test="${mode=='add'}">
            <table>
                <tr>
                    <td>手持机ID:</td>
                    <td>
                    	<input type="hidden" required>
                        <select class="editInfo" id="hid"></select>
                    </td>
                </tr>
                <tr>
                    <td>加油站:</td>
                    <td>
                    	<input type="hidden" required>
                        <select class="editInfo" id="gasstation">
                            <option value="0">暂不指定加油站</option>
                            <c:forEach items="${stations}" var="station">
                                <option value="${station.id}">${station.name}</option>
                            </c:forEach>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>负责人:</td>
                    <td>
                        <input type="text" class="editInfo" id="director">
                    </td>
                </tr>
                <tr>
                    <td>联系电话:</td>
                    <td>
                        <input type="text" class="editInfo" id="phone">
                    </td>
                </tr>
                <tr>
                    <td>身份证号:</td>
                    <td>
                        <input type="tel" class="editInfo" id="identity">
                    </td>
                </tr>
                <tr>
                    <td>备注:</td>
                    <td>
                        <input type="text" class="editInfo" id="remark">
                    </td>
                </tr>
            </table>
            <div class="oper-zone">
                <input type="button" id="cancel" value="取消">
                <input type="button" id="confirm" value="确认">
            </div>
        </c:if>
        <c:if test="${mode=='edit'}">
            <input type="hidden" id="id" value="${handset.id}">
            <table>
                <tr>
                    <td>手持机ID:</td>
                    <td>
                        <input type="text" class="editInfo" id="hid" value="${handset.deviceId}" readonly>
                    </td>
                </tr>
                <tr>
                    <td>版本号:</td>
                    <td>
                        <input type="text" class="editInfo" id="ver" value="${handset.ver}" readonly>
                    </td>
                </tr>
                <tr>
                    <td>加油站:</td>
                    <td>
                        <select class="editInfo" id="gasstation">
                            <option value="${handset.gasStation.id}">${handset.gasStation.name}</option>
                            <c:forEach items="${stations}" var="station">
                                <option value="${station.id}">${station.name}</option>
                            </c:forEach>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>负责人:</td>
                    <td>
                        <input type="text" class="editInfo" id="director" value="${handset.director}">
                    </td>
                </tr>
                <tr>
                    <td>联系电话:</td>
                    <td>
                        <input type="text" class="editInfo" id="phone" value="${handset.phone}">
                    </td>
                </tr>
                <tr>
                    <td>身份证号:</td>
                    <td>
                        <input type="tel" class="editInfo" id="identity" value="${handset.identityCard}">
                    </td>
                </tr>
                <tr>
                    <td>备注:</td>
                    <td>
                        <input type="text" class="editInfo" id="remark" value="${handset.remark}">
                    </td>
                </tr>
            </table>
            <div class="oper-zone">
                <input type="button" id="cancel" value="取消">
                <input type="button" id="confirm" value="确认">
            </div>
        </c:if>
        </pop:Permission>
    </div>
</body>

</html>
</pop:Permission>