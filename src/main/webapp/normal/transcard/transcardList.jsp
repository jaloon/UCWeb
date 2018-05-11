<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="pop" uri="/pop-tags" %>
<pop:Permission ename="viewTranscard">
<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8">
    <title>配送卡列表</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=center-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="../../resources/css/base.css" media="all">
    <link rel="stylesheet" href="../../resources/css/normal.css" media="all">
    <script src="../../resources/plugins/jquery-1.8.3.min.js"></script>
    <script src="../../resources/plugins/layer/layer.js"></script>
    <script src="../../resources/plugins/verify.js"></script>
    <script src="../../resources/js/base.js"></script>
    <script src="../../resources/js/normal.js"></script>
    <script src="../../resources/js/transcard/transcardList.js"></script>
    <style type="text/css">
        .transcard-id {
            width: 200px;
        }
        
        .transcard-carno {
            width: 200px;
        }
        
        .transcard-remark {
            width: 200px;
        }
        
        .transcard-action {
            width: 200px;
        }
    </style>
    <script type="text/javascript">
    <pop:Permission ename="editTranscard">
    deleteTranscard = function(id) {
        layer.confirm('删除后不可撤销，是否确认删除？', {
            icon: 0,
            title: ['删除', 'font-size:14px;color:#ffffff;background:#478de4;']
        }, function() {
            $.post("../../manage/transcard/delete.do", encodeURI("id=" + id),
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
            );
        });
        // 阻止事件冒泡到DOM树上
        event.stopPropagation();
    };
    </pop:Permission>
    function showList(transcardId, pageId) {
        var rows = $("#page_size").val();
        var startRow = (pageId - 1) * rows;

        $.post(
            "../../manage/transcard/ajaxFindForPage.do",
            encodeURI("transportCardId=" + transcardId + "&pageId=" + pageId + "&startRow=" + startRow + "&rows=" + rows),
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
                $("#tcid").val(gridPage.t.transportCardId);
                $(".table-body").html("");
                var transcards = gridPage.dataList;
                var tableData = "<table width='100%'>";
                for (var i = 0; i < gridPage.currentRows; i++) {
                    var transcard = transcards[i];
                    tableData += "<tr onclick=\"dispatch('edit'," + transcard.id + ")\">" +
                        "<td class=\"transcard-id\">" + transcard.transportCardId + "</td>";
                   	if (transcard.carNumber == undefined) {
                   		tableData += "<td class=\"transcard-carno\"></td>";
                   	} else {
                   		tableData += "<td class=\"transcard-carno\">" + transcard.carNumber + "</td>";
                   	}
                   	tableData += "<td class=\"transcard-remark\">" + transcard.remark + "</td>" +
                        "<td class=\"transcard-action\">" +
                        "<img src=\"../../resources/images/operate/view.png\" alt=\"查看\" title=\"查看\" onclick=\"dispatch('view'," + transcard.id + ")\">" +
                        <pop:Permission ename="editTranscard">
                        "&emsp;<img src=\"../../resources/images/operate/edit.png\" alt=\"编辑\" title=\"编辑\" onclick=\"dispatch('edit'," + transcard.id + ")\">&emsp;" +
                        "<img src=\"../../resources/images/operate/delete.png\" alt=\"删除\" title=\"删除\" onclick=\"deleteTranscard(" + transcard.id + ")\">" +
                        </pop:Permission>
                        "</td>" +
                        "</tr>";
                }
                tableData += "</table>";
                tableData = tableData.replace(/>null</g, "><");
                $(".table-body").html(tableData);
            },
            "json"
        );
    }
    </script>
</head>

<body>
    <div class="container">
    	<div class="nav-addr">
	        <img src="../../resources/images/navbar/basicinfo.png"> 基本信息管理
	        <img src="../../resources/images/navbar/nav_right_12.png"> 配送卡管理
	    </div>
        <div class="search-zone">
            <label for="search_text" class="search-type" id="search_type">配送卡ID</label>
            <input type="text" class="search-text" id="search_text">
            <input type="button" class="search-btn" id="search_btn" value="查询">
            <pop:Permission ename="editTranscard">
            <input type="button" id="add" value="添加" onclick="dispatch('add',0)">
            </pop:Permission>
        </div>
        <div class="data-zone">
            <div class="table-head">
                <table width="100%">
                    <thead>
                        <tr>
                            <th class="transcard-id">配送卡ID</th>
                            <th class="transcard-carno">车牌号</th>
                            <th class="transcard-remark">备注</th>
                            <th class="transcard-action">操作</th>
                        </tr>
                    </thead>
                </table>
            </div>
            <div class="table-body">
                <table width="100%">
                </table>
            </div>
            <div class="page">
                <span>第</span>
                <select id="page_id">
                    <option value="1">1</option>
                </select>
                <span id="page_info">页(几条数据)/共几页(共几条数据)</span>
                <select id="page_size">
                    <option value="10">10条/页</option>
                    <option value="20">20条/页</option>
                    <option value="25">25条/页</option>
                    <option value="30">30条/页</option>
                    <option value="50">50条/页</option>
                </select>
                <input type="hidden" id="tcid">
            </div>
        </div>
    </div>
</body>

</html>
</pop:Permission>