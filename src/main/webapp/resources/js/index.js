var ws = null;
var tipIcoObj;
var tipIcoUrl;
var css3filter;
var alarmBoxLayer;
var alarmId;
var alarmCount = 0; // 报警数
var alarmTagCache = new Map(); // 报警标识缓存
var alarmNotify; // 报警通知对象（浏览器通知）
var receiveAlarmCount = 1;

function receiveEliminat(alarmlist) {
    alarmTagCache.clear();
    alarmCount = alarmlist.length;
    if (alarmCount < 1) {
        $("#alarm_tips tbody").html("");
        grayscale(tipIcoObj);
        if (alarmBoxLayer != undefined) {
            layer.close(alarmBoxLayer);
        }
        if (window.Notification && Notification.permission === "granted") {
            alarmNotify.faviconClear();
        }
        return;
    }
    upAlarmList(alarmlist);
}

function receiveAlarm(alarmInfo) {
    removeGrayscale(tipIcoObj, css3filter, tipIcoUrl);
    addAlarm(alarmInfo);
    if (window.Notification && Notification.permission === "granted") {
        alarmNotify.setTitle(true).setFavicon(alarmCount).notify({
            title: "报警通知",
            body: parseAlarm(alarmInfo)
        }).player();
    } else {
        var toast = document.querySelector('.iziToast');
        if (toast == null) {
            receiveAlarmCount = 1;
            iziToast.info({
                title: '报警通知',
                message: parseAlarm(alarmInfo),
                timeout: 5000,
                position: 'bottomRight',
                buttons: [
                    ['<button>查看报警信息</button>', function (instance, toast) {
                        instance.hide({transitionOut: 'fadeOutUp'}, toast);
                        showAlarm(alarmInfo.alarmId);
                    }]
                ]
            });
        } else {
            iziToast.destroy();
            receiveAlarmCount++;
            iziToast.info({
                title: '报警通知',
                message: '收到' + receiveAlarmCount + "条新报警信息！",
                timeout: 5000,
                position: 'bottomRight',
                buttons: [
                    ['<button>查看报警信息</button>', function (instance, toast) {
                        instance.hide({transitionOut: 'fadeOutUp'}, toast);
                        showAlarmBox();
                    }]
                ]
            });
        }
    }
}

function upAlarmList(alarmlist) {
    removeGrayscale(tipIcoObj, css3filter, tipIcoUrl);
    $(".alarm-num").html(alarmCount);
    var trHtml = "";
    // var jsonData = [];
    alarmlist.forEach(function (alarmInfo) {
        var alarm_id = alarmInfo.alarmId;
        trHtml += "<tr class='alarm-content' id='alarm_id_" + alarm_id + "' onclick='showAlarm(" + alarm_id + ")'>"
            + "<td class='alarm-car'>" + alarmInfo.carNumber + "</td>"
            + "<td class='alarm-dev'>" +  parseAlarmDev(alarmInfo) + "</td>"
            + "<td class='alarm-type'>" + alarmInfo.alarmName + "</td>"
            + "<td class='alarm-eli' title='消除报警' onclick='eliminateAlarm(\"" + alarmInfo.carNumber + "\"," + alarm_id + ")'>"
            + "<img src='../../resources/images/operate/delete.png' alt='消除报警'/></td>"
            + "</tr>";
        // jsonData.push({
        //     id: alarm_id,
        //     car: alarmInfo.carNumber,
        //     dev: parseAlarmDev(alarmInfo),
        //     type: alarmInfo.alarmName
        // });
        alarmTagCache.set(alarmInfo.alarmTag, alarm_id);
    });
    $("#alarm_tips tbody").html(trHtml);
    // // 更新表格数据
    // var tableHtml = template('table-tpl', {data: jsonData});
    // $('.c-table').eq(0).data('table').updateHtml(tableHtml);
    if (window.Notification && Notification.permission === "granted") {
        alarmNotify.setTitle(true).setFavicon(alarmCount);
    }
}

function parseAlarmDev(alarm) {
    if (alarm.deviceType == 1) {
        return "车载终端（" + alarm.deviceId + "）";
    }
    if (alarm.deviceType == 2) {
        var lock = alarm.alarmLock;
        var alaramDev = "锁（" + alarm.deviceId + "）" + "<br>仓" + lock.storeId
            + (lock.seat == 1 ? "-上仓锁-" : "-下仓锁-") + lock.seatIndex;
        return alaramDev;
    }
    return "未知报警设备类型（" + alarm.deviceType + "）";
}

