package PSM.DAO;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import hibernate.ReadilyShoot;
import hibernate.Weixianyuan;
import hibernate.Yinhuanpaicha;

public class HiddenTroubleSolutionDAO extends HibernateDaoSupport {
	public int datacount;

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

	public List<Weixianyuan> checkWeixianyuanID(int id) {
		String hql = "from Weixianyuan where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Weixianyuan> list = query.list();
		getSession().close();
		return list;
	}

	/* ���� */
	public void insertWeixianyuan(Weixianyuan p) {
		this.getHibernateTemplate().save(p);
	}

	/* ���� */
	public void updateWeixianyuan(Weixianyuan p) {
		this.getHibernateTemplate().update(p);
	}

	/* ɾ�� */
	public void deleteWeixianyuan(Weixianyuan p) {
		this.getHibernateTemplate().delete(p);
	}

	// ��ȡ��Ŀ��ϢList private Integer id;
	public List<Weixianyuan> getWeixianyuanList(String findstr, int start, int limit, String projectName) {
		String hql = "from Weixianyuan where id is not null";
		hql += " and projectName like '%" + projectName + "%'";

		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( jobTime like '%" + strKey[i] + "%' or jobLocation like '%" + strKey[i]
							+ "%' or jobContent like '%" + strKey[i] + "%' or mainRisk like '%"
							+ "%' or riskRank like '%" + strKey[i] + "%' or preAction like '%" + "%' or jobMan like '%"
							+ strKey[i] + "%' or jobJiandu like '%" + strKey[i] + "%' )";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Weixianyuan> list = query.list();
		getSession().close();
		return list;
	}

	public List<Yinhuanpaicha> checkYinhuanpaichaID(int id) {
		String hql = "from Yinhuanpaicha where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Yinhuanpaicha> list = query.list();
		getSession().close();
		return list;
	}

	/* ���� */
	public void insertYinhuanpaicha(Yinhuanpaicha p) {
		this.getHibernateTemplate().save(p);
	}

	/* ���� */
	public void updateYinhuanpaicha(Yinhuanpaicha p) {
		this.getHibernateTemplate().update(p);
	}

	/* ɾ�� */
	public void deleteYinhuanpaicha(Yinhuanpaicha p) {
		this.getHibernateTemplate().delete(p);
	}

	// ��ȡ��Ŀ��ϢList
	public List<Yinhuanpaicha> getYinhuanpaichaList(String findstr, int start, int limit) {
		String hql = "from Yinhuanpaicha where id is not null";

		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( head like '%" + strKey[i] + "%' or viceHead like '%" + strKey[i]
							+ "%' or form like '%" + strKey[i] + "%' or agency like '%" + strKey[i] + "%' )";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Yinhuanpaicha> list = query.list();
		getSession().close();
		return list;
	}

	public List<ReadilyShoot> getReadilyShootList(String projectName, String findstr, int limit, int start) {
		String hql = "from ReadilyShoot where id is not null";
		if (!projectName.equals("全部项目"))
			hql += " and project='" + projectName + "'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++)
				if (strKey[i].length() > 0)
					hql += " and ( position like '%" + strKey[i] + "%' or comment like '%" + strKey[i] + "%')";
		}
		Query query = getSession().createQuery(hql);
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<ReadilyShoot> list = query.list();
		getSession().close();
		return list;
	}

	public ReadilyShoot getReadilyShoot(int id) {
		String hql = "from ReadilyShoot where id=" + id;
		Query query = getSession().createQuery(hql);
		List<ReadilyShoot> list = query.list();
		getSession().close();
		if (list.size() == 0)
			return null;
		return list.get(0);
	}

	public void deleteReadilyShoot(ReadilyShoot p) {
		this.getHibernateTemplate().delete(p);
	}

}
