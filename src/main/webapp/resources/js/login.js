function login() {
    var account = $.trim($("#account").val());
    var password = $.trim($("#password").val());
    if (!isNull(account) && !isNull(password)) {
        $.post(
            "manage/session/login.do",
            encodeURI("account=" + account + "&password=" + password),
            function (data) {
                if (data.id == 0) {
                    // var requestUrl = $.trim($("#requestUrl").val());
                    // if (isNull(requestUrl) || requestUrl.indexOf("login.jsp") >= 0) {
                    //     requestUrl = "index.jsp";
                    // }
                    // location.replace(requestUrl);
                    location.replace("index.jsp");
                } else {
                    $(".login-errorMsg").html(data.msg);
                }
            },
            "json"
        );
    } else if (!isNull(account) && isNull(password)) {
        $(".login-errorMsg").html("密码不能为空!");
    } else if (isNull(account) && !isNull(password)) {
        $(".login-errorMsg").html("账号不能为空!");
    } else {
        $(".login-errorMsg").html("账号和密码不能为空!");
    }
}

$(document).ready(function () {
    if (window.top.location.href != location.href) {
        window.top.location.href = location.href;
    }

    $("#account").on("input", function () {
        $("#password").val("");
    });

    $("#login").click(function () {
        login();
    });

    $("#password").on("keydown", function (e) {
        var e = e || event;
        if (e.keyCode == 13) {
            login();
        }
    });

    $("#account").focus();
});