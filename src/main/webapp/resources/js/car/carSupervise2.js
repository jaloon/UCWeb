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

var onlineCarIds = [];
var markerClusterer = new BMapLib.MarkerClusterer(map, {markers: null});
var carLabelStyle = {
    color: "red",
    fontSize: "10px",
    height: "16px",
    lineHeight: "16px",
    fontFamily: "微软雅黑"
};
var carLabelOffset = new BMap.Size(-32, -18);
var carIconY = new BMap.Icon('../../resources/images/marker/车辆图标-32-黄.png', new BMap.Size(32, 15));
var carIconR = new BMap.Icon('../../resources/images/marker/车辆图标-32-红.png', new BMap.Size(32, 15));
var $tbody = $("#table-cont").find("tbody");
var offlineTd = "<td class=\"car-coordinate\">-</td>" +
    "<td class=\"car-velocity\">-</td>" +
    "<td class=\"car-aspect\">-</td>" +
    "<td class=\"car-status\">-</td>";
var trackCache = {
    size: 0,
    carIds: [],
    markers: [],
    trDoms: [],
    indexOf: function (carId) {
        return this.carIds.indexOf(carId);
    },
    putCache: function (carId, marker, trDom) {
        if (carId == undefined || carId == null
            || marker == undefined || marker == null
            || trDom == undefined || trDom == null) {
            console.warn("添加轨迹缓存，参数不全！");
            return;
        }
        var index = this.indexOf(carId);
        if (index < 0) {
            this.carIds.push(carId);
            this.markers.push(marker);
            this.trDoms.push(trDom);
            this.size++;
            return;
        }
        this.markers.splice(index, 1, marker);
        this.trDoms.splice(index, 1, trDom);
    },
    updateMaker: function (carId, marker) {
        if (carId == undefined || carId == null
            || marker == undefined || marker == null) {
            console.warn("更新Marker缓存，参数不全！");
            return;
        }
        var index = this.indexOf(carId);
        if (index < 0) {
            console.warn("更新Marker缓存，缓存不存在！")
        }
        this.markers.splice(index, 1, marker);
    },
    updateTrDom: function (carId, trDom) {
        if (carId == undefined || carId == null
            || trDom == undefined || trDom == null) {
            console.warn("更新TrDom缓存，参数不全！");
            return;
        }
        var index = this.indexOf(carId);
        if (index < 0) {
            console.warn("更新TrDom缓存，缓存不存在！")
        }
        this.trDoms.splice(index, 1, trDom);
    },
    getMarker: function (carId) {
        if (carId == undefined || carId == null) {
            return null;
        }
        var index = this.indexOf(carId);
        if (index < 0) {
            return null;
        }
        return this.markers[index];
    },
    getTrDom: function (carId) {
        if (carId == undefined || carId == null) {
            return null;
        }
        var index = this.indexOf(carId);
        if (index < 0) {
            return null;
        }
        return this.trDoms[index];
    },
    getTrDoms: function () {
        var trDomStr = "";
        this.trDoms.forEach(function (value) {
            trDomStr += value;
        });
        return trDomStr;
    }
};

function initCarIcon(alarm) {
    if (alarm == "是") {
        return carIconR;
    }
    return carIconY;
}

function flushTrack(tracks) {
    tracks.forEach(function (track) {
        var carId = track.carId;
        if (onlineCarIds.indexOf(carId) < 0) {
            return;
        }
        var point = new BMap.Point(track.longitude, track.latitude);
        // console.log("point: (" + track.longitude + "," + track.latitude + ")")
        var carLabel = new BMap.Label(track.carNumber + ", " + track.carStatus, {
            position: point,
            offset: carLabelOffset
        });
        carLabel.setStyle(carLabelStyle);
        var carMarker = new BMap.Marker(point, {
            icon: initCarIcon(track.alarm),
            rotation: -track.angle
        });
        carMarker.setLabel(carLabel);
        carMarker.addEventListener("click", function (e) {
            layer.open({
                type: 2,
                title: ['车辆轨迹查看', 'font-size:14px;color:#ffffff;background:#478de4;'],
                shadeClose: true,
                shade: 0.6,
                resize: false,
                area: ['800px', '560px'],
                content: '../../normal/car/monitor/carMonitorFocusTrack.html?wsUrl=' + wsUrl +
                '&carId=' + carId + '&carNumber=' + encodeURIComponent(track.carNumber) +
                '&parentSession=' + sessionId + '&user=' + encodeURIComponent(userJson),
                end: function () {
                    ws.send(focusCancelOrder);
                }
            });
        });
        var carTr = "<tr id='car_" + carId + "'>" +
            "<td class=\"car-num\">" + track.carNumber + "</td>" +
            "<td class=\"car-company\">" + track.carCom + "</td>" +
            "<td class=\"car-coordinate\">(" + track.longitude + ", " + track.latitude + ")</td>" +
            "<td class=\"car-velocity\">" + track.velocity + "</td>" +
            "<td class=\"car-aspect\">" + angle2aspect(track.angle) + "</td>" +
            "<td class=\"car-status\">" + track.carStatus + "</td>" +
            "<td class=\"car-alarm\">" + track.alarm + "</td>" +
            "<td class=\"car-online\">在线</td>" +
            "</tr>";
        trackCache.putCache(carId, carMarker, carTr);
    });
    markerClusterer.clearMarkers();
    markerClusterer.addMarkers(trackCache.markers);
    $tbody.html(trackCache.getTrDoms());
}

