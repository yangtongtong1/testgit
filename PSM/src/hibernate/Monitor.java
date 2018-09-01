package hibernate;
// Generated 2016-11-9 22:01:59 by Hibernate Tools 3.4.0.CR1



/**
 * Monitor generated by hbm2java
 */
public class Monitor  implements java.io.Serializable {


     /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
    private String monitorName;
    private Double longitude;
    private Double latitude;
    private String userName;
    private String userPwd;
    private String ipaddress;
    private String port;
    private String mobilePort;
    private String channel;
    private String remarks;
    private Integer defaultpos;
 	private String projectName;

    public Monitor() {
    }

    public Monitor(String monitorName, Double longitude, Double latitude, String userName, String userPwd, String ipaddress, String port, String mobilePort,String channel, String remarks, Integer defaultpos) {
       this.monitorName = monitorName;
       this.longitude = longitude;
       this.latitude = latitude;
       this.userName = userName;
       this.userPwd = userPwd;
       this.ipaddress = ipaddress;
       this.port = port;
       this.mobilePort = mobilePort;
       this.channel = channel;
       this.remarks = remarks;
       this.defaultpos = defaultpos;
    }
   
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    public String getMonitorName() {
        return this.monitorName;
    }
    
    public void setMonitorName(String monitorName) {
        this.monitorName = monitorName;
    }
    public Double getLongitude() {
        return this.longitude;
    }
    
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    public Double getLatitude() {
        return this.latitude;
    }
    
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
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
    public String getIpaddress() {
        return this.ipaddress;
    }
    
    public void setIpaddress(String ipaddress) {
        this.ipaddress = ipaddress;
    }
    public String getPort() {
        return this.port;
    }
    
    public void setPort(String port) {
        this.port = port;
    }
    public String getMobilePort() {
		return mobilePort;
	}

	public void setMobilePort(String mobilePort) {
		this.mobilePort = mobilePort;
	}

	public String getChannel() {
        return this.channel;
    }
    
    public void setChannel(String channel) {
        this.channel = channel;
    }
    public String getRemarks() {
        return this.remarks;
    }
    
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    public Integer getDefaultpos() {
        return this.defaultpos;
    }
    
    public void setDefaultpos(Integer defaultpos) {
        this.defaultpos = defaultpos;
    }

	public String getProjectName() {
		return this.projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}



}


