/*
 * Copyright (C) 2007 Rob Manning
 * manningr@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.plugins.oracle.types;

import java.lang.reflect.Method;
import java.sql.Connection;
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
 * A custom DatatType implementation of IDataTypeComponent that can handle Oracle's SYS.XMLTYPE (DataType
 * value of 2007). This requires that the XDK (XML Developer Kit) be downloaded from Oracle and the jars from
 * this kit included along with the driver in "Extra ClassPath".
 * 
 * @author manningr
 */
public class OracleXmlTypeDataTypeComponent extends BaseDataTypeComponent implements IDataTypeComponent
{

	/**
	 * The fully-qualified name of Oracle's utility class that will convert betweem XMLType and String for us.
	 */
	private static final String XML_TYPE_CLASSNAME = "oracle.xdb.XMLType";

	/**
	 * When we lookup the class def for oracle.xdb.XMLType, save a copy of it so that we don't have to look it
	 * up again.
	 */
	private static Class<?> XML_TYPE_CLASS = null;

	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(OracleXmlTypeDataTypeComponent.class);

	/**
	 * Internationalized strings for this class.
	 */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(OracleXmlTypeDataTypeComponent.class);

	/**
	 * I18n messages
	 */
	static interface i18n
	{
		// i18n[OracleXmlTypeDataTypeComponent.cellErrorMsg=<Error: see log file>]
		String CELL_ERROR_MSG = s_stringMgr.getString("OracleXmlTypeDataTypeComponent.cellErrorMsg");
	}

