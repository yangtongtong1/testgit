package PSM.DAO;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import hibernate.Approval;
import hibernate.Flownode;
import hibernate.Missionstate;
import hibernate.PrescribedActionDynamic;
import hibernate.PrescribedActionView;
import hibernate.Projectmanagement;
import hibernate.Taizhang;

public class MissionDAO extends HibernateDaoSupport{
	public List getAllpro(String projectName) {
		String hql = "select mis.progroup, count(mis.progroup) from Missionstate mis group by mis.progroup";
		if (!projectName.equals("全部项目")) {
			hql = "select mis.progroup, count(mis.progroup) from Missionstate mis WHERE progroup='" + projectName + "'group by mis.progroup";
			
		}
		List list = this.getHibernateTemplate().find(hql);
		return list;
	}
	public void insertAppro(Approval app){
		this.getHibernateTemplate().save(app);
	}
	
	public Missionstate getTask(int id) {
		String hql = "FROM Missionstate WHERE id=?";
		List<Missionstate> ms = getHibernateTemplate().find(hql, id);
		if (ms.size() > 0) return ms.get(0);
		return null;
	}
	
	public void deleteTask(Missionstate m) {
		getHibernateTemplate().delete(m);
	}
	
	public List getAllprojectname(){
		String hql = "FROM Flownode f WHERE f.id IN (SELECT MAX(id) FROM Flownode GROUP BY projectName)";
		List list = this.getHibernateTemplate().find(hql);
		return list;
	}
	
	public List getAllProjectPerson(String projectName){
		String hql = "from Flownode f where tableId=56 and projectName='" + projectName + "'";
		List list = this.getHibernateTemplate().find(hql);
		return list;
	}

	public List<Flownode> getallProject(){
		String hql = "FROM Flownode f WHERE f.id IN (SELECT MAX(id) FROM Flownode WHERE tableId=56 GROUP BY projectName)";
		List<Flownode> list = this.getHibernateTemplate().find(hql);
		return list;
	}
	
	public List getprojectByname(String projectname){
		String hql = "from Flownode where projectName='"+projectname+"' and tableId=56 order by stepNum,orderNum";
		List list = this.getHibernateTemplate().find(hql);
		return list;
	}
	
	public List getOtherType(String type){
		String hql = "from Person where Type='"+type+"'";
		List list = this.getHibernateTemplate().find(hql);
		return list;
	}
	
	public Taizhang getTaizhang(int id) {
		String hql = "from Taizhang where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Taizhang> list = query.list();
		getSession().close();
		return list.get(0);
	}
	
	public void updateTaizhang(Taizhang tai){
		this.getHibernateTemplate().update(tai);
	}
	public List<Missionstate> getAllMis(){
		String hql = "FROM Missionstate f WHERE f.id IN (SELECT MAX(id) FROM Missionstate GROUP BY missionname)"; 	
		List<Missionstate> list = this.getHibernateTemplate().find(hql);
		return list;
	}
	
	
	public List<Missionstate> getbyMission(String misname){
		String hql = "from Missionstate mis where mis.missionname='"+misname+"'"; 	
		List<Missionstate> list = this.getHibernateTemplate().find(hql);
		return list;
	}
	public List<Missionstate> getMission(String pro){
		String hql = "from Missionstate mis where progroup = '"+pro+"' and isdown = 0 or progroup ='"+pro+"' and isdown = 2";
		List<Missionstate> list = this.getHibernateTemplate().find(hql);
		return list;
	}
	public int totalmission(String cat,String username){
		int count = 0;
		String hql = ("select count(*) from Missionstate mis where isdown like'%"+cat+"%' and properson like'%"+username+"%'");
		Query q = getSession().createQuery(hql);
		count = (new Integer(q.uniqueResult().toString()))  
                    .intValue();  
		getSession().close();
		return count;
	}
	public int totalapproval(String cat,String username){
		int count = 0;
		String hql = new String();
		if(cat.equals("所有"))
			hql = ("select count(*) from Approval App");
		else
			hql = ("select count(*) from Approval App where approperson like'%"+username+"%'");
		Query q = getSession().createQuery(hql);
		count = (new Integer(q.uniqueResult().toString()))  
                    .intValue();  
		getSession().close();
		return count;
	}
	
	public List<Missionstate> getallpro(String cat,String username){
		String hql = new String();
		if(cat.equals("管理员")){
			hql = ("from Missionstate mis where releaseperson like'%"+username+"%' order by mis.isdown DESC");
		}
		else
		    hql = ("from Missionstate mis where isdown like'%"+cat+"%' and properson like'%"+username+"%' order by mis.isdown DESC, ID DESC");
		List<Missionstate> list = this.getHibernateTemplate().find(hql);
		return list;
	}
	
