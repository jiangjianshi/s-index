
package com.huifenqi.index.conf;

import java.io.File;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;




/**
 * 配置接口对象
 * @author majianchun
 * 
 *
 */
public class ConfigFactory{
	
	private static final String CONFIG_FILE_DEFAULT_PATH ="config/app_config.xml";
	private static XMLConfiguration config = null;
	private static File path;
	//方便在系统启动时候
	public static void init(String configFilePath){
		//如果未配置获取默认值
		if(configFilePath==null){
			configFilePath = CONFIG_FILE_DEFAULT_PATH;
		}
		try {
			File p=new File(configFilePath);
			config = new XMLConfiguration(configFilePath);
			
			config.setReloadingStrategy(new FileChangedReloadingStrategy());
			config.setDelimiterParsingDisabled(true); 
			path=p.getParentFile();
		} catch (ConfigurationException e) {
			System.out.println("Fatal:Create Config Object Error!!!");
			System.exit(1);
		}
	}
	public static File getPath()
	{
		return path;
	}
	//不允许外部实例化
	private ConfigFactory(){
	}

	/**
	 * 获取配置的字符串值
	 * @param configXPath  配置项路径
	 * @return
	 */
	public static String getString(String configXPath){
		return config.getString(configXPath, null);
	}
	
	/**
	 * 获取配置的字符串值
	 * @param configXPath  配置项路径
	 * @param defaultValue 配置项的默认值
	 * @return
	 */
	public static String getString(String configXPath,String defaultValue){
		return config.getString(configXPath, defaultValue);
	}
	
	/**
	 * 获取配置的整数值
	 * @param configXPath  配置项路径
	 * @return
	 */
	public static int getInt(String configXPath){
		return config.getInt(configXPath);
	}
	
	/**
	 * 获取float型类型参数
	 * @param configXPath
	 * @param defaultValue
	 * @return
	 */
	public static float getFloat(String configXPath,float defaultValue){
		return config.getFloat(configXPath);
	}
	
	/**
	 * 获取配置的整数值
	 * @param configXPath  配置项路径
	 * @param defaultValue 配置项的默认值
	 * @return
	 */
	public static int getInt(String configXPath,int defaultValue){
		return config.getInt(configXPath,defaultValue);
	}
	public static long getLong(String configXPath,long defaultValue){
		return config.getLong(configXPath,defaultValue);
	}
	
	/**
	 * 获取配置的boolean值
	 * @param configXPath  配置项路径
	 * @return
	 */
	public static boolean getBoolean(String configXPath){
		return config.getBoolean(configXPath);
	}
	
	/**
	 * 获取配置的boolean值
	 * @param configXPath  配置项路径
	 * @param defaultValue 配置项的默认值
	 * @return
	 */
	public static boolean getBoolean(String configXPath,boolean defaultValue){
		return config.getBoolean(configXPath, defaultValue);
	}
	


	
///////////////////////////////////////////
	/**
	 * 获取配置的字符串值
	 * @param configXPath  配置项路径
	 * @return
	 */
	public static void setString(String configXPath,String value){
		 config.setProperty(configXPath, value);
	}
		
	/**
	 * 获取配置的整数值
	 * @param configXPath  配置项路径
	 * @return
	 */
	public static void setInt(String configXPath,int value){
		config.setProperty(configXPath,value);
	}
	
	/**
	 * 获取float型类型参数
	 * @param configXPath
	 * @param defaultValue
	 * @return
	 */
	public static void setFloat(String configXPath,float defaultValue){
		 config.setProperty(configXPath,defaultValue);
	}
	
	/**
	 * 设置float型类型参数
	 * @param configXPath
	 * @param defaultValue
	 * @return
	 */
	public static void setLong(String configXPath,long defaultValue){
		 config.setProperty(configXPath,defaultValue);
	}
	

	/**
	 * 获取配置的boolean值
	 * @param configXPath  配置项路径
	 * @param defaultValue 配置项的默认值
	 * @return
	 */
	public static void setBoolean(String configXPath,boolean defaultValue){
		 config.setProperty(configXPath, defaultValue);
	}
	
	/**
	 * 获取配置的List值
	 * @param configXPath 配置项路径
	 * @return
	 */
	public static void setList(String configXPath, List<String>  list) {
		 config.setProperty(configXPath,list);
	}
	/**
	 * 删除指定配置项
	 * @param key
	 */
	public static void remove(String key)
	{
		config.clearTree(key);
	}
	/**
	 * 
	 */
	public static void save() {
		try {
			config.save();
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
