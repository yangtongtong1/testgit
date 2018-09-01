package PSM.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import PSM.DAO.BasicInfoDAO;
import PSM.DAO.DayManageDAO;
import PSM.DAO.EduTrainDAO;
import PSM.DAO.GoalDutyDAO;
import PSM.DAO.SaftyCheckDAO;
import PSM.DAO.SaftyCostDAO;
import PSM.Tool.ExcelToPdf;
import PSM.Tool.JacobUtil;
import PSM.Tool.WordToPdf;
import hibernate.AttLog;
import hibernate.Daibanplanmade;
import hibernate.Daibanrecord;
import hibernate.Fingerprint;
import hibernate.Meeting;
import hibernate.Monitor;
import hibernate.Periodreport;
import hibernate.Projectmanagement;
import hibernate.Risksafepg;
import hibernate.Saftyworkrizhi;
import hibernate.UserInfo;
import hibernate.Zhiwenkaoqin;
import hibernate.Zhiwenuserinfo;


public class DayManageService {
	
	private EduTrainDAO eduTrainDAO;
	
	public EduTrainDAO getEduTrainDAO() {
		return eduTrainDAO;
	}

	public void setEduTrainDAO(EduTrainDAO eduTrainDAO) {
		this.eduTrainDAO = eduTrainDAO;
	}
	
	private DayManageDAO dayManageDAO;

	public DayManageDAO getDayManageDAO() {
		return dayManageDAO;
	}

	public void setDayManageDAO(DayManageDAO dayManageDAO) {
		this.dayManageDAO = dayManageDAO;
	}
	
	private BasicInfoDAO basicInfoDAO;

	public BasicInfoDAO getBasicInfoDAO() {
		return basicInfoDAO;
	}

	public void setBasicInfoDAO(BasicInfoDAO basicInfoDAO) {
		this.basicInfoDAO = basicInfoDAO;
	}
	
	private GoalDutyDAO goalDutyDAO;

	public GoalDutyDAO getGoalDutyDAO() {
		return goalDutyDAO;
	}

	public void setGoalDutyDAO(GoalDutyDAO goalDutyDAO) {
		this.goalDutyDAO = goalDutyDAO;
	}
	
	private SaftyCheckDAO saftyCheckDAO;

	public SaftyCheckDAO getSaftyCheckDAO() {
		return saftyCheckDAO;
	}

	public void setSaftyCheckDAO(SaftyCheckDAO saftyCheckDAO) {
		this.saftyCheckDAO = saftyCheckDAO;
	}
	
	private SaftyCostDAO saftyCostDAO;

	public SaftyCostDAO getSaftyCostDAO() {
		return saftyCostDAO;
	}

