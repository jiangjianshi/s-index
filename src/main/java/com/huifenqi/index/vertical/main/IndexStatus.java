package com.huifenqi.index.vertical.main;


public class IndexStatus {
	private static boolean fullIndexing = false;
	private static boolean incrementIndexing = false;
	public static synchronized boolean isFullIndexing() {
		return fullIndexing;
	}
	public static synchronized void setFullIndexing(boolean fullIndex) {
		IndexStatus.fullIndexing = fullIndex;
	}
	public static synchronized boolean isIncrementIndexing() {
		return incrementIndexing;
	}
	public static synchronized void setIncrementIndexing(boolean incrementIndex) {
		IndexStatus.incrementIndexing = incrementIndex;
	}
	public static synchronized boolean isIndexing() {
		return (fullIndexing || incrementIndexing);
	}
	

}
