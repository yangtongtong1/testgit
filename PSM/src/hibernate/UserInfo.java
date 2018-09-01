package hibernate;

public class UserInfo {
	private String enrollNumber;//�����û�ID
	private String name;//�û���
	private String password;//����
	private String privilege;//�û�Ȩ�ޣ�3 Ϊ����Ա��0 Ϊ��ͨ�û�
	private String enabled;//�û����ñ�־��1 Ϊ���ã�0 Ϊ����
	
	public String getEnrollNumber(){
		return enrollNumber;
	}
	public void setEnrollNumber(String EnrollNumber){
		this.enrollNumber = EnrollNumber;
	}
	
	public String getName(){
		return name;
	}
	public void setName(String Name){
		this.name = Name;
	}
	
	public String getPassword(){
		return password;
	}
	public void setPassword(String Password){
		this.password = Password;
	}
	
	public String getPrivilege(){
		return privilege;
	}
	public void setPrivilege(String Privilege){
		this.privilege= Privilege;
	}
	
	public String getEnabled(){
		return enabled;
	}
	public void setEnabled(String Enabled){
		this.enabled= Enabled;
	}
}
