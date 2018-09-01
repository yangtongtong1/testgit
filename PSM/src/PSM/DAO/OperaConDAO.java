package PSM.DAO;

import java.util.List;

import org.hibernate.Query;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import hibernate.Constructionelec;
import hibernate.ElecEquip;
import hibernate.FireSafety;
import hibernate.ODHequipment;
import hibernate.PODgoods;
import hibernate.Proapproval;
import hibernate.Safecheckrec;
import hibernate.SpEquip;
import hibernate.Tezhongpeople;
import hibernate.Tezhongsbpeople;
import hibernate.TransportSafety;
import hibernate.Xiandongtai;

public class OperaConDAO extends HibernateDaoSupport {

	public int datacount;

	public List<Safecheckrec> checkSafecheckrecID(int id) {
		String hql = "from Safecheckrec where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Safecheckrec> list = query.list();
		getSession().close();
		return list;
	}

	/* 插入 */
	public void insertSafecheckrec(Safecheckrec p) {
		this.getHibernateTemplate().save(p);
	}

	/* 更新 */
	public void updateSafecheckrec(Safecheckrec p) {
		this.getHibernateTemplate().update(p);
	}

	/* 删除 */
	public void deleteSafecheckrec(Safecheckrec p) {
		this.getHibernateTemplate().delete(p);
	}

	// 获取项目信息List
	public List<Safecheckrec> getSafecheckrecList(String findstr, int start, int limit, String tableID,
			String projectName) {
		String hql = "from Safecheckrec where id is not null and tableID=" + tableID;
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( name like '%" + strKey[i] + "%' or type like '%" + strKey[i] + "%' or agent like '%"
							+ strKey[i] + "%' or registrant like '%" + strKey[i] + "%' or other like '%" + strKey[i]
							+ "%' )";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Safecheckrec> list = query.list();
		getSession().close();
		return list;
	}
	
	public void insertTezhongpeople(Tezhongpeople p) {
		this.getHibernateTemplate().save(p);
	}
	
	public void insertTezhongsbpeople(Tezhongsbpeople p) {
		this.getHibernateTemplate().save(p);
	}
	
	public void insertXiandongtai(Xiandongtai p) {
		this.getHibernateTemplate().save(p);
	}
	
	public void insertSpEquip(SpEquip p) {
		this.getHibernateTemplate().save(p);
	}
	
	public void insertElecEquip(ElecEquip p) {
		this.getHibernateTemplate().save(p);
	}

	public void insertFireSafety(FireSafety p) {
		this.getHibernateTemplate().save(p);
	}
	
	public void insertPODgoods(PODgoods p) {
		this.getHibernateTemplate().save(p);
	}
	
	public void insertODHequipment(ODHequipment p) {
		this.getHibernateTemplate().save(p);
	}
	
	public void insertProapproval(Proapproval p) {
		this.getHibernateTemplate().save(p);
	}
	
	public void insertConstructionelec(Constructionelec p) {
		this.getHibernateTemplate().save(p);
	}	

	public void insertTransportSafety(TransportSafety p) {
		this.getHibernateTemplate().save(p);
	}
}