	public List<Approval> getallApproval(String cat,String username){
		String hql = new String();
		if(cat.equals("所有")){
			hql = ("from Approval app order by app.approtime DESC");
		}
		else
			hql = ("from Approval app where approperson like'%"+username+"%' order by app.approtime DESC");
		List<Approval> list = this.getHibernateTemplate().find(hql);
		return list;
	}
	
	public void finishMission(String proName,String user,Date truefistime,String Acc,String finsit){
		java.sql.Date sqlDate=new java.sql.Date(truefistime.getTime());
		String hql = new String();
		if(Acc.contains("null"))
			hql = ("update Missionstate mis set mis.isdown=1,mis.downtime='"+sqlDate+"',mis.finsituation='"+finsit+"' where mis.properson='"+user+"' and mis.missionname='"+proName+"'");
		else 
			hql = ("update Missionstate mis set mis.finishfile='"+Acc+"',mis.isdown=1,mis.downtime='"+sqlDate+"',mis.finsituation='"+finsit+"' where mis.properson='"+user+"' and mis.missionname='"+proName+"'");
		System.out.println(hql);
		Query q = getSession().createQuery(hql);
		q.executeUpdate();
		getSession().close();
	}
	public void insertMis(Missionstate mis){
		this.getHibernateTemplate().save(mis);
	}
	
	public List<Approval> getApproval(int id){
		String hql = "from Approval app where app.id = "+id;
		List<Approval> list = this.getHibernateTemplate().find(hql);
		return list;
	}

	public void updateApproval(Approval app){
		this.getHibernateTemplate().update(app);
	}
	
	public List<Missionstate> getGroMission(String group) {
		String hql;
		if (group.equals("全部项目")) {
			hql = "from Missionstate mis order by mis.isdown DESC, ID DESC";
		} else {
			hql = "from Missionstate mis where mis.progroup='"+group+"' order by mis.isdown DESC, ID DESC";
		}		 
		List<Missionstate> list = this.getHibernateTemplate().find(hql);
		return list;
	}
	
	public int gettotalTaz(String user){
		int count = 0;
		
		Query q = getSession().createQuery("select count(*) from Taizhang taz where taz.solvePerson like '%"+user+"%'");
		count = (new Integer(q.uniqueResult().toString()))  
                    .intValue();  
		getSession().close();
		return count;
	}
	
	public List<Taizhang> getMyTaizhang(String user,int start,int limit){
		String hql = "from Taizhang taz where taz.solvePerson like '%"+user+"%'";
		Query query = getSession().createQuery(hql);
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Taizhang> list = query.list();
		getSession().flush();
		getSession().close();
		return list;
	}
	
	public List<Missionstate> getMissionstateByProjectName(String projectName) {
		String hql = "from Missionstate where progroup='" + projectName + "'";
		Query query = getSession().createQuery(hql);
		List<Missionstate> list = query.list();
		getSession().close();
		return list;
	}
	
	public void updateMissionstateByProjectName(String projectName,String prevName) {
		List<Missionstate> updatelist = getMissionstateByProjectName(prevName);
		for(int i=0;i<updatelist.size();i++) {
			updatelist.get(i).setProgroup(projectName);
			this.getHibernateTemplate().update(updatelist.get(i));
		}
	}
	
	public void deleteMissionstateByProjectName(String projectName) {
		List<Missionstate> dellist = getMissionstateByProjectName(projectName);
		for(int i=0;i<dellist.size();i++) {
			this.getHibernateTemplate().delete(dellist.get(i));
		}
	}
	
	public List<Projectmanagement> getSomeprojectname(String type){
		String hql = "from Projectmanagement pro";
		if (!type.equals("全部阶段")) {
			hql += " where progress='" + type + "'";
		}
		List<Projectmanagement> list = this.getHibernateTemplate().find(hql);
		return list;
	}
	
	public void updateMissionState(String project, int actionId) {
		String selectHQL = "FROM PrescribedActionDynamic WHERE project=? AND actionId=?";
		List<PrescribedActionDynamic> pads = getHibernateTemplate().find(selectHQL, new Object[]{project, actionId});
		PrescribedActionDynamic pad = pads.get(0);
		pad.setIsDone(true);
		pad.setCompletedDate(new Date());
		getHibernateTemplate().update(pad);
	}
	
	public List<PrescribedActionView> getPrescribedAction(String project) {
		String selectHQL = "FROM PrescribedActionView WHERE id.project=?";
		List<PrescribedActionView> list = getHibernateTemplate().find(selectHQL, new String[]{project});
		return list;
	}
	
	public void init() {
		List<Projectmanagement> pros = getHibernateTemplate().find("FROM Projectmanagement");
		for (Projectmanagement projectmanagement : pros) {
			String project = projectmanagement.getName();
			for (int i = 1; i <= 41; i++) {
				PrescribedActionDynamic p = new PrescribedActionDynamic();
				p.setProject(project);
				p.setActionId(i);
				p.setIsDone(false);
				p.setCompletedDate(null);
				getHibernateTemplate().save(p);
			}			
		}
	}
}
