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
		String COMMIT_ON_CLOSING_CONNECTION = "commitOnClosingConnection";
		String CONTENTS_LIMIT_ROWS = "contentsLimitRows";
		String CONTENTS_NBR_ROWS_TO_SHOW = "contentsNbrOfRowsToShow";
		String FONT_INFO = "fontInfo";
		String META_DATA_OUTPUT_CLASS_NAME = "metaDataOutputClassName";
		String SHOW_ROW_COUNT = "showRowCount";
		String SHOW_TOOL_BAR = "showToolBar";
		String SQL_LIMIT_ROWS = "sqlLimitRows";
		String SQL_NBR_ROWS_TO_SHOW = "sqlNbrOfRowsToShow";
		String SQL_READ_BLOBS = "sqlReadBlobs";
		String SQL_RESULTS_OUTPUT_CLASS_NAME= "sqlResultsOutputClassName";
		String SQL_STATEMENT_SEPARATOR = "sqlStatementSeparator";
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

	/** Font information for the jEdit text area. */
	private FontInfo _fi;

	/** Read blobs from Result sets. */
	private boolean _sqlReadBlobs = false;

	public SessionProperties()
	{
		super();
	}

	public void assignFrom(SessionProperties rhs)
	{
		setAutoCommit(rhs.getAutoCommit());
		setCommitOnClosingConnection(rhs.getCommitOnClosingConnection());
		setContentsLimitRows(rhs.getContentsLimitRows());
		setContentsNbrRowsToShow(rhs.getContentsNbrRowsToShow());
		setFontInfo(rhs.getFontInfo());
		setMetaDataOutputClassName(rhs.getMetaDataOutputClassName());
		setShowRowCount(rhs.getShowRowCount());
		setShowToolBar(rhs.getShowToolBar());
		setSQLLimitRows(rhs.getSQLLimitRows());
		setSQLNbrRowsToShow(rhs.getSQLNbrRowsToShow());
		setSQLReadBlobs(rhs.getSQLReadBlobs());
		setSQLStatementSeparatorChar(rhs.getSQLStatementSeparatorChar());
	}

	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		_propChgReporter.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		_propChgReporter.removePropertyChangeListener(listener);
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
			_propChgReporter.firePropertyChange(
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
			_propChgReporter.firePropertyChange(
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

	public boolean getSQLReadBlobs()
	{
		return _sqlReadBlobs;
	}

	public void setSQLReadBlobs(boolean value)
	{
		if (_sqlReadBlobs != value)
		{
			final boolean oldValue = _sqlReadBlobs;
			_sqlReadBlobs = value;
			_propChgReporter.firePropertyChange(
				IPropertyNames.SQL_READ_BLOBS,
				oldValue,
				_sqlReadBlobs);
		}
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