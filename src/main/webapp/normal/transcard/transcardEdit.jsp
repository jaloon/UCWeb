<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri='http://java.sun.com/jsp/jstl/functions' prefix='fn'%>
<%@ taglib prefix="pop" uri="/pop-tags" %>
<pop:Permission ename="viewTranscard">
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=center-width, initial-scale=1, maximum-scale=1">
    <title>配送卡管理</title>
    <script src="../../resources/js/base.js"></script>
    <link rel="stylesheet" href="../../resources/css/base.css ">
    <link rel="stylesheet" href="../../resources/css/baseEdit.css ">
    <script src="../../resources/plugins/jquery-1.8.3.min.js"></script>
    <script src="../../resources/plugins/layer/layer.js"></script>
    <script src="../../resources/plugins/verify.js"></script>
    <script src="../../resources/js/transcard/transcardEdit.js"></script>
</head>

<body>
    <div class="container">
        <input type="hidden" id="mode" value="${mode}">
        <c:if test="${mode=='view'}">
            <table>
                <tr>
                    <td>配送卡ID:</td>
                    <td>
                        <input type="text" class="editInfo" id="cid" value="${transcard.transportCardId}" readonly>
                    </td>
                </tr>
                <tr>
                    <td>车牌号:</td>
                    <td>
                        <input type="text" class="editInfo" id="carno" value="${transcard.carNumber}" readonly>
                    </td>
                </tr>
                <tr>
                    <td>备注:</td>
                    <td>
                        <input type="text" class="editInfo" id="remark" value="${transcard.remark}" readonly>
                    </td>
                </tr>
            </table>
        </c:if>
        <pop:Permission ename="editTranscard">
        <c:if test="${mode=='add'}">
            <table>
                <tr>
                    <td>配送卡ID:</td>
                    <td>
                        <input type="text" class="editInfo" id="cid" value="${transcard.transportCardId}" required>
                    </td>
                </tr>
                <!-- <tr>
                    <td>车牌号:</td>
                    <td>
                        <input type="text" class="editInfo" id="carno" value="${transcard.carNumber}">
                    </td>
                </tr> -->
                <tr>
                    <td>备注:</td>
                    <td>
                        <input type="text" class="editInfo" id="remark" value="${transcard.remark}">
                    </td>
                </tr>
            </table>
            <div class="oper-zone">
                <input type="button" id="cancel" value="取消">
                <input type="button" id="confirm" value="确认">
            </div>
        </c:if>
        <c:if test="${mode=='edit'}">
            <input type="hidden" id="id" value="${transcard.id}">
            <table>
                <tr>
                    <td>配送卡ID:</td>
                    <td>
                        <input type="text" class="editInfo" id="cid" value="${transcard.transportCardId}" readonly>
                    </td>
                </tr>
                <tr>
                    <td>车牌号:</td>
                    <td>
                        <input type="text" class="editInfo" id="carno" value="${transcard.carNumber}" readonly>
                    </td>
                </tr>
                <tr>
                    <td>备注:</td>
                    <td>
                        <input type="text" class="editInfo" id="remark" value="${transcard.remark}">
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