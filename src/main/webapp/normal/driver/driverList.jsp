<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="pop" uri="/pop-tags" %>
<pop:Permission ename="viewDriver">
<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8">
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=center-width, initial-scale=1, maximum-scale=1">
    <title>司机列表</title>
    <script src="../../resources/js/base.js"></script>
    <link rel="stylesheet" href="../../resources/css/base.css" media="all">
    <link rel="stylesheet" href="../../resources/css/normal.css" media="all">
    <script src="../../resources/plugins/jquery-1.8.3.min.js"></script>
    <script src="../../resources/plugins/layer/layer.js"></script>
    <script src="../../resources/plugins/verify.js"></script>
    <script src="../../resources/js/normal.js"></script>
    <script src="../../resources/js/driver/driverList.js"></script>
    <style type="text/css">
        /*.driver-name {*/
            /*width: 160px;*/
        /*}*/
        
        /*.driver-phone {*/
            /*width: 200px;*/
        /*}*/
        
        /*.driver-identity {*/
            /*width: 120px;*/
        /*}*/
        
        /*.driver-address {*/
            /*width: 120px;*/
        /*}*/
        
        /*.driver-remark {*/
            /*width: 100px;*/
        /*}*/
        
        /*.driver-action {*/
            /*width: 200px;*/
        /*}*/
    </style>
    <script type="text/javascript">
    	<pop:Permission ename="editDriver">
	    deleteDriver = function(id) {
	        layer.confirm('删除后不可撤销，是否确认删除？', {
	            icon: 0,
	            title: ['删除', 'font-size:14px;color:#ffffff;background:#478de4;']
	        }, function() {
	            $.post("../../manage/driver/delete.do", encodeURI("id=" + id),
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
	    function showList(driverName, pageId) {
	        var rows = $("#page_size").val();
	        var startRow = (pageId - 1) * rows;
            var loadLayer = layer.load();
	        $.post(
	            "../../manage/driver/ajaxFindForPage.do",
	            encodeURI("name=" + driverName + "&pageId=" + pageId + "&startRow=" + startRow + "&rows=" + rows),
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
	                $("#dname").val(gridPage.t.name);
	                $(".table-body").html("");
	                var drivers = gridPage.dataList;
	                var tableData = "<table width='100%'>";
	                for (var i = 0; i < gridPage.currentRows; i++) {
	                    var driver = drivers[i];
	                    tableData += "<tr class='list-content' onclick=\"dispatch('edit'," + driver.id + ")\">" +
	                        "<td class=\"driver-name\">" + driver.name + "</td>" +
	                        "<td class=\"driver-phone\">" + driver.phone + "</td>" +
	                        "<td class=\"driver-identity\">" + driver.identityCard + "</td>" +
	                        "<td class=\"driver-address\">" + driver.address + "</td>" +
	                        "<td class=\"driver-remark\">" + driver.remark + "</td>" +
	                        "<td class=\"driver-action\">" +
	                        "<img class='edit-btn' src=\"../../resources/images/operate/view.png\" alt=\"查看\" title=\"查看\" onclick=\"dispatch('view'," + driver.id + ")\">" +
	                        <pop:Permission ename="editDriver">
	                        "&emsp;<img class='edit-btn' src=\"../../resources/images/operate/edit.png\" alt=\"编辑\" title=\"编辑\" onclick=\"dispatch('edit'," + driver.id + ")\">&emsp;" +
	                        "<img class='edit-btn' src=\"../../resources/images/operate/delete.png\" alt=\"删除\" title=\"删除\" onclick=\"deleteDriver(" + driver.id + ")\">" +
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
	        <img src="../../resources/images/navbar/nav_right_12.png"> 司机管理
	    </div>
        <div class="search-zone">
            <label for="search_text" class="search-type" id="search_type">司机姓名</label>
            <input type="text" class="search-text" id="search_text">
            <input type="button" class="search-btn button" id="search_btn" value="查询">
            <pop:Permission ename="editDriver">
            <input type="button" class="button" id="add" value="添加" onclick="dispatch('add',0)">
            </pop:Permission>
        </div>
        <div class="data-zone">
            <div class='table-box'>
                <table class="table-cont" width="100%">
                    <thead class="table-head">
                        <tr>
                            <th class="driver-name">司机姓名</th>
                            <th class="driver-phone">联系电话</th>
                            <th class="driver-identity">身份证号</th>
                            <th class="driver-address">联系地址</th>
                            <th class="driver-remark">备注</th>
                            <th class="driver-action">操作</th>
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
                <input type="hidden" id="dname">
            </div>
        </div>
    </div>
</body>

</html>
</pop:Permission>