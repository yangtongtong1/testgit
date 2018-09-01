package hibernate;

public class AttLog {
	private String enrollNumber;//�����û�ID
	private String verifyMode;//��֤��ʽ��0Ϊ������֤��1Ϊָ����֤��2Ϊ����֤
	private String inOutMode;//���ռ�¼�Ŀ���״̬�����庬�����£�Ĭ�� 0��Check-In 1��Check-Out 2��Break-Out 3��Break-In 4��OT-In 5��OT-Out
	private String time;//����ʱ��
	private String workCode;//���ֻ�¼��workCodeֵ
	
	public String getEnrollNumber(){
		return enrollNumber;
	}
	public void setEnrollNumber(String EnrollNumber){
		this.enrollNumber = EnrollNumber;
	}
	
	public String getVerifyMode(){
		return verifyMode;
	}
	public void setVerifyMode(String VerifyMode){
		this.verifyMode = VerifyMode;
	}
	
	public String getInOutMode(){
		return inOutMode;
	}
	public void setInOutMode(String InOutMode){
		this.inOutMode = InOutMode;
	}
	
	public String getTime(){
		return time;
	}
	public void setTime(String Time){
		this.time = Time;
	}
	
	public String getWorkCode(){
		return workCode;
	}
	public void setWorkCode(String WorkCode){
		this.workCode = WorkCode;
	}
}
