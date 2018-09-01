package PSM.Action;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

import PSM.Service.SaftyCheckService;
import hibernate.Fenbaoyinhuanpczlgzfa;
import hibernate.Riskfenbao;
import hibernate.Riskprodanger;
import hibernate.Risksafepg;
import hibernate.Saftycheck;
import hibernate.Saftycheckplan;
import hibernate.Saftycheckyearplan;
import hibernate.Saftycheckyearplanfb;
import hibernate.Saftycheckyinhuanpc;
import hibernate.Taizhang;
import hibernate.Taizhangfb;

public class SaftyCkeckAction extends ActionSupport {

	private int start; // 分页查询参
	private int limit; // 分页查询参数
	private SaftyCheckService saftyCheckService;
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

	public SaftyCheckService getSaftyCheckService() {
		return saftyCheckService;
	}

	public void setSaftyCheckService(SaftyCheckService saftyCheckService) {
		this.saftyCheckService = saftyCheckService;
	}

	public String execute() throws Exception {
		return SUCCESS;
	}

	// *******saftycheck*********
	public String getSaftycheckListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {

			type = request.getParameter("type");
			String projectName = request.getParameter("projectName");
			String jsonStr = saftyCheckService.getSaftycheckList("", type, start, limit, projectName);
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

	public String getSaftycheckListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = saftyCheckService.getSaftycheckList(findstr, type, start, limit, projectName);
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

	public Saftycheck getSaftycheck() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 2016-10-26 15:51:00
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = sdf.format(new Date());
		System.out.println(dateStr);
		java.sql.Date checktime = java.sql.Date.valueOf(request.getParameter("checktime"));

		String timelinestr = request.getParameter("timeline");
		String replytimestr = request.getParameter("replytime");
		java.sql.Date timeline = null, replytime = null;
		if (timelinestr != null && timelinestr != "") {
			timeline = java.sql.Date.valueOf(timelinestr);
		}
		if (replytimestr != null && replytimestr != "") {
			replytime = java.sql.Date.valueOf(replytimestr);
		}

		Saftycheck saftycheck = new Saftycheck();
		saftycheck.setChecktime(checktime);

		saftycheck.setCheckunit(request.getParameter("checkunit"));

		saftycheck.setShoujianunit(request.getParameter("shoujianunit"));
		saftycheck.setCheckperson(request.getParameter("checkperson"));

		saftycheck.setNoticeandnum(request.getParameter("noticeandnum"));

		String replyandnumstr = request.getParameter("replyandnum");
		if (replyandnumstr != null && replyandnumstr != "") {
			saftycheck.setReplyandnum(replyandnumstr);
		} else {
			saftycheck.setReplyandnum("");
		}
		saftycheck.setTimeline(timeline);
		saftycheck.setReplytime(replytime);

		StringBuilder problem = new StringBuilder("");
		int proNum = 0;
		int flag = 0;
		for (int i = 1; i <= 9; i++) {
			String t = request.getParameter("problem" + i);
			if (t != null && !t.isEmpty()) {
				String tmp = t.replaceAll(" ", "");
				if (tmp != null && !tmp.isEmpty()) {
					if (flag == 0) {
						problem.append(tmp);
						proNum++;
						flag = 1;
					} else {
						problem.append("*").append(tmp);
						proNum++;
					}
				}

			}
		}

		StringBuilder prokind = new StringBuilder("");
		int flag2 = 0;
		for (int i = 1; i <= 9; i++) {
			String t = request.getParameter("kind" + i);
			if (t != null && !t.isEmpty()) {
				String tmp = t.replaceAll(" ", "");
				if (tmp != null && !tmp.isEmpty()) {
					if (flag2 == 0) {
						prokind.append(tmp);
						flag2 = 1;
					} else {
						prokind.append("*").append(tmp);
					}
				}
			}
		}

		StringBuilder prodegree = new StringBuilder("");
		int flag3 = 0;
		for (int i = 1; i <= 9; i++) {
			String t = request.getParameter("degree" + i);
			if (t != null && !t.isEmpty()) {
				String tmp = t.replaceAll(" ", "");
				if (tmp != null && !tmp.isEmpty()) {
					if (flag3 == 0) {
						prodegree.append(tmp);
						flag3 = 1;
					} else {
						prodegree.append("*").append(tmp);
					}
				}
			}
		}

		saftycheck.setPronum(proNum);
		saftycheck.setProkind(prokind.toString());
		saftycheck.setProdegree(prodegree.toString());
		saftycheck.setProblem(problem.toString());

		if (proNum == 0) {
			saftycheck.setProblem("无");
			saftycheck.setProkind("");
			saftycheck.setProdegree("");
		}

		StringBuilder advice = new StringBuilder("");
		int advicenum = 0;
		int flag1 = 0;
		for (int i = 1; i <= 9; i++) {
			String t = request.getParameter("advice" + i);

			if (t != null && !t.isEmpty()) {
				String tmp = t.replaceAll(" ", "");
				if (tmp != null && !tmp.isEmpty()) {
					if (flag1 == 0) {
						advice.append(tmp);
						advicenum++;
						flag1 = 1;
					} else {
						advice.append("*").append(tmp);
						advicenum++;
					}
				}

			}
		}

		saftycheck.setAdvicenum(advicenum);
		saftycheck.setAdvice(advice.toString());
		if (advicenum == 0) {
			saftycheck.setAdvice("无");
		}

		// 类型判断参考basicinfoAction
		String checktype = request.getParameter("checktype");
		if (checktype == null || checktype == "") {
			saftycheck.setChecktype(request.getParameter("type"));
		} else {
			saftycheck.setChecktype(checktype);
		}

		String last = request.getParameter("last");
		String content = request.getParameter("content");
		if (last != null) {
			saftycheck.setLast(last);
		}
		if (content != null) {
			saftycheck.setContent(content);
		}

		saftycheck.setType(request.getParameter("type"));
		saftycheck.setProjectName(request.getParameter("ProjectName"));
		// System.out.println(saftycheck.toString() +
		// "7777777777777777777777777777");
		return saftycheck;
	}

