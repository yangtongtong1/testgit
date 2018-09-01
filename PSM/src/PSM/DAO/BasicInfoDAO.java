package PSM.DAO;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import hibernate.Fenbao;
import hibernate.Kaoheresult;
import hibernate.Koufen;
import hibernate.Koufentongji;
import hibernate.Person;
import hibernate.Persondb;
import hibernate.PrescribedActionDynamic;
import hibernate.Project;
import hibernate.Projectmanagement;
import hibernate.Projectperson;
import hibernate.Saftyproblem;

public class BasicInfoDAO extends HibernateDaoSupport {
	public int datacount;

	public List<Project> checkProjectID(int id) {
		String hql = "from Project where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Project> list = query.list();
		getSession().close();
		return list;
	}

	/* ���� */
	public void insertProject(Project p) {
		this.getHibernateTemplate().save(p);
	}

	/* ���� */
	public void updateProject(Project p) {
		this.getHibernateTemplate().update(p);
	}

	/* ɾ�� */
	public void deleteProject(Project p) {
		this.getHibernateTemplate().delete(p);
	}

	// ��ȡ��Ŀ��ϢList
	public List<Project> getProjectList(String findstr, String projectNo, int start, int limit) {
		String hql = "from Project where id is not null";
		if (projectNo != null || projectNo.length() > 0) {
			hql += " and no like '%" + projectNo + "%'";
		}
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( no like '%" + strKey[i] + "%' or name like '%" + strKey[i] + "%' or scale like '%"
							+ strKey[i] + "%' or progress like '%" + strKey[i] + "%' or buildUnit like '%" + strKey[i]
							+ "%' or place like '%" + strKey[i] + "%' or buildContent like '%" + strKey[i]
							+ "%' or manager like '%" + strKey[i] + "%'or cost like '%" + strKey[i]
							+ "%'or fileTime like '%" + strKey[i] + "%'or startTime like '%" + strKey[i] + "%')";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Project> list = query.list();
		getSession().close();
		return list;
	}

	// liuchi 设计分包单位信息 DAO

	// ------------------
	public List<Projectperson> checkProjectpersonID(int id) {
		String hql = "Projectperson where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Projectperson> list = query.list();
		getSession().close();
		return list;
	}

	/* 插入 */
	public void insertProjectperson(Projectperson p) {
		this.getHibernateTemplate().save(p);
	}

	/* 更新 */
	public void updateProjectperson(Projectperson p) {
		this.getHibernateTemplate().update(p);
	}

	/* 删除 */
	public void deleteProjectperson(Projectperson p) {
		this.getHibernateTemplate().delete(p);
	}

	// 获取项目信息List
	public List getProjectpersonList(String findstr, int start, int limit, String projectName) {
		// String hql = "from Projectperson where id is not null";
		// if(findstr != null || findstr.length()>0)
		// {
		// String[] strKey = findstr.split(",");
		// for(int i=0; i<strKey.length; i++)
		// {
		// if(strKey[i].length()>0)
		// {
		// hql += " and ( name like '%" + strKey[i] +"%' or job like '%" +
		// strKey[i] +"%' or duty like '%" + strKey[i] + "%' )";
		// }
		// }
		// }
		String hql = "from Flownode where id is not null and projectName='" + projectName + "' and tableId='56'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( name like '%" + strKey[i] + "%' or NodeName like '%" + strKey[i]
							+ "%' or duty like '%" + strKey[i] + "%' )";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List list = query.list();
		getSession().close();
		return list;
	}

	public List<Fenbao> checkfbID(int id) {
		String hql = "from Fenbao where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Fenbao> list = query.list();
		getSession().close();
		return list;
	}

	/* ���� */
	public void insertfb(Fenbao p) {
		this.getHibernateTemplate().save(p);
	}

	/* ���� */
	public void updatefb(Fenbao p) {
		this.getHibernateTemplate().update(p);
	}

	/* ɾ�� */
	public void deletefb(Fenbao p) {
		this.getHibernateTemplate().delete(p);
	}

	public List<Fenbao> getfbList(String findstr, String type, int start, int limit, String projectName) {
		String hql = "from Fenbao where id is not null";
		System.out.println(type);
		hql += " and type like '%" + type + "%'";
		hql += " and project like '%" + projectName + "%'";
		// if(projectNo != null || projectNo.length()>0)
		// {
		// hql += " and proNo like '%" + projectNo +"%'" ;
		// }

		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and (  proRange like '%" + strKey[i] + "%' or cost like '%" + strKey[i]
							+ "%' or head like '%" + strKey[i] + "%' or techHead like '%" + strKey[i]
							+ "%' or proHead like '%" + strKey[i] + "%' or proTechHead like '%" + strKey[i]
							+ "%' or proSaveHead like '%" + strKey[i] + "%' " + "or Name like '%" + strKey[i]
							+ "%' or Rank like '%" + strKey[i] + "%'  or ProSavePeople like '%" + strKey[i] + "%')";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Fenbao> list = query.list();
		getSession().close();
		return list;
	}

	public List<Person> checkPersonID(int id) {
		String hql = "Person where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Person> list = query.list();
		getSession().close();
		return list;
	}

	/* 插入 */
	public void insertPerson(Person p) {
		this.getHibernateTemplate().save(p);
	}

	/* 更新 */
	public void updatePerson(Person p) {
		this.getHibernateTemplate().update(p);
	}

	/* 删除 */
	public void deletePerson(Person p) {
		this.getHibernateTemplate().delete(p);
	}

	// 获取项目信息List
	public List<Person> getPersonList(String findstr, int start, int limit) {
		String hql = "from Person where id is not null";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and (type like '%" + strKey[i] + "%' or name like '%" + strKey[i] + "%' or job like '%"
							+ strKey[i] + "%' or unitName like '%" + strKey[i] + "%' or identityNo like '%" + strKey[i]
							+ "%' or phoneNo like '%" + strKey[i] + "%')";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Person> list = query.list();
		getSession().close();
		return list;
	}

	// jianglingfeng 项目管理 DAO

	// ----------------------
	public List<Projectmanagement> checkProjectmanagementID(int id) {
		String hql = "from Projectmanagement where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Projectmanagement> list = query.list();
		getSession().close();
		return list;
	}

	public void insertProjectmanagement(Projectmanagement p) {
		this.getHibernateTemplate().save(p);
	}
	
	// 规定动作完成表初始化（新建项目部时）
	public void initProjectmanagement(String project) {
		for (int i = 1; i <= 41; i++) {
			PrescribedActionDynamic p = new PrescribedActionDynamic();
			p.setProject(project);
			p.setActionId(i);
			p.setIsDone(false);
			p.setCompletedDate(null);
			getHibernateTemplate().save(p);
		}
	}

	public void updateProjectmanagement(Projectmanagement p) {
		this.getHibernateTemplate().update(p);
	}

	public void deleteProjectmanagement(Projectmanagement p) {
		this.getHibernateTemplate().delete(p);
	}

	public List<Projectmanagement> getProjectmanagementList(String findstr, int start, int limit, String projectName) {
		String hql = "from Projectmanagement where id is not null";
		System.out.println(projectName);
		hql += " and name like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( no like '%" + strKey[i] + "%' or name like '%" + strKey[i] + "%' or scale like '%"
							+ strKey[i] + "%' or buildUnit like '%" + strKey[i] + "%' " + "or place like '%" + strKey[i]
							+ "%' or price like '%" + strKey[i] + "%' or manager like '%" + strKey[i] + "%'"
							+ " or progress like '%" + strKey[i] + "%'or content like '%" + strKey[i]
							+ "%'or cost like '%" + strKey[i] + "%')";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Projectmanagement> list = query.list();
		getSession().close();
		return list;
	}

	public List<String> getPersondbProjectName(String name) {
		String hql = "SELECT projectName from Flownode f WHERE f.name = '" + name + "'";
		List<String> list = this.getHibernateTemplate().find(hql);
		getSession().close();
		return list;
	}

	public Projectmanagement getPersondbProjectmanagementList(String name) {
		String hql = "from Projectmanagement f WHERE f.name = '" + name + "'";
		List<Projectmanagement> list = this.getHibernateTemplate().find(hql);
		getSession().close();
		return list.get(0);
	}
	// ------------------end-----------------//

	public List<Persondb> checkPersondbID(int id) {
		String hql = "Persondb where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Persondb> list = query.list();
		getSession().close();
		return list;
	}

	/* 插入 */
	public void insertPersondb(Persondb p) {
		this.getHibernateTemplate().save(p);
	}

	/* 更新 */
	public void updatePersondb(Persondb p) {
		this.getHibernateTemplate().update(p);
	}

	/* 删除 */
	public void deletePersondb(Persondb p) {
		this.getHibernateTemplate().delete(p);
	}

	// 获取项目信息List
	public List<Persondb> getPersondbList(String findstr, int start, int limit) {
		String hql = "from Persondb where id is not null";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( ptype = '" + strKey[i] + "' or name like '%" + strKey[i] + "%' or sex like '%"
							+ strKey[i] + "%' or papersNo like '%" + strKey[i] + "%'or papersNoTwo like '%" + strKey[i]
							+ "%'" + "or idcard like '%" + strKey[i] + "%' or birthday like '%" + strKey[i]
							+ "%'or phoneUrgent like '%" + strKey[i] + "%' or phone like '%" + strKey[i]
							+ "%'or papersTypeTwo like '%" + strKey[i] + "%' or papersType like '%" + strKey[i] + "%')";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Persondb> list = query.list();
		getSession().close();
		return list;
	}

	public List<Persondb> getPersondbListReflash(String findstr, int start, int limit) {
		String hql = "from Persondb where id is not null";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( ptype = '" + strKey[i] + "')";

				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Persondb> list = query.list();
		getSession().close();
		return list;
	}
	
	public List<Persondb> getPersondbListReflash(String projectName, String findStr, int start, int limit) {
		String hql = "FROM Persondb WHERE name IN (SELECT name FROM Flownode WHERE projectName=? AND pType=?)";
		List<Persondb> list = getHibernateTemplate().find(hql, new String[]{projectName, findStr});
		return list;
	}
	
	public String getPersonPhoneByName(String name) {
		String hql = "SELECT phone from Persondb pbd where pbd.name='" + name + "'";
		List<String> list = this.getHibernateTemplate().find(hql);
		if(list.size()>0)
			return list.get(0);
		else {
			hql = "SELECT phoneNo from Person pbd where pbd.name='" + name + "'";
			list = this.getHibernateTemplate().find(hql);
			if(list.size() > 0)
				return list.get(0);
			return "no";
		}
			
	}

	public List getXMJLNameList() {
		String xmjl = "项目经理";
		String hql = "SELECT DISTINCT(name) from Persondb pbd where pbd.ptype='" + xmjl + "'";
		List list = this.getHibernateTemplate().find(hql);
		return list;
	}

	public List getManagerNameList() {
		String xmjl = "项目经理";
		String hql = "SELECT DISTINCT(name) from Persondb pbd where pbd.ptype='" + xmjl + "'";
		// String hql = "SELECT DISTINCT(name) from Persondb";
		List list = this.getHibernateTemplate().find(hql);
		return list;
	}

	// Saftyproblem**********************
	public List<Saftyproblem> checkSaftyproblemID(int id) {
		String hql = "from Saftyproblem where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Saftyproblem> list = query.list();
		getSession().close();
		return list;
	}

	// 增
	public void insertSaftyproblem(Saftyproblem t) {
		this.getHibernateTemplate().save(t);
	}

	// 改
	public void updateSaftyproblem(Saftyproblem t) {
		this.getHibernateTemplate().update(t);
	}

	// 删
	public void deleteSaftyproblem(Saftyproblem t) {
		this.getHibernateTemplate().delete(t);
	}

	// 根据查询条件获取List
	public List<Saftyproblem> getSaftyproblemList(String findstr, int start, int limit) {
		String hql = "from Saftyproblem where id is not null";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and (No kind '%" + strKey[i] + "%' or subkind like '%" + strKey[i]
							+ "%' or showkind like '%" + strKey[i] + "%' or score like '%" + strKey[i] + "%')";
				}
			}
		}

		System.out.println(hql + "**********************");
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Saftyproblem> list = query.list();
		getSession().close();
		return list;
	}

	// Koufen**********************
	public List<Koufen> checkKoufenID(int id) {
		String hql = "from Koufen where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Koufen> list = query.list();
		getSession().close();
		return list;
	}

	// 增
	public void insertKoufen(Koufen t) {
		this.getHibernateTemplate().save(t);
	}

	// 改
	public void updateKoufen(Koufen t) {
		this.getHibernateTemplate().update(t);
	}

	// 删
	public void deleteKoufen(Koufen t) {
		this.getHibernateTemplate().delete(t);
	}

	// 根据查询条件获取List
	public List<Koufen> getKoufenList(String findstr, int start, int limit) {
		String hql = "from Koufen where id is not null";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and (No koufenx '%" + strKey[i] + "%' or koufenzhi like '%" + strKey[i]
							+ "%' or koufenzq like '%" + strKey[i] + "%' or kaoheqidian like '%" + strKey[i]
							+ "%' or duiykub like '%" + strKey[i] + "%' or duiyzid like '%" + strKey[i]
							+ "%' or baohfile like '%" + strKey[i] + "%')";
				}
			}
		}

		System.out.println(hql + "**********************");
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Koufen> list = query.list();
		getSession().close();
		return list;
	}

