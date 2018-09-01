package hibernate;
// Generated 2017-4-19 10:22:30 by Hibernate Tools 5.2.0.CR1

import java.util.Date;

/**
 * Safetypromanagement generated by hbm2java
 */
public class Safetypromanagement implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String name;
	private Date time;
	private String sperson;
	private String person;
	private String accessory;
	private String projectName;

	public Safetypromanagement() {
	}

	public Safetypromanagement(String name, Date time, String sperson, String person, String accessory,
			String projectName) {
		this.name = name;
		this.time = time;
		this.sperson = sperson;
		this.person = person;
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

	public Date getTime() {
		return this.time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getSperson() {
		return this.sperson;
	}

	public void setSperson(String sperson) {
		this.sperson = sperson;
	}

	public String getPerson() {
		return this.person;
	}

	public void setPerson(String person) {
		this.person = person;
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
