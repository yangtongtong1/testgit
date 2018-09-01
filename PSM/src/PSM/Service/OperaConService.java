package PSM.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import PSM.DAO.OperaConDAO;
import PSM.Tool.ReadExcel;
import PSM.Tool.WordToPdf;
import hibernate.Constructionelec;
import hibernate.ElecEquip;
import hibernate.FireSafety;
import hibernate.ODHequipment;
import hibernate.PODgoods;
import hibernate.Proapproval;
import hibernate.Safecheckrec;
import hibernate.SpEquip;
import hibernate.Tezhongpeople;
import hibernate.Tezhongsbpeople;
import hibernate.TransportSafety;
import hibernate.Xiandongtai;

public class OperaConService {
	private OperaConDAO operaConDAO;

	public String getSafecheckrecList(String findstr, int start, int limit, String tableID, String projectName) {
		List<Safecheckrec> list = operaConDAO.getSafecheckrecList(findstr, start, limit, tableID, projectName);
		int total = operaConDAO.datacount;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0)
				jsonStr += ",";
			Safecheckrec p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"Name\":\"" + p.getName() + "\",\"Type\":\"" + p.getType()
					+ "\",\"Num\":\"" + p.getNum() + "\",\"ThisTime\":\"" + sdf.format(p.getThisTime())
					+ "\",\"LastTime\":\"" + sdf.format(p.getLastTime()) + "\",\"Agent\":\"" + p.getAgent()
					+ "\",\"Other\":\"" + p.getOther() + "\",\"Registrant\":\"" + p.getRegistrant()
					+ "\",\"ProjectName\":\"" + p.getProjectName() + "\",\"Accessory\":\"" + p.getAccessory() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	// 添加

	// 获得 accessory 并建立相应的文件夹
	private String addAccessory(String rootPath, String fileName, String pName) {
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
		String ympname = ym + pName + "\\";
		File directory1 = new File(ym); // 根目录是否存在
		if (!directory1.exists())
			directory1.mkdir();
		File directory2 = new File(ympname); // 根目录是否存在
		if (!directory2.exists())
			directory2.mkdir();

		// 拷贝temp中的文件到正确的文件夹
		String[] newFile = fileName.split("\\*");
		for (String copyFile : newFile) {
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym + pName);
		}

