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

import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import net.sourceforge.squirrel_sql.fw.gui.SortableTable;
import net.sourceforge.squirrel_sql.fw.gui.SortableTableModel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;


public class PluginSummaryTable extends SortableTable
{
    private static final long serialVersionUID = 1L;

    /** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(PluginSummaryTable.class); 
    
	private final static String[] s_hdgs = new String[]
	{
		s_stringMgr.getString("PluginSummaryTable.loadAtStartup"),
        s_stringMgr.getString("PluginSummaryTable.internalName"),
		s_stringMgr.getString("PluginSummaryTable.name"),
		s_stringMgr.getString("PluginSummaryTable.loaded"),
		s_stringMgr.getString("PluginSummaryTable.version"),
		s_stringMgr.getString("PluginSummaryTable.author"),
		s_stringMgr.getString("PluginSummaryTable.contributors"),
	};

	private final static Class<?>[] s_dataTypes = new Class[]
	{
		Boolean.class,
        String.class,
		String.class,
		String.class,
		String.class,
		String.class,
		String.class,
	};

	private final static int[] s_columnWidths = new int[]
	{
		100, 100, 150, 50, 50, 100, 100,
	};

	public PluginSummaryTable(PluginInfo[] pluginInfo, PluginStatus[] pluginStatus)
	{
		super(new MyTableModel(pluginInfo, pluginStatus));

		setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		getTableHeader().setResizingAllowed(true);
		getTableHeader().setReorderingAllowed(true);
		setAutoCreateColumnsFromModel(false);
		setAutoResizeMode(AUTO_RESIZE_LAST_COLUMN);

		final TableColumnModel tcm = new DefaultTableColumnModel();
		for (int i = 0; i < s_columnWidths.length; ++i)
		{
			final TableColumn col = new TableColumn(i, s_columnWidths[i]);
			col.setHeaderValue(s_hdgs[i]);
			tcm.addColumn(col);
		}
		setColumnModel(tcm);
	}

	PluginStatus[] getPluginStatus()
	{
        SortableTableModel stm = getSortableTableModel();
        SortableTableModel stm2 = (SortableTableModel)stm.getActualModel();
        MyTableModel tm = (MyTableModel)(stm2.getActualModel());
		return tm.getPluginStatus();
	}

	private static class MyTableModel extends AbstractTableModel
	{
        private static final long serialVersionUID = 1L;
        private ArrayList<PluginData> _pluginData = new ArrayList<PluginData>();

		MyTableModel(PluginInfo[] pluginInfo, PluginStatus[] pluginStatus)
		{
			if (pluginInfo == null)
			{
				pluginInfo = new PluginInfo[0];
			}
			if (pluginStatus == null)
			{
				pluginStatus = new PluginStatus[0];
			}

			Map<String, PluginStatus> statuses = 
                new HashMap<String, PluginStatus>();
			for (int i = 0; i < pluginStatus.length; ++i)
			{
				statuses.put(pluginStatus[i].getInternalName(), pluginStatus[i]);
			}

			for (int i = 0; i < pluginInfo.length; ++i)
			{
				final PluginInfo pi = pluginInfo[i];
				final PluginStatus ps = statuses.get(pi.getInternalName());
				final PluginData pd = new PluginData(pi, ps);
				_pluginData.add(pd);
			}
		}

		synchronized PluginStatus[] getPluginStatus()
		{
			final PluginStatus[] ar = new PluginStatus[_pluginData.size()];
			for (int i = 0; i < ar.length; ++i)
			{
				ar[i] = (_pluginData.get(i))._status;
			}
			return ar;
		}

		public Object getValueAt(int row, int col)
		{
			final PluginData pd = _pluginData.get(row);
			switch (col)
			{
				case 0:
					return Boolean.valueOf(pd._status.isLoadAtStartup());
                case 1:
                    return pd._info.getInternalName();                    
				case 2:
					return pd._info.getDescriptiveName();
				case 3:
					return pd._info.isLoaded()
						? s_stringMgr.getString("PluginSummaryTable.true")
						: s_stringMgr.getString("PluginSummaryTable.false");
				case 4:
					return pd._info.getVersion();
				case 5:
					return pd._info.getAuthor();
				case 6:
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

		public Class<?> getColumnClass(int col)
		{
			return s_dataTypes[col];
		}

		public boolean isCellEditable(int row, int col)
		{
			return col == 0;
		}

        public void setValueAt(Object value, int row, int col)
		{
        	if (col == 0)
        	{
        		final PluginData pd = _pluginData.get(row);
                boolean loadAtStartup = 
                    Boolean.valueOf(value.toString()).booleanValue();
        		pd._status.setLoadAtStartup(loadAtStartup);
        		fireTableCellUpdated(row, col);
        	}
            if (col == 3) {
                final PluginData pd = _pluginData.get(row);
                pd._info.setLoaded(Boolean.valueOf(value.toString()));
                fireTableCellUpdated(row, col);
            }
		}

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
			
			public String getInternalName() {
			    return _internalName;
			}
		}
	}
}
