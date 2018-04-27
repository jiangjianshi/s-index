package com.huifenqi.index.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huifenqi.index.analyzer.AnalyzerFactory;
import com.huifenqi.index.conf.ConfigFactory;
import com.huifenqi.index.conf.IndexTypeConf;
import com.huifenqi.index.conf.SourceConf;


public class IndexUtil {

	private static final Logger LOG = LoggerFactory.getLogger(IndexUtil.class);
	public static final char TAB = 9;
	public static final String LINE_SEP = System.getProperty("line.separator");
	
	//获取分词器
	public static Analyzer getAnalyzer() throws Exception {
		String analyzer_version = ConfigFactory.getString("analyzer_version", "aspire");
		return AnalyzerFactory.getAnalyzer(analyzer_version);
	}
	
	public static void writeIndex(List<Document> data, IndexWriter writer) {
		for (Document doc:data) {
			try {
				writer.addDocument(doc);
			} catch (IOException e) {
				LOG.error("", e);
			}
		}
	}
	
	public static void writeIndexPath(String indexPath) throws Exception {
		String path = ConfigFactory.getString("fullIndexPath");
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path)));
		writer.write(indexPath);
		writer.flush();
		writer.close();
	}
	


	
	public static String readInexPath() {
		String path = ConfigFactory.getString("fullIndexPath");
		BufferedReader reader;
		String indexDir = null;
		try {
			reader = new BufferedReader(new FileReader(path));
			indexDir = reader.readLine();
			reader.close();
		} catch (Exception e) {
			LOG.error("", e);
		}
		return indexDir;
	}

	// 获取indexWriter
	public static IndexWriter getWriter(String indexPath, boolean addFlag) throws Exception {
		
		Directory indexDir = FSDirectory.open(Paths.get(indexPath));
		
		IndexWriterConfig writerConfig = null;
		Analyzer analyzer = null;
		analyzer = getAnalyzer();
		
		writerConfig = new IndexWriterConfig(analyzer);
		
		if (addFlag == true) {
			writerConfig.setOpenMode(IndexWriterConfig.OpenMode.APPEND);
		} else {
			writerConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		}
		IndexWriter writer = null;
		writer = new IndexWriter(indexDir, writerConfig);
		return writer;
	}


	// 关闭indexWriter
	public static void closeWriter(IndexWriter writer) {
		try {
			if (writer != null) {
				writer.getAnalyzer().close();
				writer.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String initIndexPath(String prefix) {
		String dateString;
		Date date = new Date();
		date.setTime(date.getTime());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
		dateString = dateFormat.format(date);
		String indexPath = ConfigFactory.getString(prefix + ".indexPath") + "/"
				+ dateString;
		
		LOG.info("index path"+indexPath);
		return indexPath;
	}
	
	public static List<IndexTypeConf> initIndexTypeConfs(String prefix) {
		
		List<IndexTypeConf> typeconfs = new ArrayList<IndexTypeConf>();
		int num = ConfigFactory.getInt(prefix + ".typenum");
		String basepath = prefix + ".item(0)";
		for (int i = 0; i < num; i++) {
			
			IndexTypeConf tc = new IndexTypeConf();
			String path = basepath.replaceAll("0", String.valueOf(i));
			tc.typename = ConfigFactory.getString(path + ".name");
			tc.ds_num = ConfigFactory.getInt(path + ".ds_num");
			
			LOG.info("tc.typename"+tc.typename+" tc.ds_num"+tc.ds_num );
			
			tc.sourceconf = new ArrayList<SourceConf>();
			String ds_path_base = path + ".ds(0)";
			for (int j = 0; j < tc.ds_num; j++) {
				
				SourceConf sc = new SourceConf();
				String ds_path = ds_path_base.replaceAll("ds\\(0\\)", "ds("
						+ String.valueOf(j) + ")");
				sc.name = ConfigFactory.getString(ds_path + ".name");
				sc.dataname = ConfigFactory.getString(ds_path + ".dataname");
				sc.selectsql = ConfigFactory.getString(ds_path + ".selectsql");
				sc.maxidsql= ConfigFactory.getString(ds_path + ".maxidsql");
				sc.picsql=ConfigFactory.getString(ds_path + ".picsql");
				sc.settingsql=ConfigFactory.getString(ds_path + ".settingsql");
				sc.batchNum = ConfigFactory.getInt(ds_path + ".batchNum");
				
				
				LOG.info("sc.name "+sc.name+" sc.batchNum"+sc.batchNum);
				LOG.info("sc.selectsql "+sc.selectsql);
				LOG.info("sc.maxidsql "+sc.maxidsql);
				LOG.info("sc.picsql "+sc.picsql);
				LOG.info("sc.settingsql "+sc.settingsql);
				tc.sourceconf.add(sc);
			}
			typeconfs.add(tc);
		}
		return typeconfs;
	}
}
