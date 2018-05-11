<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="pop" uri="/pop-tags" %>

<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8">
    <title>操作日志列表</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=center-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="../../resources/css/base.css" media="all">
    <link rel="stylesheet" href="../../resources/css/normal2.css" media="all">
    <script src="../../resources/plugins/jquery-1.8.3.min.js"></script>
    <script src="../../resources/plugins/laydate/laydate.js"></script>
    <script src="../../resources/plugins/layer/layer.js"></script>
    <script src="../../resources/js/base.js"></script>
    <script src="../../resources/js/normal.js"></script>
    <script src="../../resources/js/log/logList.js"></script>
    <style type="text/css">
        #type_type,
        #type_begin,
        #type_end {
            left: 40px;
        }
        
        .log-id {
            width: 100px;
        }
        
        .log-account {
            width: 120px;
        }
        
        .log-name {
            width: 120px;
        }
        
        .log-type {
            width: 160px;
        }
        
        .log-description {
            width: 500px;
            text-align: left;
        }
        
        .log-time {
            width: 200px;
        }
    </style>
</head>

<body>
    <div class="container">
    	<div class="nav-addr">
	        <img src="../../resources/images/navbar/log.png"> 操作日志管理
	        <img src="../../resources/images/navbar/nav_right_12.png"> 操作日志查看
	    </div>
        <div class="search-zone">
            <select class="search-type" id="type_user">
                <option value="1">账号</option>
                <option value="2">姓名</option>
            </select>
            <input type="text" class="search-text" id="text_user">
            <label for="text_type" class="search-type" id="type_type">日志标志</label>
            <input type="text" class="search-text" id="text_type">
            <label for="text_begin" class="search-type" id="type_begin">开始时间</label>
            <input type="text" class="search-text" id="text_begin">
            <label for="text_end" class="search-type" id="type_end">结束时间</label>
            <input type="text" class="search-text" id="text_end">

            <input type="button" class="search-btn" id="search_btn" value="查询">
        </div>
        <div class="data-zone">
            <div class="table-head">
                <table width="100%">
                    <thead>
                        <tr>
                            <th class="log-id">序号</th>
                            <th class="log-account">操作员账号</th>
                            <th class="log-name">操作员姓名</th>
                            <th class="log-type">日志标志</th>
                            <th class="log-description">操作内容</th>
                            <th class="log-time">操作时间</th>
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
                <input type="hidden" id="olaccount">
                <input type="hidden" id="olname">
                <input type="hidden" id="oltype">
                <input type="hidden" id="olbegin">
                <input type="hidden" id="olend">
            </div>
        </div>
    </div>
</body>

</html>