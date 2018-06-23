<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="pop" uri="/pop-tags" %>
<pop:Permission ename="viewHandset">
<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8">
    <title>手持机列表</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=center-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="../../resources/css/base.css" media="all">
    <link rel="stylesheet" href="../../resources/css/normal.css" media="all">
    <script src="../../resources/plugins/jquery-1.8.3.min.js"></script>
    <script src="../../resources/plugins/layer/layer.js"></script>
    <script src="../../resources/js/base.js"></script>
    <script src="../../resources/js/normal.js"></script>
    <script src="../../resources/js/handset/handsetList.js"></script>
    <style type="text/css">
        .handset-id {
            width: 160px;
        }
        
        .handset-gasstation {
            width: 200px;
        }
        
        .handset-director {
            width: 160px;
        }
        
        .handset-phone {
            width: 160px;
        }
        
        .handset-identity {
            width: 200px;
        }
        
        .handset-remark {
            width: 160px;
        }
        
        .handset-action {
            width: 200px;
        }
    </style>
    <script type="text/javascript">
    	<pop:Permission ename="editHandset">
	    deleteHandset = function(id) {
	        layer.confirm('删除后不可撤销，是否确认删除？', {
	            icon: 0,
	            title: ['删除', 'font-size:14px;color:#ffffff;background:#478de4;']
	        }, function() {
	            $.post("../../manage/handset/delete.do", encodeURI("id=" + id),
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
	        // 阻止事件冒泡到DOM树上
	        event.stopPropagation();
	    };
	    </pop:Permission>
	    function showList(gasStationId, pageId) {
	        var rows = $("#page_size").val();
	        var startRow = (pageId - 1) * rows;
	
	        $.post(
	            "../../manage/handset/ajaxFindForPage.do",
	            encodeURI("gasStation.id=" + gasStationId + "&pageId=" + pageId + "&startRow=" + startRow + "&rows=" + rows),
	            function(data) {
	                var gridPage = eval(data);
	
	                var maxIndex = $("#page_id option:last").index(); //获取Select最大的索引值
	                var len = maxIndex + 1 - gridPage.total;
	                if (len > 0) {
	                    for (var i = gridPage.total > 0 ? gridPage.total : 1; i < maxIndex + 1; i++) {
	                        $("#page_id option:last").remove(); //删除Select中索引值最大Option(最后一个)
	                    }
	                } else if (len < 0) {
	                    for (var i = maxIndex + 2; i <= gridPage.total; i++) {
	                        $("#page_id").append("<option value=" + i + ">" + i + "</option>"); //为Select追加一个Option下拉项
	                    }
	                }
	                $("#page_id").val(gridPage.page);
	
	                $("#page_info").html("页(" + gridPage.currentRows + "条数据)/共" + gridPage.total + "页(共" + gridPage.records + "条数据)");
	                $("#hgsid").val(gridPage.t.gasStation.id);
	                $(".table-body").html("");
	                var handsets = gridPage.dataList;
	                var tableData = "<table width='100%'>";
	                for (var i = 0; i < gridPage.currentRows; i++) {
	                    var handset = handsets[i];
	                    tableData += "<tr class='list-content' onclick=\"dispatch('edit'," + handset.id + ")\">" +
	                        "<td class=\"handset-id\">" + handset.deviceId + "</td>";
                        if (handset.gasStation.name == undefined) {
                        	tableData += "<td class=\"handset-gasstation\"></td>";
                        } else {
                        	tableData += "<td class=\"handset-gasstation\">" + handset.gasStation.name + "</td>";
                        }
                        tableData += "<td class=\"handset-director\">" + handset.director + "</td>" +
	                        "<td class=\"handset-phone\">" + handset.phone + "</td>" +
	                        "<td class=\"handset-identity\">" + handset.identityCard + "</td>" +
	                        "<td class=\"handset-remark\">" + handset.remark + "</td>" +
	                        "<td class=\"handset-action\">" +
	                        "<img class='edit-btn' src=\"../../resources/images/operate/view.png\" alt=\"查看\" title=\"查看\" onclick=\"dispatch('view'," + handset.id + ")\">" +
	                        <pop:Permission ename="editHandset">
	                        "&emsp;<img class='edit-btn' src=\"../../resources/images/operate/edit.png\" alt=\"编辑\" title=\"编辑\" onclick=\"dispatch('edit'," + handset.id + ")\">&emsp;" +
	                        "<img class='edit-btn' src=\"../../resources/images/operate/delete.png\" alt=\"删除\" title=\"删除\" onclick=\"deleteHandset(" + handset.id + ")\">" +
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
                if (XMLHttpRequest.readyState == 4 && XMLHttpRequest.status == 200 && textStatus == "parsererror") {
                    layer.confirm('登录失效，是否刷新页面重新登录？', {
                        icon: 0,
                        title: ['登录失效', 'font-size:14px;color:#ffffff;background:#478de4;']
                    }, function() {
                        location.reload(true);
                    });
                }
            });
	    }
    </script>
</head>

<body>
    <div class="container">
    	<div class="nav-addr">
	        <img src="../../resources/images/navbar/basicinfo.png"> 基本信息管理
	        <img src="../../resources/images/navbar/nav_right_12.png"> 手持机管理
	    </div>
        <div class="search-zone">
            <label for="search_text" class="search-type" id="search_type">加油站</label>
            <select class="search-text" id="search_text"></select>
            <input type="button" class="search-btn button" id="search_btn" value="查询">
            <pop:Permission ename="editHandset">
            <input type="button" class="button" id="add" value="添加" onclick="dispatch('add',0)">
            </pop:Permission>
        </div>
        <div class="data-zone">
            <div class='table-cont' id='table-cont'>
                <table width="100%">
                    <thead class="table-head">
                        <tr>
                            <th class="handset-id">手持机ID</th>
                            <th class="handset-gasstation">加油站</th>
                            <th class="handset-director">负责人</th>
                            <th class="handset-phone">联系电话</th>
                            <th class="handset-identity">身份证号</th>
                            <th class="handset-remark">备注</th>
                            <th class="handset-action">操作</th>
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
                <input type="hidden" id="hgsid">
            </div>
        </div>
    </div>
</body>

</html>
</pop:Permission>