package PSM.Action;

import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

import PSM.DAO.GoalDutyDAO;
import PSM.Service.GoalDutyService;
import PSM.Service.HiddenTroubleSolutionService;
import hibernate.Anweihui;
import hibernate.Fenbao;
import hibernate.Flownode;
import hibernate.Goaldecom;
import hibernate.Project;
import hibernate.Saveculture;
import hibernate.Saveproduct;
import hibernate.Securityplan;
import hibernate.Taizhang;
import hibernate.Weixianyuan;
import hibernate.Yinhuanpaicha;

public class HiddenTroubleSolutionAction extends ActionSupport {
	private int start; // 分页查询参
	private int limit; // 分页查询参数
	private HiddenTroubleSolutionService hiddenTroubleSolutionService;
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

	public HiddenTroubleSolutionService getHiddenTroubleSolutionService() {
		return hiddenTroubleSolutionService;
	}

	public void setHiddenTroubleSolutionService(HiddenTroubleSolutionService hiddenTroubleSolutionService) {
		this.hiddenTroubleSolutionService = hiddenTroubleSolutionService;
	}

	public HiddenTroubleSolutionAction() {

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
			String fileLength = hiddenTroubleSolutionService.getFileInfo(filePath);

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
			String jsonStr = hiddenTroubleSolutionService.deleteAllFile(ppID, fileName, rootPath);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
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
			String jsonStr = hiddenTroubleSolutionService.deleteOneFile(ppID, fileName, rootPath);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public void getWeixianyuanListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = hiddenTroubleSolutionService.getWeixianyuanList("", start, limit, projectName);

			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public void getWeixianyuanListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = hiddenTroubleSolutionService.getWeixianyuanList(findstr, start, limit, projectName);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	
//	private Date jobTime;
//	private String jobLocation;
//	private String jobContent;
//	private String mainRisk;
//	private String riskRank;
//	private String preAction;
//	private String jobMan;
//	private String jobJiandu;
	public Weixianyuan getWeixianyuan() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		// java.sql.Date solveTime =
		// java.sql.Date.valueOf(request.getParameter("solveTime"));

		Weixianyuan pro = new Weixianyuan();
		java.sql.Date jobTime = java.sql.Date.valueOf(request.getParameter("jobTime"));
		pro.setJobTime(jobTime);
		pro.setJobLocation(request.getParameter("jobLocation"));
		
		pro.setJobContent(request.getParameter("jobContent"));
		pro.setMainRisk(request.getParameter("mainRisk"));
		pro.setRiskRank(request.getParameter("riskRank"));

		pro.setJobMan(request.getParameter("jobMan"));
		
		pro.setJobJiandu(request.getParameter("jobJiandu"));

		pro.setPreAction(request.getParameter("preAction").replace("\n", ""));
//		pro.setYijian(request.getParameter("Yijian"));
		if (pro.getPreAction().equals("不超过500个字符")) {
			pro.setPreAction("");
		}
		pro.setProjectName(request.getParameter("ProjectName"));

		return pro;
	}

	public void addWeixianyuan() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Weixianyuan pro = getWeixianyuan();

			String jsonStr = hiddenTroubleSolutionService.addWeixianyuan(pro, fileName, rootPath);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public void editWeixianyuan() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Weixianyuan pro = getWeixianyuan();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));

			String jsonStr = hiddenTroubleSolutionService.editWeixianyuan(pro, fileName, rootPath);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public void deleteWeixianyuan() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {

			String jsonStr = hiddenTroubleSolutionService.deleteWeixianyuan(ID, rootPath);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public void getYinhuanpaichaListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String jsonStr = hiddenTroubleSolutionService.getYinhuanpaichaList("", start, limit);

			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public void getYinhuanpaichaListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String jsonStr = hiddenTroubleSolutionService.getYinhuanpaichaList(findstr, start, limit);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public Yinhuanpaicha getYinhuanpaicha() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Yinhuanpaicha pro = new Yinhuanpaicha();

		return pro;
	}

	public void addYinhuanpaicha() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Yinhuanpaicha pro = getYinhuanpaicha();

			String jsonStr = hiddenTroubleSolutionService.addYinhuanpaicha(pro, fileName, rootPath);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public void editYinhuanpaicha() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Yinhuanpaicha pro = getYinhuanpaicha();
			pro.setId(Integer.parseInt(request.getParameter("ID")));

			String jsonStr = hiddenTroubleSolutionService.editYinhuanpaicha(pro, fileName, rootPath);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public void deleteYinhuanpaicha() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {

			String jsonStr = hiddenTroubleSolutionService.deleteYinhuanpaicha(ID, rootPath);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
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
			int total = hiddenTroubleSolutionService.importExcel(type, rootPath, fileName, projectName);
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
	
	public void getReadilyShootListDef() throws Exception {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
//			String jsonStr = "{\"total\":1,\"rows\":[{\"url\":\"http://c.hiphotos.baidu.com/image/h%3D220/sign=e20667c7a6c379316268812bdbc6b784/09fa513d269759eea79bc50abbfb43166c22df2c.jpg\",\"comment\":\"Hello\"}]}";;
			String projectName = request.getParameter("ProjectName");
			String userRole = request.getParameter("userRole");
			if (userRole.equals("全部项目")) projectName = "全部项目";
			String jsonStr = hiddenTroubleSolutionService.getReadilyShootList(projectName, "", limit, start);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
			outputJSON(response, "{\"result\":\"failed\"}");
		}
	}
	
	public void getReadilyShootListDef2() throws Exception {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
//			String jsonStr = "{\"total\":1,\"rows\":[{\"url\":\"http://c.hiphotos.baidu.com/image/h%3D220/sign=e20667c7a6c379316268812bdbc6b784/09fa513d269759eea79bc50abbfb43166c22df2c.jpg\",\"comment\":\"Hello\"}]}";;
			String projectName = request.getParameter("ProjectName");
			String userRole = request.getParameter("userRole");
			if (userRole.equals("全部项目")) projectName = "全部项目";
			String jsonStr = hiddenTroubleSolutionService.getReadilyShootList2(projectName, "", limit, start);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
			outputJSON(response, "{\"result\":\"failed\"}");
		}
	}
	
	public void getReadilyShootListSearch() throws Exception {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
//			String jsonStr = "{\"total\":1,\"rows\":[{\"url\":\"http://c.hiphotos.baidu.com/image/h%3D220/sign=e20667c7a6c379316268812bdbc6b784/09fa513d269759eea79bc50abbfb43166c22df2c.jpg\",\"comment\":\"Hello\"}]}";;
			String projectName = request.getParameter("ProjectName");
			String userRole = request.getParameter("userRole");
			if (userRole.equals("全部项目")) projectName = "全部项目";
			String findstr = request.getParameter("findStr");
			String jsonStr = hiddenTroubleSolutionService.getReadilyShootList(projectName, findstr, limit, start);
			outputJSON(response, jsonStr);
		}
		catch(Exception e) {
			e.printStackTrace();
			outputJSON(response, "{\"result\":\"failed\"}");
		}
	}
	
	public void deleteReadilyShoot() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/")+"upload\\";
		try {			
			String jsonStr = hiddenTroubleSolutionService.deleteReadilyShoot(ID, rootPath);
			outputJSON(response, jsonStr);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
