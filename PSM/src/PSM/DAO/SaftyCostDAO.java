package PSM.DAO;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import hibernate.Exemplaryfile;
import hibernate.Fenbaoplan;
import hibernate.Fenbaosaftyaccounts;
import hibernate.Fenbaosaftycostsum;
import hibernate.Fenbaosaftyjiancha;
import hibernate.Project;
import hibernate.Projectperson;
import hibernate.Saftyaccounts;
import hibernate.Saftycheck;
import hibernate.Saftycost;
import hibernate.Saftycostplan;
import hibernate.Saftycosttj;
import hibernate.Saftycosttj1;
import hibernate.Saftycosttj2;
import hibernate.Saftycosttj3;
import hibernate.Saftyjiancha;
import hibernate.Sanxiang;

public class SaftyCostDAO extends HibernateDaoSupport {
	public int datacount;

	// saftycost表的数据库操作*********************************************
	/**
	 * 说明:此表包含两个项目显示，通过planorcost字段判断 0:计划 1:统计
	 */

	public List<Saftycost> checkSaftycostID(int id) {
		String hql = "from Saftycost where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Saftycost> list = query.list();
		getSession().close();
		return list;
	}

	public List<Saftycost> checkSaftycost(String subjectnum) {
		String hql = "from Saftycost where subjectnum=" + subjectnum;
		Query query = getSession().createQuery(hql);
		List<Saftycost> list = query.list();
		getSession().close();
		return list;
	}

	// 增
	public void insertSaftycost(Saftycost s) {
		this.getHibernateTemplate().save(s);
	}

	// 改
	public void updateSaftycost(Saftycost s) {
		this.getHibernateTemplate().update(s);
	}

	// 删
	public void deleteSaftycost(Saftycost s) {
		this.getHibernateTemplate().delete(s);
	}

	// 查询所有
	public List<Saftycost> findAllSaftycost() {
		try {
			String queryString = "from Saftycost";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			throw re;
		}
	}

	// 查询所有费用计划的条目
	public List<Saftycost> findAllSaftycostplan() {
		try {
			String queryString = "from Saftycost where planorcost=0";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			throw re;
		}
	}

	// 查询所有费用统计的条目
	public List<Saftycost> findAllSaftycostcost() {
		try {
			String queryString = "from Saftycost where planorcost=1";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			throw re;
		}
	}

