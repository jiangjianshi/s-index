package com.huifenqi.index.index;

import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huifenqi.index.common.IndexUtil;
import com.huifenqi.index.common.SQLUtil;
import com.huifenqi.index.conf.IndexTypeConf;
import com.huifenqi.index.conf.SourceConf;
import com.huifenqi.index.index.field.FieldInfo;

/**
 * @author dingpeng
 * 
 */
public class IndexDataGetter {
	
	private static final Logger LOG = LoggerFactory.getLogger(IndexDataGetter.class);
	
	public void export(IndexTypeConf tc, SourceConf sc, LinkedHashMap<String, FieldInfo> fieldInfos, IndexWriter writer,ReadExtendSQL readExtendSQl) {
		
		
		Connection conn = null;
		DocGenerator docGenerator = new DocGenerator();
		docGenerator.setSourceConf(sc);
		try {
			conn = SQLUtil.getConnection(sc.name);
			docGenerator.setConn(conn);
			docGenerator.setFieldInfos(fieldInfos);
			int maxId = docGenerator.getMaxID();
			int startId = 0;
			int endId = 0;
			while(endId <= maxId) {
				startId = endId;
				endId = endId + sc.batchNum;
				List<Document> data = docGenerator.getDocumentBatch(sc,startId, endId,sc.batchNum,readExtendSQl);
				if (data != null && data.size() > 0) {
					IndexUtil.writeIndex(data, writer);
					LOG.info("data.size:{}", data.size());
				}
			}
		} catch (Exception e) {
			LOG.error("", e);
		} finally {
			SQLUtil.close(conn);
		}
	}
}
