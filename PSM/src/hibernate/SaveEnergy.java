package hibernate;

public class SaveEnergy {
	private Integer id;
	private String name;//节能减排专项措施名称
	private String department;//编制单位
	private String time;//编制时间
	private String approval;//是否要求审批
	private String accessory;//附件
	
	public SaveEnergy(){}
	
	public SaveEnergy(Integer id, String name, String department, String time, String approval, String accessory) {
		super();
		this.id = id;
		this.name = name;
		this.department = department;
		this.time = time;
		this.approval = approval;
		this.accessory = accessory;
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
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getApproval() {
		return approval;
	}
	public void setApproval(String approval) {
		this.approval = approval;
	}
	public String getAccessory() {
		return accessory;
	}
	public void setAccessory(String accessory) {
		this.accessory = accessory;
	}
	
}
