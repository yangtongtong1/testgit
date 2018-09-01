package PSM.Action;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

import PSM.Service.GoalDutyService;
import hibernate.Anweihui;
import hibernate.Fbplan;
import hibernate.Fileupload;
import hibernate.Goaldecom;
import hibernate.Monthplan;
import hibernate.Safetypromanagement;
import hibernate.Safetypromanagementfb;
import hibernate.Saveculture;
import hibernate.Saveprodbook;
import hibernate.Saveproduct;
import hibernate.Saveproplan;
import hibernate.Securityplan;
import hibernate.Threeworkplan;
import hibernate.Workplan;
import hibernate.Yearplan;

public class GoalDutyAction extends ActionSupport {
	private int start; // 分页查询参
	private int limit; // 分页查询参数
	private GoalDutyService goalDutyService;
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

	public GoalDutyService getGoalDutyService() {
		return goalDutyService;
	}

	public void setGoalDutyService(GoalDutyService goalDutyService) {
		this.goalDutyService = goalDutyService;
	}

	public GoalDutyAction() {

	}

	public String execute() throws Exception {
		return SUCCESS;
	}

	public void getNodeNameList() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String jsonStr = goalDutyService.getNodeNameList();
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

	public void getNameList() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String nodename = request.getParameter("NodeName");
		System.out.println("---mynodename--" + nodename);
		try {
			String jsonStr = goalDutyService.getNameList(nodename);
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

	public void getPhone() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String nodename = request.getParameter("NodeName");
		String name = request.getParameter("Name");
		try {
			String jsonStr = goalDutyService.getPhone(nodename, name);
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

	public void getFlowNodeList() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String tableid = request.getParameter("tableid");
		String projectNamedummy = request.getParameter("projectName");
		
		String projectName = projectNamedummy.replace("@", "#");
		

		System.out.println("--------------store  " + projectName);
		try {
			String jsonStr = goalDutyService.getFlowNodeList(tableid, projectName);
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

	public void addNode() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String tableid = request.getParameter("tableid");
		String stepNum = request.getParameter("stepNum");
		String fileName = request.getParameter("fileName");
		String duty = request.getParameter("Duty");
		String phone = request.getParameter("Phone");
		String nodeName = request.getParameter("NodeName");
		String realName = request.getParameter("Name");
		String lastNodeName = request.getParameter("lastNodeName");
		String nowNodeName = request.getParameter("nowNodeName");
		String projectName = request.getParameter("ProjectName");
		
		//System.out.println(duty.indexOf("\n"));
		duty = duty.replace("\n", "<br>").replace("\r", "<br>").replace("\"", "\'");

		String rootPath = request.getRealPath("/") + "upload\\";
		System.out.println("--------------add  " + projectName);
		System.out.println("---tableid: " + tableid);
		System.out.println("---stepNum: " + stepNum);
		System.out.println("---lastNodeName: " + lastNodeName);
		try {
			String jsonStr = "";
			if (lastNodeName.equals("未选择节点"))
				jsonStr = goalDutyService.addFirstNode(tableid, stepNum, lastNodeName, nodeName, projectName, realName,
						duty, phone, fileName, rootPath);
			else
				jsonStr = goalDutyService.addNode(tableid, stepNum, lastNodeName, nodeName, projectName, realName, duty,
						phone, fileName, rootPath);
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

	public void deleteNode() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String tableid = request.getParameter("tableid");
		String stepNum = request.getParameter("stepNum");
		String nodeName = request.getParameter("nodeName");
		String projectName = request.getParameter("projectName");

		System.out.println("--------------del  " + projectName);
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			String jsonStr = goalDutyService.deleteNode(tableid, stepNum, nodeName, rootPath, projectName);
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

	public void editNode() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String nodeid = request.getParameter("ID");
		String nodeName = request.getParameter("NodeName");
		String realName = request.getParameter("Name");
		String tableid = request.getParameter("tableid");
		String duty = request.getParameter("Duty");
		String phone = request.getParameter("Phone");
		String projectName = request.getParameter("ProjectName");
		
		duty = duty.replace("\n", "<br>").replace("\r", "<br>").replace("\"", "\'");

		String rootPath = request.getRealPath("/") + "upload\\";
		try {

			String jsonStr = goalDutyService.editNode(fileName, tableid, nodeid, nodeName, realName, duty, phone,
					projectName, rootPath);
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

	public void getFileInfo() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		String path = request.getParameter("path");
		String fileName = request.getParameter("name");
		try {
			String filePath = request.getRealPath("/") + "upload\\" + path + "\\" + fileName;
			String fileLength = goalDutyService.getFileInfo(filePath);

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
		// return null;

	}

	public void deleteAllFile() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		String fileName = request.getParameter("fileName");
		String ppID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			String jsonStr = goalDutyService.deleteAllFile(ppID, fileName, rootPath);
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

	public void deleteOneFile() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		String fileName = request.getParameter("name");
		String ppID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			String jsonStr = goalDutyService.deleteOneFile(ppID, fileName, rootPath);
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

	public void getFileNameList() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		String nodeName = request.getParameter("clicknode");
		String tableid = request.getParameter("tableid");
		String stepNum = request.getParameter("stepNum");
		String projectNamedummy = request.getParameter("projectName");
		
		String projectName = projectNamedummy.replace("@", "#");

		System.out.println("--------------getfilenamelist  " + projectName);
		System.out.println("--nodeName:  " + nodeName);
		System.out.println("--tableid:  " + tableid);
		System.out.println("--stepNum:  " + stepNum);
		try {
			String jsonStr = goalDutyService.getFileNameList(tableid, stepNum, nodeName, projectName);
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

	// end----------------------------------------------------------------------------------------------------
	public void Saveculture() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			type = request.getParameter("type");
			System.out.println(type);
			String jsonStr = goalDutyService.getSaveculture(type);

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

	public void UpdateSaveculture() {

		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			Saveculture s = getSaveculture();
			type = request.getParameter("type");
			System.out.println("类型类型类型类型");
			System.out.println(type);
			System.out.println(type.equals("安全理念"));
			if (type.equals("安全理念")) {
				s.setId(1);
			} else if (type.equals("安全警示语"))
				s.setId(2);
			else if (type.equals("安全承诺"))
				s.setId(3);
			else if (type.equals("安全行为激励"))
				s.setId(4);

			s.setType(type);

			String jsonStr = goalDutyService.UpdateSaveculture(s);
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

	public Saveculture getSaveculture() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Saveculture s = new Saveculture();
		// s.setId(Integer.parseInt(request.getParameter("ID")));
		String temp_Abs = request.getParameter("Content").replace("\n", "<br>").replace(" ", "&nbsp;");
		s.setContent(temp_Abs);

		return s;
	}

	public void getSecurityplanListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			type = request.getParameter("Type");
			System.out.println(type);
			String projectName = request.getParameter("projectName");
			String jsonStr = goalDutyService.getSecurityplanList("", type, start, limit,projectName);

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

	public void getSecurityplanListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			type = request.getParameter("Type");
			String jsonStr = goalDutyService.getSecurityplanList(findstr, type, start, limit,projectName);
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

	public Securityplan getSecurityplan() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		Securityplan pro = new Securityplan();
		pro.setProjectName(request.getParameter("ProjectName"));
		System.out.println(request.getParameter("ProjectName"));
		pro.setTimeyear(request.getParameter("Timeyear"));
		pro.setFname(request.getParameter("Fname"));
		pro.setTitle(type);
		String userName = (String) session.getAttribute("UserName");
		pro.setUserName(userName);
		pro.setTime(new Date(System.currentTimeMillis()));
		return pro;
	}

	public void addSecurityplan() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");

		String[] fileSplit = fileName.split("\\*");

		System.out.println("下面输入文件名了！！！！！！！！！！！！！！");
		System.out.println(Arrays.toString(fileSplit));
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			for (int i = 0; i < fileSplit.length; i++) {
				Securityplan pro = getSecurityplan();

				String jsonStr = goalDutyService.addSecurityplan(pro, fileSplit[i], rootPath);
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

	public void editSecurityplan() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Securityplan pro = getSecurityplan();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));

			String jsonStr = goalDutyService.editSecurityplan(pro, fileName, rootPath);
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

	public void deleteSecurityplan() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {

			String jsonStr = goalDutyService.deleteSecurityplan(ID, rootPath);
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

	public void getAnweihuiListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = goalDutyService.getAnweihuiList("", start, limit, projectName);

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

	public void getAnweihuiListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = goalDutyService.getAnweihuiList(findstr, start, limit, projectName);
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

	public Anweihui getAnweihui() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Anweihui pro = new Anweihui();
		pro.setEsorad(request.getParameter("Esorad"));
		pro.setTime(request.getParameter("Time"));
		pro.setHead(request.getParameter("Head"));
		pro.setViceHead(request.getParameter("ViceHead"));
		pro.setForm(request.getParameter("Form"));
		pro.setAgency(request.getParameter("Agency"));
		pro.setProjectName(request.getParameter("ProjectName"));
		return pro;
	}

	public void addAnweihui() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Anweihui pro = getAnweihui();

			String jsonStr = goalDutyService.addAnweihui(pro, fileName, rootPath);
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

	public void editAnweihui() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Anweihui pro = getAnweihui();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));

			String jsonStr = goalDutyService.editAnweihui(pro, fileName, rootPath);
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

	public void deleteAnweihui() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {

			String jsonStr = goalDutyService.deleteAnweihui(ID, rootPath);
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

	public void getSaveproductListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			type = request.getParameter("type");
			String projectName = request.getParameter("projectName");
			String jsonStr = goalDutyService.getSaveproductList("", type, start, limit, projectName);
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

	public void getSaveproductListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			type = request.getParameter("type");
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = goalDutyService.getSaveproductList(findstr, type, start, limit, projectName);
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

	// jiang----------------------------------------------------
	public Saveproduct getSaveproduct() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Saveproduct pro = new Saveproduct();
		pro.setAcciRate(request.getParameter("AcciRate"));
		pro.setBehave(request.getParameter("Behave"));
		pro.setBottomRate(request.getParameter("BottomRate"));
		pro.setCheckRate(request.getParameter("CheckRate"));
		pro.setDisaster(request.getParameter("Disaster"));
		pro.setEnviPassRate(request.getParameter("EnviPassRate"));
		pro.setEvaluateRate(request.getParameter("EvaluateRate"));
		pro.setExamineRate(request.getParameter("ExamineRate"));
		pro.setFenBaoAcci(request.getParameter("FenBaoAcci"));
		pro.setFireAcci(request.getParameter("FireAcci"));
		pro.setJobEvent(request.getParameter("JobEvent"));
		pro.setPollutEvent(request.getParameter("PollutEvent"));
		pro.setProdAcci(request.getParameter("ProdAcci"));
		pro.setReachRate(request.getParameter("ReachRate"));
		pro.setReformRate(request.getParameter("ReformRate"));
		pro.setSickPassRate(request.getParameter("SickPassRate"));
		pro.setTrainRate(request.getParameter("TrainRate"));
		pro.setWorkAcci(request.getParameter("WorkAcci"));
		pro.setType(request.getParameter("Type"));
		pro.setContrl(request.getParameter("Contrl"));
		pro.setManagement(request.getParameter("Management"));
		pro.setTimeYear(request.getParameter("TimeYear"));
		pro.setProjectName(request.getParameter("ProjectName"));
		return pro;
	}

	// end=------------------------------------------------------------
	public void addSaveproduct() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Saveproduct pro = getSaveproduct();

			String jsonStr = goalDutyService.addSaveproduct(pro, fileName, rootPath);
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

	public void editSaveproduct() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Saveproduct pro = getSaveproduct();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));

