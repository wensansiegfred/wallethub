package com.ef.model;

import java.util.Date;

public class LogIp {
	private String ipAddress;
	private Date logTime;

	public LogIp(String ipAddress, Date logTime) {
		this.ipAddress = ipAddress;
		this.logTime = logTime;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public Date getLogTime() {
		return logTime;
	}

	public void setLogTime(Date logTime) {
		this.logTime = logTime;
	}
}
