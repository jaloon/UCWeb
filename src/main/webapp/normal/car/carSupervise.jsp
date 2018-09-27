<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/functions' prefix='fn' %>
<%@ taglib prefix="pop" uri="/pop-tags" %>

<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8">
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=center-width, initial-scale=1, maximum-scale=1">
    <title>车辆配置及监控</title>
    <script src="../../resources/js/base.js"></script>
    <link rel="stylesheet" href="../../resources/css/base.css">
    <link rel="stylesheet" href="../../resources/css/carSupervise.css">
    <link rel="stylesheet" href="../../resources/plugins/combo/jquery.combo.select.css">
    <link rel="stylesheet" href="../../resources/plugins/jRange/jquery.range.css">
    <link rel="stylesheet" href="../../resources/plugins/jqTable/css/jqTable.css">
</head>

<body>
<div class="container">
    <div class="nav-addr">
        <img src="../../resources/images/navbar/car.png"> 车辆管理
        <img src="../../resources/images/navbar/nav_right_12.png"> 车辆监控及远程操作
    </div>
    <div class="search-zone">
        <label for="search_text" class="search-type" id="search_type">车牌号码</label>
        <!--<input type="text" class="search-text" id="search_text">-->
        <input class="search-text" id="hidden_car" style="display: none; opacity: 0;" disabled>
        <select class="search-text" id="search_text">
            <option value="">车牌号</option>
        </select>
        <%--<input type="button" class="search-btn button" id="search_btn" value="查询">--%>
        <div id="nav">
            <ul>
                <pop:Permission ename="bindModule">
                    <li>设备绑定
                        <ul>
                            <pop:Permission ename="bindTerminal">
                                <li id="bind_terminal">终端绑定</li>
                            </pop:Permission>
                            <pop:Permission ename="bindLock">
                                <li id="bind_lock">锁绑定</li>
                            </pop:Permission>
                        </ul>
                    </li>
                </pop:Permission>
                <pop:Permission ename="configModule">
                    <li>终端配置
                        <ul>
                            <pop:Permission ename="gpsConfig">
                                <li id="gps_config">GPS配置</li>
                            </pop:Permission>
                            <pop:Permission ename="funcEnable">
                                <li id="func_enable">功能启用</li>
                            </pop:Permission>
                            <pop:Permission ename="softUpgrade">
                                <li id="soft_upgrade">软件升级</li>
                                <li id="cancel_upgrade">取消升级</li>
                            </pop:Permission>
                        </ul>
                    </li>
                </pop:Permission>
                <pop:Permission ename="realtimeMonitor">
                    <li id="realtime_monitor">实时监控</li>
                </pop:Permission>
                <pop:Permission ename="remoteModule">
                    <li>远程控制
                        <ul>
                            <pop:Permission ename="intoOildepot">
                                <li id="into_oildepot">进油库</li>
                            </pop:Permission>
                            <pop:Permission ename="quitOildepot">
                                <li id="quit_oildepot">出油库</li>
                            </pop:Permission>
                            <pop:Permission ename="intoGasstation">
                                <li id="into_gasstation">进加油站</li>
                            </pop:Permission>
                            <pop:Permission ename="quitGasstation">
                                <li id="quit_gasstation">出加油站</li>
                            </pop:Permission>
                            <pop:Permission ename="intoUrgent">
                                <li id="into_urgent">进入应急</li>
                            </pop:Permission>
                            <pop:Permission ename="quitUrgent">
                                <li id="quit_urgent">取消应急</li>
                            </pop:Permission>
                            <pop:Permission ename="waitOildom">
                                <li id="wait_oildom">待进油区</li>
                            </pop:Permission>
                            <pop:Permission ename="intoOildom">
                                <li id="into_oildom">进油区</li>
                            </pop:Permission>
                            <pop:Permission ename="quitOildom">
                                <li id="quit_oildom">出油区</li>
                            </pop:Permission>
                            <pop:Permission ename="alterStatus">
                                <li id="alter_status">状态变更</li>
                            </pop:Permission>
                            <pop:Permission ename="unlockReset">
                                <li id="unlock_reset">开锁重置</li>
                            </pop:Permission>
                            <pop:Permission ename="authCodeVerify">
                                <li id="auth_verify">授权认证</li>
                            </pop:Permission>
                        </ul>
                    </li>
                </pop:Permission>
                <pop:Permission ename="changeStation">
                    <li id="change_station">远程换站</li>
                </pop:Permission>
            </ul>
        </div>
    </div>

    <div class="data-zone">
        <div class="map" id="bmap"></div>
        <div class="table">
            <div id="car_list" title="隐藏车辆列表"></div>
            <div id="car_info">
                <div class="monitor-log">
                    <div class="log-title">车辆监控日志</div>
                    <div class="log-box"></div>
                </div>
                <div class="table-list">
                    <div class="table-box c-table c-table--border">
                        <table cellspacing="0" cellpadding="0" border="0" role="c-table" data-height="336">
                            <colgroup>
                                <col name="" width="92">
                                <col name="" width="120">
                                <col name="" width="96">
                                <col name="" width="">
                                <col name="" width="60">
                                <col name="" width="100">
                                <col name="" width="">
                                <col name="" width="120">
                                <col name="" width="120">
                                <col name="" width="82">
                                <col name="" width="60">
                                <col name="" width="82">
                            </colgroup>
                            <thead>
                            <tr>
                                <th class="">
                                    <div class="cell">车牌号码</div>
                                </th>
                                <th class="">
                                    <div class="cell">所属公司</div>
                                </th>
                                <th class="">
                                    <div class="cell">GPS有效性</div>
                                </th>
                                <th class="">
                                    <div class="cell">经纬度</div>
                                </th>
                                <th class="">
                                    <div class="cell">速度</div>
                                </th>
                                <th class="">
                                    <div class="cell">方向</div>
                                </th>
                                <th class="">
                                    <div class="cell">最后有效坐标</div>
                                </th>
                                <th class="">
                                    <div class="cell">最后有效速度</div>
                                </th>
                                <th class="">
                                    <div class="cell">最后有效方向</div>
                                </th>
                                <th class="">
                                    <div class="cell">车辆状态</div>
                                </th>
                                <th class="">
                                    <div class="cell">报警</div>
                                </th>
                                <th class="">
                                    <div class="cell">在线状态</div>
                                </th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<div class="hidden-box container" id="monitor_conf">
    <div class="info-zone">
        <div class="base-info">
            <div class="info-title">
                实时监控
            </div>
            <table class="base-table">
                <%--
                <tr>
                    <td>配置范围</td>
                    <td>
                        <select class="editInfo" id="scope">
                            <option value=1>单部车辆</option>
                            <option value=2>运输公司</option>
                        </select>
                    </td>
                </tr>
                --%>
                <tr>
                    <td id="select_name">车牌号</td>
                    <td>
                        <input type="text" class="editInfo" id="carno" style="background: #e6e7e9;" readonly>
                        <%--
                        <select class="editInfo" id="carno">
                            <option value="">车牌号</option>
                        </select>
                        <select class="editInfo" id="company" style="display: none;"></select>
                        --%>
                    </td>
                </tr>
                <tr>
                    <td>轨迹上报间隔（秒）</td>
                    <td>
                        <input type="hidden" id="interval" value="5"/>
                    </td>
                </tr>
                <tr>
                    <td>监控时长（分钟）</td>
                    <td>
                        <input type="hidden" id="duration" value="30"/>
                    </td>
                </tr>
            </table>
        </div>
    </div>
    <div class="oper-zone">
        <input type="button" id="cancel" value="取消">
        <input type="button" id="confirm" value="确认">
    </div>
