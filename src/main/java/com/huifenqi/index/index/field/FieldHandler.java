package com.huifenqi.index.index.field;

import java.sql.ResultSet;

import org.apache.lucene.document.Document;


public interface FieldHandler {
	public void HandleStringTpye(String fieldName, FieldInfo fieldInfo, Document doc, ResultSet rst);
}
