<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="pop" uri="/pop-tags" %>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=center-width, initial-scale=1, maximum-scale=1">
    <title>普利通电子签封安全监管系统</title>
    <link rel="shortcut icon" href="favicon.ico"/>
    <script src="resources/js/base.js"></script>
    <link rel="stylesheet" type="text/css" href="resources/css/base.css">
    <link rel="stylesheet" type="text/css" href="resources/css/index.css"/>
    <link rel="stylesheet" type="text/css" href="resources/plugins/iziToast/iziToast.min.css"/>
    <%--<link rel="stylesheet" type="text/css" href="resources/plugins/jqTable/css/jqTable.css"/>--%>
    <style type="text/css">
        .userManage dt {
            background-image: url(resources/images/navbar/permission.png);
        }

        .logManage dt {
            background-image: url(resources/images/navbar/log.png);
        }

        .infoManage dt {
            background-image: url(resources/images/navbar/basicinfo.png);
        }

        .carManage dt {
            background-image: url(resources/images/navbar/car.png);
        }

        .statisticsManage dt {
            background-image: url(resources/images/navbar/statistics.png);
        }
    </style>
    <c:set var="user" value="<%=com.tipray.core.ThreadVariable.getUser()%>"></c:set>
</head>

<body>
<div class="container">
    <div class="top">
        <div class="profile-zone">
            <input type="hidden" id="ctx" value="${pageContext.request.contextPath}">
            <input type="hidden" id="id" value="${user.id}">
            <span class="profile-name">${user.name}<img src="resources/images/profile/profile_down.png"/></span>
            <img class="profile-pic" src="resources/images/default_headpic.png"/>
            <div class="info-zone">
                <div class="user-info" id="info-edit">修改个人信息</div>
                <br>
                <div class="user-info" id="pwd-edit">修改密码</div>
                <hr/>
                <div class="user-info" id="logout">退出</div>
            </div>
        </div>
        <div class="alarm-zone">
            <img class="alarm-tip" src="resources/images/bell-icon.png"/>
            <span class="alarm-num"></span>
        </div>
        <div class="logo-zone">
            <img src="resources/images/commen-logo.png"/>
            <span class="logo-text">普利通电子签封安全监管系统</span>
        </div>
    </div>
    <div class="left-nav">
        <pop:Permission ename="systemOperate">
            <pop:Permission ename="infoManage">
                <dl class="infoManage">
                    <dt>基本信息管理</dt>
                    <pop:Permission ename="oildepotModule">
                        <dd class="first_dd"><a name="normal/oildepot/oildepotList.jsp">油库管理</a></dd>
                    </pop:Permission>
                    <pop:Permission ename="gasstationModule">
                        <dd><a name="normal/gasstation/gasstationList.jsp">加油站管理</a></dd>
                    </pop:Permission>
                    <pop:Permission ename="cardModule">
                        <dd><a name="normal/card/cardList.jsp">卡信息管理</a></dd>
                    </pop:Permission>
                    <pop:Permission ename="handsetModule">
                        <dd><a name="normal/handset/handsetList.jsp">手持机管理</a></dd>
                    </pop:Permission>
                    <pop:Permission ename="transcardModule">
                        <dd><a name="normal/transcard/transcardList.jsp">配送卡管理</a></dd>
                    </pop:Permission>
                    <pop:Permission ename="transcomModule">
                        <dd><a name="normal/transcom/transcomList.jsp">运输公司管理</a></dd>
                    </pop:Permission>
                    <pop:Permission ename="driverModule">
                        <dd><a name="normal/driver/driverList.jsp">司机管理</a></dd>
                    </pop:Permission>
                    <pop:Permission ename="deviceModule">
                        <dd><a name="normal/device/deviceList.jsp">设备信息同步</a></dd>
                    </pop:Permission>
                </dl>
            </pop:Permission>
            <pop:Permission ename="carManage">
                <dl class="carManage">
                    <dt>车辆管理</dt>
                    <pop:Permission ename="carModule">
                        <dd class="first_dd"><a name="normal/car/carList.jsp">车辆信息管理</a></dd>
                    </pop:Permission>
                    <dd>
                        <a name="normal/car/carSupervise.jsp">车辆监控及远程操作</a>
                    </dd>
                    <pop:Permission ename="retrackModule">
                        <dd><a name="normal/car/track/carRetrack.html">车辆轨迹回放</a></dd>
                    </pop:Permission>
                </dl>
            </pop:Permission>
            <pop:Permission ename="statisticsManage">
                <dl class="statisticsManage">
                    <dt>查询统计</dt>
                    <pop:Permission ename="alarmRecordModule">
                        <dd class="first_dd"><a name="normal/statistics/alarmList.html">报警记录</a></dd>
                    </pop:Permission>
                    <pop:Permission ename="remoteRecordModule">
                        <dd><a name="normal/statistics/remoteList.html">远程操作记录</a></dd>
                    </pop:Permission>
                    <pop:Permission ename="sealRecordModule,lockStatusModule">
                        <dd><a name="normal/statistics/sealAndLockList.html">施解封和锁动作记录</a></dd>
                    </pop:Permission>
                    <pop:Permission ename="transRecordModule">
                        <dd><a name="normal/statistics/transportList.html">配送信息</a></dd>
                    </pop:Permission>
                    <pop:Permission ename="changeRecordModule">
                        <dd><a name="normal/statistics/changeList.html">远程换站记录</a></dd>
                    </pop:Permission>
                    <pop:Permission ename="usageRecordModule">
                        <dd><a name="normal/statistics/usageList.html">卡及设备使用记录</a></dd>
                    </pop:Permission>
                    <pop:Permission ename="resetRecordModule">
                        <dd><a name="normal/statistics/resetList.html">开锁重置记录</a></dd>
                    </pop:Permission>
                    <pop:Permission ename="eventRecordModule">
                        <dd><a name="normal/statistics/eventList.html">车载终端事件记录</a></dd>
                    </pop:Permission>
                </dl>
            </pop:Permission>
        </pop:Permission>
        <pop:Permission ename="systemManage">
            <pop:Permission ename="userManage">
                <dl class="userManage">
                    <dt>操作员权限管理</dt>
                    <pop:Permission ename="userModule">
                        <dd class="first_dd"><a name="normal/user/userList.jsp">操作员管理</a></dd>
                    </pop:Permission>
                    <pop:Permission ename="roleModule">
                        <dd><a name="normal/role/roleList.jsp">角色管理</a></dd>
                    </pop:Permission>
                </dl>
            </pop:Permission>
            <pop:Permission ename="logManage">
                <dl class="logManage">
                    <dt>操作日志管理</dt>
                    <pop:Permission ename="infologModule">
                        <dd class="first_dd"><a name="normal/log/infoLog.html">信息管理日志</a></dd>
                    </pop:Permission>
                    <pop:Permission ename="carlogModule">
                        <dd><a name="normal/log/carLog.html">车辆管理日志</a></dd>
                    </pop:Permission>
                </dl>
            </pop:Permission>
        </pop:Permission>
    </div>
    <div class="main">
        <c:set var="mainAuth" value="<%=com.tipray.core.ThreadVariable.mainAuth()%>"></c:set>
        <c:if test="${mainAuth==true}">
            <iframe src="normal/car/carSupervise.jsp?user=${user.id}&ctx=${pageContext.request.contextPath}"
                    frameborder="0" name="showContent" width="100%" height="100%"></iframe>
        </c:if>
        <c:if test="${mainAuth==false}">
            <iframe src="normal/main.html" frameborder="0" name="showContent" width="100%" height="100%"></iframe>
        </c:if>
    </div>
