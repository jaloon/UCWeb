<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="pop" uri="/pop-tags" %>
<pop:Permission ename="viewOildepot">
<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8">
    <title>油库列表</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=center-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="../../resources/css/base.css" media="all">
    <link rel="stylesheet" href="../../resources/css/normal.css" media="all">
    <link rel="stylesheet" href="../../resources/plugins/upload/upload.css" media="all">
    <script src="../../resources/plugins/jquery-1.8.3.min.js"></script>
    <script src="../../resources/plugins/upload/upload.js"></script>
    <script src="../../resources/plugins/layer/layer.js"></script>
    <script src="../../resources/js/base.js"></script>
    <script src="../../resources/js/normal.js"></script>
    <script src="../../resources/js/oildepot/oildepotList.js"></script>
    <style type="text/css">
        /*.oildepot-id {*/
            /*width: 100px;*/
        /*}*/

        /*.oildepot-no {*/
            /*width: 100px;*/
        /*}*/

        /*.oildepot-name {*/
            /*width: 200px;*/
        /*}*/
        
        /*.oildepot-abbr {*/
            /*width: 160px;*/
        /*}*/
        
        /*.oildepot-coordinate {*/
            /*width: 200px;*/
        /*}*/
        
        /*.oildepot-director {*/
            /*width: 100px;*/
        /*}*/
        
        /*.oildepot-phone {*/
            /*width: 160px;*/
        /*}*/
        
        /*.oildepot-address {*/
            /*width: 300px;*/
        /*}*/
        
        /*.oildepot-company {*/
            /*width: 200px;*/
        /*}*/
        
        /*.oildepot-remark {*/
            /*width: 100px;*/
        /*}*/
        
        /*.oildepot-action {*/
            /*width: 200px;*/
        /*}*/
        
        .file-box {
            position: relative;
            float: right;
            top: 10px;
            margin-right: 30px;
        }
        
        .inputfile-1+label {
		   	color: #ffffff;
		    background-color: #478de4;
		    border-radius: 3px;
		    padding: 7.5px 20px;
		    font-size: 16px;
		    font-weight: normal;
		}
		
		.inputfile-1:focus+label,
		.inputfile-1.has-focus+label,
		.inputfile-1+label:hover {
		    background-color: #5ca1f7;
		}
    </style>
    <script type="text/javascript">
    <pop:Permission ename="editOildepot">
    deleteOildepot = function(id) {
        layer.confirm('删除后不可撤销，是否确认删除？', {
            icon: 0,
            title: ['删除', 'font-size:14px;color:#ffffff;background:#478de4;']
        }, function() {
            $.post("../../manage/oildepot/delete.do", encodeURI("id=" + id),
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
                if (XMLHttpRequest.readyState == 4) {
                    var http_status = XMLHttpRequest.status;
                    if (http_status == 0 || http_status > 600) {
                        location.reload(true);
                    } else if (http_status == 200) {
                        if (textStatus == "parsererror") {
                            layer.alert("应答数据格式解析错误！")
                        } else {
                            layer.alert("http response error: " + textStatus)
                        }
                    } else {
                        layer.alert("http connection error: status[" + http_status + "], " + XMLHttpRequest.statusText)
                    }
                }
            });
        });
        // 阻止事件冒泡到DOM树上
        event.stopPropagation();
    };
    </pop:Permission>
    function showList(oildepotName, pageId) {
        var rows = $("#page_size").val();
        var startRow = (pageId - 1) * rows;
        var loadLayer = layer.load();
        $.post(
            "../../manage/oildepot/ajaxFindForPage.do",
            encodeURI("name=" + oildepotName + "&pageId=" + pageId + "&startRow=" + startRow + "&rows=" + rows),
            function(gridPage) {
                layer.close(loadLayer);
                var pageCount = gridPage.total;
                if (pageCount > 1) {
                    var pageOpts = "";
                    for (var i = 1; i <= pageCount; i++) {
                        pageOpts += "<option value=" + i + ">" + i + "</option>";
                    }
                    $("#page_id").html(pageOpts);
                    $("#page_id").val(gridPage.page);
                } else {
                    $("#page_id").html("<option value='1'>1</option>");
                }
                $("#page_info").html("页(" + gridPage.currentRows + "条数据)/共" + pageCount + "页(共" + gridPage.records + "条数据)");
                $("#odname").val(gridPage.t.name);
                $(".table-body").html("");
                var oildepots = gridPage.dataList;
                var tableData = "<table width='100%'>";
                for (var i = 0; i < gridPage.currentRows; i++) {
                    var oildepot = oildepots[i];
                    tableData += "<tr class='list-content' onclick=\"dispatch('edit'," + oildepot.id + ")\">" +
                        "<td class=\"oildepot-id\">" + oildepot.id + "</td>" +
                        "<td class=\"oildepot-no\">" + oildepot.officialId + "</td>" +
                        "<td class=\"oildepot-name\">" + oildepot.name + "</td>" +
                        "<td class=\"oildepot-abbr\">" + oildepot.abbr + "</td>" +
                        "<td class=\"oildepot-coordinate\">(" + oildepot.longitude + ", " + oildepot.latitude + ")</td>" +
                        "<td class=\"oildepot-director\">" + oildepot.director + "</td>" +
                        "<td class=\"oildepot-phone\">" + oildepot.phone + "</td>" +
                        "<td class=\"oildepot-address\">" + oildepot.address + "</td>" +
                        "<td class=\"oildepot-company\">" + oildepot.company + "</td>" +
                        "<td class=\"oildepot-remark\">" + oildepot.remark + "</td>" +
                        "<td class=\"oildepot-action\">" +
                        "<img class='edit-btn' src=\"../../resources/images/operate/view.png\" alt=\"查看\" title=\"查看\" onclick=\"dispatch('view'," + oildepot.id + ")\">" +
                        <pop:Permission ename="editOildepot">
                        "&emsp;<img class='edit-btn' src=\"../../resources/images/operate/edit.png\" alt=\"编辑\" title=\"编辑\" onclick=\"dispatch('edit'," + oildepot.id + ")\">&emsp;" +
                        "<img class='edit-btn' src=\"../../resources/images/operate/delete.png\" alt=\"删除\" title=\"删除\" onclick=\"deleteOildepot(" + oildepot.id + ")\">" +
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
            layer.close(loadLayer);
            if (XMLHttpRequest.readyState == 4) {
                var http_status = XMLHttpRequest.status;
                if (http_status == 0 || http_status > 600) {
                    location.reload(true);
                } else if (http_status == 200) {
                    if (textStatus == "parsererror") {
                        layer.alert("应答数据格式解析错误！")
                    } else {
                        layer.alert("http response error: " + textStatus)
                    }
                } else {
                    layer.alert("http connection error: status[" + http_status + "], " + XMLHttpRequest.statusText)
                }
            }
        });
    }
    </script>
</head>

<body>
    <div class="container">
    	<div class="nav-addr">
	        <img src="../../resources/images/navbar/basicinfo.png"> 基本信息管理
	        <img src="../../resources/images/navbar/nav_right_12.png"> 油库管理
	    </div>
        <div class="search-zone">
            <label for="search_text" class="search-type" id="search_type">油库名称</label>
            <input type="text" class="search-text" id="search_text">
            <input type="button" class="search-btn button" id="search_btn" value="查询">
            <pop:Permission ename="editOildepot">
            <span class="file-box">
		        <input type="file" name="file-1[]" id="file-1" class="inputfile inputfile-1" data-multiple-caption="{count} files selected" multiple />
		        <label for="file-1">
		        	<svg xmlns="http://www.w3.org/2000/svg" width="20" height="17" viewBox="0 0 20 17">
		        		<path d="M10 0l-5.2 4.9h3.3v5.1h3.8v-5.1h3.3l-5.2-4.9zm9.3 11.5l-3.2-2.1h-2l3.4 2.6h-3.5c-.1 0-.2.1-.2.1l-.8 2.3h-6l-.8-2.2c-.1-.1-.1-.2-.2-.2h-3.6l3.4-2.6h-2l-3.2 2.1c-.4.3-.7 1-.6 1.5l.6 3.1c.1.5.7.9 1.2.9h16.3c.6 0 1.1-.4 1.3-.9l.6-3.1c.1-.5-.2-1.2-.7-1.5z"/>
	        		</svg>
        			<span>批量导入&hellip;</span>
       			</label>
		    </span>
		    <script type="text/javascript">
		    function batchImport(fileInput, fileName, labelVal) {
				var fileObj = fileInput.files[0]; // js 获取文件对象
				if (typeof(fileObj) == "undefined" || fileObj.size <= 0) {
				    layer.alert('请选择要导入的油库Excel文件！', { icon: 2 }, function(index) {
				    	layer.close(index);
				    	//$(fileInput).click();
				    	$(fileInput).trigger("click");
				    });
		            return;
				}
				if (!fileName.endsWithIgnoreCase(".xls") && !fileName.endsWithIgnoreCase(".xlsx")) {
					layer.alert('文件类型不匹配，只能导入Excel文件！', { icon: 2 }, function(index) {
						layer.close(index);
						$(fileInput).next().html(labelVal);
					});
		            return;
				}
				var index = layer.load(); 
				var formData = new FormData();
				formData.append("uploadFile", fileObj); //加入文件对象
				formData.append("biz", 1); // 1 油库，2 加油站，3车辆
		        $.ajax({
		            url: "../../manage/file/upload",
		            data: formData,
		            type: "post",
		            dataType: "json",
		            cache: false, //上传文件无需缓存
		            processData: false, //用于对data参数进行序列化处理 这里必须false
		            contentType: false, //必须
		            success: function(result) {
		            	layer.close(index);
		            	$(fileInput).next().html(labelVal);
		            	if ("success" == result.msg) {
		                    layer.msg('批量导入油库成功！', { icon: 1, time: 500 }, function() {
		                        refreshPage();
		                    });
		                } else {
		                    layer.msg('批量导入油库失败！', { icon: 2, time: 500 });
		                }
		            },
                    error: function(XMLHttpRequest, textStatus, errorThrown) {  //#3这个error函数调试时非常有用，如果解析不正确，将会弹出错误框
                        layer.close(index);
                        if (XMLHttpRequest.readyState == 4) {
                            var http_status = XMLHttpRequest.status;
                            if (http_status == 0 || http_status > 600) {
                                location.reload(true);
                            } else if (http_status == 200) {
                                if (textStatus == "parsererror") {
                                    layer.alert("应答数据格式解析错误！")
                                } else {
                                    layer.alert("http response error: " + textStatus)
                                }
                            } else {
                                layer.alert("http connection error: status[" + http_status + "], " + XMLHttpRequest.statusText)
                            }
                        }
                    }
		        });
		    }
		    customUpload.config({
		    	upload: true,
		        fnBack: batchImport
		    }).listen();
		    </script>
            <input type="button" class="button" id="add" value="添加" onclick="dispatch('add',0)">
            </pop:Permission>
        </div>
        <div class="data-zone">
            <div class='table-box'>
                <table class="table-cont" width="100%">
                    <thead class="table-head">
                        <tr>
                            <th class="oildepot-id">油库ID</th>
                            <th class="oildepot-no">油库编号</th>
                            <th class="oildepot-name">油库名称</th>
                            <th class="oildepot-abbr">油库简称</th>
                            <th class="oildepot-coordinate">油库经纬度</th>
                            <th class="oildepot-director">负责人</th>
                            <th class="oildepot-phone">联系电话</th>
                            <th class="oildepot-address">联系地址</th>
                            <th class="oildepot-company">所属公司</th>
                            <th class="oildepot-remark">备注</th>
                            <th class="oildepot-action">操作</th>
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
                <input type="hidden" id="odname">
            </div>
        </div>
    </div>
</body>

</html>
</pop:Permission>