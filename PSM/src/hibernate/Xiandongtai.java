package hibernate;
// Generated 2018-4-23 10:47:01 by Hibernate Tools 5.2.1.Final

import java.util.Date;

/**
 * Xiandongtai generated by hbm2java
 */
public class Xiandongtai implements java.io.Serializable {

	private Integer id;
	private String repoter;
	private String peixun;
	private String gangwei;
	private String name;
	private String sfz;
	private String sex;
	private Date intimeplan;
	private String intimereal;
	private Date lvetimeplan;
	private String lvetimereal;
	private String phone;
	private String istijian;
	private String isgsbx;
	private String accessory;
	private String projectName;

	public Xiandongtai() {
	}

	public Xiandongtai(String repoter, String peixun, String gangwei, String name, String sfz, String sex,
			Date intimeplan, String intimereal, Date lvetimeplan, String lvetimereal, String phone, String istijian,
			String isgsbx, String accessory, String projectName) {
		this.repoter = repoter;
		this.peixun = peixun;
		this.gangwei = gangwei;
		this.name = name;
		this.sfz = sfz;
		this.sex = sex;
		this.intimeplan = intimeplan;
		this.intimereal = intimereal;
		this.lvetimeplan = lvetimeplan;
		this.lvetimereal = lvetimereal;
		this.phone = phone;
		this.istijian = istijian;
		this.isgsbx = isgsbx;
		this.accessory = accessory;
		this.projectName = projectName;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getRepoter() {
		return this.repoter;
	}

	public void setRepoter(String repoter) {
		this.repoter = repoter;
	}

	public String getPeixun() {
		return this.peixun;
	}

	public void setPeixun(String peixun) {
		this.peixun = peixun;
	}

	public String getGangwei() {
		return this.gangwei;
	}

	public void setGangwei(String gangwei) {
		this.gangwei = gangwei;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSfz() {
		return this.sfz;
	}

	public void setSfz(String sfz) {
		this.sfz = sfz;
	}

	public String getSex() {
		return this.sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public Date getIntimeplan() {
		return this.intimeplan;
	}

	public void setIntimeplan(Date intimeplan) {
		this.intimeplan = intimeplan;
	}

	public String getIntimereal() {
		return this.intimereal;
	}

	public void setIntimereal(String intimereal) {
		this.intimereal = intimereal;
	}

	public Date getLvetimeplan() {
		return this.lvetimeplan;
	}

	public void setLvetimeplan(Date lvetimeplan) {
		this.lvetimeplan = lvetimeplan;
	}

	public String getLvetimereal() {
		return this.lvetimereal;
	}

	public void setLvetimereal(String lvetimereal) {
		this.lvetimereal = lvetimereal;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getIstijian() {
		return this.istijian;
	}

	public void setIstijian(String istijian) {
		this.istijian = istijian;
	}

	public String getIsgsbx() {
		return this.isgsbx;
	}

	public void setIsgsbx(String isgsbx) {
		this.isgsbx = isgsbx;
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