		// 存入数据库的内容
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}

		String accessory = sy + sm + "*" + pName + "*" + fileName;

		// 打包
		/*
		 * byte[] buffer = new byte[1024]; if(fileName.length() > 0) { String
		 * strZipName = ympname + sy + sm + sd + pName + ".zip";
		 * System.out.println("压缩包名称:" + strZipName); try { ZipOutputStream out
		 * = new ZipOutputStream(new FileOutputStream(strZipName)); String[]
		 * allfile = fileName.split("\\*");
		 * System.out.println(Arrays.toString(allfile)); for(int i = 0;i <
		 * allfile.length; i++) { File tempfile = new File(ympname+allfile[i]);
		 * if(tempfile.exists()) { FileInputStream fis = new
		 * FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while((len =
		 * fis.read(buffer)) > 0) { out.write(buffer, 0, len); }
		 * out.closeEntry(); fis.close(); } } accessory += sy + sm + sd + pName
		 * + ".zip"; out.close(); } catch (Exception e) { e.printStackTrace(); }
		 * }
		 */

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
		return accessory;
	}

	public String addSafecheckrec(Safecheckrec p, String fileName, String rootPath) {
		String json = "{\"success\":\"true\"}";
		String accessory = addAccessory(rootPath, fileName, p.getName());
		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			p.setAccessory(fileName);
		} else {
			p.setAccessory(accessory);
		}
		operaConDAO.insertSafecheckrec(p);
		return json;
	}

	// 编辑

	// 更新 accessory 并建立相应文件夹
	private String editAccessory(String rootPath, String fileName, String pName, String pAccessory) {
		int oldAccessoryNone = 0;
		if (pAccessory == null || pAccessory.length() <= 0) {
			oldAccessoryNone = 1;
		}
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

		if ((pAccessory == null || pAccessory.length() <= 0) && (fileName != null && fileName.length() > 0)) {
			// 建立月份和项目名文件夹
			String ym = rootPath + sy + sm + "\\";
			String ympname = ym + pName + "\\";
			File directory1 = new File(ym); // 根目录是否存在
			if (!directory1.exists())
				directory1.mkdir();
			File directory2 = new File(ympname); // 根目录是否存在
			if (!directory2.exists())
				directory2.mkdir();
		}

		String oldAccessory = pAccessory;
		String[] oldFile = oldAccessory.split("\\*");
		int oldFileLength = oldFile.length;
		String oldFoldName;
		if (oldAccessoryNone == 0) {
			oldFoldName = oldFile[0] + "\\" + oldFile[1];
		} else {
			oldFoldName = sy + sm + "\\" + pName;
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			newAccessory = sy + sm + "*" + pName + "*" + fileName;
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
			// deleteFile(rootPath + oldFoldName + "\\" +
			// oldFile[oldFile.length-1]);
		}
		// ----------打包------------------//
		/*
		 * byte[] buffer = new byte[1024]; if(fileName.length()>0) { String
		 * strZipName = rootPath+oldFoldName +
		 * "\\" + sy + sm + sd + pName + ".zip"; try { ZipOutputStream out = new
		 * ZipOutputStream(new FileOutputStream(strZipName)); String[] allfile =
		 * fileName.split("\\*"); for(int i = 0;i < allfile.length; i++) { File
		 * tempfile = new File(rootPath + oldFoldName + "\\" + allfile[i]);
		 * if(tempfile.exists()) { FileInputStream fis = new
		 * FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while((len =
		 * fis.read(buffer)) > 0) { out.write(buffer,0,len); } out.closeEntry();
		 * fis.close(); } } newAccessory = newAccessory + sy + sm + sd + pName +
		 * ".zip"; out.close(); } catch (Exception e) { e.printStackTrace(); } }
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
		return newAccessory;
	}

	public String editSafecheckrec(Safecheckrec p, String fileName, String rootPath) {
		String json = "{\"success\":\"true\"}";
		String newAccessory = editAccessory(rootPath, fileName, p.getName(), p.getAccessory());
		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			p.setAccessory(fileName);
		} else {
			p.setAccessory(newAccessory);
		}
		operaConDAO.updateSafecheckrec(p);
		return json;
	}

	// 删除

	// 删除指定附件、目录或文件
	private void deleteAccessory(String rootPath, String fileName) {
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

	public String deleteSafecheckrec(String ID, String rootPath) {
		Safecheckrec pro = new Safecheckrec();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			List<Safecheckrec> list = operaConDAO.checkSafecheckrecID(Integer.parseInt(temp[i]));
			// 删除附件
			if (list != null) {
				Safecheckrec p = list.get(0);
				deleteAccessory(rootPath, p.getAccessory());
			}
			pro.setId(Integer.parseInt(temp[i]));
			operaConDAO.deleteSafecheckrec(pro);
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
			List<Safecheckrec> list = operaConDAO.checkSafecheckrecID(Integer.parseInt(ppID));
			if (list.size() > 0) {
				Safecheckrec p = list.get(0);
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

	public OperaConDAO getOperaConDAO() {
		return operaConDAO;
	}

	public void setOperaConDAO(OperaConDAO operaConDAO) {
		this.operaConDAO = operaConDAO;
	}
	
	public int importExcel(String type, String rootPath, String fileName, String projectName) {
		String path = rootPath + "temp\\" + fileName;
		int total = 0;
		System.out.println("type : " + type);
		switch (type) {
		case "特种作业人员":
			List<Tezhongpeople> tezhongpeoples = ReadExcel.readTezhongpeopleExcel(path);
			for (Tezhongpeople tezhongpeople : tezhongpeoples) {
				tezhongpeople.setProjectName(projectName);
				operaConDAO.insertTezhongpeople(tezhongpeople);
			}
			total = tezhongpeoples.size();
			break;
		case "特种设备作业人员":
			List<Tezhongsbpeople> tezhongsbpeoples  = ReadExcel.readTezhongsbpeopleExcel(path);
			for (Tezhongsbpeople tezhongsbpeople : tezhongsbpeoples) {
				tezhongsbpeople.setProjectName(projectName);
				operaConDAO.insertTezhongsbpeople(tezhongsbpeople);				
			}
			total = tezhongsbpeoples.size();
			break;
		case "现场作业人员动态管理":
			List<Xiandongtai> xiandongtais  = ReadExcel.readXiandongtaiExcel(path);
			for (Xiandongtai xiandongtai : xiandongtais) {
				xiandongtai.setProjectName(projectName);
				operaConDAO.insertXiandongtai(xiandongtai);				
			}
			total = xiandongtais.size();
			break;
		case "特种设备管理台账":
			List<SpEquip> spEquips = ReadExcel.readSpEquipExcel(path);
			for (SpEquip spEquip : spEquips) {
				spEquip.setProjectName(projectName);
				operaConDAO.insertSpEquip(spEquip);				
			}
			total = spEquips.size();
			break;
		case "电气设备设施管理台账":
			List<ElecEquip> elecEquips = ReadExcel.readElecEquipExcel(path);
			for (ElecEquip elecEquip : elecEquips) {
				elecEquip.setProjectName(projectName);
				operaConDAO.insertElecEquip(elecEquip);				
			}
			total = elecEquips.size();
			break;
		case "消防安全":
			List<FireSafety> fireSafeties = ReadExcel.readFireSafetyExcel(path);
			for (FireSafety fireSafetie : fireSafeties) {
				fireSafetie.setProjectName(projectName);
				operaConDAO.insertFireSafety(fireSafetie);		
			}
			total = fireSafeties.size();
			break;
		case "个人职业病防护用品":
			List<PODgoods> poDgoodss = ReadExcel.readPODgoodsExcel(path);
			for (PODgoods poDgoods : poDgoodss) {
				poDgoods.setProjectName(projectName);
				operaConDAO.insertPODgoods(poDgoods);
			}
			total = poDgoodss.size();
			break;
		case "职业健康防护设施":
			List<ODHequipment> odHequipments = ReadExcel.readODHequipmentExcel(path);
			for (ODHequipment odHequipment : odHequipments) {
				odHequipment.setProjectName(projectName);
				operaConDAO.insertODHequipment(odHequipment);
			}
			total = odHequipments.size();
			break;
		case "专项方案编审批":
			List<Proapproval> proapprovals = ReadExcel.readProapprovalExcel(path);
			for (Proapproval proapproval : proapprovals) {
				proapproval.setProjectName(projectName);
				operaConDAO.insertProapproval(proapproval);
			}
			total = proapprovals.size();
			break;
		case "建筑机械、施工机具管理台账":
			List<Constructionelec> constructionelecs = ReadExcel.readConstructionelecExcel(path);
			for (Constructionelec constructionelec : constructionelecs) {
				constructionelec.setProjectName(projectName);
				operaConDAO.insertConstructionelec(constructionelec);
			}
			total = constructionelecs.size();
			break;
		case "交通车辆管理台账":
			List<TransportSafety> transportSafeties = ReadExcel.readTransportSafetyExcel(path);
			for (TransportSafety transportSafety : transportSafeties) {
				transportSafety.setProjectName(projectName);
				operaConDAO.insertTransportSafety(transportSafety);
			}
			total = transportSafeties.size();
			break;
		default:
			break;
		}
		return total;
	}

}
