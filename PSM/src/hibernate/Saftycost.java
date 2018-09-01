package hibernate;
// Generated 2016-10-20 16:15:30 by Hibernate Tools 5.2.0.Beta1

/**
 * Saftycost generated by hbm2java
 */
public class Saftycost implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String subjectnum;
	private String costkind;
	private String costetails;
	private Integer jan;
	private Integer feb;
	private Integer mar;
	private Integer apr;
	private Integer may;
	private Integer june;
	private Integer july;
	private Integer aug;
	private Integer sept;
	private Integer oct;
	private Integer nov;
	private Integer dec;
	private Integer sumcost;
	private Integer planorcost;

	public Saftycost() {
	}

	public Saftycost(String subjectnum, String costkind, Integer sumcost, Integer planorcost) {
		this.subjectnum = subjectnum;
		this.costkind = costkind;
		this.sumcost = sumcost;
		this.planorcost = planorcost;
	}

	public Saftycost(Integer id,String subjectnum, String costkind, String costetails, Integer jan, Integer feb, Integer mar,
			Integer apr, Integer may, Integer june, Integer july, Integer aug, Integer sept, Integer oct, Integer nov,
			Integer dec, Integer sumcost, Integer planorcost) {
		this.id = id;
		this.subjectnum = subjectnum;
		this.costkind = costkind;
		this.costetails = costetails;
		this.jan = jan;
		this.feb = feb;
		this.mar = mar;
		this.apr = apr;
		this.may = may;
		this.june = june;
		this.july = july;
		this.aug = aug;
		this.sept = sept;
		this.oct = oct;
		this.nov = nov;
		this.dec = dec;
		this.sumcost = sumcost;
		this.planorcost = planorcost;
	}

	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSubjectnum() {
		return this.subjectnum;
	}

	public void setSubjectnum(String subjectnum) {
		this.subjectnum = subjectnum;
	}

	public String getCostkind() {
		return this.costkind;
	}

	public void setCostkind(String costkind) {
		this.costkind = costkind;
	}

	public String getCostetails() {
		return this.costetails;
	}

	public void setCostetails(String costetails) {
		this.costetails = costetails;
	}

	public Integer getJan() {
		return this.jan;
	}

	public void setJan(Integer jan) {
		this.jan = jan;
	}

	public Integer getFeb() {
		return this.feb;
	}

	public void setFeb(Integer feb) {
		this.feb = feb;
	}

	public Integer getMar() {
		return this.mar;
	}

	public void setMar(Integer mar) {
		this.mar = mar;
	}

	public Integer getApr() {
		return this.apr;
	}

	public void setApr(Integer apr) {
		this.apr = apr;
	}

	public Integer getMay() {
		return this.may;
	}

	public void setMay(Integer may) {
		this.may = may;
	}

	public Integer getJune() {
		return this.june;
	}

	public void setJune(Integer june) {
		this.june = june;
	}

	public Integer getJuly() {
		return this.july;
	}

	public void setJuly(Integer july) {
		this.july = july;
	}

	public Integer getAug() {
		return this.aug;
	}

	public void setAug(Integer aug) {
		this.aug = aug;
	}

	public Integer getSept() {
		return this.sept;
	}

	public void setSept(Integer sept) {
		this.sept = sept;
	}

	public Integer getOct() {
		return this.oct;
	}

	public void setOct(Integer oct) {
		this.oct = oct;
	}

	public Integer getNov() {
		return this.nov;
	}

	public void setNov(Integer nov) {
		this.nov = nov;
	}

	public Integer getDec() {
		return this.dec;
	}

	public void setDec(Integer dec) {
		this.dec = dec;
	}

	public Integer getSumcost() {
		return this.sumcost;
	}

	public void setSumcost(Integer sumcost) {
		this.sumcost = sumcost;
	}

	public Integer getPlanorcost() {
		return this.planorcost;
	}

	public void setPlanorcost(Integer planorcost) {
		this.planorcost = planorcost;
	}

}
