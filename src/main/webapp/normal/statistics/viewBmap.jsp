<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/functions' prefix='fn' %>
<%@ taglib prefix="pop" uri="/pop-tags" %>

<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8"/>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=center-width, initial-scale=1, maximum-scale=1">
    <title>统计记录查看</title>
    <script src="../../resources/js/base.js"></script>
    <script src="../../resources/plugins/jquery-1.8.3.min.js"></script>
    <script type="text/javascript"
            src="https://api.map.baidu.com/api?v=2.0&ak=F0i6SrLmHquLVNLCqpExxPrj8mWVdFwx&s=1"></script>
    <script type="text/javascript"
            src="https://api.map.baidu.com/library/SearchInfoWindow/1.5/src/SearchInfoWindow_min.js"></script>
    <link rel="stylesheet" href="https://api.map.baidu.com/library/SearchInfoWindow/1.5/src/SearchInfoWindow_min.css"/>
    <style>
        .map {
            width: 100%;
            height: 500px;
        }
    </style>
</head>

<body>
<div class="map" id="bmap"></div>
<script type="text/javascript">
    // 百度地图API功能
    var map = new BMap.Map("bmap"); // 创建Map实例
    var bd09 = wgs84tobd09(${record.longitude}, ${record.latitude});
    var poi = new BMap.Point(bd09[0], bd09[1]); //以指定的经度和纬度创建一个地理点坐标
    map.centerAndZoom(poi, 16); // 初始化地图,设置中心点坐标和地图级别
    map.addControl(new BMap.NavigationControl()); //左上角，添加默认缩放平移控件
    map.addControl(new BMap.MapTypeControl()); //添加地图类型控件
    map.addControl(new BMap.ScaleControl({
        anchor: BMAP_ANCHOR_BOTTOM_RIGHT
    })); // 右下角，添加比例尺
    map.enableScrollWheelZoom(); //开启鼠标滚轮缩放
    var carIcon = new BMap.Icon(
        <c:choose>
        <c:when test="${mode == 'alarm' && record.status == '未消除报警'}">
        '../../resources/images/marker/车辆图标-64-红.png', //图像地址
        </c:when>
        <%--<c:when test="${mode == 'remote'}">--%>
        <%--'../../resources/images/marker/车辆图标-64-黄.png',--%>
        <%--</c:when>--%>
        <c:when test="${mode == 'lock' && record.alarm != '无报警'}">
        '../../resources/images/marker/车辆图标-64-红.png',
        </c:when>
        <c:when test="${mode == 'seal' && record.alarm == '是'}">
        '../../resources/images/marker/车辆图标-64-红.png',
        </c:when>
        <c:when test="${mode == 'usage' && record.alarm == '是'}">
        '../../resources/images/marker/车辆图标-64-红.png',
        </c:when>
        <c:when test="${mode == 'event' && record.alarm == '是'}">
        '../../resources/images/marker/车辆图标-64-红.png',
        </c:when>
        <%--<c:when test="${(mode=='alarm' && record.status == '未消除报警') || (mode!='alarm' && mode!='remote' && fn:contains(record.alarm, '报警'))}">--%>
        <%--'../../resources/images/marker/车辆图标-64-红.png', //图像地址--%>
        <%--</c:when>--%>
        <c:otherwise>
        '../../resources/images/marker/车辆图标-64-黄.png',
        </c:otherwise>
        </c:choose>
        new BMap.Size(64, 30), //图像大小。以指定的宽度和高度创建一个矩形区域大小对象
        {
            anchor: new BMap.Size(32, 15) //图标的定位锚点。此点用来决定图标与地理位置的关系，是相对于图标左上角的偏移值，默认等于图标宽度和高度的中间值
        }
    ); //车辆图标。以给定的图像地址和大小创建图标对象实例
    var carMarker = new BMap.Marker(
        poi, //图像标注所在的地理位置
        {
            icon: carIcon, //标注所用的图标对象
            rotation: ${record.angle} - 90, //旋转角度
            title: '' //鼠标移到marker上的显示内容
        }
    ); //车辆标注。创建一个图像标注实例
    var carLabel = new BMap.Label(
        '${record.carNumber}', //文本标注内容
        {
            position: poi, //文本标注的地理位置
            offset: new BMap.Size(-32, -18) //文本标注的位置偏移值
        }
    ); //车辆标注信息。创建一个文本标注实例
    /**
     * 设置文本标注样式，该样式将作用于文本标注的容器元素上。
     * 其中styles为JavaScript对象常量，比如： setStyle({ color : "red", fontSize : "12px" })
     * 注意：如果css的属性名中包含连字符，需要将连字符去掉并将其后的字母进行大写处理，例如：背景色属性要写成：backgroundColor
     */
    carLabel.setStyle({
        color: "red",
        fontSize: "10px",
        height: "16px",
        lineHeight: "16px",
        fontFamily: "微软雅黑"
    });
    //信息窗内容
    var content = "<div style='margin:0;line-height:20px;padding:2px;'><table>"
        + "<tr><td>车牌号：</td><td>${record.carNumber}</td></tr>"
        + "<tr><td>GPS有效性：</td><td>" + parseGpsValid(${record.coorValid}) + "</td></tr>"
        + "<tr><td>经度：</td><td>${record.longitude}</td></tr>"
        + "<tr><td>纬度：</td><td>${record.latitude}</td></tr>"
        <c:if test="${mode!='remote'}">
        + "<tr><td>速度：</td><td>${record.velocity} km/h</td></tr>"
        + "<tr><td>方向：</td><td>" + angle2aspect(${record.angle}) + "</td></tr>"
        </c:if>
        <c:if test="${mode=='remote'}">
        + "<tr><td>操作时间：</td><td>${record.recordTime}</td></tr>"
        + "<tr><td>操作类型：</td><td>${record.typeName}</td></tr>"
        + "<tr><td>操作结果：</td><td>${record.statusName}</td></tr>"
        + "<tr><td>操作员：</td><td>${record.user.name}</td></tr>"
        </c:if>
        <c:if test="${mode=='alarm'}">
        + "<tr><td>报警时间：</td><td>${record.recordTime}</td></tr>"
        + "<tr><td>上报时间：</td><td>${record.alarmReportTime}</td></tr>"
        + "<tr><td>报警类型：</td><td>${record.typeName}</td></tr>"
        + "<tr><td>报警状态：</td><td>${record.status}</td></tr>"
        + "<tr><td style='vertical-align: top;'>锁状态：</td><td>${record.lockStatus}</td></tr>"
        </c:if>
        <c:if test="${mode=='lock'}">
        + "<tr><td>锁设备ID：</td><td>${record.lockId}</td></tr>"
        + "<tr><td>锁位置：</td><td>仓${record.storeId}-${record.seatName}-${record.seatIndex}</td></tr>"
        <%--+ "<tr><td>仓号：</td><td>${record.storeId}</td></tr>"--%>
        <%--+ "<tr><td>仓位：</td><td>${record.seatName}</td></tr>"--%>
        <%--+ "<tr><td>同仓位锁序号：</td><td>${record.seatIndex}</td></tr>"--%>
        + "<tr><td>动作时间：</td><td>${record.recordTime}</td></tr>"
        + "<tr><td>上报时间：</td><td>${record.changeReportTime}</td></tr>"
        + "<tr><td>锁动作：</td><td>${record.statusName}</td></tr>"
        + "<tr><td>报警类型：</td><td>${record.alarm}</td></tr>"
        </c:if>
        <c:if test="${mode=='seal'}">
        + "<tr><td>施解封时间：</td><td>${record.recordTime}</td></tr>"
        + "<tr><td>施解封类型：</td><td>" + parseSealType(${record.type}) + "</td></tr>"
        + "<tr><td>是否报警：</td><td>${record.alarm}</td></tr>"
        <c:if test="${record.alarm=='是'}">
        + "<tr><td style='vertical-align: top;'>报警类型：</td><td>${record.alarmType}</td></tr>"
        </c:if>
        + "<tr><td style='vertical-align: top;'>锁状态：</td><td>${record.lockStatus}</td></tr>"
        </c:if>
        <c:if test="${mode=='usage'}">
        + "<tr><td>时间：</td><td>${record.recordTime}</td></tr>"
        + "<tr><td>卡或设备类型：</td><td>${record.typeName}</td></tr>"
        + "<tr><td>卡或设备ID：</td><td>${record.devId}</td></tr>"
        + "<tr><td>是否报警：</td><td>${record.alarm}</td></tr>"
        <c:if test="${record.alarm=='是'}">
        + "<tr><td style='vertical-align: top;'>报警类型：</td><td>${record.alarmType}</td></tr>"
        </c:if>
        + "<tr><td style='vertical-align: top;'>锁状态：</td><td>${record.lockStatus}</td></tr>"
        </c:if>
        <c:if test="${mode=='reset'}">
        + "<tr><td>锁设备ID：</td><td>${record.lockId}</td></tr>"
        + "<tr><td>锁位置：</td><td>仓${record.storeId}-${record.seatName}-${record.seatIndex}</td></tr>"
        <%--+ "<tr><td>仓号：</td><td>${record.storeId}</td></tr>"--%>
        <%--+ "<tr><td>仓位：</td><td>${record.seatName}</td></tr>"--%>
        <%--+ "<tr><td>同仓位锁序号：</td><td>${record.seatIndex}</td></tr>"--%>
        + "<tr><td>重置时间：</td><td>${record.recordTime}</td></tr>"
        + "<tr><td>上报时间：</td><td>${record.resetReportTime}</td></tr>"
        + "<tr><td>重置状态：</td><td>" + parseResetStatus(${record.seatIndex}) + "</td></tr>"
        </c:if>
        <c:if test="${mode=='event'}">
        + "<tr><td>终端编号：</td><td>${record.terminalId}</td></tr>"
        + "<tr><td>事件类型：</td><td>" + parseEventType(${record.type}) +"</td></tr>"
        + "<tr><td>是否报警：</td><td>${record.alarm}</td></tr>"
        <c:if test="${record.alarm=='是'}">
        + "<tr><td style='vertical-align: top;'>报警类型：</td><td>${record.alarmType}</td></tr>"
        </c:if>
        + "<tr><td style='vertical-align: top;'>锁状态：</td><td>${record.lockStatus}</td></tr>"
        </c:if>
        + "</table></div>";
    var searchInfoWindow = new BMapLib.SearchInfoWindow(map, content, {
        title: "报警信息", //标题
        width: 290, //宽度
        //height: 105, //高度
        panel: "panel", //检索结果面板
        enableAutoPan: true, //自动平移
        searchTypes: [
            BMAPLIB_TAB_SEARCH, //周边检索
            BMAPLIB_TAB_TO_HERE, //到这里去
            BMAPLIB_TAB_FROM_HERE //从这里出发
        ]
    }); //创建检索信息窗口对象
    searchInfoWindow.open(carMarker);
    carMarker.setLabel(carLabel); //为图像标注添加文本标注
    carMarker.addEventListener("click", function (e) {
        searchInfoWindow.open(carMarker);
    });
    map.addOverlay(carMarker); //向地图上添加车辆标注

    function parseGpsValid(gpsValid) {
        if (gpsValid == undefined || gpsValid == null) {
            return "数据异常";
        }
        if (gpsValid) {
            return "有效";
        }
        return "无效";
    }

    /**
     * 开锁重置状态
     * @param {number} status 状态值
     * @returns {string} 重置状态
     */
    function parseResetStatus(status) {
        switch (status) {
            case 0:
                return "未完成";
            case 1:
                return "远程操作请求中";
            case 2:
                return "远程操作完成";
            case 3:
                return "车台主动重置完成";
            default:
                return "未知状态[" + status + "]";
        }
    }

    /**
     * 车台事件
     * @param {number} type 事件类型值
     * @returns {string} 事件类型
     */
    function parseEventType(type) {
        if (type == 1) {
            return "终端断电";
        }
        return "未知类型[" + type + "]";
    }

    /**
     * 施解封类型
     * @param type
     * @returns {string}
     */
    function parseSealType(type) {
        switch (type) {
            case 1:
                return "进油库";
            case 2:
                return "出油库";
            case 3:
                return "进加油站";
            case 4:
                return "出加油站";
            case 5:
                return "进入应急";
            case 6:
                return "取消应急";
            case 7:
                return "状态强制变更";
            case 8:
                return "待进油区";
            case 9:
                return "进油区";
            case 10:
                return "出油区";
            default:
                return "未知(" + type + ")";
        }
    }
</script>
</body>

</html>