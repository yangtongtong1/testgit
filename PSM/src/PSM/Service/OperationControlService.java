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

import PSM.DAO.OperationControlDAO;
import PSM.Tool.ExcelToPdf;
import PSM.Tool.WordToPdf;
import hibernate.SecureJobSlip;
import hibernate.Schemeimple;
import hibernate.TransportSafety;
import hibernate.FireSafety;
import hibernate.Projectperson;
import hibernate.Safeprojd;
import hibernate.Safepromonitor;
import hibernate.Safetyprotocal;
import hibernate.SecuritySymbol;
import hibernate.Securityplan;
import hibernate.Tezhongpeople;
import hibernate.Tezhongsbpeople;
import hibernate.SaveEnergy;
import hibernate.Constructdesign;
import hibernate.Constructionelec;
import hibernate.EnviromentPro;
import hibernate.Fbtestjudge;
import hibernate.Feemanagement;
import hibernate.PODgoods;
import hibernate.Proapproval;
import hibernate.Project;
import hibernate.ODHequipment;
import hibernate.Occuevaluation;
import hibernate.Occumonitor;
import hibernate.Otherjob;
import hibernate.Traintable;

public class OperationControlService {
	private OperationControlDAO operationControlDAO;

	public OperationControlDAO getOperationControlDAO() {
		return operationControlDAO;
	}

	public void setOperationControlDAO(OperationControlDAO operationControlDAO) {
		this.operationControlDAO = operationControlDAO;
	}

	public String getSecureJobSlipList(String findstr, int start, int limit, String projectName) {

		List<SecureJobSlip> list = operationControlDAO.getSecureJobSlipList(findstr, start, limit, projectName);
		int total = operationControlDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			SecureJobSlip p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"WorkPlace\":\"" + p.getWorkPlace() + "\",\"WorkContent\":\""
					+ p.getWorkContent() + "\",\"WorkTime\":\"" + p.getWorkTime() + "\",\"DangerSource\":\""
					+ p.getDangerSource() + "\"";
			jsonStr += ",\"Principle\":\"" + p.getPrinciple() + "\",\"WorkerNum\":\"" + p.getWorkerNum()
					+ "\",\"ProjectName\":\"" + p.getProjectName() + "\",\"Accessory\":\"" + p.getAccessory() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addSecureJobSlip(SecureJobSlip p, String fileName, String rootPath) {
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
		String ympname = ym + p.getPrinciple() + "\\";
		File directory1 = new File(ym); // 根目录是否存在
		if (!directory1.exists())
			directory1.mkdir();
		File directory2 = new File(ympname); // 根目录是否存在
		if (!directory2.exists())
			directory2.mkdir();

		// 拷贝temp中的文件到正确的文件夹
		String[] newFile = fileName.split("\\*");
		for (String copyFile : newFile) {
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym + p.getPrinciple());
		}

		// 存入数据库的内容
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}
		String accessory = sy + sm + "*" + p.getPrinciple() + "*" + fileName;

		// 打包
		// byte[] buffer = new byte[1024];
		// if(fileName.length()>0)
		// {
		// String strZipName = ympname + sy + sm + sd + p.getPrinciple()+".zip";
		// System.out.println("压缩包名称:"+strZipName);
		// try
		// {
		// ZipOutputStream out = new ZipOutputStream(new
		// FileOutputStream(strZipName));
		// String[] allfile = fileName.split("\\*");
		// System.out.println(Arrays.toString(allfile));
		// for(int i =0;i<allfile.length;i++)
		// {
		// File tempfile = new File(ympname+allfile[i]);
		// if(tempfile.exists())
		// {
		// FileInputStream fis = new FileInputStream(tempfile);
		// out.putNextEntry(new ZipEntry(tempfile.getName()));
		// int len;
		// while((len = fis.read(buffer))>0)
		// {
		// out.write(buffer,0,len);
		// }
		// out.closeEntry();
		// fis.close();
		// }
		// }
		// accessory = accessory+sy+sm+sd+p.getPrinciple()+".zip";
		// out.close();
		// }
		// catch (Exception e)
		// {
		// e.printStackTrace();
		// }
		// }