	public String addSaftycheck() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");

		System.out.println(fileName + "**************************************");
		// String fileName2 = request.getParameter("fileName2");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Saftycheck s = getSaftycheck();
			
			

			// System.out.println(s.toString()+"*****************************");

			String jsonStr = saftyCheckService.addSaftycheck(s, fileName, rootPath);
			String OptNote = "添加了检查时间为" + s.getChecktime() + "的安全检查";
			InsertLog.InsertOptLog(OptNote);
			
			
			//如果是 专项检查里 面的  分包目标专项检查 
//			if (s.getType().equals("专项安全检查")&&s.getChecktype().equals("分包目标专项检查")) {
//				s.setChecktype("分包目标专项检查");
//				s.setType("分包方目标管理");
//				
//				
//				String jsonStr2 = saftyCheckService.addSaftycheck(s, fileName, rootPath);
//				System.out.println(s.getType()+"***********"+s.getChecktype()+"&&&&&&&&&&&&&&&&&&&&&&&&&&&&&"+jsonStr2);
//				
//			}

			// 插入台账表Taizhang
			String[] problems = s.getProblem().split("[*]");
			for (int i = 0; i < problems.length; i++) {
				addTaizhang(s, problems[i], i + 1, fileName, rootPath, s.getProjectName(), s.getType());
			}

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

	// 附件问题：是否都是这个附件
	private void addTaizhang(Saftycheck s, String problem, int no, String fileName, String rootPath, String projectName,
			String type) {
		Taizhang taizhang = new Taizhang();
		taizhang.setProblem(problem);
		taizhang.setNo(no);
		taizhang.setCheckId(s.getId());
		taizhang.setProlevel("一般");
		taizhang.setExpTime(s.getTimeline());
		taizhang.setCheckperson(s.getCheckperson());
		taizhang.setProjectName(projectName);
		taizhang.setType(type);
		saftyCheckService.addTaizhang(taizhang, fileName, rootPath);

	}

	// 编辑添加台账
	public String editSaftycheck() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		// String fileName2 = request.getParameter("fileName2");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Saftycheck s = getSaftycheck();
			s.setId(Integer.parseInt(request.getParameter("ID")));
			s.setAccessory(request.getParameter("Accessory"));

			// s.setAccessory2(request.getParameter("Accessory2"));
			String jsonStr = saftyCheckService.editSaftycheck(s, fileName, rootPath);
			
			//删除Taizhang 中checkID和 s.id中一样的，再重新添加
			String json = saftyCheckService.deleteTaizhangByCheckID(s,start,limit);
			System.out.println("editsaftycheck---deleteTaizhang:"+json);
			String[] problems = s.getProblem().split("[*]");
			for (int i = 0; i < problems.length; i++) {
				addTaizhang(s, problems[i], i + 1, fileName, rootPath, s.getProjectName(), s.getType());
			}
			
			
			String OptNote = "编辑了检查时间为" + s.getChecktime() + "的安全检查";
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