	// Koufentongji**********************
	public List<Koufentongji> checkKoufentongjiID(int id) {
		String hql = "from Koufentongji where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Koufentongji> list = query.list();
		getSession().close();
		return list;
	}

	// 增
	public void insertKoufentongji(Koufentongji t) {
		this.getHibernateTemplate().save(t);
	}

	// 改
	public void updateKoufentongji(Koufentongji t) {
		this.getHibernateTemplate().update(t);
	}
	
	public void updateKoufentongjiByProjectName(String projectName,String prevName) {
		List<Koufentongji> updatelist = getKoufentongjiByProjectName(prevName);
		for(int i=0;i<updatelist.size();i++) {
			updatelist.get(i).setProname(projectName);
			this.getHibernateTemplate().update(updatelist.get(i));
		}
	}

	// 删
	public void deleteKoufentongji(Koufentongji t) {
		this.getHibernateTemplate().delete(t);
	}
	
	public void deleteKoufentongjiByProjectName(String projectName) {
		List<Koufentongji> dellist = getKoufentongjiByProjectName(projectName);
		for(int i=0;i<dellist.size();i++) {
			this.getHibernateTemplate().delete(dellist.get(i));
		}
	}
	
	public List<Koufentongji> getKoufentongjiByProjectName(String projectName) {
		String hql = "from Koufentongji where proname='" + projectName + "'";
		Query query = getSession().createQuery(hql);
		List<Koufentongji> list = query.list();
		getSession().close();
		return list;
	}

