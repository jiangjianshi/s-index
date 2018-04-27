
package com.huifenqi.index.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.CharUtils;
/**
 * @author majianchun
 *
 */
public class StringOp {

	private StringOp() {

	}

	public static String dotFilter(String str) {
		char[] charArray = str.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			if (i == 0 || i == charArray.length - 1) {
				continue;
			}
			char c = charArray[i];
			// 当前字符是'.'
			if (c != '.') {
				continue;
			}
			// 上个字符是数字
			if (CharUtils.isAsciiNumeric(charArray[i - 1])) {
				continue;
			}
			// 下个字符是数字
			if (CharUtils.isAsciiNumeric(charArray[i + 1])) {
				continue;
			}
			charArray[i] = ' ';
		}
		return String.valueOf(charArray);
	}
	/**
	 * read all lines from one file to memory
	 * 
	 * @author lwm
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static ArrayList<String> getAllLines(String filePath)
			throws IOException {
		ArrayList<String> strs = new ArrayList<String>();
		File file = new File(filePath);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new UnicodeReader(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		String line = null;
		while ((line = br.readLine()) != null) {
			strs.add(line.trim());
		}
		return strs;
	}

	public static HashSet<String> getAllLines2Set(String filePath)
			throws IOException {
		HashSet<String> strs = new HashSet<String>();
		File file = new File(filePath);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new UnicodeReader(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		String line = null;
		while ((line = br.readLine()) != null) {
			strs.add(line.trim());
		}
		return strs;
	}

	/**
	 * write all lines to one file
	 * 
	 * @author lwm
	 * @param filePath
	 * @param strs
	 * @return
	 * @throws IOException
	 */
	public static boolean writeAllLinesToFile(String filePath,
			ArrayList<String> strs) throws IOException {
		PrintWriter pw = new PrintWriter(new FileWriter(filePath));
		int len = strs.size();
		for (int i = 0; i < len; i++) {
			pw.println(strs.get(i));
		}
		pw.close();
		return true;
	}

	/**
	 * check whether a string is a number
	 * 
	 * @author lwm
	 * @param str
	 * @return
	 */
	public static boolean isNumericByJava(String str) {
		for (int i = 0; i < str.length(); i++) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public boolean isNumericByPattern(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	/**
	 * compare two string[]
	 * 
	 * @author lwm
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean equals(String[] a, String[] b) {
		if (a.length != b.length) {
			return false;
		}
		HashSet<String> aSet = new HashSet<String>();
		for (int i = 0; i < a.length; i++) {
			aSet.add(a[i]);
		}
		for (int i = 0; i < b.length; i++) {
			if (aSet.contains(b[i])) {
				continue;
			} else {
				return false;
			}
		}
		return true;

	}

	/**
	 * get the max string from the arraylist
	 * 
	 * @author lwm
	 * @param strings
	 * @return
	 */
	public static String getMaxString(ArrayList<String> strings) {
		if (strings.size() == 0) {
			return null;
		}
		String result = "";
		for (int i = 0; i < strings.size(); i++) {
			String curStr = strings.get(i);
			if (result.compareTo(curStr) < 0) {
				result = curStr;
			}
		}
		return result;
	}

	/**
	 * sort the string arrays
	 * 
	 * @author lwm
	 * @param strs
	 * @return
	 */
	public static String[] strsSort(String[] strs) {
		Arrays.sort(strs);
		return strs;
	}

	/**
	 * sort the string arrays
	 * 
	 * @author lwm
	 * @param strs
	 * @return
	 */
	public static String[] strsSort(ArrayList<String> strs) {
		String[] strsnew = new String[strs.size()];
		for (int i = 0; i < strs.size(); i++) {
			strsnew[i] = strs.get(i);
		}
		Arrays.sort(strsnew);
		return strsnew;
	}

	// 将words中的出现的单个词进行连接，输出
	// get single not connected words as a string
	public static ArrayList<String> getSingleCharacterString(String[] words) {
		ArrayList<String> result = new ArrayList<String>();
		int start = -1;
		int end = -1;
		int flag = 0;
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			if ((word.length() > 1) && (i < words.length - 1)) {
				end = i - 1;
				if ((start <= end) && (start >= 0)) {
					String temp = "";
					for (int j = start; j <= end; j++) {
						temp += words[j];
					}
					if (temp.length() > 0) {
						result.add(temp);
						flag = 0;
					}
				}
				result.add("#");
			}
			if (word.length() == 1) {
				if (flag == 0) {
					start = i;
					end = i;
					flag = 1;
				} else {
					end = i;
				}
			}
			if (i == words.length - 1) {
				if (word.length() > 1) {
					if ((start <= end) && (start >= 0)) {
						String temp = "";
						for (int j = start; j <= end; j++) {
							temp += words[j];
						}
						if (temp.length() > 0) {
							result.add(temp);
						}
					}
					result.add("#");
				} else {
					if ((start >= 0) && (start <= end) && (word.length() == 1)) {
						String temp = "";
						for (int j = start; j <= end; j++) {
							temp += words[j];
						}
						if (temp.length() > 0) {
							result.add(temp);
						}
					}
				}
			}
		}

		return result;
	}

	public static LinkedList<String> string2linkedlist(String[] strs) {
		LinkedList<String> linkedList = new LinkedList<String>();
		for (int i = 0; i < strs.length; i++) {
			linkedList.add(strs[i]);
		}
		return linkedList;
	}

	public static String[] linkedList2String(LinkedList<String> linkedList) {
		int len = linkedList.size();
		String[] result = new String[len];
		for (int i = 0; i < len; i++) {
			result[i] = linkedList.get(i);
		}
		return result;
	}

	public static String[] filterStringVec(String[] strs) {
		ArrayList<String> strArr = new ArrayList<String>();
		for (int i = 0; i < strs.length; i++) {
			if (!".".equals(strs[i])) {
				strArr.add(strs[i]);
			}
		}
		return arrayList2Strings(strArr);
	}

	public static String[] arrayList2Strings(ArrayList<String> strArr) {
		String[] strs = new String[strArr.size()];
		for (int i = 0; i < strArr.size(); i++) {
			strs[i] = strArr.get(i);
		}
		return strs;
	}

	// define whether a string is a url
	public static boolean isUrl(String str) {
		if (str == null) {
			return false;
		} else {
			String rexUrl = "((thunder://)|(flashget://)|(ed2k://)|(mms://)|(mailto://)|(file://)|(http(s)?://)|(ftp://))?"
					+ "[^\\/]*\\.(com|co|tv|hk|asia|edu|cn|org|so|net|cc|info|biz|gov|name|me|mobi|tel).*";
			boolean result = str.matches(rexUrl);
			return result;
		}
	}

	
	/**
	 * 将String[] 改写为一个String，gap是间隔符
	 * @param strs
	 * @param gap
	 * @return
	 */
	public static String StringVec2String(String[] strs,String gap)
	{
		int len = strs.length;
		if(len<1)
		{
			return "";
		}
		String result = "";
		for(int i=0;i<len;i++)
		{
			result = result+gap+strs[i];
		}
		if(result.startsWith(gap))
		{
			result = result.substring(gap.length());
		}
		result = result.trim();
		return result;
	}
	
	public static String[] filterWords(String[] words,HashSet<String> set,String splitStr, int comType)
	{
		//System.out.println("comType = "+comType);
		if((comType&4)==4)
		{
		//	System.out.println("(comType&4)==4 ");
			return words;
		}
		ArrayList<String> result = new ArrayList<String>();
		int len = words.length;
		int flag = 0;
		if((len<1))
		{
			return null;
		}
		else
		{
			for(int i=0;i<len;i++)
			{
				//flag = true;
				if(words[i].equals(splitStr))
				{
					flag = flag+1;;
				}
				if((!set.contains(words[i]))||(flag%2==1))
				{
					result.add(words[i]);
				}
			}
		}
		if((result.size()==0))
		{
			return words;
		}
		String[] resultvec = StringOp.arrayList2Strings(result);
		return resultvec;
	}
	
	public static HashMap<String, Float> fillHashMap3line(ArrayList<String> nameList) {
		// TODO Auto-generated method stub
		HashMap<String, Float> result = new HashMap<String, Float>();
		for (int i = 0; i < nameList.size(); i++) {
			String[] strs = nameList.get(i).split(" ");
			String name = "";
			float weight = -1f;
			if (strs.length > 0) {
				name = strs[0];
			}
			if (strs.length > 2) {
				weight = Float.parseFloat(strs[2]);
			}
			result.put(name, weight);
		}
		return result;
	}
	public static HashMap<String, Float> fillHashMap2line(ArrayList<String> nameList) {
		// TODO Auto-generated method stub
		HashMap<String, Float> result = new HashMap<String, Float>();
		for (int i = 0; i < nameList.size(); i++) {
			String[] strs = nameList.get(i).split(" ");
			String name = "";
			float weight = -1f;
			if (strs.length > 0) {
				name = strs[0];
			}
			if (strs.length > 1) {
				weight = Float.parseFloat(strs[1]);
			}
			result.put(name, weight);
		}
		return result;
	}
	public static HashMap<String, Float> fillHashMap1line(ArrayList<String> nameList) {
		// TODO Auto-generated method stub
		HashMap<String, Float> result = new HashMap<String, Float>();
		for (int i = 0; i < nameList.size(); i++) {
			String[] strs = nameList.get(i).split(" ");
			String name = "";
			float weight = -1f;
			if (strs.length > 0) {
				name = strs[0];
			}
			result.put(name, weight);
		}
		return result;
	}
	
	

	// ****************************
	// Get minimum of three values
	// ****************************

	public static int Minimum(int a, int b, int c) {
		int mi;
		mi = a;
		if (b < mi) {
			mi = b;
		}
		if (c < mi) {
			mi = c;
		}
		return mi;
	}

	// *****************************
	// Compute Levenshtein distance
	// *****************************

	/**
	 * 计算两个字符串的编辑距离
	 * @param s
	 * @param t
	 * @return 编辑距离
	 */
	public static int getEditDistance(String s, String t) {
		int d[][]; // matrix
		int n; // length of s
		int m; // length of t
		int i; // iterates through s
		int j; // iterates through t
		char s_i; // ith character of s
		char t_j; // jth character of t
		int cost; // cost

		// Step 1

		n = s.length();
		m = t.length();
		if (n == 0) {
			return m;
		}
		if (m == 0) {
			return n;
		}
		d = new int[n + 1][m + 1];
		// Step 2
		for (i = 0; i <= n; i++) {
			d[i][0] = i;
		}
		for (j = 0; j <= m; j++) {
			d[0][j] = j;
		}
		// Step 3
		for (i = 1; i <= n; i++) {
			s_i = s.charAt(i - 1);
			// Step 4
			for (j = 1; j <= m; j++) {
				t_j = t.charAt(j - 1);
				// Step 5
				if (s_i == t_j) {
					cost = 0;
				} else {
					cost = 1;
				}
				// Step 6
				d[i][j] = Minimum(d[i - 1][j] + 1, d[i][j - 1] + 1,
						d[i - 1][j - 1] + cost);
			}
		}

		// Step 7
		return d[n][m];

	}
	
	/**
	 * 将两列中间用空格分隔的字符串填充到HASHMAP中
	 * @param path
	 * @return
	 */
	public static HashMap<String,String> fillHashMapFrom2Col(String path)
	{
		ArrayList<String> lines = null;;
		try {
			lines = StringOp.getAllLines(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		HashMap<String,String> hashMap = new HashMap<String,String>();
		for(int i=0;i<lines.size();i++)
		{
			String line = lines.get(i).trim();
			String[] strs = line.split(" ");
			if(strs.length==2)
			{
				String str1 = strs[0];
				String str2 = strs[1];
				hashMap.put(str1, str2);
			}
		}
		return hashMap;
	}
	
}
