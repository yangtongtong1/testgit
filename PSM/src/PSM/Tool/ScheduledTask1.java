package PSM.Tool;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import PSM.SMS.SendTest;

public class ScheduledTask1 extends HibernateDaoSupport {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
	public void test() {  
        System.out.println("规定动作定时任务");
        System.out.print("现在时间是 ： " + sdf.format(new Date()));
        for (Object[] objects : getUnCompletedAction()) {
        	String project = (String) objects[0];
        	Long count = (Long) objects[1];
        	System.out.println(project + " : " + count);
        	String msg = "您好，您负责的"+project+"项目部尚有"+count+"项指定任务未完成，请登录网页端查看详情。";
        	String[] roles = {"项目经理", "安全总监", "施工经理"};
        	for (String phone : getAllPersonNumber(project))
        		for (String role : roles)
        			sendMessage(project, role, msg);
        }        
    }
	
	private List<Object[]> getUnCompletedAction() {
		String hql = "SELECT project, count(*) FROM PrescribedActionDynamic WHERE is_done = FALSE GROUP BY project HAVING count(*)>0";
		List<Object[]> list = getHibernateTemplate().find(hql);
		return list;
	}
	
	private List<String> getAllPersonNumber(String project) {
		String hql = "SELECT phone FROM Flownode WHERE projectName=? and (duty=? or duty=? or duty=?)";
		List<String> list = getHibernateTemplate().find(hql, new String[]{project, "安全总监", "项目经理", "施工经理"});
		return list;
	}
	
	private void sendMessage(String project, String role, String msg) {
		String hql = "SELECT phone FROM Flownode WHERE projectName=? and duty=?";
		List<String> list = getHibernateTemplate().find(hql, new String[]{project, role});
		for (String phone : list) 
			SendTest.sendMessage2(msg, phone, role);
		//SendTest.sendMessageByType(msg, type);
	
	}
}
