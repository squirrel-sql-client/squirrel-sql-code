package net.sourceforge.squirrel_sql.fw.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NetezzaSynonym {
	private String catalog;
	private String schema;
	private String table;
	
	private static final String SQL_SYNONYM_REFERENCE = 
			"select " +
			"synonym_name, refdatabase, refschema, refobjname " +
			"from _v_synonym " +
			"where synonym_name = ? ";
	
	public NetezzaSynonym(String catalog, String schema, String table) {
		this.catalog = catalog;
		this.schema = schema;
		this.table = table;
	}

	public static PreparedStatement getSqlSynonymReference(Connection connection) throws SQLException {
		PreparedStatement pstmt = connection.prepareStatement(SQL_SYNONYM_REFERENCE);
		return pstmt;
	}
	
	public String getCatalog() {
		return catalog;
	}
	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}
	public String getSchema() {
		return schema;
	}
	public void setSchema(String schema) {
		this.schema = schema;
	}
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
}
