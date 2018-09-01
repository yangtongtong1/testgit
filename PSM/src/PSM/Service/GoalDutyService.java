package PSM.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipOutputStream;

import org.apache.tools.zip.ZipEntry;

import PSM.DAO.GoalDutyDAO;
import PSM.Tool.ExcelToPdf;
import PSM.Tool.WordToPdf;
import hibernate.Anweihui;
import hibernate.Fbplan;
import hibernate.Fileupload;
import hibernate.Flownode;
import hibernate.Goaldecom;
import hibernate.Monthplan;
import hibernate.Safetypromanagement;
import hibernate.Safetypromanagementfb;
import hibernate.Saveculture;
import hibernate.Saveprodbook;
import hibernate.Saveproduct;
import hibernate.Saveproplan;
import hibernate.Securityplan;
import hibernate.Threeworkplan;
import hibernate.Workplan;
import hibernate.Yearplan;

public class GoalDutyService {

	private GoalDutyDAO goalDutyDAO;

	public GoalDutyDAO getGoalDutyDAO() {
		return goalDutyDAO;
	}

	public void setGoalDutyDAO(GoalDutyDAO goalDutyDAO) {
		this.goalDutyDAO = goalDutyDAO;
	}

	public String getFlowNodeList(String tableid, String projectName) {
		List<Flownode> list = goalDutyDAO.getFlowNodeList(tableid, projectName);
		String jsonStr = "[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Flownode p = list.get(i);
			String myname = "";
			String myduty = "";
			// List<Projectperson> personlist =
			// goalDutyDAO.checkProjectpersonJob(p.getNodeName());
			// if(personlist!=null && personlist.size()>0)
			// {
			// Projectperson temp = personlist.get(0);
			// myname = temp.getName();
			// myduty = temp.getDuty();
			// }
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"TableID\":\"" + p.getTableId() + "\",\"StepNum\":\""
					+ p.getStepNum() + "\",\"OrderNum\":\"" + p.getOrderNum() + "\",\"NodeName\":\"" + p.getNodeName()
					+ "\"";
			jsonStr += ",\"NodeX\":\"" + p.getNodeX() + "\",\"NodeY\":\"" + p.getNodeY() + "\",\"Name\":\""
					+ p.getName() + "\",\"Duty\":\"" + p.getDuty() + "\",\"Phone\":\"" + p.getPhone()
					+ "\",\"ProjectName\":\"" + p.getProjectName() + "\",\"Accessory\":\"" + p.getAccessory() + "\"}";
		}
		jsonStr += "]";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String getNodeNameList() {
		List list = goalDutyDAO.getNodeNameList();
		String jsonStr = "[";
		for (int i = 0; i < list.size(); i++) {
			if (i > 0)
				jsonStr += ",";
			String temp = "";
			temp += list.get(i);
			jsonStr += "{\"NodeName\":\"" + temp + "\"}";
		}
		jsonStr += "]";
		return jsonStr;
	}

	public String getNameList(String nodeName) {
		List list = goalDutyDAO.getNameList(nodeName);
		String jsonStr = "[";
		for (int i = 0; i < list.size(); i++) {
			if (i > 0)
				jsonStr += ",";
			String temp = "";
			temp += list.get(i);
			jsonStr += "{\"Name\":\"" + temp + "\"}";
		}
		jsonStr += "]";
		return jsonStr;
	}

	public String getPhone(String nodeName, String name) {
		List list = goalDutyDAO.getPhone(nodeName, name);
		String temp = "";
		temp += list.get(0);
		return temp;
	}

	public String addNode(String tableid, String stepNum, String lastNodeName, String nowNodeName, String projectName,
			String realName, String duty, String phone, String fileName, String rootPath) {
		String json = "{\"success\":\"true\"}";

		Flownode lastp = goalDutyDAO.checkFlowNodeName(tableid, stepNum, lastNodeName, projectName).get(0);
		Flownode p = new Flownode();

		p.setTableId(lastp.getTableId());
		p.setStepNum(lastp.getStepNum());
		p.setOrderNum(lastp.getOrderNum() + 1);
		p.setNodeName(nowNodeName);
		p.setNodeX(0);
		p.setNodeY(lastp.getNodeY());
		p.setName(realName);
		if (duty.equals("不超过500个字符")) {
			duty = "";
		}
		p.setDuty(duty);
		p.setPhone(phone);
		p.setProjectName(projectName);

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
		if (fileName != null && fileName.length() > 0) {
			// 建立月份和项目名文件夹
			String ym = rootPath + sy + sm + "\\";
			String ympname = ym + tableid + "-" + p.getNodeName() + "\\";
			File directory1 = new File(ym); // 根目录是否存在
			if (!directory1.exists())
				directory1.mkdir();
			File directory2 = new File(ympname); // 根目录是否存在
			if (!directory2.exists())
				directory2.mkdir();
		}

		String[] newFile = fileName.split("\\*");
		for (String copyFile : newFile) {
			cutGeneralFile(rootPath + "temp\\" + copyFile, rootPath + sy + sm + "\\" + tableid + "-" + p.getNodeName());
		}
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}

		String accessory = sy + sm + "*" + tableid + "-" + p.getNodeName() + "*" + fileName;

		// ----------打包------------------//
		/*
		 * byte[] buffer = new byte[1024]; if(fileName.length()>0) { String
		 * strZipName = rootPath+ sy + sm
		 * +"\\"+ tableid + "-" +p.getNodeName() +"
		 * \\"+ sy+sm+sd+p.getNodeName()+".zip"; try { ZipOutputStream out = new
		 * ZipOutputStream(new FileOutputStream(strZipName)); String[] allfile =
		 * fileName.split("\\*"); for(int i =0;i<allfile.length;i++) { File
		 * tempfile = new File(rootPath+ sy + sm
		 * +"\\"+ tableid + "-" +p.getNodeName() +"\\"+ allfile[i]);
		 * if(tempfile.exists()) { FileInputStream fis = new
		 * FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while((len =
		 * fis.read(buffer))>0) { out.write(buffer,0,len); } out.closeEntry();
		 * fis.close(); } } accessory =
		 * accessory+sy+sm+sd+p.getNodeName()+".zip"; out.close(); } catch
		 * (Exception e) { e.printStackTrace(); } }
		 */

