package PSM.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import PSM.DAO.BasicInfoDAO;
import PSM.DAO.MissionDAO;
import PSM.SMS.SendTest;
import PSM.Tool.ExcelToPdf;
import PSM.Tool.WordToPdf;
import hibernate.Approval;
import hibernate.Flownode;
import hibernate.Missionstate;
import hibernate.Person;
import hibernate.PrescribedActionView;
import hibernate.Projectmanagement;
import hibernate.Taizhang;

public class MissionService {
	private MissionDAO missionDAO;

	public MissionDAO getMissionDAO() {
		return missionDAO;
	}

	public void setMissionDAO(MissionDAO missionDAO) {
		this.missionDAO = missionDAO;
	}
	
	private BasicInfoDAO basicInfoDAO;

	public BasicInfoDAO getBasicInfoDAO() {
		return basicInfoDAO;
	}

	public void setBasicInfoDAO(BasicInfoDAO basicInfoDAO) {
		this.basicInfoDAO = basicInfoDAO;
	}
	
	public String examinApproval(String id,String title,String view,String person){
		String jsonStr = "{\"success\":\"true\"}";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
		String approview = new String();
		approview +="审批人：&nbsp;" + person+"&nbsp;&nbsp;审批时间：&nbsp;" + df.format(new Date()).toString()+"&nbsp;&nbsp;审批结果：&nbsp;&nbsp;"+title;
		approview +="<br>审批意见："+view;
		Approval app = missionDAO.getApproval(Integer.parseInt(id)).get(0);
		
		System.out.println("id=="+id);
		if(app.getView()==null){
			app.setView(approview);
		}
		else{
			app.setView(app.getView()+"**"+approview);
		}
		app.setApprostate(title);
		app.setJudgeperson(person);
		app.setApprotime(new Date());
		missionDAO.updateApproval(app);
		return jsonStr;
	}
	
	
	public String getGroupMis(String group){
		String jsonStr = "[";
		System.out.println("serv"+ group);
		 List<Missionstate> mission = missionDAO.getGroMission(group);
		 String misstate = new String();
		 for(Missionstate mis : mission){
				switch(Integer.parseInt(mis.getIsdown())){
		    	case 2:
		    		misstate = "进行中";
		    		break;
		    	case 1:
		    		misstate = "已完成";
		    		break;
		    	case 0:
		    		misstate = "未完成";
		    		break;
	    	}
			 jsonStr += "{\"ID\":"+mis.getId()+",\"missionname\":\"" + mis.getMissionname() + "\",\"properson\":\"" + mis.getProperson() + "\","
				 		+ "\"missionstate\":\"" + misstate + "\",\"fintime\":\"" + mis.getExpecttime() + "\",\"truefintime\":\"" + mis.getDowntime() + "\","
				 				+ "\"type\":\"" + mis.getMissiontype() + "\",\"score\":\"" + mis.getSubscore() + "\",\"downtime\":\"" + mis.getDowntime() + "\","
				 		+"\"finishfile\":\"" + mis.getFinishfile() + "\",\"progroup\":\"" + mis.getProgroup() + "\",\"missionfile\":\"" + mis.getMissionfile() + "\"},";
		 }if(jsonStr.length()>1){
				jsonStr.substring(jsonStr.length()-1);
			}
		jsonStr += "]";	
		return jsonStr;
	}
	
	public String getTotalMission(String projectName){
		//updateMissionState();
		String jsonStr = new String("[");
		List pro = missionDAO.getAllpro(projectName);  //所有项目组
		for(Object obj : pro){
			 Object[] arrpro = (Object[])obj; 
			 int unfinished = 0;
			 int subscore = 0;
			 int underfinished = 0;
			 System.out.println(arrpro[0].toString());
			 List<Missionstate> mission = missionDAO.getMission(arrpro[0].toString());
			 for(Missionstate mis : mission){
				 System.out.println(mis.getIsdown());
				 if(mis.getIsdown().equals("0")){
					 unfinished++;
					 subscore+=mis.getScore();
				 }else if(mis.getIsdown().equals("2")){
					 underfinished++;
				 };
			 }
			 System.out.println(underfinished);
			 jsonStr += "{\"progroup\":\"" + arrpro[0] + "\",\"allpro\":\"" + arrpro[1] + "\",\"unfinished\":\"" + unfinished + "\",\"isfinished\":\"" + (Integer.parseInt(arrpro[1].toString())-unfinished-underfinished) + "\",";
			 jsonStr +="\"subscore\":\""+ subscore +"\",\"underfinished\":\""+ underfinished +"\",\"totalscore\":\""+(100-subscore)+"\"},";
		}
		if(jsonStr.length()>1){
			jsonStr.substring(jsonStr.length()-1);
		}
		jsonStr += "]";	
		return jsonStr;
	}
	
