
package net.sourceforge.squirrel_sql.plugins.mysql.types;

import net.sourceforge.squirrel_sql.fw.dialects.DialectType;

/**
 * A factory that creates Data for rendering columns of
 * {@code DB2Types.XML}.
 * 
 */
public class MySQL5ByteTypeDataTypeComponentFactory extends MySQLByteTypeDataTypeComponentFactory {

	/**
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponentFactory#getDialectType()
	 */
	public DialectType getDialectType() {
		return DialectType.MYSQL5;
	}

}
