package PSM.SMS;

import java.util.ArrayList;

import com.qcloud.sms.SmsMultiSender;
import com.qcloud.sms.SmsMultiSenderResult;


public class SmsSDKDemo {
    public static void main(String[] args) {
    	try {
    		//请根据实际 appid 和 appkey 进行开发，以下只作为演示 sdk 使用
    		//int appid = 1400025616;
    		//String appkey = "13776d99625a7c0248fe1a9382bfc8b5";
    		
    		
    		
    		int appid = 1400045042;
    		String appkey = "4137e296481ae55a3697842fa0004ff8";
    		//int tmplId = 49492; //验证码
    		//int tmplId = 49820; //湖北院任务提醒
    		int tmplId = 49821; //尊敬的{1}{2}
    		
    		
    		
    		
    		String content = "";
    		
    		/*int appid = 1400028065;
    		String appkey = "185f37464b8e9c44d2bb872d4ad86427";
    		*/
    		String phoneNumber1 = "15827188633";
    		//String phoneNumber2 = "13006303992";
    		//String phoneNumber3 = "15527242595";
    		//测试一模板id
    		//int tmplId = 11799;
    		
    		//SendTest test = new SendTest(content, phoneNumber1,appid,appkey);
    		
    		/*int tmplId = 14874;*/

    		 //初始化单发
	    	SmsSingleSender singleSender = new SmsSingleSender(appid, appkey);
	    	SmsSingleSenderResult singleSenderResult;
	
	    	 //普通单发
	    //singleSenderResult = singleSender.send(0, "86", phoneNumber1, "jdfkasljfkdlasj", "", "");
	    	//System.out.println(singleSenderResult);
	
	    	 //指定模板单发
	    	 //假设短信模板 id 为 1，模板内容为：测试短信，{1}，{2}，{3}，上学。
	    	ArrayList<String> params = new ArrayList<>();
	    	params.add("刘迟");
	    	params.add("武汉");
	    	//params.add("小明");
	    	singleSenderResult = singleSender.sendWithParam("86", phoneNumber1, tmplId, params, "", "", "");
	    	
	    	// 初始化群发
	    	SmsMultiSender multiSender = new SmsMultiSender(appid, appkey);
	    	SmsMultiSenderResult multiSenderResult;
	
	    	// 普通群发
	    	// 下面是 3 个假设的号码
	    	ArrayList<String> phoneNumbers = new ArrayList<>();
	    	phoneNumbers.add(phoneNumber1);
	    	//phoneNumbers.add(phoneNumber2);
	    	//phoneNumbers.add(phoneNumber3);
	        //multiSenderResult = multiSender.send(0, "86", phoneNumbers, "请及时缴纳欠费", "", "");
	    	//System.out.println(multiSenderResult);

	    	// 指定模板群发
	    	// 假设短信模板 id 为 1，模板内容为：测试短信，{1}，{2}，{3}，上学。
	    	params = new ArrayList<>();
	    	params.add("刘迟");
	    	params.add("深圳");
	    	//params.add("小明");
	    	multiSenderResult = multiSender.sendWithParam("86", phoneNumbers, tmplId, params, "", "", "");
	    	System.out.println(multiSenderResult);
	    	
    		
    		//拉取短信回执和回复
    		//SmsStatusPuller pullstatus = new SmsStatusPuller(appid,appkey);
    		//SmsStatusPullCallbackResult callback_result = pullstatus.pullCallback(100);
    		//System.out.println(callback_result);
    		//SmsStatusPullReplyResult reply_result = pullstatus.pullReply(100);
    		//System.out.println(reply_result);
	    	
    		//另加的短信统计
	    	//SmsStatusSendPuller sendPuller = new SmsStatusSendPuller(appid,appkey);
	    	//SmsStatusSendPullerResult sendPuller_result;
	    	//sendPuller_result = sendPuller.pull("2017032901", "2017033123");
	    	//System.out.println(sendPuller_result);
    		
    	} catch (Exception e) {
			e.printStackTrace();
		}
    }
}