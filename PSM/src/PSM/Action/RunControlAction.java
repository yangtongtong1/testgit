package PSM.Action;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

import PSM.DAO.RunControlDAO;
import PSM.Service.RunControlService;
import hibernate.Gongtitai;
import hibernate.Xiandongtai;

public class RunControlAction extends ActionSupport {

	private int start; // 分页查询参
	private int limit; // 分页查询参数
	private RunControlService runControlService;
	private RunControlDAO runControlDAO;

	public RunControlDAO getRunControlDAO() {
		return runControlDAO;
	}

	public void setRunControlDAO(RunControlDAO runControlDAO) {
		this.runControlDAO = runControlDAO;
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

	public RunControlService getRunControlService() {
		return runControlService;
	}

	public void setRunControlService(RunControlService runControlService) {
		this.runControlService = runControlService;
	}

	public String execute() throws Exception {
		return SUCCESS;
	}

	/*
	 * public void getEmployee() { HttpServletResponse response =
	 * ServletActionContext.getResponse(); HttpServletRequest request =
	 * ServletActionContext.getRequest();
	 * 
	 * try { String jsonStr = runControlService.getEmployee();
	 * System.out.println(jsonStr); response.setCharacterEncoding("UTF-8");
	 * PrintWriter out = response.getWriter(); out.write(jsonStr); out.flush();
	 * out.close(); } catch(Exception e) { e.printStackTrace(); //return ERROR;
	 * } //return SUCCESS; //return null; }
	 */

	// *******Xiandongtai
	public void getXiandongtaiListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {

			type = request.getParameter("type");
			String projectName = request.getParameter("projectName");
			String jsonStr = runControlService.getXiandongtaiList("", start, limit, projectName);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public void getXiandongtaiListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = runControlService.getXiandongtaiList(findstr, start, limit, projectName);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public Xiandongtai getXiandongtai() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 2016-10-26 15:51:00
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = sdf.format(new Date());
		System.out.println(dateStr);
		java.sql.Date intimeplan = java.sql.Date.valueOf(request.getParameter("intimeplan"));
		//java.sql.Date intimereal = java.sql.Date.valueOf(request.getParameter("intimereal"));
		java.sql.Date lvetimeplan = java.sql.Date.valueOf(request.getParameter("lvetimeplan"));
		//java.sql.Date lvetimereal = java.sql.Date.valueOf(request.getParameter("lvetimereal"));

		Xiandongtai xiandongtai = new Xiandongtai();
		xiandongtai.setRepoter(request.getParameter("repoter"));
		xiandongtai.setGangwei(request.getParameter("gangwei"));
		xiandongtai.setName(request.getParameter("name"));
		xiandongtai.setSfz(request.getParameter("sfz"));

		xiandongtai.setSex(request.getParameter("sex"));
		xiandongtai.setIntimeplan(intimeplan);
		xiandongtai.setIntimereal(request.getParameter("intimereal"));
		xiandongtai.setLvetimeplan(lvetimeplan);
		xiandongtai.setLvetimereal(request.getParameter("lvetimereal"));

		xiandongtai.setPhone(request.getParameter("phone"));
		xiandongtai.setIstijian(request.getParameter("istijian"));
		xiandongtai.setIsgsbx(request.getParameter("isgsbx"));
		// 类型判断参考basicinfoAction

		xiandongtai.setPeixun(request.getParameter("Peixun"));

		xiandongtai.setProjectName(request.getParameter("ProjectName"));

		return xiandongtai;
	}

	public void addXiandongtai() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");

		System.out.println(fileName + "**************************************");
		// String fileName2 = request.getParameter("fileName2");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Xiandongtai s = getXiandongtai();

			/*
			 * List emp = runControlDAO.getEmployee(122); String temp = ""; for
			 * (int i=0;i<emp.size();i++){
			 * 
			 * temp += emp.get(i); temp += ","; } String[] em = temp.split(",");
			 * int pd=1; for (int i=0;i<=em.length-1;i++) {
			 * if(s.getName().equals(em[i])) { pd=2; break; } }
			 * 
			 * 
			 * if(pd==2) s.setPeixun("是"); else s.setPeixun("否");
			 */

			// System.out.println(s.toString()+"*****************************");

			String jsonStr = runControlService.addXiandongtai(s, fileName, rootPath);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public void editXiandongtai() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		// String fileName2 = request.getParameter("fileName2");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Xiandongtai s = getXiandongtai();

			/*
			 * List emp = runControlDAO.getEmployee(122); String temp = ""; for
			 * (int i=0;i<emp.size();i++){
			 * 
			 * temp += emp.get(i); temp += ","; } String[] em = temp.split(",");
			 * int pd=1; for (int i=0;i<=em.length-1;i++) {
			 * if(s.getName().equals(em[i])) { pd=2; break; } }
			 * 
			 * 
			 * if(pd==2) s.setPeixun("是"); else s.setPeixun("否");
			 */

			s.setId(Integer.parseInt(request.getParameter("ID")));
			s.setAccessory(request.getParameter("Accessory"));
			// s.setAccessory2(request.getParameter("Accessory2"));
			String jsonStr = runControlService.editXiandongtai(s, fileName, rootPath);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public void deleteXiandongtai() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		System.out.println("-------" + ID);
		try {
			String jsonStr = runControlService.deleteXiandongtai(ID);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	// ***************Gongtitai表
	public void getGongtitaiListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {

			type = request.getParameter("type");
			String projectName = request.getParameter("projectName");
			String jsonStr = runControlService.getGongtitaiList("", start, limit, projectName);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public void getGongtitaiListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = runControlService.getGongtitaiList(findstr, start, limit, projectName);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public Gongtitai getGongtitai() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		java.sql.Date tijiantime = java.sql.Date.valueOf(request.getParameter("tijiantime"));

		Gongtitai gongtitai = new Gongtitai();
		gongtitai.setRepoter(request.getParameter("repoter"));
		gongtitai.setGangwei(request.getParameter("gangwei"));
		gongtitai.setName(request.getParameter("name"));
		gongtitai.setSex(request.getParameter("sex"));
		gongtitai.setTijiantime(tijiantime);
		gongtitai.setTijianplace(request.getParameter("tijianplace"));
		gongtitai.setTijianresult(request.getParameter("tijianresult"));
		gongtitai.setType(request.getParameter("type"));
		gongtitai.setProjectName(request.getParameter("ProjectName"));
		return gongtitai;
	}

	public void addGongtitai() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");

		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Gongtitai s = getGongtitai();

			String jsonStr = runControlService.addGongtitai(s, fileName, rootPath);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public void editGongtitai() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		// String fileName2 = request.getParameter("fileName2");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Gongtitai s = getGongtitai();
			s.setId(Integer.parseInt(request.getParameter("ID")));
			s.setAccessory(request.getParameter("Accessory"));
			// s.setAccessory2(request.getParameter("Accessory2"));
			String jsonStr = runControlService.editGongtitai(s, fileName, rootPath);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public void deleteGongtitai() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		System.out.println("-------" + ID);
		try {
			String jsonStr = runControlService.deleteGongtitai(ID);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	// **********文件
	public void getFileInfo() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		String path = request.getParameter("path");
		String fileName = request.getParameter("name");
		System.out.println();
		try {
			String filePath = request.getRealPath("/") + "upload\\" + path + "\\" + fileName;
			String fileLength = runControlService.getFileInfo(filePath);

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
		// HttpServletRequest request = ServletActionContext.getRequest();
		// HttpServletResponse response = ServletActionContext.getResponse();
		// String fileName = request.getParameter("fileName");
		// String ppID = request.getParameter("id");
		// String rootPath = request.getRealPath("/") + "upload\\";
		// try {
		// String jsonStr = runControlService.deleteAllFile(ppID, fileName,
		// rootPath);
		// response.setCharacterEncoding("UTF-8");
		// PrintWriter out = response.getWriter();
		// out.write(jsonStr);
		// out.flush();
		// out.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// return ERROR;
		// }
		// return SUCCESS;
	}

	public void deleteOneFile() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		String fileName = request.getParameter("name");
		String ppID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			String jsonStr = runControlService.deleteOneFile(ppID, fileName, rootPath);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			// return ERROR;
		}
		// return SUCCESS;
	}
}
