<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri='http://java.sun.com/jsp/jstl/functions' prefix='fn'%>
<%@ taglib prefix="pop" uri="/pop-tags" %>
<pop:Permission ename="viewCar">
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
	<meta name="renderer" content="webkit">
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	<meta name="viewport" content="width=center-width, initial-scale=1, maximum-scale=1">
    <title>车辆信息管理</title>
	<script src="../../resources/js/base.js"></script>
    <link rel="stylesheet" href="../../resources/css/base.css ">
    <link rel="stylesheet" href="../../resources/css/tabEdit.css ">
    <link rel="stylesheet" href="../../resources/plugins/combo/jquery.combo.select.css">
    <script src="../../resources/plugins/jquery-1.8.3.min.js"></script>
    <link rel="stylesheet" href="../../resources/plugins/jtab/jtab.css ">
    <script src="../../resources/plugins/jtab/jtab.js"></script>
    <script src="../../resources/plugins/layer/layer.js"></script>
    <script src="../../resources/plugins/combo/jquery.combo.select.js"></script>
    <script src="../../resources/plugins/verify.js"></script>
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
    <c:if test="${mode!='veiw'}">
    <script>
        $(function() {
            $.getJSON("../../manage/transcom/getCompanyList.do",
                function(companies) {
                    var len = companies.length;
                    if (len === 0) {
                        $("#company").append("<option value=0>运输公司未配置</option>");
                        return;
                    }
                    var comHtml = "<option value=0></option>";
                    for (var i = 0; i < len; i++) {
                        var company = companies[i];
                        comHtml += "<option value=" + company.id + ">" + company.name + "</option>";
                    }
                    $("#company").append(comHtml);
                    <c:if test="${mode=='edit'}">
                        $("#company").val("${car.transCompany.id}");
                    </c:if>
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
            $.getJSON("../../manage/transcard/findUnusedTranscards.do",
                function(data) {
                    var transcardHtml = "";
                        // "<option value=0>暂不设置配送卡</option>";
            		<c:if test="${mode=='edit' && car.transportCard.transportCardId != 0}">
                        transcardHtml += "<option value='${car.transportCard.transportCardId}'>${car.transportCard.transportCardId}</option>";
                    </c:if>
                    for (var i = 0, len = data.length; i < len; i++) {
                        var transcard = data[i];
                        transcardHtml += "<option value='" + transcard.transportCardId + "'>" + transcard.transportCardId + "</option>";
                    }
                    var $transcard = $("#transcard");
                    $transcard.append(transcardHtml);
                    $transcard.comboSelect();
                    $transcard.hide();
                    $transcard.closest(".combo-select").css({
                        width: '398px',
                        height: '28px',
                        "margin-bottom": "0px"
                    });
                    $transcard.siblings(".combo-dropdown").css("max-height", "165px");
                    $transcard.siblings(".combo-input").height(2);
                    <c:if test="${mode=='edit'}">
                        var transportCardId = '${car.transportCard.transportCardId}';
                        if (transportCardId == '0') {
                            transportCardId = "";
                        }
                        $transcard.val(transportCardId);
                        var $ul = $transcard.next().next();
                        $ul.children().each(function () {
                            if (transportCardId == $(this).attr("data-value")) {
                                $(this).addClass("option-selected option-hover");
                            } else {
                                $(this).removeClass("option-selected option-hover");
                            }
                        });
                        $ul.next().val(transportCardId);
                    </c:if>
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
        });
    </script>
    </c:if>
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
	                                <select class="editInfo" id="transcard">
                                        <option value="">配送卡ID</option>
                                    </select>
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
	                                <select class="editInfo" id="transcard">
                                        <option value="">配送卡ID</option>
                                    </select>
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
					<div class="tab-con-list">
						<table class="sub-table" style="cursor: default;">
							<c:if test="${locks==null || fn:length(locks)==0}">
								<tr><td>未绑定锁</td></tr>
							</c:if>
							<c:if test="${locks!=null && fn:length(locks)!=0}">
								<tr>
                                    <td width="74px">锁设备ID</td>
                                    <td width="90px">仓位</td>
                                    <td width="62px">允许开锁</td>
                                    <td width="78px">绑定状态</td>
                                    <td width="88px">设备备注</td>
                                    <td width="88px">备注</td>
								</tr>
								<c:forEach var="lock" items="${locks}" varStatus="status">
									<tr class="locks">
										<td style="display: none;">${lock.id}</td>
										<td>${lock.lockId}</td>
										<td>仓${lock.storeId}-${lock.seatName}-${lock.seatIndex}</td>
										<c:if test="${lock.allowOpen==1}">
											<td>否</td>
										</c:if>
										<c:if test="${lock.allowOpen==2}">
											<td>是</td>
										</c:if>
										<c:if test="${lock.bindStatus==1}">
											<td>未同步绑定</td>
										</c:if>
										<c:if test="${lock.bindStatus==2}">
											<td>已同步绑定</td>
										</c:if>
                                        <td>${lock.deviceRemark}</td>
										<td>
                                            <input type="text" value="${lock.remark}" style="width: 96px; height: 20px">
                                        </td>
									</tr>
								</c:forEach>
							</c:if>
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
								<td width="74px">锁设备ID</td>
								<td width="90px">仓位</td>
								<td width="62px">允许开锁</td>
								<td width="78px">绑定状态</td>
								<td width="88px">设备备注</td>
								<td width="88px">备注</td>
	                        </tr>
	                        <c:forEach var="lock" items="${locks}" varStatus="status">
	                            <tr>
	                                <td>${lock.lockId}</td>
	                                <td>仓${lock.storeId}-${lock.seatName}-${lock.seatIndex}</td>
									<c:if test="${lock.allowOpen==1}">
                                        <td>否</td>
                                    </c:if>
									<c:if test="${lock.allowOpen==2}">
                                        <td>是</td>
                                    </c:if>
									<c:if test="${lock.bindStatus==1}">
                                        <td>未同步绑定</td>
                                    </c:if>
									<c:if test="${lock.bindStatus==2}">
                                        <td>已同步绑定</td>
                                    </c:if>
	                                <td>${lock.deviceRemark}</td>
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