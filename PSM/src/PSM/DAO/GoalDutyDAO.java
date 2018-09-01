package PSM.DAO;

import java.util.List;

import org.hibernate.Query;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import hibernate.Anweihui;
import hibernate.Fbplan;
import hibernate.Fileupload;
import hibernate.Flownode;
import hibernate.Goaldecom;
import hibernate.Monthplan;
import hibernate.Projectperson;
import hibernate.Saveculture;
import hibernate.Saveprodbook;
import hibernate.Saveproduct;
import hibernate.Saveproplan;
import hibernate.Securityplan;
import hibernate.Threeworkplan;
import hibernate.Workplan;
import hibernate.Yearplan;
import hibernate.Safetypromanagement;
import hibernate.Safetypromanagementfb;

public class GoalDutyDAO extends HibernateDaoSupport {
	public int datacount;

	public List<Flownode> checkFlowNodeName(String tableid, String stepNum, String nodeName, String projectName) {
		String hql = "from Flownode where nodeName='" + nodeName + "' and stepNum=" + stepNum + " and projectName='"
				+ projectName + "' and tableId='" + tableid + "'";
		Query query = getSession().createQuery(hql);
		List<Flownode> list = query.list();
		getSession().close();
		return list;
	}

	public List<Flownode> checkFlowNodeID(int id) {
		String hql = "from Flownode where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Flownode> list = query.list();
		getSession().close();
		return list;
	}

	public List<Projectperson> checkProjectpersonJob(String job) {
		String hql = "from Projectperson where job='" + job + "'";
		Query query = getSession().createQuery(hql);
		List<Projectperson> list = query.list();
		getSession().close();
		return list;
	}

	/* 插入 */
	public void insertFlowNode(Flownode p) {
		this.getHibernateTemplate().save(p);
	}

	/* 更新 */
	public void updateFlowNode(Flownode p) {
		this.getHibernateTemplate().update(p);
	}
	
	public void updateFlowNodeByProjectName(String projectName,String prevName) {
		List<Flownode> updatelist = getFlowNodeByProjectName(prevName);
		for(int i=0;i<updatelist.size();i++) {
			updatelist.get(i).setProjectName(projectName);
			this.getHibernateTemplate().update(updatelist.get(i));
		}
	}

	/* 删除 */
	public void deleteFlowNode(Flownode p) {
		this.getHibernateTemplate().delete(p);
	}
	
	
	public void deleteFlowNodeByProjectName(String projectName) {
		List<Flownode> dellist = getFlowNodeByProjectName(projectName);
		for(int i=0;i<dellist.size();i++) {
			this.getHibernateTemplate().delete(dellist.get(i));
		}
	}
	
	public List<Flownode> getFlowNodeByProjectName(String projectName) {
		String hql = "from Flownode where projectName='" + projectName + "'";
		Query query = getSession().createQuery(hql);
		List<Flownode> list = query.list();
		getSession().close();
		return list;
	}

	// jianglf-------------------------------------------------
	// public List getContentList()
	// {
	// String hql = "SELECT DISTINCT(content) from Yearplan";
	// List list = this.getHibernateTemplate().find(hql);
	// return list;
	// }
	// end--------------------------------------------------------

	public List getNodeNameList() {
		String hql = "SELECT DISTINCT(ptype) from Persondb";
		List list = this.getHibernateTemplate().find(hql);
		return list;
	}

	public List getNameList(String nodeName) {
		String hql = "select DISTINCT(name) from Persondb where ptype = '" + nodeName + "' order by name";
		List list = this.getHibernateTemplate().find(hql);
		return list;
	}

	public List getPhone(String nodeName, String name) {
		String hql = "select phone from Persondb where ptype = '" + nodeName + "' and name = '" + name + "'";
		List list = this.getHibernateTemplate().find(hql);
		return list;
	}