	public void setSaftyCostDAO(SaftyCostDAO saftyCostDAO) {
		this.saftyCostDAO = saftyCostDAO;
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
			List<Meeting> list = dayManageDAO.checkMeetingID((Integer.parseInt(ppID)));
			if (list.size() > 0) {
				Meeting p = list.get(0);
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

	public String getMeetingList(String findstr, String type, int start, int limit,String projectName) {

		List<Meeting> list = dayManageDAO.getMeetingList(findstr, type, start, limit,projectName);
		int total = dayManageDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Meeting p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"Time\":\"" + p.getTime() + "\",\"Place\":\"" + p.getPlace()
					+ "\",\"Topic\":\"" + p.getTopic() + "\",\"Host\":\"" + p.getHost() + "\"";
			jsonStr += ",\"Record\":\"" + p.getRecord() + "\",\"Participants\":\"" + p.getParticipants()
					+ "\",\"Accessory\":\"" + p.getAccessory() + "\",\"Type\":\"" + p.getType() + "\",\"ProjectName\":\"" + p.getProjectName() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addMeeting(Meeting p, String fileName, String rootPath) {
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
		String ympname = ym + p.getHost() + "\\";
		File directory1 = new File(ym); // 根目录是否存在
		if (!directory1.exists())
			directory1.mkdir();
		File directory2 = new File(ympname); // 根目录是否存在
		if (!directory2.exists())
			directory2.mkdir();

		// 拷贝temp中的文件到正确的文件夹
		String[] newFile = fileName.split("\\*");
		for (String copyFile : newFile) {
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym + p.getHost());
		}
		
		if(fileName != null && fileName.length()> 0)
		{
			if (fileName.charAt(fileName.length()-1)=='*') 
				fileName = fileName.substring(0,fileName.length()-1);
		}

		// 存入数据库的内容
		String accessory = sy + sm + "*" + p.getHost() + "*" + fileName;

		// 打包
		/*byte[] buffer = new byte[1024];
		if (fileName.length() > 0) {
			String strZipName = ympname + sy + sm + sd + p.getHost() + ".zip";
			System.out.println("压缩包名称:" + strZipName);
			try {
				ZipOutputStream out = new ZipOutputStream(new FileOutputStream(strZipName));
				String[] allfile = fileName.split("\\*");
				System.out.println(Arrays.toString(allfile));
				for (int i = 0; i < allfile.length; i++) {
					File tempfile = new File(ympname + allfile[i]);
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
				accessory = accessory + sy + sm + sd + p.getHost() + ".zip";
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/

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
		dayManageDAO.insertMeeting(p);
		return json;
	}

	public String editMeeting(Meeting p, String fileName, String rootPath) {
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
			String ympname = ym + p.getHost() + "\\";
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
			oldFoldName = sy + sm + "\\" + p.getHost();
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			newAccessory = sy + sm + "*" + p.getHost() + "*" + fileName;
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
			//deleteFile(rootPath + oldFoldName + "\\" + oldFile[oldFile.length - 1]);
		}
		// ----------打包------------------//
		/*byte[] buffer = new byte[1024];
		if (fileName.length() > 0) {
			String strZipName = rootPath + oldFoldName + "\\" + sy + sm + sd + p.getHost() + ".zip";
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
				newAccessory = newAccessory + sy + sm + sd + p.getHost() + ".zip";
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/
		
		newAccessory = newAccessory.substring(0, newAccessory.length()-1);

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

		dayManageDAO.updateMeeting(p);
		return json;
	}

	public String deleteMeeting(String ID, String rootPath) {
		Meeting pro = new Meeting();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			List<Meeting> list = dayManageDAO.checkMeetingID((Integer.parseInt(temp[i])));
			// 删除附件
			if (list != null) {
				Meeting p = list.get(0);
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
			dayManageDAO.deleteMeeting(pro);
		}
		return json;
	}

	// ***************Daibanplanmade***********
	public String getDaibanplanmadeList(String findstr, int start, int limit, String projectName) {

		List<Daibanplanmade> list = dayManageDAO.getDaibanplanmadeList(findstr, start, limit,projectName);
		int total = dayManageDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";

		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Daibanplanmade s = list.get(i);
			jsonStr += "{\"ID\":\"" + s.getId() + "\",\"holiday\":\"" + s.getHoliday() + "\",\"planname\":\""
					+ s.getPlanname() + "\",\"bianzhitime\":\"" + s.getBianzhitime() + "\",\"Accessory\":\""
					+ s.getAccessory() + "\",\"ProjectName\":\"" + s.getProjectName() + "\"}";

			System.out.println(jsonStr);
		}
		jsonStr += "]}";
		return jsonStr;
	}

	public String addDaibanplanmade(Daibanplanmade s, String fileName, String rootPath) {
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

		
		if(fileName != null && fileName.length()> 0)
		{
			if (fileName.charAt(fileName.length()-1)=='*') 
				fileName = fileName.substring(0,fileName.length()-1);
		}
		// 存入数据库的内容
		// String accessory = sy+sm + "*" + p.getName() + "*"+ fileName;
		String accessory = sy + sm + "*" + s.getId() + "*" + fileName;

		// 打包
		/*byte[] buffer = new byte[1024];
		if (fileName.length() > 0) {
			// String strZipName = ympname + sy + sm + sd + p.getName()+".zip";
			String strZipName = ympname + sy + sm + sd + s.getId() + ".zip";
			System.out.println("压缩包名称:" + strZipName);
			try {
				ZipOutputStream out = new ZipOutputStream(new FileOutputStream(strZipName));
				String[] allfile = fileName.split("\\*");
				System.out.println(Arrays.toString(allfile));
				for (int i = 0; i < allfile.length; i++) {
					File tempfile = new File(ympname + allfile[i]);
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
				// accessory = accessory+sy+sm+sd+p.getName()+".zip";
				accessory = accessory + sy + sm + sd + s.getId() + ".zip";
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/

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

		dayManageDAO.insertDaibanplanmade(s);
		return json;
	}

	public String editDaibanplanmade(Daibanplanmade s, String fileName, String rootPath) {
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
			//deleteFile(rootPath + oldFoldName + "\\" + oldFile[oldFile.length - 1]);
		}
		// ----------打包------------------//
		/*byte[] buffer = new byte[1024];
		if (fileName.length() > 0) {
			String strZipName = rootPath + oldFoldName + "\\" + sy + sm + sd + s.getId() + ".zip";
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
				newAccessory = newAccessory + sy + sm + sd + s.getId() + ".zip";
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/

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

		dayManageDAO.updateDaibanplanmade(s);

		// return editSaftycheck2(s, fileName2, rootPath);
		return json;
	}

	public String addDaibanplanmade(Daibanplanmade s) {
		String json = "{\"success\":\"true\"}";
		dayManageDAO.insertDaibanplanmade(s);
		return json;
	}

	public String updateDaibanplanmade(Daibanplanmade s) {
		String json = "{\"success\":\"true\"}";
		dayManageDAO.updateDaibanplanmade(s);
		return json;
	}

	public String deleteDaibanplanmade(String ID, String rootPath) {
		Daibanplanmade pro = new Daibanplanmade();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			List<Daibanplanmade> list = dayManageDAO.checkDaibanplanmadeID((Integer.parseInt(temp[i])));
			// 删除附件
			if (list != null) {
				Daibanplanmade p = list.get(0);
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
			dayManageDAO.deleteDaibanplanmade(pro);
		}
		return json;
	}

	// ***************Daibanrecord***********
	public String getDaibanrecordList(String findstr, int start, int limit,String projectName) {

		List<Daibanrecord> list = dayManageDAO.getDaibanrecordList(findstr, start, limit,projectName);
		int total = dayManageDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";

		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Daibanrecord s = list.get(i);
			jsonStr += "{\"ID\":\"" + s.getId() + "\",\"holiday\":\"" + s.getHoliday() + "\",\"ondutytime\":\""
					+ s.getOndutytime() + "\",\"ondutyperson\":\"" + s.getOndutyperson() + "\",\"nextperson\":\""
					+ s.getNextperson() + "\",\"Accessory\":\"" + s.getAccessory() + "\",\"ProjectName\":\"" + s.getProjectName() + "\"}";

			System.out.println(jsonStr);
		}
		jsonStr += "]}";
		return jsonStr;
	}

	public String addDaibanrecord(Daibanrecord s, String fileName, String rootPath) {
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
		
		if(fileName != null && fileName.length()> 0)
		{
			if (fileName.charAt(fileName.length()-1)=='*') 
				fileName = fileName.substring(0,fileName.length()-1);
		}

		// 存入数据库的内容
		// String accessory = sy+sm + "*" + p.getName() + "*"+ fileName;
		String accessory = sy + sm + "*" + s.getId() + "*" + fileName;

		// 打包
		/*byte[] buffer = new byte[1024];
		if (fileName.length() > 0) {
			// String strZipName = ympname + sy + sm + sd + p.getName()+".zip";
			String strZipName = ympname + sy + sm + sd + s.getId() + ".zip";
			System.out.println("压缩包名称:" + strZipName);
			try {
				ZipOutputStream out = new ZipOutputStream(new FileOutputStream(strZipName));
				String[] allfile = fileName.split("\\*");
				System.out.println(Arrays.toString(allfile));
				for (int i = 0; i < allfile.length; i++) {
					File tempfile = new File(ympname + allfile[i]);
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
				// accessory = accessory+sy+sm+sd+p.getName()+".zip";
				accessory = accessory + sy + sm + sd + s.getId() + ".zip";
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/

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

		dayManageDAO.insertDaibanrecord(s);
		return json;
	}

	public String editDaibanrecord(Daibanrecord s, String fileName, String rootPath) {
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
			//deleteFile(rootPath + oldFoldName + "\\" + oldFile[oldFile.length - 1]);
		}
		// ----------打包------------------//
		/*byte[] buffer = new byte[1024];
		if (fileName.length() > 0) {
			String strZipName = rootPath + oldFoldName + "\\" + sy + sm + sd + s.getId() + ".zip";
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
				newAccessory = newAccessory + sy + sm + sd + s.getId() + ".zip";
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
*/
		
		newAccessory = newAccessory.substring(0, newAccessory.length()-1);
		
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

		dayManageDAO.updateDaibanrecord(s);

		// return editSaftycheck2(s, fileName2, rootPath);
		return json;
	}

	public String addDaibanrecord(Daibanrecord s) {
		String json = "{\"success\":\"true\"}";
		dayManageDAO.insertDaibanrecord(s);
		return json;
	}

	public String updateDaibanrecord(Daibanrecord s) {
		String json = "{\"success\":\"true\"}";
		dayManageDAO.updateDaibanrecord(s);
		return json;
	}

	public String deleteDaibanrecord(String ID) {
		Daibanrecord daibanrecord = new Daibanrecord();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			daibanrecord.setId(Integer.parseInt(temp[i]));
			dayManageDAO.deleteDaibanrecord(daibanrecord);
		}
		return json;
	}

	//指纹考勤
	public String getFingerprintList(String findstr, int start, int limit) {

		List<Fingerprint> list = dayManageDAO.getFingerprintList(findstr, start, limit);
		int total = dayManageDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Fingerprint p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"Name\":\"" + p.getName() + "\",\"Ip\":\"" + p.getIp()
					+ "\",\"Portno\":\"" + p.getPortno() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addFingerprint(Fingerprint p) {
		String json = "{\"success\":\"true\"}";
		dayManageDAO.insertFingerprint(p);
		return json;
	}

	public String editFingerprint(Fingerprint p) {

		String json = "{\"success\":\"true\"}";
		dayManageDAO.updateFingerprint(p);
		return json;
	}

	public String deleteFingerprint(String ID) {
		Fingerprint pro = new Fingerprint();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			pro.setId(Integer.parseInt(temp[i]));
			dayManageDAO.deleteFingerprint(pro);
		}
		return json;
	}

	
	private int id;
	private Date realtime;
	private String weekday;
	private String qixiday;
	private String qixinight;
	private String degreeday;
	private String degreenight;
	private String windday;
	private String windnight;
	private String rizhi;
	private String workplan;
	private String accessory;

	// ***************Saftyworkrizhi***********
	public String getSaftyworkrizhiList(String findstr, int start, int limit,String projectName) {

		List<Saftyworkrizhi> list = dayManageDAO.getSaftyworkrizhiList(findstr, start, limit,projectName);
		int total = dayManageDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";

		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Saftyworkrizhi s = list.get(i);
			jsonStr += "{\"ID\":\"" + s.getId() + "\",\"rizhitime\":\"" + s.getRizhitime() + "\",\"weekday\":\""
					+ s.getWeekday() + "\",\"qixiday\":\"" + s.getQixiday() + "\",\"qixinight\":\"" + s.getQixinight()
					+ "\",\"degreeday\":\"" + s.getDegreeday() + "\",\"degreenight\":\"" + s.getDegreenight()
					+ "\",\"windday\":\"" + s.getWindday() + "\",\"windnight\":\"" + s.getWindnight()
					+ "\",\"rizhi\":\"" + s.getRizhi() + "\",\"workplan\":\"" + s.getWorkplan() + "\",\"Accessory\":\""
					+ s.getAccessory() + "\",\"ProjectName\":\"" + s.getProjectName() + "\"}";

			System.out.println(jsonStr);
		}
		jsonStr += "]}";
		return jsonStr;
	}

