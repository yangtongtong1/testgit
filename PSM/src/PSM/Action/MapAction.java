package PSM.Action;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import PSM.Service.MapService;

import com.opensymphony.xwork2.ActionSupport;

import hibernate.Monitor;


public class MapAction extends ActionSupport {
	private MapService mapService;
	private int start;	//分页查询参
	private int limit;	//分页查询参数  
	
	
	public MapService getMapService() {
		return mapService;
	}
	public void setMapService(MapService mapService) {
		this.mapService = mapService;
	}
	public int getStart(){
		return start;
	}
	public void setStart(int start){
		this.start = start;
	}
	public int getLimit(){
		return limit;
	}
	public void setLimit(int limit){
		this.limit = limit;
	}
	
	private void outputJSON(HttpServletResponse response, String jsonStr) throws Exception {
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.write(jsonStr);
		out.flush();
		out.close();
	}
	
	public String getMonitorInfo(){
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			Integer id = Integer.parseInt(request.getParameter("id"));
			outputJSON(response, mapService.getMonitor(id));
		}
		catch(Exception e){
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String setMonitorInMap(){
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		try{	
			outputJSON(response,mapService.getMonitorList(request.getParameter("projectName")));
		}
		catch(Exception e){
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String getMonitorListInPage(){
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		try{
			String projectName = request.getParameter("projectName");
			outputJSON(response,mapService.getMonitorList(start, limit, projectName));
		}
		catch(Exception e){
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String addMonitor(){
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		
		Monitor mot = new Monitor();
		mot.setMonitorName(request.getParameter("monitorName"));
		mot.setLongitude(Double.parseDouble(request.getParameter("longitude")));
		mot.setLatitude(Double.parseDouble(request.getParameter("latitude")));
		mot.setIpaddress(request.getParameter("ipaddress"));
		mot.setPort(request.getParameter("port"));
		mot.setMobilePort(request.getParameter("mobilePort"));
		mot.setRemarks(request.getParameter("remarks"));
		mot.setUserName(request.getParameter("userName"));
		mot.setUserPwd(request.getParameter("userPwd"));
		mot.setChannel(request.getParameter("channel"));
		mot.setDefaultpos(0);
		mot.setProjectName(request.getParameter("projectName"));
		try{
		outputJSON(response,mapService.addMonitor(mot));
		}catch(Exception e){
			e.printStackTrace();
			return ERROR;
		}	
		return SUCCESS;
	}
	
	public String getCenterPoint(){
		HttpServletResponse response = ServletActionContext.getResponse();
		try{	
			System.out.println(mapService.getCenterPoint());
			outputJSON(response,mapService.getCenterPoint());
		}
		catch(Exception e){
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String searchMonitor()
	{
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		try
		{
			String findstr =  request.getParameter("findStr");
			String projectNo = request.getParameter("projectNo");
			outputJSON(response,mapService.monitorSearch(findstr,start,limit));
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String deleteMonitor(){
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		String ID = request.getParameter("id");
		try{
		outputJSON(response,mapService.deleteMonitor(ID));
		}catch(Exception e){
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String editMonitor(){
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		Monitor mot = new Monitor();
		mot.setId(Integer.parseInt(request.getParameter("ID")));
		mot.setMonitorName(request.getParameter("monitorName"));
		mot.setLongitude(Double.parseDouble(request.getParameter("longitude")));
		mot.setLatitude(Double.parseDouble(request.getParameter("latitude")));
		mot.setIpaddress(request.getParameter("ipaddress"));
		mot.setPort(request.getParameter("port"));
		mot.setMobilePort(request.getParameter("mobilePort"));
		mot.setRemarks(request.getParameter("remarks"));
		mot.setUserName(request.getParameter("userName"));
		mot.setUserPwd(request.getParameter("userPwd"));
		mot.setChannel(request.getParameter("channel"));
		mot.setDefaultpos(0);
		mot.setProjectName(request.getParameter("projectName"));
		try{
		outputJSON(response,mapService.updateMonitor(mot));
		}catch(Exception e){
		e.printStackTrace();
		return ERROR;
	}
		return SUCCESS;
	}
	public String setCenterPoint(){
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		int ID = Integer.parseInt(request.getParameter("ID"));
		try{
			outputJSON(response,mapService.setCenter(ID));
			}catch(Exception e){
			e.printStackTrace();
			return ERROR;
		}
			return SUCCESS;
	}
	
	public void getMonitorNameList() {
		HttpServletResponse response = ServletActionContext.getResponse();
		//HttpServletRequest request = ServletActionContext.getRequest();
		try {
			String jsonStr = mapService.getMonitorNameList();
			System.out.println(jsonStr);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonStr);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// return ERROR;
		}
	}
}
