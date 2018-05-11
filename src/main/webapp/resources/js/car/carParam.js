function changeLock(obj, lockId) {
    var tr = $(obj).closest('tr');
    var lockType = tr.children().last();
    for (var i = 0, len = locks.length; i < len; i++) {
        if (locks[i].id == lockId) {
            lockType.html(locks[i].type);
            break;
        }
    }
}
$(function() {
    function getCars() {
        var cars = [];
        $.ajax({
            type: "get",
            async: false, //不异步，先执行完ajax，再干别的
            url: "../../manage/car/getCarList.do",
            dataType: "json",
            success: function(response) {
                for (var i = 0, len = response.length; i < len; i++) {
                    var res = response[i];
                    var car = {};
                    car.id = res.id;
                    if (isNull(res.vehicleDevice)) {
                        car.name = res.carNumber;
                        car.terminal = null;
                    } else {
                        car.name = res.carNumber + "(已绑定车载终端)";
                        car.terminal = res.vehicleDevice.deviceId;
                    }
                    cars.push(car);
                }
            }
        });
        return cars;
    }

    function getTerminals() {
        var terminals = [];
        $.ajax({
            type: "get",
            async: false, //不异步，先执行完ajax，再干别的
            url: "../../manage/car/findUnusedTerminals.do",
            dataType: "json",
            success: function(response) {
                for (var i = 0, len = response.length; i < len; i++) {
                    var res = response[i];
                    var terminal = {};
                    terminal.id = res.deviceId;
                    terminal.name = toHexId(res.deviceId);
                    terminals.push(terminal);
                }
            }
        });
        return terminals;
    }

    function getBindedCars() {
        var bindedCars = [];
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
                    car.terminal = res.vehicleDevice.deviceId;
                    car.store = res.storeNum;
                    bindedCars.push(car);
                }
            }
        });
        return bindedCars;
    }

    function findUnusedLocks() {
        var locks = [];
        $.ajax({
            type: "get",
            async: false, //不异步，先执行完ajax，再干别的
            url: "../../manage/car/findUnusedLocks.do",
            dataType: "json",
            success: function(response) {
                for (var i = 0, len = response.length; i < len; i++) {
                    var res = response[i];
                    var lock = {};
                    lock.id = res.lockId;
                    lock.type = res.type;
                    locks.push(lockId);
                }
            }
        });
        return locks;
    }

    var opts = {
        dataName: 'name', //option的html
        dataId: 'id', //option的value
        fontSize: '14', //字体大小
        optionFontSize: '14', //下拉框字体大小
        textIndent: 6, //字体缩进							
        color: '#57647c', //输入框字体颜色
        optionColor: '#57647c', //下拉框字体颜色
        arrowColor: '#57647c', //箭头颜色
        borderColor: '#d7dbe2', //边线颜色
        hoverColor: '#f3f3f3', //下拉框HOVER颜色						
        borderWidth: 1, //边线宽度
        arrowBorderWidth: 1, //箭头左侧分割线宽度。如果为0则不显示
        borderRadius: 3, //边线圆角		
        // placeholder: '请输入文字', //默认提示				
        // defalut: 'firstData', //默认显示内容。如果是'firstData',则默认显示第一个
        retur: false, //是否阻止在建立数据后调用回掉函数
        allowInput: true, //是否允许输入						
        width: 400, //宽
        height: 26, //高
        optionMaxHeight: 210 //下拉框最大高度
    };
    var cars = getCars();
    var bindedCars = getBindedCars();
    var terminals = getTerminals();
    var carSelect;
    if ("car" == "${mode}") {
        carSelect = new SelectBox($('#carno'), bindedCars, function(result) {
            console.log(result.name + ' ' + result.id);
        }, opts);
    } else {
        carSelect = new SelectBox($('#carno'), cars, function(result) {
            console.log(result.name + ' ' + result.id);
        }, opts);
    }
    var terminalSelect = new SelectBox($('#terminal'), terminals, function(result) {
        console.log(result.name + ' ' + result.id);
    }, opts);
    carSelect.setValue("${car.carNumber}");
    terminalSelect.setValue(toHexId("${car.vehicleDevice.deviceId}"));

    changeLock = function(obj, lockId) {
        var tr = $(obj).closest('tr');
        var lockType = tr.children().last();
        for (var i = 0, len = locks.length; i < len; i++) {
            if (locks[i].id == lockId) {
                lockType.html(locks[i].type);
                break;
            }
        }
    };

    var locks = findUnusedLocks();
    var lockHtml = "<td><select class=\"lockIds\" onclick=\"changeLock(this,this.value)\">";
    for (var i = 0, len = locks.length; i < len; i++) {
        lockHtml += "<option value=\"" + locks[i].id + "\">" + toHexId(locks[i].id) + "</option>"
    }
    lockHtml += "</select></td><td>" + locks[0].type + "</td>";

    var tableHtml = "";
    for (var i = 0; i < "${car.storeNum}"; i++) {
        tableHtml += "<tr><td>" + i + "</td>" + lockHtml + "</tr>";
    }
    $("#lock_info").append(tableHtml);


    $("#confirm").click(function() {
        var carNumber = carSelect.getResult().name;
        var terminalId = parseInt(terminalSelect.getResult().name, 16);
        var url = "../../manage/car/terminalBind.do";
        var param = "carNumber=" + carNumber + "&terminalId=" + terminalId;
        $.post(url, encodeURI(param),
            function(data) {
                if ("error" == data.msg) {
                    layer.msg("绑定失败！", {
                        icon: 2,
                        time: 500
                    });
                } else {
                    layer.msg("绑定成功！", {
                        icon: 1,
                        time: 500
                    }, function() {
                        parent.layer.close(index);
                    });
                }
            },
            "json"
        );
    });

});