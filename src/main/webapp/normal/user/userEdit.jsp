<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri='http://java.sun.com/jsp/jstl/functions' prefix='fn'%>
<%@ taglib prefix="pop" uri="/pop-tags" %>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>操作员管理</title>
    <link rel="stylesheet" href="../../resources/css/base.css ">
    <link rel="stylesheet" href="../../resources/css/baseEdit.css ">
    <script src="../../resources/plugins/jquery-1.8.3.min.js"></script>
    <script src="../../resources/plugins/layer/layer.js"></script>
    <script src="../../resources/plugins/verify.js"></script>
    <script src="../../resources/js/base.js"></script>
    <script src="../../resources/js/user/userEdit.js"></script>
    <style type="text/css">
        select.editInfo {
            width: 398px;
            height: 28px;
        }
    </style>
    <script>
        $(function() {
            $.getJSON("../../manage/role/findAllRoles.do",
                function(data) {
                    for (let i = 0, len = data.length; i < len; i++) {
                    	var role = data[i];
                    	if (role.isApp > 0) {
                    		$("#appRoleId").append("<option value=" + role.id + ">" + role.name + "</option>");
                    	} else {
                       		$("#roleId").append("<option value=" + role.id + ">" + role.name + "</option>");
                    	}
                    }
                    <c:if test="${mode=='edit'}">
                        $("#roleId").val("${user.role.id}");
                        $("#appRoleId").val("${user.appRole.id}");
                    </c:if>
                }
            );
        });
    </script>
</head>

