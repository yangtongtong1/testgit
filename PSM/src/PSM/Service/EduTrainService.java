package PSM.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import PSM.DAO.EduTrainDAO;
import PSM.Tool.WordToPdf;
import hibernate.Fbactivity;
import hibernate.Fbdailytrain;
import hibernate.Multimediafile;
import hibernate.PageModel;
import hibernate.Projectmanagement;
import hibernate.Question;
import hibernate.Testpaper;
import hibernate.Testrecord;
import hibernate.Tezhongpeople;
import hibernate.Tezhongsbpeople;
import hibernate.Trainplan1;
import hibernate.Trainplan2;
import hibernate.Traintable;
import hibernate.Xiandongtai;

public class EduTrainService 
{
	private EduTrainDAO eduTrainDAO;
	private HttpSession session;
		
	public String getTrainplan1List(String findstr, int start,int limit, String projectName)
	{
		List<Trainplan1> list = eduTrainDAO.getTrainplan1List(findstr, start, limit, projectName);
		int total = eduTrainDAO.datacount;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for(int i = 0; i < list.size(); ++i) {
			if(i > 0)
				jsonStr += ",";
			Trainplan1 p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId()
					+ "\",\"Content\":\"" + p.getContent()
					+ "\",\"Employee\":\"" + p.getEmployee()
					+ "\",\"Method\":\"" + p.getMethod()
					+ "\",\"ClassDate\":\"" + sdf.format(p.getClassDate())
					+ "\",\"ClassTime\":\"" + p.getClassTime()
					+ "\",\"Budget\":\"" + p.getBudget()
					+ "\",\"Result\":\"" + p.getResult()
					+ "\",\"ProjectName\":\"" + p.getProjectName()
					+ "\",\"Accessory\":\"" + p.getAccessory()  + "\"}";
		}
		jsonStr += "]}";
		return jsonStr;
	}
	
	public String getTrainplan2List(String findstr, int start,int limit, String projectName)
	{
		List<Trainplan2> list = eduTrainDAO.getTrainplan2List(findstr, start, limit, projectName);
		int total = eduTrainDAO.datacount;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for(int i = 0; i < list.size(); ++i) {
			if(i > 0)
				jsonStr += ",";
			Trainplan2 p = list.get(i);
			String RegistDate = "";
			if (p.getRegistDate() != null) 
				RegistDate = sdf.format(p.getRegistDate());
			jsonStr += "{\"ID\":\"" + p.getId()
					+ "\",\"Content\":\"" + p.getContent()
					+ "\",\"Method\":\"" + p.getMethod()
					+ "\",\"ActDate\":\"" + sdf.format(p.getActDate())
					+ "\",\"RegistDate\":\"" + RegistDate
					+ "\",\"Funding\":\"" + p.getFunding()
					+ "\",\"ProjectName\":\"" + p.getProjectName()
					+ "\",\"Accessory\":\"" + p.getAccessory()  + "\"}";
		}
		jsonStr += "]}";
		return jsonStr;
	}
	
	public String getTraintableList(String findstr, int start,int limit, String tableID, String projectName)
	{
		List<Traintable> list = eduTrainDAO.getTraintableList(findstr, start, limit, tableID, projectName);
		int total = eduTrainDAO.datacount;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for(int i = 0; i < list.size(); ++i) {
			if(i > 0)
				jsonStr += ",";
			Traintable p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId()
					+ "\",\"Content\":\"" + p.getContent()
					+ "\",\"Employee\":\"" + p.getEmployee()
					+ "\",\"Method\":\"" + p.getMethod()
					+ "\",\"TrainDate\":\"" + sdf.format(p.getTrainDate())
					+ "\",\"RegistDate\":\"" + sdf.format(p.getRegistDate())
					+ "\",\"Funding\":\"" + p.getFunding()
					+ "\",\"Accessory\":\"" + p.getAccessory()  + "\"}";
		}
		jsonStr += "]}";
		return jsonStr;
	}
	
	public String getFbdailytrainList(String findstr, int start,int limit, String projectName)
	{
		List<Fbdailytrain> list = eduTrainDAO.getFbdailytrainList(findstr, start, limit, projectName);
		int total = eduTrainDAO.datacount;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for(int i = 0; i < list.size(); ++i) {
			if(i > 0)
				jsonStr += ",";
			Fbdailytrain p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId()
					+ "\",\"Fenbao\":\"" + p.getFenbao()
					+ "\",\"StartDate\":\"" + sdf.format(p.getStartDate())
					+ "\",\"EndDate\":\"" + sdf.format(p.getEndDate())
					+ "\",\"Time\":\"" + p.getTime()
					+ "\",\"RegistDate\":\"" + sdf.format(p.getRegistDate())
					+ "\",\"Record\":\"" + p.getRecord()
					+ "\",\"Accessory\":\"" + p.getAccessory()  + "\"}";
		}
		jsonStr += "]}";
		return jsonStr;
	}
	
