package com.ef.service;

import java.util.Date;
import java.util.List;

import com.ef.dao.Dao;
import com.ef.model.LogIp;

public class IpService {
	private Dao dao;
	private static final IpService instance = new IpService();

	private IpService() {
		dao = new Dao();
	}

	public static IpService getInstance() {
		return instance;
	}

	public void insertIpLogs(String sql, List<LogIp> logIps) {
		dao.insertLog(sql, logIps);
	}

	public List<String> getIpByThreshold(String sql, Date startDate, Date stopDate, int occurence) {
		return dao.getIpByThreshold(sql, startDate, stopDate, occurence);
	}

	public void insertBlockedIp(String sql, List<String> ips, String comment) {
		dao.insertBlockedIp(sql, ips, comment);
	}

	public List<LogIp> getLogById(String sql, String ip) {
		return dao.getLogById(sql, ip);
	}

	public void closeDB() {
		dao.closeAll();
	}
}
