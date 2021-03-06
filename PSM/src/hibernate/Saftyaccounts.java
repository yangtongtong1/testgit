package hibernate;
// Generated 2017-3-31 10:34:06 by Hibernate Tools 5.2.0.CR1

import java.util.Date;

/**
 * Saftyaccounts generated by hbm2java
 */
public class Saftyaccounts implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String subjectnum;
	private String costkind;
	private String costetails;
	private String applysector;
	private String costuse;
	private String amount;
	private String manager;
	private String registerperson;
	private Date approtime;
	private String accessory;
	private String remarks;
	private String checksituation;
	private String projectName;

	public Saftyaccounts() {
	}

	public Saftyaccounts(String subjectnum, String costkind, String costetails, String applysector, String costuse,
			String amount, String manager, String registerperson, Date approtime, String accessory, String remarks,
			String checksituation, String projectName) {
		this.subjectnum = subjectnum;
		this.costkind = costkind;
		this.costetails = costetails;
		this.applysector = applysector;
		this.costuse = costuse;
		this.amount = amount;
		this.manager = manager;
		this.registerperson = registerperson;
		this.approtime = approtime;
		this.accessory = accessory;
		this.remarks = remarks;
		this.checksituation = checksituation;
		this.projectName = projectName;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSubjectnum() {
		return this.subjectnum;
	}

	public void setSubjectnum(String subjectnum) {
		this.subjectnum = subjectnum;
	}

	public String getCostkind() {
		return this.costkind;
	}

	public void setCostkind(String costkind) {
		this.costkind = costkind;
	}

	public String getCostetails() {
		return this.costetails;
	}

	public void setCostetails(String costetails) {
		this.costetails = costetails;
	}

	public String getApplysector() {
		return this.applysector;
	}

	public void setApplysector(String applysector) {
		this.applysector = applysector;
	}

	public String getCostuse() {
		return this.costuse;
	}

	public void setCostuse(String costuse) {
		this.costuse = costuse;
	}

	public String getAmount() {
		return this.amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getManager() {
		return this.manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

	public String getRegisterperson() {
		return this.registerperson;
	}

	public void setRegisterperson(String registerperson) {
		this.registerperson = registerperson;
	}

	public Date getApprotime() {
		return this.approtime;
	}

	public void setApprotime(Date approtime) {
		this.approtime = approtime;
	}

	public String getAccessory() {
		return this.accessory;
	}

	public void setAccessory(String accessory) {
		this.accessory = accessory;
	}

	public String getRemarks() {
		return this.remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getChecksituation() {
		return this.checksituation;
	}

	public void setChecksituation(String checksituation) {
		this.checksituation = checksituation;
	}

	public String getProjectName() {
		return this.projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

}
