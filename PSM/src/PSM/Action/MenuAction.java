package PSM.Action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import PSM.DAO.MenuDAO;
import hibernate.Menus;

public class MenuAction extends ActionSupport
{
	private MenuDAO menuDAO;
	private String role;
	private String projectName ;
	

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public MenuDAO getMenuDAO() 
	{
		return menuDAO;
	}

	public void setMenuDAO(MenuDAO menuDAO) 
	{
		this.menuDAO = menuDAO;
	}
	
	public MenuAction() throws Exception
	{
		
	}
	

	/**
	 * 登录的方法   余明星修改  
	 * @return
	 * @throws Exception
	 */
	public String login() throws Exception{
		HttpServletRequest request = ServletActionContext.getRequest();
        Map session = (Map)ActionContext.getContext().getSession(); 
		
        
        //判断是否已经登录
        if(session.get("UserName")==null)
        	return "failer";
        
        //获取LoginAction传递过来的role
//		if(request.getAttribute("role") != null)
//			 role = request.getAttribute("role").toString();
//		else 
//			role = request.getParameter("role");
	
		role = (String) session.get("UserLoginType");
		session.put("UserType", role);
		
		return loadmenu(role);
	}
	
	
	/**
	 * 加载全部项目的方法  余明星修改  
	 * @return
	 */
	public String all()throws Exception{
		Map session = (Map)ActionContext.getContext().getSession(); 
		
		role = "全部项目";
		session.put("UserType", "全部项目");

        //判断是否已经登录
        if(session.get("UserLoginType")==null || session.get("UserLoginType")=="项目部人员")
        	return "failer";
		
		return loadmenu(role);
	}
	
	
	/**
	 * 异步存取projectName的方法
	 * @return 
	 * @throws IOException 
	 */
	public String savePname() throws IOException{
		// HttpServletRequest request = ServletActionContext.getRequest();
		 Map session = (Map)ActionContext.getContext().getSession(); 
		// projectName = request.getParameter("projectName");
		 //projectName = new String(projectName.getBytes("ISO-8859-1"),"utf-8");
		 session.put("projectName", projectName);
		
		
		return NONE;
	}
	
	
	/**
	 * 查询三级菜单，即叶子节点
	 */
	public void getleaf() throws Exception{
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String role = request.getParameter("role");
		String name = request.getParameter("name");
		System.out.println(name+"aaaaaaaaaaaaaaaa");
		//String role = new String(role1.getBytes("iso-8859-1"),"utf-8");
		//String name = new String(name1.getBytes("iso-8859-1"),"utf-8");
		System.out.println(role +"bbbbbbbbbb");
		//new String(role.getBytes("iso-8859-1"),"utf-8");
		int i, childID;
		String json = "[";
		childID = menuDAO.getChildId(name, role);	//获取二级菜单ID
		List<Menus> list = menuDAO.getMenuList(role);
		for(i=0; i<list.size(); i++){
			Menus temp = list.get(i);
			if(temp.getPid() == childID && temp.getPpid() != 0){		//遍历数据库，获取leaf节点
				//json += "{'text':'" + temp.getName() + "','id':'" + temp.getId() + "','leaf':'true'},"; 
				json += "{\"text\":\"" + temp.getName() + "\",\"id\":\"" + temp.getId() + "\",\"leaf\":\"true\"},"; 
			}
		}
		i = json.length();
		if(i>1)
			json = json.substring(0, i-1);
		json += "]";
		System.out.println(json);
		out.write(json);
		out.flush();
		out.close();
		//return SUCCESS;
	}
	
	
	/**
	 * 加载菜单的公用方法 余明星修改  
	 */
	public String loadmenu(String role){
		
		HttpServletRequest request = ServletActionContext.getRequest();
        Map session = (Map)ActionContext.getContext().getSession(); 
		int i, j;
		String menu_li="";
		ArrayList<String> menu_Root = new ArrayList<String>();
		ArrayList<ArrayList<String>> menu_Chlid = new ArrayList<ArrayList<String>>();
		List<Menus> list = menuDAO.getMenuList(role);
		for(i=0; i<list.size(); i++){
			Menus temp = list.get(i);
			if(temp.getPid() == 0){
				menu_li += "<li id=\"Li" + i + "\"  class=\"nav\" onclick=\"\">" + temp.getName() + "</li>";	  //<li id="Li3" class="nav"  onclick="">专利管理</li>
				menu_Root.add("\"" + temp.getName() + "\"");	//字符串加上双引号方便js中获取数组
			}else if(temp.getPid()!= 0 && temp.getPpid() == 0){
				ArrayList<String> child = new ArrayList<String>();	//二级菜单数组
				child.add("\"" + temp.getName() + "\"");				
				child.add("\"" + menuDAO.getChildName(temp.getPid()) + "\"");	//根据三级菜单的Pid在数据库中查询二级菜单父节点
				
				menu_Chlid.add(child);
			}
		}
		request.setAttribute("menu_li", menu_li);
		request.setAttribute("menu_Root", menu_Root);
		request.setAttribute("menu_Child", menu_Chlid);	
		return "getRootMenu";
	}
}