	public String addSaftyworkrizhi(Saftyworkrizhi s, String fileName, String rootPath) {
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

		
		if(fileName != null && fileName.length()> 0)
		{
			if (fileName.charAt(fileName.length()-1)=='*') 
				fileName = fileName.substring(0,fileName.length()-1);
		}
		// 存入数据库的内容
		// String accessory = sy+sm + "*" + p.getName() + "*"+ fileName;
		String accessory = sy + sm + "*" + s.getId() + "*" + fileName;

		// 打包
		/*byte[] buffer = new byte[1024];
		if (fileName.length() > 0) {
			// String strZipName = ympname + sy + sm + sd + p.getName()+".zip";
			String strZipName = ympname + sy + sm + sd + s.getId() + ".zip";
			System.out.println("压缩包名称:" + strZipName);
			try {
				ZipOutputStream out = new ZipOutputStream(new FileOutputStream(strZipName));
				String[] allfile = fileName.split("\\*");
				System.out.println(Arrays.toString(allfile));
				for (int i = 0; i < allfile.length; i++) {
					File tempfile = new File(ympname + allfile[i]);
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
				// accessory = accessory+sy+sm+sd+p.getName()+".zip";
				accessory = accessory + sy + sm + sd + s.getId() + ".zip";
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/

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

		dayManageDAO.insertSaftyworkrizhi(s);
		return json;
	}

	public String editSaftyworkrizhi(Saftyworkrizhi s, String fileName, String rootPath) {
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
			//deleteFile(rootPath + oldFoldName + "\\" + oldFile[oldFile.length - 1]);
		}
		// ----------打包------------------//
		/*byte[] buffer = new byte[1024];
		if (fileName.length() > 0) {
			String strZipName = rootPath + oldFoldName + "\\" + sy + sm + sd + s.getId() + ".zip";
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
				newAccessory = newAccessory + sy + sm + sd + s.getId() + ".zip";
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/
		
		newAccessory = newAccessory.substring(0, newAccessory.length()-1);

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

		dayManageDAO.updateSaftyworkrizhi(s);

		// return editSaftycheck2(s, fileName2, rootPath);
		return json;
	}

	public String addSaftyworkrizhi(Saftyworkrizhi s) {
		String json = "{\"success\":\"true\"}";
		dayManageDAO.insertSaftyworkrizhi(s);
		return json;
	}

	public String updateSaftyworkrizhi(Saftyworkrizhi s) {
		String json = "{\"success\":\"true\"}";
		dayManageDAO.updateSaftyworkrizhi(s);
		return json;
	}

	public String deleteSaftyworkrizhi(String ID) {
		Saftyworkrizhi saftyworkrizhi = new Saftyworkrizhi();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			saftyworkrizhi.setId(Integer.parseInt(temp[i]));
			dayManageDAO.deleteSaftyworkrizhi(saftyworkrizhi);
		}
		return json;
	}
	
	public String getPeriodreportList(String findstr, int start,int limit, String type,String projectName)
	{
		List<Periodreport> list = dayManageDAO.getPeriodreportList(findstr, type, start, limit,projectName);
		int total = dayManageDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for(int i = 0; i < list.size(); ++i) {
			if(i > 0)
				jsonStr += ",";
			Periodreport p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId()
					+ "\",\"Year\":\"" + p.getYear()
					+ "\",\"Month\":\"" + p.getMonth()
					+ "\",\"Week\":\"" + p.getWeek()
					+ "\",\"Type\":\"" + p.getType()
					+ "\",\"Time\":\"" + p.getTime()
					+ "\",\"Accessory\":\"" + p.getAccessory()  + "\",\"ProjectName\":\"" + p.getProjectName() + "\"}";
		}
		jsonStr += "]}";
		return jsonStr;
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
 		
		if(fileName != null && fileName.length()> 0)
		{
			if (fileName.charAt(fileName.length()-1)=='*') 
				fileName = fileName.substring(0,fileName.length()-1);
		}
		
		//存入数据库的内容
      	String accessory = sy + sm + "*" + pName + "*" + fileName;
		
      	//打包
      	/*byte[] buffer = new byte[1024];   
      	if(fileName.length() > 0) {      	
      		String strZipName = ympname + sy + sm + sd + pName + ".zip"; 
      		System.out.println("压缩包名称:" + strZipName);
      		try {
      			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(strZipName)); 
          		String[] allfile = fileName.split("\\*");
          		System.out.println(Arrays.toString(allfile));
          		for(int i = 0;i < allfile.length; i++) {
          			File tempfile = new File(ympname+allfile[i]);
          			if(tempfile.exists()) {
          				FileInputStream fis = new FileInputStream(tempfile); 
              			out.putNextEntry(new ZipEntry(tempfile.getName()));   
              			int len;   
              			while((len = fis.read(buffer)) > 0) {   
              				out.write(buffer, 0, len);    
              			}   
              			out.closeEntry();   
              			fis.close(); 
          			}	  
          		} 
          		accessory += sy + sm + sd + pName + ".zip";
          		out.close();
      		}
      		catch (Exception e) {
          		e.printStackTrace();
          	}	
      	} */
      	
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
	
	public String addPeriodreport(Periodreport p, String fileName, String rootPath)
	{
		String json = "{\"success\":\"true\"}";
      	String accessory = addAccessory(rootPath, fileName, p.getYear()+"-"+p.getMonth());
      	if(fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			p.setAccessory(fileName);
		} else {
			p.setAccessory(accessory);
		}
        dayManageDAO.insertPeriodreport(p);
		return json;
	}

	// 更新 accessory 并建立相应文件夹
	private String editAccessory(String rootPath, String fileName, String pName, String pAccessory) {
		int oldAccessoryNone = 0 ;
		if(pAccessory == null || pAccessory.length() <= 0) {
			oldAccessoryNone = 1;
		}		
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
        if(month < 10) {
        	sm = "0" + sm;
        }
        String sd = String.valueOf(day);
        if(day < 10) {
        	sd = "0" + sd;
        }
      	
        if((pAccessory == null || pAccessory.length() <= 0) && (fileName != null && fileName.length() > 0)) {        	
        	//建立月份和项目名文件夹
          	String ym = rootPath+ sy + sm + "\\";
          	String ympname = ym + pName + "\\";
          	File directory1 =new File(ym);	//根目录是否存在
     		if(!directory1.exists())
     			directory1.mkdir();
     		File directory2 =new File(ympname);	//根目录是否存在
     		if(!directory2.exists())
     			directory2.mkdir();
        }
        
        String oldAccessory = pAccessory;
        String[] oldFile = oldAccessory.split("\\*");	
		int oldFileLength = oldFile.length;
		String oldFoldName;
		if(oldAccessoryNone == 0) {
			oldFoldName = oldFile[0] + "\\" + oldFile[1];
		} else {
			oldFoldName = sy + sm + "\\" + pName;
		}	
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if(oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1]  +"*"+fileName;
		} else {
			newAccessory = sy + sm + "*" + pName  + "*" + fileName;
		}
        
		//------------------------删除列表中不存在的文件---------------------------//
		for(int i = 2; i < oldFileLength - 1; i++) {
			String existFileInDB = oldFile[i];
			//列表中是否存在该文件
			boolean hasFileInList = false;
			for(String existFileInList : newFile) {
				if(existFileInDB.equals(existFileInList)) {
					hasFileInList = true;
					break;
				}
			}
			if(!hasFileInList) {
				deleteFile(rootPath + oldFoldName+"\\" + existFileInDB);
			}
		}
		//-----------------------end----------------------------------//
		//拷贝temp文件夹的新文件到对应文件夹
		for(String oneFile : newFile) {
			boolean hasFileInList = false;
			for(int i = 2;i < oldFileLength - 1; i++) {
				if(oneFile.equals(oldFile[i])) {
					hasFileInList = true;
					break;
				}
			}
			if(!hasFileInList) {
				cutGeneralFile(rootPath + "temp\\" + oneFile,rootPath + oldFoldName);
			}			
		}
		if(oldAccessoryNone == 0) {
			//deleteFile(rootPath + oldFoldName + "\\" + oldFile[oldFile.length-1]);
		}			
		//----------打包------------------//
      	/*byte[] buffer = new byte[1024];   
      	if(fileName.length()>0) {      	
      		String strZipName = rootPath+oldFoldName + "\\" + sy + sm + sd + pName + ".zip";
      		try {
      			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(strZipName)); 
          		String[] allfile = fileName.split("\\*");
          		for(int i = 0;i < allfile.length; i++) {
          			File tempfile = new File(rootPath + oldFoldName + "\\" + allfile[i]);
          			if(tempfile.exists()) {
          				FileInputStream fis = new FileInputStream(tempfile); 
              			out.putNextEntry(new ZipEntry(tempfile.getName()));   
              			int len;   
              			while((len = fis.read(buffer)) > 0) {   
              				out.write(buffer,0,len);    
              			}   
              			out.closeEntry();   
              			fis.close();  
          			}  			 
          		} 
          		newAccessory = newAccessory + sy + sm + sd + pName + ".zip";
          		out.close();
      		} 		
      		catch (Exception e) {
          		e.printStackTrace();
          	}	
      	}*/
		
		newAccessory = newAccessory.substring(0, newAccessory.length()-1);
		
      	if(fileName.length() != 0) {
      		String[] allFile = fileName.split("\\*");
      		for(String tempFile : allFile) {
      			if(tempFile.endsWith(".doc")) {
      				WordToPdf.translateThread(rootPath + oldFoldName +"\\"+tempFile, rootPath + oldFoldName +"\\"+tempFile.substring(0,tempFile.length()-4)+".pdf"); 			
      			}
      			else if(tempFile.endsWith(".docx")) {
      				WordToPdf.translateThread(rootPath + oldFoldName +"\\"+tempFile, rootPath + oldFoldName +"\\"+tempFile.substring(0,tempFile.length()-5)+".pdf");
      			}
      		}
      	}      	
      	return newAccessory;
	}
	
