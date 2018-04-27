package com.huifenqi.index.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author majianchun
 * 
 */
public class AnalyzerStandard {

	private static final Logger LOG = LoggerFactory.getLogger(AnalyzerStandard.class);

	public static Analyzer getAnalyzer() {
		
		Analyzer analyzer = new StandardAnalyzer();
		return analyzer;
		
	}


}