</div>
<div class="alarm-box">
    <div class='table-cont' id='alarm_tips'>
        <table width="100%">
            <thead class="table-head">
            <tr>
                <th class="alarm-car">报警车辆</th>
                <th class="alarm-dev">报警设备</th>
                <th class="alarm-type">报警类型</th>
                <th class="">操作</th>
            </tr>
            </thead>
            <tbody class="table-body"></tbody>
        </table>
    </div>
    <%--<div class='table-cont c-table c-table--border' id='alarm_tips'>--%>
        <%--<table cellspacing="0" cellpadding="0" border="0" role="c-table" data-height="200">--%>
            <%--<colgroup>--%>
                <%--<col name="" width="100px">--%>
                <%--<col name="" width="180px">--%>
                <%--<col name="" width="181px">--%>
                <%--<col name="" width="99px">--%>
            <%--</colgroup>--%>
        <%--</table>--%>
    <%--</div>--%>
</div>
</body>

</html>
<!-- 数据模版 -->
<script id="table-tpl" type="text/html">
    <table cellspacing="0" cellpadding="0" border="0" class="" role="c-table" data-height="200">
        <colgroup>
            <col name="" width="100px">
            <col name="" width="180px">
            <col name="" width="181px">
            <col name="" width="99px">
        </colgroup>
        <thead>
        <tr>
            <th class="">
                <div class="cell">报警车辆</div>
            </th>
            <th class="">
                <div class="cell">报警设备</div>
            </th>
            <th class="">
                <div class="cell">报警类型</div>
            </th>
            <th class="">
                <div class="cell">操作</div>
            </th>
        </tr>
        </thead>
        <tbody>
        {{each data}}
        <tr class="alarm-content" id="alarm_id_{{$value.id}}" onclick="showAlarm({{$value.id}})">
            <td class="">
                <div class="cell">{{$value.car}}</div>
            </td>
            <td class="">
                <div class="cell">{{$value.dev}}</div>
            </td>
            <td class="">
                <div class="cell">{{$value.type}}</div>
            </td>
            <td class="alarm-eli">
                <div class="cell" title="消除报警" onclick="eliminateAlarm('{{$value.car}}',{{$value.id}})">
                    <img src="resources/images/operate/delete.png" alt="消除报警">
                </div>
            </td>
        </tr>
        {{/each}}
        </tbody>
    </table>
</script>
<script src="resources/plugins/jquery-1.8.3.min.js"></script>
<script src="resources/plugins/ReconnectingWebSocket/reconnecting-websocket.js"></script>
<script src="resources/plugins/ConcurrentThread/Concurrent.Thread.min.js"></script>
<script src="resources/plugins/iNotify/iNotify.js"></script>
<script src="resources/plugins/iziToast/iziToast.min.js"></script>
<script src="resources/plugins/layer/layer.js"></script>
<script src="resources/plugins/polyfill/grayscale.js"></script>
<%--<!--开源模版引擎： art-template@4.12.2 for browser | https://github.com/aui/art-template -->--%>
<%--<script src="resources/plugins/jqTable/js/plugins/artTemplate.js"></script>--%>
<%--<!-- 必要插件：固定列滚动需要用到，鼠标滚动兼容多浏览器 -->--%>
<%--<script src="resources/plugins/jqTable/js/jquery.mousewheel.min.js"></script>--%>
<%--<!-- 表格插件 -->--%>
<%--<script src="resources/plugins/jqTable/js/zipJs/jqTable.all.min.js"></script>--%>
<script src="resources/js/index.js"></script>