function dealOnlineCarsCache(onlineCars) {
    onlineCars.forEach(function (carId) {
        var onlineIndex = onlineCarIds.indexOf(carId);
        if (onlineIndex == -1) {
            onlineCarIds.push(carId);
        }
        var index = trackCache.indexOf(carId);
        if (index > -1) {
            var trDom = trackCache.trDoms[index];
            if (trDom.index("离线") > -1) {
                trDom = trDom.replace("离线", "在线");
                trackCache.updateTrDom(carId, trDom);
                $("#car_" + carId).children().last().html("在线");
            }
        }
    })
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
        var index = trackCache.indexOf(carId);
        if (index > -1) {
            markerClusterer.removeMarker(trackCache.markers[index]);
            var trDom = trackCache.trDoms[index];
            var startIndex = trDom.indexOf("<td class=\"car-coordinate\">"),
                endIndex = trDom.indexOf("<td class=\"car-alarm\">");
            trDom = trDom.replace(trDom.slice(startIndex, endIndex), offlineTd);
            trDom = trDom.replace("在线", "离线");
            trackCache.updateTrDom(trDom);
            $("#car_" + carId).replaceWith(trDom);
            // $tbody.html(trackCache.getTrDoms());
        }
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
        var index = trackCache.indexOf(carId);
        if (index > -1) {
            var trDom = trackCache.trDoms[index];
            if (trDom.indexOf("离线") > -1) {
                trDom = trDom.replace("离线", "在线");
                trackCache.updateTrDom(trDom);
                $("#car_" + carId).children().last().html("在线");
            }
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
    // iframe高度自适应
    $(window).resize(function () {
        if ($('#car_info').is(':hidden')) {
            $(".map").height($(window).height() - 88);
        } else {
            $(".map").height($(window).height() - 90 - $("#car_info").height());
        }
    }).resize();

    // 固定表头
    var tableCont = document.querySelector('#table-cont');

    /**
     * scroll handle
     * @param {event} e -- scroll event
     */
    function scrollHandle(e) {
        // console.log(this)
        var scrollTop = this.scrollTop;
        this.querySelector('.table-head').style.transform = 'translateY(' + scrollTop + 'px)';
    }

    tableCont.addEventListener('scroll', scrollHandle);

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
                $("#search_btn").attr('disabled', true);
                layer.alert("当前账号权限下无可查询车辆！", {
                    icon: 0,
                    title: ['警告', 'font-size:14px;color:#ffffff;background:#478de4;']
                });
            }
            var realtimeSelectObj = $("#carno");
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
            realtimeSelectObj.append(carOpts);
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

            realtimeSelectObj.comboSelect();
            realtimeSelectObj.hide();
            realtimeSelectObj.closest(".combo-select").css({
                width: '346px',
                height: '28px',
                'z-index': 10000,
                "margin-bottom": "0px"
            });
            realtimeSelectObj.siblings(".combo-dropdown").css("max-height", "180px");
            realtimeSelectObj.siblings(".combo-input").height(2);
        }
    ).error(function (XMLHttpRequest, textStatus, errorThrown) {
        if (XMLHttpRequest.readyState == 4 && XMLHttpRequest.status == 200 && textStatus == "parsererror") {
            layer.confirm('登录失效，是否刷新页面重新登录？', {
                icon: 0,
                title: ['登录失效', 'font-size:14px;color:#ffffff;background:#478de4;']
            }, function () {
                location.reload(true);
            });
        }
    });

    userId = getUrlParam("user");
    var ctx = getUrlParam("ctx");
    wsUrl = window.location.host + ctx;
    if (window.console) console.log("wsUrl: " + wsUrl);

    var order = {
        biz: 'general',
        user: userId
    };
    var orderText = JSON.stringify(order);

    function createWebSocket() {
        if ('WebSocket' in window) {
            ws = new ReconnectingWebSocket("ws://" + wsUrl + "/track");
        // } else if ('MozWebSocket' in window) {
        //     ws = new MozWebSocket("ws://" + wsUrl + "/track");
        } else {
            // ws = new SockJS("http://" + wsUrl + "/sockjs/track");
            layer.alert("您的浏览器不支持websocket协议，建议使用新版谷歌、火狐等浏览器，请勿使用IE10以下浏览器，360浏览器请使用极速模式，不要使用兼容模式！");
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

    /** 设备绑定 */
    // 车载终端绑定
    $("#bind_terminal").click(function () {
        if (validCarCount == 0) {
            layer.alert("当前账号权限下无可操作车辆！", {icon: 0, title: ['警告', 'font-size:14px;color:#ffffff;background:#478de4;']});
            return;
        }
        layer.open({
            type: 2,
            title: ['设备绑定', 'font-size:14px;color:#ffffff;background:#478de4;'],
            shadeClose: true,
            shade: 0.8,
            resize: false,
            area: ['540px', '435px'],
            content: 'bind/carBind.html'
        });
    });
    // 锁绑定
    $("#bind_lock").click(function () {
        if (validCarCount == 0) {
            layer.alert("当前账号权限下无可操作车辆！", {icon: 0, title: ['警告', 'font-size:14px;color:#ffffff;background:#478de4;']});
            return;
        }
        layer.open({
            type: 2,
            title: ['设备绑定', 'font-size:14px;color:#ffffff;background:#478de4;'],
            shadeClose: true,
            shade: 0.8,
            resize: false,
            area: ['540px', '435px'],
            content: 'bind/lockBind.html'
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
            shadeClose: true,
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
            shadeClose: true,
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
            shadeClose: true,
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
            shadeClose: true,
            shade: 0.8,
            resize: false,
            area: ['640px', '600px'],
            // area: '640px',
            content: 'conf/cancelUpgrade.html'
        });
    });

    /** 实时监控 */

    $.getJSON("../../manage/transcom/getCompanyList.do",
        function (data, textStatus, jqXHR) {
            var selectObj = $('#company');
            var comHtml = "";
            for (var i = 0, len = data.length; i < len; i++) {
                var company = data[i];
                comHtml += "<option value=" + company.id + ">" + company.name + "</option>";
            }
            selectObj.append(comHtml);
        }
    ).error(function (XMLHttpRequest, textStatus, errorThrown) {
        if (XMLHttpRequest.readyState == 4 && XMLHttpRequest.status == 200 && textStatus == "parsererror") {
            layer.confirm('登录失效，是否刷新页面重新登录？', {
                icon: 0,
                title: ['登录失效', 'font-size:14px;color:#ffffff;background:#478de4;']
            }, function () {
                location.reload(true);
            });
        }
    });

    $("#scope").change(function () {
        var scope = $("#scope").val();
        if (scope == 1) {
            $("#select_name").html("车牌号");
            $("#company").hide();
            $("#carno").closest(".combo-select").show();
            // $("#carcom").replaceWith("<input type=\"text\" class=\"editInfo\" id=\"carcom\">");
        } else {
            $("#select_name").html("运输公司名称");
            $("#carno").closest(".combo-select").hide();
            $("#company").show();
            // $("#carcom").replaceWith(comHtml);
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

    $("#realtime_monitor").click(function () {
        if (validCarCount == 0) {
            layer.alert("当前账号权限下无可操作车辆！", {icon: 0, title: ['警告', 'font-size:14px;color:#ffffff;background:#478de4;']});
            return;
        }
        realtimeLayer = layer.open({
            type: 1,
            title: ['车辆实时监控', 'font-size:14px;color:#ffffff;background:#478de4;'],
            shadeClose: true,
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
        var scope = $("#scope").val();
        var carNumber = "";
        var comId = 0;
        if (scope == 1) {
            carNumber = trimAll($("#carno").val());
            if (carNumber == "") {
                layer.alert('未选择系统已有车辆！', {icon: 2}, function (index2) {
                    layer.close(index2);
                    $("#carno").focus();
                });
                return;
            }
        } else {
            comId = $("#company").val();
        }
        var interval = $("#interval").val();
        var duration = $("#duration").val();
        layer.open({
            type: 2,
            title: ['车辆实时监控', 'font-size:14px;color:#ffffff;background:#478de4;'],
            shadeClose: true,
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
                    shadeClose: true,
                    shade: 0.8,
                    resize: false,
                    area: ['540px', '435px'],
                    content: '../../manage/car/carStatusDispatch.do?' + encodeURI('mode=' + mode + '&carNumber=' + carNumber)
                });
            }
        );
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
        layer.open({
            type: 2,
            title: ['车载终端配置', 'font-size:14px;color:#ffffff;background:#478de4;'],
            shadeClose: true,
            shade: 0.8,
            resize: false,
            area: ['540px', '435px'],
            content: 'remote/unlockReset.html'
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
                        shadeClose: true,
                        shade: 0.8,
                        resize: false,
                        area: ['540px', '435px'],
                        content: '../../manage/car/changeDispatch.do?' + encodeURI('carNumber=' + carNumber)
                    });
                }
            }
        ).error(function (XMLHttpRequest, textStatus, errorThrown) {
            if (XMLHttpRequest.readyState == 4 && XMLHttpRequest.status == 200 && textStatus == "parsererror") {
                layer.confirm('登录失效，是否刷新页面重新登录？', {
                    icon: 0,
                    title: ['登录失效', 'font-size:14px;color:#ffffff;background:#478de4;']
                }, function () {
                    location.reload(true);
                });
            }
            return;
        });


    });

});