function cacheAlarm(alarmlist) {
    alarmCount = alarmlist.length;
    if (alarmCount < 1) {
        grayscale(tipIcoObj);
        return;
    }
    upAlarmList(alarmlist);
    iziToast.info({
        title: '报警通知',
        message: '您有' + alarmCount + '条报警信息待处理！',
        timeout: 3000,
        position: 'bottomRight',
        buttons: [
            ['<button>查看报警信息列表</button>', function (instance, toast) {
                instance.hide({transitionOut: 'fadeOutUp'}, toast);
                showAlarmBox();
            }]
        ]
    });
}

function addAlarm(alarmInfo) {
    alarmCount++;
    alarmId = alarmInfo.alarmId;
    var alarmTag = alarmInfo.alarmTag;
    if (alarmTagCache.has(alarmTag)) {
        $("#alarm_id_" + alarmTagCache.get(alarmTag)).remove();
        alarmCount--;
    }
    $(".alarm-num").html(alarmCount);
    alarmTagCache.set(alarmTag, alarmId);
    var trHtml = "<tr class='alarm-content' id='alarm_id_" + alarmId + "' onclick='showAlarm(" + alarmId + ")'>"
        + "<td class='alarm-car'>" + alarmInfo.carNumber + "</td>"
        + "<td class='alarm-dev'>" + parseAlarmDev(alarmInfo) + "</td>"
        + "<td class='alarm-type'>" + alarmInfo.alarmName + "</td>"
        + "<td class='alarm-eli' title='消除报警' onclick='eliminateAlarm(\"" + alarmInfo.carNumber + "\"," + alarmId + ")'>"
        + "<img src='../../resources/images/operate/delete.png' alt='消除报警'/></td>"
        + "</tr>";
    $("#alarm_tips tbody").prepend(trHtml);
}

function parseAlarm(alarmInfo) {
    var alarm = alarmInfo.carNumber + "：";
    var dev = alarmInfo.deviceType === 1 ? "车载终端" : "锁";
    var devId = "（" + alarmInfo.deviceId + "）";
    alarm += dev + devId + "，" + alarmInfo.alarmName;
    return alarm;
}

function showAlarmBox() {
    alarmBoxLayer = layer.open({
        type: 1,
        title: ['报警信息查看', 'font-size:14px;color:#ffffff;background:#478de4;'],
        // shadeClose: true,
        shade: 0.8,
        area: '582px',
        // area: ['540px', '435px'],
        resize: false,
        content: $('.alarm-box')
    });
}

