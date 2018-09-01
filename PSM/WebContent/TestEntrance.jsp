<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="favicon.ico" type="image/x-icon" />
<link rel="shortcut icon" href="favicon.ico" type="image/x-icon" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script src="Scripts/jQuery/jquery-1.9.0.min.js"></script>
<title>总承包项目安全管理系统考试页面</title>
<style>
body {
	background-image: url("Images/ims/header/testbg.jpg");
	font-family: 'PT Sans', Helvetica, Arial, sans-serif;
	text-align: center;
	color: #fff;
}

.page-container {
	margin: 7.5% auto 0 auto;
}

h1 {
	font-size: 75px;
	font-weight: 700;
	text-shadow: 0 1px 4px rgba(0, 0, 0, .2);
	margin-bottom: 50px;
}

form {
	position: relative;
	width: 305px;
	margin: 15px auto 0 auto;
	text-align: center;
}

input, select {
	width: 270px;
	height: 42px;
	margin-top: 25px;
	padding: 0 15px;
	background: #2d2d2d; /* browsers that don't support rgba */
	background: rgba(45, 45, 45, .15);
	-moz-border-radius: 6px;
	-webkit-border-radius: 6px;
	border-radius: 6px;
	border: 1px solid #3d3d3d; /* browsers that don't support rgba */
	border: 1px solid rgba(255, 255, 255, .15);
	-moz-box-shadow: 0 2px 3px 0 rgba(0, 0, 0, .1) inset;
	-webkit-box-shadow: 0 2px 3px 0 rgba(0, 0, 0, .1) inset;
	box-shadow: 0 2px 3px 0 rgba(0, 0, 0, .1) inset;
	font-family: 'PT Sans', Helvetica, Arial, sans-serif;
	font-size: 14px;
	color: #fff;
	text-shadow: 0 1px 2px rgba(0, 0, 0, .1);
	-o-transition: all .2s;
	-moz-transition: all .2s;
	-webkit-transition: all .2s;
	-ms-transition: all .2s;
}

form select {
	width: 300px;
}

form select option {
	font-size:20px;
	background: #ccc;
}

input:-moz-placeholder {
	color: #fff;
}

input:-ms-input-placeholder {
	color: #fff;
}

input::-webkit-input-placeholder {
	color: #fff;
}

input:focus {
	outline: none;
	-moz-box-shadow: 0 2px 3px 0 rgba(0, 0, 0, .1) inset, 0 2px 7px 0
		rgba(0, 0, 0, .2);
	-webkit-box-shadow: 0 2px 3px 0 rgba(0, 0, 0, .1) inset, 0 2px 7px 0
		rgba(0, 0, 0, .2);
	box-shadow: 0 2px 3px 0 rgba(0, 0, 0, .1) inset, 0 2px 7px 0
		rgba(0, 0, 0, .2);
}

.button {
	cursor: pointer;
	width: 300px;
	height: 44px;
	margin-top: 25px;
	margin-bottom: 25px;
	padding: 0;
	background: #ef4300;
	-moz-border-radius: 6px;
	-webkit-border-radius: 6px;
	border-radius: 6px;
	border: 1px solid #ff730e;
	-moz-box-shadow: 0 15px 30px 0 rgba(255, 255, 255, .25) inset, 0 2px 7px
		0 rgba(0, 0, 0, .2);
	-webkit-box-shadow: 0 15px 30px 0 rgba(255, 255, 255, .25) inset, 0 2px
		7px 0 rgba(0, 0, 0, .2);
	box-shadow: 0 15px 30px 0 rgba(255, 255, 255, .25) inset, 0 2px 7px 0
		rgba(0, 0, 0, .2);
	font-family: 'PT Sans', Helvetica, Arial, sans-serif;
	font-size: 14px;
	font-weight: 700;
	color: #fff;
	text-shadow: 0 1px 2px rgba(0, 0, 0, .1);
	-o-transition: all .2s;
	-moz-transition: all .2s;
	-webkit-transition: all .2s;
	-ms-transition: all .2s;
}

.button:hover {
	-moz-box-shadow: 0 15px 30px 0 rgba(255, 255, 255, .15) inset, 0 2px 7px
		0 rgba(0, 0, 0, .2);
	-webkit-box-shadow: 0 15px 30px 0 rgba(255, 255, 255, .15) inset, 0 2px
		7px 0 rgba(0, 0, 0, .2);
	box-shadow: 0 15px 30px 0 rgba(255, 255, 255, .15) inset, 0 2px 7px 0
		rgba(0, 0, 0, .2);
}

