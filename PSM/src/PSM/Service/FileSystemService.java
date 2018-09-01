package PSM.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import PSM.DAO.FileSystemDAO;
import PSM.Tool.ExcelToPdf;
import PSM.Tool.InsertTask;
import PSM.Tool.ReadExcel;
import PSM.Tool.WordToPdf;
import hibernate.Commonlaw;
import hibernate.Commonfile;
import hibernate.Commonsystem;
import hibernate.Exemplaryfile;
import hibernate.Fbsystem;
import hibernate.Prodepartdoc;
import hibernate.Risksafepg;
import hibernate.Standmodel;

public class FileSystemService 
{
	private FileSystemDAO fileSystemDAO;

	public FileSystemDAO getFileSystemDAO() {
		return fileSystemDAO;
	}

	public void setFileSystemDAO(FileSystemDAO fileSystemDAO) {
		this.fileSystemDAO = fileSystemDAO;
	}

	public String getHSEinfo(int id) {
		Commonfile p = fileSystemDAO.getCommonfile(id);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String jsonStr = "{\"success\":\"true\",\"data\":{\"No\":\"" + p.getNo() + "\",\"Name\":\"" + p.getName() + "\",\"FromUnit\":\"" + p.getFromUnit() + "\"}}";
		return jsonStr;
	}
	
	//刘迟优化，并发插入，提升约4倍，原始插入153数据约5s，并发插入后约1-2s
	public synchronized int importExcel(String type, String rootPath, String fileName) {
		String path = rootPath + "temp\\" + fileName;
		int total = 0;
		switch (type) {
		case "法律":
		case "国务院行政法规":
		case "部门规章、地方性法规":
		case "规范性文件":
		case "国家标准、行业标准":
			System.out.println("开始时间 ：" + System.currentTimeMillis());
			/*long start1 = System.currentTimeMillis();*/
			List<Commonlaw> commonlaws = ReadExcel.readCommonlawExcel(path, type);
			/*long end1 = System.currentTimeMillis();
			System.out.println("读取所用时间 ：" + (end1-start1));*/
			/*long start2 = System.currentTimeMillis();
			for (Commonlaw commonlaw : commonlaws)
				fileSystemDAO.insertCommonlaw(commonlaw);
			long end2 = System.currentTimeMillis();
			System.out.println("插入所用时间 ：" + (end2-start2));*/
			
			//
			/*System.out.println("并发插入");
			long start3 = System.currentTimeMillis();*/
			InsertTask task = new InsertTask(fileSystemDAO,commonlaws, 0, commonlaws.size()-1);
			ForkJoinPool pool = new ForkJoinPool();
			pool.execute(task);
			do {
				/*System.out.printf("Main: Thread Count: %d\n",pool.getActiveThreadCount());
				System.out.printf("Main: Thread Steal: %d\n",pool.getStealCount());
				System.out.printf("Main: Parallelism: %d\n",pool.getParallelism());
				try {
				TimeUnit.MILLISECONDS.sleep(5);
				} catch (InterruptedException e) {
				e.printStackTrace();
				}*/
				} while (!task.isDone());
			pool.shutdown();
			/*if (task.isCompletedAbnormally()){
				System.out.printf("Main: The process has completed normally.\n");
				}*/
			long end3 = System.currentTimeMillis();
			//System.out.println("并发插入所用时间 ：" + (end3-start3));
			total = commonlaws.size();
			break;
		case "集团公司制度":
		case "湖北公司制度":
		case "湖北院制度":
		case "项目部参考HSE制度":
			List<Commonfile> commonfiles = ReadExcel.readCommonfileExcel(path, type);
			for (Commonfile commonfile : commonfiles)
				fileSystemDAO.insertCommonfile(commonfile);
			total = commonfiles.size();
			break;
		default:
			List<Commonfile> commonfiles1 = ReadExcel.readCommonfileExcel(path, type);
			for (Commonfile commonfile : commonfiles1)
				fileSystemDAO.insertCommonfile(commonfile);
			total = commonfiles1.size();
			break;
		}
		return total;
	}
	