	/* IDataTypeComponent interface methods 
	
	/**
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent#canDoFileIO()
	 */
	public boolean canDoFileIO()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent#getClassName()
	 */
	public String getClassName()
	{
		return "java.lang.String";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent#getDefaultValue(java.lang.String)
	 */
	public Object getDefaultValue(String dbDefaultValue)
	{
		// At the moment, no default value
		if (s_log.isInfoEnabled())
		{
			s_log.info("getDefaultValue: not yet implemented");
		}
		return dbDefaultValue;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent#getWhereClauseValue(
	 * java.lang.Object, net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData)
	 */
	public String getWhereClauseValue(Object value, ISQLDatabaseMetaData md)
	{
	  /*
		* For Oracle 10g we could say something like : 
		* 
		* "where XMLSERIALIZE(CONTENT " + _colDef.getLabel() +") like '<value>'"
		* 
		* This doesn't appear to work on Oracle 9i at the moment, so we will
		* avoid using this column in any where clause if the value is non-null,
		* which is what the superclass implementation does.
		* 
		* TODO: Find a way to do this for both versions or split this behavior
		* so that it works on 10g and is disabled on 9i.
		*/
		return super.getWhereClauseValue(value, md);
	}

	/**
	 * This Data Type can be edited in a table cell as long as there are no issues using the XDK to display the
	 * data. If we should encounter Exceptions using XDK, then we should prevent the user from editing the cell
	 * (our error message is not meant to be valid XML data; further, we don't want to let the user whack their
	 * data with our tool accidentally)
	 * 
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent#isEditableInCell(java.lang.Object)
	 */
	public boolean isEditableInCell(Object originalValue)
	{
		return !i18n.CELL_ERROR_MSG.equals(originalValue);
	}

	/**
	 * This Data Type can be edited in a popup as long as there are no issues using the XDK to display the
	 * data. If we should encounter Exceptions using XDK, then we should prevent the user from editing the cell
	 * (our error message is not meant to be valid XML data; further, we don't want to let the user whack their
	 * data with our tool accidentally)
	 * 
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent#isEditableInPopup(java.lang.Object)
	 */
	public boolean isEditableInPopup(Object originalValue)
	{
		return !i18n.CELL_ERROR_MSG.equals(originalValue);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent#needToReRead(java.lang.Object)
	 */
	public boolean needToReRead(Object originalValue)
	{
		return false;
	}

	/**
	 * This class relies on reflection to get a handle to Oracle's XMLType which is made available separately
	 * from the JDBC driver, so we cannot just assume the user will always have, nor can we depend on it to
	 * compile SQuirreL code. So we remove this dependency here by using reflection which doesn't require this
	 * library in order to just compile the code.
	 * 
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent#readResultSet(java.sql.ResultSet,
	 *      int, boolean)
	 */
	public Object readResultSet(ResultSet rs, int idx, boolean limitDataRead) throws SQLException
	{
		Object result = null;
		try
		{
			Object o = rs.getObject(idx);
			if (o == null)
			{
				return NULL_VALUE_PATTERN;
			}
			else if ("oracle.sql.OPAQUE".equals(o.getClass().getName()))
			{
				Method createXmlMethod = getCreateXmlMethod(o.getClass());

				// Below is equivalent to the following:
				// xmlType = XMLType.createXML(o);
				Object xmlTypeObj = createXmlMethod.invoke(null, o);
				Method getStringValMethod = XML_TYPE_CLASS.getMethod("getStringVal", (Class[]) null);

				// Below is equivalent to the following:
				// stringValueResult = xmlType.getStringVal();
				Object stringValueResult = getStringValMethod.invoke(xmlTypeObj, (Object[]) null);
				result = stringValueResult;

			}
			else if (XML_TYPE_CLASSNAME.equals(o.getClass().getName()))
			{
				XML_TYPE_CLASS = o.getClass();
				Method getStringValMethod = XML_TYPE_CLASS.getMethod("getStringVal", (Class[]) null);

				// Below is equivalent to the following:
				// stringValueResult = xmlType.getStringVal();
				Object stringValueResult = getStringValMethod.invoke(o, (Object[]) null);
				result = stringValueResult;
			}
			else
			{
				result = o;
			}
		}
		catch (ClassNotFoundException e)
		{
			s_log.error("Perhaps the XDK, which contains the class " + XML_TYPE_CLASSNAME
				+ " is not in the CLASSPATH?", e);
		}
		catch (Exception e)
		{
			s_log.error("Unexpected exception while attempting to read " + "SYS.XMLType column", e);
		}
		if (result == null)
		{
			result = i18n.CELL_ERROR_MSG;
		}
		return result;
	}

	/**
	 * This method attempts to find a method called createXML or createXml in the class oracle.xdb.XMLType
	 * using reflection.
	 * 
	 * @param argClasses
	 *           the classes of the arguments that are used to lookup the method.
	 * @return the Method object obtained by using reflection.
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 */
	private Method getCreateXmlMethod(Class<?>... argClasses) throws ClassNotFoundException,
		NoSuchMethodException
	{
		if (XML_TYPE_CLASS == null)
		{
			XML_TYPE_CLASS = Class.forName(XML_TYPE_CLASSNAME);
		}

		Method createXmlMethod = null;

		try
		{
			createXmlMethod = XML_TYPE_CLASS.getMethod("createXML", argClasses);
		}
		catch (SecurityException e)
		{ /* Do nothing - the method could be called createXml instead */
		}
		catch (NoSuchMethodException e)
		{ /* Do nothing - the method could be called createXml instead */
		}

		if (createXmlMethod == null)
		{
			try
			{
				createXmlMethod = XML_TYPE_CLASS.getMethod("createXml", argClasses);
			}
			catch (SecurityException e)
			{
				s_log.error("getCreateXmlMethod: Unable to get method named createXml or createXML in class "
					+ "oracle.xdb.XMLType: " + e.getMessage(), e);
				throw e;
			}
			catch (NoSuchMethodException e)
			{
				s_log.error("getCreateXmlMethod: Unable to get method named createXml or createXML in class "
					+ "oracle.xdb.XMLType: " + e.getMessage(), e);
				throw e;
			}

		}
		return createXmlMethod;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent#setPreparedStatementValue(java.sql.PreparedStatement,
	 *      java.lang.Object, int)
	 */
	public void setPreparedStatementValue(PreparedStatement pstmt, Object value, int position)
		throws SQLException
	{
		if (value == null)
		{
			// Throws an exception claiming that 2007 isn't a valid type - go
			// figure.
			// pstmt.setNull(position, _colDef.getSqlType());

			// Both of these throw an exception claiming that it got a clob
			// and expected a number (inconsistent data types):
			//
			// pstmt.setClob(position, null);
			// pstmt.setNull(position, java.sql.Types.CLOB);
			//

			// This seems to work for both Oracle 9i and 10g
			pstmt.setObject(position, null);
		}
		else
		{
			try
			{
				Class<?>[] args = new Class[] { Connection.class, String.class };

				Method createXmlMethod = getCreateXmlMethod(args);

				Object xmlTypeObj = createXmlMethod.invoke(null, pstmt.getConnection(), value.toString());

				// now bind the string..
				pstmt.setObject(position, xmlTypeObj);
			}
			catch (Exception e)
			{
				s_log.error("setPreparedStatementValue: Unexpected exception - " + e.getMessage(), e);
			}

		}
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent#useBinaryEditingPanel()
	 */
	public boolean useBinaryEditingPanel()
	{
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent#areEqual(java.lang.Object,
	 *      java.lang.Object)
	 */
	public boolean areEqual(Object obj1, Object obj2)
	{
		return ((String) obj1).equals(obj2);
	}

}
