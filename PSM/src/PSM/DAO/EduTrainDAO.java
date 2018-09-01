package PSM.DAO;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import hibernate.Fbactivity;
import hibernate.Fbdailytrain;
import hibernate.Multimediafile;
import hibernate.PageModel;
import hibernate.Projectmanagement;
import hibernate.Question;
import hibernate.Testpaper;
import hibernate.Testrecord;
import hibernate.Tezhongpeople;
import hibernate.Tezhongsbpeople;
import hibernate.Trainplan1;
import hibernate.Trainplan2;
import hibernate.Traintable;
import hibernate.Xiandongtai;

public class EduTrainDAO extends HibernateDaoSupport{
	
	public int datacount = 0;
	
	public Traintable getTraintable(int id)
	{
		String hql = "from Traintable where id=" + id;
		Query query = getSession().createQuery(hql);
      	List<Traintable> list = query.list();
      	getSession().close();
      	if (list.size() == 0) return null;
      	return list.get(0);
	}
	
	public Question getQuestion(int id)
	{
		String hql = "from Question where id=" + id;
		Query query = getSession().createQuery(hql);
      	List<Question> list = query.list();
      	getSession().close();
      	if (list.size() == 0) return null;
      	return list.get(0);
	}

	public Trainplan1 getTrainplan1(int id)
	{
		String hql = "from Trainplan1 where id=" + id;
		Query query = getSession().createQuery(hql);
      	List<Trainplan1> list = query.list();
      	getSession().close();
      	if (list.size() == 0) return null;
      	return list.get(0);
	}

	public Trainplan2 getTrainplan2(int id)
	{
		String hql = "from Trainplan2 where id=" + id;
		Query query = getSession().createQuery(hql);
      	List<Trainplan2> list = query.list();
      	getSession().close();
      	if (list.size() == 0) return null;
      	return list.get(0);
	}
	
	public Fbdailytrain getFbdailytrain(int id)
	{
		String hql = "from Fbdailytrain where id=" + id;
		Query query = getSession().createQuery(hql);
      	List<Fbdailytrain> list = query.list();
      	getSession().close();
      	if (list.size() == 0) return null;
      	return list.get(0);
	}
	
	public Fbactivity getFbactivity(int id)
	{
		String hql = "from Fbactivity where id=" + id;
		Query query = getSession().createQuery(hql);
      	List<Fbactivity> list = query.list();
      	getSession().close();
      	if (list.size() == 0) return null;
      	return list.get(0);
	}
	
	public Multimediafile getMultimediafile(int id)
	{
		String hql = "from Multimediafile where id=" + id;
		Query query = getSession().createQuery(hql);
      	List<Multimediafile> list = query.list();
      	if (list.size() == 0) return null;
      	return list.get(0);
	}

	public Testrecord getTestrecord(int ID) {
		String hql = "from Testrecord where id =" + ID;
		Query query = getSession().createQuery(hql);
		List<Testrecord> list = query.list();
		getSession().close();
		if (list.size() == 0) return null;
		else return list.get(0);
	}
		
	public Testpaper getTestpaper(int ID) {
		String hql = "from Testpaper where id =" + ID;
		Query query = getSession().createQuery(hql);
		Testpaper testpaper = (Testpaper) query.list().get(0);
		getSession().close();
		return testpaper;
	}
	
	public Projectmanagement getProjectmanagement(int ID) {
		String hql = "from Projectmanagement where id =" + ID;
		Query query = getSession().createQuery(hql);
		Projectmanagement projectmanagement = (Projectmanagement) query.list().get(0);
		getSession().close();
		return projectmanagement;
	}
	
	/*插入*/
	public void insertTrainplan1(Trainplan1 p)
	{
		this.getHibernateTemplate().save(p);
	}	

	public void insertTrainplan2(Trainplan2 p)
	{
		this.getHibernateTemplate().save(p);
	}
	public void insertFbdailytrain(Fbdailytrain p)
	{
		this.getHibernateTemplate().save(p);
	}
	