	// 根据查询条件获取List
	public List<Saftycost> getSaftycostList(String findstr, int start, int limit) {
		String hql = "from Saftycost where id is not null";

		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( costkind like '%" + strKey[i] + "%' or costetails like '%" + strKey[i]
							+ "%' or jan like '%" + strKey[i] + "%' or feb like '%" + strKey[i] + "%' or mar like '%"
							+ strKey[i] + "%' or apr like '%" + strKey[i] + "%' or may like '%" + strKey[i]
							+ "%' or june like '%" + strKey[i] + "%' or july like '%" + strKey[i] + "%' or aug like '%"
							+ strKey[i] + "%' or sept like '%" + strKey[i] + "%' or oct like '%" + strKey[i]
							+ "%' or nov like '%" + strKey[i] + "%' or dec like '%" + strKey[i]
							+ "%' or sumcost like '%" + strKey[i] + "%' or planorcost like '%" + strKey[i] + "%')";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Saftycost> list = query.list();
		getSession().close();
		return list;
	}

	// ************saftycostplan表的数据库操作*********************************************
	public List<Saftycostplan> checkSaftycostplanID(int id) {
		String hql = "from Saftycostplan where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Saftycostplan> list = query.list();
		getSession().close();
		return list;
	}

	// 增
	public void insertSaftycostplan(Saftycostplan s) {
		this.getHibernateTemplate().save(s);
	}

	// 改
	public void updateSaftycostplan(Saftycostplan s) {
		this.getHibernateTemplate().update(s);
	}

	// 删
	public void deleteSaftycostplan(Saftycostplan s) {
		this.getHibernateTemplate().delete(s);
	}

	// // 查询所有
	// public List<Saftycostplan> findAllSaftycostplan() {
	// try {
	// String queryString = "from Saftycostplan";
	// return getHibernateTemplate().find(queryString);
	// } catch (RuntimeException re) {
	// throw re;
	// }
	// }yearorfull

	// 根据查询条件获取List
	public List<Saftycostplan> getSaftycostplanList(String findstr, int start, int limit, String projectName) {
		String hql = "from Saftycostplan where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		hql += "order by year";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and (subjectnum like '%" + strKey[i] + "%' or costkind0 like '%" + strKey[i]
							+ "%' or costkind1 like '%" + strKey[i] + "%' or costkind2 like '%" + strKey[i]
							+ "%' or costkind3 like '%" + strKey[i] + "%' or costkind4 like '%" + strKey[i]
							+ "%' or costkind5 like '%" + strKey[i] + "%' or costkind6 like '%" + strKey[i]
							+ "%' or costkind7 like '%" + strKey[i] + "%' or costkind8 like '%" + strKey[i]
							+ "%' or costkind9 like '%" + strKey[i] + "%' or costkind01 like '%" + strKey[i]
							+ "%' or costkind11 like '%" + strKey[i] + "%' or costkind21 like '%" + strKey[i]
							+ "%' or costkind31 like '%" + strKey[i] + "%' or costkind41 like '%" + strKey[i]
							+ "%' or costkind51 like '%" + strKey[i] + "%' or costkind61 like '%" + strKey[i]
							+ "%' or costkind71 like '%" + strKey[i] + "%' or costkind81 like '%" + strKey[i]
							+ "%' or costkind91 like '%" + strKey[i] + "%' or costkind02 like '%" + strKey[i]
							+ "%' or costkind12 like '%" + strKey[i] + "%' or costkind22 like '%" + strKey[i]
							+ "%' or costkind32 like '%" + strKey[i] + "%' or costkind42 like '%" + strKey[i]
							+ "%' or costkind52 like '%" + strKey[i] + "%' or costkind62 like '%" + strKey[i]
							+ "%' or costkind72 like '%" + strKey[i] + "%' or costkind82 like '%" + strKey[i]
							+ "%' or costkind92 like '%" + strKey[i] + "%' or year like '%" + strKey[i]
							+ "%' or costplan like '%" + strKey[i] + "%' or costplansum like '%" + strKey[i]
							+ "%' or costplansum1 like '%" + strKey[i] + "%' or costplansum2 like '%" + strKey[i]
							+ "%' or yearorfull like '%" + strKey[i] + "%')";
				}
			}
		}

		System.out.println(hql + "**********************");
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Saftycostplan> list = query.list();
		getSession().close();
		return list;
	}

	// ************Saftycosttj表的数据库操作*********************************************
	public List<Saftycosttj> checkSaftycosttjID(int id) {
		String hql = "from Saftycosttj where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Saftycosttj> list = query.list();
		getSession().close();
		return list;
	}

	public List<Saftycosttj> gettjBysubjectnum(String subjectnum) {
		String hql = "from Saftycosttj where subjectnum=" + subjectnum;
		Query query = getSession().createQuery(hql);
		List<Saftycosttj> list = query.list();
		getSession().close();
		return list;
	}

	// 增
	public void insertSaftycosttj(Saftycosttj s) {
		this.getHibernateTemplate().save(s);
	}

	// 改
	public void updateSaftycosttj(Saftycosttj s) {
		this.getHibernateTemplate().update(s);
	}

	// 删
	public void deleteSaftycosttj(Saftycosttj s) {
		this.getHibernateTemplate().delete(s);
	}

	// // 查询所有
	// public List<Saftycostplan> findAllSaftycostplan() {
	// try {
	// String queryString = "from Saftycostplan";
	// return getHibernateTemplate().find(queryString);
	// } catch (RuntimeException re) {
	// throw re;
	// }
	// }

	// 根据查询条件获取List
	public List<Saftycosttj> getSaftycosttjList(String findstr, int start, int limit) {
		String hql = "from Saftycosttj where id is not null";

		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and (subjectnum like '%" + strKey[i] + "%' or costkind like '%" + strKey[i]
							+ "%' or year like '%" + strKey[i] + "%' or cost like '%" + strKey[i]
							+ "%' or costrealtime like '%" + strKey[i] + "%')";
				}
			}
		}

		System.out.println(hql + "**********************");
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Saftycosttj> list = query.list();
		getSession().close();
		return list;
	}

	// saftyaccounts表的数据库操作*********************************************

	// 根据主键查找
	// public List<Saftyaccounts> checkSaftyaccounts(String subjectnum) {
	// String hql = "from Saftyaccounts where subjectnum=" + subjectnum;
	// Query query = getSession().createQuery(hql);
	// List<Saftyaccounts> list = query.list();
	// getSession().close();
	// return list;
	// }

	public List<Saftyaccounts> checkSaftyaccountsID(int id) {
		String hql = "from Saftyaccounts where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Saftyaccounts> list = query.list();
		getSession().close();
		return list;
	}

	// 增
	public void insertSaftyaccounts(Saftyaccounts s) {
		this.getHibernateTemplate().save(s);
	}

	// 改
	public void updateSaftyaccounts(Saftyaccounts s) {
		this.getHibernateTemplate().update(s);
	}

	// 删
	public void deleteSaftyaccounts(Saftyaccounts s) {
		this.getHibernateTemplate().delete(s);
	}

	// 查询所有
	public List<Saftyaccounts> findAllSaftyaccounts() {
		try {
			String queryString = "from Saftyaccounts";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			throw re;
		}
	}

	// 获取项目信息List
	public List<Saftyaccounts> getSaftyaccountsList(String findstr, int start, int limit, String projectName) {
		String hql = "from Saftyaccounts where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		hql += "order by approtime";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( costkind like '%" + strKey[i] + "%' or costetails like '%" + strKey[i]
							+ "%' or applysector like '%" + strKey[i] + "%' or costuse like '%" + strKey[i]
							+ "%' or amount like '%" + strKey[i] + "%' or manager like '%" + strKey[i]
							+ "%' or registerperson like '%" + strKey[i] + "%' or approtime like '%" + strKey[i]
							+ "%' or accessory like '%" + strKey[i] + "%' or remarks like '%" + strKey[i]
							+ "%' or checksituation like '%" + strKey[i] + "%' )";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Saftyaccounts> list = query.list();
		getSession().close();
		return list;
	}

	// fenbaosaftyaccounts表的数据库操作*********************************************

	public List<Fenbaosaftyaccounts> checkFenbaosaftyaccountsID(int id) {
		String hql = "Fenbaosaftyaccounts where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Fenbaosaftyaccounts> list = query.list();
		getSession().close();
		return list;
	}

	// 增
	public void insertFenbaosaftyaccounts(Fenbaosaftyaccounts s) {
		this.getHibernateTemplate().save(s);
	}

	// 改
	public void updateFenbaosaftyaccounts(Fenbaosaftyaccounts s) {
		this.getHibernateTemplate().update(s);
	}

	// 删
	public void deleteFenbaosaftyaccounts(Fenbaosaftyaccounts s) {
		this.getHibernateTemplate().delete(s);
	}

	// 查询所有
	public List<Fenbaosaftyaccounts> findAllFenbaosaftyaccounts() {
		try {
			String queryString = "from Fenbaosaftyaccounts";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			throw re;
		}
	}

	// 获取项目信息List
	public List<Fenbaosaftyaccounts> getFenbaosaftyaccountsList(String findstr, int start, int limit,
			String projectName) {
		String hql = "from Fenbaosaftyaccounts where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( subcontractor like '%" + strKey[i] + "%' or checktime like '%" + strKey[i]
							+ "%' or taizhang like '%" + strKey[i] + "%' )";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Fenbaosaftyaccounts> list = query.list();
		getSession().close();
		return list;
	}

	// fenbaosaftycostsum表的数据库操作*********************************************

	public List<Fenbaosaftycostsum> checkFenbaosaftycostsum(int num) {
		String hql = "from Fenbaosaftycostsum where num=" + num;
		Query query = getSession().createQuery(hql);
		List<Fenbaosaftycostsum> list = query.list();
		getSession().close();
		return list;
	}

	public List<Fenbaosaftycostsum> checkFenbaosaftycostsumID(int id) {
		String hql = "from Fenbaosaftycostsum where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Fenbaosaftycostsum> list = query.list();
		getSession().close();
		return list;
	}

	// 增
	public void insertFenbaosaftycostsum(Fenbaosaftycostsum s) {
		this.getHibernateTemplate().save(s);
	}

	// 改
	public void updateFenbaosaftycostsum(Fenbaosaftycostsum s) {
		this.getHibernateTemplate().update(s);
	}

	// 删
	public void deleteFenbaosaftycostsum(Fenbaosaftycostsum s) {
		this.getHibernateTemplate().delete(s);
	}

	// 查询所有
	public List<Fenbaosaftycostsum> findAllFenbaosaftycostsum() {
		try {
			String queryString = "from Fenbaosaftycostsum";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			throw re;
		}
	}

	// 获取项目信息List
	public List<Fenbaosaftycostsum> getFenbaosaftycostsumList(String findstr, int start, int limit) {
		String hql = "from Fenbaosaftycostsum where id is not null";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( costkind like '%" + strKey[i] + "%' or repoter like '%" + strKey[i]
							+ "%' or regtime like '%" + strKey[i] + "%' or cost like '%" + strKey[i]
							+ "%' or sumcost like '%" + strKey[i] + "%' )";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Fenbaosaftycostsum> list = query.list();
		getSession().close();
		return list;
	}

	// Sanxiang表的数据库操作*********************************************
	public List<Sanxiang> checkSanxiangID(int id) {
		String hql = "from Sanxiang where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Sanxiang> list = query.list();
		getSession().close();
		return list;
	}

	// 增
	public void insertSanxiang(Sanxiang s) {
		this.getHibernateTemplate().save(s);
	}

	// 改
	public void updateSanxiang(Sanxiang s) {
		this.getHibernateTemplate().update(s);
	}

	// 删
	public void deleteSanxiang(Sanxiang s) {
		this.getHibernateTemplate().delete(s);
	}

	// 查询所有
	public List<Sanxiang> findAllSanxiang() {
		try {
			String queryString = "from Sanxiang";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			throw re;
		}
	}

	// 获取项目信息List
	public List<Sanxiang> getSanxiangList(String findstr, int start, int limit) {
		String hql = "from Sanxiang where id is not null";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( project like '%" + strKey[i] + "%' or sumnum like '%" + strKey[i]
							+ "%' or danwei like '%" + strKey[i] + "%' )";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Sanxiang> list = query.list();
		getSession().close();
		return list;
	}

	// saftyjiancha表数据库操作*********************
	public List<Saftyjiancha> checkSaftyjianchaID(int id) {
		String hql = "from Saftyjiancha where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Saftyjiancha> list = query.list();
		getSession().close();
		return list;
	}

	// 增
	public void insertSaftyjiancha(Saftyjiancha s) {
		this.getHibernateTemplate().save(s);
	}

	// 改
	public void updateSaftyjiancha(Saftyjiancha s) {
		this.getHibernateTemplate().update(s);
	}

	// 删
	public void deleteSaftyjiancha(Saftyjiancha s) {
		this.getHibernateTemplate().delete(s);
	}

	// 根据查询条件获取List
	public List<Saftyjiancha> getSaftyjianchaList(String findstr, String type, int start, int limit) {
		String hql = "from Saftyjiancha where id is not null";
		hql += " and type like '%" + type + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and (jcperson like '%" + strKey[i] + "%' or jctime like '%" + strKey[i]
							+ "%' or jcresult like '%" + strKey[i] + "%')";

				}
			}
		}

		System.out.println(hql + "**********************");
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Saftyjiancha> list = query.list();
		getSession().close();
		return list;
	}

	public List<Saftyjiancha> getSaftyjianchaList(String findstr, int start, int limit, String projectName) {
		String hql = "from Saftyjiancha where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and (jcperson like '%" + strKey[i] + "%' or jctime like '%" + strKey[i]
							+ "%' or jcresult like '%" + strKey[i] + "%')";

				}
			}
		}

		System.out.println(hql + "**********************");
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Saftyjiancha> list = query.list();
		getSession().close();
		return list;
	}

	// Fenbaoplan表数据库操作*********************
	public List<Fenbaoplan> checkFenbaoplanID(int id) {
		String hql = "from Fenbaoplan where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Fenbaoplan> list = query.list();
		getSession().close();
		return list;
	}

	// 增
	public void insertFenbaoplan(Fenbaoplan s) {
		this.getHibernateTemplate().save(s);
	}

	// 改
	public void updateFenbaoplan(Fenbaoplan s) {
		this.getHibernateTemplate().update(s);
	}

	// 删
	public void deleteFenbaoplan(Fenbaoplan s) {
		this.getHibernateTemplate().delete(s);
	}

	public List<Fenbaoplan> getFenbaoplanList(String findstr, int start, int limit, String projectName) {
		String hql = "from Fenbaoplan where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and (fenbaoname like '%" + strKey[i] + "%' or planname like '%" + strKey[i]
							+ "%' or time like '%" + strKey[i] + "%')";

				}
			}
		}

		System.out.println(hql + "**********************");
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Fenbaoplan> list = query.list();
		getSession().close();
		return list;
	}

	// Fenbaosaftyjiancha表数据库操作*********************
	public List<Fenbaosaftyjiancha> checkFenbaosaftyjianchaID(int id) {
		String hql = "from Fenbaosaftyjiancha where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Fenbaosaftyjiancha> list = query.list();
		getSession().close();
		return list;
	}

	// 增
	public void insertFenbaosaftyjiancha(Fenbaosaftyjiancha s) {
		this.getHibernateTemplate().save(s);
	}

	// 改
	public void updateFenbaosaftyjiancha(Fenbaosaftyjiancha s) {
		this.getHibernateTemplate().update(s);
	}

	// 删
	public void deleteFenbaosaftyjiancha(Fenbaosaftyjiancha s) {
		this.getHibernateTemplate().delete(s);
	}

	public List<Fenbaosaftyjiancha> getFenbaosaftyjianchaList(String findstr, int start, int limit,
			String projectName) {
		String hql = "from Fenbaosaftyjiancha where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and (fenbaoname like '%" + strKey[i] + "%' or jcperson like '%" + strKey[i]
							+ "%' or jctime like '%" + strKey[i] + "%' or jcresult like '%" + strKey[i] + "%')";

				}
			}
		}

		System.out.println(hql + "**********************");
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Fenbaosaftyjiancha> list = query.list();
		getSession().close();
		return list;
	}

	// Saftycosttj1表数据库操作*********************
	public List<Saftycosttj1> checkSaftycosttj1ID(int id) {
		String hql = "from Saftycosttj1 where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Saftycosttj1> list = query.list();
		getSession().close();
		return list;
	}

	// 增
	public void insertSaftycosttj1(Saftycosttj1 s) {
		this.getHibernateTemplate().save(s);
	}

	// 改
	public void updateSaftycosttj1(Saftycosttj1 s) {
		this.getHibernateTemplate().update(s);
	}

	// 删
	public void deleteSaftycosttj1(Saftycosttj1 s) {
		this.getHibernateTemplate().delete(s);
	}

	public List<Saftycosttj1> getSaftycosttj1List(String findstr, int start, int limit, String projectName) {
		String hql = "from Saftycosttj1 where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and (year like '%" + strKey[i] + "%')";

				}
			}
		}

		System.out.println(hql + "**********************");
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Saftycosttj1> list = query.list();
		getSession().close();
		return list;
	}

	// Saftycosttj2表数据库操作*********************
	public List<Saftycosttj2> checkSaftycosttj2ID(int id) {
		String hql = "from Saftycosttj2 where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Saftycosttj2> list = query.list();
		getSession().close();
		return list;
	}

	// 增
	public void insertSaftycosttj2(Saftycosttj2 s) {
		this.getHibernateTemplate().save(s);
	}

	// 改
	public void updateSaftycosttj2(Saftycosttj2 s) {
		this.getHibernateTemplate().update(s);
	}

	// 删
	public void deleteSaftycosttj2(Saftycosttj2 s) {
		this.getHibernateTemplate().delete(s);
	}

	public List<Saftycosttj2> getSaftycosttj2List(String findstr, int start, int limit, String projectName) {
		String hql = "from Saftycosttj2 where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and (year like '%" + strKey[i] + "%')";

				}
			}
		}

		System.out.println(hql + "**********************");
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Saftycosttj2> list = query.list();
		getSession().close();
		return list;
	}

	// Saftycosttj3表数据库操作*********************
	public List<Saftycosttj3> checkSaftycosttj3ID(int id) {
		String hql = "from Saftycosttj3 where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Saftycosttj3> list = query.list();
		getSession().close();
		return list;
	}

	// 增
	public void insertSaftycosttj3(Saftycosttj3 s) {
		this.getHibernateTemplate().save(s);
	}

	// 改
	public void updateSaftycosttj3(Saftycosttj3 s) {
		this.getHibernateTemplate().update(s);
	}

	// 删
	public void deleteSaftycosttj3(Saftycosttj3 s) {
		this.getHibernateTemplate().delete(s);
	}

	public List<Saftycosttj3> getSaftycosttj3List(String findstr, int start, int limit, String projectName) {
		String hql = "from Saftycosttj3 where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and (year like '%" + strKey[i] + "%')";

				}
			}
		}

		System.out.println(hql + "**********************");
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Saftycosttj3> list = query.list();
		getSession().close();
		return list;
	}
}