	public List<Flownode> getFlowNodeList(String tableid, String projectName) {
		String hql = "from Flownode where tableId ='" + tableid + "' and projectName = '" + projectName
				+ "' order by stepNum,orderNum";
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		List<Flownode> list = query.list();
		getSession().close();
		return list;
	}

	public List<Flownode> getFlowNodeListByStep(String tableid, String stepNum, String projectName) {
		String hql = "from Flownode where tableId ='" + tableid + "' and stepNum=" + stepNum + " and projectName = '"
				+ projectName + "' order by orderNum";
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		List<Flownode> list = query.list();
		getSession().close();
		return list;
	}

	public Saveculture getSaveculture(String type) {
		String hql = "from Saveculture where id is not null";
		System.out.println(type);
		hql += " and type like '%" + type + "%'";

		Query query = getSession().createQuery(hql);

		Saveculture s = (Saveculture) getHibernateTemplate().find(hql).get(0);
		getSession().close();
		return s;
	}

	/* 更新 */
	public void updateSaveculture(Saveculture p) {
		this.getHibernateTemplate().update(p);
	}

	/**
	 * 执行SQL语句
	 * 
	 * @param sql
	 */
	public void executeSQL(String sql) {
		getSession().beginTransaction();
		Query query = getSession().createSQLQuery(sql);
		query.executeUpdate();
		getSession().beginTransaction().commit();
		getSession().close();
	}

	public List<Securityplan> checkSecurityplanID(int id) {
		String hql = "from Securityplan where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Securityplan> list = query.list();
		getSession().close();
		return list;
	}

	/* ���� */
	public void insertSecurityplan(Securityplan p) {
		this.getHibernateTemplate().save(p);
	}

	/* ���� */
	public void updateSecurityplan(Securityplan p) {
		this.getHibernateTemplate().update(p);
	}

	/* ɾ�� */
	public void deleteSecurityplan(Securityplan p) {
		this.getHibernateTemplate().delete(p);
	}

	// ��ȡ��Ŀ��ϢList
	public List<Securityplan> getSecurityplanList(String findstr, String type, int start, int limit,String projectName) {
		String hql = "from Securityplan where id is not null";

		System.out.println(type);
		hql += " and projectName like '%" + projectName +"%'";
		hql += " and title like '%" + type + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Securityplan> list = query.list();
		getSession().close();
		return list;
	}

	public List<Anweihui> checkAnweihuiID(int id) {
		String hql = "from Anweihui where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Anweihui> list = query.list();
		getSession().close();
		return list;
	}

	/* ���� */
	public void insertAnweihui(Anweihui p) {
		this.getHibernateTemplate().save(p);
	}

	/* ���� */
	public void updateAnweihui(Anweihui p) {
		this.getHibernateTemplate().update(p);
	}

	/* ɾ�� */
	public void deleteAnweihui(Anweihui p) {
		this.getHibernateTemplate().delete(p);
	}

	// ��ȡ��Ŀ��ϢList
	public List<Anweihui> getAnweihuiList(String findstr, int start, int limit, String projectName) {
		String hql = "from Anweihui where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( esorad like '%" + strKey[i] + "%' or time like '%" + strKey[i] + "%' or head like '%"
							+ strKey[i] + "%' or viceHead like '%" + strKey[i] + "%' or form like '%" + strKey[i]
							+ "%' or agency like '%" + strKey[i] + "%' )";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Anweihui> list = query.list();
		getSession().close();
		return list;
	}

	public List<Saveproduct> checkSaveproductID(int id) {
		String hql = "from Saveproduct where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Saveproduct> list = query.list();
		getSession().close();
		return list;
	}

	/* ���� */
	public void insertSaveproduct(Saveproduct p) {
		this.getHibernateTemplate().save(p);
	}

	/* ���� */
	public void updateSaveproduct(Saveproduct p) {
		this.getHibernateTemplate().update(p);
	}

	/* ɾ�� */
	public void deleteSaveproduct(Saveproduct p) {
		this.getHibernateTemplate().delete(p);
	}

