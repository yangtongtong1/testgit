package PSM.SMS;

public class SmsStatusSendPullerResult {

	public class Data {
		int request;
		int success;
		int bill_number;
	}

	int result;
	String errmsg = "";
	Data data;
	
	public String toString() {
		if (null != data) {
			return String.format("SmsStatusSendPullerResult:\n"
					+"result %d\n"
					+"errmsg %s\n"
					+"\trequest %d\n"
					+"\tsuccess %d\n"
					+"\tbill_number %d\n",
					result, errmsg, data.request, data.success,
					data.bill_number);
		}
		return String.format("SmsStatusPullerResult:\n"
				+"result %d errmsg %s", result, errmsg);
	}
}
