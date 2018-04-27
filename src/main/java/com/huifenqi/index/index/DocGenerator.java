package com.huifenqi.index.index;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huifenqi.index.common.ChineseToEnglish;
import com.huifenqi.index.common.QueryPreProcessUtils;
import com.huifenqi.index.common.SQLUtil;
import com.huifenqi.index.conf.SourceConf;
import com.huifenqi.index.index.field.FieldHandler;
import com.huifenqi.index.index.field.FieldInfo;
import org.apache.lucene.spatial.SpatialStrategy;
import org.apache.lucene.spatial.prefix.RecursivePrefixTreeStrategy;
import org.apache.lucene.spatial.prefix.tree.GeohashPrefixTree;
import org.apache.lucene.spatial.prefix.tree.SpatialPrefixTree;
import org.apache.lucene.spatial.query.SpatialArgs;
import org.apache.lucene.spatial.query.SpatialOperation;
import org.apache.lucene.spatial.SpatialStrategy;
import org.apache.lucene.spatial.prefix.RecursivePrefixTreeStrategy;
import org.apache.lucene.spatial.prefix.tree.GeohashPrefixTree;
import org.apache.lucene.spatial.prefix.tree.SpatialPrefixTree;
import org.apache.lucene.spatial.query.SpatialArgs;
import org.apache.lucene.spatial.query.SpatialOperation;
import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.distance.DistanceUtils;
import org.locationtech.spatial4j.shape.Point;
import org.locationtech.spatial4j.shape.Shape;

public class DocGenerator {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(DocGenerator.class);
	private Connection conn = null;
	private SourceConf sc=null;
	
	
	private LinkedHashMap<String, FieldInfo> fieldInfos = null;

