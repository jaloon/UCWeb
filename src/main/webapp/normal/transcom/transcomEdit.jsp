<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri='http://java.sun.com/jsp/jstl/functions' prefix='fn'%>
<%@ taglib prefix="pop" uri="/pop-tags" %>
<pop:Permission ename="viewTranscom">
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=center-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>运输公司管理</title>
    <link rel="stylesheet" href="../../resources/css/base.css ">
    <link rel="stylesheet" href="../../resources/css/baseEdit.css ">
    <script src="../../resources/plugins/jquery-1.8.3.min.js"></script>
    <script src="../../resources/plugins/layer/layer.js"></script>
    <script src="../../resources/plugins/verify.js"></script>
    <script src="../../resources/js/base.js"></script>
    <script src="../../resources/js/transcom/transcomEdit.js"></script>
    <style type="text/css">
        #superior {
            width: 398px;
            height: 28px;
        }
    </style>
    <script type="text/javascript">
        $(function() {
            $.getJSON("../../manage/transcom/getCompanyList.do",
                function(data) {
                    $("#superior").append("<option value=0>无上级公司</option>");
                    var companies = eval(data);
                    var len = companies.length;
                    for (var i = 0; i < len; i++) {
                        var company = companies[i];
                        if (company.id != "${transcom.id}") {
                            $("#superior").append("<option value=" + company.id + ">" + company.name + "</option>");
                        }
                        <c:if test="${mode=='edit'}">
                            $("#superior").val("${transcom.superior.id}");
                        </c:if>
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
        });
    </script>
</head>

<body>
    <div class="container">
        <input type="hidden" id="mode" value="${mode}">
        <c:if test="${mode=='view'}">
            <table>
                <tr>
                    <td>公司ID:</td>
                    <td>
                        <input type="text" class="editInfo" id="id" value="${transcom.id}" readonly>
                    </td>
                </tr>
                <tr>
                    <td>公司名称:</td>
                    <td>
                        <input type="text" class="editInfo" id="name" value="${transcom.name}" readonly>
                    </td>
                </tr>
                <tr>
                    <td>联系地址:</td>
                    <td>
                        <input type="tel" class="editInfo" id="address" value="${transcom.address}" readonly>
                    </td>
                </tr>
                <tr>
                    <td>负责人:</td>
                    <td>
                        <input type="tel" class="editInfo" id="director" value="${transcom.director}" readonly>
                    </td>
                </tr>
                <tr>
                    <td>联系电话:</td>
                    <td>
                        <input type="text" class="editInfo" id="phone" value="${transcom.phone}" readonly>
                    </td>
                </tr>
                <tr>
                    <td>上级公司:</td>
                    <td>
                        <input type="text" class="editInfo" value="${transcom.superior.name}" readonly>
                    </td>
                </tr>
                <tr>
                    <td>备注:</td>
                    <td>
                        <input type="text" class="editInfo" id="remark" value="${transcom.remark}" readonly>
                    </td>
                </tr>
            </table>
        </c:if>
        <pop:Permission ename="editTranscom">
        <c:if test="${mode=='add'}">
            <table>
                <tr>
                    <td>公司名称:</td>
                    <td>
                        <input type="text" class="editInfo" id="name" required>
                    </td>
                </tr>
                <tr>
                    <td>联系地址:</td>
                    <td>
                        <input type="tel" class="editInfo" id="address">
                    </td>
                </tr>
                <tr>
                    <td>负责人:</td>
                    <td>
                        <input type="tel" class="editInfo" id="director">
                    </td>
                </tr>
                <tr>
                    <td>联系电话:</td>
                    <td>
                        <input type="text" class="editInfo" id="phone">
                    </td>
                </tr>
                <tr>
                    <td>上级公司:</td>
                    <td>
                    	<input type="hidden" required>
                        <select class="editInfo" id="superior"></select>
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
                    <td>公司ID:</td>
                    <td>
                        <input type="text" class="editInfo" id="id" value="${transcom.id}" readonly>
                    </td>
                </tr>
                <tr>
                    <td>公司名称:</td>
                    <td>
                        <input type="text" class="editInfo" id="name" value="${transcom.name}" readonly>
                    </td>
                </tr>
                <tr>
                    <td>联系地址:</td>
                    <td>
                        <input type="tel" class="editInfo" id="address" value="${transcom.address}">
                    </td>
                </tr>
                <tr>
                    <td>负责人:</td>
                    <td>
                        <input type="tel" class="editInfo" id="director" value="${transcom.director}">
                    </td>
                </tr>
                <tr>
                    <td>联系电话:</td>
                    <td>
                        <input type="text" class="editInfo" id="phone" value="${transcom.phone}">
                    </td>
                </tr>
                <tr>
                    <td>上级公司:</td>
                    <td>
                    	<input type="hidden" required>
                        <select class="editInfo" id="superior"></select>
                    </td>
                </tr>
                <tr>
                    <td>备注:</td>
                    <td>
                        <input type="text" class="editInfo" id="remark" value="${transcom.remark}">
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