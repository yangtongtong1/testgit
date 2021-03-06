package hibernate;

// Generated 2016-12-20 16:18:17 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

/**
 * Missionstate generated by hbm2java
 */
public class Missionstate implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String missionname;
	private String progroup;
	private String properson;
	private String isdown;
	private Integer score;
	private Date releasetime;
	private Date downtime;
	private Date expecttime;
	private String finishfile;
	private String missionfile;
	private String missiontype;
	private Integer subscore;
	private String finsituation;
	private String misexplain;
	private String releaseperson;

	public Missionstate() {
	}

	public Missionstate(String missionname, String progroup, String properson,
			String isdown, Integer score, Date releasetime, Date downtime, Date expecttime,
			String finishfile, String missionfile, String missiontype,
			Integer subscore, String finsituation, String misexplain,
			String releaseperson) {
		this.missionname = missionname;
		this.progroup = progroup;
		this.properson = properson;
		this.isdown = isdown;
		this.score = score;
		this.releasetime = releasetime;
		this.downtime = downtime;
		this.expecttime = expecttime;
		this.finishfile = finishfile;
		this.missionfile = missionfile;
		this.missiontype = missiontype;
		this.subscore = subscore;
		this.finsituation = finsituation;
		this.misexplain = misexplain;
		this.releaseperson = releaseperson;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMissionname() {
		return this.missionname;
	}

	public void setMissionname(String missionname) {
		this.missionname = missionname;
	}

	public String getProgroup() {
		return this.progroup;
	}

	public void setProgroup(String progroup) {
		this.progroup = progroup;
	}

	public String getProperson() {
		return this.properson;
	}

	public void setProperson(String properson) {
		this.properson = properson;
	}

	public String getIsdown() {
		return this.isdown;
	}

	public void setIsdown(String isdown) {
		this.isdown = isdown;
	}

	public Integer getScore() {
		return this.score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}	

	public Date getReleasetime() {
		return releasetime;
	}

	public void setReleasetime(Date releasetime) {
		this.releasetime = releasetime;
	}

	public Date getDowntime() {
		return this.downtime;
	}

	public void setDowntime(Date downtime) {
		this.downtime = downtime;
	}

	public Date getExpecttime() {
		return this.expecttime;
	}

	public void setExpecttime(Date expecttime) {
		this.expecttime = expecttime;
	}

	public String getFinishfile() {
		return this.finishfile;
	}

	public void setFinishfile(String finishfile) {
		this.finishfile = finishfile;
	}

	public String getMissionfile() {
		return this.missionfile;
	}

	public void setMissionfile(String missionfile) {
		this.missionfile = missionfile;
	}

	public String getMissiontype() {
		return this.missiontype;
	}

	public void setMissiontype(String missiontype) {
		this.missiontype = missiontype;
	}

	public Integer getSubscore() {
		return this.subscore;
	}

	public void setSubscore(Integer subscore) {
		this.subscore = subscore;
	}

	public String getFinsituation() {
		return this.finsituation;
	}

	public void setFinsituation(String finsituation) {
		this.finsituation = finsituation;
	}

	public String getMisexplain() {
		return this.misexplain;
	}

	public void setMisexplain(String misexplain) {
		this.misexplain = misexplain;
	}

	public String getReleaseperson() {
		return this.releaseperson;
	}

	public void setReleaseperson(String releaseperson) {
		this.releaseperson = releaseperson;
	}

}