		// doc转pdf
		if (fileName.length() != 0) {
			String[] allFile = fileName.split("\\*");
			for (String temp : allFile) {
				if (temp.endsWith(".doc")) {
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 4) + ".pdf");
				} else if (temp.endsWith(".docx")) {
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 5) + ".pdf");
				}
			}
		}

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			p.setAccessory(fileName);
		} else {
			p.setAccessory(accessory);
		}
		operationControlDAO.insertSecureJobSlip(p); //////////////////////////////////////////////////////////////////
		return json;
	}

	public String editSecureJobSlip(SecureJobSlip p, String fileName, String rootPath) {
		int oldAccessoryNone = 0;
		if (p.getAccessory() == null || p.getAccessory().length() <= 0) {
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

		if ((p.getAccessory() == null || p.getAccessory().length() <= 0)
				&& (fileName != null && fileName.length() > 0)) {
			// 建立月份和项目名文件夹
			String ym = rootPath + sy + sm + "\\";
			String ympname = ym + p.getPrinciple() + "\\";
			File directory1 = new File(ym); // 根目录是否存在
			if (!directory1.exists())
				directory1.mkdir();
			File directory2 = new File(ympname); // 根目录是否存在
			if (!directory2.exists())
				directory2.mkdir();
		}

		String oldAccessory = p.getAccessory();
		String[] oldFile = oldAccessory.split("\\*");
		int oldFileLength = oldFile.length;
		String oldFoldName;
		if (oldAccessoryNone == 0) {
			oldFoldName = oldFile[0] + "\\" + oldFile[1];
		} else {
			oldFoldName = sy + sm + "\\" + p.getPrinciple();
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			newAccessory = sy + sm + "*" + p.getPrinciple() + "*" + fileName;
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
		 * "\\" + sy + sm + sd + p.getPrinciple() + ".zip"; try {
		 * ZipOutputStream out = new ZipOutputStream(new
		 * FileOutputStream(strZipName)); String[] allfile =
		 * fileName.split("\\*"); for (int i = 0; i < allfile.length; i++) {
		 * File tempfile = new File(rootPath + oldFoldName + "\\" + allfile[i]);
		 * if (tempfile.exists()) { FileInputStream fis = new
		 * FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while ((len =
		 * fis.read(buffer)) > 0) { out.write(buffer, 0, len); }
		 * out.closeEntry(); fis.close(); } } newAccessory = newAccessory + sy +
		 * sm + sd + p.getPrinciple() + ".zip"; out.close(); } catch (Exception
		 * e) { e.printStackTrace(); } }
		 */

		newAccessory = newAccessory.substring(0, newAccessory.length() - 1);

		if (fileName.length() != 0) {
			String[] allFile = fileName.split("\\*");
			for (String tempFile : allFile) {
				if (tempFile.endsWith(".doc")) {
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 4) + ".pdf");
				} else if (tempFile.endsWith(".docx")) {
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 5) + ".pdf");
				}
			}
		}

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			p.setAccessory(fileName);
		} else {
			p.setAccessory(newAccessory);
		}

		operationControlDAO.updateSecureJobSlip(p); //////////////////////////////////////////////////////////////////////
		return json;
	}

	public String deleteSecureJobSlip(String ID, String rootPath) {
		SecureJobSlip pro = new SecureJobSlip();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			List<SecureJobSlip> list = operationControlDAO.checkSecureJobSlipID((Integer.parseInt(temp[i])));
			// 删除附件
			if (list != null) {
				SecureJobSlip p = list.get(0);
				String fileName = p.getAccessory();
				System.out.println(fileName + "length" + fileName.length());
				if (fileName != null && fileName.length() != 0) {
					String[] name = fileName.split("\\*");
					String folder = name[0] + "//" + name[1];
					String path = rootPath + folder;
					System.out.println("--path:" + path);
					for (int j = 2; j < name.length; j++) {
						deleteFile(path + "//" + name[j]);
					}
					DeleteFolder(path);
				}
			}
			pro.setId(Integer.parseInt(temp[i]));
			operationControlDAO.deleteSecureJobSlip(pro);
		}
		return json;
	}

	// jianglf--------------------------------
	public String getFeemanagementList(String findstr, String type, int start, int limit) {

		List<Feemanagement> list = operationControlDAO.getFeemanagementList(findstr, type, start, limit);
		int total = operationControlDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Feemanagement p = list.get(i);

			// String acc = p.getAccessory();
			// String[] accArray = acc.split("\\*");

			// String t = p.getTime().toString().substring(0, 4);

			String t = p.getTime().toString().split("-")[0];

			// String accJson = accArray[2];
			// accJson = accJson.split("\\.")[0];

			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"Title\":\"" + p.getTitle() + "\"";
			jsonStr += ",\"Fee\":\"" + p.getFee() + "\",\"Usea\":\"" + p.getUsea() + "\"";
			jsonStr += ",\"Jperson\":\"" + p.getJperson() + "\",\"Dperson\":\"" + p.getDperson() + "\"";
			jsonStr += ",\"Time\":\"" + p.getTime() + "\",\"Content\":\"" + p.getContent() + "\"";
			jsonStr += ",\"Typei\":\"" + p.getTypei() + "\",\"Unit\":\"" + p.getUnit() + "\"";
			jsonStr += ",\"Accessory\":\"" + p.getAccessory() + "\",\"Year\":\"" + t + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addFeemanagement(Feemanagement p, String fileName, String rootPath) {
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
		String ympname = ym + p.getTitle() + "\\";
		File directory1 = new File(ym); // 根目录是否存在
		if (!directory1.exists())
			directory1.mkdir();
		File directory2 = new File(ympname); // 根目录是否存在
		if (!directory2.exists())
			directory2.mkdir();

		// 拷贝temp中的文件到正确的文件夹
		String[] newFile = fileName.split("\\*");
		for (String copyFile : newFile) {
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym + p.getTitle());
		}

		// 存入数据库的内容
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}
		String accessory = sy + sm + "*" + p.getTitle() + "*" + fileName;

		// 打包
		// byte[] buffer = new byte[1024];
		// if(fileName.length()>0)
		// {
		// String strZipName = ympname + sy + sm + sd + p.getPrinciple()+".zip";
		// System.out.println("压缩包名称:"+strZipName);
		// try
		// {
		// ZipOutputStream out = new ZipOutputStream(new
		// FileOutputStream(strZipName));
		// String[] allfile = fileName.split("\\*");
		// System.out.println(Arrays.toString(allfile));
		// for(int i =0;i<allfile.length;i++)
		// {
		// File tempfile = new File(ympname+allfile[i]);
		// if(tempfile.exists())
		// {
		// FileInputStream fis = new FileInputStream(tempfile);
		// out.putNextEntry(new ZipEntry(tempfile.getName()));
		// int len;
		// while((len = fis.read(buffer))>0)
		// {
		// out.write(buffer,0,len);
		// }
		// out.closeEntry();
		// fis.close();
		// }
		// }
		// accessory = accessory+sy+sm+sd+p.getPrinciple()+".zip";
		// out.close();
		// }
		// catch (Exception e)
		// {
		// e.printStackTrace();
		// }
		// }

		// doc转pdf
		if (fileName.length() != 0) {
			String[] allFile = fileName.split("\\*");
			for (String temp : allFile) {
				if (temp.endsWith(".doc")) {
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 4) + ".pdf");
				} else if (temp.endsWith(".docx")) {
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 5) + ".pdf");
				}
			}
		}

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			p.setAccessory(fileName);
		} else {
			p.setAccessory(accessory);
		}
		operationControlDAO.insertFeemanagement(p);
		return json;
	}

	public String editFeemanagement(Feemanagement p, String fileName, String rootPath) {
		int oldAccessoryNone = 0;
		if (p.getAccessory() == null || p.getAccessory().length() <= 0) {
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

		if ((p.getAccessory() == null || p.getAccessory().length() <= 0)
				&& (fileName != null && fileName.length() > 0)) {
			// 建立月份和项目名文件夹
			String ym = rootPath + sy + sm + "\\";
			String ympname = ym + p.getTitle() + "\\";
			File directory1 = new File(ym); // 根目录是否存在
			if (!directory1.exists())
				directory1.mkdir();
			File directory2 = new File(ympname); // 根目录是否存在
			if (!directory2.exists())
				directory2.mkdir();
		}

		String oldAccessory = p.getAccessory();
		String[] oldFile = oldAccessory.split("\\*");
		int oldFileLength = oldFile.length;
		String oldFoldName;
		if (oldAccessoryNone == 0) {
			oldFoldName = oldFile[0] + "\\" + oldFile[1];
		} else {
			oldFoldName = sy + sm + "\\" + p.getTitle();
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			newAccessory = sy + sm + "*" + p.getTitle() + "*" + fileName;
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
		 * "\\" + sy + sm + sd + p.getPrinciple() + ".zip"; try {
		 * ZipOutputStream out = new ZipOutputStream(new
		 * FileOutputStream(strZipName)); String[] allfile =
		 * fileName.split("\\*"); for (int i = 0; i < allfile.length; i++) {
		 * File tempfile = new File(rootPath + oldFoldName + "\\" + allfile[i]);
		 * if (tempfile.exists()) { FileInputStream fis = new
		 * FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while ((len =
		 * fis.read(buffer)) > 0) { out.write(buffer, 0, len); }
		 * out.closeEntry(); fis.close(); } } newAccessory = newAccessory + sy +
		 * sm + sd + p.getPrinciple() + ".zip"; out.close(); } catch (Exception
		 * e) { e.printStackTrace(); } }
		 */

		newAccessory = newAccessory.substring(0, newAccessory.length() - 1);

		if (fileName.length() != 0) {
			String[] allFile = fileName.split("\\*");
			for (String tempFile : allFile) {
				if (tempFile.endsWith(".doc")) {
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 4) + ".pdf");
				} else if (tempFile.endsWith(".docx")) {
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 5) + ".pdf");
				}
			}
		}

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			p.setAccessory(fileName);
		} else {
			p.setAccessory(newAccessory);
		}

		operationControlDAO.updateFeemanagement(p);
		return json;
	}

	public String deleteFeemanagement(String ID, String rootPath) {
		Feemanagement pro = new Feemanagement();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			List<Feemanagement> list = operationControlDAO.checkFeemanagementID((Integer.parseInt(temp[i])));
			// 删除附件
			if (list != null) {
				Feemanagement p = list.get(0);
				String fileName = p.getAccessory();
				System.out.println(fileName + "length" + fileName.length());
				if (fileName != null && fileName.length() != 0) {
					String[] name = fileName.split("\\*");
					String folder = name[0] + "//" + name[1];
					String path = rootPath + folder;
					System.out.println("--path:" + path);
					for (int j = 2; j < name.length; j++) {
						deleteFile(path + "//" + name[j]);
					}
					DeleteFolder(path);
				}
			}
			pro.setId(Integer.parseInt(temp[i]));
			operationControlDAO.deleteFeemanagement(pro);
		}
		return json;
	}
	// end------------------------------------

	public String getSchemeimpleList(String findstr, int start, int limit, String projectName) {

		List<Schemeimple> list = operationControlDAO.getSchemeimpleList(findstr, start, limit, projectName);
		int total = operationControlDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Schemeimple p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"No\":\"" + p.getNo() + "\",\"SchemeName\":\""
					+ p.getSchemeName() + "\",\"Supervise\":\"" + p.getSupervise() + "\",\"Acceptance\":\""
					+ p.getAcceptance() + "\",\"ProjectName\":\"" + p.getProjectName() + "\",\"Jd\":\"" + p.getJd() + "\"";
			jsonStr += ",\"Accessory\":\"" + p.getAccessory() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addSchemeimple(Schemeimple p, String fileName, String rootPath) {
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
		String ympname = ym + p.getSchemeName() + "\\";
		File directory1 = new File(ym); // 根目录是否存在
		if (!directory1.exists())
			directory1.mkdir();
		File directory2 = new File(ympname); // 根目录是否存在
		if (!directory2.exists())
			directory2.mkdir();

		// 拷贝temp中的文件到正确的文件夹
		String[] newFile = fileName.split("\\*");
		for (String copyFile : newFile) {
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym + p.getSchemeName());
		}

		// 存入数据库的内容
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}
		String accessory = sy + sm + "*" + p.getSchemeName() + "*" + fileName;

		// 打包
		// byte[] buffer = new byte[1024];
		// if (fileName.length() > 0) {
		// String strZipName = ympname + sy + sm + sd + p.getSchemeName() +
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
		// accessory = accessory + sy + sm + sd + p.getSchemeName() + ".zip";
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
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 4) + ".pdf");
				} else if (temp.endsWith(".docx")) {
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 5) + ".pdf");
				}
			}
		}

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			p.setAccessory(fileName);
		} else {
			p.setAccessory(accessory);
		}
		operationControlDAO.insertSchemeimple(p); //////////////////////////////////////////////////////////////////
		return json;
	}

	public String editSchemeimple(Schemeimple p, String fileName, String rootPath) {
		int oldAccessoryNone = 0;
		if (p.getAccessory() == null || p.getAccessory().length() <= 0) {
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

		if ((p.getAccessory() == null || p.getAccessory().length() <= 0)
				&& (fileName != null && fileName.length() > 0)) {
			// 建立月份和项目名文件夹
			String ym = rootPath + sy + sm + "\\";
			String ympname = ym + p.getSchemeName() + "\\";
			File directory1 = new File(ym); // 根目录是否存在
			if (!directory1.exists())
				directory1.mkdir();
			File directory2 = new File(ympname); // 根目录是否存在
			if (!directory2.exists())
				directory2.mkdir();
		}

		String oldAccessory = p.getAccessory();
		String[] oldFile = oldAccessory.split("\\*");
		int oldFileLength = oldFile.length;
		String oldFoldName;
		if (oldAccessoryNone == 0) {
			oldFoldName = oldFile[0] + "\\" + oldFile[1];
		} else {
			oldFoldName = sy + sm + "\\" + p.getSchemeName();
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			newAccessory = sy + sm + "*" + p.getSchemeName() + "*" + fileName;
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
		 * "\\" + sy + sm + sd + p.getSchemeName() + ".zip"; try {
		 * ZipOutputStream out = new ZipOutputStream(new
		 * FileOutputStream(strZipName)); String[] allfile =
		 * fileName.split("\\*"); for (int i = 0; i < allfile.length; i++) {
		 * File tempfile = new File(rootPath + oldFoldName + "\\" + allfile[i]);
		 * if (tempfile.exists()) { FileInputStream fis = new
		 * FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while ((len =
		 * fis.read(buffer)) > 0) { out.write(buffer, 0, len); }
		 * out.closeEntry(); fis.close(); } } newAccessory = newAccessory + sy +
		 * sm + sd + p.getSchemeName() + ".zip"; out.close(); } catch (Exception
		 * e) { e.printStackTrace(); } }
		 */

		newAccessory = newAccessory.substring(0, newAccessory.length() - 1);

		if (fileName.length() != 0) {
			String[] allFile = fileName.split("\\*");
			for (String tempFile : allFile) {
				if (tempFile.endsWith(".doc")) {
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 4) + ".pdf");
				} else if (tempFile.endsWith(".docx")) {
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 5) + ".pdf");
				}
			}
		}

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			p.setAccessory(fileName);
		} else {
			p.setAccessory(newAccessory);
		}

		operationControlDAO.updateSchemeimple(p); //////////////////////////////////////////////////////////////////////
		return json;
	}

	public String deleteSchemeimple(String ID, String rootPath) {
		Schemeimple pro = new Schemeimple();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			List<Schemeimple> list = operationControlDAO.checkSchemeimpleID((Integer.parseInt(temp[i])));
			// 删除附件
			if (list != null) {
				Schemeimple p = list.get(0);
				String fileName = p.getAccessory();
				
				System.out.println(fileName + "length" + fileName.length());
				if (fileName != null && fileName.length() != 0) {
					String[] name = fileName.split("\\*");
					String folder = name[0] + "//" + name[1];
					String path = rootPath + folder;
					System.out.println("--path:" + path);
					for (int j = 2; j < name.length; j++) {
						deleteFile(path + "//" + name[j]);
					}
					DeleteFolder(path);
				}
			}
			pro.setId(Integer.parseInt(temp[i]));
			operationControlDAO.deleteSchemeimple(pro);
		}
		return json;
	}

	public String getFireSafetyList(String findstr, int start, int limit, String projectName) {

		List<FireSafety> list = operationControlDAO.getFireSafetyList(findstr, start, limit, projectName);
		int total = operationControlDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			FireSafety p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"Name\":\"" + p.getName() + "\",\"Model\":\"" + p.getModel()
					+ "\",\"Num\":\"" + p.getNum() + "\",\"Department\":\"" + p.getDepartment() + "\",\"AcqDate\":\""
					+ p.getAcqDate() + "\",\"ChangeDate\":\"" + p.getChangeDate() + "\",\"Place\":\"" + p.getPlace()
					+ "\",\"ChargePerson\":\"" + p.getChargePerson() + "\",\"CheckPeriodically\":\""
					+ p.getCheckPeriodically() + "\",\"CheckResult\":\"" + p.getCheckResult() + "\",\"ProjectName\":\""
					+ p.getProjectName() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addFireSafety(FireSafety p) {
		String json = "{\"success\":\"true\"}";
		operationControlDAO.insertFireSafety(p);
		return json;
	}

	public String editFireSafety(FireSafety p) {

		String json = "{\"success\":\"true\"}";
		operationControlDAO.updateFireSafety(p);
		return json;
	}

	public String deleteFireSafety(String ID) {
		FireSafety pro = new FireSafety();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			pro.setId(Integer.parseInt(temp[i]));
			operationControlDAO.deleteFireSafety(pro);
		}
		return json;
	}

	public String getTransportSafetyList(String findstr, int start, int limit, String projectName) {

		List<TransportSafety> list = operationControlDAO.getTransportSafetyList(findstr, start, limit, projectName);
		int total = operationControlDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			TransportSafety p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"CarNum\":\"" + p.getCarNum() + "\",\"CarName\":\""
					+ p.getCarName() + "\",\"Department\":\"" + p.getDepartment() + "\",\"License\":\"" + p.getLicense()
					+ "\",\"Driver\":\"" + p.getDriver() + "\",\"DriverNum\":\"" + p.getDriverNum()
					+ "\",\"Maintenance\":\"" + p.getMaintenance() + "\",\"ProjectName\":\"" + p.getProjectName()
					+ "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addTransportSafety(TransportSafety p) {
		String json = "{\"success\":\"true\"}";
		operationControlDAO.insertTransportSafety(p);
		return json;
	}

	public String editTransportSafety(TransportSafety p) {

		String json = "{\"success\":\"true\"}";
		operationControlDAO.updateTransportSafety(p);
		return json;
	}

	public String deleteTransportSafety(String ID) {
		TransportSafety pro = new TransportSafety();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			pro.setId(Integer.parseInt(temp[i]));
			operationControlDAO.deleteTransportSafety(pro);
		}
		return json;
	}

	public String getPODgoodsList(String findstr, int start, int limit, String projectName) {

		List<PODgoods> list = operationControlDAO.getPODgoodsList(findstr, start, limit, projectName);
		int total = operationControlDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			PODgoods p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"Name\":\"" + p.getName() + "\",\"DeliveryTime\":\""
					+ p.getDeliveryTime() + "\",\"Num\":\"" + p.getNum() + "\",\"PersonName\":\"" + p.getPersonName()
					+ "\",\"Department\":\"" + p.getDepartment() + "\",\"TimeLimit\":\"" + p.getTimeLimit()
					+ "\",\"Autograph\":\"" + p.getAutograph() + "\",\"Comment\":\"" + p.getComment()
					+ "\",\"ProjectName\":\"" + p.getProjectName() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addPODgoods(PODgoods p) {
		String json = "{\"success\":\"true\"}";
		operationControlDAO.insertPODgoods(p);
		return json;
	}

	public String editPODgoods(PODgoods p) {

		String json = "{\"success\":\"true\"}";
		operationControlDAO.updatePODgoods(p);
		return json;
	}

	public String deletePODgoods(String ID) {
		PODgoods pro = new PODgoods();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			pro.setId(Integer.parseInt(temp[i]));
			operationControlDAO.deletePODgoods(pro);
		}
		return json;
	}

	public String getODHequipmentList(String findstr, int start, int limit, String projectName) {

		List<ODHequipment> list = operationControlDAO.getODHequipmentList(findstr, start, limit, projectName);
		int total = operationControlDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			ODHequipment p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"Name\":\"" + p.getName() + "\",\"Model\":\"" + p.getModel()
					+ "\",\"Department\":\"" + p.getDepartment() + "\",\"Num\":\"" + p.getNum() + "\",\"BuyTime\":\""
					+ p.getBuyTime() + "\",\"SerialNumber\":\"" + p.getSerialNumber() + "\",\"Place\":\"" + p.getPlace()
					+ "\",\"Responsibility\":\"" + p.getResponsibility() + "\",\"ProjectName\":\"" + p.getProjectName()
					+ "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addODHequipment(ODHequipment p) {
		String json = "{\"success\":\"true\"}";
		operationControlDAO.insertODHequipment(p);
		return json;
	}

	public String editODHequipment(ODHequipment p) {

		String json = "{\"success\":\"true\"}";
		operationControlDAO.updateODHequipment(p);
		return json;
	}

	public String deleteODHequipment(String ID) {
		ODHequipment pro = new ODHequipment();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			pro.setId(Integer.parseInt(temp[i]));
			operationControlDAO.deleteODHequipment(pro);
		}
		return json;
	}

	public String getEnviromentProList(String findstr, int start, int limit) {

		List<EnviromentPro> list = operationControlDAO.getEnviromentProList(findstr, start, limit);
		int total = operationControlDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			EnviromentPro p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"Name\":\"" + p.getName() + "\",\"Department\":\""
					+ p.getDepartment() + "\",\"Time\":\"" + p.getTime() + "\",\"Approval\":\"" + p.getApproval()
					+ "\"";
			jsonStr += ",\"Accessory\":\"" + p.getAccessory() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addEnviromentPro(EnviromentPro p, String fileName, String rootPath) {
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
		String ympname = ym + p.getName() + "\\";
		File directory1 = new File(ym); // 根目录是否存在
		if (!directory1.exists())
			directory1.mkdir();
		File directory2 = new File(ympname); // 根目录是否存在
		if (!directory2.exists())
			directory2.mkdir();

		// 拷贝temp中的文件到正确的文件夹
		String[] newFile = fileName.split("\\*");
		for (String copyFile : newFile) {
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym + p.getName());
		}

		// 存入数据库的内容
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}
		String accessory = sy + sm + "*" + p.getName() + "*" + fileName;

		// 打包
		// byte[] buffer = new byte[1024];
		// if (fileName.length() > 0) {
		// String strZipName = ympname + sy + sm + sd + p.getName() + ".zip";
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
		// accessory = accessory + sy + sm + sd + p.getName() + ".zip";
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
			p.setAccessory(fileName);
		} else {
			p.setAccessory(accessory);
		}
		operationControlDAO.insertEnviromentPro(p);
		return json;
	}

	public String editEnviromentPro(EnviromentPro p, String fileName, String rootPath) {
		int oldAccessoryNone = 0;
		if (p.getAccessory() == null || p.getAccessory().length() <= 0) {
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

		if ((p.getAccessory() == null || p.getAccessory().length() <= 0)
				&& (fileName != null && fileName.length() > 0)) {
			// 建立月份和项目名文件夹
			String ym = rootPath + sy + sm + "\\";
			String ympname = ym + p.getName() + "\\";
			File directory1 = new File(ym); // 根目录是否存在
			if (!directory1.exists())
				directory1.mkdir();
			File directory2 = new File(ympname); // 根目录是否存在
			if (!directory2.exists())
				directory2.mkdir();
		}

		String oldAccessory = p.getAccessory();
		String[] oldFile = oldAccessory.split("\\*");
		int oldFileLength = oldFile.length;
		String oldFoldName;
		if (oldAccessoryNone == 0) {
			oldFoldName = oldFile[0] + "\\" + oldFile[1];
		} else {
			oldFoldName = sy + sm + "\\" + p.getName();
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			newAccessory = sy + sm + "*" + p.getName() + "*" + fileName;
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
		 * "\\" + sy + sm + sd + p.getName() + ".zip"; try { ZipOutputStream out
		 * = new ZipOutputStream(new FileOutputStream(strZipName)); String[]
		 * allfile = fileName.split("\\*"); for (int i = 0; i < allfile.length;
		 * i++) { File tempfile = new File(rootPath + oldFoldName + "\\" +
		 * allfile[i]); if (tempfile.exists()) { FileInputStream fis = new
		 * FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while ((len =
		 * fis.read(buffer)) > 0) { out.write(buffer, 0, len); }
		 * out.closeEntry(); fis.close(); } } newAccessory = newAccessory + sy +
		 * sm + sd + p.getName() + ".zip"; out.close(); } catch (Exception e) {
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
			p.setAccessory(fileName);
		} else {
			p.setAccessory(newAccessory);
		}

		operationControlDAO.updateEnviromentPro(p);
		return json;
	}

	public String deleteEnviromentPro(String ID, String rootPath) {
		EnviromentPro pro = new EnviromentPro();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			List<EnviromentPro> list = operationControlDAO.checkEnviromentProID((Integer.parseInt(temp[i])));
			// 删除附件
			if (list != null) {
				EnviromentPro p = list.get(0);
				String fileName = p.getAccessory();
				System.out.println(fileName + "length" + fileName.length());
				if (fileName != null && fileName.length() != 0) {
					String[] name = fileName.split("\\*");
					String folder = name[0] + "//" + name[1];
					String path = rootPath + folder;
					System.out.println("--path:" + path);
					for (int j = 2; j < name.length; j++) {
						deleteFile(path + "//" + name[j]);
					}
					DeleteFolder(path);
				}
			}
			pro.setId(Integer.parseInt(temp[i]));
			operationControlDAO.deleteEnviromentPro(pro);
		}
		return json;
	}

	/*
	 * public String getEmployee() { List list =
	 * operationControlDAO.getEmployee(122); String temp = ""; for (int i = 0; i
	 * < list.size(); ++i) { temp+=list.get(i); }
	 * 
	 * List list = operationControlDAO.getEmployee(120);
	 * 
	 * System.out.println(
	 * "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	 * System.out.println(list);
	 * 
	 * return list; }
	 */

	public String getTezhongpeopleList(String findstr, int start, int limit, String projectName) {

		List<Tezhongpeople> list = operationControlDAO.getTezhongpeopleList(findstr, start, limit, projectName);

		int total = operationControlDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Tezhongpeople p = list.get(i);

			jsonStr += "{\"ID\":\"" + p.getId() + "\" ,\"Name\":\"" + p.getName() + "\",\"FbName\":\"" + p.getFbName()
					+ "\",\"Type\":\"" + p.getType() + "\",\"Sex\":\"" + p.getSex() + "\"";

			jsonStr += ",\"CardName\":\"" + p.getCardName() + "\",\"CardTime\":\"" + p.getCardTime()
					+ "\",\"UseTime\":\"" + p.getUseTime() + "\",\"CardNo\":\"" + p.getCardNo() + "\",\"CardPlace\":\""
					+ p.getCardPlace() + "\",\"Ps\":\"" + p.getPs() + "\",\"ProjectName\":\"" + p.getProjectName()
					+ "\"";
			jsonStr += ",\"Peixunnr\":\"";
			List<Traintable> emp = operationControlDAO.findTrainRecordByName(p.getName(), 120);
			if (emp.isEmpty()) {
				if (p.getPeixun() == null || !p.getPeixun().equals("否")) {
					p.setPeixun("否");
					operationControlDAO.updateTezhongpeople(p);
				}
				jsonStr += "无" + "\"";
			} else {
				String tmpJson = "";
				for (int j = 0; j < emp.size(); ++j) {
					String tmpNames = "," + emp.get(j).getEmployee() + ",";
					if (tmpNames.indexOf("," + p.getName() + ",") >= 0)

						tmpJson += p.getName() + "在" + emp.get(j).getTrainDate().toString().substring(0, 10) + "参加了“"
								+ emp.get(j).getContent() + "”培训<br>";
				}

				if ((tmpJson.length() > 0)) {
					if (p.getPeixun() == null || !p.getPeixun().equals("是")) {
						p.setPeixun("是");
						operationControlDAO.updateTezhongpeople(p);
					}
					jsonStr += tmpJson.substring(0, tmpJson.length() - 4) + "\"";
				} else {
					if (p.getPeixun() == null || !p.getPeixun().equals("否")) {
						p.setPeixun("否");
						operationControlDAO.updateTezhongpeople(p);
					}
					jsonStr += "无" + "\"";
				}
			}
			jsonStr += ",\"Peixun\":\"" + p.getPeixun() + "\"";
			jsonStr += ",\"Accessory\":\"" + p.getAccessory() + "\"}";
		}
		jsonStr += "]}";

		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addTezhongpeople(Tezhongpeople p, String fileName, String rootPath) {
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
		String ympname = ym + p.getName() + "\\";
		File directory1 = new File(ym); // 根目录是否存在
		if (!directory1.exists())
			directory1.mkdir();
		File directory2 = new File(ympname); // 根目录是否存在
		if (!directory2.exists())
			directory2.mkdir();

		// 拷贝temp中的文件到正确的文件夹
		String[] newFile = fileName.split("\\*");
		for (String copyFile : newFile) {
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym + p.getName());
		}

		// 存入数据库的内容
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}
		String accessory = sy + sm + "*" + p.getName() + "*" + fileName;

		// 打包
		// byte[] buffer = new byte[1024];
		// if (fileName.length() > 0) {
		// String strZipName = ympname + sy + sm + sd + p.getName() + ".zip";
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
		// accessory = accessory + sy + sm + sd + p.getName() + ".zip";
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
			p.setAccessory(fileName);
		} else {
			p.setAccessory(accessory);
		}
		operationControlDAO.insertTezhongpeople(p);
		return json;
	}

	public String editTezhongpeople(Tezhongpeople p, String fileName, String rootPath) {
		int oldAccessoryNone = 0;
		if (p.getAccessory() == null || p.getAccessory().length() <= 0) {
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

		if ((p.getAccessory() == null || p.getAccessory().length() <= 0)
				&& (fileName != null && fileName.length() > 0)) {
			// 建立月份和项目名文件夹
			String ym = rootPath + sy + sm + "\\";
			String ympname = ym + p.getName() + "\\";
			File directory1 = new File(ym); // 根目录是否存在
			if (!directory1.exists())
				directory1.mkdir();
			File directory2 = new File(ympname); // 根目录是否存在
			if (!directory2.exists())
				directory2.mkdir();
		}

		String oldAccessory = p.getAccessory();
		String[] oldFile = oldAccessory.split("\\*");
		int oldFileLength = oldFile.length;
		String oldFoldName;
		if (oldAccessoryNone == 0) {
			oldFoldName = oldFile[0] + "\\" + oldFile[1];
		} else {
			oldFoldName = sy + sm + "\\" + p.getName();
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			newAccessory = sy + sm + "*" + p.getName() + "*" + fileName;
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
		 * "\\" + sy + sm + sd + p.getName() + ".zip"; try { ZipOutputStream out
		 * = new ZipOutputStream(new FileOutputStream(strZipName)); String[]
		 * allfile = fileName.split("\\*"); for (int i = 0; i < allfile.length;
		 * i++) { File tempfile = new File(rootPath + oldFoldName + "\\" +
		 * allfile[i]); if (tempfile.exists()) { FileInputStream fis = new
		 * FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while ((len =
		 * fis.read(buffer)) > 0) { out.write(buffer, 0, len); }
		 * out.closeEntry(); fis.close(); } } newAccessory = newAccessory + sy +
		 * sm + sd + p.getName() + ".zip"; out.close(); } catch (Exception e) {
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
			p.setAccessory(fileName);
		} else {
			p.setAccessory(newAccessory);
		}

		operationControlDAO.updateTezhongpeople(p);
		return json;
	}

	public String deleteTezhongpeople(String ID, String rootPath) {
		Tezhongpeople pro = new Tezhongpeople();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			List<Tezhongpeople> list = operationControlDAO.checkTezhongpeopleID((Integer.parseInt(temp[i])));
			// 删除附件
			if (list != null) {
				Tezhongpeople p = list.get(0);
				String fileName = p.getAccessory();
				System.out.println(fileName + "length" + fileName.length());
				if (fileName != null && fileName.length() != 0) {
					String[] name = fileName.split("\\*");
					String folder = name[0] + "//" + name[1];
					String path = rootPath + folder;
					System.out.println("--path:" + path);
					for (int j = 2; j < name.length; j++) {
						deleteFile(path + "//" + name[j]);
					}
					DeleteFolder(path);
				}
			}
			pro.setId(Integer.parseInt(temp[i]));
			operationControlDAO.deleteTezhongpeople(pro);
		}
		return json;
	}

	public String getSecuritySymbolList(String findstr, int start, int limit) {

		List<SecuritySymbol> list = operationControlDAO.getSecuritySymbolList(findstr, start, limit);
		int total = operationControlDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			SecuritySymbol p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"Name\":\"" + p.getName() + "\",\"Num\":\"" + p.getNum()
					+ "\",\"Department\":\"" + p.getDepartment() + "\",\"InstallTime\":\"" + p.getInstallTime()
					+ "\",\"InstallPlace\":\"" + p.getInstallPlace() + "\",\"Responsibility\":\""
					+ p.getResponsibility() + "\",\"Accessory\":\"" + p.getAccessory() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addSecuritySymbol(SecuritySymbol p, String fileName, String rootPath) {
		String json = "{\"success\":\"true\"}";
      	String accessory = addAccessory(rootPath, fileName, p.getDepartment());
		if(fileName == null || fileName.length() <= 0) {
			p.setAccessory(fileName);
		} else {
			p.setAccessory(accessory);
		}
		operationControlDAO.insertSecuritySymbol(p);
		return json;
	}

	public String editSecuritySymbol(SecuritySymbol p, String fileName, String rootPath) {
		String json = "{\"success\":\"true\"}";
		String accessory = addAccessory(rootPath, fileName, p.getDepartment());
		if(fileName == null || fileName.length() <= 0) {
			p.setAccessory(fileName);
		} else {
			p.setAccessory(accessory);
		}
		operationControlDAO.updateSecuritySymbol(p);
		return json;
	}

	public String deleteSecuritySymbol(String ID) {
		SecuritySymbol pro = new SecuritySymbol();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			pro.setId(Integer.parseInt(temp[i]));
			operationControlDAO.deleteSecuritySymbol(pro);
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

	public String deleteAllFile(String ppID, String fileName, String rootPath)////////////////////// ??????
	{
		String[] newFile = fileName.split("\\*");
		String json = "{\"success\":\"true\"}";

		if (ppID == null || ppID.length() <= 0)// 没有ID 说明是添加 直接去temp全部删除
		{
			for (String existFileInList : newFile) {
				deleteFile(rootPath + "temp\\" + existFileInList);
			}
		} else {
			String[] strKey = null;
			List<SecureJobSlip> list = operationControlDAO.checkSecureJobSlipID((Integer.parseInt(ppID)));
			if (list.size() > 0) {
				SecureJobSlip p = list.get(0);
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

	public String getSaveEnergyList(String findstr, int start, int limit) {

		List<SaveEnergy> list = operationControlDAO.getSaveEnergyList(findstr, start, limit);
		int total = operationControlDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			SaveEnergy p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"Name\":\"" + p.getName() + "\",\"Department\":\""
					+ p.getDepartment() + "\",\"Time\":\"" + p.getTime() + "\",\"Approval\":\"" + p.getApproval()
					+ "\"";
			jsonStr += ",\"Accessory\":\"" + p.getAccessory() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addSaveEnergy(SaveEnergy p, String fileName, String rootPath) {
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
		String ympname = ym + p.getName() + "\\";
		File directory1 = new File(ym); // 根目录是否存在
		if (!directory1.exists())
			directory1.mkdir();
		File directory2 = new File(ympname); // 根目录是否存在
		if (!directory2.exists())
			directory2.mkdir();

		// 拷贝temp中的文件到正确的文件夹
		String[] newFile = fileName.split("\\*");
		for (String copyFile : newFile) {
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym + p.getName());
		}

		// 存入数据库的内容
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}
		String accessory = sy + sm + "*" + p.getName() + "*" + fileName;

		// 打包
		// byte[] buffer = new byte[1024];
		// if (fileName.length() > 0) {
		// String strZipName = ympname + sy + sm + sd + p.getName() + ".zip";
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
		// accessory = accessory + sy + sm + sd + p.getName() + ".zip";
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
			p.setAccessory(fileName);
		} else {
			p.setAccessory(accessory);
		}
		operationControlDAO.insertSaveEnergy(p);
		return json;
	}

	public String editSaveEnergy(SaveEnergy p, String fileName, String rootPath) {
		int oldAccessoryNone = 0;
		if (p.getAccessory() == null || p.getAccessory().length() <= 0) {
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

		if ((p.getAccessory() == null || p.getAccessory().length() <= 0)
				&& (fileName != null && fileName.length() > 0)) {
			// 建立月份和项目名文件夹
			String ym = rootPath + sy + sm + "\\";
			String ympname = ym + p.getName() + "\\";
			File directory1 = new File(ym); // 根目录是否存在
			if (!directory1.exists())
				directory1.mkdir();
			File directory2 = new File(ympname); // 根目录是否存在
			if (!directory2.exists())
				directory2.mkdir();
		}

		String oldAccessory = p.getAccessory();
		String[] oldFile = oldAccessory.split("\\*");
		int oldFileLength = oldFile.length;
		String oldFoldName;
		if (oldAccessoryNone == 0) {
			oldFoldName = oldFile[0] + "\\" + oldFile[1];
		} else {
			oldFoldName = sy + sm + "\\" + p.getName();
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			newAccessory = sy + sm + "*" + p.getName() + "*" + fileName;
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
		 * "\\" + sy + sm + sd + p.getName() + ".zip"; try { ZipOutputStream out
		 * = new ZipOutputStream(new FileOutputStream(strZipName)); String[]
		 * allfile = fileName.split("\\*"); for (int i = 0; i < allfile.length;
		 * i++) { File tempfile = new File(rootPath + oldFoldName + "\\" +
		 * allfile[i]); if (tempfile.exists()) { FileInputStream fis = new
		 * FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while ((len =
		 * fis.read(buffer)) > 0) { out.write(buffer, 0, len); }
		 * out.closeEntry(); fis.close(); } } newAccessory = newAccessory + sy +
		 * sm + sd + p.getName() + ".zip"; out.close(); } catch (Exception e) {
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
			p.setAccessory(fileName);
		} else {
			p.setAccessory(newAccessory);
		}

		operationControlDAO.updateSaveEnergy(p);
		return json;
	}

	public String deleteSaveEnergy(String ID, String rootPath) {
		SaveEnergy pro = new SaveEnergy();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			List<SaveEnergy> list = operationControlDAO.checkSaveEnergyID((Integer.parseInt(temp[i])));
			// 删除附件
			if (list != null) {
				SaveEnergy p = list.get(0);
				String fileName = p.getAccessory();
				System.out.println(fileName + "length" + fileName.length());
				if (fileName != null && fileName.length() != 0) {
					String[] name = fileName.split("\\*");
					String folder = name[0] + "//" + name[1];
					String path = rootPath + folder;
					System.out.println("--path:" + path);
					for (int j = 2; j < name.length; j++) {
						deleteFile(path + "//" + name[j]);
					}
					DeleteFolder(path);
				}
			}
			pro.setId(Integer.parseInt(temp[i]));
			operationControlDAO.deleteSaveEnergy(pro);
		}
		return json;
	}

	public String getConstructdesignList(String findstr, int start, int limit, String projectName) {

		List<Constructdesign> list = operationControlDAO.getConstructdesignList(findstr, start, limit, projectName);
		int total = operationControlDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Constructdesign p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"Type\":\"" + p.getType()
					+ "\",\"Unit\":\"" + p.getUnit() + "\",\"ProjectName\":\"" + p.getProjectName() + "\"";
			jsonStr += ",\"Accessory\":\"" + p.getAccessory() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addConstructdesign(Constructdesign p, String fileName, String rootPath) {
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
		String ympname = ym + p.getUnit() + "\\";
		File directory1 = new File(ym); // 根目录是否存在
		if (!directory1.exists())
			directory1.mkdir();
		File directory2 = new File(ympname); // 根目录是否存在
		if (!directory2.exists())
			directory2.mkdir();

		// 拷贝temp中的文件到正确的文件夹
		String[] newFile = fileName.split("\\*");
		for (String copyFile : newFile) {
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym + p.getUnit());
		}

		// 存入数据库的内容
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}
		String accessory = sy + sm + "*" + p.getUnit() + "*" + fileName;

		// 打包
		// byte[] buffer = new byte[1024];
		// if (fileName.length() > 0) {
		// String strZipName = ympname + sy + sm + sd + p.getUnit() + ".zip";
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
		// accessory = accessory + sy + sm + sd + p.getUnit() + ".zip";
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
			p.setAccessory(fileName);
		} else {
			p.setAccessory(accessory);
		}
		operationControlDAO.insertConstructdesign(p);
		return json;
	}

	public String editConstructdesign(Constructdesign p, String fileName, String rootPath) {
		int oldAccessoryNone = 0;
		if (p.getAccessory() == null || p.getAccessory().length() <= 0) {
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

		if ((p.getAccessory() == null || p.getAccessory().length() <= 0)
				&& (fileName != null && fileName.length() > 0)) {
			// 建立月份和项目名文件夹
			String ym = rootPath + sy + sm + "\\";
			String ympname = ym + p.getUnit() + "\\";
			File directory1 = new File(ym); // 根目录是否存在
			if (!directory1.exists())
				directory1.mkdir();
			File directory2 = new File(ympname); // 根目录是否存在
			if (!directory2.exists())
				directory2.mkdir();
		}

		String oldAccessory = p.getAccessory();
		String[] oldFile = oldAccessory.split("\\*");
		int oldFileLength = oldFile.length;
		String oldFoldName;
		if (oldAccessoryNone == 0) {
			oldFoldName = oldFile[0] + "\\" + oldFile[1];
		} else {
			oldFoldName = sy + sm + "\\" + p.getUnit();
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			newAccessory = sy + sm + "*" + p.getUnit() + "*" + fileName;
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
		 * "\\" + sy + sm + sd + p.getUnit() + ".zip"; try { ZipOutputStream out
		 * = new ZipOutputStream(new FileOutputStream(strZipName)); String[]
		 * allfile = fileName.split("\\*"); for (int i = 0; i < allfile.length;
		 * i++) { File tempfile = new File(rootPath + oldFoldName + "\\" +
		 * allfile[i]); if (tempfile.exists()) { FileInputStream fis = new
		 * FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while ((len =
		 * fis.read(buffer)) > 0) { out.write(buffer, 0, len); }
		 * out.closeEntry(); fis.close(); } } newAccessory = newAccessory + sy +
		 * sm + sd + p.getUnit() + ".zip"; out.close(); } catch (Exception e) {
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
			p.setAccessory(fileName);
		} else {
			p.setAccessory(newAccessory);
		}

		operationControlDAO.updateConstructdesign(p);
		return json;
	}

	public String deleteConstructdesign(String ID, String rootPath) {
		Constructdesign pro = new Constructdesign();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			List<Constructdesign> list = operationControlDAO.checkConstructdesignID((Integer.parseInt(temp[i])));
			// 删除附件
			if (list != null) {
				Constructdesign p = list.get(0);
				String fileName = p.getAccessory();
				System.out.println(fileName + "length" + fileName.length());
				if (fileName != null && fileName.length() != 0) {
					String[] name = fileName.split("\\*");
					String folder = name[0] + "//" + name[1];
					String path = rootPath + folder;
					System.out.println("--path:" + path);
					for (int j = 2; j < name.length; j++) {
						deleteFile(path + "//" + name[j]);
					}
					DeleteFolder(path);
				}
			}
			pro.setId(Integer.parseInt(temp[i]));
			operationControlDAO.deleteConstructdesign(pro);
		}
		return json;
	}

	public String getProapprovalList(String findstr, int start, int limit, String projectName) {

		List<Proapproval> list = operationControlDAO.getProapprovalList(findstr, start, limit, projectName);
		int total = operationControlDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Proapproval p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"Name\":\"" + p.getName()
					+ "\",\"Type\":\"" + p.getType() + "\",\"Unit\":\"" + p.getUnit() + "\",\"Time\":\"" + p.getTime()
					+ "\",\"Approval\":\"" + p.getApproval() + "\",\"ProjectName\":\"" + p.getProjectName() + "\"";
			jsonStr += ",\"Accessory\":\"" + p.getAccessory() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addProapproval(Proapproval p, String fileName, String rootPath) {
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
		String ympname = ym + p.getName() + "\\";
		File directory1 = new File(ym); // 根目录是否存在
		if (!directory1.exists())
			directory1.mkdir();
		File directory2 = new File(ympname); // 根目录是否存在
		if (!directory2.exists())
			directory2.mkdir();

		// 拷贝temp中的文件到正确的文件夹
		String[] newFile = fileName.split("\\*");
		for (String copyFile : newFile) {
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym + p.getName());
		}

		// 存入数据库的内容
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}
		String accessory = sy + sm + "*" + p.getName() + "*" + fileName;

		// 打包
		// byte[] buffer = new byte[1024];
		// if (fileName.length() > 0) {
		// String strZipName = ympname + sy + sm + sd + p.getName() + ".zip";
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
		// accessory = accessory + sy + sm + sd + p.getName() + ".zip";
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
			p.setAccessory(fileName);
		} else {
			p.setAccessory(accessory);
		}
		
		
		operationControlDAO.insertProapproval(p);
		return json;
	}

	public String editProapproval(Proapproval p, String fileName, String rootPath, String pName) {
		int oldAccessoryNone = 0;
		if (p.getAccessory() == null || p.getAccessory().length() <= 0) {
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

		if ((p.getAccessory() == null || p.getAccessory().length() <= 0)
				&& (fileName != null && fileName.length() > 0)) {
			// 建立月份和项目名文件夹
			String ym = rootPath + sy + sm + "\\";
			String ympname = ym + p.getName() + "\\";
			File directory1 = new File(ym); // 根目录是否存在
			if (!directory1.exists())
				directory1.mkdir();
			File directory2 = new File(ympname); // 根目录是否存在
			if (!directory2.exists())
				directory2.mkdir();
		}

		String oldAccessory = p.getAccessory();
		String[] oldFile = oldAccessory.split("\\*");
		int oldFileLength = oldFile.length;
		String oldFoldName;
		if (oldAccessoryNone == 0) {
			oldFoldName = oldFile[0] + "\\" + oldFile[1];
		} else {
			oldFoldName = sy + sm + "\\" + p.getName();
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			newAccessory = sy + sm + "*" + p.getName() + "*" + fileName;
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
		 * "\\" + sy + sm + sd + p.getUnit() + ".zip"; try { ZipOutputStream out
		 * = new ZipOutputStream(new FileOutputStream(strZipName)); String[]
		 * allfile = fileName.split("\\*"); for (int i = 0; i < allfile.length;
		 * i++) { File tempfile = new File(rootPath + oldFoldName + "\\" +
		 * allfile[i]); if (tempfile.exists()) { FileInputStream fis = new
		 * FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while ((len =
		 * fis.read(buffer)) > 0) { out.write(buffer, 0, len); }
		 * out.closeEntry(); fis.close(); } } newAccessory = newAccessory + sy +
		 * sm + sd + p.getUnit() + ".zip"; out.close(); } catch (Exception e) {
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
			p.setAccessory(fileName);
		} else {
			p.setAccessory(newAccessory);
		}

		String Name = p.getName();
		operationControlDAO.updateName(pName,Name);
		operationControlDAO.updateProapproval(p);
		return json;
	}

	public String deleteProapproval(String ID, String rootPath) {
		Proapproval pro = new Proapproval();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			List<Proapproval> list = operationControlDAO.checkProapprovalID((Integer.parseInt(temp[i])));
			// 删除附件
			if (list != null) {
				Proapproval p = list.get(0);
				String fileName = p.getAccessory();
				System.out.println(fileName + "length" + fileName.length());
				if (fileName != null && fileName.length() != 0) {
					String[] name = fileName.split("\\*");
					String folder = name[0] + "//" + name[1];
					String path = rootPath + folder;
					System.out.println("--path:" + path);
					for (int j = 2; j < name.length; j++) {
						deleteFile(path + "//" + name[j]);
					}
					DeleteFolder(path);
				}
			}
			pro.setId(Integer.parseInt(temp[i]));
			operationControlDAO.deleteProapproval(pro);
		}
		return json;
	}

	public String getOccuevaluationList(String findstr, int start, int limit, String projectName) {

		List<Occuevaluation> list = operationControlDAO.getOccuevaluationList(findstr, start, limit, projectName);
		int total = operationControlDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Occuevaluation p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"No\":\"" + p.getNo() + "\",\"Date\":\"" + p.getDate()
					+ "\",\"ProjectName\":\"" + p.getProjectName() + "\"";
			jsonStr += ",\"Accessory\":\"" + p.getAccessory() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addOccuevaluation(Occuevaluation p, String fileName, String rootPath) {
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
		String ympname = ym + p.getDate() + "\\";
		File directory1 = new File(ym); // 根目录是否存在
		if (!directory1.exists())
			directory1.mkdir();
		File directory2 = new File(ympname); // 根目录是否存在
		if (!directory2.exists())
			directory2.mkdir();

		// 拷贝temp中的文件到正确的文件夹
		String[] newFile = fileName.split("\\*");
		for (String copyFile : newFile) {
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym + p.getDate());
		}

		// 存入数据库的内容
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}
		String accessory = sy + sm + "*" + p.getDate() + "*" + fileName;

		// 打包
		// byte[] buffer = new byte[1024];
		// if (fileName.length() > 0) {
		// String strZipName = ympname + sy + sm + sd + p.getDate() + ".zip";
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
		// accessory = accessory + sy + sm + sd + p.getDate() + ".zip";
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
			p.setAccessory(fileName);
		} else {
			p.setAccessory(accessory);
		}
		operationControlDAO.insertOccuevaluation(p);
		return json;
	}

	public String editOccuevaluation(Occuevaluation p, String fileName, String rootPath) {
		int oldAccessoryNone = 0;
		if (p.getAccessory() == null || p.getAccessory().length() <= 0) {
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

		if ((p.getAccessory() == null || p.getAccessory().length() <= 0)
				&& (fileName != null && fileName.length() > 0)) {
			// 建立月份和项目名文件夹
			String ym = rootPath + sy + sm + "\\";
			String ympname = ym + p.getDate() + "\\";
			File directory1 = new File(ym); // 根目录是否存在
			if (!directory1.exists())
				directory1.mkdir();
			File directory2 = new File(ympname); // 根目录是否存在
			if (!directory2.exists())
				directory2.mkdir();
		}

		String oldAccessory = p.getAccessory();
		String[] oldFile = oldAccessory.split("\\*");
		int oldFileLength = oldFile.length;
		String oldFoldName;
		if (oldAccessoryNone == 0) {
			oldFoldName = oldFile[0] + "\\" + oldFile[1];
		} else {
			oldFoldName = sy + sm + "\\" + p.getDate();
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			newAccessory = sy + sm + "*" + p.getDate() + "*" + fileName;
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
		 * "\\" + sy + sm + sd + p.getDate() + ".zip"; try { ZipOutputStream out
		 * = new ZipOutputStream(new FileOutputStream(strZipName)); String[]
		 * allfile = fileName.split("\\*"); for (int i = 0; i < allfile.length;
		 * i++) { File tempfile = new File(rootPath + oldFoldName + "\\" +
		 * allfile[i]); if (tempfile.exists()) { FileInputStream fis = new
		 * FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while ((len =
		 * fis.read(buffer)) > 0) { out.write(buffer, 0, len); }
		 * out.closeEntry(); fis.close(); } } newAccessory = newAccessory + sy +
		 * sm + sd + p.getDate() + ".zip"; out.close(); } catch (Exception e) {
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
			p.setAccessory(fileName);
		} else {
			p.setAccessory(newAccessory);
		}

		operationControlDAO.updateOccuevaluation(p);
		return json;
	}

	public String deleteOccuevaluation(String ID, String rootPath) {
		Occuevaluation pro = new Occuevaluation();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			List<Occuevaluation> list = operationControlDAO.checkOccuevaluationID((Integer.parseInt(temp[i])));
			// 删除附件
			if (list != null) {
				Occuevaluation p = list.get(0);
				String fileName = p.getAccessory();
				System.out.println(fileName + "length" + fileName.length());
				if (fileName != null && fileName.length() != 0) {
					String[] name = fileName.split("\\*");
					String folder = name[0] + "//" + name[1];
					String path = rootPath + folder;
					System.out.println("--path:" + path);
					for (int j = 2; j < name.length; j++) {
						deleteFile(path + "//" + name[j]);
					}
					DeleteFolder(path);
				}
			}
			pro.setId(Integer.parseInt(temp[i]));
			operationControlDAO.deleteOccuevaluation(pro);
		}
		return json;
	}

	public String getOccumonitorList(String findstr, int start, int limit, String projectName) {

		List<Occumonitor> list = operationControlDAO.getOccumonitorList(findstr, start, limit, projectName);
		int total = operationControlDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Occumonitor p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"No\":\"" + p.getNo() + "\",\"Factor\":\"" + p.getFactor()
					+ "\",\"Time\":\"" + p.getTime() + "\",\"Result\":\"" + p.getResult() + "\",\"ProjectName\":\""
					+ p.getProjectName() + "\",\"Accessory\":\"" + p.getAccessory() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addOccumonitor(Occumonitor p, String fileName, String rootPath) {
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
		String ympname = ym + p.getNo() + "\\";
		File directory1 = new File(ym); // 根目录是否存在
		if (!directory1.exists())
			directory1.mkdir();
		File directory2 = new File(ympname); // 根目录是否存在
		if (!directory2.exists())
			directory2.mkdir();

		// 拷贝temp中的文件到正确的文件夹
		String[] newFile = fileName.split("\\*");
		for (String copyFile : newFile) {
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym + p.getNo());
		}

		// 存入数据库的内容
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}
		String accessory = sy + sm + "*" + p.getNo() + "*" + fileName;

		// 打包
		// byte[] buffer = new byte[1024];
		// if (fileName.length() > 0) {
		// String strZipName = ympname + sy + sm + sd + p.getNo() + ".zip";
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
		// accessory = accessory + sy + sm + sd + p.getNo() + ".zip";
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
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 4) + ".pdf");
				} else if (temp.endsWith(".docx")) {
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 5) + ".pdf");
				}
			}
		}

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			p.setAccessory(fileName);
		} else {
			p.setAccessory(accessory);
		}
		operationControlDAO.insertOccumonitor(p); //////////////////////////////////////////////////////////////////
		return json;
	}

	public String editOccumonitor(Occumonitor p, String fileName, String rootPath) {
		int oldAccessoryNone = 0;
		if (p.getAccessory() == null || p.getAccessory().length() <= 0) {
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

		if ((p.getAccessory() == null || p.getAccessory().length() <= 0)
				&& (fileName != null && fileName.length() > 0)) {
			// 建立月份和项目名文件夹
			String ym = rootPath + sy + sm + "\\";
			String ympname = ym + p.getNo() + "\\";
			File directory1 = new File(ym); // 根目录是否存在
			if (!directory1.exists())
				directory1.mkdir();
			File directory2 = new File(ympname); // 根目录是否存在
			if (!directory2.exists())
				directory2.mkdir();
		}

		String oldAccessory = p.getAccessory();
		String[] oldFile = oldAccessory.split("\\*");
		int oldFileLength = oldFile.length;
		String oldFoldName;
		if (oldAccessoryNone == 0) {
			oldFoldName = oldFile[0] + "\\" + oldFile[1];
		} else {
			oldFoldName = sy + sm + "\\" + p.getNo();
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			newAccessory = sy + sm + "*" + p.getNo() + "*" + fileName;
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
		 * "\\" + sy + sm + sd + p.getNo() + ".zip"; try { ZipOutputStream out =
		 * new ZipOutputStream(new FileOutputStream(strZipName)); String[]
		 * allfile = fileName.split("\\*"); for (int i = 0; i < allfile.length;
		 * i++) { File tempfile = new File(rootPath + oldFoldName + "\\" +
		 * allfile[i]); if (tempfile.exists()) { FileInputStream fis = new
		 * FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while ((len =
		 * fis.read(buffer)) > 0) { out.write(buffer, 0, len); }
		 * out.closeEntry(); fis.close(); } } newAccessory = newAccessory + sy +
		 * sm + sd + p.getNo() + ".zip"; out.close(); } catch (Exception e) {
		 * e.printStackTrace(); } }
		 */

		newAccessory = newAccessory.substring(0, newAccessory.length() - 1);

		if (fileName.length() != 0) {
			String[] allFile = fileName.split("\\*");
			for (String tempFile : allFile) {
				if (tempFile.endsWith(".doc")) {
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 4) + ".pdf");
				} else if (tempFile.endsWith(".docx")) {
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 5) + ".pdf");
				}
			}
		}

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			p.setAccessory(fileName);
		} else {
			p.setAccessory(newAccessory);
		}

		operationControlDAO.updateOccumonitor(p); //////////////////////////////////////////////////////////////////////
		return json;
	}

	public String deleteOccumonitor(String ID, String rootPath) {
		Occumonitor pro = new Occumonitor();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			List<Occumonitor> list = operationControlDAO.checkOccumonitorID((Integer.parseInt(temp[i])));
			// 删除附件
			if (list != null) {
				Occumonitor p = list.get(0);
				String fileName = p.getAccessory();
				System.out.println(fileName + "length" + fileName.length());
				if (fileName != null && fileName.length() != 0) {
					String[] name = fileName.split("\\*");
					String folder = name[0] + "//" + name[1];
					String path = rootPath + folder;
					System.out.println("--path:" + path);
					for (int j = 2; j < name.length; j++) {
						deleteFile(path + "//" + name[j]);
					}
					DeleteFolder(path);
				}
			}
			pro.setId(Integer.parseInt(temp[i]));
			operationControlDAO.deleteOccumonitor(pro);
		}
		return json;
	}

	public String getOtherjobList(String findstr, int start, int limit, String projectName) {

		List<Otherjob> list = operationControlDAO.getOtherjobList(findstr, start, limit, projectName);
		int total = operationControlDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Otherjob p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"Name\":\"" + p.getName() + "\",\"Time\":\"" + p.getTime()
					+ "\",\"Unit\":\"" + p.getUnit() + "\",\"Record\":\"" + p.getRecord() + "\",\"ProjectName\":\""
					+ p.getProjectName() + "\",\"Accessory\":\"" + p.getAccessory() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addOtherjob(Otherjob p, String fileName, String rootPath) {
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
		String ympname = ym + p.getName() + "\\";
		File directory1 = new File(ym); // 根目录是否存在
		if (!directory1.exists())
			directory1.mkdir();
		File directory2 = new File(ympname); // 根目录是否存在
		if (!directory2.exists())
			directory2.mkdir();

		// 拷贝temp中的文件到正确的文件夹
		String[] newFile = fileName.split("\\*");
		for (String copyFile : newFile) {
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym + p.getName());
		}

		// 存入数据库的内容
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}
		String accessory = sy + sm + "*" + p.getName() + "*" + fileName;

		// 打包
		// byte[] buffer = new byte[1024];
		// if (fileName.length() > 0) {
		// String strZipName = ympname + sy + sm + sd + p.getNo() + ".zip";
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
		// accessory = accessory + sy + sm + sd + p.getNo() + ".zip";
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
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 4) + ".pdf");
				} else if (temp.endsWith(".docx")) {
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 5) + ".pdf");
				}
			}
		}

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			p.setAccessory(fileName);
		} else {
			p.setAccessory(accessory);
		}
		operationControlDAO.insertOtherjob(p); //////////////////////////////////////////////////////////////////
		return json;
	}

	public String editOtherjob(Otherjob p, String fileName, String rootPath) {
		int oldAccessoryNone = 0;
		if (p.getAccessory() == null || p.getAccessory().length() <= 0) {
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

		if ((p.getAccessory() == null || p.getAccessory().length() <= 0)
				&& (fileName != null && fileName.length() > 0)) {
			// 建立月份和项目名文件夹
			String ym = rootPath + sy + sm + "\\";
			String ympname = ym + p.getName() + "\\";
			File directory1 = new File(ym); // 根目录是否存在
			if (!directory1.exists())
				directory1.mkdir();
			File directory2 = new File(ympname); // 根目录是否存在
			if (!directory2.exists())
				directory2.mkdir();
		}

		String oldAccessory = p.getAccessory();
		String[] oldFile = oldAccessory.split("\\*");
		int oldFileLength = oldFile.length;
		String oldFoldName;
		if (oldAccessoryNone == 0) {
			oldFoldName = oldFile[0] + "\\" + oldFile[1];
		} else {
			oldFoldName = sy + sm + "\\" + p.getName();
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			newAccessory = sy + sm + "*" + p.getName() + "*" + fileName;
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
		 * "\\" + sy + sm + sd + p.getNo() + ".zip"; try { ZipOutputStream out =
		 * new ZipOutputStream(new FileOutputStream(strZipName)); String[]
		 * allfile = fileName.split("\\*"); for (int i = 0; i < allfile.length;
		 * i++) { File tempfile = new File(rootPath + oldFoldName + "\\" +
		 * allfile[i]); if (tempfile.exists()) { FileInputStream fis = new
		 * FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while ((len =
		 * fis.read(buffer)) > 0) { out.write(buffer, 0, len); }
		 * out.closeEntry(); fis.close(); } } newAccessory = newAccessory + sy +
		 * sm + sd + p.getNo() + ".zip"; out.close(); } catch (Exception e) {
		 * e.printStackTrace(); } }
		 */

		newAccessory = newAccessory.substring(0, newAccessory.length() - 1);

		if (fileName.length() != 0) {
			String[] allFile = fileName.split("\\*");
			for (String tempFile : allFile) {
				if (tempFile.endsWith(".doc")) {
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 4) + ".pdf");
				} else if (tempFile.endsWith(".docx")) {
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 5) + ".pdf");
				}
			}
		}

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			p.setAccessory(fileName);
		} else {
			p.setAccessory(newAccessory);
		}

		operationControlDAO.updateOtherjob(p); //////////////////////////////////////////////////////////////////////
		return json;
	}

	public String deleteOtherjob(String ID, String rootPath) {
		Otherjob pro = new Otherjob();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			List<Otherjob> list = operationControlDAO.checkOtherjobID((Integer.parseInt(temp[i])));
			// 删除附件
			if (list != null) {
				Otherjob p = list.get(0);
				String fileName = p.getAccessory();
				System.out.println(fileName + "length" + fileName.length());
				if (fileName != null && fileName.length() != 0) {
					String[] name = fileName.split("\\*");
					String folder = name[0] + "//" + name[1];
					String path = rootPath + folder;
					System.out.println("--path:" + path);
					for (int j = 2; j < name.length; j++) {
						deleteFile(path + "//" + name[j]);
					}
					DeleteFolder(path);
				}
			}
			pro.setId(Integer.parseInt(temp[i]));
			operationControlDAO.deleteOtherjob(pro);
		}
		return json;
	}

	public String getSafepromonitorList(String findstr, int start, int limit) {

		List<Safepromonitor> list = operationControlDAO.getSafepromonitorList(findstr, start, limit);
		int total = operationControlDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Safepromonitor p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"Bzperson\":\"" + p.getBzperson() + "\",\"Bztime\":\""
					+ p.getBztime() + "\",\"Shperson\":\"" + p.getShperson() + "\",\"Shtime\":\"" + p.getShtime()
					+ "\",\"Pzperson\":\"" + p.getPzperson() + "\",\"Pztime\":\"" + p.getPztime()
					+ "\",\"Accessory\":\"" + p.getAccessory() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addSafepromonitor(Safepromonitor p, String fileName, String rootPath) {
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
		String ympname = ym + p.getPzperson() + "\\";
		File directory1 = new File(ym); // 根目录是否存在
		if (!directory1.exists())
			directory1.mkdir();
		File directory2 = new File(ympname); // 根目录是否存在
		if (!directory2.exists())
			directory2.mkdir();

		// 拷贝temp中的文件到正确的文件夹
		String[] newFile = fileName.split("\\*");
		for (String copyFile : newFile) {
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym+ p.getPzperson() );
		}

		// 存入数据库的内容
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}
		String accessory = sy + sm + "*"+ p.getPzperson() +"*" + fileName;

		// 打包
		// byte[] buffer = new byte[1024];
		// if (fileName.length() > 0) {
		// String strZipName = ympname + sy + sm + sd + ".zip";
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
		// accessory = accessory + sy + sm + sd + ".zip";
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
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 4) + ".pdf");
				} else if (temp.endsWith(".docx")) {
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 5) + ".pdf");
				}
			}
		}

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			p.setAccessory(fileName);
		} else {
			p.setAccessory(accessory);
		}
		operationControlDAO.insertSafepromonitor(p); //////////////////////////////////////////////////////////////////
		return json;
	}

	public String editSafepromonitor(Safepromonitor p, String fileName, String rootPath) {
		int oldAccessoryNone = 0;
		if (p.getAccessory() == null || p.getAccessory().length() <= 0) {
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

		if ((p.getAccessory() == null || p.getAccessory().length() <= 0)
				&& (fileName != null && fileName.length() > 0)) {
			// 建立月份和项目名文件夹
			String ym = rootPath + sy + sm + "\\";
			String ympname = ym + p.getPzperson() + "\\";
			File directory1 = new File(ym); // 根目录是否存在
			if (!directory1.exists())
				directory1.mkdir();
			File directory2 = new File(ympname); // 根目录是否存在
			if (!directory2.exists())
				directory2.mkdir();
		}

		String oldAccessory = p.getAccessory();
		String[] oldFile = oldAccessory.split("\\*");
		int oldFileLength = oldFile.length;
		String oldFoldName;
		if (oldAccessoryNone == 0) {
			oldFoldName = oldFile[0] + "\\" + oldFile[1];
		} else {
			oldFoldName = sy + sm + "\\"+ p.getPzperson() ;
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			newAccessory = sy + sm + "*"+ p.getPzperson() +"*" + fileName;
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
		 * strZipName = rootPath + oldFoldName + "\\" + sy + sm + sd + ".zip";
		 * try { ZipOutputStream out = new ZipOutputStream(new
		 * FileOutputStream(strZipName)); String[] allfile =
		 * fileName.split("\\*"); for (int i = 0; i < allfile.length; i++) {
		 * File tempfile = new File(rootPath + oldFoldName + "\\" + allfile[i]);
		 * if (tempfile.exists()) { FileInputStream fis = new
		 * FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while ((len =
		 * fis.read(buffer)) > 0) { out.write(buffer, 0, len); }
		 * out.closeEntry(); fis.close(); } } newAccessory = newAccessory + sy +
		 * sm + sd + ".zip"; out.close(); } catch (Exception e) {
		 * e.printStackTrace(); } }
		 */

		newAccessory = newAccessory.substring(0, newAccessory.length() - 1);

		if (fileName.length() != 0) {
			String[] allFile = fileName.split("\\*");
			for (String tempFile : allFile) {
				if (tempFile.endsWith(".doc")) {
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 4) + ".pdf");
				} else if (tempFile.endsWith(".docx")) {
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 5) + ".pdf");
				}
			}
		}

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			p.setAccessory(fileName);
		} else {
			p.setAccessory(newAccessory);
		}

		operationControlDAO.updateSafepromonitor(p); //////////////////////////////////////////////////////////////////////
		return json;
	}

	public String deleteSafepromonitor(String ID, String rootPath) {
		Safepromonitor pro = new Safepromonitor();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			List<Safepromonitor> list = operationControlDAO.checkSafepromonitorID((Integer.parseInt(temp[i])));
			// 删除附件
			if (list != null) {
				Safepromonitor p = list.get(0);
				String fileName = p.getAccessory();
				System.out.println(fileName + "length" + fileName.length());
				if (fileName != null && fileName.length() != 0) {
					String[] name = fileName.split("\\*");
					String folder = name[0] + "//" + name[1];
					String path = rootPath + folder;
					System.out.println("--path:" + path);
					for (int j = 2; j < name.length; j++) {
						deleteFile(path + "//" + name[j]);
					}
					DeleteFolder(path);
				}
			}
			pro.setId(Integer.parseInt(temp[i]));
			operationControlDAO.deleteSafepromonitor(pro);
		}
		return json;
	}

	public String getSafeprojdList(String findstr, int start, int limit) {

		List<Safeprojd> list = operationControlDAO.getSafeprojdList(findstr, start, limit);
		int total = operationControlDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Safeprojd p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"Person\":\"" + p.getPerson() + "\",\"Jdperson\":\""
					+ p.getJdperson() + "\",\"Time\":\"" + p.getTime() + "\",\"Accessory\":\"" + p.getAccessory()
					+ "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addSafeprojd(Safeprojd p, String fileName, String rootPath) {
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
		String ympname = ym + p.getPerson() + "\\";
		File directory1 = new File(ym); // 根目录是否存在
		if (!directory1.exists())
			directory1.mkdir();
		File directory2 = new File(ympname); // 根目录是否存在
		if (!directory2.exists())
			directory2.mkdir();

		// 拷贝temp中的文件到正确的文件夹
		String[] newFile = fileName.split("\\*");
		for (String copyFile : newFile) {
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym+ p.getPerson());
		}

		// 存入数据库的内容
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}
		String accessory = sy + sm + "*" + p.getPerson()+"*"+ fileName;

		// 打包
		// byte[] buffer = new byte[1024];
		// if (fileName.length() > 0) {
		// String strZipName = ympname + sy + sm + sd + ".zip";
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
		// accessory = accessory + sy + sm + sd + ".zip";
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
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 4) + ".pdf");
				} else if (temp.endsWith(".docx")) {
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 5) + ".pdf");
				}
			}
		}

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			p.setAccessory(fileName);
		} else {
			p.setAccessory(accessory);
		}
		operationControlDAO.insertSafeprojd(p); //////////////////////////////////////////////////////////////////
		return json;
	}

	public String editSafeprojd(Safeprojd p, String fileName, String rootPath) {
		int oldAccessoryNone = 0;
		if (p.getAccessory() == null || p.getAccessory().length() <= 0) {
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

		if ((p.getAccessory() == null || p.getAccessory().length() <= 0)
				&& (fileName != null && fileName.length() > 0)) {
			// 建立月份和项目名文件夹
			String ym = rootPath + sy + sm + "\\";
			String ympname = ym + p.getPerson()+ "\\";
			File directory1 = new File(ym); // 根目录是否存在
			if (!directory1.exists())
				directory1.mkdir();
			File directory2 = new File(ympname); // 根目录是否存在
			if (!directory2.exists())
				directory2.mkdir();
		}

		String oldAccessory = p.getAccessory();
		String[] oldFile = oldAccessory.split("\\*");
		int oldFileLength = oldFile.length;
		String oldFoldName;
		if (oldAccessoryNone == 0) {
			oldFoldName = oldFile[0] + "\\" + oldFile[1];
		} else {
			oldFoldName = sy + sm + "\\"+ p.getPerson();
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			newAccessory = sy + sm + "*" + p.getPerson()+"*"+ fileName;
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
		 * strZipName = rootPath + oldFoldName + "\\" + sy + sm + sd + ".zip";
		 * try { ZipOutputStream out = new ZipOutputStream(new
		 * FileOutputStream(strZipName)); String[] allfile =
		 * fileName.split("\\*"); for (int i = 0; i < allfile.length; i++) {
		 * File tempfile = new File(rootPath + oldFoldName + "\\" + allfile[i]);
		 * if (tempfile.exists()) { FileInputStream fis = new
		 * FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while ((len =
		 * fis.read(buffer)) > 0) { out.write(buffer, 0, len); }
		 * out.closeEntry(); fis.close(); } } newAccessory = newAccessory + sy +
		 * sm + sd + ".zip"; out.close(); } catch (Exception e) {
		 * e.printStackTrace(); } }
		 */

		newAccessory = newAccessory.substring(0, newAccessory.length() - 1);

		if (fileName.length() != 0) {
			String[] allFile = fileName.split("\\*");
			for (String tempFile : allFile) {
				if (tempFile.endsWith(".doc")) {
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 4) + ".pdf");
				} else if (tempFile.endsWith(".docx")) {
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 5) + ".pdf");
				}
			}
		}

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			p.setAccessory(fileName);
		} else {
			p.setAccessory(newAccessory);
		}

		operationControlDAO.updateSafeprojd(p); //////////////////////////////////////////////////////////////////////
		return json;
	}

	public String deleteSafeprojd(String ID, String rootPath) {
		Safeprojd pro = new Safeprojd();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			List<Safeprojd> list = operationControlDAO.checkSafeprojdID((Integer.parseInt(temp[i])));
			// 删除附件
			if (list != null) {
				Safeprojd p = list.get(0);
				String fileName = p.getAccessory();
				System.out.println(fileName + "length" + fileName.length());
				if (fileName != null && fileName.length() != 0) {
					String[] name = fileName.split("\\*");
					String folder = name[0] + "//" + name[1];
					String path = rootPath + folder;
					System.out.println("--path:" + path);
					for (int j = 2; j < name.length; j++) {
						deleteFile(path + "//" + name[j]);
					}
					DeleteFolder(path);
				}
			}
			pro.setId(Integer.parseInt(temp[i]));
			operationControlDAO.deleteSafeprojd(pro);
		}
		return json;
	}
	// end----------------------------------------

	// jianglf-----------------------------------------------------------------
	public String getTezhongsbpeopleList(String findstr, int start, int limit, String projectName) {

		List<Tezhongsbpeople> list = operationControlDAO.getTezhongsbpeopleList(findstr, start, limit, projectName);
		int total = operationControlDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Tezhongsbpeople p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"No\":\"" + p.getNo() + "\",\"FbName\":\"" + p.getFbName()
					+ "\",\"Type\":\"" + p.getType() + "\",\"Project\":\"" + p.getProject() + "\",\"Name\":\""
					+ p.getName() + "\",\"Gender\":\"" + p.getGender() + "\",\"CardNo\":\"" + p.getCardNo()
					+ "\",\"BeginTime\":\"" + p.getBeginTime() + "\",\"ValidTime\":\"" + p.getValidTime()
					+ "\",\"Unit\":\"" + p.getUnit() + "\",\"Ps\":\"" + p.getPs() + "\",\"ProjectName\":\""
					+ p.getProjectName() + "\"";
			jsonStr += ",\"Peixunnr\":\"";
			List<Traintable> emp = operationControlDAO.findTrainRecordByName(p.getName(), 213);
			if (emp.isEmpty()) {
				if (p.getPeixun() == null || !p.getPeixun().equals("否")) {
					p.setPeixun("否");
					operationControlDAO.updateTezhongsbpeople(p);
				}
				jsonStr += "无" + "\"";
			} else {
				String tmpJson = "";
				for (int j = 0; j < emp.size(); ++j) {
					String tmpNames = "," + emp.get(j).getEmployee() + ",";
					if (tmpNames.indexOf("," + p.getName() + ",") >= 0)

						tmpJson += p.getName() + "在" + String.format("%tF", emp.get(j).getTrainDate()) + "参加了“"
								+ emp.get(j).getContent() + "”培训<br>";
				}

				if ((tmpJson.length() > 0)) {
					if (p.getPeixun() == null || !p.getPeixun().equals("是")) {
						p.setPeixun("是");
						operationControlDAO.updateTezhongsbpeople(p);
					}
					jsonStr += tmpJson.substring(0, tmpJson.length() - 4) + "\"";
				} else {
					if (p.getPeixun() == null || !p.getPeixun().equals("否")) {
						p.setPeixun("否");
						operationControlDAO.updateTezhongsbpeople(p);
					}
					jsonStr += "无" + "\"";
				}
			}
			jsonStr += ",\"Peixun\":\"" + p.getPeixun() + "\"";
			jsonStr += ",\"Accessory\":\"" + p.getAccessory() + "\"}";

		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addTezhongsbpeople(Tezhongsbpeople p, String fileName, String rootPath) {
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
		String ympname = ym + p.getFbName()+ "\\";
		File directory1 = new File(ym); // 根目录是否存在
		if (!directory1.exists())
			directory1.mkdir();
		File directory2 = new File(ympname); // 根目录是否存在
		if (!directory2.exists())
			directory2.mkdir();

		// 拷贝temp中的文件到正确的文件夹
		String[] newFile = fileName.split("\\*");
		for (String copyFile : newFile) {
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym + p.getFbName());
		}

		// 存入数据库的内容
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}
		String accessory = sy + sm + "*"  + p.getFbName()+"*"+ fileName;

		// 打包
		// byte[] buffer = new byte[1024];
		// if (fileName.length() > 0) {
		// String strZipName = ympname + sy + sm + sd + ".zip";
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
		// accessory = accessory + sy + sm + sd + ".zip";
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
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 4) + ".pdf");
				} else if (temp.endsWith(".docx")) {
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 5) + ".pdf");
				}
			}
		}

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			p.setAccessory(fileName);
		} else {
			p.setAccessory(accessory);
		}
		operationControlDAO.insertTezhongsbpeople(p); //////////////////////////////////////////////////////////////////
		return json;
	}

	public String editTezhongsbpeople(Tezhongsbpeople p, String fileName, String rootPath) {
		int oldAccessoryNone = 0;
		if (p.getAccessory() == null || p.getAccessory().length() <= 0) {
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

		if ((p.getAccessory() == null || p.getAccessory().length() <= 0)
				&& (fileName != null && fileName.length() > 0)) {
			// 建立月份和项目名文件夹
			String ym = rootPath + sy + sm + "\\";
			String ympname = ym  + p.getFbName()+ "\\";
			File directory1 = new File(ym); // 根目录是否存在
			if (!directory1.exists())
				directory1.mkdir();
			File directory2 = new File(ympname); // 根目录是否存在
			if (!directory2.exists())
				directory2.mkdir();
		}

		String oldAccessory = p.getAccessory();
		String[] oldFile = oldAccessory.split("\\*");
		int oldFileLength = oldFile.length;
		String oldFoldName;
		if (oldAccessoryNone == 0) {
			oldFoldName = oldFile[0] + "\\" + oldFile[1];
		} else {
			oldFoldName = sy + sm + "\\" + p.getFbName();
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			newAccessory = sy + sm + "*" + p.getFbName()+"*" + fileName;
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
		 * strZipName = rootPath + oldFoldName + "\\" + sy + sm + sd + ".zip";
		 * try { ZipOutputStream out = new ZipOutputStream(new
		 * FileOutputStream(strZipName)); String[] allfile =
		 * fileName.split("\\*"); for (int i = 0; i < allfile.length; i++) {
		 * File tempfile = new File(rootPath + oldFoldName + "\\" + allfile[i]);
		 * if (tempfile.exists()) { FileInputStream fis = new
		 * FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while ((len =
		 * fis.read(buffer)) > 0) { out.write(buffer, 0, len); }
		 * out.closeEntry(); fis.close(); } } newAccessory = newAccessory + sy +
		 * sm + sd + ".zip"; out.close(); } catch (Exception e) {
		 * e.printStackTrace(); } }
		 */

		newAccessory = newAccessory.substring(0, newAccessory.length() - 1);

		if (fileName.length() != 0) {
			String[] allFile = fileName.split("\\*");
			for (String tempFile : allFile) {
				if (tempFile.endsWith(".doc")) {
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 4) + ".pdf");
				} else if (tempFile.endsWith(".docx")) {
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 5) + ".pdf");
				}
			}
		}

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			p.setAccessory(fileName);
		} else {
			p.setAccessory(newAccessory);
		}

		operationControlDAO.updateTezhongsbpeople(p); //////////////////////////////////////////////////////////////////////
		return json;
	}

	public String deleteTezhongsbpeople(String ID, String rootPath) {
		Tezhongsbpeople pro = new Tezhongsbpeople();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			List<Tezhongsbpeople> list = operationControlDAO.checkTezhongsbpeopleID((Integer.parseInt(temp[i])));
			// 删除附件
			if (list != null) {
				Tezhongsbpeople p = list.get(0);
				String fileName = p.getAccessory();
				System.out.println(fileName + "length" + fileName.length());
				if (fileName != null && fileName.length() != 0) {
					String[] name = fileName.split("\\*");
					String folder = name[0] + "//" + name[1];
					String path = rootPath + folder;
					System.out.println("--path:" + path);
					for (int j = 2; j < name.length; j++) {
						deleteFile(path + "//" + name[j]);
					}
					DeleteFolder(path);
				}
			}
			pro.setId(Integer.parseInt(temp[i]));
			operationControlDAO.deleteTezhongsbpeople(pro);
		}
		return json;
	}

	public String getFbtestjudgeList(String findstr, int start, int limit, String projectName) {

		List<Fbtestjudge> list = operationControlDAO.getFbtestjudgeList(findstr, start, limit, projectName);
		int total = operationControlDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Fbtestjudge p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"No\":\"" + p.getNo() + "\",\"Tester\":\"" + p.getTester()
					+ "\",\"Time\":\"" + p.getTime() + "\",\"Result\":\"" + p.getResult() + "\",\"ProjectName\":\""
					+ p.getProjectName() + "\",\"Accessory\":\"" + p.getAccessory() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addFbtestjudge(Fbtestjudge p, String fileName, String rootPath) {
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
		String ympname = ym + p.getTester()+ "\\";
		File directory1 = new File(ym); // 根目录是否存在
		if (!directory1.exists())
			directory1.mkdir();
		File directory2 = new File(ympname); // 根目录是否存在
		if (!directory2.exists())
			directory2.mkdir();

		// 拷贝temp中的文件到正确的文件夹
		String[] newFile = fileName.split("\\*");
		for (String copyFile : newFile) {
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym+ p.getTester());
		}

		// 存入数据库的内容
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}
		String accessory = sy + sm + "*" + p.getTester()+"*"+ fileName;

		// 打包
		// byte[] buffer = new byte[1024];
		// if (fileName.length() > 0) {
		// String strZipName = ympname + sy + sm + sd + ".zip";
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
		// accessory = accessory + sy + sm + sd + ".zip";
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
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 4) + ".pdf");
				} else if (temp.endsWith(".docx")) {
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 5) + ".pdf");
				}
			}
		}

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			p.setAccessory(fileName);
		} else {
			p.setAccessory(accessory);
		}
		operationControlDAO.insertFbtestjudge(p); //////////////////////////////////////////////////////////////////
		return json;
	}

	public String editFbtestjudge(Fbtestjudge p, String fileName, String rootPath) {
		int oldAccessoryNone = 0;
		if (p.getAccessory() == null || p.getAccessory().length() <= 0) {
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

		if ((p.getAccessory() == null || p.getAccessory().length() <= 0)
				&& (fileName != null && fileName.length() > 0)) {
			// 建立月份和项目名文件夹
			String ym = rootPath + sy + sm + "\\";
			String ympname = ym + p.getTester()+ "\\";
			File directory1 = new File(ym); // 根目录是否存在
			if (!directory1.exists())
				directory1.mkdir();
			File directory2 = new File(ympname); // 根目录是否存在
			if (!directory2.exists())
				directory2.mkdir();
		}

		String oldAccessory = p.getAccessory();
		String[] oldFile = oldAccessory.split("\\*");
		int oldFileLength = oldFile.length;
		String oldFoldName;
		if (oldAccessoryNone == 0) {
			oldFoldName = oldFile[0] + "\\" + oldFile[1];
		} else {
			oldFoldName = sy + sm + "\\"+ p.getTester();
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			newAccessory = sy + sm + "*" + p.getTester()+"*"+ fileName;
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
		 * strZipName = rootPath + oldFoldName + "\\" + sy + sm + sd + ".zip";
		 * try { ZipOutputStream out = new ZipOutputStream(new
		 * FileOutputStream(strZipName)); String[] allfile =
		 * fileName.split("\\*"); for (int i = 0; i < allfile.length; i++) {
		 * File tempfile = new File(rootPath + oldFoldName + "\\" + allfile[i]);
		 * if (tempfile.exists()) { FileInputStream fis = new
		 * FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while ((len =
		 * fis.read(buffer)) > 0) { out.write(buffer, 0, len); }
		 * out.closeEntry(); fis.close(); } } newAccessory = newAccessory + sy +
		 * sm + sd + ".zip"; out.close(); } catch (Exception e) {
		 * e.printStackTrace(); } }
		 */

		newAccessory = newAccessory.substring(0, newAccessory.length() - 1);

		if (fileName.length() != 0) {
			String[] allFile = fileName.split("\\*");
			for (String tempFile : allFile) {
				if (tempFile.endsWith(".doc")) {
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 4) + ".pdf");
				} else if (tempFile.endsWith(".docx")) {
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 5) + ".pdf");
				}
			}
		}

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			p.setAccessory(fileName);
		} else {
			p.setAccessory(newAccessory);
		}

		operationControlDAO.updateFbtestjudge(p); //////////////////////////////////////////////////////////////////////
		return json;
	}

	public String deleteFbtestjudge(String ID, String rootPath) {
		Fbtestjudge pro = new Fbtestjudge();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			List<Fbtestjudge> list = operationControlDAO.checkFbtestjudgeID((Integer.parseInt(temp[i])));
			// 删除附件
			if (list != null) {
				Fbtestjudge p = list.get(0);
				String fileName = p.getAccessory();
				System.out.println(fileName + "length" + fileName.length());
				if (fileName != null && fileName.length() != 0) {
					String[] name = fileName.split("\\*");
					String folder = name[0] + "//" + name[1];
					String path = rootPath + folder;
					System.out.println("--path:" + path);
					for (int j = 2; j < name.length; j++) {
						deleteFile(path + "//" + name[j]);
					}
					DeleteFolder(path);
				}
			}
			pro.setId(Integer.parseInt(temp[i]));
			operationControlDAO.deleteFbtestjudge(pro);
		}
		return json;
	}

	// end---------------------------------------------------------------------
	public String getSafetyprotocalList(String findstr, int start, int limit, String projectName) {

		List<Safetyprotocal> list = operationControlDAO.getSafetyprotocalList(findstr, start, limit, projectName);
		int total = operationControlDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Safetyprotocal p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"Type\":\"" + p.getType() + "\",\"Fbname\":\"" + p.getFbname()
					+ "\",\"Name\":\"" + p.getName() + "\",\"Date\":\"" + p.getDate() + "\",\"ProjectName\":\""
					+ p.getProjectName() + "\",\"Accessory\":\"" + p.getAccessory() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addSafetyprotocal(Safetyprotocal p, String fileName, String rootPath) {
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
		String ympname = ym + p.getName() + "\\";
		File directory1 = new File(ym); // 根目录是否存在
		if (!directory1.exists())
			directory1.mkdir();
		File directory2 = new File(ympname); // 根目录是否存在
		if (!directory2.exists())
			directory2.mkdir();

		// 拷贝temp中的文件到正确的文件夹
		String[] newFile = fileName.split("\\*");
		for (String copyFile : newFile) {
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym + p.getName());
		}

		// 存入数据库的内容
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}
		String accessory = sy + sm + "*" + p.getName() + "*" + fileName;

		// 打包
		// byte[] buffer = new byte[1024];
		// if (fileName.length() > 0) {
		// String strZipName = ympname + sy + sm + sd + p.getName() + ".zip";
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
		// accessory = accessory + sy + sm + sd + p.getName() + ".zip";
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
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 4) + ".pdf");
				} else if (temp.endsWith(".docx")) {
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 5) + ".pdf");
				}
			}
		}

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			p.setAccessory(fileName);
		} else {
			p.setAccessory(accessory);
		}
		operationControlDAO.insertSafetyprotocal(p); //////////////////////////////////////////////////////////////////
		return json;
	}

	public String editSafetyprotocal(Safetyprotocal p, String fileName, String rootPath) {
		int oldAccessoryNone = 0;
		if (p.getAccessory() == null || p.getAccessory().length() <= 0) {
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

		if ((p.getAccessory() == null || p.getAccessory().length() <= 0)
				&& (fileName != null && fileName.length() > 0)) {
			// 建立月份和项目名文件夹
			String ym = rootPath + sy + sm + "\\";
			String ympname = ym + p.getName() + "\\";
			File directory1 = new File(ym); // 根目录是否存在
			if (!directory1.exists())
				directory1.mkdir();
			File directory2 = new File(ympname); // 根目录是否存在
			if (!directory2.exists())
				directory2.mkdir();
		}

		String oldAccessory = p.getAccessory();
		String[] oldFile = oldAccessory.split("\\*");
		int oldFileLength = oldFile.length;
		String oldFoldName;
		if (oldAccessoryNone == 0) {
			oldFoldName = oldFile[0] + "\\" + oldFile[1];
		} else {
			oldFoldName = sy + sm + "\\" + p.getName();
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			newAccessory = sy + sm + "*" + p.getName() + "*" + fileName;
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
		 * "\\" + sy + sm + sd + p.getName() + ".zip"; try { ZipOutputStream out
		 * = new ZipOutputStream(new FileOutputStream(strZipName)); String[]
		 * allfile = fileName.split("\\*"); for (int i = 0; i < allfile.length;
		 * i++) { File tempfile = new File(rootPath + oldFoldName + "\\" +
		 * allfile[i]); if (tempfile.exists()) { FileInputStream fis = new
		 * FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while ((len =
		 * fis.read(buffer)) > 0) { out.write(buffer, 0, len); }
		 * out.closeEntry(); fis.close(); } } newAccessory = newAccessory + sy +
		 * sm + sd + p.getName() + ".zip"; out.close(); } catch (Exception e) {
		 * e.printStackTrace(); } }
		 */

		newAccessory = newAccessory.substring(0, newAccessory.length() - 1);

		if (fileName.length() != 0) {
			String[] allFile = fileName.split("\\*");
			for (String tempFile : allFile) {
				if (tempFile.endsWith(".doc")) {
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 4) + ".pdf");
				} else if (tempFile.endsWith(".docx")) {
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 5) + ".pdf");
				}
			}
		}

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			p.setAccessory(fileName);
		} else {
			p.setAccessory(newAccessory);
		}

		operationControlDAO.updateSafetyprotocal(p); //////////////////////////////////////////////////////////////////////
		return json;
	}

	public String deleteSafetyprotocal(String ID, String rootPath) {
		Safetyprotocal pro = new Safetyprotocal();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			List<Safetyprotocal> list = operationControlDAO.checkSafetyprotocalID((Integer.parseInt(temp[i])));
			// 删除附件
			if (list != null) {
				Safetyprotocal p = list.get(0);
				String fileName = p.getAccessory();
				System.out.println(fileName + "length" + fileName.length());
				if (fileName != null && fileName.length() != 0) {
					String[] name = fileName.split("\\*");
					String folder = name[0] + "//" + name[1];
					String path = rootPath + folder;
					System.out.println("--path:" + path);
					for (int j = 2; j < name.length; j++) {
						deleteFile(path + "//" + name[j]);
					}
					DeleteFolder(path);
				}
			}
			pro.setId(Integer.parseInt(temp[i]));
			operationControlDAO.deleteSafetyprotocal(pro);
		}
		return json;
	}

	public String getConstructionelecList(String findstr, int start, int limit, String projectName) {

		List<Constructionelec> list = operationControlDAO.getConstructionelecList(findstr, start, limit, projectName);
		int total = operationControlDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Constructionelec p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"Type\":\"" + p.getType() + "\",\"Status\":\"" + p.getStatus() + "\",\"Approve\":\"" + p.getApprove() + "\",\"Name\":\"" + p.getName()
					+ "\",\"Num\":\"" + p.getNum() + "\",\"Singlepower\":\"" + p.getSinglepower() + "\",\"Sumpower\":\"" + p.getSumpower() + "\",\"Factory\":\"" + p.getFactory() + "\",\"Shiyong\":\""
					+ p.getShiyong() + "\",\"Outtime\":\"" + p.getOuttime() + "\",\"Intime\":\"" + p.getIntime() + "\",\"Plantime\":\"" + p.getPlantime() + "\",\"Realtime\":\"" + p.getRealtime() + "\",\"ProjectName\":\""
					+ p.getProjectName() + "\",\"Accessory\":\""
					+ p.getAccessory() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addConstructionelec(Constructionelec p, String fileName, String rootPath) {
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
		String ympname = ym + p.getName() + "\\";
		File directory1 = new File(ym); // 根目录是否存在
		if (!directory1.exists())
			directory1.mkdir();
		File directory2 = new File(ympname); // 根目录是否存在
		if (!directory2.exists())
			directory2.mkdir();

		// 拷贝temp中的文件到正确的文件夹
		String[] newFile = fileName.split("\\*");
		for (String copyFile : newFile) {
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym + p.getName());
		}

		// 存入数据库的内容
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}
		String accessory = sy + sm + "*" + p.getName() + "*" + fileName;

		// 打包
		// byte[] buffer = new byte[1024];
		// if (fileName.length() > 0) {
		// String strZipName = ympname + sy + sm + sd + p.getName() + ".zip";
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
		// accessory = accessory + sy + sm + sd + p.getName() + ".zip";
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
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 4) + ".pdf");
				} else if (temp.endsWith(".docx")) {
					WordToPdf.translateThread(ympname + temp, ympname + temp.substring(0, temp.length() - 5) + ".pdf");
				}
			}
		}

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			p.setAccessory(fileName);
		} else {
			p.setAccessory(accessory);
		}
		operationControlDAO.insertConstructionelec(p); //////////////////////////////////////////////////////////////////
		return json;
	}

	public String editConstructionelec(Constructionelec p, String fileName, String rootPath) {
		int oldAccessoryNone = 0;
		if (p.getAccessory() == null || p.getAccessory().length() <= 0) {
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

		if ((p.getAccessory() == null || p.getAccessory().length() <= 0)
				&& (fileName != null && fileName.length() > 0)) {
			// 建立月份和项目名文件夹
			String ym = rootPath + sy + sm + "\\";
			String ympname = ym + p.getName() + "\\";
			File directory1 = new File(ym); // 根目录是否存在
			if (!directory1.exists())
				directory1.mkdir();
			File directory2 = new File(ympname); // 根目录是否存在
			if (!directory2.exists())
				directory2.mkdir();
		}

		String oldAccessory = p.getAccessory();
		String[] oldFile = oldAccessory.split("\\*");
		int oldFileLength = oldFile.length;
		String oldFoldName;
		if (oldAccessoryNone == 0) {
			oldFoldName = oldFile[0] + "\\" + oldFile[1];
		} else {
			oldFoldName = sy + sm + "\\" + p.getName();
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			newAccessory = sy + sm + "*" + p.getName() + "*" + fileName;
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
		 * "\\" + sy + sm + sd + p.getName() + ".zip"; try { ZipOutputStream out
		 * = new ZipOutputStream(new FileOutputStream(strZipName)); String[]
		 * allfile = fileName.split("\\*"); for (int i = 0; i < allfile.length;
		 * i++) { File tempfile = new File(rootPath + oldFoldName + "\\" +
		 * allfile[i]); if (tempfile.exists()) { FileInputStream fis = new
		 * FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while ((len =
		 * fis.read(buffer)) > 0) { out.write(buffer, 0, len); }
		 * out.closeEntry(); fis.close(); } } newAccessory = newAccessory + sy +
		 * sm + sd + p.getName() + ".zip"; out.close(); } catch (Exception e) {
		 * e.printStackTrace(); } }
		 */

		newAccessory = newAccessory.substring(0, newAccessory.length() - 1);

		if (fileName.length() != 0) {
			String[] allFile = fileName.split("\\*");
			for (String tempFile : allFile) {
				if (tempFile.endsWith(".doc")) {
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 4) + ".pdf");
				} else if (tempFile.endsWith(".docx")) {
					WordToPdf.translateThread(rootPath + oldFoldName + "\\" + tempFile,
							rootPath + oldFoldName + "\\" + tempFile.substring(0, tempFile.length() - 5) + ".pdf");
				}
			}
		}

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			p.setAccessory(fileName);
		} else {
			p.setAccessory(newAccessory);
		}

		operationControlDAO.updateConstructionelec(p); //////////////////////////////////////////////////////////////////////
		return json;
	}

	public String deleteConstructionelec(String ID, String rootPath) {
		Constructionelec pro = new Constructionelec();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			List<Constructionelec> list = operationControlDAO.checkConstructionelecID((Integer.parseInt(temp[i])));
			// 删除附件
			if (list != null) {
				Constructionelec p = list.get(0);
				String fileName = p.getAccessory();
				System.out.println(fileName + "length" + fileName.length());
				if (fileName != null && fileName.length() != 0) {
					String[] name = fileName.split("\\*");
					String folder = name[0] + "//" + name[1];
					String path = rootPath + folder;
					System.out.println("--path:" + path);
					for (int j = 2; j < name.length; j++) {
						deleteFile(path + "//" + name[j]);
					}
					DeleteFolder(path);
				}
			}
			pro.setId(Integer.parseInt(temp[i]));
			operationControlDAO.deleteConstructionelec(pro);
		}
		return json;
	}
	
	// 获得 accessory 并建立相应的文件夹
		private String addAccessory(String rootPath, String fileName, String pName) {
			//有可能需要用到当前时间作为编号
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");//设置日期格式        
	      	String mytime = df.format(new Date());
	      	
	      	//分别获取当前年月日
	      	Calendar now = Calendar.getInstance();  
	        int year  = now.get(Calendar.YEAR);
	        int month = now.get(Calendar.MONTH) + 1;
	        int day   = now.get(Calendar.DATE);
	        String sy = String.valueOf(year);
	        String sm = String.valueOf(month);
	        if (month < 10) {
	        	sm = "0" + sm;
	        }
	        String sd = String.valueOf(day);
	        if(day < 10) {
	        	sd = "0" + sd;
	        }
	      	
	        //建立月份和项目名文件夹
	      	String ym = rootPath+ sy + sm +"\\";
	      	String ympname = ym + pName + "\\";
	      	File directory1 =new File(ym);	//根目录是否存在
	 		if(!directory1.exists())
	 			directory1.mkdir();
	 		File directory2 =new File(ympname);	//根目录是否存在
	 		if(!directory2.exists())
	 			directory2.mkdir();
	        
	 		//拷贝temp中的文件到正确的文件夹
	 		String[] newFile = fileName.split("\\*");
			for(String copyFile : newFile) {
				cutGeneralFile(rootPath + "temp\\" + copyFile,ym + pName);
			}
	 		
			//存入数据库的内容
			
			if(fileName != null && fileName.length()> 0)
			{
				if (fileName.charAt(fileName.length()-1)=='*') 
					fileName = fileName.substring(0,fileName.length()-1);
			}
			
	      	String accessory = sy + sm + "*" + pName + "*" + fileName;
	      	//doc转pdf
	      	if(fileName.length() != 0)
	      	{
	      		String[] allFile = fileName.split("\\*");
	      		for(String temp : allFile) {
	      			if(temp.endsWith(".doc")) {
	      				WordToPdf.translateThread(ympname+temp, ympname+temp.substring(0,temp.length()-4)+".pdf"); 			
	      			} else if(temp.endsWith(".docx")) {
	      				WordToPdf.translateThread(ympname+temp, ympname+temp.substring(0,temp.length()-5)+".pdf");
	      			}
	      		}
	      	}      	
	      	return accessory;
		}

}
