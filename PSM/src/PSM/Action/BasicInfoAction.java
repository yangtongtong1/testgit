package PSM.Action;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import PSM.DAO.BasicInfoDAO;
import PSM.DAO.LoginDAO;
import PSM.SMS.SendTest;
import PSM.Service.BasicInfoService;
import PSM.Service.EmeRescueService;
import PSM.Service.FileSystemService;
import PSM.Service.GoalDutyService;
import PSM.Service.OperationControlService;
import PSM.Service.SaftyCheckService;
import PSM.Service.SaftyCostService;
import PSM.Tool.DESEncryptCoder;
import hibernate.Admin;
import hibernate.Fbplan;
import hibernate.Fbsystem;
import hibernate.Fenbao;
import hibernate.Fenbaoplan;
import hibernate.Fenbaosaftyaccounts;
import hibernate.Fenbaoyinhuanpczlgzfa;
import hibernate.Kaoheresult;
import hibernate.Koufen;
import hibernate.Koufentongji;
import hibernate.Person;
import hibernate.Persondb;
import hibernate.Project;
import hibernate.Projectmanagement;
import hibernate.Projectperson;
import hibernate.Riskfenbao;
import hibernate.Safetypromanagementfb;
import hibernate.Safetyprotocal;
import hibernate.Saftyproblem;
import hibernate.Taizhangfb;
import hibernate.Yingjifenbao;
import sun.misc.BASE64Encoder;

public class BasicInfoAction extends ActionSupport {
	private int start; // 分页查询参
	private int limit; // 分页查询参数
	private BasicInfoService basicInfoService;
	private LoginDAO loginDAO;
	private String type;
	private GoalDutyService goalDutyService;
	private SaftyCostService saftyCostService;
	private FileSystemService fileSystemService;
	private SaftyCheckService saftyCheckService;
	private OperationControlService operationControlService;
	private EmeRescueService emeRescueService;
	private String projectName;
	
	private BasicInfoDAO basicInfoDAO;

	public BasicInfoDAO getBasicInfoDAO() {
		return basicInfoDAO;
	}

	public void setBasicInfoDAO(BasicInfoDAO basicInfoDAO) {
		this.basicInfoDAO = basicInfoDAO;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		System.out.println("ProjectName" + projectName);
		this.projectName = projectName;
	}

	LogAction InsertLog = new LogAction();

	BASE64Encoder enc = new BASE64Encoder();

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public LoginDAO getLoginDAO() {
		return loginDAO;
	}

	public void setLoginDAO(LoginDAO loginDAO) {
		this.loginDAO = loginDAO;
	}

	public BasicInfoService getBasicInfoService() {
		return basicInfoService;
	}

	public void setBasicInfoService(BasicInfoService basicInfoService) {
		this.basicInfoService = basicInfoService;
	}

	public GoalDutyService getGoalDutyService() {
		return goalDutyService;
	}

	public void setGoalDutyService(GoalDutyService goalDutyService) {
		this.goalDutyService = goalDutyService;
	}

	public SaftyCostService getSaftyCostService() {
		return saftyCostService;
	}

	public void setSaftyCostService(SaftyCostService saftyCostService) {
		this.saftyCostService = saftyCostService;
	}

	public FileSystemService getFileSystemService() {
		return fileSystemService;
	}

	public void setFileSystemService(FileSystemService fileSystemService) {
		this.fileSystemService = fileSystemService;
	}

	public SaftyCheckService getSaftyCheckService() {
		return saftyCheckService;
	}

	public void setSaftyCheckService(SaftyCheckService saftyCheckService) {
		this.saftyCheckService = saftyCheckService;
	}

	public OperationControlService getOperationControlService() {
		return operationControlService;
	}

	public void setOperationControlService(OperationControlService operationControlService) {
		this.operationControlService = operationControlService;
	}

	public EmeRescueService getEmeRescueService() {
		return emeRescueService;
	}

	public void setEmeRescueService(EmeRescueService emeRescueService) {
		this.emeRescueService = emeRescueService;
	}

	public BasicInfoAction() {

	}

	public String execute() throws Exception {
		return SUCCESS;
	}

	public void getProjectListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectNo = request.getParameter("projectNo");
			System.out.println(projectNo);
			String jsonStr = basicInfoService.getProjectList("", projectNo, start, limit);

			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public void getProjectListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectNo = request.getParameter("projectNo");
			String jsonStr = basicInfoService.getProjectList(findstr, projectNo, start, limit);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public Project getProject() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Project pro = new Project();
		pro.setNo(request.getParameter("No"));
		pro.setName(request.getParameter("Name"));
		pro.setScale(request.getParameter("Scale"));
		pro.setBuildUnit(request.getParameter("BuildUnit"));
		pro.setPlace(request.getParameter("Place"));
		pro.setProgress(request.getParameter("Progress"));
		pro.setManager(request.getParameter("Manager"));
		pro.setCost(request.getParameter("Cost"));

