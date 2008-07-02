/*
 * Copyright (C) 2008 Michael Romankiewicz
 * microm at users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package net.sourceforge.squirrel_sql.plugins.smarttools;

import java.awt.print.PrinterException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTable.PrintMode;
import javax.swing.table.TableColumnModel;

import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class SmarttoolsHelper {
	// Logger for this class
    private final static ILogger log = LoggerController.createLogger(SmarttoolsHelper.class);
    // line separator for easy using ;-)
    public final static String CR = System.getProperty("line.separator", "\n");

    
    private SmarttoolsHelper() {}
	
    /**
     * Load an icon image with iconName from the sub package images to package of class FirebirdManagerPlugin
     * @param imageIconName filename of the icon image
     * @return ImageIcon or null
     */
	public static ImageIcon loadIcon(String imageIconName) {
        URL imgURL = SmarttoolsPlugin.class.getResource("images/" + imageIconName);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            log.error("Couldn't find file: images/" + imageIconName);
            return null;
        }
	}

	
	/**
	 * Get the parsed string as int or defaultValue if an exception occured
	 * @param string string to convert
	 * @param defaultValue default value which will be returned on an exception
	 * @return converted string or defaultValue
	 */
	public static int convertStringToIntDef(String string, int defaultValue) {
		try {
			return Integer.parseInt(string);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	
	/**
	 * Print the content of the given table
	 * @param table tables content to print
	 * @param headerText header text for output page
	 * @param footerText footer text for output page
	 */
	public static void printTable(JTable table, String headerText, String footerText) {
		try {
			MessageFormat headerFormat = new MessageFormat(headerText);
			MessageFormat footerFormat = new MessageFormat(footerText);
			table.print(PrintMode.FIT_WIDTH, headerFormat,
					footerFormat);
		} catch (PrinterException e) {
			log.error(e.getLocalizedMessage());
		}
	}

	
	/**
	 * Get s list of jdbc datatypes and names in SmarttoolsDataTypes
	 * @return jdbc datatypes and names
	 */
	public static List<STDataType> getListSmarttoolsDataType(boolean withGroupNull) {
		ArrayList<STDataType> listSmarttoolsDataType = new ArrayList<STDataType>();

		listSmarttoolsDataType.add(new STDataType(-1, "Group integer", STDataType.GROUP_INT));
		listSmarttoolsDataType.add(new STDataType(-1, "Group char", STDataType.GROUP_CHAR));
		listSmarttoolsDataType.add(new STDataType(-1, "Group numeric", STDataType.GROUP_NUMERIC));
		
		if (withGroupNull) {
			listSmarttoolsDataType.add(new STDataType(Types.NULL, "NULL", STDataType.GROUP_NULL));
		}
		listSmarttoolsDataType.add(new STDataType(Types.BIGINT, "BIGINT", STDataType.GROUP_INT));
		listSmarttoolsDataType.add(new STDataType(Types.CHAR, "CHAR", STDataType.GROUP_CHAR));
		listSmarttoolsDataType.add(new STDataType(Types.DATE, "DATE", STDataType.GROUP_DATE));
		listSmarttoolsDataType.add(new STDataType(Types.DECIMAL, "DECIMAL", STDataType.GROUP_NUMERIC));
		listSmarttoolsDataType.add(new STDataType(Types.DOUBLE, "DOUBLE", STDataType.GROUP_NUMERIC));
		listSmarttoolsDataType.add(new STDataType(Types.FLOAT, "FLOAT", STDataType.GROUP_NUMERIC));
		listSmarttoolsDataType.add(new STDataType(Types.INTEGER, "INTEGER", STDataType.GROUP_INT));
		listSmarttoolsDataType.add(new STDataType(Types.LONGVARCHAR, "LONGVARCHAR", STDataType.GROUP_CHAR));
		listSmarttoolsDataType.add(new STDataType(Types.NUMERIC, "NUMERIC", STDataType.GROUP_NUMERIC));
		listSmarttoolsDataType.add(new STDataType(Types.REAL, "REAL", STDataType.GROUP_NUMERIC));
		listSmarttoolsDataType.add(new STDataType(Types.SMALLINT, "SMALLINT", STDataType.GROUP_INT));
		listSmarttoolsDataType.add(new STDataType(Types.TIME, "TIME", STDataType.GROUP_DATE));
		listSmarttoolsDataType.add(new STDataType(Types.TIMESTAMP, "TIMESTAMP", STDataType.GROUP_DATE));
		listSmarttoolsDataType.add(new STDataType(Types.TINYINT, "TINYINT", STDataType.GROUP_INT));
		listSmarttoolsDataType.add(new STDataType(Types.VARCHAR, "VARCHAR", STDataType.GROUP_CHAR));
		
		return listSmarttoolsDataType;
	}
	
	/**
	 * Filled the given JComboBox with operator values
	 * @param cbOperator JComboBox to fill
	 * @param usedGroup data type group (@see STDataType)
	 */
	public static void fillOperatorTypes(JComboBox cbOperator, int usedGroup) {
		cbOperator.removeAllItems();

		cbOperator.addItem("is null");
		cbOperator.addItem("is not null");
		cbOperator.addItem("=");
		cbOperator.addItem("<>");
		cbOperator.addItem(">");
		cbOperator.addItem("<");
		if (usedGroup == STDataType.GROUP_CHAR) {
			cbOperator.addItem("like");
			cbOperator.addItem("not like");
		}
		cbOperator.setSelectedIndex(0);
	}

	
	/**
	 * Returns true if dataType is a valid string data type
	 * @param dataType data type to check
	 * @return true/false
	 */
	public static boolean isDataTypeString(int dataType) {
		return dataType == Types.CHAR 
			|| dataType == Types.LONGVARCHAR
			|| dataType == Types.VARCHAR;
	}

	/**
	 * Returns true if dataType is a valid int data type
	 * @param dataType data type to check
	 * @return true/false
	 */
	public static boolean isDataTypeInt(int dataType) {
		return dataType == Types.BIGINT 
			|| dataType == Types.INTEGER
			|| dataType == Types.SMALLINT 
			|| dataType == Types.TINYINT;
	}

	/**
	 * Returns true if dataType is a valid numeric data type
	 * @param dataType data type to check
	 * @return true/false
	 */
	public static boolean isDataTypeNumeric(int dataType) {
		return dataType == Types.DECIMAL 
			|| dataType == Types.DOUBLE
			|| dataType == Types.FLOAT 
			|| dataType == Types.NUMERIC
			|| dataType == Types.REAL;
	}

	/**
	 * Returns true if dataType is a valid date data type
	 * @param dataType data type to check
	 * @return true/false
	 */
	public static boolean isDataTypeDate(int dataType) {
		return dataType == Types.DATE 
			|| dataType == Types.TIMESTAMP
			|| dataType == Types.TIME;
	}
	
	
	/**
	 * Returns the readable data type for the given tableColumnInfo
	 * @param tableColumnInfo data to generate the readable data type info
	 * @return readable data type info
	 */
	public static String getDataTypeForDisplay(TableColumnInfo tableColumnInfo) {
		StringBuffer buf = new StringBuffer();

		buf.append(tableColumnInfo.getTypeName());
		if (SmarttoolsHelper.isDataTypeString(tableColumnInfo.getDataType())) {
			buf.append(" (" + tableColumnInfo.getColumnSize() + ")");
		} else if (SmarttoolsHelper.isDataTypeNumeric(tableColumnInfo.getDataType())) {
			buf.append(" (" + tableColumnInfo.getColumnSize() + ","
					+ tableColumnInfo.getDecimalDigits() + ")");
		}

		return buf.toString();
	}
	
	
	/**
	 * Check the columns data and returns found record number
	 * @param stmt Statement to execute query
	 * @param sql SQL-Statement for executeQuery
	 * @return number of found records
	 * @throws SQLException SQLException
	 */
	public static int checkColumnData(Statement stmt, String sql)
			throws SQLException {
		int resultFound = 0;
		ResultSet rs = stmt.executeQuery(sql);
		if (rs.next()) {
			resultFound = rs.getInt(1);
		}
		rs.close();
		return resultFound;
	}
	
	/**
	 * Mark or demark all rows in column col of table tbl
	 * @param tbl JTable
	 * @param col column to mark or demark
	 * @param mark true=mark; false=demark
	 */
	public static void markAllRows(JTable tbl, int col, boolean mark) {
		for (int row = 0; row < tbl.getRowCount(); row++) {
			tbl.setValueAt(new Boolean(mark), row, col);
		}
	}
	
	/**
	 * Set the widths for the columns of table 
	 * @param table table to set the column width
	 * @param colWidth array with the new column width
	 */
	public static void setColumnWidth(JTable table, int[] colWidth) {
		TableColumnModel tcm = table.getColumnModel();
		for (int col = 0; col < tcm.getColumnCount(); col++) {
			if (col < colWidth.length) {
				tcm.getColumn(col).setPreferredWidth(colWidth[col]);
			}
		}
		table.doLayout();
	}
	
	/**
	 * Returns the row count of the given table
	 * @param stmt Statement for sql 
	 * @param tableName table to check
	 * @return row count
	 * @throws SQLException SQLException
	 */
	public static int getRowCount(Statement stmt, String tableName) throws SQLException {
		int rowCount = 0;
		ResultSet rs  = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName);
		if (rs.next()) {
			rowCount = rs.getInt(1);
		}
		rs.close();
		
		return rowCount;
	}
}
