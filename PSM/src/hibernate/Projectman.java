package hibernate;
// Generated 2017-2-4 16:27:45 by Hibernate Tools 5.2.0.CR1

/**
 * Projectman generated by hbm2java
 */
public class Projectman implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String userName;
	private String userPwd;
	private String uuid;

	public Projectman() {
	}

	public Projectman(String userName, String userPwd) {
		this.userName = userName;
		this.userPwd = userPwd;
	}

	public Projectman(String userName, String userPwd, String uuid) {
		this.userName = userName;
		this.userPwd = userPwd;
		this.uuid = uuid;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPwd() {
		return this.userPwd;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	public String getUuid() {
		return this.uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

}
