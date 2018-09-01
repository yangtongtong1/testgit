package PSM.DAO;

import java.util.List;

import org.hibernate.Query;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import hibernate.Commonfile;
import hibernate.Commonlaw;
import hibernate.Commonsystem;
import hibernate.Exemplaryfile;
import hibernate.Fbsystem;
import hibernate.Prodepartdoc;
import hibernate.Standmodel;

public class FileSystemDAO extends HibernateDaoSupport{
	
public int datacount;
	// 根据ID获得相应的Model
	public List<Exemplaryfile> checkProjectID(int id)
	{
		String hql = "from Exemplaryfile where id=" + id;
		Query query = getSession().createQuery(hql);
      	List<Exemplaryfile> list = query.list();
      	getSession().close();
      	return list;
	}
	
	public Commonlaw getCommonlaw(int id)
	{
		String hql = "from Commonlaw where id=" + id;
		Query query = getSession().createQuery(hql);
      	List<Commonlaw> list = query.list();
      	getSession().close();
      	if (list.size() == 0) return null;
      	return list.get(0);
	}
		
	public Commonfile getCommonfile(int id)
	{
		String hql = "from Commonfile where id=" + id;
		Query query = getSession().createQuery(hql);
      	List<Commonfile> list = query.list();
      	getSession().close();
      	if (list.size() == 0) return null;
      	return list.get(0);
	}
	
	public Commonsystem getCommonsystem(int id)
	{
		String hql = "from Commonsystem where id=" + id;
		Query query = getSession().createQuery(hql);
      	List<Commonsystem> list = query.list();
      	getSession().close();
      	if (list.size() == 0) return null;
      	return list.get(0);
	}
	
	public Prodepartdoc getProdepartdoc(int id)
	{
		String hql = "from Prodepartdoc where id=" + id;
		Query query = getSession().createQuery(hql);
      	List<Prodepartdoc> list = query.list();
      	if (list.size() == 0) return null;
      	return list.get(0);
	}
	
	public Fbsystem getFbsystem(int id)
	{
		String hql = "from Fbsystem where id=" + id;
		Query query = getSession().createQuery(hql);
      	List<Fbsystem> list = query.list();
      	if (list.size() == 0) return null;
      	return list.get(0);
	}
	
	/*插入*/
	public void insertProject(Exemplaryfile p)
	{
		this.getHibernateTemplate().save(p);
	}	
	
	public void insertCommonlaw(Commonlaw p)
	{
		this.getHibernateTemplate().save(p);
	}
	
	public void insertCommonfile(Commonfile p)
	{
		this.getHibernateTemplate().save(p);
	}
	
	public void insertCommonsystem(Commonsystem p)
	{
		this.getHibernateTemplate().save(p);
	}
	
	public void insertProdepartdoc(Prodepartdoc p)
	{
		this.getHibernateTemplate().save(p);
	}
	
	public void insertFbsystem(Fbsystem p)
	{
		this.getHibernateTemplate().save(p);
	}
	
	/*更新*/
	public void updateProject(Exemplaryfile p)
	{
		this.getHibernateTemplate().update(p);		
	}
	
	public void updateCommonlaw(Commonlaw p)
	{
		this.getHibernateTemplate().update(p);		
	}
		
	public void updateCommonfile(Commonfile p)
	{
		this.getHibernateTemplate().update(p);		
	}
	
	public void updateCommonsystem(Commonsystem p)
	{
		this.getHibernateTemplate().update(p);		
	}
	
	public void updateProdepartdoc(Prodepartdoc p)
	{
		this.getHibernateTemplate().update(p);		
	}
	
	public void updateFbsystem(Fbsystem p)
	{
		this.getHibernateTemplate().update(p);		
	}
	
	/*删除*/
	public void deleteProject(Exemplaryfile p)
	{
		this.getHibernateTemplate().delete(p);
	}
	
	public void deleteCommonlaw(Commonlaw p)
	{
		this.getHibernateTemplate().delete(p);
	}
	
	public void deleteCommonfile(Commonfile p)
	{
		this.getHibernateTemplate().delete(p);
	}
		
	public void deleteCommonsystem(Commonsystem p)
	{
		this.getHibernateTemplate().delete(p);
	}
	
	public void deleteProdepartdoc(Prodepartdoc p)
	{
		this.getHibernateTemplate().delete(p);
	}
	
	public void deleteFbsystem(Fbsystem p)
	{
		this.getHibernateTemplate().delete(p);
	}
	
	//获取项目信息List
	public List<Exemplaryfile> getProjectList(String findstr, int start, int limit)
	{
		String hql = "from Exemplaryfile where id is not null";
		if(findstr != null || findstr.length()>0)
		{
    		String[] strKey = findstr.split(",");
        	for(int i=0; i<strKey.length; i++)
        	{
        		if(strKey[i].length()>0)
        		{
        			hql += " and ( no like '%" + strKey[i] +"%' or name like '%" + strKey[i] +"%' or fromunit like '%" + strKey[i] +"%' or howfast like '%" + strKey[i]
        					+"%' or filerequire like '%" + strKey[i]  +"%' or writeopinion like '%" + strKey[i] + "%' )";
        		}
        	}
    	}
		Query query = getSession().createQuery(hql);
    	datacount = query.list().size();
    	query.setFirstResult(start);
		query.setMaxResults(limit);
    	List<Exemplaryfile> list = query.list();   	
    	getSession().close();
    	return list;
	}
	