	public String editPeriodreport(Periodreport p, String fileName, String rootPath)
	{		
		String json = "{\"success\":\"true\"}";
		String newAccessory = editAccessory(rootPath, fileName, p.getYear()+"-"+p.getMonth(), p.getAccessory());
      	if(fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			p.setAccessory(fileName);
		}
		else {
			p.setAccessory(newAccessory);
		}
        dayManageDAO.updatePeriodreport(p);    
		return json;
	}
	

	// 删除指定附件、目录或文件
	private void deleteAccessory(String rootPath, String fileName) {
		System.out.println(fileName + "length" + fileName.length());
		if(fileName != null && fileName.length() != 0) {
			String[] name = fileName.split("\\*");
			String folder = name[0] +"//" + name[1];
			String path = rootPath +folder;
			System.out.println("--path:" +path);
			for(int j = 2; j < name.length; j++) {					
				deleteFile(path + "//" + name[j]);
			}
			DeleteFolder(path);
		}
	}
	
	public String deletePeriodreport(String ID, String rootPath)
	{
		Periodreport pro = new Periodreport();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for(int i = 0; i < temp.length; i++) {
			Periodreport p = dayManageDAO.getPeriodreport(Integer.parseInt(temp[i]));
			deleteAccessory(rootPath, p.getAccessory());
			pro.setId(Integer.parseInt(temp[i]));
			dayManageDAO.deletePeriodreport(pro);
		} 
		return json;
	}
	
