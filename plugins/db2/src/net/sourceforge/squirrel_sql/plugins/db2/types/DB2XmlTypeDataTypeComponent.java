/*
 * Copyright (C) 2008 Lars Heller lhe@cedros.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sourceforge.squirrel_sql.plugins.db2.types;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.BaseDataTypeComponent;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * A custom DatatType implementation of IDataTypeComponent that can handle DB2's
 * {@code DB2Types.XML} (since DB2 Version V9). This requires the DB2 JCC Driver
 * included in the "Extra ClassPath".
 * 
 * @author Lars Heller
 */
public class DB2XmlTypeDataTypeComponent extends BaseDataTypeComponent
		implements IDataTypeComponent {

	/** Logger for this class. */
	private static final ILogger s_log = LoggerController
			.createLogger(DB2XmlTypeDataTypeComponent.class);

	/**
	 * Internationalized strings for this class.
	 */
	private static final StringManager s_stringMgr = StringManagerFactory
			.getStringManager(DB2XmlTypeDataTypeComponent.class);

	/**
	 * I18n messages
	 */
	static interface i18n {
		String CELL_ERROR_MSG = s_stringMgr
				.getString("DB2XmlTypeDataTypeComponent.cellErrorMsg");
	}

	/* IDataTypeComponent interface methods */

	/**
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent#canDoFileIO()
	 */
	public boolean canDoFileIO() {
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent#getClassName()
	 */
	public String getClassName() {
		return String.class.getName();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent#getDefaultValue(java.lang.String)
	 */
	public Object getDefaultValue(String dbDefaultValue) {
		// At the moment, no default value
		if (s_log.isInfoEnabled()) {
			s_log.info("getDefaultValue: not yet implemented");
		}
		return null;
	}

	/**
	 * To compare the given value in a where clause with a column, the column's
	 * content gets serialized to a CLOB. Comparison then can take place as a
	 * string comparison.
	 * 
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent#getWhereClauseValue(java.lang.Object,
	 *      net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData)
	 */
	public String getWhereClauseValue(Object value, ISQLDatabaseMetaData md) {
		StringBuilder where = new StringBuilder();
		if (value == null || value.toString() == null) {
			where.append(_colDef.getFullTableColumnName());
			where.append(" IS NULL");
		} else {
			where.append("XMLSERIALIZE (CONTENT ");
			where.append(_colDef.getFullTableColumnName());
			where.append(" AS CLOB(1M)) like '");
			where.append(value);
			where.append("'");
		}
		return where.toString();
	}

	/**
	 * This Data Type can be edited in a table cell as long as there are no
	 * issues displaying the data.
	 * 
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent#isEditableInCell(java.lang.Object)
	 */
	public boolean isEditableInCell(Object originalValue) {
		return !i18n.CELL_ERROR_MSG.equals(originalValue);
	}

	/**
	 * This Data Type can be edited in a popup as long as there are no issues
	 * using displaying the data.
	 * 
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent#isEditableInPopup(java.lang.Object)
	 */
	public boolean isEditableInPopup(Object originalValue) {
		return !i18n.CELL_ERROR_MSG.equals(originalValue);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent#needToReRead(java.lang.Object)
	 */
	public boolean needToReRead(Object originalValue) {
		return false;
	}

	/**
	 * Column value is read by retrieving the value via {@link ResultSet#getString(int)}.
	 * Alternatively we could use {@link ResultSet#getObject(int)}, cast it to
	 * DB2Xml and use DB2's methods for retrieving the data. But this would implicate a dependency
	 * on the DB2 driver classes for building this plugin
	 * 
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent#readResultSet(java.sql.ResultSet,
	 *      int, boolean)
	 * @see <a
	 *      href="ftp://ftp.software.ibm.com/ps/products/db2/info/vr9/pdf/letter/en_US/db2aje90.pdf">DB2
	 *      Version 9 - Developing Java Applications</a>
	 */
	public Object readResultSet(ResultSet rs, int idx, boolean limitDataRead)
			throws SQLException {
		String result = null;
		try {
			result = rs.getString(idx);
			if (rs.wasNull() || result == null) {
				return NULL_VALUE_PATTERN;
			}
		} catch (Exception e) {
			s_log.error("Unexpected exception while attempting to read "
					+ "SYS.XMLType column", e);
		}
		if (result == null) {
			result = i18n.CELL_ERROR_MSG;
		}
		return result;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent#setPreparedStatementValue(java.sql.PreparedStatement,
	 *      java.lang.Object, int)
	 */
	public void setPreparedStatementValue(PreparedStatement pstmt,
			Object value, int position) throws SQLException {
		try {
			if (value == null) {
				pstmt.setNull(position, _colDef.getSqlType(), _colDef
						.getSqlTypeName());
			} else {
				pstmt.setString(position, value.toString());

			}
		} catch (Exception e) {
			s_log.error("setPreparedStatementValue: Unexpected exception - "
					+ e.getMessage(), e);
		}
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent#useBinaryEditingPanel()
	 */
	public boolean useBinaryEditingPanel() {
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent#areEqual(java.lang.Object,
	 *      java.lang.Object)
	 */
	public boolean areEqual(Object obj1, Object obj2) {
		return ((String) obj1).equals(obj2);
	}

}