package net.sourceforge.squirrel_sql.client.session.properties;
/*
 * Copyright (C) 2001-2002 Colin Bell
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

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTextPanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.LargeResultSetObjectInfo;
import net.sourceforge.squirrel_sql.fw.gui.FontInfo;
import net.sourceforge.squirrel_sql.fw.util.PropertyChangeReporter;

public class SessionProperties implements Cloneable, Serializable
{
	public interface IDataSetDestinations
	{
		String TEXT = DataSetViewerTextPanel.class.getName();
		String TABLE = DataSetViewerTablePanel.class.getName();
	}

	public interface IPropertyNames
	{
		String AUTO_COMMIT = "autoCommit";
		String COMMIT_ON_CLOSING_CONNECTION = "commitOnClosingConnection";
		String CONTENTS_LIMIT_ROWS = "contentsLimitRows";
		String CONTENTS_NBR_ROWS_TO_SHOW = "contentsNbrOfRowsToShow";
		String FONT_INFO = "fontInfo";
		String LARGE_RESULT_SET_OBJECT_INFO = "largeResultSetObjectInfo";
		String META_DATA_OUTPUT_CLASS_NAME = "metaDataOutputClassName";
		String SHOW_ROW_COUNT = "showRowCount";
		String SHOW_TOOL_BAR = "showToolBar";
		String SQL_LIMIT_ROWS = "sqlLimitRows";
		String SQL_NBR_ROWS_TO_SHOW = "sqlNbrOfRowsToShow";
		String SQL_RESULTS_OUTPUT_CLASS_NAME = "sqlResultsOutputClassName";
		String SQL_START_OF_LINE_COMMENT = "sqlStartOfLineComment";
		String SQL_STATEMENT_SEPARATOR = "sqlStatementSeparator";
	}

	/** Object to handle property change events. */
	private transient PropertyChangeReporter _propChgReporter;

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

	private String _metaDataOutputClassName = IDataSetDestinations.TABLE;
	private String _sqlResultsOutputClassName = IDataSetDestinations.TABLE;

	/**
	 * <TT>true</TT> if row count should be displayed for every table in object tree.
	 */
	private boolean _showRowCount = false;

	/** <TT>true</TT> if toolbar should be shown. */
	private boolean _showToolbar = true;

	private String _sqlOutputMetaDataClassName = IDataSetDestinations.TABLE;

	private char _sqlStmtSepChar = ';';

	/** Used to indicate a &quot;Start Of Line&quot; comment in SQL. */
	private String _solComment = "--";

	/** Font information for the jEdit text area. */
	private FontInfo _fi;

	private LargeResultSetObjectInfo _largeObjectInfo = new LargeResultSetObjectInfo();

	public SessionProperties()
	{
		super();
	}

	/**
	 * Return a copy of this object.
	 */
	public Object clone()
	{
		try
		{
			SessionProperties props = (SessionProperties) super.clone();
			props._propChgReporter = null;
			if (_fi != null)
			{
				props.setFontInfo((FontInfo) _fi.clone());
			}
			if (_largeObjectInfo != null)
			{
				props.setLargeResultSetObjectInfo(
					(LargeResultSetObjectInfo) _largeObjectInfo.clone());
			}

			return props;
		}
		catch (CloneNotSupportedException ex)
		{
			throw new InternalError(ex.getMessage()); // Impossible.
		}
	}

	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		getPropertyChangeReporter().addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		getPropertyChangeReporter().removePropertyChangeListener(listener);
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
			getPropertyChangeReporter().firePropertyChange(
				IPropertyNames.META_DATA_OUTPUT_CLASS_NAME,
				oldValue,
				_metaDataOutputClassName);
		}
	}

	public String getSQLResultsOutputClassName()
	{
		return _sqlResultsOutputClassName;
	}

	public void setSQLResultsOutputClassName(String value)
	{
		if (value == null)
		{
			value = "";
		}
		if (!_sqlResultsOutputClassName.equals(value))
		{
			final String oldValue = _sqlResultsOutputClassName;
			_sqlResultsOutputClassName = value;
			getPropertyChangeReporter().firePropertyChange(
				IPropertyNames.SQL_RESULTS_OUTPUT_CLASS_NAME,
				oldValue,
				_sqlResultsOutputClassName);
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
			getPropertyChangeReporter().firePropertyChange(
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
			getPropertyChangeReporter().firePropertyChange(
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
			getPropertyChangeReporter().firePropertyChange(
				IPropertyNames.CONTENTS_NBR_ROWS_TO_SHOW,
				oldValue,
				_contentsNbrRowsToShow);
		}
	}

	public int getSQLNbrRowsToShow()
	{
		return _sqlNbrRowsToShow;
	}

	public void setSQLNbrRowsToShow(int value)
	{
		if (_sqlNbrRowsToShow != value)
		{
			final int oldValue = _sqlNbrRowsToShow;
			_sqlNbrRowsToShow = value;
			getPropertyChangeReporter().firePropertyChange(
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
			getPropertyChangeReporter().firePropertyChange(
				IPropertyNames.CONTENTS_LIMIT_ROWS,
				oldValue,
				_contentsLimitRows);
		}
	}

	public boolean getSQLLimitRows()
	{
		return _sqlLimitRows;
	}

	public void setSQLLimitRows(boolean value)
	{
		if (_sqlLimitRows != value)
		{
			final boolean oldValue = _sqlLimitRows;
			_sqlLimitRows = value;
			getPropertyChangeReporter().firePropertyChange(
				IPropertyNames.SQL_LIMIT_ROWS,
				oldValue,
				_sqlLimitRows);
		}
	}

	public char getSQLStatementSeparatorChar()
	{
		return _sqlStmtSepChar;
	}

	public void setSQLStatementSeparatorChar(char value)
	{
		if (_sqlStmtSepChar != value)
		{
			final char oldValue = _sqlStmtSepChar;
			_sqlStmtSepChar = value;
			getPropertyChangeReporter().firePropertyChange(
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
		getPropertyChangeReporter().firePropertyChange(
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
		getPropertyChangeReporter().firePropertyChange(
			IPropertyNames.SHOW_ROW_COUNT,
			oldValue,
			_showRowCount);
	}

	/**
	 * Return the string used to represent a Start of Line Comment in SQL.
	 */
	public String getStartOfLineComment()
	{
		return _solComment;
	}

	/**
	 * Set the string used to represent a Start of Line Comment in SQL.
	 */
	public synchronized void setStartOfLineComment(String data)
	{
		final String oldValue = _solComment;
		_solComment = data;
		getPropertyChangeReporter().firePropertyChange(
			IPropertyNames.SQL_START_OF_LINE_COMMENT,
			oldValue,
			_solComment);
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
			getPropertyChangeReporter().firePropertyChange(
				IPropertyNames.FONT_INFO,
				oldValue,
				_fi);
		}
	}

	public LargeResultSetObjectInfo getLargeResultSetObjectInfo()
	{
		return _largeObjectInfo;
	}

	public void setLargeResultSetObjectInfo(LargeResultSetObjectInfo data)
	{
		final LargeResultSetObjectInfo oldValue = _largeObjectInfo;
		_largeObjectInfo = data;
		getPropertyChangeReporter().firePropertyChange(
			IPropertyNames.LARGE_RESULT_SET_OBJECT_INFO,
			oldValue,
			_largeObjectInfo);
	}

	private synchronized PropertyChangeReporter getPropertyChangeReporter()
	{
		if (_propChgReporter == null)
		{
			_propChgReporter = new PropertyChangeReporter(this);
		}
		return _propChgReporter;
	}
}