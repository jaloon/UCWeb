$(function() {
    // 必填字段添加星号标识
	$('input[required]').each(function(i, e) {
    	$(e).closest('tr').children().first().prepend('<span style="color:red">* </span>');
    });
	
    var index = parent.layer.getFrameIndex(window.name); //获取窗口索引

    var success_zh_text = "修改成功！";
    var error_zh_text = "修改失败！";
    var mode = $("#mode").val();

    if ("edit" == mode) {
        $("#carno").click(function() {
            layer.msg('请前往车辆信息管理修改', { icon: 6, time: 1500 });
        });
    }

    $("#cancel").click(function() {
        parent.layer.close(index);
    });

    $("#confirm").click(function() {
        var id = $("#id").val();
        var cid = $.trim($("#cid").val());
        var remark = $.trim($("#remark").val());
        var url = "../../manage/transcard/update.do";
        var param = "id=" + id + "&transportCardId=" + cid + "&remark=" + remark;

        if ("add" == mode) {
            url = "../../manage/transcard/add.do";
            param = "transportCardId=" + cid + "&remark=" + remark;
            success_zh_text = "添加成功！";
            error_zh_text = "添加失败！"
        }

        if (isNull(cid)) {
            layer.alert('配送卡ID不能为空！', { icon: 2 }, function(index2) {
                layer.close(index2);
                $("#cid").select();
            });
            return;
        }

        $.post(url, encodeURI(param),
            function(data) {
                if ("error" == data.msg) {
                    layer.msg(error_zh_text, { icon: 2, time: 500 });
                } else {
                    layer.msg(success_zh_text, { icon: 1, time: 500 }, function() {
                        parent.layer.close(index);
                    });
                }
            },
            "json"
        );

    });
});