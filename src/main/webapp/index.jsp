<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="pop" uri="/pop-tags" %>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>普利通电子签封安全监管系统</title>
    <link rel="shortcut icon" href="favicon.ico"/>
    <link rel="stylesheet" type="text/css" href="resources/css/base.css">
    <link rel="stylesheet" type="text/css" href="resources/css/index.css"/>
    <link rel="stylesheet" type="text/css" href="resources/plugins/iziToast/iziToast.min.css"/>
    <script src="resources/plugins/jquery-1.8.3.min.js"></script>
    <script src="resources/plugins/ReconnectingWebSocket/reconnecting-websocket.min.js"></script>
    <script src="resources/plugins/ConcurrentThread/Concurrent.Thread.min.js"></script>
    <script src="resources/plugins/iNotify/iNotify.js"></script>
    <script src="resources/plugins/iziToast/iziToast.min.js"></script>
    <script src="resources/plugins/layer/layer.js"></script>
    <script src="resources/plugins/grayscale.js"></script>
    <script src="resources/plugins/sockjs-0.3.4.min.js"></script>
    <script src="resources/js/base.js"></script>
    <script src="resources/js/index.js"></script>
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
        <img class="alarm-tip" src="resources/images/bell-icon.png"/>
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
                        <a name="normal/car/carSupervise.html">车辆监控及远程操作</a>
                    </dd>
                        <%--<dd><a name="normal/car/carMonitor.html">车辆实时监控</a></dd>--%>
                    <pop:Permission ename="retrackModule">
                        <dd><a name="normal/car/track/carRetrack.html">车辆轨迹回放</a></dd>
                    </pop:Permission>
                </dl>
            </pop:Permission>
            <pop:Permission ename="statisticsManage">
                <dl class="statisticsManage">
                    <dt>查询统计</dt>
                    <pop:Permission ename="alarmRecordModule">
                        <dd class="first_dd"><a name="normal/statistics/alarmList.html">报警记录查询</a></dd>
                    </pop:Permission>
                    <pop:Permission ename="remoteRecordModule">
                        <dd><a name="normal/statistics/remoteList.html">远程操作查询</a></dd>
                    </pop:Permission>
                    <pop:Permission ename="lockStatusModule">
                        <dd><a name="normal/statistics/lockstatusList.html">锁动作记录查询</a></dd>
                    </pop:Permission>
                    <pop:Permission ename="inAndOutRecordModule">
                        <dd><a name="normal/statistics/inandoutList.html">车辆进出记录查询</a></dd>
                    </pop:Permission>
                    <pop:Permission ename="transRecordModule">
                        <dd><a name="normal/statistics/transportList.html">配送信息查询</a></dd>
                    </pop:Permission>
                    <pop:Permission ename="changeRecordModule">
                        <dd><a name="normal/statistics/changeList.html">远程换站记录查询</a></dd>
                    </pop:Permission>
                    <pop:Permission ename="cardUseRecordModule">
                        <dd><a name="normal/statistics/deviceuseList.html">卡及设备使用记录查询</a></dd>
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
            <iframe src="normal/car/carSupervise.html?user=${user.id}&ctx=${pageContext.request.contextPath}"
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
                    <th class="alarm-id">报警序号</th>
                    <th class="alarm-dev">报警设备</th>
                    <th class="alarm-type">报警类型</th>
                    <th class="alarm-eli">操作</th>
                </tr>
            </thead>
            <tbody class="table-body"></tbody>
        </table>
    </div>
</div>
</body>

</html>