package com.huifenqi.index.dispatch;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.huifenqi.index.common.HttpToolkit;
import com.huifenqi.index.conf.ConfigFactory;

public class DispatchServiceImpl extends IndexDispath{
	private static final Logger logger = LoggerFactory
			.getLogger(DispatchServiceImpl.class);
	/**
	 * 索引上传
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public boolean dispatch(String store, String localPath) {
		logger.info("准备同步索引库：{}, {}", store, localPath);
		
		//如果索引验证不正确，不进行分发
		if(!isRight(localPath))
			return false;
			
		List<String> ips = null;
		try {
			ips = getIps();
			int retryNum = 0;
			while ((ips == null || ips.size() == 0) && retryNum < 10) {
				Thread.sleep(1000);
				retryNum++;
				ips = getIps();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if (ips == null || 0 == ips.size()) {
			logger.error("无法获取服务ip, 同步索引库{}失败。", store);
			return false;
		}
		for (String ip : ips) {
			try {
				String remotePath = getRemotePath(ip, store);
				System.out.println(remotePath);
				if (remotePath!=null) {
					
					int index=remotePath.indexOf("body\":{\"result\":\"");
					if(index!=-1)
					{
						remotePath=remotePath.substring(index+"body\":{\"result\":\"".length(),remotePath.length()-3);
						System.out.println(remotePath);
						System.out.println("rm -rf "+remotePath+"/*");
						System.out.println("cp -rf "+ localPath+"/* "+remotePath+"/*");
						uploadFile(ip, localPath, remotePath);
						System.out.println("upload finish");
						reloadIndex(ip, store);
						System.out.println("reload finish");
					}
					
				} else {
					logger.error("远程路径错误.");
				}
			} catch (Exception e) {
				logger.error("索引分发失败{}, {},{}", ip,store, e);
			}
		}
		logger.info("同步索引库{}完成。", store);
		return true;
	}

	private String getRemotePath(String ip, String store) {
		String url = ConfigFactory.getString("getPathUrl");
		url = url.replace("{ip}", ip).replace("{store}", store);
		String result = null;
		result = HttpToolkit.readContent(url).trim();
		return result;
	}
	
	private void reloadIndex(String ip, String store) {
		String url = ConfigFactory.getString("reloadUrl");
		url = url.replace("{ip}", ip).replace("{store}", store);
		String result=HttpToolkit.readContent(url);
		
		System.out.println("reload index"+result);
	}
	
	/**
	 * 通过ip.list文件获取ip列表
	 * @return
	 * @throws IOException
	 */
	private List<String> getIps() throws IOException {
		String  iplist = ConfigFactory.getString("iplist");
		System.out.println("iplist"+iplist);
		List<String> list = new LinkedList<String>();
		try {
			String[] ips=iplist.split("\\|");
			for(String ip:ips)
                  list.add(ip);
			
		} catch (Exception e) {
			logger.error("读取ip列表错误", e);
		}
		return list;
	}

	
	
	private static final void uploadFile(int num, String ip, String orig,
			
			String dest, String workdirectory) throws IOException {
			
			String upload = ConfigFactory.getString("dispatchCommand");
			
			System.out.println(upload);
			upload = "sudo sh "+ upload.replace("{ip}", ip).replace("{orig}", orig).replace("{dest}", dest);
			System.out.println(upload);
			
			
			try { 
				Process ps = Runtime.getRuntime().exec(upload); 
				ps.waitFor(); 
		  
				BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream())); 
				StringBuffer sb = new StringBuffer(); 
				String line; 
				while ((line = br.readLine()) != null) { 
					sb.append(line).append("\n"); 
				} 
				String result = sb.toString(); 
				System.out.println(result); 
		     }  
			 catch (Exception e) { 
			      e.printStackTrace(); 
			 } 

		}
	
	
	
	

	/**
	 * 索引上传
	 * 
	 * @param ip
	 * @param orig
	 * @param dest
	 * @throws IOException
	 */
	public static final void uploadFile(String ip, String orig, String dest)
			throws IOException {
		uploadFile(0, ip, orig, dest, "/");
	}

	public static final void uploadFile(int num, String ip, String orig,
			String dest) throws IOException {
		uploadFile(num, ip, orig, dest, "/");
	}
	
	/***
	 * 验证生成的索引是否有问题
	 *
	 * @param path
	 * @return
	 */
	public static boolean isRight(String path) {
		
		IndexSearcher searcher = null;
		IndexReader reader=null;
		try {
			
			reader = DirectoryReader.open(FSDirectory.open(Paths.get(path)));  
			searcher = new IndexSearcher(reader);
			Query query = new MatchAllDocsQuery();
			TopDocs hits = searcher.search(query, 1);
			
			if(hits.scoreDocs.length>0)
				logger.info("idex is ok :"+hits.totalHits);
			return hits.scoreDocs.length > 0 ? true : false;
			
		} catch (Exception e) {
			logger.error("index format error:{}", e);
			return false;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					logger.error("", e);
				}
			}
		}
	}

}