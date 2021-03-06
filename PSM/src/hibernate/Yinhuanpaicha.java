package hibernate;
// Generated 2016-11-7 16:44:39 by Hibernate Tools 5.1.0.Beta1

import java.math.BigDecimal;
import java.util.Date;

/**
 * Yinhuanpaicha generated by hbm2java
 */
public class Yinhuanpaicha implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String hiddenTrouble;
	private String place;
	private String findMan;
	private String rank;
	private String action;
	private String unit;
	private String inChargeMan;
	private Date planTime;
	private BigDecimal cost;
	private Date realTime;
	private String supervisor;
	private String preventAction;
	private Integer fileNo;
	private String accessory;

	public Yinhuanpaicha() {
	}

	public Yinhuanpaicha(String hiddenTrouble, String place, String findMan, String rank, String action, String unit,
			String inChargeMan, Date planTime, BigDecimal cost, Date realTime, String supervisor, String preventAction,
			Integer fileNo, String accessory) {
		this.hiddenTrouble = hiddenTrouble;
		this.place = place;
		this.findMan = findMan;
		this.rank = rank;
		this.action = action;
		this.unit = unit;
		this.inChargeMan = inChargeMan;
		this.planTime = planTime;
		this.cost = cost;
		this.realTime = realTime;
		this.supervisor = supervisor;
		this.preventAction = preventAction;
		this.fileNo = fileNo;
		this.accessory = accessory;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getHiddenTrouble() {
		return this.hiddenTrouble;
	}

	public void setHiddenTrouble(String hiddenTrouble) {
		this.hiddenTrouble = hiddenTrouble;
	}

	public String getPlace() {
		return this.place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getFindMan() {
		return this.findMan;
	}

	public void setFindMan(String findMan) {
		this.findMan = findMan;
	}

	public String getRank() {
		return this.rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public String getAction() {
		return this.action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getUnit() {
		return this.unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getInChargeMan() {
		return this.inChargeMan;
	}

	public void setInChargeMan(String inChargeMan) {
		this.inChargeMan = inChargeMan;
	}

	public Date getPlanTime() {
		return this.planTime;
	}

	public void setPlanTime(Date planTime) {
		this.planTime = planTime;
	}

	public BigDecimal getCost() {
		return this.cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public Date getRealTime() {
		return this.realTime;
	}

	public void setRealTime(Date realTime) {
		this.realTime = realTime;
	}

	public String getSupervisor() {
		return this.supervisor;
	}

	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}

	public String getPreventAction() {
		return this.preventAction;
	}

	public void setPreventAction(String preventAction) {
		this.preventAction = preventAction;
	}

	public Integer getFileNo() {
		return this.fileNo;
	}

	public void setFileNo(Integer fileNo) {
		this.fileNo = fileNo;
	}

	public String getAccessory() {
		return this.accessory;
	}

	public void setAccessory(String accessory) {
		this.accessory = accessory;
	}

}