	private String proname;
	private String koufenitem;
	private Integer koufenzhi;
	private Integer zongfen;

	// 根据查询条件获取List
	public List<Koufentongji> getKoufentongjiList(String findstr, int start, int limit, List<String> projectNameList) {
		
		
		
		
		String hql = "from Koufentongji where id is not null";
		
		
		
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and (proname like '%" + strKey[i] + "%' or koufenitem like '%" + strKey[i]
							+ "%' or koufenzhi like '%" + strKey[i] + "%' or zongfen like '%" + strKey[i] + "%')";
				}
			}
		}

		System.out.println(hql + "**********************");
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Koufentongji> list = query.list();
		
		if(!projectNameList.isEmpty()) {
			List<Koufentongji> pdblist = new ArrayList<Koufentongji>();
			for(int i=0;i<list.size();i++) {
				if(projectNameList.contains(list.get(i).getProname()))
					pdblist.add(list.get(i));
			}
			getSession().close();
			return pdblist;
		}
		
		getSession().close();
		return list;
	}
	
	public boolean checkTable(String table, String field, String word, String projectName) {
		
		String hql = "from " + table + " fnd where fnd." + field +" like '%" + word + "%' and fnd.projectName = '" + projectName +"'" ;
		System.out.println("QQQQQQQQQQQQQQQQQQQQQ"+hql);
		
		if(table.equals("Feemanagement")) {
			hql = "from " + table + " fnd where fnd." + field +" like '%" + word + "'" ;
		}
		
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		if(datacount == 0)
			return false;
		else
			return true;
	}
	
	public boolean checkTableByPoint(String table, String field, String word, String projectName, String startDate, String endDate) {
		String hql = "from " + table + " fnd where fnd." + field +" between '" + startDate + "' and '" + endDate + "'and fnd.projectName = '" + projectName +"'and fnd.type = '" + word +"'" ;
		System.out.println("QQQQQQQQQQQQQQQQQQQQQ"+hql);
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		if(datacount == 0)
			return false;
		else
			return true;
	}
	
	/*public boolean checkTableDate(String table, String field, String word, String projectName, String date) {
		String hql = "from " + table + " fnd where fnd." + field +" like '%" + date + "%' and fnd.projectName = '" + projectName +"'" ;
		System.out.println("QQQQQQQQQQQQQQQQQQQQQ"+hql);
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		if(datacount == 0)
			return false;
		else if()
	}*/
	
	public List<Persondb> getPersondbByType(String type) {
		String hql = "from Persondb pbd where pbd.ptype='" + type + "'";
		Query query = getSession().createQuery(hql);
		List<Persondb> list = query.list();
		getSession().close();
		return list;
	}
	
	
	//*****************yangtong*********2017.9.25**************************
			//   Kaoheresult
			public List<Kaoheresult> checkKaoheresultID(int id) {
				String hql = "from Kaoheresult where id=" + id;
				Query query = getSession().createQuery(hql);
				List<Kaoheresult> list = query.list();
				getSession().close();
				return list;
			}

			// 增
			public void insertKaoheresult(Kaoheresult s) {
				this.getHibernateTemplate().save(s);
			}

			// 改
			public void updateKaoheresult(Kaoheresult s) {
				this.getHibernateTemplate().update(s);
			}

			// 删
			public void deleteKaoheresult(Kaoheresult s) {
				this.getHibernateTemplate().delete(s);
			}
			
			// 根据查询条件获取List
			public List<Kaoheresult> getKaoheresultList(String findstr, int start, int limit, String projectName) {
				String hql = "from Kaoheresult where id is not null";
				hql += " and projectName like '%" + projectName + "%'";
				if (findstr != null || findstr.length() > 0) {
					String[] strKey = findstr.split(",");
					for (int i = 0; i < strKey.length; i++) {
						if (strKey[i].length() > 0) {
							hql += " and (year bstype '%" + strKey[i] + "%' or month like '%" + strKey[i]
									+ "%' or score like '%" + strKey[i] + "%' or reason like '%" + strKey[i] + "%')";
						}
					}
				}
				// System.out.println(hql + "**********************");
				Query query = getSession().createQuery(hql);
				datacount = query.list().size();
				query.setFirstResult(start);
				query.setMaxResults(limit);
				List<Kaoheresult> list = query.list();
				getSession().close();
				return list;
			}

	
	
}
