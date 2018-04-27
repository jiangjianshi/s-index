
package com.huifenqi.index.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huifenqi.index.conf.ConfigFactory;

class DSinfo {
	public String name;
	public String driver;
	public String url;
	public String username;
	public String password;
}

/**
 * @author majianchun
 *
 */
public class SQLUtil {
	
	private static final Logger LOG = LoggerFactory.getLogger(SQLUtil.class);
	
	public static HashMap<String, DSinfo> dsmap = null;	
	public static String getBatchSQL(String dataTable,int offset,int ncount, String sql){
		return sql + " limit "+offset+" ,"+ncount;
	}
	
	public static String getBatchSQL(int offset,int ncount, String sql){
		return sql + " limit "+offset+" ,"+ncount;
	}
	
    public static Connection getConnection(){
    	
    	Connection conn=null;
    	String url=ConfigFactory.getString("jdbc.url");
    	String username=ConfigFactory.getString("jdbc.username");
    	String password=ConfigFactory.getString("jdbc.password");
    	System.out.println(url);
    	System.out.println(username);    	  	
    	try{
    			
	    	Class.forName("com.mysql.jdbc.Driver");  	    	
	        conn = DriverManager.getConnection(url,username,password);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return conn;
    }
    
    public static Connection getConnection(DSinfo info){
    	Connection conn=null;
    	try{
    		String url=info.url;
        	String username=info.username;
        	String password=info.password;
        	Class.forName(info.driver);  
	        conn = DriverManager.getConnection(url,username,password);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return conn;
    }
    
    public static Connection getConnection(String ds) {
    	DSinfo info = dsmap.get(ds);
    	if (info == null) {
    		return null;
    	}
    	return getConnection(info);
    }

    public static void Init() {
    	
    	
    	dsmap = new HashMap<String, DSinfo>();   	
    	int num = ConfigFactory.getInt("ds.num");
    	String base_str = "ds.item(0)";
    	for(int i = 0; i < num; i++) {
    		String str = base_str.replaceAll("0", String.valueOf(i));
    		
    		DSinfo info = new DSinfo();
    		
    		info.name = ConfigFactory.getString(str+".name");
    		
    		info.driver = ConfigFactory.getString(str+".driverClassName");
    		info.url = ConfigFactory.getString(str+".url");
    		info.username = ConfigFactory.getString(str+".username");
    		String password = ConfigFactory.getString(str+".password");			
    		
    		
    		info.password = password;
    		
    		LOG.info(info.name+"||"+info.driver+"||"+info.url+"||"+info.username+"||"+password);
    		
    		dsmap.put(info.name, info);
    	}
    }    
    
	public static void close(Statement stmt) {
		try {
			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
		} catch (Exception e) {

		}
	}

	public static void close(PreparedStatement pstmt) {

		try {
			if (pstmt != null) {
				pstmt.close();
				pstmt = null;
			}
		} catch (Exception e) {

		}
	}

	public static void close(ResultSet rst) {
		try {
			if (rst != null) {
				rst.close();
				rst = null;
			}
		} catch (Exception e) {

		}
	}

	public static void close(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
				conn = null;
			}
		} catch (Exception e) {

		}
	}
}