	public String getProjectList(String findstr, int start,int limit)
	{
		List <Exemplaryfile> list = fileSystemDAO.getProjectList(findstr, start,limit);
		int total = fileSystemDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for(int i = 0; i < list.size(); ++i)
		{
			if(i > 0)
				jsonStr += ",";
			Exemplaryfile p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"No\":\"" + p.getNo() + "\",\"Name\":\"" + p.getName() + "\",\"FromUnit\":\"" + p.getFromUnit() + "\",\"HowFast\":\"" + p.getHowFast() + "\"";
			jsonStr += ",\"FileRequire\":\"" + p.getFileRequire() + "\",\"WriteOpinion\":\"" + p.getWriteOpinion() + "\",\"Accessory\":\"" + p.getAccessory()  + "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
		return jsonStr;
	}
	
	public String getCommonlawList(String findstr, int start, int limit, String type) 
	{
		List<Commonlaw> list = fileSystemDAO.getCommonlawList(findstr, start, limit, type);
		int total = fileSystemDAO.datacount;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for(int i = 0; i < list.size(); ++i)
		{
			if(i > 0)
				jsonStr += ",";
			Commonlaw p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"No\":\"" + p.getNo() + "\",\"Name\":\"" + p.getName().replace("\"", "'") + "\",\"FromUnit\":\"" + p.getFromUnit() + "\",\"EnactDate\":\"" + sdf.format(p.getEnactDate());
			jsonStr += "\",\"ApplyDate\":\"" + sdf.format(p.getApplyDate()) + "\",\"Accessory\":\"" + p.getAccessory() + "\",\"Type\":\"" + p.getType() + "\"}";
		}
		jsonStr += "]}";
		return jsonStr;
	}
	
	public String getCommonfileList(String findstr, int start, int limit, String type)
	{
		List<Commonfile> list = fileSystemDAO.getCommonfileList(findstr, start, limit, type);
		int total = fileSystemDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for(int i = 0; i < list.size(); ++i)
		{
			if(i > 0)
				jsonStr += ",";
			Commonfile p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"No\":\"" + p.getNo() + "\",\"Name\":\"" + p.getName() + "\",\"FromUnit\":\"" + p.getFromUnit();
			jsonStr += "\",\"Accessory\":\"" + p.getAccessory() + "\",\"Type\":\"" + p.getType() + "\"}";
		}
		jsonStr += "]}";
		return jsonStr;
	}
	
	public String getCommonsystemList(String findstr, int start, int limit, String type)
	{
		List<Commonsystem> list = fileSystemDAO.getCommonsystemList(findstr, start, limit, type);
		int total = fileSystemDAO.datacount;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for(int i = 0; i < list.size(); ++i)
		{
			if(i > 0)
				jsonStr += ",";
			Commonsystem p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"SendDate\":\"" + sdf.format(p.getSendDate()) + "\",\"ReceiveDate\":\"" + (p.getReceiveDate() == null ? "" : sdf.format(p.getReceiveDate()) );
			jsonStr += "\",\"No\":\"" + p.getNo() + "\",\"Name\":\"" + p.getName() + "\",\"FromUnit\":\"" + p.getFromUnit() + "\",\"Accessory\":\"" + p.getAccessory();
			jsonStr += "\",\"Urgency\":\"" + p.getUrgency() + "\",\"Requirement\":\"" + p.getRequirement() + "\",\"Opinion\":\"" + p.getOpinion();
			jsonStr += "\",\"Type\":\"" + p.getType() + "\"}";
		}
		jsonStr += "]}";
		return jsonStr;
	}
	
	public String getProdepartdocList(String findstr, int start, int limit, String projectName)
	{
		List<Prodepartdoc> list = fileSystemDAO.getProdepartdocList(findstr, start, limit, projectName);
		int total = fileSystemDAO.datacount;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for(int i = 0; i < list.size(); ++i) {
			if(i > 0) jsonStr += ",";
			Prodepartdoc p = list.get(i);
			String accessory = p.getAccessory();
			String[] strarray = accessory.split("\\*");
			String filename = strarray[strarray.length - 1];
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"Accessory\":\"" + p.getAccessory() + "\",\"UploadDate\":\"" + sdf.format(p.getUploadDate());
			jsonStr += "\",\"Filename\":\"" + filename + "\",\"Type\":\"" + p.getType() + "\"}";
		}
		jsonStr += "]}";
		return jsonStr;
	}
	
	public String getFbsystemList(String findstr, int start, int limit, String projectName)
	{
		List<Fbsystem> list = fileSystemDAO.getFbsystemList(findstr, start, limit, projectName);
		int total = fileSystemDAO.datacount;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for(int i = 0; i < list.size(); ++i) {
			if(i > 0) jsonStr += ",";
			Fbsystem p = list.get(i);
			String accessory = p.getAccessory();
			String[] strarray = accessory.split("\\*");
			String filename = strarray[strarray.length - 1];
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"Accessory\":\"" + p.getAccessory() + "\",\"UploadDate\":\"" + sdf.format(p.getUploadDate());
			jsonStr += "\",\"Filename\":\"" + filename + "\",\"FbUnit\":\"" + p.getFbUnit() + "\"}";
		}
		jsonStr += "]}";
		return jsonStr;
	}
	
	
	
	// 添加
	
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
		
      	//打包
		/*
      	byte[] buffer = new byte[1024];   
      	if(fileName.length()>0)
      	{      	
      		String strZipName = ympname + sy + sm + sd + pName + ".zip"; 
      		System.out.println("压缩包名称:" + strZipName);
      		try {
      			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(strZipName)); 
          		String[] allfile = fileName.split("\\*");
          		System.out.println(Arrays.toString(allfile));
          		for(int i =0;i<allfile.length;i++)
          		{
          			File tempfile = new File(ympname+allfile[i]);
          			if(tempfile.exists())
          			{
          				FileInputStream fis = new FileInputStream(tempfile); 
              			out.putNextEntry(new ZipEntry(tempfile.getName()));   
              			int len;   
              			while((len = fis.read(buffer))>0) {   
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
      			}
      			else if(temp.endsWith(".docx")) {
      				WordToPdf.translateThread(ympname+temp, ympname+temp.substring(0,temp.length()-5)+".pdf");
      			}
      		}
      	}      	
      	return accessory;
	}
	
	public String addProject(Exemplaryfile p, String fileName, String rootPath)
	{
		String json = "{\"success\":\"true\"}";		
      	String accessory = addAccessory(rootPath, fileName, p.getName());
      	if(fileName == null || fileName.length()<=0) {
			System.out.println("----is null---");
			p.setAccessory(fileName);
		}
		else {
			p.setAccessory(accessory);
		}
        fileSystemDAO.insertProject(p);    
		return json;
	}
	
	public String addCommonlaw(Commonlaw p, String fileName, String rootPath)
	{
		String json = "{\"success\":\"true\"}";
		String accessory = addAccessory(rootPath, fileName, p.getName());
      	if(fileName == null || fileName.length()<=0) {
			System.out.println("----is null---");
			p.setAccessory(fileName);
		}
		else {
			p.setAccessory(accessory);
		}
        fileSystemDAO.insertCommonlaw(p);    
		return json;
	}
	
	public String addCommonfile(Commonfile p, String fileName, String rootPath)
	{
		String json = "{\"success\":\"true\"}";
		String accessory = addAccessory(rootPath, fileName, p.getName());
      	if(fileName == null || fileName.length()<=0) {
			System.out.println("----is null---");
			p.setAccessory(fileName);
		}
		else {
			p.setAccessory(accessory);
		}
        fileSystemDAO.insertCommonfile(p);    
		return json;
	}
	
	public String addCommonsystem(Commonsystem p,String fileName, String rootPath)
	{
		String json = "{\"success\":\"true\"}";
		String accessory = addAccessory(rootPath, fileName, p.getName());
      	if(fileName == null || fileName.length()<=0) {
			System.out.println("----is null---");
			p.setAccessory(fileName);
		}
		else {
			p.setAccessory(accessory);
		}
        fileSystemDAO.insertCommonsystem(p);    
		return json;
	}
	
	public String addProdepartdoc(Prodepartdoc p, String fileName, String rootPath)
	{
		String json = "{\"success\":\"true\"}";
		String accessory = addAccessory(rootPath, fileName, p.getType());
      	if(fileName == null || fileName.length()<=0) {
			System.out.println("----is null---");
			p.setAccessory(fileName);
		}
		else {
			p.setAccessory(accessory);
		}
        fileSystemDAO.insertProdepartdoc(p);
		return json;
	}
	
	public String addFbsystem(Fbsystem p, String fileName, String rootPath)
	{
		String json = "{\"success\":\"true\"}";
		String accessory = addAccessory(rootPath, fileName, p.getFbUnit());
      	if(fileName == null || fileName.length()<=0) {
			System.out.println("----is null---");
			p.setAccessory(fileName);
		}
		else {
			p.setAccessory(accessory);
		}
        fileSystemDAO.insertFbsystem(p);
		return json;
	}
	
	
	
	// 编辑
	
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
		}
		else {
			oldFoldName = sy + sm + "\\" + pName;
		}	
		String[] newFile = fileName.split("\\*");		
		String newAccessory;
		if(oldAccessoryNone == 0) {
			newAccessory = oldFile[0] + "*" + oldFile[1]  +"*"+fileName;
		}
		else {
			newAccessory = sy + sm + "*" + pName  + "*" + fileName;
		}
        
		//------------------------删除列表中不存在的文件---------------------------//
		/*for(int i = 2; i < oldFileLength - 1; i++) {
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
		}*/
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
				cutGeneralFile(rootPath +"temp\\"+oneFile,rootPath + oldFoldName);
			}			
		}
		if(oldAccessoryNone == 0) {
			//deleteFile(rootPath + oldFoldName +"\\"+oldFile[oldFile.length-1]);
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
              			while((len = fis.read(buffer))>0) {   
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
      	if(fileName.length()!=0)
      	{
      		String[] allFile = fileName.split("\\*");
      		for(String tempFile : allFile)
      		{
      			if(tempFile.endsWith(".doc"))
      			{
      				WordToPdf.translateThread(rootPath + oldFoldName +"\\"+tempFile, rootPath + oldFoldName +"\\"+tempFile.substring(0,tempFile.length()-4)+".pdf"); 			
      			}
      			else if(tempFile.endsWith(".docx"))
      			{
      				WordToPdf.translateThread(rootPath + oldFoldName +"\\"+tempFile, rootPath + oldFoldName +"\\"+tempFile.substring(0,tempFile.length()-5)+".pdf");
      			}
      		}
      	}      	
      	return newAccessory;
	}
	
	public String editProject(Exemplaryfile p, String fileName, String rootPath)
	{		
		String json = "{\"success\":\"true\"}";
		String newAccessory = editAccessory(rootPath, fileName, p.getName(), p.getAccessory());
      	if(fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			p.setAccessory(fileName);
		}
		else {
			p.setAccessory(newAccessory);
		}
        fileSystemDAO.updateProject(p);    
		return json;
	}
	
	public String editCommonlaw(Commonlaw p,String fileName,String rootPath)
	{
		String json = "{\"success\":\"true\"}";
		String newAccessory = editAccessory(rootPath, fileName, p.getName(), p.getAccessory());
      	if(fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			p.setAccessory(fileName);
		}
		else {
			p.setAccessory(newAccessory);
		}
        fileSystemDAO.updateCommonlaw(p);    
		return json;
	}
	
	public String editCommonfile(Commonfile p,String fileName,String rootPath)
	{
		String json = "{\"success\":\"true\"}";
		String newAccessory = editAccessory(rootPath, fileName, p.getName(), p.getAccessory());
      	if(fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			p.setAccessory(fileName);
		}
		else {
			p.setAccessory(newAccessory);
		}
        fileSystemDAO.updateCommonfile(p);    
		return json;
	}
	
	public String editCommonsystem(Commonsystem p,String fileName,String rootPath)
	{
		String json = "{\"success\":\"true\"}";
		String newAccessory = editAccessory(rootPath, fileName, p.getName(), p.getAccessory());
      	if(fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			p.setAccessory(fileName);
		}
		else {
			p.setAccessory(newAccessory);
		}
        fileSystemDAO.updateCommonsystem(p);    
		return json;
	}
	
	public String editProdepartdoc(Prodepartdoc p, String fileName, String rootPath)
	{
		String json = "{\"success\":\"true\"}";
		String newAccessory = editAccessory(rootPath, fileName, p.getType(), p.getAccessory());
      	if(fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			p.setAccessory(fileName);
		}
		else {
			p.setAccessory(newAccessory);
		}
        fileSystemDAO.updateProdepartdoc(p);    
		return json;
	}
	
	public String editFbsystem(Fbsystem p, String fileName, String rootPath)
	{
		String json = "{\"success\":\"true\"}";
		String newAccessory = editAccessory(rootPath, fileName, p.getFbUnit(), p.getAccessory());
      	if(fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			p.setAccessory(fileName);
		}
		else {
			p.setAccessory(newAccessory);
		}
        fileSystemDAO.updateFbsystem(p);    
		return json;
	}
	
	
	
	
	// 删除
	
	// 删除指定附件、目录或文件
	private void deleteAccessory(String rootPath, String fileName) {
		System.out.println(fileName + "length"+fileName.length());
		if(fileName != null && fileName.length() != 0)
		{
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
	
	public String deleteProject(String ID,String rootPath)
	{
		Exemplaryfile pro = new Exemplaryfile();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for(int i = 0; i < temp.length; i++)
		{
			List<Exemplaryfile> list = fileSystemDAO.checkProjectID((Integer.parseInt(temp[i])));
			//删除附件
			if(list != null) {
				Exemplaryfile p = list.get(0);
				deleteAccessory(rootPath, p.getAccessory());
			}
			pro.setId(Integer.parseInt(temp[i]));
			fileSystemDAO.deleteProject(pro);
		} 
		return json;
	}
	
	public String deleteCommonlaw(String ID,String rootPath)
	{
		Commonlaw pro = new Commonlaw();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for(int i = 0; i < temp.length; i++) {
			//删除附件
			Commonlaw p = fileSystemDAO.getCommonlaw((Integer.parseInt(temp[i])));
			if(p != null)
				deleteAccessory(rootPath, p.getAccessory());
			pro.setId(Integer.parseInt(temp[i]));
			fileSystemDAO.deleteCommonlaw(pro);
		} 
		return json;
	}
	
	public String deleteCommonfile(String ID,String rootPath)
	{
		Commonfile pro = new Commonfile();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for(int i = 0; i < temp.length; i++) {
			//删除附件
			Commonfile p = fileSystemDAO.getCommonfile((Integer.parseInt(temp[i])));
			if(p != null) {
				deleteAccessory(rootPath, p.getAccessory());
			}
			pro.setId(Integer.parseInt(temp[i]));
			fileSystemDAO.deleteCommonfile(pro);
		} 
		return json;
	}
	
	public String deleteCommonsystem(String ID,String rootPath) {
		Commonsystem pro = new Commonsystem();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for(int i = 0; i < temp.length; i++) {
			//删除附件
			Commonsystem p = fileSystemDAO.getCommonsystem((Integer.parseInt(temp[i])));;
			if(p != null) {
				deleteAccessory(rootPath, p.getAccessory());
			}
			pro.setId(Integer.parseInt(temp[i]));
			fileSystemDAO.deleteCommonsystem(pro);
		} 
		return json;
	}
	
	public String deleteProdepartdoc(String ID, String rootPath)
	{
		Prodepartdoc pro = new Prodepartdoc();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for(int i = 0; i < temp.length; i++)
		{
			//删除附件
			Prodepartdoc p = fileSystemDAO.getProdepartdoc((Integer.parseInt(temp[i])));
			if(p != null) {
				deleteAccessory(rootPath, p.getAccessory());
			}
			pro.setId(Integer.parseInt(temp[i]));
			fileSystemDAO.deleteProdepartdoc(pro);
		} 
		return json;
	}
	
	public String deleteFbsystem(String ID, String rootPath)
	{
		Fbsystem pro = new Fbsystem();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for(int i = 0; i < temp.length; i++) {
			//删除附件
			Fbsystem p = fileSystemDAO.getFbsystem((Integer.parseInt(temp[i])));
			if(p != null) {
				deleteAccessory(rootPath, p.getAccessory());
			}
			pro.setId(Integer.parseInt(temp[i]));
			fileSystemDAO.deleteFbsystem(pro);
		} 
		return json;
	}
	
	public String getFileInfo(String filePath)
	{
		 String fileLength = "0";
		 File file = new File(filePath);
		 System.out.println(filePath);
		 if(file.exists())
		 {
			 fileLength = Long.toString(file.length());
		 }
		 return fileLength;
	}
	
	public String deleteAllFile(String ppID,String fileName, String rootPath)
	{
		String[] newFile = fileName.split("\\*");
		String json = "{\"success\":\"true\"}";
		
		if(ppID == null || ppID.length()<=0)//没有ID 说明是添加 直接去temp全部删除
		{
			for(String existFileInList : newFile){
				deleteFile(rootPath+"temp\\"+existFileInList);
			}
		}
		else
		{
			String[] strKey = null;
			List<Exemplaryfile> list = fileSystemDAO.checkProjectID((Integer.parseInt(ppID)));
			if(list.size()>0)
			{
				Exemplaryfile p = list.get(0);
				String a = p.getAccessory();
        		if( a !=null && a.length()>0 )
        		{
        			strKey = a.split("\\*");
        			for(String existFileInList : newFile)
        			{ 	
                    	boolean hasFileInDB = false;	
                    	for(int i = 2;i<strKey.length-1; i++ )
                    	{       			
                			String existFileInDB = strKey[i];
                			//列表中是否存在该文件	        				        			
            				if(existFileInDB.equals(existFileInList))
            				{								
            					hasFileInDB = true;			
            					break;	        			
                			}	        		
                		}
                    	if(!hasFileInDB)
                    	{	
            				deleteFile(rootPath+"temp\\"+existFileInList);
            			}
                    }
        		}
			}
			
		}		
		return json;
	}
	
	public String deleteOneFile(String ppID,String fileName, String rootPath)
	{
		String json = "{\"success\":\"true\"}";
		String deletePath = rootPath+"temp"+"\\"+fileName;
		if(ppID == null||ppID.length()<=0)//如果是新增才删除
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
	public boolean deleteFile(String sPath){
		boolean flag = false;
		File file = new File(sPath);
		if(!file.exists())
			return false;
		if(sPath.endsWith(".doc"))
		{
			String strpdf = sPath.substring(0,sPath.length()-4)+".pdf";
			File filepdf = new File(strpdf);
			if(filepdf.isFile()&&filepdf.exists()){
				filepdf.delete();
			}
		}
		if(sPath.endsWith(".docx"))
		{
			String strpdf = sPath.substring(0,sPath.length()-5)+".pdf";
			File filepdf = new File(strpdf);
			if(filepdf.isFile()&&filepdf.exists()){
				filepdf.delete();
			}
		}
		//路径为文件且不为空则删除
		if(file.isFile()&&file.exists()){
			file.delete();
			flag = true;
		}
		return flag;
	}
	
	public boolean deleteDirectory(String sPath){
		//如果sPath不以文件分隔符结尾，自动添加文件分隔符
		if(!sPath.endsWith(File.separator)){
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);
		
		//如果dir对应的文件不存在，或者不是一个目录，则退出
		if(!dirFile.exists() || !dirFile.isDirectory()){
			return false;
		}
		boolean flag = true;
		
		//删除文件夹下所有的文件(包括子目录)
		File[] files = dirFile.listFiles();
		for(int i=0; i<files.length; i++){
			//删除子文件
			if(files[i].isFile()){
				flag = deleteFile(files[i].getAbsolutePath());
				if(!flag) break;
			}	//删除子目录
			else{
				flag = deleteDirectory(files[i].getAbsolutePath());
				if(!flag) break;
			}
		}
		if(!flag) return false;
		
		//删除当前目录
		if(dirFile.delete()){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean DeleteFolder(String sPath){
		boolean flag = false;
		File file = new File(sPath);
		//判断文件或目录是否存在
		if(!file.exists()){
			return flag;
		}else{
			//判断是否为文件
			if(file.isFile()){
				return deleteFile(sPath);	//为文件时调用删除文件方法
			}else{
				return deleteDirectory(sPath);	//为目录时调用删除目录方法
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
        String fileName = srcPath  
                .substring(srcPath.lastIndexOf(File.separator));  
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



	// *****************Standmodel表操作************************
	public String getStandmodelList(String findstr, int start, int limit) {
		List<Standmodel> list = fileSystemDAO.getStandmodelList(findstr, start, limit);
		int total = fileSystemDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";

		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				jsonStr += ",";
			}
			Standmodel s = list.get(i);
			jsonStr += "{\"ID\":\"" + s.getId() + "\",\"one\":\"" + s.getOne() + "\",\"two\":\""
					+ s.getTwo() + "\",\"three\":\"" + s.getThree() + "\",\"modelname\":\"" + s.getModelname()
					 + "\",\"Accessory\":\"" + s.getAccessory() + "\"}";

			System.out.println(jsonStr);
		}
		jsonStr += "]}";
		return jsonStr;
	}

	public String addStandmodel(Standmodel s, String fileName, String rootPath) {
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

		fileSystemDAO.insertStandmodel(s);
		return json;
	}

	public String editStandmodel(Standmodel s, String fileName, String rootPath) {
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

		fileSystemDAO.updateStandmodel(s);

		// return Saftycheck2(s, fileName2, rootPath);
		return json;
	}

	public String addStandmodel(Standmodel s) {
		String json = "{\"success\":\"true\"}";
		fileSystemDAO.insertStandmodel(s);
		return json;
	}

	public String updateStandmodel(Standmodel s) {
		String json = "{\"success\":\"true\"}";
		fileSystemDAO.updateStandmodel(s);
		return json;
	}

	public String deleteStandmodel(String ID) {
		Standmodel standmodel = new Standmodel();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			standmodel.setId(Integer.parseInt(temp[i]));
			fileSystemDAO.deleteStandmodel(standmodel);
		}
		return json;
	}
}
