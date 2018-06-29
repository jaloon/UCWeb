var center;
if (navigator.geolocation) {
    var geolocation = new BMap.Geolocation();
    geolocation.getCurrentPosition(function (geolocationResult) {
            switch (this.getStatus()) {
                case BMAP_STATUS_SUCCESS: // 检索成功。对应数值“0”
                    // point	Point	定位坐标点
                    // accuracy	Number	定位精度，单位为米
                    // address	AddressComponent	根据定位坐标点解析出的地址信息，可能为空（3.0新增
                    center = geolocationResult.point;
                    break;
                case BMAP_STATUS_UNKNOWN_LOCATION: // 位置结果未知。对应数值“2”
                    getLocalCity();
                    break;
                case BMAP_STATUS_PERMISSION_DENIED: // 没有权限。对应数值“6”
                    getLocalCity();
                    break;
                case BMAP_STATUS_TIMEOUT: // 超时。对应数值“8”
                    getLocalCity();
                    break;
                default:
                    getLocalCity();
                    break;
            }
        }
    )
} else {
    console.log("浏览器不支持地理定位。");
    getLocalCity();
}

function getLocalCity() {
    var localCity = new BMap.LocalCity();
    localCity.get(function (localCityResult) {
        // center	Point	城市所在中心点
        // level	Number	展示当前城市的最佳地图级别，如果您在使用此对象时提供了map实例，则地图级别将根据您提供的地图大小进行调整
        // name	String	城市名称
        center = localCityResult.center;
    });
}

var GeometryType = {
    Point: "Point",
    LineString: "LineString",
    Polygon: "Polygon",
    MultiPoint: "MultiPoint",
    MultiLineString: "MultiLineString",
    MultiPolygon: "MultiPolygon"
}

// options通用的属性:
var options = {
    zIndex: 1, // 层级
    size: 5, // 大小值
    unit: 'px', // 'px': 以像素为单位绘制,默认值。'm': 以米制为单位绘制，会跟随地图比例放大缩小
    mixBlendMode: 'normal', // 不同图层之间的叠加模式，参考[https://developer.mozilla.org/en-US/docs/Web/CSS/mix-blend-mode](https://developer.mozilla.org/en-US/docs/Web/CSS/mix-blend-mode)
    fillStyle: 'rgba(200, 200, 50, 1)', // 填充颜色
    strokeStyle: 'rgba(0, 0, 255, 1)', // 描边颜色
    lineWidth: 4, // 描边宽度
    globalAlpha: 1, // 透明度
    globalCompositeOperation: 'lighter', // 颜色叠加方式
    coordType: 'bd09ll', // 可选百度墨卡托坐标类型bd09mc和百度经纬度坐标类型bd09ll(默认)
    shadowColor: 'rgba(255, 255, 255, 1)', // 投影颜色
    shadowBlur: 35,  // 投影模糊级数
    updateCallback: function (time) { // 重绘回调函数，如果是时间动画、返回当前帧的时间
    },
    shadowOffsetX: 0,
    shadowOffsetY: 0,
    context: '2d', // 可选2d和webgl，webgl目前只支持画simple模式的点和线
    lineCap: 'butt',
    lineJoin: 'miter',
    miterLimit: 10,
    methods: { // 一些事件回调函数
        click: function (item) { // 点击事件，返回对应点击元素的对象值
            console.log(item);
        },
        mousemove: function (item) { // 鼠标移动事件，对应鼠标经过的元素对象值
            console.log(item);
        }
    },
    animation: {
        type: 'time', // 按时间展示动画
        stepsRange: { // 动画时间范围,time字段中值
            start: 0,
            end: 100
        },
        trails: 10, // 时间动画的拖尾大小
        duration: 5, // 单个动画的时间，单位秒
    }
}
options.draw = {
    simple: "simple" // 最直接的方式绘制点线面
    , time: "time" // 按时间字段来动画展示数据
    , heatmap: "heatmap" // 热力图展示
    , grid: "grid" // 网格状展示
    , honeycomb: "honeycomb" // 蜂窝状展示
    , bubble: "bubble" // 用不同大小的圆来展示
    , intensity: "intensity" // 根据不同的值对应按渐变色中颜色进行展示
    , category: "category" // 按不同的值进行分类，并使用对应的颜色展示
    , choropleth: "choropleth" // 按不同的值区间进行分类，并使用对应的颜色展示
    , text: "text" // 展示文本
    , icon: "icon" // 展示icon
}