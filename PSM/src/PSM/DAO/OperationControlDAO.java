package PSM.DAO;

import java.util.List;

import org.hibernate.Query;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import hibernate.Constructdesign;
import hibernate.Constructionelec;
import hibernate.EnviromentPro;
import hibernate.Fbtestjudge;
import hibernate.Feemanagement;
import hibernate.FireSafety;
import hibernate.ODHequipment;
import hibernate.Occuevaluation;
import hibernate.Occumonitor;
import hibernate.Otherjob;
import hibernate.PODgoods;
import hibernate.Proapproval;
import hibernate.Safeprojd;
import hibernate.Safepromonitor;
import hibernate.Safetyprotocal;
import hibernate.SaveEnergy;
import hibernate.Saveenergyjc;
import hibernate.Schemeimple;
import hibernate.SecureJobSlip;
import hibernate.SecuritySymbol;
import hibernate.Tezhongpeople;
import hibernate.Tezhongsbpeople;
import hibernate.Traintable;
import hibernate.TransportSafety;

public class OperationControlDAO extends HibernateDaoSupport {

	public int datacount;

	public List<SecureJobSlip> checkSecureJobSlipID(int id) {
		String hql = "from SecureJobSlip where id=" + id;
		Query query = getSession().createQuery(hql);
		List<SecureJobSlip> list = query.list();
		getSession().close();
		return list;
	}

	/* 插入 */
	public void insertSecureJobSlip(SecureJobSlip p) {
		this.getHibernateTemplate().save(p);
	}

	/* 更新 */
	public void updateSecureJobSlip(SecureJobSlip p) {
		this.getHibernateTemplate().update(p);
	}

	/* 删除 */
	public void deleteSecureJobSlip(SecureJobSlip p) {
		this.getHibernateTemplate().delete(p);
	}

	// 获取项目信息List
	public List<SecureJobSlip> getSecureJobSlipList(String findstr, int start, int limit, String projectName) {
		String hql = "from SecureJobSlip where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( workPlace like '%" + strKey[i] + "%' or workContent like '%" + strKey[i]
							+ "%' or workTime like '%" + strKey[i] + "%' or dangerSource like '%" + strKey[i]
							+ "%' or principle like '%" + strKey[i] + "%' or workerNum like '%" + strKey[i] + "%' )";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<SecureJobSlip> list = query.list();
		getSession().close();
		return list;
	}

	// jianglf----------------------------------------------------------
	public List<Feemanagement> checkFeemanagementID(int id) {
		String hql = "from Feemanagement where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Feemanagement> list = query.list();
		getSession().close();
		return list;
	}

	/* ���� */
	public void insertFeemanagement(Feemanagement p) {
		this.getHibernateTemplate().save(p);
	}

	/* ���� */
	public void updateFeemanagement(Feemanagement p) {
		this.getHibernateTemplate().update(p);
	}

	/* ɾ�� */
	public void deleteFeemanagement(Feemanagement p) {
		this.getHibernateTemplate().delete(p);
	}

	// ��ȡ��Ŀ��ϢList
	public List<Feemanagement> getFeemanagementList(String findstr, String type, int start, int limit) {
		String hql = "from Feemanagement where id is not null";

		System.out.println(type);
		hql += " and title like '%" + type + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					// hql += " and ( typei like '%" + strKey[i] +"%' or unit
					// like '%" + strKey[i] +"%' or fee like '%" + strKey[i]
					// +"%' or use like '%" + strKey[i] +"%' or jperson like '%"
					// + strKey[i] +"%' or dperson like '%" + strKey[i] +"%' or
					// time like '%" + strKey[i] +"%' or content like '%" +
					// strKey[i] +"%' or accessory like '%" + strKey[i] +"%' )";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Feemanagement> list = query.list();
		getSession().close();
		return list;
	}
	// end-------------------------------------------------------------

	public List<Schemeimple> checkSchemeimpleID(int id) {
		String hql = "from Schemeimple where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Schemeimple> list = query.list();
		getSession().close();
		return list;
	}

	/* 插入 */
	public void insertSchemeimple(Schemeimple p) {
		this.getHibernateTemplate().save(p);
	}

	/* 更新 */
	public void updateSchemeimple(Schemeimple p) {
		this.getHibernateTemplate().update(p);
	}

