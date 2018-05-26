<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="pop" uri="/pop-tags" %>
<pop:Permission ename="viewGasstation">
<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8">
    <title>加油站列表</title>
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
    <script src="../../resources/js/gasstation/gasstationList.js"></script>
    <style type="text/css">
        .gasstation-id {
            width: 100px;
        }
        
        .gasstation-name {
            width: 200px;
        }
        
        .gasstation-abbr {
            width: 160px;
        }
        
        .gasstation-coordinate {
            width: 200px;
        }
        
        .gasstation-director {
            width: 100px;
        }
        
        .gasstation-phone {
            width: 160px;
        }
        
        .gasstation-address {
            width: 300px;
        }
        
        .gasstation-company {
            width: 200px;
        }
        
        .gasstation-remark {
            width: 100px;
        }
        
        .gasstation-action {
            width: 200px;
        }
        
        .file-box {
	        position:absolute;
		    left: 600px;
		    top: 31px;
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
   		<pop:Permission ename="editGasstation">
	    deleteGasstation = function(id) {
	        layer.confirm('删除后不可撤销，是否确认删除？', {
	            icon: 0,
	            title: ['删除', 'font-size:14px;color:#ffffff;background:#478de4;']
	        }, function() {
	            $.post("../../manage/gasstation/delete.do", encodeURI("id=" + id),
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
	    };
	    </pop:Permission>
	    function showList(gasstationName, pageId) {
	        var rows = $("#page_size").val();
	        var startRow = (pageId - 1) * rows;
	
	        $.post(
	            "../../manage/gasstation/ajaxFindForPage.do",
	            encodeURI("name=" + gasstationName + "&pageId=" + pageId + "&startRow=" + startRow + "&rows=" + rows),
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
	                $("#gsname").val(gridPage.t.name);
	                $(".table-body").html("");
	                var gasstations = gridPage.dataList;
	                var tableData = "<table width='100%'>";
	                for (var i = 0; i < gridPage.currentRows; i++) {
	                    var gasstation = gasstations[i];
	                    tableData += "<tr onclick=\"dispatch('edit'," + gasstation.id + ")\">" +
	                        "<td class=\"gasstation-id\">" + gasstation.officialId + "</td>" +
	                        "<td class=\"gasstation-name\">" + gasstation.name + "</td>" +
	                        "<td class=\"gasstation-abbr\">" + gasstation.abbr + "</td>" +
	                        "<td class=\"gasstation-coordinate\">(" + gasstation.longitude + ", " + gasstation.latitude +  ")</td>" +
	                        "<td class=\"gasstation-director\">" + gasstation.director + "</td>" +
	                        "<td class=\"gasstation-phone\">" + gasstation.phone + "</td>" +
	                        "<td class=\"gasstation-address\">" + gasstation.address + "</td>" +
	                        "<td class=\"gasstation-company\">" + gasstation.company + "</td>" +
	                        "<td class=\"gasstation-remark\">" + gasstation.remark + "</td>" +
	                        "<td class=\"gasstation-action\">" +
	                        "<img src=\"../../resources/images/operate/view.png\" alt=\"查看\" title=\"查看\" onclick=\"dispatch('view'," + gasstation.id + ")\">" +
	                        <pop:Permission ename="editGasstation">
	                        "&emsp;<img src=\"../../resources/images/operate/edit.png\" alt=\"编辑\" title=\"编辑\" onclick=\"dispatch('edit'," + gasstation.id + ")\">&emsp;" +
	                        "<img src=\"../../resources/images/operate/delete.png\" alt=\"删除\" title=\"删除\" onclick=\"deleteGasstation(" + gasstation.id + ")\">" +
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
	        <img src="../../resources/images/navbar/basicinfo.png"> 基本信息管理
	        <img src="../../resources/images/navbar/nav_right_12.png"> 加油站管理
	    </div>
        <div class="search-zone">
            <label for="search_text" class="search-type" id="search_type">加油站名称</label>
            <input type="text" class="search-text" id="search_text">
            <input type="button" class="search-btn button" id="search_btn" value="查询">
            <pop:Permission ename="editGasstation">
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
				formData.append("biz", 2); // 1 油库，2 加油站，3车辆
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
	                        layer.msg('批量导入加油站成功！', { icon: 1, time: 500 }, function() {
	                            refreshPage();
	                        });
	                    } else {
	                        layer.msg('批量导入加油站失败！', { icon: 2, time: 500 });
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
            <div class='table-cont' id='table-cont'>
                <table width="100%">
                    <thead class="table-head">
                        <tr>
                            <th class="gasstation-id">加油站编号</th>
                            <th class="gasstation-name">加油站名称</th>
                            <th class="gasstation-abbr">加油站简称</th>
                            <th class="gasstation-coordinate">加油站经纬度</th>
                            <th class="gasstation-director">负责人</th>
                            <th class="gasstation-phone">联系电话</th>
                            <th class="gasstation-address">联系地址</th>
                            <th class="gasstation-company">所属公司</th>
                            <th class="gasstation-remark">备注</th>
                            <th class="gasstation-action">操作</th>
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
                <input type="hidden" id="gsname">
            </div>
        </div>
    </div>
    
</body>

</html>
</pop:Permission>