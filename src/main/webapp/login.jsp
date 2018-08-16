<%@ page import="java.util.Calendar" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="pop" uri="/pop-tags" %>

<!DOCTYPE html>
<html lang="en">

<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta http-equiv="X-UA-Compatible" content="ie=edge">
	<title>登录-普利通电子签封安全监管系统</title>
	<link rel="shortcut icon" href="favicon.ico" />
	<link rel="stylesheet" type="text/css" href="resources/css/base.css">
	<link rel="stylesheet" type="text/css" href="resources/css/login.css" />
	<script src="resources/plugins/jquery-1.8.3.min.js"></script>
    <script src="resources/plugins/layer/layer.js"></script>
	<script src="resources/js/base.js"></script>
	<script src="resources/js/login.js"></script>
</head>

<body>
	<div class="container">
		<input type="hidden" id="requestUrl" value="${param.requestUrl}">
		<div class="top">
			<div class="login-logo"><img src="resources/images/login/login-logo.png" /></div>
			<div class="login-errorMsg">${param.errorMessage}</div>
			<div class="login-form">
				<div class="welcome">
					<div class="welcome-zh">欢迎登录</div>
					<div class="welcome-en">WELCOME LOGIN</div>
				</div>
				<div class="account-form">
					<img class="form-logo" src="resources/images/login/login-account.png" />
					<input class="form-input" type="text" id="account" name="account" placeholder="请输入账号" />
				</div>
				<div class="password-form">
					<img class="form-logo" src="resources/images/login/login-password.png" />
					<input class="form-input" type="password" id="password" name="password" placeholder="请输入密码" />
				</div>

				<input type="button" id="login" value="登&emsp;&emsp;录" />
			</div>
		</div>
		<div class="bottom">
			Copyright&copy;2016-<%=Calendar.getInstance().get(Calendar.YEAR) %>
			<a title="下载证书" href="manage/file/download/pltone.crt" style="color:#FFFFFF; text-decoration: none;">
                普利通信息科技有限公司
            </a>
		</div>
	</div>
</body>

</html>