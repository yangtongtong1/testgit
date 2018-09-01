package PSM.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipOutputStream;

import org.apache.tools.zip.ZipEntry;

import PSM.DAO.BasicInfoDAO;
import PSM.DAO.GoalDutyDAO;
import PSM.DAO.MissionDAO;
import PSM.Tool.ExcelToPdf;
import PSM.Tool.WordToPdf;
import hibernate.Fenbao;
import hibernate.Flownode;
import hibernate.Kaoheresult;
import hibernate.Koufen;
import hibernate.Koufentongji;
import hibernate.Person;
import hibernate.Persondb;
import hibernate.Project;
import hibernate.Projectmanagement;
import hibernate.Projectperson;
import hibernate.Saftyproblem;

public class BasicInfoService {
	private GoalDutyDAO goalDutyDAO;

	public GoalDutyDAO getGoalDutyDAO() {
		return goalDutyDAO;
	}

	public void setGoalDutyDAO(GoalDutyDAO goalDutyDAO) {
		this.goalDutyDAO = goalDutyDAO;
	}

	private BasicInfoDAO basicInfoDAO;

	public BasicInfoDAO getBasicInfoDAO() {
		return basicInfoDAO;
	}

	public void setBasicInfoDAO(BasicInfoDAO basicInfoDAO) {
		this.basicInfoDAO = basicInfoDAO;
	}
	
	private MissionDAO missionDAO;

	public MissionDAO getMissionDAO() {
		return missionDAO;
	}

	public void setMissionDAO(MissionDAO missionDAO) {
		this.missionDAO = missionDAO;
	}
	
	private SaftyCheckService saftyCheckService;
	
	public SaftyCheckService getSaftyCheckService() {
		return saftyCheckService;
	}

	public void setSaftyCheckService(SaftyCheckService saftyCheckService) {
		this.saftyCheckService = saftyCheckService;
	}
	

	public String getProjectList(String findstr, String projectNo, int start, int limit) {

		List<Project> list = basicInfoDAO.getProjectList(findstr, projectNo, start, limit);
		int total = basicInfoDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Project p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"No\":\"" + p.getNo() + "\",\"Name\":\"" + p.getName()
					+ "\",\"Scale\":\"" + p.getScale() + "\",\"BuildUnit\":\"" + p.getBuildUnit() + "\"";
			jsonStr += ",\"Manager\":\"" + p.getManager() + "\",\"Cost\":\"" + p.getCost() + "\",\"FileTime\":\""
					+ p.getFileTime() + "\",\"StartTime\":\"" + p.getStartTime() + "\"";
			jsonStr += ",\"Place\":\"" + p.getPlace() + "\",\"BuildContent\":\"" + p.getBuildContent()
					+ "\",\"Progress\":\"" + p.getProgress() + "\",\"Accessory\":\"" + p.getAccessory() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addProject(Project p, String fileName, String rootPath) {
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

		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}
		// 存入数据库的内容
		String accessory = sy + sm + "*" + p.getName() + "*" + fileName;

		// 打包
		/*
		 * byte[] buffer = new byte[1024]; if(fileName.length()>0) { String
		 * strZipName = ympname + sy + sm + sd + p.getName()+".zip";
		 * System.out.println("压缩包名称:"+strZipName); try { ZipOutputStream out =
		 * new ZipOutputStream(new FileOutputStream(strZipName)); String[]
		 * allfile = fileName.split("\\*");
		 * System.out.println(Arrays.toString(allfile)); for(int i
		 * =0;i<allfile.length;i++) { File tempfile = new
		 * File(ympname+allfile[i]); if(tempfile.exists()) { FileInputStream fis
		 * = new FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while((len =
		 * fis.read(buffer))>0) { out.write(buffer,0,len); } out.closeEntry();
		 * fis.close(); } } accessory = accessory+sy+sm+sd+p.getName()+".zip";
		 * out.close(); } catch (Exception e) { e.printStackTrace(); } }
		 */

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
		basicInfoDAO.insertProject(p);
		return json;
	}

