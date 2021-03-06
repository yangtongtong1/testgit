package hibernate;
// Generated 2017-9-30 14:54:06 by Hibernate Tools 5.1.4.Final

import java.util.Date;

/**
 * ReadilyShoot generated by hbm2java
 */
public class ReadilyShoot implements java.io.Serializable {

	private Integer id;
	private String url;
	private String position;
	private String comment;
	private String uploadedBy;
	private Date uploadTime;
	private Date takeTime;
	private String project;

	public ReadilyShoot() {
	}

	public ReadilyShoot(String url, Date uploadTime, String project) {
		this.url = url;
		this.uploadTime = uploadTime;
		this.project = project;
	}

	public ReadilyShoot(String url, String position, String comment, String uploadedBy, Date uploadTime, Date takeTime,
			String project) {
		this.url = url;
		this.position = position;
		this.comment = comment;
		this.uploadedBy = uploadedBy;
		this.uploadTime = uploadTime;
		this.takeTime = takeTime;
		this.project = project;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPosition() {
		return this.position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getUploadedBy() {
		return this.uploadedBy;
	}

	public void setUploadedBy(String uploadedBy) {
		this.uploadedBy = uploadedBy;
	}

	public Date getUploadTime() {
		return this.uploadTime;
	}

	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}

	public Date getTakeTime() {
		return this.takeTime;
	}

	public void setTakeTime(Date takeTime) {
		this.takeTime = takeTime;
	}

	public String getProject() {
		return this.project;
	}

	public void setProject(String project) {
		this.project = project;
	}

}
