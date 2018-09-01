package PSM.Tool;



import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

import hibernate.AttLog;
import hibernate.UserInfo;

public class JacobUtil {
	
/*	public static void main(String[] args) throws IOException{

		String IPAdd = "172.16.141.151"; 

    	List<AttLog> attLogList = getAttLogList(IPAdd,4370);
    	
    	List<UserInfo> userInfoList =  getUserInfoList(IPAdd,4370);

    	System.out.println("userInfoList:"+userInfoList.size());
    	

    	System.out.println("attLogList:"+attLogList.size());
    	
    	int total = attLogList.size();
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		for(int i = 0; i < attLogList.size(); ++i) {
			if(i > 0)
				jsonStr += ",";
			AttLog p = attLogList.get(i);
			jsonStr += "{\"EnrollNumber\":\"" + p.getEnrollNumber()
					+ "\",\"VerifyMode\":\"" + p.getVerifyMode()
					+ "\",\"InOutMode\":\"" + p.getInOutMode()
					+ "\",\"Time\":\"" + p.getTime()
					+ "\",\"WorkCode\":\"" + p.getWorkCode()
					+ "\"}";
		}
		jsonStr += "]}";
		System.out.println(jsonStr);
}*/
	
	public static List<AttLog> getAttLogList(String IPAddress,int port) throws UnsupportedEncodingException{
		System.out.println("PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP");
		 List<AttLog> result = new ArrayList<AttLog>();
			ComThread.InitSTA();  
	        ActiveXComponent app = new ActiveXComponent("zkemkeeper.ZKEM.1");  
	        Dispatch TFT = (Dispatch) app.getObject();
	        Boolean loginResult = Dispatch.call(TFT, "Connect_Net", IPAddress  ,port).getBoolean();
	        if(loginResult){
	        	//System.out.println("connected success");
	        	Boolean readData = Dispatch.call(TFT, "ReadGeneralLogData",(int)254).getBoolean();
	        	if(readData){
	        		//System.out.println("readData success");       		
	        		String EnrollNumber = "";
	        		int imachineNumber =254;
	        		Variant enrollNumber = new Variant(EnrollNumber, true);
	        		Variant idwVerifyMode = new Variant(0, true);
	        		Variant idwInOutMode= new Variant(0, true);
	        		Variant idwYear= new Variant(0, true);
	        		Variant idwMonth = new Variant(0, true);
	        		Variant idwDay= new Variant(0, true);
	        		Variant idwHour= new Variant(0, true);
	        		Variant idwMinute= new Variant(0, true);
	        		Variant idwSecond= new Variant(0, true);
	        		Variant idwWorkcode = new Variant(0, true);
	        		// int count = 0;
	        		// System.out.println("count"+"     "+"EnrollNumber"+"    "+"verifyMode"+"    "+"InOutMode"+"    "+"time"+"    "+"workCode");
	        		while(Dispatch.call(TFT, "SSR_GetGeneralLogData",imachineNumber,enrollNumber, idwVerifyMode,
	        				idwInOutMode, idwYear, idwMonth, idwDay, idwHour,idwMinute,idwSecond, idwWorkcode).getBoolean())
	        		{
	        			//count++;
	        			AttLog attLog = new AttLog();
	        			attLog.setEnrollNumber(enrollNumber.toString());
	        			attLog.setVerifyMode(idwVerifyMode.toString());
	        			attLog.setInOutMode(idwInOutMode.toString());
	        			attLog.setTime(idwYear.toString() + "-" + idwMonth.toString() + "-" + idwDay.toString() + " " + idwHour.toString() + ":" + idwMinute.toString() + ":" + idwSecond.toString());
	        			attLog.setWorkCode(idwWorkcode.toString());
	        			result.add(attLog);
	        			//System.out.println(" "+count+"                      "+attLog.getEnrollNumber()+"                      "+attLog.getVerifyMode()+"            "+attLog.getInOutMode()+"         "+attLog.getTime()+"       "+attLog.getWorkCode());
    				
	        		}
	        		return result;
	        	}
	        	
	        }
	        
		return result;
	}
	public static List<UserInfo> getUserInfoList(String IPAddress,int port) throws IOException{
		 	List<UserInfo> result = new ArrayList<UserInfo>();
		 	ComThread.InitSTA();  
	        ActiveXComponent app = new ActiveXComponent("zkemkeeper.ZKEM.1");  
	        Dispatch TFT = (Dispatch) app.getObject();
	        Boolean loginResult = Dispatch.call(TFT, "Connect_Net", IPAddress  ,port).getBoolean();
	        if(loginResult){
	        	System.out.println("connected success");
	        	Boolean readData = Dispatch.call(TFT, "ReadAllUserId",(int)254).getBoolean();
	        	if(readData){
	        		System.out.println("readUserData success");
	        		String EnrollNumber = "";
	        		int imachineNumber =254;
	        		Variant enrollNumber = new Variant(EnrollNumber, true);
	        		Variant name = new Variant("",true);
	        		Variant password = new Variant("",true);
	        		Variant privilege = new Variant(0,true);
	        		Variant enabled  = new Variant(true,true);
	        		//    int count=0;
	        		while(Dispatch.call(TFT, "SSR_GetAllUserInfo",imachineNumber,enrollNumber,name,password,privilege,enabled).getBoolean())
	        		{
	        			//count++;
	        			UserInfo userInfo  = new UserInfo();
	        			userInfo.setEnrollNumber(enrollNumber.toString());
	        			String tempName=StringFilter(name.toString());
	        			for(int i =0 ; i < tempName.length();i++){
	        				if((int)tempName.charAt(i) == 0){
	        					tempName=tempName.substring(0,i);
	        					break;
	        				}
	        			}
	        			userInfo.setName(tempName);
	        			userInfo.setPassword(password.toString());
	        			userInfo.setPrivilege(privilege.toString());
	        			userInfo.setEnabled(enabled.toString());
	        			result.add(userInfo);
	        			//System.out.println(count+"    "+userInfo.getEnrollNumber()+"    "+userInfo.getName()+"    "+userInfo.getPrivilege()+"   "+userInfo.getEnabled());
	        		}
	        	}
	        	else
	        		System.out.println("readUserData failed");
	        }
	        else
	        	System.out.println("connected failed");
		return result;
	}
	public static String StringFilter(String str) throws PatternSyntaxException { 
		// 清除掉所有特殊字符 
				String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、?]"; 
				Pattern p = Pattern.compile(regEx); 
				Matcher m = p.matcher(str);
				return m.replaceAll("").trim();
				} 
}
