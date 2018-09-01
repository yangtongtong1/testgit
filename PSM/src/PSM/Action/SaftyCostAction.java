package PSM.Action;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.CheckboxInterceptor;

import com.opensymphony.xwork2.ActionSupport;
import com.sun.javafx.image.IntPixelGetter;
import com.sun.org.apache.bcel.internal.generic.NEW;

import PSM.Service.SaftyCostService;
import hibernate.Fenbaoplan;
import hibernate.Fenbaosaftyaccounts;
import hibernate.Fenbaosaftycostsum;
import hibernate.Fenbaosaftyjiancha;
import hibernate.Project;
import hibernate.Projectperson;
import hibernate.Saftyaccounts;
import hibernate.Saftycost;
import hibernate.Saftycostplan;
import hibernate.Saftycosttj;
import hibernate.Saftyjiancha;
import hibernate.Sanxiang;
import hibernate.Xiandongtai;
import javafx.scene.chart.PieChart.Data;

public class SaftyCostAction extends ActionSupport {
	private int start; // 分页查询参
	private int limit; // 分页查询参数
	private SaftyCostService saftyCostService;
	private String type;

	LogAction InsertLog = new LogAction();

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

	public SaftyCostService getSaftyCostService() {
		return saftyCostService;
	}

	public void setSaftyCostService(SaftyCostService saftyCostService) {
		this.saftyCostService = saftyCostService;
	}

	public String execute() throws Exception {
		return SUCCESS;
	}

	// saftycost表的请求处理函数*********************************************

	public String getSaftycostListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String jsonStr = saftyCostService.getSaftycostList("", start, limit);
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

	public String getAllSaftycost() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String jsonStr = saftyCostService.getAllSaftycost();
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

	public String getAllSaftycostplan() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String jsonStr = saftyCostService.getAllSaftycostplan();
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

	public String getAllSaftycostcost() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String jsonStr = saftyCostService.getAllSaftycostcost();
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

	public String getSaftycostListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String jsonStr = saftyCostService.getSaftycostList(findstr, start, limit);
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

	public Saftycost getSaftycost() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Saftycost saftycost = new Saftycost();
		saftycost.setSubjectnum(request.getParameter("subjectnum"));
		saftycost.setCostkind(request.getParameter("costkind"));
		saftycost.setCostetails(request.getParameter("costetails"));
		saftycost.setJan(CheckInt(Integer.parseInt(request.getParameter("jan"))));
		saftycost.setFeb(CheckInt(Integer.parseInt(request.getParameter("feb"))));
		saftycost.setMar(CheckInt(Integer.parseInt(request.getParameter("mar"))));
		saftycost.setApr(CheckInt(Integer.parseInt(request.getParameter("apr"))));
		saftycost.setMay(CheckInt(Integer.parseInt(request.getParameter("may"))));
		saftycost.setJune(CheckInt(Integer.parseInt(request.getParameter("june"))));
		saftycost.setJuly(CheckInt(Integer.parseInt(request.getParameter("july"))));
		saftycost.setAug(CheckInt(Integer.parseInt(request.getParameter("aug"))));
		saftycost.setSept(CheckInt(Integer.parseInt(request.getParameter("sept"))));
		saftycost.setOct(CheckInt(Integer.parseInt(request.getParameter("oct"))));
		saftycost.setNov(CheckInt(Integer.parseInt(request.getParameter("nov"))));

		System.out.println(Integer.parseInt(request.getParameter("dec")));
		saftycost.setDec(CheckInt(Integer.parseInt(request.getParameter("dec"))));

		saftycost.setSumcost(Integer.parseInt(request.getParameter("sumcost")));
		saftycost.setPlanorcost(Integer.parseInt(request.getParameter("planorcost")));