	public String deleteSaftycheck() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		System.out.println("-------" + ID);
		try {
			String jsonStr = saftyCheckService.deleteSaftycheck(ID);
			String OptNote = "删除了ID为" + ID + "的安全检查";
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

	// ***************Taizhang表
	public String getTaizhangListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			// type = request.getParameter("type");
			String projectName = request.getParameter("projectName");
			String jsonStr = saftyCheckService.getTaizhangList("", start, limit, projectName);
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

	public String getTaizhangListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = saftyCheckService.getTaizhangList(findstr, start, limit, projectName);
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

	public Taizhang getTaizhang() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		java.sql.Date solveTime = java.sql.Date.valueOf(request.getParameter("solveTime"));

		Taizhang taizhang = new Taizhang();
		taizhang.setNo(Integer.parseInt(request.getParameter("no")));
		taizhang.setCheckId(Integer.parseInt(request.getParameter("checkId")));
		taizhang.setProblem(request.getParameter("problem"));
		taizhang.setSolveTime(solveTime);
		return taizhang;
	}

	public void addTaizhang() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");

		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Taizhang s = getTaizhang();

			String jsonStr = saftyCheckService.addTaizhang(s, fileName, rootPath);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	public void editTaizhang() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		// String fileName2 = request.getParameter("fileName2");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Taizhang s = getTaizhang();
			s.setId(Integer.parseInt(request.getParameter("ID")));
			s.setAccessory(request.getParameter("Accessory"));
			// s.setAccessory2(request.getParameter("Accessory2"));
			String jsonStr = saftyCheckService.editTaizhang(s, fileName, rootPath);
			String OptNote = "编辑了问题为" + s.getProblem() + "的隐患排查台账";
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

	public void deleteTaizhang() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		System.out.println("-------" + ID);
		try {
			String jsonStr = saftyCheckService.deleteTaizhang(ID);
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
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
			String fileLength = saftyCheckService.getFileInfo(filePath);

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
			String jsonStr = saftyCheckService.deleteAllFile(ppID, fileName, rootPath);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
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
			String jsonStr = saftyCheckService.deleteOneFile(ppID, fileName, rootPath);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	// -----------------------LZZ-----------------//
	public void chkdispatch() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		String userName = request.getParameter("userName");
		System.out.println("user+++" + userName);
		String ID = request.getParameter("ID");
		System.out.println("id++++" + ID);
		try {
			String jsonStr = saftyCheckService.chkdispatch(userName, ID);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
		// return SUCCESS;
	}

	// *******Saftycheckplan*****************
	public String getSaftycheckplanListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = saftyCheckService.getSaftycheckplanList("", start, limit, projectName);
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

	public String getSaftycheckplanListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = saftyCheckService.getSaftycheckplanList(findstr, start, limit, projectName);
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

	public Saftycheckplan getSaftycheckplan() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 2016-10-26 15:51:00
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = sdf.format(new Date());
		System.out.println(dateStr);
		java.sql.Date bztime = java.sql.Date.valueOf(request.getParameter("bztime"));
		java.sql.Date sptime = java.sql.Date.valueOf(request.getParameter("sptime"));
		Saftycheckplan saftycheckplan = new Saftycheckplan();
		saftycheckplan.setBzren(request.getParameter("bzren"));
		saftycheckplan.setSpren(request.getParameter("spren"));
		saftycheckplan.setBztime(bztime);
		saftycheckplan.setSptime(sptime);
		saftycheckplan.setProjectName(request.getParameter("ProjectName"));

		return saftycheckplan;
	}

	public String addSaftycheckplan() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Saftycheckplan s = getSaftycheckplan();
			String jsonStr = saftyCheckService.addSaftycheckplan(s, fileName, rootPath);
			String OptNote = "添加了编制时间为" + s.getBztime() + "的项目部安全检查计划";
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

	public String editSaftycheckplan() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		// String fileName2 = request.getParameter("fileName2");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Saftycheckplan s = getSaftycheckplan();
			s.setId(Integer.parseInt(request.getParameter("ID")));
			s.setAccessory(request.getParameter("Accessory"));

			// s.setAccessory2(request.getParameter("Accessory2"));
			String jsonStr = saftyCheckService.editSaftycheckplan(s, fileName, rootPath);
			String OptNote = "编辑了编制时间为" + s.getBztime() + "的项目部安全检查计划";
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

