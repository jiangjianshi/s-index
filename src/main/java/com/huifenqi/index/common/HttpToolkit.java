package com.huifenqi.index.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import java.nio.charset.Charset;

import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* Http请求
*
*/
public final class HttpToolkit {
	private final static Logger log = LoggerFactory.getLogger(HttpToolkit.class);
	private static String defaultContentEncoding = Charset.defaultCharset().name();
	
	public static boolean urlExists(final String URLName) {
		try {
			// 设置此类是否应该自动执行 HTTP 重定向（响应代码为 3xx 的请求）。
			HttpURLConnection.setFollowRedirects(false);
			// 到 URL 所引用的远程对象的连接
			HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
			con.setConnectTimeout(200);
			
			/* 设置 URL 请求的方法， GET POST HEAD OPTIONS PUT DELETE TRACE
			 * 以上方法之一是合法的，具体取决于协议的限制。
			 */
			con.setRequestMethod("HEAD");
			// 从 HTTP 响应消息获取状态码
			return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
		} catch (Exception e) {
			log.error("urlExists execute error!", e);
			return false;
		}
	}
	
	/**
	 * 发送GET请求
	 * 
	 * @param urlString URL地址
	 * @return 响应对象
	 * @throws IOException
	 */
	public static String sendGet(final String urlString) {
		return send(urlString, "GET", null, null);
	}
	
	/**
	 * 发送GET请求
	 * 
	 * @param urlString URL地址
	 * @param params 参数集合
	 * @return 响应对象
	 * @throws IOException
	 */
	public static String sendGet(final String urlString, final Map<String, String> params) {
		return send(urlString, "GET", params, null);
	}
	
	/**
	 * 发送GET请求
	 * 
	 * @param urlString URL地址
	 * @param params 参数集合
	 * @param propertys 请求属性
	 * @return 响应对象
	 * @throws IOException
	 */
	public static String sendGet(final String urlString, final Map<String, String> params,
			final Map<String, String> propertys) {
		return send(urlString, "GET", params, propertys);
	}
	
	/**
	 * 发送POST请求
	 *
	 * @param urlString URL地址
	 * @return 响应对象
	 * @throws IOException
	 */
	public static String sendPost(final String urlString) {
		log.debug("request query - " + urlString);
		return send(urlString, "POST", null, null);
	}
	
	/**
	 * 发送POST请求
	 * 
	 * @param urlString URL地址
	 * @param params 参数集合
	 * @return 响应对象
	 * @throws IOException
	 */
	public static String sendPost(final String urlString, final Map<String, String> params) {
		return send(urlString, "POST", params, null);
	}

	/**
	 * 发送POST请求
	 *
	 * @param urlString URL地址
	 * @param params 参数集合
	 * @param propertys 请求属性
	 * @return 响应对象
	 * @throws IOException
	 */
	public static String sendPost(final String urlString, final Map<String, String> params,
			final Map<String, String> propertys) {
		return send(urlString, "POST", params, propertys);
	}
	
	/**
	 * 发送HTTP请求
	 *
	 * @param urlString
	 * @return 响映对象
	 * @throws IOException
	 */
	private static String send(String urlString, final String method,
			final Map<String, String> parameters, final Map<String, String> propertys) {
		try {
    		HttpURLConnection urlConnection = null;
    		if (method.equalsIgnoreCase("GET") && parameters != null) {
    			StringBuffer param = new StringBuffer();
    			int i = 0;
    			for (String key : parameters.keySet()) {
    				if (i == 0) {
						param.append("?");
					} else {
						param.append("&");
					}
    				param.append(key).append("=").append(parameters.get(key));
    				i++;
    			}
    			urlString += param;
    		}
    		URL url = new URL(urlString);
    		urlConnection = (HttpURLConnection) url.openConnection();
    		//urlConnection.setConnectTimeout(500);
    		urlConnection.setRequestMethod(method);
    		urlConnection.setDoOutput(true);
    		urlConnection.setDoInput(true);
    		urlConnection.setUseCaches(false);
    		
    		if (propertys != null) {
				for (String key : propertys.keySet()) {
    				urlConnection.addRequestProperty(key, propertys.get(key));
    			}
			}
    		
    		if (method.equalsIgnoreCase("POST") && parameters != null) {
    			StringBuffer param = new StringBuffer();
    			for (String key : parameters.keySet()) {
    				param.append("&");
    				param.append(key).append("=").append(parameters.get(key));
    			}
    			urlConnection.getOutputStream().write(param.toString().getBytes());
    			urlConnection.getOutputStream().flush();
    			urlConnection.getOutputStream().close();
    		}
    		
    		return makeContent(urlString, urlConnection);
		} catch (Exception e) {
			log.warn(e.getMessage());
			return null;
		}
	}
	