		if (fileName.length() != 0) {
			String[] allFile = fileName.split("\\*");
			for (String tempFile : allFile) {
				if (tempFile.endsWith(".doc")) {
					WordToPdf.translateThread(
							rootPath + sy + sm + "\\" + tableid + "-" + p.getNodeName() + "\\" + tempFile,
							rootPath + sy + sm + "\\" + tableid + "-" + p.getNodeName() + "\\"
									+ tempFile.substring(0, tempFile.length() - 4) + ".pdf");
				} else if (tempFile.endsWith(".docx")) {
					WordToPdf.translateThread(
							rootPath + sy + sm + "\\" + tableid + "-" + p.getNodeName() + "\\" + tempFile,
							rootPath + sy + sm + "\\" + tableid + "-" + p.getNodeName() + "\\"
									+ tempFile.substring(0, tempFile.length() - 5) + ".pdf");
				}
			}
		}

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			p.setAccessory(fileName);
		} else {
			p.setAccessory(accessory);
		}

		// 把lastnode后面的结点的OrderNum都加1
		String sqlstr = "update flownode set orderNum = (orderNum + 1) where tableid ='" + tableid
				+ "' and projectName ='" + projectName + "' and stepNum =" + stepNum + " and orderNum > "
				+ lastp.getOrderNum();
		goalDutyDAO.executeSQL(sqlstr);

		// 添加新结点到数据库
		goalDutyDAO.insertFlowNode(p);

		// 计算新坐标并更新
		List<Flownode> list = goalDutyDAO.getFlowNodeListByStep(tableid, stepNum, projectName);
		int count = goalDutyDAO.datacount;
		int width = 1000;
		int pwidth = 115;
		int[] xArray = new int[count];

		for (int i = 0; i < count; i++) {
			xArray[i] = (int) Math.round((i + 1) * width / (count + 1.0) - pwidth / 2);
		}

		for (int i = 0; i < list.size(); i++) {
			Flownode temp = list.get(i);
			temp.setNodeX(xArray[i]);
			goalDutyDAO.updateFlowNode(temp);
		}
		return json;
	}

	public String addFirstNode(String tableid, String stepNum, String lastNodeName, String nowNodeName,
			String projectName, String realName, String duty, String phone, String fileName, String rootPath) {
		String json = "{\"success\":\"true\"}";

		Flownode p = new Flownode();

		p.setTableId(tableid);
		p.setStepNum(Integer.parseInt(stepNum));
		p.setOrderNum(1);
		p.setNodeName(nowNodeName);
		p.setNodeX(0);
		p.setNodeY(60);
		p.setName(realName);
		if (duty.equals("不超过500个字符")) {
			duty = "";
		}
		p.setDuty(duty);
		p.setPhone(phone);
		p.setProjectName(projectName);

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
		if (fileName != null && fileName.length() > 0) {
			// 建立月份和项目名文件夹
			String ym = rootPath + sy + sm + "\\";
			String ympname = ym + tableid + "-" + p.getNodeName() + "\\";
			File directory1 = new File(ym); // 根目录是否存在
			if (!directory1.exists())
				directory1.mkdir();
			File directory2 = new File(ympname); // 根目录是否存在
			if (!directory2.exists())
				directory2.mkdir();
		}

		String[] newFile = fileName.split("\\*");
		for (String copyFile : newFile) {
			cutGeneralFile(rootPath + "temp\\" + copyFile, rootPath + sy + sm + "\\" + tableid + "-" + p.getNodeName());
		}
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}

		String accessory = sy + sm + "*" + tableid + "-" + p.getNodeName() + "*" + fileName;

		// ----------打包------------------//
		/*
		 * byte[] buffer = new byte[1024]; if(fileName.length()>0) { String
		 * strZipName = rootPath+ sy + sm
		 * +"\\"+ tableid + "-" +p.getNodeName() +"
		 * \\"+ sy+sm+sd+p.getNodeName()+".zip"; try { ZipOutputStream out = new
		 * ZipOutputStream(new FileOutputStream(strZipName)); String[] allfile =
		 * fileName.split("\\*"); for(int i =0;i<allfile.length;i++) { File
		 * tempfile = new File(rootPath+ sy + sm
		 * +"\\"+ tableid + "-" +p.getNodeName() +"\\"+ allfile[i]);
		 * if(tempfile.exists()) { FileInputStream fis = new
		 * FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while((len =
		 * fis.read(buffer))>0) { out.write(buffer,0,len); } out.closeEntry();
		 * fis.close(); } } accessory =
		 * accessory+sy+sm+sd+p.getNodeName()+".zip"; out.close(); } catch
		 * (Exception e) { e.printStackTrace(); } }
		 */

		if (fileName.length() != 0) {
			String[] allFile = fileName.split("\\*");
			for (String tempFile : allFile) {
				if (tempFile.endsWith(".doc")) {
					WordToPdf.translateThread(
							rootPath + sy + sm + "\\" + tableid + "-" + p.getNodeName() + "\\" + tempFile,
							rootPath + sy + sm + "\\" + tableid + "-" + p.getNodeName() + "\\"
									+ tempFile.substring(0, tempFile.length() - 4) + ".pdf");
				} else if (tempFile.endsWith(".docx")) {
					WordToPdf.translateThread(
							rootPath + sy + sm + "\\" + tableid + "-" + p.getNodeName() + "\\" + tempFile,
							rootPath + sy + sm + "\\" + tableid + "-" + p.getNodeName() + "\\"
									+ tempFile.substring(0, tempFile.length() - 5) + ".pdf");
				}
			}
		}

		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			p.setAccessory(fileName);
		} else {
			p.setAccessory(accessory);
		}

		// 把lastnode后面的结点的OrderNum都加1
		String sqlstr = "update flownode set orderNum = (orderNum + 1) where tableid ='" + tableid
				+ "' and projectName ='" + projectName + "' and stepNum =" + stepNum;
		goalDutyDAO.executeSQL(sqlstr);

		// 添加新结点到数据库
		goalDutyDAO.insertFlowNode(p);

		// 计算新坐标并更新
		List<Flownode> list = goalDutyDAO.getFlowNodeListByStep(tableid, stepNum, projectName);
		int count = goalDutyDAO.datacount;
		int width = 1000;
		int pwidth = 115;
		int padding = (width - count * pwidth) / (count + 1);
		int[] xArray = new int[count];

		for (int i = 0; i < count; i++) {
			// xArray[i] = padding + i * (pwidth + padding) - 20;
			xArray[i] = (int) Math.round((i + 1) * width / (count + 1.0) - pwidth / 2);
		}

		for (int i = 0; i < list.size(); i++) {
			Flownode temp = list.get(i);
			temp.setNodeX(xArray[i]);
			goalDutyDAO.updateFlowNode(temp);
		}
		return json;
	}

	public String deleteNode(String tableid, String stepNum, String nodeName, String rootPath, String projectName) {
		String json = "{\"success\":\"true\"}";

		Flownode p = goalDutyDAO.checkFlowNodeName(tableid, stepNum, nodeName, projectName).get(0);
		int orderNum = p.getOrderNum();

		// 把node后面的结点的OrderNum都-1
		String sqlstr = "update flownode set orderNum = (orderNum - 1) where tableid ='" + tableid
				+ "' and projectName ='" + projectName + "' and stepNum =" + stepNum + " and orderNum > "
				+ p.getOrderNum();
		goalDutyDAO.executeSQL(sqlstr);

		// 删除点
		String fileName = p.getAccessory();
		goalDutyDAO.deleteFlowNode(p);

		// 删除附件
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

		// 计算新坐标并更新
		List<Flownode> list = goalDutyDAO.getFlowNodeListByStep(tableid, stepNum, projectName);
		int count = goalDutyDAO.datacount;
		int width = 1000;
		int pwidth = 115;
		int padding = (width - count * pwidth) / (count + 1);
		int[] xArray = new int[count];

		for (int i = 0; i < count; i++) {
			// xArray[i] = padding + i * (pwidth + padding) - 20;

			xArray[i] = (int) Math.round((i + 1) * width / (count + 1.0) - pwidth / 2);
		}

		for (int i = 0; i < list.size(); i++) {
			Flownode temp = list.get(i);
			temp.setNodeX(xArray[i]);
			goalDutyDAO.updateFlowNode(temp);
		}

		return json;
	}

	public String editNode(String fileName, String tableid, String nodeid, String nodeName, String realName,
			String duty, String phone, String projectName, String rootPath) {

		Flownode p = goalDutyDAO.checkFlowNodeID(Integer.parseInt(nodeid)).get(0);
		p.setNodeName(nodeName);
		p.setName(realName);
		if (duty.equals("不超过500个字符")) {
			duty = "";
		}
		p.setDuty(duty);
		p.setPhone(phone);

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
			String ympname = ym + tableid + "-" + p.getNodeName() + "\\";
			File directory1 = new File(ym); // 根目录是否存在
			if (!directory1.exists())
				directory1.mkdir();
			File directory2 = new File(ympname); // 根目录是否存在
			if (!directory2.exists())
				directory2.mkdir();
		}

		String oldAccessory = p.getAccessory();
		if(oldAccessory == null || oldAccessory.length() <= 0) {
			oldAccessoryNone = 1;
		}
		String[] oldFile = oldAccessory.split("\\*");
		int oldFileLength = oldFile.length;
		String oldFoldName;
		if (oldAccessoryNone == 0) {
			oldFoldName = oldFile[0] + "\\" + oldFile[1];
		} else {
			oldFoldName = sy + sm + "\\" + tableid + "-" + p.getNodeName();
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			newAccessory = sy + sm + "*" + tableid + "-" + p.getNodeName() + "*" + fileName;
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
		/*byte[] buffer = new byte[1024];
		if (fileName.length() > 0) {
			String strZipName = rootPath + oldFoldName + "\\" + sy + sm + sd + p.getNodeName() + ".zip";
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
				newAccessory = newAccessory + sy + sm + sd + p.getNodeName() + ".zip";
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/
		
		
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

		goalDutyDAO.updateFlowNode(p);
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

	public String getFileNameList(String tableid, String stepNum, String nodeName, String projectName) {
		List<Flownode> list = goalDutyDAO.checkFlowNodeName(tableid, stepNum, nodeName, projectName);
		String jsonStr = "[";
		if (list != null && list.size() > 0) {
			Flownode p = list.get(0);
			String[] Files = p.getAccessory().split("\\*");
			for (int i = 2; i < Files.length; ++i) {
				String foldName = Files[0] + "*" + Files[1];
				if (i > 2) {
					jsonStr += ",";
				}
				jsonStr += "{\"FoldName\":\"" + foldName + "\",\"FileName\":\"" + Files[i] + "\"}";
			}
		}
		jsonStr += "]";
		System.out.println(jsonStr);
		return jsonStr;
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
		System.out.println("--in delete all--" + ppID + "  " + fileName + " " + rootPath);
		String[] newFile = fileName.split("\\*");
		String json = "{\"success\":\"true\"}";

		if (ppID == null || ppID.length() <= 0)// 没有ID 说明是添加 直接去temp全部删除
		{
			for (String existFileInList : newFile) {
				deleteFile(rootPath + "temp\\" + existFileInList);
			}
		} else {
			String[] strKey = null;
			List<Flownode> list = goalDutyDAO.checkFlowNodeID((Integer.parseInt(ppID)));
			if (list.size() > 0) {
				Flownode p = list.get(0);
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
							System.out.println(rootPath + "temp\\" + existFileInList);
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

	public String getSaveculture(String type) {

		Saveculture p = goalDutyDAO.getSaveculture(type);
		String jsonStr = "[";
		jsonStr += "{\"ID\":\"" + p.getId() + "\",\"Content\":\"" + p.getContent() + "\",\"Type\":\"" + p.getType()
				+ "\"}";
		jsonStr += "]";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String UpdateSaveculture(Saveculture p) {
		String json = "{\"success\":\"true\"}";
		goalDutyDAO.updateSaveculture(p);
		return json;
	}

	public String getSecurityplanList(String findstr, String type, int start, int limit,String projectName) {

		List<Securityplan> list = goalDutyDAO.getSecurityplanList(findstr, type, start, limit,projectName);
		int total = goalDutyDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Securityplan p = list.get(i);

			String acc = p.getAccessory();
			//String acc = p.getAccessory();
			String accJson = "";
			if(acc!=null&&acc.length()!=0)
			{
			String[] accArray = acc.split("\\*");
			accJson += accArray[2];
			}
			// accJson = accJson.split("\\.")[0];

			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"Timeyear\":\"" + p.getTimeyear() + "\",\"Fname\":\""
					+ p.getFname() + "\",\"Title\":\"" + p.getTitle() + "\"";
			jsonStr += ",\"Accessory\":\"" + acc + "\",\"Filename\":\"" + accJson + "\"";
			jsonStr += ",\"Time\":\"" + p.getTime() + "\",\"UserName\":\"" + p.getUserName() + "\",\"ProjectName\":\"" + p.getProjectName() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addSecurityplan(Securityplan p, String fileName, String rootPath) {
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

		/*
		 * String[] fileSplit = fileName.split("\\*");
		 * 
		 * System.out.println("下面输入文件名了！！！！！！！！！！！！！！");
		 * System.out.println(Arrays.toString(fileSplit)); String[] accessory =
		 * new String[fileSplit.length];
		 * 
		 * for(int i=0;i<fileSplit.length;i++) { accessory[i] = sy+sm + "*" +
		 * p.getTitle() + "*"+ fileSplit[i]; }
		 */
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}

		String accessory = sy + sm + "*" + p.getTitle() + "*" + fileName + "*";
		// accessory = accessory.substring(0, accessory.length()-1);

		// 打包
		/*
		 * byte[] buffer = new byte[1024]; if(fileName.length()>0) { String
		 * strZipName = ympname + sy + sm + sd + p.getTitle()+".zip";
		 * System.out.println("压缩包名称:"+strZipName); try { ZipOutputStream out =
		 * new ZipOutputStream(new FileOutputStream(strZipName)); String[]
		 * allfile = fileName.split("\\*");
		 * System.out.println(Arrays.toString(allfile)); for(int i
		 * =0;i<allfile.length;i++) { File tempfile = new
		 * File(ympname+allfile[i]); if(tempfile.exists()) { FileInputStream fis
		 * = new FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while((len =
		 * fis.read(buffer))>0) { out.write(buffer,0,len); } out.closeEntry();
		 * fis.close(); } //accessory[i] =
		 * accessory[i]+sy+sm+sd+p.getTitle()+".zip"; } accessory =
		 * accessory+sy+sm+sd+p.getTitle()+".zip"; out.close(); } catch
		 * (Exception e) { e.printStackTrace(); } }
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
		goalDutyDAO.insertSecurityplan(p);
		return json;
	}

	public String editSecurityplan(Securityplan p, String fileName, String rootPath) {
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
			deleteFile(rootPath + oldFoldName + "\\" + oldFile[oldFile.length - 1]);
		}
		// ----------打包------------------//
		byte[] buffer = new byte[1024];
		if (fileName.length() > 0) {
			String strZipName = rootPath + oldFoldName + "\\" + sy + sm + sd + p.getTitle() + ".zip";
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
				newAccessory = newAccessory + sy + sm + sd + p.getTitle() + ".zip";
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

		goalDutyDAO.updateSecurityplan(p);
		return json;
	}

	public String deleteSecurityplan(String ID, String rootPath) {
		Securityplan pro = new Securityplan();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			List<Securityplan> list = goalDutyDAO.checkSecurityplanID((Integer.parseInt(temp[i])));
			// 删除附件
			if (list != null) {
				Securityplan p = list.get(0);
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
			goalDutyDAO.deleteSecurityplan(pro);
		}
		return json;
	}

	public String getAnweihuiList(String findstr, int start, int limit, String projectName) {

		List<Anweihui> list = goalDutyDAO.getAnweihuiList(findstr, start, limit, projectName);
		int total = goalDutyDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Anweihui p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"Esorad\":\"" + p.getEsorad() + "\",\"Time\":\"" + p.getTime()
					+ "\",\"Head\":\"" + p.getHead() + "\",\"ViceHead\":\"" + p.getViceHead() + "\",\"Form\":\""
					+ p.getForm() + "\",\"Agency\":\"" + p.getAgency() + "\",\"ProjectName\":\"" + p.getProjectName()
					+ "\"";
			jsonStr += ",\"Accessory\":\"" + p.getAccessory() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addAnweihui(Anweihui p, String fileName, String rootPath) {
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

		String accessory = sy + sm + "*" + p.getHead() + "*" + fileName;

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
		goalDutyDAO.insertAnweihui(p);
		return json;
	}

	public String editAnweihui(Anweihui p, String fileName, String rootPath) {
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
			newAccessory = sy + sm + "*" + p.getHead() + "*" + fileName;
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

		goalDutyDAO.updateAnweihui(p);
		return json;
	}

	public String deleteAnweihui(String ID, String rootPath) {
		Anweihui pro = new Anweihui();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			List<Anweihui> list = goalDutyDAO.checkAnweihuiID((Integer.parseInt(temp[i])));
			// 删除附件
			if (list != null) {
				Anweihui p = list.get(0);
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
			goalDutyDAO.deleteAnweihui(pro);
		}
		return json;
	}

	// jianglf-----------------------------------------------------
	public String getSaveproductList(String findstr, String type, int start, int limit, String projectName) {

		List<Saveproduct> list = goalDutyDAO.getSaveproductList(findstr, type, start, limit, projectName);
		int total = goalDutyDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Saveproduct p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"EvaluateRate\":\"" + p.getEvaluateRate() + "\",\"TrainRate\":\""
					+ p.getTrainRate() + "\",\"ReformRate\":\"" + p.getReformRate() + "\",\"ReachRate\":\""
					+ p.getReachRate() + "\"";
			jsonStr += ",\"ExamineRate\":\"" + p.getExamineRate() + "\",\"EnviPassRate\":\"" + p.getEnviPassRate()
					+ "\",\"BottomRate\":\"" + p.getBottomRate() + "\",\"SickPassRate\":\"" + p.getSickPassRate()
					+ "\",\"CheckRate\":\"" + p.getCheckRate() + "\"";
			jsonStr += ",\"WorkAcci\":\"" + p.getWorkAcci() + "\",\"ProdAcci\":\"" + p.getProdAcci()
					+ "\",\"AcciRate\":\"" + p.getAcciRate() + "\",\"FenBaoAcci\":\"" + p.getFenBaoAcci()
					+ "\",\"Disaster\":\"" + p.getDisaster() + "\"";
			jsonStr += ",\"JobEvent\":\"" + p.getJobEvent() + "\",\"PollutEvent\":\"" + p.getPollutEvent()
					+ "\",\"Behave\":\"" + p.getBehave() + "\",\"TimeYear\":\"" + p.getTimeYear() + "\"";
			jsonStr += ",\"Accessory\":\"" + p.getAccessory() + "\",\"FireAcci\":\"" + p.getFireAcci()
					+ "\",\"Management\":\"" + p.getManagement()
					+ "\",\"Contrl\":\"" + p.getContrl() + "\",\"Type\":\"" + p.getType() + "\",\"ProjectName\":\""
					+ p.getProjectName() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}
	// end--------------------------------------------------------------------------------------

	public String addSaveproduct(Saveproduct p, String fileName, String rootPath) {
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
		String ympname = ym + p.getBehave() + "\\";
		File directory1 = new File(ym); // 根目录是否存在
		if (!directory1.exists())
			directory1.mkdir();
		File directory2 = new File(ympname); // 根目录是否存在
		if (!directory2.exists())
			directory2.mkdir();

		// 拷贝temp中的文件到正确的文件夹
		String[] newFile = fileName.split("\\*");
		for (String copyFile : newFile) {
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym + p.getBehave());
		}

		// 存入数据库的内容
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}
		String accessory = sy + sm + "*" + p.getBehave() + "*" + fileName;

		// 打包
		/*
		 * byte[] buffer = new byte[1024]; if(fileName.length()>0) { String
		 * strZipName = ympname + sy + sm + sd + p.getBehave()+".zip";
		 * System.out.println("压缩包名称:"+strZipName); try { ZipOutputStream out =
		 * new ZipOutputStream(new FileOutputStream(strZipName)); String[]
		 * allfile = fileName.split("\\*");
		 * System.out.println(Arrays.toString(allfile)); for(int i
		 * =0;i<allfile.length;i++) { File tempfile = new
		 * File(ympname+allfile[i]); if(tempfile.exists()) { FileInputStream fis
		 * = new FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while((len =
		 * fis.read(buffer))>0) { out.write(buffer,0,len); } out.closeEntry();
		 * fis.close(); } } accessory = accessory+sy+sm+sd+p.getBehave()+".zip";
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
		goalDutyDAO.insertSaveproduct(p);
		return json;
	}

	public String editSaveproduct(Saveproduct p, String fileName, String rootPath) {
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
			String ympname = ym + p.getBehave() + "\\";
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
			oldFoldName = sy + sm + "\\" + p.getBehave();
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			newAccessory = sy + sm + "*" + p.getBehave() + "*" + fileName;
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
		 * strZipName = rootPath+oldFoldName
		 * +"\\"+ sy+sm+sd+p.getBehave()+".zip"; try { ZipOutputStream out = new
		 * ZipOutputStream(new FileOutputStream(strZipName)); String[] allfile =
		 * fileName.split("\\*"); for(int i =0;i<allfile.length;i++) { File
		 * tempfile = new File(rootPath+ oldFoldName +"\\"+ allfile[i]);
		 * if(tempfile.exists()) { FileInputStream fis = new
		 * FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while((len =
		 * fis.read(buffer))>0) { out.write(buffer,0,len); } out.closeEntry();
		 * fis.close(); } } newAccessory =
		 * newAccessory+sy+sm+sd+p.getBehave()+".zip"; out.close(); } catch
		 * (Exception e) { e.printStackTrace(); } }
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

		goalDutyDAO.updateSaveproduct(p);
		return json;
	}

	public String deleteSaveproduct(String ID, String rootPath) {
		Saveproduct pro = new Saveproduct();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			List<Saveproduct> list = goalDutyDAO.checkSaveproductID((Integer.parseInt(temp[i])));
			// 删除附件
			if (list != null) {
				Saveproduct p = list.get(0);
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
			goalDutyDAO.deleteSaveproduct(pro);
		}
		return json;
	}

	public String deleteGoaldecom(String ID, String rootPath) {
		Goaldecom pro = new Goaldecom();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			List<Goaldecom> list = goalDutyDAO.checkGoaldecomID((Integer.parseInt(temp[i])));
			// 删除附件
			if (list != null) {
				Goaldecom p = list.get(0);
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
			goalDutyDAO.deleteGoaldecom(pro);
		}
		return json;
	}

	public String editGoaldecom(Goaldecom p, String fileName, String rootPath) {
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
			String ympname = ym + p.getManager() + "\\";
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
			oldFoldName = sy + sm + "\\" + p.getManager();
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			newAccessory = sy + sm + "*" + p.getManager() + "*" + fileName;
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
		goalDutyDAO.updateGoaldecom(p);
		return json;
	}

	public String getGoaldecomList(String findstr, int start, int limit, String projectName) {

		List<Goaldecom> list = goalDutyDAO.getGoaldecomList(findstr, start, limit, projectName);
		int total = goalDutyDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Goaldecom p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"Target\":\"" + p.getTarget() + "\",\"Content\":\""
					+ p.getContent() + "\",\"Mvalue\":\"" + p.getMvalue() + "\",\"Measure\":\"" + p.getMeasure()
					+ "\",\"Time\":\"" + p.getTime() + "\",\"Manager\":\"" + p.getManager() + "\",\"Completed\":\""
					+ p.getCompleted() + "\",\"GoalDecom\":\"" + p.getGoalDecom() + "\",\"IndexValue\":\""
					+ p.getIndexValue() + "\",\"Factor\":\"" + p.getFactor() + "\"";
			jsonStr += ",\"QuaSave\":\"" + p.getQuaSave() + "\",\"Design\":\"" + p.getDesign() + "\",\"Engineer\":\""
					+ p.getEngineer() + "\",\"Affair\":\"" + p.getAffair() + "\",\"Buy\":\"" + p.getBuy() + "\"";
			jsonStr += ",\"MakeOne\":\"" + p.getMakeOne() + "\",\"ExamineOne\":\"" + p.getExamineOne()
					+ "\",\"AgreeOne\":\"" + p.getAgreeOne() + "\",\"Accessory\":\"" + p.getAccessory() + "\",\"ProjectName\":\"" + p.getProjectName() + "\"";
			jsonStr += ",\"SignOne\":\"" + p.getSignOne() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addGoaldecom(Goaldecom p, String fileName, String rootPath) {
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
		String ympname = ym + p.getManager() + "\\";
		File directory1 = new File(ym); // 根目录是否存在
		if (!directory1.exists())
			directory1.mkdir();
		File directory2 = new File(ympname); // 根目录是否存在
		if (!directory2.exists())
			directory2.mkdir();

		// 拷贝temp中的文件到正确的文件夹
		String[] newFile = fileName.split("\\*");
		for (String copyFile : newFile) {
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym + p.getManager());
		}

		// 存入数据库的内容
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}

		String accessory = sy + sm + "*" + p.getManager() + "*" + fileName;

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
		goalDutyDAO.insertGoaldecom(p);
		return json;
	}

	public String addSaveprodbook(Saveprodbook p, String fileName, String rootPath) throws Exception {
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
		String ympname = ym + p.getType() + "\\";
		File directory1 = new File(ym); // 根目录是否存在
		if (!directory1.exists())
			directory1.mkdir();
		File directory2 = new File(ympname); // 根目录是否存在
		if (!directory2.exists())
			directory2.mkdir();

		// 拷贝temp中的文件到正确的文件夹
		String[] newFile = fileName.split("\\*");
		for (String copyFile : newFile) {
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym + p.getType());
		}

		// 存入数据库的内容
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}

		String accessory = sy + sm +  "*" + p.getType() + "*" + fileName;

		// 打包
		/*
		 * byte[] buffer = new byte[1024]; if(fileName.length()>0) { String
		 * strZipName = ympname + sy + sm + sd + p.getToTarget()+".zip";
		 * System.out.println("压缩包名称:"+strZipName); try { ZipOutputStream out =
		 * new ZipOutputStream(new FileOutputStream(strZipName)); String[]
		 * allfile = fileName.split("\\*");
		 * System.out.println(Arrays.toString(allfile)); for(int i
		 * =0;i<allfile.length;i++) { File tempfile = new
		 * File(ympname+allfile[i]); if(tempfile.exists()) { FileInputStream fis
		 * = new FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while((len =
		 * fis.read(buffer))>0) { out.write(buffer,0,len); } out.closeEntry();
		 * fis.close(); } } accessory =
		 * accessory+sy+sm+sd+p.getToTarget()+".zip"; out.close(); } catch
		 * (Exception e) { e.printStackTrace(); } }
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
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String uploadtime = sy + sm +sd;
		if (fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			p.setAccessory(fileName);
		} else {
			p.setAccessory(accessory);
			p.setUploadTime(sdf.parse(uploadtime));
		}
		goalDutyDAO.insertSaveprodbook(p);
		return json;
	}

	public String editSaveprodbook(Saveprodbook p, String fileName, String rootPath) {
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
			String ympname = ym + p.getType() + "\\";
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
			oldFoldName = sy + sm + "\\" + p.getType();
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			newAccessory = sy + sm +"*" + p.getType() + "*" + fileName;
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
		 * strZipName = rootPath+oldFoldName
		 * +"\\"+ sy+sm+sd+p.getToTarget()+".zip"; try { ZipOutputStream out =
		 * new ZipOutputStream(new FileOutputStream(strZipName)); String[]
		 * allfile = fileName.split("\\*"); for(int i =0;i<allfile.length;i++) {
		 * File tempfile = new File(rootPath+ oldFoldName +"\\"+ allfile[i]);
		 * if(tempfile.exists()) { FileInputStream fis = new
		 * FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while((len =
		 * fis.read(buffer))>0) { out.write(buffer,0,len); } out.closeEntry();
		 * fis.close(); } } newAccessory =
		 * newAccessory+sy+sm+sd+p.getToTarget()+".zip"; out.close(); } catch
		 * (Exception e) { e.printStackTrace(); } }
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

		goalDutyDAO.updateSaveprodbook(p);
		return json;
	}

	public String deleteSaveprodbook(String ID, String rootPath) {
		Saveprodbook pro = new Saveprodbook();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			List<Saveprodbook> list = goalDutyDAO.checkSaveprodbookID((Integer.parseInt(temp[i])));
			// 删除附件
			if (list != null) {
				Saveprodbook p = list.get(0);
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
			goalDutyDAO.deleteSaveprodbook(pro);
		}
		return json;
	}

	public String getSaveprodbookList(String findstr, int start, int limit, String projectName) {

		List<Saveprodbook> list = goalDutyDAO.getSaveprodbookList(findstr, start, limit, projectName);
		int total = goalDutyDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Saveprodbook p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"ToTarget\":\"" + p.getToTarget() + "\",\"Type\":\""
					+ p.getType() + "\",\"TimeYear\":\"" + p.getTimeYear() + "\",\"ProjectName\":\""
					+ p.getProjectName() + "\"";

			jsonStr += ",\"Accessory\":\"" + p.getAccessory() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String deleteSaveproplan(String ID, String rootPath) {
		Saveproplan pro = new Saveproplan();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			List<Saveproplan> list = goalDutyDAO.checkSaveproplanID((Integer.parseInt(temp[i])));
			// 删除附件
			if (list != null) {
				Saveproplan p = list.get(0);
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
			goalDutyDAO.deleteSaveproplan(pro);
		}
		return json;
	}

	public String editSaveproplan(Saveproplan p, String fileName, String rootPath) {

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
			String ympname = ym + p.getAnweihui() + "\\";
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
			oldFoldName = sy + sm + "\\" + p.getAnweihui();
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			newAccessory = sy + sm + "*" + p.getAnweihui() + "*" + fileName;
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
		 * strZipName = rootPath+oldFoldName
		 * +"\\"+ sy+sm+sd+p.getAnweihui()+".zip"; try { ZipOutputStream out =
		 * new ZipOutputStream(new FileOutputStream(strZipName)); String[]
		 * allfile = fileName.split("\\*"); for(int i =0;i<allfile.length;i++) {
		 * File tempfile = new File(rootPath+ oldFoldName +"\\"+ allfile[i]);
		 * if(tempfile.exists()) { FileInputStream fis = new
		 * FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while((len =
		 * fis.read(buffer))>0) { out.write(buffer,0,len); } out.closeEntry();
		 * fis.close(); } } newAccessory =
		 * newAccessory+sy+sm+sd+p.getAnweihui()+".zip"; out.close(); } catch
		 * (Exception e) { e.printStackTrace(); } }
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

		goalDutyDAO.updateSaveproplan(p);
		return json;
	}

	public String getSaveproplanList(String findstr, int start, int limit, String projectName) {

		List<Saveproplan> list = goalDutyDAO.getSaveproplanList(findstr, start, limit, projectName);
		int total = goalDutyDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Saveproplan p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"Anweihui\":\"" + p.getAnweihui() + "\",\"ThreeGroup\":\""
					+ p.getThreeGroup() + "\",\"FourBuild\":\"" + p.getFourBuild() + "\",\"SaveBuild\":\""
					+ p.getSaveBuild() + "\"";
			jsonStr += ",\"SavePlan\":\"" + p.getSavePlan() + "\",\"SaveCheck\":\"" + p.getSaveCheck()
					+ "\",\"BuildPlan\":\"" + p.getBuildPlan() + "\",\"HandlePlan\":\"" + p.getHandlePlan()
					+ "\",\"SaveBuildPlan\":\"" + p.getSaveBuildPlan() + "\"";
			jsonStr += ",\"DangerPublic\":\"" + p.getDangerPublic() + "\",\"ExecutePlan\":\"" + p.getExecutePlan()
					+ "\",\"WorkPlan\":\"" + p.getWorkPlan() + "\",\"ProjectName\":\"" + p.getProjectName() + "\"";
			jsonStr += ",\"Accessory\":\"" + p.getAccessory() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addSaveproplan(Saveproplan p, String fileName, String rootPath) {
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
		String ympname = ym + p.getAnweihui() + "\\";
		File directory1 = new File(ym); // 根目录是否存在
		if (!directory1.exists())
			directory1.mkdir();
		File directory2 = new File(ympname); // 根目录是否存在
		if (!directory2.exists())
			directory2.mkdir();

		// 拷贝temp中的文件到正确的文件夹
		String[] newFile = fileName.split("\\*");
		for (String copyFile : newFile) {
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym + p.getAnweihui());
		}

		// 存入数据库的内容
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}

		String accessory = sy + sm + "*" + p.getAnweihui() + "*" + fileName;

		// 打包
		/*
		 * byte[] buffer = new byte[1024]; if(fileName.length()>0) { String
		 * strZipName = ympname + sy + sm + sd + p.getAnweihui()+".zip";
		 * System.out.println("压缩包名称:"+strZipName); try { ZipOutputStream out =
		 * new ZipOutputStream(new FileOutputStream(strZipName)); String[]
		 * allfile = fileName.split("\\*");
		 * System.out.println(Arrays.toString(allfile)); for(int i
		 * =0;i<allfile.length;i++) { File tempfile = new
		 * File(ympname+allfile[i]); if(tempfile.exists()) { FileInputStream fis
		 * = new FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while((len =
		 * fis.read(buffer))>0) { out.write(buffer,0,len); } out.closeEntry();
		 * fis.close(); } } accessory =
		 * accessory+sy+sm+sd+p.getAnweihui()+".zip"; out.close(); } catch
		 * (Exception e) { e.printStackTrace(); } }
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
		goalDutyDAO.insertSaveproplan(p);
		return json;
	}

	public String addWorkplan(Workplan p, String fileName, String rootPath) {
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
		String ympname = ym + p.getDutyMan() + "\\";
		File directory1 = new File(ym); // 根目录是否存在
		if (!directory1.exists())
			directory1.mkdir();
		File directory2 = new File(ympname); // 根目录是否存在
		if (!directory2.exists())
			directory2.mkdir();

		// 拷贝temp中的文件到正确的文件夹
		String[] newFile = fileName.split("\\*");
		for (String copyFile : newFile) {
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym + p.getDutyMan());
		}

		// 存入数据库的内容
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}

		String accessory = sy + sm + "*" + p.getDutyMan() + "*" + fileName;

		// 打包
		/*
		 * byte[] buffer = new byte[1024]; if(fileName.length()>0) { String
		 * strZipName = ympname + sy + sm + sd + p.getDutyMan()+".zip";
		 * System.out.println("压缩包名称:"+strZipName); try { ZipOutputStream out =
		 * new ZipOutputStream(new FileOutputStream(strZipName)); String[]
		 * allfile = fileName.split("\\*");
		 * System.out.println(Arrays.toString(allfile)); for(int i
		 * =0;i<allfile.length;i++) { File tempfile = new
		 * File(ympname+allfile[i]); if(tempfile.exists()) { FileInputStream fis
		 * = new FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while((len =
		 * fis.read(buffer))>0) { out.write(buffer,0,len); } out.closeEntry();
		 * fis.close(); } } accessory =
		 * accessory+sy+sm+sd+p.getDutyMan()+".zip"; out.close(); } catch
		 * (Exception e) { e.printStackTrace(); } }
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
		goalDutyDAO.insertWorkplan(p);
		return json;
	}

	public String editWorkplan(Workplan p, String fileName, String rootPath) {
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
			String ympname = ym + p.getDutyMan() + "\\";
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
			oldFoldName = sy + sm + "\\" + p.getDutyMan();
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			newAccessory = sy + sm + "*" + p.getDutyMan() + "*" + fileName;
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
		 * strZipName = rootPath+oldFoldName
		 * +"\\"+ sy+sm+sd+p.getDutyMan()+".zip"; try { ZipOutputStream out =
		 * new ZipOutputStream(new FileOutputStream(strZipName)); String[]
		 * allfile = fileName.split("\\*"); for(int i =0;i<allfile.length;i++) {
		 * File tempfile = new File(rootPath+ oldFoldName +"\\"+ allfile[i]);
		 * if(tempfile.exists()) { FileInputStream fis = new
		 * FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while((len =
		 * fis.read(buffer))>0) { out.write(buffer,0,len); } out.closeEntry();
		 * fis.close(); } } newAccessory =
		 * newAccessory+sy+sm+sd+p.getDutyMan()+".zip"; out.close(); } catch
		 * (Exception e) { e.printStackTrace(); } }
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

		goalDutyDAO.updateWorkplan(p);
		return json;
	}

	public String deleteWorkplan(String ID, String rootPath) {
		Workplan pro = new Workplan();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			List<Workplan> list = goalDutyDAO.checkWorkplanID((Integer.parseInt(temp[i])));
			// 删除附件
			if (list != null) {
				Workplan p = list.get(0);
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
			goalDutyDAO.deleteWorkplan(pro);
		}
		return json;
	}

	public String getWorkplanList(String findstr, String type, int start, int limit) {

		List<Workplan> list = goalDutyDAO.getWorkplanList(findstr, type, start, limit);
		int total = goalDutyDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Workplan p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"TaskContent\":\"" + p.getTaskContent() + "\",\"DutyMan\":\""
					+ p.getDutyMan() + "\",\"TaskScale\":\"" + p.getTaskScale() + "\",\"TimeYear\":\"" + p.getTimeYear()
					+ "\"";
			jsonStr += ",\"PlanFinishTime\":\"" + p.getPlanFinishTime() + "\",\"RealFinishTime\":\""
					+ p.getRealFinishTime() + "\",\"Type\":\"" + p.getType() + "\",\"FinishedTask\":\""
					+ p.getFinishedTask() + "\"";
			jsonStr += ",\"Accessory\":\"" + p.getAccessory() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String getMonthplanList(String findstr, int start, int limit, String projectName) {

		List<Monthplan> list = goalDutyDAO.getMonthplanList(findstr, start, limit, projectName);
		int total = goalDutyDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Monthplan p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"No\":\"" + p.getNo() + "\",\"Year\":\"" + p.getYear()
					+ "\",\"Month\":\"" + p.getMonth() + "\",\"Workload\":\"" + p.getWorkload() + "\",\"Completed\":\""
					+ p.getCompleted() + "\"";
			jsonStr += ",\"Content\":\"" + p.getContent() + "\",\"Manager\":\"" + p.getManager() + "\",\"PlanDate\":\""
					+ p.getPlanDate() + "\",\"RealDate\":\"" + p.getRealDate() + "\",\"Unit\":\"" + p.getUnit()
					+ "\",\"Completedsm\":\"" + p.getCompletedsm() + "\",\"ProjectName\":\"" + p.getProjectName()
					+ "\",\"Accessory\":\"" + p.getAccessory() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addMonthplan(Monthplan p, String fileName, String rootPath) {
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
		String ympname = ym + p.getManager() + "\\";
		File directory1 = new File(ym); // 根目录是否存在
		if (!directory1.exists())
			directory1.mkdir();
		File directory2 = new File(ympname); // 根目录是否存在
		if (!directory2.exists())
			directory2.mkdir();

		// 拷贝temp中的文件到正确的文件夹
		String[] newFile = fileName.split("\\*");
		for (String copyFile : newFile) {
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym + p.getManager());
		}

		// 存入数据库的内容
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}

		String accessory = sy + sm + "*" + p.getManager() + "*" + fileName;

		// 打包
		/*
		 * byte[] buffer = new byte[1024]; if(fileName.length()>0) { String
		 * strZipName = ympname + sy + sm + sd + p.getManager()+".zip";
		 * System.out.println("压缩包名称:"+strZipName); try { ZipOutputStream out =
		 * new ZipOutputStream(new FileOutputStream(strZipName)); String[]
		 * allfile = fileName.split("\\*");
		 * System.out.println(Arrays.toString(allfile)); for(int i
		 * =0;i<allfile.length;i++) { File tempfile = new
		 * File(ympname+allfile[i]); if(tempfile.exists()) { FileInputStream fis
		 * = new FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while((len =
		 * fis.read(buffer))>0) { out.write(buffer,0,len); } out.closeEntry();
		 * fis.close(); } } accessory =
		 * accessory+sy+sm+sd+p.getManager()+".zip"; out.close(); } catch
		 * (Exception e) { e.printStackTrace(); } }
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
		goalDutyDAO.insertMonthplan(p);
		return json;
	}

	public String editMonthplan(Monthplan p, String fileName, String rootPath) {
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
			String ympname = ym + p.getManager() + "\\";
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
			oldFoldName = sy + sm + "\\" + p.getManager();
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			newAccessory = sy + sm + "*" + p.getManager() + "*" + fileName;
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
		 * strZipName = rootPath+oldFoldName
		 * +"\\"+ sy+sm+sd+p.getManager()+".zip"; try { ZipOutputStream out =
		 * new ZipOutputStream(new FileOutputStream(strZipName)); String[]
		 * allfile = fileName.split("\\*"); for(int i =0;i<allfile.length;i++) {
		 * File tempfile = new File(rootPath+ oldFoldName +"\\"+ allfile[i]);
		 * if(tempfile.exists()) { FileInputStream fis = new
		 * FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while((len =
		 * fis.read(buffer))>0) { out.write(buffer,0,len); } out.closeEntry();
		 * fis.close(); } } newAccessory =
		 * newAccessory+sy+sm+sd+p.getManager()+".zip"; out.close(); } catch
		 * (Exception e) { e.printStackTrace(); } }
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

		goalDutyDAO.updateMonthplan(p);
		return json;
	}

	public String deleteMonthplan(String ID, String rootPath) {
		Monthplan pro = new Monthplan();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			List<Monthplan> list = goalDutyDAO.checkMonthplanID((Integer.parseInt(temp[i])));
			// 删除附件
			if (list != null) {
				Monthplan p = list.get(0);
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
			goalDutyDAO.deleteMonthplan(pro);
		}
		return json;
	}

	public String getFbplanList(String findstr, int start, int limit, String projectName) {

		List<Fbplan> list = goalDutyDAO.getFbplanList(findstr, start, limit, projectName);
		int total = goalDutyDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Fbplan p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"No\":\"" + p.getNo() + "\",\"Name\":\"" + p.getName()
					+ "\",\"PlanName\":\"" + p.getPlanName() + "\",\"Date\":\"" + p.getDate() + "\",\"ProjectName\":\""
					+ p.getProjectName() + "\"";
			jsonStr += ",\"Accessory\":\"" + p.getAccessory() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addFbplan(Fbplan p, String fileName, String rootPath) {
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
		String ympname = ym + p.getPlanName() + "\\";
		File directory1 = new File(ym); // 根目录是否存在
		if (!directory1.exists())
			directory1.mkdir();
		File directory2 = new File(ympname); // 根目录是否存在
		if (!directory2.exists())
			directory2.mkdir();

		// 拷贝temp中的文件到正确的文件夹
		String[] newFile = fileName.split("\\*");
		for (String copyFile : newFile) {
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym + p.getPlanName());
		}

		// 存入数据库的内容
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}

		String accessory = sy + sm + "*" + p.getPlanName() + "*" + fileName;

		// 打包
		/*
		 * byte[] buffer = new byte[1024]; if(fileName.length()>0) { String
		 * strZipName = ympname + sy + sm + sd + p.getPlanName()+".zip";
		 * System.out.println("压缩包名称:"+strZipName); try { ZipOutputStream out =
		 * new ZipOutputStream(new FileOutputStream(strZipName)); String[]
		 * allfile = fileName.split("\\*");
		 * System.out.println(Arrays.toString(allfile)); for(int i
		 * =0;i<allfile.length;i++) { File tempfile = new
		 * File(ympname+allfile[i]); if(tempfile.exists()) { FileInputStream fis
		 * = new FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while((len =
		 * fis.read(buffer))>0) { out.write(buffer,0,len); } out.closeEntry();
		 * fis.close(); } } accessory =
		 * accessory+sy+sm+sd+p.getPlanName()+".zip"; out.close(); } catch
		 * (Exception e) { e.printStackTrace(); } }
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
		goalDutyDAO.insertFbplan(p);
		return json;
	}

	public String editFbplan(Fbplan p, String fileName, String rootPath) {
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
			String ympname = ym + p.getPlanName() + "\\";
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
			oldFoldName = sy + sm + "\\" + p.getPlanName();
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			newAccessory = sy + sm + "*" + p.getPlanName() + "*" + fileName;
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
		 * strZipName = rootPath+oldFoldName
		 * +"\\"+ sy+sm+sd+p.getPlanName()+".zip"; try { ZipOutputStream out =
		 * new ZipOutputStream(new FileOutputStream(strZipName)); String[]
		 * allfile = fileName.split("\\*"); for(int i =0;i<allfile.length;i++) {
		 * File tempfile = new File(rootPath+ oldFoldName +"\\"+ allfile[i]);
		 * if(tempfile.exists()) { FileInputStream fis = new
		 * FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while((len =
		 * fis.read(buffer))>0) { out.write(buffer,0,len); } out.closeEntry();
		 * fis.close(); } } newAccessory =
		 * newAccessory+sy+sm+sd+p.getPlanName()+".zip"; out.close(); } catch
		 * (Exception e) { e.printStackTrace(); } }
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

		goalDutyDAO.updateFbplan(p);
		return json;
	}

	public String deleteFbplan(String ID, String rootPath) {
		Fbplan pro = new Fbplan();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			List<Fbplan> list = goalDutyDAO.checkFbplanID((Integer.parseInt(temp[i])));
			// 删除附件
			if (list != null) {
				Fbplan p = list.get(0);
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
			goalDutyDAO.deleteFbplan(pro);
		}
		return json;
	}

	public String getYearplanList(String findstr, int start, int limit, String projectName) {

		List<Yearplan> list = goalDutyDAO.getYearplanList(findstr, start, limit, projectName);
		int total = goalDutyDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Yearplan p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"Year\":\"" + p.getYear() + "\",\"Workload\":\""
					+ p.getWorkload() + "\",\"Completed\":\"" + p.getCompleted() + "\"";
			jsonStr += ",\"Content\":\"" + p.getContent() + "\",\"Manager\":\"" + p.getManager() + "\",\"PlanDate\":\""
					+ p.getPlanDate() + "\",\"RealDate\":\"" + p.getRealDate() + "\",\"ProjectName\":\""
					+ p.getProjectName() + "\",\"Accessory\":\"" + p.getAccessory() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addYearplan(Yearplan p, String fileName, String rootPath) {
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
		String ympname = ym + p.getManager() + "\\";
		File directory1 = new File(ym); // 根目录是否存在
		if (!directory1.exists())
			directory1.mkdir();
		File directory2 = new File(ympname); // 根目录是否存在
		if (!directory2.exists())
			directory2.mkdir();

		// 拷贝temp中的文件到正确的文件夹
		String[] newFile = fileName.split("\\*");
		for (String copyFile : newFile) {
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym + p.getManager());
		}

		// 存入数据库的内容
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}

		String accessory = sy + sm + "*" + p.getManager() + "*" + fileName;

		// 打包
		/*
		 * byte[] buffer = new byte[1024]; if(fileName.length()>0) { String
		 * strZipName = ympname + sy + sm + sd + p.getManager()+".zip";
		 * System.out.println("压缩包名称:"+strZipName); try { ZipOutputStream out =
		 * new ZipOutputStream(new FileOutputStream(strZipName)); String[]
		 * allfile = fileName.split("\\*");
		 * System.out.println(Arrays.toString(allfile)); for(int i
		 * =0;i<allfile.length;i++) { File tempfile = new
		 * File(ympname+allfile[i]); if(tempfile.exists()) { FileInputStream fis
		 * = new FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while((len =
		 * fis.read(buffer))>0) { out.write(buffer,0,len); } out.closeEntry();
		 * fis.close(); } } accessory =
		 * accessory+sy+sm+sd+p.getManager()+".zip"; out.close(); } catch
		 * (Exception e) { e.printStackTrace(); } }
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
		goalDutyDAO.insertYearplan(p);
		return json;
	}

	public String editYearplan(Yearplan p, String fileName, String rootPath) {
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
			String ympname = ym + p.getManager() + "\\";
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
			oldFoldName = sy + sm + "\\" + p.getManager();
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			newAccessory = sy + sm + "*" + p.getManager() + "*" + fileName;
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
		 * strZipName = rootPath+oldFoldName
		 * +"\\"+ sy+sm+sd+p.getManager()+".zip"; try { ZipOutputStream out =
		 * new ZipOutputStream(new FileOutputStream(strZipName)); String[]
		 * allfile = fileName.split("\\*"); for(int i =0;i<allfile.length;i++) {
		 * File tempfile = new File(rootPath+ oldFoldName +"\\"+ allfile[i]);
		 * if(tempfile.exists()) { FileInputStream fis = new
		 * FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while((len =
		 * fis.read(buffer))>0) { out.write(buffer,0,len); } out.closeEntry();
		 * fis.close(); } } newAccessory =
		 * newAccessory+sy+sm+sd+p.getManager()+".zip"; out.close(); } catch
		 * (Exception e) { e.printStackTrace(); } }
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

		goalDutyDAO.updateYearplan(p);
		return json;
	}

	public String deleteYearplan(String ID, String rootPath) {
		Yearplan pro = new Yearplan();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			List<Yearplan> list = goalDutyDAO.checkYearplanID((Integer.parseInt(temp[i])));
			// 删除附件
			if (list != null) {
				Yearplan p = list.get(0);
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
			goalDutyDAO.deleteYearplan(pro);
		}
		return json;
	}

	// jianglf-----------------------------------------------------------------------------------
	public String getSafetypromanagementList(String findstr, int start, int limit, String projectName) {

		List<Safetypromanagement> list = goalDutyDAO.getSafetypromanagementList(findstr, start, limit, projectName);
		int total = goalDutyDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Safetypromanagement p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"Name\":\"" + p.getName() + "\",\"Time\":\"" + p.getTime()
					+ "\",\"Sperson\":\"" + p.getSperson() + "\",\"Person\":\"" + p.getPerson()
					+ "\",\"ProjectName\":\"" + p.getProjectName() + "\"";
			jsonStr += ",\"Accessory\":\"" + p.getAccessory() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addSafetypromanagement(Safetypromanagement p, String fileName, String rootPath) {
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
		/*
		 * byte[] buffer = new byte[1024]; if(fileName.length()>0) { String
		 * strZipName = ympname + sy + sm + sd + p.getManager()+".zip";
		 * System.out.println("压缩包名称:"+strZipName); try { ZipOutputStream out =
		 * new ZipOutputStream(new FileOutputStream(strZipName)); String[]
		 * allfile = fileName.split("\\*");
		 * System.out.println(Arrays.toString(allfile)); for(int i
		 * =0;i<allfile.length;i++) { File tempfile = new
		 * File(ympname+allfile[i]); if(tempfile.exists()) { FileInputStream fis
		 * = new FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while((len =
		 * fis.read(buffer))>0) { out.write(buffer,0,len); } out.closeEntry();
		 * fis.close(); } } accessory =
		 * accessory+sy+sm+sd+p.getManager()+".zip"; out.close(); } catch
		 * (Exception e) { e.printStackTrace(); } }
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
		goalDutyDAO.insertSafetypromanagement(p);
		return json;
	}

	public String editSafetypromanagement(Safetypromanagement p, String fileName, String rootPath) {
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
			// deleteFile(rootPath + oldFoldName
			// +"\\"+oldFile[oldFile.length-1]);
		}
		// ----------打包------------------//
		/*
		 * byte[] buffer = new byte[1024]; if(fileName.length()>0) { String
		 * strZipName = rootPath+oldFoldName
		 * +"\\"+ sy+sm+sd+p.getManager()+".zip"; try { ZipOutputStream out =
		 * new ZipOutputStream(new FileOutputStream(strZipName)); String[]
		 * allfile = fileName.split("\\*"); for(int i =0;i<allfile.length;i++) {
		 * File tempfile = new File(rootPath+ oldFoldName +"\\"+ allfile[i]);
		 * if(tempfile.exists()) { FileInputStream fis = new
		 * FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while((len =
		 * fis.read(buffer))>0) { out.write(buffer,0,len); } out.closeEntry();
		 * fis.close(); } } newAccessory =
		 * newAccessory+sy+sm+sd+p.getManager()+".zip"; out.close(); } catch
		 * (Exception e) { e.printStackTrace(); } }
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

		goalDutyDAO.updateSafetypromanagement(p);
		return json;
	}

	public String deleteSafetypromanagement(String ID, String rootPath) {
		Safetypromanagement pro = new Safetypromanagement();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			List<Safetypromanagement> list = goalDutyDAO.checkSafetypromanagementID((Integer.parseInt(temp[i])));
			// 删除附件
			if (list != null) {
				Safetypromanagement p = list.get(0);
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
			goalDutyDAO.deleteSafetypromanagement(pro);
		}
		return json;
	}

	public String getSafetypromanagementfbList(String findstr, int start, int limit, String projectName) {

		List<Safetypromanagementfb> list = goalDutyDAO.getSafetypromanagementfbList(findstr, start, limit, projectName);
		int total = goalDutyDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Safetypromanagementfb p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"Name\":\"" + p.getName() + "\",\"FbName\":\"" + p.getFbName()
					+ "\",\"Time\":\"" + p.getTime() + "\",\"Sperson\":\"" + p.getSperson() + "\",\"Person\":\""
					+ p.getPerson() + "\",\"ProjectName\":\"" + p.getProjectName() + "\"";
			jsonStr += ",\"Accessory\":\"" + p.getAccessory() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addSafetypromanagementfb(Safetypromanagementfb p, String fileName, String rootPath) {
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
		/*
		 * byte[] buffer = new byte[1024]; if(fileName.length()>0) { String
		 * strZipName = ympname + sy + sm + sd + p.getManager()+".zip";
		 * System.out.println("压缩包名称:"+strZipName); try { ZipOutputStream out =
		 * new ZipOutputStream(new FileOutputStream(strZipName)); String[]
		 * allfile = fileName.split("\\*");
		 * System.out.println(Arrays.toString(allfile)); for(int i
		 * =0;i<allfile.length;i++) { File tempfile = new
		 * File(ympname+allfile[i]); if(tempfile.exists()) { FileInputStream fis
		 * = new FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while((len =
		 * fis.read(buffer))>0) { out.write(buffer,0,len); } out.closeEntry();
		 * fis.close(); } } accessory =
		 * accessory+sy+sm+sd+p.getManager()+".zip"; out.close(); } catch
		 * (Exception e) { e.printStackTrace(); } }
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
		goalDutyDAO.insertSafetypromanagementfb(p);
		return json;
	}

	public String editSafetypromanagementfb(Safetypromanagementfb p, String fileName, String rootPath) {
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
			// deleteFile(rootPath + oldFoldName
			// +"\\"+oldFile[oldFile.length-1]);
		}
		// ----------打包------------------//
		/*
		 * byte[] buffer = new byte[1024]; if(fileName.length()>0) { String
		 * strZipName = rootPath+oldFoldName
		 * +"\\"+ sy+sm+sd+p.getManager()+".zip"; try { ZipOutputStream out =
		 * new ZipOutputStream(new FileOutputStream(strZipName)); String[]
		 * allfile = fileName.split("\\*"); for(int i =0;i<allfile.length;i++) {
		 * File tempfile = new File(rootPath+ oldFoldName +"\\"+ allfile[i]);
		 * if(tempfile.exists()) { FileInputStream fis = new
		 * FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while((len =
		 * fis.read(buffer))>0) { out.write(buffer,0,len); } out.closeEntry();
		 * fis.close(); } } newAccessory =
		 * newAccessory+sy+sm+sd+p.getManager()+".zip"; out.close(); } catch
		 * (Exception e) { e.printStackTrace(); } }
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

		goalDutyDAO.updateSafetypromanagementfb(p);
		return json;
	}

	public String deleteSafetypromanagementfb(String ID, String rootPath) {
		Safetypromanagementfb pro = new Safetypromanagementfb();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			List<Safetypromanagementfb> list = goalDutyDAO.checkSafetypromanagementfbID((Integer.parseInt(temp[i])));
			// 删除附件
			if (list != null) {
				Safetypromanagementfb p = list.get(0);
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
			goalDutyDAO.deleteSafetypromanagementfb(pro);
		}
		return json;
	}

	public String getThreeworkplanList(String findstr, int start, int limit, String projectName) {

		List<Threeworkplan> list = goalDutyDAO.getThreeworkplanList(findstr, start, limit, projectName);
		int total = goalDutyDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Threeworkplan p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"No\":\"" + p.getNo() + "\",\"Year\":\"" + p.getYear()
					+ "\",\"Workload\":\"" + p.getWorkload() + "\",\"Completed\":\"" + p.getCompleted() + "\"";
			jsonStr += ",\"Content\":\"" + p.getContent() + "\",\"Manager\":\"" + p.getManager() + "\",\"PlanDate\":\""
					+ p.getPlanDate() + "\",\"RealDate\":\"" + p.getRealDate() + "\",\"ProjectName\":\""
					+ p.getProjectName() + "\",\"Accessory\":\"" + p.getAccessory() + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}

	public String addThreeworkplan(Threeworkplan p, String fileName, String rootPath) {
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
		String ympname = ym + p.getManager() + "\\";
		File directory1 = new File(ym); // 根目录是否存在
		if (!directory1.exists())
			directory1.mkdir();
		File directory2 = new File(ympname); // 根目录是否存在
		if (!directory2.exists())
			directory2.mkdir();

		// 拷贝temp中的文件到正确的文件夹
		String[] newFile = fileName.split("\\*");
		for (String copyFile : newFile) {
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym + p.getManager());
		}

		// 存入数据库的内容
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}

		String accessory = sy + sm + "*" + p.getManager() + "*" + fileName;

		// 打包
		/*
		 * byte[] buffer = new byte[1024]; if(fileName.length()>0) { String
		 * strZipName = ympname + sy + sm + sd + p.getManager()+".zip";
		 * System.out.println("压缩包名称:"+strZipName); try { ZipOutputStream out =
		 * new ZipOutputStream(new FileOutputStream(strZipName)); String[]
		 * allfile = fileName.split("\\*");
		 * System.out.println(Arrays.toString(allfile)); for(int i
		 * =0;i<allfile.length;i++) { File tempfile = new
		 * File(ympname+allfile[i]); if(tempfile.exists()) { FileInputStream fis
		 * = new FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while((len =
		 * fis.read(buffer))>0) { out.write(buffer,0,len); } out.closeEntry();
		 * fis.close(); } } accessory =
		 * accessory+sy+sm+sd+p.getManager()+".zip"; out.close(); } catch
		 * (Exception e) { e.printStackTrace(); } }
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
		goalDutyDAO.insertThreeworkplan(p);
		return json;
	}

	public String editThreeworkplan(Threeworkplan p, String fileName, String rootPath) {
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
			String ympname = ym + p.getManager() + "\\";
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
			oldFoldName = sy + sm + "\\" + p.getManager();
		}
		String[] newFile = fileName.split("\\*");
		String newAccessory;
		if (oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1] + "*" + fileName;
		} else {
			newAccessory = sy + sm + "*" + p.getManager() + "*" + fileName;
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
		 * strZipName = rootPath+oldFoldName
		 * +"\\"+ sy+sm+sd+p.getManager()+".zip"; try { ZipOutputStream out =
		 * new ZipOutputStream(new FileOutputStream(strZipName)); String[]
		 * allfile = fileName.split("\\*"); for(int i =0;i<allfile.length;i++) {
		 * File tempfile = new File(rootPath+ oldFoldName +"\\"+ allfile[i]);
		 * if(tempfile.exists()) { FileInputStream fis = new
		 * FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while((len =
		 * fis.read(buffer))>0) { out.write(buffer,0,len); } out.closeEntry();
		 * fis.close(); } } newAccessory =
		 * newAccessory+sy+sm+sd+p.getManager()+".zip"; out.close(); } catch
		 * (Exception e) { e.printStackTrace(); } }
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

		goalDutyDAO.updateThreeworkplan(p);
		return json;
	}
	
	
	
	

	public String deleteThreeworkplan(String ID, String rootPath) {
		Threeworkplan pro = new Threeworkplan();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			List<Threeworkplan> list = goalDutyDAO.checkThreeworkplanID((Integer.parseInt(temp[i])));
			// 删除附件
			if (list != null) {
				Threeworkplan p = list.get(0);
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
			goalDutyDAO.deleteThreeworkplan(pro);
		}
		return json;
	}
	// end--------------------------------------------------------------------------------------

	public String addFileUpload(Fileupload pro, String fileName, String rootPath, String title) {
		String json = "{\"success\":\"true\"}";
		

		// 建立月份和项目名文件夹
		String ym = rootPath + "fileUpload" + "\\";
		String ympname = ym + title + "\\";
		File directory1 = new File(ym); // 根目录是否存在
		if (!directory1.exists())
			directory1.mkdir();
		File directory2 = new File(ympname); // 根目录是否存在
		if (!directory2.exists())
			directory2.mkdir();

		// 拷贝temp中的文件到正确的文件夹
		String[] newFile = fileName.split("\\*");
		for (String copyFile : newFile) {
			cutGeneralFile(rootPath + "temp\\" + copyFile, ym + title);
		}

		// 存入数据库的内容
		if (fileName != null && fileName.length() > 0) {
			if (fileName.charAt(fileName.length() - 1) == '*')
				fileName = fileName.substring(0, fileName.length() - 1);
		}

		String accessory = fileName;

		// 打包
		/*
		 * byte[] buffer = new byte[1024]; if(fileName.length()>0) { String
		 * strZipName = ympname + sy + sm + sd + p.getManager()+".zip";
		 * System.out.println("压缩包名称:"+strZipName); try { ZipOutputStream out =
		 * new ZipOutputStream(new FileOutputStream(strZipName)); String[]
		 * allfile = fileName.split("\\*");
		 * System.out.println(Arrays.toString(allfile)); for(int i
		 * =0;i<allfile.length;i++) { File tempfile = new
		 * File(ympname+allfile[i]); if(tempfile.exists()) { FileInputStream fis
		 * = new FileInputStream(tempfile); out.putNextEntry(new
		 * ZipEntry(tempfile.getName())); int len; while((len =
		 * fis.read(buffer))>0) { out.write(buffer,0,len); } out.closeEntry();
		 * fis.close(); } } accessory =
		 * accessory+sy+sm+sd+p.getManager()+".zip"; out.close(); } catch
		 * (Exception e) { e.printStackTrace(); } }
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

		pro.setFileName(accessory);
		goalDutyDAO.insertFileUpload(pro);
		return json;
	}
	
	public String getFileUploadNameList(String title, String projectName) {
		List<Fileupload> list = goalDutyDAO.getFileUploadNameList(title, projectName);
		String json = "[";
		for(int i=0;i<list.size();i++) {
			if(i>0)
				json +=",";
			Fileupload fp = list.get(i);
			json += "{\"FileName\":\"" + fp.getFileName() + "\",\"Year\":\"" + fp.getYear() + "\",\"Month\":\"" + fp.getMonth() + "\",\"ProjectName\":\"" + fp.getProjectName() + "\"}"; 
		}
		json +="]";
		return json;
	}
	
	public String deleteFileUpload(String file,String title, String rootPath, String projectName) {
		//Fileupload pro = new Fileupload();
		String json = "{\"success\":\"true\"}";
		//String[] temp = ID.split(",");
		
		Fileupload list = goalDutyDAO.findByFileNameAndTitle(file,title,projectName);
			// 删除附件
		if (list != null) {
				
			if (file != null && file.length() != 0) {
					
				String path = rootPath + "fileUpload\\" + title + "\\";
				System.out.println("--path:" + path);
				deleteFile(path + file);
			}
		}
		goalDutyDAO.deleteFileUpload(list);
		return json;
	}
}
