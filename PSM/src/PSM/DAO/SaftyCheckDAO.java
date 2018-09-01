package PSM.DAO;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import hibernate.Fenbaoyinhuanpczlgzfa;
import hibernate.Projectmanagement;
import hibernate.Riskfenbao;
import hibernate.Riskprodanger;
import hibernate.Risksafepg;
import hibernate.Saftycheck;
import hibernate.Saftycheckplan;
import hibernate.Saftycheckyearplan;
import hibernate.Saftycheckyearplanfb;
import hibernate.Saftycheckyinhuanpc;
import hibernate.Saftycostplan;
import hibernate.Saftyproblem;
import hibernate.Taizhang;
import hibernate.Taizhangfb;

public class SaftyCheckDAO extends HibernateDaoSupport {
	public int datacount;

	// *************saftycheck表的操作数据
	public List<Saftycheck> checkSaftycheckID(int id) {
		String hql = "from Saftycheck where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Saftycheck> list = query.list();
		getSession().close();
		return list;
	}

	// ----------------LZZ----------------//
	public List<Taizhang> checkTaizhangCheckID(int id) {
		String hql = "from Taizhang where checkId=" + id;
		Query query = getSession().createQuery(hql);
		List<Taizhang> list = query.list();
		getSession().close();
		return list;
	}

	// 增
	public void insertSaftycheck(Saftycheck s) {
		this.getHibernateTemplate().save(s);
	}

	// 改
	public void updateSaftycheck(Saftycheck s) {
		this.getHibernateTemplate().update(s);
	}

	// 删
	public void deleteSaftycheck(Saftycheck s) {
		this.getHibernateTemplate().delete(s);
	}

