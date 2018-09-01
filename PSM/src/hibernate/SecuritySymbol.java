package hibernate;

public class SecuritySymbol {
	private Integer id;
	private String name;//安全标志、标识名称
	private String num;//数量
	private String department;//单位
	private String installTime;//安装时间
	private String installPlace;//安装地点
	private String responsibility;//责任人
	private String accessory;
	
	public SecuritySymbol(){}
	
	public SecuritySymbol(Integer id, String name, String num, String department, String installTime,
			String installPlace, String responsibility) {
		super();
		this.id = id;
		this.name = name;
		this.num = num;
		this.department = department;
		this.installTime = installTime;
		this.installPlace = installPlace;
		this.responsibility = responsibility;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getInstallTime() {
		return installTime;
	}
	public void setInstallTime(String installTime) {
		this.installTime = installTime;
	}
	public String getInstallPlace() {
		return installPlace;
	}
	public void setInstallPlace(String installPlace) {
		this.installPlace = installPlace;
	}
	public String getResponsibility() {
		return responsibility;
	}
	public void setResponsibility(String responsibility) {
		this.responsibility = responsibility;
	}
	public String getAccessory() {
		return this.accessory;
	}
	public void setAccessory(String accessory) {
		this.accessory = accessory;
	}
	
}