	public void insertTraintable(Traintable p)
	{
		this.getHibernateTemplate().save(p);
	}
	
	public void insertFbactivity(Fbactivity p)
	{
		this.getHibernateTemplate().save(p);
	}
	
	public void insertQuestion(Question p)
	{
		this.getHibernateTemplate().save(p);
	}
	
	public void insertTestrecord(Testrecord p)
	{
		this.getHibernateTemplate().save(p);
	}
	
	public void insertTestpaper(Testpaper p)
	{
		this.getHibernateTemplate().save(p);
	}
	
	public void insertMultimediafile(Multimediafile p)
	{
		this.getHibernateTemplate().save(p);
	}
	
	/*更新*/
	public void updateTrainplan1(Trainplan1 p)
	{
		this.getHibernateTemplate().update(p);		
	}
	
	public void updateTrainplan2(Trainplan2 p)
	{
		this.getHibernateTemplate().update(p);		
	}
	
	public void updateTraintable(Traintable p)
	{
		this.getHibernateTemplate().update(p);		
	}
	
	public void updateFbdailytrain(Fbdailytrain p)
	{
		this.getHibernateTemplate().update(p);		
	}
	
	public void updateFbactivity(Fbactivity p)
	{
		this.getHibernateTemplate().update(p);		
	}
	
	public void updateQuestion(Question p)
	{
		this.getHibernateTemplate().update(p);		
	}
	
	public void updateTestrecord(Testrecord p)
	{
		this.getHibernateTemplate().update(p);		
	}
	
	public void updateTestpaper(Testpaper p)
	{
		this.getHibernateTemplate().update(p);		
	}
	
	public void updateMultimediafile(Multimediafile p)
	{
		this.getHibernateTemplate().update(p);		
	}
		
	/*删除*/
	public void deleteTrainplan1(Trainplan1 p)
	{
		this.getHibernateTemplate().delete(p);
	}
	
	public void deleteTrainplan2(Trainplan2 p)
	{
		this.getHibernateTemplate().delete(p);
	}
	
	public void deleteTraintable(Traintable p)
	{
		this.getHibernateTemplate().delete(p);
	}
	
	public void deleteFbdailytrain(Fbdailytrain p)
	{
		this.getHibernateTemplate().delete(p);
	}
	
	public void deleteFbactivity(Fbactivity p)
	{
		this.getHibernateTemplate().delete(p);
	}
	
	public void deleteMultimediafile(Multimediafile p)
	{
		this.getHibernateTemplate().delete(p);
	}
	
	public void deleteQuestion(int ID)
	{
		Question q = getQuestion(ID);
		q.setEnable(false);
		this.getHibernateTemplate().update(q);
	}
	
	public void deleteTestrecord(Testrecord p)
	{
		this.getHibernateTemplate().delete(p);
	}
	
	public void deleteTestpaper(Testpaper p)
	{
		this.getHibernateTemplate().delete(p);
	}
	
	//获取项目信息List
	public List<Trainplan1> getTrainplan1List(String findstr, int start, int limit, String projectName)
	{
		String hql = "from Trainplan1 where ";
		if (projectName.equals(""))
			hql += "id is not null";
		else
			hql += "projectName='" + projectName + "'";
		if (findstr != null || findstr.length() > 0) {
    		String[] strKey = findstr.split(",");
        	for(int i = 0; i < strKey.length; i++) {
        		if(strKey[i].length()>0) {
        			hql += " and ( Content like '%" + strKey[i]
        				+ "%' or Employee like '%" + strKey[i] 
        				+ "%' or Method like '%" + strKey[i] 
        				+ "%' or ClassTime like '%" + strKey[i] 
        				+ "%' or Budget like '%" + strKey[i]
        				+ "%' or Result like '%" + strKey[i] + "%' )";
        		}
        	}
    	}
		Query query = getSession().createQuery(hql);
    	datacount = query.list().size();
    	query.setFirstResult(start);
		query.setMaxResults(limit);
    	List<Trainplan1> list = query.list();   	
    	getSession().close();
    	return list;
	}

