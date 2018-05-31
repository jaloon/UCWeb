<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="pop" uri="/pop-tags" %>
<pop:Permission ename="viewCar">
<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8">
    <title>车辆列表</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=center-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="../../resources/css/base.css" media="all">
    <link rel="stylesheet" href="../../resources/css/normal.css" media="all">
    <script src="../../resources/plugins/jquery-1.8.3.min.js"></script>
    <script src="../../resources/plugins/layer/layer.js"></script>
    <script src="../../resources/plugins/verify.js"></script>
    <script src="../../resources/js/base.js"></script>
    <script src="../../resources/js/normal.js"></script>
    <script src="../../resources/js/car/carList.js"></script>
    <style type="text/css">
        .car-num {
            width: 160px;
        }
        
        .car-company {
            width: 200px;
        }
        
        .car-type {
            width: 120px;
        }
        
        .car-device {
            width: 120px;
        }
        
        .car-dmodel {
            width: 120px;
        }
        
        .car-sim {
            width: 120px;
        }
        
        .car-transcard {
            width: 160px;
        }
        
        .car-store {
            width: 80px;
        }

        .car-remark {
            width: 100px;
        }
        
        .car-action {
            width: 200px;
        }
    </style>
    <script type="text/javascript">
	    
    	<pop:Permission ename="editCar">
	    function deleteCar(id, carNumber) {
	        layer.confirm('删除后不可撤销，是否确认删除？', {
	            icon: 0,
	            title: ['删除', 'font-size:14px;color:#ffffff;background:#478de4;']
	        }, function() {
	            $.post("../../manage/car/delete.do", encodeURI("id=" + id + "&carNumber=" + carNumber),
	                function(data) {
	                    if ("success" == data.msg) {
	                        layer.msg('删除成功！', { icon: 1, time: 500 }, function() {
	                            refreshPage();
	                        });
	                    } else {
	                        layer.msg('删除失败！', { icon: 2, time: 500 });
	                    }
	                },
	                "json"
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
	        });
	        // 阻止事件冒泡到DOM树上
	        event.stopPropagation();
	    }	
 		</pop:Permission>
 			
 		function showList(carNo, company, pageId) {
	        var rows = $("#page_size").val();
	        var startRow = (pageId - 1) * rows;

	        $.post(
	            "../../manage/car/ajaxFindForPage.do",
	            encodeURI("carNumber=" + carNo + "&transCompany.id=" + company + "&pageId=" + pageId + "&startRow=" + startRow + "&rows=" + rows),
	            function(data) {
	                var gridPage = eval(data);

	                var maxIndex = $("#page_id option:last").index(); //获取Select最大的索引值
	                var len = maxIndex + 1 - gridPage.total;
	                if (len > 0) {
	                    for (var i = gridPage.total > 0 ? gridPage.total : 1; i < maxIndex + 1; i++) {
	                        $("#page_id option:last").remove(); //删除Select中索引值最大Option(最后一个)
	                    }
	                } else if (len < 0) {
	                    for (var i = maxIndex + 2; i <= gridPage.total; i++) {
	                        $("#page_id").append("<option value=" + i + ">" + i + "</option>"); //为Select追加一个Option下拉项
	                    }
	                }
	                $("#page_id").val(gridPage.page);

	                $("#page_info").html("页(" + gridPage.currentRows + "条数据)/共" + gridPage.total + "页(共" + gridPage.records + "条数据)");
	                $("#cnum").val(gridPage.t.carNumber);
	                $("#ccom").val(gridPage.t.transCompany.id);
	                $(".table-body").html("");
	                var cars = gridPage.dataList;
	                var tableData = "<table width='100%'>";
	                for (var i = 0; i < gridPage.currentRows; i++) {
	                    var car = cars[i];
	                    tableData += "<tr class='list-content' onclick=\"dispatch('edit'," + car.id + ")\">" +
	                        "<td class=\"car-num\">" + car.carNumber + "</td>" +
	                        "<td class=\"car-company\">" + (car.transCompany.name == undefined ? "" : car.transCompany.name) + "</td>" +
	                        "<td class=\"car-type\">" + car.typeName + "</td>";
	                    if (isNull(car.vehicleDevice) || isNull(car.vehicleDevice.deviceId) || car.vehicleDevice.deviceId == 0) {
	                        tableData +=
	                            "<td class=\"car-device\"></td>" +
	                            "<td class=\"car-dmodel\"></td>";
	                    } else {
	                        tableData +=
	                            "<td class=\"car-device\">" + car.vehicleDevice.deviceId + "</td>" +
	                            "<td class=\"car-dmodel\">" + car.vehicleDevice.model + "</td>";
	                    }
	                    tableData += "<td class=\"car-sim\">" + car.sim + "</td>";
	                    if (isNull(car.transportCard) || car.transportCard.transportCardId == 0) {
	                        tableData += "<td class=\"car-transcard\"></td>";
	                    } else {
	                        tableData += "<td class=\"car-transcard\">" + car.transportCard.transportCardId + "</td>";
	                    }
	                    tableData += "<td class=\"car-store\">" + car.storeNum + "</td>" +
	                        "<td class=\"car-remark\">" + car.remark + "</td>" +
	                        "<td class=\"car-action\">" +
	                        "<img class='edit-btn' src=\"../../resources/images/operate/view.png\" alt=\"查看\" title=\"查看\" onclick=\"dispatch('view'," + car.id + ")\">" +
	                       	<pop:Permission ename="editCar">
	                        "&emsp;<img class='edit-btn' src=\"../../resources/images/operate/edit.png\" alt=\"编辑\" title=\"编辑\" onclick=\"dispatch('edit'," + car.id + ")\">&emsp;" +
	                        "<img class='edit-btn' src=\"../../resources/images/operate/delete.png\" alt=\"删除\" title=\"删除\" onclick=\"deleteCar(" + car.id + "," + car.carNumber + ")\">" +
	                        </pop:Permission>
	                        "</td>" +
	                        "</tr>";
	                }
	                tableData += "</table>";
	                tableData = tableData.replace(/>null</g, "><");
	                $(".table-body").html(tableData);
	            },
	            "json"
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
 		
    </script>
</head>

<body>
    <div class="container">
    	<div class="nav-addr">
	        <img src="../../resources/images/navbar/car.png"> 车辆管理
	        <img src="../../resources/images/navbar/nav_right_12.png"> 车辆信息管理
	    </div>
        <div class="search-zone">
            <select class="search-type" id="search_type">
                <option value="0">查询类型</option>
                <option value="1">车牌号码</option>
                <option value="2">所属公司</option>
            </select>
            <input type="text" class="search-text" id="search_text">
            <input type="button" class="search-btn button" id="search_btn" value="查询">
            <pop:Permission ename="editCar">
            <input type="button" class="button" id="add" value="添加" onclick="dispatch('add',0)">
            </pop:Permission>
        </div>
        <div class="data-zone">
            <div class='table-cont' id='table-cont'>
                <table width="100%">
                    <thead class="table-head">
                        <tr>
                            <th class="car-num">车牌号码</th>
                            <th class="car-company">所属公司</th>
                            <th class="car-type">车辆类型</th>
                            <th class="car-device">车台ID</th>
                            <th class="car-dmodel">车台型号</th>
                            <th class="car-sim">SIM卡号</th>
                            <th class="car-transcard">配送卡ID</th>
                            <th class="car-store">仓数</th>
                            <th class="car-remark">备注</th>
                            <th class="car-action">操作</th>
                        </tr>
                    </thead>
                    <tbody class="table-body"></tbody>
                </table>
            </div>
            <div class="page">
                <span>第</span>
                <select id="page_id">
                    <option value="1">1</option>
                </select>
                <span id="page_info">页(几条数据)/共几页(共几条数据)</span>
                <select id="page_size">
                    <option value="25">25条/页</option>
                    <option value="50">50条/页</option>
                    <option value="100">100条/页</option>
                    <option value="150">150条/页</option>
                </select>
                <input type="hidden" id="cnum">
                <input type="hidden" id="ccom">
            </div>
        </div>
    </div>
</body>

</html>
</pop:Permission>