package hibernate;

import java.sql.Date;

// Generated 2016-10-20 16:15:30 by Hibernate Tools 5.2.0.Beta1

/**
 * Fenbaosaftycostsum generated by hbm2java
 */
public class Fenbaosaftycostsum implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String costkind;
	private Date regtime;
	private String repoter;
	private Integer cost;
	private Integer sumcost;

	public Fenbaosaftycostsum() {

	}

	public Fenbaosaftycostsum(Integer id, String costkind, Date regtime, String repoter, Integer cost,
			Integer sumcost) {
		super();
		this.id = id;
		this.costkind = costkind;
		this.regtime = regtime;
		this.repoter = repoter;
		this.cost = cost;
		this.sumcost = sumcost;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCostkind() {
		return costkind;
	}

	public void setCostkind(String costkind) {
		this.costkind = costkind;
	}

	public Date getRegtime() {
		return regtime;
	}

	public void setRegtime(Date regtime) {
		this.regtime = regtime;
	}

	public String getRepoter() {
		return repoter;
	}

	public void setRepoter(String repoter) {
		this.repoter = repoter;
	}

	public Integer getCost() {
		return cost;
	}

	public void setCost(Integer cost) {
		this.cost = cost;
	}

	public Integer getSumcost() {
		return sumcost;
	}

	public void setSumcost(Integer sumcost) {
		this.sumcost = sumcost;
	}

}