	public String getZiliaoTongJi(int start, int limit) {
		List<Projectmanagement> list = basicInfoDAO.getProjectmanagementList("", start, limit, "");
		int total = basicInfoDAO.datacount;
		/*List<String> projectList = new ArrayList<>();
		for(int i=0;i<list.size();i++)
			projectList.add(list.get(i).getName());
		int total = projectList.size();*/
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for(int i=0;i<list.size();i++) {
			if (i > 0) {
				jsonStr += ",";
			}
			String projectname = list.get(i).getName();
			//List<Integer> res = new ArrayList<>();
			int[] nums = new int[22];
			//安全生产工作策划
			nums[0] = goalDutyDAO.getSaveproplanList("", start, limit, projectname).size();
			//准备阶段安全评估,土建施工阶段安全评估,安装调试阶段安全评估,试运行阶段安全评估
			List<Risksafepg> rlist = saftyCheckDAO.getRisksafepgList("", start, limit, projectname);
			for(int j=0;j<rlist.size();j++) {
				String state = rlist.get(j).getShigstage();
				if(state.equals("开工准备阶段"))
					nums[1]++;
				else if(state.equals("土建施工阶段"))
					nums[2]++;
				else if(state.equals("安装调试阶段"))
					nums[3]++;
				else if(state.equals("试运行阶段"))
					nums[4]++;
			}
			//list<Saveproduct> slist = goalDutyDAO.getSaveproductList("", "", start, limit, "");
			//总体安全生产目标
			nums[5] = goalDutyDAO.getSaveproductList("", "总体安全生产目标", start, limit, projectname).size();
			//年度安全生产目标
			nums[6] = goalDutyDAO.getSaveproductList("", "年度安全生产目标", start, limit, projectname).size();
			//年度目标分解
			nums[7] = goalDutyDAO.getGoaldecomList("", start, limit, projectname).size();
			//安全生产投入计划
			nums[8] = saftyCostDAO.getSaftycostplanList("", start, limit, projectname).size();
			//培训计划
			nums[9] = eduTrainDAO.getTrainplan1List("", start, limit, projectname).size();
			//安全检查计划
			nums[10] = saftyCheckDAO.getSaftycheckyearplanList("", start, limit, projectname).size();
			//标准化创建方案
			nums[11] = goalDutyDAO.getSecurityplanList("", "标准化创建方案", start, limit,projectname).size();
			nums[12] = goalDutyDAO.getSecurityplanList("", "标准化自评报告", start, limit,projectname).size();
			nums[13] = saftyCheckDAO.getSaftycheckyinhuanpcList("", start, limit, projectname).size();
			//安全工作日志
			nums[14] = dayManageDAO.getSaftyworkrizhiList("", start, limit, projectname).size();
			//安全周报
			nums[15] = dayManageDAO.getPeriodreportList("", "安全周报", start, limit, projectname).size();
			//安全生产管理信息月报表
			nums[16] = dayManageDAO.getPeriodreportList("", "安全生产管理信息月报表", start, limit, projectname).size();
			//事故隐患排查治理台账
			nums[17] = dayManageDAO.getPeriodreportList("", "事故隐患排查治理台账", start, limit, projectname).size();
			nums[18] = goalDutyDAO.getSecurityplanList("", "节能减排统计监测", start, limit,projectname).size();
			nums[19] = goalDutyDAO.getSecurityplanList("", "半年度工作总结", start, limit,projectname).size();
			nums[20] = goalDutyDAO.getSecurityplanList("", "年度安全工作总结", start, limit,projectname).size();
			nums[21] = goalDutyDAO.getSecurityplanList("", "完工安全工作总结", start, limit,projectname).size();
			
			jsonStr += "{\"projectName\":\"" + projectname + "\",\"num0\":\"" + nums[0] + "\",\"num1\":\""
					+ nums[1] + "\",\"num2\":\"" + nums[2] + "\",\"num3\":\"" + nums[3]
					+ "\",\"num4\":\"" + nums[4] + "\",\"num5\":\"" + nums[5]
					+ "\",\"num6\":\"" + nums[6] + "\",\"num7\":\"" + nums[7]
					+ "\",\"num8\":\"" + nums[8] + "\",\"num9\":\"" + nums[9]
					+ "\",\"num10\":\"" + nums[10] + "\",\"num11\":\"" + nums[11]
					+ "\",\"num12\":\"" + nums[12] + "\",\"num13\":\"" + nums[13]
					+ "\",\"num14\":\"" + nums[14] + "\",\"num15\":\"" + nums[15]
					+ "\",\"num16\":\"" + nums[16] + "\",\"num17\":\"" + nums[17]
					+ "\",\"num18\":\"" + nums[18] + "\",\"num19\":\"" + nums[19]
					+ "\",\"num20\":\"" + nums[20] + "\",\"num21\":\"" + nums[21]
					 + "\"}";

			System.out.println(jsonStr);
		}
		jsonStr += "]}";
		return jsonStr;
	}
	
	
	public String getAttLogList(String IPAddress,int port) throws IOException
	{
		
		System.out.println("getAttLogList begin");
		
		List<Zhiwenkaoqin> list = dayManageDAO.getZhiWenList(IPAddress, port);
		//List<UserInfo> userlist = JacobUtil.getUserInfoList(IPAddress, port);
		//String name = "";
		int total = list.size();
		System.out.println(total);
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for(int i = 0; i < list.size(); ++i) {
			if(i > 0)
				jsonStr += ",";
			Zhiwenkaoqin p = list.get(i);
			
			jsonStr += "{\"EnrollNumber\":\"" + p.getEnrollNumber()
					+ "\",\"VerifyMode\":\"" + "指纹"
					+ "\",\"InOutMode\":\"" + p.getInOutMode()
					+ "\",\"Time\":\"" + p.getTime()
					//+ "\",\"WorkCode\":\"" + p.getWorkCode()
					+ "\",\"Name\":\"" + p.getUserName()
					+ "\"}";
		}
		jsonStr += "]}";
		System.out.println("getAttLogList end");
		System.out.println(jsonStr);
		return jsonStr;
	}
	