	public List<Trainplan2> getTrainplan2List(String findstr, int start, int limit, String projectName)
	{
		String hql = "from Trainplan2 where projectName='" + projectName + "'";
		if (findstr != null || findstr.length() > 0) {
    		String[] strKey = findstr.split(",");
        	for(int i = 0; i < strKey.length; i++) {
        		if(strKey[i].length()>0) {
        			hql += " and ( Content like '%" + strKey[i]
        				+ "%' or Employee like '%" + strKey[i] 
        				+ "%' or Method like '%" + strKey[i] 
        				+ "%' or Funding like '%" + strKey[i] + "%' )";
        		}
        	}
    	}
		Query query = getSession().createQuery(hql);
    	datacount = query.list().size();
    	query.setFirstResult(start);
		query.setMaxResults(limit);
    	List<Trainplan2> list = query.list();   	
    	getSession().close();
    	return list;
	}
	
	public List<Traintable> getTraintableList(String findstr, int start, int limit, String tableID, String projectName)
	{
		String hql = "from Traintable where tableID=" + tableID + " and projectName='" + projectName + "'";
		if (findstr != null || findstr.length() > 0) {
    		String[] strKey = findstr.split(",");
        	for(int i = 0; i < strKey.length; i++) {
        		if(strKey[i].length()>0) {
        			hql += " and ( Content like '%" + strKey[i]
        				+ "%' or Employee like '%" + strKey[i] 
        				+ "%' or Method like '%" + strKey[i] 
        				+ "%' or Funding like '%" + strKey[i] + "%' )";
        		}
        	}
    	}
		Query query = getSession().createQuery(hql);
    	datacount = query.list().size();
    	query.setFirstResult(start);
		query.setMaxResults(limit);
    	List<Traintable> list = query.list();   	
    	getSession().close();
    	return list;
	}
	
	public List<Fbdailytrain> getFbdailytrainList(String findstr, int start, int limit, String projectName)
	{
		String hql = "from Fbdailytrain where projectName='" + projectName + "'";
		if (findstr != null || findstr.length() > 0) {
    		String[] strKey = findstr.split(",");
        	for(int i = 0; i < strKey.length; i++) {
        		if(strKey[i].length()>0) {
        			hql += " and ( fenbao like '%" + strKey[i] + "%' )";
        		}
        	}
    	}
		Query query = getSession().createQuery(hql);
    	datacount = query.list().size();
    	query.setFirstResult(start);
		query.setMaxResults(limit);
    	List<Fbdailytrain> list = query.list();   	
    	getSession().close();
    	return list;
	}
	
	public List<Fbactivity> getFbactivity1List(String findstr, int start, int limit, String projectName)
	{
		String hql = "from Fbactivity where theme='分包方安全生产班前会' and projectName='" + projectName + "'";
		if (findstr != null || findstr.length() > 0) {
    		String[] strKey = findstr.split(",");
        	for(int i = 0; i < strKey.length; i++) {
        		if(strKey[i].length()>0) {
        			hql += " and ( organization like '%" + strKey[i] + "%' )";
        		}
        	}
    	}
		Query query = getSession().createQuery(hql);
    	datacount = query.list().size();
    	query.setFirstResult(start);
		query.setMaxResults(limit);
    	List<Fbactivity> list = query.list();   	
    	getSession().close();
    	return list;
	}
	
	public List<Fbactivity> getFbactivity2List(String findstr, int start, int limit, String projectName)
	{
		String hql = "from Fbactivity where theme!='分包方安全生产班前会' and projectName='" + projectName + "'";
		if (findstr != null || findstr.length() > 0) {
    		String[] strKey = findstr.split(",");
        	for(int i = 0; i < strKey.length; i++) {
        		if(strKey[i].length()>0) {
        			hql += " and ( organization like '%" + strKey[i] + "%' or theme like '%" + strKey[i] + "' )";
        		}
        	}
    	}
		Query query = getSession().createQuery(hql);
    	datacount = query.list().size();
    	query.setFirstResult(start);
		query.setMaxResults(limit);
    	List<Fbactivity> list = query.list();   	
    	getSession().close();
    	return list;
	}
	
