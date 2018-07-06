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
    <script type="text/javascript">
        $(function() {
        	<c:if test="${mode!='add'}">
                $("#ver").val(stringifyVer(${handset.ver}));
                <c:if test="${mode=='edit'}">
                    $("#gasstation").click(function(){
                        layer.msg('请前往加油站管理修改', { icon: 6, time: 1500 }); 
                    });
                </c:if>
            </c:if>
            <c:if test="${mode=='add'}">
                $("#hid").css({
                    width: '398px',
                    height: '28px'
                });
                $("#gasstation").css({
                    width: '398px',
                    height: '28px'
                });
                $.getJSON("../../manage/handset/findUnaddHandset.do",
                    function(data) {
                        var len = data.length;
                        for (var i = 0; i < len; i++) {
                            var hid = data[i];
                            $("#hid").append("<option value=" + hid + ">" + hid + "</option>");
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
                $.getJSON("../../manage/handset/findUnconfigGasStation.do",
                    function(data) {
                        var gasStations = eval(data);
                        var len = gasStations.length;
                        for (var i = 0; i < len; i++) {
                            var gasStation = gasStations[i];
                            $("#gasstation").append("<option value=" + gasStation.id + ">" + gasStation.name + "</option>");
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
                            <option value="">暂不指定加油站</option>
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
                        <input type="hidden" id="gasstation" value="${handset.gasStation.id}">
                        <input type="text" class="editInfo" value="${handset.gasStation.name}" readonly>
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