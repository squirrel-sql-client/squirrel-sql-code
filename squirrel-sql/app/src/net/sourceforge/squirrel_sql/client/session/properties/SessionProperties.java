package net.sourceforge.squirrel_sql.client.session.properties;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
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
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import net.sourceforge.squirrel_sql.fw.gui.FontInfo;
import net.sourceforge.squirrel_sql.fw.util.PropertyChangeReporter;

public class SessionProperties implements Serializable
{
	public interface IDataSetDestinations
	{
		String TEXT =
			net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTextPanel.class.getName();
		String TABLE =
			net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel.class.getName();
	}

	public interface IPropertyNames
	{
		String AUTO_COMMIT = "autoCommit";
		String COLUMN_PRIVILIGES_OUTPUT_CLASS_NAME = "columnPriviligesOutputClassName";
		String COLUMNS_OUTPUT_CLASS_NAME = "columnsOutputClassName";
		String COMMIT_ON_CLOSING_CONNECTION = "commitOnClosingConnection";
		String CONTENTS_LIMIT_ROWS = "contentsLimitRows";
		String CONTENTS_NBR_ROWS_TO_SHOW = "contentsNbrOfRowsToShow";
		String CONTENTS_OUTPUT_CLASS_NAME = "contentsOutputClassName";
		String DATA_TYPES_OUTPUT_CLASS_NAME = "dataTypesOutputClassName";
		String EXP_KEYS_OUTPUT_CLASS_NAME = "exportedKeysOutputClassName";
		String FONT_INFO = "fontInfo";
		String IMP_KEYS_OUTPUT_CLASS_NAME = "importedKeysOutputClassName";
		String INDEXES_OUTPUT_CLASS_NAME = "indexesOutputClassName";
		String META_DATA_OUTPUT_CLASS_NAME = "metaDataOutputClassName";
		String PRIM_KEY_OUTPUT_CLASS_NAME = "primaryKeyOutputClassName";
		String PRIVILIGES_OUTPUT_CLASS_NAME = "priviligesOutputClassName";
		String PROC_COLUMNS_OUTPUT_CLASS_NAME = "procedureColumnsOutputClassName";
		String ROWID_OUTPUT_CLASS_NAME = "rowIdOutputClassName";
		String SHOW_ROW_COUNT = "showRowCount";
		String SHOW_TOOL_BAR = "showToolBar";
		String SQL_LIMIT_ROWS = "sqlLimitRows";
		String SQL_NBR_ROWS_TO_SHOW = "sqlNbrOfRowsToShow";
		String SQL_OUTPUT_RESULTSET_CLASS_NAME = "sqlOutputResultSetClassName";
		String SQL_OUTPUT_META_DATA_CLASS_NAME = "sqlOutputMetaDataClassName";
		String SQL_STATEMENT_SEPARATOR = "sqlStatementSeparator";
		String TABLE_OUTPUT_CLASS_NAME = "tableOutputClassName";
		String VERSIONS_OUTPUT_CLASS_NAME = "versionsOutputClassName";
	}

	/** Object to handle property change events. */
	private PropertyChangeReporter _propChgReporter = new PropertyChangeReporter(this);

	private boolean _autoCommit = true;
	private int _contentsNbrRowsToShow = 100;
	private int _sqlNbrRowsToShow = 100;

	/**
	 * If <CODE>true</CODE> then issue a commit when closing a connection
	 * else issue a rollback. This property is only valid if the
	 * connection is not in auto-commit mode.
	 */
	private boolean _commitOnClosingConnection = false;

	private boolean _contentsLimitRows = true;
	private boolean _sqlLimitRows = true;

	private String _dataTypesOutputClassName = IDataSetDestinations.TABLE;
	private String _metaDataOutputClassName = IDataSetDestinations.TABLE;
	private String _tableOutputClassName = IDataSetDestinations.TABLE;
	private String _contentsOutputClassName = IDataSetDestinations.TABLE;
	private String _columnsOutputClassName = IDataSetDestinations.TABLE;
	private String _priviligesOutputClassName = IDataSetDestinations.TABLE;
	private String _versionsOutputClassName = IDataSetDestinations.TABLE;
	private String _primaryKeyOutputClassName = IDataSetDestinations.TABLE;
	private String _exportedKeysOutputClassName = IDataSetDestinations.TABLE;
	private String _importedKeysOutputClassName = IDataSetDestinations.TABLE;
	private String _indexesOutputClassName = IDataSetDestinations.TABLE;
	private String _columnPriviligesOutputClassName = IDataSetDestinations.TABLE;
	private String _rowIdOutputClassName = IDataSetDestinations.TABLE;