	public String getFbactivityList(String findstr, int start, int limit, String type, String projectName)
	{
		List<Fbactivity> list;
		if (type != null && type.equals("分包方安全生产班前会"))
			list = eduTrainDAO.getFbactivity1List(findstr, start, limit, projectName);
		else
			list = eduTrainDAO.getFbactivity2List(findstr, start, limit, projectName);
		int total = eduTrainDAO.datacount;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for(int i = 0; i < list.size(); ++i) {
			if(i > 0)
				jsonStr += ",";
			Fbactivity p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId()
					+ "\",\"ActivityDate\":\"" + sdf.format(p.getActivityDate())
					+ "\",\"Organization\":\"" + p.getOrganization()
					+ "\",\"Theme\":\"" + p.getTheme()
					+ "\",\"Accessory\":\"" + p.getAccessory()  + "\"}";
		}
		jsonStr += "]}";
		return jsonStr;
	}
	
	public String getQuestionList(String belongTo, String type, String findstr, int start,int limit)
	{
		List<Question> list = eduTrainDAO.getQuestionList(belongTo, type, findstr, start, limit);
		int total = eduTrainDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for(int i = 0; i < list.size(); ++i) {
			if(i > 0)
				jsonStr += ",";
			Question p = list.get(i);
			jsonStr += "{\"ID\":\"" + p.getId()
					+ "\",\"BelongTo\":\"" + p.getBelongTo()
					+ "\",\"Type\":\"" + p.getType()
					+ "\",\"Question\":\"" + p.getQuestion()
					+ "\",\"OptionA\":\"" + p.getOptionA()
					+ "\",\"OptionB\":\"" + p.getOptionB()
					+ "\",\"OptionC\":\"" + p.getOptionC()
					+ "\",\"OptionD\":\"" + p.getOptionD()
					+ "\",\"OptionE\":\"" + p.getOptionE()
					+ "\",\"Answer\":\"" + p.getAnswer() + "\"}";
		}
		jsonStr += "]}";
		return jsonStr;
	}
	
	public String getTestrecordList(String findstr, int start, int limit, String projectName)
	{		
		PageModel<Testrecord> pageModel = eduTrainDAO.getTestrecordList(findstr, start, limit, projectName);
		int total = 0;
		total = pageModel.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for(int i = 0; i < pageModel.models.size(); ++i) {
			if(i > 0)
				jsonStr += ",";
			Testrecord p = pageModel.models.get(i);
			jsonStr += "{\"ID\":\"" + p.getId()
					+ "\",\"Name\":\"" + p.getName()
					+ "\",\"BelongTo\":\"" + p.getBelongTo()
					+ "\",\"ActualNum\":" + eduTrainDAO.getActualNum(p.getId())
					+ ",\"AvgScore\":" + eduTrainDAO.getAvgScore(p.getId()) + "}";
		}
		jsonStr += "]}";
		return jsonStr;
	}

	public String getDoubleTestrecordList(String projectName)
	{		
		PageModel<Testrecord> pageModel = eduTrainDAO.getDoubleTestrecordList(projectName);
		int total = 0;
		total = pageModel.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for(int i = 0; i < pageModel.models.size(); ++i) {
			if(i > 0)
				jsonStr += ",";
			Testrecord p = pageModel.models.get(i);
			jsonStr += "{\"ID\":\"" + p.getId()
					+ "\",\"Name\":\"" + p.getName()
					+ "\",\"BelongTo\":\"" + p.getBelongTo()
					+ "\",\"ActualNum\":" + eduTrainDAO.getActualNum(p.getId())
					+ ",\"AvgScore\":" + eduTrainDAO.getAvgScore(p.getId()) + "}";
		}
		jsonStr += "]}";
		return jsonStr;
	}
	
	public String getTestpaperList(int testID, String projectName, String findstr, int start, int limit)
	{
		int projectID = eduTrainDAO.getProjectID(projectName);
		List<Testpaper> list = eduTrainDAO.getTestpaperList(testID, projectID, findstr, start, limit);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm"); 
		int total = eduTrainDAO.datacount;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for(int i = 0; i < list.size(); ++i) {
			if(i > 0) jsonStr += ",";
			Testpaper p = list.get(i);
			Projectmanagement pro = eduTrainDAO.getProjectmanagement(p.getProjectId());
			jsonStr += "{\"ID\":\"" + p.getId()
					+ "\",\"Name\":\"" + p.getName()
					+ "\",\"Project\":\"" + pro.getName()
					+ "\",\"IDNO\":\"" + p.getIdno()
					+ "\",\"Score1\":" + p.getScore1()
					+ ",\"Score2\":" + p.getScore2()
					+ ",\"Score3\":" + p.getScore3()
					+ ",\"Score4\":" + p.getScore4()
					+ ",\"Score\":" + ( p.getScore1() + p.getScore2() + p.getScore3() + p.getScore4() )
					+ ",\"TestDate\":\"" + sdf.format(p.getTestDate()) + "\"}";
		}
		jsonStr += "]}";
		return jsonStr;
	}
	
