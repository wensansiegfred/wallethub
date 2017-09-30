package com.ef.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.ef.model.LogIp;

public class Util {
	public static Properties loadProperties(String path) {
		Properties props = new Properties();
		FileInputStream in = null;

		try {
			in = new FileInputStream(path);
			if (in != null) {
				props.load(in);
			}
		} catch (IOException e) {
			System.out.println("Cannot open file: " + path);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {}
			}
		}
		return props;
	}

	public static List<LogIp> getLogIps(String logPath) throws ParseException {
		BufferedReader bufferReader = null;
		List<LogIp> logIps = new ArrayList<LogIp>();
		try {
			String log;
			bufferReader = new BufferedReader(new FileReader(logPath));
			while((log = bufferReader.readLine()) != null) {
				String[] splitLog = log.split("\\|");
				logIps.add(new LogIp(splitLog[0], getDateFormat().parse(splitLog[1])));
			}
		} catch(IOException e) {
			//this should be a Logger
			System.out.println("ERROR: File not found: " + logPath);
			e.printStackTrace();
		} finally {
			try {
                if (bufferReader != null) {
                	bufferReader.close();
                }
            } catch (IOException ex) {
            	//this should be a Logger
            	System.out.println("ERROR: Cannot Read File: ");
                ex.printStackTrace();
            }
		}

		return logIps;
	}

	public static DateFormat getDateFormat() {
		return new SimpleDateFormat("yyyy-MM-dd.HH:mm:ss");
	}
}