	public void setConn(Connection conn) {
		this.conn = conn;
	}
	public void setSourceConf(SourceConf sc)
	{
		this.sc=sc;
	}
	
	
	public int getMaxID() {
		
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int maxId = 0;
		try {
			pstmt = conn.prepareStatement(sc.maxidsql);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				maxId = rs.getInt(1);
			}
		} catch (Exception e) {
			LOG.error("", e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstmt);
			LOG.info("max id: {}", maxId);
		}
		return maxId;
	}
	
	public void setFieldInfos(
			LinkedHashMap<String, FieldInfo> fieldInfos) {
		this.fieldInfos = fieldInfos;
	}

	public List<Document> getDocumentBatch(SourceConf sc,int startId, int endId,int batchNum,ReadExtendSQL readExtendSQl) {
		
		
		PreparedStatement pstmt = null;
		ResultSet rst = null;
		List<Document> dataList = new ArrayList<Document>();
		try {
			String strSql = sc.selectsql+"  limit  " + startId + ",  " + batchNum;
			LOG.info(strSql);
			pstmt = conn.prepareStatement(strSql);
			rst = pstmt.executeQuery();
			while (rst.next()) {

              
				Document doc = new Document();
				Iterator<String> keyIt = fieldInfos.keySet().iterator();
				
				String key="";
				while (keyIt.hasNext()) {
					
					
					String fieldName = keyIt.next();	
					FieldInfo fd = fieldInfos.get(fieldName);
					
					if(fieldName.equals("f_house_sell_id") && sc.dataname.equals("house"))
					{
						key = rst.getString(fieldName)+",0";
					}
					if(sc.dataname.equals("room")&& fieldName.equals("f_house_sell_id"))
					{
						key = rst.getString(fieldName);
					}
					if(sc.dataname.equals("room")&& fieldName.equals("ID"))
					{
						key = key+","+rst.getString(fieldName);
					}
					
					int extend=fd.getExtend();
					if ( extend==1) {
						   HandleExtend(key,fieldName, fd, doc, readExtendSQl);
					} else {
						try {
							if(fieldName.contentEquals("Google"))
							{
								HandleGoogleTpye(fieldName, fd, doc, rst);
							}
							
                            else  if (fd.getFieldType() == 0) {
								HandleStringTpye(fieldName, fd, doc, rst);
							} else {
								HandleNumberType(fieldName, fd, doc, rst);
							}
						} catch (Exception e) {
							LOG.error("", e);
						}
					}
				}
				dataList.add(doc);
			}
		} catch (Exception e) {
			LOG.error("{}", e);
		} finally {
			SQLUtil.close(rst);
			SQLUtil.close(pstmt);
		}
		return dataList;
	}
	
	public static void HandleGoogleTpye(String fieldName, FieldInfo fieldInfo, Document doc, ResultSet rst) {
		try {
			String val = rst.getString(fieldName);
			String originalVal = val;
			
			if(val!=null)
			{	
				    int index=val.indexOf(",");
				    if(index!=-1)
				    {
				    	String x="";
				    	String y="";
				    
				    	x=val.substring(0,index);
				    	y=val.substring(index+1);
				    	
				    	if(!x.equals("") && !y.equals(""))
				    	{
				    		//System.out.println("x y"+x+" "+y);
				    		SpatialContext  ctx = SpatialContext.GEO;

				    		/**	
				    		 * 网格最大11层或Geo Hash的精度
				    		 * 1: SpatialPrefixTree定义的Geo Hash最大精度为24
				    		 * 2: GeohashUtils定义类经纬度到Geo Hash值公用方法
				    		 * */
				    		SpatialPrefixTree spatialPrefixTree = new GeohashPrefixTree(ctx, 11);

				    		/**
				    		 * 索引和搜索的策略接口,两个主要实现类
				    		 * 1: RecursivePrefixTreeStrategy(支持任何Shape的索引和检索)
				    		 * 2: TermQueryPrefixTreeStrategy(仅支持Point Shape)
				    		 * 上述两个类继承PrefixTreeStrategy(有使用缓存)
				    		 * */
				    		SpatialStrategy strategy = new RecursivePrefixTreeStrategy(spatialPrefixTree, "google");
			
				    		Shape shape = null;
				    		/**
				    		 * 对小于MaxLevel的Geo Hash构建Field(IndexType[indexed,tokenized,omitNorms])
				    		 * */
				    		Field []fields = strategy.createIndexableFields((shape = ctx.getShapeFactory().pointXY(new Double(x).doubleValue(), new Double(y).doubleValue())));
				    		for (Field field : fields) {
				    			doc.add(field);
				    		}

				
				
				    		//判断是否需要保存
				    		if (fieldInfo.getStore() == 1) {
				    			doc.add(new StoredField(fieldInfo.getIndexfield()+"_store", originalVal));
				    		}
				    	}
				    }
			}
		} catch (Exception e) {
			LOG.error("fieldName {} {}", fieldName, e);
			e.printStackTrace();
		}
	}	
	
	
	
	
	
	
	public static void HandleStringTpye(String fieldName, FieldInfo fieldInfo, Document doc, ResultSet rst) {
		try {
			String val = rst.getString(fieldName);
			String originalVal = val;
	
			if(val!=null)
			{	
			
			    val = QueryPreProcessUtils.getInstance().process(val);		
				val = val.trim();
				Field field = null;
				
				
				if (fieldInfo.getMultiple() == 1) {
					String[] vlist = val.split(fieldInfo.getMultiplesign());
					for (String v : vlist) {
						
						if (fieldInfo.getAnalyzed() == 1) {
							field = new TextField(fieldInfo.getIndexfield(), v, Field.Store.NO);
						} else {
							field = new StringField(fieldInfo.getIndexfield(), v, Field.Store.NO);
						}
						doc.add(field);
					}
				} else {
					// 是否索引和分词
					if (fieldInfo.getIndex() > 0) {		
						
						if (fieldInfo.getAnalyzed() > 0) {
							
							
							field = new TextField(fieldInfo.getIndexfield(), val, Field.Store.NO);
							doc.add(field);
							
							//对title 做拼音索引
							if(fieldInfo.getIndexfield().equals("title"))
							{
								String pinyin=ChineseToEnglish.getPingYin(val);
								//field = new TextField("title_pinyin", pinyin, Field.Store.NO);
                                field = new StringField("title_pinyin", pinyin, Field.Store.NO);

								doc.add(field);
							}
							//对subway 做拼音索引
							if(fieldInfo.getIndexfield().equals("subway"))
							{
								String pinyin=ChineseToEnglish.getPingYin(val);
								//field = new TextField("subway_pinyin", pinyin, Field.Store.NO);
								field = new StringField("subway_pinyin", pinyin, Field.Store.NO);
								doc.add(field);
							}
							
							
						} else {
							field = new StringField(fieldInfo.getIndexfield(), val, Field.Store.NO);
							doc.add(field);
						}
					} 
					if(fieldInfo.getWhole() > 0) {
						field = new StringField("W" + fieldInfo.getIndexfield(), originalVal.toLowerCase().trim(), Field.Store.NO);
						doc.add(field);
					}
				}
				//判断是否需要保存
				if (fieldInfo.getStore() == 1) {
					doc.add(new StoredField(fieldInfo.getIndexfield()+"_store", originalVal));
				}
			}
		} catch (Exception e) {
			LOG.error("fieldName {} {}", fieldName, e);
			e.printStackTrace();
		}
	}	
	
	public static void HandleNumberType(String fieldName, FieldInfo fieldInfo, Document doc, ResultSet rst) {
		
		boolean hasvalue = true;
		try {			
			
			switch (fieldInfo.getFieldType()) {
			case 1: // int
				int valInt = rst.getInt(fieldName);
				
				if(fieldInfo.getSort()==1)
					doc.add(new NumericDocValuesField(fieldInfo.getIndexfield()+"_sort", valInt));	
				    doc.add(new IntPoint(fieldInfo.getIndexfield(), valInt));	
					
				//判断是否需要保存
				if (fieldInfo.getStore() == 1) {
				
					doc.add(new StoredField(fieldInfo.getIndexfield()+"_store", valInt));
				}
				break;
				
			case 2: // long
				long valLong = rst.getLong(fieldName);
				if(fieldInfo.getSort()==1)
			        doc.add(new NumericDocValuesField(fieldInfo.getIndexfield()+"_sort", valLong));	//排序
				    doc.add(new LongPoint(fieldInfo.getIndexfield(), valLong));//区间值
				//判断是否需要保存
				if (fieldInfo.getStore() == 1) {
				
					doc.add(new StoredField(fieldInfo.getIndexfield()+"_store", valLong));
				}
				break;
			case 3: // double
				
				float valFloat = rst.getFloat(fieldName);
				doc.add(new DoublePoint(fieldInfo.getIndexfield(), valFloat));
				
				//判断是否需要保存
				if (fieldInfo.getStore() == 1) {
				
					doc.add(new StoredField(fieldInfo.getIndexfield()+"_store", valFloat));
				}
				
				break;
			case 4: // datetime
			    Date  val = rst.getTimestamp(fieldName);
				if (val != null) {
					doc.add(new LongPoint(fieldInfo.getIndexfield(), val.getTime()));
				} else {
					hasvalue = false;
				}
				
				//判断是否需要保存
				if (fieldInfo.getStore() == 1) {
					doc.add(new StoredField(fieldInfo.getIndexfield()+"_store", val.getTime()));
				}
				//判断是否需要
				if(fieldInfo.getSort()==1){
                    doc.add(new NumericDocValuesField(fieldInfo.getIndexfield()+"_sort", val.getTime()));  
                }
				break;
			case 5: // time
				Time time = rst.getTime(fieldName);
				if (time != null) {
					doc.add(new LongPoint(fieldInfo.getIndexfield(), time.getTime()));
					
				} else
					hasvalue = false;
				
				//判断是否需要保存
				if (fieldInfo.getStore() == 1) {
				
					doc.add(new StoredField(fieldInfo.getIndexfield()+"_store", time.getTime()));
				}
				
				break;
			default:
				break;
			}
			
		} catch (Exception e) {
			
    		LOG.error(fieldName+e);
		}
	}
	
	
	
	
	public static void HandleExtend(String key,String fieldName, FieldInfo fieldInfo, Document doc, ReadExtendSQL readExtendSQl) {
		
		
		try {
		
			String val =null;
			if(fieldName.equals("f_pic_root_path"))
			{
			     val=readExtendSQl.f_pic_root_path_map.get(key);
			}
			
			if(fieldName.equals("f_pic_web_path"))
            {
                 val=readExtendSQl.f_pic_web_path_map.get(key);
            }
		     
			if(fieldName.equals("f_setting_code"))
			{
				val=readExtendSQl.f_setting_code_map.get(key);
			}
			
			if(fieldName.equals("f_setting_nums"))
			{
				val=readExtendSQl.f_setting_nums_map.get(key);
			}
			
			if(fieldName.contentEquals("f_category_type"))
			{
				val=readExtendSQl.f_category_type_map.get(key);
			}
			
			if(val==null)
			{
				val="";
			}
            //System.out.println("key"+key+":value"+val);
			doc.add(new StoredField(fieldInfo.getIndexfield()+"_store", val));
			
		} catch (Exception e) {
			LOG.error("fieldName {} {}", fieldName, e);
			e.printStackTrace();
		}
		
	}
	
	
	
	
	
	
	
	
	
}
