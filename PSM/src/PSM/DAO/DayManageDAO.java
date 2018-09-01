package PSM.DAO;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

import hibernate.AttLog;
import hibernate.Daibanplanmade;
import hibernate.Daibanrecord;
import hibernate.Fingerprint;
import hibernate.Meeting;
import hibernate.Monitor;
import hibernate.Periodreport;
import hibernate.Saftyworkrizhi;
import hibernate.Zhiwenkaoqin;
import hibernate.Zhiwenuserinfo;

public class DayManageDAO extends HibernateDaoSupport {
	public int datacount;

	public List<Meeting> checkMeetingID(int id) {
		String hql = "from Meeting where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Meeting> list = query.list();
		getSession().close();
		return list;
	}

	/* ���� */
	public void insertMeeting(Meeting p) {
		this.getHibernateTemplate().save(p);
	}

	/* ���� */
	public void updateMeeting(Meeting p) {
		this.getHibernateTemplate().update(p);
	}

	/* ɾ�� */
	public void deleteMeeting(Meeting p) {
		this.getHibernateTemplate().delete(p);
	}

	public List<Meeting> getMeetingList(String findstr, String type, int start, int limit,String projectName) {
		String hql = "from Meeting where id is not null";
		System.out.println(type);
		hql += " and type like '%" + type + "%'";
		System.out.println(projectName);
		hql += " and projectName like '%" + projectName +"%'" ;
		// if(projectNo != null || projectNo.length()>0)
		// {
		// hql += " and proNo like '%" + projectNo +"%'" ;
		// }

		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( proNo like '%" + strKey[i] + "%' or proRange like '%" + strKey[i]
							+ "%' or cost like '%" + strKey[i] + "%' or head like '%" + strKey[i]
							+ "%' or techHead like '%" + strKey[i] + "%' or proHead like '%" + strKey[i]
							+ "%' or proTechHead like '%" + strKey[i] + "%' or proSaveHead like '%" + strKey[i]
							+ "%' )";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Meeting> list = query.list();
		getSession().close();
		return list;
	}

	// ***********Daibanplanmade********
	public List<Daibanplanmade> checkDaibanplanmadeID(int id) {
		String hql = "from Daibanplanmade where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Daibanplanmade> list = query.list();
		getSession().close();
		return list;
	}

	public void insertDaibanplanmade(Daibanplanmade p) {
		this.getHibernateTemplate().save(p);
	}

	public void updateDaibanplanmade(Daibanplanmade p) {
		this.getHibernateTemplate().update(p);
	}

	public void deleteDaibanplanmade(Daibanplanmade p) {
		this.getHibernateTemplate().delete(p);
	}

	public List<Daibanplanmade> getDaibanplanmadeList(String findstr, int start, int limit,String projectName) {
		String hql = "from Daibanplanmade where id is not null";
		hql += " and projectName like '%" + projectName +"%'" ;
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( holiday like '%" + strKey[i] + "%' or planname like '%" + strKey[i]
							+ "%' or planname like '%" + strKey[i] + "%' )";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Daibanplanmade> list = query.list();
		getSession().close();
		return list;
	}

	// **********指纹考勤********
	public List<Fingerprint> checkFingerprintID(int id) {
		String hql = "from Fingerprint where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Fingerprint> list = query.list();
		getSession().close();
		return list;
	}

	public void insertFingerprint(Fingerprint p) {
		this.getHibernateTemplate().save(p);
	}

	public void updateFingerprint(Fingerprint p) {
		this.getHibernateTemplate().update(p);
	}

	public void deleteFingerprint(Fingerprint p) {
		this.getHibernateTemplate().delete(p);
	}

	public List<Fingerprint> getFingerprintList(String findstr, int start, int limit) {
		String hql = "from Fingerprint where id is not null";
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( name like '%" + strKey[i] + "%' or ip like '%" + strKey[i]
							+ "%' or portno like '%" + strKey[i] + "%'  )";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Fingerprint> list = query.list();
		getSession().close();
		return list;
	}
	
	/**
	 * 刘迟
	 * 获取指纹考勤表单
	 * 根据监控点的名称进行查询
	 * @param id
	 * @return
	 */
	
	public Fingerprint getFingerprintByName(String name) {
		String hql = "from Fingerprint where name like '%" + name +"%'";
		List<Fingerprint> fingerprint = getHibernateTemplate().find(hql);
		if (fingerprint.size() == 0)
			return null;
		return fingerprint.get(0);
	}

	// ***********Daibanrecord********
		public List<Daibanrecord> checkDaibanrecordID(int id) {
			String hql = "from Daibanrecord where id=" + id;
			Query query = getSession().createQuery(hql);
			List<Daibanrecord> list = query.list();
			getSession().close();
			return list;
		}

		public void insertDaibanrecord(Daibanrecord p) {
			this.getHibernateTemplate().save(p);
		}

		public void updateDaibanrecord(Daibanrecord p) {
			this.getHibernateTemplate().update(p);
		}

		public void deleteDaibanrecord(Daibanrecord p) {
			this.getHibernateTemplate().delete(p);
		}

		public List<Daibanrecord> getDaibanrecordList(String findstr, int start, int limit,String projectName) {
			String hql = "from Daibanrecord where id is not null";
			hql += " and projectName like '%" + projectName +"%'" ;
			if (findstr != null || findstr.length() > 0) {
				String[] strKey = findstr.split(",");
				for (int i = 0; i < strKey.length; i++) {
					if (strKey[i].length() > 0) {
						hql += " and ( holiday like '%" + strKey[i] + "%' or ondutytime like '%" + strKey[i]
								+ "%' or ondutyperson like '%" + strKey[i] + "%' or nextperson like '%" + strKey[i]
								+ "%' )";
					}
				}
			}
			Query query = getSession().createQuery(hql);
			datacount = query.list().size();
			query.setFirstResult(start);
			query.setMaxResults(limit);
			List<Daibanrecord> list = query.list();
			getSession().close();
			return list;
		}
	
	// ***********Saftyworkrizhi********
	public List<Saftyworkrizhi> checkSaftyworkrizhiID(int id) {
		String hql = "from Saftyworkrizhi where id=" + id;
		Query query = getSession().createQuery(hql);
		List<Saftyworkrizhi> list = query.list();
		getSession().close();
		return list;
	}

	public void insertSaftyworkrizhi(Saftyworkrizhi p) {
		this.getHibernateTemplate().save(p);
	}

	public void updateSaftyworkrizhi(Saftyworkrizhi p) {
		this.getHibernateTemplate().update(p);
	}

	public void deleteSaftyworkrizhi(Saftyworkrizhi p) {
		this.getHibernateTemplate().delete(p);
	}


	public List<Saftyworkrizhi> getSaftyworkrizhiList(String findstr, int start, int limit,String projectName) {
		String hql = "from Saftyworkrizhi where id is not null";
		hql += " and projectName like '%" + projectName +"%'" ;
		if (findstr != null || findstr.length() > 0) {
			String[] strKey = findstr.split(",");
			for (int i = 0; i < strKey.length; i++) {
				if (strKey[i].length() > 0) {
					hql += " and ( rizhitime like '%" + strKey[i] + "%' or weekday like '%" + strKey[i]
							+ "%' or qixiday like '%" + strKey[i] + "%' or qixinight like '%" + strKey[i]
							+ "%' or degreeday like '%" + strKey[i] + "%' or degreenight like '%" + strKey[i]
							+ "%' or windday like '%" + strKey[i] + "%' or windnight like '%" + strKey[i]
							+ "%' or rizhi like '%" + strKey[i] + "%' or workplan like '%" + strKey[i] + "%' )";
				}
			}
		}
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		query.setFirstResult(start);
		query.setMaxResults(limit);
		List<Saftyworkrizhi> list = query.list();
		getSession().close();
		return list;
	}
	
	public Periodreport getPeriodreport(int id)
	{
		String hql = "from Periodreport where id=" + id;
		Query query = getSession().createQuery(hql);
      	List<Periodreport> list = query.list();
      	getSession().close();
      	if (list.size() == 0) return null;
      	return list.get(0);
	}
	
	public void insertPeriodreport(Periodreport p)
	{
		this.getHibernateTemplate().save(p);
	}
	
	public void updatePeriodreport(Periodreport p)
	{
		this.getHibernateTemplate().update(p);		
	}
	
	public void deletePeriodreport(Periodreport p)
	{
		this.getHibernateTemplate().delete(p);
	}
	
	public List<Periodreport> getPeriodreportList(String findstr, String type, int start, int limit,String projectName)
	{
		String hql = "from Periodreport where type='" + type + "'";
		hql += " and projectName like '%" + projectName +"%'" ;
		if(findstr != null || findstr.length()>0) {
    		String[] strKey = findstr.split(",");
        	for(int i=0; i<strKey.length; i++)
        		if(strKey[i].length()>0) {
        			hql += " and ( year like '%" + strKey[i] +"%' or month like '%" + strKey[i] +"%' or day like '%" + strKey[i] + "%' or time like '%" + strKey[i] + "%' )";
        		}
    	}
		Query query = getSession().createQuery(hql);
    	datacount = query.list().size();
    	query.setFirstResult(start);
		query.setMaxResults(limit);
    	List<Periodreport> list = query.list();   	
    	getSession().close();
    	return list;
	}
	
	public List<AttLog> getAttLogList(String IPAddress,int port) throws UnsupportedEncodingException{
		 List<AttLog> result = new ArrayList<AttLog>();
			ComThread.InitSTA();  
	        ActiveXComponent app = new ActiveXComponent("zkemkeeper.ZKEM.1");  
	        Dispatch TFT = (Dispatch) app.getObject();
	        Boolean loginResult = Dispatch.call(TFT, "Connect_Net", IPAddress  ,port).getBoolean();
	        if(loginResult){
	        	System.out.println("connected success");
	        	Boolean readData = Dispatch.call(TFT, "ReadGeneralLogData",(int)254).getBoolean();
	        	if(readData){
	        		System.out.println("readData success");       		
	        		String EnrollNumber = "";
	        		int imachineNumber =254;
	        		Variant enrollNumber = new Variant(EnrollNumber, true);
	        		Variant idwVerifyMode = new Variant(0, true);
	        		Variant idwInOutMode= new Variant(0, true);
	        		Variant idwYear= new Variant(0, true);
	        		Variant idwMonth = new Variant(0, true);
	        		Variant idwDay= new Variant(0, true);
	        		Variant idwHour= new Variant(0, true);
	        		Variant idwMinute= new Variant(0, true);
	        		Variant idwSecond= new Variant(0, true);
	        		Variant idwWorkcode = new Variant(0, true);
	        		// int count = 0;
	        		// System.out.println("count"+"     "+"EnrollNumber"+"    "+"verifyMode"+"    "+"InOutMode"+"    "+"time"+"    "+"workCode");
	        		while(Dispatch.call(TFT, "SSR_GetGeneralLogData",imachineNumber,enrollNumber, idwVerifyMode,
	        				idwInOutMode, idwYear, idwMonth, idwDay, idwHour,idwMinute,idwSecond, idwWorkcode).getBoolean())
	        		{
	        			//count++;
	        			AttLog attLog = new AttLog();
	        			attLog.setEnrollNumber(enrollNumber.toString());
	        			attLog.setVerifyMode(idwVerifyMode.toString());
	        			attLog.setInOutMode(idwInOutMode.toString());
	        			attLog.setTime(idwYear.toString() + "-" + idwMonth.toString() + "-" + idwDay.toString() + " " + idwHour.toString() + ":" + idwMinute.toString() + ":" + idwSecond.toString());
	        			attLog.setWorkCode(idwWorkcode.toString());
	        			result.add(attLog);
	        			//System.out.println(" "+count+"                      "+attLog.getEnrollNumber()+"                      "+attLog.getVerifyMode()+"            "+attLog.getInOutMode()+"         "+attLog.getTime()+"       "+attLog.getWorkCode());
   				
	        		}
	        		return result;
	        	}
	        	else
	        	{
	        		System.out.println("readData failed");
	        	}
	        }
	        else
	        	System.out.println("connected failed");
		return result;
	}
	
	public Monitor getMonitor(int id) {
		String hql = "from Monitor where id = " + id;
		List<Monitor> monitors = getHibernateTemplate().find(hql);
		if (monitors.size() == 0)
			return null;
		return monitors.get(0);
		
	}
	
	public void synZhiWen(Zhiwenkaoqin p) {
		
		String IPAddress = p.getIp();
		String port = String.valueOf(p.getPort());
		
		String hql = "from Zhiwenkaoqin where id is not null";
		hql += " and ip like '%" + IPAddress +"%'" ;
		hql += " and port like '%" + String.valueOf(port) +"%'" ;
		
		Query query = getSession().createQuery(hql);
		//datacount = query.list().size();
		List<Zhiwenkaoqin> list = query.list();
		getSession().close();
		
		int i=0;
		for(;i<list.size();i++) {
			if(list.get(i).getTime().equals(p.getTime()))
				break;
		}
		
		if(i == list.size())
			this.getHibernateTemplate().save(p);
	}
	
	public List<Zhiwenkaoqin> getZhiWenList(String IPAddress,int port) {
		String hql = "from Zhiwenkaoqin where id is not null";
		hql += " and ip like '%" + IPAddress +"%'" ;
		hql += " and port like '%" + String.valueOf(port) +"%'" ;
		
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		List<Zhiwenkaoqin> list = query.list();
		getSession().close();
		return list;
	}
	
	public void synZhiWenUser(Zhiwenuserinfo p) {
		
		String IPAddress = p.getIp();
		String port = String.valueOf(p.getPort());
		
		String hql = "from Zhiwenuserinfo where id is not null";
		hql += " and ip like '%" + IPAddress +"%'" ;
		hql += " and port like '%" + String.valueOf(port) +"%'" ;
		
		Query query = getSession().createQuery(hql);
		//datacount = query.list().size();
		List<Zhiwenuserinfo> list = query.list();
		getSession().close();
		int i=0;
		for(;i<list.size();i++) {
			if(list.get(i).getEnrollNumber().equals(p.getEnrollNumber()))
				break;
		}
		
		if(i == list.size())
			this.getHibernateTemplate().save(p);
		
		
		
	}
	
	public List<Zhiwenuserinfo> getZhiwenuserinfoList(String IPAddress,int port) {
		String hql = "from Zhiwenuserinfo where id is not null";
		hql += " and ip like '%" + IPAddress +"%'" ;
		hql += " and port like '%" + String.valueOf(port) +"%'" ;
		
		Query query = getSession().createQuery(hql);
		datacount = query.list().size();
		List<Zhiwenuserinfo> list = query.list();
		getSession().close();
		return list;
	}
}
