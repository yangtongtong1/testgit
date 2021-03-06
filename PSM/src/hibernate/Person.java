package hibernate;
// Generated 2017-2-28 9:59:26 by Hibernate Tools 5.1.0.Beta1

/**
 * Person generated by hbm2java
 */
public class Person implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String name;
	private String unitName;
	private String job;
	private String identityNo;
	private String phoneNo;
	private String type;
	private String userPwd;

	public Person() {
	}

	public Person(String name, String unitName, String job, String identityNo, String phoneNo, String type,
			String userPwd) {
		this.name = name;
		this.unitName = unitName;
		this.job = job;
		this.identityNo = identityNo;
		this.phoneNo = phoneNo;
		this.type = type;
		this.userPwd = userPwd;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUnitName() {
		return this.unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getJob() {
		return this.job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getIdentityNo() {
		return this.identityNo;
	}

	public void setIdentityNo(String identityNo) {
		this.identityNo = identityNo;
	}

	public String getPhoneNo() {
		return this.phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUserPwd() {
		return this.userPwd;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

}
