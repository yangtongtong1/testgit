package hibernate;

public class Saftycosttj {
	private Integer id;
	private String subjectnum;
	private String costkind;
	private String year;
	private String cost;
	private String costrealtime;
	private String accessory;
	
	
	public Saftycosttj() {
		
	}


	public Saftycosttj(Integer id, String subjectnum, String costkind, String year, String cost, String costrealtime,
			String accessory) {
		super();
		this.id = id;
		this.subjectnum = subjectnum;
		this.costkind = costkind;
		this.year = year;
		this.cost = cost;
		this.costrealtime = costrealtime;
		this.accessory = accessory;
	}


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public String getSubjectnum() {
		return subjectnum;
	}


	public void setSubjectnum(String subjectnum) {
		this.subjectnum = subjectnum;
	}


	public String getCostkind() {
		return costkind;
	}


	public void setCostkind(String costkind) {
		this.costkind = costkind;
	}


	public String getYear() {
		return year;
	}


	public void setYear(String year) {
		this.year = year;
	}


	public String getCost() {
		return cost;
	}


	public void setCost(String cost) {
		this.cost = cost;
	}


	public String getCostrealtime() {
		return costrealtime;
	}


	public void setCostrealtime(String costrealtime) {
		this.costrealtime = costrealtime;
	}


	public String getAccessory() {
		return accessory;
	}


	public void setAccessory(String accessory) {
		this.accessory = accessory;
	}


	@Override
	public String toString() {
		return "Saftycosttj [subjectnum=" + subjectnum + ", costkind=" + costkind + ", year=" + year + ", cost=" + cost
				+ ", costrealtime=" + costrealtime + ", accessory=" + accessory + "]";
	}
	
	
	
	
	
	
}
