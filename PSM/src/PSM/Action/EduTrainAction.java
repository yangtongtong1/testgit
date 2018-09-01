package PSM.Action;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

import PSM.Service.EduTrainService;
import hibernate.Fbactivity;
import hibernate.Fbdailytrain;
import hibernate.Multimediafile;
import hibernate.Question;
import hibernate.Testrecord;
import hibernate.Trainplan1;
import hibernate.Trainplan2;
import hibernate.Traintable;

public class EduTrainAction extends ActionSupport {
	
	private int start;	//分页查询参
	private int limit;	//分页查询参数 
	private String tableID;
	private String type;
	private String projectName;
	private LogAction InsertLog = new LogAction();
	
	private EduTrainService eduTrainService;

	public int getStart() 
	{
		return start;
	}
	
	public void setStart(int start) 
	{
		this.start = start;
	}
	
	public int getLimit() 
	{
		return limit;
	}
	
	public void setLimit(int limit) 
	{
		this.limit = limit;
	}
		
	public String getTableID() {
		return tableID;
	}
	
	public void setTableID(String tableID) {
		this.tableID = tableID;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getProjectName() {
		return projectName;
	}
	
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	public EduTrainService getEduTrainService() {
		return eduTrainService;
	}
	
	public void setEduTrainService(EduTrainService eduTrainService) {
		this.eduTrainService = eduTrainService;
	}
	
	public String execute() throws Exception
	{	
		return SUCCESS;
	}
	
	private void outputJSON(HttpServletResponse response, String jsonStr){
		try {
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html; charset=utf-8");
			PrintWriter out = response.getWriter();
			System.out.println(jsonStr);
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getTrainplan1ListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		try {
			String jsonStr = eduTrainService.getTrainplan1List("", start, limit, projectName);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getTrainplan2ListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		try {
			String jsonStr = eduTrainService.getTrainplan2List("", start, limit, projectName);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getTraintableListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String tableID = request.getParameter("tableID");
		try {
			String jsonStr = eduTrainService.getTraintableList("", start, limit, tableID, projectName);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getFbdailytrainListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String jsonStr = eduTrainService.getFbdailytrainList("", start, limit, projectName);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getFbactivityListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String jsonStr = eduTrainService.getFbactivityList("", start, limit, request.getParameter("Theme"), projectName);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getMultimediafileListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String jsonStr = eduTrainService.getMultimediafileList("", start, limit, type, projectName);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getQuestionListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String type = request.getParameter("type");
			String belongTo = request.getParameter("belongTo");
			String jsonStr = eduTrainService.getQuestionList(belongTo, type, "", start, limit);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getTestrecordListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		try {
			String jsonStr = eduTrainService.getTestrecordList("", start, limit, projectName);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	// 获得项目部，以及设计院的考试列表
	public void getDoubleTestrecordListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		try {
			String jsonStr = eduTrainService.getDoubleTestrecordList(projectName);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getTestpaperListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			int testID = Integer.parseInt(request.getParameter("testID"));
			String projectName = request.getParameter("projectName");
			String jsonStr = eduTrainService.getTestpaperList(testID, projectName, "", start, limit);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getProjectListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		try {
			String jsonStr = eduTrainService.getProjectList(start, limit);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getTrainplan1ListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String jsonStr = eduTrainService.getTrainplan1List(findstr, start, limit, projectName);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getTrainplan2ListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String jsonStr = eduTrainService.getTrainplan2List(findstr, start, limit, projectName);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getTraintableListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String tableID = request.getParameter("tableID");
			String jsonStr = eduTrainService.getTraintableList(findstr, start, limit, tableID, projectName);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getFbdailytrainListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String jsonStr = eduTrainService.getFbdailytrainList(findstr, start, limit, projectName);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getFbactivityListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String jsonStr = eduTrainService.getFbactivityList(findstr, start, limit, request.getParameter("Theme"), projectName);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getMultimediafileListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String jsonStr = eduTrainService.getMultimediafileList(findstr, start, limit, type, projectName);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
		
	public void getQuestionListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String type = request.getParameter("type");
			String findstr = request.getParameter("findStr");
			String belongTo = request.getParameter("belongTo");
			String jsonStr = eduTrainService.getQuestionList(belongTo, type, findstr, start, limit);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getTestrecordListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String jsonStr = eduTrainService.getTestrecordList(findstr, start, limit, projectName);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getTestpaperListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			int testID = Integer.parseInt(request.getParameter("testID"));
			String projectName = request.getParameter("projectName");
			String jsonStr = eduTrainService.getTestpaperList(testID, projectName, findstr, start, limit);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public Trainplan1 getTrainplan1() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Trainplan1 pro = new Trainplan1();
		pro.setContent(request.getParameter("Content"));
		pro.setEmployee(request.getParameter("Employee"));
		pro.setMethod(request.getParameter("Method"));
		pro.setClassDate(sdf.parse(request.getParameter("ClassDate")));
		pro.setClassTime(request.getParameter("ClassTime"));
		pro.setBudget(request.getParameter("Budget"));
		pro.setResult(request.getParameter("Result"));
		pro.setProjectName(projectName);
		return pro;
	}
	
	public Trainplan2 getTrainplan2() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Trainplan2 pro = new Trainplan2();
		pro.setContent(request.getParameter("Content"));
		pro.setMethod(request.getParameter("Method"));
		pro.setActDate(sdf.parse(request.getParameter("ActDate")));
		String RegistDate = request.getParameter("RegistDate");
		if (RegistDate != null && !RegistDate.equals(""))
			pro.setRegistDate(sdf.parse(RegistDate));
		pro.setFunding(request.getParameter("Funding"));
		pro.setProjectName(projectName);
		return pro;
	}
	
	public Traintable getTraintable() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Traintable pro = new Traintable();
		pro.setContent(request.getParameter("Content"));
		pro.setEmployee(request.getParameter("Employee"));
		pro.setMethod(request.getParameter("Method"));
		pro.setTrainDate(sdf.parse(request.getParameter("TrainDate")));
		pro.setRegistDate(sdf.parse(request.getParameter("RegistDate")));
		pro.setFunding(request.getParameter("Funding"));
		pro.setTableId(request.getParameter("TableID"));
		pro.setProjectName(projectName);
		return pro;
	}
	
	public Fbdailytrain getFbdailytrain() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Fbdailytrain pro = new Fbdailytrain();
		pro.setFenbao(request.getParameter("Fenbao"));
		pro.setStartDate(sdf.parse(request.getParameter("StartDate")));
		pro.setEndDate(sdf.parse(request.getParameter("EndDate")));
		pro.setTime(Integer.parseInt(request.getParameter("Time")));
		pro.setRegistDate(sdf.parse(request.getParameter("RegistDate")));
		pro.setRecord(request.getParameter("Record"));
		pro.setProjectName(projectName);
		return pro;
	}
	
	public Fbactivity getFbactivity() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Fbactivity pro = new Fbactivity();
		pro.setActivityDate(sdf.parse(request.getParameter("ActivityDate")));
		pro.setOrganization(request.getParameter("Organization"));
		pro.setTheme(request.getParameter("Theme"));
		pro.setProjectName(projectName);
		return pro;
	}
	
	public Multimediafile getMultimediafile() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Multimediafile pro = new Multimediafile();
		pro.setUploadDate(new Date());
		pro.setType(type);
		pro.setProjectName(projectName);
		return pro;
	}
	
	public Question getQuestion() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Question pro = new Question();
		String Type = request.getParameter("Type");
		pro.setType(Type);
		pro.setQuestion(request.getParameter("Question"));
		pro.setOptionA(request.getParameter("OptionA"));
		pro.setOptionB(request.getParameter("OptionB"));
		pro.setOptionC(request.getParameter("OptionC"));
		pro.setOptionD(request.getParameter("OptionD"));
		pro.setOptionE(request.getParameter("OptionE"));
		pro.setBelongTo(request.getParameter("belongTo"));
		pro.setEnable(true);
		String Answer = "";
		if (Type.equals("单选题"))
			Answer = request.getParameter("Answer1");
		else if (Type.equals("多选题")) {
			String[] Answer2Array = request.getParameterValues("Answer2");
			for (String s : Answer2Array) Answer += s;
		} else if (Type.equals("判断题"))
			Answer = request.getParameter("Answer3");
		else if (Type.equals("简答题"))
			Answer = request.getParameter("Answer");
		pro.setAnswer(Answer);
		return pro;
	}
	
	public Testrecord getTestrecord() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Testrecord pro = new Testrecord();
		pro.setName(request.getParameter("Name"));
		pro.setBelongTo(request.getParameter("belongTo"));
		pro.setQuestionScore1(Integer.parseInt(request.getParameter("QuestionScore1")));
		pro.setQuestionScore2(Integer.parseInt(request.getParameter("QuestionScore2")));
		pro.setQuestionScore3(Integer.parseInt(request.getParameter("QuestionScore3")));
		pro.setQuestionScore4(Integer.parseInt(request.getParameter("QuestionScore4")));
		return pro;
	}
	
	public void addTrainplan1() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/")+"upload\\";
		try {
			Trainplan1 pro = getTrainplan1();
			String jsonStr = eduTrainService.addTrainplan1(pro, fileName, rootPath);
			String OptNote = "添加了教育培训主题为" + pro.getContent() + "的年度安全教育培训";
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addTrainplan2() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/")+"upload\\";
		try {
			Trainplan2 pro = getTrainplan2();			
			String jsonStr = eduTrainService.addTrainplan2(pro, fileName, rootPath);
			String OptNote = "添加了教育培训主题为" + pro.getContent() + "的三项业务宣传培训";
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addTraintable() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/")+"upload\\";
		try {
			Traintable pro = getTraintable();			
			String jsonStr = eduTrainService.addTraintable(pro, fileName, rootPath);
			String OptNote = "添加了教育培训主题为" + pro.getContent() + "的" + pro.getTableId();
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addFbdailytrain() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/")+"upload\\";
		try {
			Fbdailytrain pro = getFbdailytrain();			
			String jsonStr = eduTrainService.addFbdailytrain(pro, fileName, rootPath);
			String OptNote = "添加了报备分包方为" + pro.getFenbao() + "的分包方人员日常教育培训";
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addFbactivity() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/")+"upload\\";
		try {
			Fbactivity pro = getFbactivity();			
			String jsonStr = eduTrainService.addFbactivity(pro, fileName, rootPath);
			String OptNote = "添加了分包单位名称为" + pro.getOrganization() + "的分包方安全生产班前会";
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addMultimediafile() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/")+"upload\\";
		try {
			Multimediafile pro = getMultimediafile();			
			String jsonStr = eduTrainService.addMultimediafile(pro, fileName, rootPath);
			String OptNote = "添加了文件名称为" + pro.getAccessory() + "的" + pro.getType() + "文件";
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addQuestion() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			Question pro = getQuestion();			
			String jsonStr = eduTrainService.addQuestion(pro);
			String OptNote = "添加了问题为" + pro.getQuestion() + "的题库问题";
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addTestrecord() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			Testrecord pro = getTestrecord();
			String source = request.getParameter("Source");
			if (source.equals("")) {
				outputJSON(response, "{\"success\":false,\"msg\":\"添加失败，请检查考试名称和试题来源！\"}");
				return;
			}
			int Question1Num = Integer.parseInt(request.getParameter("Question1Num"));
			int Question2Num = Integer.parseInt(request.getParameter("Question2Num"));
			int Question3Num = Integer.parseInt(request.getParameter("Question3Num"));
			int Question4Num = Integer.parseInt(request.getParameter("Question4Num"));
			String jsonStr = eduTrainService.addTestrecord(pro, source, Question1Num, Question2Num, Question3Num, Question4Num);
			String OptNote = "添加了考试名称为" + pro.getName() + "的考试";
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void editTrainplan1() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/")+"upload\\";
		try {
			Trainplan1 pro = getTrainplan1();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));			
			String jsonStr = eduTrainService.editTrainplan1(pro, fileName, rootPath);
			String OptNote = "编辑了教育培训主题为" + pro.getContent() + "的年度安全教育培训";
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void editTrainplan2() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/")+"upload\\";
		try {
			Trainplan2 pro = getTrainplan2();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));			
			String jsonStr = eduTrainService.editTrainplan2(pro, fileName, rootPath);
			String OptNote = "编辑了教育培训主题为" + pro.getContent() + "的三项业务宣传培训";
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void editTraintable() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/")+"upload\\";
		try {
			Traintable pro = getTraintable();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));
			String jsonStr = eduTrainService.editTraintable(pro, fileName, rootPath);
			String OptNote = "编辑了教育培训主题为" + pro.getContent() + "的" + pro.getTableId();
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void editFbdailytrain() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/")+"upload\\";
		try {
			Fbdailytrain pro = getFbdailytrain();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));
			String jsonStr = eduTrainService.editFbdailytrain(pro, fileName, rootPath);
			String OptNote = "编辑了报备分包方为" + pro.getFenbao() + "的分包方人员日常教育培训";
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void editFbactivity() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/")+"upload\\";
		try {
			Fbactivity pro = getFbactivity();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));
			String jsonStr = eduTrainService.editFbactivity(pro, fileName, rootPath);
			String OptNote = "编辑了分包单位名称为" + pro.getOrganization() + "的分包方安全生产班前会";
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void editMultimediafile() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/")+"upload\\";
		try {
			Multimediafile pro = getMultimediafile();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));
			String jsonStr = eduTrainService.editMultimediafile(pro, fileName, rootPath);
			String OptNote = "编辑了文件名称为" + pro.getAccessory() + "的" + pro.getType() + "文件";
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void editQuestion() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			Question pro = getQuestion();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			String jsonStr = eduTrainService.editQuestion(pro);
			String OptNote = "编辑了问题为" + pro.getQuestion() + "的题库问题";
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void editTestrecord() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			Testrecord pro = getTestrecord();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			String jsonStr = eduTrainService.editTestrecord(pro);
			String OptNote = "编辑了考试名称为" + pro.getName() + "的考试";
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deleteTrainplan1() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/")+"upload\\";
		try {			
			String jsonStr = eduTrainService.deleteTrainplan1(ID, rootPath);
			String OptNote = "删除了ID为" + ID + "的年度安全教育培训";
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deleteTrainplan2() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/")+"upload\\";
		try {			
			String jsonStr = eduTrainService.deleteTrainplan2(ID, rootPath);
			String OptNote = "删除了ID为" + ID + "的三项业务宣传培训";
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deleteTraintable() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/")+"upload\\";
		try {			
			String jsonStr = eduTrainService.deleteTraintable(ID, rootPath);
			String OptNote = "删除了ID为" + ID + "的通用培训表";
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deleteFbdailytrain() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/")+"upload\\";
		try {			
			String jsonStr = eduTrainService.deleteFbdailytrain(ID, rootPath);
			String OptNote = "删除了ID为" + ID + "的分包方人员日常教育培训";
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deleteFbactivity() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/")+"upload\\";
		try {			
			String jsonStr = eduTrainService.deleteFbactivity(ID, rootPath);
			String OptNote = "删除了ID为" + ID + "的分包方安全生产班前会";
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deleteMultimediafile() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/")+"upload\\";
		try {			
			String jsonStr = eduTrainService.deleteMultimediafile(ID, rootPath);
			String OptNote = "删除了ID为" + ID + "的资料文件";
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deleteQuestion() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		try {			
			String jsonStr = eduTrainService.deleteQuestion(ID);
			String OptNote = "删除了ID为" + ID + "的题库问题";
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deleteTestrecord() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		try {			
			String jsonStr = eduTrainService.deleteTestrecord(ID);
			String OptNote = "删除了ID为" + ID + "的考试";
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deleteTestpaper() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		try {			
			String jsonStr = eduTrainService.deleteTestpaper(ID);
			String OptNote = "删除了ID为" + ID + "的试卷";
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getEmployees() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String tableID = request.getParameter("tableID");String jsonStr = eduTrainService.getEmployees(tableID);
			outputJSON(response, jsonStr);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void previewDoc() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String foldname = request.getParameter("foldname");
			String filename = request.getParameter("filename");
			String rootPath = request.getRealPath("/") + foldname.replace('/', '\\');
			System.out.println("rootPath : " + rootPath);
			outputJSON(response, "{\"result\":\"success\"}");
		}
		catch(Exception e) {
			System.out.println("error !!!!!");
			outputJSON(response, "{\"result\":\"failed\"}");
		}
	}
	
	public void getFileInfo() {
		 HttpServletRequest request = ServletActionContext.getRequest();
		 HttpServletResponse response = ServletActionContext.getResponse();
		 String path = request.getParameter("path");
		 String fileName = request.getParameter("name");
		 try {
			 String filePath = request.getRealPath("/")+"upload\\"+path+"\\"+fileName; 
			 String fileLength = eduTrainService.getFileInfo(filePath);			 
			 outputJSON(response, fileLength);
		 }
		 catch(Exception e) {
			e.printStackTrace();
		 }
		 
	 }
	
	public void deleteAllFile() {
		 HttpServletRequest request = ServletActionContext.getRequest();
		 HttpServletResponse response = ServletActionContext.getResponse();
		 String fileName = request.getParameter("fileName");
		 String ppID = request.getParameter("id");
		 String rootPath = request.getRealPath("/")+"upload\\";
		 String tableID = request.getParameter("tableID");
		 try {
			 String jsonStr = eduTrainService.deleteAllFile(ppID, fileName, rootPath);			 
			 outputJSON(response, jsonStr);
		 }
		 catch(Exception e) {
			e.printStackTrace();
		 }
	 }
	 
	 public void deleteOneFile() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		String fileName = request.getParameter("name");
		String ppID = request.getParameter("id");			
		String rootPath = request.getRealPath("/")+"upload\\";
		try {
			 String jsonStr = eduTrainService.deleteOneFile(ppID,fileName,rootPath);			 
			 outputJSON(response, jsonStr);
		 }
		 catch(Exception e) {
			e.printStackTrace();
		 }
	 }
	 
	 public void getFileNameList() {
		 HttpServletRequest request = ServletActionContext.getRequest();
		 HttpServletResponse response = ServletActionContext.getResponse();
		 String node = request.getParameter("clicknode");
		 try {
			 String jsonStr = "[";
			 jsonStr += "{\"FoldName\":\"" + "201608*test" + "\",\"FileName\":\"" + "湖北1.docx" + "\"}";
			 jsonStr += ",{\"FoldName\":\"" + "201608*test" + "\",\"FileName\":\"" + "湖北2.docx" + "\"}";
			 jsonStr += "]";
			 outputJSON(response, jsonStr);
		 }
		 catch(Exception e) {
			e.printStackTrace();
		 }
	 }	 
	
	public void testLoginBefore() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		int testID = Integer.parseInt(request.getParameter("testID"));
		String idno = request.getParameter("idno");
		boolean result = eduTrainService.testLogin(testID, idno);
		outputJSON(response, "{\"success\":"+result+"}");
	} 
	
	public String testLogin() {
		HttpServletRequest request = ServletActionContext.getRequest();
		int testID = Integer.parseInt(request.getParameter("testID"));
		String idno = request.getParameter("idno");
		if (eduTrainService.testLogin(testID, idno))
			return "test";
		return "testlogin";
	}
	 
	 public void getTest()
	 {
		 HttpServletResponse response = ServletActionContext.getResponse();
		 HttpServletRequest request = ServletActionContext.getRequest();
		 try {
			String type = request.getParameter("type");
			int testID = Integer.parseInt(request.getParameter("testID"));
			String jsonStr = eduTrainService.getQuestion(testID, type);
			outputJSON(response, jsonStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	 }
	 
	 public void submitPaper()
	 {
		 HttpServletResponse response = ServletActionContext.getResponse();
		 HttpServletRequest request = ServletActionContext.getRequest();
		 try {
			int testID = Integer.parseInt(request.getParameter("testID"));
			int projectID = Integer.parseInt(request.getParameter("projectID"));
			String name = request.getParameter("name");
			String idno = request.getParameter("idno");
			String answerList1 = request.getParameter("answerList1");
			String answerList2 = request.getParameter("answerList2");
			String answerList3 = request.getParameter("answerList3");
			String answerList4 = request.getParameter("answerList4");
			String jsonStr = eduTrainService.submitPaper(testID, projectID, name, idno,
					answerList1, answerList2, answerList3, answerList4);
			outputJSON(response, jsonStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	 }
	 
	 public String testReview()
	 {
		 return "testreview";
	 }
	 
	 public String testPeview()
	 {
		 return "test";
	 }
	 
	 public void getReviewPaper()
	 {
		 HttpServletResponse response = ServletActionContext.getResponse();
		 HttpServletRequest request = ServletActionContext.getRequest();
		 try {
			String type = request.getParameter("type");
			int testpaperID = Integer.parseInt(request.getParameter("testpaperID"));
			String jsonStr = eduTrainService.getReviewQuestion(testpaperID, type);
			outputJSON(response, jsonStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	 }
	 
	 public void getTestTitle() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String jsonStr = "";
			if (request.getParameter("testID") != null) {
				int testID = Integer.parseInt(request.getParameter("testID"));
				jsonStr = eduTrainService.getTestTitleByTestID(testID);
			} else if (request.getParameter("testpaperID") != null) {
				int testpaperID = Integer.parseInt(request.getParameter("testpaperID"));
				jsonStr = eduTrainService.getTestTitleByTestpaperID(testpaperID);
			}
			outputJSON(response, jsonStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	 }
	 
	 public void getTestName() {
		 HttpServletResponse response = ServletActionContext.getResponse();
		 HttpServletRequest request = ServletActionContext.getRequest();
		 try {			
			int testpaperID = Integer.parseInt(request.getParameter("testpaperID"));
			String jsonStr = eduTrainService.getTestName(testpaperID);
			outputJSON(response, jsonStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	 }
	 
	 public void getProjectNameList() {
		 HttpServletResponse response = ServletActionContext.getResponse();
		 String jsonStr = eduTrainService.getProjectNameList();
		 outputJSON(response, jsonStr);
	 }
}