	public String deleteSaftycheckplan() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		System.out.println("-------" + ID);
		try {
			String jsonStr = saftyCheckService.deleteSaftycheckplan(ID);
			String OptNote = "删除了ID为" + ID + "的项目部安全检查计划";
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

	// *******Saftycheckyearplan*****************
	public String getSaftycheckyearplanListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = saftyCheckService.getSaftycheckyearplanList("", start, limit, projectName);
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

	public String getSaftycheckyearplanListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = saftyCheckService.getSaftycheckyearplanList(findstr, start, limit, projectName);
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

	public Saftycheckyearplan getSaftycheckyearplan() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 2016-10-26 15:51:00
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = sdf.format(new Date());
		System.out.println(dateStr);
		java.sql.Date bztime = java.sql.Date.valueOf(request.getParameter("bztime"));
		java.sql.Date sptime = java.sql.Date.valueOf(request.getParameter("sptime"));
		Saftycheckyearplan saftycheckyearplan = new Saftycheckyearplan();
		saftycheckyearplan.setBzren(request.getParameter("bzren"));
		saftycheckyearplan.setSpren(request.getParameter("spren"));
		saftycheckyearplan.setBztime(bztime);
		saftycheckyearplan.setSptime(sptime);
		saftycheckyearplan.setProjectName(request.getParameter("ProjectName"));
		System.out.println(request.getParameter("ProjectName") + "9999999999999999999");

		return saftycheckyearplan;
	}

	public String addSaftycheckyearplan() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Saftycheckyearplan s = getSaftycheckyearplan();
			String jsonStr = saftyCheckService.addSaftycheckyearplan(s, fileName, rootPath);
			String OptNote = "添加了编制时间为" + s.getBztime() + "的项目部年度安全检查计划";
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

	public String editSaftycheckyearplan() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		// String fileName2 = request.getParameter("fileName2");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Saftycheckyearplan s = getSaftycheckyearplan();
			s.setId(Integer.parseInt(request.getParameter("ID")));
			s.setAccessory(request.getParameter("Accessory"));

			// s.setAccessory2(request.getParameter("Accessory2"));
			String jsonStr = saftyCheckService.editSaftycheckyearplan(s, fileName, rootPath);
			String OptNote = "编辑了编制时间为" + s.getBztime() + "的项目部年度安全检查计划";
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

	public String deleteSaftycheckyearplan() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		System.out.println("-------" + ID);
		try {
			String jsonStr = saftyCheckService.deleteSaftycheckyearplan(ID);
			String OptNote = "删除了ID为" + ID + "的项目部年度安全检查计划";
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

	// *******Saftycheckyearplanfb*****************
	public String getSaftycheckyearplanfbListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = saftyCheckService.getSaftycheckyearplanfbList("", start, limit, projectName);
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

	public String getSaftycheckyearplanfbListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = saftyCheckService.getSaftycheckyearplanfbList(findstr, start, limit, projectName);
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

	public Saftycheckyearplanfb getSaftycheckyearplanfb() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 2016-10-26 15:51:00
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = sdf.format(new Date());
		System.out.println(dateStr);
		java.sql.Date bztime = java.sql.Date.valueOf(request.getParameter("bztime"));
		java.sql.Date bbtime = java.sql.Date.valueOf(request.getParameter("bbtime"));
		java.sql.Date sptime = java.sql.Date.valueOf(request.getParameter("sptime"));
		Saftycheckyearplanfb saftycheckyearplanfb = new Saftycheckyearplanfb();
		saftycheckyearplanfb.setBzunit(request.getParameter("bzunit"));
		saftycheckyearplanfb.setBbren(request.getParameter("bbren"));
		saftycheckyearplanfb.setSpren(request.getParameter("spren"));
		saftycheckyearplanfb.setBztime(bztime);
		saftycheckyearplanfb.setSptime(sptime);
		saftycheckyearplanfb.setBbtime(bbtime);
		saftycheckyearplanfb.setProjectName(request.getParameter("ProjectName"));

		return saftycheckyearplanfb;
	}

	public String addSaftycheckyearplanfb() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Saftycheckyearplanfb s = getSaftycheckyearplanfb();
			String jsonStr = saftyCheckService.addSaftycheckyearplanfb(s, fileName, rootPath);
			String OptNote = "添加了编制时间为" + s.getBztime() + "的分包方年度安全检查计划";
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

	public String editSaftycheckyearplanfb() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		// String fileName2 = request.getParameter("fileName2");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Saftycheckyearplanfb s = getSaftycheckyearplanfb();
			s.setId(Integer.parseInt(request.getParameter("ID")));
			s.setAccessory(request.getParameter("Accessory"));

