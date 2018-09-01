package PSM.DAO;

import java.util.List;

import org.hibernate.Query;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import hibernate.ElecEquip;
import hibernate.Managepara;
import hibernate.SpEquip;
import hibernate.Yingjifenbao;
import hibernate.Yingjijyzz;
import hibernate.Yingjipxyl;
import hibernate.Yingjiyuan;

public class EmeRescueDAO extends HibernateDaoSupport {

	public int datacount;

	public List<Managepara> checkManageparaID(int id) {
		String hql = "from Managepara where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Managepara> list = query.list();
		getSession().close();
		return list;
	}

	public SpEquip getSpEquip(int id) {
		String hql = "from SpEquip where id=" + id;
		Query query = getSession().createQuery(hql);
		List<SpEquip> list = query.list();
		getSession().close();
		if (list.size() == 0)
			return null;
		return list.get(0);
	}

	public ElecEquip getElecEquip(int id) {
		String hql = "from ElecEquip where id=" + id;
		Query query = getSession().createQuery(hql);
		List<ElecEquip> list = query.list();
		getSession().close();
		if (list.size() == 0)
			return null;
		return list.get(0);
	}

	/* 插入 */
	public void insertManagepara(Managepara p) {
		this.getHibernateTemplate().save(p);
	}

	public void insertSpEquip(SpEquip p) {
		this.getHibernateTemplate().save(p);
	}

	public void insertElecEquip(ElecEquip p) {
		this.getHibernateTemplate().save(p);
	}

	/* 更新 */
	public void updateManagepara(Managepara p) {
		this.getHibernateTemplate().update(p);
	}

	public void updateSpEquip(SpEquip p) {
		this.getHibernateTemplate().update(p);
	}

	public void updateElecEquip(ElecEquip p) {
		this.getHibernateTemplate().update(p);
	}

	/* 删除 */
	public void deleteManagepara(Managepara p) {
		this.getHibernateTemplate().delete(p);
	}

	public void deleteSpEquip(SpEquip p) {
		this.getHibernateTemplate().delete(p);
	}

	public void deleteElecEquip(ElecEquip p) {
		this.getHibernateTemplate().delete(p);
	}

