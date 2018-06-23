<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri='http://java.sun.com/jsp/jstl/functions' prefix='fn'%>
<%@ taglib prefix="pop" uri="/pop-tags" %>

<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>统计记录查看</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <script src="../../resources/plugins/jquery-1.8.3.min.js"></script>
    <script src="../../resources/js/base.js"></script>
    <script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=F0i6SrLmHquLVNLCqpExxPrj8mWVdFwx"></script>
    <script type="text/javascript" src="http://api.map.baidu.com/library/SearchInfoWindow/1.5/src/SearchInfoWindow_min.js"></script>
    <link rel="stylesheet" href="http://api.map.baidu.com/library/SearchInfoWindow/1.5/src/SearchInfoWindow_min.css" />
    <style>
        .map {
            width: 100%;
            height: 500px;
        }
    </style>
</head>

<body>
    <div>
        <table>
            <tr>
                <td>报警ID：</td>
                <td>${record.id}</td>
            </tr>
            <tr>
                <td>车牌号：</td>
                <td>${record.carNumber}</td>
            </tr>
            <tr>
                <td>时间：</td>
                <td>${record.alarmTime}</td>
            </tr>
            <tr>
                <td>经度：</td>
                <td>${record.longitude}</td>
            </tr>
            <tr>
                <td>纬度：</td>
                <td>${record.latitude}</td>
            </tr>
            <tr>
                <td>报警设备：</td>
                <td>
                    <c:if test="${record.deviceType==1}">
                        车载终端
                    </c:if>
                    <c:if test="${record.deviceType==1}">
                        锁
                    </c:if>
                    （${record.deviceId}）
                </td>
            </tr>
            <tr>
                <td>报警类型：</td>
                <td>${record.typeName}</td>
            </tr>
            <tr>
                <td>报警状态：</td>
                <td>${record.status}</td>
            </tr>
            <tr>
                <td>锁状态：</td>
                <td>${record.lockStatus}</td>
            </tr>
        </table>
    </div>

"<div style='margin:0;line-height:20px;padding:2px;'>" +
    "车牌号：&emsp;${record.carNumber}" +
    "车牌号：&emsp;${record.carNumber}" +
    "<br> 时间：&emsp;&emsp;" + new Date("${record.createDate}").format("yyyy-MM-dd HH:mm:ss") +
    "<br> 经度：&emsp;&emsp;${record.longitude}" +
    "<br> 纬度：&emsp;&emsp;${record.latitude}" +
    "<br> 报警类型：${record.typeName}" +
    "<br> 报警状态：${record.status}" +
    "<br> 锁状态：&emsp;${record.lockStatus}" +
    "</div>";
    <div class="map" id="bmap"></div>
    <script type="text/javascript">
        // 百度地图API功能
        var map = new BMap.Map("bmap"); // 创建Map实例
        var poi = new BMap.Point(${record.longitude}, ${record.latitude}); //以指定的经度和纬度创建一个地理点坐标
        map.centerAndZoom(poi, 16); // 初始化地图,设置中心点坐标和地图级别
        map.addControl(new BMap.NavigationControl()); //左上角，添加默认缩放平移控件
        map.addControl(new BMap.MapTypeControl()); //添加地图类型控件
        map.addControl(new BMap.ScaleControl({
            anchor: BMAP_ANCHOR_BOTTOM_RIGHT
        })); // 右下角，添加比例尺
        map.enableScrollWheelZoom(); //开启鼠标滚轮缩放
        var carIcon = new BMap.Icon(
        	<c:choose>
	            <c:when test="${record.status == '未消除报警'}">
	            	'../../resources/images/marker/车辆图标-64-红.png', //图像地址
	            </c:when>
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
                rotation: -${record.angle}, //旋转角度。正东方向0°，按顺时针方向表示角度，正南90°，正北-90°
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
        var content = "<div style='margin:0;line-height:20px;padding:2px;'>" +
            "车牌号：&emsp;${record.carNumber}" +
            "车牌号：&emsp;${record.carNumber}" +
            "<br> 时间：&emsp;&emsp;" + new Date("${record.createDate}").format("yyyy-MM-dd HH:mm:ss") +
            "<br> 经度：&emsp;&emsp;${record.longitude}" +
            "<br> 纬度：&emsp;&emsp;${record.latitude}" +
            "<br> 报警类型：${record.typeName}" +
            "<br> 报警状态：${record.status}" +
            "<br> 锁状态：&emsp;${record.lockStatus}" +
            "</div>";
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
        carMarker.addEventListener("click", function(e) {
            searchInfoWindow.open(carMarker);
        });
        map.addOverlay(carMarker); //向地图上添加车辆标注
    </script>
</body>

</html>