	public List<Multimediafile> getMultimediafileList(String findstr, int start, int limit, String type, String projectName)
	{
		String hql = "from Multimediafile where type='"+type+"' AND projectName='"+projectName+"'";
		if(findstr != null || findstr.length()>0) {
    		String[] strKey = findstr.split(",");
        	for(int i=0; i<strKey.length; i++)
        		if(strKey[i].length()>0)
        			hql += " and ( accessory like '%*%*%" + strKey[i] + "%')";
    	}
		Query query = getSession().createQuery(hql);
	    datacount = query.list().size();
    	query.setFirstResult(start);
		query.setMaxResults(limit);
    	List<Multimediafile> list = query.list();
    	getSession().close();
    	return list;
	}
	
	public List<Question> getQuestionList(String belongTo, String type, String findstr, int start, int limit)
	{
		String hql = "from Question where enable = 1 and belongTo='" + belongTo + "' and type='" + type + "'";
		if (findstr != null || findstr.length() > 0) {
    		String[] strKey = findstr.split(",");
        	for(int i = 0; i < strKey.length; i++) {
        		if(strKey[i].length()>0) {
        			hql += " and ( Question like '%" + strKey[i]
        				+ "%' or OptionA like '%" + strKey[i] 
        				+ "%' or OptionB like '%" + strKey[i] 
        				+ "%' or OptionC like '%" + strKey[i]
        				+ "%' or OptionD like '%" + strKey[i] + "%' )";
        		}
        	}
    	}
		hql += " order by id desc";
		Query query = getSession().createQuery(hql);
    	datacount = query.list().size();
    	query.setFirstResult(start);
		query.setMaxResults(limit);
    	List<Question> list = query.list();   	
    	getSession().close();
    	return list;
	}
	
	public PageModel<Testrecord> getTestrecordList(String findstr, int start, int limit, String projectName)
	{
		PageModel<Testrecord> pageModel = new PageModel<>();
		String hql = "from Testrecord where belongTo = '" + projectName + "'";
		if (findstr != null || findstr.length() > 0) {
    		String[] strKey = findstr.split(",");
        	for(int i = 0; i < strKey.length; i++) {
        		if(strKey[i].length()>0) {
        			hql += " and ( Name like '%" + strKey[i] + "%' )";
        		}
        	}
    	}
		Query query = getSession().createQuery(hql);
		pageModel.datacount = query.list().size();
    	query.setFirstResult(start);
		query.setMaxResults(limit);
    	pageModel.models = query.list();
    	getSession().close();
    	return pageModel;
	}
	
	public PageModel<Testrecord> getDoubleTestrecordList(String projectName)
	{
		PageModel<Testrecord> pageModel = new PageModel<>();
		String hql = "from Testrecord where belongTo='设计院题库' or belongTo='" + projectName + "'";
		Query query = getSession().createQuery(hql);
		pageModel.datacount = query.list().size();
		pageModel.models = query.list();
    	getSession().close();
    	return pageModel;
	}

	
	public List<Testpaper> getTestpaperList(int testID, int projectID, String findstr, int start, int limit)
	{
		String hql = "from Testpaper where testId=" + testID;
		if (projectID != -1) hql += " and projectID=" + projectID;
		if (findstr != null || findstr.length() > 0) {
    		String[] strKey = findstr.split(",");
        	for(int i = 0; i < strKey.length; i++) {
        		if(strKey[i].length()>0) {
        			hql += " and ( Name like '%" + strKey[i] + "%' or IDNO like '%" + strKey[i] + "%')";
        		}
        	}
    	}
		Query query = getSession().createQuery(hql);
    	datacount = query.list().size();
    	query.setFirstResult(start);
		query.setMaxResults(limit);
    	List<Testpaper> list = query.list();   	
    	getSession().close();
    	return list;
	}
	
