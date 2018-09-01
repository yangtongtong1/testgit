package PSM.Action;

import java.io.File;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

import PSM.DAO.BasicInfoDAO;
import PSM.Service.MissionService;

public class MissionAction extends ActionSupport{
	private MissionService missionService;
	private int start;	//分页查询参
	private int limit;	//分页查询参数  
	public MissionService getMissionService() {
		return missionService;
	}

	public void setMissionService(MissionService missionService) {
		this.missionService = missionService;
	}
	
	private BasicInfoDAO basicInfoDAO;

	public BasicInfoDAO getBasicInfoDAO() {
		return basicInfoDAO;
	}

	public void setBasicInfoDAO(BasicInfoDAO basicInfoDAO) {
		this.basicInfoDAO = basicInfoDAO;
	}
	
	public int getStart(){
		return start;
	}
	public void setStart(int start){
		this.start = start;
	}
	public int getLimit(){
		return limit;
	}
	public void setLimit(int limit){
		this.limit = limit;
	}
	private void outputJSON(HttpServletResponse response, String jsonStr) throws Exception {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=utf-8");
		PrintWriter out = response.getWriter();
		out.write(jsonStr);
		out.flush();
		out.close();
	}
	//所有任务
	public String totalmission(){
		HttpServletResponse response = ServletActionContext.getResponse();	
		HttpServletRequest request = ServletActionContext.getRequest();		
		try{
			String projectName = request.getParameter("projectName");
			outputJSON(response,missionService.getTotalMission(projectName));
		}
		catch(Exception e){
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String mytaizhang(){
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String user = request.getParameter("user");
		try{	
			outputJSON(response,missionService.getmytaizhang(user,start,limit));
		}
		catch(Exception e){
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String totalbymis(){
		HttpServletResponse response = ServletActionContext.getResponse();
		try{	
			outputJSON(response,missionService.getTotalbyMis());
		}
		catch(Exception e){
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	
	public String checkAppro(){
		HttpServletResponse response = ServletActionContext.getResponse();	
		HttpServletRequest request = ServletActionContext.getRequest();
		String id = request.getParameter("id");  //选择要显示数据的类别
		String title = request.getParameter("title");
		String view = request.getParameter("approview");
		String person = request.getParameter("person");
		try{	
			outputJSON(response,missionService.examinApproval(id,title,view,person));
		}
		catch(Exception e){
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	public String mymission(){
		HttpServletResponse response = ServletActionContext.getResponse();	
		HttpServletRequest request = ServletActionContext.getRequest();
		String category = request.getParameter("data");  //选择要显示数据的类别
		String username = request.getParameter("user");
		try{	
			outputJSON(response,missionService.mymission(category,username));
		}
		catch(Exception e){
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	public String myApproval(){
		HttpServletResponse response = ServletActionContext.getResponse();	
		HttpServletRequest request = ServletActionContext.getRequest();
		String user = request.getParameter("user");  //选择要显示数据的类别
		String data = request.getParameter("data");
		try{	
			outputJSON(response,missionService.myApproval(data,user));
		}
		catch(Exception e){
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	public String addAppro(){
		HttpServletResponse response = ServletActionContext.getResponse();	
		HttpServletRequest request = ServletActionContext.getRequest();
		String approname = request.getParameter("approname");
		String filename = request.getParameter("fileName");
		String user = request.getParameter("user");
	    String approtype = request.getParameter("approtype");
	    String approexp = request.getParameter("approexp");
		String rootPath = request.getRealPath("/")+"upload\\";
		try {
			outputJSON(response,missionService.addAppro(rootPath,approname,filename,approtype,approexp,user));
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return SUCCESS;
	}
	public String alloTask() throws ParseException
	{
		HttpServletResponse response = ServletActionContext.getResponse();
		Date finishtime = new Date();
		Date truefistime = new Date();
		HttpServletRequest request = ServletActionContext.getRequest();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String fileName = request.getParameter("fileName");
		System.out.println(fileName);
		String title = request.getParameter("title");
		System.out.println(title);
		String properson = request.getParameter("properson");
		System.out.println(properson);
		String score = request.getParameter("score");
		System.out.println(score);
		String type = request.getParameter("missionfield");
		System.out.println(type);
		String finsit = request.getParameter("finishSit");
		System.out.println(finsit);
		String misexp = request.getParameter("missionexp");
		System.out.println(misexp);
		String user = request.getParameter("user");
		System.out.println(user);
		if(request.getParameter("fintime")!=null){
			finishtime = sdf.parse(request.getParameter("fintime"));
		}; 
		if(request.getParameter("truefistime")!=null){
			truefistime = sdf.parse(request.getParameter("truefistime"));
		}
		String folder = request.getParameter("folder");
		String missionname = request.getParameter("missionname");
		String rootPath = request.getRealPath("/")+"upload\\";
		System.out.println(type+folder+score+properson+finishtime+truefistime+title+missionname+fileName+rootPath+finsit+misexp+user);
		try {
			outputJSON(response,missionService.alloTask(type,folder,score,properson,finishtime,truefistime,title,missionname,fileName,rootPath,finsit,misexp,user));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return SUCCESS;
	}
	
	public void deleteTask() throws Exception {
		HttpServletResponse response = ServletActionContext.getResponse();	
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		String jsonStr = missionService.deleteTask(ID, rootPath);
		outputJSON(response, jsonStr);
	}
	
	public String finishDis() throws Exception
	{
		HttpServletResponse response = ServletActionContext.getResponse();
		Date solveTime = new Date();
		HttpServletRequest request = ServletActionContext.getRequest();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		int id = Integer.parseInt(request.getParameter("ID"));
		String fileName = request.getParameter("fileName");
		String problem = request.getParameter("problem");
		String solveExp = request.getParameter("solveExp");
		String correction = request.getParameter("correction");
		String correctionfee = request.getParameter("correctionfee");
		String prevent = request.getParameter("prevent");
		String supperson = request.getParameter("supperson");
		String rootPath = request.getRealPath("/")+"upload\\";
		if(request.getParameter("solveTime")!=null){
			solveTime = sdf.parse(request.getParameter("solveTime"));
		}; 
		try {
			outputJSON(response,missionService.finishDis(id,fileName,problem,solveExp,solveTime,rootPath,correction,correctionfee,prevent,supperson));
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return SUCCESS;
	}
	
	
	 public void getFileInfo() throws Exception{
		 HttpServletResponse response = ServletActionContext.getResponse();
		 HttpServletRequest request = ServletActionContext.getRequest();
		 String path = request.getParameter("path");
		 String fileName = request.getParameter("name");
		 String fileLength = new String();
		 File file = new File(request.getRealPath("/")+"upload\\"+path+"\\"+fileName);
		 System.out.println(request.getRealPath("/")+"upload\\"+path+"\\"+fileName);
		 if(file.exists())
			 fileLength = Long.toString(file.length());
		 outputJSON(response,fileLength);
	 }
	 
	 public void getGroupMis() throws Exception{
		 HttpServletResponse response = ServletActionContext.getResponse();
		 HttpServletRequest request = ServletActionContext.getRequest();
		 String group = request.getParameter("group");
		 System.out.println(group);
		 outputJSON(response, missionService.getGroupMis(group));
	 }
	 
	 public void  getPersonNode() throws Exception{
		 HttpServletResponse response = ServletActionContext.getResponse();
		 HttpServletRequest request = ServletActionContext.getRequest();
		 String role = request.getParameter("role");
		 String projectName = request.getParameter("projectName");
		 outputJSON(response,missionService.getPersonNode(role, projectName));
	 }
	 
	 public void getallprojectname() throws Exception{
		 HttpServletResponse response = ServletActionContext.getResponse();
		 HttpServletRequest request = ServletActionContext.getRequest();		
		 outputJSON(response,missionService.getallprojectname());
	 }
	 
	// 获得6种不同状态的项目
	 public void getsomeprojectname() throws Exception{
		 HttpServletResponse response = ServletActionContext.getResponse();
		 HttpServletRequest request = ServletActionContext.getRequest();
		 outputJSON(response, missionService.getsomeprojectname(request.getParameter("type")));
	 }
	 
	 // 统计规定动作，由前台触发请求
	 public void updateMissionState() throws Exception {
		 HttpServletResponse response = ServletActionContext.getResponse();
		 HttpServletRequest request = ServletActionContext.getRequest();
		 String project = request.getParameter("project");
		 int actionId = Integer.parseInt(request.getParameter("actionId"));
		 outputJSON(response, missionService.updateMissionState(project, actionId));
	 }
	 
	 // 返回项目部的规定动作
	 public void getPrescribedAction() throws Exception {
		 HttpServletResponse response = ServletActionContext.getResponse();
		 HttpServletRequest request = ServletActionContext.getRequest();
		 String project = request.getParameter("project");
		 outputJSON(response, missionService.getPrescribedAction(project));
	 }
	 
	 public void init() {
		 missionService.init();
	 }

}