	// jiagnlf----------------------------
	// ��ȡ��Ŀ��ϢList
	public List<Saveproduct> getSaveproductList(String findstr, String type, int start, int limit, String projectName) {
		String hql = "from Saveproduct where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		System.out.println(type);
		hql += " and type like '%" + type + "%'";

		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( evaluateRate like '%" + strKey[i] + "%' or trainRate like '%" + strKey[i]
							+ "%' or reformRate like '%" + strKey[i] + "%' or reachRate like '%" + strKey[i]
							+ "%' or examineRate like '%" + strKey[i] + "%' or enviPassRate like '%" + strKey[i]
							+ "%' or bottomRate like '%" + strKey[i] + "%' or behave like '%" + strKey[i]
							+ "%' or timeYear like '%" + strKey[i] + "%'" + " or sickPassRate like '%" + strKey[i]
							+ "%' or checkRate like '%" + strKey[i] + "%' or workAcci like '%" + strKey[i]
							+ "%' or prodAcci like '%" + strKey[i] + "%' or acciRate like '%" + strKey[i] + "%'"
							+ " or fenBaoAcci like '%" + strKey[i] + "%' or disaster like '%" + strKey[i]
							+ "%' or fireAcci like '%" + strKey[i] + "%' or jobEvent like '%" + strKey[i]
							+ "%' or pollutEvent like '%" + strKey[i] + "%' or management like '%" + strKey[i]
							+ "%'or contrl like '%" + strKey[i] + "%'" + ")";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Saveproduct> list = query.list();
		getSession().close();
		return list;
	}

	// end-------------------------------------------------------
	public List<Goaldecom> checkGoaldecomID(int id) {
		String hql = "from Goaldecom where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Goaldecom> list = query.list();
		getSession().close();
		return list;
	}

	/* ���� */
	public void insertGoaldecom(Goaldecom p) {
		this.getHibernateTemplate().save(p);
	}

	/* ���� */
	public void updateGoaldecom(Goaldecom p) {
		this.getHibernateTemplate().update(p);
	}

	/* ɾ�� */
	public void deleteGoaldecom(Goaldecom p) {
		this.getHibernateTemplate().delete(p);
	}

	// ��ȡ��Ŀ��ϢList
	public List<Goaldecom> getGoaldecomList(String findstr, int start, int limit, String projectName) {
		String hql = "from Goaldecom where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( target like '%" + strKey[i] + "%' or goalDecom like '%" + strKey[i]
							+ "%' or completed like '%" + strKey[i] + "%' or manager like '%" + strKey[i]
							+ "%' or time like '%" + strKey[i] + "%' or measure like '%" + strKey[i]
							+ "%' or mvalue like '%" + strKey[i] + "%' or content like '%" + strKey[i]
							+ "%' or indexValue like '%" + strKey[i] + "%' or factor like '%" + strKey[i]
							+ "%' or quaSave like '%" + strKey[i] + "%' or design like '%" + strKey[i]
							+ "%' or engineer like '%" + strKey[i] + "%' or accessory like '%" + strKey[i] + "%' or affair like '%" + strKey[i]
							+ "%' or signOne like '%" + strKey[i] + "%' or buy like '%" + strKey[i]
							+ "%' or makeOne like '%" + strKey[i] + "%' or examineOne like '%" + strKey[i]
							+ "%' or agreeOne like '%" + strKey[i] + "%'" + ")";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Goaldecom> list = query.list();
		getSession().close();
		return list;
	}

	public List<Saveprodbook> checkSaveprodbookID(int id) {
		String hql = "from Saveprodbook  where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Saveprodbook> list = query.list();
		getSession().close();
		return list;
	}

	/* ���� */
	public void insertSaveprodbook(Saveprodbook p) {
		this.getHibernateTemplate().save(p);
	}

	/* ���� */
	public void updateSaveprodbook(Saveprodbook p) {
		this.getHibernateTemplate().update(p);
	}

	/* ɾ�� */
	public void deleteSaveprodbook(Saveprodbook p) {
		this.getHibernateTemplate().delete(p);
	}

	// ��ȡ��Ŀ��ϢList
	public List<Saveprodbook> getSaveprodbookList(String findstr, int start, int limit, String projectName) {
		String hql = "from Saveprodbook where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( type like '%" + strKey[i] + "%'  or timeYear like '%" + strKey[i]
							+ "%' or toTarget like '%" + strKey[i] + "%' )";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Saveprodbook> list = query.list();
		getSession().close();
		return list;
	}

	public List<Saveproplan> checkSaveproplanID(int id) {
		String hql = "from Saveproplan where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Saveproplan> list = query.list();
		getSession().close();
		return list;
	}

	/* ���� */
	public void insertSaveproplan(Saveproplan p) {
		this.getHibernateTemplate().save(p);
	}

	/* ���� */
	public void updateSaveproplan(Saveproplan p) {
		this.getHibernateTemplate().update(p);
	}

	/* ɾ�� */
	public void deleteSaveproplan(Saveproplan p) {
		this.getHibernateTemplate().delete(p);
	}

	// ��ȡ��Ŀ��ϢList
	public List<Saveproplan> getSaveproplanList(String findstr, int start, int limit, String projectName) {
		String hql = "from Saveproplan where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( anweihui like '%" + strKey[i] + "%'  or threeGroup like '%" + strKey[i]
							+ "%' or fourBuild like '%" + strKey[i] + "%' or saveBuild like '%" + strKey[i]
							+ "%'  or savePlan like '%" + strKey[i] + "%' or saveCheck like '%" + strKey[i]
							+ "%' or handlePlan like '%" + strKey[i] + "%'  or saveBuildPlan like '%" + strKey[i]
							+ "%' or dangerPublic like '%" + strKey[i] + "%' or executePlan like '%" + strKey[i]
							+ "%'  or workPlan like '%" + strKey[i] + "%' )";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Saveproplan> list = query.list();
		getSession().close();
		return list;
	}

	public List<Workplan> checkWorkplanID(int id) {
		String hql = "from Workplan  where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Workplan> list = query.list();
		getSession().close();
		return list;
	}

	/* ���� */
	public void insertWorkplan(Workplan p) {
		this.getHibernateTemplate().save(p);
	}

	/* ���� */
	public void updateWorkplan(Workplan p) {
		this.getHibernateTemplate().update(p);
	}

	/* ɾ�� */
	public void deleteWorkplan(Workplan p) {
		this.getHibernateTemplate().delete(p);
	}

	// ��ȡ��Ŀ��ϢList
	public List<Workplan> getWorkplanList(String findstr, String type, int start, int limit) {
		String hql = "from Workplan where id is not null";

		System.out.println(type);
		hql += " and type like '%" + type + "%'";

		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( taskContent like '%" + strKey[i] + "%'  or dutyMan like '%" + strKey[i]
							+ "%' or taskScale like '%" + strKey[i] + "%'  or finishedTask like '%" + strKey[i]
							+ "%' or timeYear like '%" + strKey[i] + "%'  or planFinishTime like '%" + strKey[i]
							+ "%' or realFinishTime like '%" + strKey[i] + "%')";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Workplan> list = query.list();
		getSession().close();
		return list;
	}

	public List<Yearplan> checkYearplanID(int id) {
		String hql = "from Yearplan where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Yearplan> list = query.list();
		getSession().close();
		return list;
	}

	/* ���� */
	public void insertYearplan(Yearplan p) {
		this.getHibernateTemplate().save(p);
	}

	/* ���� */
	public void updateYearplan(Yearplan p) {
		this.getHibernateTemplate().update(p);
	}

	/* ɾ�� */
	public void deleteYearplan(Yearplan p) {
		this.getHibernateTemplate().delete(p);
	}

	// ��ȡ��Ŀ��ϢList
	public List<Yearplan> getYearplanList(String findstr, int start, int limit, String projectName) {
		String hql = "from Yearplan where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( year like '%" + strKey[i] + "%' or workload like '%" + strKey[i]
							+ "%' or completed like '%" + strKey[i] + "%' " + "or content like '%" + strKey[i]
							+ "%' or manager like '%" + strKey[i] + "%' or planDate like '%" + strKey[i]
							+ "%' or realDate like '%" + strKey[i] + "%'" + " or accessory like '%" + strKey[i] + "%')";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Yearplan> list = query.list();
		getSession().close();
		return list;
	}

	// jianglf----------------------------------------------------------
	public List<Safetypromanagement> checkSafetypromanagementID(int id) {
		String hql = "from Safetypromanagement where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Safetypromanagement> list = query.list();
		getSession().close();
		return list;
	}

	/* ���� */
	public void insertSafetypromanagement(Safetypromanagement p) {
		this.getHibernateTemplate().save(p);
	}

	/* ���� */
	public void updateSafetypromanagement(Safetypromanagement p) {
		this.getHibernateTemplate().update(p);
	}

	/* ɾ�� */
	public void deleteSafetypromanagement(Safetypromanagement p) {
		this.getHibernateTemplate().delete(p);
	}

	// ��ȡ��Ŀ��ϢList
	public List<Safetypromanagement> getSafetypromanagementList(String findstr, int start, int limit,
			String projectName) {
		String hql = "from Safetypromanagement where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( name like '%" + strKey[i] + "%' or time like '%" + strKey[i]
							+ "%' or sperson like '%" + strKey[i] + "%' or person like '%" + strKey[i]
							+ "%' or accessory like '%" + strKey[i] + "%' )";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Safetypromanagement> list = query.list();
		getSession().close();
		return list;
	}

	public List<Safetypromanagementfb> checkSafetypromanagementfbID(int id) {
		String hql = "from Safetypromanagementfb where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Safetypromanagementfb> list = query.list();
		getSession().close();
		return list;
	}

	/* ���� */
	public void insertSafetypromanagementfb(Safetypromanagementfb p) {
		this.getHibernateTemplate().save(p);
	}

	/* ���� */
	public void updateSafetypromanagementfb(Safetypromanagementfb p) {
		this.getHibernateTemplate().update(p);
	}

	/* ɾ�� */
	public void deleteSafetypromanagementfb(Safetypromanagementfb p) {
		this.getHibernateTemplate().delete(p);
	}

	// ��ȡ��Ŀ��ϢList
	public List<Safetypromanagementfb> getSafetypromanagementfbList(String findstr, int start, int limit,
			String projectName) {
		String hql = "from Safetypromanagementfb where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( name like '%" + strKey[i] + "%' or fbName like '%" + strKey[i] + "%' or time like '%"
							+ strKey[i] + "%' or sperson like '%" + strKey[i] + "%' or person like '%" + strKey[i]
							+ "%' or accessory like '%" + strKey[i] + "%' )";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Safetypromanagementfb> list = query.list();
		getSession().close();
		return list;
	}

	public List<Threeworkplan> checkThreeworkplanID(int id) {
		String hql = "from Threeworkplan where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Threeworkplan> list = query.list();
		getSession().close();
		return list;
	}

	/* ���� */
	public void insertThreeworkplan(Threeworkplan p) {
		this.getHibernateTemplate().save(p);
	}

	/* ���� */
	public void updateThreeworkplan(Threeworkplan p) {
		this.getHibernateTemplate().update(p);
	}

	/* ɾ�� */
	public void deleteThreeworkplan(Threeworkplan p) {
		this.getHibernateTemplate().delete(p);
	}

	// ��ȡ��Ŀ��ϢList
	public List<Threeworkplan> getThreeworkplanList(String findstr, int start, int limit, String projectName) {
		String hql = "from Threeworkplan where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( no like '%" + strKey[i] + "%' or year like '%" + strKey[i] + "%' or workload like '%"
							+ strKey[i] + "%' or completed like '%" + strKey[i] + "%' " + "or content like '%"
							+ strKey[i] + "%' or manager like '%" + strKey[i] + "%' or planDate like '%" + strKey[i]
							+ "%' or realDate like '%" + strKey[i] + "%'" + " or accessory like '%" + strKey[i] + "%')";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Threeworkplan> list = query.list();
		getSession().close();
		return list;
	}
	// end--------------------------------------------------------------

	public List<Monthplan> checkMonthplanID(int id) {
		String hql = "from Monthplan where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Monthplan> list = query.list();
		getSession().close();
		return list;
	}

	/* ���� */
	public void insertMonthplan(Monthplan p) {
		this.getHibernateTemplate().save(p);
	}

	/* ���� */
	public void updateMonthplan(Monthplan p) {
		this.getHibernateTemplate().update(p);
	}

	/* ɾ�� */
	public void deleteMonthplan(Monthplan p) {
		this.getHibernateTemplate().delete(p);
	}

	// ��ȡ��Ŀ��ϢList
	public List<Monthplan> getMonthplanList(String findstr, int start, int limit, String projectName) {
		String hql = "from Monthplan where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( no like '%" + strKey[i] + "%' or year like '%" + strKey[i] + "%'or month like '%"
							+ strKey[i] + "%' or workload like '%" + strKey[i] + "%' or completed like '%" + strKey[i]
							+ "%' " + "or content like '%" + strKey[i] + "%' or manager like '%" + strKey[i]
							+ "%' or planDate like '%" + strKey[i] + "%' or realDate like '%" + strKey[i]
							+ "%' or unit like '%" + strKey[i] + "%' or completedsm like '%" + strKey[i] + "%'"
							+ " or accessory like '%" + strKey[i] + "%')";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Monthplan> list = query.list();
		getSession().close();
		return list;
	}

	public List<Fbplan> checkFbplanID(int id) {
		String hql = "from Fbplan where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Fbplan> list = query.list();
		getSession().close();
		return list;
	}

	/* ���� */
	public void insertFbplan(Fbplan p) {
		this.getHibernateTemplate().save(p);
	}

	/* ���� */
	public void updateFbplan(Fbplan p) {
		this.getHibernateTemplate().update(p);
	}

	/* ɾ�� */
	public void deleteFbplan(Fbplan p) {
		this.getHibernateTemplate().delete(p);
	}

	// ��ȡ��Ŀ��ϢList
	public List<Fbplan> getFbplanList(String findstr, int start, int limit, String projectName) {
		String hql = "from Fbplan where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( no like '%" + strKey[i] + "%' or name like '%" + strKey[i] + "%' or planName like '%"
							+ strKey[i] + "%' or date like '%" + strKey[i] + "%' " + " or accessory like '%" + strKey[i]
							+ "%')";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Fbplan> list = query.list();
		getSession().close();
		return list;
	}

	public void insertFileUpload(Fileupload pro) {
		this.getHibernateTemplate().save(pro);
		
	}

	public List<Fileupload> getFileUploadNameList(String title, String projectName) {
		String hql = "";
		if(projectName != null && projectName.length() > 0)
			hql = "FROM Fileupload f WHERE f.title='" + title + "'and f.projectName = '" + projectName +"'";
		else
			hql = "FROM Fileupload f WHERE f.title='" + title + "'";
		List<Fileupload> list = this.getHibernateTemplate().find(hql);
		return list;
	}
	
	public Fileupload findByFileNameAndTitle(String file, String title , String projectName) {
		String hql = "FROM Fileupload f WHERE f.title='" + title + "'and f.file = '" + file +"'and f.projectName = '" + projectName +"'";
		List<Fileupload> list = this.getHibernateTemplate().find(hql);
		return list.get(0);
	}
	
	public void deleteFileUpload(Fileupload pro) {
		this.getHibernateTemplate().delete(pro);
	}
}
