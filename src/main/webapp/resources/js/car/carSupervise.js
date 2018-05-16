var ws = null;
var carIds = [];
var carMarkerMap = new Map();
var sessionId;
var user;
var focusId;
var realtimeId;

$(function() {
    $(window).resize(function() {
        if ($('#car_info').is(':hidden')) {
            $(".map").height($(window).height() - 88);
        } else {
            $(".map").height($(window).height() - 90 - $("#car_info").height());
        }
    }).resize();

    var tableCont = document.querySelector('#table-cont')
    /**
     * scroll handle
     * @param {event} e -- scroll event
     */
    function scrollHandle (e){
        // console.log(this)
        var scrollTop = this.scrollTop;
        this.querySelector('.table-head').style.transform = 'translateY(' + scrollTop + 'px)';
    }

    tableCont.addEventListener('scroll',scrollHandle);

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
                case "log":
                    reFreshLog(receiveObj);
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

    function reFreshLog(log) {
        var timestamp = "<span class='log-time'>[" + new Date().format("yyyy-MM-dd HH:mm:ss") + "]</span>";
        var task = isNull(log.task) ? "" : "[" + log.task + "]";
        var result = "<span " + (log.fail > 0 ? "class='log-fail'>" : "class='log-done'>") + log.result + "</span>";
        var logHtml = "<div class='log-content'>" + timestamp + task + "&nbsp;&nbsp;" + result;
        $(".log-box").prepend(logHtml);
    }

    // for (var i = 0; i < 10; i++) {
    //     var log = {
    //         fail: Math.round(Math.random()),
    //         task: "锁绑定变更下发：黄卓明通过网页对车辆桂A12345增加锁绑定。",
    //         result: "成功或失败！"
    //     }
    //     reFreshLog(log)
    // }

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
                content: '../../normal/car/monitor/carMonitorFocusTrack.html?wsUrl=' + wsUrl +
                '&carId=' + id + '&carNumber=' + track.carNumber + '&parentSession=' + sessionId + '&user=' + encodeURIComponent(JSON.stringify(user)),
                end: function() {
                    var order = {
                        biz: 'focus',
                        bizType: 'cancel',
                        session: focusId,
                        user: JSON.stringify(user),
                        token: generateUUID()
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


    /** 设备绑定 */
    // 车载终端绑定
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
    // 锁绑定
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
    // GPS配置
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
    // 车台功能启用
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
    // 车台软件升级
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

    /** 实时监控 */
    var comHtml = "<select class=\"editInfo\" id=\"carcom\">";
    $.ajax({
        type: "get",
        async: false, //不异步，先执行完ajax，再干别的
        url: "../../manage/transcom/getCompanyList.do",
        dataType: "json",
        success: function(response) {
            for (var i = 0, len = response.length; i < len; i++) {
                var company = response[i];
                comHtml += "<option value=" + company.id + ">" + company.name + "</option>";
            }
            comHtml += "</select>";
        }
    });

    $("#scope").change(function() {
        var scope = $("#scope").val();
        if (scope == 1) {
            $("#select_name").html("车牌号");
            $("#carcom").replaceWith("<input type=\"text\" class=\"editInfo\" id=\"carcom\">");
        } else {
            $("#select_name").html("运输公司名称");
            $("#carcom").replaceWith(comHtml);
        }
    });

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

    $("#realtime_monitor").click(function() {
        realtimeLayer = layer.open({
            type: 1,
            title: ['车辆实时监控', 'font-size:14px;color:#ffffff;background:#478de4;'],
            shadeClose: true,
            shade: 0.8,
            area: ['540px', '435px'],
            content: $('.hidden-box')
        });
    });

    $("#cancel").click(function() {
        layer.close(realtimeLayer);
    });

    $("#confirm").click(function() {
        var scope = $("#scope").val();
        var carNumber = "";
        var comId = 0;
        if (scope == 1) {
            carNumber = trimAll($("#carcom").val());
            if (!isCarNo(carNumber)) {
                layer.alert('车牌号不正确，请输入一个完整的车牌号！', { icon: 2 }, function(index2) {
                    layer.close(index2);
                    $("#carcom").select();
                });
                return;
            }
        } else {
            comId = $("#carcom").val();
        }
        var interval = $("#interval").val();
        var duration = $("#duration").val();
        layer.open({
            type: 2,
            title: ['车辆实时监控', 'font-size:14px;color:#ffffff;background:#478de4;'],
            shadeClose: true,
            shade: 0.6,
            area: ['800px', '560px'],
            content: '../../normal/car/monitor/carMonitorRealtime.html?wsUrl=' + wsUrl + '&car=' + encodeURI(carNumber) +
            '&comId=' + comId + '&interval=' + interval + '&duration=' + duration +
            '&parentSession=' + sessionId + '&user=' + encodeURIComponent(JSON.stringify(user)),
            end: function() {
                var order = {
                    biz: 'realtime',
                    bizType: 'cancel',
                    session: realtimeId,
                    user: JSON.stringify(user),
                    token: generateUUID()
                };
                ws.send(JSON.stringify(order));
            }
        });
        layer.close(realtimeLayer);
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