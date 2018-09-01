function appendQuestion1(index, question, optionA, optionB, optionC, optionD) {
	html = '<li>'
		 + '<h3 id="question1_'+index+'">'+index+'. '+question+'</h3>'
	     + '<ul class="question uk-list uk-list-space">'
	     + '<li>'
	     + '<input type="radio" name="question1_'+index+'" id="question1_'+index+'_A" value="A">'
	     + '<label for="question1_'+index+'_A">A. '+optionA+'</label>'
	     + '</li>'
		 + '<li>'
	     + '<input type="radio" name="question1_'+index+'" id="question1_'+index+'_B" value="B">'
	     + '<label for="question1_'+index+'_B">B. '+optionB+'</label>'
	     + '</li>'
	     + '<li>'
	     + '<input type="radio" name="question1_'+index+'" id="question1_'+index+'_C" value="C">'
	     + '<label for="question1_'+index+'_C">C. '+optionC+'</label>'
	     + '</li>'
	     + '<li>'
	     + '<input type="radio" name="question1_'+index+'" id="question1_'+index+'_D" value="D">'
	     + '<label for="question1_'+index+'_D">D. '+optionD+'</label>'
	     + '</li>'
	     + '</ul>'
	     + '</li>'
	$('.Question1>ul').append(html);
}

function appendQuestion2(index, question, optionA, optionB, optionC, optionD, optionE) {
	html = '<li>'
		 + '<h3 id="question2_'+index+'">'+index+'. '+question+'</h3>'
	     + '<ul class="question uk-list uk-list-space">'
	     + '<li>'
	     + '<input type="checkbox" name="question2_'+index+'" id="question2_'+index+'_A" value="A">'
	     + '<label for="question2_'+index+'_A">A. '+optionA+'</label>'
	     + '</li>'
		 + '<li>'
	     + '<input type="checkbox" name="question2_'+index+'" id="question2_'+index+'_B" value="B">'
	     + '<label for="question2_'+index+'_B">B. '+optionB+'</label>'
	     + '</li>'
	     + '<li>'
	     + '<input type="checkbox" name="question2_'+index+'" id="question2_'+index+'_C" value="C">'
	     + '<label for="question2_'+index+'_C">C. '+optionC+'</label>'
	     + '</li>'
	     + '<li>'
	     + '<input type="checkbox" name="question2_'+index+'" id="question2_'+index+'_D" value="D">'
	     + '<label for="question2_'+index+'_D">D. '+optionD+'</label>'
	     + '</li>'
	     + '<li>'
	     + '<input type="checkbox" name="question2_'+index+'" id="question2_'+index+'_E" value="E">'
	     + '<label for="question2_'+index+'_E">E. '+optionE+'</label>'
	     + '</li>'
	     + '</ul>'
	     + '</li>'
	$('.Question2>ul').append(html);
}

function appendQuestion3(index, question) {
	html = '<li>'
		 + '<h3 id="question3_'+index+'">'+index+'. '+question+'</h3>'
	     + '<ul class="uk-list uk-list-space">'
	     + '<li>'
	     + '<input type="radio" name="question3_'+index+'" id="question3_'+index+'_A" value="T">'
	     + '<label for="question3_'+index+'_A">正确</label>'
	     + '</li>'
		 + '<li>'
	     + '<input type="radio" name="question3_'+index+'" id="question3_'+index+'_B" value="F">'
	     + '<label for="question3_'+index+'_B">错误</label>'
	     + '</li>'	     
	     + '</ul>'
	     + '</li>'
	$('.Question3>ul').append(html);
}

function appendQuestion4(index, question) {
	html = '<li><h3 id="question4_'+index+'">'+index+'. '+question+'</h3>'
	     + '<textarea name="question4_'+index+'" cols ="75" rows = "5"></textarea></li>'
	$('.Question4>ul').append(html);
}