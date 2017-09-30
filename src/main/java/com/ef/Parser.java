package com.ef;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.ef.model.LogIp;
import com.ef.service.IpService;
import com.ef.util.Util;

public class Parser {
	private static String tmpStartDate;
	private static String duration;
	private static int threshold;
	private final static String configFile = "./config.properties";
	private static Date startDate;
	private static Date endDate;

	public static void main(String args[]) {
		try {
			for(int i = 0; i < args.length; i++) {
				if (args[i] != null) {
					if (args[i].split("=")[0].equals("--startDate") && args[i].split("=")[1] != null) {
						tmpStartDate = args[i].split("=")[1];
					} else if (args[i].split("=")[0].equals("--duration") && args[i].split("=")[1] != null) {
						duration = args[i].split("=")[1];
					} else if (args[i].split("=")[0].equals("--threshold") && args[i].split("=")[1] != null) {
						threshold = Integer.parseInt(args[i].split("=")[1]);
					}
				}
	        }
		} catch (Exception e) {
			throw new RuntimeException("Invalid parameters.", e);
		}

		//validation
		if (!duration.equals("daily") && !duration.equals("hourly")) {
			throw new RuntimeException("Duration takes only daily & hourly values.");
		}
		
		if (!(threshold > 0)) {
			throw new RuntimeException("Invalid threshold value");
		}
		
		try {
			startDate = Util.getDateFormat().parse(tmpStartDate);

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(startDate);

			if (duration.equals("daily")) {
				calendar.add(Calendar.DATE, 1);
				endDate = calendar.getTime();
			} else if (duration.equals("hourly")) {
				calendar.add(Calendar.HOUR, 1);
				endDate = calendar.getTime();
			}
		} catch (ParseException e) {
			throw new RuntimeException("Cannot parse start date.", e);
		}
		
		Properties configProp = Util.loadProperties(configFile);

		List<LogIp> logIps;
		
		Map<String, Integer> ipOccurence = new HashMap<String, Integer>();
		IpService svc = IpService.getInstance();
		try {
			logIps = Util.getLogIps(configProp.getProperty("log_file"));
			if (logIps != null && logIps.size() > 0) {
				String insertLogSQL = "INSERT INTO server_log (ip_address, log_time) "
							+ "SELECT * FROM (SELECT ?, ?) AS tmp "
							+ "WHERE NOT EXISTS (SELECT ip_address, log_time FROM server_log WHERE ip_address = ? AND log_time = ?) LIMIT 1";
				
				svc.insertIpLogs(insertLogSQL, logIps);
				
				for(LogIp logIp : logIps) {
					System.out.println("Ip:" + logIp.getIpAddress() + ", Time: " + logIp.getLogTime());
					if (ipOccurence.get(logIp.getIpAddress()) == null) {
						ipOccurence.put(logIp.getIpAddress(), 1);
					} else {
						int currentCount = ipOccurence.get(logIp.getIpAddress());
						ipOccurence.put(logIp.getIpAddress(), currentCount++);
					}					
				}
				
				String getOccurenceSQL = "SELECT count(*) as count, ip_address FROM server_log WHERE log_time BETWEEN ? AND ? GROUP BY ip_address HAVING count >= ?";
				List<String> ips = svc.getIpByThreshold(getOccurenceSQL, startDate, endDate, threshold);
				if (ips.size() > 0) {
					String insertBlockIp = "INSERT INTO blocked(ip_address, date_added, comment) VALUES ( ?, now(), ?)";
					String comment = "Ip address is blocked due to access from " + startDate + " to " + endDate + " more than or equal to " + threshold;
					svc.insertBlockedIp(insertBlockIp, ips, comment);
				}
				svc.closeDB();
			}
		} catch (ParseException e) {
			//should be a Logger
			System.out.println("ERROR: Cannot parse date");
			e.printStackTrace();
		}
	}
}
