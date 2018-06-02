var ws = null;
var carIds = [];
var carMarkerMap = new Map();
var sessionId;
var user;
var focusId;
var realtimeId;

$(function() {
    $(window).resize(function() {
        if ($('.table-list').is(':hidden')) {
            $(".map").height($(window).height() - 68);
        } else {
            $(".map").height($(window).height() - 80 - $(".table-list").height());
        }
    }).resize();

    var ctx = getUrlParam("ctx");
    var wsUrl = window.location.host + ctx;
    if (window.console) console.log("wsUrl: " + wsUrl);

    var order = {
        biz: 'general'
    };
    var orderText = JSON.stringify(order);

    if ('WebSocket' in window) {
        ws = new WebSocket("mws://" + wsUrl + "/track");
    } else if ('MozWebSocket' in window) {
        ws = new MozWebSocket("mws://" + wsUrl + "/track");
    } else {
        ws = new SockJS("http://" + wsUrl + "/sockjs/track");
    }
    ws.onopen = function(event) {
        if (window.console) console.log("websocket connected");
        ws.send(orderText);
    };
    ws.onmessage = function(event) {
        if (window.console) console.log("websocket receive message");
        var receive = event.data;
        if (receive == "repeat") {
            ws.send(orderText);
        } else {
            var receiveObj = JSON.parse(receive);
            var biz = receiveObj.biz;
            switch (biz) {
                case "track":
                    reFreshPage(receiveObj);
                    break;
                case "session":
                    sessionId = receiveObj.sessionId;
                    user = {
                        id: receiveObj.userId,
                        account: receiveObj.userAccount,
                        name: receiveObj.userName
                    };
                    break;
                case "focus":
                    focusId = receiveObj.sessionId;
                    break;
                case "realtime":
                    realtimeId = receiveObj.sessionId;
                    break;
                default:
                    break;
            }
        }
    };
    ws.onerror = function(event) {
        if (window.console) console.log("websocket error");
    };
    ws.onclose = function(event) {
        if (window.console) console.log("websocket closed： " + event);
    };

    function initCarIcon(alarm) {
        var carIcon;
        if (alarm == true || alarm == "true") {
            carIcon = new BMap.Icon(
                '../../resources/images/marker/车辆图标-32-红.png',
                new BMap.Size(32, 15)
            );
        } else {
            carIcon = new BMap.Icon(
                '../../resources/images/marker/车辆图标-32-黄.png',
                new BMap.Size(32, 15)
            );
        }
        return carIcon;
    }

    function reFreshPage(track) {
        var id = track.id;
        // online: 0 离线，1 在线
        if (track.online == 0) {
            if (carIds.indexOf(id) >= 0) {
                map.removeOverlay(carMarkerMap.get(id));
                $("#car_" + id).children().last().html("离线");
            }
            return;
        }
        var point = new BMap.Point(track.longitude, track.latitude);
        var carLabel = new BMap.Label(track.carNumber + ", " + track.carStatus, {
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
        if (carIds.indexOf(id) >= 0) {
            map.removeOverlay(carMarkerMap.get(id));
            $("#car_" + id).children().eq(2).html("(" + track.longitude + ", " + track.latitude + ")");
            $("#car_" + id).children().eq(3).html(track.velocity);
            $("#car_" + id).children().eq(4).html(angle2aspect(track.angle));
            $("#car_" + id).children().eq(5).html(track.carStatus);
            $("#car_" + id).children().eq(6).html(track.alarm);
        } else {
            carIds.push(id);
            var carTr = "<tr id='car_" + id + "'>" +
                "<td class=\"car-num\">" + track.carNumber + "</td>" +
                "<td class=\"car-company\">" + track.carCom + "</td>" +
                // "<td class=\"car-type\">" + track.carType + "</td>" +
                "<td class=\"car-coordinate\">(" + track.longitude + ", " + track.latitude + ")</td>" +
                "<td class=\"car-velocity\">" + track.velocity + "</td>" +
                "<td class=\"car-aspect\">" + angle2aspect(track.angle) + "</td>" +
                "<td class=\"car-status\">" + track.carStatus + "</td>" +
                // "<td class=\"car-lock\">" + track.lockStatusInfo + "</td>" +
                "<td class=\"car-alarm\">" + track.alarm + "</td>" +
                "<td class=\"car-online\">在线</td>" +
                "</tr>";
            $("#car_tbl").find("tbody").append(carTr);
        }
        var carMarker = new BMap.Marker(point, {
            icon: initCarIcon(track.alarm),
            rotation: -track.angle
        });
        carMarker.setLabel(carLabel);
        carMarker.addEventListener("click", function(e) {
            layer.open({
                type: 2,
                title: ['车辆轨迹查看', 'font-size:14px;color:#ffffff;background:#478de4;'],
                shadeClose: true,
                shade: 0.6,
                area: ['800px', '560px'],
                content: '../../normal/car/carMonitorFocusTrack.html?' +
                    'carId=' + id + '&carNumber=' + track.carNumber + '&parentSession=' + sessionId + '&user=' + encodeURIComponent(JSON.stringify(user)),
                end: function() {
                    var order = {
                        biz: 'focus',
                        bizType: 'cancel',
                        // car: id,
                        session: focusId,
                        user: JSON.stringify(user)
                    };
                    ws.send(JSON.stringify(order));
                }
            });
        });
        carMarkerMap.set(id, carMarker);
        map.addOverlay(carMarker);
    }

    function getBindedCars() {
        var bindedCars = [{ id: '', name: '' }];
        $.ajax({
            type: "get",
            async: false, //不异步，先执行完ajax，再干别的
            url: "../../manage/car/findBindedCars.do",
            dataType: "json",
            success: function(response) {
                for (var i = 0, len = response.length; i < len; i++) {
                    var res = response[i];
                    var car = {};
                    car.id = res.id;
                    car.name = res.carNumber;
                    bindedCars.push(car);
                }
            }
        });
        return bindedCars;
    }

    var cars = getBindedCars();

    function editableCarList() {
        for (var i = 0, len = cars.length; i < len; i++) {
            var car = cars[i];
            if (i == 0) {
                $("#text_car").append("<option selected>" + car.name + "</option>");
            } else {
                $("#text_car").append("<option value=" + car.id + ">" + car.name + "</option>");
            }
        }
        $('#text_car').editableSelect();
        $('#text_car').css({
            width: '266px',
            background: 'white'
        })
    }

    editableCarList();

    function getCompanies() {
        var coms = [];
        $.ajax({
            type: "get",
            async: false, //不异步，先执行完ajax，再干别的
            url: "../../manage/transcom/getCompanyList.do",
            dataType: "json",
            success: function(response) {
                coms = eval(response);
            }
        });
        return coms;
    }
    var companies = getCompanies();

    $("#type_car").change(function() {
        var type = $("#type_car").val();
        if (type == 1) {
            $("#text_car").empty();
            editableCarList();
        } else {
            $('#text_car').editableSelect('destroy');
            $('#text_car').css({
                width: '296px'
            });
            $("#text_car").empty();
            var len = companies.length;
            for (var i = 0; i < len; i++) {
                var company = companies[i];
                $("#text_car").append("<option value=" + company.id + ">" + company.name + "</option>");
            }
        }
    });

    $('#text_interval').jRange({
        from: 1,
        to: 10,
        step: 1,
        scale: [1, 4, 7, 10],
        format: '%s',
        width: 140,
        snap: true,
        showLabels: true,
        showScale: true
    });

    $('#text_end').jRange({
        from: 1,
        to: 60,
        step: 1,
        scale: [1, 20, 40, 60],
        format: '%s',
        width: 140,
        snap: true,
        showLabels: true,
        showScale: true
    });

    // laydate.render({
    //     elem: '#text_end',
    //     type: 'time',
    //     format: 'yyyy-MM-dd HH:mm:ss',
    //     value: new Date(),
    //     min: new Date().getTime(), //指定日期最小值为当前时间，new Date().getTime() 当前时间戳
    //     max: new Date().setHours(new Date().getHours() + 1) //指定日期最大值为1小时后，new Date().setHours(new Date().getHours() + 1) 当前1小时后时间戳
    // });

    $("#btn_start").click(function() {
        var type = $("#type_car").val();
        var carNumber = "";
        var comId = 0;
        if (type == 1) {
            carNumber = trimAll($("#text_car").val());
            if (isNull(carNumber)) {
                location.reload(true);
                return;
            } else if (!isCarNo(carNumber)) {
                layer.alert('车牌号不正确，请输入一个完整的车牌号！', { icon: 2 }, function(index2) {
                    layer.close(index2);
                    $("#text_car").select();
                });
                return;
            }
        } else {
            comId = $("#text_car").val();
            // comName = $("#text_car").find("option:selected").text();
        }
        var interval = $("#text_interval").val();
        var end = $("#text_end").val();
        layer.open({
            type: 2,
            title: ['车辆实时监控', 'font-size:14px;color:#ffffff;background:#478de4;'],
            shadeClose: true,
            shade: 0.6,
            area: ['800px', '560px'],
            content: '../../normal/car/carMonitorRealtime.html?' + 'car=' + encodeURI(carNumber) +
                '&comId=' + comId + '&interval=' + interval + '&duration=' + end +
                '&parentSession=' + sessionId + '&user=' + encodeURIComponent(JSON.stringify(user)),
            end: function() {
                var order = {
                    biz: 'realtime',
                    bizType: 'cancel',
                    // car: carNumber,
                    // com: company,
                    session: realtimeId,
                    user: JSON.stringify(user)
                };
                ws.send(JSON.stringify(order));
            }
        });
    });

    $("#car_list").click(function() {
        if ($('.table-list').is(':hidden')) {
            $('.table-list').show();
            $('#car_list').css({
                transform: 'rotate(0deg)'
            });
            $('#car_list').attr('title', '隐藏车辆列表');
            $(".map").height($(window).height() - 80 - $(".table-list").height());
        } else {
            $('.table-list').hide();
            $('#car_list').css({
                transform: 'rotate(180deg)'
            });
            $('#car_list').attr('title', '显示车辆列表');
            $(".map").height($(window).height() - 68);
        }
    });

});