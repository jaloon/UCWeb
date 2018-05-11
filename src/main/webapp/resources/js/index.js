var ws = null;

function eliminateAlarm(alarmId, eAlarm) {
    $.post("manage/car/eliminateAlarm.do", "eAlarm=" + eAlarm,
        function(data) {
            if ("error" == data.msg) {
                layer.msg("消除报警失败！", { icon: 2, time: 500 }, function() {
                    $("#" + alarmId).removeClass("highlight");
                });
            } else {
                layer.msg("消除报警成功！", { icon: 1, time: 500 }, function() {
                    alarmCount--;
                    if (alarmCount <= 0) {
                        grayscale(tipIcoObj);
                    }
                    $("#" + alarmId).remove();
                });
            }
        },
        "json"
    );
}
$(function() {
    var tipIcoObj = $(".alarm-tip");
    var tipIcoUrl = tipIcoObj.attr("src");
    var css3filter = grayscale(tipIcoObj);
    var ctx = $("#ctx").val();
    var wsUrl = window.location.host + ctx;
    var alarmCount = 0;
    var tipCount = 0;
    var alarmIds = [];
    var currentAlarmId;
    var record;
    var alarmNotify;
    if (window.Notification) {
        alarmNotify = new iNotify({
            message: '有新报警信息！', // 标题
            effect: 'flash', // flash | scroll 闪烁还是滚动
            onclick: function(n) { // 点击通知弹窗事件
                n.close();
                tipCount--;
                alarmIds.removeByValue(currentAlarmId);
                if (tipCount <= 0) {
                    alarmNotify.faviconClear();
                    alarmNotify.setTitle();
                } else {
                    alarmNotify.setFavicon(tipCount);
                }
                layer.open({
                    type: 2,
                    title: ['报警信息查看', 'font-size:14px;color:#ffffff;background:#478de4;'],
                    shadeClose: true,
                    shade: 0.8,
                    area: ['800px', '560px'],
                    content: 'normal/alarm/alarmTipView.html?' + encodeURI('record=' + record)
                });
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
        ws = new WebSocket("ws://" + wsUrl + "/alarm");
    } else if ('MozWebSocket' in window) {
        ws = new MozWebSocket("ws://" + wsUrl + "/alarm");
    } else {
        ws = new SockJS("http://" + wsUrl + "/sockjs/alarm");
    }
    ws.onopen = function(event) {
        if (window.console) console.log("websocket connected");
        ws.send(JSON.stringify({
            biz: 1,
            userId: $("#id").val()
        }));
    };
    ws.onmessage = function(event) {
        if (window.console) console.log("websocket receive message");
        var receive = event.data;
        var receiveObj = JSON.parse(receive);
        var biz = receiveObj.biz;
        switch (biz) {
            case 100: //消除报警
                receiveEliminat(receiveObj);
                break;
            case 110: //报警
                receiveAlarm(receive, receiveObj);
                break;
            case 120: //延时推送报警
                receiveDelay(receiveObj);
                break;
            case 130: //忽略报警
                receiveIgnore(receiveObj);
                break;
            default:
                break;
        }
    };
    ws.onerror = function(event) {
        if (window.console) console.log("websocket error");
    };
    ws.onclose = function(event) {
        if (window.console) console.log("websocket closed： " + event);
    };

    function receiveEliminat(receiveObj) {
        alarmCount--;
        if (alarmCount <= 0) {
            grayscale(tipIcoObj);
        }
        removeAlarm(receiveObj.clearAlarmId, 1);
    }

    function receiveAlarm(receive, receiveObj) {
        alarmCount++;
        removeGrayscale(tipIcoObj, css3filter, tipIcoUrl);
        addAlarm(receiveObj, 2);
        record = encodeURIComponent(receive);
        if (window.Notification && Notification.permission === "granted") {
            tipCount++;
            currentAlarmId = receiveObj.id;
            alarmIds.push(currentAlarmId);
            alarmNotify.setTitle(true).setFavicon(tipCount).notify({
                title: "报警通知",
                body: parseAlarm(receiveObj)
            }).player();
        } else {
            iziToast.info({
                title: '报警通知',
                message: parseAlarm(receiveObj),
                timeout: 5000,
                position: 'bottomRight',
                buttons: [
                    ['<button>查看报警信息</button>', function(instance, toast) {
                        instance.hide({ transitionOut: 'fadeOutUp' }, toast);
                        layer.open({
                            type: 2,
                            title: ['报警信息查看', 'font-size:14px;color:#ffffff;background:#478de4;'],
                            shadeClose: true,
                            shade: 0.8,
                            area: ['800px', '560px'],
                            content: 'normal/alarm/alarmTipView.html?' + encodeURI('record=' + record)
                        });
                    }]
                ]
            });
        }
    }

    function receiveDelay(receiveObj) {
        receiveIgnore(receiveObj);
        var alarmCacheList = receiveObj.alarmCacheList;
        var len = alarmCacheList.length;
        if (len > 0) {
            iziToast.info({
                title: '报警通知',
                message: len + '条新报警信息',
                timeout: 5000,
                position: 'bottomRight',
            });
        }
    }

    function receiveIgnore(receiveObj) {
        var alarmRecords = receiveObj.alarmRecords;
        alarmCount += alarmRecords.length;
        if (alarmCount > 0) {
            removeGrayscale(tipIcoObj, css3filter, tipIcoUrl);
            alarmRecords.forEach(function(alarm) {
                addAlarm(alarm, 1);
            });
        }
    }

    function addAlarm(alarm, type) {
        //type 1 正常显示，2 高亮显示
        var eAlarm = encodeURIComponent(JSON.stringify({
            id: alarm.id,
            terminalId: alarm.terminalId,
            deviceType: alarm.deviceType,
            deviceId: alarm.deviceId,
            alarmType: (2 << (alarm.type - 1)),
            forward: 0 //不转发
        }));
        //table#alarm_tips的最后一行
        var trHtml = "<tr id='" + alarm.id + "'>" +
            "<td>" + alarm.id + "</td>" +
            "<td>" + parseAlarm(alarm) + "</td>" +
            "<td><button onclick=\"eliminateAlarm(" + alarm.id + ", '" + eAlarm + "')\">消除报警</button></td>" +
            "</tr>";
        $("#alarm_tips tbody").append(trHtml);
        if (type == 2) {
            $("#alarm_tips tr:last").addClass("highlight");
        }
    }

    function removeAlarm(alarmId, type) {
        //type 1 直接移除，2 取消高亮
        var tr = $("#" + alarmId);
        switch (type) {
            case 1:
                tr.remove();
                break;
            case 2:
                tr.removeClass("highlight");
                break;
            default:
                break;
        }
    }

    function parseAlarm(receiveObj) {
        var alarm = receiveObj.carNumber + "：";
        var dev = receiveObj.deviceType === 1 ? "车载终端" : "锁";
        var devId = toHexId(receiveObj.deviceId);
        alarm += dev + devId + "，" + receiveObj.typeName;
        return alarm;
    }

    $(".alarm-tip").click(function() {
        if (alarmCount <= 0) {
            return;
        }
        layer.open({
            type: 1,
            title: ['报警信息查看', 'font-size:14px;color:#ffffff;background:#478de4;'],
            // time: 5000,
            // offset: ['100px', $("body").width() - 300 + 'px'],
            shade: 0,
            // resize: false,
            content: $('.alarm-box')
        });
    });

    $(".profile-zone").mouseover(function() {
        $(".info-zone").css({
            "display": "block"
        });
    });
    $(".profile-zone").mouseout(function() {
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
            shadeClose: true,
            shade: 0.8,
            area: ['540px', h],
            content: 'manage/user/dispatch.do?' + encodeURI('mode=' + mode + '&id=' + id)
        });
    }

    $("#info-edit").click(function() {
        setUser("info", "395px");
    });

    $("#pwd-edit").click(function() {
        setUser("pwd", "244px");
    });

    $("#logout").click(function() {
        layer.confirm('您确定要退出系统吗？', {
            icon: 3,
            title: ['退出', 'font-size:14px;color:#ffffff;background:#478de4;']
        }, function() {
        	$.ajax({
        		url: "manage/session/logout.do",
        		cache: false,
        		dataType: "json",
        		success: function(response) {
        			if (response && response.e) {
                        layer.alert(response.msg, { icon: 5 });
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
        		}
        	});
        });
    });

    $(".left-nav dt").append("<img src='resources/images/navbar/nav_right_12.png' />");
    $(".left-nav dd").hide();

    $(".left-nav dt").click(function() {
        $(".left-nav dt").css({ "background-color": "#445065" });
        $(this).css({ "background-color": "#478de4" });
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

    $(".left-nav dt").mouseover(function() {
        if ($(this).find('img').attr("src") == "resources/images/navbar/nav_down_12.png") {
            $(this).css({ "background-color": "#478de4" });
        } else {
            $(this).css({ "background-color": "#3e81d5" });
        }
    });

    $(".left-nav dt").mouseout(function() {
        if ($(this).find('img').attr("src") == "resources/images/navbar/nav_down_12.png") {
            $(this).css({ "background-color": "#478de4" });
        } else {
            $(this).css({ "background-color": "#445065" });
        }
    });

    $(".left-nav dd").click(function() {
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
        window.showContent.location.href = $(this).find('a').attr("name"); //showContent当前页的iframe名字,js控制iframe中页面跳转
    });

    $(window).resize(function() {
        $("iframe").attr("height", $(window).height() - 66 + "px");
        $("iframe").attr("width", $(window).width() - 200 + "px");
    }).resize();

});