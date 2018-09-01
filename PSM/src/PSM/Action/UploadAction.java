package PSM.Action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

public class UploadAction extends ActionSupport {
	private String Filedata;
	private String Filename;
	private String path;

	public void setFiledata(String Filedata) {
		this.Filedata = Filedata;
	}

	public String getFiledata() {
		return Filedata;
	}

	public void setFilename(String Filename) {
		this.Filename = Filename;
	}

	public String getFilename() {
		return Filename;
	}

	public UploadAction() throws Exception {

	}

	public void writeJSON(String json) throws Exception {
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		out.write(json);
		out.flush();
		out.close();
	}

	public String execute() {
		String successJson = "{\"success\":\"true\",";
		HttpServletRequest request = ServletActionContext.getRequest();
		String table = request.getParameter("table");
		String item = request.getParameter("item");
		String end = request.getParameter("end");
		String tableitem = request.getParameter("tableitem");
		System.out.println("table:" + table + "begin:" + item + "end" + end + "name" + tableitem);
		try {
			String rootPath = request.getRealPath("/") + "upload\\";
			path = rootPath + "temp\\";

			File directory1 = new File(rootPath); // 根目录是否存在
			if (!directory1.exists())
				directory1.mkdir();
			File directory2 = new File(path); // 根目录是否存在
			if (!directory2.exists())
				directory2.mkdir();

			// 解决重名文件问题
			String oldFilename = getFilename();
			int pointIndex = oldFilename.lastIndexOf(".");
			String oldFullname = oldFilename.substring(0, pointIndex);
			String extension = oldFilename.substring(pointIndex + 1, oldFilename.length());
			File file = new File(path + getFilename());
			int index = 1;
			while (file.exists()) {
				String temp = oldFullname + "(" + index + ")." + extension;
				file = new File(path + temp);
				index++;
			}
			String newFilename = file.getName();
			FileInputStream fis = new FileInputStream(getFiledata());
			FileOutputStream fos = new FileOutputStream(path + newFilename);
			byte[] buf = new byte[10240];
			int length;
			while ((length = fis.read(buf)) > 0) {
				fos.write(buf, 0, length);
			}
			fis.close();
			fos.close();
			successJson += "\"newFilename\":\"" + newFilename + "\"";
			// *******************************************************//在其他项目
			// 没有传table,则table==null
			if (table == null || newFilename.indexOf(".doc") == -1 || newFilename.indexOf(table) == -1) {
				successJson += "}";
			} else {
				successJson += getcontent(path + newFilename, table, item, end, tableitem);
			}
			System.out.println(successJson);
			// *******************************************************
			writeJSON(successJson);
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}

	// *******************************************************
	public String getcontent(String path, String table, String begin, String end, String tableitem) throws Exception {
		String jsonStr = new String();
		File wordfile = new File(path);
		// System.out.println(path);
		String str = "";
		String tmpstr = "";
		String docstr = "";
		FileInputStream worldfis = new FileInputStream(wordfile);
		if (path.endsWith(".doc")) {
			HWPFDocument doc = new HWPFDocument(worldfis);
			docstr = doc.getDocumentText();
			end = end.replace("TAB", "\007").replace("Tab", "\007").replace("TCR", "\r").replace("CR", "\r");
		} else if (path.endsWith(".docx")) {
			XWPFDocument xdoc = new XWPFDocument(worldfis);
			XWPFWordExtractor extractor = new XWPFWordExtractor(xdoc);
			docstr = extractor.getText();
			end = end.replace("TAB", "\t").replace("Tab", "\n").replace("TCR", "\t").replace("CR", "\n");
		}
		worldfis.close();
		// System.out.println(docstr);
		String[] tableitemArr = tableitem.split("\\|");
		String[] beginArr = begin.split("\\|");
		String[] endArr = end.split("\\|");

		// *****************
//		System.out.println(docstr);
		String problem = getProblem(docstr);
		String advice = getadv(docstr);
		jsonStr = jsonStr + ",\"saftycheck_problem\":" + "\"" + problem + "\"";
		jsonStr = jsonStr + ",\"saftycheck_advice\":" + "\"" + advice + "\"";
		// *****************
		for (int i = 0; i < beginArr.length; i++) {
			str = "";
			int indexOfbegin = docstr.indexOf(beginArr[i]);
			int indexOfend = docstr.indexOf(endArr[i], indexOfbegin + beginArr[i].length() + 1);
			if (indexOfbegin > 0 && indexOfend > indexOfbegin + beginArr[i].length() + 1) {
				tmpstr = docstr.substring(indexOfbegin + beginArr[i].length() + 1, indexOfend);
				if (tableitemArr[i].contains("time"))
					tmpstr = tmpstr.replace("年", "-").replace("月", "-").replace("日", "");
				char Lastc = tmpstr.charAt(tmpstr.length() - 1);
				if (Lastc == '\r' || Lastc == '\n' || Lastc == '\t')
					tmpstr = tmpstr.substring(0, tmpstr.length() - 1); // 去掉最后一个换行
				tmpstr = tmpstr.replace("\r", "<br>").replace("\n", "<br>").replace("\t", "<br>");
				for (int k = 0; k < tmpstr.length(); k++) {
					char c = tmpstr.charAt(k);
					if (c >= 32)
						str += c;
					else
						System.out.println((int) (c));
				}
			}
			jsonStr = jsonStr + ",\"" + tableitemArr[i] + "\":" + "\"" + str + "\"";
		}
		System.out.println(jsonStr);
		jsonStr += "}";
		return jsonStr;
	}

	
	//**********************yangtong***********************************
	private String getProblem(String docstr) {
		int indexOfbegin = docstr.indexOf("（需整改）");
		int indexOfend = docstr.indexOf("三");
		String problem = "";
		String[] problems = new String[] {};
		if (indexOfbegin > 0 && indexOfend > indexOfbegin + "（需整改）".length() + 1) {
			problem = docstr.substring(indexOfbegin + "（需整改）".length() + 1, indexOfend);

			char Lastc = problem.charAt(problem.length() - 1);
			if (Lastc == '\r' || Lastc == '\n' || Lastc == '\t')
				problem = problem.substring(0, problem.length() - 1); // 去掉最后一个换行
			problem = problem.replace("\r", "<br>").replace("\n", "<br>").replace("\t", "<br>").replace(" ", "")
					.replace("<br>", "");
		}
//		System.out.println("*********" + problem + "*********");
		problems = problem.split("、");
		int flag = 0;
		problem = "";
		for (int i = 0; i < problems.length; i++) {
			if (i > 0) {
				if (i < problems.length - 1) {
					problems[i] = problems[i].substring(0, problems[i].length() - 1);
				}
				if (flag == 0) {
					problem += problems[i];
					flag++;
				} else {
					problem = problem + "*" + problems[i];
				}
			}
		}
		System.out.println(problem);
		return problem;
	}
	
	
	private String getadv(String docstr) {

		int indexOfbegin = docstr.indexOf("四、进一步改进建议");
		int indexOfend = docstr.indexOf("整改要求");
		String advice = "";
		String[] advices = new String[] {};
		if (indexOfbegin > 0 && indexOfend > indexOfbegin + "四、进一步改进建议".length() + 1) {
			advice = docstr.substring(indexOfbegin + "四、进一步改进建议".length() + 1, indexOfend);

			char Lastc = advice.charAt(advice.length() - 1);
			if (Lastc == '\r' || Lastc == '\n' || Lastc == '\t')
				advice = advice.substring(0, advice.length() - 1); // 去掉最后一个换行
			advice = advice.replace("\r", "<br>").replace("\n", "<br>").replace("\t", "<br>").replace(" ", "")
					.replace("<br>", "");
		}
//		System.out.println("*********" + problem + "*********");
		advices = advice.split("、");
		int flag = 0;
		advice = "";
		for (int i = 0; i < advices.length; i++) {
			if (i > 0) {
				if (i < advices.length - 1) {
					advices[i] = advices[i].substring(0, advices[i].length() - 1);
				}
				if (flag == 0) {
					advice += advices[i];
					flag++;
				} else {
					advice = advice + "*" + advices[i];
				}
			}
		}
		System.out.println(advice);
		return advice;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
