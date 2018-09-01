package PSM.DAO;

import java.util.List;

import org.hibernate.Query;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import hibernate.Log;
public class LogDAO extends HibernateDaoSupport{

	public void Insert(Log log){
		this.getHibernateTemplate().save(log);
	}

	public void Update(Log log){
		this.getHibernateTemplate().update(log);
	}
	
	private List<Log> executeQuery(String hql, int start, int limit){
		Query query = getSession().createQuery(hql);
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Log> list = query.list();
		getSession().flush();
		getSession().close();
		return list;
	}
	
	public int totalLogin(){
		int count = 0;
		Query q = getSession().createQuery("select count(log.id) from Log log where log.action='Login'");
		count = (new Integer(q.uniqueResult().toString()))  
                    .intValue();  
		getSession().flush();
		getSession().close();
		return count;
	}
	
	public List<Log> getLoginLog(int start, int limit){
		String hql = "from Log log where log.action='Login' order by log.id desc";
		return executeQuery(hql, start, limit);
	}
	
	public int totalOpt(){
		int count = 0;
		Query q = getSession().createQuery("select count(log.id) from Log log where log.action='Opt'");
		count = (new Integer(q.uniqueResult().toString()))  
                    .intValue();  
		getSession().flush();
		getSession().close();
		return count;
	}
	
	public List<Log> getOptLog(int start, int limit){
		String hql = "from Log log where log.action='Opt' order by log.id desc";
		return executeQuery(hql, start, limit);
	}
	
	public List<Log> SearchLogin(String[] fs, int start, int limit){		
		String hqlfind = "from Log log where log.action='Login'";
		for(Integer temp = 0; temp<fs.length;temp++){
			if(fs[temp].length() == 0)
				continue;
			hqlfind += " and(log.userName like '%"+fs[temp]+"%' or log.role like '%"+fs[temp]
					+"%' or log.logintime like '%"+fs[temp]
					+"%' or log.loginip like '%"+fs[temp]+"%')";
		}
		return executeQuery(hqlfind, start, limit);
	}
	public List<Log> SearchOpt(String[] fs, int start, int limit){		
		String hqlfind = "from Log log where log.action='Opt'";
		for(Integer temp = 0; temp<fs.length;temp++){
			if(fs[temp].length() == 0)
				continue;
			hqlfind += " and(log.userName like '%"+fs[temp]+"%' or log.role like '%"+fs[temp]
					+"%' or log.logintime like '%"+fs[temp]
					+"%' or log.note like '%"+fs[temp]+"%')";		
		}
		return executeQuery(hqlfind, start, limit);
	}
	
	public int totalSearchLogin(String[] fs){
		int count = 0;
		String hqlfind = "select count(log.id) from Log log where log.action='Login'";
		for(Integer temp = 0; temp<fs.length;temp++){
			if(fs[temp].length() == 0)
				continue;
			hqlfind += " and(log.userName like '%"+fs[temp]+"%' or log.role like '%"+fs[temp]
					+"%' or log.logintime like '%"+fs[temp]
					+"%' or log.loginip like '%"+fs[temp]+"%')";
		}
		Query q = getSession().createQuery(hqlfind);
		count = (new Integer(q.uniqueResult().toString()))  
                    .intValue();  
		getSession().flush();
		getSession().close();
		return count;
	}
	
	public int totalSearchOpt(String[] fs){
		int count = 0;
		String hqlfind = "select count(log.id) from Log log where log.action='Opt'";
		for(Integer temp = 0; temp<fs.length;temp++){
			if(fs[temp].length() == 0)
				continue;
			hqlfind += " and(log.userName like '%"+fs[temp]+"%' or log.role like '%"+fs[temp]
					+"%' or log.logintime like '%"+fs[temp]
					+"%' or log.note like '%"+fs[temp]+"%')";
		}
		Query q = getSession().createQuery(hqlfind);
		count = (new Integer(q.uniqueResult().toString()))  
                    .intValue();  
		getSession().flush();
		getSession().close();
		return count;
	}

	public List<Log> GetMessageLogs(int start, int limit) {
		String hql = "from Log log where log.action='Message' order by log.id desc";
		return executeQuery(hql, start, limit);
	}

}

