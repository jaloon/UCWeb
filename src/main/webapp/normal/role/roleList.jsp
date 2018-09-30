<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="pop" uri="/pop-tags" %>
<pop:Permission ename="viewRole">
    <!DOCTYPE html>
    <html>

    <head>
        <meta charset="utf-8">
        <meta name="renderer" content="webkit">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        <meta name="viewport" content="width=center-width, initial-scale=1, maximum-scale=1">
        <title>角色列表</title>
        <script src="../../resources/js/base.js"></script>
        <link rel="stylesheet" href="../../resources/css/base.css">
        <link rel="stylesheet" href="../../resources/css/normal.css">
        <link rel="stylesheet" href="../../resources/plugins/jqTable/css/jqTable.css">
    </head>

    <body>
    <div class="container">
        <div class="nav-addr">
            <img src="../../resources/images/navbar/permission.png"> 操作员权限管理
            <img src="../../resources/images/navbar/nav_right_12.png"> 角色管理
        </div>
        <div class="search-zone">
            <label for="search_text" class="search-type" id="search_type">角色名称</label>
            <input type="text" class="search-text" id="search_text">
            <input type="button" class="search-btn button" id="search_btn" value="查询">
            <pop:Permission ename="editRole">
                <input type="button" class="button" id="add" value="添加" onclick="dispatch('add',0)">
            </pop:Permission>
        </div>
        <div class="data-zone">
            <div class="table-wrap">
                <div class="table-box c-table c-table--border">
                    <table cellspacing="0" cellpadding="0" border="0" role="c-table" data-height="200">
                        <colgroup>
                            <col name="" width="">
                            <col name="" width="">
                            <col name="" width="">
                            <col name="" width="">
                            <col name="" width="">
                        </colgroup>
                    </table>
                </div>
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
                <input type="hidden" id="rname">
            </div>
        </div>
    </div>
    </body>

    </html>
    <!-- 数据模版 -->
    <script id="table-tpl" type="text/html">
        <table cellspacing="0" cellpadding="0" border="0" class="" role="c-table" data-height="200">
            <colgroup>
                <col name="" width="">
                <col name="" width="">
                <col name="" width="">
                <col name="" width="">
                <col name="" width="">
            </colgroup>
            <thead>
            <tr>
                <th class="">
                    <div class="cell">角色ID</div>
                </th>
                <th class="">
                    <div class="cell">角色名称</div>
                </th>
                <th class="">
                    <div class="cell">是否APP角色</div>
                </th>
                <th class="">
                    <div class="cell">备注</div>
                </th>
                <th class="">
                    <div class="cell">操作</div>
                </th>
            </tr>
            </thead>
            <tbody>
            {{each data}}
            {{if $value.id > 20}}
            <tr onclick="dispatch('edit',{{$value.id}})">
                {{else}}
            <tr>
                {{/if}}
                <td class="">
                    <div class="cell">{{$value.id}}</div>
                </td>
                <td class="">
                    <div class="cell">{{$value.name}}</div>
                </td>
                <td class="">
                    <div class="cell">{{$value.app}}</div>
                </td>
                <td class="">
                    <div class="cell">{{$value.remark}}</div>
                </td>
                <td class="">
                    <div class="cell">
                        <img class="edit-btn" src="../../resources/images/operate/view.png" alt="查看" title="查看"
                             onclick="dispatch('view',{{$value.id}})">
                        <pop:Permission ename="editCard">
                            {{if $value.id > 20}}
                            <img class="edit-btn" src="../../resources/images/operate/edit.png" alt="编辑" title="编辑"
                                 onclick="dispatch('edit',{{$value.id}})">
                            <img class="edit-btn" src="../../resources/images/operate/delete.png" alt="删除" title="删除"
                                 onclick="deleteCard({{$value.id}})">
                            {{/if}}
                        </pop:Permission>
                    </div>
                </td>
            </tr>
            {{/each}}
            </tbody>
        </table>
    </script>
    <script src="../../resources/plugins/jquery-1.8.3.min.js"></script>
    <script src="../../resources/plugins/layer/layer.js"></script>
    <!--开源模版引擎： art-template@4.12.2 for browser | https://github.com/aui/art-template -->
    <script src="../../resources/plugins/jqTable/js/plugins/artTemplate.js"></script>
    <!-- 必要插件：固定列滚动需要用到，鼠标滚动兼容多浏览器 -->
    <script src="../../resources/plugins/jqTable/js/jquery.mousewheel.min.js"></script>
    <!-- 表格插件 -->
    <script src="../../resources/plugins/jqTable/js/zipJs/jqTable.all.min.js"></script>
    <script type="text/javascript">
        <pop:Permission ename="editRole">

        function deleteRole(id, app) {
            if (id < 21) {
                layer.alert('系统内置角色，不可删除！', {icon: 6});
            } else {
                layer.confirm('删除后不可撤销，是否确认删除？', {
                    icon: 0,
                    title: ['删除', 'font-size:14px;color:#ffffff;background:#478de4;']
                }, function () {
                    $.post("../../manage/role/delete.do", encodeURI("id=" + id + "&app=" + app),
                        function (data) {
                            if ("success" == data.msg) {
                                layer.msg('删除成功！', {icon: 1, time: 500}, function () {
                                    refreshPage();
                                });
                            } else {
                                layer.msg('删除失败！', {icon: 2, time: 500});
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
            }
        };
        </pop:Permission>

        function dispatch(mode, id) {
            var title = "添加角色";
            var h = "435px";
            if ("edit" == mode) {
                if (id < 21) {
                    layer.alert('系统内置角色，不可修改！', {icon: 6});
                    return;
                }
                title = "修改角色信息";
            }
            if ("view" == mode) {
                title = "查看角色信息";
                h = "381px";
            }
            layer.open({
                type: 2,
                title: ['角色管理（' + title + '）', 'font-size:14px;color:#ffffff;background:#478de4;'],
                // shadeClose: true,
                shade: 0.8,
                resize: false,
                area: ['540px', h],
                content: '../../manage/role/dispatch.do?' + encodeURI('mode=' + mode + '&id=' + id), //iframe的url
                end: function () {
                    if (mode != "view") {
                        refreshPage();
                    }
                }
            });
            // 阻止事件冒泡到DOM树上
            stopPropagation(event);
        }

        function refreshPage() {
            var pageId = $("#page_id").val();
            if (isNull(pageId)) {
                pageId = 1;
            }
            var name = $("#rname").val();
            if (isNull(name)) {
                name = "";
            }
            showList(name, pageId);
        }

        function showList(name, pageId) {
            var rows = $("#page_size").val();
            var startRow = (pageId - 1) * rows;
            var loadLayer = layer.load();
            $.post(
                "../../manage/role/ajaxFindForPage.do",
                encodeURI("name=" + name + "&pageId=" + pageId + "&startRow=" + startRow + "&rows=" + rows),
                function (gridPage) {
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
                    var dataCount = gridPage.currentRows;
                    $("#page_info").html("页(" + dataCount + "条数据)/共" + pageCount + "页(共" + gridPage.records + "条数据)");
                    $("#rname").val(gridPage.t.name);
                    $(".table-body").html("");
                    var roles = gridPage.dataList;
                    var jsonData = [];
                    for (var i = 0; i < dataCount; i++) {
                        var role = roles[i];
                        jsonData.push({
                            id: role.id,
                            name: role.name,
                            app: role.isApp == 0 ? "否" : "是",
                            remark: role.remark
                        });
                    }
                    // 更新表格数据
                    var tableHtml = template('table-tpl', {data: jsonData});
                    $('.c-table').eq(0).data('table').updateHtml(tableHtml);
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

        $(function () {
            $(window).resize(function () {
                var height = $(window).height() - 133;
                $('.table-box').data('height', height);
                // $(window).trigger('resize');
            }).resize();

            $('[role="c-table"]').jqTable();
            showList("", 1);

            $("#search_btn").click(function () {
                var name = $.trim($("#search_text").val());
                if (isNull(name)) {
                    layer.alert('请输入要查询的角色名称！', {icon: 6}, function (index2) {
                        layer.close(index2);
                        $("#search_text").select();
                    });
                    return;
                }
                showList(name, 1);
            });

            $("#page_id").change(function () {
                refreshPage();
            });

            $("#page_size").change(function () {
                var name = $("#rname").val();
                if (isNull(name)) {
                    name = "";
                }
                showList(name, 1);
            });
        });
    </script>
</pop:Permission>