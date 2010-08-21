package net.sourceforge.squirrel_sql.plugins.codecompletion.prefs;

import javax.swing.table.DefaultTableModel;
import java.util.Arrays;
import java.util.ArrayList;

public class PrefixesTableModel extends DefaultTableModel
{
    private static final long serialVersionUID = 1L;

    private ArrayList<PrefixedConfig> _data = new ArrayList<PrefixedConfig>();

	public PrefixesTableModel(PrefixedConfig[] prefixedConfigs)
	{
		_data.addAll(Arrays.asList(prefixedConfigs));

	}
	public Object getValueAt(int row, int column)
	{
		PrefixedConfig buf = _data.get(row);

		if(0 == column)
		{
			return buf.getPrefix();
		}
		else
		{
			return ConfigCboItem.getItemForConfig(buf.getCompletionConfig());
		}
	}

	public void setValueAt(Object aValue, int row, int column)
	{
		PrefixedConfig buf = _data.get(row);

		if(0 == column)
		{
			buf.setPrefix(null == aValue ? "" : aValue.toString());
		}
		else
		{
			buf.setCompletionConfig(((ConfigCboItem)aValue).getCompletionConfig());
		}

		fireTableCellUpdated(row, column);
	}


	public void addNewConfig()
	{
		_data.add(new PrefixedConfig());
		fireTableRowsInserted(_data.size() - 1, _data.size() - 1);
	}

	public int getRowCount()
	{
		if(null == _data)
		{
			return 0;
		}
		else
		{
			return _data.size();
		}
	}


	public void removeRows(int[] selRows)
	{
		ArrayList<PrefixedConfig> toRemove = 
            new ArrayList<PrefixedConfig>(selRows.length);

		for (int i = 0; i < selRows.length; i++)
		{
			toRemove.add(_data.get(selRows[i]));
		}
		_data.removeAll(toRemove);

		for (int i = 0; i < selRows.length; i++)
		{
			fireTableRowsDeleted(selRows[i], selRows[i]);
		}
	}

	public PrefixedConfig[] getData()
	{
		return _data.toArray(new PrefixedConfig[_data.size()]);
	}
}