	public String getAttLogList(int id) throws UnsupportedEncodingException
	{
		Monitor monitor = dayManageDAO.getMonitor(id);
		
		Fingerprint fingerprint = dayManageDAO.getFingerprintByName(monitor.getMonitorName());
		
		List<Zhiwenkaoqin> list = dayManageDAO.getZhiWenList(fingerprint.getIp(), Integer.parseInt(fingerprint.getPortno()));
		
		int total = list.size();
		System.out.println(total);
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for(int i = 0; i < list.size(); ++i) {
			if(i > 0)
				jsonStr += ",";
			Zhiwenkaoqin p = list.get(i);
			
			jsonStr += "{\"EnrollNumber\":\"" + p.getEnrollNumber()
					+ "\",\"VerifyMode\":\"" + "指纹"
					+ "\",\"InOutMode\":\"" + p.getInOutMode()
					+ "\",\"Time\":\"" + p.getTime()
					//+ "\",\"WorkCode\":\"" + p.getWorkCode()
					+ "\",\"Name\":\"" + p.getUserName()
					+ "\"}";
		}
		jsonStr += "]}";
		System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		System.out.println(jsonStr);
		return jsonStr;
	}
	
	public String getUserInfoList(String IPAddress,int port) throws Exception
	{
		
		System.out.println("SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS");
		
		List<Zhiwenuserinfo> list = dayManageDAO.getZhiwenuserinfoList(IPAddress, port);
		
		int total = list.size();
		System.out.println(total);
		String state = "";
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for(int i = 0; i < list.size(); ++i) {
			if(i > 0)
				jsonStr += ",";
			Zhiwenuserinfo p = list.get(i);

			jsonStr += "{\"EnrollNumber\":\"" + p.getEnrollNumber()
					+ "\",\"Name\":\"" + p.getName()
					+ "\",\"Password\":\"" + p.getPassword()
					+ "\",\"Privilege\":\"" + p.getPrivilege()
					+ "\",\"Enabled\":\"" + p.getEnabled()
					+ "\"}";
		}
		jsonStr += "]}";
		System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		System.out.println(jsonStr);
		return jsonStr;
	}
	
