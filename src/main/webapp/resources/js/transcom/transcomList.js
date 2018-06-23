var dispatch;
var refreshPage;
$(function() {
    dispatch = function(mode, id) {
        var title = "添加运输公司";
        var h = "344px";
        if ("edit" == mode) {
            title = "修改运输公司信息";
            h = "380px";
        }
        if ("view" == mode) {
            title = "查看运输公司信息";
            h = "341px";
        }
        layer.open({
            type: 2,
            title: ['运输公司管理（' + title + '）', 'font-size:14px;color:#ffffff;background:#478de4;'],
            shadeClose: true,
            shade: 0.8,
            resize: false,
            area: ['540px', h],
            content: '../../manage/transcom/dispatch.do?' + encodeURI('mode=' + mode + '&id=' + id), //iframe的url
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
            $.getJSON("../../manage/transcom/getSuperiorCompanyList.do",
                function(data) {
                    var companies = eval(data);
                    var len = companies.length;
                    for (var i = 0; i < len; i++) {
                        var company = companies[i];
                        $("#search_text").append("<option value=" + company.id + ">" + company.name + "</option>");
                    }
                }
            ).error(function (XMLHttpRequest, textStatus, errorThrown) {
                if (XMLHttpRequest.readyState == 4 && XMLHttpRequest.status == 200 && textStatus == "parsererror") {
                    layer.confirm('登录失效，是否刷新页面重新登录？', {
                        icon: 0,
                        title: ['登录失效', 'font-size:14px;color:#ffffff;background:#478de4;']
                    }, function() {
                        location.reload(true);
                    });
                }
            });
        }
    });

    $("#search_btn").click(function() {
        var type = $("#search_type").val();
        if (type == 0) {
            layer.alert('请选择查询类型！', { icon: 6 });
        } else {
            var transcomName = "";
            var superiorId = "";
            if (type == 1) {
                transcomName = trimAll($("#search_text").val());
            } else {
                superiorId = $("#search_text").val();
            }
            showList(transcomName, superiorId, 1);
        }
    });

    refreshPage = function () {
        var pageId = $("#page_id").val();
        if (isNull(pageId)) {
            pageId = 1;
        }
        var transcomName = $("#cname").val();
        if (isNull(transcomName)) {
            transcomName = "";
        }
        var superiorId = $("#supid").val();
        if (isNull(superiorId)) {
            superiorId = "";
        }
        showList(transcomName, superiorId, pageId);
    };

    $("#page_id").change(function() {
        refreshPage();
    });

    $("#page_size").change(function() {
        var transcomName = $("#cname").val();
        if (isNull(transcomName)) {
            transcomName = "";
        }
        var superiorId = $("#supid").val();
        if (isNull(superiorId)) {
            superiorId = "";
        }
        showList(transcomName, superiorId, 1);
    });
});