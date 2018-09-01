<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="favicon.ico" type="image/x-icon" />
<link rel="shortcut icon" href="favicon.ico" type="image/x-icon" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="Lib/uikit.gradient.min.css">
<script src="Scripts/jQuery/jquery-1.9.0.min.js"></script>
<script src="Lib/uikit.min.js"></script>
<script src="Scripts/Test.js"></script>
<title>总承包项目安全管理系统考试页面</title>
<style>
body { 
	text-algin: center; 
	background-image: url(/PSM/Images/ims/header/backImage.jpg);
    background-position: auto;
    background-repeat: repeat;
    background-size: auto;
    background-attachment: scroll;
}
li { list-style-type:none; }
li ul.uk-list li label { display:inline-block; width: 90%; cursor: pointer; padding: 8px 0;}
li ul.uk-list li {
	margin-top: 0!important;
	/*padding: 8px 0;*/
}
li ul.uk-list li:hover { background: rgba(52, 152, 219, 1) }
.formPanel { margin: 2% 10%; }
h3 { margin-bottom: 0; }
.question { margin-top: 5px; }

.main-grid { margin: auto; }
.question-level label { font-size:16px; }
.uk-margin-large-bottom { padding-bottom: 100px!important; }
.button-center {
	width: 20%;
	margin: 30px  20%;
}
h2 { font-size: 30px; padding-top: 10px; }

.subtitle { width: 100%; text-align:center;}
.subtitle span {
	font-size: 16px;
	padding: 10px 30px;
}
.paper-grid { background: rgba(245,245,245,0.8); }

.uk-margin-large-bottom { margin-bottom: 0px!important; }
</style>
<script>
function getUrlParam(name) {
	var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)"); 
	var r = window.location.search.substr(1).match(reg);  
	if (r != null) return unescape(r[2]);
	return null; //返回参数值
}

function getTitle(testID) {
	$.ajax({
        type: "GET",
        url: "EduTrainAction!getTestTitle",
        data: {	testID: testID },
        dataType: "json",
        success: function(data) {
        	$('h1.title').html(data.title + '考试试卷');
        }
    });
}
function getName() {
	var name = '<%=request.getParameter("name") %>';
	if (name != 'null')
		$('span.name').html('考生：' + name);
}

var testID;
total = [];

$(function(){
	testID = getUrlParam('testID');
	if (testID == null) testID = '<%=request.getParameter("testID") %>';
	else {
		$('button').remove();
		$('.subtitle').remove();
	}
	getTitle(testID);
	getName();
	
	
	$.ajax({
        type: "GET",
        url: "EduTrainAction!getTest",
        data: {	type: "单选题", testID: testID },
        dataType: "json",
        success: function(data) {
        	total[1] = data.total;
        	if (data.total == 0) {
        		$('#header1').remove();
        		return;
        	}
        	for (var i = 1; i <= data.total; i++)
        		appendQuestion1(i, data.data[i - 1].Question, data.data[i - 1].OptionA, 
        				data.data[i - 1].OptionB, data.data[i - 1].OptionC, data.data[i - 1].OptionD)
        }
    });
	$.ajax({
        type: "GET",
        url: "EduTrainAction!getTest",
        data: { type: "多选题", testID: testID },
        dataType: "json",
        success: function(data){
        	total[2] = data.total;
        	if (data.total == 0) {
        		$('#header2').remove();
        		return;
        	}
        	for (var i = 1; i <= data.total; i++)
        		appendQuestion2(i, data.data[i - 1].Question, data.data[i - 1].OptionA, 
        				data.data[i - 1].OptionB, data.data[i - 1].OptionC, data.data[i - 1].OptionD, data.data[i - 1].OptionE)
        }
    });
	$.ajax({
        type: "GET",
        url: "EduTrainAction!getTest",
        data: { type: "判断题", testID: testID },
        dataType: "json",
        success: function(data){
        	total[3] = data.total;
        	if (data.total == 0) {
        		$('#header3').remove();
        		return;
        	}
        	for (var i = 1; i <= data.total; i++) 
        		appendQuestion3(i, data.data[i - 1].Question);
        }
    });
	$.ajax({
        type: "GET",
        url: "EduTrainAction!getTest",
        data: { type: "简答题", testID: testID },
        dataType: "json",
        success: function(data){
        	total[4] = data.total;
        	if (data.total == 0) {
        		$('#header4').remove();
        		return;
        	}
        	for (var i = 1; i <= data.total; i++) 
        		appendQuestion4(i, data.data[i - 1].Question);
        }
    });
})