			// s.setAccessory2(request.getParameter("Accessory2"));
			String jsonStr = saftyCheckService.editSaftycheckyearplanfb(s, fileName, rootPath);
			String OptNote = "编辑了编制时间为" + s.getBztime() + "的分包方年度安全检查计划";
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

	public String deleteSaftycheckyearplanfb() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		System.out.println("-------" + ID);
		try {
			String jsonStr = saftyCheckService.deleteSaftycheckyearplanfb(ID);
			String OptNote = "删除了ID为" + ID + "的项目部年度安全检查计划";
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

	// *******Saftycheckyinhuanpc*****************
	public String getSaftycheckyinhuanpcListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = saftyCheckService.getSaftycheckyinhuanpcList("", start, limit, projectName);
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

	public String getSaftycheckyinhuanpcListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = saftyCheckService.getSaftycheckyinhuanpcList(findstr, start, limit, projectName);
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

	public Saftycheckyinhuanpc getSaftycheckyinhuanpc() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 2016-10-26 15:51:00
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// String dateStr = sdf.format(new Date());
		// System.out.println(dateStr);

		// 当前日期
		Date d = new Date();
		System.out.println(d);

		SimpleDateFormat uploadtimesdf = new SimpleDateFormat("yyyy-MM-dd");
		String uploadtime = uploadtimesdf.format(d);
		java.sql.Date uploadtimeset = java.sql.Date.valueOf(uploadtime);
		SimpleDateFormat yearsdf = new SimpleDateFormat("yyyy");
		String year = yearsdf.format(d);

		Saftycheckyinhuanpc saftycheckyinhuanpc = new Saftycheckyinhuanpc();

		saftycheckyinhuanpc.setYear(year);
		saftycheckyinhuanpc.setUploadtime(uploadtimeset);
		// saftycheckyinhuanpc.setUploaduser(request.getParameter("uploaduser"));
		saftycheckyinhuanpc.setProjectName(request.getParameter("ProjectName"));
		return saftycheckyinhuanpc;
	}

	public String addSaftycheckyinhuanpc() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Saftycheckyinhuanpc s = getSaftycheckyinhuanpc();
			String jsonStr = saftyCheckService.addSaftycheckyinhuanpc(s, fileName, rootPath);
			String OptNote = "添加了上传用户为" + s.getUploaduser() + "的隐患排查治理年度工作方案";
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

	public String editSaftycheckyinhuanpc() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		// String fileName2 = request.getParameter("fileName2");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Saftycheckyinhuanpc s = getSaftycheckyinhuanpc();
			s.setId(Integer.parseInt(request.getParameter("ID")));
			s.setAccessory(request.getParameter("Accessory"));

			// s.setAccessory2(request.getParameter("Accessory2"));
			String jsonStr = saftyCheckService.editSaftycheckyinhuanpc(s, fileName, rootPath);
			String OptNote = "编辑了上传用户为" + s.getUploaduser() + "的隐患排查治理年度工作方案";
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

	public String deleteSaftycheckyinhuanpc() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		System.out.println("-------" + ID);
		try {
			String jsonStr = saftyCheckService.deleteSaftycheckyinhuanpc(ID);
			String OptNote = "删除了ID为" + ID + "的隐患排查治理年度工作方案";
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

	// *******Taizhangfb*****************
	public String getTaizhangfbListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = saftyCheckService.getTaizhangfbList("", start, limit, projectName);
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

	public String getTaizhangfbListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = saftyCheckService.getTaizhangfbList(findstr, start, limit, projectName);
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

	public Taizhangfb getTaizhangfb() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = sdf.format(new Date());
		Taizhangfb taizhangfb = new Taizhangfb();
		taizhangfb.setFbname(request.getParameter("fbname"));
		taizhangfb.setYear(request.getParameter("year"));
		taizhangfb.setMonth(request.getParameter("month"));
		taizhangfb.setProjectName(request.getParameter("ProjectName"));
		return taizhangfb;
	}

	public String addTaizhangfb() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Taizhangfb s = getTaizhangfb();
			String jsonStr = saftyCheckService.addTaizhangfb(s, fileName, rootPath);
			String OptNote = "添加了分包方为" + s.getFbname() + "的分包方隐患排查治理台账";
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

	public String editTaizhangfb() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		// String fileName2 = request.getParameter("fileName2");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Taizhangfb s = getTaizhangfb();
			s.setId(Integer.parseInt(request.getParameter("ID")));
			s.setAccessory(request.getParameter("Accessory"));
			String jsonStr = saftyCheckService.editTaizhangfb(s, fileName, rootPath);
			String OptNote = "编辑了分包方为" + s.getFbname() + "的分包方隐患排查治理台账";
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

