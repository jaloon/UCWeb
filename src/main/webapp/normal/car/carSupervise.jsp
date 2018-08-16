<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/functions' prefix='fn' %>
<%@ taglib prefix="pop" uri="/pop-tags" %>

<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8">
    <title>车辆配置及监控</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=center-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="../../resources/css/base.css">
    <link rel="stylesheet" href="../../resources/css/carSupervise.css">
    <link rel="stylesheet" href="../../resources/plugins/combo/jquery.combo.select.css">
    <link rel="stylesheet" href="../../resources/plugins/jRange/jquery.range.css">
    <script src="../../resources/plugins/jquery-1.8.3.min.js"></script>
    <script src="../../resources/plugins/ReconnectingWebSocket/reconnecting-websocket.min.js"></script>
    <script src="../../resources/plugins/combo/jquery.combo.select.js"></script>
    <script src="../../resources/plugins/json2.js"></script>
    <!--<script src="../../resources/plugins/laydate/laydate.js"></script>-->
    <script src="../../resources/plugins/layer/layer.js"></script>
    <script src="../../resources/plugins/jRange/jquery.range-min.js"></script>
    <script src="../../resources/plugins/verify.js"></script>
    <script src="../../resources/js/base.js"></script>
    <!--加载百度地图api-->
    <script type="text/javascript" src="https://api.map.baidu.com/api?v=3.0&ak=yMyX3teIjmYDWDceyUFTzikOLBOSClCt&s=1"></script>
    <!--<script src="../../resources/plugins/BMapLib/TextIconOverlay.min.js"></script>-->
    <!--<script src="../../resources/plugins/BMapLib/MarkerClusterer.js"></script>-->
    <script src="../../resources/plugins/BMapLib/mapv.min.js"></script>
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
        <!--<input type="button" class="search-btn button" id="search_btn" value="查询">-->
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
                            <li id="into_urgent">进入应急</li>
                            <li id="quit_urgent">取消应急</li>
                            <li id="wait_oildom">待进油区</li>
                            <li id="into_oildom">进油区</li>
                            <li id="quit_oildom">出油区</li>
                            <pop:Permission ename="alterStatus">
                                <li id="alter_status">状态变更</li>
                            </pop:Permission>
                            <pop:Permission ename="unlockReset">
                                <li id="unlock_reset">开锁重置</li>
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
        <script type="text/javascript">
            var isInitMap = false;
            // 百度地图API功能
            var map = new BMap.Map("bmap"); // 创建Map实例
            var localCity = new BMap.LocalCity();
            localCity.get(function (localCityResult) {
                if (isInitMap)  {
                    console.log("track point")
                    return;
                }
                console.log("local point")
                // center	Point	城市所在中心点
                // level	Number	展示当前城市的最佳地图级别，如果您在使用此对象时提供了map实例，则地图级别将根据您提供的地图大小进行调整
                // name	String	城市名称
                initMap(localCityResult.center, 12); // 初始化地图,设置中心点坐标和地图级别
                // console.log(localCityResult.center)
                isInitMap = true;
            });
            function initMap(centerPoint, zoom) {
                map.centerAndZoom(centerPoint, zoom);
                map.addControl(new BMap.NavigationControl()); //左上角，添加默认缩放平移控件
                map.addControl(new BMap.MapTypeControl()); //添加地图类型控件
                map.addControl(new BMap.ScaleControl({
                    anchor: BMAP_ANCHOR_BOTTOM_RIGHT
                })); // 右下角，添加比例尺
                map.enableScrollWheelZoom(); //开启鼠标滚轮缩放
            }
        </script>

        <div class="table">
            <div id="car_list" title="隐藏车辆列表"></div>
            <div id="car_info">
                <table width="100%">
                    <tr>
                        <td>
                            <div class='table-list'>
                                <table class="table-cont" width="100%">
                                    <thead class="table-head">
                                    <tr>
                                        <th class="car-num">车牌号码</th>
                                        <th class="car-company">所属公司</th>
                                        <!--<th class="car-type">车辆类型</th>-->
                                        <th class="car-coordinate">经纬度</th>
                                        <th class="car-velocity">速度</th>
                                        <th class="car-aspect">方向</th>
                                        <th class="car-status">车辆状态</th>
                                        <!--<th class="car-lock">锁状态</th>-->
                                        <th class="car-alarm">报警</th>
                                        <th class="car-online">在线状态</th>
                                    </tr>
                                    </thead>
                                    <tbody class="table-body"></tbody>
                                </table>
                            </div>
                        </td>
                        <td>
                            <div class="monitor-log">
                                <div class="log-title">车辆监控日志</div>
                                <div class="log-box"></div>
                            </div>
                        </td>
                    </tr>
                </table>
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
                <%--<tr>
                    <td>配置范围</td>
                    <td>
                        <select class="editInfo" id="scope">
                            <option value=1>单部车辆</option>
                            <option value=2>运输公司</option>
                        </select>
                    </td>
                </tr>--%>
                <tr>
                    <td id="select_name">车牌号</td>
                    <td>
                        <!--<input type="text" class="editInfo" id="carcom">-->
                        <select class="editInfo" id="carno">
                            <option value="">车牌号</option>
                        </select>
                        <select class="editInfo" id="company" style="display: none;"></select>
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
<script src="../../resources/js/car/carSupervise3.js"></script>