			String jsonStr = goalDutyService.editSaveproduct(pro, fileName, rootPath);
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

	public void deleteSaveproduct() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {

			String jsonStr = goalDutyService.deleteSaveproduct(ID, rootPath);
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

	public void getGoaldecomListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = goalDutyService.getGoaldecomList("", start, limit, projectName);

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

	public void getGoaldecomListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = goalDutyService.getGoaldecomList(findstr, start, limit, projectName);
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

	public Goaldecom getGoaldecom() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Goaldecom pro = new Goaldecom();
		pro.setAffair(request.getParameter("Affair"));
		pro.setAgreeOne(request.getParameter("AgreeOne"));
		pro.setBuy(request.getParameter("Buy"));
		pro.setDesign(request.getParameter("Design"));
		pro.setEngineer(request.getParameter("Engineer"));
		pro.setExamineOne(request.getParameter("ExamineOne"));
		pro.setFactor(request.getParameter("Factor"));
		pro.setGoalDecom(request.getParameter("GoalDecom"));
		pro.setIndexValue(request.getParameter("IndexValue"));
		pro.setMakeOne(request.getParameter("MakeOne"));
		pro.setQuaSave(request.getParameter("QuaSave"));
		pro.setSignOne(request.getParameter("SignOne"));
		pro.setTarget(request.getParameter("Target"));

		pro.setContent(request.getParameter("Content"));
		pro.setMvalue(request.getParameter("Mvalue"));
		pro.setMeasure(request.getParameter("Measure"));
		pro.setTime(request.getParameter("Time"));
		pro.setManager(request.getParameter("Manager"));
		pro.setCompleted(request.getParameter("Completed"));
		pro.setProjectName(request.getParameter("ProjectName"));
		return pro;
	}

