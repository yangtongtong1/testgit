package hibernate;
// Generated 2017-5-23 14:36:41 by Hibernate Tools 5.2.1.Final

import java.util.Date;

/**
 * Daibanrecord generated by hbm2java
 */
public class Daibanrecord implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String holiday;
	private Date ondutytime;
	private String ondutyperson;
	private String nextperson;
	private String accessory;
	private String projectName;

	public Daibanrecord() {
	}

	public Daibanrecord(String holiday, Date ondutytime, String ondutyperson, String nextperson, String accessory,
			String projectName) {
		this.holiday = holiday;
		this.ondutytime = ondutytime;
		this.ondutyperson = ondutyperson;
		this.nextperson = nextperson;
		this.accessory = accessory;
		this.projectName = projectName;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getHoliday() {
		return this.holiday;
	}

	public void setHoliday(String holiday) {
		this.holiday = holiday;
	}

	public Date getOndutytime() {
		return this.ondutytime;
	}

	public void setOndutytime(Date ondutytime) {
		this.ondutytime = ondutytime;
	}

	public String getOndutyperson() {
		return this.ondutyperson;
	}

	public void setOndutyperson(String ondutyperson) {
		this.ondutyperson = ondutyperson;
	}

	public String getNextperson() {
		return this.nextperson;
	}

	public void setNextperson(String nextperson) {
		this.nextperson = nextperson;
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
