package com.huifenqi.index.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author majianchun
 * 
 */
public class AnalyzerFactory {

	private static final Logger LOG = LoggerFactory.getLogger(AnalyzerFactory.class);

	public static Analyzer getAnalyzer(String v) {
		
		LOG.debug("analyzer_version is:{}", v);
		if (v.equalsIgnoreCase("standard")) {
			LOG.info("init standard analyzer");
			return AnalyzerStandard.getAnalyzer();
		} 
		return AnalyzerStandard.getAnalyzer();

	}


}