	// 根据查询条件获取List
	public List<Saftycheck> getSaftycheckList(String findstr, String type, int start, int limit, String projectName) {
		String hql = "from Saftycheck where id is not null";
		hql += " and type like '%" + type + "%'";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and (checktime like '%" + strKey[i] + "%' or checktype like '%" + strKey[i]
							+ "%' or checkunit like '%" + strKey[i] + "%' or shoujianunit like '%" + strKey[i]
							+ "%' or checkperson like '%" + strKey[i] + "%' or noticeandnum like '%" + strKey[i]
							+ "%' or prokind like '%" + strKey[i] + "%' or problem like '%" + strKey[i]
							+ "%' or prodegree like '%" + strKey[i] + "%' or advice like '%" + strKey[i]
							+ "%' or timeline like '%" + strKey[i] + "%' or iscorrective like '%" + strKey[i]
							+ "%' or replytime like '%" + strKey[i] + "%' or last like '%" + strKey[i]
							+ "%' or content like '%" + strKey[i] + "%' or replyandnum like '%" + strKey[i]
							+ "%' or type like '%" + strKey[i] + "%')";
				}
			}
		}

		System.out.println(hql + "**********************");
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Saftycheck> list = query.list();
		getSession().close();
		return list;
	}

	public List<Saftycheck> getSaftycheckListAll() {
		String hql = "from Saftycheck where id is not null";

		System.out.println(hql + "**********************");
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		// query.setFirstResult(start);
		// query.setMaxResults(limit);
		List<Saftycheck> list = query.list();
		getSession().close();
		return list;
	}

	// Taizhang表的操作**********************************
	public List<Taizhang> checkTaizhangID(int id) {
		String hql = "from Taizhang where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Taizhang> list = query.list();
		getSession().close();
		return list;
	}

	// 增
	public void insertTaizhang(Taizhang t) {
		this.getHibernateTemplate().save(t);
	}

	// 改
	public void updateTaizhang(Taizhang t) {
		this.getHibernateTemplate().update(t);
	}

	// 删
	public void deleteTaizhang(Taizhang t) {
		this.getHibernateTemplate().delete(t);
	}

	// 根据查询条件获取List
	public List<Taizhang> getTaizhangList(String findstr, int start, int limit, String projectName) {
		String hql = "from Taizhang where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		hql+=" order by ID desc";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and (No like '%" + strKey[i] + "%' or problem like '%" + strKey[i]
							+ "%' or location like '%" + strKey[i] + "%' or checkperson like '%" + strKey[i]
							+ "%' or prolevel like '%" + strKey[i] + "%' or correction like '%" + strKey[i]
							+ "%' or solvedep like '%" + strKey[i] + "%' or solvePerson like '%" + strKey[i]
							+ "%' or expTime like '%" + strKey[i] + "%' or correctionfee like '%" + strKey[i]
							+ "%' or solveExp like '%" + strKey[i] + "%' or solveTime like '%" + strKey[i]
							+ "%' or supperson like '%" + strKey[i] + "%' or prevent like '%" + strKey[i] + "%')";
				}
			}
		}

		System.out.println(hql + "**********************");
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Taizhang> list = query.list();
		getSession().close();
		return list;
	}
	
	// 根据查询条件获取List
		public List<Taizhang> getTaizhangListCheckID(String findstr, int start, int limit, String projectName,int checkID) {
			String hql = "from Taizhang where id is not null";
			hql += " and projectName like '%" + projectName + "%'";
			hql += " and checkId like '%" + checkID + "%'";
			hql+=" order by ID desc";
			if (findstr != null || findstr.length() > 0) {
				String[] strKey = findstr.split(",");
				for (int i = 0; i < strKey.length; i++) {
					if (strKey[i].length() > 0) {
						hql += " and (No like '%" + strKey[i] + "%' or problem like '%" + strKey[i]
								+ "%' or location like '%" + strKey[i] + "%' or checkperson like '%" + strKey[i]
								+ "%' or prolevel like '%" + strKey[i] + "%' or correction like '%" + strKey[i]
								+ "%' or solvedep like '%" + strKey[i] + "%' or solvePerson like '%" + strKey[i]
								+ "%' or expTime like '%" + strKey[i] + "%' or correctionfee like '%" + strKey[i]
								+ "%' or solveExp like '%" + strKey[i] + "%' or solveTime like '%" + strKey[i]
								+ "%' or supperson like '%" + strKey[i] + "%' or prevent like '%" + strKey[i] + "%')";
					}
				}
			}

			System.out.println(hql + "**********************");
			Query query = getSession().createQuery(hql);
			datacount = query.list().size();
			query.setFirstResult(start);
			query.setMaxResults(limit);
			List<Taizhang> list = query.list();
			getSession().close();
			return list;
		}
	

	// **********Saftycheckplan**********
	public List<Saftycheckplan> checkSaftycheckplanID(int id) {
		String hql = "from Saftycheckplan where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Saftycheckplan> list = query.list();
		getSession().close();
		return list;
	}

	// 增
	public void insertSaftycheckplan(Saftycheckplan s) {
		this.getHibernateTemplate().save(s);
	}

	// 改
	public void updateSaftycheckplan(Saftycheckplan s) {
		this.getHibernateTemplate().update(s);
	}

	// 删
	public void deleteSaftycheckplan(Saftycheckplan s) {
		this.getHibernateTemplate().delete(s);
	}

	// 根据查询条件获取List
	public List<Saftycheckplan> getSaftycheckplanList(String findstr, int start, int limit, String projectName) {
		String hql = "from Saftycheckplan where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and (bzren like '%" + strKey[i] + "%' or bztime like '%" + strKey[i]
							+ "%' or spren like '%" + strKey[i] + "%' or sptime like '%" + strKey[i] + "%')";
				}
			}
		}

		System.out.println(hql + "**********************");
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Saftycheckplan> list = query.list();
		getSession().close();
		return list;
	}

	// **********Saftycheckyearplan**********
	public List<Saftycheckyearplan> checkSaftycheckyearplanID(int id) {
		String hql = "from Saftycheckyearplan where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Saftycheckyearplan> list = query.list();
		getSession().close();
		return list;
	}

	// 增
	public void insertSaftycheckyearplan(Saftycheckyearplan s) {
		this.getHibernateTemplate().save(s);
	}

	// 改
	public void updateSaftycheckyearplan(Saftycheckyearplan s) {
		this.getHibernateTemplate().update(s);
	}

	// 删
	public void deleteSaftycheckyearplan(Saftycheckyearplan s) {
		this.getHibernateTemplate().delete(s);
	}

	// 根据查询条件获取List
	public List<Saftycheckyearplan> getSaftycheckyearplanList(String findstr, int start, int limit,
			String projectName) {
		String hql = "from Saftycheckyearplan where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and (bzren like '%" + strKey[i] + "%' or bztime like '%" + strKey[i]
							+ "%' or spren like '%" + strKey[i] + "%' or sptime like '%" + strKey[i] + "%')";
				}
			}
		}

		System.out.println(hql + "**********************");
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Saftycheckyearplan> list = query.list();
		getSession().close();
		return list;
	}

	// **********Saftycheckyearplanfb**********
	public List<Saftycheckyearplanfb> checkSaftycheckyearplanfbID(int id) {
		String hql = "from Saftycheckyearplanfb where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Saftycheckyearplanfb> list = query.list();
		getSession().close();
		return list;
	}

	// 增
	public void insertSaftycheckyearplanfb(Saftycheckyearplanfb s) {
		this.getHibernateTemplate().save(s);
	}

	// 改
	public void updateSaftycheckyearplanfb(Saftycheckyearplanfb s) {
		this.getHibernateTemplate().update(s);
	}

	// 删
	public void deleteSaftycheckyearplanfb(Saftycheckyearplanfb s) {
		this.getHibernateTemplate().delete(s);
	}

	// 根据查询条件获取List
	public List<Saftycheckyearplanfb> getSaftycheckyearplanfbList(String findstr, int start, int limit,
			String projectName) {
		String hql = "from Saftycheckyearplanfb where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and (bzunit like '%" + strKey[i] + "%' or bztime like '%" + strKey[i]
							+ "%' or bbren like '%" + strKey[i] + "%' or bbtime like '%" + strKey[i]
							+ "%' or spren like '%" + strKey[i] + "%' or sptime like '%" + strKey[i] + "%')";
				}
			}
		}

		System.out.println(hql + "**********************");
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Saftycheckyearplanfb> list = query.list();
		getSession().close();
		return list;
	}

	// **********Saftycheckyinhuanpc**********
	public List<Saftycheckyinhuanpc> checkSaftycheckyinhuanpcID(int id) {
		String hql = "from Saftycheckyinhuanpc where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Saftycheckyinhuanpc> list = query.list();
		getSession().close();
		return list;
	}

	// 增
	public void insertSaftycheckyinhuanpc(Saftycheckyinhuanpc s) {
		this.getHibernateTemplate().save(s);
	}

	// 改
	public void updateSaftycheckyinhuanpc(Saftycheckyinhuanpc s) {
		this.getHibernateTemplate().update(s);
	}

	// 删
	public void deleteSaftycheckyinhuanpc(Saftycheckyinhuanpc s) {
		this.getHibernateTemplate().delete(s);
	}

	// 根据查询条件获取List
	public List<Saftycheckyinhuanpc> getSaftycheckyinhuanpcList(String findstr, int start, int limit,
			String projectName) {
		String hql = "from Saftycheckyinhuanpc where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and (year like '%" + strKey[i] + "%' or uploaduser like '%" + strKey[i]
							+ "%' or uploadtime like '%" + "%')";
				}
			}
		}

		System.out.println(hql + "**********************");
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Saftycheckyinhuanpc> list = query.list();
		getSession().close();
		return list;
	}

	// **********Taizhangfb**********
	public List<Taizhangfb> checkcTaizhangfbID(int id) {
		String hql = "from Taizhangfb where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Taizhangfb> list = query.list();
		getSession().close();
		return list;
	}

	// 增
	public void insertTaizhangfb(Taizhangfb s) {
		this.getHibernateTemplate().save(s);
	}

	// 改
	public void updateTaizhangfb(Taizhangfb s) {
		this.getHibernateTemplate().update(s);
	}

	// 删
	public void deleteTaizhangfb(Taizhangfb s) {
		this.getHibernateTemplate().delete(s);
	}

	// 根据查询条件获取List
	public List<Taizhangfb> getTaizhangfbList(String findstr, int start, int limit, String projectName) {
		String hql = "from Taizhangfb where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and (year fbname '%" + strKey[i] + "%' or year like '%" + strKey[i] + "%' or month like '%"
							+ strKey[i] + "%' or uploadtime like '%" + "%')";
				}
			}
		}

		System.out.println(hql + "**********************");
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Taizhangfb> list = query.list();
		getSession().close();
		return list;
	}

	// **********Riskfenbao**********
	public List<Riskfenbao> checkRiskfenbaoID(int id) {
		String hql = "from Riskfenbao where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Riskfenbao> list = query.list();
		getSession().close();
		return list;
	}

	// 增
	public void insertRiskfenbao(Riskfenbao s) {
		this.getHibernateTemplate().save(s);
	}

	// 改
	public void updateRiskfenbao(Riskfenbao s) {
		this.getHibernateTemplate().update(s);
	}

	// 删
	public void deleteRiskfenbao(Riskfenbao s) {
		this.getHibernateTemplate().delete(s);
	}

	// 根据查询条件获取List
	public List<Riskfenbao> getRiskfenbaoList(String findstr, int start, int limit, String projectName) {
		String hql = "from Riskfenbao where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and (year fenbaoname '%" + strKey[i] + "%' or bbtime like '%" + strKey[i] + "%')";
				}
			}
		}
		System.out.println(hql + "**********************");
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Riskfenbao> list = query.list();
		getSession().close();
		return list;
	}

	// **********Riskprodanger**********
	public List<Riskprodanger> checkRiskprodangerID(int id) {
		String hql = "from Riskfenbao where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Riskprodanger> list = query.list();
		getSession().close();
		return list;
	}

	// 增
	public void insertRiskprodanger(Riskprodanger s) {
		this.getHibernateTemplate().save(s);
	}

	// 改
	public void updateRiskprodanger(Riskprodanger s) {
		this.getHibernateTemplate().update(s);
	}

	// 删
	public void deleteRiskprodanger(Riskprodanger s) {
		this.getHibernateTemplate().delete(s);
	}

	// 根据查询条件获取List
	public List<Riskprodanger> getRiskprodangerList(String findstr, int start, int limit, String projectName) {
		String hql = "from Riskprodanger where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and (year bstype '%" + strKey[i] + "%' or bstime like '%" + strKey[i] + "%')";
				}
			}
		}
		System.out.println(hql + "**********************");
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Riskprodanger> list = query.list();
		getSession().close();
		return list;
	}

	// **********Risksafepg**********
	public List<Risksafepg> checkRisksafepgID(int id) {
		String hql = "from Risksafepg where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Risksafepg> list = query.list();
		getSession().close();
		return list;
	}

	// 增
	public void insertRisksafepg(Risksafepg s) {
		this.getHibernateTemplate().save(s);
	}

	// 改
	public void updateRisksafepg(Risksafepg s) {
		this.getHibernateTemplate().update(s);
	}

	// 删
	public void deleteRisksafepg(Risksafepg s) {
		this.getHibernateTemplate().delete(s);
	}

	// 根据查询条件获取List
	public List<Risksafepg> getRisksafepgList(String findstr, int start, int limit, String projectName) {
		String hql = "from Risksafepg where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and (shigstage bstype '%" + strKey[i] + "%' or safeptime like '%" + strKey[i]
							+ "%' or bzperson like '%" + strKey[i] + "%' or shperson like '%" + strKey[i] + "%')";
				}
			}
		}
		// System.out.println(hql + "**********************");
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Risksafepg> list = query.list();
		getSession().close();
		return list;
	}

	// **********Fenbaoyinhuanpczlgzfa**********
	public List<Fenbaoyinhuanpczlgzfa> checkFenbaoyinhuanpczlgzfaID(int id) {
		String hql = "from Fenbaoyinhuanpczlgzfa where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Fenbaoyinhuanpczlgzfa> list = query.list();
		getSession().close();
		return list;
	}

	// 增
	public void insertFenbaoyinhuanpczlgzfa(Fenbaoyinhuanpczlgzfa s) {
		this.getHibernateTemplate().save(s);
	}

	// 改
	public void updateFenbaoyinhuanpczlgzfa(Fenbaoyinhuanpczlgzfa s) {
		this.getHibernateTemplate().update(s);
	}

	// 删
	public void deleteFenbaoyinhuanpczlgzfa(Fenbaoyinhuanpczlgzfa s) {
		this.getHibernateTemplate().delete(s);
	}

	// 根据查询条件获取List
	public List<Fenbaoyinhuanpczlgzfa> getFenbaoyinhuanpczlgzfaList(String findstr, int start, int limit,
			String projectName) {
		String hql = "from Fenbaoyinhuanpczlgzfa where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and (year bstype '%" + strKey[i] + "%' or fenbaoname like '%" + strKey[i]
							+ "%' or workname like '%" + strKey[i] + "%' or filename like '%" + strKey[i]
							+ "%' or uploadtime like '%" + "%')";
				}
			}
		}
		// System.out.println(hql + "**********************");
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Fenbaoyinhuanpczlgzfa> list = query.list();
		getSession().close();
		return list;
	}

	// jlf=================
	public List<Saftycheck> getSaftycheck326List(String findstr, int start, int limit) {
		String hql = "from Saftycheck where id is not null";
		hql += " and type like '专项安全检查'";
		hql += " and checktype like '总包目标专项检查'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and (checktime like '%" + strKey[i] + "%' or checktype like '%" + strKey[i]
							+ "%' or checkunit like '%" + strKey[i] + "%' or shoujianunit like '%" + strKey[i]
							+ "%' or checkperson like '%" + strKey[i] + "%' or noticeandnum like '%" + strKey[i]
							+ "%' or prokind like '%" + strKey[i] + "%' or problem like '%" + strKey[i]
							+ "%' or prodegree like '%" + strKey[i] + "%' or advice like '%" + strKey[i]
							+ "%' or timeline like '%" + strKey[i] + "%' or iscorrective like '%" + strKey[i]
							+ "%' or replytime like '%" + strKey[i] + "%' or last like '%" + strKey[i]
							+ "%' or content like '%" + strKey[i] + "%' or replyandnum like '%" + strKey[i]
							+ "%' or type like '%" + strKey[i] + "%')";
				}
			}
		}

		System.out.println(hql + "**********************");
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Saftycheck> list = query.list();
		getSession().close();
		return list;
	}

	public List<Saftycheck> getSaftycheck327List(String findstr, int start, int limit) {
		String hql = "from Saftycheck where id is not null";
		hql += " and type like '专项安全检查'";
		hql += " and checktype like '分包目标专项检查'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and (checktime like '%" + strKey[i] + "%' or checktype like '%" + strKey[i]
							+ "%' or checkunit like '%" + strKey[i] + "%' or shoujianunit like '%" + strKey[i]
							+ "%' or checkperson like '%" + strKey[i] + "%' or noticeandnum like '%" + strKey[i]
							+ "%' or prokind like '%" + strKey[i] + "%' or problem like '%" + strKey[i]
							+ "%' or prodegree like '%" + strKey[i] + "%' or advice like '%" + strKey[i]
							+ "%' or timeline like '%" + strKey[i] + "%' or iscorrective like '%" + strKey[i]
							+ "%' or replytime like '%" + strKey[i] + "%' or last like '%" + strKey[i]
							+ "%' or content like '%" + strKey[i] + "%' or replyandnum like '%" + strKey[i]
							+ "%' or type like '%" + strKey[i] + "%')";
				}
			}
		}

		System.out.println(hql + "**********************");
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Saftycheck> list = query.list();
		getSession().close();
		return list;
	}

	public List<Taizhang> getTaizhangList(String findstr, String type, int start, int limit, String projectName) {
		String hql = "from Taizhang where id is not null";
		hql += " and type like '%" + type + "%'";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and (No like '%" + strKey[i] + "%' or problem like '%" + strKey[i]
							+ "%' or location like '%" + strKey[i] + "%' or checkperson like '%" + strKey[i]
							+ "%' or prolevel like '%" + strKey[i] + "%' or correction like '%" + strKey[i]
							+ "%' or solvedep like '%" + strKey[i] + "%' or solvePerson like '%" + strKey[i]
							+ "%' or expTime like '%" + strKey[i] + "%' or correctionfee like '%" + strKey[i]
							+ "%' or solveExp like '%" + strKey[i] + "%' or solveTime like '%" + strKey[i]
							+ "%' or supperson like '%" + strKey[i] + "%' or prevent like '%" + strKey[i] + "%')";
				}
			}
		}

		System.out.println(hql + "**********************");
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Taizhang> list = query.list();
		getSession().close();
		return list;
	}

	// ==================
	// 直方图显示统计
	// 根据查询条件获取List
	public List<Saftyproblem> getSaftyproblemList(String findstr, int start, int limit) {
		String hql = "from Saftyproblem where id is not null";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and (checktime like '%" + strKey[i] + "%' or checktype like '%" + strKey[i]
							+ "%' or checkunit like '%" + strKey[i] + "%' or shoujianunit like '%" + strKey[i]
							+ "%' or checkperson like '%" + strKey[i] + "%' or noticeandnum like '%" + strKey[i]
							+ "%' or prokind like '%" + strKey[i] + "%' or problem like '%" + strKey[i]
							+ "%' or prodegree like '%" + strKey[i] + "%' or advice like '%" + strKey[i]
							+ "%' or timeline like '%" + strKey[i] + "%' or iscorrective like '%" + strKey[i]
							+ "%' or replytime like '%" + strKey[i] + "%' or last like '%" + strKey[i]
							+ "%' or content like '%" + strKey[i] + "%' or replyandnum like '%" + strKey[i]
							+ "%' or type like '%" + strKey[i] + "%')";
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

	public List<Saftyproblem> getSaftyproblemList(String findstr, int start, int limit, String kind) {
		String hql = "from Saftyproblem where id is not null";
		hql += " and kind like '%" + kind + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and (checktime like '%" + strKey[i] + "%' or checktype like '%" + strKey[i]
							+ "%' or checkunit like '%" + strKey[i] + "%' or shoujianunit like '%" + strKey[i]
							+ "%' or checkperson like '%" + strKey[i] + "%' or noticeandnum like '%" + strKey[i]
							+ "%' or prokind like '%" + strKey[i] + "%' or problem like '%" + strKey[i]
							+ "%' or prodegree like '%" + strKey[i] + "%' or advice like '%" + strKey[i]
							+ "%' or timeline like '%" + strKey[i] + "%' or iscorrective like '%" + strKey[i]
							+ "%' or replytime like '%" + strKey[i] + "%' or last like '%" + strKey[i]
							+ "%' or content like '%" + strKey[i] + "%' or replyandnum like '%" + strKey[i]
							+ "%' or type like '%" + strKey[i] + "%')";
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
	
	
	public List<Saftycheck> getSaftycheckList2(String findstr, String type, int start, int limit, String projectName,
			String startdate,String enddate) {
		String hql = "from Saftycheck where id is not null";
		hql += " and type like '%" + type + "%'";
		hql += " and projectName like '%" + projectName + "%'";
		hql += " and checktime>='" + startdate + "'and checktime<='" + enddate+"'";
//		datetime >=  between
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and (checktime like '%" + strKey[i] + "%' or checktype like '%" + strKey[i]
							+ "%' or checkunit like '%" + strKey[i] + "%' or shoujianunit like '%" + strKey[i]
							+ "%' or checkperson like '%" + strKey[i] + "%' or noticeandnum like '%" + strKey[i]
							+ "%' or prokind like '%" + strKey[i] + "%' or problem like '%" + strKey[i]
							+ "%' or prodegree like '%" + strKey[i] + "%' or advice like '%" + strKey[i]
							+ "%' or timeline like '%" + strKey[i] + "%' or iscorrective like '%" + strKey[i]
							+ "%' or replytime like '%" + strKey[i] + "%' or last like '%" + strKey[i]
							+ "%' or content like '%" + strKey[i] + "%' or replyandnum like '%" + strKey[i]
							+ "%' or type like '%" + strKey[i] + "%')";
				}
			}
		}

		System.out.println(hql + "**********************");
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Saftycheck> list = query.list();
		getSession().close();
		return list;
	}

}
