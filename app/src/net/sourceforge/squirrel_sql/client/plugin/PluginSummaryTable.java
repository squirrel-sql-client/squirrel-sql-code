package net.sourceforge.squirrel_sql.client.plugin;
/*
 * Copyright (C) 2001-2004 Colin Bell
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import net.sourceforge.squirrel_sql.fw.gui.SortableTableModel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

// TODO: Rename to PluginSummaryTable

//TO: Enable the "load at startup" maintenance.
// This is disabled at the moment because if a plugin is not loaded at startup
// it will not appear in this panel (as it isn't loaded by the plugin manager)
// and so cannot be set to load. Once unloaded it can never be loaded.
public class PluginSummaryTable extends JTable
{
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(PluginSummaryTable.class);

	private final static String[] s_hdgs = new String[]
	{
//		s_stringMgr.getString("PluginSummaryTable.loadAtStartup"),
		s_stringMgr.getString("PluginSummaryTable.name"),
		s_stringMgr.getString("PluginSummaryTable.loaded"),
		s_stringMgr.getString("PluginSummaryTable.version"),
		s_stringMgr.getString("PluginSummaryTable.author"),
		s_stringMgr.getString("PluginSummaryTable.contributors"),
	};

	public PluginSummaryTable(PluginInfo[] pluginInfo, PluginStatus[] pluginStatus)
	{
		super(new SortableTableModel(new MyTableModel(pluginInfo, pluginStatus)));
	}

	PluginStatus[] getPluginStatus()
	{
		final SortableTableModel wrapper = (SortableTableModel)getModel();
		return ((MyTableModel)wrapper.getActualModel()).getPluginStatus();
	}

	private static class MyTableModel extends AbstractTableModel
	{
		private ArrayList _pluginData = new ArrayList();

		MyTableModel(PluginInfo[] pluginInfo, PluginStatus[] pluginStatus)
		{
			super();
			if (pluginInfo == null)
			{
				pluginInfo = new PluginInfo[0];
			}
			if (pluginStatus == null)
			{
				pluginStatus = new PluginStatus[0];
			}

			Map statuses = new HashMap();
			for (int i = 0; i < pluginStatus.length; ++i)
			{
				statuses.put(pluginStatus[i].getInternalName(), pluginStatus[i]);
			}

			for (int i = 0; i < pluginInfo.length; ++i)
			{
				final PluginInfo pi = pluginInfo[i];
				final PluginStatus ps = (PluginStatus)statuses.get(pi.getInternalName());
				final PluginData pd = new PluginData(pi, ps);
				_pluginData.add(pd);
			}
		}

		synchronized PluginStatus[] getPluginStatus()
		{
			final PluginStatus[] ar = new PluginStatus[_pluginData.size()];
			for (int i = 0; i < ar.length; ++i)
			{
				ar[i] = ((PluginData)_pluginData.get(i))._status;
			}
			return ar;
		}

		public Object getValueAt(int row, int col)
		{
			final PluginData pd = (PluginData)_pluginData.get(row);
			switch (col)
			{
//				case 0:
//					return new Boolean(pd._status.isLoadAtStartup());
				case 0:
					return pd._info.getDescriptiveName();
				case 1:
					return pd._info.isLoaded()
						? s_stringMgr.getString("PluginSummaryTable.true")
						: s_stringMgr.getString("PluginSummaryTable.false");
				case 2:
					return pd._info.getVersion();
				case 3:
					return pd._info.getAuthor();
				case 4:
					return pd._info.getContributors();
				default :
					throw new IndexOutOfBoundsException("" + col);
			}
		}

		public int getRowCount()
		{
			return _pluginData.size();
		}

		public int getColumnCount()
		{
			return s_hdgs.length;
		}

		public String getColumnName(int col)
		{
			return s_hdgs[col];
		}

		public Class getColumnClass(int col)
		{
//			if (col == 0)
//			{
//				return Boolean.class;
//			}
			return String.class;
		}

		public boolean isCellEditable(int row, int col)
		{
			return false;
			//return col == 0;
		}

//        public void setValueAt(Object value, int row, int col)
//		{
//        	if (col == 0)
//        	{
//        		final PluginData pd = (PluginData)_pluginData.get(row);
//        		pd._status.setLoadAtStartup(Boolean.valueOf(value.toString()).booleanValue());
//        		fireTableCellUpdated(row, col);
//        	}
//		}

		private static class PluginData
		{
			final private String _internalName;
			final private PluginInfo _info;
			final private PluginStatus _status;

			PluginData(PluginInfo info, PluginStatus status)
			{
				super();
				_info = info;
				_status = (status != null) ? status : new PluginStatus(_info.getInternalName());
				_internalName = _info.getInternalName();
			}
		}
	}
}
