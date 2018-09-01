<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
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
	background-image: url(/PSM/Images/ims/header/timg.jpeg);
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
.uk-margin-large-bottom { margin-bottom: 200px!important; }
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
.paper-grid {
	background: rgba(245,245,245,0.8);
}
</style>
<script>
testpaperID = '<%=request.getParameter("testpaperID") %>';

function getTitle(testpaperID) {
	$.ajax({
        type: "GET",
        url: "EduTrainAction!getTestTitle",
        data: {	testpaperID: testpaperID },
        dataType: "json",
        success: function(data) {
        	$('h1.title').html(data.title + '考试试卷');
        }
    });
}

function getName() {
	var name = '<%=request.getParameter("name") %>';
	if (name != 'null')
		$('h6.name').html('考生：' + name);
}

$(function(){
	getTitle(testpaperID);
	getName();
	$.ajax({
        type: "GET",
        url: "EduTrainAction!getReviewPaper",
        data: {	testpaperID: testpaperID, type: "单选题" },
        dataType: "json",
        success: function(data){
        	if (data.total == 0) {
        		$('#header1').remove();
        		return;
        	}
        	for (var i = 1; i <= data.total; i++) {
        		appendQuestion1(i, data.data[i - 1].Question, data.data[i - 1].OptionA, 
        				data.data[i - 1].OptionB, data.data[i - 1].OptionC, data.data[i - 1].OptionD);
        		appendAnswer(i, '.paper-grid .Question1>ul>li', data.data[i - 1].MyAnswer, data.data[i - 1].StdAnswer);
        	}
        }
    });
	$.ajax({
        type: "GET",
        url: "EduTrainAction!getReviewPaper",
        data: { testpaperID: testpaperID, type: "多选题" },
        dataType: "json",
        success: function(data){
        	if (data.total == 0) {
        		$('#header2').remove();
        		return;
        	}
        	for (var i = 1; i <= data.total; i++) {
        		appendQuestion2(i, data.data[i - 1].Question, data.data[i - 1].OptionA, 
        				data.data[i - 1].OptionB, data.data[i - 1].OptionC, data.data[i - 1].OptionD);
        		appendAnswer(i, '.paper-grid .Question2>ul>li', data.data[i - 1].MyAnswer, data.data[i - 1].StdAnswer);
        	}
        }
    });
	$.ajax({
        type: "GET",
        url: "EduTrainAction!getReviewPaper",
        data: { testpaperID: testpaperID, type: "判断题" },
        dataType: "json",
        success: function(data){
        	if (data.total == 0) {
        		$('#header3').remove();
        		return;
        	}
        	for (var i = 1; i <= data.total; i++) {
        		appendQuestion3(i, data.data[i - 1].Question);
        		appendAnswer(i, '.paper-grid .Question3>ul>li', data.data[i - 1].MyAnswer, data.data[i - 1].StdAnswer);        		
        	}
        }
    });
	$.ajax({
        type: "GET",
        url: "EduTrainAction!getReviewPaper",
        data: { testpaperID: testpaperID, type: "简答题" },
        dataType: "json",
        success: function(data){
        	if (data.total == 0) {
        		$('#header4').remove();
        		return;
        	}
        	for (var i = 1; i <= data.total; i++) {
        		appendQuestion4(i, data.data[i - 1].Question);
        		appendAnswer(i, '.paper-grid .Question4>ul>li', data.data[i - 1].MyAnswer, data.data[i - 1].StdAnswer);
        	}
        }
    });

    $('textarea').each(function() {
    	$(this).remove();
    })
})

function appendAnswer(index, loc ,myAnswer, stdAnswer) {
	html = '<h4>我的答案：  '+ myAnswer + '</h4><h4>参考答案：  ' + stdAnswer + '</h4>';
	$(loc).eq(index - 1).append(html);
	if (myAnswer != stdAnswer) {
		$(loc).eq(index - 1).css('color', 'red');
		$(loc + '>h3').eq(index - 1).css('color', 'red');
		$(loc + '>h4').eq(2 * (index - 1)).css('color', 'red');
	}
}
</script>
</head>
<body>
<h1 class="title" align="center" style="margin-top:15px;"></h1>
<h6 class="name" align="center"></h6>
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
								
			</div>        		
		</div>
	</div>
</div>
</body>
</html>