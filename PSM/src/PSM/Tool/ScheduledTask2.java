package PSM.Tool;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import PSM.SMS.SendTest;
import hibernate.Missionstate;

public class ScheduledTask2 extends HibernateDaoSupport {
	
	private Date today;
	private Date threeDaysAfter;
	private Date sevenDaysAfter;
	private Date oneMonthAfter;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public void test() {  
        System.out.println("任务分配定时任务");
        initDate();
        for (Missionstate m : getUnCompletedMission()) {
        	if (acceptMission(m)) {
        		String phone = getPersonPhoneByName(m.getProperson());
            	String project = m.getProgroup();
            	String msg = "您好，您负责的"+project+"项目部尚有分配的任务（"+m.getMissionname()+"）未完成，请登录网页端查看详情。";
            	SendTest.sendMessage2(msg, phone, phone);
        	}
        }
    }
	
	private void initDate() {
		Calendar date = Calendar.getInstance();
		
		today = new Date();		
		System.out.println(sdf.format(today));
		
		date.setTime(today);
		date.add(Calendar.DATE, 3);
		threeDaysAfter = date.getTime();
		System.out.println(sdf.format(threeDaysAfter));
		
		date.setTime(today);
		date.add(Calendar.DATE, 7);
		sevenDaysAfter = date.getTime();
		System.out.println(sdf.format(sevenDaysAfter));
		
		date.setTime(today);
		date.add(Calendar.MONTH, 1);
		oneMonthAfter = date.getTime();
		System.out.println(sdf.format(oneMonthAfter));
	}
	
	private boolean acceptMission(Missionstate m) {
		if (isMoreThanOneMonth(m.getReleasetime(), m.getExpecttime())) 
			return sdf.format(sevenDaysAfter).equals(sdf.format(m.getExpecttime()));
		return sdf.format(threeDaysAfter).equals(sdf.format(m.getExpecttime()));
	}
	
	private boolean isMoreThanOneMonth(Date date1, Date date2) {
		Calendar date = Calendar.getInstance();
		date.setTime(date1);
		date.add(Calendar.MONTH, 1);
		date1 = date.getTime();
		return date1.before(date2);
		
	}
	
	public List<Missionstate> getUnCompletedMission() {
		String hql = "FROM Missionstate WHERE isdown='2' AND expecttime<=?";
		List<Missionstate> list = getHibernateTemplate().find(hql, new Object[]{sevenDaysAfter});
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
}