	public String getTotalbyMis(){
		String jsonStr = new String("[");
		List<Missionstate> mission = missionDAO.getAllMis();  //所有项目组\
		System.out.println(mission.size());
		for( Missionstate mis : mission){
			jsonStr+= "{\"proname\":\"" + mis.getMissionname() + "\",";
			
				// String group = "项目组"+i;
				// boolean ispa = false;
				 String state = new String();
				 List<Missionstate> allgro = missionDAO.getbyMission(mis.getMissionname());
				 for(Missionstate ago : allgro){		
				 switch(Integer.parseInt(ago.getIsdown())){
			    	case 2:
			    		state = "进行中";
			    		break;
			    	case 1:
			    		state = "已完成";
			    		break;
			    	case 0:
			    		state = "未完成";
			    		break;
			    	}
				 jsonStr = jsonStr+"\""+ago.getProgroup()+"\":"+"\""+state+"\",";
				 }				
				 jsonStr+="},";
			 }		
		System.out.println(jsonStr);
		if(jsonStr.length()>1){
			jsonStr.substring(jsonStr.length()-1);
		}
		jsonStr += "]";	
		return jsonStr;
	}
	
	public String mymission(String category,String username){	
		int total = missionDAO.totalmission(category,username);
		System.out.println("s"+category);
		if(category.equals("所有"))
			category = "";
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
	    List<Missionstate> mission = missionDAO.getallpro(category,username);
	    for(Missionstate mis : mission){
	    	String misstate = new String();
	    	int score;
	    	if(Integer.parseInt(mis.getIsdown())>0){
	    		score = 0;
	    	}
	    	else{
	    		score = mis.getScore();
	    	}
	    	switch(Integer.parseInt(mis.getIsdown())){
		    	case 2:
		    		misstate = "进行中";
		    		break;
		    	case 1:
		    		misstate = "已完成";
		    		break;
		    	case 0:
		    		misstate = "未完成";
		    		break;
	    	}
	    	jsonStr += "{\"ID\":"+mis.getId()+",\"missionname\":\"" + mis.getMissionname() + "\",\"truefintime\":\""+mis.getDowntime()+"\",\"finsit\":\""+mis.getFinsituation()+"\",\"misexp\":\""+mis.getMisexplain()+"\","
			 		+ "\"missionstate\":\"" + misstate + "\",\"fintime\":\"" + mis.getExpecttime() + "\",\"type\":\"" + mis.getMissiontype() + "\",\"properson\":\"" + mis.getProperson()+ "\"," 
			 		+"\"score\":\"" + score + "\",\"state\":\"" + mis.getIsdown() + "\",\"finishfile\":\"" + mis.getFinishfile() + "\",\"missionfile\":\"" + mis.getMissionfile() + "\"},";
	    }
	    if(jsonStr.length()>1){
			jsonStr.substring(jsonStr.length()-1);
		}
		jsonStr += "]}";		
		return jsonStr;
	}
	
	public String myApproval(String category,String username){	
		int total = missionDAO.totalapproval(category,username);
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
	    List<Approval> approval = missionDAO.getallApproval(category,username);
	    for(Approval app : approval){
	    	String misstate = new String();	    	  	
	    	jsonStr += "{\"approname\":\"" + app.getApproname() + "\",\"approtype\":\""+app.getApprotype()+"\",\"approtime\":\""+app.getApprotime()+"\",\"approexp\":\""+app.getApproexp()+"\","
			 		+ "\"approfile\":\"" + app.getApprofile() + "\",\"approstate\":\"" + app.getApprostate() + "\",\"ID\":\"" + app.getId() + "\",\"approview\":\"" + app.getView() + "\",\"approperson\":\"" + app.getApproperson() + "\"},";
	    }
	    if(jsonStr.length()>1){
			jsonStr.substring(jsonStr.length()-1);
		}
		jsonStr += "]}";		
		return jsonStr;
	}
	
