<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri='http://java.sun.com/jsp/jstl/functions' prefix='fn'%>
<%@ taglib prefix="pop" uri="/pop-tags" %>
<pop:Permission ename="viewRole">
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>角色管理</title>
    <link rel="stylesheet" href="../../resources/css/base.css">
    <link rel="stylesheet" href="../../resources/css/tabEdit.css">
    <link rel="stylesheet" href="../../resources/plugins/ztree/css/metroStyle/metroStyle.css">
    <script src="../../resources/plugins/jquery-1.8.3.min.js"></script>
    <link rel="stylesheet" href="../../resources/plugins/jtab/jtab.css ">
    <script src="../../resources/plugins/jtab/jtab.js"></script>
    <script src="../../resources/plugins/layer/layer.js"></script>
    <script src="../../resources/plugins/ztree/js/jquery.ztree.all.min.js"></script>
    <script src="../../resources/plugins/verify.js"></script>
    <script src="../../resources/js/base.js"></script>
    <script src="../../resources/js/role/roleEdit.js"></script>
    <style type="text/css">
        .tree-box {
            /* border: 1px solid #d7dbe2; */
            padding: 5px;
            font-size: 14px;
        }
        
        .tab-con {
        	height: 242px;
        	overflow: auto;
        }
        
        select#app {
        	width: 378px;
        	height: 28px;
        }
    </style>
</head>

<body>
    <div class="container">
        <input type="hidden" id="mode" value="${mode}">
        <c:if test="${mode=='view'}">
            <div class="info-zone">
	            <div class="tab-title">
					<div class="on">角色基本信息</div>
					<div>角色权限信息</div>
				</div>
				<div class="tab-con">
					<div class="tab-con-list">
						<table>
	                        <tr>
	                            <td>角色ID:</td>
	                            <td>
	                                <input type="text" class="editInfo" id="id" value="${role.id}" readonly>
	                            </td>
	                        </tr>
	                        <tr>
	                            <td>角色名称:</td>
	                            <td>
	                                <input type="text" class="editInfo" id="name" value="${role.name}" readonly>
	                            </td>
	                        </tr>
	                        <tr>
	                            <td>是否APP角色:</td>
	                            <td>
	                            	<c:if test="${role.isApp==0}">
	                                <input type="text" class="editInfo" id="app" value="否" readonly>
	                            	</c:if>
	                            	<c:if test="${role.isApp==1}">
	                                <input type="text" class="editInfo" id="app" value="是" readonly>
	                            	</c:if>
	                            </td>
	                        </tr>
	                        <tr>
	                            <td>备注:</td>
	                            <td>
	                                <input type="text" class="editInfo" id="remark" value="${role.remark}" readonly>
	                            </td>
	                        </tr>
	                    </table>
					</div>
					<div class="tab-con-list">
						<div class="tree-box">
	                        <ul id="treeDemo" class="ztree"></ul>
	                    </div>
					</div>
				</div>
            </div>
        </c:if>
        <pop:Permission ename="editRole">
        <c:if test="${mode=='add'}">
            <div class="info-zone">
            	<div class="tab-title">
					<div class="on">角色基本信息</div>
					<div>角色权限信息</div>
				</div>
				<div class="tab-con">
					<div class="tab-con-list">
						<table>
	                        <tr>
	                            <td>角色名称:</td>
	                            <td>
	                                <input type="text" class="editInfo" id="name" required>
	                            </td>
	                        </tr>
	                        <tr>
								<td>是否APP角色:</td>
								<td>
									<select class="editInfo" id="app">
										<option value="0">否</option>
										<option value="1">是</option>
									</select>
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
						<div class="tree-box">
	                        <ul id="treeDemo" class="ztree"></ul>
	                    </div>
					</div>
				</div>
            </div>
            <div class="oper-zone">
                <input type="button" id="cancel" value="取消">
                <input type="button" id="confirm" value="确认">
            </div>
        </c:if>
        <c:if test="${mode=='edit'}">
            <div class="info-zone">
            	<div class="tab-title">
					<div class="on">角色基本信息</div>
					<div>角色权限信息</div>
				</div>
				<div class="tab-con">
					<div class="tab-con-list">
						<table>
	                        <tr>
	                            <td>角色ID:</td>
	                            <td>
	                                <input type="text" class="editInfo" id="id" value="${role.id}" readonly>
	                            </td>
	                        </tr>
	                        <tr>
	                            <td>角色名称:</td>
	                            <td>
	                                <input type="text" class="editInfo" id="name" value="${role.name}" readonly>
	                            </td>
	                        </tr>
	                        <tr>
								<td>是否APP角色:</td>
								<td>
									<select class="editInfo" id="app" >
										<c:if test="${role.isApp==0}">
										<option value="0" selected>否</option>
										<option value="1">是</option>
										</c:if>
										<c:if test="${role.isApp==1}">
										<option value="0">否</option>
										<option value="1" selected>是</option>
										</c:if>
									</select>
								</td>
							</tr>
	                        <tr>
	                            <td>备注:</td>
	                            <td>
	                                <input type="text" class="editInfo" id="remark" value="${role.remark}">
	                            </td>
	                        </tr>
	                    </table>
					</div>
					<div class="tab-con-list">
						<div class="tree-box">
	                        <ul id="treeDemo" class="ztree"></ul>
	                    </div>
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