package PSM.DAO;

import java.util.List;

import org.hibernate.Query;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import hibernate.Admin;
import hibernate.Log;
import hibernate.Person;
import hibernate.Persondb;

public class LoginDAO extends HibernateDaoSupport
{
	
	public Admin QueryAdm(String admin){
		System.out.println("进来了么");
		String hql = "from Admin";
		Query query = getSession().createQuery(hql);
		
		
		
		List<Admin> list = query.list();
		return list.get(0);
	}
	
	public void setAdminPwd(String adm){
		String hql = "update from Admin adm set adm.userPwd='"+adm+"' where adm.userName='admin'";
			Query query=getSession().createQuery(hql);
			query.executeUpdate();
			getSession().flush();
			getSession().close();
	}
	
	public void setPersondbPwd(String pwd, String id){
		String hql = "update from Persondb pdb set pdb.userPwd='"+pwd+"' where pdb.idcard='"+ id +"'";
			Query query=getSession().createQuery(hql);
			query.executeUpdate();
			getSession().flush();
			getSession().close();
	}
	
	public void setPersonPwd(String pwd, String id){
		String hql = "update from Person pdb set pdb.userPwd='"+pwd+"' where pdb.identityNo='"+ id +"'";
			Query query=getSession().createQuery(hql);
			query.executeUpdate();
			getSession().flush();
			getSession().close();
	}
	
	public Admin QueryUser(String UserName)
	{
		String hql = "from Admin adm where adm.userName = '" + UserName +"'";
		//List<Admin> list =  this.getHibernateTemplate().find(hql);
		Query query = getSession().createQuery(hql);
		List<Admin> list = query.list();
		/*if(list.size() == 0)
			return null;
		else*/
		getSession().close();
			return list.get(0);
	}
	
	public boolean IsAdmin(String UserName)
	{
		String hql = "from Admin adm where adm.userName = '" + UserName +"'";
		//List<Admin> list =  this.getHibernateTemplate().find(hql);
		Query query = getSession().createQuery(hql);
		List<Admin> list = query.list();
		/*if(list.size() == 0)
			return null;
		else*/
		getSession().close();
		return (list.size()!=0);
	}
	
	public void InsertLog(Log log){
		this.getHibernateTemplate().save(log);
	}
	
	public Persondb QueryPersondb(String id){
		String hql = "from Persondb pbd where pbd.idcard ='"+ id +"' or pbd.phone='"+ id +"'";
		List<Persondb> list = this.getHibernateTemplate().find(hql);
		if(list.size() == 0)
			return null;
		else
			return list.get(0);
	}
	
	public Person QueryPerson(String id){
		String hql = "from Person pbd where pbd.identityNo ='"+ id +"' or pbd.phoneNo='"+ id +"'";
		List<Person> list = this.getHibernateTemplate().find(hql);
		if(list.size() == 0)
			return null;
		else
			return list.get(0);
	}
	
	public boolean IsPerson(String id){
		String hql = "from Person pbd where pbd.identityNo ='"+ id +"' or pbd.phoneNo='"+ id +"'";
		List<Person> list = this.getHibernateTemplate().find(hql);
		if(list.size() == 0)
			return false;
		else
			return true;
	}
	
	public void insertPersondb(Persondb user)
	{
		this.getHibernateTemplate().save(user);
	}
	
	public String judgeRepeat(String IDCard,String Phone)    //判断是否重复
	{
		String hqlIDCard = "from Persondb us where us.idcard ='"+ IDCard+"'";
		List<Persondb> listSid = this.getHibernateTemplate().find(hqlIDCard);
		if(listSid.size() != 0)
			return "{\"failure\":\"true\",\"msg\":\"该身份证已注册！\"}";
		String hqlPhone = "from Persondb us where us.phone ='" + Phone+"'";
		List<Persondb> listPhone = this.getHibernateTemplate().find(hqlPhone);
		if(listPhone.size()!= 0)
			return "{\"failure\":\"true\",\"msg\":\"该手机号已注册！\"}";
		return "{\"success\":\"true\",\"msg\":\"注册成功！\"}";
		
	}
	
	public boolean QueryHasProject(String name) {
		String hql = "from Flownode fnd where fnd.name ='" + name +"'";
		List<Persondb> list = this.getHibernateTemplate().find(hql);
		if(list.size() == 0)
			return false;
		else
			return true;
	}
	
	public String getPersondbProjectName(String name) {
		String hql = "SELECT projectName from Flownode f WHERE f.name = '" + name +"'";
		List<String> list = this.getHibernateTemplate().find(hql);
		if(list.size() >= 1)
			return list.get(0);
		else
			return "";
	}
	
	public String getProjectName() {
		String hql = "SELECT DISTINCT(name) from Projectmanagement ";
		List<String> list = this.getHibernateTemplate().find(hql);
		if(list.size() >= 1)
			return list.get(0);
		else
			return "暂无进行中项目";
	}
}
