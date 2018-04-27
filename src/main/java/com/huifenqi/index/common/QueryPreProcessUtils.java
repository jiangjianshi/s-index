
package com.huifenqi.index.common;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author majianchun
 *
 */
public class QueryPreProcessUtils {

	private static final Logger LOG = LoggerFactory.getLogger(QueryPreProcessUtils.class);


	private static QueryPreProcessUtils instance = null;
	
	private QueryPreProcessUtils() {
		
	}
	
	
	private static HashMap<String,String> normalizeMap=new HashMap<String,String>();
	static{
		
		normalizeMap.put("一", "1");
		normalizeMap.put("两", "2");
		normalizeMap.put("俩", "2");
		normalizeMap.put("二", "2");
		normalizeMap.put("三", "3");
		normalizeMap.put("四", "4");
		normalizeMap.put("五", "5");
		normalizeMap.put("六", "6");
		normalizeMap.put("七", "7");
		normalizeMap.put("八", "8");
		normalizeMap.put("九", "9");
	    
	}

	
	public static QueryPreProcessUtils getInstance(){
		
		
		if (instance == null) {
			synchronized (QueryPreProcessUtils.class) {
				if (instance == null) {
					try {
						instance= new QueryPreProcessUtils();
					
					} catch (Exception e) {
						LOG.error("", e);
					}
					
				}
			}
		}
		return instance;
	}
	
	public String process(String query) {
		query=normalizeStr(query);
		query = query.toLowerCase();
		query = query.trim();
		return query;
	}
	
	public String normalizeStr(String str)
	{
		String result="";
		for (int i = 0; i < str.length(); i++) {
			String sub=str.substring(i, i+1);
			if (normalizeMap.containsKey(sub))
				result=result+normalizeMap.get(sub);
			else
				result=result+sub;
		    
		}
		return result;
		
	}
	
	
}
