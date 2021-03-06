var ws = null;
var wsUrl;
var userId;
var validCarCount = 0;
var sessionId;
var user;
var userJson;
var focusId;
var realtimeId;
var focusCancelOrder;

var isInitMap = false;
// 百度地图API功能
var map = new BMap.Map("bmap"); // 创建Map实例
var localCity = new BMap.LocalCity();
localCity.get(function (localCityResult) {
    if (isInitMap) {
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

var point_layer, icon_layer, text_layer;
var point_dataSet, icon_dataSet, text_dataSet;

var car_pos_cache = new Map();
var car_deg_cache = new Map();
var car_img_cache = new Map();
var car_text_cache = new Map();
var car_list_cache = new Map();

var point_opts = {
    fillStyle: 'rgba(255, 50, 50, 0.6)',
    // shadowColor: 'rgba(255, 50, 50, 1)',
    // shadowBlur: 1,
    // globalCompositeOperation: 'lighter',
    methods: {
        click: openFocusLayer
    },
    size: 2,
    draw: 'simple'
};
var icon_opts = {
    draw: 'icon',
    methods: {
        click: openFocusLayer
    }
};
var text_opts = {
    draw: 'text',
    fillStyle: 'red',
    textAlign: 'center',
    avoid: true, // 开启文本标注避让
    textBaseline: 'middle',
    offset: { // 文本便宜值
        x: 0,
        y: 0
    }
};

var GeometryType = {
    Point: "Point",
    LineString: "LineString",
    Polygon: "Polygon",
    MultiPoint: "MultiPoint",
    MultiLineString: "MultiLineString",
    MultiPolygon: "MultiPolygon"
};

var alarm_car_img = new Image();
alarm_car_img.src = '../../resources/images/marker/车辆图标-32-红.png';
var normal_car_img = new Image();
normal_car_img.src = '../../resources/images/marker/车辆图标-32-黄.png';
var invalid_car_img = new Image();
invalid_car_img.src = '../../resources/images/marker/车辆图标-32-灰.png';

function openFocusLayer(item) {
    if (item != null) {
        console.log(item);
        layer.open({
            type: 2,
            title: ['车辆轨迹查看', 'font-size:14px;color:#ffffff;background:#478de4;'],
            // shadeClose: true,
            shade: 0.6,
            resize: false,
            area: ['800px', '560px'],
            content: '../../normal/car/monitor/carMonitorFocusTrack.html?wsUrl=' + wsUrl +
            '&carId=' + item.carid + '&carNumber=' + encodeURIComponent(item.carno) +
            '&parentSession=' + sessionId + '&user=' + encodeURIComponent(userJson),
            end: function () {
                ws.send(focusCancelOrder);
            }
        });
    }
}

/**
 * 图随点移
 * @param longitude
 * @param latitude
 */
function panTo(longitude, latitude) {
    var bd09 = wgs84tobd09(longitude, latitude);
    var point = new BMap.Point(bd09[0], bd09[1]);
    map.panTo(point);
    map.setZoom(18);
}

/**
 * 更新视图
 */
function upView() {
    var point_data = [];
    var icon_data = [];
    var text_data = [];

    var bounds = map.getBounds(); // 返回map可视区域，以地理坐标表示
    var sw = bounds.getSouthWest(); // 返回矩形区域的西南角
    var ne = bounds.getNorthEast(); // 返回矩形区域的东北角
    var swlng = sw.lng,
        swlat = sw.lat,
        nelng = ne.lng,
        nelat = ne.lat;

    car_pos_cache.forEach(function (pos, carId, mapObj) {
        if (pos[0] > swlng && pos[0] < nelng && pos[1] > swlat && pos[1] < nelat && onlineCarIds.indexOf(carId) >= 0) {
            var geometry = {
                type: GeometryType.Point,
                coordinates: pos
            };
            var text = car_text_cache.get(carId);
            point_data.push({
                carid: carId,
                carno: text,
                geometry: geometry
            });
            icon_data.push({
                carid: carId,
                carno: text,
                geometry: geometry,
                deg: car_deg_cache.get(carId),
                icon: car_img_cache.get(carId)
            });
            text_data.push({
                geometry: geometry,
                text: text
            });
        }
    });
    show(point_data, icon_data, text_data);
}

/**
 * 展示视图
 * @param point_data
 * @param icon_data
 * @param text_data
 */
function show(point_data, icon_data, text_data) {
    if (point_data.length > 100) {
        if (icon_layer != undefined) {
            icon_layer.hide();
        }
        if (text_layer != undefined) {
            text_layer.hide();
        }
        if (point_layer == undefined) {
            point_dataSet = new mapv.DataSet(point_data);
            point_layer = new mapv.baiduMapLayer(map, point_dataSet, point_opts);
        } else {
            point_dataSet.set(point_data);
            point_layer.show();
        }
    } else {
        if (point_layer != undefined) {
            point_layer.hide();
        }

        if (icon_layer == undefined) {
            icon_dataSet = new mapv.DataSet(icon_data);
            icon_layer = new mapv.baiduMapLayer(map, icon_dataSet, icon_opts);
        } else {
            icon_dataSet.set(icon_data);
            icon_layer.show();
        }

        if (text_layer == undefined) {
            text_dataSet = new mapv.DataSet(text_data);
            text_layer = new mapv.baiduMapLayer(map, text_dataSet, text_opts);
        } else {
            text_dataSet.set(text_data);
            text_layer.show();
        }
    }
}

var onlineCarIds = [];
// var $tbody = $(".table-body");

function getCarIcon(gpsValid, alarm) {
    if (!gpsValid) {
        return invalid_car_img;
    }
    if (alarm == "是") {
        return alarm_car_img;
    }
    return normal_car_img;
}

function flushTrack(tracks) {
    tracks.forEach(function (track) {
        var carId = track.carId;
        var lng = track.lastValidLongitude;
        var lat = track.lastValidLatitude;
        // var pos = [lng, lat];
        var pos = wgs84tobd09(lng, lat);
        var deg = track.lastValidAngle - 90;
        var gpsValid = track.gpsValid;
        var img = getCarIcon(gpsValid, track.alarm);
        var text = track.carNumber;
        if (pos[0] != 0 && pos[1] != 0) {
            car_pos_cache.set(carId, pos);
        }
        car_deg_cache.set(carId, deg);
        car_img_cache.set(carId, img);
        car_text_cache.set(carId, text);

        if (onlineCarIds.indexOf(carId) >= 0) {
            car_list_cache.set(carId, {
                carId: carId,
                carNo: text,
                gps: gpsValid ? "有效" : "无效",
                coordinate: track.longitude + ", " + track.latitude,
                velocity: track.velocity,
                aspect: angle2aspect(track.angle),
                lastcoord: lng + ", " + lat,
                lastspeed: track.lastValidSpeed,
                lastaspect: angle2aspect(track.lastValidAngle),
                carStatus: track.carStatus,
                alarm: track.alarm
            });
        }
    });
    // 更新地图
    upView();

    var jsonData = [];
    car_list_cache.forEach(function (carInfo, carId, mapObj) {
        jsonData.push(carInfo);
    });
    // 更新表格数据
    var tableHtml = template('table-tpl', {data: jsonData});
    $('.c-table').eq(0).data('table').updateHtml(tableHtml);
}

function dealOnlineCarsCache(onlineCars) {
    onlineCars.forEach(function (carId) {
        if (onlineCarIds.indexOf(carId) < 0) {
            onlineCarIds.push(carId);
        }
    });
}

function flushOnline(onlineInfo) {
    var carId = onlineInfo.carId;
    var carNo = onlineInfo.carNo;
    var online = onlineInfo.online;
    var onlineIndex = onlineCarIds.indexOf(carId);
    if (online == 0 && onlineIndex > -1) { // 离线
        onlineCarIds.splice(onlineIndex, 1);
        reFreshLog({
            fail: 0,
            task: '',
            result: carNo + '离线'
        });
        if (car_list_cache.get(carId) != undefined) {
            car_list_cache.delete(carId);
            car_pos_cache.delete(carId);
            car_deg_cache.delete(carId);
            car_img_cache.delete(carId);
            car_text_cache.delete(carId);
            $("#car_" + carId).remove();
        }
        upView();
        return;
    }
    if (online == 1) { // 在线
        if (onlineIndex == -1) {
            onlineCarIds.push(carId);
            reFreshLog({
                fail: 0,
                task: '',
                result: carNo + '上线'
            });
        }
    }
}



var logCount = 0;

function reFreshLog(log) {
    var timestamp = "<span class='log-time'>[" + new Date().format("yyyy-MM-dd HH:mm:ss") + "]</span>";
    var task = isNull(log.task) ? "" : "[" + log.task + "]";
    var result = "<span " + (log.fail > 0 ? "class='log-fail'>" : "class='log-done'>") + log.result + "</span>";
    var logHtml = "<div class='log-content'>" + timestamp + task + "&nbsp;&nbsp;" + result;
    var $logBox = $(".log-box");
    $logBox.prepend(logHtml);
    if (logCount < 5000) {
        logCount++;
    } else {
        $logBox.children().last().remove();
    }
}

$(function () {
    $('[role="c-table"]').jqTable();

    // iframe高度自适应
    $(window).resize(function () {
        if ($('#car_info').is(':hidden')) {
            $(".map").height($(window).height() - 88);
        } else {
            $(".map").height($(window).height() - 90 - $("#car_info").height());
        }
    }).resize();

    var navWidth = $("#nav").width();
    $(".search-zone").css("min-width", (400 + navWidth) + "px");

    $.getJSON("../../../manage/car/selectCars.do", "scope=0",
        function (data, textStatus, jqXHR) {
            var selectObj = $('#search_text');
            var cars = data.car;
            validCarCount = cars.length;
            if (validCarCount == 0) {
                selectObj.hide();
                var $hiddenCar = $("#hidden_car");
                $hiddenCar.css({
                    display: 'line-block',
                    opacity: 1,
                    color: '#d80e0e'
                });
                $hiddenCar.val("当前账号权限下无可查询车辆！");
                // $("#search_btn").attr('disabled', true);
                layer.alert("当前账号权限下无可查询车辆！", {
                    icon: 0,
                    title: ['警告', 'font-size:14px;color:#ffffff;background:#478de4;']
                });
            }
            // var realtimeSelectObj = $("#carno");
            selectObj.append(data.com);
            var groupObj;
            var carOpts = "";
            for (var i = 0; i < validCarCount; i++) {
                var car = cars[i];
                groupObj = $("#com_" + car.comId);
                var carOpt = "<option value = '" + car.carNumber + "'>" + car.carNumber + "</option>";
                carOpts += carOpt;
                groupObj.append(carOpt);
            }
            // realtimeSelectObj.append(carOpts);
            selectObj.comboSelect();
            selectObj.hide();
            $("#hidden_car").show();
            selectObj.closest(".combo-select").css({
                position: 'absolute',
                'z-index': 100000,
                left: '116px',
                top: '30px',
                width: '266px',
                height: '34px',
                'font-size': '16px',
                "margin-bottom": "0px"
            });
            selectObj.siblings(".combo-input").height(10);

            // realtimeSelectObj.comboSelect();
            // realtimeSelectObj.hide();
            // realtimeSelectObj.closest(".combo-select").css({
            //     width: '346px',
            //     height: '28px',
            //     'z-index': 10000,
            //     "margin-bottom": "0px"
            // });
            // realtimeSelectObj.siblings(".combo-dropdown").css("max-height", "180px");
            // realtimeSelectObj.siblings(".combo-input").height(2);
        }
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

    userId = getUrlParam("user");
    var ctx = getUrlParam("ctx");
    var httpProtocol = "http";
    var wsProtocol = "ws";
    if ("https:" === document.location.protocol || location.href.indexOf("https") > -1) {
        httpProtocol = "https";
        wsProtocol = "wss";
    }
    wsUrl = window.location.host + ctx;
    if (window.console) console.log("wsUrl: " + wsUrl);

    var order = {
        biz: 'general',
        user: userId
    };
    var orderText = JSON.stringify(order);

    function createWebSocket() {
        if ('WebSocket' in window) {
            ws = new ReconnectingWebSocket(wsProtocol + "://" + wsUrl + "/track");
            // } else if ('MozWebSocket' in window) {
            //     ws = new MozWebSocket("ws://" + wsUrl + "/track");
        } else {
            ws = new SockJS(httpProtocol + "://" + wsUrl + "/sockjs/track");
        }
    }

    createWebSocket();

    ws.onopen = function (event) {
        if (window.console) console.log("websocket connected");
        ws.send(orderText);
    };
    ws.onmessage = function (event) {
        if (window.console) console.log("websocket receive message");
        var receive = event.data;
        // if (receive != undefined) return;
        if (receive == "repeat") {
            layer.confirm('获取用户信息失败，是否刷新页面重试？', {
                icon: 0,
                title: ['获取用户信息失败', 'font-size:14px;color:#ffffff;background:#478de4;']
            }, function () {
                location.reload(true);
            });
        } else {
            var receiveObj = JSON.parse(receive);
            var biz = receiveObj.biz;
            switch (biz) {
                case "onlineCache":
                    var onlineCars = receiveObj.cars;
                    dealOnlineCarsCache(onlineCars);
                    break;
                case "online":
                    flushOnline(receiveObj);
                    break;
                case "track":
                    flushTrack([receiveObj]);
                    break;
                case "tracks":
                    // console.log(receive)
                    var tracks = receiveObj.tracks;
                    if (!isInitMap) {
                        var track = tracks[0];
                        var bd09 = wgs84tobd09(track.lastValidLongitude, track.lastValidLatitude);
                        var centerPoint = new BMap.Point(bd09[0], bd09[1]);
                        initMap(centerPoint, 18);
                        isInitMap = true;
                    }
                    flushTrack(tracks);
                    break;
                case "session":
                    sessionId = receiveObj.sessionId;
                    user = {
                        id: receiveObj.userId,
                        account: receiveObj.userAccount,
                        name: receiveObj.userName
                    };
                    userJson = JSON.stringify(user);
                    break;
                case "focus":
                    focusId = receiveObj.sessionId;
                    focusCancelOrder = JSON.stringify({
                        biz: 'focus',
                        bizType: 'cancel',
                        session: focusId,
                        user: userJson,
                        token: generateUUID()
                    });
                    break;
                case "realtime":
                    realtimeId = receiveObj.sessionId;
                    break;
                case "log":
                    reFreshLog(receiveObj);
                    break;
                default:
                    break;
            }
        }
    };
    ws.onerror = function (event) {
        if (window.console) console.log("websocket error");
    };
    ws.onclose = function (event) {
        if (window.console) console.log("websocket closed： " + event);
        createWebSocket();
    };

    $("#search_btn").click(function () {
        if (validCarCount == 0) {
            layer.alert("当前账号权限下无可用车辆！", {icon: 0, title: ['警告', 'font-size:14px;color:#ffffff;background:#478de4;']});
            return;
        }
        var carNumber = trimAll($("#search_text").val());
        if (!isCarNo(carNumber)) {
            layer.alert('车牌号不正确，请输入一个完整的车牌号！', {icon: 2}, function (index2) {
                layer.close(index2);
                $("#search_text").select();
            });
        }
    });

    $("#car_list").click(function () {
        if ($('#car_info').is(':hidden')) {
            $('#car_info').show();
            $('#car_list').css({
                // background: 'url(../../resources/images/operate/hide.png)'
                transform: 'rotate(0deg)'
            });
            $('#car_list').attr('title', '隐藏车辆列表');
            $(".map").height($(window).height() - 90 - $("#car_info").height());
        } else {
            $('#car_info').hide();
            $('#car_list').css({
                // background: 'url(../../resources/images/operate/show.png)'
                transform: 'rotate(180deg)'
            });
            $('#car_list').attr('title', '显示车辆列表');
            $(".map").height($(window).height() - 88);
        }
    });

    map.addEventListener("zoomend", upView); //地图缩放事件
    map.addEventListener("moveend", upView); //地图移动事件

    /** 设备绑定 */
    // 车载终端绑定
    $("#bind_terminal").click(function () {
        if (validCarCount == 0) {
            layer.alert("当前账号权限下无可操作车辆！", {icon: 0, title: ['警告', 'font-size:14px;color:#ffffff;background:#478de4;']});
            return;
        }
        var carNumber = trimAll($("#search_text").val());
        if (carNumber == "") {
            layer.alert('未选择系统已有车辆！', {icon: 0}, function (index2) {
                layer.close(index2);
                $("#search_text").focus();
            });
            return;
        }
        layer.open({
            type: 2,
            title: ['设备绑定', 'font-size:14px;color:#ffffff;background:#478de4;'],
            // shadeClose: true,
            shade: 0.8,
            resize: false,
            area: ['540px', '435px'],
            content: 'bind/carBind.html?carNumber=' + encodeURI(carNumber)
        });
    });
    // 锁绑定
    $("#bind_lock").click(function () {
        if (validCarCount == 0) {
            layer.alert("当前账号权限下无可操作车辆！", {icon: 0, title: ['警告', 'font-size:14px;color:#ffffff;background:#478de4;']});
            return;
        }
        var carNumber = trimAll($("#search_text").val());
        if (carNumber == "") {
            layer.alert('未选择系统已有车辆！', {icon: 0}, function (index2) {
                layer.close(index2);
                $("#search_text").focus();
            });
            return;
        }
        layer.open({
            type: 2,
            title: ['设备绑定', 'font-size:14px;color:#ffffff;background:#478de4;'],
            // shadeClose: true,
            shade: 0.8,
            resize: false,
            area: ['540px', '435px'],
            content: 'bind/lockBind.html?carNumber=' + encodeURI(carNumber)
        });
    });

    /** 车载终端配置 */
    // GPS配置
    $("#gps_config").click(function () {
        if (validCarCount == 0) {
            layer.alert("当前账号权限下无可操作车辆！", {icon: 0, title: ['警告', 'font-size:14px;color:#ffffff;background:#478de4;']});
            return;
        }
        layer.open({
            type: 2,
            title: ['车载终端配置', 'font-size:14px;color:#ffffff;background:#478de4;'],
            // shadeClose: true,
            shade: 0.8,
            resize: false,
            area: ['540px', '435px'],
            content: 'conf/gpsConfig.html'
        });
    });
    // 车台功能启用
    $("#func_enable").click(function () {
        if (validCarCount == 0) {
            layer.alert("当前账号权限下无可操作车辆！", {icon: 0, title: ['警告', 'font-size:14px;color:#ffffff;background:#478de4;']});
            return;
        }
        layer.open({
            type: 2,
            title: ['车载终端配置', 'font-size:14px;color:#ffffff;background:#478de4;'],
            // shadeClose: true,
            shade: 0.8,
            resize: false,
            area: ['540px', '435px'],
            content: 'conf/funcEnable.html'
        });
    });
    // 车台软件升级
    $("#soft_upgrade").click(function () {
        if (validCarCount == 0) {
            layer.alert("当前账号权限下无可操作车辆！", {icon: 0, title: ['警告', 'font-size:14px;color:#ffffff;background:#478de4;']});
            return;
        }
        layer.open({
            type: 2,
            title: ['车载终端配置', 'font-size:14px;color:#ffffff;background:#478de4;'],
            // shadeClose: true,
            shade: 0.8,
            resize: false,
            area: ['540px', '435px'],
            content: 'conf/softUpgrade.html'
        });
    });

    // 车台取消升级
    $("#cancel_upgrade").click(function () {
        if (validCarCount == 0) {
            layer.alert("当前账号权限下无可操作车辆！", {icon: 0, title: ['警告', 'font-size:14px;color:#ffffff;background:#478de4;']});
            return;
        }
        layer.open({
            type: 2,
            title: ['车载终端配置（取消升级）', 'font-size:14px;color:#ffffff;background:#478de4;'],
            // shadeClose: true,
            shade: 0.8,
            resize: false,
            area: ['640px', '600px'],
            // area: '640px',
            content: 'conf/cancelUpgrade.html'
        });
    });

    /** 实时监控 */

    // $.getJSON("../../manage/transcom/getCompanyList.do",
    //     function (data, textStatus, jqXHR) {
    //         var selectObj = $('#company');
    //         var comHtml = "";
    //         for (var i = 0, len = data.length; i < len; i++) {
    //             var company = data[i];
    //             comHtml += "<option value=" + company.id + ">" + company.name + "</option>";
    //         }
    //         selectObj.append(comHtml);
    //     }
    // ).error(function (XMLHttpRequest, textStatus, errorThrown) {
    //     if (XMLHttpRequest.readyState == 4) {
    //         var http_status = XMLHttpRequest.status;
    //         if (http_status == 0 || http_status > 600) {
    //             location.reload(true);
    //         } else if (http_status == 200) {
    //             if (textStatus == "parsererror") {
    //                 layer.alert("应答数据格式解析错误！")
    //             } else {
    //                 layer.alert("http response error: " + textStatus)
    //             }
    //         } else {
    //             layer.alert("http connection error: status[" + http_status + "], " + XMLHttpRequest.statusText)
    //         }
    //     }
    // });
    //
    // $("#scope").change(function () {
    //     var scope = $("#scope").val();
    //     if (scope == 1) {
    //         $("#select_name").html("车牌号");
    //         $("#company").hide();
    //         $("#carno").closest(".combo-select").show();
    //         // $("#carcom").replaceWith("<input type=\"text\" class=\"editInfo\" id=\"carcom\">");
    //     } else {
    //         $("#select_name").html("运输公司名称");
    //         $("#carno").closest(".combo-select").hide();
    //         $("#company").show();
    //         // $("#carcom").replaceWith(comHtml);
    //     }
    // });

    $('#interval').jRange({
        from: 1,
        to: 10,
        step: 1,
        scale: [1, 4, 7, 10],
        format: '%s',
        width: 348,
        snap: true,
        showLabels: true,
        showScale: true
    });

    $('#duration').jRange({
        from: 1,
        to: 60,
        step: 1,
        scale: [1, 20, 40, 60],
        format: '%s',
        width: 348,
        snap: true,
        showLabels: true,
        showScale: true
    });

    var realtimeLayer;

    $("#realtime_monitor").click(function () {
        if (validCarCount == 0) {
            layer.alert("当前账号权限下无可操作车辆！", {icon: 0, title: ['警告', 'font-size:14px;color:#ffffff;background:#478de4;']});
            return;
        }
        var carNumber = trimAll($("#search_text").val());
        if (carNumber == "") {
            layer.alert('未选择系统已有车辆！', {icon: 0}, function (index2) {
                layer.close(index2);
                $("#search_text").focus();
            });
            return;
        }
        $("#carno").val(carNumber);
        realtimeLayer = layer.open({
            type: 1,
            title: ['车辆实时监控', 'font-size:14px;color:#ffffff;background:#478de4;'],
            // shadeClose: true,
            shade: 0.8,
            area: ['540px', '435px'],
            resize: false,
            content: $('#monitor_conf')
        });
    });

    $("#cancel").click(function () {
        layer.close(realtimeLayer);
    });

    $("#confirm").click(function () {
        // var scope = $("#scope").val();
        var carNumber = "";
        var comId = 0;
        // if (scope == 1) {
        carNumber = trimAll($("#carno").val());
        if (carNumber == "") {
            layer.alert('未选择系统已有车辆！', {icon: 2}, function (index2) {
                layer.close(index2);
                $("#carno").focus();
            });
            return;
        }
        // } else {
        //     comId = $("#company").val();
        // }
        var interval = $("#interval").val();
        var duration = $("#duration").val();
        layer.open({
            type: 2,
            title: ['车辆实时监控', 'font-size:14px;color:#ffffff;background:#478de4;'],
            // shadeClose: true,
            shade: 0.6,
            resize: false,
            area: ['800px', '560px'],
            content: '../../normal/car/monitor/carMonitorRealtime.html?wsUrl=' + wsUrl + '&car=' + encodeURI(carNumber) +
            '&comId=' + comId + '&interval=' + interval + '&duration=' + duration +
            '&parentSession=' + sessionId + '&user=' + encodeURIComponent(userJson),
            end: function () {
                var order = {
                    biz: 'realtime',
                    bizType: 'cancel',
                    session: realtimeId,
                    user: userJson,
                    token: generateUUID()
                };
                ws.send(JSON.stringify(order));
            }
        });
        layer.close(realtimeLayer);
    });

    /** 车辆远程控制 */
    function remoteControl(mode) {
        if (validCarCount == 0) {
            layer.alert("当前账号权限下无可操作车辆！", {icon: 0, title: ['警告', 'font-size:14px;color:#ffffff;background:#478de4;']});
            return;
        }
        var carNumber = trimAll($("#search_text").val());
        if (carNumber == "") {
            layer.alert('未选择系统已有车辆！', {icon: 0}, function (index2) {
                layer.close(index2);
                $("#search_text").focus();
            });
            return;
        }
        $.getJSON("../../manage/car/getCarByNo.do", encodeURI("carNo=" + carNumber),
            function (data) {
                if (isNull(data)) {
                    layer.alert(carNumber + '不在车辆列表中，请重新选择车辆或先将' + carNumber + '添加到车辆列表中再绑定车载终端。');
                    return;
                }
                if (data.vehicleDevice == null || data.vehicleDevice.deviceId == null || data.vehicleDevice.deviceId == 0) {
                    layer.alert(carNumber + '未绑定车载终端！', {icon: 5});
                    return;
                }
                layer.open({
                    type: 2,
                    title: ['车辆远程控制', 'font-size:14px;color:#ffffff;background:#478de4;'],
                    // shadeClose: true,
                    shade: 0.8,
                    resize: false,
                    area: ['540px', '435px'],
                    content: '../../manage/car/carStatusDispatch.do?' + encodeURI('mode=' + mode + '&carNumber=' + carNumber)
                });
            }
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
    }

    $("#into_oildepot").click(function () {
        if (validCarCount == 0) {
            layer.alert("当前账号权限下无可操作车辆！", {icon: 0, title: ['警告', 'font-size:14px;color:#ffffff;background:#478de4;']});
            return;
        }
        remoteControl(1);
    });
    $("#quit_oildepot").click(function () {
        if (validCarCount == 0) {
            layer.alert("当前账号权限下无可操作车辆！", {icon: 0, title: ['警告', 'font-size:14px;color:#ffffff;background:#478de4;']});
            return;
        }
        remoteControl(2);
    });
    $("#into_gasstation").click(function () {
        if (validCarCount == 0) {
            layer.alert("当前账号权限下无可操作车辆！", {icon: 0, title: ['警告', 'font-size:14px;color:#ffffff;background:#478de4;']});
            return;
        }
        remoteControl(3);
    });
    $("#quit_gasstation").click(function () {
        if (validCarCount == 0) {
            layer.alert("当前账号权限下无可操作车辆！", {icon: 0, title: ['警告', 'font-size:14px;color:#ffffff;background:#478de4;']});
            return;
        }
        remoteControl(4);
    });
    $("#into_urgent").click(function () {
        if (validCarCount == 0) {
            layer.alert("当前账号权限下无可操作车辆！", {icon: 0, title: ['警告', 'font-size:14px;color:#ffffff;background:#478de4;']});
            return;
        }
        remoteControl(5);
    });
    $("#quit_urgent").click(function () {
        if (validCarCount == 0) {
            layer.alert("当前账号权限下无可操作车辆！", {icon: 0, title: ['警告', 'font-size:14px;color:#ffffff;background:#478de4;']});
            return;
        }
        remoteControl(6);
    });
    $("#alter_status").click(function () {
        if (validCarCount == 0) {
            layer.alert("当前账号权限下无可操作车辆！", {icon: 0, title: ['警告', 'font-size:14px;color:#ffffff;background:#478de4;']});
            return;
        }
        remoteControl(7);
    });
    $("#wait_oildom").click(function () {
        if (validCarCount == 0) {
            layer.alert("当前账号权限下无可操作车辆！", {icon: 0, title: ['警告', 'font-size:14px;color:#ffffff;background:#478de4;']});
            return;
        }
        remoteControl(8);
    });
    $("#into_oildom").click(function () {
        if (validCarCount == 0) {
            layer.alert("当前账号权限下无可操作车辆！", {icon: 0, title: ['警告', 'font-size:14px;color:#ffffff;background:#478de4;']});
            return;
        }
        remoteControl(9);
    });
    $("#quit_oildom").click(function () {
        if (validCarCount == 0) {
            layer.alert("当前账号权限下无可操作车辆！", {icon: 0, title: ['警告', 'font-size:14px;color:#ffffff;background:#478de4;']});
            return;
        }
        remoteControl(10);
    });

    /** 开锁重置 */
    $("#unlock_reset").click(function () {
        if (validCarCount == 0) {
            layer.alert("当前账号权限下无可操作车辆！", {icon: 0, title: ['警告', 'font-size:14px;color:#ffffff;background:#478de4;']});
            return;
        }
        var carNumber = trimAll($("#search_text").val());
        if (carNumber == "") {
            layer.alert('未选择系统已有车辆！', {icon: 0}, function (index2) {
                layer.close(index2);
                $("#search_text").focus();
            });
            return;
        }
        layer.open({
            type: 2,
            title: ['远程开锁重置', 'font-size:14px;color:#ffffff;background:#478de4;'],
            // shadeClose: true,
            shade: 0.8,
            resize: false,
            area: ['540px', '435px'],
            content: 'remote/unlockReset.html?carNumber=' + encodeURI(carNumber)
        });
    });

    /** 授权码验证 */
    $("#auth_verify").click(function () {
        layer.open({
            type: 2,
            title: ['授权认证', 'font-size:14px;color:#ffffff;background:#478de4;'],
            // shadeClose: true,
            shade: 0.8,
            resize: false,
            area: ['540px', '435px'],
            content: 'remote/authCodeVerify.html'
        });
    });

    /** 车辆远程换站 */
    $("#change_station").click(function () {
        if (validCarCount == 0) {
            layer.alert("当前账号权限下无可操作车辆！", {icon: 0, title: ['警告', 'font-size:14px;color:#ffffff;background:#478de4;']});
            return;
        }
        var carNumber = trimAll($("#search_text").val());
        if (carNumber == "") {
            layer.alert('未选择系统已有车辆！', {icon: 0}, function (index2) {
                layer.close(index2);
                $("#search_text").focus();
            });
            return;
        }
        $.getJSON("../../manage/car/getDistribution.do", encodeURI("carNumber=" + carNumber),
            function (data) {
                if (data == undefined || data == null || data.length == 0) {
                    layer.alert('当前车辆无处于配送中的配送单，不能换站，请查证后处理！', {icon: 5});
                } else {
                    layer.open({
                        type: 2,
                        title: ['车辆远程换站', 'font-size:14px;color:#ffffff;background:#478de4;'],
                        // shadeClose: true,
                        shade: 0.8,
                        resize: false,
                        area: ['540px', '435px'],
                        content: '../../manage/car/changeDispatch.do?' + encodeURI('carNumber=' + carNumber)
                    });
                }
            }
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

});