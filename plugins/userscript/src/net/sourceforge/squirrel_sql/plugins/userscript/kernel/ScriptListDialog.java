package net.sourceforge.squirrel_sql.plugins.userscript.kernel;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import java.awt.*;

public class ScriptListDialog extends JDialog
{
	JTable tblScriptList;
	JButton btnExecute;
	JButton btnAdd;
	JButton btnEdit;
	JButton btnRemove;
	JButton btnGenerateTemplate;

	JList lstExtraClasspath;
	JButton btnCpAdd;
	JButton btnCpRemove;


	ScriptListDialog(Frame owner, String title, String applicableScriptsText)
	{
      super(owner, title, false);

		JTabbedPane tab = new JTabbedPane();
		tab.addTab("Scripts", createScriptPanel(applicableScriptsText));
		tab.setSelectedIndex(0);
		tab.addTab("Extra class path", createClasspathPanel());

		getContentPane().add(tab);
		setSize(500, 400);
	}

	private JPanel createClasspathPanel()
	{
		JPanel ret = new JPanel();
		ret.setLayout(new BorderLayout());

		lstExtraClasspath = new JList();
		ret.add(new JScrollPane(lstExtraClasspath), BorderLayout.CENTER);

		JPanel pnlSouth = new JPanel();
		pnlSouth.setLayout(new GridLayout(1,2));

		btnCpAdd = new JButton("Add...");
		btnCpRemove = new JButton("Remove");

		pnlSouth.add(btnCpAdd);
		pnlSouth.add(btnCpRemove);

		ret.add(pnlSouth, BorderLayout.SOUTH);

		return ret;
	}

	private JPanel createScriptPanel(String applicableScriptsText)
	{
		JPanel scriptPnl = new JPanel();
		scriptPnl.setLayout(new BorderLayout());

		ScriptListTableModel tm = new ScriptListTableModel();
		tblScriptList = new JTable();
		tblScriptList.setModel(tm);

		/////////////////////////////////////////////////////////
		// Setting the model initializes columns. We want to
		// initialize the columns ourselves below.
		tblScriptList.setColumnModel(new DefaultTableColumnModel());
		//
		///////////////////////////////////////////////////////


		TableColumn[] tcs = tm.getTableColumns();
		for (int i = 0; i < tcs.length; i++)
		{
			tblScriptList.addColumn(tcs[i]);
		}

		tblScriptList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		scriptPnl.add(new JScrollPane(tblScriptList), BorderLayout.CENTER);

		JPanel southPanel = new JPanel();
		southPanel.setLayout(new GridLayout(2, 1));

		JPanel southPanelLower = new JPanel();
		southPanelLower.setLayout(new GridLayout(1, 3));


      btnExecute = new JButton("Execute");
		btnAdd = new JButton("Add...");
		btnEdit = new JButton("Edit...");
		btnRemove = new JButton("Remove");
		btnGenerateTemplate = new JButton("Generate script template...");
		southPanelLower.add(btnExecute);
		southPanelLower.add(btnAdd);
		southPanelLower.add(btnEdit);
		southPanelLower.add(btnRemove);
		southPanel.add(southPanelLower);
		southPanel.add(btnGenerateTemplate);

		scriptPnl.add(southPanel, BorderLayout.SOUTH);

		return scriptPnl;
	}
}
