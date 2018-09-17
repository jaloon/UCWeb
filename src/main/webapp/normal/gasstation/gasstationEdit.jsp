<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/functions' prefix='fn' %>
<%@ taglib prefix="pop" uri="/pop-tags" %>
<pop:Permission ename="viewGasstation">
    <!DOCTYPE html>
    <html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=center-width, initial-scale=1.0">
        <meta http-equiv="X-UA-Compatible" content="ie=edge">
        <title>加油站管理</title>
        <link rel="stylesheet" href="../../resources/css/base.css ">
        <link rel="stylesheet" href="../../resources/css/tabEdit.css ">
        <script src="../../resources/plugins/jquery-1.8.3.min.js"></script>
        <link rel="stylesheet" href="../../resources/plugins/jtab/jtab.css ">
        <script src="../../resources/plugins/jtab/jtab.js"></script>
        <script src="../../resources/plugins/layer/layer.js"></script>
        <script src="../../resources/plugins/verify.js"></script>
        <script src="../../resources/js/base.js"></script>
        <script src="../../resources/js/gasstation/gasstationEdit.js"></script>
        <script type="text/javascript">
            $(function () {
                <pop:Permission ename="editGasstation">
                <pop:Permission ename="editHandset">
                <c:if test="${mode=='edit'}">
                $.getJSON("../../manage/gasstation/findUnusedHandset.do",
                    function (data) {
                        if (${handset != null && handset.deviceId != null}) {
                            $("#handset").append("<option value=${handset.deviceId}>" + ${handset.deviceId} +"</option>");
                        }
                        var len = data.length;
                        if (len == 0 && ${handset == null || handset.deviceId == null}) {
                            $("#handset").closest("td").replaceWith("<td style='text-align: center'>无可配手持机<input type='hidden' id='handset' value='0'></td><")
                        }
                        for (var i = 0; i < len; i++) {
                            var handset = data[i];
                            $("#handset").append("<option value=" + handset + ">" + handset + "</option>");
                        }
                        if (${handset != null && handset.deviceId != null}) {
                            $("#handset").val(${handset.deviceId});
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
                </pop:Permission>
                </pop:Permission>
            });
        </script>
    </head>

    <body>
    <div class="container">
        <input type="hidden" id="mode" value="${mode}">
        <c:if test="${mode=='view'}">
            <div class="info-zone" style="height:540px">
                <div class="tab-title">
                    <div class="on">加油站基本信息</div>
                    <pop:Permission ename="viewHandset">
                        <div>手持机信息</div>
                    </pop:Permission>
                    <div>加油站卡信息</div>
                </div>
                <div class="tab-con">
                    <div class="tab-con-list">
                        <table class="base-table">
                            <tr>
                                <td>加油站ID:</td>
                                <td>
                                    <input type="text" class="editInfo" id="id" value="${gasStation.id}" readonly>
                                </td>
                            </tr>
                            <tr>
                                <td>加油站编号:</td>
                                <td>
                                    <input type="text" class="editInfo" id="officialId" value="${gasStation.officialId}"
                                           readonly>
                                </td>
                            </tr>
                            <tr>
                                <td>加油站名称:</td>
                                <td>
                                    <input type="text" class="editInfo" id="name" value="${gasStation.name}" readonly>
                                </td>
                            </tr>
                            <tr>
                                <td>加油站简称:</td>
                                <td>
                                    <input type="text" class="editInfo" id="abbr" value="${gasStation.abbr}" readonly>
                                </td>
                            </tr>
                            <tr>
                                <td>负责人:</td>
                                <td>
                                    <input type="text" class="editInfo" id="director" value="${gasStation.director}"
                                           readonly>
                                </td>
                            </tr>
                            <tr>
                                <td>联系电话:</td>
                                <td>
                                    <input type="tel" class="editInfo" id="phone" value="${gasStation.phone}" readonly>
                                </td>
                            </tr>
                            <tr>
                                <td>联系地址:</td>
                                <td>
                                    <input type="text" class="editInfo" id="address" value="${gasStation.address}"
                                           readonly>
                                </td>
                            </tr>
                            <tr>
                                <td>所属公司:</td>
                                <td>
                                    <input type="text" class="editInfo" id="company" value="${gasStation.company}"
                                           readonly>
                                </td>
                            </tr>
                            <tr>
                                <td>经度:</td>
                                <td>
                                    <input type="text" class="editInfo" id="longitude" value="${gasStation.longitude}"
                                           readonly>
                                </td>
                            </tr>
                            <tr>
                                <td>纬度:</td>
                                <td>
                                    <input type="text" class="editInfo" id="latitude" value="${gasStation.latitude}"
                                           readonly>
                                </td>
                            </tr>
                            <tr>
                                <td>施解封半径:</td>
                                <td>
                                    <input type="text" class="editInfo" id="radius" value="${gasStation.radius}"
                                           readonly>
                                </td>
                            </tr>
                            <tr>
                                <td>占地范围:</td>
                                <td>
                                    <input type="text" class="editInfo" id="region" value="${gasStation.coverRegion}"
                                           readonly>
                                </td>
                            </tr>
                            <tr>
                                <td>备注:</td>
                                <td>
                                    <input type="text" class="editInfo" id="remark" value="${gasStation.remark}"
                                           readonly>
                                </td>
                            </tr>
                        </table>
                    </div>
                    <pop:Permission ename="viewHandset">
                        <div class="tab-con-list">
                            <table class="sub-table">
                                <c:if test="${handset != null}">
                                    <tr>
                                        <td>序号</td>
                                        <td>手持机ID</td>
                                    </tr>
                                    <tr>
                                        <td>1</td>
                                        <td>${handset.deviceId}</td>
                                    </tr>
                                </c:if>
                                <c:if test="${handset == null}">
                                    <tr>
                                        <td colspan="2">未配置手持机</td>
                                    </tr>
                                </c:if>
                                </tr>
                            </table>
                        </div>
                    </pop:Permission>
                    <div class="tab-con-list">
                        <table class="sub-table">
                            <tr>
                                <td>序号</td>
                                <td>卡类型</td>
                                <td>卡ID</td>
                            </tr>
                            <c:forEach var="card" items="${cards}" varStatus="status">
                                <tr>
                                    <td>${status.index+1}</td>
                                    <td>${card.typeName}</td>
                                    <td>${card.cardId}</td>
                                </tr>
                            </c:forEach>
                        </table>
                    </div>
                </div>
            </div>
        </c:if>
        <pop:Permission ename="editGasstation">
            <c:if test="${mode=='add'}">
                <div class="info-zone" style="height:480px">
                    <!-- <div class="tab-title">
                        <div class="on">加油站基本信息</div>
                    </div> -->
                    <div class="tab-con">
                        <div class="tab-con-list">
                            <table class="base-table">
                                <tr>
                                    <td>加油站编号:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="officialId" required>
                                    </td>
                                </tr>
                                <tr>
                                    <td>加油站名称:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="name" required>
                                    </td>
                                </tr>
                                <tr>
                                    <td>加油站简称:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="abbr" required>
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
                                        <input type="tel" class="editInfo" id="phone">
                                    </td>
                                </tr>
                                <tr>
                                    <td>联系地址:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="address">
                                    </td>
                                </tr>
                                <tr>
                                    <td>所属公司:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="company">
                                    </td>
                                </tr>
                                <tr>
                                    <td>经度:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="longitude" required>
                                    </td>
                                </tr>
                                <tr>
                                    <td>纬度:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="latitude" required>
                                    </td>
                                </tr>
                                <tr>
                                    <td>施解封半径:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="radius" required>
                                    </td>
                                </tr>
                                <tr>
                                    <td>占地范围:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="region">
                                    </td>
                                </tr>
                                <tr>
                                    <td>备注:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="remark">
                                    </td>
                                </tr>
                            </table>
                        </div>
                    </div>
                </div>
                <div class="oper-zone">
                    <input type="button" id="cancel" value="取消">
                    <input type="button" id="confirm" value="确认">
                </div>
            </c:if>
            <c:if test="${mode=='edit'}">
                <div class="info-zone" style="height:540px">
                    <div class="tab-title">
                        <div class="on">加油站基本信息</div>
                        <pop:Permission ename="editHandset">
                            <div>手持机信息</div>
                        </pop:Permission>
                        <div>加油站卡信息</div>
                    </div>
                    <div class="tab-con">
                        <div class="tab-con-list">
                            <table class="base-table">
                                <tr>
                                    <td>加油站ID:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="id" value="${gasStation.id}" readonly>
                                    </td>
                                </tr>
                                <tr>
                                    <td>加油站编号:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="officialId"
                                               value="${gasStation.officialId}" readonly>
                                    </td>
                                </tr>
                                <tr>
                                    <td>加油站名称:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="name" value="${gasStation.name}"
                                               readonly>
                                    </td>
                                </tr>
                                <tr>
                                    <td>加油站名称:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="abbr" value="${gasStation.abbr}"
                                               readonly>
                                    </td>
                                </tr>
                                <tr>
                                    <td>负责人:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="director"
                                               value="${gasStation.director}">
                                    </td>
                                </tr>
                                <tr>
                                    <td>联系电话:</td>
                                    <td>
                                        <input type="tel" class="editInfo" id="phone" value="${gasStation.phone}">
                                    </td>
                                </tr>
                                <tr>
                                    <td>联系地址:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="address" value="${gasStation.address}">
                                    </td>
                                </tr>
                                <tr>
                                    <td>所属公司:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="company" value="${gasStation.company}">
                                    </td>
                                </tr>
                                <tr>
                                    <td>经度:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="longitude"
                                               value="${gasStation.longitude}" required>
                                    </td>
                                </tr>
                                <tr>
                                    <td>纬度:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="latitude" value="${gasStation.latitude}"
                                               required>
                                    </td>
                                </tr>
                                <tr>
                                    <td>施解封半径:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="radius" value="${gasStation.radius}"
                                               required>
                                    </td>
                                </tr>
                                <tr>
                                    <td>占地范围:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="region"
                                               value="${gasStation.coverRegion}">
                                    </td>
                                </tr>
                                <tr>
                                    <td>备注:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="remark" value="${gasStation.remark}">
                                    </td>
                                </tr>
                            </table>
                        </div>
                        <pop:Permission ename="editHandset">
                            <div class="tab-con-list">
                                <table class="sub-table">
                                    <tr>
                                        <td>序号</td>
                                        <td>手持机ID</td>
                                    </tr>
                                    <tr>
                                        <td>1</td>
                                        <td>
                                            <select id="handset">
                                            </select>
                                        </td>
                                    </tr>
                                </table>
                            </div>
                        </pop:Permission>
                        <div class="tab-con-list">
                            <table class="sub-table" id="card_info">
                                <tr>
                                    <td width="60px">序号</td>
                                    <td width="120px">卡类型</td>
                                    <td width="200px">卡ID</td>
                                    <td width="100px">操作</td>
                                </tr>
                                <c:forEach var="card" items="${cards}" varStatus="status">
                                    <tr>
                                        <td class="serialNo">${status.index+1}</td>
                                        <td>${card.typeName}</td>
                                        <td class="cardIds">${card.cardId}</td>
                                        <td><img alt="删除" title="删除" src="../../resources/images/operate/delete.png"
                                                 onclick="deleteTr(this)"></td>
                                    </tr>
                                </c:forEach>
                                <tr>
                                    <td><img style="cursor: pointer;" alt="添加" title="添加"
                                             src="../../resources/images/operate/addNew.png" onclick="addTr()"></td>
                                    <td></td>
                                    <td></td>
                                    <td></td>
                                </tr>
                            </table>
                        </div>
                    </div>
                </div>
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