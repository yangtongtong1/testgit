package PSM.Action;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

import PSM.DAO.OperationControlDAO;
import PSM.Service.OperationControlService;
import hibernate.Schemeimple;
import hibernate.SecureJobSlip;
import hibernate.FireSafety;
import hibernate.Projectperson;
import hibernate.Safeprojd;
import hibernate.Safepromonitor;
import hibernate.Safetyprotocal;
import hibernate.TransportSafety;
import hibernate.PODgoods;
import hibernate.Proapproval;
import hibernate.Project;
import hibernate.ODHequipment;
import hibernate.Occuevaluation;
import hibernate.Occumonitor;
import hibernate.Otherjob;
import hibernate.Constructdesign;
import hibernate.Constructionelec;
import hibernate.EnviromentPro;
import hibernate.Fbtestjudge;
import hibernate.Feemanagement;
import hibernate.SaveEnergy;
import hibernate.Saveprodbook;
import hibernate.SecuritySymbol;
import hibernate.Securityplan;
import hibernate.Tezhongpeople;
import hibernate.Tezhongsbpeople;
import hibernate.Traintable;

public class OperationControlAction extends ActionSupport {
	private int start; // 分页查询参
	private int limit; // 分页查询参数
	private OperationControlService operationControlService;
	private OperationControlDAO operationControlDAO;

	public OperationControlDAO getOperationControlDAO() {
		return operationControlDAO;
	}

	public void setOperationControlDAO(OperationControlDAO operationControlDAO) {
		this.operationControlDAO = operationControlDAO;
	}

	private String type;

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

	public OperationControlService getOperationControlService() {
		return operationControlService;
	}

	public void setOperationControlService(OperationControlService operationControlService) {
		this.operationControlService = operationControlService;
	}

	public OperationControlAction() {

	}

	public String execute() throws Exception {
		return SUCCESS;
	}

	public void getSecureJobSlipListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = operationControlService.getSecureJobSlipList("", start, limit, projectName);
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

	public void getSecureJobSlipListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = operationControlService.getSecureJobSlipList(findstr, start, limit, projectName);
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

