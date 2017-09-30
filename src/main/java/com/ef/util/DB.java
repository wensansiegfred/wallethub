package com.ef.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

public class DB {
	protected String username;
	protected String password;
	protected String driver;
	protected Connection con = null;
	protected String url;
	protected Statement stmt = null;
	protected PreparedStatement pstmt = null;
	private final static String configFile = "./config.properties";
	
	public DB() {		
		Properties configProp = Util.loadProperties(configFile);
		this.username = configProp.getProperty("db.user");
		this.password = configProp.getProperty("db.password");
		this.url = configProp.getProperty("db.url");
		this.driver = configProp.getProperty("db.driver");
		
		connect();
	}

	protected void connect() {
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Driver not in classpath: " + driver, e);
		}

		try {
			con = DriverManager.getConnection(url, username, password);
			if (con == null) {
				throw new RuntimeException("Cannot connect to DB server");
			}
		} catch (SQLException sql){
			sql.printStackTrace();
	    } catch (Exception e){
			e.printStackTrace();
		}
	}

	protected ResultSet selectQuery(String sql) {
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			return rs;
		} catch (SQLException e) {
			throw new RuntimeException("ERROR: Cannot perform your query statement: " + sql, e);
		}
	}

	protected void insertBatch(List<String> sql) {
		try {
			stmt = con.createStatement();
			for (String q : sql) {
				stmt.addBatch(q);
			}
			stmt.executeBatch();
		} catch (SQLException e) {
			throw new RuntimeException("ERROR: Cannot perform your query statement: " + sql, e);
		}
	}

	protected void insertSql(String sql) {
		try {
			stmt = con.createStatement();
			stmt.execute(sql);
		} catch (SQLException e) {
			throw new RuntimeException("ERROR: Cannot perform your query statement: " + sql, e);
		}
	}
	
	protected PreparedStatement getPrep(String sql) {		
		try {
			pstmt = con.prepareStatement(sql);
		} catch (SQLException e) {
			throw new RuntimeException("ERROR: Cannot execute Preparedstatement.", e);
		}
		
		return pstmt;
	}
	
	protected void commit() {
		try {
			con.commit();
		} catch (SQLException e) {
			throw new RuntimeException("ERROR: Cannot execute commit.", e);
		}
	}
	
	protected void rollback() {
		try {
			con.rollback();
		} catch (SQLException e) {
			throw new RuntimeException("ERROR: Cannot execute rollback.", e);
		}
	}
	
	protected void setAutocommit(boolean commit) {
		if (con != null)
			try {
				con.setAutoCommit(commit);
			} catch (SQLException e) {
				throw new RuntimeException("ERROR: Cannot performing auto commit.", e);
			}
	}
	
	protected void close() {
		try {
			if (con != null)
				con.close();
			if (stmt != null)
				stmt.close();
			if (pstmt != null)
				pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
