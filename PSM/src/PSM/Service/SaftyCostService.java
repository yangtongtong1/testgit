package PSM.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipOutputStream;

import org.apache.tools.zip.ZipEntry;

import PSM.DAO.SaftyCostDAO;
import PSM.Tool.ExcelToPdf;
import PSM.Tool.ReadExcel;
import PSM.Tool.WordToPdf;
import hibernate.Fenbaoplan;
import hibernate.Fenbaosaftyaccounts;
import hibernate.Fenbaosaftycostsum;
import hibernate.Fenbaosaftyjiancha;
import hibernate.Gongtitai;
import hibernate.Project;
import hibernate.Projectperson;
import hibernate.Saftyaccounts;
import hibernate.Saftycost;
import hibernate.Saftycostplan;
import hibernate.Saftycosttj;
import hibernate.Saftycosttj1;
import hibernate.Saftycosttj2;
import hibernate.Saftycosttj3;
import hibernate.Saftyjiancha;
import hibernate.Sanxiang;
import hibernate.Weixianyuan;

public class SaftyCostService {
	private SaftyCostDAO saftyCostDAO;

	public SaftyCostDAO getSaftyCostDAO() {
		return saftyCostDAO;
	}

	public void setSaftyCostDAO(SaftyCostDAO saftyCostDAO) {
		this.saftyCostDAO = saftyCostDAO;
	}

	// saftycost表的服务*********************************************

	public String getSaftycostList(String findstr, int start, int limit) {
		List<Saftycost> list = saftyCostDAO.getSaftycostList(findstr, start, limit);
		int total = saftyCostDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Saftycost s = list.get(i);
			jsonStr += "{\"ID\":\"" + s.getId() + "\",\"subjectnum\":\"" + s.getSubjectnum() + "\",\"costkind\":\""
					+ s.getCostkind() + "\",\"costetails\":\"" + s.getCostetails() + "\",\"jan\":\"" + s.getJan()
					+ "\",\"feb\":\"" + s.getFeb() + "\"";
			jsonStr += ",\"mar\":\"" + s.getMar() + "\",\"apr\":\"" + s.getApr() + "\",\"may\":\"" + s.getMay()
					+ "\",\"june\":\"" + s.getJune() + "\",\"july\":\"" + s.getJuly() + "\",\"aug\":\"" + s.getAug()
					+ "\",\"sept\":\"" + s.getSept() + "\",\"oct\":\"" + s.getOct() + "\",\"nov\":\"" + s.getNov()
					+ "\",\"dec\":\"" + s.getDec() + "\",\"sumcost\":\"" + s.getSumcost() + "\",\"planorcost\":\""
					+ s.getPlanorcost() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String getAllSaftycost() {
		List<Saftycost> list = saftyCostDAO.findAllSaftycost();
		int total = saftyCostDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Saftycost s = list.get(i);
			jsonStr += "{\"ID\":\"" + s.getId() + "\",\"subjectnum\":\"" + s.getSubjectnum() + "\",\"costkind\":\""
					+ s.getCostkind() + "\",\"costetails\":\"" + s.getCostetails() + "\",\"jan\":\"" + s.getJan()
					+ "\",\"feb\":\"" + s.getFeb() + "\"";
			jsonStr += ",\"mar\":\"" + s.getMar() + "\",\"apr\":\"" + s.getApr() + "\",\"may\":\"" + s.getMay()
					+ "\",\"june\":\"" + s.getJune() + "\",\"july\":\"" + s.getJuly() + "\",\"aug\":\"" + s.getAug()
					+ "\",\"sept\":\"" + s.getSept() + "\",\"oct\":\"" + s.getOct() + "\",\"nov\":\"" + s.getNov()
					+ "\",\"dec\":\"" + s.getDec() + "\",\"sumcost\":\"" + s.getSumcost() + "\",\"planorcost\":\""
					+ s.getPlanorcost() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String getAllSaftycostplan() {
		List<Saftycost> list = saftyCostDAO.findAllSaftycostplan();
		int total = saftyCostDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Saftycost s = list.get(i);
			jsonStr += "{\"ID\":\"" + s.getId() + "\",\"subjectnum\":\"" + s.getSubjectnum() + "\",\"costkind\":\""
					+ s.getCostkind() + "\",\"costetails\":\"" + s.getCostetails() + "\",\"jan\":\"" + s.getJan()
					+ "\",\"feb\":\"" + s.getFeb() + "\"";
			jsonStr += ",\"mar\":\"" + s.getMar() + "\",\"apr\":\"" + s.getApr() + "\",\"may\":\"" + s.getMay()
					+ "\",\"june\":\"" + s.getJune() + "\",\"july\":\"" + s.getJuly() + "\",\"aug\":\"" + s.getAug()
					+ "\",\"sept\":\"" + s.getSept() + "\",\"oct\":\"" + s.getOct() + "\",\"nov\":\"" + s.getNov()
					+ "\",\"dec\":\"" + s.getDec() + "\",\"sumcost\":\"" + s.getSumcost() + "\",\"planorcost\":\""
					+ s.getPlanorcost() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String getAllSaftycostcost() {
		List<Saftycost> list = saftyCostDAO.findAllSaftycostcost();
		int total = saftyCostDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Saftycost s = list.get(i);
			jsonStr += "{\"ID\":\"" + s.getId() + "\",\"subjectnum\":\"" + s.getSubjectnum() + "\",\"costkind\":\""
					+ s.getCostkind() + "\",\"costetails\":\"" + s.getCostetails() + "\",\"jan\":\"" + s.getJan()
					+ "\",\"feb\":\"" + s.getFeb() + "\"";
			jsonStr += ",\"mar\":\"" + s.getMar() + "\",\"apr\":\"" + s.getApr() + "\",\"may\":\"" + s.getMay()
					+ "\",\"june\":\"" + s.getJune() + "\",\"july\":\"" + s.getJuly() + "\",\"aug\":\"" + s.getAug()
					+ "\",\"sept\":\"" + s.getSept() + "\",\"oct\":\"" + s.getOct() + "\",\"nov\":\"" + s.getNov()
					+ "\",\"dec\":\"" + s.getDec() + "\",\"sumcost\":\"" + s.getSumcost() + "\",\"planorcost\":\""
					+ s.getPlanorcost() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addSaftycost(Saftycost s) {
		String json = "{\"success\":\"true\"}";
		saftyCostDAO.insertSaftycost(s);
		return json;
	}

	public String updateSaftycost(Saftycost s) {
		String json = "{\"success\":\"true\"}";
		saftyCostDAO.updateSaftycost(s);
		return json;
	}

	public String deleteSaftycost(String ID) {
		Saftycost saftycost = new Saftycost();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			saftycost.setId(Integer.parseInt(temp[i]));
			saftyCostDAO.deleteSaftycost(saftycost);
		}
		return json;
	}

	// Saftycostplan表的服务*********************************************

	public String getSaftycostplanList(String findstr, int start, int limit, String projectName) {
		List<Saftycostplan> list = saftyCostDAO.getSaftycostplanList(findstr, start, limit, projectName);
		int total = saftyCostDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Saftycostplan s = list.get(i);
			jsonStr += "{\"ID\":\"" + s.getId() + "\",\"subjectnum\":\"" + s.getSubjectnum() + "\",\"costkind0\":\""
					+ s.getCostkind0() + "\",\"costkind1\":\"" + s.getCostkind1() + "\",\"costkind2\":\""
					+ s.getCostkind2() + "\",\"costkind3\":\"" + s.getCostkind3() + "\",\"costkind4\":\""
					+ s.getCostkind4() + "\",\"costkind5\":\"" + s.getCostkind5() + "\",\"costkind6\":\""
					+ s.getCostkind6() + "\",\"costkind7\":\"" + s.getCostkind7() + "\",\"costkind8\":\""
					+ s.getCostkind8() + "\",\"costkind9\":\"" + s.getCostkind9() + "\",\"costkind01\":\""
					+ s.getCostkind01() + "\",\"costkind11\":\"" + s.getCostkind11() + "\",\"costkind21\":\""
					+ s.getCostkind21() + "\",\"costkind31\":\"" + s.getCostkind31() + "\",\"costkind41\":\""
					+ s.getCostkind41() + "\",\"costkind51\":\"" + s.getCostkind51() + "\",\"costkind61\":\""
					+ s.getCostkind61() + "\",\"costkind71\":\"" + s.getCostkind71() + "\",\"costkind81\":\""
					+ s.getCostkind81() + "\",\"costkind91\":\"" + s.getCostkind91() + "\",\"costkind02\":\""
					+ s.getCostkind02() + "\",\"costkind12\":\"" + s.getCostkind12() + "\",\"costkind22\":\""
					+ s.getCostkind22() + "\",\"costkind32\":\"" + s.getCostkind32() + "\",\"costkind42\":\""
					+ s.getCostkind42() + "\",\"costkind52\":\"" + s.getCostkind52() + "\",\"costkind62\":\""
					+ s.getCostkind62() + "\",\"costkind72\":\"" + s.getCostkind72() + "\",\"costkind82\":\""
					+ s.getCostkind82() + "\",\"costkind92\":\"" + s.getCostkind92() + "\",\"year\":\"" + s.getYear()
					+ "\",\"costplan\":\"" + s.getCostplan() + "\",\"costplansum\":\"" + s.getCostplansum()
					+ "\",\"costplansum1\":\"" + s.getCostplansum1() + "\",\"costplansum2\":\"" + s.getCostplansum2()
					+ "\",\"ProjectName\":\"" + s.getProjectName() + "\",\"yearorfull\":\"" + s.getYearorfull()
					+ "\",\"Accessory\":\"" + s.getAccessory() + "\"}";

			System.out.println(jsonStr);
		}
		jsonStr += "]}";
		// System.out.println(jsonStr);
		return jsonStr;
	}

	public String addSaftycostplan(Saftycostplan s, String fileName, String rootPath) {
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
		String ympname = ym + s.getSubjectnum() + "\\";
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
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym + s.getSubjectnum());
		}

		// 存入数据库的内容
		// String accessory = sy+sm + "*" + p.getName() + "*"+ fileName;
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}
		String accessory = sy + sm + "*" + s.getSubjectnum() + "*" + fileName;