	public List<Commonlaw> getCommonlawList(String findstr, int start, int limit, String type)
	{
		String hql = "from Commonlaw where type='"+type+"'";
		if(findstr != null || findstr.length()>0) {
    		String[] strKey = findstr.split(",");
        	for(int i=0; i<strKey.length; i++) {
        		if(strKey[i].length()>0) {
        			hql += " and ( no like '%" + strKey[i] +"%' or name like '%" + strKey[i] +"%' or fromunit like '%" + strKey[i] +"%')";
        		}
        	}
    	}
		Query query = getSession().createQuery(hql);
    	datacount = query.list().size();
    	query.setFirstResult(start);
		query.setMaxResults(limit);
    	List<Commonlaw> list = query.list();
    	getSession().close();
    	return list;
	}
		
	public List<Commonfile> getCommonfileList(String findstr, int start, int limit, String type)
	{
		String hql = "from Commonfile where type='"+type+"'";
		if(findstr != null || findstr.length()>0) {
    		String[] strKey = findstr.split(",");
        	for(int i=0; i<strKey.length; i++) {
        		if(strKey[i].length()>0) {
        			hql += " and ( no like '%" + strKey[i] +"%' or name like '%" + strKey[i] +"%' or fromunit like '%" + strKey[i] +"%')";
        		}
        	}
    	}
		Query query = getSession().createQuery(hql);
    	datacount = query.list().size();
    	System.out.println("getCommonfileList length" + datacount);
    	query.setFirstResult(start);
		query.setMaxResults(limit);
    	List<Commonfile> list = query.list();
    	getSession().close();
    	return list;
	}
	
	public List<Commonsystem> getCommonsystemList(String findstr, int start, int limit, String type)
	{
		String hql = "from Commonsystem where type='"+type+"'";
		if(findstr != null || findstr.length()>0)
		{
    		String[] strKey = findstr.split(",");
        	for(int i=0; i<strKey.length; i++)
        		if(strKey[i].length()>0)
        			hql += " and ( no like '%" + strKey[i] + "%' or name like '%" + strKey[i] + "%' or fromunit like '%" + strKey[i] 
        					+ "%' or urgency like '%" + strKey[i] + "%' or requirement like '%" + strKey[i] + "%' or opinion like '%" + strKey[i] + "%')";
    	}
		Query query = getSession().createQuery(hql);
    	datacount = query.list().size();
    	query.setFirstResult(start);
		query.setMaxResults(limit);
    	List<Commonsystem> list = query.list();
    	getSession().close();
    	return list;
	}
	
	public List<Prodepartdoc> getProdepartdocList(String findstr, int start, int limit, String projectName)
	{
		String hql = "from Prodepartdoc where projectName='"+projectName+"'";
		if(findstr != null || findstr.length()>0) {
    		String[] strKey = findstr.split(",");
        	for(int i=0; i<strKey.length; i++)
        		if(strKey[i].length()>0)
        			hql += " and ( accessory like '%" + strKey[i] + "%' or type like '%" + strKey[i] + "%')";
    	}
		Query query = getSession().createQuery(hql);
    	datacount = query.list().size();
    	query.setFirstResult(start);
		query.setMaxResults(limit);
    	List<Prodepartdoc> list = query.list();
    	getSession().close();
    	return list;
	}
	
	public List<Fbsystem> getFbsystemList(String findstr, int start, int limit, String projectName)
	{
		String hql = "from Fbsystem where projectName='"+projectName+"'";
		if(findstr != null || findstr.length()>0) {
    		String[] strKey = findstr.split(",");
        	for(int i=0; i<strKey.length; i++)
        		if(strKey[i].length()>0)
        			hql += " and ( accessory like '%" + strKey[i] + "%' or fbUnit like '%" + strKey[i] + "%')";
    	}
		Query query = getSession().createQuery(hql);
    	datacount = query.list().size();
    	query.setFirstResult(start);
		query.setMaxResults(limit);
    	List<Fbsystem> list = query.list();
    	getSession().close();
    	return list;
	}
	
	
	// **********Standmodel**********
		public List<Standmodel> checkRisksafepgID(int id) {
			String hql = "from Standmodel where id=" + id;
			Query query = getSession().createQuery(hql);
			List<Standmodel> list = query.list();
			getSession().close();
			return list;
		}

		// 增
		public void insertStandmodel(Standmodel s) {
			this.getHibernateTemplate().save(s);
		}

		// 改
		public void updateStandmodel(Standmodel s) {
			this.getHibernateTemplate().update(s);
		}

		// 删
		public void deleteStandmodel(Standmodel s) {
			this.getHibernateTemplate().delete(s);
		}

	
		// 根据查询条件获取List
		public List<Standmodel> getStandmodelList(String findstr, int start, int limit) {
			String hql = "from Standmodel where id is not null";
			if (findstr != null || findstr.length() > 0) {
				String[] strKey = findstr.split(",");
				for (int i = 0; i < strKey.length; i++) {
					if (strKey[i].length() > 0) {
						hql += " and (one like '%" + strKey[i] + "%' or two like '%" + strKey[i]
								+ "%' or three like '%" + strKey[i] + "%' or modelname like '%" + strKey[i] + "%')";
					}
				}
			}
			// System.out.println(hql + "**********************");
			Query query = getSession().createQuery(hql);
			datacount = query.list().size();
			query.setFirstResult(start);
			query.setMaxResults(limit);
			List<Standmodel> list = query.list();
			getSession().close();
			return list;
		}
	

}
