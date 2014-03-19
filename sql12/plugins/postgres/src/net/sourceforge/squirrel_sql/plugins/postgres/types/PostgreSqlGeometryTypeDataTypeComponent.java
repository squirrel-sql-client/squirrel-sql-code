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

import java.lang.reflect.Method;
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

import org.postgis.PGgeometry;
import org.postgis.Point;

/**
 * A custom DatatType implementation of IDataTypeComponent that can handle
 * Postgis geometry
 * 
 * @author jarmolow
 */
public class PostgreSqlGeometryTypeDataTypeComponent extends
		BaseDataTypeComponent {

	private static final int NUM_POINTS_INDEX = 3;

	private static final int CENTER_INDEX = 2;

	private static final int TYPE_INDEX = 1;
	
	private static final int BRIEF_INDICATOR_INDEX = 0;

	/** Logger for this class. */
	private static ILogger s_log = LoggerController
			.createLogger(PostgreSqlGeometryTypeDataTypeComponent.class);

	/**
	 * Internationalized strings for this class.
	 */
	private static final StringManager s_stringMgr = StringManagerFactory
			.getStringManager(PostgreSqlGeometryTypeDataTypeComponent.class);

	private static final String BRIEF_INDICATOR = "bRiEf";

	private final PostgreSqlGeometryTypeDataTypeComponentFactory factory;

	/**
	 * I18n messages
	 */
	static interface i18n {

		// i18n[PostgreSqlXmlTypeDataTypeComponent.cellErrorMsg=<Error: see log
		// file>]
		String CELL_ERROR_MSG = s_stringMgr
				.getString("PostgreSqlXmlTypeDataTypeComponent.cellErrorMsg");
		String BRIEF_GEO_FORMAT = "PostgreSqlGeometryTypeDataTypeComponent.briefGeoFormat";
	}


	public PostgreSqlGeometryTypeDataTypeComponent(PostgreSqlGeometryTypeDataTypeComponentFactory factory) {
		this.factory = factory;
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
		Point point = new Point(0, 0);
		try {
			point.setSrid(factory.fetchSrid(_colDef));
		} catch (SQLException sqlE) {
			new RuntimeException(sqlE);
		}
		return point;
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
		return originalValue instanceof String;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent#readResultSet(java.sql.ResultSet,
	 *      int, boolean)
	 */
	@Override
	public Object readResultSet(final ResultSet rs, final int idx,
			final boolean limitDataRead) throws SQLException {

		Object object = rs.getObject(idx);
		if (object ==null) {
			return NULL_VALUE_PATTERN;
		}
		if (limitDataRead) {
			
			String[] split = object.toString().split(";");
			if (split[BRIEF_INDICATOR_INDEX].equals(BRIEF_INDICATOR)) {
				return createBriefInfo(split);
			}
		}

		try {
			//there are problems with postgis, postgres versions so casting to PGgeometry can cause class cast exceptions
			Method method = PGgeometry.class.getMethod("getGeometry");
			return method.invoke(object);
		} catch (final Exception e) {
			s_log.error(
					"Unexpected exception while attempting to read PostgreSQL Geometry column",
					e);
		}
		return i18n.CELL_ERROR_MSG;
	}

	private Object createBriefInfo(String[] info) {
		return s_stringMgr
				.getString(i18n.BRIEF_GEO_FORMAT, info[TYPE_INDEX],
						info[CENTER_INDEX].replace("POINT", ""),
						info[NUM_POINTS_INDEX]);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent#setPreparedStatementValue(java.sql.PreparedStatement,
	 *      java.lang.Object, int)
	 */
	@Override
	public void setPreparedStatementValue(final PreparedStatement pstmt,
			final Object value, final int position) throws SQLException {
		if (value == null) {
			pstmt.setNull(position, java.sql.Types.OTHER);
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
	public String getCondition(String column, String operator, String value) {
		if (operator.startsWith("ST_")) {
			return new StringBuilder().append(operator).append('(')
					.append(column).append(',')
					.append(StringUtilities.singleQuote(value)).append(')')
					.toString();
		} else {
			return super.getCondition(column, operator, value);
		}
	}

	@Override
	public String[] getSupportedOperators() {
		return new String[] { "=", "<>", SquirrelConstants.IN,
				SquirrelConstants.IS_NULL, SquirrelConstants.IS_NOT_NULL,
				"ST_Equals", "ST_Intersects", "ST_Touches", "ST_Crosses",
				"ST_Within", "ST_Overlaps", "ST_Contains", "ST_Covers",
				"ST_CoveredBy", "ST_Intersects" };
	}

	/**
	 * Geometries can be big, so we don't want all of them
	 */
	@Override
	public String getColumnForContentSelect(String columnPrefix) {
		String fullName = columnPrefix + _colDef.getColumnName();
		return "'"+BRIEF_INDICATOR+";'||GeometryType(" + fullName + ")||';'||st_asText(st_centroid("
				+ fullName + "))||';'||st_NPoints(" + fullName + ")";
	}
}