	/* 删除 */
	public void deleteSchemeimple(Schemeimple p) {
		this.getHibernateTemplate().delete(p);
	}

	// 获取项目信息List
	public List<Schemeimple> getSchemeimpleList(String findstr, int start, int limit, String projectName) {
		String hql = "from Schemeimple where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( no like '%" + strKey[i] + "%' or schemeName like '%" + strKey[i]
							+ "%' or supervise like '%" + strKey[i] + "%' or acceptance like '%" + strKey[i]
							+ "%' or accessory like '%" + strKey[i] + "%'  or jd like '%" + strKey[i] + "%')";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Schemeimple> list = query.list();
		getSession().close();
		return list;
	}

	public List<FireSafety> checkFireSafetyID(int id) {
		String hql = "FireSafety where id=" + id;
		Query query = getSession().createQuery(hql);
		List<FireSafety> list = query.list();
		getSession().close();
		return list;
	}

	/* 插入 */
	public void insertFireSafety(FireSafety p) {
		this.getHibernateTemplate().save(p);
	}

	/* 更新 */
	public void updateFireSafety(FireSafety p) {
		this.getHibernateTemplate().update(p);
	}

	/* 删除 */
	public void deleteFireSafety(FireSafety p) {
		this.getHibernateTemplate().delete(p);
	}

	// 获取项目信息List
	public List<FireSafety> getFireSafetyList(String findstr, int start, int limit, String projectName) {
		String hql = "from FireSafety where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( name like '%" + strKey[i] + "%' or model like '%" + strKey[i] + "%' or num like '%"
							+ strKey[i] + "%' or department like '%" + strKey[i] + "%' or acqDate like '%" + strKey[i]
							+ "%' or changeDate like '%" + strKey[i] + "%' or place like '%" + strKey[i]
							+ "%' or chargePerson like '%" + strKey[i] + "%' or checkPeriodically like '%" + strKey[i]
							+ "%' or checkResult like '%" + strKey[i] + "%')";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<FireSafety> list = query.list();
		getSession().close();
		return list;
	}

	public List<TransportSafety> checkTransportSafetyID(int id) {
		String hql = "TransportSafety where id=" + id;
		Query query = getSession().createQuery(hql);
		List<TransportSafety> list = query.list();
		getSession().close();
		return list;
	}

	/* 插入 */
	public void insertTransportSafety(TransportSafety p) {
		this.getHibernateTemplate().save(p);
	}

	/* 更新 */
	public void updateTransportSafety(TransportSafety p) {
		this.getHibernateTemplate().update(p);
	}

	/* 删除 */
	public void deleteTransportSafety(TransportSafety p) {
		this.getHibernateTemplate().delete(p);
	}

	// 获取项目信息List
	public List<TransportSafety> getTransportSafetyList(String findstr, int start, int limit, String projectName) {
		String hql = "from TransportSafety where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( carNum like '%" + strKey[i] + "%' or carName like '%" + strKey[i]
							+ "%' or department like '%" + strKey[i] + "%' or license like '%" + strKey[i]
							+ "%' or driver like '%" + strKey[i] + "%' or driverNum like '%" + strKey[i]
							+ "%' or maintenance like '%" + strKey[i] + "%' )";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<TransportSafety> list = query.list();
		getSession().close();
		return list;
	}

	public List<PODgoods> checkPODgoodsID(int id) {
		String hql = "PODgoods where id=" + id;
		Query query = getSession().createQuery(hql);
		List<PODgoods> list = query.list();
		getSession().close();
		return list;
	}

	/* 插入 */
	public void insertPODgoods(PODgoods p) {
		this.getHibernateTemplate().save(p);
	}

	/* 更新 */
	public void updatePODgoods(PODgoods p) {
		this.getHibernateTemplate().update(p);
	}

	/* 删除 */
	public void deletePODgoods(PODgoods p) {
		this.getHibernateTemplate().delete(p);
	}

	// 获取项目信息List
	public List<PODgoods> getPODgoodsList(String findstr, int start, int limit, String projectName) {
		String hql = "from PODgoods where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( name like '%" + strKey[i] + "%' or deliveryTime like '%" + strKey[i]
							+ "%' or num like '%" + strKey[i] + "%' or personName like '%" + strKey[i]
							+ "%' or department like '%" + strKey[i] + "%' or timeLimit like '%" + strKey[i]
							+ "%' or autograph like '%" + strKey[i] + "%' or comment like '%" + strKey[i] + "%')";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<PODgoods> list = query.list();
		getSession().close();
		return list;
	}

