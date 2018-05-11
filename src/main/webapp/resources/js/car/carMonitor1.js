$(function() {
    $(window).resize(function() {
        $(".map").height($(window).height() - 80 - $(".table").height());
    }).resize();

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

    function getCars() {
        var cars = [{ id: '', name: '' }];
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
                    car.name = res.carNumber;
                    cars.push(car);
                }
            }
        });
        return cars;
    }
    var cars = getBindedCars();

    function editableCarList() {
        for (var i = 0, len = cars.length; i < len; i++) {
            var car = cars[i];
            if (i == 0) {
                $("#search_text").append("<option selected>" + car.name + "</option>");
            } else {
                $("#search_text").append("<option value=" + car.id + ">" + car.name + "</option>");
            }
        }
        $('#search_text').editableSelect();
        $('#search_text').css({
            width: '266px',
            background: 'white'
        })
    }

    editableCarList();

    $("#search_type").change(function() {
        $("#search_text").empty();
        var type = $("#search_type").val();
        if (type == 1) {
            editableCarList();
        } else {
            $('#search_text').editableSelect('destroy');
            $('#search_text').css({
                width: '296px'
            });
            $.getJSON("../../manage/transcom/getCompanyList.do",
                function(data) {
                    var companies = eval(data);
                    var len = companies.length;
                    for (var i = 0; i < len; i++) {
                        var company = companies[i];
                        $("#search_text").append("<option value=" + company.id + ">" + company.name + "</option>");
                    }
                }
            );
        }
    });

    $("#search_btn").click(function() {
        var type = $("#search_type").val();
        var carNumber = "";
        var company = "";
        if (type == 1) {
            carNumber = trimAll($("#search_text").val());
            if (!isCarNo(carNumber)) {
                layer.alert('车牌号不正确，请输入一个完整的车牌号！', { icon: 2 }, function(index2) {
                    layer.close(index2);
                    $("#search_text").select();
                });

            }
        } else {
            company = $("#search_text").val();
        }

    });
    $("#car_list").click(function() {
        if ($('.table').is(':hidden')) {
            $('.table').show();
            $('#car_list').val('隐藏车辆列表');
            $(".map").height($(window).height() - 80 - $(".table").height());
        } else {
            $('.table').hide();
            $('#car_list').val('显示车辆列表');
            $(".map").height($(window).height() - 80);
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


    /** 车辆参数设置 */
    function bindDispatch(mode) {
        var carNumber = trimAll($("#search_text").val());
        layer.open({
            type: 2,
            title: ['车辆参数设置', 'font-size:14px;color:#ffffff;background:#478de4;'],
            shadeClose: true,
            shade: 0.8,
            area: ['540px', '435px'],
            content: 
            	'../../manage/car/bindDispatch.do?' + encodeURI('mode=' + mode + '&carNumber=' + carNumber)
        });
    }

    $("#bind_terminal").click(function() {
        bindDispatch('car');
    });
    $("#bind_lock").click(function() {
        bindDispatch('lock');
    });

    /** 车辆远程控制 */
    function remoteControl(mode) {
        if (mode != "status") {
            var carNumber = trimAll($("#search_text").val());
            $.post("../../manage/car/remoteControl.do", encodeURI("mode=" + mode + "&carNumber=" + carNumber),
                function(data) {
                    if ("error" == data.msg) {
                        layer.msg("车辆远程控制失败！", { icon: 2, time: 500 });
                    } else {
                        layer.msg("车辆远程控制成功！", { icon: 1, time: 500 });
                    }
                },
                "json"
            );
        } else {
            layer.open({
                type: 2,
                title: ['车辆远程控制', 'font-size:14px;color:#ffffff;background:#478de4;'],
                shadeClose: true,
                shade: 0.8,
                area: ['540px', '435px'],
                content: '../../manage/car/carStatusDispatch.do?' + encodeURI('carNumber=' + carNumber)
            });
        }

    }

    $("#into_oildepot").click(function() {
        remoteControl("in_depot");
    });
    $("#quit_oildepot").click(function() {
        remoteControl("out_depot");
    });
    $("#into_gasstation").click(function() {
        remoteControl("in_station");
    });
    $("#quit_gasstation").click(function() {
        remoteControl("out_station");
    });
    $("#unseal").click(function() {
        remoteControl("unseal");
    });
    $("#seal").click(function() {
        remoteControl("seal");
    });
    $("#alter_status").click(function() {
        remoteControl("status");
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


    /** 车辆轨迹回放 */
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
    var carSelect = new SelectBox($('#carno'), cars, function(result) {
        console.log(result.name + ' ' + result.id);
    }, opts);
    laydate.render({
        elem: '#begin',
        type: 'datetime',
        value: '2017-10-01 00:00:00'
    });
    laydate.render({
        elem: '#end',
        type: 'datetime',
        value: new Date()
    });
});