	public void addGoaldecom() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Goaldecom pro = getGoaldecom();

			String jsonStr = goalDutyService.addGoaldecom(pro, fileName, rootPath);
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

	public void editGoaldecom() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Goaldecom pro = getGoaldecom();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));
			
			String jsonStr = goalDutyService.editGoaldecom(pro, fileName, rootPath);
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

	public void deleteGoaldecom() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {

			String jsonStr = goalDutyService.deleteGoaldecom(ID, rootPath);
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

	public void getSaveproplanListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = goalDutyService.getSaveproplanList("", start, limit, projectName);

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

	public void getSaveproplanListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = goalDutyService.getSaveproplanList(findstr, start, limit, projectName);
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

	public Saveproplan getSaveproplan() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Saveproplan pro = new Saveproplan();

		Date Anweihui = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (request.getParameter("Anweihui") != null) {
			pro.setAnweihui(sdf.parse(request.getParameter("Anweihui")));
		}
		;

		Date ThreeGroup = new Date();
		if (request.getParameter("ThreeGroup") != null) {
			pro.setThreeGroup(sdf.parse(request.getParameter("ThreeGroup")));
		}
		;

		Date FourBuild = new Date();
		if (request.getParameter("FourBuild") != null) {
			pro.setFourBuild(sdf.parse(request.getParameter("FourBuild")));
		}
		;

		Date SaveBuild = new Date();
		if (request.getParameter("SaveBuild") != null) {
			pro.setSaveBuild(sdf.parse(request.getParameter("SaveBuild")));
		}
		;

		Date SavePlan = new Date();
		if (request.getParameter("SavePlan") != null) {
			pro.setSavePlan(sdf.parse(request.getParameter("SavePlan")));
		}
		;

		Date SaveCheck = new Date();
		if (request.getParameter("SaveCheck") != null) {
			pro.setSaveCheck(sdf.parse(request.getParameter("SaveCheck")));
		}
		;

		Date BuildPlan = new Date();
		if (request.getParameter("BuildPlan") != null) {
			pro.setBuildPlan(sdf.parse(request.getParameter("BuildPlan")));
		}
		;

		Date HandlePlan = new Date();
		if (request.getParameter("HandlePlan") != null) {
			pro.setHandlePlan(sdf.parse(request.getParameter("HandlePlan")));
		}
		;

		Date SaveBuildPlan = new Date();
		if (request.getParameter("SaveBuildPlan") != null) {
			pro.setSaveBuildPlan(sdf.parse(request.getParameter("SaveBuildPlan")));
		}
		;

		Date DangerPublic = new Date();
		if (request.getParameter("DangerPublic") != null) {
			pro.setDangerPublic(sdf.parse(request.getParameter("DangerPublic")));
		}
		;

		Date ExecutePlan = new Date();
		if (request.getParameter("ExecutePlan") != null) {
			pro.setExecutePlan(sdf.parse(request.getParameter("ExecutePlan")));
		}
		;

		Date WorkPlan = new Date();
		if (request.getParameter("WorkPlan") != null) {
			pro.setWorkPlan(sdf.parse(request.getParameter("WorkPlan")));
		}
		;

		pro.setProjectName(request.getParameter("ProjectName"));
		return pro;
	}

	public void addSaveproplan() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Saveproplan pro = getSaveproplan();

			String jsonStr = goalDutyService.addSaveproplan(pro, fileName, rootPath);
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

	public void editSaveproplan() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Saveproplan pro = getSaveproplan();
			pro.setId(Integer.parseInt(request.getParameter("ID")));

			String jsonStr = goalDutyService.editSaveproplan(pro, fileName, rootPath);
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

	public void deleteSaveproplan() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {

			String jsonStr = goalDutyService.deleteSaveproplan(ID, rootPath);
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

	public void getSaveprodbookListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = goalDutyService.getSaveprodbookList("", start, limit, projectName);

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

	public void getSaveprodbookListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = goalDutyService.getSaveprodbookList(findstr, start, limit, projectName);
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

	public Saveprodbook getSaveprodbook() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Saveprodbook pro = new Saveprodbook();
		pro.setToTarget(request.getParameter("ToTarget"));
		pro.setType(request.getParameter("Type"));
		pro.setTimeYear(request.getParameter("TimeYear"));
		pro.setAccessory(request.getParameter("Accessory"));
		pro.setProjectName(request.getParameter("ProjectName"));
		return pro;
	}

	public void addSaveprodbook() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Saveprodbook pro = getSaveprodbook();

			String jsonStr = goalDutyService.addSaveprodbook(pro, fileName, rootPath);
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

	public void editSaveprodbook() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Saveprodbook pro = getSaveprodbook();
			pro.setId(Integer.parseInt(request.getParameter("ID")));

			String jsonStr = goalDutyService.editSaveprodbook(pro, fileName, rootPath);
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

	public void deleteSaveprodbook() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {

			String jsonStr = goalDutyService.deleteSaveprodbook(ID, rootPath);
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

	public void getWorkplanListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			type = request.getParameter("type");
			String jsonStr = goalDutyService.getWorkplanList("", type, start, limit);

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

	public void getWorkplanListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			type = request.getParameter("type");
			String findstr = request.getParameter("findStr");
			String jsonStr = goalDutyService.getWorkplanList(findstr, type, start, limit);
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

	public Workplan getWorkplan() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Workplan pro = new Workplan();
		pro.setTaskContent(request.getParameter("TaskContent"));
		pro.setDutyMan(request.getParameter("DutyMan"));

		pro.setDutyMan(request.getParameter("FinishedTask"));
		pro.setTaskScale(request.getParameter("TaskScale"));
		pro.setType(request.getParameter("Type"));

		Date timeYear = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (request.getParameter("TimeYear") != null) {
			pro.setTimeYear(sdf.parse(request.getParameter("TimeYear")));
		}
		;

		Date PlanFinishTime = new Date();

		if (request.getParameter("PlanFinishTime") != null) {
			pro.setPlanFinishTime(sdf.parse(request.getParameter("PlanFinishTime")));
		}
		;

		Date RealFinishTime = new Date();

		if (request.getParameter("RealFinishTime") != null) {
			pro.setRealFinishTime(sdf.parse(request.getParameter("RealFinishTime")));
		}
		;

		return pro;
	}

	public void addWorkplan() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Workplan pro = getWorkplan();

			String jsonStr = goalDutyService.addWorkplan(pro, fileName, rootPath);
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

	public void editWorkplan() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Workplan pro = getWorkplan();
			pro.setId(Integer.parseInt(request.getParameter("ID")));

			String jsonStr = goalDutyService.editWorkplan(pro, fileName, rootPath);
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

	public void deleteWorkplan() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {

			String jsonStr = goalDutyService.deleteWorkplan(ID, rootPath);
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

	public void getYearplanListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			// type = request.getParameter("Type");
			// System.out.println(type);
			String projectName = request.getParameter("projectName");
			String jsonStr = goalDutyService.getYearplanList("", start, limit, projectName);

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

	public void getYearplanListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			// type = request.getParameter("Type");
			String projectName = request.getParameter("projectName");
			String jsonStr = goalDutyService.getYearplanList(findstr, start, limit, projectName);
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

	public Yearplan getYearplan() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Yearplan pro = new Yearplan();

		pro.setWorkload(request.getParameter("Workload"));
		pro.setCompleted(replaceSomeChar(request.getParameter("Completed")));
		
		String Content = (String) request.getParameter("Content").replace("\n", "<br>").replace("\r", "<br>").replace("\"", "\'");
		
		pro.setContent(Content);
		
		pro.setManager(request.getParameter("Manager"));
		pro.setAccessory(request.getParameter("Accessory"));
		pro.setYear(request.getParameter("Year"));
		
		Date PlanDate = new Date();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		if(request.getParameter("PlanDate") == null||request.getParameter("PlanDate").equals("")){
			pro.setPlanDate(null);
		}
		if (request.getParameter("PlanDate") != null) {
			pro.setPlanDate(sdf1.parse(request.getParameter("PlanDate")));
		}

		pro.setRealDate(request.getParameter("RealDate"));
		pro.setProjectName(request.getParameter("ProjectName"));
		return pro;
	}

	public void addYearplan() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");

		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Yearplan pro = getYearplan();

			String jsonStr = goalDutyService.addYearplan(pro, fileName, rootPath);
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

	public void editYearplan() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Yearplan pro = getYearplan();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));

			String jsonStr = goalDutyService.editYearplan(pro, fileName, rootPath);
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

	public void deleteYearplan() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {

			String jsonStr = goalDutyService.deleteYearplan(ID, rootPath);
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

	// jianglf-------------------------------------------------------------------------------------
	public void getSafetypromanagementListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			// type = request.getParameter("Type");
			// System.out.println(type);
			String projectName = request.getParameter("projectName");
			String jsonStr = goalDutyService.getSafetypromanagementList("", start, limit, projectName);

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

	public void getSafetypromanagementListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			// type = request.getParameter("Type");
			String projectName = request.getParameter("projectName");
			String jsonStr = goalDutyService.getSafetypromanagementList(findstr, start, limit, projectName);
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

	public Safetypromanagement getSafetypromanagement() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Safetypromanagement pro = new Safetypromanagement();
		pro.setName(request.getParameter("Name"));

		pro.setPerson(request.getParameter("Person"));
		pro.setSperson(request.getParameter("Sperson"));

		pro.setAccessory(request.getParameter("Accessory"));

		Date Time = new Date();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		if (request.getParameter("Time") != null) {
			pro.setTime(sdf1.parse(request.getParameter("Time")));
		}
		pro.setProjectName(request.getParameter("ProjectName"));

		return pro;
	}

	public void addSafetypromanagement() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");

		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Safetypromanagement pro = getSafetypromanagement();

			String jsonStr = goalDutyService.addSafetypromanagement(pro, fileName, rootPath);
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

	public void editSafetypromanagement() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Safetypromanagement pro = getSafetypromanagement();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));

			String jsonStr = goalDutyService.editSafetypromanagement(pro, fileName, rootPath);
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

	public void deleteSafetypromanagement() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {

			String jsonStr = goalDutyService.deleteSafetypromanagement(ID, rootPath);
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

	public void getSafetypromanagementfbListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			// type = request.getParameter("Type");
			// System.out.println(type);
			String projectName = request.getParameter("projectName");
			String jsonStr = goalDutyService.getSafetypromanagementfbList("", start, limit, projectName);

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

	public void getSafetypromanagementfbListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			// type = request.getParameter("Type");
			String projectName = request.getParameter("projectName");
			String jsonStr = goalDutyService.getSafetypromanagementfbList(findstr, start, limit, projectName);
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

	public Safetypromanagementfb getSafetypromanagementfb() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Safetypromanagementfb pro = new Safetypromanagementfb();
		pro.setName(request.getParameter("Name"));
		pro.setFbName(request.getParameter("FbName"));

		pro.setPerson(request.getParameter("Person"));
		pro.setSperson(request.getParameter("Sperson"));

		pro.setAccessory(request.getParameter("Accessory"));

		Date Time = new Date();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		if (request.getParameter("Time") != null) {
			pro.setTime(sdf1.parse(request.getParameter("Time")));
		}
		;
		pro.setProjectName(request.getParameter("ProjectName"));

		return pro;
	}

	public void addSafetypromanagementfb() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");

		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Safetypromanagementfb pro = getSafetypromanagementfb();

			String jsonStr = goalDutyService.addSafetypromanagementfb(pro, fileName, rootPath);
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

	public void editSafetypromanagementfb() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Safetypromanagementfb pro = getSafetypromanagementfb();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));

			String jsonStr = goalDutyService.editSafetypromanagementfb(pro, fileName, rootPath);
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

	public void deleteSafetypromanagementfb() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {

			String jsonStr = goalDutyService.deleteSafetypromanagementfb(ID, rootPath);
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

	public void getThreeworkplanListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			// type = request.getParameter("Type");
			// System.out.println(type);
			String projectName = request.getParameter("projectName");
			String jsonStr = goalDutyService.getThreeworkplanList("", start, limit,projectName);

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

	public void getThreeworkplanListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			// type = request.getParameter("Type");
			String projectName = request.getParameter("projectName");
			String jsonStr = goalDutyService.getThreeworkplanList(findstr, start, limit,projectName);
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

	public Threeworkplan getThreeworkplan() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Threeworkplan pro = new Threeworkplan();
		pro.setNo(request.getParameter("No"));
		pro.setWorkload(request.getParameter("Workload"));
		pro.setCompleted(request.getParameter("Completed"));
		pro.setContent(request.getParameter("Content"));
		pro.setManager(request.getParameter("Manager"));
		pro.setAccessory(request.getParameter("Accessory"));
		pro.setYear(request.getParameter("Year"));

		Date PlanDate = new Date();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		if (request.getParameter("PlanDate") != null) {
			pro.setPlanDate(sdf1.parse(request.getParameter("PlanDate")));
		}
		;
		pro.setRealDate(request.getParameter("RealDate"));

		/*Date RealDate = new Date();
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		if (request.getParameter("RealDate") != null) {
			pro.setRealDate(sdf2.parse(request.getParameter("RealDate")));
		}
		;*/
		pro.setProjectName(request.getParameter("ProjectName"));
		return pro;
	}

	public void addThreeworkplan() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");

		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Threeworkplan pro = getThreeworkplan();

			String jsonStr = goalDutyService.addThreeworkplan(pro, fileName, rootPath);
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

	public void editThreeworkplan() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Threeworkplan pro = getThreeworkplan();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));

			String jsonStr = goalDutyService.editThreeworkplan(pro, fileName, rootPath);
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

	public void deleteThreeworkplan() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {

			String jsonStr = goalDutyService.deleteThreeworkplan(ID, rootPath);
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
	// end----------------------------------------------------------------------------------------

	public void getMonthplanListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			// type = request.getParameter("Type");
			// System.out.println(type);
			String projectName = request.getParameter("projectName");
			String jsonStr = goalDutyService.getMonthplanList("", start, limit, projectName);

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

	public void getMonthplanListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			// type = request.getParameter("Type");
			String projectName = request.getParameter("projectName");
			String jsonStr = goalDutyService.getMonthplanList(findstr, start, limit, projectName);
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

	public Monthplan getMonthplan() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Monthplan pro = new Monthplan();
		pro.setNo(request.getParameter("No"));
		pro.setWorkload(request.getParameter("Workload"));
		pro.setCompleted(replaceSomeChar(request.getParameter("Completed")));
		
		String Content = (String) request.getParameter("Content").replace("\n", "<br>").replace("\r", "<br>").replace("\"", "\'");
		
		pro.setContent(Content);
		
		pro.setManager(request.getParameter("Manager"));
		pro.setAccessory(request.getParameter("Accessory"));
		pro.setYear(request.getParameter("Year"));
		pro.setMonth(request.getParameter("Month"));
		pro.setUnit(request.getParameter("Unit"));
		
		String Completedsm = (String) request.getParameter("Completedsm").replace("\n", "<br>").replace("\r", "<br>").replace("\"", "\'");
		
		pro.setCompletedsm(Completedsm);
		
		pro.setRealDate(request.getParameter("RealDate"));
		
		Date PlanDate = new Date();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		if (request.getParameter("PlanDate") != null) {
			pro.setPlanDate(sdf1.parse(request.getParameter("PlanDate")));
		}
		;

		/*Date RealDate = new Date();
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		if(request.getParameter("RealDate").equals("")){
			pro.setRealDate(null);
		}
		else{
			pro.setRealDate(sdf2.parse(request.getParameter("RealDate")));
		}
		;*/
		pro.setProjectName(request.getParameter("ProjectName"));
		return pro;
	}

	public void addMonthplan() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");

		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Monthplan pro = getMonthplan();

			String jsonStr = goalDutyService.addMonthplan(pro, fileName, rootPath);
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

	public void editMonthplan() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Monthplan pro = getMonthplan();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));

			String jsonStr = goalDutyService.editMonthplan(pro, fileName, rootPath);
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

	public void deleteMonthplan() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {

			String jsonStr = goalDutyService.deleteMonthplan(ID, rootPath);
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

	public void getFbplanListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			// type = request.getParameter("Type");
			// System.out.println(type);
			String projectName = request.getParameter("projectName");
			String jsonStr = goalDutyService.getFbplanList("", start, limit, projectName);

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

	public void getFbplanListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			// type = request.getParameter("Type");
			String projectName = request.getParameter("projectName");
			String jsonStr = goalDutyService.getFbplanList(findstr, start, limit, projectName);
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

	public Fbplan getFbplan() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Fbplan pro = new Fbplan();
		pro.setNo(request.getParameter("No"));
		pro.setName(request.getParameter("Name"));
		pro.setPlanName(request.getParameter("PlanName"));
		pro.setAccessory(request.getParameter("Accessory"));

		Date Date = new Date();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		if (request.getParameter("Date") != null) {
			pro.setDate(sdf1.parse(request.getParameter("Date")));
		}
		;
		pro.setProjectName(request.getParameter("ProjectName"));
		return pro;
	}

	public void addFbplan() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");

		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Fbplan pro = getFbplan();

			String jsonStr = goalDutyService.addFbplan(pro, fileName, rootPath);
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

	public void editFbplan() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Fbplan pro = getFbplan();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));

			String jsonStr = goalDutyService.editFbplan(pro, fileName, rootPath);
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

	public void deleteFbplan() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {

			String jsonStr = goalDutyService.deleteFbplan(ID, rootPath);
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
	
	public String addFileUpload() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String title = request.getParameter("title");
		String projectName = request.getParameter("projectName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Fileupload pro = new Fileupload();
			
			// 分别获取当前年月日
			Calendar now = Calendar.getInstance();
			
			int year = now.get(Calendar.YEAR);
			int month = now.get(Calendar.MONTH) + 1;
			
			pro.setYear(String.valueOf(year));
			
			if(month < 10)
				pro.setMonth("0" + String.valueOf(month));
			else
				pro.setMonth(String.valueOf(month));
			
			pro.setTitle(title);
			pro.setProjectName(projectName);
			String jsonStr = goalDutyService.addFileUpload(pro, fileName, rootPath, title);
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
	
	public void getFileUploadNameList() {
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String title = request.getParameter("title");
			String projectName = request.getParameter("projectName");
			String jsonStr = goalDutyService.getFileUploadNameList(title,projectName);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		
	}
	
	public void deleteFileUpload() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String file = request.getParameter("file");
		String title = request.getParameter("title");
		String projectName = request.getParameter("projectName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {

			String jsonStr = goalDutyService.deleteFileUpload(file,title ,rootPath,projectName);
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