function submitPaper() {
	answerList = [];
	for (var i = 1; i <= 4; i++)
		answerList[i] = "";
	var target = 0;
	// 单选题
	for (var j = 1; j <= total[1]; j++) {
		var t = $('input:radio[name="question1_'+j+'"]:checked').val();
		if (typeof(t) == 'undefined' || t == '') {
			if (target == 0)
				target = $('#question1_'+j).offset().top;//获取位置
			t = ' ';
		}
		if (j > 1) answerList[1] += ",";
		answerList[1] += t;
	}
	
	// 多选题
	for (var j = 1; j <= total[2]; j++) {
		var t = '';
		$('input:checkbox[name="question2_'+j+'"]:checked').each(function() {
			t += $(this).val();
		});
		if (typeof(t) == 'undefined' || t == '') {
			if (target == 0)
				target = $('#question1_'+j).offset().top;//获取位置
			t = ' ';
		}
		if (j > 1) answerList[2] += ",";
		answerList[2] += t;
	}
	
	// 判断题
	for (var j = 1; j <= total[3]; j++) {
		var t = $('input:radio[name="question3_'+j+'"]:checked').val();
		if (typeof(t) == 'undefined' || t == '') {
			if (target == 0)
				target = $('#question1_'+j).offset().top;//获取位置
			t = ' ';
		}
		if (j > 1) answerList[3] += ",";
		answerList[3] += t;
	}
	
	// 简答题
	for (var j = 1; j <= total[4]; j++) {
		var t = $('textarea[name="question4_'+j+'"]').val();
		if (typeof(t) == 'undefined' || t == '') {
			if (target == 0)
				target = $('#question1_'+j).offset().top;//获取位置
			t = ' ';
		}
		if (j > 1) answerList[4] += "*--*";
		answerList[4] += t;
	}
	
	if (target != 0 && !confirm('您尚未回答完整，确定提交吗？')) {
		$("html,body").animate({scrollTop: target},300);//跳转
		return false;
	}
	$.ajax({
		type: "POST",
		url: "EduTrainAction!submitPaper",
		data: {
			testID: "<%=request.getParameter("testID") %>",
			projectID: "<%=request.getParameter("projectID") %>",
			name: "<%=request.getParameter("name") %>",
			idno: "<%=request.getParameter("idno") %>",
			answerList1: answerList[1],
			answerList2: answerList[2],
			answerList3: answerList[3],
			answerList4: answerList[4]
		},
		dataType: "json",
		success: function(data){
			if (data.result == 'failed') {
				alert("提交失败");				
			} else if (data.result == 'success'){
				alert('您本次考试得分为' + data.score);
				window.location.href = "TestEntrance";
			}
		}
	});
	return true;
}
</script>
</head>
<body>
<h1 class="title" align="center" style="padding-top:15px;"></h1>
<div class="subtitle">
	<span id="clock"></span>
	<span class="name"></span>
</div>
<script type="text/javascript">
var h=0;
var m=0;
var s=0;
function clock(el) {
	if (++s >= 60) {
		s = 0;
		if (++m >= 60) {
			m = 0;
			h++;
		}
	}
    ss=s>=10?s:('0'+s);
	mm=m>=10?m:('0'+m);
    hh=h>=10?h:('0'+h);
    el.innerHTML = hh+":"+mm+":"+ss;
    setTimeout(function(){clock(el)}, 1000);
}
clock(document.getElementById('clock'));
</script>
<hr>

<div class="uk-container uk-container-center uk-margin-large-top uk-margin-large-bottom">
	<div class="uk-grid">
		<div class="main-grid uk-width-medium-3-4">
			<div class="paper-grid">
				<h2 id="header1">单选题</h2>
				<div class="Question1 question-level uk-form-stacked">
					<ul class="uk-list uk-list-space uk-list-striped">
						
					</ul>
				</div>
				
				<h2 id="header2">多选题</h2>
				<div class="Question2 question-level uk-form-stacked">
					<ul class="uk-list uk-list-space uk-list-striped">
						
					</ul>
				</div>
				
				<h2 id="header3">判断题</h2>
				<div class="Question3 question-level uk-form-stacked">
					<ul class="uk-list uk-list-space uk-list-striped">
						
					</ul>
				</div>
				
				<h2 id="header4">简答题</h2>
				<div class="Question4 question-level uk-form-stacked">
					<ul class="uk-list uk-list-space uk-list-striped">
						
					</ul>
				</div>
				<div style="text-align: center;">
					<button class="uk-button uk-button-primary uk-button-large button-center" onclick="submitPaper()">提交</button>
				</div>;
				
			</div>        		
		</div>
	</div>
</div>
</body>
</html>