</div>
</body>

</html>
<!-- 数据模版 -->
<script id="table-tpl" type="text/html">
    <table cellspacing="0" cellpadding="0" border="0" class="" role="c-table" data-height="336">
        <colgroup>
            <col name="" width="92">
            <col name="" width="120">
            <col name="" width="96">
            <col name="" width="">
            <col name="" width="60">
            <col name="" width="100">
            <col name="" width="">
            <col name="" width="120">
            <col name="" width="120">
            <col name="" width="82">
            <col name="" width="60">
            <col name="" width="82">
        </colgroup>
        <thead>
        <tr>
            <th class="">
                <div class="cell">车牌号码</div>
            </th>
            <th class="">
                <div class="cell">所属公司</div>
            </th>
            <th class="">
                <div class="cell">GPS有效性</div>
            </th>
            <th class="">
                <div class="cell">经纬度</div>
            </th>
            <th class="">
                <div class="cell">速度</div>
            </th>
            <th class="">
                <div class="cell">方向</div>
            </th>
            <th class="">
                <div class="cell">最后有效坐标</div>
            </th>
            <th class="">
                <div class="cell">最后有效速度</div>
            </th>
            <th class="">
                <div class="cell">最后有效方向</div>
            </th>
            <th class="">
                <div class="cell">车辆状态</div>
            </th>
            <th class="">
                <div class="cell">报警</div>
            </th>
            <th class="">
                <div class="cell">在线状态</div>
            </th>
        </tr>
        </thead>
        <tbody>
        {{each data}}
        <tr id="car_{{$value.carId}}">
            <td class="">
                <div class="cell">{{$value.carNo}}</div>
            </td>
            <td class="">
                <div class="cell">{{$value.carCom}}</div>
            </td>
            <td class="">
                <div class="cell">{{$value.gps}}</div>
            </td>
            <td class="">
                <div class="cell">{{$value.coordinate}}</div>
            </td>
            <td class="">
                <div class="cell">{{$value.velocity}}</div>
            </td>
            <td class="">
                <div class="cell">{{$value.aspect}}</div>
            </td>
            <td class="">
                <div class="cell">{{$value.lastcoord}}</div>
            </td>
            <td class="">
                <div class="cell">{{$value.lastspeed}}</div>
            </td>
            <td class="">
                <div class="cell">{{$value.lastaspect}}</div>
            </td>
            <td class="">
                <div class="cell">{{$value.carStatus}}</div>
            </td>
            <td class="">
                <div class="cell">{{$value.alarm}}</div>
            </td>
            <td class="">
                <div class="cell">在线</div>
            </td>
        </tr>
        {{/each}}
        </tbody>
    </table>
