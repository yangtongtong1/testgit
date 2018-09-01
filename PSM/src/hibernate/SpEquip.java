package hibernate;
// Generated 2017-6-1 10:46:53 by Hibernate Tools 5.1.0.Beta1

/**
 * SpEquip generated by hbm2java
 */
public class SpEquip implements java.io.Serializable {

	private Integer id;
	private String name;
	private String type;
	private String purpose;
	private String inDate;
	private String outDate;
	private String registNo;
	private String kind;
	private String manuUnit;
	private String purchaseDate;
	private String installUnit;
	private String checkStatus;
	private String useStatus;
	private String majorStatus;
	private String otherStatus;
	private String accessory;
	private String projectName;

	public SpEquip() {
	}

	public SpEquip(String name, String type, String purpose, String inDate, String outDate, String registNo,
			String kind, String manuUnit, String purchaseDate, String installUnit, String checkStatus, String useStatus,
			String majorStatus, String otherStatus, String accessory, String projectName) {
		this.name = name;
		this.type = type;
		this.purpose = purpose;
		this.inDate = inDate;
		this.outDate = outDate;
		this.registNo = registNo;
		this.kind = kind;
		this.manuUnit = manuUnit;
		this.purchaseDate = purchaseDate;
		this.installUnit = installUnit;
		this.checkStatus = checkStatus;
		this.useStatus = useStatus;
		this.majorStatus = majorStatus;
		this.otherStatus = otherStatus;
		this.accessory = accessory;
		this.projectName = projectName;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPurpose() {
		return this.purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getInDate() {
		return this.inDate;
	}

	public void setInDate(String inDate) {
		this.inDate = inDate;
	}

	public String getOutDate() {
		return this.outDate;
	}

	public void setOutDate(String outDate) {
		this.outDate = outDate;
	}

	public String getRegistNo() {
		return this.registNo;
	}

	public void setRegistNo(String registNo) {
		this.registNo = registNo;
	}

	public String getKind() {
		return this.kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getManuUnit() {
		return this.manuUnit;
	}

	public void setManuUnit(String manuUnit) {
		this.manuUnit = manuUnit;
	}

	public String getPurchaseDate() {
		return this.purchaseDate;
	}

	public void setPurchaseDate(String purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public String getInstallUnit() {
		return this.installUnit;
	}

	public void setInstallUnit(String installUnit) {
		this.installUnit = installUnit;
	}

	public String getCheckStatus() {
		return this.checkStatus;
	}

	public void setCheckStatus(String checkStatus) {
		this.checkStatus = checkStatus;
	}

	public String getUseStatus() {
		return this.useStatus;
	}

	public void setUseStatus(String useStatus) {
		this.useStatus = useStatus;
	}

	public String getMajorStatus() {
		return this.majorStatus;
	}

	public void setMajorStatus(String majorStatus) {
		this.majorStatus = majorStatus;
	}

	public String getOtherStatus() {
		return this.otherStatus;
	}

	public void setOtherStatus(String otherStatus) {
		this.otherStatus = otherStatus;
	}

	public String getAccessory() {
		return this.accessory;
	}

	public void setAccessory(String accessory) {
		this.accessory = accessory;
	}

	public String getProjectName() {
		return this.projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

}
