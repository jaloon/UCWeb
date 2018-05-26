<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="pop" uri="/pop-tags" %>
<pop:Permission ename="viewCard">
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>卡信息列表</title>
    <link rel="stylesheet" href="../../resources/css/base.css" media="all">
    <link rel="stylesheet" href="../../resources/css/normal.css" media="all">
    <script src="../../resources/plugins/jquery-1.8.3.min.js"></script>
    <script src="../../resources/plugins/layer/layer.js"></script>
    <script src="../../resources/js/base.js"></script>
    <script src="../../resources/js/normal.js"></script>
    <script src="../../resources/js/card/cardList.js"></script>
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

        .card-id {
            width: 100px;
        }
        
        .card-type {
            width: 200px;
        }
        
        .card-director {
            width: 100px;
        }
        
        .card-phone {
            width: 160px;
        }
        
        .card-identity {
            width: 300px;
        }
        
        .card-remark {
            width: 100px;
        }
        
        .card-action {
            width: 200px;
        }
    </style>
    <script type="text/javascript">
    <pop:Permission ename="editCard">
	    function deleteCard(id) {
	        layer.confirm('删除后不可撤销，是否确认删除？', {
	            icon: 0,
	            title: ['删除', 'font-size:14px;color:#ffffff;background:#478de4;']
	        }, function() {
	            $.post("../../manage/card/delete.do", encodeURI("id=" + id),
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
	    }
	    function findCardByReader() {
            
        }
    </pop:Permission>
	    function showList(cardType, pageId) {
	        var rows = $("#page_size").val();
	        var startRow = (pageId - 1) * rows;
	
	        $.post(
	            "../../manage/card/ajaxFindForPage.do",
	            encodeURI("type=" + cardType + "&pageId=" + pageId + "&startRow=" + startRow + "&rows=" + rows),
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
	                $("#ctype").val(gridPage.t.type);
	                $(".table-body").html("");
	                var cards = gridPage.dataList;
	                var tableData = "<table width='100%'>";
	                for (var i = 0; i < gridPage.currentRows; i++) {
	                    var card = cards[i];
	                    tableData += "<tr onclick=\"dispatch('edit'," + card.id + ")\">" +
	                        "<td class=\"card-id\">" + card.cardId + "</td>" +
	                        "<td class=\"card-type\">" + card.typeName + "</td>" +
	                        "<td class=\"card-director\">" + card.director + "</td>" +
	                        "<td class=\"card-phone\">" + card.phone + "</td>" +
	                        "<td class=\"card-identity\">" + card.identityCard + "</td>" +
	                        "<td class=\"card-remark\">" + card.remark + "</td>" +
	                        "<td class=\"card-action\">" +
	                        "<img src=\"../../resources/images/operate/view.png\" alt=\"查看\" title=\"查看\" onclick=\"dispatch('view'," + card.id + ")\">" +
	                        <pop:Permission ename="editCard">
	                        "&emsp;<img src=\"../../resources/images/operate/edit.png\" alt=\"编辑\" title=\"编辑\" onclick=\"dispatch('edit'," + card.id + ")\">&emsp;" +
	                        "<img src=\"../../resources/images/operate/delete.png\" alt=\"删除\" title=\"删除\" onclick=\"deleteCard(" + card.id + ")\">" +
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
	        <img src="../../resources/images/navbar/nav_right_12.png"> 卡信息管理
	    </div>
        <div class="search-zone">
            <label for="search_text" class="search-type" id="search_type">卡类型</label>
            <select class="search-text" id="search_text">
                <option value=1>应急卡</option>
                <option value=2>入库卡</option>
                <option value=3>出库卡</option>
                <option value=4>出入库卡</option>
                <option value=5>普通卡</option>
                <option value=6>管理卡</option>
            </select>
            <input type="button" class="search-btn button" id="search_btn" value="查询">
            <pop:Permission ename="editCard">
            <input type="button" class="button" id="reader" value="批量添加" onclick="findCardByReader()">
            <script>
                $("#reader").click(function () {
                    layer.open({
                        type: 2,
                        title: ['卡管理（读卡器批量添加卡）', 'font-size:14px;color:#ffffff;background:#478de4;'],
                        shadeClose: true,
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
            <div class='table-cont' id='table-cont'>
                <table width="100%">
                    <thead class="table-head">
                        <tr>
                            <th class="card-id">卡ID</th>
                            <th class="card-type">卡类型</th>
                            <th class="card-director">负责人</th>
                            <th class="card-phone">联系电话</th>
                            <th class="card-identity">身份证号</th>
                            <th class="card-remark">备注</th>
                            <th class="card-action">操作</th>
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
                <input type="hidden" id="ctype">
            </div>
        </div>
    </div>
    <div class="reader-box">

    </div>
</body>
</html>
</pop:Permission>