
package com.huifenqi.index.index.field;
/**
 * @author majianchun
 *
 */
public class FieldInfo
{
	int FieldType;
	float boost;     //boost 都设1
	int multiple;    //0    如果是1，要设置multiplesign="'" 
	int index;       //>0索引  =0 不索引
	int store;       // >0 保存  =0 不保存
	int spellcheck;  //先不设
	int whole;       // >0 做完全匹配
	int analyzed;    //>0 分词  =0 不分词
	String indexfield;    // 1 int  2 long  3 double  4 datatime  5 time  0 string 
	String multiplesign;   
	int extend;
	int sort;
	
	
	
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}
	FieldHandler handler = null;   //其他处理
	
	
	public int getAnalyzed() {
		return analyzed;
	}
	public void setAnalyzed(int analyzed) {
		this.analyzed = analyzed;
	}
	public int getWhole() {
		return whole;
	}
	public void setWhole(int whole) {
		this.whole = whole;
	}
	public int getSpellcheck() {
		return spellcheck;
	}
	public void setSpellcheck(int spellcheck) {
		this.spellcheck = spellcheck;
	}
	public FieldHandler getHandler() {
		return handler;
	}
	public void setHandler(FieldHandler handler) {
		this.handler = handler;
	}
	public FieldInfo()
	{	

	}
	public int getFieldType()
	{
		return FieldType;
	}
	public void setFieldType(int fieldType)
	{
		FieldType = fieldType;
	}
	public float getBoost()
	{
		return boost;
	}
	public void setBoost(float boost)
	{
		this.boost = boost;
	}
	public int getIndex()
	{
		return index;
	}
	public void setIndex(int index)
	{
		this.index = index;
	}
	public int getStore()
	{
		return store;
	}
	public void setStore(int store)
	{
		this.store = store;
	}
	public String getIndexfield()
	{
		return indexfield;
	}
	public void setIndexfield(String indexfield)
	{
		this.indexfield = indexfield;
	}
	public String getMultiplesign()
	{
		return multiplesign;
	}
	public void setMultiplesign(String multiplesign)
	{
		this.multiplesign = multiplesign;
	}
	public int getMultiple()
	{
		return multiple;
	}
	public void setMultiple(int multiple)
	{
		this.multiple = multiple;
	}
	public int getExtend() {
		return extend;
	}
	public void setExtend(int extend) {
		this.extend = extend;
	}

}
