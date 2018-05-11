$(function() {
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
        width: 172, //宽
        height: 26, //高
        optionMaxHeight: 210 //下拉框最大高度
    };
    var cars = getBindedCars();
    var carSelect = new SelectBox($('#carno'), cars, function(result) {
        console.log(result.name + ' ' + result.id);
    }, opts);
});