	public List<ODHequipment> checkODHequipmentID(int id) {
		String hql = "ODHequipment where id=" + id;
		Query query = getSession().createQuery(hql);
		List<ODHequipment> list = query.list();
		getSession().close();
		return list;
	}

	/* 插入 */
	public void insertODHequipment(ODHequipment p) {
		this.getHibernateTemplate().save(p);
	}

	/* 更新 */
	public void updateODHequipment(ODHequipment p) {
		this.getHibernateTemplate().update(p);
	}

	/* 删除 */
	public void deleteODHequipment(ODHequipment p) {
		this.getHibernateTemplate().delete(p);
	}

	// 获取项目信息List
	public List<ODHequipment> getODHequipmentList(String findstr, int start, int limit, String projectName) {
		String hql = "from ODHequipment where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( name like '%" + strKey[i] + "%' or model like '%" + strKey[i]
							+ "%' or department like '%" + strKey[i] + "%' or num like '%" + strKey[i]
							+ "%' or buyTime like '%" + strKey[i] + "%' or serialNumber like '%" + strKey[i]
							+ "%' or place like '%" + strKey[i] + "%' or responsibility like '%" + strKey[i] + "%')";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<ODHequipment> list = query.list();
		getSession().close();
		return list;
	}

	public List<EnviromentPro> checkEnviromentProID(int id) {
		String hql = "from EnviromentPro where id=" + id;
		Query query = getSession().createQuery(hql);
		List<EnviromentPro> list = query.list();
		getSession().close();
		return list;
	}

	/* ���� */
	public void insertEnviromentPro(EnviromentPro p) {
		this.getHibernateTemplate().save(p);
	}

	/* ���� */
	public void updateEnviromentPro(EnviromentPro p) {
		this.getHibernateTemplate().update(p);
	}

	/* ɾ�� */
	public void deleteEnviromentPro(EnviromentPro p) {
		this.getHibernateTemplate().delete(p);
	}

