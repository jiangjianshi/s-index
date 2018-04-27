package com.huifenqi.index.index;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.document.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huifenqi.index.common.SQLUtil;
import com.huifenqi.index.conf.SourceConf;


public class ReadExtendSQL {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(DocGenerator.class);
		
	public HashMap<String,String>  f_pic_root_path_map=new HashMap<String,String>();
	public HashMap<String,String>  f_pic_web_path_map=new HashMap<String,String>();
	
	public HashMap<String,String>  f_setting_code_map=new HashMap<String,String>();
	public HashMap<String,String>  f_setting_nums_map=new HashMap<String,String>();
	public HashMap<String,String>  f_category_type_map=new HashMap<String,String>();
	
	
	
	
    public void readPicData(SourceConf sc)
    {
    	Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rst = null;
		
		try {
			conn = SQLUtil.getConnection(sc.name);
			String strSql=sc.picsql;
			LOG.info(strSql);
			pstmt = conn.prepareStatement(strSql);
			rst = pstmt.executeQuery();
			while (rst.next()) {

               String f_house_sell_id=rst.getString("f_house_sell_id");
               String f_room_id=rst.getString("f_room_id");
               String f_pic_root_path=rst.getString("f_pic_root_path");
               String f_pic_web_path=rst.getString("f_pic_web_path");

               String key=f_house_sell_id+","+f_room_id;
               
               //oss图片
               if(f_pic_root_path_map.containsKey(key))
               {
            		String value=f_pic_root_path_map.get(key);
            		value=value+"|"+f_pic_root_path;
            		f_pic_root_path_map.put(key, value);
               }else{
            		   
            		f_pic_root_path_map.put(key, f_pic_root_path);
               }
               //外部图片
               if(f_pic_web_path_map.containsKey(key))
               {
                    String value=f_pic_web_path_map.get(key);
                    value=value+"|"+f_pic_web_path;
                    f_pic_web_path_map.put(key, value);
               }else{
                       
                    f_pic_web_path_map.put(key, f_pic_web_path);
               }
			}
		} catch (Exception e) {
			LOG.error("{}", e);
		} finally {
			SQLUtil.close(rst);
			SQLUtil.close(pstmt);
		}
    }
    
    
    
    public void readSettingData(SourceConf sc)
    {
    	Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rst = null;
		
		try {
			conn = SQLUtil.getConnection(sc.name);
			String strSql=sc.settingsql;
			LOG.info(strSql);
			pstmt = conn.prepareStatement(strSql);
			rst = pstmt.executeQuery();
			while (rst.next()) {

               String f_house_sell_id=rst.getString("f_house_sell_id");
               String f_room_id=rst.getString("f_room_id");
               
               String f_setting_code=rst.getString("f_setting_code");
               String f_setting_nums=rst.getString("f_setting_nums");
               String f_category_type=rst.getString("f_category_type");
               
               
               String key=f_house_sell_id+","+f_room_id;
               
     
               if(f_setting_code_map.containsKey(key))
               {
            		String value=f_setting_code_map.get(key);
            		value=value+"|"+f_setting_code;
            		f_setting_code_map.put(key, value);
               }
               else{
            		   
            	   f_setting_code_map.put(key, f_setting_code);
               }	
               
               
               if(f_setting_nums_map.containsKey(key))
               {
            		String value=f_setting_nums_map.get(key);
            		value=value+"|"+f_setting_nums;
            		f_setting_nums_map.put(key, value);
               }
               else{
            		   
            	   f_setting_nums_map.put(key, f_setting_nums);
               }	
               
               
               
               if(f_category_type_map.containsKey(key))
               {
            		String value=f_category_type_map.get(key);
            		value=value+"|"+f_category_type;
            		f_category_type_map.put(key, value);
               }
               else{
            		   
            	   f_category_type_map.put(key, f_category_type);
               }	
       
			}
		} catch (Exception e) {
			LOG.error("{}", e);
		} finally {
			SQLUtil.close(rst);
			SQLUtil.close(pstmt);
		}
    }
    
	
	

}