		return saftycost;
	}

	public Integer CheckInt(Integer a) {
		if (a == null) {
			return 0;
		}
		return a;
	}

	public String addSaftycostPlan() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		// String fileName = request.getParameter("fileName");
		//
		// String rootPath = request.getRealPath("/")+"upload\\";
		try {
			Saftycost s = getSaftycost();
			s.setPlanorcost(0);

			System.out.println(s.getSubjectnum() + "," + s.getCostetails() + "," + s.getCostkind() + "\n" + s.getFeb());

			String jsonStr = saftyCostService.addSaftycost(s);
			System.out.println(jsonStr);

			String OptNote = "添加了" + s.getSubjectnum() + "项目部安全投入计划";
			InsertLog.InsertOptLog(OptNote);

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

	public String addSaftycostCost() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		// String fileName = request.getParameter("fileName");
		// String rootPath = request.getRealPath("/")+"upload\\";
		try {
			Saftycost s = getSaftycost();
			s.setPlanorcost(1);

			System.out.println(s.getSubjectnum() + "," + s.getCostetails() + "," + s.getCostkind() + "\n" + s.getDec());

			String jsonStr = saftyCostService.addSaftycost(s);

			String OptNote = "添加了" + s.getSubjectnum() + "项目部安全投入计划";
			InsertLog.InsertOptLog(OptNote);

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

	public String editSaftycost() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			Saftycost saftycost = getSaftycost();
			saftycost.setId(Integer.parseInt(request.getParameter("ID")));
			// saftycost.setId(2);
			System.out.println(saftycost.getPlanorcost());

			String jsonStr = saftyCostService.updateSaftycost(saftycost);

			String OptNote = "编辑了" + saftycost.getSubjectnum() + "项目部安全投入计划";
			InsertLog.InsertOptLog(OptNote);

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

	public String deleteSaftycost() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");

		System.out.println("******************" + ID);
		try {

			String jsonStr = saftyCostService.deleteSaftycost(ID);

			String OptNote = "删除了ID为" + ID + "的项目部安全投入计划";
			InsertLog.InsertOptLog(OptNote);

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

	// Saftycosttj表的请求处理函数*************************************************

	public String getSaftycosttjListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String jsonStr = saftyCostService.getSaftycosttjList("", start, limit);
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

	public String getSaftycosttjListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String jsonStr = saftyCostService.getSaftycosttjList(findstr, start, limit);
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

	public Saftycosttj getSaftycosttj() throws Exception {

		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 2016-10-26 15:51:00
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = sdf.format(new Date());
		System.out.println(dateStr);
		java.sql.Date date = java.sql.Date.valueOf(dateStr);

		HttpServletRequest request = ServletActionContext.getRequest();
		Saftycosttj saftycosttj = new Saftycosttj();
		saftycosttj.setSubjectnum(request.getParameter("subjectnum"));
		saftycosttj.setCostkind(request.getParameter("costkind"));
		saftycosttj.setYear(request.getParameter("year"));
		saftycosttj.setCost(request.getParameter("cost"));
		saftycosttj.setCostrealtime(request.getParameter("costrealtime"));

		System.out.println(saftycosttj.toString());
		return saftycosttj;
	}

	public String addSaftycosttj() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Saftycosttj s = getSaftycosttj();

			// System.out.println(s.toString()+"*****************************");

			String jsonStr = saftyCostService.addSaftycosttj(s, fileName, rootPath);

			String OptNote = "添加了" + s.getSubjectnum() + "的费用统计";
			InsertLog.InsertOptLog(OptNote);

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

	public String editSaftycosttj() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Saftycosttj s = getSaftycosttj();
			s.setId(Integer.parseInt(request.getParameter("ID")));
			s.setAccessory(request.getParameter("Accessory"));
			String jsonStr = saftyCostService.editSaftycosttj(s, fileName, rootPath);

			String OptNote = "编辑了" + s.getSubjectnum() + "的安全费用统计";
			InsertLog.InsertOptLog(OptNote);
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

	public String deleteSaftycosttj() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		System.out.println("-------" + ID);
		try {
			String jsonStr = saftyCostService.deleteSaftycosttj(ID);

			String OptNote = "删除了ID为" + ID + "的安全费用统计";
			InsertLog.InsertOptLog(OptNote);

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

	// saftycostplan表的请求处理函数*************************************************

	public String getSaftycostplanListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			// String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = saftyCostService.getSaftycostplanList("", start, limit, projectName);
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

	public String getSaftycostplanListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = saftyCostService.getSaftycostplanList(findstr, start, limit, projectName);
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

	public Saftycostplan getSaftycostplan() throws Exception {

		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 2016-10-26 15:51:00
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = sdf.format(new Date());
		System.out.println(dateStr);
		java.sql.Date date = java.sql.Date.valueOf(dateStr);

		HttpServletRequest request = ServletActionContext.getRequest();
		Saftycostplan saftycostplan = new Saftycostplan();
		// saftycostplan.setSubjectnum(request.getParameter("subjectnum"));
		saftycostplan.setCostkind0(request.getParameter("costkind0"));
		saftycostplan.setCostkind1(request.getParameter("costkind1") == "" ? "0" : request.getParameter("costkind1"));
		saftycostplan.setCostkind2(request.getParameter("costkind2") == "" ? "0" : request.getParameter("costkind2"));
		saftycostplan.setCostkind3(request.getParameter("costkind3") == "" ? "0" : request.getParameter("costkind3"));
		saftycostplan.setCostkind4(request.getParameter("costkind4") == "" ? "0" : request.getParameter("costkind4"));
		saftycostplan.setCostkind5(request.getParameter("costkind5") == "" ? "0" : request.getParameter("costkind5"));
		saftycostplan.setCostkind6(request.getParameter("costkind6") == "" ? "0" : request.getParameter("costkind6"));
		saftycostplan.setCostkind7(request.getParameter("costkind7") == "" ? "0" : request.getParameter("costkind7"));
		saftycostplan.setCostkind8(request.getParameter("costkind8") == "" ? "0" : request.getParameter("costkind8"));
		saftycostplan.setCostkind9(request.getParameter("costkind9") == "" ? "0" : request.getParameter("costkind9"));

		saftycostplan
				.setCostkind01(request.getParameter("costkind01") == "" ? "0" : request.getParameter("costkind01"));
		saftycostplan
				.setCostkind11(request.getParameter("costkind11") == "" ? "0" : request.getParameter("costkind11"));
		saftycostplan
				.setCostkind21(request.getParameter("costkind21") == "" ? "0" : request.getParameter("costkind21"));
		saftycostplan
				.setCostkind31(request.getParameter("costkind31") == "" ? "0" : request.getParameter("costkind31"));
		saftycostplan
				.setCostkind41(request.getParameter("costkind41") == "" ? "0" : request.getParameter("costkind41"));
		saftycostplan
				.setCostkind51(request.getParameter("costkind51") == "" ? "0" : request.getParameter("costkind51"));
		saftycostplan
				.setCostkind61(request.getParameter("costkind61") == "" ? "0" : request.getParameter("costkind61"));
		saftycostplan
				.setCostkind71(request.getParameter("costkind71") == "" ? "0" : request.getParameter("costkind71"));
		saftycostplan
				.setCostkind81(request.getParameter("costkind81") == "" ? "0" : request.getParameter("costkind81"));
		saftycostplan
				.setCostkind91(request.getParameter("costkind91") == "" ? "0" : request.getParameter("costkind91"));
		saftycostplan
				.setCostkind02(request.getParameter("costkind02") == "" ? "0" : request.getParameter("costkind02"));
		saftycostplan
				.setCostkind12(request.getParameter("costkind12") == "" ? "0" : request.getParameter("costkind12"));
		saftycostplan
				.setCostkind22(request.getParameter("costkind22") == "" ? "0" : request.getParameter("costkind22"));
		saftycostplan
				.setCostkind32(request.getParameter("costkind32") == "" ? "0" : request.getParameter("costkind32"));
		saftycostplan
				.setCostkind42(request.getParameter("costkind42") == "" ? "0" : request.getParameter("costkind42"));
		saftycostplan
				.setCostkind52(request.getParameter("costkind52") == "" ? "0" : request.getParameter("costkind52"));
		saftycostplan
				.setCostkind62(request.getParameter("costkind62") == "" ? "0" : request.getParameter("costkind62"));
		saftycostplan
				.setCostkind72(request.getParameter("costkind72") == "" ? "0" : request.getParameter("costkind72"));
		saftycostplan
				.setCostkind82(request.getParameter("costkind82") == "" ? "0" : request.getParameter("costkind82"));
		saftycostplan
				.setCostkind92(request.getParameter("costkind92") == "" ? "0" : request.getParameter("costkind92"));

		// saftycostplan.setCostplan(request.getParameter("costplan"));
		saftycostplan.setCostplansum(request.getParameter("costplansum"));
		saftycostplan.setCostplansum1(request.getParameter("costplansum1"));
		saftycostplan.setCostplansum2(request.getParameter("costplansum2"));
		saftycostplan.setProjectName(request.getParameter("ProjectName"));

		String yearorfull = request.getParameter("yearorfull");
		saftycostplan.setYearorfull(yearorfull);

		if (yearorfull.equals("项目整体投入计划")) {
			saftycostplan.setYear("/");
		} else {
			saftycostplan.setYear(request.getParameter("year"));
		}

		System.out.println(saftycostplan.toString());
		return saftycostplan;
	}

	public void addSaftycostplan() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Saftycostplan s = getSaftycostplan();

			// System.out.println(s.toString()+"*****************************");

			String jsonStr = saftyCostService.addSaftycostplan(s, fileName, rootPath);

			String OptNote = "添加了" + s.getYear() + "年的项目部安全生产投入计划";
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

	public void editSaftycostplan() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Saftycostplan s = getSaftycostplan();
			s.setId(Integer.parseInt(request.getParameter("ID")));
			s.setAccessory(request.getParameter("Accessory"));
			String jsonStr = saftyCostService.editSaftycostplan(s, fileName, rootPath);

			String OptNote = "编辑了" + s.getYear() + "年的项目部安全生产投入计划";
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

	public void deleteSaftycostplan() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		System.out.println("-------" + ID);
		try {
			String jsonStr = saftyCostService.deleteSaftycostplan(ID);
			String OptNote = "删除了ID为" + ID + "年的项目部安全生产投入计划";
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

	// saftyaccounts表的请求处理函数*****************************************
	public void getSaftyaccountsListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {

			String projectName = request.getParameter("projectName");
			String jsonStr = saftyCostService.getSaftyaccountsList("", start, limit, projectName);
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

	public void getAllSaftyaccounts() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String jsonStr = saftyCostService.getAllSaftyaccounts();
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

	public void getSaftyaccountsListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = saftyCostService.getSaftyaccountsList(findstr, start, limit, projectName);
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

	public Saftyaccounts getSaftyaccounts() throws Exception {

		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 2016-10-26 15:51:00
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = sdf.format(new Date());
		System.out.println(dateStr);
		java.sql.Date date = java.sql.Date.valueOf(dateStr);

		HttpServletRequest request = ServletActionContext.getRequest();
		Saftyaccounts saftyaccounts = new Saftyaccounts();
		saftyaccounts.setSubjectnum(request.getParameter("subjectnum"));
		saftyaccounts.setCostkind(request.getParameter("costkind"));
		saftyaccounts.setCostetails(request.getParameter("costetails"));
		saftyaccounts.setApplysector(request.getParameter("applysector"));
		saftyaccounts.setCostuse(request.getParameter("costuse"));
		saftyaccounts.setAmount(request.getParameter("amount"));
		saftyaccounts.setManager(request.getParameter("manager"));
		saftyaccounts.setRegisterperson(request.getParameter("registerperson"));
		saftyaccounts.setApprotime(date);
		// saftyaccounts.setReceiptcopy(request.getParameter("receiptcopy"));
		// saftyaccounts.setAccessory("要附件");
		// saftyaccounts.setAccessory(request.getParameter("accessory"));

		saftyaccounts.setRemarks(request.getParameter("remarks").replace("\n", ""));
		if (saftyaccounts.getRemarks().equals("不超过500个字符")) {
			saftyaccounts.setRemarks("");
		}

		saftyaccounts.setChecksituation(
				request.getParameter("checksituation") == null ? "无" : request.getParameter("checksituation"));
		saftyaccounts.setProjectName(request.getParameter("ProjectName"));

		return saftyaccounts;
	}

	// ***********************项目部安全费用使用台账******
	public void addSaftyaccounts() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		System.out.println(fileName);
		System.out.println(fileName + "llllllllllllllllllllllllll");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Saftyaccounts s = getSaftyaccounts();
			System.out.println(
					"***************" + s.getSubjectnum() + "," + s.getCostetails() + "," + s.getCostkind() + "\n");
			String jsonStr = saftyCostService.addSaftyaccounts(s, fileName, rootPath);

			String OptNote = "添加了科目编码为" + s.getSubjectnum() + "的项目部安全安全费用使用台账";
			InsertLog.InsertOptLog(OptNote);

			// 更新统计表中saftycostjt
			String subjectnum = s.getSubjectnum().substring(0, 8);
			System.out.println("subjectnum:" + subjectnum);
			List<Saftycosttj> list = saftyCostService.gettjBysubjectnum(subjectnum);
			Saftycosttj saftycosttj = list.get(0);
			if (saftycosttj != null) {
				String old = saftycosttj.getCost();
				int cost = Integer.parseInt(old) + Integer.parseInt(s.getAmount());
				saftycosttj.setCost(String.valueOf(cost));
				saftyCostService.updateSaftycosttj(saftycosttj);
			}

			// String jsonStr = saftyCostService.addSaftyaccounts(s);
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

	// ***********************项目部安全费用台账检查******
	public void addSaftyaccountscheck() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Saftyaccounts s = getSaftyaccounts();
			// System.out.println(s.getSubjectnum()+","+s.getCostetails()+","+s.getCostkind()+"\n"+s.getFeb());
			String jsonStr = saftyCostService.addSaftyaccounts(s, fileName, rootPath);
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

	public void editSaftyaccounts() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Saftyaccounts s = getSaftyaccounts();
			s.setId(Integer.parseInt(request.getParameter("ID")));
			s.setAccessory(request.getParameter("Accessory"));

			// System.out.println("*********************"+request.getParameter("ID")+"**********"+request.getParameter("Accessory"));

			// String jsonStr =
			// saftyCostService.updateSaftyaccounts(saftyaccounts);
			String jsonStr = saftyCostService.editSaftyaccounts(s, fileName, rootPath);

			String OptNote = "编辑了科目编码为" + s.getSubjectnum() + "的项目部安全安全费用使用台账";
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

	public void deleteSaftyaccounts() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		System.out.println("-------" + ID);
		try {
			String jsonStr = saftyCostService.deleteSaftyaccounts(ID);
			String OptNote = "删除了ID为" + ID + "的项目部安全安全费用使用台账";
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

	public void getFileInfo() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		String path = request.getParameter("path");
		String fileName = request.getParameter("name");
		System.out.println();
		try {
			String filePath = request.getRealPath("/") + "upload\\" + path + "\\" + fileName;
			String fileLength = saftyCostService.getFileInfo(filePath);

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
			String jsonStr = saftyCostService.deleteAllFile(ppID, fileName, rootPath);
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
			String jsonStr = saftyCostService.deleteOneFile(ppID, fileName, rootPath);
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

	// fenbaosaftyaccounts表的请求处理函数***********************************
	public void getFenbaosaftyaccountsListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = saftyCostService.getFenbaosaftyaccountsList("", start, limit, projectName);
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

	public void getFenbaosaftyaccountsListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = saftyCostService.getFenbaosaftyaccountsList(findstr, start, limit, projectName);
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

	public Fenbaosaftyaccounts getFenbaosaftyaccounts() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 2016-10-26 15:51:00
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = sdf.format(new Date());
		System.out.println(dateStr);

		java.sql.Date checktime = java.sql.Date.valueOf(request.getParameter("checktime"));

		Fenbaosaftyaccounts fenbaosaftyaccounts = new Fenbaosaftyaccounts();
		fenbaosaftyaccounts.setSubcontractor(request.getParameter("subcontractor"));
		fenbaosaftyaccounts.setChecktime(checktime);
		fenbaosaftyaccounts.setTaizhang(request.getParameter("taizhang"));
		// 类型判断参考basicinfoAction
		fenbaosaftyaccounts.setProjectName(request.getParameter("ProjectName"));
		return fenbaosaftyaccounts;
	}

	public void addFenbaosaftyaccounts() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");

		// System.out.println(fileName +
		// "**************************************");
		// String fileName2 = request.getParameter("fileName2");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Fenbaosaftyaccounts s = getFenbaosaftyaccounts();

			// System.out.println(s.toString()+"*****************************");
			String jsonStr = saftyCostService.addFenbaosaftyaccounts(s, fileName, rootPath);

			String OptNote = "添加了分包单位为" + s.getSubcontractor() + "的分包方安全费用使用台账";
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

	public void editFenbaosaftyaccounts() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		// String fileName2 = request.getParameter("fileName2");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Fenbaosaftyaccounts s = getFenbaosaftyaccounts();
			s.setId(Integer.parseInt(request.getParameter("ID")));
			s.setAccessory(request.getParameter("Accessory"));
			// s.setAccessory2(request.getParameter("Accessory2"));
			String jsonStr = saftyCostService.editFenbaosaftyaccounts(s, fileName, rootPath);

			String OptNote = "编辑了分包单位为" + s.getSubcontractor() + "的分包方安全费用使用台账";
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

	public void deleteFenbaosaftyaccounts() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		System.out.println("-------" + ID);
		try {
			String jsonStr = saftyCostService.deleteFenbaosaftyaccounts(ID);
			String OptNote = "删除了ID" + ID + "的分包方安全费用使用台账";
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

	// fenbaosaftycostsum表的请求处理函数************************************
	public void getFenbaosaftycostsumDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String jsonStr = saftyCostService.getFenbaosaftycostsumList("", start, limit);
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

	public void getAllFenbaosaftycostsum() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String jsonStr = saftyCostService.getAllFenbaosaftycostsum();
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

	public void getFenbaosaftycostsumListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String jsonStr = saftyCostService.getFenbaosaftycostsumList(findstr, start, limit);
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

	public Fenbaosaftycostsum getFenbaosaftycostsum() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 2016-10-26 15:51:00
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = sdf.format(new Date());
		java.sql.Date date = java.sql.Date.valueOf(dateStr);

		java.sql.Date regtime = java.sql.Date.valueOf(request.getParameter("regtime"));

		Fenbaosaftycostsum fenbaosaftycostsum = new Fenbaosaftycostsum();
		fenbaosaftycostsum.setRegtime(regtime);
		fenbaosaftycostsum.setCostkind(request.getParameter("costkind"));
		fenbaosaftycostsum.setRepoter(request.getParameter("repoter"));
		fenbaosaftycostsum.setCost(Integer.parseInt(request.getParameter("cost")));
		// fenbaosaftycostsum.setSumcost(Integer.parseInt(request.getParameter("sumcost")));

		return fenbaosaftycostsum;
	}

	public void addFenbaosaftycostsum() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			Fenbaosaftycostsum s = getFenbaosaftycostsum();
			String jsonStr = saftyCostService.addFenbaosaftycostsum(s);
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

	public void editFenbaosaftycostsum() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			Fenbaosaftycostsum fenbaosaftycostsum = getFenbaosaftycostsum();
			fenbaosaftycostsum.setId(Integer.parseInt(request.getParameter("ID")));

			String jsonStr = saftyCostService.updateFenbaosaftycostsum(fenbaosaftycostsum);
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

	public void deleteFenbaosaftycostsum() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		System.out.println("-------" + ID);
		try {
			String jsonStr = saftyCostService.deleteFenbaosaftycostsum(ID);
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

	// ******Sanxiang
	public void getSanxiangListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {

			type = request.getParameter("type");
			String jsonStr = saftyCostService.getSanxiangList("", start, limit);
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

	public void getSanxiangListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String jsonStr = saftyCostService.getSanxiangList(findstr, start, limit);
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

	public Sanxiang getSanxiang() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 2016-10-26 15:51:00
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = sdf.format(new Date());
		System.out.println(dateStr);

		Sanxiang sanxiang = new Sanxiang();
		sanxiang.setProject(request.getParameter("project"));
		sanxiang.setSumnum(request.getParameter("sumnum"));
		sanxiang.setDanwei(request.getParameter("danwei"));
		// 类型判断参考basicinfoAction
		return sanxiang;
	}

	public void addSanxiang() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");

		System.out.println(fileName + "**************************************");
		// String fileName2 = request.getParameter("fileName2");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Sanxiang s = getSanxiang();

			// System.out.println(s.toString()+"*****************************");

			String jsonStr = saftyCostService.addSanxiang(s, fileName, rootPath);
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

	public void editSanxiang() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		// String fileName2 = request.getParameter("fileName2");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Sanxiang s = getSanxiang();
			s.setId(Integer.parseInt(request.getParameter("ID")));
			s.setAccessory(request.getParameter("Accessory"));
			// s.setAccessory2(request.getParameter("Accessory2"));
			String jsonStr = saftyCostService.editSanxiang(s, fileName, rootPath);
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

	public void deleteSanxiang() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		System.out.println("-------" + ID);
		try {
			String jsonStr = saftyCostService.deleteSanxiang(ID);
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

	// ******Saftyjiancha
	public void getSaftyjianchaListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = saftyCostService.getSaftyjianchaList("", start, limit, projectName);
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

	public void getSaftyjianchaListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = saftyCostService.getSaftyjianchaList(findstr, start, limit, projectName);
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

	public Saftyjiancha getSaftyjiancha() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 2016-10-26 15:51:00
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = sdf.format(new Date());
		System.out.println(dateStr);

		java.sql.Date jctime = java.sql.Date.valueOf(request.getParameter("jctime"));

		Saftyjiancha saftyjiancha = new Saftyjiancha();
		saftyjiancha.setJcperson(request.getParameter("jcperson"));
		saftyjiancha.setJctime(jctime);
		saftyjiancha.setJcresult(request.getParameter("jcresult"));
		saftyjiancha.setProjectName(request.getParameter("ProjectName"));
		// 类型判断参考basicinfoAction
		return saftyjiancha;
	}

	public void addSaftyjiancha() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");

		// System.out.println(fileName +
		// "**************************************");
		// String fileName2 = request.getParameter("fileName2");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Saftyjiancha s = getSaftyjiancha();

			// System.out.println(s.toString()+"*****************************");
			String jsonStr = saftyCostService.addSaftyjiancha(s, fileName, rootPath);
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

	public void editSaftyjiancha() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		// String fileName2 = request.getParameter("fileName2");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Saftyjiancha s = getSaftyjiancha();
			s.setId(Integer.parseInt(request.getParameter("ID")));
			s.setAccessory(request.getParameter("Accessory"));
			// s.setAccessory2(request.getParameter("Accessory2"));
			String jsonStr = saftyCostService.editSaftyjiancha(s, fileName, rootPath);
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

	public void deleteSaftyjiancha() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		System.out.println("-------" + ID);
		try {
			String jsonStr = saftyCostService.deleteSaftyjiancha(ID);
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

	// ******Fenbaoplan******************
	public void getFenbaoplanListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = saftyCostService.getFenbaoplanList("", start, limit, projectName);
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

	public void getFenbaoplanListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = saftyCostService.getFenbaoplanList(findstr, start, limit, projectName);
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

	public Fenbaoplan getFenbaoplan() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 2016-10-26 15:51:00
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = sdf.format(new Date());
		System.out.println(dateStr);

		java.sql.Date time = java.sql.Date.valueOf(request.getParameter("time"));

		Fenbaoplan fenbaoplan = new Fenbaoplan();
		fenbaoplan.setFenbaoname(request.getParameter("fenbaoname"));
		fenbaoplan.setTime(time);
		fenbaoplan.setPlanname(request.getParameter("planname"));
		fenbaoplan.setProjectName(request.getParameter("ProjectName"));
		// 类型判断参考basicinfoAction
		return fenbaoplan;
	}

	public void addFenbaoplan() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");

		// System.out.println(fileName +
		// "**************************************");
		// String fileName2 = request.getParameter("fileName2");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Fenbaoplan s = getFenbaoplan();

			// System.out.println(s.toString()+"*****************************");
			String jsonStr = saftyCostService.addFenbaoplan(s, fileName, rootPath);

			String OptNote = "添加了分包单位为" + s.getFenbaoname() + "的分包方安全生产投入计划";
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

	public void editFenbaoplan() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		// String fileName2 = request.getParameter("fileName2");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Fenbaoplan s = getFenbaoplan();
			s.setId(Integer.parseInt(request.getParameter("ID")));
			s.setAccessory(request.getParameter("Accessory"));
			// s.setAccessory2(request.getParameter("Accessory2"));
			String jsonStr = saftyCostService.editFenbaoplan(s, fileName, rootPath);
			String OptNote = "编辑了分包单位为" + s.getFenbaoname() + "的分包方安全生产投入计划";
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

	public void deleteFenbaoplan() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		System.out.println("-------" + ID);
		try {
			String jsonStr = saftyCostService.deleteFenbaoplan(ID);
			String OptNote = "删除了ID为" + ID + "的分包方安全生产投入计划";
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

	// ******Fenbaosaftyjiancha******
	public void getFenbaosaftyjianchaListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = saftyCostService.getFenbaosaftyjianchaList("", start, limit, projectName);
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

	public void getFenbaosaftyjianchaListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = saftyCostService.getFenbaosaftyjianchaList(findstr, start, limit, projectName);
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

	public Fenbaosaftyjiancha getFenbaosaftyjiancha() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 2016-10-26 15:51:00
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = sdf.format(new Date());
		System.out.println(dateStr);

		java.sql.Date jctime = java.sql.Date.valueOf(request.getParameter("jctime"));

		Fenbaosaftyjiancha fenbaosaftyjiancha = new Fenbaosaftyjiancha();
		fenbaosaftyjiancha.setFenbaoname(request.getParameter("fenbaoname"));
		fenbaosaftyjiancha.setJcperson(request.getParameter("jcperson"));
		fenbaosaftyjiancha.setJctime(jctime);
		fenbaosaftyjiancha.setJcresult(request.getParameter("jcresult"));
		fenbaosaftyjiancha.setProjectName(request.getParameter("ProjectName"));
		// 类型判断参考basicinfoAction
		return fenbaosaftyjiancha;
	}

	public void addFenbaosaftyjiancha() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Fenbaosaftyjiancha s = getFenbaosaftyjiancha();

			// System.out.println(s.toString()+"*****************************");
			String jsonStr = saftyCostService.addFenbaosaftyjiancha(s, fileName, rootPath);

			String OptNote = "添加了分包单位为" + s.getFenbaoname() + "的分包方安全费用使用检查";
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

	public void editFenbaosaftyjiancha() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		// String fileName2 = request.getParameter("fileName2");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Fenbaosaftyjiancha s = getFenbaosaftyjiancha();
			s.setId(Integer.parseInt(request.getParameter("ID")));
			s.setAccessory(request.getParameter("Accessory"));
			String jsonStr = saftyCostService.editFenbaosaftyjiancha(s, fileName, rootPath);
			String OptNote = "编辑了分包单位为" + s.getFenbaoname() + "的分包方安全费用使用检查";
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

	public void deleteFenbaosaftyjiancha() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		System.out.println("-------" + ID);
		try {
			String jsonStr = saftyCostService.deleteFenbaosaftyjiancha(ID);
			String OptNote = "删除了ID为" + ID + "的分包方安全费用使用检查";
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

	// ******Saftycosttj1******
	public void getSaftycosttj1ListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = saftyCostService.getSaftycosttj1List("", start, limit, projectName);
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

	public void getSaftycosttj1ListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = saftyCostService.getSaftycosttj1List(findstr, start, limit, projectName);
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

	// ******Saftycosttj2******
	public void getSaftycosttj2ListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = saftyCostService.getSaftycosttj2List("", start, limit, projectName);
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

	public void getSaftycosttj2ListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = saftyCostService.getSaftycosttj2List(findstr, start, limit, projectName);
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

	// ******Saftycosttj3******
	public void getSaftycosttj3ListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = saftyCostService.getSaftycosttj3List("", start, limit, projectName);
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

	public void getSaftycosttj3ListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = saftyCostService.getSaftycosttj3List(findstr, start, limit, projectName);
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
	
	
	public void getSaftycosttjrenwu1ListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = saftyCostService.getSaftycosttjrenwu1List("", start, limit, projectName);
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
	

	
	public void importExcel() throws Exception {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String type = request.getParameter("type");
			String fileName = request.getParameter("fileName");
			String projectName = request.getParameter("projectName");
			String rootPath = request.getRealPath("/")+"upload\\";
			int total = saftyCostService.importExcel(type, rootPath, fileName, projectName);
			outputJSON(response, "{\"result\":\"success\",\"total\":"+total+"}");
		}
		catch(Exception e) {
			e.printStackTrace();
			outputJSON(response, "{\"result\":\"failed\"}");
		}
	}
	
	private void outputJSON(HttpServletResponse response, String jsonStr) throws Exception {
		System.out.println(jsonStr);
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.write(jsonStr);
		out.flush();
		out.close();
	}

}
