package PSM.Service;

import java.util.*;

import com.sun.org.apache.regexp.internal.recompile;

import PSM.DAO.MapDAO;
import hibernate.Monitor;

public class MapService {
	private MapDAO mapDAO;

	public MapDAO getMapDAO() {
		return mapDAO;
	}
	public void setMapDAO(MapDAO mapDAO) {
		this.mapDAO = mapDAO;
	}
	
	public String getMonitor(int id) {
		Monitor mot = mapDAO.getMonitor(id);
		String jsonStr = "";
		jsonStr += "{\"monitorName\":\"" + mot.getMonitorName() + "\",\"longitude\":\"" + mot.getLongitude() + "\",\"latitude\":\"" + mot.getLatitude() + "\",\"remarks\":\"" + mot.getRemarks() + "\",";
		 jsonStr += "\"ID\":\"" + mot.getId() + "\",\"userName\":\"" + mot.getUserName() + "\",\"userPwd\":\"" + mot.getUserPwd() + "\",\"ipaddress\":\"" + mot.getIpaddress() + "\",";
		 jsonStr +="\"port\":\"" + mot.getPort() + "\",\"mobilePort\":\"" + mot.getMobilePort() + "\",\"channel\":\"" + mot.getChannel() + "\",\"defaultpos\":\"" + mot.getDefaultpos() + "\"}";
		return jsonStr;
	}
	
	public String getMonitorList(String projectName){
		
		String jsonStr = new String("[");
		
		List<Monitor> monitorList = mapDAO.monitorList(projectName);
		
		for(Monitor mot : monitorList){
			 jsonStr += "{\"monitorName\":\"" + mot.getMonitorName() + "\",\"longitude\":\"" + mot.getLongitude() + "\",\"ID\":\"" + mot.getId() + "\",\"latitude\":\"" + mot.getLatitude() + "\",\"remarks\":\"" + mot.getRemarks() + "\"},";
		}
		if(jsonStr.length()>1){
			jsonStr.substring(jsonStr.length()-1);
		}
		jsonStr += "]";		
		return jsonStr;
	}
	
	public String getMonitorList(int start,int limit, String projectName) {
		
		int total = mapDAO.totalMonitor();
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		List<Monitor> monitorList = mapDAO.monitorList(start, limit, projectName);
		for(Monitor mot : monitorList){
			 jsonStr += "{\"monitorName\":\"" + mot.getMonitorName() + "\",\"longitude\":\"" + mot.getLongitude() + "\",\"latitude\":\"" + mot.getLatitude() + "\",\"remarks\":\"" + mot.getRemarks() + "\",";
			 jsonStr += "\"ID\":\"" + mot.getId() + "\",\"userName\":\"" + mot.getUserName() + "\",\"userPwd\":\"" + mot.getUserPwd() + "\",\"ipaddress\":\"" + mot.getIpaddress() + "\",";
			 jsonStr +="\"port\":\"" + mot.getPort() + "\",\"mobilePort\":\"" + mot.getMobilePort() + "\",\"channel\":\"" + mot.getChannel() + "\",\"defaultpos\":\"" + mot.getDefaultpos() + "\",\"projectName\":\"" + mot.getProjectName() + "\"},";
		}
		if(jsonStr.length()>1){
			jsonStr.substring(jsonStr.length()-1);
		}
		jsonStr += "]}";		
		return jsonStr;
	}
	
	public String addMonitor(Monitor mot){
		String jsonStr = "{\"success\":\"true\",\"msg\":[{\"ms\":\"修改成功！\"}]}";
		mapDAO.addMonitor(mot);
		return jsonStr;
	}
	
	public String getCenterPoint(){
		String jsonStr = new String("[");
		
		List<Monitor> monitorList = mapDAO.getCenterPoint();
		
		for(Monitor mot : monitorList){
			 jsonStr += "{\"longitude\":\"" + mot.getLongitude() + "\",\"latitude\":\"" + mot.getLatitude() + "\"},";
		}
		if(jsonStr.length()>1){
			jsonStr.substring(jsonStr.length()-1);
		}
		jsonStr += "]";		
		return jsonStr;
	}
	
	public String monitorSearch(String findStr,int start,int limit){
		int total;
		total = mapDAO.searchAllMonitor(findStr);
		String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
		List<Monitor> monitorList = mapDAO.monitorSearch(findStr,start,limit);	
		for(Monitor mot : monitorList){
			 jsonStr += "{\"monitorName\":\"" + mot.getMonitorName() + "\",\"longitude\":\"" + mot.getLongitude() + "\",\"latitude\":\"" + mot.getLatitude() + "\",\"remarks\":\"" + mot.getRemarks() + "\",";
			 jsonStr += "\"ID\":\"" + mot.getId() + "\",\"userName\":\"" + mot.getUserName() + "\",\"userPwd\":\"" + mot.getUserPwd() + "\",\"ipaddress\":\"" + mot.getIpaddress() + "\",";
			 jsonStr +="\"port\":\"" + mot.getPort() + "\",\"mobilePort\":\"" + mot.getMobilePort() + "\",\"channel\":\"" + mot.getChannel() + "\"},";
		}
		if(jsonStr.length()>1){
			jsonStr.substring(jsonStr.length()-1);
		}
		jsonStr += "]}";		
		return jsonStr;
	}
	
	public String deleteMonitor(String ID){
		String json = "{\"success\":\"true\"}";
		String[] temp = ID.split(",");
		for(int i=0; i<temp.length; i++){
			Monitor mot = new Monitor();
			mot.setId(Integer.parseInt(temp[i]));
			mapDAO.deleteMonitor(mot);
		}
		return json;
	}
	public String updateMonitor(Monitor mot){			
		String jsonStr = "{\"success\":\"true\",\"msg\":[{\"ms\":\"修改成功！\"}]}";
	    mapDAO.updateMonitor(mot);
	    return jsonStr;
	}
	public String setCenter(int ID){
		mapDAO.setCenterPoint(ID);
		String jsonStr = "{\"success\":\"true\"}";
	    return jsonStr;
	}
	
	public String getMonitorNameList() {
		List list = mapDAO.getMonitorNameList();
		String jsonStr = "[";
		for (int i = 0; i < list.size(); i++) {
			if (i > 0)
				jsonStr += ",";
			String temp = "";
			temp += list.get(i);
			jsonStr += "{\"MonitorName\":\"" + temp + "\"}";
		}
		jsonStr += "]";
		return jsonStr;
	}
}