	/** Class name for panel to display Procedure Columns info in. */
	private String _procColsOutputClassName = IDataSetDestinations.TABLE;

	/**
	 * <TT>true</TT> if row count should be displayed for every table in object tree.
	 */
	private boolean _showRowCount = false;

	/** <TT>true</TT> if toolbar should be shown. */
	private boolean _showToolbar = true;

	/**
	 * Name of the class to use as the <TT>IDataSetModelConverter</TT>
	 * for SQL output.
	 */
	//private String _sqlOutputClassName = IDataSetDestinations.TABLE;
	private String _sqlOutputConverterClassName = IDataSetDestinations.TABLE;

	private String _sqlOutputMetaDataClassName = IDataSetDestinations.TABLE;

	private char _sqlStmtSepChar = ';';

	/** Font information for the jEdit text area. */
	private FontInfo _fi;

	public SessionProperties()
	{
		super();
	}

	public void assignFrom(SessionProperties rhs)
	{
		setAutoCommit(rhs.getAutoCommit());
		setColumnsOutputClassName(rhs.getColumnsOutputClassName());
		setColumnPriviligesOutputClassName(rhs.getColumnPriviligesOutputClassName());
		setCommitOnClosingConnection(rhs.getCommitOnClosingConnection());
		setContentsLimitRows(rhs.getContentsLimitRows());
		setContentsNbrRowsToShow(rhs.getContentsNbrRowsToShow());
		setContentsOutputClassName(rhs.getContentsOutputClassName());
		setDataTypesOutputClassName(rhs.getDataTypesOutputClassName());
		setExportedKeysOutputClassName(rhs.getExportedKeysOutputClassName());
		setFontInfo(rhs.getFontInfo());
		setImportedKeysOutputClassName(rhs.getImportedKeysOutputClassName());
		setIndexesOutputClassName(rhs.getIndexesOutputClassName());
		setMetaDataOutputClassName(rhs.getMetaDataOutputClassName());
		setPrimaryKeyOutputClassName(rhs.getPrimaryKeyOutputClassName());
		setPriviligesOutputClassName(rhs.getPriviligesOutputClassName());
		setProcedureColumnsOutputClassName(rhs.getProcedureColumnsOutputClassName());
		setRowIdOutputClassName(rhs.getRowIdOutputClassName());
		setShowRowCount(rhs.getShowRowCount());
		setShowToolBar(rhs.getShowToolBar());
		setSqlLimitRows(rhs.getSqlLimitRows());
		setSqlNbrRowsToShow(rhs.getSqlNbrRowsToShow());
		setSqlOutputResultSetClassName(rhs.getSqlOutputResultSetClassName());
		setSqlOutputMetaDataClassName(rhs.getSqlOutputMetaDataClassName());
		setSqlStatementSeparatorChar(rhs.getSqlStatementSeparatorChar());
		setTableOutputClassName(rhs.getTableOutputClassName());
		setVersionsOutputClassName(rhs.getVersionsOutputClassName());
	}

	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		_propChgReporter.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		_propChgReporter.removePropertyChangeListener(listener);
	}

	public String getDataTypesOutputClassName()
	{
		return _dataTypesOutputClassName;
	}

	public void setDataTypesOutputClassName(String value)
	{
		if (value == null)
		{
			value = "";
		}
		if (!_dataTypesOutputClassName.equals(value))
		{
			final String oldValue = _dataTypesOutputClassName;
			_dataTypesOutputClassName = value;
			_propChgReporter.firePropertyChange(
				IPropertyNames.DATA_TYPES_OUTPUT_CLASS_NAME,
				oldValue,
				_dataTypesOutputClassName);
		}
	}

	public String getMetaDataOutputClassName()
	{
		return _metaDataOutputClassName;
	}

	public void setMetaDataOutputClassName(String value)
	{
		if (value == null)
		{
			value = "";
		}
		if (!_metaDataOutputClassName.equals(value))
		{
			final String oldValue = _metaDataOutputClassName;
			_metaDataOutputClassName = value;
			_propChgReporter.firePropertyChange(
				IPropertyNames.META_DATA_OUTPUT_CLASS_NAME,
				oldValue,
				_metaDataOutputClassName);
		}
	}

	public String getTableOutputClassName()
	{
		return _tableOutputClassName;
	}

	public void setTableOutputClassName(String value)
	{
		if (value == null)
		{
			value = "";
		}
		if (!_tableOutputClassName.equals(value))
		{
			final String oldValue = _tableOutputClassName;
			_tableOutputClassName = value;
			_propChgReporter.firePropertyChange(
				IPropertyNames.TABLE_OUTPUT_CLASS_NAME,
				oldValue,
				_tableOutputClassName);
		}
	}

	public String getContentsOutputClassName()
	{
		return _contentsOutputClassName;
	}

	public void setContentsOutputClassName(String value)
	{
		if (value == null)
		{
			value = "";
		}
		if (!_contentsOutputClassName.equals(value))
		{
			final String oldValue = _contentsOutputClassName;
			_contentsOutputClassName = value;
			_propChgReporter.firePropertyChange(
				IPropertyNames.CONTENTS_OUTPUT_CLASS_NAME,
				oldValue,
				_contentsOutputClassName);
		}
	}

	public String getColumnsOutputClassName()
	{
		return _columnsOutputClassName;
	}

	public void setColumnsOutputClassName(String value)
	{
		if (value == null)
		{
			value = "";
		}
		if (!_columnsOutputClassName.equals(value))
		{
			final String oldValue = _columnsOutputClassName;
			_columnsOutputClassName = value;
			_propChgReporter.firePropertyChange(
				IPropertyNames.COLUMNS_OUTPUT_CLASS_NAME,
				oldValue,
				_columnsOutputClassName);
		}
	}

	public String getPriviligesOutputClassName()
	{
		return _priviligesOutputClassName;
	}

	public void setPriviligesOutputClassName(String value)
	{
		if (value == null)
		{
			value = "";
		}
		if (!_priviligesOutputClassName.equals(value))
		{
			final String oldValue = _priviligesOutputClassName;
			_priviligesOutputClassName = value;
			_propChgReporter.firePropertyChange(
				IPropertyNames.PRIVILIGES_OUTPUT_CLASS_NAME,
				oldValue,
				_priviligesOutputClassName);
		}
	}

	public String getVersionsOutputClassName()
	{
		return _versionsOutputClassName;
	}

	public void setVersionsOutputClassName(String value)
	{
		if (value == null)
		{
			value = "";
		}
		if (!_versionsOutputClassName.equals(value))
		{
			final String oldValue = _versionsOutputClassName;
			_versionsOutputClassName = value;
			_propChgReporter.firePropertyChange(
				IPropertyNames.VERSIONS_OUTPUT_CLASS_NAME,
				oldValue,
				_versionsOutputClassName);
		}
	}

	public String getPrimaryKeyOutputClassName()
	{
		return _primaryKeyOutputClassName;
	}

	public void setPrimaryKeyOutputClassName(String value)
	{
		if (value == null)
		{
			value = "";
		}
		if (!_primaryKeyOutputClassName.equals(value))
		{
			final String oldValue = _primaryKeyOutputClassName;
			_primaryKeyOutputClassName = value;
			_propChgReporter.firePropertyChange(
				IPropertyNames.PRIM_KEY_OUTPUT_CLASS_NAME,
				oldValue,
				_primaryKeyOutputClassName);
		}
	}

	public String getExportedKeysOutputClassName()
	{
		return _exportedKeysOutputClassName;
	}

	public void setExportedKeysOutputClassName(String value)
	{
		if (value == null)
		{
			value = "";
		}
		if (!_exportedKeysOutputClassName.equals(value))
		{
			final String oldValue = _exportedKeysOutputClassName;
			_exportedKeysOutputClassName = value;
			_propChgReporter.firePropertyChange(
				IPropertyNames.EXP_KEYS_OUTPUT_CLASS_NAME,
				oldValue,
				_exportedKeysOutputClassName);
		}
	}

	public String getIndexesOutputClassName()
	{
		return _indexesOutputClassName;
	}

	public void setIndexesOutputClassName(String value)
	{
		if (value == null)
		{
			value = "";
		}
		if (!_indexesOutputClassName.equals(value))
		{
			final String oldValue = _indexesOutputClassName;
			_indexesOutputClassName = value;
			_propChgReporter.firePropertyChange(
				IPropertyNames.INDEXES_OUTPUT_CLASS_NAME,
				oldValue,
				_indexesOutputClassName);
		}
	}

	public String getImportedKeysOutputClassName()
	{
		return _importedKeysOutputClassName;
	}

	public void setImportedKeysOutputClassName(String value)
	{
		if (value == null)
		{
			value = "";
		}
		if (!_importedKeysOutputClassName.equals(value))
		{
			final String oldValue = _importedKeysOutputClassName;
			_importedKeysOutputClassName = value;
			_propChgReporter.firePropertyChange(
				IPropertyNames.IMP_KEYS_OUTPUT_CLASS_NAME,
				oldValue,
				_importedKeysOutputClassName);
		}
	}

	public String getColumnPriviligesOutputClassName()
	{
		return _columnPriviligesOutputClassName;
	}

	public void setColumnPriviligesOutputClassName(String value)
	{
		if (value == null)
		{
			value = "";
		}
		if (!_columnPriviligesOutputClassName.equals(value))
		{
			final String oldValue = _columnPriviligesOutputClassName;
			_columnPriviligesOutputClassName = value;
			_propChgReporter.firePropertyChange(
				IPropertyNames.COLUMN_PRIVILIGES_OUTPUT_CLASS_NAME,
				oldValue,
				_columnPriviligesOutputClassName);
		}
	}

	public String getRowIdOutputClassName()
	{
		return _rowIdOutputClassName;
	}

	public void setRowIdOutputClassName(String value)
	{
		if (value == null)
		{
			value = "";
		}
		if (!_rowIdOutputClassName.equals(value))
		{
			final String oldValue = _rowIdOutputClassName;
			_rowIdOutputClassName = value;
			_propChgReporter.firePropertyChange(
				IPropertyNames.ROWID_OUTPUT_CLASS_NAME,
				oldValue,
				_rowIdOutputClassName);
		}
	}

	/**
	 * Return the name of the class to use as the
	 * <TT>IDataSetViewer</TT> for SQL output.
	 *
	 * @return	the name of the class to use as the
	 *			<TT>IDataSetViewer</TT> for SQL output.
	 */
	public String getSqlOutputResultSetClassName()
	{
		return _sqlOutputConverterClassName;
	}

	/**
	 * Set the name of the class to use as the
	 * <TT>IDataSetViewer</TT> for SQL output.
	 *
	 * @param	value	The name of the class to use as the
	 *					<TT>IDataSetViewer</TT> for
	 *					SQL output.
	 */
	public void setSqlOutputResultSetClassName(String value)
	{
		if (value == null)
		{
			value = "";
		}
		if (!_sqlOutputConverterClassName.equals(value))
		{
			final String oldValue = _sqlOutputConverterClassName;
			_sqlOutputConverterClassName = value;
			_propChgReporter.firePropertyChange(
				IPropertyNames.SQL_OUTPUT_RESULTSET_CLASS_NAME,
				oldValue,
				_sqlOutputConverterClassName);
		}
	}

	public String getSqlOutputMetaDataClassName()
	{
		return _sqlOutputMetaDataClassName;
	}

	public void setSqlOutputMetaDataClassName(String value)
	{
		if (value == null)
		{
			value = "";
		}
		if (!_sqlOutputMetaDataClassName.equals(value))
		{
			final String oldValue = _sqlOutputMetaDataClassName;
			_sqlOutputMetaDataClassName = value;
			_propChgReporter.firePropertyChange(
				IPropertyNames.SQL_OUTPUT_META_DATA_CLASS_NAME,
				oldValue,
				_sqlOutputMetaDataClassName);
		}
	}

	public String getProcedureColumnsOutputClassName()
	{
		return _procColsOutputClassName;
	}

	public void setProcedureColumnsOutputClassName(String value)
	{
		if (value == null)
		{
			value = "";
		}
		if (!_procColsOutputClassName.equals(value))
		{
			final String oldValue = _procColsOutputClassName;
			_procColsOutputClassName = value;
			_propChgReporter.firePropertyChange(
				IPropertyNames.PROC_COLUMNS_OUTPUT_CLASS_NAME,
				oldValue,
				_procColsOutputClassName);
		}
	}

	public boolean getAutoCommit()
	{
		return _autoCommit;
	}

	public void setAutoCommit(boolean value)
	{
		if (_autoCommit != value)
		{
			_autoCommit = value;
			_propChgReporter.firePropertyChange(
				IPropertyNames.AUTO_COMMIT,
				!_autoCommit,
				_autoCommit);
		}
	}

	public boolean getShowToolBar()
	{
		return _showToolbar;
	}

	public void setShowToolBar(boolean value)
	{
		if (_showToolbar != value)
		{
			_showToolbar = value;
			_propChgReporter.firePropertyChange(
				IPropertyNames.SHOW_TOOL_BAR,
				!_showToolbar,
				_showToolbar);
		}
	}

	public int getContentsNbrRowsToShow()
	{
		return _contentsNbrRowsToShow;
	}

	public void setContentsNbrRowsToShow(int value)
	{
		if (_contentsNbrRowsToShow != value)
		{
			final int oldValue = _contentsNbrRowsToShow;
			_contentsNbrRowsToShow = value;
			_propChgReporter.firePropertyChange(
				IPropertyNames.CONTENTS_NBR_ROWS_TO_SHOW,
				oldValue,
				_contentsNbrRowsToShow);
		}
	}

	public int getSqlNbrRowsToShow()
	{
		return _sqlNbrRowsToShow;
	}

	public void setSqlNbrRowsToShow(int value)
	{
		if (_sqlNbrRowsToShow != value)
		{
			final int oldValue = _sqlNbrRowsToShow;
			_sqlNbrRowsToShow = value;
			_propChgReporter.firePropertyChange(
				IPropertyNames.SQL_NBR_ROWS_TO_SHOW,
				oldValue,
				_sqlNbrRowsToShow);
		}
	}

	public boolean getContentsLimitRows()
	{
		return _contentsLimitRows;
	}

	public void setContentsLimitRows(boolean value)
	{
		if (_contentsLimitRows != value)
		{
			final boolean oldValue = _contentsLimitRows;
			_contentsLimitRows = value;
			_propChgReporter.firePropertyChange(
				IPropertyNames.CONTENTS_LIMIT_ROWS,
				oldValue,
				_contentsLimitRows);
		}
	}

	public boolean getSqlLimitRows()
	{
		return _sqlLimitRows;
	}

	public void setSqlLimitRows(boolean value)
	{
		if (_sqlLimitRows != value)
		{
			final boolean oldValue = _sqlLimitRows;
			_sqlLimitRows = value;
			_propChgReporter.firePropertyChange(
				IPropertyNames.SQL_LIMIT_ROWS,
				oldValue,
				_sqlLimitRows);
		}
	}

	public char getSqlStatementSeparatorChar()
	{
		return _sqlStmtSepChar;
	}

	public void setSqlStatementSeparatorChar(char value)
	{
		if (_sqlStmtSepChar != value)
		{
			final char oldValue = _sqlStmtSepChar;
			_sqlStmtSepChar = value;
			_propChgReporter.firePropertyChange(
				IPropertyNames.SQL_STATEMENT_SEPARATOR,
				oldValue,
				_sqlStmtSepChar);
		}
	}

	public boolean getCommitOnClosingConnection()
	{
		return _commitOnClosingConnection;
	}

	public synchronized void setCommitOnClosingConnection(boolean data)
	{
		final boolean oldValue = _commitOnClosingConnection;
		_commitOnClosingConnection = data;
		_propChgReporter.firePropertyChange(
			IPropertyNames.COMMIT_ON_CLOSING_CONNECTION,
			oldValue,
			_commitOnClosingConnection);
	}

	/**
	 * Return <TT>true</TT> if row count should be displayed for every table in
	 * object tree.
	 */
	public boolean getShowRowCount()
	{
		return _showRowCount;
	}

	/**
	 * Specify whether row count should be displayed for every table in
	 * object tree.
	 *
	 * @param   data	<TT>true</TT> fi row count should be displayed
	 *				  else <TT>false</TT>.
	 */
	public synchronized void setShowRowCount(boolean data)
	{
		final boolean oldValue = _showRowCount;
		_showRowCount = data;
		_propChgReporter.firePropertyChange(
			IPropertyNames.SHOW_ROW_COUNT,
			oldValue,
			_showRowCount);
	}

	public FontInfo getFontInfo()
	{
		return _fi;
	}

	public void setFontInfo(FontInfo data)
	{
		if (_fi == null || !_fi.equals(data))
		{
			final FontInfo oldValue = _fi;
			_fi = data;
			_propChgReporter.firePropertyChange(IPropertyNames.FONT_INFO, oldValue, _fi);
		}
	}
}