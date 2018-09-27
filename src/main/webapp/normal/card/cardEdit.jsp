<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri='http://java.sun.com/jsp/jstl/functions' prefix='fn'%>
<%@ taglib prefix="pop" uri="/pop-tags" %>
<pop:Permission ename="viewCard">
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=center-width, initial-scale=1, maximum-scale=1">
    <title>卡管理</title>
	<script src="../../resources/js/base.js"></script>
    <link rel="stylesheet" href="../../resources/css/base.css ">
    <link rel="stylesheet" href="../../resources/css/tabEdit.css ">
    <script src="../../resources/plugins/jquery-1.8.3.min.js"></script>
    <link rel="stylesheet" href="../../resources/plugins/jtab/jtab.css ">
    <script src="../../resources/plugins/jtab/jtab.js"></script>
    <script src="../../resources/plugins/layer/layer.js"></script>
    <script src="../../resources/plugins/verify.js"></script>
    <script src="../../resources/js/card/cardEdit.js"></script>
    <style>
    	input.editInfo {
    		width: 400px;
    	}
    	
    	select.editInfo {
    		width: 414px;
            height: 28px;
    	}
    </style>
</head>

<body>
    <div class="container">
        <input type="hidden" id="mode" value="${mode}">
         <pop:Permission ename="editCard">
        <c:if test="${mode=='add'}">
            <div class="info-zone" style="height:252px">
            	<!-- <div class="tab-title">
            		<div class="on">卡基本信息</div>
            	</div> -->
            	<div class="tab-con">
            		<div class="tab-con-list">
	            		<table class="base-table">
	                        <tr>
	                            <td>卡ID:</td>
	                            <td>
	                                <input type="text" class="editInfo" id="cid" required>
	                            </td>
	                        </tr>
	                        <tr>
	                            <td>卡类型:</td>
	                            <td>
	                            	<input type="hidden" required>
	                                <select class="editInfo" id="type">
	                                        <option value=1>应急卡</option>
	                                        <option value=2>入库卡</option>
	                                        <option value=3>出库卡</option>
	                                        <option value=4>出入库卡</option>
	                                        <option value=5>普通卡</option>
	                                        <option value=6>管理卡</option>
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
	                                <input type="tel" class="editInfo" id="phone">
	                            </td>
	                        </tr>
	                        <tr>
	                            <td>身份证号:</td>
	                            <td>
	                                <input type="text" class="editInfo" id="identity">
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
            <input type="hidden" id="id" value="${card.id}">
            <div class="info-zone" style="height:296px">
            	<div class="tab-title">
            		<div class="on">卡基本信息</div>
            		<div>卡所属油库信息</div>
            		<div>卡所属加油站信息</div>
            	</div>
            	<div class="tab-con">
            		<div class="tab-con-list">
	            		<table class="base-table">
	                        <tr>
	                            <td>卡ID:</td>
	                            <td>
	                                <input type="text" class="editInfo" id="cid" value="${card.cardId}" readonly>
	                            </td>
	                        </tr>
	                        <tr>
	                            <td>卡类型:</td>
	                            <td>
	                            	<input type="hidden" id="type" value="${card.type}">
	                            	<input type="text" class="editInfo" value="${card.typeName}" readonly>
	                            </td>
	                        </tr>
	                        <tr>
	                            <td>负责人:</td>
	                            <td>
	                                <input type="text" class="editInfo" id="director" value="${card.director}">
	                            </td>
	                        </tr>
	                        <tr>
	                            <td>联系电话:</td>
	                            <td>
	                                <input type="tel" class="editInfo" id="phone" value="${card.phone}">
	                            </td>
	                        </tr>
	                        <tr>
	                            <td>身份证号:</td>
	                            <td>
	                                <input type="text" class="editInfo" id="identity" value="${card.identityCard}">
	                            </td>
	                        </tr>
	                        <tr>
	                            <td>备注:</td>
	                            <td>
	                                <input type="text" class="editInfo" id="remark" value="${card.remark}">
	                            </td>
	                        </tr>
	                    </table>
            		</div>
            		<div class="tab-con-list">
            			<table class="sub-table">
	                        <tr>
	                            <td style="color:red">请前往油库管理修改</td>
	                        </tr>
	                    </table>
            		</div>
            		<div class="tab-con-list">
	            		<table class="sub-table">
	                        <tr>
	                            <td style="color:red">请前往加油站管理修改</td>
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
            <div class="info-zone" style="height:296px">
            	<div class="tab-title">
            		<div class="on">卡基本信息</div>
            		<div>卡所属油库信息</div>
            		<div>卡所属加油站信息</div>
            	</div>
            	<div class="tab-con">
            		<div class="tab-con-list">
	            		<table class="base-table">
	                        <tr>
	                            <td>卡ID:</td>
	                            <td>
	                                <input type="text" class="editInfo" id="cid" value="${card.cardId}" readonly>
	                            </td>
	                        </tr>
	                        <tr>
	                            <td>卡类型:</td>
	                            <td>
	                                <input type="text" class="editInfo" value="${card.typeName}" readonly>
	                            </td>
	                        </tr>
	                        <tr>
	                            <td>负责人:</td>
	                            <td>
	                                <input type="text" class="editInfo" id="director" value="${card.director}" readonly>
	                            </td>
	                        </tr>
	                        <tr>
	                            <td>联系电话:</td>
	                            <td>
	                                <input type="tel" class="editInfo" id="phone" value="${card.phone}" readonly>
	                            </td>
	                        </tr>
	                        <tr>
	                            <td>身份证号:</td>
	                            <td>
	                                <input type="text" class="editInfo" id="identity" value="${card.identityCard}" readonly>
	                            </td>
	                        </tr>
	                        <tr>
	                            <td>备注:</td>
	                            <td>
	                                <input type="text" class="editInfo" id="remark" value="${card.remark}" readonly>
	                            </td>
	                        </tr>
	                    </table>
            		</div>
            		<div class="tab-con-list">
	            		<table class="sub-table">
	                        <tr>
	                            <td>序号</td>
	                            <td>油库编号</td>
	                            <td>油库名称</td>
	                        </tr>
	                        <c:forEach var="oilDepot" items="${oilDepots}" varStatus="status">
	                            <tr>
	                                <td>${status.index+1}</td>
	                                <td>${oilDepot.officialId}</td>
	                                <td>${oilDepot.name}</td>
	                            </tr>
	                        </c:forEach>
	                    </table>
            		</div>
            		<div class="tab-con-list">
	            		<table class="sub-table">
	                        <tr>
	                            <td>序号</td>
	                            <td>加油站编号</td>
	                            <td>加油站名称</td>
	                        </tr>
	                        <c:forEach var="gasStation" items="${gasStations}" varStatus="status">
	                            <tr>
	                                <td>${status.index+1}</td>
	                                <td>${gasStation.officialId}</td>
	                                <td>${gasStation.name}</td>
	                            </tr>
	                        </c:forEach>
	                    </table>
            		</div>
            	</div>
            </div>
        </c:if>
    </div>
</body>

</html>
</pop:Permission>