	// 获取项目信息List
	public List<Managepara> getManageparaList(String findstr, int start, int limit, String tableID,
			String projectName) {
		String hql = "from Managepara where id is not null and tableID=" + tableID;
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( Content like '%" + strKey[i] + "%' or Type like '%" + strKey[i]
							+ "%' or Quantity like '%" + strKey[i] + "%' or Unit like '%" + strKey[i]
							+ "%' or State like '%" + strKey[i] + "%' or Place like '%" + strKey[i]+ "%' or Fbunit like '%" + strKey[i] 
							+ "%' or Responsible like '%" + strKey[i] + "%' )";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Managepara> list = query.list();
		getSession().close();
		return list;
	}

	public List<SpEquip> getSpEquipList(String findstr, int start, int limit, String projectName) {
		String hql = "from SpEquip where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and (name like '%" + strKey[i] + "%' or type like '%" + strKey[i]
							+ "%' or registNo like '%" + strKey[i] + "%' or kind like '%" + strKey[i]
							+ "%' or manuUnit like '%" + strKey[i] + "%' or installUnit like '%" + strKey[i]
							+ "%' or checkStatus like '%" + strKey[i] + "%' or useStatus like '%" + strKey[i]
							+ "%' or majorStatus like '%" + strKey[i] + "%' or otherStatus like '%" + strKey[i]
							+ "%' )";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<SpEquip> list = query.list();
		getSession().close();
		return list;
	}

	public List<ElecEquip> getElecEquipList(String findstr, int start, int limit, String projectName) {
		String hql = "from ElecEquip where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( equipNo like '%" + strKey[i] + "%' or name like '%" + strKey[i]
							+ "%' or type like '%" + strKey[i] + "%' or manuUnit like '%" + strKey[i]
							+ "%' or quantity like '%" + strKey[i] + "%' or unit like '%" + strKey[i]
							+ "%' or purpose like '%" + strKey[i] + "%' or registNo like '%" + strKey[i]
							+ "%' or usePlace like '%" + strKey[i] + "%' or responser like '%" + strKey[i] + "%' )";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<ElecEquip> list = query.list();
		getSession().close();
		return list;
	}

	// **********Yingjijyzz**********
	public List<Yingjijyzz> checkYingjijyzzID(int id) {
		String hql = "from Yingjijyzz where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Yingjijyzz> list = query.list();
		getSession().close();
		return list;
	}

	// 增
	public void insertYingjijyzz(Yingjijyzz s) {
		this.getHibernateTemplate().save(s);
	}

	// 改
	public void updateYingjijyzz(Yingjijyzz s) {
		this.getHibernateTemplate().update(s);
	}

	// 删
	public void deleteYingjijyzz(Yingjijyzz s) {
		this.getHibernateTemplate().delete(s);
	}

	// 根据查询条件获取List
	public List<Yingjijyzz> getYingjijyzzList(String findstr, int start, int limit, String projectName) {
		String hql = "from Yingjijyzz where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and (zuizhiname like '%" + strKey[i] + "%' or clortz like '%" + strKey[i]
							+ "%' or clortztime like '%" + strKey[i] + "%' or fuzeren like '%" + strKey[i]
							+ "%' or chengyuan like '%" + strKey[i] + "%' or gongzuojg like '%" + strKey[i] + "%')";
				}
			}
		}
		// System.out.println(hql + "**********************");
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Yingjijyzz> list = query.list();
		getSession().close();
		return list;
	}

	// **********Yingjipxyl**********
	public List<Yingjipxyl> checkYingjipxylID(int id) {
		String hql = "from Yingjipxyl where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Yingjipxyl> list = query.list();
		getSession().close();
		return list;
	}

	// 增
	public void insertYingjipxyl(Yingjipxyl s) {
		this.getHibernateTemplate().save(s);
	}

	// 改
	public void updateYingjipxyl(Yingjipxyl s) {
		this.getHibernateTemplate().update(s);
	}

	// 删
	public void deleteYingjipxyl(Yingjipxyl s) {
		this.getHibernateTemplate().delete(s);
	}

	// 根据查询条件获取List
	public List<Yingjipxyl> getYingjipxylList(String findstr, int start, int limit, String projectName) {
		String hql = "from Yingjipxyl where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and (content like '%" + strKey[i] + "%' or peixuntime like '%" + strKey[i] + "%')";
				}
			}
		}
		// System.out.println(hql + "**********************");
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Yingjipxyl> list = query.list();
		getSession().close();
		return list;
	}

	// *************Yingjifenbao表的操作数据
	public List<Yingjifenbao> checkYingjifenbaoID(int id) {
		String hql = "from Yingjifenbao where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Yingjifenbao> list = query.list();
		getSession().close();
		return list;
	}

	// 增
	public void insertYingjifenbao(Yingjifenbao s) {
		this.getHibernateTemplate().save(s);
	}

	// 改
	public void updateYingjifenbao(Yingjifenbao s) {
		this.getHibernateTemplate().update(s);
	}

	// 删
	public void deleteYingjifenbao(Yingjifenbao s) {
		this.getHibernateTemplate().delete(s);
	}

	// 根据查询条件获取List
	public List<Yingjifenbao> getYingjifenbaoList(String findstr, String type, int start, int limit,
			String projectName) {
		String hql = "from Yingjifenbao where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		hql += " and type like '%" + type + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and (fenbaoname like '%" + strKey[i] + "%' or uploadtime like '%" + strKey[i] + "%')";
				}
			}
		}

		System.out.println(hql + "**********************");
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Yingjifenbao> list = query.list();
		getSession().close();
		return list;
	}

	// **********Yingjiyuan**********
	public List<Yingjiyuan> checkYingjiyuanID(int id) {
		String hql = "from Yingjiyuan where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Yingjiyuan> list = query.list();
		getSession().close();
		return list;
	}

	// 增
	public void insertYingjiyuan(Yingjiyuan s) {
		this.getHibernateTemplate().save(s);
	}

	// 改
	public void updateYingjiyuan(Yingjiyuan s) {
		this.getHibernateTemplate().update(s);
	}

	// 删
	public void deleteYingjiyuan(Yingjiyuan s) {
		this.getHibernateTemplate().delete(s);
	}

	// 根据查询条件获取List
	public List<Yingjiyuan> getYingjiyuanList(String findstr, int start, int limit, String projectName) {
		String hql = "from Yingjiyuan where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and (zuizhiname like '%" + strKey[i] + "%' or bianzhiren like '%" + strKey[i]
							+ "%' or bianzhitime like '%" + strKey[i] + "%' or shenheren like '%" + strKey[i]
							+ "%' or shenhetime like '%" + strKey[i] + "%' or pizhunren like '%" + strKey[i]
							+ "%' or pizhuntime like '%" + strKey[i] + "%')";
				}
			}
		}
		// System.out.println(hql + "**********************");
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Yingjiyuan> list = query.list();
		getSession().close();
		return list;
	}
}