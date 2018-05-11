<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri='http://java.sun.com/jsp/jstl/functions' prefix='fn'%>
<%@ taglib prefix="pop" uri="/pop-tags" %>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=center-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>车辆远程换站</title>
    <link rel="stylesheet" href="../../resources/css/base.css ">
    <link rel="stylesheet" href="../../resources/css/cfgEdit.css ">
    <script src="../../resources/plugins/jquery-1.8.3.min.js"></script>
    <script src="../../resources/plugins/layer/layer.js"></script>
    <script src="../../resources/plugins/verify.js"></script>
    <script src="../../resources/plugins/SelectBox.min.js"></script>
    <script src="../../resources/js/base.js"></script>
    <style>
        #changeStatus {
            width: 398px;
            height: 28px;
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
                    换站信息
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
    $(function() {
        var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
        $("#cancel").click(function() {
            parent.layer.close(index);
        });
        $("#confirm").click(function() {
            var len = $("#change_info").find("tr").length;
            var changeInfos = [];
            for (let i = 1; i < len; i++) {
                var tr = $("#change_info").find("tr").eq(i);
                var	gasstationId = parseInt(tr.children().eq(5).html(),10),
                	changedGasstationId = parseInt(tr.children().last().find("select").val(),10);
                if (gasstationId == changedGasstationId) {
                	continue;
                }
                var change = {
               		userId: ${userId},
                   	carId: parseInt(tr.children().first().html(), 10),
                   	transportId: parseInt(tr.children().eq(1).html(), 10),
                   	invoice: tr.children().eq(2).html(),
                   	storeId: parseInt(tr.children().eq(3).html(),10),
                   	oildepotId: parseInt(tr.children().eq(4).html(),10),
                   	gasstationId: gasstationId,
                   	changedGasstationId: changedGasstationId,
                   	isApp: 0,
                   	longitude: 0,
                   	latitude: 0
                };
                changeInfos.push(change);
            }
            if (changeInfos.length == 0) {
            	layer.alert('远程换站必须更改加油站！', { icon: 2 }, function(index2) {
                    layer.close(index2);
                });
            	return;
            }
            // encodeURIComponent(URIstring)函数可把字符串作为 URI 组件进行编码。
            // 该方法不会对 ASCII 字母和数字进行编码，也不会对这些 ASCII 标点符号进行编码： - _ . ! ~ * ' ( ) 。
			// 其他字符（比如 ：;/?:@&=+$,# 这些用于分隔 URI 组件的标点符号），都是由一个或多个十六进制的转义序列替换的。
            var changeParam = encodeURIComponent(JSON.stringify(changeInfos));
            $.post("../../manage/car/remoteChange.do",
            	"changeInfos=" + changeParam + encodeURI("&carNumber=${carNumber}"),
                function(data) {
            		switch (data.errorTag) {
					case 0:
						layer.msg("远程换站成功！", {
                            icon: 1,
                            time: 500
                        }, function() {
                            parent.layer.close(index);
                        });
						break;
					case 20:
						layer.msg(data.errorId + "个远程换站操作失败！", {
                            icon: 2,
                            time: 500
                        }, function() {
                        	layer.open({
                       		  type: 1,
                       		  skin: 'layui-layer-demo', //样式类名
                       		  closeBtn: 0, //不显示关闭按钮
                       		  anim: 2,
                       		  shadeClose: true, //开启遮罩关闭
                       		  content:data.msg
                       		});
                        });
						break;
					default:
						layer.msg("远程换站失败！", {
                            icon: 2,
                            time: 500
                        });
						break;
					}
                },
                "json"
            );
        });
    });
</script>