	public String editProject(Project p, String fileName, String rootPath) {
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
			deleteFile(rootPath + oldFoldName + "\\" + oldFile[oldFile.length - 1]);
		}
		// ----------打包------------------//
		byte[] buffer = new byte[1024];
		if (fileName.length() > 0) {
			String strZipName = rootPath + oldFoldName + "\\" + sy + sm + sd + p.getName() + ".zip";
			try {
				ZipOutputStream out = new ZipOutputStream(new FileOutputStream(strZipName));
				String[] allfile = fileName.split("\\*");
				for (int i = 0; i < allfile.length; i++) {
					File tempfile = new File(rootPath + oldFoldName + "\\" + allfile[i]);
					if (tempfile.exists()) {
						FileInputStream fis = new FileInputStream(tempfile);
						out.putNextEntry(new ZipEntry(tempfile.getName()));
						int len;
						while ((len = fis.read(buffer)) > 0) {
							out.write(buffer, 0, len);
						}
						out.closeEntry();
						fis.close();
					}
				}
				newAccessory = newAccessory + sy + sm + sd + p.getName() + ".zip";
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

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

		basicInfoDAO.updateProject(p);
		return json;
	}

	public String deleteProject(String ID, String rootPath) {
		Project pro = new Project();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			List<Project> list = basicInfoDAO.checkProjectID((Integer.parseInt(temp[i])));
			// 删除附件
			if (list != null) {
				Project p = list.get(0);
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
			basicInfoDAO.deleteProject(pro);
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
			List<Project> list = basicInfoDAO.checkProjectID((Integer.parseInt(ppID)));
			if (list.size() > 0) {
				Project p = list.get(0);
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

	// -----------------------------
	// --------------------
	public String getProjectpersonList(String findstr, int start, int limit, String projectName) {

		List<Flownode> list = basicInfoDAO.getProjectpersonList(findstr, start, limit, projectName);
		int total = basicInfoDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Flownode p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"Job\":\"" + p.getNodeName() + "\",\"Name\":\"" + p.getName()
					+ "\",\"Duty\":\"" + p.getDuty() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addProjectperson(Projectperson p) {
		String json = "{\"success\":\"true\"}";
		basicInfoDAO.insertProjectperson(p);
		return json;
	}

	public String editProjectperson(Projectperson p) {

		String json = "{\"success\":\"true\"}";
		basicInfoDAO.updateProjectperson(p);
		return json;
	}

	public String deleteProjectperson(String ID) {
		Projectperson pro = new Projectperson();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			pro.setId(Integer.parseInt(temp[i]));
			basicInfoDAO.deleteProjectperson(pro);
		}
		return json;
	}

	public String getfbList(String findstr, String type, int start, int limit,String projectName) {

		List<Fenbao> list = basicInfoDAO.getfbList(findstr, type, start, limit, projectName);
		int total = basicInfoDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Fenbao p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"ProRange\":\"" + p.getProRange() + "\",\"Cost\":\""
					+ p.getCost() + "\",\"Head\":\"" + p.getHead() + "\",\"Rank\":\"" + p.getRank() + "\",\"Name\":\""
					+ p.getName() + "\",\"ProSavePeople\":\"" + p.getProSavePeople() + "\"";
			jsonStr += ",\"TechHead\":\"" + p.getTechHead() + "\",\"ProHead\":\"" + p.getProHead()
					+ "\",\"ProTechHead\":\"" + p.getProTechHead() + "\",\"ProSaveHead\":\"" + p.getProSaveHead()
					+ "\",\"Project\":\"" + p.getProject() + "\",\"Accessory\":\"" + p.getAccessory() + "\",\"Type\":\"" + p.getType() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addfb(Fenbao p, String fileName, String rootPath) {
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
		String ympname = ym + p.getHead() + "\\";
		File directory1 = new File(ym); // 根目录是否存在
		if (!directory1.exists())
			directory1.mkdir();
		File directory2 = new File(ympname); // 根目录是否存在
		if (!directory2.exists())
			directory2.mkdir();

		// 拷贝temp中的文件到正确的文件夹
		String[] newFile = fileName.split("\\*");
		for (String copyFile : newFile) {
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym + p.getHead());
		}

		// 存入数据库的内容
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}

		String accessory = sy + sm + sd +"*" + p.getHead() + "*" + fileName;

		// 打包
		/*
		 * byte[] buffer = new byte[1024]; if(fileName.length()>0) { String
		 * strZipName = ympname + sy + sm + sd + p.getHead()+".zip";
		 * System.out.println("压缩包名称:"+strZipName); try { ZipOutputStream out =
		 * new ZipOutputStream(new FileOutputStream(strZipName)); String[]
		 * allfile = fileName.split("\\*");
		 * System.out.println(Arrays.toString(allfile)); for(int i
		 * =0;i<allfile.length;i++) { File tempfile = new
		 * File(ympname+allfile[i]); if(tempfile.exists()) { FileInputStream fis
		 * = new FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while((len =
		 * fis.read(buffer))>0) { out.write(buffer,0,len); } out.closeEntry();
		 * fis.close(); } } accessory = accessory+sy+sm+sd+p.getHead()+".zip";
		 * out.close(); } catch (Exception e) { e.printStackTrace(); } }
		 */

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
		basicInfoDAO.insertfb(p);
		return json;
	}

	public String editfb(Fenbao p, String fileName, String rootPath) {
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
			String ympname = ym + p.getHead() + "\\";
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
			oldFoldName = sy + sm + "\\" + p.getHead();
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			newAccessory = sy + sm + sd +"*" + p.getHead() + "*" + fileName;
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
			// deleteFile(rootPath + oldFoldName
			// +"\\"+oldFile[oldFile.length-1]);
		}
		// ----------打包------------------//
		/*
		 * byte[] buffer = new byte[1024]; if(fileName.length()>0) { String
		 * strZipName = rootPath+oldFoldName +"\\"+ sy+sm+sd+p.getHead()+".zip";
		 * try { ZipOutputStream out = new ZipOutputStream(new
		 * FileOutputStream(strZipName)); String[] allfile =
		 * fileName.split("\\*"); for(int i =0;i<allfile.length;i++) { File
		 * tempfile = new File(rootPath+ oldFoldName +"\\"+ allfile[i]);
		 * if(tempfile.exists()) { FileInputStream fis = new
		 * FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while((len =
		 * fis.read(buffer))>0) { out.write(buffer,0,len); } out.closeEntry();
		 * fis.close(); } } newAccessory =
		 * newAccessory+sy+sm+sd+p.getHead()+".zip"; out.close(); } catch
		 * (Exception e) { e.printStackTrace(); } }
		 */

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
			p.setAccessory(fileName);
		} else {
			p.setAccessory(newAccessory);
		}

		basicInfoDAO.updatefb(p);
		return json;
	}

	public String deletefb(String ID, String rootPath) {
		Fenbao pro = new Fenbao();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			List<Fenbao> list = basicInfoDAO.checkfbID((Integer.parseInt(temp[i])));
			// 删除附件
			if (list != null) {
				Fenbao p = list.get(0);
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
			basicInfoDAO.deletefb(pro);
		}
		return json;
	}

	public String getPersonList(String findstr, int start, int limit) {

		List<Person> list = basicInfoDAO.getPersonList(findstr, start, limit);
		int total = basicInfoDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Person p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"Name\":\"" + p.getName() + "\",\"UnitName\":\""
					+ p.getUnitName() + "\",\"Job\":\"" + p.getJob() + "\",\"IdentityNo\":\"" + p.getIdentityNo()
					+ "\",\"PhoneNo\":\"" + p.getPhoneNo() + "\",\"Type\":\"" + p.getType() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addPerson(Person p) {
		String json = "{\"success\":\"true\"}";
		basicInfoDAO.insertPerson(p);
		return json;
	}

	public String editPerson(Person p) {

		String json = "{\"success\":\"true\"}";
		basicInfoDAO.updatePerson(p);
		return json;
	}

	public String deletePerson(String ID) {
		Person pro = new Person();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			pro.setId(Integer.parseInt(temp[i]));
			basicInfoDAO.deletePerson(pro);
		}
		return json;
	}

	// jianglingfeng
	// --------------------------

	public String getProjectmanagementList(String findstr, int start, int limit, String projectName) {

		List<Projectmanagement> list = basicInfoDAO.getProjectmanagementList(findstr, start, limit, projectName);
		int total = basicInfoDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Projectmanagement p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"Name\":\"" + p.getName() + "\",\"No\":\"" + p.getNo()
					+ "\",\"Scale\":\"" + p.getScale() + "\"" + ",\"BuildUnit\":\"" + p.getBuildUnit()
					+ "\",\"Place\":\"" + p.getPlace() + "\",\"Price\":\"" + p.getPrice() + "\",\"Manager\":\""
					+ p.getManager() + "\"" + ",\"StartDate\":\"" + p.getStartDate() + "\",\"Schedule\":\""
					+ p.getSchedule() + "\",\"Content\":\"" + p.getContent() + "\",\"Cost\":\"" + p.getCost()
					+ "\",\"Progress\":\"" + p.getProgress() + "\"}";

		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addProjectmanagement(Projectmanagement p, String fileName, String rootPath) {
		String json = "{\"success\":\"true\"}";

		basicInfoDAO.insertProjectmanagement(p);
		String projectName = p.getName();
		
		// 规定动作完成表初始化
		basicInfoDAO.initProjectmanagement(projectName);
		
		// 安全生产行政管理体系
		addXingZheng(projectName);

		// 安全生产技术支撑体系
		addJiShu(projectName);

		// 安全生产监督体系
		addJianDu(projectName);

		// 安全生产实施体系
		addShi(projectName);
		
		return json;
	}

	// 初始化安全生产行政管理体系
	private void addXingZheng(String projectName) {
		Flownode[] nodexz = new Flownode[11];
		for (int i = 0; i < nodexz.length; i++) {
			nodexz[i] = new Flownode();
			nodexz[i].setTableId("97");
			nodexz[i].setNodeY(60);
			nodexz[i].setProjectName(projectName);
			nodexz[i].setAccessory("");
			nodexz[i].setName("姓名");
		}

		nodexz[0].setNodeName("项目经理");
		nodexz[0].setStepNum(1);
		nodexz[0].setOrderNum(1);
		nodexz[0].setNodeX(443);

		nodexz[1].setNodeName("设计经理");
		nodexz[1].setStepNum(2);
		nodexz[1].setOrderNum(1);
		nodexz[1].setNodeX(86);

		nodexz[2].setNodeName("采购（物资）经理");
		nodexz[2].setStepNum(2);
		nodexz[2].setOrderNum(2);
		nodexz[2].setNodeX(229);

		nodexz[3].setNodeName("副经理/施工经理");
		nodexz[3].setStepNum(2);
		nodexz[3].setOrderNum(3);
		nodexz[3].setNodeX(372);

		nodexz[4].setNodeName("调试经理");
		nodexz[4].setStepNum(2);
		nodexz[4].setOrderNum(4);
		nodexz[4].setNodeX(514);

		nodexz[5].setNodeName("项目总工");
		nodexz[5].setStepNum(2);
		nodexz[5].setOrderNum(5);
		nodexz[5].setNodeX(657);

		nodexz[6].setNodeName("安全总监");
		nodexz[6].setStepNum(2);
		nodexz[6].setOrderNum(6);
		nodexz[6].setNodeX(800);

		nodexz[7].setNodeName("设计分包项目经理");
		nodexz[7].setStepNum(3);
		nodexz[7].setOrderNum(1);
		nodexz[7].setNodeX(143);

		nodexz[8].setNodeName("土建分包项目经理");
		nodexz[8].setStepNum(3);
		nodexz[8].setOrderNum(2);
		nodexz[8].setNodeX(343);

		nodexz[9].setNodeName("安装分包项目经理");
		nodexz[9].setStepNum(3);
		nodexz[9].setOrderNum(3);
		nodexz[9].setNodeX(543);

		nodexz[10].setNodeName("调试分包项目经理");
		nodexz[10].setStepNum(3);
		nodexz[10].setOrderNum(4);
		nodexz[10].setNodeX(743);

		for (Flownode node : nodexz)
			goalDutyDAO.insertFlowNode(node);
	}

	// 初始化安全生产技术支撑体系
	private void addJiShu(String projectName) {
		Flownode[] nodejs = new Flownode[5];
		for (int i = 0; i < nodejs.length; i++) {
			nodejs[i] = new Flownode();
			nodejs[i].setTableId("98");
			nodejs[i].setNodeY(60);
			nodejs[i].setProjectName(projectName);
			nodejs[i].setName("姓名");
			nodejs[i].setDuty("");
			nodejs[i].setPhone("");
			nodejs[i].setAccessory("");
		}

		nodejs[0].setNodeName("项目总工");
		nodejs[0].setStepNum(1);
		nodejs[0].setOrderNum(1);
		nodejs[0].setNodeX(450);

		nodejs[1].setNodeName("设计总工程师");
		nodejs[1].setStepNum(2);
		nodejs[1].setOrderNum(1);
		nodejs[1].setNodeX(150);

		nodejs[2].setNodeName("土建分包项目部技术负责人");
		nodejs[2].setStepNum(2);
		nodejs[2].setOrderNum(2);
		nodejs[2].setNodeX(350);

		nodejs[3].setNodeName("安装分包项目部技术负责人");
		nodejs[3].setStepNum(2);
		nodejs[3].setOrderNum(3);
		nodejs[3].setNodeX(550);

		nodejs[4].setNodeName("调试分包项目部技术负责人");
		nodejs[4].setStepNum(2);
		nodejs[4].setOrderNum(4);
		nodejs[4].setNodeX(750);

		for (Flownode node : nodejs)
			goalDutyDAO.insertFlowNode(node);
	}

	// 初始化安全生产监督体系
	private void addJianDu(String projectName) {
		Flownode[] nodejs = new Flownode[7];
		for (int i = 0; i < nodejs.length; i++) {
			nodejs[i] = new Flownode();
			nodejs[i].setTableId("99");
			nodejs[i].setNodeY(60);
			nodejs[i].setProjectName(projectName);
			nodejs[i].setName("姓名");
			nodejs[i].setDuty("");
			nodejs[i].setPhone("");
			nodejs[i].setAccessory("");
		}

		nodejs[0].setNodeName("安全总监");
		nodejs[0].setStepNum(1);
		nodejs[0].setOrderNum(1);
		nodejs[0].setNodeX(443);

		nodejs[1].setNodeName("土建分包项目部安全负责人");
		nodejs[1].setStepNum(2);
		nodejs[1].setOrderNum(1);
		nodejs[1].setNodeX(193);

		nodejs[2].setNodeName("安装分包项目部安全负责人");
		nodejs[2].setStepNum(2);
		nodejs[2].setOrderNum(2);
		nodejs[2].setNodeX(443);

		nodejs[3].setNodeName("安装分包项目部安全负责人");
		nodejs[3].setStepNum(2);
		nodejs[3].setOrderNum(3);
		nodejs[3].setNodeX(693);

		nodejs[4].setNodeName("土建分包项目部班组安全员");
		nodejs[4].setStepNum(3);
		nodejs[4].setOrderNum(1);
		nodejs[4].setNodeX(193);

		nodejs[5].setNodeName("安装分包项目部班组安全员");
		nodejs[5].setStepNum(3);
		nodejs[5].setOrderNum(2);
		nodejs[5].setNodeX(443);

		nodejs[6].setNodeName("安装分包项目部班组安全员");
		nodejs[6].setStepNum(3);
		nodejs[6].setOrderNum(3);
		nodejs[6].setNodeX(693);

		for (Flownode node : nodejs)
			goalDutyDAO.insertFlowNode(node);
	}

	// 初始化安全生产实施体系
	private void addShi(String projectName) {
		Flownode[] nodejs = new Flownode[9];
		for (int i = 0; i < nodejs.length; i++) {
			nodejs[i] = new Flownode();
			nodejs[i].setTableId("100");
			nodejs[i].setNodeY(60);
			nodejs[i].setProjectName(projectName);
			nodejs[i].setName("姓名");
			nodejs[i].setDuty("");
			nodejs[i].setPhone("");
			nodejs[i].setAccessory("");
		}

		nodejs[0].setNodeName("项目管生产经理");
		nodejs[0].setStepNum(1);
		nodejs[0].setOrderNum(1);
		nodejs[0].setNodeX(443);

		nodejs[1].setNodeName("设计经理");
		nodejs[1].setStepNum(2);
		nodejs[1].setOrderNum(1);
		nodejs[1].setNodeX(143);

		nodejs[2].setNodeName("物资经理");
		nodejs[2].setStepNum(2);
		nodejs[2].setOrderNum(2);
		nodejs[2].setNodeX(343);

		nodejs[3].setNodeName("施工经理");
		nodejs[3].setStepNum(2);
		nodejs[3].setOrderNum(3);
		nodejs[3].setNodeX(543);

		nodejs[4].setNodeName("调试经理");
		nodejs[4].setStepNum(2);
		nodejs[4].setOrderNum(4);
		nodejs[4].setNodeX(743);

		nodejs[5].setNodeName("设计分包项目经理");
		nodejs[5].setStepNum(3);
		nodejs[5].setOrderNum(1);
		nodejs[5].setNodeX(143);

		nodejs[6].setNodeName("土建分包项目经理");
		nodejs[6].setStepNum(3);
		nodejs[6].setOrderNum(2);
		nodejs[6].setNodeX(343);

		nodejs[7].setNodeName("安装分包项目经理");
		nodejs[7].setStepNum(3);
		nodejs[7].setOrderNum(3);
		nodejs[7].setNodeX(543);

		nodejs[8].setNodeName("调试分包项目经理");
		nodejs[8].setStepNum(3);
		nodejs[8].setOrderNum(4);
		nodejs[8].setNodeX(743);

		for (Flownode node : nodejs)
			goalDutyDAO.insertFlowNode(node);
	}

	public String editProjectmanagement(Projectmanagement p, String fileName, String rootPath, String prevName) {
		String json = "{\"success\":\"true\"}";
		basicInfoDAO.updateProjectmanagement(p);
		String projectName = p.getName();
		goalDutyDAO.updateFlowNodeByProjectName(projectName, prevName);
		missionDAO.updateMissionstateByProjectName(projectName, prevName);
		basicInfoDAO.updateKoufentongjiByProjectName(projectName, prevName);
		return json;
	}

	public String deleteProjectmanagement(String ID, String rootPath) {
		Projectmanagement pro = new Projectmanagement();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		
		List<String> projectNameList = new ArrayList<>();
		
		for (int i = 0; i < temp.length; i++) {
			List<Projectmanagement> list = basicInfoDAO.checkProjectmanagementID((Integer.parseInt(temp[i])));
			for(int j=0;j<list.size();j++) {
				projectNameList.add(list.get(j).getName());
			}
			pro.setId(Integer.parseInt(temp[i]));
			basicInfoDAO.deleteProjectmanagement(pro);
		}
		
		for(int i = 0;i<projectNameList.size();i++) {
			goalDutyDAO.deleteFlowNodeByProjectName(projectNameList.get(i));
			missionDAO.deleteMissionstateByProjectName(projectNameList.get(i));
			basicInfoDAO.deleteKoufentongjiByProjectName(projectNameList.get(i));
		}
		
		
		return json;
	}
	// ---------end--------//

	public String getPersondbList(String findstr, int start, int limit) {

		List<Persondb> list = basicInfoDAO.getPersondbList(findstr, start, limit);
		int total = basicInfoDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Persondb p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"PType\":\"" + p.getPtype() + "\",\"Name\":\"" + p.getName()
					+ "\",\"PhoneUrgent\":\"" + p.getPhoneUrgent() + "\",\"PapersType\":\"" + p.getPapersType()
					+ "\",\"PapersNoTwo\":\"" + p.getPapersNoTwo() + "\",\"PapersDateTwo\":\"" + p.getPapersDateTwo()
					+ "\"" + ",\"Sex\":\"" + p.getSex() + "\",\"IDCard\":\"" + p.getIdcard() + "\",\"Birthday\":\""
					+ p.getBirthday() + "\",\"Phone\":\"" + p.getPhone() + "\",\"PapersNo\":\"" + p.getPapersNo()
					+ "\",\"PapersDate\":\"" + p.getPapersDate() + "\",\"PapersTypeTwo\":\"" + p.getPapersTypeTwo()
					+ "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String getPersondbListReflash(String findstr, int start, int limit) {

		List<Persondb> list = basicInfoDAO.getPersondbListReflash(findstr, start, limit);
		int total = basicInfoDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Persondb p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"PType\":\"" + p.getPtype() + "\",\"Name\":\"" + p.getName()
					+ "\",\"PhoneUrgent\":\"" + p.getPhoneUrgent() + "\",\"PapersType\":\"" + p.getPapersType()
					+ "\",\"PapersNoTwo\":\"" + p.getPapersNoTwo() + "\",\"PapersDateTwo\":\"" + p.getPapersDateTwo()
					+ "\"" + ",\"Sex\":\"" + p.getSex() + "\",\"IDCard\":\"" + p.getIdcard() + "\",\"Birthday\":\""
					+ p.getBirthday() + "\",\"Phone\":\"" + p.getPhone() + "\",\"PapersNo\":\"" + p.getPapersNo()
					+ "\",\"PapersDate\":\"" + p.getPapersDate() + "\",\"PapersTypeTwo\":\"" + p.getPapersTypeTwo()
					+ "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}
	
	public String getPersondbListReflash(String projectName, String findStr, int start, int limit) {

		List<Persondb> list = basicInfoDAO.getPersondbListReflash(projectName, findStr, start, limit);
		int total = list.size();
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Persondb p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"PType\":\"" + p.getPtype() + "\",\"Name\":\"" + p.getName()
					+ "\",\"PhoneUrgent\":\"" + p.getPhoneUrgent() + "\",\"PapersType\":\"" + p.getPapersType()
					+ "\",\"PapersNoTwo\":\"" + p.getPapersNoTwo() + "\",\"PapersDateTwo\":\"" + p.getPapersDateTwo()
					+ "\"" + ",\"Sex\":\"" + p.getSex() + "\",\"IDCard\":\"" + p.getIdcard() + "\",\"Birthday\":\""
					+ p.getBirthday() + "\",\"Phone\":\"" + p.getPhone() + "\",\"PapersNo\":\"" + p.getPapersNo()
					+ "\",\"PapersDate\":\"" + p.getPapersDate() + "\",\"PapersTypeTwo\":\"" + p.getPapersTypeTwo()
					+ "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addPersondb(Persondb p) {
		String json = "{\"success\":\"true\"}";
		basicInfoDAO.insertPersondb(p);
		return json;
	}

	public String editPersondb(Persondb p) {

		String json = "{\"success\":\"true\"}";
		basicInfoDAO.updatePersondb(p);
		return json;
	}

	public String deletePersondb(String ID) {
		Persondb pro = new Persondb();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			pro.setId(Integer.parseInt(temp[i]));
			basicInfoDAO.deletePersondb(pro);
		}
		return json;
	}

	public String getProjectNameList(String findstr, int start, int limit) {

		List<Projectmanagement> list = basicInfoDAO.getProjectmanagementList(findstr, start, limit, "");
		int total = basicInfoDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Projectmanagement p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"Name\":\"" + p.getName() + "\",\"No\":\"" + p.getNo()
					+ "\",\"Manager\":\"" + p.getManager() + "\",\"Progress\":\"" + p.getProgress() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String getPersondbProjectName(String name, int start, int limit) {

		List<String> namelist = basicInfoDAO.getPersondbProjectName(name);
		List<Projectmanagement> list = new ArrayList<>();
		for (int i = 0; i < namelist.size(); i++) {
			String projectName = namelist.get(i);
			Projectmanagement project = basicInfoDAO.getPersondbProjectmanagementList(projectName);
			list.add(project);
		}

		int total = list.size();
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Projectmanagement p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"Name\":\"" + p.getName() + "\",\"No\":\"" + p.getNo()
					+ "\",\"Manager\":\"" + p.getManager() + "\",\"Progress\":\"" + p.getProgress() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}
	

	public String getXMJLNameList() {
		List list = basicInfoDAO.getXMJLNameList();
		String jsonStr = "[";
		for (int i = 0; i < list.size(); i++) {
			if (i > 0)
				jsonStr += ",";
			String temp = "";
			temp += list.get(i);
			jsonStr += "{\"Manager\":\"" + temp + "\"}";
		}
		jsonStr += "]";
		return jsonStr;
	}

	public String getManagerNameList() {
		List list = basicInfoDAO.getManagerNameList();
		String jsonStr = "[";
		for (int i = 0; i < list.size(); i++) {
			if (i > 0)
				jsonStr += ",";
			String temp = "";
			temp += list.get(i);
			jsonStr += "{\"Manager\":\"" + temp + "\"}";
		}
		jsonStr += "]";
		return jsonStr;
	}

	// *******************saftyproblem******************
	public String getSaftyproblemList(String findstr, int start, int limit) {

		List<Saftyproblem> list = basicInfoDAO.getSaftyproblemList(findstr, start, limit);
		int total = basicInfoDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";

		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Saftyproblem s = list.get(i);
			jsonStr += "{\"ID\":\"" + s.getId() + "\",\"kind\":\"" + s.getKind() + "\",\"subkind\":\"" + s.getSubkind()
					+ "\",\"showkind\":\"" + s.getShowkind() + "\",\"score\":\"" + s.getScore() + "\",\"Accessory\":\""
					+ s.getAccessory() + "\"}";

			System.out.println(jsonStr);
		}
		jsonStr += "]}";
		return jsonStr;
	}

	public String addSaftyproblem(Saftyproblem s, String fileName, String rootPath) {
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

		basicInfoDAO.insertSaftyproblem(s);
		return json;
	}

	public String editSaftyproblem(Saftyproblem s, String fileName, String rootPath) {
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

		basicInfoDAO.updateSaftyproblem(s);

		// return editSaftycheck2(s, fileName2, rootPath);
		return json;
	}

	public String addSaftyproblem(Saftyproblem s) {
		String json = "{\"success\":\"true\"}";
		basicInfoDAO.insertSaftyproblem(s);
		return json;
	}

	public String updateSaftyproblem(Saftyproblem s) {
		String json = "{\"success\":\"true\"}";
		basicInfoDAO.updateSaftyproblem(s);
		return json;
	}

	public String deleteSaftyproblem(String ID) {
		Saftyproblem saftyproblem = new Saftyproblem();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			saftyproblem.setId(Integer.parseInt(temp[i]));
			basicInfoDAO.deleteSaftyproblem(saftyproblem);
		}
		return json;
	}

	// *******************Koufen******************
	public String getKoufenList(String findstr, int start, int limit) {

		List<Koufen> list = basicInfoDAO.getKoufenList(findstr, start, limit);
		int total = basicInfoDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";

		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Koufen s = list.get(i);
			jsonStr += "{\"ID\":\"" + s.getId() + "\",\"koufenx\":\"" + s.getKoufenx() + "\",\"koufenzhi\":\""
					+ s.getKoufenzhi() + "\",\"koufenzq\":\"" + s.getKoufenzq() + "\",\"kaoheqidian\":\""
					+ s.getKaoheqidian() + "\",\"duiykub\":\"" + s.getDuiykub() + "\",\"duiyzid\":\"" + s.getDuiyzid()
					+ "\",\"baohfile\":\"" + s.getBaohfile() + "\",\"Accessory\":\"" + s.getAccessory() + "\"}";

			System.out.println(jsonStr);
		}
		jsonStr += "]}";
		return jsonStr;
	}

