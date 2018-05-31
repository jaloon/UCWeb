<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri='http://java.sun.com/jsp/jstl/functions' prefix='fn'%>
<%@ taglib prefix="pop" uri="/pop-tags" %>
<pop:Permission ename="viewCar">
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=center-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>车辆信息管理</title>
    <link rel="stylesheet" href="../../resources/css/base.css ">
    <link rel="stylesheet" href="../../resources/css/tabEdit.css ">
    <script src="../../resources/plugins/jquery-1.8.3.min.js"></script>
    <link rel="stylesheet" href="../../resources/plugins/jtab/jtab.css ">
    <script src="../../resources/plugins/jtab/jtab.js"></script>
    <script src="../../resources/plugins/layer/layer.js"></script>
    <script src="../../resources/plugins/json2.js"></script>
    <script src="../../resources/plugins/verify.js"></script>
    <script src="../../resources/js/base.js"></script>
    <script src="../../resources/js/car/carEdit.js"></script>
    <style>
    	.editInfo {
    		height: 26px;
    		width: 384px;
    	}
    	
        #company,
        #type,
        #transcard {
            width: 398px;
            height: 28px;
        }
    </style>
    <script>
        $(function() {
            $.getJSON("../../manage/transcom/getCompanyList.do",
                function(data) {
                    var companies = eval(data);
                    var len = companies.length;
                    for (var i = 0; i < len; i++) {
                        var company = companies[i];
                        $("#company").append("<option value=" + company.id + ">" + company.name + "</option>");
                    }
                    <c:if test="${mode=='edit'}">
                        $("#company").val("${car.transCompany.id}");
                    </c:if>
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
            $.getJSON("../../manage/transcard/findUnusedTranscards.do",
                function(data) {
            		<c:if test="${mode=='edit' && car.transportCard.transportCardId != 0}">
                        $("#transcard").append("<option value=${car.transportCard.transportCardId}>${car.transportCard.transportCardId}</option>");
                    </c:if>
                    $("#transcard").append("<option value=0>暂不设置配送卡</option>");
                    var len = data.length;
                    for (var i = 0; i < len; i++) {
                        var transcard = data[i];
                        $("#transcard").append("<option value=" + transcard.transportCardId + ">" + transcard.transportCardId + "</option>");
                    }
                    <c:if test="${mode=='edit'}">
                        $("#transcard").val("${car.transportCard.transportCardId}");
                    </c:if>
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
        });
    </script>
</head>

<body>
    <div class="container">
        <input type="hidden" id="mode" value="${mode}">
        <pop:Permission ename="editCar">
        <c:if test="${mode=='add'}">
            <div class="info-zone" style="height:296px">
            	<div class="tab-title">
					<div class="on">车辆基本信息</div>
					<div>司机信息</div>
				</div>
				<div class="tab-con">
					<div class="tab-con-list">
						<table class="base-table">
	                        <tr>
	                            <td>车牌号:</td>
	                            <td>
	                                <input type="text" class="editInfo" id="cid" required>
	                            </td>
	                        </tr>
	                        <tr>
	                            <td>所属公司:</td>
	                            <td>
	                            	<input type="hidden" required>
	                                <select class="editInfo" id="company">
	                                </select>
	                            </td>
	                        </tr>
	                        <tr>
	                            <td>车辆类型:</td>
	                            <td>
	                            	<input type="hidden" required>
	                                <select class="editInfo" id="type">
	                                    <option value=1>自有车辆</option>
	                                    <option value=2>社会车辆</option>
	                                </select>
	                            </td>
	                        </tr>
	                        <tr>
	                            <td>配送卡ID:</td>
	                            <td>
	                            	<input type="hidden" required>
	                                <select class="editInfo" id="transcard"></select>
	                            </td>
	                        </tr>
	                        <tr>
	                            <td>仓数:</td>
	                            <td>
	                            	<input type="hidden" required>
	                                <input type="text" class="editInfo" id="store">
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
					<div class="tab-con-list">
						<table class="sub-table" id="driver_info">
	                        <tr>
	                            <td>序号</td>
	                            <td>司机姓名</td>
	                            <td>联系电话</td>
	                            <td>联系地址</td>
	                            <td>操作</td>
	                        </tr>
	                        <tr>
	                            <td><img style="cursor: pointer;" alt="添加" title="添加" src="../../resources/images/operate/addNew.png" onclick="addTr()"></td>
	                            <td></td>
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
        <c:if test="${mode=='edit'}">
            <input type="hidden" id="id" value="${car.id}">
            <div class="info-zone" style="height:410px">
	            <div class="tab-title">
					<div class="on">车辆基本信息</div>
					<div>司机信息</div>
				</div>
				<div class="tab-con">
					<div class="tab-con-list">
						<table class="base-table">
	                        <tr>
	                            <td>车牌号:</td>
	                            <td>
	                                <input type="text" class="editInfo" id="cid" value="${car.carNumber}" readonly>
	                            </td>
	                        </tr>
	                        <tr>
	                            <td>所属公司:</td>
	                            <td>
	                            	<input type="hidden" required>
	                                <select class="editInfo" id="company"></select>
	                            </td>
	                        </tr>
	                        <tr>
	                            <td>车辆类型:</td>
	                            <td>
	                            	<input type="hidden" required>
	                                <select class="editInfo" id="type">
	                                    <option value=1>自有车辆</option>
	                                    <option value=2>社会车辆</option>
	                                </select>
	                            </td>
	                        </tr>
	                        <tr>
	                            <td>车台ID:</td>
	                            <td>
	                                <input type="text" class="editInfo" id="device" value="${car.vehicleDevice.deviceId}" readonly>
	                            </td>
	                        </tr>
	                        <tr>
	                            <td>车台型号:</td>
	                            <td>
	                                <input type="tel" class="editInfo" id="dmodel" value="${car.vehicleDevice.model}" readonly>
	                            </td>
	                        </tr>
	                        <tr>
	                            <td>SIM卡号:</td>
	                            <td>
	                                <input type="text" class="editInfo" id="sim" value="${car.sim}">
	                            </td>
	                        </tr>
	                        <tr>
	                            <td>配送卡ID:</td>
	                            <td>
	                            	<input type="hidden" required>
	                                <select class="editInfo" id="transcard"></select>
	                            </td>
	                        </tr>
	                        <tr>
	                            <td>仓数:</td>
	                            <td>
	                            	<input type="hidden" required>
	                                <input type="text" class="editInfo" id="store" value="${car.storeNum}">
	                            </td>
	                        </tr>
	                        <tr>
	                            <td>备注:</td>
	                            <td>
	                                <input type="text" class="editInfo" id="remark" value="${car.remark}">
	                            </td>
	                        </tr>
	                    </table>
					</div>
					<div class="tab-con-list">
						<table class="sub-table" id="driver_info">
	                        <tr>
	                            <td>序号</td>
	                            <td width="65px">司机姓名</td>
	                            <td width="60px">联系电话</td>
	                            <td>联系地址</td>
	                            <td width="60px">操作</td>
	                        </tr>
	                        <c:forEach var="driver" items="${drivers}" varStatus="status">
	                            <tr>
	                                <td class="serialNo">${status.index+1}</td>
	                                <td class="driverIds" style="display:none">${driver.id}</td>
	                                <td>${driver.name}</td>
	                                <td>${driver.phone}</td>
	                                <td>${driver.address}</td>
	                                <td><img alt="删除" title="删除" src="../../resources/images/operate/delete.png" onclick="deleteTr(this,${driver.id})"></td>
	                            </tr>
	                        </c:forEach>
	                        <tr>
	                            <td><img style="cursor: pointer;" alt="添加" title="添加" src="../../resources/images/operate/addNew.png" onclick="addTr()"></td>
	                            <td></td>
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
        <c:if test="${mode=='view'}">
            <div class="info-zone" style="height:410px">
            	<div class="tab-title">
					<div class="on">车辆基本信息</div>
					<div>司机信息</div>
					<div>锁信息</div>
				</div>
				<div class="tab-con">
					<div class="tab-con-list">
						<table class="base-table">
	                        <tr>
	                            <td>车牌号:</td>
	                            <td>
	                                <input type="text" class="editInfo" id="cid" value="${car.carNumber}" readonly>
	                            </td>
	                        </tr>
	                        <tr>
	                            <td>所属公司:</td>
	                            <td>
	                                <input type="text" class="editInfo" value="${car.transCompany.name}" readonly>
	                            </td>
	                        </tr>
	                        <tr>
	                            <td>车辆类型:</td>
	                            <td>
	                                <input type="text" class="editInfo" value="${car.typeName}" readonly>
	                            </td>
	                        </tr>
	                        <tr>
	                            <td>车台ID:</td>
	                            <td>
	                                <input type="text" class="editInfo" id="device" value="${car.vehicleDevice.deviceId}" readonly>
	                            </td>
	                        </tr>
	                        <tr>
	                            <td>车台型号:</td>
	                            <td>
	                                <input type="tel" class="editInfo" id="dmodel" value="${car.vehicleDevice.model}" readonly>
	                            </td>
	                        </tr>
	                        <tr>
	                            <td>SIM卡号:</td>
	                            <td>
	                                <input type="text" class="editInfo" id="sim" value="${car.sim}" readonly>
	                            </td>
	                        </tr>
	                        <tr>
	                            <td>配送卡ID:</td>
	                            <td>
	                                <input type="text" class="editInfo" value="${car.transportCard.transportCardId}" readonly>
	                            </td>
	                        </tr>
	                        <tr>
	                            <td>仓数:</td>
	                            <td>
	                                <input type="text" class="editInfo" id="store" value="${car.storeNum}" readonly>
	                            </td>
	                        </tr>
	                        <tr>
	                            <td>备注:</td>
	                            <td>
	                                <input type="text" class="editInfo" id="remark" value="${car.remark}" readonly>
	                            </td>
	                        </tr>
	                    </table>
					</div>
					<div class="tab-con-list">
						<table class="sub-table">
                            <c:if test="${drivers==null || fn:length(drivers)==0}">
                                <tr><td>未配置司机</td></tr>
                            </c:if>
                            <c:if test="${drivers!=null && fn:length(drivers)!=0}">
	                        <tr>
	                            <td>序号</td>
	                            <td>司机姓名</td>
	                            <td>联系电话</td>
	                            <td>联系地址</td>
	                        </tr>
	                        <c:forEach var="driver" items="${drivers}" varStatus="status">
	                            <tr>
	                                <td>${status.index+1}</td>
	                                <td>${driver.name}</td>
	                                <td>${driver.phone}</td>
	                                <td>${driver.address}</td>
	                            </tr>
	                        </c:forEach>
                            </c:if>
	                    </table>
					</div>
					<div class="tab-con-list">
						<table class="sub-table">
							<c:if test="${locks==null || fn:length(locks)==0}">
								<tr><td>未绑定锁</td></tr>
							</c:if>
							<c:if test="${locks!=null && fn:length(locks)!=0}">
	                        <tr>
	                            <td>序号</td>
	                            <td>锁ID</td>
	                            <td>仓号</td>
	                            <td>仓位</td>
	                            <td>仓位序号</td>
	                            <td>备注</td>
	                        </tr>
	                        <c:forEach var="lock" items="${locks}" varStatus="status">
	                            <tr>
	                                <td>${lock.index}</td>
	                                <td>${lock.lockId}</td>
	                                <td>${lock.storeId}</td>
	                                <td>${lock.seatName}</td>
	                                <td>${lock.seatIndex}</td>
	                                <td>${lock.remark}</td>
	                            </tr>
	                        </c:forEach>
							</c:if>
	                    </table>
					</div>
				</div>
            </div>
        </c:if>
    </div>
</body>

</html>
</pop:Permission>