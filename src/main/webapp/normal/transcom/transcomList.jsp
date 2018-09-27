<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="pop" uri="/pop-tags" %>
<pop:Permission ename="viewTranscom">
<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8">
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=center-width, initial-scale=1, maximum-scale=1">
    <title>运输公司列表</title>
    <script src="../../resources/js/base.js"></script>
    <link rel="stylesheet" href="../../resources/css/base.css" media="all">
    <link rel="stylesheet" href="../../resources/css/normal.css" media="all">
    <script src="../../resources/plugins/jquery-1.8.3.min.js"></script>
    <script src="../../resources/plugins/layer/layer.js"></script>
    <script src="../../resources/plugins/verify.js"></script>
    <script src="../../resources/js/normal.js"></script>
    <script src="../../resources/js/transcom/transcomList.js"></script>
    <style type="text/css">
        /*.transcom-id {*/
            /*width: 100px;*/
        /*}*/
        
        /*.transcom-name {*/
            /*width: 160px;*/
        /*}*/
        
        /*.transcom-address {*/
            /*width: 160px;*/
        /*}*/
        
        /*.transcom-director {*/
            /*width: 120px;*/
        /*}*/
        
        /*.transcom-phone {*/
            /*width: 160px;*/
        /*}*/
        
        /*.transcom-superior {*/
            /*width: 160px;*/
        /*}*/
        
        /*.transcom-remark {*/
            /*width: 100px;*/
        /*}*/
        
        /*.transcom-action {*/
            /*width: 200px;*/
        /*}*/
    </style>
    <script type="text/javascript">
    <pop:Permission ename="editTranscom">
    deleteTranscom = function(id) {
        layer.confirm('删除后不可撤销，是否确认删除？', {
            icon: 0,
            title: ['删除', 'font-size:14px;color:#ffffff;background:#478de4;']
        }, function() {
            $.post("../../manage/transcom/delete.do", encodeURI("id=" + id),
                function(data) {
                    if ("success" == data.msg) {
                        layer.msg('删除成功！', { icon: 1, time: 500 }, function() {
                            refreshPage();
                        });
                    } else {
                        layer.msg('删除失败！', { icon: 2, time: 500 });
                    }
                },
                "json"
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
        // 阻止事件冒泡到DOM树上
        stopPropagation(event);
    };
    </pop:Permission>
    function showList(transcomName, superiorId, pageId) {
        var rows = $("#page_size").val();
        var startRow = (pageId - 1) * rows;
        var loadLayer = layer.load();
        $.post(
            "../../manage/transcom/ajaxFindForPage.do",
            encodeURI("name=" + transcomName + "&superior.id=" + superiorId + "&pageId=" + pageId + "&startRow=" + startRow + "&rows=" + rows),
            function(gridPage) {
                layer.close(loadLayer);
                var pageCount = gridPage.total;
                if (pageCount > 1) {
                    var pageOpts = "";
                    for (var i = 1; i <= pageCount; i++) {
                        pageOpts += "<option value=" + i + ">" + i + "</option>";
                    }
                    $("#page_id").html(pageOpts);
                    $("#page_id").val(gridPage.page);
                } else {
                    $("#page_id").html("<option value='1'>1</option>");
                }
                $("#page_info").html("页(" + gridPage.currentRows + "条数据)/共" + pageCount + "页(共" + gridPage.records + "条数据)");
                $("#cname").val(gridPage.t.name);
                $("#supid").val(gridPage.t.superior.id);
                $(".table-body").html("");
                var transcoms = gridPage.dataList;
                var tableData = "<table width='100%'>";
                for (var i = 0; i < gridPage.currentRows; i++) {
                    var transcom = transcoms[i];
                    tableData += "<tr class='list-content' onclick=\"dispatch('edit'," + transcom.id + ")\">" +
                        "<td class=\"transcom-id\">" + transcom.id + "</td>" +
                        "<td class=\"transcom-name\">" + transcom.name + "</td>" +
                        "<td class=\"transcom-address\">" + transcom.address + "</td>" +
                        "<td class=\"transcom-director\">" + transcom.director + "</td>" +
                        "<td class=\"transcom-phone\">" + transcom.phone + "</td>";
                    if (transcom.superior.name == undefined) {
                    	tableData += "<td class=\"transcom-phone\">无上级公司</td>";
                    } else {
                    	tableData += "<td class=\"transcom-phone\">" + transcom.superior.name + "</td>";
                    }
                    tableData += "<td class=\"transcom-remark\">" + transcom.remark + "</td>" +
                        "<td class=\"transcom-action\">" +
                        "<img class='edit-btn' src=\"../../resources/images/operate/view.png\" alt=\"查看\" title=\"查看\" onclick=\"dispatch('view'," + transcom.id + ")\">" +
                        <pop:Permission ename="editTranscom">
                        "&emsp;<img class='edit-btn' src=\"../../resources/images/operate/edit.png\" alt=\"编辑\" title=\"编辑\" onclick=\"dispatch('edit'," + transcom.id + ")\">&emsp;" +
                        "<img class='edit-btn' src=\"../../resources/images/operate/delete.png\" alt=\"删除\" title=\"删除\" onclick=\"deleteTranscom(" + transcom.id + ")\">" +
                        </pop:Permission>
                        "</td>" +
                        "</tr>";
                }
                tableData += "</table>";
                tableData = tableData.replace(/>null</g, "><");
                $(".table-body").html(tableData);
            },
            "json"
        ).error(function (XMLHttpRequest, textStatus, errorThrown) {
            layer.close(loadLayer);
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
    }
    </script>
</head>

<body>
    <div class="container">
    	<div class="nav-addr">
	        <img src="../../resources/images/navbar/basicinfo.png"> 基本信息管理
	        <img src="../../resources/images/navbar/nav_right_12.png"> 运输公司管理
	    </div>
        <div class="search-zone">
            <select class="search-type" id="search_type">
                        <option value="0">查询类型</option>
                        <option value="1">运输公司名称</option>
                        <option value="2">上级公司名称</option>
                    </select>
            <input type="text" class="search-text" id="search_text">
            <input type="button" class="search-btn button" id="search_btn" value="查询">
            <pop:Permission ename="editTranscom">
            <input type="button" class="button" id="add" value="添加" onclick="dispatch('add',0)">
            </pop:Permission>
        </div>
        <div class="data-zone">
            <div class='table-box'>
                <table class="table-cont" width="100%">
                    <thead class="table-head">
                        <tr>
                            <th class="transcom-id">运输公司ID</th>
                            <th class="transcom-name">运输公司名称</th>
                            <th class="transcom-address">联系地址</th>
                            <th class="transcom-director">负责人</th>
                            <th class="transcom-phone">联系电话</th>
                            <th class="transcom-superior">上级公司</th>
                            <th class="transcom-remark">备注</th>
                            <th class="transcom-action">操作</th>
                        </tr>
                    </thead>
                    <tbody class="table-body"></tbody>
                </table>
            </div>
            <div class="page">
                <span>第</span>
                <select id="page_id">
                    <option value="1">1</option>
                </select>
                <span id="page_info">页(几条数据)/共几页(共几条数据)</span>
                <select id="page_size">
                    <option value="25">25条/页</option>
                    <option value="50">50条/页</option>
                    <option value="100">100条/页</option>
                    <option value="150">150条/页</option>
                </select>
                <input type="hidden" id="cname">
                <input type="hidden" id="supid">
            </div>
        </div>
    </div>
</body>

</html>
</pop:Permission>