	public String addKoufen(Koufen s, String fileName, String rootPath) {
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

		basicInfoDAO.insertKoufen(s);
		return json;
	}

	public String editKoufen(Koufen s, String fileName, String rootPath) {
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

		basicInfoDAO.updateKoufen(s);

		// return editSaftycheck2(s, fileName2, rootPath);
		return json;
	}

	public String addKoufen(Koufen s) {
		String json = "{\"success\":\"true\"}";
		basicInfoDAO.insertKoufen(s);
		return json;
	}

	public String updateKoufen(Koufen s) {
		String json = "{\"success\":\"true\"}";
		basicInfoDAO.updateKoufen(s);
		return json;
	}

	public String deleteKoufen(String ID) {
		Koufen koufen = new Koufen();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			koufen.setId(Integer.parseInt(temp[i]));
			basicInfoDAO.deleteKoufen(koufen);
		}
		return json;
	}

	// Koufentongji表的服务********************************
	public String getKoufentongjiList(String findstr, int start, int limit, String date,String startDate,String endDate,List<String> projectNameList) {
		List<Koufentongji> list = basicInfoDAO.getKoufentongjiList(findstr, start, limit, projectNameList);
		int total = basicInfoDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Koufentongji s = list.get(i);
			s = checkKoufen(s, start, limit, date,startDate,endDate);
			jsonStr += "{\"ID\":\"" + s.getId() + "\",\"proname\":\"" + s.getProname() + "\",\"koufenitem\":\""
					+ s.getKoufenitem().substring(0, s.getKoufenitem().length()-1) + "\",\"koufenzhi\":\"" + s.getKoufenzhi() + "\",\"zongfen\":\""
					+ s.getZongfen() + "\"}";
			//basicInfoDAO.updateKoufentongji(s);
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}
	
	public Koufentongji checkKoufen(Koufentongji tj, int start, int limit,String date,String startDate,String endDate) {
		List<Koufen> list = basicInfoDAO.getKoufenList("", 0, 50);
		StringBuilder item = new StringBuilder();
		int decendScore = 0;
		String projectName = tj.getProname();
		for(int i=0;i<list.size();i++) {
			Koufen kf = list.get(i);
			if( kf.getKoufenzq().equals("整个项目")) {
				String table = kf.getDuiykub();
				String field = kf.getDuiyzid();
				String word = kf.getBaohfile();
				int score = Integer.parseInt(kf.getKoufenzhi().substring(0, 1));
				if(!basicInfoDAO.checkTable(table, field, word, projectName)) {
					item.append(kf.getKoufenx());
					item.append("  扣");
					item.append(kf.getKoufenzhi().substring(0, 2));
					item.append(", ");
					decendScore +=score;
				}
			}
			else if(kf.getKoufenzq().equals("月度") ) {
				String table = kf.getDuiykub();
				String field = kf.getDuiyzid();
				String word = kf.getBaohfile();
				int score = Integer.parseInt(kf.getKoufenzhi().substring(0, 1));
				if(!basicInfoDAO.checkTable(table, field, date, projectName) || !basicInfoDAO.checkTable(table, field, word, projectName)) {
					item.append(kf.getKoufenx());
					item.append("  扣");
					item.append(kf.getKoufenzhi().substring(0, 2));
					item.append(", ");
					decendScore +=score;
				}
			}
			else if(kf.getKoufenzq().equals("年度")) {
				String table = kf.getDuiykub();
				String field = kf.getDuiyzid();
				String word = kf.getBaohfile();
				int score = Integer.parseInt(kf.getKoufenzhi().substring(0, 1));
				date = date.substring(0, 4);
				if(!basicInfoDAO.checkTable(table, field, date, projectName) || !basicInfoDAO.checkTable(table, field, word, projectName)) {
					item.append(kf.getKoufenx());
					item.append("  扣");
					item.append(kf.getKoufenzhi().substring(0, 2));
					item.append(", ");
					decendScore +=score;
				}
			}
			else if(kf.getKaoheqidian().equals("安全检查")) {
				//String table = kf.getDuiykub();
				int score = Integer.parseInt(kf.getKoufenzhi().substring(0, 1));
				int avgNum = saftyCheckService.getAvgProNums("", "", start, limit, projectName, startDate, endDate);
				if(avgNum > 0) {
					item.append(kf.getKoufenx());
					item.append("  扣");
					item.append(kf.getKoufenzhi().substring(0, 2));
					item.append(", ");
					decendScore +=score*avgNum;
				}
			}
			
			
			else if(kf.getKoufenzq().equals("月/节点")) {
				String table = kf.getDuiykub();
				String field = kf.getDuiyzid();
				String word = kf.getBaohfile();
				int score = Integer.parseInt(kf.getKoufenzhi().substring(0, 1));
				if(!basicInfoDAO.checkTableByPoint(table, field, word, projectName,startDate,endDate)) {
					item.append(kf.getKoufenx());
					item.append("  扣");
					item.append(kf.getKoufenzhi().substring(0,2));
					item.append(", ");
					decendScore +=score;
				}
			}
		}
		item.deleteCharAt(item.length()-1);
		tj.setKoufenitem(item.toString());
		tj.setKoufenzhi(decendScore);
		tj.setZongfen(tj.getZongfen() - decendScore);
		return tj;
	}	

	public String addKoufentongji(Koufentongji s) {
		String json = "{\"success\":\"true\"}";
		basicInfoDAO.insertKoufentongji(s);
		return json;
	}

	public String updatekoufentongji(Koufentongji s) {
		String json = "{\"success\":\"true\"}";
		basicInfoDAO.updateKoufentongji(s);
		return json;
	}

	public String editkoufentongji(Koufentongji s) {
		String json = "{\"success\":\"true\"}";
		basicInfoDAO.updateKoufentongji(s);
		return json;
	}

	public String deleteKoufentongji(String id) {
		Koufentongji koufentongji = new Koufentongji();
		String json = "{\"success\":\"true\"}";
		String[] temp = id.split(",");
		for (int i = 0; i < temp.length; i++) {
			koufentongji.setId(Integer.parseInt(temp[i]));
			basicInfoDAO.deleteKoufentongji(koufentongji);
		}
		return json;
	}
	
	// *****************yangtong*********2017.9.25**************************
		// Kaoheresult
		public String getKaoheresultList(String findstr, int start, int limit, String projectName) {
			List<Kaoheresult> list = basicInfoDAO.getKaoheresultList(findstr, start, limit, projectName);
			int total = basicInfoDAO.datacount;
			String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";

			for (int i = 0; i < list.size(); ++i) {
				if (i > 0) {
					jsonStr += ",";
				}
				Kaoheresult s = list.get(i);
				jsonStr += "{\"ID\":\"" + s.getId() + "\",\"year\":\"" + s.getYear() + "\",\"month\":\"" + s.getMonth()
						+ "\",\"score\":\"" + s.getScore() + "\",\"reason\":\"" + s.getReason() + "\",\"ProjectName\":\""
						+ s.getProjectName() + "\",\"Accessory\":\"" + s.getAccessory() + "\"}";

				System.out.println(jsonStr);
			}
			jsonStr += "]}";
			return jsonStr;
		}

		public String addKaoheresult(Kaoheresult s, String fileName, String rootPath) {
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

			basicInfoDAO.insertKaoheresult(s);
			return json;
		}

		public String editKaoheresult(Kaoheresult s, String fileName, String rootPath) {
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

			basicInfoDAO.updateKaoheresult(s);

			// return Saftycheck2(s, fileName2, rootPath);
			return json;
		}

		public String addKaoheresult(Kaoheresult s) {
			String json = "{\"success\":\"true\"}";
			basicInfoDAO.insertKaoheresult(s);
			return json;
		}

		public String updateRisksafepg(Kaoheresult s) {
			String json = "{\"success\":\"true\"}";
			basicInfoDAO.updateKaoheresult(s);
			return json;
		}

		public String deleteKaoheresult(String ID) {
			Kaoheresult kaoheresult = new Kaoheresult();
			String json = "{\"success\":\"true\"}";
			String[] temp = ID.split(",");
			for (int i = 0; i < temp.length; i++) {
				kaoheresult.setId(Integer.parseInt(temp[i]));
				basicInfoDAO.deleteKaoheresult(kaoheresult);
			}
			return json;
		}
}
