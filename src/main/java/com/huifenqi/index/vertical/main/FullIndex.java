package com.huifenqi.index.vertical.main;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huifenqi.index.common.SQLUtil;
import com.huifenqi.index.conf.ConfigFactory;
import com.huifenqi.index.log.LogConfig;

public class FullIndex extends CommonIndex {

    private static final Logger LOG = LoggerFactory.getLogger(FullIndex.class);

    @Override
    public void run() {

        LOG.info("全量索引开始");
        // 如果有索引在建，重试两次 间隔5分钟
        int repeatTime = 2;
        boolean canRunFlag = false;
        while (repeatTime > 0) {
            if (IndexStatus.isIndexing()) {
                LOG.info("索引进程在运行，等待...");
                try {
                    Thread.sleep(5 * 60 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                repeatTime--;
            } else {
                IndexStatus.setFullIndexing(true);
                canRunFlag = true;
                break;
            }
        }
        if (!canRunFlag) {
            LOG.info("等待后索引仍在运行，放弃这次执行");
            return;
        }
        try {
            index();
        } catch (Exception e) {
            LOG.error("", e);
        } finally {
            IndexStatus.setFullIndexing(false);
            LOG.info("全量索引结束");
        }
    }

    public static void main(String[] args) throws IOException {
        
        
        //LogConfig.config("D:/Workspaces/search-indexing/config-dev/logback.xml");
        //ConfigFactory.init("D:/Workspaces/search-indexing/config-dev/app_config.xml"); 
        
        
        LogConfig.config("/data/www/search-indexing/config/logback.xml");
        ConfigFactory.init("file:/data/www/search-indexing/config/app_config.xml"); 
        LOG.info("init start");
        SQLUtil.Init();
        LOG.info("init end");
        FullIndex indexer = new FullIndex();
    
        
        indexer.init("fullIndex");
        indexer.index();
    
    }
}
