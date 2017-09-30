package com.ef.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ef.model.LogIp;
import com.ef.util.DB;

public class Dao extends DB {	
	private PreparedStatement pre = null;	
	
	public Dao() {
		super();
	}
	
	public void insertLog(String sql, List<LogIp> logIps) {
		pre = getPrep(sql);
		setAutocommit(false);
		try {
			for (LogIp logIp : logIps) {
				pre.setString(1, logIp.getIpAddress());
				pre.setTimestamp(2, new Timestamp(logIp.getLogTime().getTime()));
				pre.setString(3, logIp.getIpAddress());
				pre.setTimestamp(4, new Timestamp(logIp.getLogTime().getTime()));				
				pre.addBatch();
			}
			
			pre.executeBatch();
			commit();
		} catch (SQLException e) {
			rollback();
			throw new RuntimeException("An error encountered.", e);
		}
	}
	
	public void insertBlockedIp(String sql, List<String> ips, String comment) {
		pre = getPrep(sql);
		setAutocommit(false);
		try {
			for (String ip : ips) {
				pre.setString(1, ip);
				pre.setString(2,  comment);
				pre.addBatch();
			}
			
			pre.executeBatch();
			commit();
		} catch (SQLException e) {
			rollback();
			throw new RuntimeException("An error occurred, cannot insert sql query." + sql);
		}
	}
	
	public List<String> getIpByThreshold(String sql, Date startDate, Date stopDate, int occurence) {
		pre = getPrep(sql);
		try {
			pre.setTimestamp(1, new Timestamp(startDate.getTime()));
			pre.setTimestamp(2, new Timestamp(stopDate.getTime()));
			pre.setInt(3, occurence);
			ResultSet rs = pre.executeQuery();
			List<String> ips = new ArrayList<String>();			
			while (rs.next()) {
				ips.add(rs.getString("ip_address"));
			}
			
			return ips;
		} catch (Exception e) {			
			throw new RuntimeException("An error encountered.", e);
		}
	}
	
	public void closeAll() {
		close();
	}
}
