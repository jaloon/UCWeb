var ws = null;

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
        ws = new WebSocket("ws://" + wsUrl + "/track");
    } else if ('MozWebSocket' in window) {
        ws = new MozWebSocket("ws://" + wsUrl + "/track");
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
        if (track.online == 0 && carIds.indexOf(id) >= 0) {
            map.removeOverlay(carMarkerMap.get(id));
            $("#car_" + id).children().last().html("离线");
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
                content: '../../normal/car/carMonitorFocusTrack.html?' + encodeURI('carId=' + id + '&parentSession=' + sessionId), //iframe的url
                end: function() { 
                    var order = {
                        biz: 'focus',
                        bizType: 'cancel',
                        // car: id,
                        session: focusId
                    };
                    ws.send(JSON.stringify(order));
                }
            });
        });
        carMarkerMap.set(id, carMarker);
        map.addOverlay(carMarker);
    }

    $("#search_btn").click(function() {
        var carNumber = trimAll($("#search_text").val());
        if (!isCarNo(carNumber)) {
            layer.alert('车牌号不正确，请输入一个完整的车牌号！', { icon: 2 }, function(index2) {
                layer.close(index2);
                $("#search_text").select();
            });

        }

    });
    $("#car_list").click(function() {
        if ($('.table-list').is(':hidden')) {
            $('.table-list').show();
            $('#car_list').css({
                // background: 'url(../../resources/images/operate/hide.png)'
                transform: 'rotate(0deg)'
            });
            $('#car_list').attr('title', '隐藏车辆列表');
            $(".map").height($(window).height() - 80 - $(".table-list").height());
        } else {
            $('.table-list').hide();
            $('#car_list').css({
                // background: 'url(../../resources/images/operate/show.png)'
                transform: 'rotate(180deg)'
            });
            $('#car_list').attr('title', '显示车辆列表');
            $(".map").height($(window).height() - 68);
        }
    });
    $("#car_manage").click(function() {
        if ($('.car-supervise').is(':hidden')) {
            $('.car-supervise').show();
            $('#car_manage').val('隐藏车辆监管菜单');
        } else {
            $('.car-supervise').hide();
            $('#car_manage').val('显示车辆监管菜单');
        }
    });


    /** 设备绑定 */
    $("#bind_terminal").click(function() {
        // bindDispatch('car');
        layer.open({
            type: 2,
            title: ['设备绑定', 'font-size:14px;color:#ffffff;background:#478de4;'],
            shadeClose: true,
            shade: 0.8,
            area: ['540px', '435px'],
            content: 'bind/carBind.html'
        });
    });
    $("#bind_lock").click(function() {
        // bindDispatch('lock');
        layer.open({
            type: 2,
            title: ['设备绑定', 'font-size:14px;color:#ffffff;background:#478de4;'],
            shadeClose: true,
            shade: 0.8,
            area: ['540px', '435px'],
            content: 'bind/lockBind.html'
        });
    });

    /** 车载终端配置 */
    $("#gps_config").click(function() {
        layer.open({
            type: 2,
            title: ['车载终端配置', 'font-size:14px;color:#ffffff;background:#478de4;'],
            shadeClose: true,
            shade: 0.8,
            area: ['540px', '435px'],
            content: 'conf/gpsConfig.html'
        });
    }); 
    $("#func_enable").click(function() {
    	layer.open({
    		type: 2,
    		title: ['车载终端配置', 'font-size:14px;color:#ffffff;background:#478de4;'],
    		shadeClose: true,
    		shade: 0.8,
    		area: ['540px', '435px'],
    		content: 'conf/funcEnable.html'
    	});
    }); 
    $("#soft_upgrade").click(function() {
    	layer.open({
    		type: 2,
    		title: ['车载终端配置', 'font-size:14px;color:#ffffff;background:#478de4;'],
    		shadeClose: true,
    		shade: 0.8,
    		area: ['540px', '435px'],
    		content: 'conf/softUpgrade.html'
    	});
    }); 
    
    
    /** 车辆远程控制 */
    function remoteControl(mode) {
        var carNumber = trimAll($("#search_text").val());
        if (!isCarNo(carNumber)) {
            layer.alert('车牌号不正确，请输入一个完整的车牌号！', { icon: 2 }, function(index2) {
                layer.close(index2);
                $("#search_text").select();
            });
            return;
        }
        $.getJSON("../../manage/car/getCarByNo.do", encodeURI("carNo=" + carNumber),
            function(data) {
                if (isNull(data)) {
                    layer.alert(carNumber + '不在车辆列表中，请重新选择车辆或先将' + carNumber + '添加到车辆列表中再绑定车载终端。');
                    return;
                }
                if (data.vehicleDevice == null || data.vehicleDevice.deviceId == null || data.vehicleDevice.deviceId == 0) {
                    layer.alert(carNumber + '未绑定车载终端！', { icon: 5 });
                    return;
                }
                layer.open({
                    type: 2,
                    title: ['车辆远程控制', 'font-size:14px;color:#ffffff;background:#478de4;'],
                    shadeClose: true,
                    shade: 0.8,
                    area: ['540px', '435px'],
                    content: '../../manage/car/carStatusDispatch.do?' + encodeURI('mode=' + mode + '&carNumber=' + carNumber)
                });
            }
        );
    }

    $("#into_oildepot").click(function() {
        remoteControl(1);
    });
    $("#quit_oildepot").click(function() {
        remoteControl(2);
    });
    $("#into_gasstation").click(function() {
        remoteControl(3);
    });
    $("#quit_gasstation").click(function() {
        remoteControl(4);
    });
    $("#alter_status").click(function() {
        remoteControl(5);
    });

    /** 车辆远程换站 */
    $("#change_station").click(function() {
        var carNumber = trimAll($("#search_text").val());
        if (!isCarNo(carNumber)) {
            layer.alert('车牌号不正确，请输入一个完整的车牌号！', { icon: 2 }, function(index2) {
                layer.close(index2);
                $("#search_text").select();
            });
            return;
        }
        $.getJSON("../../manage/car/getDistribution.do", encodeURI("carNumber=" + carNumber),
            function(data) {
                if (data == null || data.length == 0) {
                    layer.alert('当前车辆配送状态不符，不能换站，请查证后处理！', { icon: 5 });
                } else {
                    layer.open({
                        type: 2,
                        title: ['车辆远程换站', 'font-size:14px;color:#ffffff;background:#478de4;'],
                        shadeClose: true,
                        shade: 0.8,
                        area: ['540px', '435px'],
                        content: '../../manage/car/changeDispatch.do?' + encodeURI('carNumber=' + carNumber)
                    });
                }
            }
        );


    });

});