</script>
<script src="../../resources/plugins/jquery-1.8.3.min.js"></script>
<script src="../../resources/plugins/ReconnectingWebSocket/reconnecting-websocket.js"></script>
<script src="../../resources/plugins/combo/jquery.combo.select.js"></script>
<%--<script src="../../resources/plugins/laydate/laydate.js"></script>--%>
<script src="../../resources/plugins/layer/layer.js"></script>
<script src="../../resources/plugins/jRange/jquery.range-min.js"></script>
<script src="../../resources/plugins/verify.js"></script>
<!--开源模版引擎： art-template@4.12.2 for browser | https://github.com/aui/art-template -->
<script src="../../resources/plugins/jqTable/js/plugins/artTemplate.js"></script>
<!-- 必要插件：固定列滚动需要用到，鼠标滚动兼容多浏览器 -->
<script src="../../resources/plugins/jqTable/js/jquery.mousewheel.min.js"></script>
<!-- 表格插件 -->
<script src="../../resources/plugins/jqTable/js/zipJs/jqTable.all.min.js"></script>
<!--加载百度地图api-->
<script src="https://api.map.baidu.com/api?v=3.0&ak=yMyX3teIjmYDWDceyUFTzikOLBOSClCt&s=1"></script>
<%--<script src="../../resources/plugins/BMapLib/TextIconOverlay.min.js"></script>--%>
<%--<script src="../../resources/plugins/BMapLib/MarkerClusterer.js"></script>--%>
<script src="../../resources/plugins/BMapLib/mapv.min.js"></script>
<script src="../../resources/js/car/carSupervise3.js"></script>