<body>
    <div class="container">
        <input type="hidden" id="mode" value="${mode}">
        <pop:Permission ename="viewUser">
        <c:if test="${mode=='view'}">
            <table>
                <tr>
                    <td>操作员ID:</td>
                    <td>
                        <input type="text" class="editInfo" id="id" value="${user.id}" readonly>
                    </td>
                </tr>
                <tr>
                    <td>账号:</td>
                    <td>
                        <input type="text" class="editInfo" id="account" value="${user.account}" readonly>
                    </td>
                </tr>
                <tr>
                    <td>角色:</td>
                    <td>
                        <input type="text" class="editInfo" id="role" value="${user.role.name}" readonly>
                    </td>
                </tr>
                <tr>
                    <td>APP角色:</td>
                    <td>
                        <input type="text" class="editInfo" id="appRole" value="${user.appRole.name}" readonly>
                    </td>
                </tr>
                <tr>
                    <td>姓名:</td>
                    <td>
                        <input type="text" class="editInfo" id="name" value="${user.name}" readonly>
                    </td>
                </tr>
                <tr>
                    <td>联系电话:</td>
                    <td>
                        <input type="text" class="editInfo" id="phone" value="${user.phone}" readonly>
                    </td>
                </tr>
                <tr>
                    <td>身份证号:</td>
                    <td>
                        <input type="text" class="editInfo" id="identityCard" value="${user.identityCard}" readonly>
                    </td>
                </tr>
                <tr>
                    <td>备注:</td>
                    <td>
                        <input type="text" class="editInfo" id="remark" value="${user.remark}" readonly>
                    </td>
                </tr>
            </table>
        </c:if>
        <pop:Permission ename="editUser">
        <c:if test="${mode=='add'}">
            <table>
                <tr>
                    <td>账号:</td>
                    <td>
                        <input type="text" class="editInfo" id="account" required>
                    </td>
                </tr>
                <tr>
                    <td>角色:</td>
                    <td>
                    	<input type="hidden" required>
                        <select class="editInfo" id="roleId"></select>
                    </td>
                </tr>
                <tr>
                    <td>APP角色:</td>
                    <td>
                    	<input type="hidden" required>
                        <select class="editInfo" id="appRoleId">
                        	<option value=0>不授予APP角色</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>姓名:</td>
                    <td>
                        <input type="text" class="editInfo" id="name" required>
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
                        <input type="text" class="editInfo" id="identityCard">
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
            <table>
                <tr>
                    <td>操作员ID:</td>
                    <td>
                        <input type="text" class="editInfo" id="id" value="${user.id}" readonly>
                    </td>
                </tr>
                <tr>
                    <td>账号:</td>
                    <td>
                        <input type="text" class="editInfo" id="account" value="${user.account}" readonly>
                    </td>
                </tr>
                <tr>
                    <td>角色:</td>
                    <td>
                    	<input type="hidden" required>
                        <select class="editInfo" id="roleId"></select>
                    </td>
                </tr>
                <tr>
                    <td>APP角色:</td>
                    <td>
                    	<input type="hidden" required>
                        <select class="editInfo" id="appRoleId">
                        	<option value=0>不授予APP角色</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>姓名:</td>
                    <td>
                        <input type="text" class="editInfo" id="name" value="${user.name}" required>
                    </td>
                </tr>
                <tr>
                    <td>联系电话:</td>
                    <td>
                        <input type="tel" class="editInfo" id="phone" value="${user.phone}">
                    </td>
                </tr>
                <tr>
                    <td>身份证号:</td>
                    <td>
                        <input type="text" class="editInfo" id="identityCard" value="${user.identityCard}">
                    </td>
                </tr>
                <tr>
                    <td>备注:</td>
                    <td>
                        <input type="text" class="editInfo" id="remark" value="${user.remark}">
                    </td>
                </tr>
            </table>
            <div class="oper-zone">
                <input type="button" id="cancel" value="取消">
                <input type="button" id="confirm" value="确认">
            </div>
        </c:if>
        </pop:Permission>
        </pop:Permission>
        <c:if test="${mode=='info'}">
            <table>
                <tr>
                    <td>操作员ID:</td>
                    <td>
                        <input type="text" class="editInfo" id="id" value="${user.id}" readonly>
                    </td>
                </tr>
                <tr>
                    <td>账号:</td>
                    <td>
                        <input type="text" class="editInfo" id="account" value="${user.account}" readonly>
                    </td>
                </tr>
                <tr>
                    <td>角色:</td>
                    <td>
                        <input type="hidden" id="roleId" value="${user.role.id}">
                        <input type="text" class="editInfo" id="role" value="${user.role.name}" readonly>
                    </td>
                </tr>
                <tr>
                    <td>APP角色:</td>
                    <td>
                        <input type="hidden" id="appRoleId" value="${user.appRole.id}">
                        <input type="text" class="editInfo" id="appRole" value="${user.appRole.name}" readonly>
                    </td>
                </tr>
                <tr>
                    <td>姓名:</td>
                    <td>
                        <input type="text" class="editInfo" id="name" value="${user.name}" required>
                    </td>
                </tr>
                <tr>
                    <td>联系电话:</td>
                    <td>
                        <input type="tel" class="editInfo" id="phone" value="${user.phone}">
                    </td>
                </tr>
                <tr>
                    <td>身份证号:</td>
                    <td>
                        <input type="text" class="editInfo" id="identityCard" value="${user.identityCard}">
                    </td>
                </tr>
                <tr>
                    <td>备注:</td>
                    <td>
                        <input type="text" class="editInfo" id="remark" value="${user.remark}">
                    </td>
                </tr>
            </table>
            <div class="oper-zone">
                <input type="button" id="cancel" value="取消">
                <input type="button" id="confirm" value="确认">
            </div>
        </c:if>
        <c:if test="${mode=='pwd'}">
            <input type="hidden" id="id" class="editInfo" value="${user.id}">
            <table>
                <tr>
                    <td>原密码</td>
                    <td><input type="password" class="editInfo" id="oldPwd" required></td>
                </tr>
                <tr>
                    <td>新密码</td>
                    <td><input type="password" class="editInfo" id="pwd" required></td>
                </tr>
                <tr>
                    <td>确认密码</td>
                    <td><input type="password" class="editInfo" id="repwd" required></td>
                </tr>
            </table>
            <div class="oper-zone">
                <input type="button" id="cancel" value="取消">
                <input type="button" id="confirm" value="确认">
            </div>
        </c:if>
    </div>
</body>

</html>