package net.sourceforge.squirrel_sql.plugins.userscript.kernel;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.Arrays;
import java.util.Vector;
import java.util.List;

public class ScriptListTableModel extends AbstractTableModel
{
	private static final String[] colNames = {"Name", "Script class", "Show in standard menues"};
	private Script[] m_scripts = new Script[0];

	void setScripts(Script[] scripts)
	{
		m_scripts = scripts;
	}


	public int getRowCount()
	{
		return m_scripts.length;
	}

	public int getColumnCount()
	{
		return colNames.length;
	}

	public Object getValueAt(int rowIndex, int columnIndex)
	{
		switch(columnIndex)
		{
			case 0:
				return m_scripts[rowIndex].getName();
			case 1:
				return m_scripts[rowIndex].getScriptClass();
			case 2:
				return new Boolean(m_scripts[rowIndex].isShowInStandard());
			default:
				throw new IllegalArgumentException("Invalid column index " + columnIndex);
		}
	}

	public String getColumnName(int column)
	{
		return colNames[column];
	}

   TableColumn[] getTableColumns()
	{
		TableColumn[] ret = new TableColumn[3];

		ret[0] = new TableColumn(0);
		ret[1] = new TableColumn(1);

		ret[2] = new TableColumn(2);
		final JCheckBox chkShowInStandard = new JCheckBox();
		chkShowInStandard.setHorizontalAlignment(SwingConstants.CENTER);

		final Color unselectedBg = (new JTextField()).getBackground();
		final Color selectedBg = (new JTextField()).getSelectionColor();

		TableCellRenderer chkRenderer =
			new TableCellRenderer()
			{
				public Component getTableCellRendererComponent(JTable table, Object value,
																			  boolean isSelected, boolean hasFocus,
																			  int row, int column)
				{
					chkShowInStandard.setSelected( ((Boolean)value).booleanValue() );
					if(isSelected)
					{
						chkShowInStandard.setBackground(selectedBg);
					}
					else
					{
						chkShowInStandard.setBackground(unselectedBg);
					}

					return chkShowInStandard;
				}
			};
		ret[2].setCellRenderer(chkRenderer);

		return ret;

	}

	void addScript(Script newScript)
	{
		Vector buf = new Vector();
		buf.addAll(Arrays.asList(m_scripts));
		buf.add(newScript);
		m_scripts = (Script[]) buf.toArray(new Script[buf.size()]);
		refresh();
	}

	public Script[] getScripts()
	{
		return m_scripts;
	}

	public void refresh()
	{
		fireTableDataChanged();
	}

	public void remove(int ix)
	{
		Vector buf = new Vector();
		buf.addAll(Arrays.asList(m_scripts));
		buf.remove(ix);
		m_scripts = (Script[]) buf.toArray(new Script[buf.size()]);
		refresh();
	}
}