	public String deleteTaizhangfb() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		System.out.println("-------" + ID);
		try {
			String jsonStr = saftyCheckService.deleteTaizhangfb(ID);
			String OptNote = "删除了ID为" + ID + "的分包方隐患排查治理台账";
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

	// *******Riskfenbao*****************
	public String getRiskfenbaoListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = saftyCheckService.getRiskfenbaoList("", start, limit, projectName);
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

	public String getRiskfenbaoListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = saftyCheckService.getRiskfenbaoList(findstr, start, limit, projectName);
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

	public Riskfenbao getRiskfenbao() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = sdf.format(new Date());

		java.sql.Date bbtime = java.sql.Date.valueOf(request.getParameter("bbtime"));
		Riskfenbao riskfenbao = new Riskfenbao();
		riskfenbao.setFenbaoname(request.getParameter("fenbaoname"));
		riskfenbao.setBbtime(bbtime);
		riskfenbao.setProjectName(request.getParameter("ProjectName"));
		return riskfenbao;
	}

	public String addRiskfenbao() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Riskfenbao s = getRiskfenbao();
			String jsonStr = saftyCheckService.addRiskfenbao(s, fileName, rootPath);
			String OptNote = "添加了分包方名称为" + s.getFenbaoname() + "的分包方危险源管理";
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

	public String editRiskfenbao() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		// String fileName2 = request.getParameter("fileName2");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Riskfenbao s = getRiskfenbao();
			s.setId(Integer.parseInt(request.getParameter("ID")));
			s.setAccessory(request.getParameter("Accessory"));
			String jsonStr = saftyCheckService.editRiskfenbao(s, fileName, rootPath);
			String OptNote = "编辑了分包方名称为" + s.getFenbaoname() + "的分包方危险源管理";
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

	public String deleteRiskfenbao() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		System.out.println("-------" + ID);
		try {
			String jsonStr = saftyCheckService.deleteRiskfenbao(ID);
			String OptNote = "删除了ID为" + ID + "的分包方危险源管理";
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

	// *******Riskprodanger*****************
	public String getRiskprodangerListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = saftyCheckService.getRiskprodangerList("", start, limit, projectName);
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

	public String getRiskprodangerListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = saftyCheckService.getRiskprodangerList(findstr, start, limit, projectName);
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

	public Riskprodanger getRiskprodanger() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = sdf.format(new Date());

		java.sql.Date bstime = java.sql.Date.valueOf(request.getParameter("bstime"));
		Riskprodanger riskprodanger = new Riskprodanger();
		riskprodanger.setBstype(request.getParameter("bstype"));
		riskprodanger.setBstime(bstime);
		riskprodanger.setProjectName(request.getParameter("ProjectName"));
		return riskprodanger;
	}

	public String addRiskprodanger() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Riskprodanger s = getRiskprodanger();
			String jsonStr = saftyCheckService.addRiskprodanger(s, fileName, rootPath);
			String OptNote = "添加了辨识类型为" + s.getBstype() + "的项目危险因素（危险源）辨识";
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

	public String editRiskprodanger() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		// String fileName2 = request.getParameter("fileName2");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Riskprodanger s = getRiskprodanger();
			s.setId(Integer.parseInt(request.getParameter("ID")));
			s.setAccessory(request.getParameter("Accessory"));
			String jsonStr = saftyCheckService.editRiskprodanger(s, fileName, rootPath);
			String OptNote = "编辑了辨识类型为" + s.getBstype() + "的项目危险因素（危险源）辨识";
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

	public String deleteRiskprodanger() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		System.out.println("-------" + ID);
		try {
			String jsonStr = saftyCheckService.deleteRiskprodanger(ID);
			String OptNote = "删除了ID为" + ID + "的项目危险因素（危险源）辨识";
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

	// *******Risksafepg*****************
	public String getRisksafepgListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = saftyCheckService.getRisksafepgList("", start, limit, projectName);
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

	public String getRisksafepgListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = saftyCheckService.getRisksafepgList(findstr, start, limit, projectName);
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

	public Risksafepg getRisksafepg() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = sdf.format(new Date());

		java.sql.Date safeptime = java.sql.Date.valueOf(request.getParameter("safeptime"));
		Risksafepg risksafepg = new Risksafepg();
		risksafepg.setShigstage(request.getParameter("shigstage"));
		risksafepg.setSafeptime(safeptime);
		risksafepg.setBzperson(request.getParameter("bzperson"));
		risksafepg.setShperson(request.getParameter("shperson"));
		risksafepg.setProjectName(request.getParameter("ProjectName"));

		return risksafepg;
	}

