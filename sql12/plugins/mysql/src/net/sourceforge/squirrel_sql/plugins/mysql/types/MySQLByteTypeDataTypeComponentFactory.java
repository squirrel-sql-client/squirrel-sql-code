
package net.sourceforge.squirrel_sql.plugins.mysql.types;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeShort;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponentFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;

/**
 * A factory that creates DataTypeShort for rendering columns of MySQL TINYINT UNSIGNED.
 * 
 */
public class MySQLByteTypeDataTypeComponentFactory implements
		IDataTypeComponentFactory {

	/**
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponentFactory#constructDataTypeComponent()
	 */
	public IDataTypeComponent constructDataTypeComponent() {
		return new DataTypeShort(null, new ColumnDisplayDefinition(10, "dummy"));
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponentFactory#getDialectType()
	 */
	public DialectType getDialectType() {
		return DialectType.MYSQL;
	}

}
