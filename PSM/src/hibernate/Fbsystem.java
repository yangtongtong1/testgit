package hibernate;
// Generated 2017-4-10 9:48:09 by Hibernate Tools 5.1.0.Beta1

import java.util.Date;

/**
 * Fbsystem generated by hbm2java
 */
public class Fbsystem implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String fbUnit;
	private Date uploadDate;
	private String accessory;
	private String projectName;

	public Fbsystem() {
	}

	public Fbsystem(String fbUnit, Date uploadDate, String accessory, String projectName) {
		this.fbUnit = fbUnit;
		this.uploadDate = uploadDate;
		this.accessory = accessory;
		this.projectName = projectName;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFbUnit() {
		return this.fbUnit;
	}

	public void setFbUnit(String fbUnit) {
		this.fbUnit = fbUnit;
	}

	public Date getUploadDate() {
		return this.uploadDate;
	}

	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
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