function eliminateAlarm(carNumber, alarmIds) {
    var loadLayer = layer.load();
    $.post("../../manage/remote/asyn_alarm_eliminate_request",
        "car_number=" + carNumber + "&alarm_ids=" + alarmIds + "&token=" + generateUUID(),
        function (data) {
            layer.close(loadLayer);
            if (data.id > 0) {
                layer.msg(data.msg, {
                    icon: 2,
                    time: 500
                });
            } else {
                layer.msg("消除报警指令发送成功！", {
                    icon: 1,
                    time: 500
                }, function () {
                    var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
                    parent.layer.close(index);
                });
            }
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
    // 阻止事件冒泡到DOM树上
    stopPropagation(event);
}

function showAlarm(alarmId) {
    layer.open({
        type: 2,
        title: ['报警信息查看', 'font-size:14px;color:#ffffff;background:#478de4;'],
        // shadeClose: true,
        shade: 0.8,
        resize: false,
        area: ['800px', '560px'],
        content: 'normal/alarm/alarmTipView.html?alarmId=' + alarmId
    });
    stopPropagation(event);
}

$(function () {
    $("#alarm_tips").scroll(function (event) {
        $(".table-head").css("transform", "translateY(" + event.target.scrollTop + "px)");
    });

    // $('[role="c-table"]').jqTable();

    tipIcoObj = $(".alarm-tip");
    tipIcoUrl = tipIcoObj.attr("src");
    css3filter = grayscale(tipIcoObj);
    var ctx = $("#ctx").val();
    var httpProtocol = "http";
    var wsProtocol = "ws";
    if ("https:" === document.location.protocol || location.href.indexOf("https") > -1) {
        httpProtocol = "https";
        wsProtocol = "wss";
    }
    var wsUrl = window.location.host + ctx;
    if (window.Notification) {
        alarmNotify = new iNotify({
            message: '有报警信息待处理', // 标题
            effect: 'flash', // flash | scroll 闪烁还是滚动
            onclick: function (n) { // 点击通知弹窗事件
                n.close();
                showAlarm(alarmId);
            },
            // 可选播放声音
            audio: {
                // 可以使用数组传多种格式的声音文件
                file: ['resources/plugins/iNotify/msg.mp4',
                    'resources/plugins/iNotify/msg.mp3',
                    'resources/plugins/iNotify/msg.wav'
                ]
                // 下面也是可以的哦
                // file: 'msg.mp4'
            },
            // 标题闪烁，或者滚动速度
            interval: 1000,
            // 可选，默认绿底白字的 Favicon
            updateFavicon: {
                // favicon 字体颜色
                textColor: "#E82634",
                // 背景颜色，设置背景颜色透明，将值设置为“transparent”
                backgroundColor: "transparent"
            },
            // 可选chrome浏览器通知，默认不填写就是下面的内容
            notification: {
                title: "通知！", // 设置标题
                icon: "", // 设置图标 icon 默认为 Favicon
                body: '您来了一条新消息' // 设置消息内容
            }
        });
        alarmNotify.faviconClear();
    }
    if ('WebSocket' in window) {
        ws = new ReconnectingWebSocket(wsProtocol + "://" + wsUrl + "/alarm");
        // } else if ('MozWebSocket' in window) {
        //     ws = new MozWebSocket("ws://" + wsUrl + "/alarm");
    } else {
        ws = new SockJS(httpProtocol + "://" + wsUrl + "/sockjs/alarm");
        layer.alert("您的浏览器内核版本太低，部分功能可能无法使用，建议使用" +
            "<a href='manage/file/download/chrome_setup.rar'>Chrome</a>、" +
            "<a href='manage/file/download/firefox_setup.rar'>Firefox</a>、" +
            "<a href='manage/file/download/ie11_setup.rar'>IE11</a>、" +
            "Edge等现代浏览器，360浏览器、QQ浏览器、搜狗浏览器等双核浏览器请使用极速模式浏览！",
            {icon: 0, title: ['提示', 'font-size:14px;color:#ffffff;background:#478de4;']});
    }
    ws.onopen = function (event) {
        if (window.console) console.log("websocket connected");
        ws.send(JSON.stringify({
            biz: 1
        }));
    };
    ws.onmessage = function (event) {
        if (window.console) console.log("websocket receive message");
        var receive = event.data;
        var receiveObj = JSON.parse(receive);
        var biz = receiveObj.biz;
        switch (biz) {
            case 100: //消除报警
                Concurrent.Thread.create(receiveEliminat, receiveObj.msg);
                // receiveEliminat(receiveObj.msg);
                break;
            case 110: //报警
                Concurrent.Thread.create(receiveAlarm, receiveObj.msg);
                // receiveAlarm(receiveObj.msg);
                break;
            case 111: //缓存报警
                Concurrent.Thread.create(cacheAlarm, receiveObj.msg);
                // cacheAlarm(receiveObj.msg);
                break;
            default:
                break;
        }
    };
    ws.onerror = function (event) {
        if (window.console) console.log("websocket error");
    };
    ws.onclose = function (event) {
        if (window.console) console.log("websocket closed：", event);
    };

    setInterval(function () {
        $.post("../../manage/session/isAlive.do",
            function (data) {
                console.log("http心跳。。。");
            },
            "json"
        ).error(function (XMLHttpRequest, textStatus, errorThrown) {
            if (XMLHttpRequest.readyState == 4) {
                var http_status = XMLHttpRequest.status;
                if (http_status == 0 || http_status > 600) {
                    location.reload(true);
                } else if (http_status == 200) {
                    console.log("http心跳。。。");
                } else {
                    layer.alert("http connection error: status[" + http_status + "], " + XMLHttpRequest.statusText)
                }
            }
        });
    }, 600000);

    $(".alarm-tip").click(function () {
        if (alarmCount <= 0) {
            return;
        }
        showAlarmBox();
    });

    $(".alarm-num").click(function () {
        if (alarmCount <= 0) {
            return;
        }
        showAlarmBox();
    });

    $(".profile-zone").mouseover(function () {
        $(".info-zone").css({
            "display": "block"
        });
    });
    $(".profile-zone").mouseout(function () {
        $(".info-zone").css({
            "display": "none"
        });
    });

    function setUser(mode, h) {
        var id = $("#id").val();
        var title = "修改个人信息";
        if ("pwd" == mode) {
            title = "修改密码";
        }

        layer.open({
            type: 2,
            title: [title, 'font-size:14px;color:#ffffff;background:#478de4;'],
            // shadeClose: true,
            shade: 0.8,
            resize: false,
            area: ['540px', h],
            content: 'manage/user/dispatch.do?' + encodeURI('mode=' + mode + '&id=' + id)
        });
    }

    $("#info-edit").click(function () {
        setUser("info", "451px");
    });

    $("#pwd-edit").click(function () {
        setUser("pwd", "235px");
    });

    $("#logout").click(function () {
        layer.confirm('您确定要退出系统吗？', {
            icon: 3,
            title: ['退出', 'font-size:14px;color:#ffffff;background:#478de4;']
        }, function () {
            $.ajax({
                url: "manage/session/logout.do",
                cache: false,
                dataType: "json",
                success: function (response) {
                    if (response && response.e) {
                        layer.alert(response.msg, {icon: 5});
                    } else {
                        // ws.onclose(); // GOING_AWAY(1001) -- 页面或浏览器关闭
                        ws.close(); // NORMAL(1000) -- 正常关闭，连接已成功完成任务
                        // void close(in optional unsigned short code, in optional DOMString reason);
                        // code 可选
                        // 一个数字值表示关闭连接的状态号，表示连接被关闭的原因。如果这个参数没有被指定，默认的取值是1000 （表示正常连接关闭）
                        // reason 可选
                        // 一个可读的字符串，表示连接被关闭的原因。这个字符串必须是不长于123字节的UTF-8 文本（不是字符）
                        location.replace("login.jsp");
                    }
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {  //#3这个error函数调试时非常有用，如果解析不正确，将会弹出错误框
                    location.replace("login.jsp");
                }
            });
        });
    });

    $(".left-nav dt").append("<img src='resources/images/navbar/nav_right_12.png' />");
    $(".left-nav dd").hide();

    $(".left-nav dt").click(function () {
        $(".left-nav dt").css({"background-color": "#445065"});
        $(this).css({"background-color": "#478de4"});
        $(this).parent().find('dd').removeClass("menu_chioce");
        $(".left-nav dt img").attr("src", "resources/images/navbar/nav_right_12.png");
        $(this).find('img').attr("src", "resources/images/navbar/nav_down_12.png");
        $(".menu_chioce").slideUp(); // slideUp()通过调整高度来滑动隐藏被选元素
        // $(this).parent().find('dd').slideDown(); // slideDown()通过调整高度来滑动显示被选元素
        $(this).parent().find('dd').slideToggle(); // slideToggle()对被选元素进行滑动隐藏和滑动显示的切换
        $(this).parent().find('dd').addClass("menu_chioce");
        var item = $(this).parent().find('.first_dd');
        item.click();
    });

    $(".left-nav dt").mouseover(function () {
        if ($(this).find('img').attr("src") == "resources/images/navbar/nav_down_12.png") {
            $(this).css({"background-color": "#478de4"});
        } else {
            $(this).css({"background-color": "#3e81d5"});
        }
    });

    $(".left-nav dt").mouseout(function () {
        if ($(this).find('img').attr("src") == "resources/images/navbar/nav_down_12.png") {
            $(this).css({"background-color": "#478de4"});
        } else {
            $(this).css({"background-color": "#445065"});
        }
    });

    $(".left-nav dd").click(function () {
        $(".left-nav dd").find('a').css({
            "font-size": 14,
            "font-weight": 400,
            color: "#ffffff"
        });
        $(this).find('a').css({
            "font-size": 15,
            "font-weight": 900,
            color: "#478de4"
        });
        var navPage = $(this).find('a').attr("name");
        var forwardUrl = navPage + "?user=" + $("#id").val() + "&ctx=" + ctx + "&token=" + generateUUID();
        window.showContent.location.href = forwardUrl; //showContent当前页的iframe名字,js控制iframe中页面跳转
    });

    $(window).resize(function () {
        $("iframe").attr("height", $(window).height() - 66 + "px");
        $("iframe").attr("width", $(window).width() - 200 + "px");
    }).resize();

});