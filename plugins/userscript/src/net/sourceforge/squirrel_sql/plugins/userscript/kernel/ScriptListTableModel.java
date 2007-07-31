package net.sourceforge.squirrel_sql.plugins.userscript.kernel;

import java.awt.Color;
import java.awt.Component;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class ScriptListTableModel extends AbstractTableModel
{
    private static final long serialVersionUID = 1L;

    private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ScriptListTableModel.class);

	private static final String[] colNames =
		{
			// i18n[userscript.tableColName=Name]
			s_stringMgr.getString("userscript.tableColName"),
			// i18n[userscript.scriptClass=Script class]
			s_stringMgr.getString("userscript.scriptClass"),
			// i18n[userscript.showInStandardMenues=Show in standard menues]
			s_stringMgr.getString("userscript.showInStandardMenues")
		};
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
		Vector<Script> buf = new Vector<Script>();
		buf.addAll(Arrays.asList(m_scripts));
		buf.add(newScript);
		m_scripts = buf.toArray(new Script[buf.size()]);
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
		Vector<Script> buf = new Vector<Script>();
		buf.addAll(Arrays.asList(m_scripts));
		buf.remove(ix);
		m_scripts = buf.toArray(new Script[buf.size()]);
		refresh();
	}
}
