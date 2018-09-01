package PSM.Action;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

import PSM.Service.OperaConService;
import hibernate.Safecheckrec;

public class OperaConAction extends ActionSupport {

	private int start; // 分页查询参
	private int limit; // 分页查询参数

	private OperaConService operaConService;

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

	public OperaConService getOperaConService() {
		return operaConService;
	}

	public void setOperaConService(OperaConService operaConService) {
		this.operaConService = operaConService;
	}

	public String execute() throws Exception {
		return SUCCESS;
	}

	private void outputJSON(HttpServletResponse response, String jsonStr) throws Exception {
		System.out.println(jsonStr);
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.write(jsonStr);
		out.flush();
		out.close();
	}

	public String getSafecheckrecListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String tableID = request.getParameter("tableID");
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = operaConService.getSafecheckrecList("", start, limit, tableID, projectName);
			outputJSON(response, jsonStr);
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}

	public String getSafecheckrecListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String tableID = request.getParameter("tableID");
			String projectName = request.getParameter("projectName");
			String jsonStr = operaConService.getSafecheckrecList(findstr, start, limit, tableID, projectName);
			outputJSON(response, jsonStr);
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}

	public Safecheckrec getSafecheckrec() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Safecheckrec pro = new Safecheckrec();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		pro.setName(request.getParameter("Name"));
		pro.setType(request.getParameter("Type"));
		pro.setNum(request.getParameter("Num"));
		pro.setThisTime(sdf.parse(request.getParameter("ThisTime")));
		pro.setLastTime(sdf.parse(request.getParameter("LastTime")));
		pro.setAgent(request.getParameter("Agent"));
		pro.setRegistrant(request.getParameter("Registrant"));
		pro.setOther(request.getParameter("Other"));
		pro.setTableId(request.getParameter("TableID"));
		pro.setProjectName(request.getParameter("ProjectName"));
		return pro;
	}

	public String addSafecheckrec() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Safecheckrec pro = getSafecheckrec();
			String jsonStr = operaConService.addSafecheckrec(pro, fileName, rootPath);
			outputJSON(response, jsonStr);
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}

	public String editSafecheckrec() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Safecheckrec pro = getSafecheckrec();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));
			String jsonStr = operaConService.editSafecheckrec(pro, fileName, rootPath);
			outputJSON(response, jsonStr);
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}

	public String deleteSafecheckrec() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			String jsonStr = operaConService.deleteSafecheckrec(ID, rootPath);
			outputJSON(response, jsonStr);
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}

	public String getFileInfo() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		String path = request.getParameter("path");
		String fileName = request.getParameter("name");
		try {
			String filePath = request.getRealPath("/") + "upload\\" + path + "\\" + fileName;
			String fileLength = operaConService.getFileInfo(filePath);
			outputJSON(response, fileLength);
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;

	}

	public String deleteAllFile() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		String fileName = request.getParameter("fileName");
		String ppID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		String tableID = request.getParameter("tableID");
		try {
			String jsonStr = operaConService.deleteAllFile(ppID, fileName, rootPath);
			outputJSON(response, jsonStr);
		} catch (Exception e) {
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
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			String jsonStr = operaConService.deleteOneFile(ppID, fileName, rootPath);
			outputJSON(response, jsonStr);
		} catch (Exception e) {
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
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public void importExcel() throws Exception {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String type = request.getParameter("type");
			String fileName = request.getParameter("fileName");
			String projectName = request.getParameter("projectName");
			String rootPath = request.getRealPath("/")+"upload\\";
			int total = operaConService.importExcel(type, rootPath, fileName, projectName);
			outputJSON(response, "{\"result\":\"success\",\"total\":"+total+"}");
		}
		catch(Exception e) {
			e.printStackTrace();
			outputJSON(response, "{\"result\":\"success\"}");
		}
	}

}