.button:active {
	-moz-box-shadow: 0 15px 30px 0 rgba(255, 255, 255, .15) inset, 0 2px 7px
		0 rgba(0, 0, 0, .2);
	-webkit-box-shadow: 0 15px 30px 0 rgba(255, 255, 255, .15) inset, 0 2px
		7px 0 rgba(0, 0, 0, .2);
	box-shadow: 0 5px 8px 0 rgba(0, 0, 0, .1) inset, 0 1px 4px 0
		rgba(0, 0, 0, .1);
	border: 0px solid #ef4300;
}

.formtips {
	margin-top: 20px;
	height: 40px;
	background: #2d2d2d; /* browsers that don't support rgba */
	background: rgba(45, 45, 45, .25);
	-moz-border-radius: 8px;
	-webkit-border-radius: 8px;
	border-radius: 8px;
}

.formtips span {
	display: inline-block;
	margin-left: 2px;
	font-size: 20px;
	color: red;
	font-weight: 700;
	line-height: 40px;
	text-shadow: 0 1px 2px rgba(0, 0, 0, .1);
}

.form-container {
	width: 400px;
	background: rgba(45, 45, 45, .15);
	margin: auto;
	padding: auto;
}
input:-webkit-autofill {background-color:rgb(196,196,196)!important;}
</style>
<script type="text/javascript">
$(function() {	
	$.ajax({
        type: "GET",
        url: "EduTrainAction!getProjectListDef?start=0&limit=1000",
        dataType: "json",
        async: false,
        success: function(data) {
        	var total = data.total;
        	for (var i = 0; i < total; i++)
        		$('select[name="projectID"]').append('<option value="'+data.rows[i].ID+'">'+data.rows[i].Name+'</option>');
        }
    });
	$('#projectSelect').change(function() {
		var projectID = $(this).val();
		var projectName = $(this).find('option[value='+projectID+']').text();
		$.ajax({
	        type: "GET",
	        url: "EduTrainAction!getDoubleTestrecordListDef?projectName=" + projectName,
	        dataType: "json",
	        async: false,
	        success: function(data) {
	        	$('select[name="testID"] option').each(function() {
	        		$(this).remove();
	        	})
	        	var total = data.total;
	        	for (var i = 0; i < total; i++)
	        		$('select[name="testID"]').append('<option value="'+data.rows[i].ID+'">'+data.rows[i].Name+'</option>');
	        }
	    });	
	})
	
	var projectID = <%=request.getParameter("projectID") %>;
	var testID = <%=request.getParameter("testID") %>;
	var name = '<%=request.getParameter("name") %>';
	var idno = '<%=request.getParameter("idno") %>';
	
	if (projectID && testID && name && idno) {
		console.log(projectID, testID, name, idno);
		$('select[name="testID"]').append('<option value="'+testID+'">'+testID+'</option>');
		$('#TestLogin #projectSelect').val(projectID);
		$('#TestLogin #testID').val(testID);
		$('#TestLogin #name').val(name);
		$('#TestLogin #idno').val(idno);
		$('#TestLogin').submit();
	}
})
</script>
<script type="text/javascript">
function testlogin_submit(){
	var idno = $('input[name="idno"]').val();
	var projectID = $('select[name="projectID"]').val();
	var testID = $('select[name="testID"]').val();
	var idnoreg = /^\d{15}$|^\d{17}[\d|X|x]$/;
	if (projectID == -1) {
		alert('请选择项目部！');
		return false;
	} else if (testID == '') {
		alert('请选择考试试卷！');
		return false;
	} else if ($('input[name="name"]').val() == '') {
		alert('请输入姓名！');
		return false;
	} else if (!idnoreg.test(idno)) {
		alert('请输入正确的身份证号！');
		return false;
	}

	console.log(projectID, testID, name, idno);
	var result;
	$.ajax({
        type: "GET",
        async: false,
        url: "EduTrainAction!testLoginBefore?testID="+testID+"&idno="+idno,
        dataType: "json",
        success: function(data) {
        	result = data.success;
        }
    });
	console.log(typeof(result));
	console.log(result);	
	if (!result) {
		alert("您已参加过考试！");
		return false;
	}
	return true;
}
</script>
</head>
<body>
<div class="page-container">
		<h1>安全管理考试系统</h1>
		<div class="form-container">
			<div class="signin">
				<form name="TestLogin" id="TestLogin" onsubmit="return testlogin_submit();" action="EduTrainAction!testLogin" method="post" autocomplete="off">
					<select id="projectSelect" name="projectID" class="role"></select>
					<select id="testID" name="testID" class="role"></select>
					<input id="name" type="text" name="name" class="username" placeholder="姓　名">
					<input id="idno" type="text" name="idno" class="username" placeholder="身份证">
					<input type="submit" class="button" value="登录">
				</form>
			</div>
		</div>
	</div>
</body>
</html>