	// ��ȡ��Ŀ��ϢList
	public List<EnviromentPro> getEnviromentProList(String findstr, int start, int limit) {
		String hql = "from EnviromentPro where id is not null";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( name like '%" + strKey[i] + "%' or department like '%" + strKey[i]
							+ "%' or time like '%" + strKey[i] + "%' or approval like '%" + strKey[i] + "%' )";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<EnviromentPro> list = query.list();
		getSession().close();
		return list;
	}

	public List<SaveEnergy> checkSaveEnergyID(int id) {
		String hql = "from SaveEnergy where id=" + id;
		Query query = getSession().createQuery(hql);
		List<SaveEnergy> list = query.list();
		getSession().close();
		return list;
	}

	/* ���� */
	public void insertSaveEnergy(SaveEnergy p) {
		this.getHibernateTemplate().save(p);
	}

	/* ���� */
	public void updateSaveEnergy(SaveEnergy p) {
		this.getHibernateTemplate().update(p);
	}

	/* ɾ�� */
	public void deleteSaveEnergy(SaveEnergy p) {
		this.getHibernateTemplate().delete(p);
	}

	// ��ȡ��Ŀ��ϢList
	public List<SaveEnergy> getSaveEnergyList(String findstr, int start, int limit) {
		String hql = "from SaveEnergy where id is not null";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( name like '%" + strKey[i] + "%' or department like '%" + strKey[i]
							+ "%' or time like '%" + strKey[i] + "%' or approval like '%" + strKey[i] + "%' )";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<SaveEnergy> list = query.list();
		getSession().close();
		return list;
	}

	public List<Constructdesign> checkConstructdesignID(int id) {
		String hql = "from Constructdesign where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Constructdesign> list = query.list();
		getSession().close();
		return list;
	}

	/* ���� */
	public void insertConstructdesign(Constructdesign p) {
		this.getHibernateTemplate().save(p);
	}

	/* ���� */
	public void updateConstructdesign(Constructdesign p) {
		this.getHibernateTemplate().update(p);
	}

	/* ɾ�� */
	public void deleteConstructdesign(Constructdesign p) {
		this.getHibernateTemplate().delete(p);
	}

	// ��ȡ��Ŀ��ϢList
	public List<Constructdesign> getConstructdesignList(String findstr, int start, int limit, String projectName) {
		String hql = "from Constructdesign where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and (type like '%" + strKey[i] + "%' or unit like '%"
							+ strKey[i] + "%' or accessory like '%" + strKey[i] + "%' )";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Constructdesign> list = query.list();
		getSession().close();
		return list;
	}

	public List<Saveenergyjc> checkSaveenergyjcID(int id) {
		String hql = "from Saveenergyjc where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Saveenergyjc> list = query.list();
		getSession().close();
		return list;
	}

	/* ���� */
	public void insertSaveenergyjc(Saveenergyjc p) {
		this.getHibernateTemplate().save(p);
	}

	/* ���� */
	public void updateSaveenergyjc(Saveenergyjc p) {
		this.getHibernateTemplate().update(p);
	}

	/* ɾ�� */
	public void deleteSaveenergyjc(Saveenergyjc p) {
		this.getHibernateTemplate().delete(p);
	}

	// SaveenergyjcList
	public List<Saveenergyjc> getSaveenergyjcList(String findstr, int start, int limit, String projectName) {
		String hql = "from Saveenergyjc where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and (year like '%" + strKey[i] + "%' or season like '%"
							+ strKey[i] + "%' or elec like '%"+strKey[i]+"%'  or gas like '%"+strKey[i]+"%'  or hgas like '%"+strKey[i]+"%'  or date like '%"+strKey[i]+"%'  or ngas like '%"+strKey[i]+"%' or accessory like '%" + strKey[i] + "%' )";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Saveenergyjc> list = query.list();
		getSession().close();
		return list;
	}
	
	public Proapproval getRecordById(String id){
		String hql = "from Proapproval f where f.id = '"+id+"'";
		Query query = getSession().createQuery(hql);
		List<Proapproval> list = query.list();
		getSession().close();
		return list.get(0);
	}
	
	public Schemeimple getRecordByProapproval( String pName){
		String hql = "from Schemeimple f where f.id is not null and f.schemeName = '"+pName+"' ";
		Query query = getSession().createQuery(hql);
		List<Schemeimple> list = query.list();
		getSession().close();
		if(list.size()==0){
			return null;
		}
		return list.get(0);
	}
	
	public void updateName(String pName, String Name){
		Schemeimple sch= getRecordByProapproval(pName);
		
		sch.setSchemeName(Name);
		updateSchemeimple(sch);
		
	}
	
	public List<Proapproval> checkProapprovalID(int id) {
		String hql = "from Proapproval where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Proapproval> list = query.list();
		getSession().close();
		return list;
	}

	/* ���� */
	public void insertProapproval(Proapproval p) {
		this.getHibernateTemplate().save(p);
	}

	/* ���� */
	public void updateProapproval(Proapproval p) {
		this.getHibernateTemplate().update(p);
	}

	/* ɾ�� */
	public void deleteProapproval(Proapproval p) {
		this.getHibernateTemplate().delete(p);
	}

	// ��ȡ��Ŀ��ϢList
	public List<Proapproval> getProapprovalList(String findstr, int start, int limit, String projectName) {
		String hql = "from Proapproval where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( name like '%" + strKey[i] + "%' or type like '%"
							+ strKey[i] + "%' or unit like '%" + strKey[i] + "%' or time like '%" + strKey[i]
							+ "%' or approval like '%" + strKey[i] + "%' or accessory like '%" + strKey[i] + "%' )";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Proapproval> list = query.list();
		getSession().close();
		return list;
	}

	public List<Occuevaluation> checkOccuevaluationID(int id) {
		String hql = "from Occuevaluation where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Occuevaluation> list = query.list();
		getSession().close();
		return list;
	}

	/* ���� */
	public void insertOccuevaluation(Occuevaluation p) {
		this.getHibernateTemplate().save(p);
	}

	/* ���� */
	public void updateOccuevaluation(Occuevaluation p) {
		this.getHibernateTemplate().update(p);
	}

	/* ɾ�� */
	public void deleteOccuevaluation(Occuevaluation p) {
		this.getHibernateTemplate().delete(p);
	}

	// ��ȡ��Ŀ��ϢList
	public List<Occuevaluation> getOccuevaluationList(String findstr, int start, int limit, String projectName) {
		String hql = "from Occuevaluation where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( no like '%" + strKey[i] + "%' or date like '%" + strKey[i]
							+ "%' or accessory like '%" + strKey[i] + "%' )";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Occuevaluation> list = query.list();
		getSession().close();
		return list;
	}

	public List<Occumonitor> checkOccumonitorID(int id) {
		String hql = "from Occumonitor where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Occumonitor> list = query.list();
		getSession().close();
		return list;
	}

	/* 插入 */
	public void insertOccumonitor(Occumonitor p) {
		this.getHibernateTemplate().save(p);
	}

	/* 更新 */
	public void updateOccumonitor(Occumonitor p) {
		this.getHibernateTemplate().update(p);
	}

	/* 删除 */
	public void deleteOccumonitor(Occumonitor p) {
		this.getHibernateTemplate().delete(p);
	}

	// 获取项目信息List
	public List<Occumonitor> getOccumonitorList(String findstr, int start, int limit, String projectName) {
		String hql = "from Occumonitor where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( no like '%" + strKey[i] + "%' or factor like '%" + strKey[i] + "%' or time like '%"
							+ strKey[i] + "%' or result like '%" + strKey[i] + "%' or accessory like '%" + strKey[i]
							+ "%' )";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Occumonitor> list = query.list();
		getSession().close();
		return list;
	}

	public List<Otherjob> checkOtherjobID(int id) {
		String hql = "from Otherjob where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Otherjob> list = query.list();
		getSession().close();
		return list;
	}

	/* 插入 */
	public void insertOtherjob(Otherjob p) {
		this.getHibernateTemplate().save(p);
	}

	/* 更新 */
	public void updateOtherjob(Otherjob p) {
		this.getHibernateTemplate().update(p);
	}

	/* 删除 */
	public void deleteOtherjob(Otherjob p) {
		this.getHibernateTemplate().delete(p);
	}

	// 获取项目信息List
	public List<Otherjob> getOtherjobList(String findstr, int start, int limit, String projectName) {
		String hql = "from Otherjob where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( name like '%" + strKey[i] + "%' or time like '%" + strKey[i] + "%' or unit like '%"
							+ strKey[i] + "%' or record like '%" + strKey[i] + "%' or accessory like '%" + strKey[i]
							+ "%' )";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Otherjob> list = query.list();
		getSession().close();
		return list;
	}

	public List<Safepromonitor> checkSafepromonitorID(int id) {
		String hql = "from Safepromonitor where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Safepromonitor> list = query.list();
		getSession().close();
		return list;
	}

	/* 插入 */
	public void insertSafepromonitor(Safepromonitor p) {
		this.getHibernateTemplate().save(p);
	}

	/* 更新 */
	public void updateSafepromonitor(Safepromonitor p) {
		this.getHibernateTemplate().update(p);
	}

	/* 删除 */
	public void deleteSafepromonitor(Safepromonitor p) {
		this.getHibernateTemplate().delete(p);
	}

	// 获取项目信息List
	public List<Safepromonitor> getSafepromonitorList(String findstr, int start, int limit) {
		String hql = "from Safepromonitor where id is not null";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( bzperson like '%" + strKey[i] + "%' or bztime like '%" + strKey[i]
							+ "%' or shperson like '%" + strKey[i] + "%' " + "or shtime like '%" + strKey[i]
							+ "%' or pzperson like '%" + strKey[i] + "%' or pztime like '%" + strKey[i]
							+ "%' or accessory like '%" + strKey[i] + "%' )";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Safepromonitor> list = query.list();
		getSession().close();
		return list;
	}

	public List<Safeprojd> checkSafeprojdID(int id) {
		String hql = "from Safeprojd where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Safeprojd> list = query.list();
		getSession().close();
		return list;
	}

	/* 插入 */
	public void insertSafeprojd(Safeprojd p) {
		this.getHibernateTemplate().save(p);
	}

	/* 更新 */
	public void updateSafeprojd(Safeprojd p) {
		this.getHibernateTemplate().update(p);
	}

	/* 删除 */
	public void deleteSafeprojd(Safeprojd p) {
		this.getHibernateTemplate().delete(p);
	}

	// 获取项目信息List
	public List<Safeprojd> getSafeprojdList(String findstr, int start, int limit) {
		String hql = "from Safeprojd where id is not null";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( person like '%" + strKey[i] + "%' or jdperson like '%" + strKey[i]
							+ "%' or time like '%" + strKey[i] + "%' or accessory like '%" + strKey[i] + "%' )";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Safeprojd> list = query.list();
		getSession().close();
		return list;
	}

	public List<Tezhongsbpeople> checkTezhongsbpeopleID(int id) {
		String hql = "from Tezhongsbpeople where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Tezhongsbpeople> list = query.list();
		getSession().close();
		return list;
	}

	/* 插入 */
	public void insertTezhongsbpeople(Tezhongsbpeople p) {
		this.getHibernateTemplate().save(p);
	}

	/* 更新 */
	public void updateTezhongsbpeople(Tezhongsbpeople p) {
		this.getHibernateTemplate().update(p);
	}

	/* 删除 */
	public void deleteTezhongsbpeople(Tezhongsbpeople p) {
		this.getHibernateTemplate().delete(p);
	}

	// 获取项目信息List
	public List<Tezhongsbpeople> getTezhongsbpeopleList(String findstr, int start, int limit, String projectName) {
		String hql = "from Tezhongsbpeople where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( no like '%" + strKey[i] + "%' or peixun like '%" + strKey[i] + "%' or fbName like '%"
							+ strKey[i] + "%' or type like '%" + strKey[i] + "%' " + "or project like '%" + strKey[i]
							+ "%' or name like '%" + strKey[i] + "%' or gender like '%" + strKey[i]
							+ "%'or cardNo like '%" + strKey[i] + "%' " + "or beginTime like '%" + strKey[i]
							+ "%' or validTime like '%" + strKey[i] + "%' " + "or unit like '%" + strKey[i]
							+ "%' or ps like '%" + strKey[i] + "%' or accessory like '%" + strKey[i] + "%' )";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Tezhongsbpeople> list = query.list();
		getSession().close();
		return list;
	}

	public List<Fbtestjudge> checkFbtestjudgeID(int id) {
		String hql = "from Fbtestjudge where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Fbtestjudge> list = query.list();
		getSession().close();
		return list;
	}

	/* 插入 */
	public void insertFbtestjudge(Fbtestjudge p) {
		this.getHibernateTemplate().save(p);
	}

	/* 更新 */
	public void updateFbtestjudge(Fbtestjudge p) {
		this.getHibernateTemplate().update(p);
	}

	/* 删除 */
	public void deleteFbtestjudge(Fbtestjudge p) {
		this.getHibernateTemplate().delete(p);
	}

	// 获取项目信息List
	public List<Fbtestjudge> getFbtestjudgeList(String findstr, int start, int limit, String projectName) {
		String hql = "from Fbtestjudge where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( no like '%" + strKey[i] + "%' or tester like '%" + strKey[i] + "%' or time like '%"
							+ strKey[i] + "%' or result like '%" + strKey[i] + "%' or accessory like '%" + strKey[i]
							+ "%' )";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Fbtestjudge> list = query.list();
		getSession().close();
		return list;
	}

	public List<SecuritySymbol> checkSecuritySymbolID(int id) {
		String hql = "SecuritySymbol where id=" + id;
		Query query = getSession().createQuery(hql);
		List<SecuritySymbol> list = query.list();
		getSession().close();
		return list;
	}

	/* 插入 */
	public void insertSecuritySymbol(SecuritySymbol p) {
		this.getHibernateTemplate().save(p);
	}

	/* 更新 */
	public void updateSecuritySymbol(SecuritySymbol p) {
		this.getHibernateTemplate().update(p);
	}

	/* 删除 */
	public void deleteSecuritySymbol(SecuritySymbol p) {
		this.getHibernateTemplate().delete(p);
	}

	// 获取项目信息List
	public List<SecuritySymbol> getSecuritySymbolList(String findstr, int start, int limit) {
		String hql = "from SecuritySymbol where id is not null";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( name like '%" + strKey[i] + "%' or num like '%" + strKey[i]
							+ "%' or department like '%" + strKey[i] + "%' or installTime like '%" + strKey[i]
							+ "%' or installPlace like '%" + strKey[i] + "%' or responsibility like '%" + strKey[i]
							+ "%' )";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<SecuritySymbol> list = query.list();
		getSession().close();
		return list;
	}

	public List<Tezhongpeople> checkTezhongpeopleID(int id) {
		String hql = "from Tezhongpeople where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Tezhongpeople> list = query.list();
		getSession().close();
		return list;
	}

	/* 插入 */
	public void insertTezhongpeople(Tezhongpeople p) {
		this.getHibernateTemplate().save(p);
	}

	/* 更新 */
	public void updateTezhongpeople(Tezhongpeople p) {
		this.getHibernateTemplate().update(p);
	}

	/* 删除 */
	public void deleteTezhongpeople(Tezhongpeople p) {
		this.getHibernateTemplate().delete(p);
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

	// 获取项目信息List
	public List<Tezhongpeople> getTezhongpeopleList(String findstr, int start, int limit, String projectName) {
		String hql = "from Tezhongpeople where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( name like '%" + strKey[i] + "%' or peixun like '%" + strKey[i]
							+ "%' or fbName like '%" + strKey[i] + "%' or type like '%" + strKey[i]
							+ "%' or sex like '%" + strKey[i] + "%' or cardName like '%" + strKey[i]
							+ "%' or cardTime like '%" + strKey[i] + "%'or cardPlace like '%" + strKey[i]
							+ "%' or cardNo like '%" + strKey[i] + "%' or ps like '%" + strKey[i]
							+ "%'  or accessory like '%" + strKey[i] + "%' or useTime like '%" + strKey[i] + "%' )";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Tezhongpeople> list = query.list();
		getSession().close();
		return list;
	}

	public List<Safetyprotocal> checkSafetyprotocalID(int id) {
		String hql = "from Safetyprotocal where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Safetyprotocal> list = query.list();
		getSession().close();
		return list;
	}

	/* 插入 */
	public void insertSafetyprotocal(Safetyprotocal p) {
		this.getHibernateTemplate().save(p);
	}

	/* 更新 */
	public void updateSafetyprotocal(Safetyprotocal p) {
		this.getHibernateTemplate().update(p);
	}

	/* 删除 */
	public void deleteSafetyprotocal(Safetyprotocal p) {
		this.getHibernateTemplate().delete(p);
	}

	// 获取项目信息List
	public List<Safetyprotocal> getSafetyprotocalList(String findstr, int start, int limit, String projectName) {
		String hql = "from Safetyprotocal where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( type like '%" + strKey[i] + "%' or fbname like '%" + strKey[i] + "%' or name like '%"
							+ strKey[i] + "%' or date like '%" + strKey[i] + "%' )";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Safetyprotocal> list = query.list();
		getSession().close();
		return list;
	}

	public List<Constructionelec> checkConstructionelecID(int id) {
		String hql = "from Constructionelec where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Constructionelec> list = query.list();
		getSession().close();
		return list;
	}

	/* 插入 */
	public void insertConstructionelec(Constructionelec p) {
		this.getHibernateTemplate().save(p);
	}

	/* 更新 */
	public void updateConstructionelec(Constructionelec p) {
		this.getHibernateTemplate().update(p);
	}

	/* 删除 */
	public void deleteConstructionelec(Constructionelec p) {
		this.getHibernateTemplate().delete(p);
	}

	// 获取项目信息List
	public List<Constructionelec> getConstructionelecList(String findstr, int start, int limit, String projectName) {
		String hql = "from Constructionelec where id is not null";
		hql += " and projectName like '%" + projectName + "%'";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( type like '%" + strKey[i] + "%' or name like '%" + strKey[i] + "%' or num like '%" + strKey[i] + "%' or singlepower like '%" + strKey[i] + "%' or sumpower like '%" + strKey[i] +
							"%' or factory like '%" + strKey[i] + "%' or shiyong like '%" + strKey[i] + "%' or outtime like '%" + strKey[i] + "%' or intime like '%" + strKey[i] + 
							"%' or plantime like '%" + strKey[i] + "%' or realtime like '%" + strKey[i] + "%' or status like '%" + strKey[i] + "%' or approve like '%" + strKey[i] + "%' or accessory like '%"
							+ strKey[i] + "%' )";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Constructionelec> list = query.list();
		getSession().close();
		return list;
	}
}
