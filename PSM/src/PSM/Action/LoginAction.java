package PSM.Action;

import java.sql.Timestamp;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import PSM.DAO.LoginDAO;
import PSM.Tool.DESEncryptCoder;
import PSM.Tool.Validater;
import hibernate.Admin;
import hibernate.Log;
import hibernate.Person;
import hibernate.Persondb;
import sun.misc.BASE64Encoder;

public class LoginAction extends ActionSupport
{
	private String userid;
	private String type;
	private String name;
	private String phone;
	private String projectName;
	private String json;
	private int userrank;
	private Admin user;
	private Persondb pdb;
	private Person ps;
	
	private LoginDAO loginDAO;
	public LoginDAO getLoginDAO() 
	{
		return loginDAO;
	}

	public void setLoginDAO(LoginDAO loginDAO) 
	{
		this.loginDAO = loginDAO;
	}
	
	public LoginAction()
	{
		
	}
	@SuppressWarnings("unchecked")
	@Override
	public String execute() throws Exception
	{ 
		HttpServletRequest request = ServletActionContext.getRequest();
        Map session = (Map)ActionContext.getContext().getSession(); 
		String errMsg = null;
        String ReturnStr="failer";
        BASE64Encoder enc=new BASE64Encoder();
		//String usertype = request.getParameterValues("usertype")[0];
//        type = "系统管理员";
//        if(usertype.equals("5")){
//        	request.setAttribute("role", "项目部人员");
//        	session.put("UserName","张三");
//			session.put("UserType","项目部人员");
//        	return "UserLogin";
//        };
//        request.setAttribute("role", "系统管理员");
        
        String testuser=request.getParameter("UserName");//登陆用户名
        String usertype = null;
        //去掉类型选择，后台根据username判断类型
        if(loginDAO.IsAdmin(testuser)) {
        	usertype = "1";
        }else if(loginDAO.IsPerson(testuser)) {
        	String type = loginDAO.QueryPerson(testuser).getType();
        	if(type.equals("院领导"))
        		usertype = "2";
        	else if(type.equals("质安部管理员"))
        		usertype = "3";
        	else
        		usertype = "4";
        } else {
        	usertype = "5";
        }
        
        
        String testPs=request.getParameter("UserPwd");
    	Boolean judgePwd = false;
		Boolean judgeRand = false;
        if(!Validater.validate(testuser)||!Validater.validate(testPs))
        {
        	errMsg = "用户名或密码含非法字符!";
			session.put("errMsg", errMsg);
			return ReturnStr;
        }
        if(usertype.equals("1"))//从Admin中验证
		{
		request.setAttribute("role", "系统管理员");
		
		type = "系统管理员";
		user = loginDAO.QueryUser(testuser);
		if(user == null)
		{
			errMsg = "该用户不存在!";
			session.put("errMsg", errMsg);
			return ReturnStr;
		}
		name = user.getUserName();
		
		projectName = loginDAO.getProjectName();
		
		judgePwd = user.getUserPwd().equals(enc.encode(DESEncryptCoder.encrypt(testPs.getBytes(), DESEncryptCoder.KEY)));
		}
        else if(usertype.equals("5"))//从User中验证
		{
			request.setAttribute("role", "项目部人员");
			
			type = "项目部人员";
			pdb = loginDAO.QueryPersondb(testuser);
			
			if(pdb == null)
			{
				errMsg = "该用户不存在!";
				session.put("errMsg", errMsg);
				return ReturnStr;
			}
			//request.setAttribute("UserId", pdb.getIdcard());
			//request.setAttribute("UserName", pdb.getIdcard());
			name = pdb.getName();
			
			if(!loginDAO.QueryHasProject(name)) {
				errMsg = "您目前没有管理项目的权限，请联系管理员!";
				session.put("errMsg", errMsg);
				return ReturnStr;
			}
			//type = pdb.getPtype();
			phone = pdb.getPhone();
			userid = pdb.getIdcard();
			userrank = 5;
			
			projectName = loginDAO.getPersondbProjectName(name);
			
			//session.put("UserTitle",user.getTitle());
			judgePwd = pdb.getUserPwd().equals(enc.encode(DESEncryptCoder.encrypt(testPs.getBytes(), DESEncryptCoder.KEY)));
		}
		else if(usertype.equals("2"))//从User中验证
		{
			request.setAttribute("role", "院领导");
			
			type = "院领导";
			ps = loginDAO.QueryPerson(testuser);
			
			if(ps == null)
			{
				errMsg = "该用户不存在!";
				session.put("errMsg", errMsg);
				return ReturnStr;
			}
			//request.setAttribute("UserId", pdb.getIdcard());
			//request.setAttribute("UserName", pdb.getIdcard());
			name = ps.getName();
			//type = pdb.getPtype();
			phone = ps.getPhoneNo();
			userid = ps.getIdentityNo();
			userrank = 2;
			projectName = loginDAO.getProjectName();
			//session.put("UserTitle",user.getTitle());
			judgePwd = ps.getUserPwd().equals(enc.encode(DESEncryptCoder.encrypt(testPs.getBytes(), DESEncryptCoder.KEY)));
		}
		else if(usertype.equals("3"))//从User中验证
		{
			request.setAttribute("role", "质安部管理员");
			
			type = "质安部管理员";
			ps = loginDAO.QueryPerson(testuser);
			
			if(ps == null)
			{
				errMsg = "该用户不存在!";
				session.put("errMsg", errMsg);
				return ReturnStr;
			}
			//request.setAttribute("UserId", pdb.getIdcard());
			//request.setAttribute("UserName", pdb.getIdcard());
			name = ps.getName();
			//type = pdb.getPtype();
			phone = ps.getPhoneNo();
			userid = ps.getIdentityNo();
			userrank = 3;
			projectName = loginDAO.getProjectName();
			//session.put("UserTitle",user.getTitle());
			judgePwd = ps.getUserPwd().equals(enc.encode(DESEncryptCoder.encrypt(testPs.getBytes(), DESEncryptCoder.KEY)));
		}
		else if(usertype.equals("4"))//从User中验证
		{
			request.setAttribute("role", "其他管理员");
			
			type = "其他管理员";
			ps = loginDAO.QueryPerson(testuser);
			
			if(ps == null)
			{
				errMsg = "该用户不存在!";
				session.put("errMsg", errMsg);
				return ReturnStr;
			}
			//request.setAttribute("UserId", pdb.getIdcard());
			//request.setAttribute("UserName", pdb.getIdcard());
			name = ps.getName();
			//type = pdb.getPtype();
			phone = ps.getPhoneNo();
			userid = ps.getIdentityNo();
			userrank = 4;
			projectName = loginDAO.getProjectName();
			//session.put("UserTitle",user.getTitle());
			judgePwd = ps.getUserPwd().equals(enc.encode(DESEncryptCoder.encrypt(testPs.getBytes(), DESEncryptCoder.KEY)));
		}

        
        
        /*余明星修改  
        *
        */
        if(judgePwd)
		{
			String rand = (String)session.get("rand");
			String validate = request.getParameter("validate");
			if(rand.equals(validate))
			{
				session.put("UserName",name);
				session.put("UserType",type);//当前角色
				session.put("UserId", userid);
				session.put("UserPhone",phone );
				session.put("UserRank", userrank);
				session.put("ProjectName", projectName);
				//session.put("UserPwdMD5",user.getUserPwd());
				session.put("UserLoginType",type);//登录的角色
			
				//System.out.println("QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ");
				System.out.println(projectName);
				
				Log log = new Log();
				log.setUserName(name);
				log.setRole(type);
				//log.setDepartment(unit);
				log.setAction("Login");
				log.setLoginip(request.getRemoteAddr());
				log.setLogintime(new Timestamp(System.currentTimeMillis()));   //记录登录日志
				loginDAO.InsertLog(log);
				
				//如果是项目部人员，直接进入主界面
				String type = (String) session.get("UserLoginType");
				if(type == "项目部人员"){
					return "UserLogin_prject";
				}
				
				return "UserLogin";
			}
			else
			{
				errMsg = "验证码错误!";
				session.put("errMsg", errMsg);		
				return ReturnStr;
			}		
		}
        errMsg = "密码错误!";
		session.put("errMsg", errMsg);	
		return ReturnStr;
	}
}