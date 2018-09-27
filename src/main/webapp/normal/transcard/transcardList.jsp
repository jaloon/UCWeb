<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="pop" uri="/pop-tags" %>
<pop:Permission ename="viewTranscard">
<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8">
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=center-width, initial-scale=1, maximum-scale=1">
    <title>配送卡列表</title>
    <script src="../../resources/js/base.js"></script>
    <link rel="stylesheet" href="../../resources/css/base.css" media="all">
    <link rel="stylesheet" href="../../resources/css/normal.css" media="all">
    <script src="../../resources/plugins/jquery-1.8.3.min.js"></script>
    <script src="../../resources/plugins/layer/layer.js"></script>
    <script src="../../resources/plugins/verify.js"></script>
    <script src="../../resources/js/normal.js"></script>
    <script src="../../resources/js/transcard/transcardList.js"></script>
    <style type="text/css">
        #reader {
            width: 100px;
            height: 36px;
            font-size: 16px;
            background: #478de4;
            border: 0;
            border-radius: 3px;
            color: white;
            position: relative;
            float: right;
            top: 10px;
            margin-right: 30px;
        }

        /*.transcard-id {*/
            /*width: 200px;*/
        /*}*/
        
        /*.transcard-carno {*/
            /*width: 200px;*/
        /*}*/
        
        /*.transcard-remark {*/
            /*width: 200px;*/
        /*}*/
        
        /*.transcard-action {*/
            /*width: 200px;*/
        /*}*/
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
    function showList(transcardId, pageId) {
        var rows = $("#page_size").val();
        var startRow = (pageId - 1) * rows;
        var loadLayer = layer.load();
        $.post(
            "../../manage/transcard/ajaxFindForPage.do",
            encodeURI("transportCardId=" + transcardId + "&pageId=" + pageId + "&startRow=" + startRow + "&rows=" + rows),
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
                $("#tcid").val(gridPage.t.transportCardId);
                $(".table-body").html("");
                var transcards = gridPage.dataList;
                var tableData = "<table width='100%'>";
                for (var i = 0; i < gridPage.currentRows; i++) {
                    var transcard = transcards[i];
                    tableData += "<tr class='list-content' onclick=\"dispatch('edit'," + transcard.id + ")\">" +
                        "<td class=\"transcard-id\">" + transcard.transportCardId + "</td>";
                   	if (transcard.carNumber == undefined) {
                   		tableData += "<td class=\"transcard-carno\"></td>";
                   	} else {
                   		tableData += "<td class=\"transcard-carno\">" + transcard.carNumber + "</td>";
                   	}
                   	tableData += "<td class=\"transcard-remark\">" + transcard.remark + "</td>" +
                        "<td class=\"transcard-action\">" +
                        "<img class='edit-btn' src=\"../../resources/images/operate/view.png\" alt=\"查看\" title=\"查看\" onclick=\"dispatch('view'," + transcard.id + ")\">" +
                        <pop:Permission ename="editTranscard">
                        "&emsp;<img class='edit-btn' src=\"../../resources/images/operate/edit.png\" alt=\"编辑\" title=\"编辑\" onclick=\"dispatch('edit'," + transcard.id + ")\">&emsp;" +
                        "<img class='edit-btn' src=\"../../resources/images/operate/delete.png\" alt=\"删除\" title=\"删除\" onclick=\"deleteTranscard(" + transcard.id + ")\">" +
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
	        <img src="../../resources/images/navbar/nav_right_12.png"> 配送卡管理
	    </div>
        <div class="search-zone">
            <label for="search_text" class="search-type" id="search_type">配送卡ID</label>
            <input type="text" class="search-text" id="search_text">
            <input type="button" class="search-btn button" id="search_btn" value="查询">
            <pop:Permission ename="editTranscard">
            <input type="button" class="button" id="reader" value="批量添加">
            <script>
                $("#reader").click(function () {
                    layer.open({
                        type: 2,
                        title: ['卡管理（读卡器批量添加卡）', 'font-size:14px;color:#ffffff;background:#478de4;'],
                        // shadeClose: true,
                        shade: 0.8,
                        resize: false,
                        area: ['606px', '430px'],
                        content: 'cardReader.html'
                    });
                });
            </script>
            <input type="button" class="button" id="add" value="添加" onclick="dispatch('add',0)">
            </pop:Permission>
        </div>
        <div class="data-zone">
            <div class='table-box'>
                <table class="table-cont" width="100%">
                    <thead class="table-head">
                        <tr>
                            <th class="transcard-id">配送卡ID</th>
                            <th class="transcard-carno">车牌号</th>
                            <th class="transcard-remark">备注</th>
                            <th class="transcard-action">操作</th>
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
                <input type="hidden" id="tcid">
            </div>
        </div>
    </div>
</body>

</html>
</pop:Permission>