		//Date StartTime = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (request.getParameter("StartTime") != null) {
			pro.setStartTime(sdf.parse(request.getParameter("StartTime")));
		}
		;

		//Date FileTime = new Date();
		if (request.getParameter("FileTime") != null) {
			pro.setFileTime(sdf.parse(request.getParameter("FileTime")));
		}
		;

		pro.setBuildContent(request.getParameter("BuildContent").replace("\n", ""));

		if (pro.getBuildContent().equals("不超过500个字符")) {
			pro.setBuildContent("");
		}
		return pro;
	}

	public void addProject() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Project pro = getProject();

			String jsonStr = basicInfoService.addProject(pro, fileName, rootPath);

			String OptNote = "添加了名称为 " + pro.getName() + " 的项目概况";
			InsertLog.InsertOptLog(OptNote);

			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public void editProject() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Project pro = getProject();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));

			String OptNote = "编辑了名称为 " + pro.getName() + " 的项目概况";
			InsertLog.InsertOptLog(OptNote);

			String jsonStr = basicInfoService.editProject(pro, fileName, rootPath);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public void deleteProject() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {

			String jsonStr = basicInfoService.deleteProject(ID, rootPath);
			System.out.println(jsonStr);

			String OptNote = "删除了ID为 " + ID + " 的项目概况";
			InsertLog.InsertOptLog(OptNote);

			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public void getFileInfo() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		String path = request.getParameter("path");
		String fileName = request.getParameter("name");
		try {
			String filePath = request.getRealPath("/") + "upload\\" + path + "\\" + fileName;
			String fileLength = basicInfoService.getFileInfo(filePath);

			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(fileLength);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;

	}

	public void deleteAllFile() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		String fileName = request.getParameter("fileName");
		String ppID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			String jsonStr = basicInfoService.deleteAllFile(ppID, fileName, rootPath);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public void deleteOneFile() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		String fileName = request.getParameter("name");
		String ppID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			String jsonStr = basicInfoService.deleteOneFile(ppID, fileName, rootPath);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	// -------------------------------------
	// ---------
	public void getPersonListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String jsonStr = basicInfoService.getPersonList("", start, limit);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public void getPersonListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String jsonStr = basicInfoService.getPersonList(findstr, start, limit);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public Person getPerson() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Person pro = new Person();
		pro.setName(request.getParameter("Name"));
		pro.setJob(request.getParameter("Job"));
		pro.setIdentityNo(request.getParameter("IdentityNo"));
		pro.setPhoneNo(request.getParameter("PhoneNo"));
		pro.setUnitName(request.getParameter("UnitName"));
		pro.setType(request.getParameter("Type"));
		pro.setUserPwd(
				enc.encode(DESEncryptCoder.encrypt(request.getParameter("UserPwd").getBytes(), DESEncryptCoder.KEY)));

		return pro;
	}

	public void addPerson() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			Person pro = getPerson();

			String jsonStr = basicInfoService.addPerson(pro);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public void editPerson() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			Person pro = getPerson();
			pro.setId(Integer.parseInt(request.getParameter("ID")));

			String jsonStr = basicInfoService.editPerson(pro);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public void deletePerson() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		System.out.println("-------" + ID);
		try {

			String jsonStr = basicInfoService.deletePerson(ID);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public void getfbListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			// String projectNo = request.getParameter("projectNo");
			type = request.getParameter("type");
			// System.out.println(projectNo);
			String projectName = request.getParameter("projectName");
			String jsonStr = basicInfoService.getfbList("", type, start, limit, projectName);
			System.out.println(type);
			System.out.println(jsonStr);
			response.setContentType("text/html; charset=utf-8");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public void getfbListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			// String projectNo = request.getParameter("ProNo");
			type = request.getParameter("type");
			String projectName = request.getParameter("projectName");
			String jsonStr = basicInfoService.getfbList(findstr, type, start, limit, projectName);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	private String replaceSomeChar(String s) {
		String returnStr = s.replace("\n", "<br>").replace("\r", "<br>").replace("\"", "\'");
		return returnStr;
	}
	
	public Fenbao getfb() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Fenbao pro = new Fenbao();

		pro.setCost(replaceSomeChar(request.getParameter("Cost")));
		pro.setHead(replaceSomeChar(request.getParameter("Head")));
		pro.setProHead(replaceSomeChar(request.getParameter("ProHead")));

		pro.setProRange(replaceSomeChar(request.getParameter("ProRange")));
		pro.setTechHead(replaceSomeChar(request.getParameter("TechHead")));
		pro.setProTechHead(replaceSomeChar(request.getParameter("ProTechHead")));
		pro.setProSaveHead(replaceSomeChar(request.getParameter("ProSaveHead")));

		pro.setName(replaceSomeChar(request.getParameter("Name")));
		pro.setRank(replaceSomeChar(request.getParameter("Rank")));
		pro.setProSavePeople(replaceSomeChar(request.getParameter("ProSavePeople")));		
		pro.setProject(replaceSomeChar(request.getParameter("Project")));
		pro.setType(type);

		return pro;
	}

	public void addfb() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Fenbao pro = getfb();

			// 2-4-1 分包单位制度建设
			Fbsystem a = new Fbsystem();
			a.setFbUnit(pro.getName());
			a.setProjectName("");
			a.setUploadDate(new Date());
			a.setProjectName(request.getParameter("projectName"));
			String json = fileSystemService.addFbsystem(a, "", "");

			// 同时查到Safetypromanagementfb这个表中3-1-7（分包方安全生产管理机构）
			Safetypromanagementfb fb = new Safetypromanagementfb();
			fb.setName(pro.getName());
			fb.setFbName(pro.getName());
			// fb.setName("");
			fb.setPerson("");
			fb.setTime(new Date());
			// fb.setProjectName(request.getParameter("projectName"));

			json = goalDutyService.addSafetypromanagementfb(fb, "", "");

			// 同时插到fbplan，3-3-3（分包方安全生产计划）
			Fbplan plan = new Fbplan();
			plan.setName(pro.getName());
			plan.setDate(new Date());
			plan.setNo("");
			plan.setPlanName("");
			// plan.setProjectName(request.getParameter("projectName"));
			json = goalDutyService.addFbplan(plan, "", "");

			// 同时插入，4-2-1（分包方安全生产投入计划）
			Fenbaoplan fbp = new Fenbaoplan();
			fbp.setFenbaoname(pro.getName());
			fbp.setPlanname("");
			fbp.setTime(new Date());
			fbp.setProjectName(request.getParameter("projectName"));

			json = saftyCostService.addFenbaoplan(fbp, "", "");

			// 4-2-2（分包方安全费用使用台账）
			Fenbaosaftyaccounts fbs = new Fenbaosaftyaccounts();
			fbs.setSubcontractor(pro.getName());
			fbs.setChecktime(new Date());
			fbs.setTaizhang("");
			fbs.setProjectName(request.getParameter("projectName"));
			json = saftyCostService.addFenbaosaftyaccounts(fbs, "", "");

			// 7-3-4（分包方隐患排查治理台账）
			Taizhangfb tfb = new Taizhangfb();
			tfb.setFbname(pro.getName());
			tfb.setMonth("1月");
			tfb.setProjectName(request.getParameter("projectName"));
			json = saftyCheckService.addTaizhangfb(tfb, "", "");

			// 7-4-4（分包方危险源管理）
			Riskfenbao rfb = new Riskfenbao();
			rfb.setFenbaoname(pro.getName());
			rfb.setBbtime(new Date());
			rfb.setProjectName(request.getParameter("projectName"));
			json = saftyCheckService.addRiskfenbao(rfb, "", "");

			// 6-2-1（安全生产协议）
			Safetyprotocal sp = new Safetyprotocal();
			sp.setFbname(pro.getName());
			sp.setDate("");
			sp.setName("");
			sp.setType("");

			json = operationControlService.addSafetyprotocal(sp, "", "");

			Fenbaoyinhuanpczlgzfa f = new Fenbaoyinhuanpczlgzfa();
			f.setFenbaoname(pro.getName());
			f.setFilename("");
			f.setUploadtime(new Date());
			f.setWorkname("");
			f.setYear("");
			f.setProjectName(request.getParameter("projectName"));
			json = saftyCheckService.addFenbaoyinhuanpczlgzfa(f, "", "");

			Yingjifenbao y1 = new Yingjifenbao();
			y1.setFenbaoname(pro.getName());
			y1.setFilename("");
			y1.setType("分包方应急培训演练");
			y1.setUploadtime(new Date());
			y1.setProjectName(request.getParameter("projectName"));
			json = emeRescueService.addYingjifenbao(y1, "", "");

			Yingjifenbao y2 = new Yingjifenbao();
			y2.setFenbaoname(pro.getName());
			y2.setFilename("");
			y2.setType("分包方防洪度汛技术要求与应急预案");
			y2.setUploadtime(new Date());
			y2.setProjectName(request.getParameter("projectName"));
			json = emeRescueService.addYingjifenbao(y2, "", "");

			Yingjifenbao y3 = new Yingjifenbao();
			y3.setFenbaoname(pro.getName());
			y3.setFilename("");
			y3.setType("分包方应急预案及现场处置方案");
			y3.setUploadtime(new Date());
			y3.setProjectName(request.getParameter("projectName"));
			json = emeRescueService.addYingjifenbao(y3, "", "");

			String jsonStr = basicInfoService.addfb(pro, fileName, rootPath);
			System.out.println(jsonStr);

			String OptNote = "添加了名称为 " + pro.getName() + " 的分包";
			InsertLog.InsertOptLog(OptNote);

			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public void editfb() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Fenbao pro = getfb();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));

			String jsonStr = basicInfoService.editfb(pro, fileName, rootPath);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public void deletefb() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {

			String jsonStr = basicInfoService.deletefb(ID, rootPath);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public void getProjectpersonListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String projectName = request.getParameter("projectName");
		try {
			String jsonStr = basicInfoService.getProjectpersonList("", start, limit, projectName);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public void getProjectpersonListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String projectName = request.getParameter("projectName");
		try {
			String findstr = request.getParameter("findStr");
			String jsonStr = basicInfoService.getProjectpersonList(findstr, start, limit, projectName);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public Projectperson getProjectperson() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Projectperson pro = new Projectperson();
		pro.setName(request.getParameter("Name"));
		pro.setJob(request.getParameter("Job"));
		pro.setDuty(request.getParameter("Duty").replace("\n", ""));
		if (pro.getDuty().equals("不超过500个字符")) {
			pro.setDuty("");
		}
		return pro;
	}

	public void addProjectperson() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			Projectperson pro = getProjectperson();

			String jsonStr = basicInfoService.addProjectperson(pro);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public void editProjectperson() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			Projectperson pro = getProjectperson();
			pro.setId(Integer.parseInt(request.getParameter("ID")));

			String jsonStr = basicInfoService.editProjectperson(pro);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public void deleteProjectperson() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		System.out.println("-------" + ID);
		try {

			String jsonStr = basicInfoService.deleteProjectperson(ID);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	// jianglingfeng
	// -------------
	public void getProjectmanagementListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		// Map session = (Map)ActionContext.getContext().getSession();
		try {
			// String projectName = (String)session.get("ProjectName");
			String projectName = request.getParameter("projectName");
			System.out.println("--------------store  " + projectName);
			String jsonStr = basicInfoService.getProjectmanagementList("", start, limit, projectName);
			// System.out.println(type);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public void getProjectmanagementListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		// Map session = (Map)ActionContext.getContext().getSession();
		try {
			// String projectName = (String)session.get("ProjectName");
			String findstr = request.getParameter("findStr");
			// String No = request.getParameter("No");
			// type = request.getParameter("type");
			String projectName = request.getParameter("projectName");
			System.out.println("--------------刷新刷新刷新刷新刷新！！！！！！！  " + projectName);
			String jsonStr = basicInfoService.getProjectmanagementList(findstr, start, limit, projectName);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public Projectmanagement getProjectmanagement() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Projectmanagement pro = new Projectmanagement();

		Date StartDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (request.getParameter("StartDate") != null) {
			pro.setStartDate(sdf.parse(request.getParameter("StartDate")));
		}
		;

		pro.setNo(request.getParameter("No"));
		pro.setName(request.getParameter("Name"));
		pro.setScale(request.getParameter("Scale"));
		pro.setBuildUnit(request.getParameter("BuildUnit"));
		pro.setPlace(request.getParameter("Place"));
		pro.setPrice(request.getParameter("Price"));
		pro.setManager(request.getParameter("Manager"));
		// pro.setStartDate(request.getParameter("StartDate"));
		pro.setSchedule(request.getParameter("Schedule"));
		
		pro.setContent(replaceSpecialSymbol(request.getParameter("Content")));
		pro.setCost(request.getParameter("Cost"));
		pro.setProgress(request.getParameter("Progress"));

		return pro;
	}

	private String replaceSpecialSymbol(String s) {
		String res = s.replace("\n", "<br>").replace("\r", "<br>").replace("\"", "\'");
		return res;
	}
	
	public void addProjectmanagement() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";

		try {
			Projectmanagement pro = getProjectmanagement();
			String jsonStr = basicInfoService.addProjectmanagement(pro, fileName, rootPath);
			
			//向扣分统计的表中添加相应的项目信息记录
			Koufentongji koufentongji = new Koufentongji();
			koufentongji.setProname(pro.getName());
			koufentongji.setKoufenitem("");
			koufentongji.setKoufenzhi(0);
			koufentongji.setZongfen(100);
			basicInfoService.addKoufentongji(koufentongji);
			
			
			
			System.out.println("ppppppppppppppppppppppppppp");
			//SendTest.sendMessageByType("test", "测试人员");
			System.out.println("mmmmmmmmmmmmmmmmmmmmmmmmmmmmmm");
			
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public void editProjectmanagement() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Projectmanagement pro = getProjectmanagement();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			String prevName = request.getParameter("prevName");
			String jsonStr = basicInfoService.editProjectmanagement(pro, fileName, rootPath,prevName);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public void deleteProjectmanagement() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {

			String jsonStr = basicInfoService.deleteProjectmanagement(ID, rootPath);
			System.out.println(jsonStr);		
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}
	// -----------end--------------//

	public void getPersondbListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String jsonStr = basicInfoService.getPersondbList("", start, limit);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public void getPersondbListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String jsonStr = basicInfoService.getPersondbList(findstr, start, limit);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public void getPersondbListReflash() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String jsonStr = "";
			String findstr = request.getParameter("findStr");
			String userRole = request.getParameter("userRole");
			String projectName = request.getParameter("projectName");
			if (userRole.equals("项目部人员"))
				jsonStr = basicInfoService.getPersondbListReflash(projectName, findstr, start, limit);
			else 
				jsonStr = basicInfoService.getPersondbListReflash(findstr, start, limit);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public Persondb getPersondb() throws Exception {
		BASE64Encoder enc = new BASE64Encoder();
		HttpServletRequest request = ServletActionContext.getRequest();
		Persondb pro = new Persondb();
		pro.setName(request.getParameter("Name"));
		pro.setBirthday(request.getParameter("Birthday"));
		pro.setIdcard(request.getParameter("IDCard"));
		pro.setPapersNo(request.getParameter("PapersNo"));
		pro.setPapersType(request.getParameter("PapersType"));
		pro.setPhone(request.getParameter("Phone"));
		pro.setPhoneUrgent(request.getParameter("PhoneUrgent"));
		pro.setPtype(request.getParameter("PType"));
		pro.setSex(request.getParameter("Sex"));
		pro.setUserPwd(
				enc.encode(DESEncryptCoder.encrypt(request.getParameter("UserPwd").getBytes(), DESEncryptCoder.KEY)));

		pro.setPapersNoTwo(request.getParameter("PapersNoTwo"));
		pro.setPapersTypeTwo(request.getParameter("PapersTypeTwo"));

		Date PapersDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (request.getParameter("PapersDate") != null && !request.getParameter("PapersDate").equals("")) {
			pro.setPapersDate(sdf.parse(request.getParameter("PapersDate")));
		}
		;

		if (request.getParameter("PapersDateTwo") != null && !request.getParameter("PapersDateTwo").equals("")) {
			pro.setPapersDateTwo(sdf.parse(request.getParameter("PapersDateTwo")));
		}
		;

		return pro;
	}

	public void addPersondb() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			Persondb pro = getPersondb();

			String jsonStr = basicInfoService.addPersondb(pro);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public void editPersondb() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			Persondb pro = getPersondb();
			pro.setId(Integer.parseInt(request.getParameter("ID")));

			String jsonStr = basicInfoService.editPersondb(pro);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public void deletePersondb() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		System.out.println("-------" + ID);
		try {

			String jsonStr = basicInfoService.deletePersondb(ID);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public void getProjectNameListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String name = request.getParameter("userName");
			String role = request.getParameter("userRole");
			String jsonStr = null;

			
			System.out.println(role);
			System.out.println(name);

			if (role.equals("项目部人员")) {
				jsonStr = basicInfoService.getPersondbProjectName(name, start, 999);
				
				System.out.println(jsonStr);
			} else {
				jsonStr = basicInfoService.getProjectNameList("", start, 999);
			}
			// System.out.println(type);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json;charset=utf-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public void getXMJLNameListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {

			String jsonStr = basicInfoService.getXMJLNameList();
			// System.out.println(type);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public void ChangeAdminPwd() {
		String json;
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String testuser = request.getParameter("UserName");
		String OldSc = request.getParameter("OldSc");
		String NewSc = request.getParameter("NewSc");
		String DNewSc = request.getParameter("DNewSc");
		response.setCharacterEncoding("UTF-8");
		try {
			Admin adm = loginDAO.QueryUser("admin");
			if (adm.getUserPwd().equals(enc.encode(DESEncryptCoder.encrypt(OldSc.getBytes(), DESEncryptCoder.KEY)))) {
				if (NewSc.equals(DNewSc)) {
					NewSc = enc.encode(DESEncryptCoder.encrypt(NewSc.getBytes(), DESEncryptCoder.KEY));
					loginDAO.setAdminPwd(NewSc);
					json = "{\"success\":\"true\",\"msg\":\"修改成功！\"}";
				} else {
					json = "{\"failure\":\"true\",\"msg\":\"两次输入不一致！\"}";
				}
			} else {
				json = "{\"failure\":\"true\",\"msg\":\"密码错误！\"}";
			}

			PrintWriter out = response.getWriter();
			out.write(json);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public void ReSetPwd() throws Exception {
		String json;
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		Map session = (Map) ActionContext.getContext().getSession();
		String userid = request.getParameter("userID");
		String name = request.getParameter("userName");

		String role = request.getParameter("userRole");

		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!");
		// System.out.println(testuser);
		System.out.println(userid);
		System.out.println(userid);

		response.setCharacterEncoding("UTF-8");
		if (role.equals("项目部人员")) {

			Persondb pdb = loginDAO.QueryPersondb(userid);
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!");
			// System.out.println(testuser);
			// System.out.println(name);
			System.out.println(pdb.getIdcard());

			String NewSc = userid.substring(userid.length() - 6, userid.length());
			System.out.println(NewSc);
			NewSc = enc.encode(DESEncryptCoder.encrypt(NewSc.getBytes(), DESEncryptCoder.KEY));
			System.out.println(NewSc);
			loginDAO.setPersondbPwd(NewSc, userid);

		} else {
			Person pdb = loginDAO.QueryPerson(userid);
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!");
			// System.out.println(testuser);
			System.out.println(name);
			System.out.println(pdb.getIdentityNo());

			String NewSc = userid.substring(userid.length() - 6, userid.length());
			System.out.println(NewSc);
			NewSc = enc.encode(DESEncryptCoder.encrypt(NewSc.getBytes(), DESEncryptCoder.KEY));
			System.out.println(NewSc);
			loginDAO.setPersonPwd(NewSc, userid);

		}
		/*
		 * json = "{\"success\":\"true\",\"msg\":\"重置成功！\"}"; PrintWriter out =
		 * response.getWriter(); out.write(json); out.flush(); out.close();
		 */
	}

	public void ChangePersondbPwd() {
		String json;
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		Map session = (Map) ActionContext.getContext().getSession();
		String userid = request.getParameter("userId");
		String name = request.getParameter("userName");
		String OldSc = request.getParameter("OldSc");
		String NewSc = request.getParameter("NewSc");
		String DNewSc = request.getParameter("DNewSc");
		String role = request.getParameter("userRole");

		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!");
		// System.out.println(testuser);
		System.out.println(role);

		response.setCharacterEncoding("UTF-8");
		
		if(role.equals("系统管理员")) {
			
			Admin admin = loginDAO.QueryAdm("admin");
			
			try {
				if (admin.getUserPwd()
						.equals(enc.encode(DESEncryptCoder.encrypt(OldSc.getBytes(), DESEncryptCoder.KEY)))) {
					if (NewSc.equals(DNewSc)) {
						NewSc = enc.encode(DESEncryptCoder.encrypt(NewSc.getBytes(), DESEncryptCoder.KEY));

						loginDAO.setAdminPwd(NewSc);
						json = "{\"success\":\"true\",\"msg\":\"修改成功！\"}";
					} else {
						json = "{\"failure\":\"true\",\"msg\":\"两次输入不一致！\"}";
					}
				} else {
					json = "{\"failure\":\"true\",\"msg\":\"密码错误！\"}";
				}

				PrintWriter out = response.getWriter();
				out.write(json);
				out.flush();
				out.close();
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
		else if (role.equals("项目部人员")) {
			try {
				Persondb pdb = loginDAO.QueryPersondb(userid);
				System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!");
				// System.out.println(testuser);
				System.out.println(name);
				System.out.println(pdb.getIdcard());
				if (pdb.getUserPwd()
						.equals(enc.encode(DESEncryptCoder.encrypt(OldSc.getBytes(), DESEncryptCoder.KEY)))) {
					if (NewSc.equals(DNewSc)) {
						NewSc = enc.encode(DESEncryptCoder.encrypt(NewSc.getBytes(), DESEncryptCoder.KEY));

						loginDAO.setPersondbPwd(NewSc, userid);
						json = "{\"success\":\"true\",\"msg\":\"修改成功！\"}";
					} else {
						json = "{\"failure\":\"true\",\"msg\":\"两次输入不一致！\"}";
					}
				} else {
					json = "{\"failure\":\"true\",\"msg\":\"密码错误！\"}";
				}

				PrintWriter out = response.getWriter();
				out.write(json);
				out.flush();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
				// return ERROR;
			}
		} else {
			try {
				Person pdb = loginDAO.QueryPerson(userid);
				System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!");
				// System.out.println(testuser);
				System.out.println(name);
				System.out.println(pdb.getIdentityNo());
				if (pdb.getUserPwd()
						.equals(enc.encode(DESEncryptCoder.encrypt(OldSc.getBytes(), DESEncryptCoder.KEY)))) {
					if (NewSc.equals(DNewSc)) {
						NewSc = enc.encode(DESEncryptCoder.encrypt(NewSc.getBytes(), DESEncryptCoder.KEY));

						loginDAO.setPersonPwd(NewSc, userid);
						json = "{\"success\":\"true\",\"msg\":\"修改成功！\"}";
					} else {
						json = "{\"failure\":\"true\",\"msg\":\"两次输入不一致！\"}";
					}
				} else {
					json = "{\"failure\":\"true\",\"msg\":\"密码错误！\"}";
				}

				PrintWriter out = response.getWriter();
				out.write(json);
				out.flush();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
				// return ERROR;
			}
		}
		// return SUCCESS;
	}

	public void DestroySession() {
		HttpServletRequest request = ServletActionContext.getRequest();
		// HttpServletResponse response = ServletActionContext.getResponse();
		HttpSession hts = request.getSession();
		hts.invalidate();
	}

	public void getManagerNameList() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String jsonStr = basicInfoService.getManagerNameList();
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
		// return null;
	}

	// *************Saftyproblem****************
	public String getSaftyproblemListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			// type = request.getParameter("type");
			String jsonStr = basicInfoService.getSaftyproblemList("", start, limit);
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

	public String getSaftyproblemListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String jsonStr = basicInfoService.getSaftyproblemList(findstr, start, limit);
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

	public Saftyproblem getSaftyproblem() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();

		Saftyproblem saftyproblem = new Saftyproblem();
		saftyproblem.setKind(request.getParameter("kind") == null ? "" : request.getParameter("kind"));
		saftyproblem.setSubkind(request.getParameter("subkind") == null ? "" : request.getParameter("subkind"));
		saftyproblem.setShowkind(request.getParameter("showkind") == null ? "" : request.getParameter("showkind"));
		saftyproblem.setScore(
				Integer.parseInt(request.getParameter("score") == null ? "0" : request.getParameter("score")));
		System.out.println(saftyproblem.toString());
		return saftyproblem;
	}

	public String addSaftyproblem() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");

		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Saftyproblem s = getSaftyproblem();

			String jsonStr = basicInfoService.addSaftyproblem(s, fileName, rootPath);
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

	public String editSaftyproblem() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Saftyproblem s = getSaftyproblem();
			s.setId(Integer.parseInt(request.getParameter("ID")));
			s.setAccessory(request.getParameter("Accessory"));
			String jsonStr = basicInfoService.editSaftyproblem(s, fileName, rootPath);
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

	public String deleteSaftyproblem() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		System.out.println("-------" + ID);
		try {
			String jsonStr = basicInfoService.deleteSaftyproblem(ID);
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

	// *************Koufen****************
	public String getKoufenListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			// type = request.getParameter("type");
			String jsonStr = basicInfoService.getKoufenList("", start, limit);
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

	public String getKoufenListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String jsonStr = basicInfoService.getKoufenList(findstr, start, limit);
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

	public Koufen getKoufen() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Koufen koufen = new Koufen();
		koufen.setKoufenx(request.getParameter("koufenx"));
		koufen.setKoufenzhi(request.getParameter("koufenzhi"));
		koufen.setKoufenzq(request.getParameter("koufenzq"));
		koufen.setKaoheqidian(request.getParameter("kaoheqidian"));

		System.out.println(request.getParameter("kaoheqidian") + "*--------------------------");
		koufen.setDuiykub(request.getParameter("duiykub"));
		koufen.setDuiyzid(request.getParameter("duiyzid"));
		koufen.setBaohfile(request.getParameter("baohfile"));
		return koufen;
	}

	public String addKoufen() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");

		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Koufen s = getKoufen();

			String jsonStr = basicInfoService.addKoufen(s, fileName, rootPath);
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

	public String editKoufen() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Koufen s = getKoufen();
			s.setId(Integer.parseInt(request.getParameter("ID")));
			s.setAccessory(request.getParameter("Accessory"));
			String jsonStr = basicInfoService.editKoufen(s, fileName, rootPath);
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

	public String deleteKoufen() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		System.out.println("-------" + ID);
		try {
			String jsonStr = basicInfoService.deleteKoufen(ID);
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

	// *************Koufentongji****************
	public String getKoufentongjiListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			// type = request.getParameter("type");
			String jsonStr = "";
			String name = request.getParameter("userName");
			String role = request.getParameter("userRole");
			List<String> projectNameList = new ArrayList<>();
			String date = request.getParameter("date");
			String startDate = request.getParameter("startDate");
			String endDate = request.getParameter("endDate");
			if (role.equals("项目部人员")) {
				projectNameList = basicInfoDAO.getPersondbProjectName(name);
				System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAA测试测试测试！！！！！！！！！！");
				for(int i=0;i<projectNameList.size();i++)
					System.out.println(projectNameList.get(i));
				//System.out.println(jsonStr);
				jsonStr = basicInfoService.getKoufentongjiList("", start, limit, date,startDate,endDate,projectNameList);
			} else {
				jsonStr = basicInfoService.getKoufentongjiList("", start, limit, date,startDate,endDate,new ArrayList<String>());
			}
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

	public String getKoufentongjiListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String jsonStr = "";
			String name = request.getParameter("userName");
			String role = request.getParameter("userRole");
			List<String> projectNameList = new ArrayList<>();
			String date = request.getParameter("date");
			String startDate = request.getParameter("startDate");
			String endDate = request.getParameter("endDate");
			if (role.equals("项目部人员")) {
				projectNameList = basicInfoDAO.getPersondbProjectName(name);
				System.out.println("测试测试测试！！！！！！！！！！");
				//System.out.println(jsonStr);
				jsonStr = basicInfoService.getKoufentongjiList(findstr, start, limit, date,startDate,endDate,projectNameList);
			} else {
				jsonStr = basicInfoService.getKoufentongjiList(findstr, start, limit, date,startDate,endDate,new ArrayList<String>());
			}
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

	public Koufentongji getKoufentongji() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Koufentongji koufentongji = new Koufentongji();
		koufentongji.setProname(request.getParameter("proname"));
		koufentongji.setKoufenzhi(Integer.parseInt(request.getParameter("koufenzhi")));
		koufentongji.setZongfen(Integer.parseInt(request.getParameter("zongfen")));

		koufentongji.setKoufenitem(request.getParameter("koufenitem").replace("\n", ""));
		if (koufentongji.getKoufenitem().equals("不超过500个字符")) {
			koufentongji.setKoufenitem("");
		}
		return koufentongji;
	}

	public String addKoufentongji() {
		HttpServletResponse response = ServletActionContext.getResponse();
		//HttpServletRequest request = ServletActionContext.getRequest();
		// String fileName = request.getParameter("fileName");

		// String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Koufentongji s = getKoufentongji();

			String jsonStr = basicInfoService.addKoufentongji(s);
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

	public String editKoufentongji() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		// String fileName = request.getParameter("fileName");
		// String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Koufentongji s = getKoufentongji();
			s.setId(Integer.parseInt(request.getParameter("ID")));
			// s.setAccessory(request.getParameter("Accessory"));
			String jsonStr = basicInfoService.editkoufentongji(s);
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

	public String deletekoufentongji() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		System.out.println("-------" + ID);
		try {
			String jsonStr = basicInfoService.deleteKoufentongji(ID);
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
	
	//*****************yangtong*********2017.9.25**************************
		//   Kaoheresult
		public String getKaoheresultListDef() {
			HttpServletResponse response = ServletActionContext.getResponse();
			HttpServletRequest request = ServletActionContext.getRequest();
			try {
				String projectName = request.getParameter("projectName");
				String jsonStr = basicInfoService.getKaoheresultList("", start, limit, projectName);
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

		public String getKaoheresultListSearch() {
			HttpServletResponse response = ServletActionContext.getResponse();
			HttpServletRequest request = ServletActionContext.getRequest();
			try {
				String findstr = request.getParameter("findStr");
				String projectName = request.getParameter("projectName");
				String jsonStr = basicInfoService.getKaoheresultList(findstr, start, limit, projectName);
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

		
		public Kaoheresult getKaoheresult() throws Exception {
			HttpServletRequest request = ServletActionContext.getRequest();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String dateStr = sdf.format(new Date());

//			java.sql.Date safeptime = java.sql.Date.valueOf(request.getParameter("safeptime"));
			Kaoheresult kaoheresult = new Kaoheresult();
			kaoheresult.setYear(Integer.parseInt(request.getParameter("year")));
			kaoheresult.setMonth(request.getParameter("month"));
			kaoheresult.setScore(Integer.parseInt(request.getParameter("score")));
			kaoheresult.setReason(request.getParameter("reason"));
			kaoheresult.setProjectName(request.getParameter("ProjectName"));

			return kaoheresult;
		}

		public String addKaoheresult() {
			HttpServletResponse response = ServletActionContext.getResponse();
			HttpServletRequest request = ServletActionContext.getRequest();
			String fileName = request.getParameter("fileName");
			String rootPath = request.getRealPath("/") + "upload\\";
			try {
				Kaoheresult s = getKaoheresult();
				String jsonStr = basicInfoService.addKaoheresult(s, fileName, rootPath);
//				String OptNote = "添加了编制人为" + s.getBzperson() + "的安全评估";
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

		public String editKaoheresult() {
			HttpServletResponse response = ServletActionContext.getResponse();
			HttpServletRequest request = ServletActionContext.getRequest();
			String fileName = request.getParameter("fileName");
			// String fileName2 = request.getParameter("fileName2");
			String rootPath = request.getRealPath("/") + "upload\\";
			try {
				Kaoheresult s = getKaoheresult();
				s.setId(Integer.parseInt(request.getParameter("ID")));
				s.setAccessory(request.getParameter("Accessory"));
				String jsonStr = basicInfoService.editKaoheresult(s, fileName, rootPath);
//				String OptNote = "添加了编制人为" + s.getBzperson() + "的安全评估";
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

		public String deleteKaoheresult() {
			HttpServletResponse response = ServletActionContext.getResponse();
			HttpServletRequest request = ServletActionContext.getRequest();
			String ID = request.getParameter("id");
			System.out.println("-------" + ID);
			try {
				String jsonStr = basicInfoService.deleteKaoheresult(ID);
				String OptNote = "删除了ID为" + ID + "的安全评估";
				InsertLog.InsertOptLog(OptNote);
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