	public List<Tezhongpeople> getTezhongpeopleList() 
	{
		String hql = "from Tezhongpeople";
		Query query = getSession().createQuery(hql);
    	datacount = query.list().size();
    	List<Tezhongpeople> list = query.list();   	
    	getSession().close();
    	return list;
	}
	
	public List<Tezhongsbpeople> getTezhongsbpeopleList() 
	{
		String hql = "from Tezhongsbpeople";
		Query query = getSession().createQuery(hql);
    	datacount = query.list().size();
    	List<Tezhongsbpeople> list = query.list();   	
    	getSession().close();
    	return list;
	}
	
	public List<Xiandongtai> getXiandongtaiList() 
	{
		String hql = "from Xiandongtai";
		Query query = getSession().createQuery(hql);
    	datacount = query.list().size();
    	List<Xiandongtai> list = query.list();   	
    	getSession().close();
    	return list;
	}
	
	public List<Projectmanagement> getProjectList(int start, int limit)
	{
		String hql = "from Projectmanagement where id is not null";
		Query query = getSession().createQuery(hql);
	    datacount = query.list().size();
    	query.setFirstResult(start);
		query.setMaxResults(limit);
    	List<Projectmanagement> list = query.list();   	
    	getSession().close();
    	return list;
	}
	
	public List<Question> getQuestion(String type, int limit) {
		String hql = "from Question where type='" + type + "' order by rand()";
		Query query = getSession().createQuery(hql);
		query.setMaxResults(limit);
    	List<Question> list = query.list();
    	getSession().close();
    	return list;
	}
	
	public List<Question> getQuestion(String type, String belongTo, int limit) {
		System.out.println(belongTo);
		String hql = "from Question where belongTo='" + belongTo + "' and type='" + type + "' and enable is true order by rand()";
		Query query = getSession().createQuery(hql);
		query.setMaxResults(limit);
    	List<Question> list = query.list();
    	getSession().close();
    	return list;
	}
	
	public List<Testpaper> getTestpaper(String type, int limit) {
		String hql = "from Testpaper where id is not null and type='" + type + "' order by rand()";
		Query query = getSession().createQuery(hql);
		query.setMaxResults(limit);
    	List<Testpaper> list = query.list();
    	getSession().close();
    	return list;
	}
	
	public Testpaper getTestpaperByIdnoTestID(String idno, int testID) {
		String hql = "from Testpaper where idno='" + idno + "' and testID=" + testID;
		Query query = getSession().createQuery(hql);
		List<Testpaper> list =  query.list();
    	getSession().close();
    	if (list.size() == 0) return null;
    	return list.get(0);
	}
	
	public float getAvgScore(int testID) {
		String sql = "SELECT AVG(Score) FROM(SELECT NAME, (Score1+Score2+Score3+Score4) AS Score FROM testpaper WHERE TestID="+testID+") t";
		Session session = getSession();
		BigDecimal temp = (BigDecimal)session.createSQLQuery(sql).uniqueResult();
		if (temp == null) return -1;
		float res = temp.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
		session.close();
		return res;
	}
	
	public int getActualNum(int testID) {
		String sql = "SELECT COUNT(*) FROM testpaper WHERE testID=" + testID;
		Session session = getSession();
		BigInteger temp = (BigInteger)session.createSQLQuery(sql).uniqueResult();
		int res = temp.intValue();
		return res;
	}
	
	public int getProjectID(String projectName) {
		if (projectName.equals("全部项目")) return -1;
		String sql = "from Projectmanagement where name='" + projectName + "'";
		Session session = getSession();
		Projectmanagement temp = (Projectmanagement) session.createQuery(sql).uniqueResult();
		if (temp == null) return -1;
		return temp.getId();
	}
	
	public List<Projectmanagement> getProjectNameList() {
		String hql = "FROM Projectmanagement";
		Query query = getSession().createQuery(hql);
    	List<Projectmanagement> list = query.list();
    	getSession().close();
    	return list;
	}
}