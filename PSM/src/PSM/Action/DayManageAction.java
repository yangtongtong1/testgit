package PSM.Action;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

import PSM.Service.DayManageService;
import hibernate.Daibanplanmade;
import hibernate.Daibanrecord;
import hibernate.Fingerprint;
import hibernate.Meeting;
import hibernate.Periodreport;
import hibernate.Saftyworkrizhi;

public class DayManageAction extends ActionSupport {
	private int start; // 分页查询参
	private int limit; // 分页查询参数
	private DayManageService dayManageService;
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

	public DayManageService getDayManageService() {
		return dayManageService;
	}

	public void setDayManageService(DayManageService dayManageService) {
		this.dayManageService = dayManageService;
	}

	public DayManageAction() {

	}

	public String execute() throws Exception {
		return SUCCESS;
	}

	public void getFileInfo() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		String path = request.getParameter("path");
		String fileName = request.getParameter("name");
		try {
			String filePath = request.getRealPath("/") + "upload\\" + path + "\\" + fileName;
			String fileLength = dayManageService.getFileInfo(filePath);

			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(fileLength);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			//return ERROR;
		}
		//return SUCCESS;

	}

	public void deleteAllFile() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		String fileName = request.getParameter("fileName");
		String ppID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			String jsonStr = dayManageService.deleteAllFile(ppID, fileName, rootPath);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			//return ERROR;
		}
		//return SUCCESS;
	}

	public void deleteOneFile() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		String fileName = request.getParameter("name");
		String ppID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			String jsonStr = dayManageService.deleteOneFile(ppID, fileName, rootPath);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			//return ERROR;
		}
		//return SUCCESS;
	}

	//指纹考勤
	public void getFingerprintListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String jsonStr = dayManageService.getFingerprintList("", start, limit);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getFingerprintListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String jsonStr = dayManageService.getFingerprintList(findstr, start, limit);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Fingerprint getFingerprint() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Fingerprint pro = new Fingerprint();
		pro.setName(request.getParameter("Name"));
		pro.setIp(request.getParameter("Ip"));
		pro.setPortno(request.getParameter("Portno"));
		
		return pro;
	}

	public void addFingerprint() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			Fingerprint pro = getFingerprint();

			String jsonStr = dayManageService.addFingerprint(pro);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void editFingerprint() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			Fingerprint pro = getFingerprint();
			pro.setId(Integer.parseInt(request.getParameter("ID")));

			String jsonStr = dayManageService.editFingerprint(pro);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteFingerprint() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		System.out.println("-------" + ID);
		try {

			String jsonStr = dayManageService.deleteFingerprint(ID);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getMeetingListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			type = request.getParameter("type");
			String projectName = request.getParameter("projectName");
			String jsonStr = dayManageService.getMeetingList("", type, start, limit,projectName);
			System.out.println(type);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			//return ERROR;
		}
		//return SUCCESS;
	}

	public void getMeetingListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			// String projectNo = request.getParameter("projectNo");
			String projectName = request.getParameter("projectName");
			type = request.getParameter("type");
			String jsonStr = dayManageService.getMeetingList(findstr, type, start, limit,projectName);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			//return ERROR;
		}
		//return SUCCESS;
	}

	public Meeting getMeeting() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Meeting pro = new Meeting();

		pro.setHost(request.getParameter("Host"));
		pro.setParticipants(request.getParameter("Participants"));
		pro.setPlace(request.getParameter("Place"));
		pro.setRecord(request.getParameter("Record"));
		pro.setTopic(request.getParameter("Topic"));
		pro.setType(type);
		pro.setProjectName(request.getParameter("ProjectName"));
		
		Date PapersDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if(request.getParameter("Time")!=null){
			pro.setTime( sdf.parse(request.getParameter("Time")) );
		};

		return pro;
	}

	public void addMeeting() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Meeting pro = getMeeting();

			String jsonStr = dayManageService.addMeeting(pro, fileName, rootPath);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			//return ERROR;
		}
		//return SUCCESS;
	}

	public void editMeeting() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Meeting pro = getMeeting();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));

			String jsonStr = dayManageService.editMeeting(pro, fileName, rootPath);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			//return ERROR;
		}
		//return SUCCESS;
	}

	public void deleteMeeting() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {

			String jsonStr = dayManageService.deleteMeeting(ID, rootPath);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			//return ERROR;
		}
		//return SUCCESS;
	}

	// ******************Daibanplanmade************
	public void getDaibanplanmadeListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = dayManageService.getDaibanplanmadeList("", start, limit,projectName);
			System.out.println(type);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			//return ERROR;
		}
		//return SUCCESS;
	}

	public void getDaibanplanmadeListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			// String projectNo = request.getParameter("projectNo");
			String jsonStr = dayManageService.getDaibanplanmadeList(findstr, start, limit,projectName);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			//return ERROR;
		}
		//return SUCCESS;
	}

	public Daibanplanmade getDaibanplanmade() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Daibanplanmade pro = new Daibanplanmade();
		java.sql.Date bianzhitime = java.sql.Date.valueOf(request.getParameter("bianzhitime"));
		pro.setHoliday(request.getParameter("holiday"));
		pro.setPlanname(request.getParameter("planname"));
		pro.setBianzhitime(bianzhitime);
		pro.setProjectName(request.getParameter("ProjectName"));

		return pro;
	}

	public void addDaibanplanmade() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Daibanplanmade pro = getDaibanplanmade();

			String jsonStr = dayManageService.addDaibanplanmade(pro, fileName, rootPath);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			//return ERROR;
		}
		//return SUCCESS;
	}

	public void editDaibanplanmade() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Daibanplanmade pro = getDaibanplanmade();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));

			String jsonStr = dayManageService.editDaibanplanmade(pro, fileName, rootPath);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			//return ERROR;
		}
		//return SUCCESS;
	}

	public void deleteDaibanplanmade() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {

			String jsonStr = dayManageService.deleteDaibanplanmade(ID, rootPath);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			//return ERROR;
		}
		//return SUCCESS;
	}

	// ******************Daibanrecord************
	public void getDaibanrecordListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = dayManageService.getDaibanrecordList("", start, limit,projectName);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			//return ERROR;
		}
		//return SUCCESS;
	}

	public void getDaibanrecordListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			// String projectNo = request.getParameter("projectNo");
			String jsonStr = dayManageService.getDaibanrecordList(findstr, start, limit,projectName);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			//return ERROR;
		}
		//return SUCCESS;
	}

	public Daibanrecord getDaibanrecord() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Daibanrecord pro = new Daibanrecord();

		
		java.sql.Date ondutytime = java.sql.Date.valueOf(request.getParameter("ondutytime"));
		pro.setHoliday(request.getParameter("holiday"));
		pro.setOndutytime(ondutytime);
		pro.setOndutyperson(request.getParameter("ondutyperson"));
		pro.setNextperson(request.getParameter("nextperson"));
		pro.setProjectName(request.getParameter("ProjectName"));

		return pro;
	}

	public void addDaibanrecord() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Daibanrecord pro = getDaibanrecord();

			String jsonStr = dayManageService.addDaibanrecord(pro, fileName, rootPath);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			//return ERROR;
		}
		//return SUCCESS;
	}

	public void editDaibanrecord() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Daibanrecord pro = getDaibanrecord();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));

			String jsonStr = dayManageService.editDaibanrecord(pro, fileName, rootPath);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			//return ERROR;
		}
		//return SUCCESS;
	}

	public void deleteDaibanrecord() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		System.out.println("-------" + ID);
		try {
			String jsonStr = dayManageService.deleteDaibanrecord(ID);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			//return ERROR;
		}
		//return SUCCESS;
	}

	// ******************Saftyworkrizhi************
	public void getSaftyworkrizhiListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = dayManageService.getSaftyworkrizhiList("", start, limit,projectName);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			//return ERROR;
		}
		//return SUCCESS;
	}

	public void getSaftyworkrizhiListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			// String projectNo = request.getParameter("projectNo");
			String jsonStr = dayManageService.getSaftyworkrizhiList(findstr, start, limit,projectName);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			//return ERROR;
		}
		//return SUCCESS;
	}

	public Saftyworkrizhi getSaftyworkrizhi() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Saftyworkrizhi pro = new Saftyworkrizhi();
		java.sql.Date rizhitime = java.sql.Date.valueOf(request.getParameter("rizhitime"));
		pro.setRizhitime(rizhitime);
		pro.setWeekday(request.getParameter("weekday"));
		pro.setQixiday(request.getParameter("qixiday"));
		pro.setQixinight(request.getParameter("qixinight"));
		pro.setDegreeday(request.getParameter("degreeday"));
		pro.setDegreenight(request.getParameter("degreenight"));
		pro.setWindday(request.getParameter("windday"));
		pro.setWindnight(request.getParameter("windnight"));
		pro.setProjectName(request.getParameter("ProjectName"));

		String rizhi = (String) request.getParameter("rizhi").replace("\n", "<br>").replace("\r", "<br>").replace("\"", "\'");
		if (rizhi.equals("不超过1000个字符") || rizhi == "" || rizhi == null) {
			pro.setRizhi("");
		} else {
			pro.setRizhi(rizhi);
		}

		String workplan = (String) request.getParameter("workplan").replace("\n", "<br>").replace("\r", "<br>").replace("\"", "\'");
		if (workplan.equals("不超过500个字符") || workplan == "" || workplan == null) {
			pro.setWorkplan("");
		} else {
			pro.setWorkplan(workplan);
		}

		return pro;
	}

	public void addSaftyworkrizhi() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Saftyworkrizhi pro = getSaftyworkrizhi();

			String jsonStr = dayManageService.addSaftyworkrizhi(pro, fileName, rootPath);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			//return ERROR;
		}
		//return SUCCESS;
	}

	public void editSaftyworkrizhi() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Saftyworkrizhi pro = getSaftyworkrizhi();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));

			String jsonStr = dayManageService.editSaftyworkrizhi(pro, fileName, rootPath);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			//return ERROR;
		}
		//return SUCCESS;
	}

	public void deleteSaftyworkrizhi() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		System.out.println("-------" + ID);
		try {
			String jsonStr = dayManageService.deleteSaftyworkrizhi(ID);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			//return ERROR;
		}
		//return SUCCESS;
	}
	
	private void outputJSON(HttpServletResponse response, String jsonStr) throws Exception {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=utf-8");
		PrintWriter out = response.getWriter();
		System.out.println(jsonStr);
		out.write(jsonStr);
		out.flush();
		out.close();
	}
	

	
	public String getPeriodreportListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String type = request.getParameter("type");
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = dayManageService.getPeriodreportList("", start, limit, type,projectName);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String getPeriodreportListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String type = request.getParameter("type");
			String jsonStr = dayManageService.getPeriodreportList(findstr, start, limit, type,projectName);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public Periodreport getPeriodreport() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Periodreport pro = new Periodreport();
		pro.setYear(Integer.parseInt(request.getParameter("Year")));
		pro.setMonth(Integer.parseInt(request.getParameter("Month")));
		pro.setType(request.getParameter("type"));
		pro.setProjectName(request.getParameter("ProjectName"));
		pro.setTime(new Date(System.currentTimeMillis()));
		if(request.getParameter("type").equals("安全生产管理信息月报表")||request.getParameter("type").equals("事故隐患排查治理台账")||request.getParameter("type").equals("企业职工伤亡事故月报表"))
		{
		pro.setWeek(0);
		}
		else
		{
		pro.setWeek(Integer.parseInt(request.getParameter("Week")));
		}
		return pro;
	}
	
	public String addPeriodreport() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/")+"upload\\";
		try {
			Periodreport pro = getPeriodreport();			
			String jsonStr = dayManageService.addPeriodreport(pro, fileName, rootPath);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String editPeriodreport() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/")+"upload\\";
		try {
			Periodreport pro = getPeriodreport();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));			
			String jsonStr = dayManageService.editPeriodreport(pro, fileName, rootPath);
			outputJSON(response, jsonStr);
		} catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String deletePeriodreport() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/")+"upload\\";
		try {			
			String jsonStr = dayManageService.deletePeriodreport(ID, rootPath);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String getZiliaoTongJi() {
		HttpServletResponse response = ServletActionContext.getResponse();
		//HttpServletRequest request = ServletActionContext.getRequest();
		//String type = request.getParameter("type");
		try {
			//String projectName = request.getParameter("projectName");
			String jsonStr = dayManageService.getZiliaoTongJi(start, limit);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public void getAttLogListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String IPAddress = request.getParameter("IPAddress");
			String portStr = request.getParameter("port");
			int port = Integer.parseInt(portStr);
			System.out.println("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB");
			System.out.println(IPAddress + "|||||||" + port);
			String jsonStr = dayManageService.getAttLogList(IPAddress, port);
			//System.out.println(type);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			//return ERROR;
		}
	}
	
	public void getAttLogListSearchByID() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			int id = Integer.parseInt(request.getParameter("id"));
			String jsonStr = dayManageService.getAttLogList(id);
			//System.out.println(type);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			//return ERROR;
		}
	}
	
	public void getUserInfoListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String IPAddress = request.getParameter("IPAddress");
			String portStr = request.getParameter("port");
			int port = Integer.parseInt(portStr);
			
			System.out.println("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB");
			System.out.println(IPAddress + "|||||||" + port);
			
			
			
			String jsonStr = dayManageService.getUserInfoList(IPAddress, port);
			//System.out.println(type);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			//return ERROR;
		}
	}
	
	public void synZhiWen() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String IPAddress = request.getParameter("IPAddress");
			String portStr = request.getParameter("port");
			int port = Integer.parseInt(portStr);
			
			System.out.println("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB");
			System.out.println(IPAddress + "|||||||" + port);
			
			
			
			String jsonStr = dayManageService.synZhiWen(IPAddress, port);
			//System.out.println(type);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			//return ERROR;
		}
	}

}
