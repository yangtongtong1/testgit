package hibernate;
// Generated 2017-4-19 10:22:30 by Hibernate Tools 5.2.0.CR1

/**
 * Anweihui generated by hbm2java
 */
public class Anweihui implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String esorad;
	private String time;
	private String head;
	private String viceHead;
	private String form;
	private String agency;
	private String accessory;
	private String projectName;

	public Anweihui() {
	}

	public Anweihui(String esorad, String time, String head, String viceHead, String form, String agency,
			String accessory, String projectName) {
		this.esorad = esorad;
		this.time = time;
		this.head = head;
		this.viceHead = viceHead;
		this.form = form;
		this.agency = agency;
		this.accessory = accessory;
		this.projectName = projectName;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEsorad() {
		return this.esorad;
	}

	public void setEsorad(String esorad) {
		this.esorad = esorad;
	}

	public String getTime() {
		return this.time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getHead() {
		return this.head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getViceHead() {
		return this.viceHead;
	}

	public void setViceHead(String viceHead) {
		this.viceHead = viceHead;
	}

	public String getForm() {
		return this.form;
	}

	public void setForm(String form) {
		this.form = form;
	}

	public String getAgency() {
		return this.agency;
	}

	public void setAgency(String agency) {
		this.agency = agency;
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
