package PSM.Action;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

import PSM.Service.FileSystemService;
import hibernate.Commonfile;
import hibernate.Commonlaw;
import hibernate.Commonsystem;
import hibernate.Exemplaryfile;
import hibernate.Fbsystem;
import hibernate.Prodepartdoc;
import hibernate.Standmodel;

public class FileSystemAction extends ActionSupport
{
	private int start;	//分页查询参
	private int limit;	//分页查询参数 
	private String type;
	private String projectName;
	private LogAction InsertLog = new LogAction();
	
	private FileSystemService fileSystemService;

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
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		System.out.println("Type" + type);
		this.type = type;
	}
	
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		System.out.println("ProjectName" + projectName);
		this.projectName = projectName;
	}
	public FileSystemService getFileSystemService() {
		return fileSystemService;
	}

	public void setFileSystemService(FileSystemService fileSystemService) {
		this.fileSystemService = fileSystemService;
	}
	
	public String execute() throws Exception
	{	
		return SUCCESS;
	}
	
	private void outputJSON(HttpServletResponse response, String jsonStr) {
		try {
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html; charset=utf-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getHSEinfo() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			int ID = Integer.parseInt(request.getParameter("ID"));
			String jsonStr = fileSystemService.getHSEinfo(ID);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void importExcel() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String type = request.getParameter("type");
			String fileName = request.getParameter("fileName");
			String rootPath = request.getRealPath("/")+"upload\\";
			int total = fileSystemService.importExcel(type, rootPath, fileName);
			outputJSON(response, "{\"result\":\"success\",\"total\":"+total+"}");
		}
		catch(Exception e) {
			e.printStackTrace();
			outputJSON(response, "{\"result\":\"success\"}");
		}
	}
	
	public String getProjectListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String jsonStr = fileSystemService.getProjectList("", start, limit);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String getCommonlawListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String jsonStr = fileSystemService.getCommonlawList("", start, limit, type);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String getCommonfileListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String jsonStr = fileSystemService.getCommonfileList("", start, limit, type);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String getCommonsystemListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String jsonStr = fileSystemService.getCommonsystemList("", start, limit, type);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String getProdepartdocListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String jsonStr = fileSystemService.getProdepartdocList("", start, limit, projectName);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String getFbsystemListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String jsonStr = fileSystemService.getFbsystemList("", start, limit, projectName);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String getProjectListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr =  request.getParameter("findStr");
			String jsonStr = fileSystemService.getProjectList(findstr, start, limit);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String getCommonlawListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr =  request.getParameter("findStr");
			String jsonStr = fileSystemService.getCommonlawList(findstr, start, limit, type);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String getCommonfileListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr =  request.getParameter("findStr");
			String jsonStr = fileSystemService.getCommonfileList(findstr, start, limit, type);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String getCommonsystemListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr =  request.getParameter("findStr");
			String jsonStr = fileSystemService.getCommonsystemList(findstr, start, limit, type);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String getProdepartdocListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr =  request.getParameter("findStr");
			String jsonStr = fileSystemService.getProdepartdocList(findstr, start, limit, projectName);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String getFbsystemListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr =  request.getParameter("findStr");
			String jsonStr = fileSystemService.getFbsystemList(findstr, start, limit, projectName);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public Exemplaryfile getProject() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Exemplaryfile pro = new Exemplaryfile();
		pro.setNo(request.getParameter("No"));
		pro.setName(request.getParameter("Name"));
		pro.setFromUnit(request.getParameter("FromUnit"));
		pro.setHowFast(request.getParameter("HowFast"));
		pro.setFileRequire(request.getParameter("FileRequire"));
		pro.setWriteOpinion(request.getParameter("WriteOpinion").replace("\n", ""));
		if(pro.getWriteOpinion().equals("不超过500个字符")) {
			pro.setWriteOpinion("");
		}
		return pro;
	}
	
	public Commonlaw getCommonlaw() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Commonlaw pro = new Commonlaw();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		pro.setNo(request.getParameter("No"));
		pro.setName(request.getParameter("Name"));
		pro.setFromUnit(request.getParameter("FromUnit"));
		pro.setEnactDate(sdf.parse(request.getParameter("EnactDate")));
		pro.setApplyDate(sdf.parse(request.getParameter("ApplyDate")));
		pro.setType(request.getParameter("Type"));
		return pro;
	}
	
	public Commonfile getCommonfile() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Commonfile pro = new Commonfile();
		pro.setNo(request.getParameter("No"));
		pro.setName(request.getParameter("Name"));
		pro.setFromUnit(request.getParameter("FromUnit"));
		pro.setType(request.getParameter("Type"));
		return pro;
	}
	
	public Commonsystem getCommonsystem() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Commonsystem pro = new Commonsystem();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		pro.setSendDate(sdf.parse(request.getParameter("SendDate")));
		try {
			pro.setReceiveDate(sdf.parse(request.getParameter("ReceiveDate")));
		} catch (Exception e) {
			pro.setReceiveDate(null);
		}
		pro.setNo(request.getParameter("No"));
		pro.setName(request.getParameter("Name"));
		pro.setFromUnit(request.getParameter("FromUnit"));
		pro.setUrgency(request.getParameter("Urgency"));
		
		String Requirement = (String) request.getParameter("Requirement").replace("\n", "<br>").replace("\r", "<br>").replace("\"", "\'");
		
		pro.setRequirement(Requirement);
		
		String Opinion = (String) request.getParameter("Opinion").replace("\n", "<br>").replace("\r", "<br>").replace("\"", "\'");
		pro.setOpinion(Opinion);
		pro.setType(request.getParameter("Type"));
		return pro;
	}
	
	public Prodepartdoc getProdepartdoc() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Prodepartdoc pro = new Prodepartdoc();
		pro.setType(request.getParameter("Type"));
		pro.setUploadDate(new Date());
		pro.setProjectName(request.getParameter("ProjectName"));
		return pro;
	}
	
	public Fbsystem getFbsystem() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Fbsystem pro = new Fbsystem();
		pro.setFbUnit(request.getParameter("FbUnit"));
		pro.setUploadDate(new Date());
		pro.setProjectName(request.getParameter("ProjectName"));
		return pro;
	}
	
	public String addProject() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/")+"upload\\";
		try {
			Exemplaryfile pro = getProject();			
			String jsonStr = fileSystemService.addProject(pro, fileName, rootPath);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String addCommonlaw() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/")+"upload\\";
		try {
			Commonlaw pro = getCommonlaw();
			String jsonStr = fileSystemService.addCommonlaw(pro, fileName, rootPath);
			String OptNote = "添加了名称为" + pro.getName() + "的" + pro.getType();
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String addCommonfile() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/")+"upload\\";
		try {
			Commonfile pro = getCommonfile();
			String jsonStr = fileSystemService.addCommonfile(pro, fileName, rootPath);
			String OptNote = "添加了名称为" + pro.getName() + "的" + pro.getType();
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String addCommonsystem() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/")+"upload\\";
		try {
			Commonsystem pro = getCommonsystem();
			String jsonStr = fileSystemService.addCommonsystem(pro, fileName, rootPath);
			String OptNote = "添加了名称为" + pro.getName() + "的" + pro.getType();
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String addProdepartdoc() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/")+"upload\\";
		try {
			Prodepartdoc pro = getProdepartdoc();
			String jsonStr = fileSystemService.addProdepartdoc(pro, fileName, rootPath);
			String OptNote = "添加了文件为" + pro.getAccessory() + "的项目部发文";
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String addFbsystem() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/")+"upload\\";
		try {
			Fbsystem pro = getFbsystem();
			String jsonStr = fileSystemService.addFbsystem(pro, fileName, rootPath);
			String OptNote = "添加了分包方为" + pro.getFbUnit() + "的分包单位制度建设";
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String editProject() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/")+"upload\\";
		try {
			Exemplaryfile pro = getProject();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));			
			String jsonStr = fileSystemService.editProject(pro,fileName,rootPath);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String editCommonlaw() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/")+"upload\\";
		try {
			Commonlaw pro = getCommonlaw();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));
			String jsonStr = fileSystemService.editCommonlaw(pro,fileName,rootPath);
			String OptNote = "编辑了名称为" + pro.getName() + "的" + pro.getType();
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String editCommonfile() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/")+"upload\\";
		try {
			Commonfile pro = getCommonfile();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));
			String jsonStr = fileSystemService.editCommonfile(pro,fileName,rootPath);
			String OptNote = "编辑了名称为" + pro.getName() + "的" + pro.getType();
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String editCommonsystem() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/")+"upload\\";
		try {
			Commonsystem pro = getCommonsystem();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));			
			String jsonStr = fileSystemService.editCommonsystem(pro,fileName,rootPath);
			String OptNote = "编辑了名称为" + pro.getName() + "的" + pro.getType();
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String editProdepartdoc() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/")+"upload\\";
		try {
			Prodepartdoc pro = getProdepartdoc();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));			
			String jsonStr = fileSystemService.editProdepartdoc(pro,fileName,rootPath);
			String OptNote = "编辑了文件为" + pro.getAccessory() + "的项目部发文";
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String editFbsystem() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/")+"upload\\";
		try {
			Fbsystem pro = getFbsystem();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));			
			String jsonStr = fileSystemService.editFbsystem(pro,fileName,rootPath);
			String OptNote = "编辑了分包方为" + pro.getFbUnit() + "的分包单位制度建设";
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String deleteProject() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/")+"upload\\";
		try {			
			String jsonStr = fileSystemService.deleteProject(ID,rootPath);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String deleteCommonlaw() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/")+"upload\\";
		try {			
			String jsonStr = fileSystemService.deleteCommonlaw(ID,rootPath);
			String OptNote = "删除了ID为" + ID + "的通用法律";
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String deleteCommonfile() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/")+"upload\\";
		try {			
			String jsonStr = fileSystemService.deleteCommonfile(ID,rootPath);
			String OptNote = "删除了ID为" + ID + "的通用制度";
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String deleteCommonsystem() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/")+"upload\\";
		try {			
			String jsonStr = fileSystemService.deleteCommonsystem(ID,rootPath);
			String OptNote = "删除了ID为" + ID + "的通用文件";
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String deleteProdepartdoc() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/")+"upload\\";
		try {			
			String jsonStr = fileSystemService.deleteProdepartdoc(ID,rootPath);
			String OptNote = "删除了ID为" + ID + "的项目部发文";
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String deleteFbsystem() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/")+"upload\\";
		try {			
			String jsonStr = fileSystemService.deleteFbsystem(ID,rootPath);
			String OptNote = "删除了ID为" + ID + "的分包单位制度建设";
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	 public String getFileInfo()
	 {
		 HttpServletRequest request = ServletActionContext.getRequest();
		 HttpServletResponse response = ServletActionContext.getResponse();
		 String path = request.getParameter("path");
		 String fileName = request.getParameter("name");
		 try {
			 String filePath = request.getRealPath("/")+"upload\\"+path+"\\"+fileName; 
			 String fileLength = fileSystemService.getFileInfo(filePath);			 
			 outputJSON(response, fileLength);
		 }
		 catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		 }
		 return SUCCESS;
		 
	 }
	 
	 public String deleteAllFile()
	 {
		 HttpServletRequest request = ServletActionContext.getRequest();
		 HttpServletResponse response = ServletActionContext.getResponse();
		 String fileName = request.getParameter("fileName");
		 String ppID = request.getParameter("id");
		 String rootPath = request.getRealPath("/")+"upload\\";
		 try {
			 String jsonStr = fileSystemService.deleteAllFile(ppID,fileName,rootPath);			 
			 outputJSON(response, jsonStr);
		 }
		 catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		 }
		 return SUCCESS;
	 }
	 
	 public String deleteOneFile() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		String fileName = request.getParameter("name");
		String ppID = request.getParameter("id");			
		String rootPath = request.getRealPath("/")+"upload\\";
		try {
			 String jsonStr = fileSystemService.deleteOneFile(ppID,fileName,rootPath);			 
			 outputJSON(response, jsonStr);
		 }
		 catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		 }
		 return SUCCESS;
	 }
	 
	 public String getFileNameList() {
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
			return ERROR;
		 }		 
		 return SUCCESS;
	 }
	 
	 
	 
	 //yangtong 2-1-10
	// *******Standmodel*****************
		public String getStandmodelListDef() {
			HttpServletResponse response = ServletActionContext.getResponse();
			HttpServletRequest request = ServletActionContext.getRequest();
			try {
				String projectName = request.getParameter("projectName");
				String jsonStr = fileSystemService.getStandmodelList("", start, limit);
				System.out.println(jsonStr);
				response.setCharacterEncoding("UTF-8");
				PrintWriter out = response.getWriter();
				out.write(jsonStr);
				out.flush();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
				return ERROR;
			}
			return SUCCESS;
		}

		public String getStandmodelListSearch() {
			HttpServletResponse response = ServletActionContext.getResponse();
			HttpServletRequest request = ServletActionContext.getRequest();
			try {
				String findstr = request.getParameter("findStr");
				String projectName = request.getParameter("projectName");
				String jsonStr = fileSystemService.getStandmodelList(findstr, start, limit);
				System.out.println(jsonStr);
				response.setCharacterEncoding("UTF-8");
				PrintWriter out = response.getWriter();
				out.write(jsonStr);
				out.flush();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
				return ERROR;
			}
			return SUCCESS;
		}

		public Standmodel getStandmodel() throws Exception {
			HttpServletRequest request = ServletActionContext.getRequest();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String dateStr = sdf.format(new Date());

//			java.sql.Date safeptime = java.sql.Date.valueOf(request.getParameter("safeptime"));
			Standmodel standmodel = new Standmodel();
			standmodel.setOne(request.getParameter("one"));
			standmodel.setTwo(request.getParameter("two"));
			standmodel.setThree(request.getParameter("three"));
			standmodel.setModelname(request.getParameter("modelname"));

			return standmodel;
		}

		public String addStandmodel() {
			HttpServletResponse response = ServletActionContext.getResponse();
			HttpServletRequest request = ServletActionContext.getRequest();
			String fileName = request.getParameter("fileName");
			String rootPath = request.getRealPath("/") + "upload\\";
			try {
				Standmodel s = getStandmodel();
				String jsonStr = fileSystemService.addStandmodel(s, fileName, rootPath);

				// System.out.println(jsonStr);
				response.setCharacterEncoding("UTF-8");
				PrintWriter out = response.getWriter();
				out.write(jsonStr);
				out.flush();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
				return ERROR;
			}
			return SUCCESS;
		}

		public String editStandmodel() {
			HttpServletResponse response = ServletActionContext.getResponse();
			HttpServletRequest request = ServletActionContext.getRequest();
			String fileName = request.getParameter("fileName");
			// String fileName2 = request.getParameter("fileName2");
			String rootPath = request.getRealPath("/") + "upload\\";
			try {
				Standmodel s = getStandmodel();
				s.setId(Integer.parseInt(request.getParameter("ID")));
				s.setAccessory(request.getParameter("Accessory"));
				String jsonStr = fileSystemService.editStandmodel(s, fileName, rootPath);
				// System.out.println(jsonStr);
				response.setCharacterEncoding("UTF-8");
				PrintWriter out = response.getWriter();
				out.write(jsonStr);
				out.flush();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
				return ERROR;
			}
			return SUCCESS;
		}

		public String deleteStandmodel() {
			HttpServletResponse response = ServletActionContext.getResponse();
			HttpServletRequest request = ServletActionContext.getRequest();
			String ID = request.getParameter("id");
			System.out.println("-------" + ID);
			try {
				String jsonStr = fileSystemService.deleteStandmodel(ID);
				String OptNote = "删除了ID为" + ID + "的安全评估";
//				InsertLog.InsertOptLog(OptNote);
				// System.out.println(jsonStr);
				response.setCharacterEncoding("UTF-8");
				PrintWriter out = response.getWriter();
				out.write(jsonStr);
				out.flush();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
				return ERROR;
			}
			return SUCCESS;
		}
}