	public String addRisksafepg() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Risksafepg s = getRisksafepg();
			String jsonStr = saftyCheckService.addRisksafepg(s, fileName, rootPath);
			String OptNote = "添加了编制人为" + s.getBzperson() + "的安全评估";
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

	public String editRisksafepg() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		// String fileName2 = request.getParameter("fileName2");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Risksafepg s = getRisksafepg();
			s.setId(Integer.parseInt(request.getParameter("ID")));
			s.setAccessory(request.getParameter("Accessory"));
			String jsonStr = saftyCheckService.editRisksafepg(s, fileName, rootPath);
			String OptNote = "添加了编制人为" + s.getBzperson() + "的安全评估";
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

	public String deleteRisksafepg() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		System.out.println("-------" + ID);
		try {
			String jsonStr = saftyCheckService.deleteRisksafepg(ID);
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

	// *******Fenbaoyinhuanpczlgzfa*****************
	public String getFenbaoyinhuanpczlgzfaListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			String jsonStr = saftyCheckService.getFenbaoyinhuanpczlgzfaList("", start, limit, projectName);
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

	public String getFenbaoyinhuanpczlgzfaListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String projectName = request.getParameter("projectName");
			String jsonStr = saftyCheckService.getFenbaoyinhuanpczlgzfaList(findstr, start, limit, projectName);
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

	public Fenbaoyinhuanpczlgzfa getFenbaoyinhuanpczlgzfa() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = sdf.format(new Date());

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
		java.sql.Date uploadtime = java.sql.Date.valueOf(df.format(new Date()));

		Fenbaoyinhuanpczlgzfa fenbaoyinhuanpczlgzfa = new Fenbaoyinhuanpczlgzfa();
		fenbaoyinhuanpczlgzfa.setYear(request.getParameter("year"));
		fenbaoyinhuanpczlgzfa.setFenbaoname(request.getParameter("fenbaoname"));
		fenbaoyinhuanpczlgzfa.setWorkname(request.getParameter("workname"));
		// fenbaoyinhuanpczlgzfa.setFilename(request.getParameter("filename"));
		fenbaoyinhuanpczlgzfa.setUploadtime(uploadtime);
		fenbaoyinhuanpczlgzfa.setProjectName(request.getParameter("ProjectName"));

		return fenbaoyinhuanpczlgzfa;
	}

	public String addFenbaoyinhuanpczlgzfa() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Fenbaoyinhuanpczlgzfa s = getFenbaoyinhuanpczlgzfa();
			String jsonStr = saftyCheckService.addFenbaoyinhuanpczlgzfa(s, fileName, rootPath);

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

