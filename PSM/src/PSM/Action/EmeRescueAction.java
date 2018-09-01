package PSM.Action;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

import PSM.Service.EmeRescueService;
import hibernate.ElecEquip;
import hibernate.Managepara;
import hibernate.SpEquip;
import hibernate.Yingjifenbao;
import hibernate.Yingjijyzz;
import hibernate.Yingjipxyl;
import hibernate.Yingjiyuan;

public class EmeRescueAction extends ActionSupport {

	private int start; // 分页查询参
	private int limit; // 分页查询参数
	private LogAction InsertLog = new LogAction();
	private EmeRescueService emeRescueService;

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

	public EmeRescueService getEmeRescueService() {
		return emeRescueService;
	}

	public void setEmeRescueService(EmeRescueService emeRescueService) {
		this.emeRescueService = emeRescueService;
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
	
	public String getManageparaListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String tableID = request.getParameter("tableID");
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = emeRescueService.getManageparaList("", start, limit, tableID, projectName);
			outputJSON(response, jsonStr);
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}

	public String getSpEquipListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = emeRescueService.getSpEquipList("", start, limit, projectName);
			outputJSON(response, jsonStr);
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}

	public String getElecEquipListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = emeRescueService.getElecEquipList("", start, limit, projectName);
			outputJSON(response, jsonStr);
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}

	public String getManageparaListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String tableID = request.getParameter("tableID");
			String projectName = request.getParameter("projectName");
			String jsonStr = emeRescueService.getManageparaList(findstr, start, limit, tableID, projectName);
			outputJSON(response, jsonStr);
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}

	public String getSpEquipListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = emeRescueService.getSpEquipList(findstr, start, limit, projectName);
			outputJSON(response, jsonStr);
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}

	public String getElecEquipListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = emeRescueService.getElecEquipList(findstr, start, limit, projectName);
			outputJSON(response, jsonStr);
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}

	public Managepara getManagepara() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Managepara pro = new Managepara();
		pro.setContent(request.getParameter("Content"));
		pro.setType(request.getParameter("Type"));
		pro.setQuantity(request.getParameter("Quantity"));
		pro.setUnit(request.getParameter("Unit"));
		pro.setFbunit(request.getParameter("Fbunit"));
		pro.setState(request.getParameter("State"));
		pro.setPlace(request.getParameter("Place"));
		pro.setResponsible(request.getParameter("Responsible"));
		pro.setTableId(request.getParameter("TableID"));
		pro.setProjectName(request.getParameter("ProjectName"));
		return pro;
	}

	public SpEquip getSpEquip() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		SpEquip pro = new SpEquip();
		pro.setName(request.getParameter("Name"));
		pro.setType(request.getParameter("Type"));
		pro.setPurpose(request.getParameter("Purpose"));
		
		pro.setInDate(request.getParameter("InDate"));
		

		pro.setOutDate(request.getParameter("OutDate"));
		
		pro.setRegistNo(request.getParameter("RegistNo"));
		pro.setKind(request.getParameter("Kind"));
		pro.setManuUnit(request.getParameter("ManuUnit"));
		
		pro.setPurchaseDate(request.getParameter("PurchaseDate"));
		
		pro.setInstallUnit(request.getParameter("InstallUnit"));
		pro.setCheckStatus(request.getParameter("CheckStatus"));
		pro.setUseStatus(request.getParameter("UseStatus"));
		pro.setType(request.getParameter("Type"));
		pro.setMajorStatus(request.getParameter("MajorStatus"));
		pro.setOtherStatus(request.getParameter("OtherStatus"));
		pro.setProjectName(request.getParameter("ProjectName"));
		return pro;
	}

	public ElecEquip getElecEquip() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		ElecEquip pro = new ElecEquip();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		pro.setEquipNo(request.getParameter("EquipNo"));
		pro.setName(request.getParameter("Name"));
		pro.setType(request.getParameter("Type"));
		pro.setManuUnit(request.getParameter("ManuUnit"));
		pro.setQuantity(request.getParameter("Quantity"));
		pro.setUnit(request.getParameter("Unit"));
		pro.setPurpose(request.getParameter("Purpose"));
		pro.setInDate(sdf.parse(request.getParameter("InDate")));
		pro.setPurpose(request.getParameter("Purpose"));
		pro.setRegistNo(request.getParameter("RegistNo"));
		pro.setUsePlace(request.getParameter("UsePlace"));
		pro.setResponser(request.getParameter("Responser"));
		pro.setProjectName(request.getParameter("ProjectName"));
		return pro;
	}

	public String addManagepara() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Managepara pro = getManagepara();
			String jsonStr = emeRescueService.addManagepara(pro, fileName, rootPath);
			String OptNote = "添加了物资名称为" + pro.getContent() + "的"  + pro.getTableId();
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}

	public String addSpEquip() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			SpEquip pro = getSpEquip();
			String jsonStr = emeRescueService.addSpEquip(pro, fileName, rootPath);
			String OptNote = "添加了名称为" + pro.getName() + "的特种设备管理台账";
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}

	public String addElecEquip() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			ElecEquip pro = getElecEquip();
			String jsonStr = emeRescueService.addElecEquip(pro, fileName, rootPath);
			String OptNote = "添加了名称为" + pro.getName() + "的电气设备管理台账";
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}

	public String editManagepara() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Managepara pro = getManagepara();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));
			String jsonStr = emeRescueService.editManagepara(pro, fileName, rootPath);
			String OptNote = "编辑了物资名称为" + pro.getContent() + "的" + pro.getTableId();
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}

	public String editSpEquip() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			SpEquip pro = getSpEquip();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));
			String jsonStr = emeRescueService.editSpEquip(pro, fileName, rootPath);
			String OptNote = "编辑了名称为" + pro.getName() + "的特种设备管理台账";
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}

	public String editElecEquip() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			ElecEquip pro = getElecEquip();
			pro.setId(Integer.parseInt(request.getParameter("ID")));
			pro.setAccessory(request.getParameter("Accessory"));
			String jsonStr = emeRescueService.editElecEquip(pro, fileName, rootPath);
			String OptNote = "编辑了名称为" + pro.getName() + "的电气设备管理台账";
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}

	public String deleteManagepara() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			String jsonStr = emeRescueService.deleteManagepara(ID, rootPath);
			String OptNote = "删除了ID为" + ID + "的通用物资";
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}

	public String deleteSpEquip() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			String jsonStr = emeRescueService.deleteSpEquip(ID, rootPath);
			String OptNote = "删除了ID为" + ID + "的特种设备管理台账";
			InsertLog.InsertOptLog(OptNote);
			outputJSON(response, jsonStr);
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}

	public String deleteElecEquip() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			String jsonStr = emeRescueService.deleteElecEquip(ID, rootPath);
			String OptNote = "删除了ID为" + ID + "的电气设备管理台账";
			InsertLog.InsertOptLog(OptNote);
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
			String fileLength = emeRescueService.getFileInfo(filePath);
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
			String jsonStr = emeRescueService.deleteAllFile(ppID, fileName, rootPath);
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
			String jsonStr = emeRescueService.deleteOneFile(ppID, fileName, rootPath);
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

	// *******Yingjijyzz*****************
	public String getYingjijyzzListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = emeRescueService.getYingjijyzzList("", start, limit, projectName);
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

	public String getYingjijyzzListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = emeRescueService.getYingjijyzzList(findstr, start, limit, projectName);
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

	public Yingjijyzz getYingjijyzz() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = sdf.format(new Date());

		java.sql.Date clortztime = java.sql.Date.valueOf(request.getParameter("clortztime"));

		Yingjijyzz yingjijyzz = new Yingjijyzz();
		yingjijyzz.setZuizhiname(request.getParameter("zuizhiname"));
		yingjijyzz.setClortz(request.getParameter("clortz"));
		yingjijyzz.setClortztime(clortztime);
		yingjijyzz.setFuzeren(request.getParameter("fuzeren"));
		yingjijyzz.setChengyuan(request.getParameter("chengyuan"));
		yingjijyzz.setGongzuojg(request.getParameter("gongzuojg"));
		yingjijyzz.setProjectName(request.getParameter("ProjectName"));

		return yingjijyzz;
	}

	public String addYingjijyzz() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Yingjijyzz s = getYingjijyzz();
			String jsonStr = emeRescueService.addYingjijyzz(s, fileName, rootPath);

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

	public String editYingjijyzz() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		// String fileName2 = request.getParameter("fileName2");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Yingjijyzz s = getYingjijyzz();
			s.setId(Integer.parseInt(request.getParameter("ID")));
			s.setAccessory(request.getParameter("Accessory"));
			String jsonStr = emeRescueService.editYingjijyzz(s, fileName, rootPath);
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

	public String deleteYingjijyzz() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		System.out.println("-------" + ID);
		try {
			String jsonStr = emeRescueService.deleteYingjijyzz(ID);
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

	// *******Yingjipxyl*****************
	public String getYingjipxylListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = emeRescueService.getYingjipxylList("", start, limit, projectName);
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

	public String getYingjipxylListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = emeRescueService.getYingjipxylList(findstr, start, limit, projectName);
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

	public Yingjipxyl getYingjipxyl() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = sdf.format(new Date());

		java.sql.Date peixuntime = java.sql.Date.valueOf(request.getParameter("peixuntime"));

		Yingjipxyl yingjipxyl = new Yingjipxyl();
		yingjipxyl.setContent(request.getParameter("content"));
		yingjipxyl.setPeixuntime(peixuntime);
		yingjipxyl.setProjectName(request.getParameter("ProjectName"));

		return yingjipxyl;
	}

	public String addYingjipxyl() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Yingjipxyl s = getYingjipxyl();
			String jsonStr = emeRescueService.addYingjipxyl(s, fileName, rootPath);
			// System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
			String OptNote = "添加了培训与演练内容为" + s.getContent() + "的应急培训与演练";
			InsertLog.InsertOptLog(OptNote);
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}

	public String editYingjipxyl() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		// String fileName2 = request.getParameter("fileName2");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Yingjipxyl s = getYingjipxyl();
			s.setId(Integer.parseInt(request.getParameter("ID")));
			s.setAccessory(request.getParameter("Accessory"));
			String jsonStr = emeRescueService.editYingjipxyl(s, fileName, rootPath);
			// System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
			String OptNote = "编辑了培训与演练内容为" + s.getContent() + "的应急培训与演练";
			InsertLog.InsertOptLog(OptNote);
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}

	public String deleteYingjipxyl() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		System.out.println("-------" + ID);
		try {
			String jsonStr = emeRescueService.deleteYingjipxyl(ID);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
			String OptNote = "删除了ID为" + ID + "的应急培训与演练";
			InsertLog.InsertOptLog(OptNote);
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}

	// *******Yingjifenbao*********
	private String type;

	public String getYingjifenbaoListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {

			type = request.getParameter("type");
			String projectName = request.getParameter("projectName");
			String jsonStr = emeRescueService.getYingjifenbaoList("", type, start, limit, projectName);
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

	public String getYingjifenbaoListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = emeRescueService.getYingjifenbaoList(findstr, type, start, limit, projectName);
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

	public Yingjifenbao getYingjifenbao() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = sdf.format(new Date());
		System.out.println(dateStr);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
		java.sql.Date uploadtime = java.sql.Date.valueOf(df.format(new Date()));

		Yingjifenbao yingjifenbao = new Yingjifenbao();
		yingjifenbao.setFenbaoname(request.getParameter("fenbaoname"));
		// yingjifenbao.setFilename(request.getParameter("filename"));
		yingjifenbao.setType(request.getParameter("type"));
		yingjifenbao.setUploadtime(uploadtime);
		yingjifenbao.setProjectName(request.getParameter("ProjectName"));
		return yingjifenbao;
	}

	public String addYingjifenbao() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");

		System.out.println(fileName + "**************************************");
		// String fileName2 = request.getParameter("fileName2");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Yingjifenbao s = getYingjifenbao();

			// System.out.println(s.toString()+"*****************************");

			String jsonStr = emeRescueService.addYingjifenbao(s, fileName, rootPath);

			// System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
			String OptNote = "添加了分包单位名称为" + s.getFenbaoname() + "的应急分包";
			InsertLog.InsertOptLog(OptNote);
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}

	// 编辑添加台账
	public String editYingjifenbao() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		// String fileName2 = request.getParameter("fileName2");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Yingjifenbao s = getYingjifenbao();
			s.setId(Integer.parseInt(request.getParameter("ID")));
			s.setAccessory(request.getParameter("Accessory"));

			// s.setAccessory2(request.getParameter("Accessory2"));
			String jsonStr = emeRescueService.editYingjifenbao(s, fileName, rootPath);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
			String OptNote = "编辑了分包单位名称为" + s.getFenbaoname() + "的应急分包";
			InsertLog.InsertOptLog(OptNote);
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}

	public String deleteYingjifenbao() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		System.out.println("-------" + ID);
		try {
			String jsonStr = emeRescueService.deleteYingjifenbao(ID);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
			String OptNote = "删除了ID为" + ID + "的应急分包";
			InsertLog.InsertOptLog(OptNote);
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}

	// *******Yingjiyuan*****************
	public String getYingjiyuanListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = emeRescueService.getYingjiyuanList("", start, limit, projectName);
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

	public String getYingjiyuanListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = emeRescueService.getYingjiyuanList(findstr, start, limit, projectName);
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

	public Yingjiyuan getYingjiyuan() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = sdf.format(new Date());
		java.sql.Date bianzhitime = java.sql.Date.valueOf(request.getParameter("bianzhitime"));
		java.sql.Date shenhetime = java.sql.Date.valueOf(request.getParameter("shenhetime"));
		java.sql.Date pizhuntime = java.sql.Date.valueOf(request.getParameter("pizhuntime"));
		Yingjiyuan yingjiyuan = new Yingjiyuan();
		yingjiyuan.setZuizhiname(request.getParameter("zuizhiname"));
		yingjiyuan.setBianzhiren(request.getParameter("bianzhiren"));
		yingjiyuan.setShenheren(request.getParameter("shenheren"));
		yingjiyuan.setPizhunren(request.getParameter("pizhunren"));
		yingjiyuan.setBianzhitime(bianzhitime);
		yingjiyuan.setShenhetime(shenhetime);
		yingjiyuan.setPizhuntime(pizhuntime);

		yingjiyuan.setProjectName(request.getParameter("ProjectName"));

		return yingjiyuan;
	}

	public String addYingjiyuan() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Yingjiyuan s = getYingjiyuan();
			String jsonStr = emeRescueService.addYingjiyuan(s, fileName, rootPath);

			// System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
			String OptNote = "添加了方案名称为" + s.getZuizhiname() + "的应急预案";
			InsertLog.InsertOptLog(OptNote);
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}

	public String editYingjiyuan() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		// String fileName2 = request.getParameter("fileName2");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Yingjiyuan s = getYingjiyuan();
			s.setId(Integer.parseInt(request.getParameter("ID")));
			s.setAccessory(request.getParameter("Accessory"));
			String jsonStr = emeRescueService.editYingjiyuan(s, fileName, rootPath);
			// System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
			String OptNote = "编辑了方案名称为" + s.getZuizhiname() + "的应急预案";
			InsertLog.InsertOptLog(OptNote);
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}

	public String deleteYingjiyuan() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		System.out.println("-------" + ID);
		try {
			String jsonStr = emeRescueService.deleteYingjiyuan(ID);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
			String OptNote = "删除了ID为" + ID + "的应急预案";
			InsertLog.InsertOptLog(OptNote);
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
			String tableID = request.getParameter("tableID");
			String projectName = request.getParameter("projectName");
			String rootPath = request.getRealPath("/")+"upload\\";
			int total = emeRescueService.importExcel(type, rootPath, fileName, tableID, projectName);
			outputJSON(response, "{\"result\":\"success\",\"total\":"+total+"}");
		}
		catch(Exception e) {
			e.printStackTrace();
			outputJSON(response, "{\"result\":\"failed\"}");
		}
	}
}