	public SecureJobSlip getSecureJobSlip() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		SecureJobSlip pro = new SecureJobSlip();
		pro.setWorkPlace(request.getParameter("WorkPlace"));
		pro.setWorkContent(request.getParameter("WorkContent"));
		pro.setWorkTime(request.getParameter("WorkTime"));
		pro.setDangerSource(request.getParameter("DangerSource"));
		pro.setPrinciple(request.getParameter("Principle"));
		pro.setWorkerNum(request.getParameter("WorkerNum"));
		pro.setProjectName(request.getParameter("ProjectName"));
		return pro;
	}

	public void addSecureJobSlip() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			SecureJobSlip pro = getSecureJobSlip();

			String jsonStr = operationControlService.addSecureJobSlip(pro, fileName, rootPath);
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

	public void editSecureJobSlip() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			SecureJobSlip pro = getSecureJobSlip();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));

			String jsonStr = operationControlService.editSecureJobSlip(pro, fileName, rootPath);
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

	public void deleteSecureJobSlip() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {

			String jsonStr = operationControlService.deleteSecureJobSlip(ID, rootPath);
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

	// jianglf---------------------------------------------------------------------
	public void getFeemanagementListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			type = request.getParameter("Type");
			System.out.println(type);
			String jsonStr = operationControlService.getFeemanagementList("", type, start, limit);

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

	public void getFeemanagementListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			type = request.getParameter("Type");
			String jsonStr = operationControlService.getFeemanagementList(findstr, type, start, limit);
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

	public Feemanagement getFeemanagement() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		Feemanagement pro = new Feemanagement();
		pro.setTitle(type);
		pro.setTypei(request.getParameter("Typei"));
		pro.setUnit(request.getParameter("Unit"));
		pro.setFee(request.getParameter("Fee"));
		pro.setUsea(request.getParameter("Usea"));
		pro.setJperson(request.getParameter("Jperson"));
		pro.setDperson(request.getParameter("Dperson"));
		pro.setContent(request.getParameter("Content"));

		Date Time = new Date();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		if (request.getParameter("Time") != null) {
			pro.setTime(sdf1.parse(request.getParameter("Time")));
		}
		;

		pro.setAccessory(request.getParameter("Accessory"));

		return pro;
	}

	public void addFeemanagement() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");

		String[] fileSplit = fileName.split("\\*");

		System.out.println("下面输入文件名了！！！！！！！！！！！！！！");
		System.out.println(Arrays.toString(fileSplit));
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			for (int i = 0; i < fileSplit.length; i++) {
				Feemanagement pro = getFeemanagement();

				String jsonStr = operationControlService.addFeemanagement(pro, fileSplit[i], rootPath);
				System.out.println(jsonStr);
				response.setCharacterEncoding("UTF-8");
				PrintWriter out = response.getWriter();
				out.write(jsonStr);
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public void editFeemanagement() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Feemanagement pro = getFeemanagement();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));

			String jsonStr = operationControlService.editFeemanagement(pro, fileName, rootPath);
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

	public void deleteFeemanagement() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {

			String jsonStr = operationControlService.deleteFeemanagement(ID, rootPath);
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

	// end-----------------------------------------------------------------------
	public void getSchemeimpleListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = operationControlService.getSchemeimpleList("", start, limit, projectName);
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

	public void getSchemeimpleListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = operationControlService.getSchemeimpleList(findstr, start, limit, projectName);
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

	public Schemeimple getSchemeimple() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Schemeimple pro = new Schemeimple();
		pro.setNo(request.getParameter("No"));
		pro.setSchemeName(request.getParameter("SchemeName"));
		pro.setSupervise(request.getParameter("Supervise"));
		pro.setAcceptance(request.getParameter("Acceptance"));
		pro.setJd(request.getParameter("Jd"));
		pro.setAccessory(request.getParameter("Accessory"));
		pro.setProjectName(request.getParameter("ProjectName"));

		return pro;
	}

	public void addSchemeimple() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Schemeimple pro = getSchemeimple();

			String jsonStr = operationControlService.addSchemeimple(pro, fileName, rootPath);
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

	public void editSchemeimple() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Schemeimple pro = getSchemeimple();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));

			String jsonStr = operationControlService.editSchemeimple(pro, fileName, rootPath);
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

	public void deleteSchemeimple() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {

			String jsonStr = operationControlService.deleteSchemeimple(ID, rootPath);
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

	public void getFireSafetyListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = operationControlService.getFireSafetyList("", start, limit, projectName);
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

	public void getFireSafetyListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = operationControlService.getFireSafetyList(findstr, start, limit, projectName);
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

	public FireSafety getFireSafety() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		FireSafety pro = new FireSafety();
		pro.setName(request.getParameter("Name"));
		pro.setModel(request.getParameter("Model"));
		pro.setNum(request.getParameter("Num"));
		pro.setAcqDate(request.getParameter("AcqDate"));
		pro.setChangeDate(request.getParameter("ChangeDate"));
		pro.setChargePerson(request.getParameter("ChargePerson"));
		pro.setCheckPeriodically(request.getParameter("CheckPeriodically"));
		pro.setCheckResult(request.getParameter("CheckResult"));
		pro.setDepartment(request.getParameter("Department"));
		pro.setPlace(request.getParameter("Place"));
		pro.setProjectName(request.getParameter("ProjectName"));

		return pro;
	}

	public void addFireSafety() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			FireSafety pro = getFireSafety();

			String jsonStr = operationControlService.addFireSafety(pro);
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

	public void editFireSafety() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			FireSafety pro = getFireSafety();
			pro.setId(Integer.parseInt(request.getParameter("ID")));

			String jsonStr = operationControlService.editFireSafety(pro);
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

	public void deleteFireSafety() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		try {

			String jsonStr = operationControlService.deleteFireSafety(ID);
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

	public void getTransportSafetyListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = operationControlService.getTransportSafetyList("", start, limit, projectName);
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

	public void getTransportSafetyListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = operationControlService.getTransportSafetyList(findstr, start, limit, projectName);
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

	public TransportSafety getTransportSafety() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		TransportSafety pro = new TransportSafety();
		pro.setCarNum(request.getParameter("CarNum"));
		pro.setCarName(request.getParameter("CarName"));
		pro.setDepartment(request.getParameter("Department"));
		pro.setDriver(request.getParameter("Driver"));
		pro.setDriverNum(request.getParameter("DriverNum"));
		pro.setLicense(request.getParameter("License"));
		pro.setMaintenance(request.getParameter("Maintenance"));
		pro.setProjectName(request.getParameter("ProjectName"));

		return pro;
	}

	public void addTransportSafety() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			TransportSafety pro = getTransportSafety();

			String jsonStr = operationControlService.addTransportSafety(pro);
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

	public void editTransportSafety() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			TransportSafety pro = getTransportSafety();
			pro.setId(Integer.parseInt(request.getParameter("ID")));

			String jsonStr = operationControlService.editTransportSafety(pro);
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

	public void deleteTransportSafety() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		try {

			String jsonStr = operationControlService.deleteTransportSafety(ID);
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

	public void getPODgoodsListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = operationControlService.getPODgoodsList("", start, limit, projectName);
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

	public void getPODgoodsListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = operationControlService.getPODgoodsList(findstr, start, limit, projectName);
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

	public PODgoods getPODgoods() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		PODgoods pro = new PODgoods();
		pro.setName(request.getParameter("Name"));
		pro.setDeliveryTime(request.getParameter("DeliveryTime"));
		pro.setNum(request.getParameter("Num"));
		pro.setPersonName(request.getParameter("PersonName"));
		pro.setDepartment(request.getParameter("Department"));
		pro.setTimeLimit(request.getParameter("TimeLimit"));
		pro.setAutograph(request.getParameter("Autograph"));
		pro.setComment(request.getParameter("Comment").replace("\n", ""));
		if (pro.getComment().equals("不超过500个字符")) {
			pro.setComment("");
		}
		pro.setProjectName(request.getParameter("ProjectName"));

		return pro;
	}

	public void addPODgoods() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			PODgoods pro = getPODgoods();

			String jsonStr = operationControlService.addPODgoods(pro);
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

	public void editPODgoods() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			PODgoods pro = getPODgoods();
			pro.setId(Integer.parseInt(request.getParameter("ID")));

			String jsonStr = operationControlService.editPODgoods(pro);
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

	public void deletePODgoods() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		try {

			String jsonStr = operationControlService.deletePODgoods(ID);
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

	public void getODHequipmentListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = operationControlService.getODHequipmentList("", start, limit, projectName);
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

	public void getODHequipmentListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = operationControlService.getODHequipmentList(findstr, start, limit, projectName);
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

	public ODHequipment getODHequipment() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		ODHequipment pro = new ODHequipment();
		pro.setName(request.getParameter("Name"));
		pro.setModel(request.getParameter("Model"));
		pro.setDepartment(request.getParameter("Department"));
		pro.setNum(request.getParameter("Num"));
		pro.setBuyTime(request.getParameter("BuyTime"));
		pro.setSerialNumber(request.getParameter("SerialNumber"));
		pro.setPlace(request.getParameter("Place"));
		pro.setResponsibility(request.getParameter("Responsibility"));
		pro.setProjectName(request.getParameter("ProjectName"));
		return pro;
	}

	public void addODHequipment() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			ODHequipment pro = getODHequipment();

			String jsonStr = operationControlService.addODHequipment(pro);
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

	public void editODHequipment() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			ODHequipment pro = getODHequipment();
			pro.setId(Integer.parseInt(request.getParameter("ID")));

			String jsonStr = operationControlService.editODHequipment(pro);
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

	public void deleteODHequipment() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		try {

			String jsonStr = operationControlService.deleteODHequipment(ID);
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

	public void getEnviromentProListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String jsonStr = operationControlService.getEnviromentProList("", start, limit);
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

	public void getEnviromentProListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String jsonStr = operationControlService.getEnviromentProList(findstr, start, limit);
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

	public EnviromentPro getEnviromentPro() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		EnviromentPro pro = new EnviromentPro();
		pro.setName(request.getParameter("Name"));
		pro.setDepartment(request.getParameter("Department"));
		pro.setTime(request.getParameter("Time"));
		pro.setApproval(request.getParameter("Approval"));
		return pro;
	}

	public void addEnviromentPro() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			EnviromentPro pro = getEnviromentPro();

			String jsonStr = operationControlService.addEnviromentPro(pro, fileName, rootPath);
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

	public void editEnviromentPro() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			EnviromentPro pro = getEnviromentPro();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));

			String jsonStr = operationControlService.editEnviromentPro(pro, fileName, rootPath);
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

	public void deleteEnviromentPro() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {

			String jsonStr = operationControlService.deleteEnviromentPro(ID, rootPath);
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

	public void getSaveEnergyListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String jsonStr = operationControlService.getSaveEnergyList("", start, limit);
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

	public void getSaveEnergyListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String jsonStr = operationControlService.getSaveEnergyList(findstr, start, limit);
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

	public SaveEnergy getSaveEnergy() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		SaveEnergy pro = new SaveEnergy();
		pro.setName(request.getParameter("Name"));
		pro.setDepartment(request.getParameter("Department"));
		pro.setTime(request.getParameter("Time"));
		pro.setApproval(request.getParameter("Approval"));
		return pro;
	}

	public void addSaveEnergy() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			SaveEnergy pro = getSaveEnergy();

			String jsonStr = operationControlService.addSaveEnergy(pro, fileName, rootPath);
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

	public void editSaveEnergy() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			SaveEnergy pro = getSaveEnergy();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));

			String jsonStr = operationControlService.editSaveEnergy(pro, fileName, rootPath);
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

	public void deleteSaveEnergy() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {

			String jsonStr = operationControlService.deleteSaveEnergy(ID, rootPath);
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

	// jianglf--------------------------------------
	public void getConstructdesignListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = operationControlService.getConstructdesignList("", start, limit, projectName);
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

	public void getConstructdesignListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = operationControlService.getConstructdesignList(findstr, start, limit, projectName);
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

	public Constructdesign getConstructdesign() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Constructdesign pro = new Constructdesign();
		pro.setType(request.getParameter("Type"));
		pro.setUnit(request.getParameter("Unit"));
		pro.setAccessory(request.getParameter("Accessory"));
		pro.setProjectName(request.getParameter("ProjectName"));
		return pro;
	}

	public void addConstructdesign() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Constructdesign pro = getConstructdesign();

			String jsonStr = operationControlService.addConstructdesign(pro, fileName, rootPath);
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

	public void editConstructdesign() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Constructdesign pro = getConstructdesign();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));

			String jsonStr = operationControlService.editConstructdesign(pro, fileName, rootPath);
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

	public void deleteConstructdesign() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {

			String jsonStr = operationControlService.deleteConstructdesign(ID, rootPath);
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

	public void getProapprovalListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = operationControlService.getProapprovalList("", start, limit, projectName);
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

	public void getProapprovalListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = operationControlService.getProapprovalList(findstr, start, limit, projectName);
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

	public Proapproval getProapproval() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Proapproval pro = new Proapproval();
		pro.setName(request.getParameter("Name"));
		pro.setType(request.getParameter("Type"));
		pro.setUnit(request.getParameter("Unit"));
		pro.setApproval(request.getParameter("Approval"));
		pro.setAccessory(request.getParameter("Accessory"));

		Date Time = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (request.getParameter("Time") != null) {
			pro.setTime(sdf.parse(request.getParameter("Time")));
		}
		pro.setProjectName(request.getParameter("ProjectName"));

		return pro;
	}

	public void addProapproval() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Proapproval pro = getProapproval();

			String jsonStr = operationControlService.addProapproval(pro, fileName, rootPath);
			String name = pro.getName();
			String projectName = pro.getProjectName();
			Schemeimple sch = new Schemeimple();
			sch.setSchemeName(name);
			sch.setProjectName(projectName);
			sch.setAccessory("");
			sch.setJd("");
			sch.setSupervise("");
			sch.setAcceptance("");
			operationControlDAO.insertSchemeimple(sch);
			System.out.println("名字为"+name+"%%%%%%%%%%%%%%%%");
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

	public void editProapproval() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		System.out.println("&&&&&&&&&&&&&&&&名字为");
		try {
			String pName = request.getParameter("pName");
			System.out.println("&&&&&&&&&&&&&&&&pName名字为"+pName);
			
			Proapproval pro = getProapproval();
			
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));
			System.out.println("&&&&&&&&&&&&&&&&新名字为"+request.getParameter("Name"));
			String jsonStr = operationControlService.editProapproval(pro, fileName, rootPath,pName);
			System.out.println(jsonStr);
			System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
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

	public void deleteProapproval() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		System.out.println("rootPath为"+rootPath+"$$$$$$$$$$$$");
		try {

			Proapproval p = operationControlDAO.getRecordById(ID);
			Schemeimple sch = operationControlDAO.getRecordByProapproval(p.getName());
			if(sch != null)
				operationControlDAO.deleteSchemeimple(sch);
			
			String jsonStr = operationControlService.deleteProapproval(ID, rootPath);
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

	public void getOccuevaluationListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = operationControlService.getOccuevaluationList("", start, limit, projectName);
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

	public void getOccuevaluationListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = operationControlService.getOccuevaluationList(findstr, start, limit, projectName);
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

	public Occuevaluation getOccuevaluation() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Occuevaluation pro = new Occuevaluation();
		pro.setNo(request.getParameter("No"));
		pro.setAccessory(request.getParameter("Accessory"));
		Date Date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (request.getParameter("Date") != null) {
			pro.setDate(sdf.parse(request.getParameter("Date")));
		}
		pro.setProjectName(request.getParameter("ProjectName"));

		return pro;
	}

	public void addOccuevaluation() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Occuevaluation pro = getOccuevaluation();

			String jsonStr = operationControlService.addOccuevaluation(pro, fileName, rootPath);
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

	public void editOccuevaluation() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Occuevaluation pro = getOccuevaluation();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));

			String jsonStr = operationControlService.editOccuevaluation(pro, fileName, rootPath);
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

	public void deleteOccuevaluation() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {

			String jsonStr = operationControlService.deleteOccuevaluation(ID, rootPath);
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

	public void getOccumonitorListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = operationControlService.getOccumonitorList("", start, limit, projectName);
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

	public void getOccumonitorListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = operationControlService.getOccumonitorList(findstr, start, limit, projectName);
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

	public Occumonitor getOccumonitor() throws Exception {
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// String dateStr = sdf.format(new Date());
		// System.out.println(dateStr);
		// java.sql.Date date = java.sql.Date.valueOf(dateStr);
		HttpServletRequest request = ServletActionContext.getRequest();
		Occumonitor pro = new Occumonitor();
		pro.setNo(request.getParameter("No"));
		pro.setFactor(request.getParameter("Factor"));
		Date Time = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (request.getParameter("Time") != null) {
			pro.setTime(sdf.parse(request.getParameter("Time")));
		}
		;
		pro.setResult(request.getParameter("Result"));
		pro.setAccessory(request.getParameter("Accessory"));
		pro.setProjectName(request.getParameter("ProjectName"));
		return pro;
	}

	public void addOccumonitor() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Occumonitor pro = getOccumonitor();

			String jsonStr = operationControlService.addOccumonitor(pro, fileName, rootPath);
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

	public void editOccumonitor() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Occumonitor pro = getOccumonitor();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));

			String jsonStr = operationControlService.editOccumonitor(pro, fileName, rootPath);
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

	public void deleteOccumonitor() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {

			String jsonStr = operationControlService.deleteOccumonitor(ID, rootPath);
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

	public void getOtherjobListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = operationControlService.getOtherjobList("", start, limit, projectName);
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

	public void getOtherjobListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = operationControlService.getOtherjobList(findstr, start, limit, projectName);
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

	public Otherjob getOtherjob() throws Exception {
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// String dateStr = sdf.format(new Date());
		// System.out.println(dateStr);
		// java.sql.Date date = java.sql.Date.valueOf(dateStr);
		HttpServletRequest request = ServletActionContext.getRequest();
		Otherjob pro = new Otherjob();
		pro.setName(request.getParameter("Name"));
		pro.setTime(request.getParameter("Time"));
		pro.setUnit(request.getParameter("Unit"));
		pro.setRecord(request.getParameter("Record"));
		pro.setAccessory(request.getParameter("Accessory"));
		pro.setProjectName(request.getParameter("ProjectName"));
		return pro;
	}

	public void addOtherjob() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Otherjob pro = getOtherjob();

			String jsonStr = operationControlService.addOtherjob(pro, fileName, rootPath);
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

	public void editOtherjob() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Otherjob pro = getOtherjob();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));

			String jsonStr = operationControlService.editOtherjob(pro, fileName, rootPath);
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

	public void deleteOtherjob() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {

			String jsonStr = operationControlService.deleteOtherjob(ID, rootPath);
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

	public void getSafepromonitorListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String jsonStr = operationControlService.getSafepromonitorList("", start, limit);
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

	public void getSafepromonitorListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String jsonStr = operationControlService.getSafepromonitorList(findstr, start, limit);
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

	public Safepromonitor getSafepromonitor() throws Exception {
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// String dateStr = sdf.format(new Date());
		// System.out.println(dateStr);
		// java.sql.Date date = java.sql.Date.valueOf(dateStr);
		HttpServletRequest request = ServletActionContext.getRequest();
		Safepromonitor pro = new Safepromonitor();
		pro.setBzperson(request.getParameter("Bzperson"));
		pro.setShperson(request.getParameter("Shperson"));
		pro.setPzperson(request.getParameter("Pzperson"));

		Date Bztime = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (request.getParameter("Bztime") != null) {
			pro.setBztime(sdf.parse(request.getParameter("Bztime")));
		}
		;
		Date Shtime = new Date();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		if (request.getParameter("Shtime") != null) {
			pro.setShtime(sdf1.parse(request.getParameter("Shtime")));
		}
		;
		Date Pztime = new Date();
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		if (request.getParameter("Pztime") != null) {
			pro.setPztime(sdf2.parse(request.getParameter("Pztime")));
		}
		;
		pro.setAccessory(request.getParameter("Accessory"));
		return pro;
	}

	public void addSafepromonitor() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Safepromonitor pro = getSafepromonitor();

			String jsonStr = operationControlService.addSafepromonitor(pro, fileName, rootPath);
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

	public void editSafepromonitor() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Safepromonitor pro = getSafepromonitor();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));

			String jsonStr = operationControlService.editSafepromonitor(pro, fileName, rootPath);
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

	public void deleteSafepromonitor() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {

			String jsonStr = operationControlService.deleteSafepromonitor(ID, rootPath);
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

	public void getSafeprojdListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String jsonStr = operationControlService.getSafeprojdList("", start, limit);
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

	public void getSafeprojdListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String jsonStr = operationControlService.getSafeprojdList(findstr, start, limit);
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

	public Safeprojd getSafeprojd() throws Exception {
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// String dateStr = sdf.format(new Date());
		// System.out.println(dateStr);
		// java.sql.Date date = java.sql.Date.valueOf(dateStr);
		HttpServletRequest request = ServletActionContext.getRequest();
		Safeprojd pro = new Safeprojd();
		pro.setPerson(request.getParameter("Person"));
		pro.setJdperson(request.getParameter("Jdperson"));
		Date Time = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (request.getParameter("Time") != null) {
			pro.setTime(sdf.parse(request.getParameter("Time")));
		}
		;

		pro.setAccessory(request.getParameter("Accessory"));
		return pro;
	}

	public void addSafeprojd() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Safeprojd pro = getSafeprojd();

			String jsonStr = operationControlService.addSafeprojd(pro, fileName, rootPath);
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

	public void editSafeprojd() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Safeprojd pro = getSafeprojd();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));

			String jsonStr = operationControlService.editSafeprojd(pro, fileName, rootPath);
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

	public void deleteSafeprojd() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {

			String jsonStr = operationControlService.deleteSafeprojd(ID, rootPath);
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
	// end------------------------------------------

	// jianglf--------------------------------------------------------------------------
	public void getTezhongsbpeopleListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = operationControlService.getTezhongsbpeopleList("", start, limit, projectName);
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

	public void getTezhongsbpeopleListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = operationControlService.getTezhongsbpeopleList(findstr, start, limit, projectName);
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

	public Tezhongsbpeople getTezhongsbpeople() throws Exception {
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// String dateStr = sdf.format(new Date());
		// System.out.println(dateStr);
		// java.sql.Date date = java.sql.Date.valueOf(dateStr);
		HttpServletRequest request = ServletActionContext.getRequest();
		Tezhongsbpeople pro = new Tezhongsbpeople();
		pro.setNo(request.getParameter("No"));
		pro.setFbName(request.getParameter("FbName"));
		pro.setType(request.getParameter("Type"));
		pro.setProject(request.getParameter("Project"));
		pro.setName(request.getParameter("Name"));
		pro.setGender(request.getParameter("Gender"));
		pro.setCardNo(request.getParameter("CardNo"));
		pro.setUnit(request.getParameter("Unit"));
		pro.setPs(request.getParameter("Ps"));

		Date BeginTime = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (request.getParameter("BeginTime") != null) {
			pro.setBeginTime(sdf.parse(request.getParameter("BeginTime")));
		}
		;

		Date ValidTime = new Date();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		if (request.getParameter("ValidTime") != null) {
			pro.setValidTime(sdf1.parse(request.getParameter("ValidTime")));
		}
		;

		pro.setAccessory(request.getParameter("Accessory"));
		pro.setProjectName(request.getParameter("ProjectName"));
		return pro;
	}

	public void addTezhongsbpeople() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Tezhongsbpeople pro = getTezhongsbpeople();

			/*
			 * List emp = operationControlDAO.getEmployee(213); String temp =
			 * ""; for (int i=0;i<emp.size();i++){
			 * 
			 * temp += emp.get(i); temp += ","; } String[] em = temp.split(",");
			 * int pd=1; for (int i=0;i<=em.length-1;i++) {
			 * if(pro.getName().equals(em[i])) { pd=2; break; } }
			 * 
			 * 
			 * if(pd==2) pro.setPeixun("是"); else pro.setPeixun("否");
			 */

			String jsonStr = operationControlService.addTezhongsbpeople(pro, fileName, rootPath);
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

	public void editTezhongsbpeople() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Tezhongsbpeople pro = getTezhongsbpeople();

			/*
			 * List emp = operationControlDAO.getEmployee(213); String temp =
			 * ""; for (int i=0;i<emp.size();i++){
			 * 
			 * temp += emp.get(i); temp += ","; } String[] em = temp.split(",");
			 * int pd=1; for (int i=0;i<=em.length-1;i++) {
			 * if(pro.getName().equals(em[i])) { pd=2; break; } }
			 * 
			 * 
			 * if(pd==2) pro.setPeixun("是"); else pro.setPeixun("否");
			 */

			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));

			String jsonStr = operationControlService.editTezhongsbpeople(pro, fileName, rootPath);
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

	public void deleteTezhongsbpeople() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {

			String jsonStr = operationControlService.deleteTezhongsbpeople(ID, rootPath);
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

	public void getFbtestjudgeListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = operationControlService.getFbtestjudgeList("", start, limit, projectName);
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

	public void getFbtestjudgeListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = operationControlService.getFbtestjudgeList(findstr, start, limit, projectName);
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

	public Fbtestjudge getFbtestjudge() throws Exception {
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// String dateStr = sdf.format(new Date());
		// System.out.println(dateStr);
		// java.sql.Date date = java.sql.Date.valueOf(dateStr);
		HttpServletRequest request = ServletActionContext.getRequest();
		Fbtestjudge pro = new Fbtestjudge();
		pro.setNo(request.getParameter("No"));
		pro.setTester(request.getParameter("Tester"));
		pro.setResult(request.getParameter("Result"));

		Date Time = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (request.getParameter("Time") != null) {
			pro.setTime(sdf.parse(request.getParameter("Time")));
		}
		pro.setProjectName(request.getParameter("ProjectName"));

		pro.setAccessory(request.getParameter("Accessory"));
		return pro;
	}

	public void addFbtestjudge() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Fbtestjudge pro = getFbtestjudge();

			String jsonStr = operationControlService.addFbtestjudge(pro, fileName, rootPath);
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

	public void editFbtestjudge() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Fbtestjudge pro = getFbtestjudge();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));

			String jsonStr = operationControlService.editFbtestjudge(pro, fileName, rootPath);
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

	public void deleteFbtestjudge() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {

			String jsonStr = operationControlService.deleteFbtestjudge(ID, rootPath);
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

	// end------------------------------------------------------------------------------
	public void getSecuritySymbolListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String jsonStr = operationControlService.getSecuritySymbolList("", start, limit);
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

	public void getSecuritySymbolListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String jsonStr = operationControlService.getSecuritySymbolList(findstr, start, limit);
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

	public SecuritySymbol getSecuritySymbol() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		SecuritySymbol pro = new SecuritySymbol();
		pro.setName(request.getParameter("Name"));
		pro.setNum(request.getParameter("Num"));
		pro.setDepartment(request.getParameter("Department"));
		pro.setInstallTime(request.getParameter("InstallTime"));
		pro.setInstallPlace(request.getParameter("InstallPlace"));
		pro.setResponsibility(request.getParameter("Responsibility"));
		return pro;
	}

	public void addSecuritySymbol() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/")+"upload\\";
		System.out.println("fileName : " + fileName);
		try {
			SecuritySymbol pro = getSecuritySymbol();
			String jsonStr = operationControlService.addSecuritySymbol(pro, fileName, rootPath);
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

	public void editSecuritySymbol() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/")+"upload\\";
		System.out.println("fileName : " + fileName);
		try {
			SecuritySymbol pro = getSecuritySymbol();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			String jsonStr = operationControlService.editSecuritySymbol(pro, fileName, rootPath);
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

	public void deleteSecuritySymbol() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		try {

			String jsonStr = operationControlService.deleteSecuritySymbol(ID);
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

	public void getFileInfo() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		String path = request.getParameter("path");
		String fileName = request.getParameter("name");
		try {
			String filePath = request.getRealPath("/") + "upload\\" + path + "\\" + fileName;
			String fileLength = operationControlService.getFileInfo(filePath);

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
			String jsonStr = operationControlService.deleteAllFile(ppID, fileName, rootPath);
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
			String jsonStr = operationControlService.deleteOneFile(ppID, fileName, rootPath);
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

	// jianglf-----------------------------------------------------------------------------------
	public void getTezhongpeopleListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = operationControlService.getTezhongpeopleList("", start, limit, projectName);

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

	public void getTezhongpeopleListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = operationControlService.getTezhongpeopleList(findstr, start, limit, projectName);
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

	/*
	 * public void getEmployee() { HttpServletResponse response =
	 * ServletActionContext.getResponse(); HttpServletRequest request =
	 * ServletActionContext.getRequest();
	 * 
	 * try { String jsonStr = operationControlService.getEmployee();
	 * System.out.println(jsonStr); response.setCharacterEncoding("UTF-8");
	 * PrintWriter out = response.getWriter(); out.write(jsonStr); out.flush();
	 * out.close(); } catch(Exception e) { e.printStackTrace(); //return ERROR;
	 * } //return SUCCESS; //return null; }
	 */

	public Tezhongpeople getTezhongpeople() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Tezhongpeople pro = new Tezhongpeople();
		pro.setFbName(request.getParameter("FbName"));
		pro.setType(request.getParameter("Type"));
		pro.setName(request.getParameter("Name"));
		pro.setPs(request.getParameter("Ps"));
		pro.setSex(request.getParameter("Sex"));
		pro.setCardName(request.getParameter("CardName"));
		pro.setCardNo(request.getParameter("CardNo"));
		pro.setCardPlace(request.getParameter("CardPlace"));
		pro.setPeixun(request.getParameter("Peixun"));

		Date CardTime = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (request.getParameter("CardTime") != null) {
			pro.setCardTime(sdf.parse(request.getParameter("CardTime")));
		}
		;

		Date UseTime = new Date();
		if (request.getParameter("UseTime") != null) {
			pro.setUseTime(sdf.parse(request.getParameter("UseTime")));
		}
		pro.setProjectName(request.getParameter("ProjectName"));
		return pro;
	}

	public void addTezhongpeople() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Tezhongpeople pro = getTezhongpeople();

			/*
			 * List emp = operationControlDAO.getEmployee(120); String temp =
			 * ""; for (int i=0;i<emp.size();i++){
			 * 
			 * temp += emp.get(i); temp += ","; } String[] em = temp.split(",");
			 * int pd=1; for (int i=0;i<=em.length-1;i++) {
			 * if(pro.getName().equals(em[i])) { pd=2; break; } }
			 * 
			 * 
			 * if(pd==2) pro.setPeixun("是"); else pro.setPeixun("否");
			 */

			String jsonStr = operationControlService.addTezhongpeople(pro, fileName, rootPath);
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

	public void editTezhongpeople() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Tezhongpeople pro = getTezhongpeople();

			/*
			 * List emp = operationControlDAO.getEmployee(120); String temp =
			 * ""; for (int i=0;i<emp.size();i++){
			 * 
			 * temp += emp.get(i); temp += ","; } String[] em = temp.split(",");
			 * int pd=1; for (int i=0;i<=em.length-1;i++) {
			 * if(pro.getName().equals(em[i])) { pd=2; break; } }
			 * 
			 * 
			 * if(pd==2) pro.setPeixun("是"); else pro.setPeixun("否");
			 */

			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));

			String jsonStr = operationControlService.editTezhongpeople(pro, fileName, rootPath);
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

	public void deleteTezhongpeople() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {

			String jsonStr = operationControlService.deleteTezhongpeople(ID, rootPath);
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
	// end-----------------------------------------------------------------------------------------------

	public void getSafetyprotocalListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = operationControlService.getSafetyprotocalList("", start, limit, projectName);
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

	public void getSafetyprotocalListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = operationControlService.getSafetyprotocalList(findstr, start, limit, projectName);
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

	public Safetyprotocal getSafetyprotocal() throws Exception {
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// String dateStr = sdf.format(new Date());
		// System.out.println(dateStr);
		// java.sql.Date date = java.sql.Date.valueOf(dateStr);
		HttpServletRequest request = ServletActionContext.getRequest();
		Safetyprotocal pro = new Safetyprotocal();
		pro.setType(request.getParameter("Type"));
		pro.setName(request.getParameter("Name"));
		pro.setName(request.getParameter("Name"));
		pro.setFbname(request.getParameter("Fbname"));
		pro.setDate(request.getParameter("Date"));
		pro.setAccessory(request.getParameter("Accessory"));
		pro.setProjectName(request.getParameter("ProjectName"));
		return pro;
	}

	public void addSafetyprotocal() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Safetyprotocal pro = getSafetyprotocal();

			String jsonStr = operationControlService.addSafetyprotocal(pro, fileName, rootPath);
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

	public void editSafetyprotocal() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Safetyprotocal pro = getSafetyprotocal();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));

			String jsonStr = operationControlService.editSafetyprotocal(pro, fileName, rootPath);
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

	public void deleteSafetyprotocal() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {

			String jsonStr = operationControlService.deleteSafetyprotocal(ID, rootPath);
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

	public void getConstructionelecListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = operationControlService.getConstructionelecList("", start, limit, projectName);
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

	public void getConstructionelecListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = operationControlService.getConstructionelecList(findstr, start, limit, projectName);
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

	public Constructionelec getConstructionelec() throws Exception {
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// String dateStr = sdf.format(new Date());
		// System.out.println(dateStr);
		// java.sql.Date date = java.sql.Date.valueOf(dateStr);
		HttpServletRequest request = ServletActionContext.getRequest();
		Constructionelec pro = new Constructionelec();
		pro.setType(request.getParameter("Type"));
		pro.setName(request.getParameter("Name"));
		pro.setNum(request.getParameter("Num"));
		pro.setSinglepower(request.getParameter("Singlepower"));
		pro.setSumpower(request.getParameter("Sumpower"));
		pro.setFactory(request.getParameter("Factory"));
		pro.setShiyong(request.getParameter("Shiyong"));
		pro.setOuttime(request.getParameter("Outtime"));
		pro.setIntime(request.getParameter("Intime"));
		pro.setPlantime(request.getParameter("Plantime"));
		pro.setRealtime(request.getParameter("Realtime"));
		pro.setStatus(request.getParameter("Status"));
		pro.setApprove(request.getParameter("Approve"));
		pro.setAccessory(request.getParameter("Accessory"));
		pro.setProjectName(request.getParameter("ProjectName"));
		return pro;
	}

	public void addConstructionelec() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Constructionelec pro = getConstructionelec();

			String jsonStr = operationControlService.addConstructionelec(pro, fileName, rootPath);
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

	public void editConstructionelec() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Constructionelec pro = getConstructionelec();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));

			String jsonStr = operationControlService.editConstructionelec(pro, fileName, rootPath);
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

	public void deleteConstructionelec() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {

			String jsonStr = operationControlService.deleteConstructionelec(ID, rootPath);
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

}
