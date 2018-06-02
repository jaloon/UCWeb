var ws = null;
var tipIcoObj;
var tipIcoUrl;
var css3filter;
var alarmBoxLayer;
var alarmCount = 0; // 报警数
var alarmIds = []; // 报警ID缓存
var record; // 报警记录
var alarmNotify; // 报警通知对象（浏览器通知）
var receiveAlarmCount = 1 ;

function receiveEliminat(receiveObj) {
    var alarmId = receiveObj.id;
    if (alarmIds.isContain(alarmId)) {
        alarmIds.removeByValue(alarmId);
        $("#alarm_id_" + alarmId).remove();
        alarmCount--;
        if (alarmCount <= 0) {
            grayscale(tipIcoObj);
            if (alarmBoxLayer != undefined) {
                layer.close(alarmBoxLayer);
            }
            if (window.Notification && Notification.permission === "granted") {
                alarmNotify.faviconClear();
            }
        } else {
            if (window.Notification && Notification.permission === "granted") {
                alarmNotify.setTitle(true).setFavicon(alarmCount);
            }
        }
    }
}

function receiveAlarm(receive, receiveObj) {
    removeGrayscale(tipIcoObj, css3filter, tipIcoUrl);
    record = encodeURIComponent(receive);
    addAlarm(receiveObj, record);
    if (window.Notification && Notification.permission === "granted") {
        alarmNotify.setTitle(true).setFavicon(alarmCount).notify({
            title: "报警通知",
            body: parseAlarm(receiveObj)
        }).player();
    } else {
        var toast = document.querySelector('.iziToast');
        if (toast == null) {
            receiveAlarmCount = 1 ;
            iziToast.info({
                title: '报警通知',
                message: parseAlarm(receiveObj),
                timeout: 5000,
                position: 'bottomRight',
                buttons: [
                    ['<button>查看报警信息</button>', function (instance, toast) {
                        instance.hide({transitionOut: 'fadeOutUp'}, toast);
                        showAlarm(record);
                    }]
                ]
            });
        } else {
            iziToast.destroy();
            receiveAlarmCount++ ;
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

function cacheAlarm(receiveObj) {
    var list = receiveObj.cacheAlarm;
    var len = list.length;
    if (len < 1) {
        return;
    }
    removeGrayscale(tipIcoObj, css3filter, tipIcoUrl);
    for (var i = 0; i < len; i++) {
        var alarm = list[i];
        addAlarm(alarm);
    }
    if (window.Notification && Notification.permission === "granted") {
        alarmNotify.setTitle(true).setFavicon(alarmCount);
    }
    iziToast.info({
        title: '报警通知',
        message: '您有' + alarmCount + '条报警信息待处理！' ,
        timeout: 3000,
        position: 'bottomRight',
        buttons: [
            ['<button>查看报警信息列表</button>', function(instance, toast) {
                instance.hide({ transitionOut: 'fadeOutUp' }, toast);
                showAlarmBox();
            }]
        ]
    });
}

function addAlarm(alarm, record) {
    var alarmId = alarm.id;
    if (alarmCount == 0 || !alarmIds.isContain(alarmId)) {
        alarmIds.push(alarmId);
        alarmCount++;
        if (record === undefined || record === null || record === "") {
            record = encodeURIComponent(JSON.stringify(alarm));
        }
        var trHtml = "<tr class='alarm-content' id='alarm_id_" + alarmId + "' onclick=\"showAlarm('" + record + "')\">" +
            "<td class='alarm-id'>" + alarmId + "</td>" +
            "<td class='alarm-dev'>" + (alarm.deviceType == 1 ? "车载终端（" : "锁（")  + alarm.deviceId + "）</td>" +
            "<td class='alarm-type'>" + alarm.typeName + "</td>" +
            "<td class='alarm-eli' title='消除报警' onclick='eliminateAlarm(" + alarm.vehicleId + "," + alarmId + ")'><img src='../../resources/images/operate/delete.png' alt='消除报警'/></td>" +
            "</tr>";
        $("#alarm_tips tbody").append(trHtml);
    }
}

function parseAlarm(receiveObj) {
    var alarm = receiveObj.carNumber + "：";
    var dev = receiveObj.deviceType === 1 ? "车载终端" : "锁";
    var devId = "（" + receiveObj.deviceId + "）";
    alarm += dev + devId + "，" + receiveObj.typeName;
    return alarm;
}

function showAlarmBox() {
    alarmBoxLayer = layer.open({
        type: 1,
        title: ['报警信息查看', 'font-size:14px;color:#ffffff;background:#478de4;'],
        shadeClose: true,
        shade: 0.8,
        area: '582px',
        // area: ['540px', '435px'],
        resize: false,
        content: $('.alarm-box')
    });
}

function eliminateAlarm(vehicleId, alarmIds) {
    $.post("../../manage/remote/asyn_alarm_eliminate_request",
        "vehicle_id=" + vehicleId + "&alarm_ids=" + alarmIds + "&token=" + generateUUID(),
        function(data) {
            if (data.id > 0) {
                layer.msg(data.msg, {
                    icon: 2,
                    time: 500
                });
            } else {
                layer.msg("消除报警指令发送成功！", {
                    icon: 1,
                    time: 500
                }, function() {
                    var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
                    parent.layer.close(index);
                });
            }
        },
        "json"
    );
    // 阻止事件冒泡到DOM树上
    event.stopPropagation();
}

function showAlarm(record) {
    layer.open({
        type: 2,
        title: ['报警信息查看', 'font-size:14px;color:#ffffff;background:#478de4;'],
        shadeClose: true,
        shade: 0.8,
        resize: false,
        area: ['800px', '560px'],
        content: 'normal/alarm/alarmTipView.html?record=' + record
    });
}

$(function() {
    var tableCont = document.querySelector('#alarm_tips');
    /**
     * scroll handle
     * @param {event} e scroll event
     */
    function scrollHandle (e){
        // console.log(this)
        var scrollTop = this.scrollTop;
        this.querySelector('.table-head').style.transform = 'translateY(' + scrollTop + 'px)';
    }

    tableCont.addEventListener('scroll', scrollHandle);

    tipIcoObj = $(".alarm-tip");
    tipIcoUrl = tipIcoObj.attr("src");
    css3filter = grayscale(tipIcoObj);
    var ctx = $("#ctx").val();
    var wsUrl = window.location.host + ctx;
    if (window.Notification) {
        alarmNotify = new iNotify({
            message: '有报警信息待处理', // 标题
            effect: 'flash', // flash | scroll 闪烁还是滚动
            onclick: function(n) { // 点击通知弹窗事件
                n.close();
                showAlarm(record);
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
            biz: 1
            // userId: $("#id").val()
        }));
    };
    ws.onmessage = function(event) {
        if (window.console) console.log("websocket receive message");
        var receive = event.data;
        var receiveObj = JSON.parse(receive);
        var biz = receiveObj.biz;
        switch (biz) {
            case 100: //消除报警
                Concurrent.Thread.create(receiveEliminat, receiveObj);
                // receiveEliminat(receiveObj);
                break;
            case 110: //报警
                Concurrent.Thread.create(receiveAlarm, receive, receiveObj);
                // receiveAlarm(receive, receiveObj);
                break;
            case 111: //缓存报警
                Concurrent.Thread.create(cacheAlarm, receiveObj);
                // cacheAlarm(receiveObj);
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

    $(".alarm-tip").click(function() {
        if (alarmCount <= 0) {
            return;
        }
        showAlarmBox();
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
            resize: false,
            area: ['540px', h],
            content: 'manage/user/dispatch.do?' + encodeURI('mode=' + mode + '&id=' + id)
        });
    }

    $("#info-edit").click(function() {
        setUser("info", "451px");
    });

    $("#pwd-edit").click(function() {
        setUser("pwd", "235px");
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
        		},
                error: function(XMLHttpRequest, textStatus, errorThrown) {  //#3这个error函数调试时非常有用，如果解析不正确，将会弹出错误框
        		    if (XMLHttpRequest.readyState == 4 && XMLHttpRequest.status == 200 && textStatus == "parsererror") {
                        location.replace(XMLHttpRequest.responseText);
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
        var navPage = $(this).find('a').attr("name");
        var forwardUrl = navPage + "?user=" + $("#id").val() + "&ctx=" + ctx + "&token=" + generateUUID();
        window.showContent.location.href = forwardUrl; //showContent当前页的iframe名字,js控制iframe中页面跳转
    });

    $(window).resize(function() {
        $("iframe").attr("height", $(window).height() - 66 + "px");
        $("iframe").attr("width", $(window).width() - 200 + "px");
    }).resize();

});