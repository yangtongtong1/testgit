package hibernate;
// Generated 2017-4-1 20:03:49 by Hibernate Tools 5.2.0.CR1

import java.util.Date;

/**
 * Yingjifenbao generated by hbm2java
 */
public class Yingjifenbao implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String fenbaoname;
	private Date uploadtime;
	private String filename;
	private String type;
	private String accessory;
	private String projectName;

	public Yingjifenbao() {
	}

	public Yingjifenbao(String fenbaoname, Date uploadtime, String filename, String type, String accessory,
			String projectName) {
		this.fenbaoname = fenbaoname;
		this.uploadtime = uploadtime;
		this.filename = filename;
		this.type = type;
		this.accessory = accessory;
		this.projectName = projectName;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFenbaoname() {
		return this.fenbaoname;
	}

	public void setFenbaoname(String fenbaoname) {
		this.fenbaoname = fenbaoname;
	}

	public Date getUploadtime() {
		return this.uploadtime;
	}

	public void setUploadtime(Date uploadtime) {
		this.uploadtime = uploadtime;
	}

	public String getFilename() {
		return this.filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
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