	/**
	 * 得到响应对象
	 * 
	 * @param urlConnection
	 * @return 响应对象
	 * @throws IOException
	 */
	private static String makeContent(final String urlString,
			final HttpURLConnection urlConnection) throws IOException {
		try {
			InputStream in = urlConnection.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(
			new InputStreamReader(in, "UTF-8"));
			StringBuffer temp = new StringBuffer();
			String line = bufferedReader.readLine();
			while (line != null) {
				temp.append(line).append("\n");
				line = bufferedReader.readLine();
			}
			bufferedReader.close();
			
			String ecod = urlConnection.getContentEncoding();
			if (ecod == null) {
				ecod = HttpToolkit.defaultContentEncoding;
			}
			return new String(temp.toString().getBytes(), "GBK");
		} catch (IOException e) {
			log.error("make content error!", e);
			return null;
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
		}
	}
	
	public static String getUrlDate(String urlStr) {
		InputStream in = null;
		try {
			URL url = new URL(urlStr);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			conn.setReadTimeout(60000);
			conn.setConnectTimeout(60000);	//毫秒
			try {
				in = url.openStream();
			} catch (Exception e) {
				log.error("url open stream error!", e);
			}
			
	        StringBuffer s = new StringBuffer();
	        String rLine = null;
	        BufferedReader bReader = new BufferedReader(new InputStreamReader(in, "utf-8"));
	        while ((rLine = bReader.readLine()) != null) {
	            String tmp_rLine = rLine;
	            int str_len = tmp_rLine.length();
	            if (str_len > 0) {
	              s.append(tmp_rLine);
	            }
	            tmp_rLine = null;
	        }
			in.close();
			return s.toString();
		} catch (Exception e) {
			log.error("get url date error!", e);
			return "";
		}
	}
	
	public static final String readContent(String url) {
		return readContend(url, 10000, 10000);
	}
	
	public static final String readContend(String url, int contimeout, int readtimeout) {
		URL url1 = null;
		BufferedReader reader = null;
		HttpURLConnection connection = null;
		try {
			url1 = new URL(url);
			connection = (HttpURLConnection) url1.openConnection();
			connection.setConnectTimeout(contimeout);
			connection.setReadTimeout(readtimeout);
			connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
			connection.connect();
			String contentEncoding = connection.getContentEncoding();	// 编码
			
			InputStream stream;
			if (null != contentEncoding && -1 != contentEncoding.indexOf("gzip")) {
				stream = new GZIPInputStream(connection.getInputStream());
			} else if (null != contentEncoding && -1 != contentEncoding.indexOf("deflate")) {
				stream = new InflaterInputStream(connection.getInputStream());
			} else {
				stream = connection.getInputStream();
			}
			
			reader = new BufferedReader(new InputStreamReader(stream));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
			reader.close();
			connection.disconnect();
			return sb.toString();
		} catch (Exception e) {
			log.error("connect url: " + url + " error!" + " contimeout=" + contimeout + " readtimeout=" + readtimeout, e);
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
					log.error("reader close error!", e1);
				}
			}
			return "-1";
		} finally {
			url1 = null;
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					log.error("reader close error!", e);
				}
			}
			
			if (connection != null) {
				connection.disconnect();
			}
		}
	}
}