	public static void main(String[] args) throws Exception{

		String IPAdd = "172.16.141.151"; 
		DayManageService d = new DayManageService();
    	String test = d.getAttLogList(IPAdd,4370);

    	System.out.println(test);
    	
    	

	}
	
	
	
	public String synZhiWen(String IPAddress,int port) throws IOException
	{
		
		
		List<AttLog> list = JacobUtil.getAttLogList(IPAddress, port);
		//List<UserInfo> userlist = JacobUtil.getUserInfoList(IPAddress, port);
		String name = "";
		String json = "";
		for(int i = 0; i < list.size(); ++i) {
			AttLog p = list.get(i);
			if(p.getEnrollNumber().equals("5234"))
				name = "汪";
			else if(p.getEnrollNumber().equals("56"))
				name = "马";
			else if(p.getEnrollNumber().equals("1"))
				name = "猪";
			else if(p.getEnrollNumber().equals("2"))
				name = "窥面";
			else if(p.getEnrollNumber().equals("3"))
				name = "大扣扣";
			
			Zhiwenkaoqin zw = new Zhiwenkaoqin(p.getEnrollNumber(), name, p.getVerifyMode(),
					p.getInOutMode(), p.getTime(), IPAddress, String.valueOf(port));
			dayManageDAO.synZhiWen(zw);
			
		}
		
		List<UserInfo> usertlist = JacobUtil.getUserInfoList(IPAddress, port);
		

		String state = "";

		for(int i = 0; i < usertlist.size(); ++i) {

			UserInfo p = usertlist.get(i);
			
			if(p.getEnabled().equals("true"))
				state = "正常";
			else
				state = "禁用";
			
			Zhiwenuserinfo zwuser = new Zhiwenuserinfo(p.getEnrollNumber(), p.getName(), p.getPassword(), p.getEnabled(), p.getEnabled()
					,IPAddress, String.valueOf(port));
			
			dayManageDAO.synZhiWenUser(zwuser);
		}
		
		
		
		
		json = "{\"success\":\"true\"}";
		return json;
	}

}