		// 打包
		// byte[] buffer = new byte[1024];
		// if (fileName.length() > 0) {
		// // String strZipName = ympname + sy + sm + sd + p.getName()+".zip";
		// String strZipName = ympname + sy + sm + sd + s.getSubjectnum() +
		// ".zip";
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
		// accessory = accessory + sy + sm + sd + s.getSubjectnum() + ".zip";
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
		// basicInfoDAO.insertProject(p);
		saftyCostDAO.insertSaftycostplan(s);
		return json;
	}

	public String editSaftycostplan(Saftycostplan s, String fileName, String rootPath) {
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
			String ympname = ym + s.getSubjectnum() + "\\";
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
			oldFoldName = sy + sm + "\\" + s.getSubjectnum();
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			// newAccessory = sy + sm + "*" + p.getName() +"*"+fileName;
			newAccessory = sy + sm + "*" + s.getSubjectnum() + "*" + fileName;
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
		/*
		 * byte[] buffer = new byte[1024]; if (fileName.length() > 0) { String
		 * strZipName = rootPath + oldFoldName +
		 * "\\" + sy + sm + sd + s.getSubjectnum() + ".zip"; try {
		 * ZipOutputStream out = new ZipOutputStream(new
		 * FileOutputStream(strZipName)); String[] allfile =
		 * fileName.split("\\*"); for (int i = 0; i < allfile.length; i++) {
		 * File tempfile = new File(rootPath + oldFoldName + "\\" + allfile[i]);
		 * if (tempfile.exists()) { FileInputStream fis = new
		 * FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while ((len =
		 * fis.read(buffer)) > 0) { out.write(buffer, 0, len); }
		 * out.closeEntry(); fis.close(); } } newAccessory = newAccessory + sy +
		 * sm + sd + s.getSubjectnum() + ".zip"; out.close(); } catch (Exception
		 * e) { e.printStackTrace(); } }
		 */

		newAccessory = newAccessory.substring(0, newAccessory.length() - 1);

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

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			s.setAccessory(fileName);
		} else {
			s.setAccessory(newAccessory);
		}

		saftyCostDAO.updateSaftycostplan(s);
		return json;
	}

	public String addSaftycostplan(Saftycostplan s) {
		String json = "{\"success\":\"true\"}";
		saftyCostDAO.insertSaftycostplan(s);
		return json;
	}

	public String updateSaftycostplan(Saftycostplan s) {
		String json = "{\"success\":\"true\"}";
		saftyCostDAO.updateSaftycostplan(s);
		return json;
	}

	public String deleteSaftycostplan(String ID) {
		Saftycostplan saftycostplan = new Saftycostplan();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			saftycostplan.setId(Integer.parseInt(temp[i]));
			saftyCostDAO.deleteSaftycostplan(saftycostplan);
		}
		return json;
	}

	// Saftycosttj表的服务*********************************************

	public String getSaftycosttjList(String findstr, int start, int limit) {
		List<Saftycosttj> list = saftyCostDAO.getSaftycosttjList(findstr, start, limit);
		int total = saftyCostDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Saftycosttj s = list.get(i);
			jsonStr += "{\"ID\":\"" + s.getId() + "\",\"subjectnum\":\"" + s.getSubjectnum() + "\",\"costkind\":\""
					+ s.getCostkind() + "\",\"year\":\"" + s.getYear() + "\",\"cost\":\"" + s.getCost()
					+ "\",\"costrealtime\":\"" + s.getCostrealtime() + "\",\"Accessory\":\"" + s.getAccessory() + "\"}";

			System.out.println(jsonStr);
		}
		jsonStr += "]}";
		// System.out.println(jsonStr);
		return jsonStr;
	}

	public String addSaftycosttj(Saftycosttj s, String fileName, String rootPath) {
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
		String ympname = ym + s.getSubjectnum() + "\\";
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
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym + s.getSubjectnum());
		}

		// 存入数据库的内容
		// String accessory = sy+sm + "*" + p.getName() + "*"+ fileName;
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}
		String accessory = sy + sm + "*" + s.getSubjectnum() + "*" + fileName;

		// 打包
		// byte[] buffer = new byte[1024];
		// if (fileName.length() > 0) {
		// // String strZipName = ympname + sy + sm + sd + p.getName()+".zip";
		// String strZipName = ympname + sy + sm + sd + s.getSubjectnum() +
		// ".zip";
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
		// accessory = accessory + sy + sm + sd + s.getSubjectnum() + ".zip";
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
		// basicInfoDAO.insertProject(p);
		saftyCostDAO.insertSaftycosttj(s);
		return json;
	}

	public String editSaftycosttj(Saftycosttj s, String fileName, String rootPath) {
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
			String ympname = ym + s.getSubjectnum() + "\\";
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
			oldFoldName = sy + sm + "\\" + s.getSubjectnum();
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			// newAccessory = sy + sm + "*" + p.getName() +"*"+fileName;
			newAccessory = sy + sm + "*" + s.getSubjectnum() + "*" + fileName;
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
		/*
		 * byte[] buffer = new byte[1024]; if (fileName.length() > 0) { String
		 * strZipName = rootPath + oldFoldName +
		 * "\\" + sy + sm + sd + s.getSubjectnum() + ".zip"; try {
		 * ZipOutputStream out = new ZipOutputStream(new
		 * FileOutputStream(strZipName)); String[] allfile =
		 * fileName.split("\\*"); for (int i = 0; i < allfile.length; i++) {
		 * File tempfile = new File(rootPath + oldFoldName + "\\" + allfile[i]);
		 * if (tempfile.exists()) { FileInputStream fis = new
		 * FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while ((len =
		 * fis.read(buffer)) > 0) { out.write(buffer, 0, len); }
		 * out.closeEntry(); fis.close(); } } newAccessory = newAccessory + sy +
		 * sm + sd + s.getSubjectnum() + ".zip"; out.close(); } catch (Exception
		 * e) { e.printStackTrace(); } }
		 */

		newAccessory = newAccessory.substring(0, newAccessory.length() - 1);

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

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			s.setAccessory(fileName);
		} else {
			s.setAccessory(newAccessory);
		}

		saftyCostDAO.updateSaftycosttj(s);
		return json;
	}

	public List<Saftycosttj> gettjBysubjectnum(String subjectnum) {
		return saftyCostDAO.gettjBysubjectnum(subjectnum);
	}

	public String addSaftycosttj(Saftycosttj s) {
		String json = "{\"success\":\"true\"}";
		saftyCostDAO.insertSaftycosttj(s);
		return json;
	}

	public String updateSaftycosttj(Saftycosttj s) {
		String json = "{\"success\":\"true\"}";
		saftyCostDAO.updateSaftycosttj(s);
		return json;
	}

	public String deleteSaftycosttj(String ID) {
		Saftycosttj saftycosttj = new Saftycosttj();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			saftycosttj.setId(Integer.parseInt(temp[i]));
			saftyCostDAO.deleteSaftycosttj(saftycosttj);
		}
		return json;
	}

	// saftyaccounts表的服务*****************************************
	public String getSaftyaccountsList(String findstr, int start, int limit, String projectName) {
		List<Saftyaccounts> list = saftyCostDAO.getSaftyaccountsList(findstr, start, limit, projectName);
		int total = saftyCostDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Saftyaccounts s = list.get(i);
			jsonStr += "{\"ID\":\"" + s.getId() + "\",\"subjectnum\":\"" + s.getSubjectnum() + "\",\"costkind\":\""
					+ s.getCostkind() + "\",\"costetails\":\"" + s.getCostetails() + "\",\"applysector\":\""
					+ s.getApplysector() + "\",\"costuse\":\"" + s.getCostuse() + "\"";
			jsonStr += ",\"amount\":\"" + s.getAmount() + "\",\"manager\":\"" + s.getManager()
					+ "\",\"registerperson\":\"" + s.getRegisterperson() + "\",\"approtime\":\"" + s.getApprotime()
					+ "\",\"Accessory\":\"" + s.getAccessory() + "\",\"remarks\":\"" + s.getRemarks()
					+ "\",\"checksituation\":\"" + s.getChecksituation() + "\",\"ProjectName\":\"" + s.getProjectName()
					+ "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String getAllSaftyaccounts() {
		List<Saftyaccounts> list = saftyCostDAO.findAllSaftyaccounts();
		int total = saftyCostDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Saftyaccounts s = list.get(i);
			jsonStr += "{\"ID\":\"" + s.getId() + "\",\"subjectnum\":\"" + s.getSubjectnum() + "\",\"costkind\":\""
					+ s.getCostkind() + "\",\"costetails\":\"" + s.getCostetails() + "\",\"applysector\":\""
					+ s.getApplysector() + "\",\"costuse\":\"" + s.getCostuse() + "\"";
			jsonStr += ",\"amount\":\"" + s.getAmount() + "\",\"manager\":\"" + s.getManager()
					+ "\",\"registerperson\":\"" + s.getRegisterperson() + "\",\"approtime\":\"" + s.getApprotime()
					+ "\",\"Accessory\":\"" + s.getAccessory() + "\",\"remarks\":\"" + s.getRemarks()
					+ "\",\"checksituation\":\"" + s.getChecksituation() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addSaftyaccounts(Saftyaccounts s) {
		String json = "{\"success\":\"true\"}";
		saftyCostDAO.insertSaftyaccounts(s);
		return json;
	}

	public String addSaftyaccounts(Saftyaccounts s, String fileName, String rootPath) {
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
		String ympname = ym + s.getSubjectnum() + "\\";
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
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym + s.getSubjectnum());
		}

		// 存入数据库的内容
		// String accessory = sy+sm + "*" + p.getName() + "*"+ fileName;
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}
		String accessory = sy + sm + "*" + s.getSubjectnum() + "*" + fileName;

		// 打包
		// byte[] buffer = new byte[1024];
		// if (fileName.length() > 0) {
		// // String strZipName = ympname + sy + sm + sd + p.getName()+".zip";
		// String strZipName = ympname + sy + sm + sd + s.getSubjectnum() +
		// ".zip";
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
		// accessory = accessory + sy + sm + sd + s.getSubjectnum() + ".zip";
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
		// basicInfoDAO.insertProject(p);
		saftyCostDAO.insertSaftyaccounts(s);
		return json;
	}

	public String editSaftyaccounts(Saftyaccounts s, String fileName, String rootPath) {
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
			String ympname = ym + s.getSubjectnum() + "\\";
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
			oldFoldName = sy + sm + "\\" + s.getSubjectnum();
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			// newAccessory = sy + sm + "*" + p.getName() +"*"+fileName;
			newAccessory = sy + sm + "*" + s.getSubjectnum() + "*" + fileName;
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
		/*
		 * byte[] buffer = new byte[1024]; if (fileName.length() > 0) { String
		 * strZipName = rootPath + oldFoldName +
		 * "\\" + sy + sm + sd + s.getSubjectnum() + ".zip"; try {
		 * ZipOutputStream out = new ZipOutputStream(new
		 * FileOutputStream(strZipName)); String[] allfile =
		 * fileName.split("\\*"); for (int i = 0; i < allfile.length; i++) {
		 * File tempfile = new File(rootPath + oldFoldName + "\\" + allfile[i]);
		 * if (tempfile.exists()) { FileInputStream fis = new
		 * FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while ((len =
		 * fis.read(buffer)) > 0) { out.write(buffer, 0, len); }
		 * out.closeEntry(); fis.close(); } } newAccessory = newAccessory + sy +
		 * sm + sd + s.getSubjectnum() + ".zip"; out.close(); } catch (Exception
		 * e) { e.printStackTrace(); } }
		 */

		newAccessory = newAccessory.substring(0, newAccessory.length() - 1);

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

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			s.setAccessory(fileName);
		} else {
			s.setAccessory(newAccessory);
		}

		saftyCostDAO.updateSaftyaccounts(s);
		return json;
	}

	public String updateSaftyaccounts(Saftyaccounts s) {
		String json = "{\"success\":\"true\"}";
		saftyCostDAO.updateSaftyaccounts(s);
		return json;
	}

	public String deleteSaftyaccounts(String ID) {
		Saftyaccounts saftyaccounts = new Saftyaccounts();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			saftyaccounts.setId(Integer.parseInt(temp[i]));
			saftyCostDAO.deleteSaftyaccounts(saftyaccounts);
		}
		return json;
	}

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
			List<Saftyaccounts> list = saftyCostDAO.checkSaftyaccountsID((Integer.parseInt(ppID)));
			if (list.size() > 0) {
				Saftyaccounts p = list.get(0);
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

	// fenbaosaftyaccounts表的服务*******************************
	public String getFenbaosaftyaccountsList(String findstr, int start, int limit, String projectName) {
		List<Fenbaosaftyaccounts> list = saftyCostDAO.getFenbaosaftyaccountsList(findstr, start, limit, projectName);
		int total = saftyCostDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Fenbaosaftyaccounts s = list.get(i);
			jsonStr += "{\"ID\":\"" + s.getId() + "\",\"subcontractor\":\"" + s.getSubcontractor()
					+ "\",\"taizhang\":\"" + s.getTaizhang() + "\",\"checktime\":\"" + s.getChecktime()
					+ "\",\"ProjectName\":\"" + s.getProjectName() + "\",\"Accessory\":\"" + s.getAccessory() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addFenbaosaftyaccounts(Fenbaosaftyaccounts s, String fileName, String rootPath) {
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
		// basicInfoDAO.insertProject(p);
		saftyCostDAO.insertFenbaosaftyaccounts(s);
		return json;
	}

	public String editFenbaosaftyaccounts(Fenbaosaftyaccounts s, String fileName, String rootPath) {
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
		/*
		 * byte[] buffer = new byte[1024]; if (fileName.length() > 0) { String
		 * strZipName = rootPath + oldFoldName +
		 * "\\" + sy + sm + sd + s.getId() + ".zip"; try { ZipOutputStream out =
		 * new ZipOutputStream(new FileOutputStream(strZipName)); String[]
		 * allfile = fileName.split("\\*"); for (int i = 0; i < allfile.length;
		 * i++) { File tempfile = new File(rootPath + oldFoldName + "\\" +
		 * allfile[i]); if (tempfile.exists()) { FileInputStream fis = new
		 * FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while ((len =
		 * fis.read(buffer)) > 0) { out.write(buffer, 0, len); }
		 * out.closeEntry(); fis.close(); } } newAccessory = newAccessory + sy +
		 * sm + sd + s.getId() + ".zip"; out.close(); } catch (Exception e) {
		 * e.printStackTrace(); } }
		 */

		newAccessory = newAccessory.substring(0, newAccessory.length() - 1);

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

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			s.setAccessory(fileName);
		} else {
			s.setAccessory(newAccessory);
		}
		saftyCostDAO.updateFenbaosaftyaccounts(s);
		return json;
	}

	public String addFenbaosaftyaccounts(Fenbaosaftyaccounts s) {
		String json = "{\"success\":\"true\"}";
		saftyCostDAO.insertFenbaosaftyaccounts(s);
		return json;
	}

	public String updateFenbaosaftyaccounts(Fenbaosaftyaccounts s) {
		String json = "{\"success\":\"true\"}";
		saftyCostDAO.updateFenbaosaftyaccounts(s);
		return json;
	}

	public String deleteFenbaosaftyaccounts(String id) {
		Fenbaosaftyaccounts fenbaosaftyaccounts = new Fenbaosaftyaccounts();
		String json = "{\"success\":\"true\"}";
		String[] temp = id.split(",");
		for (int i = 0; i < temp.length; i++) {
			fenbaosaftyaccounts.setId(Integer.parseInt(temp[i]));
			saftyCostDAO.deleteFenbaosaftyaccounts(fenbaosaftyaccounts);
		}
		return json;
	}

	// fenbaosaftycostsum表的服务********************************
	public String getFenbaosaftycostsumList(String findstr, int start, int limit) {
		List<Fenbaosaftycostsum> list = saftyCostDAO.getFenbaosaftycostsumList(findstr, start, limit);
		int total = saftyCostDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Fenbaosaftycostsum s = list.get(i);
			jsonStr += "{\"ID\":\"" + s.getId() + "\",\"regtime\":\"" + s.getRegtime() + "\",\"costkind\":\""
					+ s.getCostkind() + "\",\"repoter\":\"" + s.getRepoter() + "\",\"cost\":\"" + s.getCost()
					+ "\",\"sumcost\":\"" + s.getSumcost() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String getAllFenbaosaftycostsum() {
		List<Fenbaosaftycostsum> list = saftyCostDAO.findAllFenbaosaftycostsum();
		int total = saftyCostDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Fenbaosaftycostsum s = list.get(i);
			jsonStr += "{\"ID\":\"" + s.getId() + "\",\"costkind\":\"" + s.getCostkind() + "\",\"repoter\":\""
					+ s.getRepoter() + "\",\"cost\":\"" + s.getCost() + "\",\"sumcost\":\"" + s.getSumcost() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addFenbaosaftycostsum(Fenbaosaftycostsum s) {
		String json = "{\"success\":\"true\"}";
		saftyCostDAO.insertFenbaosaftycostsum(s);
		return json;
	}

	public String updateFenbaosaftycostsum(Fenbaosaftycostsum s) {
		String json = "{\"success\":\"true\"}";
		saftyCostDAO.updateFenbaosaftycostsum(s);
		return json;
	}

	public String deleteFenbaosaftycostsum(String id) {
		Fenbaosaftycostsum fenbaosaftycostsum = new Fenbaosaftycostsum();
		String json = "{\"success\":\"true\"}";
		String[] temp = id.split(",");
		for (int i = 0; i < temp.length; i++) {
			fenbaosaftycostsum.setId(Integer.parseInt(temp[i]));
			saftyCostDAO.deleteFenbaosaftycostsum(fenbaosaftycostsum);
		}
		return json;
	}

	// Sanxiang表的服务********************************
	public String getSanxiangList(String findstr, int start, int limit) {
		List<Sanxiang> list = saftyCostDAO.getSanxiangList(findstr, start, limit);
		int total = saftyCostDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Sanxiang s = list.get(i);
			jsonStr += "{\"ID\":\"" + s.getId() + "\",\"project\":\"" + s.getProject() + "\",\"sumnum\":\""
					+ s.getSumnum() + "\",\"danwei\":\"" + s.getDanwei() + "\",\"Accessory\":\"" + s.getAccessory()
					+ "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addSanxiang(Sanxiang s, String fileName, String rootPath) {
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

		saftyCostDAO.insertSanxiang(s);
		return json;
	}

	public String editSanxiang(Sanxiang s, String fileName, String rootPath) {
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
		/*
		 * byte[] buffer = new byte[1024]; if (fileName.length() > 0) { String
		 * strZipName = rootPath + oldFoldName +
		 * "\\" + sy + sm + sd + s.getId() + ".zip"; try { ZipOutputStream out =
		 * new ZipOutputStream(new FileOutputStream(strZipName)); String[]
		 * allfile = fileName.split("\\*"); for (int i = 0; i < allfile.length;
		 * i++) { File tempfile = new File(rootPath + oldFoldName + "\\" +
		 * allfile[i]); if (tempfile.exists()) { FileInputStream fis = new
		 * FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while ((len =
		 * fis.read(buffer)) > 0) { out.write(buffer, 0, len); }
		 * out.closeEntry(); fis.close(); } } newAccessory = newAccessory + sy +
		 * sm + sd + s.getId() + ".zip"; out.close(); } catch (Exception e) {
		 * e.printStackTrace(); } }
		 */

		newAccessory = newAccessory.substring(0, newAccessory.length() - 1);

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

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			s.setAccessory(fileName);
		} else {
			s.setAccessory(newAccessory);
		}

		saftyCostDAO.updateSanxiang(s);

		// return editSaftycheck2(s, fileName2, rootPath);
		return json;
	}

	public String addSanxiang(Sanxiang s) {
		String json = "{\"success\":\"true\"}";
		saftyCostDAO.insertSanxiang(s);
		return json;
	}

	public String updateSanxiang(Sanxiang s) {
		String json = "{\"success\":\"true\"}";
		saftyCostDAO.updateSanxiang(s);
		return json;
	}

	public String deleteSanxiang(String id) {
		Sanxiang sanxiang = new Sanxiang();
		String json = "{\"success\":\"true\"}";
		String[] temp = id.split(",");
		for (int i = 0; i < temp.length; i++) {
			sanxiang.setId(Integer.parseInt(temp[i]));
			saftyCostDAO.deleteSanxiang(sanxiang);
		}
		return json;
	}

	// Saftyjiancha表服务**************
	public String getSaftyjianchaList(String findstr, int start, int limit, String projectName) {
		List<Saftyjiancha> list = saftyCostDAO.getSaftyjianchaList(findstr, start, limit, projectName);
		int total = saftyCostDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Saftyjiancha s = list.get(i);
			jsonStr += "{\"ID\":\"" + s.getId() + "\",\"jcperson\":\"" + s.getJcperson() + "\",\"jctime\":\""
					+ s.getJctime() + "\",\"jcresult\":\"" + s.getJcresult() + "\",\"ProjectName\":\""
					+ s.getProjectName() + "\",\"Accessory\":\"" + s.getAccessory() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addSaftyjiancha(Saftyjiancha s, String fileName, String rootPath) {
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
		// basicInfoDAO.insertProject(p);
		saftyCostDAO.insertSaftyjiancha(s);
		return json;
	}

	public String editSaftyjiancha(Saftyjiancha s, String fileName, String rootPath) {
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
		/*
		 * byte[] buffer = new byte[1024]; if (fileName.length() > 0) { String
		 * strZipName = rootPath + oldFoldName +
		 * "\\" + sy + sm + sd + s.getId() + ".zip"; try { ZipOutputStream out =
		 * new ZipOutputStream(new FileOutputStream(strZipName)); String[]
		 * allfile = fileName.split("\\*"); for (int i = 0; i < allfile.length;
		 * i++) { File tempfile = new File(rootPath + oldFoldName + "\\" +
		 * allfile[i]); if (tempfile.exists()) { FileInputStream fis = new
		 * FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while ((len =
		 * fis.read(buffer)) > 0) { out.write(buffer, 0, len); }
		 * out.closeEntry(); fis.close(); } } newAccessory = newAccessory + sy +
		 * sm + sd + s.getId() + ".zip"; out.close(); } catch (Exception e) {
		 * e.printStackTrace(); } }
		 */

		newAccessory = newAccessory.substring(0, newAccessory.length() - 1);

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

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			s.setAccessory(fileName);
		} else {
			s.setAccessory(newAccessory);
		}
		saftyCostDAO.updateSaftyjiancha(s);
		return json;
	}

	public String addSaftyjiancha(Saftyjiancha s) {
		String json = "{\"success\":\"true\"}";
		saftyCostDAO.insertSaftyjiancha(s);
		return json;
	}

	public String updateSaftyjiancha(Saftyjiancha s) {
		String json = "{\"success\":\"true\"}";
		saftyCostDAO.updateSaftyjiancha(s);
		return json;
	}

	public String deleteSaftyjiancha(String id) {
		Saftyjiancha saftyjiancha = new Saftyjiancha();
		String json = "{\"success\":\"true\"}";
		String[] temp = id.split(",");
		for (int i = 0; i < temp.length; i++) {
			saftyjiancha.setId(Integer.parseInt(temp[i]));
			saftyCostDAO.deleteSaftyjiancha(saftyjiancha);
		}
		return json;
	}

	// Fenbaoplan表服务**************
	public String getFenbaoplanList(String findstr, int start, int limit, String projectName) {
		List<Fenbaoplan> list = saftyCostDAO.getFenbaoplanList(findstr, start, limit, projectName);
		int total = saftyCostDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Fenbaoplan s = list.get(i);
			jsonStr += "{\"ID\":\"" + s.getId() + "\",\"fenbaoname\":\"" + s.getFenbaoname() + "\",\"planname\":\""
					+ s.getPlanname() + "\",\"time\":\"" + s.getTime() + "\",\"ProjectName\":\"" + s.getProjectName()
					+ "\",\"Accessory\":\"" + s.getAccessory() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addFenbaoplan(Fenbaoplan s, String fileName, String rootPath) {
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
		// basicInfoDAO.insertProject(p);
		saftyCostDAO.insertFenbaoplan(s);
		return json;
	}

	public String editFenbaoplan(Fenbaoplan s, String fileName, String rootPath) {
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
		/*
		 * byte[] buffer = new byte[1024]; if (fileName.length() > 0) { String
		 * strZipName = rootPath + oldFoldName +
		 * "\\" + sy + sm + sd + s.getId() + ".zip"; try { ZipOutputStream out =
		 * new ZipOutputStream(new FileOutputStream(strZipName)); String[]
		 * allfile = fileName.split("\\*"); for (int i = 0; i < allfile.length;
		 * i++) { File tempfile = new File(rootPath + oldFoldName + "\\" +
		 * allfile[i]); if (tempfile.exists()) { FileInputStream fis = new
		 * FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while ((len =
		 * fis.read(buffer)) > 0) { out.write(buffer, 0, len); }
		 * out.closeEntry(); fis.close(); } } newAccessory = newAccessory + sy +
		 * sm + sd + s.getId() + ".zip"; out.close(); } catch (Exception e) {
		 * e.printStackTrace(); } }
		 */

		newAccessory = newAccessory.substring(0, newAccessory.length() - 1);

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

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			s.setAccessory(fileName);
		} else {
			s.setAccessory(newAccessory);
		}
		saftyCostDAO.updateFenbaoplan(s);
		return json;
	}

	public String addFenbaoplan(Fenbaoplan s) {
		String json = "{\"success\":\"true\"}";
		saftyCostDAO.insertFenbaoplan(s);
		return json;
	}

	public String updateFenbaoplan(Fenbaoplan s) {
		String json = "{\"success\":\"true\"}";
		saftyCostDAO.updateFenbaoplan(s);
		return json;
	}

	public String deleteFenbaoplan(String id) {
		Fenbaoplan fenbaoplan = new Fenbaoplan();
		String json = "{\"success\":\"true\"}";
		String[] temp = id.split(",");
		for (int i = 0; i < temp.length; i++) {
			fenbaoplan.setId(Integer.parseInt(temp[i]));
			saftyCostDAO.deleteFenbaoplan(fenbaoplan);
		}
		return json;
	}

	// Fenbaosaftyjiancha表服务**************
	public String getFenbaosaftyjianchaList(String findstr, int start, int limit, String projectName) {
		List<Fenbaosaftyjiancha> list = saftyCostDAO.getFenbaosaftyjianchaList(findstr, start, limit, projectName);
		int total = saftyCostDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Fenbaosaftyjiancha s = list.get(i);
			jsonStr += "{\"ID\":\"" + s.getId() + "\",\"fenbaoname\":\"" + s.getFenbaoname() + "\",\"jcperson\":\""
					+ s.getJcperson() + "\",\"jctime\":\"" + s.getJctime() + "\",\"jcresult\":\"" + s.getJcresult()
					+ "\",\"ProjectName\":\"" + s.getProjectName() + "\",\"Accessory\":\"" + s.getAccessory() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addFenbaosaftyjiancha(Fenbaosaftyjiancha s, String fileName, String rootPath) {
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
		// basicInfoDAO.insertProject(p);
		saftyCostDAO.insertFenbaosaftyjiancha(s);
		return json;
	}

	public String editFenbaosaftyjiancha(Fenbaosaftyjiancha s, String fileName, String rootPath) {
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
		/*
		 * byte[] buffer = new byte[1024]; if (fileName.length() > 0) { String
		 * strZipName = rootPath + oldFoldName +
		 * "\\" + sy + sm + sd + s.getId() + ".zip"; try { ZipOutputStream out =
		 * new ZipOutputStream(new FileOutputStream(strZipName)); String[]
		 * allfile = fileName.split("\\*"); for (int i = 0; i < allfile.length;
		 * i++) { File tempfile = new File(rootPath + oldFoldName + "\\" +
		 * allfile[i]); if (tempfile.exists()) { FileInputStream fis = new
		 * FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while ((len =
		 * fis.read(buffer)) > 0) { out.write(buffer, 0, len); }
		 * out.closeEntry(); fis.close(); } } newAccessory = newAccessory + sy +
		 * sm + sd + s.getId() + ".zip"; out.close(); } catch (Exception e) {
		 * e.printStackTrace(); } }
		 */

		newAccessory = newAccessory.substring(0, newAccessory.length() - 1);

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

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			s.setAccessory(fileName);
		} else {
			s.setAccessory(newAccessory);
		}
		saftyCostDAO.updateFenbaosaftyjiancha(s);
		return json;
	}

	public String addFenbaosaftyjiancha(Fenbaosaftyjiancha s) {
		String json = "{\"success\":\"true\"}";
		saftyCostDAO.insertFenbaosaftyjiancha(s);
		return json;
	}

	public String updateFenbaosaftyjiancha(Fenbaosaftyjiancha s) {
		String json = "{\"success\":\"true\"}";
		saftyCostDAO.updateFenbaosaftyjiancha(s);
		return json;
	}

	public String deleteFenbaosaftyjiancha(String id) {
		Fenbaosaftyjiancha fenbaosaftyjiancha = new Fenbaosaftyjiancha();
		String json = "{\"success\":\"true\"}";
		String[] temp = id.split(",");
		for (int i = 0; i < temp.length; i++) {
			fenbaosaftyjiancha.setId(Integer.parseInt(temp[i]));
			saftyCostDAO.deleteFenbaosaftyjiancha(fenbaosaftyjiancha);
		}
		return json;
	}

	// Saftycosttj1表的服务********************************
	public String getSaftycosttj1List(String findstr, int start, int limit, String projectName) {
		// List<Saftycosttj1> list = saftyCostDAO.getSaftycosttj1List(findstr,
		// start, limit, projectName);

		// ******************************************
		// 一年一年的
		List<Saftycostplan> listplan = saftyCostDAO.getSaftycostplanList(findstr, start, limit, projectName);
		// 之前所有
		List<Saftyaccounts> listaccount = saftyCostDAO.getSaftyaccountsList(findstr, start, limit, projectName);

		int total = 0;
		for (int y = 0; y < listplan.size(); y++) {
			if (listplan.get(y).getYearorfull().equals("年度投入计划")) {
				total++;
			}
		}

		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < listplan.size(); i++) {
			if (listplan.get(i).getYearorfull().equals("年度投入计划")) {

				int sum1 = 0, sum2 = 0, sum3 = 0, sum4 = 0;
				int year = Integer.valueOf(listplan.get(i).getYear());
				// 设置sum1
				sum1 = Integer.valueOf(listplan.get(i).getCostplansum());
				// 设置sum2
				for (int j = 0; j < listaccount.size(); j++) {
					Date date = listaccount.get(j).getApprotime();
					Calendar c = Calendar.getInstance();
					c.setTime(date);
					int year2 =c.get(Calendar.YEAR);
					if (year2==year) {
						sum2 += Integer.valueOf(listaccount.get(j).getAmount());
					}
				}
				// 设置sum3
				for (int k = 0; k < listplan.size(); k++) {
					if (listplan.get(k).getYearorfull().equals("年度投入计划")) {
						int year3 = Integer.valueOf(listplan.get(k).getYear());
						if (year3<=year) {
							sum3 += Integer.valueOf(listplan.get(k).getCostplansum());
						}
					}
				}
				// 设置sum4
				for (int n = 0; n < listaccount.size(); n++) {
					Date date = listaccount.get(n).getApprotime();
					Calendar c = Calendar.getInstance();
					c.setTime(date);
					int year4 = c.get(Calendar.YEAR);
					if (year4 <= year) {
						sum4 += Integer.valueOf(listaccount.get(n).getAmount());
					}
				}

				jsonStr += "{\"ID\":\"" + i + "\",\"year\":\"" + year + "\",\"sum1\":\"" + sum1 + "\",\"sum2\":\""
						+ sum2 + "\",\"sum3\":\"" + sum3 + "\",\"sum4\":\"" + sum4 + "\",\"ProjectName\":\""
						+ projectName + "\"}";

			}
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
		// ******************************************
		// int total = saftyCostDAO.datacount;
		// String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		// for (int i = 0; i < list.size(); ++i) {
		// if (i > 0) {
		// jsonStr += ",";
		// }
		// Saftycosttj1 s = list.get(i);
		// jsonStr += "{\"ID\":\"" + s.getId() + "\",\"year\":\"" + s.getYear()
		// + "\",\"costkind0\":\""
		// + s.getCostkind0() + "\",\"costkind1\":\"" + s.getCostkind1() +
		// "\",\"costkind2\":\""
		// + s.getCostkind2() + "\",\"costkind3\":\"" + s.getCostkind3() +
		// "\",\"costkind4\":\""
		// + s.getCostkind4() + "\",\"costkind5\":\"" + s.getCostkind5() +
		// "\",\"costkind6\":\""
		// + s.getCostkind6() + "\",\"costkind7\":\"" + s.getCostkind7() +
		// "\",\"costkind8\":\""
		// + s.getCostkind8() + "\",\"costkind9\":\"" + s.getCostkind9() +
		// "\",\"costkind01\":\""
		// + s.getCostkind01() + "\",\"costkind11\":\"" + s.getCostkind11() +
		// "\",\"costkind21\":\""
		// + s.getCostkind21() + "\",\"costkind31\":\"" + s.getCostkind31() +
		// "\",\"costkind41\":\""
		// + s.getCostkind41() + "\",\"costkind51\":\"" + s.getCostkind51() +
		// "\",\"costkind61\":\""
		// + s.getCostkind61() + "\",\"costkind71\":\"" + s.getCostkind71() +
		// "\",\"costkind81\":\""
		// + s.getCostkind81() + "\",\"costkind91\":\"" + s.getCostkind91() +
		// "\",\"costkind02\":\""
		// + s.getCostkind02() + "\",\"costkind12\":\"" + s.getCostkind12() +
		// "\",\"costkind22\":\""
		// + s.getCostkind22() + "\",\"costkind32\":\"" + s.getCostkind32() +
		// "\",\"costkind42\":\""
		// + s.getCostkind42() + "\",\"costkind52\":\"" + s.getCostkind52() +
		// "\",\"costkind62\":\""
		// + s.getCostkind62() + "\",\"costkind72\":\"" + s.getCostkind72() +
		// "\",\"costkind82\":\""
		// + s.getCostkind82() + "\",\"costkind92\":\"" + s.getCostkind92() +
		// "\",\"costkind03\":\""
		// + s.getCostkind03() + "\",\"costkind13\":\"" + s.getCostkind13() +
		// "\",\"costkind23\":\""
		// + s.getCostkind23() + "\",\"costkind33\":\"" + s.getCostkind33() +
		// "\",\"costkind43\":\""
		// + s.getCostkind43() + "\",\"costkind53\":\"" + s.getCostkind53() +
		// "\",\"costkind63\":\""
		// + s.getCostkind63() + "\",\"costkind73\":\"" + s.getCostkind73() +
		// "\",\"costkind83\":\""
		// + s.getCostkind83() + "\",\"costkind93\":\"" + s.getCostkind93() +
		// "\",\"sum1\":\"" + s.getSum1()
		// + "\",\"sum2\":\"" + s.getSum2() + "\",\"sum3\":\"" + s.getSum3() +
		// "\",\"sum4\":\"" + s.getSum4()
		// + "\",\"ProjectName\":\"" + s.getProjectName() + "\"}";
		// }
		// jsonStr += "]}";
		// System.out.println(jsonStr);
		// return jsonStr;
	}

	public String addSaftycosttj1(Saftycosttj1 s) {
		String json = "{\"success\":\"true\"}";
		saftyCostDAO.insertSaftycosttj1(s);
		return json;
	}

	public String updateSaftycosttj1(Saftycosttj1 s) {
		String json = "{\"success\":\"true\"}";
		saftyCostDAO.updateSaftycosttj1(s);
		return json;
	}

	public String deleteSaftycosttj1(String id) {
		Saftycosttj1 saftycosttj1 = new Saftycosttj1();
		String json = "{\"success\":\"true\"}";
		String[] temp = id.split(",");
		for (int i = 0; i < temp.length; i++) {
			saftycosttj1.setId(Integer.parseInt(temp[i]));
			saftyCostDAO.deleteSaftycosttj1(saftycosttj1);
		}
		return json;
	}

	// Saftycosttj2表的服务********************************
	public String getSaftycosttj2List(String findstr, int start, int limit, String projectName) {
		List<Saftycosttj2> list = saftyCostDAO.getSaftycosttj2List(findstr, start, limit, projectName);
		int total = saftyCostDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Saftycosttj2 s = list.get(i);
			jsonStr += "{\"ID\":\"" + s.getId() + "\",\"year\":\"" + s.getYear() + "\",\"costkind0\":\""
					+ s.getCostkind0() + "\",\"costkind1\":\"" + s.getCostkind1() + "\",\"costkind2\":\""
					+ s.getCostkind2() + "\",\"costkind3\":\"" + s.getCostkind3() + "\",\"costkind4\":\""
					+ s.getCostkind4() + "\",\"costkind5\":\"" + s.getCostkind5() + "\",\"costkind6\":\""
					+ s.getCostkind6() + "\",\"costkind7\":\"" + s.getCostkind7() + "\",\"costkind8\":\""
					+ s.getCostkind8() + "\",\"costkind9\":\"" + s.getCostkind9() + "\",\"costkind01\":\""
					+ s.getCostkind01() + "\",\"costkind11\":\"" + s.getCostkind11() + "\",\"costkind21\":\""
					+ s.getCostkind21() + "\",\"costkind31\":\"" + s.getCostkind31() + "\",\"costkind41\":\""
					+ s.getCostkind41() + "\",\"costkind51\":\"" + s.getCostkind51() + "\",\"costkind61\":\""
					+ s.getCostkind61() + "\",\"costkind71\":\"" + s.getCostkind71() + "\",\"costkind81\":\""
					+ s.getCostkind81() + "\",\"costkind91\":\"" + s.getCostkind91() + "\",\"costkind02\":\""
					+ s.getCostkind02() + "\",\"costkind12\":\"" + s.getCostkind12() + "\",\"costkind22\":\""
					+ s.getCostkind22() + "\",\"costkind32\":\"" + s.getCostkind32() + "\",\"costkind42\":\""
					+ s.getCostkind42() + "\",\"costkind52\":\"" + s.getCostkind52() + "\",\"costkind62\":\""
					+ s.getCostkind62() + "\",\"costkind72\":\"" + s.getCostkind72() + "\",\"costkind82\":\""
					+ s.getCostkind82() + "\",\"costkind92\":\"" + s.getCostkind92() + "\",\"costkind03\":\""
					+ s.getCostkind03() + "\",\"costkind13\":\"" + s.getCostkind13() + "\",\"costkind23\":\""
					+ s.getCostkind23() + "\",\"costkind33\":\"" + s.getCostkind33() + "\",\"costkind43\":\""
					+ s.getCostkind43() + "\",\"costkind53\":\"" + s.getCostkind53() + "\",\"costkind63\":\""
					+ s.getCostkind63() + "\",\"costkind73\":\"" + s.getCostkind73() + "\",\"costkind83\":\""
					+ s.getCostkind83() + "\",\"costkind93\":\"" + s.getCostkind93() + "\",\"sum1\":\"" + s.getSum1()
					+ "\",\"sum2\":\"" + s.getSum2() + "\",\"sum3\":\"" + s.getSum3() + "\",\"sum4\":\"" + s.getSum4()
					+ "\",\"ProjectName\":\"" + s.getProjectName() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addSaftycosttj2(Saftycosttj2 s) {
		String json = "{\"success\":\"true\"}";
		saftyCostDAO.insertSaftycosttj2(s);
		return json;
	}

	public String updateSaftycosttj2(Saftycosttj2 s) {
		String json = "{\"success\":\"true\"}";
		saftyCostDAO.updateSaftycosttj2(s);
		return json;
	}

	public String deleteSaftycosttj2(String id) {
		Saftycosttj2 saftycosttj2 = new Saftycosttj2();
		String json = "{\"success\":\"true\"}";
		String[] temp = id.split(",");
		for (int i = 0; i < temp.length; i++) {
			saftycosttj2.setId(Integer.parseInt(temp[i]));
			saftyCostDAO.deleteSaftycosttj2(saftycosttj2);
		}
		return json;
	}

	// Saftycosttj3表的服务********************************
	public String getSaftycosttj3List(String findstr, int start, int limit, String projectName) {
		List<Saftycosttj3> list = saftyCostDAO.getSaftycosttj3List(findstr, start, limit, projectName);
		int total = saftyCostDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Saftycosttj3 s = list.get(i);
			jsonStr += "{\"ID\":\"" + s.getId() + "\",\"year\":\"" + s.getYear() + "\",\"costkind0\":\""
					+ s.getCostkind0() + "\",\"costkind1\":\"" + s.getCostkind1() + "\",\"costkind2\":\""
					+ s.getCostkind2() + "\",\"costkind3\":\"" + s.getCostkind3() + "\",\"costkind4\":\""
					+ s.getCostkind4() + "\",\"costkind5\":\"" + s.getCostkind5() + "\",\"costkind6\":\""
					+ s.getCostkind6() + "\",\"costkind7\":\"" + s.getCostkind7() + "\",\"costkind8\":\""
					+ s.getCostkind8() + "\",\"costkind9\":\"" + s.getCostkind9() + "\",\"costkind01\":\""
					+ s.getCostkind01() + "\",\"costkind11\":\"" + s.getCostkind11() + "\",\"costkind21\":\""
					+ s.getCostkind21() + "\",\"costkind31\":\"" + s.getCostkind31() + "\",\"costkind41\":\""
					+ s.getCostkind41() + "\",\"costkind51\":\"" + s.getCostkind51() + "\",\"costkind61\":\""
					+ s.getCostkind61() + "\",\"costkind71\":\"" + s.getCostkind71() + "\",\"costkind81\":\""
					+ s.getCostkind81() + "\",\"costkind91\":\"" + s.getCostkind91() + "\",\"costkind02\":\""
					+ s.getCostkind02() + "\",\"costkind12\":\"" + s.getCostkind12() + "\",\"costkind22\":\""
					+ s.getCostkind22() + "\",\"costkind32\":\"" + s.getCostkind32() + "\",\"costkind42\":\""
					+ s.getCostkind42() + "\",\"costkind52\":\"" + s.getCostkind52() + "\",\"costkind62\":\""
					+ s.getCostkind62() + "\",\"costkind72\":\"" + s.getCostkind72() + "\",\"costkind82\":\""
					+ s.getCostkind82() + "\",\"costkind92\":\"" + s.getCostkind92() + "\",\"costkind03\":\""
					+ s.getCostkind03() + "\",\"costkind13\":\"" + s.getCostkind13() + "\",\"costkind23\":\""
					+ s.getCostkind23() + "\",\"costkind33\":\"" + s.getCostkind33() + "\",\"costkind43\":\""
					+ s.getCostkind43() + "\",\"costkind53\":\"" + s.getCostkind53() + "\",\"costkind63\":\""
					+ s.getCostkind63() + "\",\"costkind73\":\"" + s.getCostkind73() + "\",\"costkind83\":\""
					+ s.getCostkind83() + "\",\"costkind93\":\"" + s.getCostkind93() + "\",\"sum1\":\"" + s.getSum1()
					+ "\",\"sum2\":\"" + s.getSum2() + "\",\"sum3\":\"" + s.getSum3() + "\",\"sum4\":\"" + s.getSum4()
					+ "\",\"ProjectName\":\"" + s.getProjectName() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addSaftycosttj3(Saftycosttj3 s) {
		String json = "{\"success\":\"true\"}";
		saftyCostDAO.insertSaftycosttj3(s);
		return json;
	}

	public String updateSaftycosttj3(Saftycosttj3 s) {
		String json = "{\"success\":\"true\"}";
		saftyCostDAO.updateSaftycosttj3(s);
		return json;
	}

	public String deleteSaftycosttj3(String id) {
		Saftycosttj3 saftycosttj3 = new Saftycosttj3();
		String json = "{\"success\":\"true\"}";
		String[] temp = id.split(",");
		for (int i = 0; i < temp.length; i++) {
			saftycosttj3.setId(Integer.parseInt(temp[i]));
			saftyCostDAO.deleteSaftycosttj3(saftycosttj3);
		}
		return json;
	}

	// 年度投入计划总计 年度实际使用总计 项目整体投入计划总计 项目整体实际使用总计
	// sum1 sum2 sum3 sum4
	// 来自“安全费用的计划”
	public String getSaftycosttjrenwu1List(String findstr, int start, int limit, String projectName) {

		// 用于统计两个 计划
		List<Saftycostplan> list = saftyCostDAO.getSaftycostplanList(findstr, start, limit, projectName);
		int total = saftyCostDAO.datacount;

		// 用于统计两个实际
		List<Saftyaccounts> listaccounts = saftyCostDAO.getSaftyaccountsList(findstr, start, limit, projectName);
		// int total = saftyCostDAO.datacount;

		int xiangmuzhengtiplan = 0;
		// 找到 “项目整体投入计划总计”对应的条
		for (int j = 0; j < list.size(); j++) {
			Saftycostplan saftycostplan = list.get(j);
			if (saftycostplan.getYearorfull().equals("项目整体投入计划")) {
				xiangmuzhengtiplan = Integer.parseInt(saftycostplan.getCostplansum2());
			}
		}

		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {

			Saftycostplan s = list.get(i);

			if (s.getYearorfull().equals("年度投入计划")) {
				if (i > 0) {
					jsonStr += ",";
				}
				int niandushiji = 0; // 年度实际使用总计
				int zhengtishiji = 0; // 项目整体实际使用总计

				for (int k = 0; k < listaccounts.size(); k++) {
					Saftyaccounts saftyaccounts = listaccounts.get(k);
					int yearaccount = Integer.parseInt(saftyaccounts.getApprotime().toString().substring(0, 4));
					if (yearaccount == Integer.parseInt(s.getYear())) {
						niandushiji += Integer.parseInt(saftyaccounts.getAmount());
					}
					if (yearaccount <= Integer.parseInt(s.getYear())) {
						zhengtishiji += Integer.parseInt(saftyaccounts.getAmount());
					}
				}
				jsonStr += "{\"sum1\":\"" + s.getCostplansum2() + "\",\"sum2\":\"" + niandushiji + "\",\"sum3\":\""
						+ xiangmuzhengtiplan + "\",\"sum4\":\"" + zhengtishiji + "\",\"year\":\"" + s.getYear()
						+ "\",\"ProjectName\":\"" + s.getProjectName() + "\"}";
			}
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}
	
	public int importExcel(String type, String rootPath, String fileName, String projectName) {
		String path = rootPath + "temp\\" + fileName;
		int total = 0;
		System.out.println("type : " + type);
		switch (type) {
		case "项目部安全费用使用台账":
			List<Saftyaccounts> items = ReadExcel.readSaftyaccounts(path);
			for (Saftyaccounts item : items) {
				item.setProjectName(projectName);
				saftyCostDAO.insertSaftyaccounts(item);
			}
			total = items.size();
			break;
		default:
			break;
		}
		return total;
	}

}
