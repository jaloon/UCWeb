<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="pop" uri="/pop-tags" %>

<pop:Permission ename="viewDevice">
<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8">
    <title>设备列表</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="../../resources/css/base.css" media="all">
    <link rel="stylesheet" href="../../resources/css/normal.css" media="all">
    <style type="text/css">
        .device-id {
            width: 120px;
        }
        
        .device-type {
            width: 120px;
        }
        
        .device-model {
            width: 200px;
        }
        
        .device-produce {
            width: 200px;
        }
        
        .device-delivery {
            width: 200px;
        }
        
        .device-remark {
            width: 100px;
        }
    </style>
    <script src="../../resources/plugins/jquery-1.8.3.min.js"></script>
    <script src="../../resources/plugins/layer/layer.js"></script>
    <script src="../../resources/js/base.js"></script>
    <script src="../../resources/js/normal.js"></script>
    <script src="../../resources/js/device/deviceList.js"></script>
</head>

<body>
    <div class="container">
    	<div class="nav-addr">
	        <img src="../../resources/images/navbar/basicinfo.png"> 基本信息管理
	        <img src="../../resources/images/navbar/nav_right_12.png"> 设备信息同步
	    </div>
        <div class="search-zone">
            <label for="search_text" class="search-type" id="search_type">设备类型</label>
            <select class="search-text" id="search_text">
                <option value=1>车载终端</option>
                <option value=2>锁</option>
                <option value=3>出入库读卡器</option>
                <option value=4>手持机</option>
            </select>
            <input type="button" class="search-btn" id="search_btn" value="查询">
            <pop:Permission ename="syncDevice">
            	<input type="button" id="add" value="同步">
            	<script type="text/javascript">
	            	$("#add").click(function() {
	            		layer.confirm('是否确认同步设备信息？', {
	        	            icon: 0,
	        	            title: ['同步设备', 'font-size:14px;color:#ffffff;background:#478de4;']
	        	        }, function() {
	        	        	var index = layer.load(); 
		                    $.post("../../manage/device/sync.do",
		                        function(data) {
		                    		layer.close(index); 
		                            if (data.msg == "success") {
		                                layer.msg('同步成功！', { icon: 1, time: 1000 });
		                                showList("", 1);
		                            } else {
		                                layer.msg('同步失败！', { icon: 5, time: 1000 });
		                            }
		                        },
		                        "json"
		                    );
	        	        });
	                });
            	</script>
            </pop:Permission>
        </div>
        <div class="data-zone">
            <div class="table-head">
                <table width="100%">
                    <thead>
                        <tr>
                            <th class="device-id">设备ID</th>
                            <th class="device-type">设备类型</th>
                            <th class="device-model">设备型号</th>
                            <th class="device-produce">出厂时间</th>
                            <th class="device-delivery">交付时间</th>
                            <th class="device-remark">备注</th>
                        </tr>
                    </thead>
                </table>
            </div>
            <div class="table-body">
                <table width="100%">
                </table>
            </div>
            <div class="page">
                <span>第</span>
                <select id="page_id">
                    <option value="1">1</option>
                </select>
                <span id="page_info">页(几条数据)/共几页(共几条数据)</span>
                <select id="page_size">
                    <option value="10">10条/页</option>
                    <option value="20">20条/页</option>
                    <option value="25">25条/页</option>
                    <option value="30">30条/页</option>
                    <option value="50">50条/页</option>
                </select>
                <input type="hidden" id="dtype">
            </div>
        </div>
    </div>
</body>

</html>
</pop:Permission>