	public String editFenbaoyinhuanpczlgzfa() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String fileName = request.getParameter("fileName");
		// String fileName2 = request.getParameter("fileName2");
		String rootPath = request.getRealPath("/") + "upload\\";
		try {
			Fenbaoyinhuanpczlgzfa s = getFenbaoyinhuanpczlgzfa();
			s.setId(Integer.parseInt(request.getParameter("ID")));
			s.setAccessory(request.getParameter("Accessory"));
			String jsonStr = saftyCheckService.editFenbaoyinhuanpczlgzfa(s, fileName, rootPath);
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

	public String deleteFenbaoyinhuanpczlgzfa() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		System.out.println("-------" + ID);
		try {
			String jsonStr = saftyCheckService.deleteFenbaoyinhuanpczlgzfa(ID);
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

	// jlf===============
	public String getSaftycheck326ListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {

			type = request.getParameter("type");
			String jsonStr = saftyCheckService.getSaftycheck326List("", start, limit);
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

	public String getSaftycheck326ListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String jsonStr = saftyCheckService.getSaftycheck326List(findstr, start, limit);
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

	public String getSaftycheck327ListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {

			type = request.getParameter("type");
			String jsonStr = saftyCheckService.getSaftycheck327List("", start, limit);
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

	public String getSaftycheck327ListSearch() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String findstr = request.getParameter("findStr");
			String jsonStr = saftyCheckService.getSaftycheck327List(findstr, start, limit);
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
	// end=============

	// 安全检查发现问题统计
	public String getfaxianwentiListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {

			type = request.getParameter("type");
			String projectName = request.getParameter("projectName");
			String jsonStr = saftyCheckService.getfaxianwentiList("", type, start, limit, projectName);
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

	// 安全检查发现问题统计
	public String getyinhuanpaichaListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {

			type = request.getParameter("type");
			String projectName = request.getParameter("projectName");
			String jsonStr = saftyCheckService.getyinhuanpaichaList("", type, start, limit, projectName);
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

	public String getColumnDataListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			if (projectName.equals("全部")) {
				projectName = "";
			}
			type = request.getParameter("type");
			String kind = request.getParameter("kind");
			if (kind.equals("全部")) {
				kind = "";
			}
			;
			String jsonStr = saftyCheckService.getColumnDataList("", type, start, limit, projectName, kind);
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

	public String getColumnData2ListDef() {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String projectName = request.getParameter("projectName");
			if (projectName.equals("全部")) {
				projectName = "";
			}
			type = request.getParameter("type");
			// String kind = request.getParameter("kind");
			String jsonStr = saftyCheckService.getColumnData2List("", type, start, limit, projectName);
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

//	public String getChartData() {
//		HttpServletResponse response = ServletActionContext.getResponse();
//		try {
//
//			String jsonStr = "{\"total\":\"" + 4 + "\",\"rows\":[";
//
//			jsonStr += "{\"year\":\"" + 2010 + "\",\"total\":\"" + 56 + "\"},";
//			jsonStr += "{\"year\":\"" + 2011 + "\",\"total\":\"" + 43 + "\"},";
//			jsonStr += "{\"year\":\"" + 2012 + "\",\"total\":\"" + 78 + "\"},";
//			jsonStr += "{\"year\":\"" + 2013 + "\",\"total\":\"" + 250 + "\"}";
//
//			jsonStr += "]}";
//			System.out.println(jsonStr);
//			response.setCharacterEncoding("UTF-8");
//			PrintWriter out = response.getWriter();
//			out.write(jsonStr);
//			out.flush();
//			out.close();
//		}
//
//		catch (Exception e) {
//			e.printStackTrace();
//			return ERROR;
//		}
//		return SUCCESS;
//	}
//	// 导出统计图表为图片
//	public void downImage() {
//
//		HttpServletResponse response = ServletActionContext.getResponse();
//		HttpServletRequest request = ServletActionContext.getRequest();
//		String type = request.getParameter("type");
//		String svg = request.getParameter("svg");
//
//		String filename = "chart";
//		if (request.getParameter("filename") != null && request.getParameter("filename").length() > 0) {
//			filename = request.getParameter("filename");
//		}
//
//		String postfix;
//		if ("image/jpeg".equals(type)) {
//			response.setContentType("image/jpeg");
//			postfix = "jpg";
//		} else {
//			response.setContentType("image/png");
//			postfix = "png";
//		}
//		response.setHeader("Content-Disposition", "attachment; filename=" + filename + "." + postfix);
//
//		try {
//			System.out.println("svg-------------" + svg);
//			StringReader stringReader = new StringReader(svg);
//			OutputStream out = response.getOutputStream();
//
//			TranscoderInput input = new TranscoderInput(stringReader);
//			TranscoderOutput output = new TranscoderOutput(out);
//
//			if ("image/jpeg".equals(type)) {
//				JPEGTranscoder t = new JPEGTranscoder();
//				t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(0.9));
//				t.transcode(input, output);
//				// new JPEGTranscoder().transcode(input, output);
//			} else {
//				new PNGTranscoder().transcode(input, output);
//			}
//
//			out.flush();
//			out.close();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			// return ERROR;
//		}
//		// return SUCCESS;
//	}

	public void downImage() {
		
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String type = request.getParameter("type");
		String svg = request.getParameter("svg");

		String filename = "chart";
		if (request.getParameter("filename") != null && request.getParameter("filename").length() > 0) {
			filename = request.getParameter("filename");
		}

		
		String prefix = type.equals("image/png") ? ".png" : ".jpg";
		response.setContentType(type);
		try {
			OutputStream os = response.getOutputStream();
			response.addHeader("Content-Disposition",
					"attachment;filename=" + new String((filename + prefix).trim().getBytes("gb2312"), "ISO8859-1"));
			StringReader reader = new StringReader(svg);
			TranscoderInput input = new TranscoderInput(reader);
			TranscoderOutput output = new TranscoderOutput(os);
			Transcoder t = null;
			if ("image/png".equals(type)) {
				t = new PNGTranscoder();
			} else if ("image/jpeg".equals(type)) {
				t = new JPEGTranscoder();
			}
			t.transcode(input, output);
			os.flush();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TranscoderException e) {
			e.printStackTrace();
		}
//		return "";
	}

}
