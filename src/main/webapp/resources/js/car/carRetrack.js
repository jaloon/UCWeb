var play;
var validCarCount = 0;
var len = 0;
var trackLayer;
var timer; //定时器
var index = 0; //轨迹回放到第几个point
var tracks = []; //轨迹点
var prePoint; //前一个有效轨迹点

var overlays = [];
var doubtableMarkers = [];

function parseGpsValid(coorValid) {
    if (coorValid) {
        return "有效";
    }
    return "无效";
}

/**
 * 解析车辆状态
 * @param carStatus 车辆状态码
 * @returns {string}
 */
function parseCarStatus(carStatus) {
    switch (carStatus) {
        case 1:
            return "在油库";
        case 2:
            return "在途中";
        case 3:
            return "在加油站";
        case 4:
            return "返程中";
        case 5:
            return "应急";
        case 6:
            return "待入油区";
        case 7:
            return "在油区";
        default:
            return "未知";
    }
}

function parseTerminalAlarm(terminalAlarm) {
    if (0 == terminalAlarm) {
        return "无报警";
    }
    var alarm = "";
    if ((terminalAlarm & 1) > 0) {
        alarm += "；未施封越界";
    }
    if (((terminalAlarm >> 1) & 1) > 0) {
        alarm += "；时钟电池报警";
    }
    if ((terminalAlarm >> 2) != 0) {
        alarm += "；未知(" + (alarmtype & 252) + ")";
    }
    return alarm.slice(1);
}

function getLineOpts(carStatus) {
    switch (carStatus) {
        case 1:
            return opt_1;
        case 2:
            return opt_2;
        case 3:
            return opt_3;
        case 4:
            return opt_4;
        case 5:
            return opt_5;
        case 6:
            return opt_6;
        case 7:
            return opt_7;
        default:
            return opt_else;
    }
}

var opt_1 = {
    strokeColor: "green",
    strokeWeight: 3,
    strokeStyle: "dashed",
    strokeOpacity: 0.8
};
var opt_2 = {
    strokeColor: "green",
    strokeWeight: 3,
    strokeStyle: "dashed",
    strokeOpacity: 0.8
};
var opt_3 = {
    strokeColor: "green",
    strokeWeight: 3,
    strokeStyle: "dashed",
    strokeOpacity: 0.8
};
var opt_4 = {
    strokeColor: "green",
    strokeWeight: 3,
    strokeStyle: "dashed",
    strokeOpacity: 0.8
};
var opt_5 = {
    strokeColor: "green",
    strokeWeight: 3,
    strokeStyle: "dashed",
    strokeOpacity: 0.8
};
var opt_6 = {
    strokeColor: "green",
    strokeWeight: 3,
    strokeStyle: "dashed",
    strokeOpacity: 0.8
};
var opt_7 = {
    strokeColor: "green",
    strokeWeight: 3,
    strokeStyle: "dashed",
    strokeOpacity: 0.8
};
var opt_else = {
    strokeColor: "green",
    strokeWeight: 3,
    strokeStyle: "dashed",
    strokeOpacity: 0.8
};

