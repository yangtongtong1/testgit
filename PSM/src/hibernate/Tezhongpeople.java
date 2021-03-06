package hibernate;
// Generated 2017-4-18 10:04:40 by Hibernate Tools 5.2.0.CR1

import java.util.Date;

/**
 * Tezhongpeople generated by hbm2java
 */
public class Tezhongpeople implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String fbName;
	private String type;
	private String name;
	private String sex;
	private String cardName;
	private Date cardTime;
	private Date useTime;
	private String cardNo;
	private String ps;
	private String peixun;
	private String cardPlace;
	private String accessory;
	private String projectName;

	public Tezhongpeople() {
	}

	public Tezhongpeople(String fbName, String type, String name, String sex, String cardName, Date cardTime,
			Date useTime, String cardNo, String ps, String peixun, String cardPlace, String accessory,
			String projectName) {
		this.fbName = fbName;
		this.type = type;
		this.name = name;
		this.sex = sex;
		this.cardName = cardName;
		this.cardTime = cardTime;
		this.useTime = useTime;
		this.cardNo = cardNo;
		this.ps = ps;
		this.peixun = peixun;
		this.cardPlace = cardPlace;
		this.accessory = accessory;
		this.projectName = projectName;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFbName() {
		return this.fbName;
	}

	public void setFbName(String fbName) {
		this.fbName = fbName;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return this.sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getCardName() {
		return this.cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	public Date getCardTime() {
		return this.cardTime;
	}

	public void setCardTime(Date cardTime) {
		this.cardTime = cardTime;
	}

	public Date getUseTime() {
		return this.useTime;
	}

	public void setUseTime(Date useTime) {
		this.useTime = useTime;
	}

	public String getCardNo() {
		return this.cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getPs() {
		return this.ps;
	}

	public void setPs(String ps) {
		this.ps = ps;
	}

	public String getPeixun() {
		return this.peixun;
	}

	public void setPeixun(String peixun) {
		this.peixun = peixun;
	}

	public String getCardPlace() {
		return this.cardPlace;
	}

	public void setCardPlace(String cardPlace) {
		this.cardPlace = cardPlace;
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
