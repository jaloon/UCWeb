var play;
$(function() {
    $(window).resize(function() {
        $(".map").height($(window).height() - 68);
    }).resize();

    // $.ajax({
    //     type: "get",
    //     async: false, //不异步，先执行完ajax，再干别的
    //     url: "../../../manage/car/selectCars.do",
    //     dataType: "text",
    //     success: function(response) {
    //         // var data = JSON.stringify(response);
    //         $('#text_car').dropdown({data:response});
    //     }
    // });

    // function dropdown_val() {
    //     var el = $("span.dropdown-selected>i.del");
    //     var val = el.attr("data-id");
    //     return val;
    // }
    //
    // function dropdown_text() {
    //     var el = $("span.dropdown-selected");
    //     var text = el.text();
    //     return text;
    // }

    // function getCars() {
    //     var cars = [{ id: '', name: '' }];
    //     $.ajax({
    //         type: "get",
    //         async: false, //不异步，先执行完ajax，再干别的
    //         url: "../../../manage/car/getCarList.do",
    //         dataType: "json",
    //         success: function(response) {
    //             for (var i = 0, len = response.length; i < len; i++) {
    //                 var res = response[i];
    //                 var car = {};
    //                 car.id = res.id;
    //                 car.name = res.carNumber;
    //                 cars.push(car);
    //             }
    //         }
    //     });
    //     return cars;
    // }
    // var cars = getCars();
    //
    // function editableCarList() {
    //     for (var i = 0, len = cars.length; i < len; i++) {
    //         var car = cars[i];
    //         $("#text_car").append("<option value=" + car.id + ">" + car.name + "</option>");
    //     }
    //     $('#text_car').editableSelect();
    //     $('#text_car').css({
    //         // width: '266px',
    //         background: 'white'
    //     })
    // }
    // editableCarList();

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

    // function start() {
    //     carNumber = trimAll($("#text_car").val());
    //     // carNumber = dropdown_text();
    //     begin = $("#text_begin").val();
    //     end = $("#text_end").val();
    //     interval = $("#text_interval").val();
    //     $.post("../../../manage/car/retrack.do", encodeURI("carNumber=" + carNumber + "&begin=" + begin + "&end=" + end),
    //         function(data) {
    //             if (isNull(data) || data.length == 0) {
    //                 layer.alert('无当前时段车辆轨迹信息，无法回放轨迹', { icon: 5 });
    //             } else {
    //                 stop();
    //                 btn_start.attr('disabled', true);
    //                 btn_stop.attr('disabled', false);
    //                 btn_pause.attr('disabled', false);
    //                 btn_go_on.attr('disabled', true);
    //                 tracks = data;
    //                 play();
    //             }
    //         },
    //         "json"
    //     ).error(function (XMLHttpRequest, textStatus, errorThrown) {
    //             if (XMLHttpRequest.readyState == 4 && XMLHttpRequest.status == 200 && textStatus == "parsererror") {
    //                 layer.confirm('登录失效，是否刷新页面重新登录？', {
    //                     icon: 0,
    //                     title: ['登录失效', 'font-size:14px;color:#ffffff;background:#478de4;']
    //                 }, function() {
    //                     location.reload(true);
    //                 });
    //             }
    //         });
    // }


    var times = [];
    var t100 = 0;
    function start() {
        carNumber = trimAll($("#text_car").val());
        begin = $("#text_begin").val();
        end = $("#text_end").val();
        for (var i = 0; i < 100; i++) {
            var t1 = new Date().getTime();
            $.ajax({
                type: "get",
                async: false, //不异步，先执行完ajax，再干别的
                url: "../../../manage/car/retrack.do",
                data: encodeURI("carNumber=" + carNumber + "&begin=" + begin + "&end=" + end),
                dataType: "json",
                success: function(response) {
                    if (isNull(response) || response.length == 0) {
                        layer.alert('无当前时段车辆轨迹信息，无法回放轨迹', { icon: 5 });
                    } else {
                        var t2 = new Date().getTime() - t1;
                        t100 += t2;
                        times.push(t2);
                    }
                }
            });
        }
        console.log(JSON.stringify(times))
        console.log(t100/100);
        times= [];
        t100 = 0;
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