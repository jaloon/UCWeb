<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="pop" uri="/pop-tags" %>
<pop:Permission ename="viewUser">
<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8">
    <title>操作员列表</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="../../resources/css/base.css" media="all">
    <link rel="stylesheet" href="../../resources/css/normal.css" media="all">
    <script src="../../resources/plugins/jquery-1.8.3.min.js"></script>
    <script src="../../resources/plugins/layer/layer.js"></script>
    <script src="../../resources/js/base.js"></script>
    <script src="../../resources/js/normal.js"></script>
    <script src="../../resources/js/user/userList.js"></script>
    <style type="text/css">
        .user-id {
            width: 80px;
        }
        
        .user-account {
            width: 100px;
        }
        
        .user-role {
            width: 100px;
        }
        
        .user-app {
            width: 100px;
        }

        .user-com {
            width: 200px;
        }

        .user-name {
            width: 100px;
        }
        
        .user-phone {
            width: 160px;
        }
        
        .user-card {
            width: 200px;
        }
        
        .user-remark {
            width: 100px;
        }
        
        .user-action {
            width: 200px;
        }
    </style>
    <script type="text/javascript">
    <pop:Permission ename="editUser">
    deleteUser = function(id) {
        layer.confirm('删除后不可撤销，是否确认删除？', {
            icon: 0,
            title: ['删除', 'font-size:14px;color:#ffffff;background:#478de4;']
        }, function() {
            $.post("../../manage/user/delete.do", encodeURI("id=" + id),
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

    resetPwd = function(id) {
        layer.confirm('确认重置密码？', {
            icon: 4,
            title: ['密码重置', 'font-size:14px;color:#ffffff;background:#478de4;']
        }, function() {
            $.post("../../manage/user/reset.do", encodeURI("id=" + id),
                function(data) {
                    if ("success" == data.msg) {
                        layer.msg('重置成功！', { icon: 1, time: 500 });
                    } else {
                        layer.msg('重置失败！', { icon: 2, time: 500 });
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
    function showList(account, name, pageId) {
        var rows = $("#page_size").val();
        var startRow = (pageId - 1) * rows;

        $.post(
            "../../manage/user/ajaxFindForPage.do",
            encodeURI("account=" + account + "&name=" + name + "&pageId=" + pageId + "&startRow=" + startRow + "&rows=" + rows),
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
                $("#uaccount").val(gridPage.t.account);
                $("#uname").val(gridPage.t.name);
                $(".table-body").html("");
                var users = gridPage.dataList;
                var tableData = "<table width='100%'>";
                for (var i = 0; i < gridPage.currentRows; i++) {
                    var user = users[i];
                    tableData += "<tr class='list-content' onclick=\"dispatch('edit'," + user.id + ")\">" +
                        "<td class=\"user-id\">" + user.id + "</td>" +
                        "<td class=\"user-account\">" + user.account + "</td>" +
                        "<td class=\"user-role\">" + user.role.name + "</td>";
                    if (user.appRole.id == 0) {
                    	tableData += "<td class=\"user-app\"></td>";
                    } else {
                    	tableData += "<td class=\"user-app\">" + user.appRole.name + "</td>";
                    } 
                    tableData += "<td class=\"user-com\">" + user.comName + "</td>" +
                        "<td class=\"user-name\">" + user.name + "</td>" +
                        "<td class=\"user-phone\">" + user.phone + "</td>" +
                        "<td class=\"user-card\">" + user.identityCard + "</td>" +
                        "<td class=\"user-remark\">" + user.remark + "</td>" +
                        "<td class=\"user-action\">" +
                        "<img class='edit-btn' src=\"../../resources/images/operate/view.png\" alt=\"查看\" title=\"查看\" onclick=\"dispatch('view'," + user.id + ")\">" +
                        <pop:Permission ename="editUser">
                        "&emsp;<img class='edit-btn' src=\"../../resources/images/operate/edit.png\" alt=\"编辑\" title=\"编辑\" onclick=\"dispatch('edit'," + user.id + ")\">&emsp;" +
                        "<img class='edit-btn' src=\"../../resources/images/operate/delete.png\" alt=\"删除\" title=\"删除\" onclick=\"deleteUser(" + user.id + ")\">&emsp;" +
                        "<img class='edit-btn' src=\"../../resources/images/operate/repwd.png\" alt=\"重置密码\" title=\"重置密码\" onclick=\"resetPwd(" + user.id + ")\">" +
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
	        <img src="../../resources/images/navbar/permission.png"> 操作员权限管理
	        <img src="../../resources/images/navbar/nav_right_12.png"> 操作员管理
	    </div>
        <div class="search-zone">
            <select class="search-type" id="search_type">
                <option value="0">查询类型</option>
                <option value="1">账号</option>
                <option value="2">姓名</option>
            </select>
            <input type="text" class="search-text" id="search_text">
            <input type="button" class="search-btn button" id="search_btn" value="查询">
            <pop:Permission ename="editUser">
            <input type="button" class="button" id="add" value="添加" onclick="dispatch('add',0)">
            </pop:Permission>
        </div>
        <div class="data-zone">
            <div class='table-cont' id='table-cont'>
                <table width="100%">
                    <thead class="table-head">
                        <tr>
                            <th class="user-id">操作员ID</th>
                            <th class="user-account">账号</th>
                            <th class="user-role">角色</th>
                            <th class="user-app">APP角色</th>
                            <th class="user-com">所属运输公司</th>
                            <th class="user-name">姓名</th>
                            <th class="user-phone">联系电话</th>
                            <th class="user-card">身份证号</th>
                            <th class="user-remark">备注</th>
                            <th class="user-action">操作</th>
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
                <input type="hidden" id="uaccount">
                <input type="hidden" id="uname">
            </div>
        </div>
    </div>
</body>

</html>
</pop:Permission>