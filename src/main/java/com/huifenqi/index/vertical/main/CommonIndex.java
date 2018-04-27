package com.huifenqi.index.vertical.main;

import java.util.LinkedHashMap;
import java.util.List;

import org.apache.lucene.index.IndexWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huifenqi.index.common.IndexUtil;
import com.huifenqi.index.conf.ConfigFactory;
import com.huifenqi.index.conf.IndexTypeConf;
import com.huifenqi.index.conf.SourceConf;
import com.huifenqi.index.dispatch.IndexDispath;
import com.huifenqi.index.index.IndexDataGetter;
import com.huifenqi.index.index.ReadExtendSQL;
import com.huifenqi.index.index.field.FieldInfo;
import com.huifenqi.index.index.field.FieldManager;

/**
 * @author majianchun
 * 
 */

public abstract class CommonIndex {
	
	private static final Logger LOG = LoggerFactory
			.getLogger(CommonIndex.class);
	
	protected String indexPath = null;
	IndexDataGetter dataGetter = null;
	IndexDispath dispater = null;
	protected String prefix = null;
	// 索引类型配置文件
	public List<IndexTypeConf> typeconfs = null;
	public void run() {
		
	}
	public void init(String prefix) {
		
		this.prefix = prefix;
		typeconfs = IndexUtil.initIndexTypeConfs(prefix);
		indexPath = initIndexPath();
		String dataGetterName = ConfigFactory.getString(prefix
				+ ".dataGetterName", "com.cyou.dao.Impl.IndexDataGetter");
		Class<?> input;
		try {
			input = Class.forName(dataGetterName);
			java.lang.Object objectCopy = input.newInstance();
			dataGetter = (IndexDataGetter) objectCopy;
		} catch (Exception e) {
			LOG.error("", e);
			dataGetter = new IndexDataGetter();
		}
		
		
		
		String dispatherName = ConfigFactory.getString(prefix+".dispatcher");
		try {
			if(dispatherName != null && !dispatherName.isEmpty()) {
				input = Class.forName(dispatherName);
				dispater = (IndexDispath) input.newInstance();
			}
		} catch (Exception e) {
			LOG.error("", e);
		}
		
		
	}

	protected String initIndexPath() {
		return IndexUtil.initIndexPath(prefix);
	}
	
	
	
	public void index() {
		
		
		for (IndexTypeConf typeConf : typeconfs) {
			
			String type = typeConf.typename;
		
			LinkedHashMap<String, FieldInfo> fieldInfos = FieldManager.getFieldsInfo(type);
			String indexDir = indexPath + "/" + type;
			
			
			
			IndexWriter writer = null;
			try {
				writer = IndexUtil.getWriter(indexDir, false);
			} catch (Exception e) {
				LOG.error("", e);
			}
			
			if (writer == null) {
				LOG.error("无法打开索引");
				return;
			}
			

			for (int i = 0; i < typeConf.ds_num; i++) {
				
				
				SourceConf sourceConf = typeConf.sourceconf.get(i);
				ReadExtendSQL readExtendSQl=new ReadExtendSQL(); 
				
				if(sourceConf.picsql!=null)
				{
					
					readExtendSQl.readPicData(sourceConf);
				}
				if(sourceConf.settingsql!=null)
				{
					readExtendSQl.readSettingData(sourceConf);
					
				}
				dataGetter.export(typeConf, sourceConf, fieldInfos, writer,readExtendSQl);
				
			}
			IndexUtil.closeWriter(writer);
			
			LOG.info("index close");
			
		    
			//索引分发
			if (dispater != null) {
				dispater.dispatch(type, indexDir);
			}
			
			
		
		}
	}
}
