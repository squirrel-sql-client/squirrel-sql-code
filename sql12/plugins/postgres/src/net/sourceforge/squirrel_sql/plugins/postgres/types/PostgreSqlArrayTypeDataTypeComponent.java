/*
 * Copyright (C) 2007 Rob Manning manningr@users.sourceforge.net This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version. This library is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser
 * General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package net.sourceforge.squirrel_sql.plugins.postgres.types;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;


import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.BaseDataTypeComponent;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause.EmptyWhereClausePart;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause.IWhereClausePart;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause.IsNullWhereClausePart;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.SquirrelConstants;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * A custom DatatType implementation of IDataTypeComponent that can handle
 * Postgis array
 * 
 * @author jarmolow
 */
public class PostgreSqlArrayTypeDataTypeComponent extends BaseDataTypeComponent {

	/** Logger for this class. */
	private static ILogger s_log = LoggerController
			.createLogger(PostgreSqlArrayTypeDataTypeComponent.class);

	/**
	 * Internationalized strings for this class.
	 */
	private static final StringManager s_stringMgr = StringManagerFactory
			.getStringManager(PostgreSqlArrayTypeDataTypeComponent.class);

	/**
	 * I18n messages
	 */
	static interface i18n {

		// i18n[PostgreSqlXmlTypeDataTypeComponent.cellErrorMsg=<Error: see log
		// file>]
		String CELL_ERROR_MSG = s_stringMgr
				.getString("PostgreSqlXmlTypeDataTypeComponent.cellErrorMsg");
	}

	/*
	 * IDataTypeComponent interface methods /**
	 * 
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.
	 * IDataTypeComponent#canDoFileIO()
	 */
	@Override
	public boolean canDoFileIO() {
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent#getClassName()
	 */
	@Override
	public String getClassName() {
		return String.class.getName();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent#getDefaultValue(java.lang.String)
	 */
	@Override
	public Object getDefaultValue(final String dbDefaultValue) {
		if (dbDefaultValue != null) {
			return dbDefaultValue;
		}
		return "{}";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent#getWhereClauseValue(java.lang.Object,
	 *      net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData)
	 */
	@Override
	public IWhereClausePart getWhereClauseValue(final Object value,
			final ISQLDatabaseMetaData md) {
		if (value == null || value.toString() == null)
			return new IsNullWhereClausePart(_colDef);
		else {
			return new EmptyWhereClausePart();
		}
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent#isEditableInCell(java.lang.Object)
	 */
	@Override
	public boolean isEditableInCell(final Object originalValue) {
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent#isEditableInPopup(java.lang.Object)
	 */
	@Override
	public boolean isEditableInPopup(final Object originalValue) {
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent#needToReRead(java.lang.Object)
	 */
	@Override
	public boolean needToReRead(final Object originalValue) {
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent#readResultSet(java.sql.ResultSet,
	 *      int, boolean)
	 */
	@Override
	public Object readResultSet(final ResultSet rs, final int idx,
			final boolean limitDataRead) throws SQLException {
		Object result = null;
		try {
			result = rs.getObject(idx);
			if (result == null) {
				return NULL_VALUE_PATTERN;
			}
		} catch (final Exception e) {
			s_log.error(
					"Unexpected exception while attempting to read PostgreSQL XML column",
					e);
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
	@Override
	public void setPreparedStatementValue(final PreparedStatement pstmt,
			final Object value, final int position) throws SQLException {
		if (value == null) {
			pstmt.setNull(position, java.sql.Types.ARRAY);
		} else {
			try {
				pstmt.setObject(position, value, Types.OTHER);
			} catch (final Exception e) {
				s_log.error(
						"setPreparedStatementValue: Unexpected exception - "
								+ e.getMessage(), e);
			}

		}
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent#useBinaryEditingPanel()
	 */
	@Override
	public boolean useBinaryEditingPanel() {
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent#areEqual(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public boolean areEqual(final Object obj1, final Object obj2) {
		return obj1.equals(obj2);
	}
	
	

	@Override
	protected String quoteTextConditionValue(String value) {
		return StringUtilities.singleQuote(value);
	}

	@Override
	public String[] getSupportedOperators() {
		return new String[] { "=", "<>", ">", "<", ">=", "<=",
				"@>", "<@", "&&",
				SquirrelConstants.IS_NULL, SquirrelConstants.IS_NOT_NULL, };
	}
	
	
}
