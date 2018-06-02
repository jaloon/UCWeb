var play;
var validCarCount = 0;
$(function() {
    $(window).resize(function() {
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
                layer.alert("当前账号权限下无可查询车辆！", {icon: 0, title: ['警告', 'font-size:14px;color:#ffffff;background:#478de4;']});
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
        if (XMLHttpRequest.readyState == 4 && XMLHttpRequest.status == 200 && textStatus == "parsererror") {
            layer.confirm('登录失效，是否刷新页面重新登录？', {
                icon: 0,
                title: ['登录失效', 'font-size:14px;color:#ffffff;background:#478de4;']
            }, function() {
                location.reload(true);
            });
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
    map.centerAndZoom("厦门", 12); // 初始化地图,设置中心点坐标和地图级别
    map.addControl(new BMap.NavigationControl()); //左上角，添加默认缩放平移控件
    map.addControl(new BMap.MapTypeControl()); //添加地图类型控件
    map.addControl(new BMap.ScaleControl()); // 添加比例尺
    map.addControl(new BMap.OverviewMapControl({ isOpen: true })); //添加缩略地图控件
    map.enableScrollWheelZoom(); //开启鼠标滚轮缩放

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

    var timer; //定时器
    var index = 0; //轨迹回放到第几个point
    var tracks = []; //轨迹点
    //4个控制按钮
    var btn_start = $("#btn_start"),
        btn_stop = $("#btn_stop"),
        btn_pause = $("#btn_pause"),
        btn_go_on = $("#btn_go_on");
    var carNumber, begin, end, interval; //轨迹回放参数

    play = function() {
        map.removeOverlay(carMarker);
        var point = new BMap.Point(tracks[index].longitude, tracks[index].latitude);
        if (index > 0) {
            var prePoint = new BMap.Point(tracks[index - 1].longitude, tracks[index - 1].latitude);
            map.addOverlay(new BMap.Polyline([prePoint, point], {
                strokeColor: "green",
                strokeWeight: 3,
                strokeOpacity: 0.8
            }));
        }
        initCarIcon(tracks[index].alarm);
        carMarker = new BMap.Marker(point, {
            icon: carIcon,
            rotation: -tracks[index].angle
        });
        carLabel = new BMap.Label(tracks[index].carNumber + ", " + tracks[index].carStatus, {
            position: point,
            offset: new BMap.Size(-32, -18)
        });
        carLabel.setStyle({
            color: "red",
            fontSize: "10px",
            height: "16px",
            lineHeight: "16px",
            fontFamily: "微软雅黑"
        });
        carMarker.setLabel(carLabel);
        map.addOverlay(carMarker);
        index++;
        // 图随点移
        map.panTo(point);
        if (index < tracks.length) {
            timer = window.setTimeout("play()", parseInt(interval));
        } else {
            btn_start.attr('disabled', false);
            btn_stop.attr('disabled', true);
            btn_pause.attr('disabled', true);
            btn_go_on.attr('disabled', true);
        }
    };

    function start() {
        if (validCarCount == 0) {
            layer.alert("当前账号权限下无可查询车辆！", {icon: 0, title: ['警告', 'font-size:14px;color:#ffffff;background:#478de4;']});
            return;
        }
        carNumber = trimAll($("#text_car").val());
        if (carNumber == "") {
            layer.alert('未选择系统已有车辆！', { icon: 2 }, function(index2) {
                layer.close(index2);
                $("#text_car").focus();
            });
            return;
        }
        begin = $("#text_begin").val();
        end = $("#text_end").val();
        interval = $("#text_interval").val();
        $.post("../../../manage/car/retrack.do", encodeURI("carNumber=" + carNumber + "&begin=" + begin + "&end=" + end),
            function(data) {
                if (isNull(data) || data.length == 0) {
                    layer.alert('无当前时段车辆轨迹信息，无法回放轨迹', { icon: 5 });
                } else {
                    stop();
                    btn_start.attr('disabled', true);
                    btn_stop.attr('disabled', false);
                    btn_pause.attr('disabled', false);
                    btn_go_on.attr('disabled', true);
                    tracks = data;
                    play();
                }
            },
            "json"
        ).error(function (XMLHttpRequest, textStatus, errorThrown) {
                if (XMLHttpRequest.readyState == 4 && XMLHttpRequest.status == 200 && textStatus == "parsererror") {
                    layer.confirm('登录失效，是否刷新页面重新登录？', {
                        icon: 0,
                        title: ['登录失效', 'font-size:14px;color:#ffffff;background:#478de4;']
                    }, function() {
                        location.reload(true);
                    });
                }
            });
    }

    function stop() {
        btn_start.attr('disabled', false);
        btn_stop.attr('disabled', true);
        btn_pause.attr('disabled', true);
        btn_go_on.attr('disabled', true);
        if (timer) {
            window.clearTimeout(timer);
        }
        index = 0;
        map.clearOverlays(); //清除地图上所有覆盖物
        tracks.length = 0;
        // map.panTo(centerPoint);
    }

    function pause() {
        btn_start.attr('disabled', false);
        btn_stop.attr('disabled', true);
        btn_pause.attr('disabled', true);
        btn_go_on.attr('disabled', false);
        if (timer) {
            window.clearTimeout(timer);
        }
    }

    function goOn() {
        btn_start.attr('disabled', false);
        btn_stop.attr('disabled', false);
        btn_pause.attr('disabled', false);
        btn_go_on.attr('disabled', true);
        play();
    }

    btn_start.click(start);
    btn_stop.click(stop);
    btn_pause.click(pause);
    btn_go_on.click(goOn);
});