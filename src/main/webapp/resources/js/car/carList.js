var dispatch;
var refreshPage;
$(function() {
    dispatch = function(mode, id) {
        var title = "添加车辆";
        var h = "420px";
        if ("edit" == mode) {
            title = "修改车辆信息";
            h = "534px";
        }
        if ("view" == mode) {
            title = "查看车辆信息";
            h = "483px";
        }
        layer.open({
            type: 2,
            title: ['车辆信息管理（' + title + '）', 'font-size:14px;color:#ffffff;background:#478de4;'],
            shadeClose: true,
            shade: 0.8,
            resize: false,
            area: ['540px', h],
            content: '../../manage/car/dispatch.do?' + encodeURI('mode=' + mode + '&id=' + id), //iframe的url
            end: function() {
                if (mode != "view") {
                    refreshPage();
                }
            }
        });
        // 阻止事件冒泡到DOM树上
        event.stopPropagation();
    };

    showList("", "", 1);

    $("#search_type").change(function() {
        var type = $("#search_type").val();
        if (type < 2) {
            $("#search_text").replaceWith("<input type=\"text\" class=\"search-text\" id=\"search_text\">");
        } else {
            $("#search_text").replaceWith("<select class=\"search-text\" id=\"search_text\"></select>");
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
        if (type == 0) {
            layer.alert('请选择查询类型！', { icon: 6 });
        } else {
            var carNo = "";
            var company = "";
            if (type == 1) {
                carNo = trimAll($("#search_text").val());
                if (!isCarNo(carNo)) {
                    layer.alert('车牌号不正确，请输入一个完整的车牌号！', { icon: 2 }, function(index2) {
                        layer.close(index2);
                        $("#search_text").select();
                    });
                    return;
                }
            } else {
                company = $("#search_text").val();
            }
            showList(carNo, company, 1);
        }
    });

    refreshPage = function () {
        var pageId = $("#page_id").val();
        if (isNull(pageId)) {
            pageId = 1;
        }
        var carNo = $("#cnum").val();
        if (isNull(carNo)) {
            carNo = "";
        }
        var company = $("#ccom").val();
        if (isNull(company)) {
            company = "";
        }
        showList(carNo, company, pageId);
    };

    $("#page_id").change(function() {
        refreshPage();
    });

    $("#page_size").change(function() {
        var carNo = $("#cnum").val();
        if (isNull(carNo)) {
            carNo = "";
        }
        var company = $("#ccom").val();
        if (isNull(company)) {
            company = "";
        }
        showList(carNo, company, 1);
    });
});