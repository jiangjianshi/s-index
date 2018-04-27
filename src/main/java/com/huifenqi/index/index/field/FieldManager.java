package com.huifenqi.index.index.field;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * lucene 索引字段定义管理，来自数据库或者本地
 * @author dingpeng
 *
 */
public class FieldManager
{
	private static final Logger LOG = LoggerFactory.getLogger(FieldManager.class);
	public static LinkedHashMap<String,FieldInfo> getFieldsInfo(String indextype) {
		return getFromLocal(indextype);
	}
	private static LinkedHashMap<String,FieldInfo> getFromLocal(String indextype) {
		LinkedHashMap<String, FieldInfo> result = new LinkedHashMap<String, FieldInfo>();
		//File configFile = new File("e:/workSpace/search-indexing_dev/config/fieldInfo.xml");
		File configFile = new File("/data/www/search-indexing/config/fieldInfo.xml");
		
		SAXBuilder xmlBuilder = new SAXBuilder();
		try {
			Document configDoc = xmlBuilder.build(configFile);
			Element paraElement = configDoc.getRootElement().getChild(indextype);
			if (paraElement != null) {
				@SuppressWarnings("unchecked")
				List<Element> fieldElements = paraElement.getChildren("field");
				for (Element element:fieldElements) {
					FieldInfo info = new FieldInfo();
					String name = element.getAttributeValue("name");
					
					info.setExtend(parseInt(element.getAttributeValue("extend"), 0));
					info.setBoost(parseFloat(element.getAttributeValue("boost"), 1));
					info.setFieldType(parseInt(element.getAttributeValue("datatype"), 0));
					info.setIndex(parseInt(element.getAttributeValue("indexed"),1));
					info.setAnalyzed(parseInt(element.getAttributeValue("analyzed"),0));
					info.setIndexfield(element.getAttributeValue("indexfield"));
					info.setMultiple(parseInt(element.getAttributeValue("multipe"),0));
					info.setMultiplesign(element.getAttributeValue("multiplesign"));
					info.setStore(parseInt(element.getAttributeValue("stored"),1));
					info.setSpellcheck(parseInt(element.getAttributeValue("spellcheck"),0));
					info.setWhole(parseInt(element.getAttributeValue("wfield"),0));
					info.setSort(parseInt(element.getAttributeValue("sort"),0));
					String handlerName = element.getChildText("fieldHandler");
			
					if (handlerName!=null && !handlerName.equals("")) {
						@SuppressWarnings("unchecked")
						Class<? extends FieldHandler>  handlerClass = (Class<? extends FieldHandler>) Class.forName(handlerName);
						FieldHandler handler = handlerClass.newInstance();
						info.setHandler(handler);
					}
					result.put(name, info);
					
				}
			}
		} catch (Exception e) {
			LOG.error("", e);
		}
		return result;
	}
	
	
    private static int parseInt(String str,int defaultInt)
    {
    	int result=defaultInt;
        if(str!=null)
        {
        	result=Integer.parseInt(str);
        }
    	return result;
    }
    
    private static float parseFloat(String str,float defaultInt)
    {
    	float result=defaultInt;
        if(str!=null)
        {
        	result=Float.parseFloat(str);
        }
    	return result;
    }
	
}