	public String getMultimediafileList(String findstr, int start, int limit, String type,String projectName)
	{
		List<Multimediafile> list = eduTrainDAO.getMultimediafileList(findstr, start, limit, type, projectName);
		int total = eduTrainDAO.datacount;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for(int i = 0; i < list.size(); ++i) {
			if(i > 0) jsonStr += ",";
			Multimediafile p = list.get(i);
			String accessory = p.getAccessory();
			String[] strarray = accessory.split("\\*");
			String foldname = "";
			String filename = "";
			String filetype = "";
			if (strarray.length == 3) {
				foldname = "upload/" + strarray[0] + "/" + strarray[1];
				filename = strarray[strarray.length - 1];
				filetype = getFileType(filename);
			}
			jsonStr += "{\"ID\":\"" + p.getId() + "\",\"Accessory\":\"" + p.getAccessory() + "\",\"UploadDate\":\"" + sdf.format(p.getUploadDate());
			jsonStr += "\",\"Filename\":\"" + filename + "\",\"Foldname\":\"" + foldname + "\",\"FileType\":\"" + filetype + "\"}";
		}
		jsonStr += "]}";
		return jsonStr;
	}
	
	public String getProjectList(int start, int limit)
	{
		List<Projectmanagement> list = eduTrainDAO.getProjectList(start, limit);
		int total = 0;
		total = eduTrainDAO.datacount + 1;
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[{\"ID\":\"-1\",\"Name\":\"全部项目部\"}";
		for(int i = 0; i < list.size(); ++i) {
			Projectmanagement p = list.get(i);
			jsonStr += ",{\"ID\":\"" + p.getId()
					+ "\",\"Name\":\"" + p.getName() + "\"}";
		}
		jsonStr += "]}";
		return jsonStr;
	}
	
	private String getFileType(String filename) {
		int index = filename.lastIndexOf('.');
		String extension = filename.substring(index + 1);
		System.out.println(filename + " : " + extension);
		extension = extension.toLowerCase();
		switch (extension) {
		case "jpg":
		case "jpeg":
		case "png":
		case "gif":
			return "图片";
		case "mp4":
		case "avi":
		case "rmvb":
		case "mkv":
		case "wmv":
			return "视频";
		case "doc":
		case "docx":
			return "Word文档";
		case "ppt":
		case "pptx":
			return "PPT文档";
		case "xls":
		case "xlsx":
			return "Excel文档";
		case "pdf":
			return "PDF文档";
		case "zip":
		case "rar":
		case "7z":
			return "压缩文件";
		default:
			return "其他文件";
		}
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
	
	public String addTrainplan1(Trainplan1 p, String fileName, String rootPath)
	{
		String json = "{\"success\":\"true\"}";		
      	String accessory = addAccessory(rootPath, fileName, p.getContent());
      	if(fileName == null || fileName.length() <= 0) {
			p.setAccessory(fileName);
		} else {
			p.setAccessory(accessory);
		}
        eduTrainDAO.insertTrainplan1(p);
		return json;
	}
	
	public String addTrainplan2(Trainplan2 p, String fileName, String rootPath)
	{
		String json = "{\"success\":\"true\"}";		
      	String accessory = addAccessory(rootPath, fileName, p.getContent());
      	if(fileName == null || fileName.length() <= 0) {
			p.setAccessory(fileName);
		} else {
			p.setAccessory(accessory);
		}
        eduTrainDAO.insertTrainplan2(p);
		return json;
	}
	
	public String addTraintable(Traintable p, String fileName, String rootPath)
	{
		String json = "{\"success\":\"true\"}";		
      	String accessory = addAccessory(rootPath, fileName, p.getContent());
      	if(fileName == null || fileName.length() <= 0) {
			p.setAccessory(fileName);
		} else {
			p.setAccessory(accessory);
		}
        eduTrainDAO.insertTraintable(p);
		return json;
	}
	
	public String addFbdailytrain(Fbdailytrain p, String fileName, String rootPath)
	{
      	String accessory = addAccessory(rootPath, fileName, p.getFenbao());
      	if(fileName == null || fileName.length() <= 0) {
			p.setAccessory(fileName);
		} else {
			p.setAccessory(accessory);
		}
        eduTrainDAO.insertFbdailytrain(p);
		return "{\"success\":\"true\"}";
	}
	
	public String addFbactivity(Fbactivity p, String fileName, String rootPath)
	{
      	String accessory = addAccessory(rootPath, fileName, p.getOrganization() + "-" + p.getTheme());
      	if(fileName == null || fileName.length() <= 0) {
			p.setAccessory(fileName);
		} else {
			p.setAccessory(accessory);
		}
        eduTrainDAO.insertFbactivity(p);
		return "{\"success\":\"true\"}";
	}
	
	public String addMultimediafile(Multimediafile p, String fileName, String rootPath)
	{
		String json = "{\"success\":\"true\"}";
		String accessory = addAccessory(rootPath, fileName, p.getType());
      	if(fileName == null || fileName.length()<=0) {
			p.setAccessory(fileName);
		}
		else {
			p.setAccessory(accessory);
		}
        eduTrainDAO.insertMultimediafile(p);
		return json;
	}
	
	public String addQuestion(Question p)
	{
		String json = "{\"success\":\"true\"}";	
        eduTrainDAO.insertQuestion(p);
		return json;
	}
	
	public String addTestrecord(Testrecord p, String source, int Question1Num, int Question2Num, int Question3Num, int Question4Num) {
		try {
			if (source.equals("设计院题库") || source.equals("项目部题库")) {
				String belongTo = source.equals("设计院题库") ? "设计院题库" : p.getBelongTo();
				p.setQuestionIdlist1(createTestrecord("单选题", Question1Num, belongTo));
				p.setQuestionIdlist2(createTestrecord("多选题", Question2Num, belongTo));
				p.setQuestionIdlist3(createTestrecord("判断题", Question3Num, belongTo));
				p.setQuestionIdlist4(createTestrecord("简答题", Question4Num, belongTo));
			} else {
				String IDList1 = createTestrecord("单选题", Question1Num / 2, "设计院题库")
						+ "," + createTestrecord("单选题", Question1Num - Question1Num / 2, p.getBelongTo());
				String IDList2 = createTestrecord("多选题", Question2Num / 2, "设计院题库")
						+ "," + createTestrecord("多选题", Question2Num - Question2Num / 2, p.getBelongTo());
				String IDList3 = createTestrecord("判断题", Question3Num / 2, "设计院题库")
						+ "," + createTestrecord("判断题", Question3Num - Question3Num / 2, p.getBelongTo());
				String IDList4 = createTestrecord("简答题", Question4Num / 2, "设计院题库")
						+ "," + createTestrecord("简答题", Question4Num - Question4Num / 2, p.getBelongTo());
				if (IDList1.equals(",")) IDList1 = "";
				if (IDList2.equals(",")) IDList2 = "";
				if (IDList3.equals(",")) IDList3 = "";
				if (IDList4.equals(",")) IDList4 = "";
				p.setQuestionIdlist1(IDList1);
				p.setQuestionIdlist2(IDList2);
				p.setQuestionIdlist3(IDList3);
				p.setQuestionIdlist4(IDList4);
			
			
			}

	        eduTrainDAO.insertTestrecord(p);
			
		} catch (Exception e) {
			e.printStackTrace();
			String msg = e.getMessage();			
			return "{\"success\":false,\"msg\":\"" + msg + "\"}";
		}
		return "{\"success\":\"true\"}";
	}
	
	// 生成卷子，三个参数分别是题目类型（单选、多选、判断、简答），需要的题目数量，题库类型（设计院题库，项目部题库）
	private String createTestrecord(String type, int num, String belongTo) throws Exception {
		List<Question> questions = eduTrainDAO.getQuestion(type, belongTo, num);
		if (questions.size() != num) throw new Exception((belongTo.equals("设计院题库") ? "设计院" : belongTo) + "题库" + type + "数量不足");
		String result = "";
		for (int i = 0; i < num; i++) {
			if (i > 0) result += ",";
			result += questions.get(i).getId();
		}
		return result;
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
	
	public String editTrainplan1(Trainplan1 p, String fileName, String rootPath)
	{		
		String json = "{\"success\":\"true\"}";
		String newAccessory = editAccessory(rootPath, fileName, p.getContent(), p.getAccessory());
      	if(fileName == null || fileName.length() <= 0) {
			p.setAccessory(fileName);
		}
		else {
			p.setAccessory(newAccessory);
		}
        eduTrainDAO.updateTrainplan1(p);    
		return json;
	}
	
	public String editTrainplan2(Trainplan2 p, String fileName, String rootPath)
	{		
		String json = "{\"success\":\"true\"}";
		String newAccessory = editAccessory(rootPath, fileName, p.getContent(), p.getAccessory());
      	if(fileName == null || fileName.length() <= 0) {
			p.setAccessory(fileName);
		}
		else {
			p.setAccessory(newAccessory);
		}
        eduTrainDAO.updateTrainplan2(p);    
		return json;
	}
	
	public String editTraintable(Traintable p, String fileName, String rootPath)
	{		
		String json = "{\"success\":\"true\"}";
		String newAccessory = editAccessory(rootPath, fileName, p.getContent(), p.getAccessory());
      	if(fileName == null || fileName.length() <= 0) {
			p.setAccessory(fileName);
		} else {
			p.setAccessory(newAccessory);
		}
        eduTrainDAO.updateTraintable(p);    
		return json;
	}
	
	public String editFbdailytrain(Fbdailytrain p, String fileName, String rootPath)
	{		
		String newAccessory = editAccessory(rootPath, fileName, p.getFenbao(), p.getAccessory());
      	if(fileName == null || fileName.length() <= 0) {
			p.setAccessory(fileName);
		} else {
			p.setAccessory(newAccessory);
		}
        eduTrainDAO.updateFbdailytrain(p);    
		return "{\"success\":\"true\"}";
	}
	
	public String editFbactivity(Fbactivity p, String fileName, String rootPath)
	{		
		String newAccessory = editAccessory(rootPath, fileName, p.getOrganization() + "-" + p.getTheme(), p.getAccessory());
      	if(fileName == null || fileName.length() <= 0) {
			System.out.println("----is null---");
			p.setAccessory(fileName);
		} else {
			p.setAccessory(newAccessory);
		}
        eduTrainDAO.updateFbactivity(p);    
		return "{\"success\":\"true\"}";
	}
	
	public String editQuestion(Question p)
	{		
		String json = "{\"success\":\"true\"}";
        eduTrainDAO.updateQuestion(p);    
		return json;
	}
	
	public String editTestrecord (Testrecord p)
	{
		eduTrainDAO.updateTestrecord(p);
		return "{\"success\":\"true\"}";
	}
	
	public String editMultimediafile(Multimediafile p, String fileName, String rootPath)
	{
		String json = "{\"success\":\"true\"}";
		String newAccessory = editAccessory(rootPath, fileName, p.getType(), p.getAccessory());
      	if(fileName == null || fileName.length() <= 0) {
			p.setAccessory(fileName);
		} else {
			p.setAccessory(newAccessory);
		}
        eduTrainDAO.updateMultimediafile(p);    
		return json;
	}
	
	
	// 删除
	
	// 删除指定附件、目录或文件
	private void deleteAccessory(String rootPath, String fileName) {
		if(fileName != null && fileName.length() != 0) {
			String[] name = fileName.split("\\*");
			String folder = name[0] +"\\" + name[1];
			String path = rootPath +folder;
			for(int j = 2; j < name.length; j++) {					
				deleteFile(path + "\\" + name[j]);
			}
			DeleteFolder(path);
		}
	}
	
	public String deleteTrainplan1(String ID, String rootPath)
	{
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for(int i = 0; i < temp.length; i++) {
			Trainplan1 pro = eduTrainDAO.getTrainplan1(Integer.parseInt(temp[i]));
			pro.setId(Integer.parseInt(temp[i]));
			eduTrainDAO.deleteTrainplan1(pro);
		} 
		return json;
	}
	
	public String deleteTrainplan2(String ID, String rootPath)
	{
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for(int i = 0; i < temp.length; i++) {
			Trainplan2 pro = eduTrainDAO.getTrainplan2(Integer.parseInt(temp[i]));
			pro.setId(Integer.parseInt(temp[i]));
			eduTrainDAO.deleteTrainplan2(pro);
		} 
		return json;
	}
	
	public String deleteTraintable(String ID, String rootPath)
	{
		Traintable pro = new Traintable();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for(int i = 0; i < temp.length; i++) {
			//删除附件
			Traintable p = eduTrainDAO.getTraintable(Integer.parseInt(temp[i]));
			if(p != null) {
				deleteAccessory(rootPath, p.getAccessory());
			}
			pro.setId(Integer.parseInt(temp[i]));
			eduTrainDAO.deleteTraintable(pro);
		} 
		return json;
	}
	
	public String deleteFbdailytrain(String ID, String rootPath)
	{
		Fbdailytrain pro = new Fbdailytrain();
		String[] temp = ID.split(",");
		for(int i = 0; i < temp.length; i++) {
			Fbdailytrain p = eduTrainDAO.getFbdailytrain(Integer.parseInt(temp[i]));
			//删除附件
			if(p != null) {
				deleteAccessory(rootPath, p.getAccessory());
			}
			pro.setId(Integer.parseInt(temp[i]));
			eduTrainDAO.deleteFbdailytrain(pro);
		} 
		return "{\"success\":\"true\"}";
	}
	
	public String deleteFbactivity(String ID, String rootPath)
	{
		Fbactivity pro = new Fbactivity();
		String[] temp = ID.split(",");
		for(int i = 0; i < temp.length; i++) {
			Fbactivity p = eduTrainDAO.getFbactivity(Integer.parseInt(temp[i]));
			//删除附件
			if(p != null) {
				deleteAccessory(rootPath, p.getAccessory());
			}
			pro.setId(Integer.parseInt(temp[i]));
			eduTrainDAO.deleteFbactivity(pro);
		} 
		return "{\"success\":\"true\"}";
	}
	
	public String deleteMultimediafile(String ID, String rootPath)
	{
		Multimediafile pro = new Multimediafile();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for(int i = 0; i < temp.length; i++) {
			//删除附件
			Multimediafile p = eduTrainDAO.getMultimediafile((Integer.parseInt(temp[i])));
			if(p != null) {
				deleteAccessory(rootPath, p.getAccessory());
			}
			pro.setId(Integer.parseInt(temp[i]));
			eduTrainDAO.deleteMultimediafile(pro);
		} 
		return json;
	}
	
	public String deleteQuestion(String ID)
	{
		Question pro = new Question();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for(int i = 0; i < temp.length; i++) {
			eduTrainDAO.deleteQuestion(Integer.parseInt(temp[i]));
		} 
		return json;
	}
	
	public String deleteTestrecord(String ID)
	{
		Testrecord pro = new Testrecord();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for(int i = 0; i < temp.length; i++) {
			pro.setId(Integer.parseInt(temp[i]));
			eduTrainDAO.deleteTestrecord(pro);
		} 
		return json;
	}
	
	public String deleteTestpaper(String ID)
	{
		Testpaper pro = new Testpaper();
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for(int i = 0; i < temp.length; i++) {
			pro.setId(Integer.parseInt(temp[i]));
			eduTrainDAO.deleteTestpaper(pro);
		} 
		return json;
	}
	
	public String getFileInfo(String filePath)
	{
		 String fileLength = "0";
		 File file = new File(filePath);
		 if(file.exists()) {
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
			for(String existFileInList : newFile) {
				deleteFile(rootPath+"temp\\"+existFileInList);
			}
		} else {
			String[] strKey = null;
			Trainplan1 p = eduTrainDAO.getTrainplan1(Integer.parseInt(ppID));
			String a = p.getAccessory();
    		if( a != null && a.length() > 0) {
    			strKey = a.split("\\*");
    			for(String existFileInList : newFile) { 	
                	boolean hasFileInDB = false;	
                	for(int i = 2; i < strKey.length - 1; i++) {       			
            			String existFileInDB = strKey[i];
            			//列表中是否存在该文件
            			if(existFileInDB.equals(existFileInList)) {								
        					hasFileInDB = true;			
        					break;
            			}	        		
            		}
                	if(!hasFileInDB) {	
        				deleteFile(rootPath+"temp\\"+existFileInList);
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
	
	public boolean deleteDirectory(String sPath, boolean really){
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
		if (files.length != 0 && really == false)
			return false;
		for(int i=0; i<files.length; i++){
			//删除子文件
			if(files[i].isFile()){
				flag = deleteFile(files[i].getAbsolutePath());
				if(!flag) break;
			}	//删除子目录
			else{
				flag = deleteDirectory(files[i].getAbsolutePath(), really);
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
		File file = new File(sPath);
		//判断文件或目录是否存在
		if(!file.exists()){
			return false;
		}else{
			//判断是否为文件
			if(file.isFile()){
				return deleteFile(sPath);	//为文件时调用删除文件方法
			} else{
				return deleteDirectory(sPath, false);	//为目录时调用删除目录方法
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
	
	public String getEmployees(String tableID) {
		String json = "{\"success\":\"true\",\"data\":[";
		if (tableID.equals("120")) {
			List<Tezhongpeople> list = eduTrainDAO.getTezhongpeopleList();
			for (int i = 0; i < list.size(); i++) {
				if (i > 0) json += ",";
				json += "{\"name\":\"" + list.get(i).getName() + "\"}";
			}
		} else if (tableID.equals("213")) {
			List<Tezhongsbpeople> list = eduTrainDAO.getTezhongsbpeopleList();
			for (int i = 0; i < list.size(); i++) {
				if (i > 0) json += ",";
				json += "{\"name\":\"" + list.get(i).getName() + "\"}";
			}
		} else if (tableID.equals("122")) {
			List<Xiandongtai> list = eduTrainDAO.getXiandongtaiList();
			for (int i = 0; i < list.size(); i++) {
				if (i > 0) json += ",";
				json += "{\"name\":\"" + list.get(i).getName() + "\"}";
			}
		}
		json += "]}";
		return json;
	}
	
	public boolean testLogin(int testID, String idno) {
		Testpaper testpaper = eduTrainDAO.getTestpaperByIdnoTestID(idno, testID);
		return testpaper == null;
	}
	
	// 通过testID获得考试卷子
	public String getQuestion(int testID, String type) {
		Testrecord testrecord = eduTrainDAO.getTestrecord(testID);
		String questionIDList = "";
		if (type.equals("单选题")) {
			questionIDList = testrecord.getQuestionIdlist1();
		} else if (type.equals("多选题")) {
			questionIDList = testrecord.getQuestionIdlist2();
		} else if (type.equals("判断题")) {
			questionIDList = testrecord.getQuestionIdlist3();
		} else if (type.equals("简答题")) {
			questionIDList = testrecord.getQuestionIdlist4();
		}
		// 如果没有题目，直接返回total:0
		if (questionIDList.equals(""))
			return "{\"total\":0}";
		String[] questionIDArray = questionIDList.split(",");
		int total = questionIDArray.length;
		String json = "{\"total\":" + total + ",\"data\":[";
		if (type.equals("单选题") || type.equals("多选题")) {
			for (int i = 0; i < total; i++) {
				Question question = eduTrainDAO.getQuestion(Integer.parseInt(questionIDArray[i]));
				if (i > 0) json += ",";
				json += "{\"Question\":\"" + question.getQuestion() + "\""
					+ ",\"OptionA\":\"" + question.getOptionA() + "\""
					+ ",\"OptionB\":\"" + question.getOptionB() + "\""
					+ ",\"OptionC\":\"" + question.getOptionC() + "\""
					+ ",\"OptionD\":\"" + question.getOptionD() + "\""
					+ ",\"OptionE\":\"" + question.getOptionE() + "\"" + "}";
			}
		} else {
			for (int i = 0; i < total; i++) {
				Question question = eduTrainDAO.getQuestion(Integer.parseInt(questionIDArray[i]));
				if (i > 0) json += ",";
				json += "{\"Question\":\"" + question.getQuestion() + "\"}";
			}
		}		
		json += "]}";
		return json;
	}
	
	public String submitPaper(int testID, int projectID, String name, String idno, 			
			String answerList1, String answerList2, String answerList3, String answerList4) {
		Testrecord testrecord = eduTrainDAO.getTestrecord(testID);
		String[] questionIDArray1 = testrecord.getQuestionIdlist1().split(",");
		String[] questionIDArray2 = testrecord.getQuestionIdlist2().split(",");
		String[] questionIDArray3 = testrecord.getQuestionIdlist3().split(",");
		String[] questionIDArray4 = testrecord.getQuestionIdlist4().split(",");
		String[] answerArray1 = answerList1.split(",");
		String[] answerArray2 = answerList2.split(",");
		String[] answerArray3 = answerList3.split(",");
		String[] answerArray4 = answerList4.split("\\*--\\*");
		int Question1Score = testrecord.getQuestionScore1();
		int Question2Score = testrecord.getQuestionScore2();
		int Question3Score = testrecord.getQuestionScore3();
		int Question4Score = testrecord.getQuestionScore4();
		int score1 = 0, score2 = 0, score3 = 0, score4 = 0;
		try {
			// 判断是否存在该题型，若该题型的列表为空表示没有此类题型
			if (!testrecord.getQuestionIdlist1().equals(""))
				score1 = checkAnswer13(questionIDArray1, answerArray1, Question1Score);
			if (!testrecord.getQuestionIdlist2().equals(""))
				score2 = checkAnswer2 (questionIDArray2, answerArray2, Question2Score);
			if (!testrecord.getQuestionIdlist3().equals(""))	
				score3 = checkAnswer13(questionIDArray3, answerArray3, Question3Score);
			if (!testrecord.getQuestionIdlist4().equals(""))
				score4 = checkAnswer4 (questionIDArray4, answerArray4, Question4Score);
		} catch (Exception e) {
			e.printStackTrace();
			return "{\"result\":\"failed\"}";
		}
		Date date = new Date();
		Timestamp timestamp = new Timestamp(date.getTime());
		
		Testpaper testpaper = new Testpaper();
		testpaper.setTestId(testID);
		testpaper.setProjectId(projectID);
		testpaper.setName(name);
		testpaper.setIdno(idno);
		testpaper.setScore1(score1);
		testpaper.setScore2(score2);
		testpaper.setScore3(score3);
		testpaper.setScore4(score4);
		testpaper.setAnswerList1(answerList1);
		testpaper.setAnswerList2(answerList2);
		testpaper.setAnswerList3(answerList3);
		testpaper.setAnswerList4(answerList4);
		testpaper.setTestDate(timestamp);
		eduTrainDAO.insertTestpaper(testpaper);
		return "{\"result\":\"success\",\"score\":"+(score1+score2+score3+score4)+"}";
	}
	
	// 判断单选题和判断题
	private int checkAnswer13(String[] questionIDArray, String[] answerArray, int singleScore) throws Exception {
		if (questionIDArray.length != answerArray.length)
			throw new Exception("题目和答案数目不一致");
		int length = questionIDArray.length;
		int score = 0;
		for (int i = 0; i < length; i++) {
			Question question = eduTrainDAO.getQuestion(Integer.parseInt(questionIDArray[i]));
			String stdAnswer = question.getAnswer();
			if (stdAnswer.equals(answerArray[i]))
				score += singleScore;
		}
		return score;
	}
	
	// 判断多选题
	private int checkAnswer2(String[] questionIDArray, String[] answerArray, int singleScore) throws Exception {
		if (questionIDArray.length != answerArray.length)
			throw new Exception("题目和答案数目不一致");
		int length = questionIDArray.length;
		int score = 0;
		for (int i = 0; i < length; i++) {
			Question question = eduTrainDAO.getQuestion(Integer.parseInt(questionIDArray[i]));
			String stdAnswer = question.getAnswer();
			float res = checkSingleAnswer2(answerArray[i], stdAnswer);
			score += (int) Math.round(res * singleScore);
		}		
		return score;
	}
	
	// 判断简答题
	private int checkAnswer4(String[] questionIDArray, String[] answerArray, int singleScore) throws Exception {
		if (questionIDArray.length != answerArray.length)
			throw new Exception("题目和答案数目不一致");
		int length = questionIDArray.length;
		int score = 0;
		for (int i = 0; i < length; i++) {
			Question question = eduTrainDAO.getQuestion(Integer.parseInt(questionIDArray[i]));
			String stdAnswer = question.getAnswer();
			float res = checkSingleAnswer4(answerArray[i], stdAnswer);
			score += (int) Math.round(res * singleScore);
		}
		return score;
	}
	
	// 判断单道多选题
	private float checkSingleAnswer2(String myAnswer, String stdAnswer) {
		char[] myAnswerArray = myAnswer.toCharArray();
		char[] stdAnswerArray = stdAnswer.toCharArray();
		if (myAnswerArray.length > stdAnswerArray.length) return 0;
		if (myAnswerArray.length == stdAnswerArray.length) {
			for (char myOpntion : myAnswerArray)
				if (!isContain(myOpntion, stdAnswerArray))
					return 0;
			return 1;
		}
		for (char myOpntion : myAnswerArray)
			if (!isContain(myOpntion, stdAnswerArray))
				return 0;
		return 0.5f;
	}
	
	// 判断单道简答题
	private float checkSingleAnswer4(String myAnswer, String stdAnswer) {
		String[] stdAnswerArray = stdAnswer.split("，");
		int total = stdAnswerArray.length;
		int num = 0;
		for (String singleStdAnswer : stdAnswerArray)
			if (myAnswer.contains(singleStdAnswer))
				num++;
		return (float)num / total;
	}
	
	private boolean isContain(char ch, char[] charArray) {
		for (char c : charArray) if (ch == c) return true;
		return false;
	}
	
	public String getReviewQuestion(int testpaperID, String type) throws Exception {
		Testpaper testpaper = eduTrainDAO.getTestpaper(testpaperID);
		Testrecord testrecord = eduTrainDAO.getTestrecord(testpaper.getTestId());
		String questionIDList = "";
		String myAnswerList = "";
		if (type.equals("单选题")) {
			questionIDList = testrecord.getQuestionIdlist1();
			myAnswerList = testpaper.getAnswerList1();
		} else if (type.equals("多选题")) {
			questionIDList = testrecord.getQuestionIdlist2();
			myAnswerList = testpaper.getAnswerList2();
		} else if (type.equals("判断题")) {
			questionIDList = testrecord.getQuestionIdlist3();
			myAnswerList = testpaper.getAnswerList3();
		} else if (type.equals("简答题")) {
			questionIDList = testrecord.getQuestionIdlist4();
			myAnswerList = testpaper.getAnswerList4();
		}
		String[] questionIDArray = questionIDList.split(",");
		String[] myAnswerArray;
		if (type.equals("简答题")) myAnswerArray = myAnswerList.split("\\*--\\*");
		else myAnswerArray = myAnswerList.split(",");
		if (questionIDArray.length != myAnswerArray.length)
			throw new Exception("题目和答案数目不一致");
		// 如果没有题目，直接返回total:0
		if (questionIDList.equals(""))
			return "{\"total\":0}";
		int total = questionIDArray.length;
		String json = "{\"total\":" + total + ",\"data\":[";
		if (type.equals("单选题") || type.equals("多选题")) {
			for (int i = 0; i < total; i++) {
				Question question = eduTrainDAO.getQuestion(Integer.parseInt(questionIDArray[i]));
				if (i > 0) json += ",";
				json += "{\"Question\":\"" + question.getQuestion() + "\""
					+ ",\"OptionA\":\"" + question.getOptionA() + "\""
					+ ",\"OptionB\":\"" + question.getOptionB() + "\""
					+ ",\"OptionC\":\"" + question.getOptionC() + "\""
					+ ",\"OptionD\":\"" + question.getOptionD() + "\"" 
					+ ",\"MyAnswer\":\"" + myAnswerArray[i] + "\"" 
					+ ",\"StdAnswer\":\"" + question.getAnswer() + "\"}";
			}
		} else {
			for (int i = 0; i < total; i++) {
				Question question = eduTrainDAO.getQuestion(Integer.parseInt(questionIDArray[i]));
				if (i > 0) json += ",";
				json += "{\"Question\":\"" + question.getQuestion() + "\""
					+ ",\"MyAnswer\":\"" + myAnswerArray[i] + "\"" 
					+ ",\"StdAnswer\":\"" + question.getAnswer() + "\"}";
			}
		}		
		json += "]}";
		return json;
	}
	
	public String getTestTitleByTestID(int testID) {
		Testrecord testrecord = eduTrainDAO.getTestrecord(testID);
		return "{\"title\":\""+testrecord.getName()+"\"}";
	}
	
	public String getTestTitleByTestpaperID(int testpaperID) {
		Testpaper testpaper = eduTrainDAO.getTestpaper(testpaperID);
		Testrecord testrecord = eduTrainDAO.getTestrecord(testpaper.getTestId());
		return "{\"title\":\""+testrecord.getName()+"\"}";
	}
	
	public String getTestName(int testpaperID) {
		Testpaper testpaper = eduTrainDAO.getTestpaper(testpaperID);
		return "{\"title\":\""+testpaper.getName()+"\"}";
	}
	
	public String getProjectNameList() {
		List<Projectmanagement> projects = eduTrainDAO.getProjectNameList();
		int total = projects.size();
		String json = "{\"total\":" + total + ",\"rows\":[";
		json += "{\"ID\":\"-1\",\"Name\":\"全部项目\"}";
		for (int i = 0; i < total; i++) {
			Projectmanagement project = projects.get(i);
			json += ",";
			json += "{\"ID\":\"" + project.getId() + "\""
				+ ",\"Name\":\"" + project.getName() + "\"}";
		}
		json += "]}";
		return json;
	}
	
	public void getSession(HttpSession session) {
		this.session = session;
	}

	public EduTrainDAO getEduTrainDAO() {
		return eduTrainDAO;
	}

	public void setEduTrainDAO(EduTrainDAO eduTrainDAO) {
		this.eduTrainDAO = eduTrainDAO;
	}

}
