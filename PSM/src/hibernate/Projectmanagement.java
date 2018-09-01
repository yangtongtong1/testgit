package hibernate;
// Generated 2017-2-2 1:00:45 by Hibernate Tools 5.2.0.CR1

import java.util.Date;

/**
 * Projectmanagement generated by hbm2java
 */
public class Projectmanagement implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String no;
	private String name;
	private String scale;
	private String buildUnit;
	private String place;
	private String price;
	private String manager;
	private Date startDate;
	private String schedule;
	private String content;
	private String cost;
	private String progress;

	public Projectmanagement() {
	}

	public Projectmanagement(String no, String name, String scale, String buildUnit, String place, String price,
			String manager, Date startDate, String schedule, String content, String cost, String progress) {
		this.no = no;
		this.name = name;
		this.scale = scale;
		this.buildUnit = buildUnit;
		this.place = place;
		this.price = price;
		this.manager = manager;
		this.startDate = startDate;
		this.schedule = schedule;
		this.content = content;
		this.cost = cost;
		this.progress = progress;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNo() {
		return this.no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getScale() {
		return this.scale;
	}

	public void setScale(String scale) {
		this.scale = scale;
	}

	public String getBuildUnit() {
		return this.buildUnit;
	}

	public void setBuildUnit(String buildUnit) {
		this.buildUnit = buildUnit;
	}

	public String getPlace() {
		return this.place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getPrice() {
		return this.price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getManager() {
		return this.manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getSchedule() {
		return this.schedule;
	}

	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCost() {
		return this.cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public String getProgress() {
		return this.progress;
	}

	public void setProgress(String progress) {
		this.progress = progress;
	}

}
