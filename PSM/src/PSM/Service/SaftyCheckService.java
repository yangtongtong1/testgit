package PSM.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipOutputStream;

import org.apache.struts2.components.Else;
import org.apache.tools.zip.ZipEntry;

import PSM.DAO.SaftyCheckDAO;
import PSM.Tool.ExcelToPdf;
import PSM.Tool.WordToPdf;
import hibernate.Fenbaoyinhuanpczlgzfa;
import hibernate.Projectmanagement;
import hibernate.Riskfenbao;
import hibernate.Riskprodanger;
import hibernate.Risksafepg;
import hibernate.Saftycheck;
import hibernate.Saftycheckplan;
import hibernate.Saftycheckyearplan;
import hibernate.Saftycheckyearplanfb;
import hibernate.Saftycheckyinhuanpc;
import hibernate.Saftyproblem;
import hibernate.Taizhang;
import hibernate.Taizhangfb;

public class SaftyCheckService {

	private SaftyCheckDAO saftyCheckDAO;

	public SaftyCheckDAO getSaftyCheckDAO() {
		return saftyCheckDAO;
	}

	public void setSaftyCheckDAO(SaftyCheckDAO saftyCheckDAO) {
		this.saftyCheckDAO = saftyCheckDAO;
	}

	public String getSaftycheckList(String findstr, String type, int start, int limit, String projectName) {
		List<Saftycheck> list = saftyCheckDAO.getSaftycheckList(findstr, type, start, limit, projectName);
		int total = saftyCheckDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Saftycheck s = list.get(i);
			jsonStr += "{\"ID\":\"" + s.getId() + "\",\"checktime\":\"" + s.getChecktime() + "\",\"checktype\":\""
					+ s.getChecktype() + "\",\"checkunit\":\"" + s.getCheckunit() + "\",\"shoujianunit\":\""
					+ s.getShoujianunit() + "\",\"checkperson\":\"" + s.getCheckperson() + "\",\"noticeandnum\":\""
					+ s.getNoticeandnum() + "\",\"problem\":\"" + s.getProblem() + "\",\"pronum\":\"" + s.getPronum()
					+ "\",\"prodegree\":\"" + s.getProdegree() + "\",\"prokind\":\"" + s.getProkind()
					+ "\",\"advice\":\"" + s.getAdvice() + "\",\"advicenum\":\"" + s.getAdvicenum()
					+ "\",\"timeline\":\"" + s.getTimeline() + "\",\"iscorrective\":\"" + s.getIscorrective()
					+ "\",\"replytime\":\"" + s.getReplytime() + "\",\"replyandnum\":\"" + s.getReplyandnum()
					+ "\",\"Accessory\":\"" + s.getAccessory() + "\",\"Accessory2\":\"" + s.getAccessory2()
					+ "\",\"last\":\"" + s.getLast() + "\",\"content\":\"" + s.getContent() + "\",\"type\":\""
					+ s.getType() + "\",\"ProjectName\":\"" + s.getProjectName() + "\"}";

			System.out.println(jsonStr);
		}
		jsonStr += "]}";
		// System.out.println(jsonStr);
		return jsonStr;
	}

	public String addSaftycheck(Saftycheck s, String fileName, String rootPath) {
		String json = "{\"success\":\"true\"}";
		// 有可能需要用到当前时间作为编号
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");// 设置日期格式
		String mytime = df.format(new Date());

		// 分别获取当前年月日
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + 1;
		int day = now.get(Calendar.DATE);
		String sy = String.valueOf(year);
		String sm = String.valueOf(month);
		if (month < 10) {
			sm = "0" + sm;
		}
		String sd = String.valueOf(day);
		if (day < 10) {
			sd = "0" + sd;
		}

		// 建立月份和项目名文件夹
		String ym = rootPath + sy + sm + "\\";
		// String ympname = ym + p.getName() + "\\";
		String ympname = ym + s.getId() + "\\";
		File directory1 = new File(ym); // 根目录是否存在
		if (!directory1.exists())
			directory1.mkdir();
		File directory2 = new File(ympname); // 根目录是否存在
		if (!directory2.exists())
			directory2.mkdir();

		// 拷贝temp中的文件到正确的文件夹
		String[] newFile = fileName.split("\\*");
		for (String copyFile : newFile) {
			// cutGeneralFile( rootPath+"temp\\"+copyFile,ym + p.getName());
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym + s.getId());
		}

		// 存入数据库的内容
		// String accessory = sy+sm + "*" + p.getName() + "*"+ fileName;
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}
		String accessory = sy + sm + "*" + s.getId() + "*" + fileName;

		// 打包
		// byte[] buffer = new byte[1024];
		// if (fileName.length() > 0) {
		// // String strZipName = ympname + sy + sm + sd + p.getName()+".zip";
		// String strZipName = ympname + sy + sm + sd + s.getId() + ".zip";
		// System.out.println("压缩包名称:" + strZipName);
		// try {
		// ZipOutputStream out = new ZipOutputStream(new
		// FileOutputStream(strZipName));
		// String[] allfile = fileName.split("\\*");
		// System.out.println(Arrays.toString(allfile));
		// for (int i = 0; i < allfile.length; i++) {
		// File tempfile = new File(ympname + allfile[i]);
		// if (tempfile.exists()) {
		// FileInputStream fis = new FileInputStream(tempfile);
		// out.putNextEntry(new ZipEntry(tempfile.getName()));
		// int len;
		// while ((len = fis.read(buffer)) > 0) {
		// out.write(buffer, 0, len);
		// }
		// out.closeEntry();
		// fis.close();
		// }
		// }
		// // accessory = accessory+sy+sm+sd+p.getName()+".zip";
		// accessory = accessory + sy + sm + sd + s.getId() + ".zip";
		// out.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }

		// doc转pdf
		if (fileName.length() != 0) {
			String[] allFile = fileName.split("\\*");
			for (String temp : allFile) {
				if (temp.endsWith(".doc")) {
					System.out.println(ympname + temp);
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 4) + ".pdf");
				} else if (temp.endsWith(".docx")) {
					System.out.println(ympname + temp);
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 5) + ".pdf");
				} else if (temp.endsWith(".xlsx")) {
					System.out.println("-----------xlsx");
					ExcelToPdf.excel2pdf(ympname + temp, ympname + temp.substring(0, temp.length() - 5) + ".pdf");
				} else if (temp.endsWith("xls"))

				{
					System.out.println("----------xls");
					ExcelToPdf.excel2pdf(ympname + temp, ympname + temp.substring(0, temp.length() - 4) + ".pdf");
				}
			}
		}

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			s.setAccessory(fileName);
		} else {
			s.setAccessory(accessory);
		}

		saftyCheckDAO.insertSaftycheck(s);

		// return addSaftycheck2(s, fileName2, rootPath);
		return json;
	}

	public String editSaftycheck(Saftycheck s, String fileName, String rootPath) {
		int oldAccessoryNone = 0;
		if (s.getAccessory() == null || s.getAccessory().length() <= 0) {
			oldAccessoryNone = 1;
		}
		String json = "{\"success\":\"true\"}";
		// 有可能需要用到当前时间作为编号
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");// 设置日期格式
		String mytime = df.format(new Date());

		// 分别获取当前年月日
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + 1;
		int day = now.get(Calendar.DATE);
		String sy = String.valueOf(year);
		String sm = String.valueOf(month);
		if (month < 10) {
			sm = "0" + sm;
		}
		String sd = String.valueOf(day);
		if (day < 10) {
			sd = "0" + sd;
		}

		if ((s.getAccessory() == null || s.getAccessory().length() <= 0)
				&& (fileName != null && fileName.length() > 0)) {
			// 建立月份和项目名文件夹
			String ym = rootPath + sy + sm + "\\";
			// String ympname = ym + p.getName() + "\\";
			String ympname = ym + s.getId() + "\\";
			File directory1 = new File(ym); // 根目录是否存在
			if (!directory1.exists())
				directory1.mkdir();
			File directory2 = new File(ympname); // 根目录是否存在
			if (!directory2.exists())
				directory2.mkdir();
		}

		String oldAccessory = s.getAccessory();
		String[] oldFile = oldAccessory.split("\\*");
		int oldFileLength = oldFile.length;
		String oldFoldName;
		if (oldAccessoryNone == 0) {
			oldFoldName = oldFile[0] + "\\" + oldFile[1];
		} else {
			// oldFoldName = sy + sm +"\\" +p.getName();
			oldFoldName = sy + sm + "\\" + s.getId();
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			// newAccessory = sy + sm + "*" + p.getName() +"*"+fileName;
			newAccessory = sy + sm + "*" + s.getId() + "*" + fileName;
		}

		// ------------------------删除列表中不存在的文件---------------------------//
		for (int i = 2; i < oldFileLength - 1; i++) {
			String existFileInDB = oldFile[i];
			// 列表中是否存在该文件
			boolean hasFileInList = false;
			for (String existFileInList : newFile) {
				if (existFileInDB.equals(existFileInList)) {
					hasFileInList = true;
					break;
				}
			}
			if (!hasFileInList) {
				deleteFile(rootPath + oldFoldName + "\\" + existFileInDB);
			}
		}
		// -----------------------end----------------------------------//
		// 拷贝temp文件夹的新文件到对应文件夹
		for (String oneFile : newFile) {
			boolean hasFileInList = false;
			for (int i = 2; i < oldFileLength - 1; i++) {
				if (oneFile.equals(oldFile[i])) {
					hasFileInList = true;
					break;
				}
			}
			if (!hasFileInList) {
				cutGeneralFile(rootPath + "temp\\" + oneFile, rootPath + oldFoldName);
			}

		}
		if (oldAccessoryNone == 0) {
			// deleteFile(rootPath + oldFoldName + "\\" + oldFile[oldFile.length
			// - 1]);
		}
		// ----------打包------------------//
		// byte[] buffer = new byte[1024];
		// if (fileName.length() > 0) {
		// String strZipName = rootPath + oldFoldName + "\\" + sy + sm + sd +
		// s.getId() + ".zip";
		// try {
		// ZipOutputStream out = new ZipOutputStream(new
		// FileOutputStream(strZipName));
		// String[] allfile = fileName.split("\\*");
		// for (int i = 0; i < allfile.length; i++) {
		// File tempfile = new File(rootPath + oldFoldName + "\\" + allfile[i]);
		// if (tempfile.exists()) {
		// FileInputStream fis = new FileInputStream(tempfile);
		// out.putNextEntry(new ZipEntry(tempfile.getName()));
		// int len;
		// while ((len = fis.read(buffer)) > 0) {
		// out.write(buffer, 0, len);
		// }
		// out.closeEntry();
		// fis.close();
		// }
		// }
		// newAccessory = newAccessory + sy + sm + sd + s.getId() + ".zip";
		// out.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }

		if (fileName.length() != 0) {
			String[] allFile = fileName.split("\\*");
			for (String tempFile : allFile) {
				if (tempFile.endsWith(".doc")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 4) + ".pdf");
				} else if (tempFile.endsWith(".docx")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 5) + ".pdf");
				} else if (tempFile.endsWith(".xlsx")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					ExcelToPdf.excel2pdf(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 5) + ".pdf");
				} else if (tempFile.endsWith(".xls")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					ExcelToPdf.excel2pdf(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 4) + ".pdf");
				}
			}
		}

		// *************************
		newAccessory = newAccessory.substring(0, newAccessory.length() - 1);

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			s.setAccessory(fileName);
		} else {
			s.setAccessory(newAccessory);
		}

		saftyCheckDAO.updateSaftycheck(s);

		// return Saftycheck2(s, fileName2, rootPath);
		return json;
	}

	public String addSaftycheck(Saftycheck s) {
		String json = "{\"success\":\"true\"}";
		saftyCheckDAO.insertSaftycheck(s);
		return json;
	}

	public String updateSaftycheck(Saftycheck s) {
		String json = "{\"success\":\"true\"}";
		saftyCheckDAO.updateSaftycheck(s);
		return json;
	}

	public String deleteSaftycheck(String ID) {
		Saftycheck saftycheck = new Saftycheck();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			saftycheck.setId(Integer.parseInt(temp[i]));
			saftyCheckDAO.deleteSaftycheck(saftycheck);
		}
		return json;
	}

	// *****************Taizhang表操作
	public String getTaizhangList(String findstr, int start, int limit, String projectName) {

		List<Taizhang> list = saftyCheckDAO.getTaizhangList(findstr, start, limit, projectName);
		int total = saftyCheckDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";

		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Taizhang s = list.get(i);
			jsonStr += "{\"ID\":\"" + s.getId() + "\",\"no\":\"" + s.getNo() + "\",\"checkId\":\"" + s.getCheckId()
					+ "\",\"problem\":\"" + s.getProblem() + "\",\"location\":\"" + s.getLocation()
					+ "\",\"checkperson\":\"" + s.getCheckperson() + "\",\"prolevel\":\"" + s.getProlevel()
					+ "\",\"correction\":\"" + s.getCorrection() + "\",\"solvedep\":\"" + s.getSolvedep()
					+ "\",\"solvePerson\":\"" + s.getSolvePerson() + "\",\"expTime\":\"" + s.getExpTime()
					+ "\",\"correctionfee\":\"" + s.getCorrectionfee() + "\",\"solveExp\":\"" + s.getSolveExp()
					+ "\",\"solveTime\":\"" + s.getSolveTime() + "\",\"supperson\":\"" + s.getSupperson()
					+ "\",\"prevent\":\"" + s.getPrevent() + "\",\"solveAcc\":\"" + s.getSolveAcc()
					+ "\",\"ProjectName\":\"" + s.getProjectName() + "\",\"Accessory\":\"" + s.getAccessory() + "\"}";

			System.out.println(jsonStr);
		}
		jsonStr += "]}";
		return jsonStr;
	}

	public String addTaizhang(Taizhang s, String fileName, String rootPath) {
		String json = "{\"success\":\"true\"}";
		// 有可能需要用到当前时间作为编号
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");// 设置日期格式
		String mytime = df.format(new Date());

		// 分别获取当前年月日
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + 1;
		int day = now.get(Calendar.DATE);
		String sy = String.valueOf(year);
		String sm = String.valueOf(month);
		if (month < 10) {
			sm = "0" + sm;
		}
		String sd = String.valueOf(day);
		if (day < 10) {
			sd = "0" + sd;
		}

		// 建立月份和项目名文件夹
		String ym = rootPath + sy + sm + "\\";
		// String ympname = ym + p.getName() + "\\";
		String ympname = ym + s.getId() + "\\";
		File directory1 = new File(ym); // 根目录是否存在
		if (!directory1.exists())
			directory1.mkdir();
		File directory2 = new File(ympname); // 根目录是否存在
		if (!directory2.exists())
			directory2.mkdir();

		// 拷贝temp中的文件到正确的文件夹
		String[] newFile = fileName.split("\\*");
		for (String copyFile : newFile) {
			// cutGeneralFile( rootPath+"temp\\"+copyFile,ym + p.getName());
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym + s.getId());
		}

		// 存入数据库的内容
		// String accessory = sy+sm + "*" + p.getName() + "*"+ fileName;
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}
		String accessory = sy + sm + "*" + s.getId() + "*" + fileName;

		// 打包
		// byte[] buffer = new byte[1024];
		// if (fileName.length() > 0) {
		// // String strZipName = ympname + sy + sm + sd + p.getName()+".zip";
		// String strZipName = ympname + sy + sm + sd + s.getId() + ".zip";
		// System.out.println("压缩包名称:" + strZipName);
		// try {
		// ZipOutputStream out = new ZipOutputStream(new
		// FileOutputStream(strZipName));
		// String[] allfile = fileName.split("\\*");
		// System.out.println(Arrays.toString(allfile));
		// for (int i = 0; i < allfile.length; i++) {
		// File tempfile = new File(ympname + allfile[i]);
		// if (tempfile.exists()) {
		// FileInputStream fis = new FileInputStream(tempfile);
		// out.putNextEntry(new ZipEntry(tempfile.getName()));
		// int len;
		// while ((len = fis.read(buffer)) > 0) {
		// out.write(buffer, 0, len);
		// }
		// out.closeEntry();
		// fis.close();
		// }
		// }
		// // accessory = accessory+sy+sm+sd+p.getName()+".zip";
		// accessory = accessory + sy + sm + sd + s.getId() + ".zip";
		// out.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }

		// doc转pdf
		if (fileName.length() != 0) {
			String[] allFile = fileName.split("\\*");
			for (String temp : allFile) {
				if (temp.endsWith(".doc")) {
					System.out.println(ympname + temp);
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 4) + ".pdf");
				} else if (temp.endsWith(".docx")) {
					System.out.println(ympname + temp);
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 5) + ".pdf");
				} else if (temp.endsWith(".xlsx")) {
					System.out.println("-----------xlsx");
					ExcelToPdf.excel2pdf(ympname + temp, ympname + temp.substring(0, temp.length() - 5) + ".pdf");
				} else if (temp.endsWith("xls"))

				{
					System.out.println("----------xls");
					ExcelToPdf.excel2pdf(ympname + temp, ympname + temp.substring(0, temp.length() - 4) + ".pdf");
				}
			}
		}

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			s.setAccessory(fileName);
		} else {
			s.setAccessory(accessory);
		}

		saftyCheckDAO.insertTaizhang(s);
		return json;
	}

	public String editTaizhang(Taizhang s, String fileName, String rootPath) {
		int oldAccessoryNone = 0;
		if (s.getAccessory() == null || s.getAccessory().length() <= 0) {
			oldAccessoryNone = 1;
		}
		String json = "{\"success\":\"true\"}";
		// 有可能需要用到当前时间作为编号
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");// 设置日期格式
		String mytime = df.format(new Date());

		// 分别获取当前年月日
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + 1;
		int day = now.get(Calendar.DATE);
		String sy = String.valueOf(year);
		String sm = String.valueOf(month);
		if (month < 10) {
			sm = "0" + sm;
		}
		String sd = String.valueOf(day);
		if (day < 10) {
			sd = "0" + sd;
		}

		if ((s.getAccessory() == null || s.getAccessory().length() <= 0)
				&& (fileName != null && fileName.length() > 0)) {
			// 建立月份和项目名文件夹
			String ym = rootPath + sy + sm + "\\";
			// String ympname = ym + p.getName() + "\\";
			String ympname = ym + s.getId() + "\\";
			File directory1 = new File(ym); // 根目录是否存在
			if (!directory1.exists())
				directory1.mkdir();
			File directory2 = new File(ympname); // 根目录是否存在
			if (!directory2.exists())
				directory2.mkdir();
		}

		String oldAccessory = s.getAccessory();
		String[] oldFile = oldAccessory.split("\\*");
		int oldFileLength = oldFile.length;
		String oldFoldName;
		if (oldAccessoryNone == 0) {
			oldFoldName = oldFile[0] + "\\" + oldFile[1];
		} else {
			// oldFoldName = sy + sm +"\\" +p.getName();
			oldFoldName = sy + sm + "\\" + s.getId();
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			// newAccessory = sy + sm + "*" + p.getName() +"*"+fileName;
			newAccessory = sy + sm + "*" + s.getId() + "*" + fileName;
		}

		// ------------------------删除列表中不存在的文件---------------------------//
		for (int i = 2; i < oldFileLength - 1; i++) {
			String existFileInDB = oldFile[i];
			// 列表中是否存在该文件
			boolean hasFileInList = false;
			for (String existFileInList : newFile) {
				if (existFileInDB.equals(existFileInList)) {
					hasFileInList = true;
					break;
				}
			}
			if (!hasFileInList) {
				deleteFile(rootPath + oldFoldName + "\\" + existFileInDB);
			}
		}
		// -----------------------end----------------------------------//
		// 拷贝temp文件夹的新文件到对应文件夹
		for (String oneFile : newFile) {
			boolean hasFileInList = false;
			for (int i = 2; i < oldFileLength - 1; i++) {
				if (oneFile.equals(oldFile[i])) {
					hasFileInList = true;
					break;
				}
			}
			if (!hasFileInList) {
				cutGeneralFile(rootPath + "temp\\" + oneFile, rootPath + oldFoldName);
			}

		}
		if (oldAccessoryNone == 0) {
			deleteFile(rootPath + oldFoldName + "\\" + oldFile[oldFile.length - 1]);
		}
		// ----------打包------------------//
		// byte[] buffer = new byte[1024];
		// if (fileName.length() > 0) {
		// String strZipName = rootPath + oldFoldName + "\\" + sy + sm + sd +
		// s.getId() + ".zip";
		// try {
		// ZipOutputStream out = new ZipOutputStream(new
		// FileOutputStream(strZipName));
		// String[] allfile = fileName.split("\\*");
		// for (int i = 0; i < allfile.length; i++) {
		// File tempfile = new File(rootPath + oldFoldName + "\\" + allfile[i]);
		// if (tempfile.exists()) {
		// FileInputStream fis = new FileInputStream(tempfile);
		// out.putNextEntry(new ZipEntry(tempfile.getName()));
		// int len;
		// while ((len = fis.read(buffer)) > 0) {
		// out.write(buffer, 0, len);
		// }
		// out.closeEntry();
		// fis.close();
		// }
		// }
		// newAccessory = newAccessory + sy + sm + sd + s.getId() + ".zip";
		// out.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }

		if (fileName.length() != 0) {
			String[] allFile = fileName.split("\\*");
			for (String tempFile : allFile) {
				if (tempFile.endsWith(".doc")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 4) + ".pdf");
				} else if (tempFile.endsWith(".docx")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 5) + ".pdf");
				} else if (tempFile.endsWith(".xlsx")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					ExcelToPdf.excel2pdf(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 5) + ".pdf");
				} else if (tempFile.endsWith(".xls")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					ExcelToPdf.excel2pdf(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 4) + ".pdf");
				}
			}
		}

		newAccessory = newAccessory.substring(0, newAccessory.length() - 1);
		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			s.setAccessory(fileName);
		} else {
			s.setAccessory(newAccessory);
		}

		saftyCheckDAO.updateTaizhang(s);

		// return Saftycheck2(s, fileName2, rootPath);
		return json;
	}

	public String addTaizhang(Taizhang s) {
		String json = "{\"success\":\"true\"}";
		saftyCheckDAO.insertTaizhang(s);
		return json;
	}

	public String updateTaizhang(Taizhang s) {
		String json = "{\"success\":\"true\"}";
		saftyCheckDAO.updateTaizhang(s);
		return json;
	}

	public String deleteTaizhang(String ID) {
		Taizhang taizhang = new Taizhang();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			taizhang.setId(Integer.parseInt(temp[i]));
			saftyCheckDAO.deleteTaizhang(taizhang);
		}
		return json;
	}
	
	public String deleteTaizhangByCheckID(Saftycheck s,int start,int limit) {
		Taizhang taizhang = new Taizhang();
		Taizhang deletetaizhang=new Taizhang();
		List<Taizhang> list = saftyCheckDAO.getTaizhangListCheckID("", start, limit, s.getProjectName(),s.getId());
		String json = "{\"success\":\"true\"}";
		for (int i = 0; i < list.size(); i++) {
			taizhang = list.get(i);
			deletetaizhang.setId(taizhang.getId());
			saftyCheckDAO.deleteTaizhang(deletetaizhang);
		}
		return json;
	}

	// ***********文件操作
	public String getFileInfo(String filePath) {
		String fileLength = "0";
		File file = new File(filePath);
		System.out.println(filePath);
		if (file.exists()) {
			fileLength = Long.toString(file.length());
		}
		return fileLength;
	}

	public String deleteAllFile(String ppID, String fileName, String rootPath) {
		String[] newFile = fileName.split("\\*");
		String json = "{\"success\":\"true\"}";

		if (ppID == null || ppID.length() <= 0)// 没有ID 说明是添加 直接去temp全部删除
		{
			for (String existFileInList : newFile) {
				deleteFile(rootPath + "temp\\" + existFileInList);
			}
		} else {
			String[] strKey = null;
			List<Saftycheck> list = saftyCheckDAO.checkSaftycheckID((Integer.parseInt(ppID)));
			if (list.size() > 0) {
				Saftycheck p = list.get(0);
				String a = p.getAccessory();
				if (a != null && a.length() > 0) {
					strKey = a.split("\\*");
					for (String existFileInList : newFile) {
						boolean hasFileInDB = false;
						for (int i = 2; i < strKey.length - 1; i++) {
							String existFileInDB = strKey[i];
							// 列表中是否存在该文件
							if (existFileInDB.equals(existFileInList)) {
								hasFileInDB = true;
								break;
							}
						}
						if (!hasFileInDB) {
							deleteFile(rootPath + "temp\\" + existFileInList);
						}
					}
				}
			}

		}
		return json;
	}

	public String deleteOneFile(String ppID, String fileName, String rootPath) {
		String json = "{\"success\":\"true\"}";
		String deletePath = rootPath + "temp" + "\\" + fileName;
		if (ppID == null || ppID.length() <= 0)// 如果是新增才删除
		{
			deleteFile(deletePath);
		}
		return json;
	}

	public boolean cutGeneralFile(String srcPath, String destDir) {
		if (!copyGeneralFile(srcPath, destDir)) {
			System.out.println("复制失败导致剪切失败!");
			return false;
		}
		if (!deleteFile(srcPath)) {
			System.out.println("删除源文件(文件夹)失败导致剪切失败!");
			return false;
		}

		System.out.println("剪切成功!");
		return true;
	}

	public boolean copyGeneralFile(String srcPath, String destDir) {
		boolean flag = false;
		File file = new File(srcPath);
		if (!file.exists()) {
			System.out.println("源文件或源文件夹不存在!");
			return false;
		}
		if (file.isFile()) { // 源文件
			System.out.println("下面进行文件复制!");
			flag = copyFile(srcPath, destDir);
		}

		return flag;
	}

	public boolean deleteFile(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		if (!file.exists())
			return false;
		if (sPath.endsWith(".doc")) {
			String strpdf = sPath.substring(0, sPath.length() - 4) + ".pdf";
			File filepdf = new File(strpdf);
			if (filepdf.isFile() && filepdf.exists()) {
				filepdf.delete();
			}
		}
		if (sPath.endsWith(".docx")) {
			String strpdf = sPath.substring(0, sPath.length() - 5) + ".pdf";
			File filepdf = new File(strpdf);
			if (filepdf.isFile() && filepdf.exists()) {
				filepdf.delete();
			}
		}
		// 路径为文件且不为空则删除
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}

	public boolean deleteDirectory(String sPath) {
		// 如果sPath不以文件分隔符结尾，自动添加文件分隔符
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);

		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		boolean flag = true;

		// 删除文件夹下所有的文件(包括子目录)
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 删除子文件
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag)
					break;
			} // 删除子目录
			else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
		}
		if (!flag)
			return false;

		// 删除当前目录
		if (dirFile.delete()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean DeleteFolder(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		// 判断文件或目录是否存在
		if (!file.exists()) {
			return flag;
		} else {
			// 判断是否为文件
			if (file.isFile()) {
				return deleteFile(sPath); // 为文件时调用删除文件方法
			} else {
				return deleteDirectory(sPath); // 为目录时调用删除目录方法
			}
		}
	}

	private boolean copyFile(String srcPath, String destDir) {
		boolean flag = false;

		File srcFile = new File(srcPath);
		if (!srcFile.exists()) { // 源文件不存在
			System.out.println("源文件不存在");
			return false;
		}
		// 获取待复制文件的文件名
		String fileName = srcPath.substring(srcPath.lastIndexOf(File.separator));
		String destPath = destDir + fileName;
		if (destPath.equals(srcPath)) { // 源文件路径和目标文件路径重复
			System.out.println("源文件路径和目标文件路径重复!");
			return false;
		}
		File destFile = new File(destPath);
		if (destFile.exists() && destFile.isFile()) { // 该路径下已经有一个同名文件
			System.out.println("目标目录下已有同名文件!");
			return false;
		}

		File destFileDir = new File(destDir);
		destFileDir.mkdirs();
		try {
			FileInputStream fis = new FileInputStream(srcPath);
			FileOutputStream fos = new FileOutputStream(destFile);
			byte[] buf = new byte[1024];
			int c;
			while ((c = fis.read(buf)) != -1) {
				fos.write(buf, 0, c);
			}
			fis.close();
			fos.close();

			flag = true;
		} catch (IOException e) {
			//
		}
		if (flag) {
			System.out.println("复制文件成功!");
		}

		return flag;
	}

	// -----------------LZZ---------------------//
	public String chkdispatch(String userName, String ID) {
		String json = "{\"success\":\"true\"}";
		List<Taizhang> saftchk = saftyCheckDAO.checkTaizhangCheckID(Integer.parseInt(ID));
		System.out.println("user+++" + userName);
		String[] properson = userName.split("-");
		for (Taizhang pro : saftchk) {
			pro.setSolvePerson(properson[0]);
			pro.setSolvedep(properson[1]);
			saftyCheckDAO.updateTaizhang(pro);
		}

		return json;
	}

	// *****************Saftycheckplan表操作************************
	public String getSaftycheckplanList(String findstr, int start, int limit, String projectName) {

		List<Saftycheckplan> list = saftyCheckDAO.getSaftycheckplanList(findstr, start, limit, projectName);
		int total = saftyCheckDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";

		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Saftycheckplan s = list.get(i);
			jsonStr += "{\"ID\":\"" + s.getId() + "\",\"bzren\":\"" + s.getBzren() + "\",\"bztime\":\"" + s.getBztime()
					+ "\",\"spren\":\"" + s.getSpren() + "\",\"sptime\":\"" + s.getSptime() + "\",\"ProjectName\":\""
					+ s.getProjectName() + "\",\"Accessory\":\"" + s.getAccessory() + "\"}";

			System.out.println(jsonStr);
		}
		jsonStr += "]}";
		return jsonStr;
	}

	public String addSaftycheckplan(Saftycheckplan s, String fileName, String rootPath) {
		String json = "{\"success\":\"true\"}";
		// 有可能需要用到当前时间作为编号
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");// 设置日期格式
		String mytime = df.format(new Date());

		// 分别获取当前年月日
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + 1;
		int day = now.get(Calendar.DATE);
		String sy = String.valueOf(year);
		String sm = String.valueOf(month);
		if (month < 10) {
			sm = "0" + sm;
		}
		String sd = String.valueOf(day);
		if (day < 10) {
			sd = "0" + sd;
		}

		// 建立月份和项目名文件夹
		String ym = rootPath + sy + sm + "\\";
		// String ympname = ym + p.getName() + "\\";
		String ympname = ym + s.getId() + "\\";
		File directory1 = new File(ym); // 根目录是否存在
		if (!directory1.exists())
			directory1.mkdir();
		File directory2 = new File(ympname); // 根目录是否存在
		if (!directory2.exists())
			directory2.mkdir();

		// 拷贝temp中的文件到正确的文件夹
		String[] newFile = fileName.split("\\*");
		for (String copyFile : newFile) {
			// cutGeneralFile( rootPath+"temp\\"+copyFile,ym + p.getName());
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym + s.getId());
		}

		// 存入数据库的内容
		// String accessory = sy+sm + "*" + p.getName() + "*"+ fileName;
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}
		String accessory = sy + sm + "*" + s.getId() + "*" + fileName;

		// 打包
		// byte[] buffer = new byte[1024];
		// if (fileName.length() > 0) {
		// // String strZipName = ympname + sy + sm + sd + p.getName()+".zip";
		// String strZipName = ympname + sy + sm + sd + s.getId() + ".zip";
		// System.out.println("压缩包名称:" + strZipName);
		// try {
		// ZipOutputStream out = new ZipOutputStream(new
		// FileOutputStream(strZipName));
		// String[] allfile = fileName.split("\\*");
		// System.out.println(Arrays.toString(allfile));
		// for (int i = 0; i < allfile.length; i++) {
		// File tempfile = new File(ympname + allfile[i]);
		// if (tempfile.exists()) {
		// FileInputStream fis = new FileInputStream(tempfile);
		// out.putNextEntry(new ZipEntry(tempfile.getName()));
		// int len;
		// while ((len = fis.read(buffer)) > 0) {
		// out.write(buffer, 0, len);
		// }
		// out.closeEntry();
		// fis.close();
		// }
		// }
		// // accessory = accessory+sy+sm+sd+p.getName()+".zip";
		// accessory = accessory + sy + sm + sd + s.getId() + ".zip";
		// out.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }

		// doc转pdf
		if (fileName.length() != 0) {
			String[] allFile = fileName.split("\\*");
			for (String temp : allFile) {
				if (temp.endsWith(".doc")) {
					System.out.println(ympname + temp);
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 4) + ".pdf");
				} else if (temp.endsWith(".docx")) {
					System.out.println(ympname + temp);
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 5) + ".pdf");
				} else if (temp.endsWith(".xlsx")) {
					System.out.println("-----------xlsx");
					ExcelToPdf.excel2pdf(ympname + temp, ympname + temp.substring(0, temp.length() - 5) + ".pdf");
				} else if (temp.endsWith("xls"))

				{
					System.out.println("----------xls");
					ExcelToPdf.excel2pdf(ympname + temp, ympname + temp.substring(0, temp.length() - 4) + ".pdf");
				}
			}
		}

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			s.setAccessory(fileName);
		} else {
			s.setAccessory(accessory);
		}

		saftyCheckDAO.insertSaftycheckplan(s);
		return json;
	}

	public String editSaftycheckplan(Saftycheckplan s, String fileName, String rootPath) {
		int oldAccessoryNone = 0;
		if (s.getAccessory() == null || s.getAccessory().length() <= 0) {
			oldAccessoryNone = 1;
		}
		String json = "{\"success\":\"true\"}";
		// 有可能需要用到当前时间作为编号
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");// 设置日期格式
		String mytime = df.format(new Date());

		// 分别获取当前年月日
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + 1;
		int day = now.get(Calendar.DATE);
		String sy = String.valueOf(year);
		String sm = String.valueOf(month);
		if (month < 10) {
			sm = "0" + sm;
		}
		String sd = String.valueOf(day);
		if (day < 10) {
			sd = "0" + sd;
		}

		if ((s.getAccessory() == null || s.getAccessory().length() <= 0)
				&& (fileName != null && fileName.length() > 0)) {
			// 建立月份和项目名文件夹
			String ym = rootPath + sy + sm + "\\";
			// String ympname = ym + p.getName() + "\\";
			String ympname = ym + s.getId() + "\\";
			File directory1 = new File(ym); // 根目录是否存在
			if (!directory1.exists())
				directory1.mkdir();
			File directory2 = new File(ympname); // 根目录是否存在
			if (!directory2.exists())
				directory2.mkdir();
		}

		String oldAccessory = s.getAccessory();
		String[] oldFile = oldAccessory.split("\\*");
		int oldFileLength = oldFile.length;
		String oldFoldName;
		if (oldAccessoryNone == 0) {
			oldFoldName = oldFile[0] + "\\" + oldFile[1];
		} else {
			// oldFoldName = sy + sm +"\\" +p.getName();
			oldFoldName = sy + sm + "\\" + s.getId();
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			// newAccessory = sy + sm + "*" + p.getName() +"*"+fileName;
			newAccessory = sy + sm + "*" + s.getId() + "*" + fileName;
		}

		// ------------------------删除列表中不存在的文件---------------------------//
		for (int i = 2; i < oldFileLength - 1; i++) {
			String existFileInDB = oldFile[i];
			// 列表中是否存在该文件
			boolean hasFileInList = false;
			for (String existFileInList : newFile) {
				if (existFileInDB.equals(existFileInList)) {
					hasFileInList = true;
					break;
				}
			}
			if (!hasFileInList) {
				deleteFile(rootPath + oldFoldName + "\\" + existFileInDB);
			}
		}
		// -----------------------end----------------------------------//
		// 拷贝temp文件夹的新文件到对应文件夹
		for (String oneFile : newFile) {
			boolean hasFileInList = false;
			for (int i = 2; i < oldFileLength - 1; i++) {
				if (oneFile.equals(oldFile[i])) {
					hasFileInList = true;
					break;
				}
			}
			if (!hasFileInList) {
				cutGeneralFile(rootPath + "temp\\" + oneFile, rootPath + oldFoldName);
			}

		}
		if (oldAccessoryNone == 0) {
			// deleteFile(rootPath + oldFoldName + "\\" + oldFile[oldFile.length
			// - 1]);
		}
		// ----------打包------------------//
		// byte[] buffer = new byte[1024];
		// if (fileName.length() > 0) {
		// String strZipName = rootPath + oldFoldName + "\\" + sy + sm + sd +
		// s.getId() + ".zip";
		// try {
		// ZipOutputStream out = new ZipOutputStream(new
		// FileOutputStream(strZipName));
		// String[] allfile = fileName.split("\\*");
		// for (int i = 0; i < allfile.length; i++) {
		// File tempfile = new File(rootPath + oldFoldName + "\\" + allfile[i]);
		// if (tempfile.exists()) {
		// FileInputStream fis = new FileInputStream(tempfile);
		// out.putNextEntry(new ZipEntry(tempfile.getName()));
		// int len;
		// while ((len = fis.read(buffer)) > 0) {
		// out.write(buffer, 0, len);
		// }
		// out.closeEntry();
		// fis.close();
		// }
		// }
		// newAccessory = newAccessory + sy + sm + sd + s.getId() + ".zip";
		// out.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }

		if (fileName.length() != 0) {
			String[] allFile = fileName.split("\\*");
			for (String tempFile : allFile) {
				if (tempFile.endsWith(".doc")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 4) + ".pdf");
				} else if (tempFile.endsWith(".docx")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 5) + ".pdf");
				} else if (tempFile.endsWith(".xlsx")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					ExcelToPdf.excel2pdf(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 5) + ".pdf");
				} else if (tempFile.endsWith(".xls")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					ExcelToPdf.excel2pdf(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 4) + ".pdf");
				}
			}
		}

		newAccessory = newAccessory.substring(0, newAccessory.length() - 1);
		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			s.setAccessory(fileName);
		} else {
			s.setAccessory(newAccessory);
		}

		saftyCheckDAO.updateSaftycheckplan(s);

		// return Saftycheck2(s, fileName2, rootPath);
		return json;
	}

	public String addSaftycheckplan(Saftycheckplan s) {
		String json = "{\"success\":\"true\"}";
		saftyCheckDAO.insertSaftycheckplan(s);
		return json;
	}

	public String updateSaftycheckplan(Saftycheckplan s) {
		String json = "{\"success\":\"true\"}";
		saftyCheckDAO.updateSaftycheckplan(s);
		return json;
	}

	public String deleteSaftycheckplan(String ID) {
		Saftycheckplan saftycheckplan = new Saftycheckplan();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			saftycheckplan.setId(Integer.parseInt(temp[i]));
			saftyCheckDAO.deleteSaftycheckplan(saftycheckplan);
		}
		return json;
	}

	// *****************Saftycheckyearplan表操作************************
	public String getSaftycheckyearplanList(String findstr, int start, int limit, String projectName) {

		List<Saftycheckyearplan> list = saftyCheckDAO.getSaftycheckyearplanList(findstr, start, limit, projectName);
		int total = saftyCheckDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";

		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Saftycheckyearplan s = list.get(i);
			jsonStr += "{\"ID\":\"" + s.getId() + "\",\"bzren\":\"" + s.getBzren() + "\",\"bztime\":\"" + s.getBztime()
					+ "\",\"spren\":\"" + s.getSpren() + "\",\"sptime\":\"" + s.getSptime() + "\",\"ProjectName\":\""
					+ s.getProjectName() + "\",\"Accessory\":\"" + s.getAccessory() + "\"}";

			System.out.println(jsonStr);
		}
		jsonStr += "]}";
		return jsonStr;
	}

	public String addSaftycheckyearplan(Saftycheckyearplan s, String fileName, String rootPath) {
		String json = "{\"success\":\"true\"}";
		// 有可能需要用到当前时间作为编号
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");// 设置日期格式
		String mytime = df.format(new Date());

		// 分别获取当前年月日
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + 1;
		int day = now.get(Calendar.DATE);
		String sy = String.valueOf(year);
		String sm = String.valueOf(month);
		if (month < 10) {
			sm = "0" + sm;
		}
		String sd = String.valueOf(day);
		if (day < 10) {
			sd = "0" + sd;
		}

		// 建立月份和项目名文件夹
		String ym = rootPath + sy + sm + "\\";
		// String ympname = ym + p.getName() + "\\";
		String ympname = ym + s.getId() + "\\";
		File directory1 = new File(ym); // 根目录是否存在
		if (!directory1.exists())
			directory1.mkdir();
		File directory2 = new File(ympname); // 根目录是否存在
		if (!directory2.exists())
			directory2.mkdir();

		// 拷贝temp中的文件到正确的文件夹
		String[] newFile = fileName.split("\\*");
		for (String copyFile : newFile) {
			// cutGeneralFile( rootPath+"temp\\"+copyFile,ym + p.getName());
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym + s.getId());
		}

		// 存入数据库的内容
		// String accessory = sy+sm + "*" + p.getName() + "*"+ fileName;
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}
		String accessory = sy + sm + "*" + s.getId() + "*" + fileName;

		// 打包
		// byte[] buffer = new byte[1024];
		// if (fileName.length() > 0) {
		// // String strZipName = ympname + sy + sm + sd + p.getName()+".zip";
		// String strZipName = ympname + sy + sm + sd + s.getId() + ".zip";
		// System.out.println("压缩包名称:" + strZipName);
		// try {
		// ZipOutputStream out = new ZipOutputStream(new
		// FileOutputStream(strZipName));
		// String[] allfile = fileName.split("\\*");
		// System.out.println(Arrays.toString(allfile));
		// for (int i = 0; i < allfile.length; i++) {
		// File tempfile = new File(ympname + allfile[i]);
		// if (tempfile.exists()) {
		// FileInputStream fis = new FileInputStream(tempfile);
		// out.putNextEntry(new ZipEntry(tempfile.getName()));
		// int len;
		// while ((len = fis.read(buffer)) > 0) {
		// out.write(buffer, 0, len);
		// }
		// out.closeEntry();
		// fis.close();
		// }
		// }
		// // accessory = accessory+sy+sm+sd+p.getName()+".zip";
		// accessory = accessory + sy + sm + sd + s.getId() + ".zip";
		// out.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }

		// doc转pdf
		if (fileName.length() != 0) {
			String[] allFile = fileName.split("\\*");
			for (String temp : allFile) {
				if (temp.endsWith(".doc")) {
					System.out.println(ympname + temp);
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 4) + ".pdf");
				} else if (temp.endsWith(".docx")) {
					System.out.println(ympname + temp);
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 5) + ".pdf");
				} else if (temp.endsWith(".xlsx")) {
					System.out.println("-----------xlsx");
					ExcelToPdf.excel2pdf(ympname + temp, ympname + temp.substring(0, temp.length() - 5) + ".pdf");
				} else if (temp.endsWith("xls"))

				{
					System.out.println("----------xls");
					ExcelToPdf.excel2pdf(ympname + temp, ympname + temp.substring(0, temp.length() - 4) + ".pdf");
				}
			}
		}

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			s.setAccessory(fileName);
		} else {
			s.setAccessory(accessory);
		}

		saftyCheckDAO.insertSaftycheckyearplan(s);
		return json;
	}

	public String editSaftycheckyearplan(Saftycheckyearplan s, String fileName, String rootPath) {
		int oldAccessoryNone = 0;
		if (s.getAccessory() == null || s.getAccessory().length() <= 0) {
			oldAccessoryNone = 1;
		}
		String json = "{\"success\":\"true\"}";
		// 有可能需要用到当前时间作为编号
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");// 设置日期格式
		String mytime = df.format(new Date());

		// 分别获取当前年月日
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + 1;
		int day = now.get(Calendar.DATE);
		String sy = String.valueOf(year);
		String sm = String.valueOf(month);
		if (month < 10) {
			sm = "0" + sm;
		}
		String sd = String.valueOf(day);
		if (day < 10) {
			sd = "0" + sd;
		}

		if ((s.getAccessory() == null || s.getAccessory().length() <= 0)
				&& (fileName != null && fileName.length() > 0)) {
			// 建立月份和项目名文件夹
			String ym = rootPath + sy + sm + "\\";
			// String ympname = ym + p.getName() + "\\";
			String ympname = ym + s.getId() + "\\";
			File directory1 = new File(ym); // 根目录是否存在
			if (!directory1.exists())
				directory1.mkdir();
			File directory2 = new File(ympname); // 根目录是否存在
			if (!directory2.exists())
				directory2.mkdir();
		}

		String oldAccessory = s.getAccessory();
		String[] oldFile = oldAccessory.split("\\*");
		int oldFileLength = oldFile.length;
		String oldFoldName;
		if (oldAccessoryNone == 0) {
			oldFoldName = oldFile[0] + "\\" + oldFile[1];
		} else {
			// oldFoldName = sy + sm +"\\" +p.getName();
			oldFoldName = sy + sm + "\\" + s.getId();
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			// newAccessory = sy + sm + "*" + p.getName() +"*"+fileName;
			newAccessory = sy + sm + "*" + s.getId() + "*" + fileName;
		}

		// ------------------------删除列表中不存在的文件---------------------------//
		for (int i = 2; i < oldFileLength - 1; i++) {
			String existFileInDB = oldFile[i];
			// 列表中是否存在该文件
			boolean hasFileInList = false;
			for (String existFileInList : newFile) {
				if (existFileInDB.equals(existFileInList)) {
					hasFileInList = true;
					break;
				}
			}
			if (!hasFileInList) {
				deleteFile(rootPath + oldFoldName + "\\" + existFileInDB);
			}
		}
		// -----------------------end----------------------------------//
		// 拷贝temp文件夹的新文件到对应文件夹
		for (String oneFile : newFile) {
			boolean hasFileInList = false;
			for (int i = 2; i < oldFileLength - 1; i++) {
				if (oneFile.equals(oldFile[i])) {
					hasFileInList = true;
					break;
				}
			}
			if (!hasFileInList) {
				cutGeneralFile(rootPath + "temp\\" + oneFile, rootPath + oldFoldName);
			}

		}
		if (oldAccessoryNone == 0) {
			// deleteFile(rootPath + oldFoldName + "\\" + oldFile[oldFile.length
			// - 1]);
		}
		// ----------打包------------------//
		// byte[] buffer = new byte[1024];
		// if (fileName.length() > 0) {
		// String strZipName = rootPath + oldFoldName + "\\" + sy + sm + sd +
		// s.getId() + ".zip";
		// try {
		// ZipOutputStream out = new ZipOutputStream(new
		// FileOutputStream(strZipName));
		// String[] allfile = fileName.split("\\*");
		// for (int i = 0; i < allfile.length; i++) {
		// File tempfile = new File(rootPath + oldFoldName + "\\" + allfile[i]);
		// if (tempfile.exists()) {
		// FileInputStream fis = new FileInputStream(tempfile);
		// out.putNextEntry(new ZipEntry(tempfile.getName()));
		// int len;
		// while ((len = fis.read(buffer)) > 0) {
		// out.write(buffer, 0, len);
		// }
		// out.closeEntry();
		// fis.close();
		// }
		// }
		// newAccessory = newAccessory + sy + sm + sd + s.getId() + ".zip";
		// out.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }

		if (fileName.length() != 0) {
			String[] allFile = fileName.split("\\*");
			for (String tempFile : allFile) {
				if (tempFile.endsWith(".doc")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 4) + ".pdf");
				} else if (tempFile.endsWith(".docx")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 5) + ".pdf");
				} else if (tempFile.endsWith(".xlsx")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					ExcelToPdf.excel2pdf(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 5) + ".pdf");
				} else if (tempFile.endsWith(".xls")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					ExcelToPdf.excel2pdf(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 4) + ".pdf");
				}
			}
		}

		newAccessory = newAccessory.substring(0, newAccessory.length() - 1);
		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			s.setAccessory(fileName);
		} else {
			s.setAccessory(newAccessory);
		}

		saftyCheckDAO.updateSaftycheckyearplan(s);

		// return Saftycheck2(s, fileName2, rootPath);
		return json;
	}

	public String addSaftycheckyearplan(Saftycheckyearplan s) {
		String json = "{\"success\":\"true\"}";
		saftyCheckDAO.insertSaftycheckyearplan(s);
		return json;
	}

	public String updateSaftycheckyearplan(Saftycheckyearplan s) {
		String json = "{\"success\":\"true\"}";
		saftyCheckDAO.updateSaftycheckyearplan(s);
		return json;
	}

	public String deleteSaftycheckyearplan(String ID) {
		Saftycheckyearplan saftycheckyearplan = new Saftycheckyearplan();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			saftycheckyearplan.setId(Integer.parseInt(temp[i]));
			saftyCheckDAO.deleteSaftycheckyearplan(saftycheckyearplan);
		}
		return json;
	}

	// *****************Saftycheckyearplanfb表操作************************
	public String getSaftycheckyearplanfbList(String findstr, int start, int limit, String projectName) {

		List<Saftycheckyearplanfb> list = saftyCheckDAO.getSaftycheckyearplanfbList(findstr, start, limit, projectName);
		int total = saftyCheckDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";

		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Saftycheckyearplanfb s = list.get(i);
			jsonStr += "{\"ID\":\"" + s.getId() + "\",\"bzunit\":\"" + s.getBzunit() + "\",\"bztime\":\""
					+ s.getBztime() + "\",\"bbren\":\"" + s.getBbren() + "\",\"bbtime\":\"" + s.getBbtime()
					+ "\",\"spren\":\"" + s.getSpren() + "\",\"sptime\":\"" + s.getSptime() + "\",\"ProjectName\":\""
					+ s.getProjectName() + "\",\"Accessory\":\"" + s.getAccessory() + "\"}";

			System.out.println(jsonStr);
		}
		jsonStr += "]}";
		return jsonStr;
	}

	public String addSaftycheckyearplanfb(Saftycheckyearplanfb s, String fileName, String rootPath) {
		String json = "{\"success\":\"true\"}";
		// 有可能需要用到当前时间作为编号
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");// 设置日期格式
		String mytime = df.format(new Date());

		// 分别获取当前年月日
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + 1;
		int day = now.get(Calendar.DATE);
		String sy = String.valueOf(year);
		String sm = String.valueOf(month);
		if (month < 10) {
			sm = "0" + sm;
		}
		String sd = String.valueOf(day);
		if (day < 10) {
			sd = "0" + sd;
		}

		// 建立月份和项目名文件夹
		String ym = rootPath + sy + sm + "\\";
		// String ympname = ym + p.getName() + "\\";
		String ympname = ym + s.getId() + "\\";
		File directory1 = new File(ym); // 根目录是否存在
		if (!directory1.exists())
			directory1.mkdir();
		File directory2 = new File(ympname); // 根目录是否存在
		if (!directory2.exists())
			directory2.mkdir();

		// 拷贝temp中的文件到正确的文件夹
		String[] newFile = fileName.split("\\*");
		for (String copyFile : newFile) {
			// cutGeneralFile( rootPath+"temp\\"+copyFile,ym + p.getName());
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym + s.getId());
		}

		// 存入数据库的内容
		// String accessory = sy+sm + "*" + p.getName() + "*"+ fileName;
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}
		String accessory = sy + sm + "*" + s.getId() + "*" + fileName;

		// 打包
		// byte[] buffer = new byte[1024];
		// if (fileName.length() > 0) {
		// // String strZipName = ympname + sy + sm + sd + p.getName()+".zip";
		// String strZipName = ympname + sy + sm + sd + s.getId() + ".zip";
		// System.out.println("压缩包名称:" + strZipName);
		// try {
		// ZipOutputStream out = new ZipOutputStream(new
		// FileOutputStream(strZipName));
		// String[] allfile = fileName.split("\\*");
		// System.out.println(Arrays.toString(allfile));
		// for (int i = 0; i < allfile.length; i++) {
		// File tempfile = new File(ympname + allfile[i]);
		// if (tempfile.exists()) {
		// FileInputStream fis = new FileInputStream(tempfile);
		// out.putNextEntry(new ZipEntry(tempfile.getName()));
		// int len;
		// while ((len = fis.read(buffer)) > 0) {
		// out.write(buffer, 0, len);
		// }
		// out.closeEntry();
		// fis.close();
		// }
		// }
		// // accessory = accessory+sy+sm+sd+p.getName()+".zip";
		// accessory = accessory + sy + sm + sd + s.getId() + ".zip";
		// out.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }

		// doc转pdf
		if (fileName.length() != 0) {
			String[] allFile = fileName.split("\\*");
			for (String temp : allFile) {
				if (temp.endsWith(".doc")) {
					System.out.println(ympname + temp);
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 4) + ".pdf");
				} else if (temp.endsWith(".docx")) {
					System.out.println(ympname + temp);
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 5) + ".pdf");
				} else if (temp.endsWith(".xlsx")) {
					System.out.println("-----------xlsx");
					ExcelToPdf.excel2pdf(ympname + temp, ympname + temp.substring(0, temp.length() - 5) + ".pdf");
				} else if (temp.endsWith("xls"))

				{
					System.out.println("----------xls");
					ExcelToPdf.excel2pdf(ympname + temp, ympname + temp.substring(0, temp.length() - 4) + ".pdf");
				}
			}
		}

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			s.setAccessory(fileName);
		} else {
			s.setAccessory(accessory);
		}

		saftyCheckDAO.insertSaftycheckyearplanfb(s);
		return json;
	}

	public String editSaftycheckyearplanfb(Saftycheckyearplanfb s, String fileName, String rootPath) {
		int oldAccessoryNone = 0;
		if (s.getAccessory() == null || s.getAccessory().length() <= 0) {
			oldAccessoryNone = 1;
		}
		String json = "{\"success\":\"true\"}";
		// 有可能需要用到当前时间作为编号
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");// 设置日期格式
		String mytime = df.format(new Date());

		// 分别获取当前年月日
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + 1;
		int day = now.get(Calendar.DATE);
		String sy = String.valueOf(year);
		String sm = String.valueOf(month);
		if (month < 10) {
			sm = "0" + sm;
		}
		String sd = String.valueOf(day);
		if (day < 10) {
			sd = "0" + sd;
		}

		if ((s.getAccessory() == null || s.getAccessory().length() <= 0)
				&& (fileName != null && fileName.length() > 0)) {
			// 建立月份和项目名文件夹
			String ym = rootPath + sy + sm + "\\";
			// String ympname = ym + p.getName() + "\\";
			String ympname = ym + s.getId() + "\\";
			File directory1 = new File(ym); // 根目录是否存在
			if (!directory1.exists())
				directory1.mkdir();
			File directory2 = new File(ympname); // 根目录是否存在
			if (!directory2.exists())
				directory2.mkdir();
		}

		String oldAccessory = s.getAccessory();
		String[] oldFile = oldAccessory.split("\\*");
		int oldFileLength = oldFile.length;
		String oldFoldName;
		if (oldAccessoryNone == 0) {
			oldFoldName = oldFile[0] + "\\" + oldFile[1];
		} else {
			// oldFoldName = sy + sm +"\\" +p.getName();
			oldFoldName = sy + sm + "\\" + s.getId();
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			// newAccessory = sy + sm + "*" + p.getName() +"*"+fileName;
			newAccessory = sy + sm + "*" + s.getId() + "*" + fileName;
		}

		// ------------------------删除列表中不存在的文件---------------------------//
		for (int i = 2; i < oldFileLength - 1; i++) {
			String existFileInDB = oldFile[i];
			// 列表中是否存在该文件
			boolean hasFileInList = false;
			for (String existFileInList : newFile) {
				if (existFileInDB.equals(existFileInList)) {
					hasFileInList = true;
					break;
				}
			}
			if (!hasFileInList) {
				deleteFile(rootPath + oldFoldName + "\\" + existFileInDB);
			}
		}
		// -----------------------end----------------------------------//
		// 拷贝temp文件夹的新文件到对应文件夹
		for (String oneFile : newFile) {
			boolean hasFileInList = false;
			for (int i = 2; i < oldFileLength - 1; i++) {
				if (oneFile.equals(oldFile[i])) {
					hasFileInList = true;
					break;
				}
			}
			if (!hasFileInList) {
				cutGeneralFile(rootPath + "temp\\" + oneFile, rootPath + oldFoldName);
			}

		}
		if (oldAccessoryNone == 0) {
			// deleteFile(rootPath + oldFoldName + "\\" + oldFile[oldFile.length
			// - 1]);
		}
		// ----------打包------------------//
		// byte[] buffer = new byte[1024];
		// if (fileName.length() > 0) {
		// String strZipName = rootPath + oldFoldName + "\\" + sy + sm + sd +
		// s.getId() + ".zip";
		// try {
		// ZipOutputStream out = new ZipOutputStream(new
		// FileOutputStream(strZipName));
		// String[] allfile = fileName.split("\\*");
		// for (int i = 0; i < allfile.length; i++) {
		// File tempfile = new File(rootPath + oldFoldName + "\\" + allfile[i]);
		// if (tempfile.exists()) {
		// FileInputStream fis = new FileInputStream(tempfile);
		// out.putNextEntry(new ZipEntry(tempfile.getName()));
		// int len;
		// while ((len = fis.read(buffer)) > 0) {
		// out.write(buffer, 0, len);
		// }
		// out.closeEntry();
		// fis.close();
		// }
		// }
		// newAccessory = newAccessory + sy + sm + sd + s.getId() + ".zip";
		// out.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }

		if (fileName.length() != 0) {
			String[] allFile = fileName.split("\\*");
			for (String tempFile : allFile) {
				if (tempFile.endsWith(".doc")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 4) + ".pdf");
				} else if (tempFile.endsWith(".docx")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 5) + ".pdf");
				} else if (tempFile.endsWith(".xlsx")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					ExcelToPdf.excel2pdf(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 5) + ".pdf");
				} else if (tempFile.endsWith(".xls")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					ExcelToPdf.excel2pdf(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 4) + ".pdf");
				}
			}
		}

		newAccessory = newAccessory.substring(0, newAccessory.length() - 1);
		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			s.setAccessory(fileName);
		} else {
			s.setAccessory(newAccessory);
		}

		saftyCheckDAO.updateSaftycheckyearplanfb(s);

		// return Saftycheck2(s, fileName2, rootPath);
		return json;
	}

	public String addSaftycheckyearplanfb(Saftycheckyearplanfb s) {
		String json = "{\"success\":\"true\"}";
		saftyCheckDAO.insertSaftycheckyearplanfb(s);
		return json;
	}

	public String updateSaftycheckyearplanfb(Saftycheckyearplanfb s) {
		String json = "{\"success\":\"true\"}";
		saftyCheckDAO.updateSaftycheckyearplanfb(s);
		return json;
	}

	public String deleteSaftycheckyearplanfb(String ID) {
		Saftycheckyearplanfb saftycheckyearplanfb = new Saftycheckyearplanfb();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			saftycheckyearplanfb.setId(Integer.parseInt(temp[i]));
			saftyCheckDAO.deleteSaftycheckyearplanfb(saftycheckyearplanfb);
		}
		return json;
	}

	// *****************Saftycheckyinhuanpc表操作************************
	public String getSaftycheckyinhuanpcList(String findstr, int start, int limit, String projectName) {

		List<Saftycheckyinhuanpc> list = saftyCheckDAO.getSaftycheckyinhuanpcList(findstr, start, limit, projectName);
		int total = saftyCheckDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";

		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Saftycheckyinhuanpc s = list.get(i);
			String[] accessorys = s.getAccessory().split("\\*");
			String filename = accessorys[2];
			jsonStr += "{\"ID\":\"" + s.getId() + "\",\"year\":\"" + s.getYear() + "\",\"uploaduser\":\"" + filename
					+ "\",\"uploadtime\":\"" + s.getUploadtime() + "\",\"ProjectName\":\"" + s.getProjectName()
					+ "\",\"Accessory\":\"" + s.getAccessory() + "\"}";

			System.out.println(jsonStr);
		}
		jsonStr += "]}";
		return jsonStr;
	}

	public String addSaftycheckyinhuanpc(Saftycheckyinhuanpc s, String fileName, String rootPath) {
		String json = "{\"success\":\"true\"}";
		// 有可能需要用到当前时间作为编号
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");// 设置日期格式
		String mytime = df.format(new Date());

		// 分别获取当前年月日
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + 1;
		int day = now.get(Calendar.DATE);
		String sy = String.valueOf(year);
		String sm = String.valueOf(month);
		if (month < 10) {
			sm = "0" + sm;
		}
		String sd = String.valueOf(day);
		if (day < 10) {
			sd = "0" + sd;
		}

		// 建立月份和项目名文件夹
		String ym = rootPath + sy + sm + "\\";
		// String ympname = ym + p.getName() + "\\";
		String ympname = ym + s.getId() + "\\";
		File directory1 = new File(ym); // 根目录是否存在
		if (!directory1.exists())
			directory1.mkdir();
		File directory2 = new File(ympname); // 根目录是否存在
		if (!directory2.exists())
			directory2.mkdir();

		// 拷贝temp中的文件到正确的文件夹
		String[] newFile = fileName.split("\\*");
		for (String copyFile : newFile) {
			// cutGeneralFile( rootPath+"temp\\"+copyFile,ym + p.getName());
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym + s.getId());
		}

		// 存入数据库的内容
		// String accessory = sy+sm + "*" + p.getName() + "*"+ fileName;
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}
		String accessory = sy + sm + "*" + s.getId() + "*" + fileName;

		// 打包
		// byte[] buffer = new byte[1024];
		// if (fileName.length() > 0) {
		// // String strZipName = ympname + sy + sm + sd + p.getName()+".zip";
		// String strZipName = ympname + sy + sm + sd + s.getId() + ".zip";
		// System.out.println("压缩包名称:" + strZipName);
		// try {
		// ZipOutputStream out = new ZipOutputStream(new
		// FileOutputStream(strZipName));
		// String[] allfile = fileName.split("\\*");
		// System.out.println(Arrays.toString(allfile));
		// for (int i = 0; i < allfile.length; i++) {
		// File tempfile = new File(ympname + allfile[i]);
		// if (tempfile.exists()) {
		// FileInputStream fis = new FileInputStream(tempfile);
		// out.putNextEntry(new ZipEntry(tempfile.getName()));
		// int len;
		// while ((len = fis.read(buffer)) > 0) {
		// out.write(buffer, 0, len);
		// }
		// out.closeEntry();
		// fis.close();
		// }
		// }
		// // accessory = accessory+sy+sm+sd+p.getName()+".zip";
		// accessory = accessory + sy + sm + sd + s.getId() + ".zip";
		// out.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }

		// doc转pdf
		if (fileName.length() != 0) {
			String[] allFile = fileName.split("\\*");
			for (String temp : allFile) {
				if (temp.endsWith(".doc")) {
					System.out.println(ympname + temp);
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 4) + ".pdf");
				} else if (temp.endsWith(".docx")) {
					System.out.println(ympname + temp);
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 5) + ".pdf");
				} else if (temp.endsWith(".xlsx")) {
					System.out.println("-----------xlsx");
					ExcelToPdf.excel2pdf(ympname + temp, ympname + temp.substring(0, temp.length() - 5) + ".pdf");
				} else if (temp.endsWith("xls"))

				{
					System.out.println("----------xls");
					ExcelToPdf.excel2pdf(ympname + temp, ympname + temp.substring(0, temp.length() - 4) + ".pdf");
				}
			}
		}

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			s.setAccessory(fileName);
		} else {
			s.setAccessory(accessory);
		}

		saftyCheckDAO.insertSaftycheckyinhuanpc(s);
		return json;
	}

	public String editSaftycheckyinhuanpc(Saftycheckyinhuanpc s, String fileName, String rootPath) {
		int oldAccessoryNone = 0;
		if (s.getAccessory() == null || s.getAccessory().length() <= 0) {
			oldAccessoryNone = 1;
		}
		String json = "{\"success\":\"true\"}";
		// 有可能需要用到当前时间作为编号
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");// 设置日期格式
		String mytime = df.format(new Date());

		// 分别获取当前年月日
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + 1;
		int day = now.get(Calendar.DATE);
		String sy = String.valueOf(year);
		String sm = String.valueOf(month);
		if (month < 10) {
			sm = "0" + sm;
		}
		String sd = String.valueOf(day);
		if (day < 10) {
			sd = "0" + sd;
		}

		if ((s.getAccessory() == null || s.getAccessory().length() <= 0)
				&& (fileName != null && fileName.length() > 0)) {
			// 建立月份和项目名文件夹
			String ym = rootPath + sy + sm + "\\";
			// String ympname = ym + p.getName() + "\\";
			String ympname = ym + s.getId() + "\\";
			File directory1 = new File(ym); // 根目录是否存在
			if (!directory1.exists())
				directory1.mkdir();
			File directory2 = new File(ympname); // 根目录是否存在
			if (!directory2.exists())
				directory2.mkdir();
		}

		String oldAccessory = s.getAccessory();
		String[] oldFile = oldAccessory.split("\\*");
		int oldFileLength = oldFile.length;
		String oldFoldName;
		if (oldAccessoryNone == 0) {
			oldFoldName = oldFile[0] + "\\" + oldFile[1];
		} else {
			// oldFoldName = sy + sm +"\\" +p.getName();
			oldFoldName = sy + sm + "\\" + s.getId();
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			// newAccessory = sy + sm + "*" + p.getName() +"*"+fileName;
			newAccessory = sy + sm + "*" + s.getId() + "*" + fileName;
		}

		// ------------------------删除列表中不存在的文件---------------------------//
		for (int i = 2; i < oldFileLength - 1; i++) {
			String existFileInDB = oldFile[i];
			// 列表中是否存在该文件
			boolean hasFileInList = false;
			for (String existFileInList : newFile) {
				if (existFileInDB.equals(existFileInList)) {
					hasFileInList = true;
					break;
				}
			}
			if (!hasFileInList) {
				deleteFile(rootPath + oldFoldName + "\\" + existFileInDB);
			}
		}
		// -----------------------end----------------------------------//
		// 拷贝temp文件夹的新文件到对应文件夹
		for (String oneFile : newFile) {
			boolean hasFileInList = false;
			for (int i = 2; i < oldFileLength - 1; i++) {
				if (oneFile.equals(oldFile[i])) {
					hasFileInList = true;
					break;
				}
			}
			if (!hasFileInList) {
				cutGeneralFile(rootPath + "temp\\" + oneFile, rootPath + oldFoldName);
			}

		}
		if (oldAccessoryNone == 0) {
			// deleteFile(rootPath + oldFoldName + "\\" + oldFile[oldFile.length
			// - 1]);
		}
		// ----------打包------------------//
		// byte[] buffer = new byte[1024];
		// if (fileName.length() > 0) {
		// String strZipName = rootPath + oldFoldName + "\\" + sy + sm + sd +
		// s.getId() + ".zip";
		// try {
		// ZipOutputStream out = new ZipOutputStream(new
		// FileOutputStream(strZipName));
		// String[] allfile = fileName.split("\\*");
		// for (int i = 0; i < allfile.length; i++) {
		// File tempfile = new File(rootPath + oldFoldName + "\\" + allfile[i]);
		// if (tempfile.exists()) {
		// FileInputStream fis = new FileInputStream(tempfile);
		// out.putNextEntry(new ZipEntry(tempfile.getName()));
		// int len;
		// while ((len = fis.read(buffer)) > 0) {
		// out.write(buffer, 0, len);
		// }
		// out.closeEntry();
		// fis.close();
		// }
		// }
		// newAccessory = newAccessory + sy + sm + sd + s.getId() + ".zip";
		// out.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }

		if (fileName.length() != 0) {
			String[] allFile = fileName.split("\\*");
			for (String tempFile : allFile) {
				if (tempFile.endsWith(".doc")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 4) + ".pdf");
				} else if (tempFile.endsWith(".docx")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 5) + ".pdf");
				} else if (tempFile.endsWith(".xlsx")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					ExcelToPdf.excel2pdf(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 5) + ".pdf");
				} else if (tempFile.endsWith(".xls")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					ExcelToPdf.excel2pdf(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 4) + ".pdf");
				}
			}
		}

		newAccessory = newAccessory.substring(0, newAccessory.length() - 1);
		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			s.setAccessory(fileName);
		} else {
			s.setAccessory(newAccessory);
		}

		saftyCheckDAO.updateSaftycheckyinhuanpc(s);

		// return Saftycheck2(s, fileName2, rootPath);
		return json;
	}

	public String addSaftycheckyinhuanpc(Saftycheckyinhuanpc s) {
		String json = "{\"success\":\"true\"}";
		saftyCheckDAO.insertSaftycheckyinhuanpc(s);
		return json;
	}

	public String updateSaftycheckyinhuanpc(Saftycheckyinhuanpc s) {
		String json = "{\"success\":\"true\"}";
		saftyCheckDAO.updateSaftycheckyinhuanpc(s);
		return json;
	}

	public String deleteSaftycheckyinhuanpc(String ID) {
		Saftycheckyinhuanpc saftycheckyinhuanpc = new Saftycheckyinhuanpc();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			saftycheckyinhuanpc.setId(Integer.parseInt(temp[i]));
			saftyCheckDAO.deleteSaftycheckyinhuanpc(saftycheckyinhuanpc);
		}
		return json;
	}

	// *****************Taizhangfb表操作************************
	public String getTaizhangfbList(String findstr, int start, int limit, String projectName) {
		List<Taizhangfb> list = saftyCheckDAO.getTaizhangfbList(findstr, start, limit, projectName);
		int total = saftyCheckDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";

		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Taizhangfb s = list.get(i);
			jsonStr += "{\"ID\":\"" + s.getId() + "\",\"fbname\":\"" + s.getFbname() + "\",\"year\":\"" + s.getYear()
					+ "\",\"month\":\"" + s.getMonth() + "\",\"ProjectName\":\"" + s.getProjectName()
					+ "\",\"Accessory\":\"" + s.getAccessory() + "\"}";

			System.out.println(jsonStr);
		}
		jsonStr += "]}";
		return jsonStr;
	}

	public String addTaizhangfb(Taizhangfb s, String fileName, String rootPath) {
		String json = "{\"success\":\"true\"}";
		// 有可能需要用到当前时间作为编号
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");// 设置日期格式
		String mytime = df.format(new Date());

		// 分别获取当前年月日
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + 1;
		int day = now.get(Calendar.DATE);
		String sy = String.valueOf(year);
		String sm = String.valueOf(month);
		if (month < 10) {
			sm = "0" + sm;
		}
		String sd = String.valueOf(day);
		if (day < 10) {
			sd = "0" + sd;
		}

		// 建立月份和项目名文件夹
		String ym = rootPath + sy + sm + "\\";
		// String ympname = ym + p.getName() + "\\";
		String ympname = ym + s.getId() + "\\";
		File directory1 = new File(ym); // 根目录是否存在
		if (!directory1.exists())
			directory1.mkdir();
		File directory2 = new File(ympname); // 根目录是否存在
		if (!directory2.exists())
			directory2.mkdir();

		// 拷贝temp中的文件到正确的文件夹
		String[] newFile = fileName.split("\\*");
		for (String copyFile : newFile) {
			// cutGeneralFile( rootPath+"temp\\"+copyFile,ym + p.getName());
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym + s.getId());
		}

		// 存入数据库的内容
		// String accessory = sy+sm + "*" + p.getName() + "*"+ fileName;
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}
		String accessory = sy + sm + "*" + s.getId() + "*" + fileName;

		// 打包
		// byte[] buffer = new byte[1024];
		// if (fileName.length() > 0) {
		// // String strZipName = ympname + sy + sm + sd + p.getName()+".zip";
		// String strZipName = ympname + sy + sm + sd + s.getId() + ".zip";
		// System.out.println("压缩包名称:" + strZipName);
		// try {
		// ZipOutputStream out = new ZipOutputStream(new
		// FileOutputStream(strZipName));
		// String[] allfile = fileName.split("\\*");
		// System.out.println(Arrays.toString(allfile));
		// for (int i = 0; i < allfile.length; i++) {
		// File tempfile = new File(ympname + allfile[i]);
		// if (tempfile.exists()) {
		// FileInputStream fis = new FileInputStream(tempfile);
		// out.putNextEntry(new ZipEntry(tempfile.getName()));
		// int len;
		// while ((len = fis.read(buffer)) > 0) {
		// out.write(buffer, 0, len);
		// }
		// out.closeEntry();
		// fis.close();
		// }
		// }
		// // accessory = accessory+sy+sm+sd+p.getName()+".zip";
		// accessory = accessory + sy + sm + sd + s.getId() + ".zip";
		// out.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }

		// doc转pdf
		if (fileName.length() != 0) {
			String[] allFile = fileName.split("\\*");
			for (String temp : allFile) {
				if (temp.endsWith(".doc")) {
					System.out.println(ympname + temp);
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 4) + ".pdf");
				} else if (temp.endsWith(".docx")) {
					System.out.println(ympname + temp);
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 5) + ".pdf");
				} else if (temp.endsWith(".xlsx")) {
					System.out.println("-----------xlsx");
					ExcelToPdf.excel2pdf(ympname + temp, ympname + temp.substring(0, temp.length() - 5) + ".pdf");
				} else if (temp.endsWith("xls"))

				{
					System.out.println("----------xls");
					ExcelToPdf.excel2pdf(ympname + temp, ympname + temp.substring(0, temp.length() - 4) + ".pdf");
				}
			}
		}

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			s.setAccessory(fileName);
		} else {
			s.setAccessory(accessory);
		}

		saftyCheckDAO.insertTaizhangfb(s);
		return json;
	}

	public String editTaizhangfb(Taizhangfb s, String fileName, String rootPath) {
		int oldAccessoryNone = 0;
		if (s.getAccessory() == null || s.getAccessory().length() <= 0) {
			oldAccessoryNone = 1;
		}
		String json = "{\"success\":\"true\"}";
		// 有可能需要用到当前时间作为编号
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");// 设置日期格式
		String mytime = df.format(new Date());

		// 分别获取当前年月日
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + 1;
		int day = now.get(Calendar.DATE);
		String sy = String.valueOf(year);
		String sm = String.valueOf(month);
		if (month < 10) {
			sm = "0" + sm;
		}
		String sd = String.valueOf(day);
		if (day < 10) {
			sd = "0" + sd;
		}

		if ((s.getAccessory() == null || s.getAccessory().length() <= 0)
				&& (fileName != null && fileName.length() > 0)) {
			// 建立月份和项目名文件夹
			String ym = rootPath + sy + sm + "\\";
			// String ympname = ym + p.getName() + "\\";
			String ympname = ym + s.getId() + "\\";
			File directory1 = new File(ym); // 根目录是否存在
			if (!directory1.exists())
				directory1.mkdir();
			File directory2 = new File(ympname); // 根目录是否存在
			if (!directory2.exists())
				directory2.mkdir();
		}

		String oldAccessory = s.getAccessory();
		String[] oldFile = oldAccessory.split("\\*");
		int oldFileLength = oldFile.length;
		String oldFoldName;
		if (oldAccessoryNone == 0) {
			oldFoldName = oldFile[0] + "\\" + oldFile[1];
		} else {
			// oldFoldName = sy + sm +"\\" +p.getName();
			oldFoldName = sy + sm + "\\" + s.getId();
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			// newAccessory = sy + sm + "*" + p.getName() +"*"+fileName;
			newAccessory = sy + sm + "*" + s.getId() + "*" + fileName;
		}

		// ------------------------删除列表中不存在的文件---------------------------//
		for (int i = 2; i < oldFileLength - 1; i++) {
			String existFileInDB = oldFile[i];
			// 列表中是否存在该文件
			boolean hasFileInList = false;
			for (String existFileInList : newFile) {
				if (existFileInDB.equals(existFileInList)) {
					hasFileInList = true;
					break;
				}
			}
			if (!hasFileInList) {
				deleteFile(rootPath + oldFoldName + "\\" + existFileInDB);
			}
		}
		// -----------------------end----------------------------------//
		// 拷贝temp文件夹的新文件到对应文件夹
		for (String oneFile : newFile) {
			boolean hasFileInList = false;
			for (int i = 2; i < oldFileLength - 1; i++) {
				if (oneFile.equals(oldFile[i])) {
					hasFileInList = true;
					break;
				}
			}
			if (!hasFileInList) {
				cutGeneralFile(rootPath + "temp\\" + oneFile, rootPath + oldFoldName);
			}

		}
		if (oldAccessoryNone == 0) {
			// deleteFile(rootPath + oldFoldName + "\\" + oldFile[oldFile.length
			// - 1]);
		}
		// ----------打包------------------//
		// byte[] buffer = new byte[1024];
		// if (fileName.length() > 0) {
		// String strZipName = rootPath + oldFoldName + "\\" + sy + sm + sd +
		// s.getId() + ".zip";
		// try {
		// ZipOutputStream out = new ZipOutputStream(new
		// FileOutputStream(strZipName));
		// String[] allfile = fileName.split("\\*");
		// for (int i = 0; i < allfile.length; i++) {
		// File tempfile = new File(rootPath + oldFoldName + "\\" + allfile[i]);
		// if (tempfile.exists()) {
		// FileInputStream fis = new FileInputStream(tempfile);
		// out.putNextEntry(new ZipEntry(tempfile.getName()));
		// int len;
		// while ((len = fis.read(buffer)) > 0) {
		// out.write(buffer, 0, len);
		// }
		// out.closeEntry();
		// fis.close();
		// }
		// }
		// newAccessory = newAccessory + sy + sm + sd + s.getId() + ".zip";
		// out.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }

		if (fileName.length() != 0) {
			String[] allFile = fileName.split("\\*");
			for (String tempFile : allFile) {
				if (tempFile.endsWith(".doc")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 4) + ".pdf");
				} else if (tempFile.endsWith(".docx")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 5) + ".pdf");
				} else if (tempFile.endsWith(".xlsx")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					ExcelToPdf.excel2pdf(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 5) + ".pdf");
				} else if (tempFile.endsWith(".xls")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					ExcelToPdf.excel2pdf(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 4) + ".pdf");
				}
			}
		}

		newAccessory = newAccessory.substring(0, newAccessory.length() - 1);
		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			s.setAccessory(fileName);
		} else {
			s.setAccessory(newAccessory);
		}

		saftyCheckDAO.updateTaizhangfb(s);

		// return Saftycheck2(s, fileName2, rootPath);
		return json;
	}

	public String addTaizhangfb(Taizhangfb s) {
		String json = "{\"success\":\"true\"}";
		saftyCheckDAO.insertTaizhangfb(s);
		return json;
	}

	public String updateTaizhangfb(Taizhangfb s) {
		String json = "{\"success\":\"true\"}";
		saftyCheckDAO.updateTaizhangfb(s);
		return json;
	}

	public String deleteTaizhangfb(String ID) {
		Taizhangfb taizhangfb = new Taizhangfb();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			taizhangfb.setId(Integer.parseInt(temp[i]));
			saftyCheckDAO.deleteTaizhangfb(taizhangfb);
		}
		return json;
	}

	// *****************Riskfenbao表操作************************
	public String getRiskfenbaoList(String findstr, int start, int limit, String projectName) {
		List<Riskfenbao> list = saftyCheckDAO.getRiskfenbaoList(findstr, start, limit, projectName);
		int total = saftyCheckDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";

		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Riskfenbao s = list.get(i);
			jsonStr += "{\"ID\":\"" + s.getId() + "\",\"fenbaoname\":\"" + s.getFenbaoname() + "\",\"bbtime\":\""
					+ s.getBbtime() + "\",\"ProjectName\":\"" + s.getProjectName() + "\",\"Accessory\":\""
					+ s.getAccessory() + "\"}";

			System.out.println(jsonStr);
		}
		jsonStr += "]}";
		return jsonStr;
	}

	public String addRiskfenbao(Riskfenbao s, String fileName, String rootPath) {
		String json = "{\"success\":\"true\"}";
		// 有可能需要用到当前时间作为编号
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");// 设置日期格式
		String mytime = df.format(new Date());

		// 分别获取当前年月日
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + 1;
		int day = now.get(Calendar.DATE);
		String sy = String.valueOf(year);
		String sm = String.valueOf(month);
		if (month < 10) {
			sm = "0" + sm;
		}
		String sd = String.valueOf(day);
		if (day < 10) {
			sd = "0" + sd;
		}

		// 建立月份和项目名文件夹
		String ym = rootPath + sy + sm + "\\";
		// String ympname = ym + p.getName() + "\\";
		String ympname = ym + s.getId() + "\\";
		File directory1 = new File(ym); // 根目录是否存在
		if (!directory1.exists())
			directory1.mkdir();
		File directory2 = new File(ympname); // 根目录是否存在
		if (!directory2.exists())
			directory2.mkdir();

		// 拷贝temp中的文件到正确的文件夹
		String[] newFile = fileName.split("\\*");
		for (String copyFile : newFile) {
			// cutGeneralFile( rootPath+"temp\\"+copyFile,ym + p.getName());
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym + s.getId());
		}

		// 存入数据库的内容
		// String accessory = sy+sm + "*" + p.getName() + "*"+ fileName;
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}
		String accessory = sy + sm + "*" + s.getId() + "*" + fileName;

		// 打包
		// byte[] buffer = new byte[1024];
		// if (fileName.length() > 0) {
		// // String strZipName = ympname + sy + sm + sd + p.getName()+".zip";
		// String strZipName = ympname + sy + sm + sd + s.getId() + ".zip";
		// System.out.println("压缩包名称:" + strZipName);
		// try {
		// ZipOutputStream out = new ZipOutputStream(new
		// FileOutputStream(strZipName));
		// String[] allfile = fileName.split("\\*");
		// System.out.println(Arrays.toString(allfile));
		// for (int i = 0; i < allfile.length; i++) {
		// File tempfile = new File(ympname + allfile[i]);
		// if (tempfile.exists()) {
		// FileInputStream fis = new FileInputStream(tempfile);
		// out.putNextEntry(new ZipEntry(tempfile.getName()));
		// int len;
		// while ((len = fis.read(buffer)) > 0) {
		// out.write(buffer, 0, len);
		// }
		// out.closeEntry();
		// fis.close();
		// }
		// }
		// // accessory = accessory+sy+sm+sd+p.getName()+".zip";
		// accessory = accessory + sy + sm + sd + s.getId() + ".zip";
		// out.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }

		// doc转pdf
		if (fileName.length() != 0) {
			String[] allFile = fileName.split("\\*");
			for (String temp : allFile) {
				if (temp.endsWith(".doc")) {
					System.out.println(ympname + temp);
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 4) + ".pdf");
				} else if (temp.endsWith(".docx")) {
					System.out.println(ympname + temp);
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 5) + ".pdf");
				} else if (temp.endsWith(".xlsx")) {
					System.out.println("-----------xlsx");
					ExcelToPdf.excel2pdf(ympname + temp, ympname + temp.substring(0, temp.length() - 5) + ".pdf");
				} else if (temp.endsWith("xls"))

				{
					System.out.println("----------xls");
					ExcelToPdf.excel2pdf(ympname + temp, ympname + temp.substring(0, temp.length() - 4) + ".pdf");
				}
			}
		}

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			s.setAccessory(fileName);
		} else {
			s.setAccessory(accessory);
		}

		saftyCheckDAO.insertRiskfenbao(s);
		return json;
	}

	public String editRiskfenbao(Riskfenbao s, String fileName, String rootPath) {
		int oldAccessoryNone = 0;
		if (s.getAccessory() == null || s.getAccessory().length() <= 0) {
			oldAccessoryNone = 1;
		}
		String json = "{\"success\":\"true\"}";
		// 有可能需要用到当前时间作为编号
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");// 设置日期格式
		String mytime = df.format(new Date());

		// 分别获取当前年月日
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + 1;
		int day = now.get(Calendar.DATE);
		String sy = String.valueOf(year);
		String sm = String.valueOf(month);
		if (month < 10) {
			sm = "0" + sm;
		}
		String sd = String.valueOf(day);
		if (day < 10) {
			sd = "0" + sd;
		}

		if ((s.getAccessory() == null || s.getAccessory().length() <= 0)
				&& (fileName != null && fileName.length() > 0)) {
			// 建立月份和项目名文件夹
			String ym = rootPath + sy + sm + "\\";
			// String ympname = ym + p.getName() + "\\";
			String ympname = ym + s.getId() + "\\";
			File directory1 = new File(ym); // 根目录是否存在
			if (!directory1.exists())
				directory1.mkdir();
			File directory2 = new File(ympname); // 根目录是否存在
			if (!directory2.exists())
				directory2.mkdir();
		}

		String oldAccessory = s.getAccessory();
		String[] oldFile = oldAccessory.split("\\*");
		int oldFileLength = oldFile.length;
		String oldFoldName;
		if (oldAccessoryNone == 0) {
			oldFoldName = oldFile[0] + "\\" + oldFile[1];
		} else {
			// oldFoldName = sy + sm +"\\" +p.getName();
			oldFoldName = sy + sm + "\\" + s.getId();
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			// newAccessory = sy + sm + "*" + p.getName() +"*"+fileName;
			newAccessory = sy + sm + "*" + s.getId() + "*" + fileName;
		}

		// ------------------------删除列表中不存在的文件---------------------------//
		for (int i = 2; i < oldFileLength - 1; i++) {
			String existFileInDB = oldFile[i];
			// 列表中是否存在该文件
			boolean hasFileInList = false;
			for (String existFileInList : newFile) {
				if (existFileInDB.equals(existFileInList)) {
					hasFileInList = true;
					break;
				}
			}
			if (!hasFileInList) {
				deleteFile(rootPath + oldFoldName + "\\" + existFileInDB);
			}
		}
		// -----------------------end----------------------------------//
		// 拷贝temp文件夹的新文件到对应文件夹
		for (String oneFile : newFile) {
			boolean hasFileInList = false;
			for (int i = 2; i < oldFileLength - 1; i++) {
				if (oneFile.equals(oldFile[i])) {
					hasFileInList = true;
					break;
				}
			}
			if (!hasFileInList) {
				cutGeneralFile(rootPath + "temp\\" + oneFile, rootPath + oldFoldName);
			}

		}
		if (oldAccessoryNone == 0) {
			// deleteFile(rootPath + oldFoldName + "\\" + oldFile[oldFile.length
			// - 1]);
		}
		// ----------打包------------------//
		// byte[] buffer = new byte[1024];
		// if (fileName.length() > 0) {
		// String strZipName = rootPath + oldFoldName + "\\" + sy + sm + sd +
		// s.getId() + ".zip";
		// try {
		// ZipOutputStream out = new ZipOutputStream(new
		// FileOutputStream(strZipName));
		// String[] allfile = fileName.split("\\*");
		// for (int i = 0; i < allfile.length; i++) {
		// File tempfile = new File(rootPath + oldFoldName + "\\" + allfile[i]);
		// if (tempfile.exists()) {
		// FileInputStream fis = new FileInputStream(tempfile);
		// out.putNextEntry(new ZipEntry(tempfile.getName()));
		// int len;
		// while ((len = fis.read(buffer)) > 0) {
		// out.write(buffer, 0, len);
		// }
		// out.closeEntry();
		// fis.close();
		// }
		// }
		// newAccessory = newAccessory + sy + sm + sd + s.getId() + ".zip";
		// out.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }

		if (fileName.length() != 0) {
			String[] allFile = fileName.split("\\*");
			for (String tempFile : allFile) {
				if (tempFile.endsWith(".doc")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 4) + ".pdf");
				} else if (tempFile.endsWith(".docx")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 5) + ".pdf");
				} else if (tempFile.endsWith(".xlsx")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					ExcelToPdf.excel2pdf(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 5) + ".pdf");
				} else if (tempFile.endsWith(".xls")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					ExcelToPdf.excel2pdf(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 4) + ".pdf");
				}
			}
		}

		newAccessory = newAccessory.substring(0, newAccessory.length() - 1);
		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			s.setAccessory(fileName);
		} else {
			s.setAccessory(newAccessory);
		}

		saftyCheckDAO.updateRiskfenbao(s);

		// return Saftycheck2(s, fileName2, rootPath);
		return json;
	}

	public String addRiskfenbao(Riskfenbao s) {
		String json = "{\"success\":\"true\"}";
		saftyCheckDAO.insertRiskfenbao(s);
		return json;
	}

	public String updateRiskfenbao(Riskfenbao s) {
		String json = "{\"success\":\"true\"}";
		saftyCheckDAO.updateRiskfenbao(s);
		return json;
	}

	public String deleteRiskfenbao(String ID) {
		Riskfenbao riskfenbao = new Riskfenbao();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			riskfenbao.setId(Integer.parseInt(temp[i]));
			saftyCheckDAO.deleteRiskfenbao(riskfenbao);
		}
		return json;
	}

	// *****************Riskprodanger表操作************************
	public String getRiskprodangerList(String findstr, int start, int limit, String projectName) {
		List<Riskprodanger> list = saftyCheckDAO.getRiskprodangerList(findstr, start, limit, projectName);
		int total = saftyCheckDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";

		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Riskprodanger s = list.get(i);
			jsonStr += "{\"ID\":\"" + s.getId() + "\",\"bstype\":\"" + s.getBstype() + "\",\"bstime\":\""
					+ s.getBstime() + "\",\"ProjectName\":\"" + s.getProjectName() + "\",\"Accessory\":\""
					+ s.getAccessory() + "\"}";

			System.out.println(jsonStr);
		}
		jsonStr += "]}";
		return jsonStr;
	}

	public String addRiskprodanger(Riskprodanger s, String fileName, String rootPath) {
		String json = "{\"success\":\"true\"}";
		// 有可能需要用到当前时间作为编号
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");// 设置日期格式
		String mytime = df.format(new Date());

		// 分别获取当前年月日
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + 1;
		int day = now.get(Calendar.DATE);
		String sy = String.valueOf(year);
		String sm = String.valueOf(month);
		if (month < 10) {
			sm = "0" + sm;
		}
		String sd = String.valueOf(day);
		if (day < 10) {
			sd = "0" + sd;
		}

		// 建立月份和项目名文件夹
		String ym = rootPath + sy + sm + "\\";
		// String ympname = ym + p.getName() + "\\";
		String ympname = ym + s.getId() + "\\";
		File directory1 = new File(ym); // 根目录是否存在
		if (!directory1.exists())
			directory1.mkdir();
		File directory2 = new File(ympname); // 根目录是否存在
		if (!directory2.exists())
			directory2.mkdir();

		// 拷贝temp中的文件到正确的文件夹
		String[] newFile = fileName.split("\\*");
		for (String copyFile : newFile) {
			// cutGeneralFile( rootPath+"temp\\"+copyFile,ym + p.getName());
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym + s.getId());
		}

		// 存入数据库的内容
		// String accessory = sy+sm + "*" + p.getName() + "*"+ fileName;
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}
		String accessory = sy + sm + "*" + s.getId() + "*" + fileName;

		// 打包
		// byte[] buffer = new byte[1024];
		// if (fileName.length() > 0) {
		// // String strZipName = ympname + sy + sm + sd + p.getName()+".zip";
		// String strZipName = ympname + sy + sm + sd + s.getId() + ".zip";
		// System.out.println("压缩包名称:" + strZipName);
		// try {
		// ZipOutputStream out = new ZipOutputStream(new
		// FileOutputStream(strZipName));
		// String[] allfile = fileName.split("\\*");
		// System.out.println(Arrays.toString(allfile));
		// for (int i = 0; i < allfile.length; i++) {
		// File tempfile = new File(ympname + allfile[i]);
		// if (tempfile.exists()) {
		// FileInputStream fis = new FileInputStream(tempfile);
		// out.putNextEntry(new ZipEntry(tempfile.getName()));
		// int len;
		// while ((len = fis.read(buffer)) > 0) {
		// out.write(buffer, 0, len);
		// }
		// out.closeEntry();
		// fis.close();
		// }
		// }
		// // accessory = accessory+sy+sm+sd+p.getName()+".zip";
		// accessory = accessory + sy + sm + sd + s.getId() + ".zip";
		// out.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }

		// doc转pdf
		if (fileName.length() != 0) {
			String[] allFile = fileName.split("\\*");
			for (String temp : allFile) {
				if (temp.endsWith(".doc")) {
					System.out.println(ympname + temp);
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 4) + ".pdf");
				} else if (temp.endsWith(".docx")) {
					System.out.println(ympname + temp);
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 5) + ".pdf");
				} else if (temp.endsWith(".xlsx")) {
					System.out.println("-----------xlsx");
					ExcelToPdf.excel2pdf(ympname + temp, ympname + temp.substring(0, temp.length() - 5) + ".pdf");
				} else if (temp.endsWith("xls"))

				{
					System.out.println("----------xls");
					ExcelToPdf.excel2pdf(ympname + temp, ympname + temp.substring(0, temp.length() - 4) + ".pdf");
				}
			}
		}

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			s.setAccessory(fileName);
		} else {
			s.setAccessory(accessory);
		}

		saftyCheckDAO.insertRiskprodanger(s);
		return json;
	}

	public String editRiskprodanger(Riskprodanger s, String fileName, String rootPath) {
		int oldAccessoryNone = 0;
		if (s.getAccessory() == null || s.getAccessory().length() <= 0) {
			oldAccessoryNone = 1;
		}
		String json = "{\"success\":\"true\"}";
		// 有可能需要用到当前时间作为编号
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");// 设置日期格式
		String mytime = df.format(new Date());

		// 分别获取当前年月日
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + 1;
		int day = now.get(Calendar.DATE);
		String sy = String.valueOf(year);
		String sm = String.valueOf(month);
		if (month < 10) {
			sm = "0" + sm;
		}
		String sd = String.valueOf(day);
		if (day < 10) {
			sd = "0" + sd;
		}

		if ((s.getAccessory() == null || s.getAccessory().length() <= 0)
				&& (fileName != null && fileName.length() > 0)) {
			// 建立月份和项目名文件夹
			String ym = rootPath + sy + sm + "\\";
			// String ympname = ym + p.getName() + "\\";
			String ympname = ym + s.getId() + "\\";
			File directory1 = new File(ym); // 根目录是否存在
			if (!directory1.exists())
				directory1.mkdir();
			File directory2 = new File(ympname); // 根目录是否存在
			if (!directory2.exists())
				directory2.mkdir();
		}

		String oldAccessory = s.getAccessory();
		String[] oldFile = oldAccessory.split("\\*");
		int oldFileLength = oldFile.length;
		String oldFoldName;
		if (oldAccessoryNone == 0) {
			oldFoldName = oldFile[0] + "\\" + oldFile[1];
		} else {
			// oldFoldName = sy + sm +"\\" +p.getName();
			oldFoldName = sy + sm + "\\" + s.getId();
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			// newAccessory = sy + sm + "*" + p.getName() +"*"+fileName;
			newAccessory = sy + sm + "*" + s.getId() + "*" + fileName;
		}

		// ------------------------删除列表中不存在的文件---------------------------//
		for (int i = 2; i < oldFileLength - 1; i++) {
			String existFileInDB = oldFile[i];
			// 列表中是否存在该文件
			boolean hasFileInList = false;
			for (String existFileInList : newFile) {
				if (existFileInDB.equals(existFileInList)) {
					hasFileInList = true;
					break;
				}
			}
			if (!hasFileInList) {
				deleteFile(rootPath + oldFoldName + "\\" + existFileInDB);
			}
		}
		// -----------------------end----------------------------------//
		// 拷贝temp文件夹的新文件到对应文件夹
		for (String oneFile : newFile) {
			boolean hasFileInList = false;
			for (int i = 2; i < oldFileLength - 1; i++) {
				if (oneFile.equals(oldFile[i])) {
					hasFileInList = true;
					break;
				}
			}
			if (!hasFileInList) {
				cutGeneralFile(rootPath + "temp\\" + oneFile, rootPath + oldFoldName);
			}

		}
		if (oldAccessoryNone == 0) {
			// deleteFile(rootPath + oldFoldName + "\\" + oldFile[oldFile.length
			// - 1]);
		}
		// ----------打包------------------//
		// byte[] buffer = new byte[1024];
		// if (fileName.length() > 0) {
		// String strZipName = rootPath + oldFoldName + "\\" + sy + sm + sd +
		// s.getId() + ".zip";
		// try {
		// ZipOutputStream out = new ZipOutputStream(new
		// FileOutputStream(strZipName));
		// String[] allfile = fileName.split("\\*");
		// for (int i = 0; i < allfile.length; i++) {
		// File tempfile = new File(rootPath + oldFoldName + "\\" + allfile[i]);
		// if (tempfile.exists()) {
		// FileInputStream fis = new FileInputStream(tempfile);
		// out.putNextEntry(new ZipEntry(tempfile.getName()));
		// int len;
		// while ((len = fis.read(buffer)) > 0) {
		// out.write(buffer, 0, len);
		// }
		// out.closeEntry();
		// fis.close();
		// }
		// }
		// newAccessory = newAccessory + sy + sm + sd + s.getId() + ".zip";
		// out.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }

		if (fileName.length() != 0) {
			String[] allFile = fileName.split("\\*");
			for (String tempFile : allFile) {
				if (tempFile.endsWith(".doc")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 4) + ".pdf");
				} else if (tempFile.endsWith(".docx")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 5) + ".pdf");
				} else if (tempFile.endsWith(".xlsx")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					ExcelToPdf.excel2pdf(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 5) + ".pdf");
				} else if (tempFile.endsWith(".xls")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					ExcelToPdf.excel2pdf(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 4) + ".pdf");
				}
			}
		}

		newAccessory = newAccessory.substring(0, newAccessory.length() - 1);
		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			s.setAccessory(fileName);
		} else {
			s.setAccessory(newAccessory);
		}

		saftyCheckDAO.updateRiskprodanger(s);

		// return Saftycheck2(s, fileName2, rootPath);
		return json;
	}

	public String addRiskprodanger(Riskprodanger s) {
		String json = "{\"success\":\"true\"}";
		saftyCheckDAO.insertRiskprodanger(s);
		return json;
	}

	public String updateRiskprodanger(Riskprodanger s) {
		String json = "{\"success\":\"true\"}";
		saftyCheckDAO.updateRiskprodanger(s);
		return json;
	}

	public String deleteRiskprodanger(String ID) {
		Riskprodanger riskprodanger = new Riskprodanger();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			riskprodanger.setId(Integer.parseInt(temp[i]));
			saftyCheckDAO.deleteRiskprodanger(riskprodanger);
		}
		return json;
	}

	// *****************Risksafepg表操作************************
	public String getRisksafepgList(String findstr, int start, int limit, String projectName) {
		List<Risksafepg> list = saftyCheckDAO.getRisksafepgList(findstr, start, limit, projectName);
		int total = saftyCheckDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";

		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Risksafepg s = list.get(i);
			jsonStr += "{\"ID\":\"" + s.getId() + "\",\"shigstage\":\"" + s.getShigstage() + "\",\"safeptime\":\""
					+ s.getSafeptime() + "\",\"bzperson\":\"" + s.getBzperson() + "\",\"shperson\":\"" + s.getShperson()
					+ "\",\"ProjectName\":\"" + s.getProjectName() + "\",\"Accessory\":\"" + s.getAccessory() + "\"}";

			System.out.println(jsonStr);
		}
		jsonStr += "]}";
		return jsonStr;
	}

	public String addRisksafepg(Risksafepg s, String fileName, String rootPath) {
		String json = "{\"success\":\"true\"}";
		// 有可能需要用到当前时间作为编号
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");// 设置日期格式
		String mytime = df.format(new Date());

		// 分别获取当前年月日
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + 1;
		int day = now.get(Calendar.DATE);
		String sy = String.valueOf(year);
		String sm = String.valueOf(month);
		if (month < 10) {
			sm = "0" + sm;
		}
		String sd = String.valueOf(day);
		if (day < 10) {
			sd = "0" + sd;
		}

		// 建立月份和项目名文件夹
		String ym = rootPath + sy + sm + "\\";
		// String ympname = ym + p.getName() + "\\";
		String ympname = ym + s.getId() + "\\";
		File directory1 = new File(ym); // 根目录是否存在
		if (!directory1.exists())
			directory1.mkdir();
		File directory2 = new File(ympname); // 根目录是否存在
		if (!directory2.exists())
			directory2.mkdir();

		// 拷贝temp中的文件到正确的文件夹
		String[] newFile = fileName.split("\\*");
		for (String copyFile : newFile) {
			// cutGeneralFile( rootPath+"temp\\"+copyFile,ym + p.getName());
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym + s.getId());
		}

		// 存入数据库的内容
		// String accessory = sy+sm + "*" + p.getName() + "*"+ fileName;
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}
		String accessory = sy + sm + "*" + s.getId() + "*" + fileName;

		// 打包
		// byte[] buffer = new byte[1024];
		// if (fileName.length() > 0) {
		// // String strZipName = ympname + sy + sm + sd + p.getName()+".zip";
		// String strZipName = ympname + sy + sm + sd + s.getId() + ".zip";
		// System.out.println("压缩包名称:" + strZipName);
		// try {
		// ZipOutputStream out = new ZipOutputStream(new
		// FileOutputStream(strZipName));
		// String[] allfile = fileName.split("\\*");
		// System.out.println(Arrays.toString(allfile));
		// for (int i = 0; i < allfile.length; i++) {
		// File tempfile = new File(ympname + allfile[i]);
		// if (tempfile.exists()) {
		// FileInputStream fis = new FileInputStream(tempfile);
		// out.putNextEntry(new ZipEntry(tempfile.getName()));
		// int len;
		// while ((len = fis.read(buffer)) > 0) {
		// out.write(buffer, 0, len);
		// }
		// out.closeEntry();
		// fis.close();
		// }
		// }
		// // accessory = accessory+sy+sm+sd+p.getName()+".zip";
		// accessory = accessory + sy + sm + sd + s.getId() + ".zip";
		// out.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }

		// doc转pdf
		if (fileName.length() != 0) {
			String[] allFile = fileName.split("\\*");
			for (String temp : allFile) {
				if (temp.endsWith(".doc")) {
					System.out.println(ympname + temp);
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 4) + ".pdf");
				} else if (temp.endsWith(".docx")) {
					System.out.println(ympname + temp);
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 5) + ".pdf");
				} else if (temp.endsWith(".xlsx")) {
					System.out.println("-----------xlsx");
					ExcelToPdf.excel2pdf(ympname + temp, ympname + temp.substring(0, temp.length() - 5) + ".pdf");
				} else if (temp.endsWith("xls"))

				{
					System.out.println("----------xls");
					ExcelToPdf.excel2pdf(ympname + temp, ympname + temp.substring(0, temp.length() - 4) + ".pdf");
				}
			}
		}

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			s.setAccessory(fileName);
		} else {
			s.setAccessory(accessory);
		}

		saftyCheckDAO.insertRisksafepg(s);
		return json;
	}

	public String editRisksafepg(Risksafepg s, String fileName, String rootPath) {
		int oldAccessoryNone = 0;
		if (s.getAccessory() == null || s.getAccessory().length() <= 0) {
			oldAccessoryNone = 1;
		}
		String json = "{\"success\":\"true\"}";
		// 有可能需要用到当前时间作为编号
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");// 设置日期格式
		String mytime = df.format(new Date());

		// 分别获取当前年月日
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + 1;
		int day = now.get(Calendar.DATE);
		String sy = String.valueOf(year);
		String sm = String.valueOf(month);
		if (month < 10) {
			sm = "0" + sm;
		}
		String sd = String.valueOf(day);
		if (day < 10) {
			sd = "0" + sd;
		}

		if ((s.getAccessory() == null || s.getAccessory().length() <= 0)
				&& (fileName != null && fileName.length() > 0)) {
			// 建立月份和项目名文件夹
			String ym = rootPath + sy + sm + "\\";
			// String ympname = ym + p.getName() + "\\";
			String ympname = ym + s.getId() + "\\";
			File directory1 = new File(ym); // 根目录是否存在
			if (!directory1.exists())
				directory1.mkdir();
			File directory2 = new File(ympname); // 根目录是否存在
			if (!directory2.exists())
				directory2.mkdir();
		}

		String oldAccessory = s.getAccessory();
		String[] oldFile = oldAccessory.split("\\*");
		int oldFileLength = oldFile.length;
		String oldFoldName;
		if (oldAccessoryNone == 0) {
			oldFoldName = oldFile[0] + "\\" + oldFile[1];
		} else {
			// oldFoldName = sy + sm +"\\" +p.getName();
			oldFoldName = sy + sm + "\\" + s.getId();
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			// newAccessory = sy + sm + "*" + p.getName() +"*"+fileName;
			newAccessory = sy + sm + "*" + s.getId() + "*" + fileName;
		}

		// ------------------------删除列表中不存在的文件---------------------------//
		for (int i = 2; i < oldFileLength - 1; i++) {
			String existFileInDB = oldFile[i];
			// 列表中是否存在该文件
			boolean hasFileInList = false;
			for (String existFileInList : newFile) {
				if (existFileInDB.equals(existFileInList)) {
					hasFileInList = true;
					break;
				}
			}
			if (!hasFileInList) {
				deleteFile(rootPath + oldFoldName + "\\" + existFileInDB);
			}
		}
		// -----------------------end----------------------------------//
		// 拷贝temp文件夹的新文件到对应文件夹
		for (String oneFile : newFile) {
			boolean hasFileInList = false;
			for (int i = 2; i < oldFileLength - 1; i++) {
				if (oneFile.equals(oldFile[i])) {
					hasFileInList = true;
					break;
				}
			}
			if (!hasFileInList) {
				cutGeneralFile(rootPath + "temp\\" + oneFile, rootPath + oldFoldName);
			}

		}
		if (oldAccessoryNone == 0) {
			// deleteFile(rootPath + oldFoldName + "\\" + oldFile[oldFile.length
			// - 1]);
		}
		// ----------打包------------------//
		// byte[] buffer = new byte[1024];
		// if (fileName.length() > 0) {
		// String strZipName = rootPath + oldFoldName + "\\" + sy + sm + sd +
		// s.getId() + ".zip";
		// try {
		// ZipOutputStream out = new ZipOutputStream(new
		// FileOutputStream(strZipName));
		// String[] allfile = fileName.split("\\*");
		// for (int i = 0; i < allfile.length; i++) {
		// File tempfile = new File(rootPath + oldFoldName + "\\" + allfile[i]);
		// if (tempfile.exists()) {
		// FileInputStream fis = new FileInputStream(tempfile);
		// out.putNextEntry(new ZipEntry(tempfile.getName()));
		// int len;
		// while ((len = fis.read(buffer)) > 0) {
		// out.write(buffer, 0, len);
		// }
		// out.closeEntry();
		// fis.close();
		// }
		// }
		// newAccessory = newAccessory + sy + sm + sd + s.getId() + ".zip";
		// out.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }

		if (fileName.length() != 0) {
			String[] allFile = fileName.split("\\*");
			for (String tempFile : allFile) {
				if (tempFile.endsWith(".doc")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 4) + ".pdf");
				} else if (tempFile.endsWith(".docx")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 5) + ".pdf");
				} else if (tempFile.endsWith(".xlsx")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					ExcelToPdf.excel2pdf(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 5) + ".pdf");
				} else if (tempFile.endsWith(".xls")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					ExcelToPdf.excel2pdf(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 4) + ".pdf");
				}
			}
		}

		newAccessory = newAccessory.substring(0, newAccessory.length() - 1);
		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			s.setAccessory(fileName);
		} else {
			s.setAccessory(newAccessory);
		}

		saftyCheckDAO.updateRisksafepg(s);

		// return Saftycheck2(s, fileName2, rootPath);
		return json;
	}

	public String addRisksafepg(Risksafepg s) {
		String json = "{\"success\":\"true\"}";
		saftyCheckDAO.insertRisksafepg(s);
		return json;
	}

	public String updateRisksafepg(Risksafepg s) {
		String json = "{\"success\":\"true\"}";
		saftyCheckDAO.updateRisksafepg(s);
		return json;
	}

	public String deleteRisksafepg(String ID) {
		Risksafepg risksafepg = new Risksafepg();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			risksafepg.setId(Integer.parseInt(temp[i]));
			saftyCheckDAO.deleteRisksafepg(risksafepg);
		}
		return json;
	}

	// *****************Fenbaoyinhuanpczlgzfa表操作************************
	public String getFenbaoyinhuanpczlgzfaList(String findstr, int start, int limit, String projectName) {
		List<Fenbaoyinhuanpczlgzfa> list = saftyCheckDAO.getFenbaoyinhuanpczlgzfaList(findstr, start, limit,
				projectName);
		int total = saftyCheckDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";

		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Fenbaoyinhuanpczlgzfa s = list.get(i);
			String[] accessary = s.getAccessory().split("\\*");
			String filename = "";
			if (accessary.length >= 2) {
				filename = accessary[2];
			}

			jsonStr += "{\"ID\":\"" + s.getId() + "\",\"year\":\"" + s.getYear() + "\",\"fenbaoname\":\""
					+ s.getFenbaoname() + "\",\"workname\":\"" + s.getWorkname() + "\",\"filename\":\"" + filename
					+ "\",\"uploadtime\":\"" + s.getUploadtime() + "\",\"ProjectName\":\"" + s.getProjectName()
					+ "\",\"Accessory\":\"" + s.getAccessory() + "\"}";

			System.out.println(jsonStr);
		}
		jsonStr += "]}";
		return jsonStr;
	}

	public String addFenbaoyinhuanpczlgzfa(Fenbaoyinhuanpczlgzfa s, String fileName, String rootPath) {
		String json = "{\"success\":\"true\"}";
		// 有可能需要用到当前时间作为编号
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");// 设置日期格式
		String mytime = df.format(new Date());

		// 分别获取当前年月日
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + 1;
		int day = now.get(Calendar.DATE);
		String sy = String.valueOf(year);
		String sm = String.valueOf(month);
		if (month < 10) {
			sm = "0" + sm;
		}
		String sd = String.valueOf(day);
		if (day < 10) {
			sd = "0" + sd;
		}

		// 建立月份和项目名文件夹
		String ym = rootPath + sy + sm + "\\";
		// String ympname = ym + p.getName() + "\\";
		String ympname = ym + s.getId() + "\\";
		File directory1 = new File(ym); // 根目录是否存在
		if (!directory1.exists())
			directory1.mkdir();
		File directory2 = new File(ympname); // 根目录是否存在
		if (!directory2.exists())
			directory2.mkdir();

		// 拷贝temp中的文件到正确的文件夹
		String[] newFile = fileName.split("\\*");
		for (String copyFile : newFile) {
			// cutGeneralFile( rootPath+"temp\\"+copyFile,ym + p.getName());
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym + s.getId());
		}

		// 存入数据库的内容
		// String accessory = sy+sm + "*" + p.getName() + "*"+ fileName;
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}
		String accessory = sy + sm + "*" + s.getId() + "*" + fileName;

		// 打包
		// byte[] buffer = new byte[1024];
		// if (fileName.length() > 0) {
		// // String strZipName = ympname + sy + sm + sd + p.getName()+".zip";
		// String strZipName = ympname + sy + sm + sd + s.getId() + ".zip";
		// System.out.println("压缩包名称:" + strZipName);
		// try {
		// ZipOutputStream out = new ZipOutputStream(new
		// FileOutputStream(strZipName));
		// String[] allfile = fileName.split("\\*");
		// System.out.println(Arrays.toString(allfile));
		// for (int i = 0; i < allfile.length; i++) {
		// File tempfile = new File(ympname + allfile[i]);
		// if (tempfile.exists()) {
		// FileInputStream fis = new FileInputStream(tempfile);
		// out.putNextEntry(new ZipEntry(tempfile.getName()));
		// int len;
		// while ((len = fis.read(buffer)) > 0) {
		// out.write(buffer, 0, len);
		// }
		// out.closeEntry();
		// fis.close();
		// }
		// }
		// // accessory = accessory+sy+sm+sd+p.getName()+".zip";
		// accessory = accessory + sy + sm + sd + s.getId() + ".zip";
		// out.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }

		// doc转pdf
		if (fileName.length() != 0) {
			String[] allFile = fileName.split("\\*");
			for (String temp : allFile) {
				if (temp.endsWith(".doc")) {
					System.out.println(ympname + temp);
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 4) + ".pdf");
				} else if (temp.endsWith(".docx")) {
					System.out.println(ympname + temp);
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 5) + ".pdf");
				} else if (temp.endsWith(".xlsx")) {
					System.out.println("-----------xlsx");
					ExcelToPdf.excel2pdf(ympname + temp, ympname + temp.substring(0, temp.length() - 5) + ".pdf");
				} else if (temp.endsWith("xls"))

				{
					System.out.println("----------xls");
					ExcelToPdf.excel2pdf(ympname + temp, ympname + temp.substring(0, temp.length() - 4) + ".pdf");
				}
			}
		}

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			s.setAccessory(fileName);
		} else {
			s.setAccessory(accessory);
		}

		saftyCheckDAO.insertFenbaoyinhuanpczlgzfa(s);
		return json;
	}

	public String editFenbaoyinhuanpczlgzfa(Fenbaoyinhuanpczlgzfa s, String fileName, String rootPath) {
		int oldAccessoryNone = 0;
		if (s.getAccessory() == null || s.getAccessory().length() <= 0) {
			oldAccessoryNone = 1;
		}
		String json = "{\"success\":\"true\"}";
		// 有可能需要用到当前时间作为编号
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");// 设置日期格式
		String mytime = df.format(new Date());

		// 分别获取当前年月日
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + 1;
		int day = now.get(Calendar.DATE);
		String sy = String.valueOf(year);
		String sm = String.valueOf(month);
		if (month < 10) {
			sm = "0" + sm;
		}
		String sd = String.valueOf(day);
		if (day < 10) {
			sd = "0" + sd;
		}

		if ((s.getAccessory() == null || s.getAccessory().length() <= 0)
				&& (fileName != null && fileName.length() > 0)) {
			// 建立月份和项目名文件夹
			String ym = rootPath + sy + sm + "\\";
			// String ympname = ym + p.getName() + "\\";
			String ympname = ym + s.getId() + "\\";
			File directory1 = new File(ym); // 根目录是否存在
			if (!directory1.exists())
				directory1.mkdir();
			File directory2 = new File(ympname); // 根目录是否存在
			if (!directory2.exists())
				directory2.mkdir();
		}

		String oldAccessory = s.getAccessory();
		String[] oldFile = oldAccessory.split("\\*");
		int oldFileLength = oldFile.length;
		String oldFoldName;
		if (oldAccessoryNone == 0) {
			oldFoldName = oldFile[0] + "\\" + oldFile[1];
		} else {
			// oldFoldName = sy + sm +"\\" +p.getName();
			oldFoldName = sy + sm + "\\" + s.getId();
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			// newAccessory = sy + sm + "*" + p.getName() +"*"+fileName;
			newAccessory = sy + sm + "*" + s.getId() + "*" + fileName;
		}

		// ------------------------删除列表中不存在的文件---------------------------//
		for (int i = 2; i < oldFileLength - 1; i++) {
			String existFileInDB = oldFile[i];
			// 列表中是否存在该文件
			boolean hasFileInList = false;
			for (String existFileInList : newFile) {
				if (existFileInDB.equals(existFileInList)) {
					hasFileInList = true;
					break;
				}
			}
			if (!hasFileInList) {
				deleteFile(rootPath + oldFoldName + "\\" + existFileInDB);
			}
		}
		// -----------------------end----------------------------------//
		// 拷贝temp文件夹的新文件到对应文件夹
		for (String oneFile : newFile) {
			boolean hasFileInList = false;
			for (int i = 2; i < oldFileLength - 1; i++) {
				if (oneFile.equals(oldFile[i])) {
					hasFileInList = true;
					break;
				}
			}
			if (!hasFileInList) {
				cutGeneralFile(rootPath + "temp\\" + oneFile, rootPath + oldFoldName);
			}

		}
		if (oldAccessoryNone == 0) {
			// deleteFile(rootPath + oldFoldName + "\\" + oldFile[oldFile.length
			// - 1]);
		}
		// ----------打包------------------//
		// byte[] buffer = new byte[1024];
		// if (fileName.length() > 0) {
		// String strZipName = rootPath + oldFoldName + "\\" + sy + sm + sd +
		// s.getId() + ".zip";
		// try {
		// ZipOutputStream out = new ZipOutputStream(new
		// FileOutputStream(strZipName));
		// String[] allfile = fileName.split("\\*");
		// for (int i = 0; i < allfile.length; i++) {
		// File tempfile = new File(rootPath + oldFoldName + "\\" + allfile[i]);
		// if (tempfile.exists()) {
		// FileInputStream fis = new FileInputStream(tempfile);
		// out.putNextEntry(new ZipEntry(tempfile.getName()));
		// int len;
		// while ((len = fis.read(buffer)) > 0) {
		// out.write(buffer, 0, len);
		// }
		// out.closeEntry();
		// fis.close();
		// }
		// }
		// newAccessory = newAccessory + sy + sm + sd + s.getId() + ".zip";
		// out.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }

		if (fileName.length() != 0) {
			String[] allFile = fileName.split("\\*");
			for (String tempFile : allFile) {
				if (tempFile.endsWith(".doc")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 4) + ".pdf");
				} else if (tempFile.endsWith(".docx")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 5) + ".pdf");
				} else if (tempFile.endsWith(".xlsx")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					ExcelToPdf.excel2pdf(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 5) + ".pdf");
				} else if (tempFile.endsWith(".xls")) {
					System.out.println(rootPath + oldFoldName + "\\" + tempFile);
					ExcelToPdf.excel2pdf(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 4) + ".pdf");
				}
			}
		}

		newAccessory = newAccessory.substring(0, newAccessory.length() - 1);
		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			s.setAccessory(fileName);
		} else {
			s.setAccessory(newAccessory);
		}

		saftyCheckDAO.updateFenbaoyinhuanpczlgzfa(s);

		// return Saftycheck2(s, fileName2, rootPath);
		return json;
	}

	public String addFenbaoyinhuanpczlgzfa(Fenbaoyinhuanpczlgzfa s) {
		String json = "{\"success\":\"true\"}";
		saftyCheckDAO.insertFenbaoyinhuanpczlgzfa(s);
		return json;
	}

	public String updateFenbaoyinhuanpczlgzfa(Fenbaoyinhuanpczlgzfa s) {
		String json = "{\"success\":\"true\"}";
		saftyCheckDAO.updateFenbaoyinhuanpczlgzfa(s);
		return json;
	}

	public String deleteFenbaoyinhuanpczlgzfa(String ID) {
		Fenbaoyinhuanpczlgzfa fenbaoyinhuanpczlgzfa = new Fenbaoyinhuanpczlgzfa();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			fenbaoyinhuanpczlgzfa.setId(Integer.parseInt(temp[i]));
			saftyCheckDAO.deleteFenbaoyinhuanpczlgzfa(fenbaoyinhuanpczlgzfa);
		}
		return json;
	}

	// jlf===============================
	public String getSaftycheck326List(String findstr, int start, int limit) {
		List<Saftycheck> list = saftyCheckDAO.getSaftycheck326List(findstr, start, limit);
		int total = saftyCheckDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Saftycheck s = list.get(i);
			jsonStr += "{\"ID\":\"" + s.getId() + "\",\"checktime\":\"" + s.getChecktime() + "\",\"checktype\":\""
					+ s.getChecktype() + "\",\"checkunit\":\"" + s.getCheckunit() + "\",\"shoujianunit\":\""
					+ s.getShoujianunit() + "\",\"checkperson\":\"" + s.getCheckperson() + "\",\"noticeandnum\":\""
					+ s.getNoticeandnum() + "\",\"problem\":\"" + s.getProblem() + "\",\"pronum\":\"" + s.getPronum()
					+ "\",\"prodegree\":\"" + s.getProdegree() + "\",\"prokind\":\"" + s.getProkind()
					+ "\",\"advice\":\"" + s.getAdvice() + "\",\"advicenum\":\"" + s.getAdvicenum()
					+ "\",\"timeline\":\"" + s.getTimeline() + "\",\"iscorrective\":\"" + s.getIscorrective()
					+ "\",\"replytime\":\"" + s.getReplytime() + "\",\"replyandnum\":\"" + s.getReplyandnum()
					+ "\",\"Accessory\":\"" + s.getAccessory() + "\",\"Accessory2\":\"" + s.getAccessory2()
					+ "\",\"last\":\"" + s.getLast() + "\",\"content\":\"" + s.getContent() + "\",\"type\":\""
					+ s.getType() + "\"}";

			System.out.println(jsonStr);
		}
		jsonStr += "]}";
		// System.out.println(jsonStr);
		return jsonStr;
	}

	public String getSaftycheck327List(String findstr, int start, int limit) {
		List<Saftycheck> list = saftyCheckDAO.getSaftycheck327List(findstr, start, limit);
		int total = saftyCheckDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Saftycheck s = list.get(i);
			jsonStr += "{\"ID\":\"" + s.getId() + "\",\"checktime\":\"" + s.getChecktime() + "\",\"checktype\":\""
					+ s.getChecktype() + "\",\"checkunit\":\"" + s.getCheckunit() + "\",\"shoujianunit\":\""
					+ s.getShoujianunit() + "\",\"checkperson\":\"" + s.getCheckperson() + "\",\"noticeandnum\":\""
					+ s.getNoticeandnum() + "\",\"problem\":\"" + s.getProblem() + "\",\"pronum\":\"" + s.getPronum()
					+ "\",\"prodegree\":\"" + s.getProdegree() + "\",\"prokind\":\"" + s.getProkind()
					+ "\",\"advice\":\"" + s.getAdvice() + "\",\"advicenum\":\"" + s.getAdvicenum()
					+ "\",\"timeline\":\"" + s.getTimeline() + "\",\"iscorrective\":\"" + s.getIscorrective()
					+ "\",\"replytime\":\"" + s.getReplytime() + "\",\"replyandnum\":\"" + s.getReplyandnum()
					+ "\",\"Accessory\":\"" + s.getAccessory() + "\",\"Accessory2\":\"" + s.getAccessory2()
					+ "\",\"last\":\"" + s.getLast() + "\",\"content\":\"" + s.getContent() + "\",\"type\":\""
					+ s.getType() + "\"}";

			System.out.println(jsonStr);
		}
		jsonStr += "]}";
		// System.out.println(jsonStr);
		return jsonStr;
	}
	// end========================

	//
	public String getfaxianwentiList(String findstr, String type, int start, int limit, String projectName) {
		List<Saftycheck> list = saftyCheckDAO.getSaftycheckList(findstr, type, start, limit, projectName);
		int total = saftyCheckDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		int flag = 0;
		for (int i = 0; i < list.size(); ++i) {
			Saftycheck s = list.get(i);
			String[] problem = s.getProblem().split("\\*");
			String[] prokind = s.getProkind().split("\\*");
			String[] prodegree = s.getProdegree().split("\\*");

			for (int j = 0; j < problem.length; j++) {
				if (flag == 1) {
					jsonStr += ",";
				} else {
					flag = 1;
				}
				jsonStr += "{\"ID\":\"" + s.getId() + "\",\"checktime\":\"" + s.getChecktime() + "\",\"ProjectName\":\""
						+ s.getProjectName() + "\",\"problem\":\"" + problem[j] + "\",\"prokind\":\"" + prokind[j]
						+ "\",\"prodegree\":\"" + prodegree[j] + "\"}";
			}

			System.out.println(jsonStr);
		}
		jsonStr += "]}";
		// System.out.println(jsonStr);
		return jsonStr;
	}

	public String getyinhuanpaichaList(String findstr, String type, int start, int limit, String projectName) {

		List<Taizhang> list = saftyCheckDAO.getTaizhangList(findstr, type, start, limit, projectName);
		int total = saftyCheckDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";

		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Taizhang s = list.get(i);
			jsonStr += "{\"ID\":\"" + s.getId() + "\",\"no\":\"" + s.getNo() + "\",\"checkId\":\"" + s.getCheckId()
					+ "\",\"problem\":\"" + s.getProblem() + "\",\"location\":\"" + s.getLocation()
					+ "\",\"checkperson\":\"" + s.getCheckperson() + "\",\"prolevel\":\"" + s.getProlevel()
					+ "\",\"correction\":\"" + s.getCorrection() + "\",\"solvedep\":\"" + s.getSolvedep()
					+ "\",\"solvePerson\":\"" + s.getSolvePerson() + "\",\"expTime\":\"" + s.getExpTime()
					+ "\",\"correctionfee\":\"" + s.getCorrectionfee() + "\",\"solveExp\":\"" + s.getSolveExp()
					+ "\",\"solveTime\":\"" + s.getSolveTime() + "\",\"supperson\":\"" + s.getSupperson()
					+ "\",\"prevent\":\"" + s.getPrevent() + "\",\"solveAcc\":\"" + s.getSolveAcc()
					+ "\",\"ProjectName\":\"" + s.getProjectName() + "\",\"Accessory\":\"" + s.getAccessory() + "\"}";

			System.out.println(jsonStr);
		}
		jsonStr += "]}";
		return jsonStr;
	}

	public String getColumnDataList(String findstr, String type, int start, int limit, String projectName) {
		List<Saftyproblem> listproKind = saftyCheckDAO.getSaftyproblemList(findstr, start, limit);
		String[] problemkinds = new String[listproKind.size()];
		String[] subproblemKinds = new String[listproKind.size()];
		int[] problemNum = new int[listproKind.size()];

		for (int j = 0; j < listproKind.size(); j++) {
			problemkinds[j] = listproKind.get(j).getKind();
			subproblemKinds[j] = listproKind.get(j).getSubkind();
			problemNum[j] = 0;
		}

		int numOfkind1 = 0;
		int numOfkind2 = 0;
		int numOfkind3 = 0;
		for (int y = 0; y < problemkinds.length; y++) {
			if (problemkinds[y].equals("基础管理类隐患")) {
				numOfkind1++;
			} else if (problemkinds.equals("现场管理类隐患")) {
				numOfkind2++;
			} else {
				numOfkind3++;
			}
		}

		List<Saftycheck> list = saftyCheckDAO.getSaftycheckList(findstr, type, start, limit, projectName);
		int total = saftyCheckDAO.datacount;
		total = numOfkind1;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		int flag = 0;
		for (int i = 0; i < list.size(); ++i) {

			Saftycheck s = list.get(i);
			String[] problem = s.getProblem().split("\\*");
			String[] prokind = s.getProkind().split("\\*");
			String[] prodegree = s.getProdegree().split("\\*");

			for (int m = 0; m < prokind.length; m++) {
				prokind[m] = prokind[m].substring(0, prokind[m].length() - 6);
			}

			for (int k = 0; k < prokind.length; k++) {
				for (int x = 0; x < subproblemKinds.length; x++) {
					if (prokind[k].equals(subproblemKinds[x])) {
						problemNum[x]++;
					}
				}
			}
		}

		int num = 0;
		for (int n = 0; n < listproKind.size(); n++) {
			// if (flag == 1) {
			// jsonStr += ",";
			// } else {
			// flag = 1;
			// }
			if (problemkinds[n].equals("基础管理类隐患")) {
				num++;
				if (n == numOfkind1) {
					jsonStr += "{\"problem\":\"" + subproblemKinds[n] + "\",\"num\":\"" + problemNum[n] + "\"}";
				} else {
					jsonStr += "{\"problem\":\"" + subproblemKinds[n] + "\",\"num\":\"" + problemNum[n] + "\"},";
				}
			}

		}
		System.out.println(jsonStr);

		jsonStr += "]}";
		// System.out.println(jsonStr);
		return jsonStr;
	}

	public String getColumnDataList(String findstr, String type, int start, int limit, String projectName,
			String kind) {
		List<Saftyproblem> listproKind = saftyCheckDAO.getSaftyproblemList(findstr, start, limit, kind);
		String[] problemkinds = new String[listproKind.size()];
		String[] subproblemKinds = new String[listproKind.size()];
		int[] problemNum = new int[listproKind.size()];

		for (int j = 0; j < listproKind.size(); j++) {
			problemkinds[j] = listproKind.get(j).getKind();
			subproblemKinds[j] = listproKind.get(j).getSubkind();
			problemNum[j] = 0;
		}

		List<Saftycheck> list = saftyCheckDAO.getSaftycheckList(findstr, type, start, limit, projectName);
		int total = saftyCheckDAO.datacount;
		total = listproKind.size();

		int flag = 0;
		for (int i = 0; i < list.size(); ++i) {

			Saftycheck s = list.get(i);
			String[] problem = s.getProblem().split("\\*");
			String[] prokind = s.getProkind().split("\\*");
			String[] prodegree = s.getProdegree().split("\\*");
			if (problem.length == 0) {
				continue;
			}

			for (int m = 0; m < prokind.length; m++) {
				prokind[m] = prokind[m].substring(0, prokind[m].length() - 6);
			}

			for (int k = 0; k < prokind.length; k++) {
				for (int x = 0; x < subproblemKinds.length; x++) {
					if (prokind[k].equals(subproblemKinds[x])) {
						problemNum[x]++;
					}
				}
			}
		}

		int lastNum = 0;
		int totalNum = 0;
		for (int n = 0; n < listproKind.size(); n++) {
			if (problemNum[n] > 0) {
				lastNum = n;
				totalNum++;
			}
		}
		total = totalNum;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		int hasPro = 0;

		// 问题总数量
		float sumOfPro = 0;
		for (int n = 0; n < listproKind.size(); n++) {
			if (problemNum[n] > 0) {
				sumOfPro += problemNum[n];
			}
		}
		System.out.println(sumOfPro+"------------");

		for (int n = 0; n < listproKind.size(); n++) {

			if (problemNum[n] > 0) {

				String percentage = String.format("%.2f", (problemNum[n] / sumOfPro)*100) + "%";

				hasPro = 1;
				if (n == lastNum) {
					jsonStr += "{\"problem\":\"" + subproblemKinds[n] + "\",\"numofpro\":\"" + problemNum[n]
							+ "\",\"percentage\":\"" + percentage + "\"}";
				} else {
					jsonStr += "{\"problem\":\"" + subproblemKinds[n] + "\",\"numofpro\":\"" + problemNum[n]
							+ "\",\"percentage\":\"" + percentage + "\"},";
				}
			}
		}
		System.out.println("哈哈哈hasPro" + hasPro);
		if (hasPro == 0) {

			jsonStr = "{\"total\":\"" + 1 + "\",\"rows\":[" + "{\"problem\":\"" + "无" + "\",\"numofpro\":\"" + 0
					 + "\",\"percentage\":\"" + "0%"+ "\"}";
			System.out.println("哈哈哈哈哈00000000000000");
		}
		System.out.println(jsonStr);

		jsonStr += "]}";
		return jsonStr;
	}

	// 横坐标为"在建项目部"， 纵坐标为"平均安全隐患数"，平均安全隐患数=安全隐患总数/检查总次数。
	// projectmanagement saftycheck
	public String getColumnData2List(String findstr, String type, int start, int limit, String projectName) {
		// 获得 项目列表
		List<Projectmanagement> listProject = saftyCheckDAO.getProjectmanagementList(findstr, start, limit,
				projectName);

		String[] projectShort = new String[listProject.size()];
		int[] proPercent = new int[listProject.size()];
		for (int k = 0; k < listProject.size(); k++) {
			Projectmanagement projectmanagement = listProject.get(k);
			projectShort[k] = projectmanagement.getScale();
		}

		List<Saftycheck> list = saftyCheckDAO.getSaftycheckList(findstr, type, start, limit, projectName);

		Projectmanagement projectmanagement;
		Saftycheck saftycheck;
		int numofjiancha = 0;
		int numofpro = 0;
		for (int i = 0; i < listProject.size(); i++) {
			numofjiancha = 0;
			numofpro = 0;
			projectmanagement = listProject.get(i);
			for (int j = 0; j < list.size(); j++) {
				saftycheck = list.get(j);
				if (saftycheck.getProjectName().equals(projectmanagement.getName())) {
					numofjiancha++;
					String[] problem = saftycheck.getProblem().split("\\*");
					numofpro += problem.length;

				}
			}

			if (numofjiancha <= 0) {
				proPercent[i] = 0;
			} else {
				// DecimalFormat df = new DecimalFormat("######0.00");
				// double t = numofpro / numofjiancha;
				proPercent[i] = numofpro / numofjiancha;
			}
		}

		int total = saftyCheckDAO.datacount;
		total = projectShort.length;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int n = 0; n < projectShort.length; n++) {
			// if (flag == 1) {
			// jsonStr += ",";
			// } else {
			// flag = 1;
			// }
			if (n == projectShort.length - 1) {
				jsonStr += "{\"projectOn\":\"" + projectShort[n] + "\",\"numofpro\":\"" + proPercent[n] + "\"}";
			} else {
				jsonStr += "{\"projectOn\":\"" + projectShort[n] + "\",\"numofpro\":\"" + proPercent[n] + "\"},";
			}

		}
		System.out.println(jsonStr);

		jsonStr += "]}";
		return jsonStr;
	}
	
	public int getAvgProNums(String findstr, String type, int start, int limit, String projectName, String startdate,
			String enddate) {
		float avg = 0;
		float days = 0;
		float totalpros = 0;
		List<Saftycheck> list = saftyCheckDAO.getSaftycheckList2(findstr, type, start, limit, projectName, startdate,
				enddate);
		// int total = saftyCheckDAO.datacount;
		int total = 1;
		days = list.size();
		//String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); i++) {
			Saftycheck s = list.get(i);
			// String[] problem = s.getProblem().split("\\*");
			// String[] prokind = s.getProkind().split("\\*");
			// String[] prodegree = s.getProdegree().split("\\*");
			totalpros += s.getPronum();
			System.out.println(s.getPronum() + "s.getPronum()&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&" );
		}

		System.out.println(days + "&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&" + totalpros+"******************"+list.size());
		if (days == 0) {
			avg = 0;
		} else {
			avg = totalpros / days;
		}
		return (int)avg;
	}

}
