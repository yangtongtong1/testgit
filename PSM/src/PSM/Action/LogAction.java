package PSM.Action;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;

import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;



import hibernate.Log;


import com.opensymphony.xwork2.ActionSupport;

import PSM.DAO.LogDAO;

	public class LogAction extends ActionSupport {
		private LogDAO logDAO;
		public LogDAO getLogDAO() {
			return logDAO;
		}
		public void setLogDAO(LogDAO logDAO) {
			this.logDAO = logDAO;
		}

		private int start;	//分页查询参数
		private int limit;	//分页查询参数
		BASE64Encoder enc=new BASE64Encoder();
		public int getStart() {
			return start;
		}
		public void setStart(int start) {
			this.start = start;
		}
		public int getLimit() {
			return limit;
		}

		public void setLimit(int limit) {
			this.limit = limit;
		}
		public LogAction(){
		}
		public String execute() throws Exception{	
			return SUCCESS;
		}
		public void GetLoginLogs(){
			HttpServletResponse response = ServletActionContext.getResponse();
			HttpServletRequest request = ServletActionContext.getRequest();
			List<Log> list = logDAO.getLoginLog(start, limit);
			int total = logDAO.totalLogin();
			String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
			for(int i = 0; i < list.size(); ++i){
				if(i > 0)
					jsonStr += ",";
				Log l = list.get(i);
				jsonStr += "{\"XH\":\"" + l.getId() + "\",\"UserName\":\"" + l.getUserName() + "\",\"Role\":\"" + l.getRole() + "\"";
				jsonStr += ",\"Logintime\":\"" + l.getLogintime() + "\",\"Loginip\":\"" + l.getLoginip() + "\"}";
			}
			jsonStr += "]}";
			try{
				response.setCharacterEncoding("UTF-8");
				PrintWriter out = response.getWriter();
				out.write(jsonStr);
				out.flush();
				out.close();
			}catch(Exception e){
				e.printStackTrace();
				//return ERROR;
			}
			
			//return SUCCESS;
		}
		public void InsertOptLog(String Opt) throws Exception{
			HttpServletRequest request = ServletActionContext.getRequest();
			HttpSession session = request.getSession();
			Log log = new Log();
			String name = (String)session.getAttribute("UserName");
			String note = name + Opt;
			String type = (String)session.getAttribute("UserType");
			//String unit = (String)session.getAttribute("UserUnit");
			String sql = "INSERT INTO log(UserName,Action,Role,Logintime,Loginip,note) VALUES('"+name+"','"+"Opt"+"','"+type+"','"+new Timestamp(System.currentTimeMillis())+"','"+request.getRemoteAddr()+"','"+note+"')";
			String url="jdbc:mysql://125.220.159.160:3306/psm?useUnicode=true&amp;characterEncoding=utf8";
			String user = "root";
			System.out.println(sql);
			String pwd = "rat605";  
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection conn = DriverManager.getConnection(url, user, pwd);
			Statement stmt = conn.createStatement();
			stmt.execute(sql);	
			stmt.close();
			conn.close();
//			log.setUserName(name);
//			log.setDepartment(unit);
//			log.setAction("Opt");
//			log.setRole(type);
//			log.setLoginip(request.getRemoteAddr());
//			log.setLogintime(new Timestamp(System.currentTimeMillis()));   //记录登录日志
//			log.setNote(name+Opt);
//			logDAO.Insert(log);
		}
		
		public void GetOptLogs(){
			HttpServletResponse response = ServletActionContext.getResponse();
			HttpServletRequest request = ServletActionContext.getRequest();
			List<Log> list = logDAO.getOptLog(start, limit);
			int total = logDAO.totalOpt();
			String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
			for(int i = 0; i < list.size(); ++i){
				if(i > 0)
					jsonStr += ",";
				Log l = list.get(i);
				jsonStr += "{\"XH\":\"" + l.getId() + "\",\"UserName\":\"" + l.getUserName() + "\",\"Role\":\"" + l.getRole() + "\"";
				jsonStr += ",\"OptTime\":\"" + l.getLogintime() + "\",\"Opt\":\"" + l.getNote().replace("\"", "'") + "\"}";
			}
			jsonStr += "]}";
			try{
				response.setCharacterEncoding("UTF-8");
				PrintWriter out = response.getWriter();
				out.write(jsonStr);
				out.flush();
				out.close();
			}catch(Exception e){
				e.printStackTrace();
				//return ERROR;
			}
			
			//return SUCCESS;
		}
		
		
		public void SearchLoginLog(){
			HttpServletResponse response = ServletActionContext.getResponse();
			HttpServletRequest request = ServletActionContext.getRequest();
			try{
				System.out.println(start+"aaa"+limit);
				String temp = request.getParameter("findstr");
				String[] fs = temp.split(",");
				List<Log> list = logDAO.SearchLogin(fs,start,limit);
				int total = logDAO.totalSearchLogin(fs);
				System.out.println("total:"+total);
				String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
				for(int i = 0; i < list.size(); ++i){
					if(i > 0)
						jsonStr += ",";
					Log l = list.get(i);
					jsonStr += "{\"XH\":\"" + l.getId() + "\",\"UserName\":\"" + l.getUserName() + "\",\"Role\":\"" + l.getRole() + "\"";
					jsonStr += ",\"Logintime\":\"" + l.getLogintime() + "\",\"Loginip\":\"" + l.getLoginip() + "\"}";
				}
				jsonStr += "]}";
				response.setCharacterEncoding("UTF-8");
				PrintWriter out = response.getWriter();
				out.write(jsonStr);
				out.flush();
				out.close();
			}catch(Exception e){
				e.printStackTrace();
				//return ERROR;
			}
			//return "Query";
		}
		
		public void SearchOptLog(){
			HttpServletResponse response = ServletActionContext.getResponse();
			HttpServletRequest request = ServletActionContext.getRequest();
			try{
				String temp = request.getParameter("findstr");
				String[] fs = temp.split(",");
				List<Log> list = logDAO.SearchOpt(fs,start,limit);
				int total = logDAO.totalSearchOpt(fs);
				String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
				for(int i = 0; i < list.size(); ++i){
					if(i > 0)
						jsonStr += ",";
					Log l = list.get(i);
					jsonStr += "{\"XH\":\"" + l.getId() + "\",\"UserName\":\"" + l.getUserName() + "\",\"Role\":\"" + l.getRole() + "\"";
					jsonStr += ",\"OptTime\":\"" + l.getLogintime() + "\",\"Opt\":\"" + l.getNote() + "\"}";
				}
				jsonStr += "]}";
				response.setCharacterEncoding("UTF-8");
				PrintWriter out = response.getWriter();
				out.write(jsonStr);
				out.flush();
				out.close();
			}catch(Exception e){
				e.printStackTrace();
				//return ERROR;
			}
			//return "Query";
		}
		
		public void GetMessageLogs(){
			HttpServletResponse response = ServletActionContext.getResponse();
			HttpServletRequest request = ServletActionContext.getRequest();
			List<Log> list = logDAO.GetMessageLogs(start, limit);
			int total = logDAO.totalOpt();
			String jsonStr = "{\"total\":\"" + total + "\",\"rows\":[";
			for(int i = 0; i < list.size(); ++i){
				if(i > 0)
					jsonStr += ",";
				Log l = list.get(i);
				jsonStr += "{\"ID\":\"" + l.getId() + "\",\"SendTo\":\"" + l.getUserName() + "\",\"SendPhone\":\"" + l.getLoginip() + "\",\"Type\":\"" + l.getRole() + "\"";
				jsonStr += ",\"SendTime\":\"" + l.getLogintime() + "\",\"SendContent\":\"" + l.getNote().replace("\"", "'") + "\"}";
			}
			jsonStr += "]}";
			try{
				response.setCharacterEncoding("UTF-8");
				PrintWriter out = response.getWriter();
				out.write(jsonStr);
				out.flush();
				out.close();
			}catch(Exception e){
				e.printStackTrace();
				//return ERROR;
			}
			
			//return SUCCESS;
		}
	
	}