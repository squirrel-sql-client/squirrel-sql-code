package net.sourceforge.squirrel_sql.client.plugin;
/*
 * Copyright (C) 2001-2003 Colin Bell
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
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
/**
 * Array dataset showing information about plugins.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class PluginInfoArrayDataSet implements IDataSet
{
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(PluginInfoArrayDataSet.class);

	private static final String UNKNOWN = s_stringMgr.getString("PluginInfoArrayDataSet.unknown");
	private PluginInfo[] _src;
	private DataSetDefinition _dsDef;

	private final static String[] s_hdgs = new String[]
	{
		s_stringMgr.getString("PluginInfoArrayDataSet.name"),
		s_stringMgr.getString("PluginInfoArrayDataSet.Loaded"),
		s_stringMgr.getString("PluginInfoArrayDataSet.version"),
		s_stringMgr.getString("PluginInfoArrayDataSet.author"),
		s_stringMgr.getString("PluginInfoArrayDataSet.contributors"),
	};

	private final static int[] s_hdgWidths = new int[] { 30, 10, 10, 25, 25 };

	private PluginInfo _curRow;
	private int _curIndex = -1;

	public PluginInfoArrayDataSet(PluginInfo[] src)
		throws DataSetException, IllegalArgumentException
	{
		super();
		if (src == null)
		{
			throw new IllegalArgumentException("Null PluginInfo[] passed");
		}
		_src = src;
		_dsDef = new DataSetDefinition(createColumnDefinitions());
	}

	public final int getColumnCount()
	{
		return s_hdgs.length;
	}

	public DataSetDefinition getDataSetDefinition()
	{
		return _dsDef;
	}

	public synchronized boolean next(IMessageHandler msgHandler)
	{
		_curRow = null;
		if (_src.length > (_curIndex + 1))
		{
			_curRow = _src[++_curIndex];
			return true;
		}
		return false;
	}

	public synchronized Object get(int columnIndex)
	{
		if (_curRow == null)
		{
			throw new IllegalStateException("PluginInfoArrayDataSet.get() called but all rows read");
		}

		IPlugin plugin = _curRow.getPlugin();

		switch (columnIndex)
		{
			case 0 :
				return plugin != null ? plugin.getDescriptiveName() : UNKNOWN;
			case 1 :
				return _curRow.isLoaded()
					? s_stringMgr.getString("PluginInfoArrayDataSet.true")
					: s_stringMgr.getString("PluginInfoArrayDataSet.false");
			case 2 :
				return plugin != null ? plugin.getVersion() : UNKNOWN;
			case 3 :
				return plugin != null ? plugin.getAuthor() : UNKNOWN;
			case 4 :
				return plugin != null ? plugin.getContributors() : UNKNOWN;
			default :
				throw new IndexOutOfBoundsException("" + columnIndex);
		}
	}

	private ColumnDisplayDefinition[] createColumnDefinitions()
	{
		final int columnCount = getColumnCount();
		ColumnDisplayDefinition[] columnDefs = new ColumnDisplayDefinition[columnCount];
		for (int i = 0; i < columnCount; ++i)
		{
			columnDefs[i] = new ColumnDisplayDefinition(s_hdgWidths[i], s_hdgs[i]);
		}
		return columnDefs;
	}
}