	private String getAcc(String rootpath,String approname,String filename){
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
      	String ym = rootpath+ sy + sm +"\\";
      	String ympname = ym + approname + "\\";
      	File directory1 =new File(ym);	//根目录是否存在
 		if(!directory1.exists())
 			directory1.mkdir();
 		File directory2 =new File(ympname);	//根目录是否存在
 		if(!directory2.exists())
 			directory2.mkdir();
        
 		//拷贝temp中的文件到正确的文件夹
 		String[] newFile = filename.split("\\*");
		for(String copyFile : newFile) {
			cutGeneralFile(rootpath + "temp\\" + copyFile,ym + approname);
		}
		if (filename.charAt(filename.length()-1)=='*') 
			filename = filename.substring(0,filename.length()-1);
		//存入数据库的内容
      	String accessory = sy + sm + "*" + approname + "*" + filename;
		
	
      	//doc转pdf
      	if(filename.length() != 0)
      	{
      		String[] allFile = filename.split("\\*");
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
	
	public String addAppro(String rootpath,String approname,String filename,String approtype,String approexp,String user){
		 
		String json = "{\"success\":\"true\"}";
		if(filename.equals(null))
			filename = "";
      	Approval approval = new Approval();
      	approval.setApproexp(approexp);
      	approval.setApprofile(getAcc(rootpath,approname,filename));
      	approval.setApproperson(user);
      	approval.setApprostate("待定");
      	approval.setApproname(approname);
      	approval.setApprotype(approtype);
      	missionDAO.insertAppro(approval);
      	return json;
	}
	
	
	public String alloTask(String type, String folder, String score, String properson, Date finishtime, Date truefistime, 
			String title, String missionname, String fileName, String rootPath, String finsit, String misexp, String user)
	{		
		String accessory = new String();
		String path = new String();
		if(folder == null||folder.equals("")){
			accessory = getAcc(rootPath, missionname, fileName);
			System.out.println("accessr+"+accessory);
		}	
		else{
		String[] folderName = folder.split("\\*");
		
		for(int i = 0;i<2;i++){
			path+=folderName[i]+"\\";
		}	
 		//拷贝temp中的文件到正确的文件夹
 		String[] newFile = fileName.split("\\*");
		for(String copyFile : newFile) {
			cutGeneralFile( rootPath+"temp\\"+copyFile,rootPath+path);
			deleteFile(rootPath+"temp\\"+copyFile);
		}
		if (fileName.charAt(fileName.length()-1)=='*') 
			fileName = fileName.substring(0,fileName.length()-1);	
		//存入数据库的内容
      	accessory = folderName[0] + "*"+folderName[1]+"*" + fileName;
      	
      	//doc转pdf
      	if(fileName.length()!=0)
      	{
      		String[] allFile = fileName.split("\\*");
      		for(String temp : allFile)
      		{
      			if(temp.endsWith(".doc"))
      			{
      				System.out.println(path+temp);
      				WordToPdf.translateThread(path+temp, path+temp.substring(0,temp.length()-4)+".pdf"); 			
      			}
      			else if(temp.endsWith(".docx"))
      			{
      				System.out.println(path+temp);
      				WordToPdf.translateThread(path+temp, path+temp.substring(0,temp.length()-5)+".pdf");
      			}
      			else if(temp.endsWith(".xlsx"))   				
      			{
      				System.out.println("-----------xlsx");
      				ExcelToPdf.excel2pdf(path+temp, path+temp.substring(0,temp.length()-5)+".pdf");
      			}
      			else if(temp.endsWith("xls"))
      				
      			{
      				System.out.println("----------xls");
      				ExcelToPdf.excel2pdf(path+temp, path+temp.substring(0,temp.length()-4)+".pdf");
      			}
      		} 		
		}
      
      	}
		
		
		System.out.println("过来了么 " + title);
		
		if(fileName.equals("null"))
  			accessory = "";
      	if(title.equals("完成任务")){
      		System.out.println(missionname+accessory);
      		
      		missionDAO.finishMission(missionname,user,truefistime,accessory,finsit);
      	}
      	else if(title.equals("分配任务")){
      		
      		System.out.println(properson);
      		
      		String pro[] = properson.split(",");
      		
      		
      		System.out.println("进来了么 " + title);
      		
      		for(int i = 1;i<pro.length;i++){
	      		System.out.println("+++++"+pro[i]);
	      		String per[] = pro[i].split("-");
	      		Missionstate mis = new Missionstate();
	      		mis.setExpecttime(finishtime);
	      		mis.setMissionname(missionname);
	      		String[] personname = per[0].split("【");
	      		mis.setProperson(personname[0]);
	      		mis.setProgroup(per[1]);
	      		mis.setIsdown("2");
	      		mis.setMissiontype(type);
	      		mis.setMisexplain(misexp);
	      		mis.setMissionfile(accessory);
	      		mis.setScore(Integer.parseInt(score));
	      		mis.setReleaseperson(user);
	      		mis.setReleasetime(new Date());
	      		missionDAO.insertMis(mis);
	      		
	      		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	      		
	      		String phone = basicInfoDAO.getPersonPhoneByName(mis.getProperson());
				//String content = pro[i] + "收到新任务," + "任务类型：" + mis.getMissiontype() + ",要求时间 ：" + mis.getExpecttime() +",任务名称： " + mis.getMissionname() + ",所属项目：" + mis.getProgroup();
				
				//您收到新任务，任务类型：XXX，要求时间：XXX，任务名称：XXX，请前往总承包项目安全管理系统【我的任务】查阅、办理。祝好！
				
				String name = mis.getProgroup() + "的" + mis.getProperson();
				String content = "任务类型： " + mis.getMissiontype() + ",要求时间： " + sdf.format( mis.getExpecttime() ) + "，任务名称： " + mis.getMissionname() + "，请前往总承包项目安全管理系统【我的任务】查阅、办理";
				
				SendTest.sendMessage(name,content, phone);
      		
      		
      		}
      		
      	}   		
      	
		return "{\"success\":\"true\"}";
	}
	
	public String deleteTask(String ID, String rootPath) {
		String json = "{\"success\":\"true\"}";
  		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String[] temp = ID.split(",");
		for (int i = 0; i < temp.length; i++) {
			Missionstate mis = missionDAO.getTask(Integer.parseInt(temp[i]));
			
      		String phone = basicInfoDAO.getPersonPhoneByName(mis.getProperson());
			//String content = pro[i] + "收到新任务," + "任务类型：" + mis.getMissiontype() + ",要求时间 ：" + mis.getExpecttime() +",任务名称： " + mis.getMissionname() + ",所属项目：" + mis.getProgroup();
			
			//您收到新任务，任务类型：XXX，要求时间：XXX，任务名称：XXX，请前往总承包项目安全管理系统【我的任务】查阅、办理。祝好！
			
			String name = mis.getProgroup() + "的" + mis.getProperson();
			String content = "任务（ " + mis.getMissionname() + "）已撤销。";
			System.out.println("姓名 ：" + mis.getProperson());
			System.out.println("电话 ：" + phone);
			SendTest.sendMessage(name, content, phone);

			missionDAO.deleteTask(mis);
		}
		return json;
	}
	
	public String getmytaizhang(String user,int start,int limit){
		 int total = missionDAO.gettotalTaz(user);
		 List<Taizhang> taizhang = missionDAO.getMyTaizhang(user,start,limit);
		 String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		 for(Taizhang taz : taizhang){
			 String solveTime = new String();
			 if(taz.getSolveTime() == null)
				 solveTime = "进行中";
			 else
				 solveTime = taz.getSolveTime().toString();
			 jsonStr += "{\"ID\":\"" + taz.getId() + "\",\"problem\":\"" + taz.getProblem() + "\","
				 		+"\"expTime\":\"" + taz.getExpTime() + "\",\"solveTime\":\"" + solveTime + "\",\"solveAcc\":\"" + taz.getSolveAcc() + "\"," 
				 		+"\"accessory\":\"" + taz.getAccessory() + "\",\"solveExp\":\"" + taz.getSolveExp() + "\",\"prevent\":\"" + taz.getPrevent() + "\","
				 		+"\"location\":\"" + taz.getLocation() + "\",\"checkperson\":\"" + taz.getCheckperson() + "\",\"prolevel\":\"" + taz.getProlevel() + "\","
				 		+"\"solvedep\":\"" + taz.getSolvedep() + "\",\"solvePerson\":\"" + taz.getSolvePerson() + "\","
				 		+"\"correction\":\"" + taz.getCorrection() + "\",\"correctionfee\":\"" + taz.getCorrectionfee() + "\",\"supperson\":\"" + taz.getSupperson() + "\"},";
		 }if(jsonStr.length()>1){
				jsonStr.substring(jsonStr.length()-1);
			}
		jsonStr += "]}";	
		System.out.println(jsonStr);
		return jsonStr;
	}
	
	public String finishDis(int id,String fileName,String problem,String solveExp,Date solveTime,String rootPath,String correction,String correctionfee,String prevent,String supperson){
		String json = "{\"success\":\"true\"}";
		String accessory = new String();
		String path = new String();
		if(fileName.equals("null"))
			accessory ="";
		else
			accessory = getAcc(rootPath, problem, fileName);
		
		Taizhang tai =  missionDAO.getTaizhang(id);
		
		tai.setSolveAcc(accessory);
		tai.setSolveTime(solveTime);
		tai.setSolveExp(solveExp.replace("\n","<br>"));
		tai.setCorrection(correction.replace("\n", "<br>"));
		tai.setCorrectionfee(correctionfee);
		tai.setSupperson(supperson);
		tai.setPrevent(prevent.replace("\n", "<br>"));	
		missionDAO.updateTaizhang(tai);
		
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
	
	public String getPersonNode(String role, String projectName){
		String json = "{";
		if (role != null && role.equals("项目部人员")) {
			List<Flownode> persons = missionDAO.getAllProjectPerson(projectName);
			String personStr = "";
			for(Flownode person : persons){
				personStr=personStr+person.getName()+"【"+person.getNodeName()+"】,";
			}
			personStr = personStr.substring(0, personStr.length()-1);
			json = json+"\""+projectName+"\":\""+personStr+"\",";
		} else {
			List<Flownode> allProject = missionDAO.getallProject();
			String[] otherType = {"院领导", "质安部管理员", "其他管理员"};
			for(Flownode temp : allProject){
				List<Flownode> allPerson = missionDAO.getprojectByname(temp.getProjectName());
				String personStr = new String();
				for(Flownode person : allPerson){
					personStr=personStr+person.getName()+"【"+person.getNodeName()+"】,";
				}
				personStr = personStr.substring(0, personStr.length()-1);
				json = json+"\""+temp.getProjectName()+"\":\""+personStr+"\",";
			}
			for (String temp : otherType) {
				String personStr = new String();
				List<Person> allPerson = missionDAO.getOtherType(temp);
				for(Person person : allPerson){
					personStr=personStr+person.getName()+"【"+temp+"】,";
				}
				personStr = personStr.substring(0, personStr.length()-1);
				json = json+"\""+temp+"\":\""+personStr+"\",";
			}
		}
		json = json.substring(0, json.length()-1);
		json+="}";
		System.out.println(json);
		return json;
	}
	
	public String getallprojectname(){
		String json = "{";
		List<Flownode> mission = missionDAO.getAllprojectname();
		for(Flownode mis : mission){
			json = json + "'"+mis.getProjectName()+"':"+"1,";
		}
		json+="}";
		System.out.println("json === "+json);
		return json;
	}
	
	public String getsomeprojectname(String type){
		String json = "{";
		List<Projectmanagement> mission = missionDAO.getSomeprojectname(type);
		for(Projectmanagement mis : mission){
			json = json + "'" + mis.getName()+"':"+"1,";
		}
		json+="}";
		System.out.println("json === "+json);
		return json;
	}
	
	public String updateMissionState(String project, int actionId) {
		Date now = new Date();
		missionDAO.updateMissionState(project, actionId);
		return "{\"success\":\"true\"}";
	}
	
	public String getPrescribedAction(String project) {
		Date now = new Date();
		List<PrescribedActionView> list = missionDAO.getPrescribedAction(project);
		return list.toString();
	}	
	
	public void init() {
		missionDAO.init();
	}
}
