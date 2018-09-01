package PSM.DAO;

import java.util.List;

import org.hibernate.Query;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import hibernate.Gongtitai;
import hibernate.Traintable;
import hibernate.Xiandongtai;

public class RunControlDAO extends HibernateDaoSupport {
	public int datacount;

	// *************Xiandongtai表的操作数据
	public List<Xiandongtai> checkXiandongtaiID(int id) {
		String hql = "from Xiandongtai where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Xiandongtai> list = query.list();
		getSession().close();
		return list;
	}

	// 增
	public void insertXiandongtai(Xiandongtai s) {
		this.getHibernateTemplate().save(s);
	}

	// 改
	public void updateXiandongtai(Xiandongtai s) {
		this.getHibernateTemplate().update(s);
	}

	// 删
	public void deleteXiandongtai(Xiandongtai s) {
		this.getHibernateTemplate().delete(s);
	}

	public List<Traintable> findTrainRecordByName(String findName, int id) {
		String hql = "from Traintable f WHERE f.tableId = '" + id + "' and f.employee  like '%" + findName + "%'";
		List list = this.getHibernateTemplate().find(hql);
		/*
		 * String hql = "select employee from Traintable where tableID = 120";
		 * List<String> list = this.getHibernateTemplate().find(hql);
		 */
		return list;
	}

	// 根据查询条件获取List
	public List<Xiandongtai> getXiandongtaiList(String findstr, int start, int limit, String projectName) {
		String hql = "from Xiandongtai where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and (repoter like '%" + strKey[i] + "%' or gangwei like '%" + strKey[i]
							+ "%' or name like '%" + strKey[i] + "%' or sfz like '%" + strKey[i]
							+ "%' or peixun like '%" + strKey[i] + "%' or sex like '%" + strKey[i]
							+ "%' or intimeplan like '%" + strKey[i] + "%' or intimereal like '%" + strKey[i]
							+ "%' or lvetimeplan like '%" + strKey[i] + "%' or lvetimereal like '%" + strKey[i]
							+ "%' or phone like '%" + strKey[i] + "%' or istijian like '%" + strKey[i]
							+ "%' or isgsbx like '%" + strKey[i] + "%')";
				}
			}
		}

		System.out.println(hql + "**********************");
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Xiandongtai> list = query.list();
		getSession().close();
		return list;
	}

	public List<Xiandongtai> getXiandongtaiListAll() {
		String hql = "from Xiandongtai where id is not null";

		System.out.println(hql + "**********************");
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		// query.setFirstResult(start);
		// query.setMaxResults(limit);
		List<Xiandongtai> list = query.list();
		getSession().close();
		return list;
	}

	// Gongtitai表的操作**********************************
	public List<Gongtitai> checkGongtitaiID(int id) {
		String hql = "from Gongtitai where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Gongtitai> list = query.list();
		getSession().close();
		return list;
	}

	// 增
	public void insertGongtitai(Gongtitai t) {
		this.getHibernateTemplate().save(t);
	}

	// 改
	public void updateGongtitai(Gongtitai t) {
		this.getHibernateTemplate().update(t);
	}

	// 删
	public void deleteGongtitai(Gongtitai t) {
		this.getHibernateTemplate().delete(t);
	}

	// 根据查询条件获取List
	public List<Gongtitai> getGongtitaiList(String findstr, int start, int limit, String projectName) {
		String hql = "from Gongtitai where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and (repoter like '%" + strKey[i] + "%' or gangwei like '%" + strKey[i]
							+ "%' or name like '%" + strKey[i] + "%' or sex like '%" + strKey[i]
							+ "%' or tijiantime like '%" + strKey[i] + "%' or tijianplace like '%" + strKey[i]
							+ "%' or tijianresult like '%" + strKey[i] + "%' or type like '%" + strKey[i] + "%')";
				}
			}
		}

		System.out.println(hql + "**********************");
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Gongtitai> list = query.list();
		getSession().close();
		return list;
	}
}