$(function () {
    $(window).resize(function () {
        $(".map").height($(window).height() - 68);
    }).resize();

    $.getJSON("../../../manage/car/selectCars.do", "scope=0",
        function (data, textStatus, jqXHR) {
            var selectObj = $('#text_car');
            var cars = data.car;
            validCarCount = cars.length;
            if (validCarCount == 0) {
                selectObj.hide();
                var $hiddenCar = $("#hidden_car");
                $hiddenCar.css({
                    display: 'block',
                    opacity: 1,
                    color: '#d80e0e',
                    'font-size': 10
                });
                $hiddenCar.val("当前账号权限下无可查询车辆！");
                $("#text_begin").attr('disabled', true);
                $("#text_end").attr('disabled', true);
                $("#btn_start").attr('disabled', true);
                layer.alert("当前账号权限下无可查询车辆！", {
                    icon: 0,
                    title: ['警告', 'font-size:14px;color:#ffffff;background:#478de4;']
                });
                return;
            }
            selectObj.append(data.com);
            var groupObj;
            for (var i = 0; i < validCarCount; i++) {
                var car = cars[i];
                groupObj = $("#com_" + car.comId);
                groupObj.append("<option value = '" + car.carNumber + "'>" + car.carNumber + "</option>");
            }
            selectObj.comboSelect();
            $("#hidden_car").show();
            selectObj.hide();
            selectObj.closest(".combo-select").css({
                position: 'absolute',
                'z-index': 100000,
                left: '60px',
                top: '2px',
                width: '178px',
                height: '34px',
                'font-size': '16px',
                "margin-bottom": "0px"
            });
            selectObj.siblings(".combo-input").height(10);
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

    $('#text_interval').jRange({
        from: 0,
        to: 1000,
        step: 100,
        scale: ['快', '慢'],
        format: '%s',
        width: 140,
        // snap: true,
        showLabels: false,
        showScale: true
    });
    laydate.render({
        elem: '#text_begin',
        type: 'datetime',
        // value: '2017-10-01 00:00:00',
        value: new Date(new Date().setDate(new Date().getDate() - 3)), //3天前
        min: -90,
        max: new Date().getTime()
    });
    laydate.render({
        elem: '#text_end',
        type: 'datetime',
        value: new Date(),
        min: -90, //指定日期最小值为90天前
        max: new Date().getTime() //new Date().getTime()获取当前时间的时间戳，指定日期最大值为当前时间
    });

    var map = new BMap.Map("bmap"); // 创建Map实例
    var localCity = new BMap.LocalCity();
    localCity.get(function (localCityResult) {
        // center	Point	城市所在中心点
        // level	Number	展示当前城市的最佳地图级别，如果您在使用此对象时提供了map实例，则地图级别将根据您提供的地图大小进行调整
        // name	String	城市名称
        map.centerAndZoom(localCityResult.center, 12); // 初始化地图,设置中心点坐标和地图级别
        map.addControl(new BMap.NavigationControl()); //左上角，添加默认缩放平移控件
        map.addControl(new BMap.MapTypeControl()); //添加地图类型控件
        map.addControl(new BMap.ScaleControl()); // 添加比例尺
        map.addControl(new BMap.OverviewMapControl({isOpen: true})); //添加缩略地图控件
        map.enableScrollWheelZoom(); //开启鼠标滚轮缩放
    });

    var carMarker, carLabel, carIcon; //车辆标注

    function initCarIcon(alarm) {
        if (alarm) {
            carIcon = new BMap.Icon(
                '../../../resources/images/marker/车辆图标-32-红.png',
                new BMap.Size(32, 15)
            );
        } else {
            carIcon = new BMap.Icon(
                '../../../resources/images/marker/车辆图标-32-黄.png',
                new BMap.Size(32, 15)
            );
        }
    }

    //5个控制按钮
    var btn_gps = $("#btn_gps"),
        btn_start = $("#btn_start"),
        btn_stop = $("#btn_stop"),
        btn_pause = $("#btn_pause"),
        btn_go_on = $("#btn_go_on");
    var carNumber, begin, end, interval; //轨迹回放参数

    $(".table-body").on("click", ".replayed", function () {
        $.getJSON("../../../manage/car/getTrackAndLockInfoByTrackId.do", "trackId=" + $(this).children().last().html(),
            function (data, textStatus, jqXHR) {
                if (data == null) {
                    layer.alert("数据查询异常，请刷新页面测试！");
                    return;
                }
                var track = data.track;
                var locks = data.locks;
                var bd09 = wgs84tobd09(track.longitude, track.latitude);
                var point = new BMap.Point(bd09[0], bd09[1]);
                // parseTerminalAlarm
                var content = "<table>"
                    + "<tr><td>GPS坐标：</td><td>" + track.longitude + ", " + track.latitude + "</td></tr>"
                    + "<tr><td>终端状态：</td><td>" + parseTerminalAlarm(track.terminalAlarm) + "</td></tr>"
                    + "<tr><td style='vertical-align: top;'>锁状态：</td><td>" + locks + "</td></tr>";
                +"</table>"
                var opts = {
                    // width : 200,     // 信息窗口宽度
                    // height: 100,     // 信息窗口高度
                    title: "&emsp;"  // 信息窗口标题
                }
                var infoWindow = new BMap.InfoWindow(content);  // 创建信息窗口对象
                map.openInfoWindow(infoWindow, point); //开启信息窗口
                map.panTo(point);
                map.setZoom(18);
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
        // var thisChild = $(this).children();
        // var bd09 = wgs84tobd09(parseFloat(thisChild.eq(3).html()), parseFloat(thisChild.eq(4).html()));
        // var point = new BMap.Point(bd09[0], bd09[1]);
        // map.panTo(point);
        // map.setZoom(18);
    });

    play = function () {
        // map.removeOverlay(carMarker);
        var trs = $(".table-body tr");
        trs.removeClass("active");
        var tr = $(trs[index]);
        tr.addClass("active replayed");
        $(".table-body").scrollTop(index * $(".table-body")[0].scrollHeight / len);
        if (tracks[index].coorValid) {
            var bd09 = wgs84tobd09(tracks[index].longitude, tracks[index].latitude);
            var point = new BMap.Point(bd09[0], bd09[1]);
            // var point = new BMap.Point(tracks[index].longitude, tracks[index].latitude);
            if (index > 0 && prePoint != undefined) {
                var polyline = new BMap.Polyline([prePoint, point], getLineOpts(tracks[index].carStatus));
                map.addOverlay(polyline);
                overlays.push(polyline);
            }
            initCarIcon(tracks[index].alarm);
            carMarker = new BMap.Marker(point, {
                icon: carIcon,
                rotation: -tracks[index].angle
            });
            // carLabel = new BMap.Label(tracks[index].carNumber + ", " + tracks[index].carStatus, {
            //     position: point,
            //     offset: new BMap.Size(-32, -18)
            // });
            carLabel = new BMap.Label(index + 1, {
                position: point,
                // offset: new BMap.Size(30, 0)
            });
            carLabel.setStyle({
                color: "red",
                fontSize: "10px",
                minWidth: "16px",
                height: "16px",
                lineHeight: "16px",
                fontFamily: "微软雅黑"
            });
            carLabel.setZIndex(10000 + index);
            carMarker.setLabel(carLabel);
            map.addOverlay(carMarker);
            overlays.push(carMarker);
            // 图随点移
            map.panTo(point);
            prePoint = point;
        }
        index++;
        if (index < tracks.length) {
            interval = $("#text_interval").val();
            timer = window.setTimeout(play, parseInt(interval));
        } else {
            index = 0;
            btn_gps.attr('disabled', false);
            btn_start.attr('disabled', false);
            btn_stop.attr('disabled', true);
            btn_pause.attr('disabled', true);
            btn_go_on.attr('disabled', true);
            $(".table-body tr").removeClass("active");
        }
    };

    $("#filter_btn").click(function () {
        var filter_id = $("#filter_id").val();
        var filter_lng = $("#filter_lng").val();
        var filter_lat = $("#filter_lat").val();
        filterTracks(tracks, filter_id, filter_lng, filter_lat);
    });

    function filterTracks(tracks, filter_id, filter_lng, filter_lat) {
        if (tracks == undefined || tracks == null || tracks.length == 0) {
            return;
        }
        var coords_html = "";
        for (var item_index = 0; item_index < len; item_index++) {
            var item = tracks[item_index];
            if (!isNull(filter_id) && filter_id != item_index + 1) {
                continue;
            }
            if (!isNull(filter_lng) && filter_lng != item.longitude) {
                continue;
            }
            if (!isNull(filter_lat) && filter_lat != item.latitude) {
                continue;
            }
            if (item_index < index - 1) {
                coords_html += "<tr class='replayed'>";
            } else if (item_index == index - 1) {
                coords_html += "<tr class='replayed active'>";
            } else {
                coords_html += "<tr>";
            }
            if (item.doubtable) {
                coords_html += "<td class='track-index'><span style='color: #d80e0e'>!</span> "
                    + (item_index + 1) + "</td>";
            } else {
                coords_html += "<td class='track-index'>" + (item_index + 1) + "</td>";
            }
            coords_html += "<td class='track-time'>" + item.createDate + "</td>"
                + "<td class='track-valid'>" + parseGpsValid(item.coorValid) + "</td>"
                + "<td class='track-lng'>" + item.longitude + "</td>"
                + "<td class='track-lat'>" + item.latitude + "</td>"
                + "<td class='track-speed'>" + item.velocity + "</td>"
                + "<td class='track-aspect'>" + angle2aspect(item.angle) + "</td>"
                + "<td class='track-status'>" + parseCarStatus(item.carStatus) + "</td>"
                + "<td style='display: none;'>" + item.trackId + "</td>"
                + "</tr>";
        }
        $("#tracks table tbody").html(coords_html);
    }

    function gpsQuery() {
        if (validCarCount == 0) {
            layer.alert("当前账号权限下无可查询车辆！", {icon: 0, title: ['警告', 'font-size:14px;color:#ffffff;background:#478de4;']});
            return;
        }
        carNumber = trimAll($("#text_car").val());
        if (carNumber == "") {
            layer.alert('未选择系统已有车辆！', {icon: 2}, function (index2) {
                layer.close(index2);
                $("#text_car").focus();
            });
            return;
        }
        begin = $("#text_begin").val();
        end = $("#text_end").val();
        // interval = $("#text_interval").val();
        var loadLayer = layer.load();
        // noinspection JSAnnotator
        $.post("../../../manage/car/retrack.do", encodeURI("carNumber=" + carNumber + "&begin=" + begin + "&end=" + end),
            function (data) {
                if (isNull(data)) {
                    tracks.length = 0;
                    if (trackLayer != undefined) {
                        layer.close(trackLayer);
                    }
                    layer.close(loadLayer);
                    layer.alert('无当前时段车辆轨迹信息！', {icon: 5});
                    return;
                }
                var retracks = data.tracks;
                var doubtable_points = data.doubtable;
                len = retracks.length;
                filterTracks(retracks);
                layer.close(loadLayer);
                if (trackLayer == undefined) {
                    trackLayer = layer.open({
                        type: 1,
                        title: ['轨迹列表', 'font-size:14px;color:#ffffff;background:#478de4;'],
                        shade: 0,
                        area: '770px',
                        offset: 'r',
                        resize: false,
                        content: $('#tracks'),
                        maxmin: true,
                        full: function (layero) {
                            layer.restore(trackLayer)
                        },
                        min: function (layero) {
                            $(".layui-layer-max").show();
                            setTimeout(function () {
                                layero.css({
                                    left: 'auto'
                                    , right: 0
                                    , bottom: 0
                                });
                            }, 0);
                        },
                        restore: function (layero) {
                            $(".layui-layer-max").hide();
                        },
                        end: function () {
                            stop();
                            btn_start.attr('disabled', true);
                            tracks.length = 0;
                            doubtableMarkers.length = 0;
                            trackLayer = undefined;
                        }
                    });
                    $(".layui-layer-max").hide();
                }
                stop();
                doubtableMarkers.length = 0;
                doubtable_points.forEach(function (item) {
                    var bd09 = wgs84tobd09(item.longitude, item.latitude);
                    var point = new BMap.Point(bd09[0], bd09[1]);
                    var opts = {
                        // 初始化警告标志的symbol
                        // icon: new BMap.Symbol(BMap_Symbol_SHAPE_WARNING, {
                        //     scale: 2,
                        //     strokeWeight: 1,
                        //     strokeColor: 'red'
                        // })
                    };
                    var marker = new BMap.Marker(point, opts);
                    marker.addEventListener("click", function () {
                        var infoWindow = new BMap.InfoWindow("坐标：" + item.longitude + ", " + item.latitude); // 创建信息窗口对象
                        map.openInfoWindow(infoWindow, point); //开启信息窗口
                        map.panTo(point);
                    });
                    doubtableMarkers.push(marker);
                    map.addOverlay(marker);
                    marker.hide();
                });
                tracks = retracks;
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

    function toggleDoubtableMarkers() {
        if ($("#toggle_button").prop("checked")) {
            doubtableMarkers.forEach(function (marker) {
                marker.show();
            });
        } else {
            doubtableMarkers.forEach(function (marker) {
                marker.hide();
            });
        }
    }

    function clearOverlays() {
        overlays.forEach(function (overlay, index, overlays) {
            map.removeOverlay(overlay);
        });
        overlays.length = 0;
    }

    function start() {
        clearOverlays(); //清除地图上所有覆盖物
        btn_gps.attr('disabled', true);
        btn_start.attr('disabled', true);
        btn_stop.attr('disabled', false);
        btn_pause.attr('disabled', false);
        btn_go_on.attr('disabled', true);
        map.setZoom(18);
        toggleDoubtableMarkers();
        play();
    }

    function stop() {
        $(".table-body tr").removeClass("active");
        btn_gps.attr('disabled', false);
        btn_start.attr('disabled', false);
        btn_stop.attr('disabled', true);
        btn_pause.attr('disabled', true);
        btn_go_on.attr('disabled', true);
        if (timer) {
            window.clearTimeout(timer);
        }
        clearOverlays(); //清除地图上所有覆盖物
        $("#toggle_button").prop("checked", false);
        toggleDoubtableMarkers();
        index = 0;
        // map.panTo(centerPoint);
    }

    function pause() {
        btn_gps.attr('disabled', true);
        btn_start.attr('disabled', true);
        btn_stop.attr('disabled', false);
        btn_pause.attr('disabled', true);
        btn_go_on.attr('disabled', false);
        if (timer) {
            window.clearTimeout(timer);
        }
    }

    function goOn() {
        if (len > $(".table-body tr").length) {
            filterTracks(tracks);
        }
        btn_gps.attr('disabled', true);
        btn_start.attr('disabled', true);
        btn_stop.attr('disabled', false);
        btn_pause.attr('disabled', false);
        btn_go_on.attr('disabled', true);
        play();
    }

    btn_gps.click(gpsQuery);
    btn_start.click(start);
    btn_stop.click(stop);
    btn_pause.click(pause);
    btn_go_on.click(goOn);

    $("#toggle_button").click(toggleDoubtableMarkers);
});