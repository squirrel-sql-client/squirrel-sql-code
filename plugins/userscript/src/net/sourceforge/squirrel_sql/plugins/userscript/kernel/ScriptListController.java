package net.sourceforge.squirrel_sql.plugins.userscript.kernel;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;

public class ScriptListController
{

	private ScriptListDialog m_dlg;
	private JFrame m_ownerFrame;
	private UserScriptAdmin m_admin;
	private ScriptTargetCollection m_targets;

	public ScriptListController(JFrame ownerFrame, UserScriptAdmin admin, boolean targetType)
	{
		try
		{
			m_admin = admin;
			m_ownerFrame = ownerFrame;

			String title;
			String applicableScriptsText;

			m_targets = m_admin.getTargets(targetType);

			if(1 < m_targets.size())
			{
				title = "Execute script on selected targets";
				applicableScriptsText = "Scripts applicable to selected targets";
			}
			else
			{
				title = "Execute script on " + m_targets.getAll()[0].getTargetInfo();
				applicableScriptsText = "Scripts applicable to objects of type " + m_targets.getAll()[0].getTargetInfo();
			}


			m_dlg = new ScriptListDialog(ownerFrame, title, applicableScriptsText);
         GUIUtils.centerWithinParent(m_dlg);
			m_dlg.setVisible(true);

			ScriptProps props = m_admin.readScriptProps();
			if(null != props)
			{
				ScriptListTableModel tm = (ScriptListTableModel) m_dlg.tblScriptList.getModel();
				tm.setScripts(props.getScripts());

				Vector extraClassPath = new Vector();
				for (int i = 0; i < props.getExtraClassPath().length; i++)
				{
					extraClassPath.add(props.getExtraClassPath()[i].getEntry());
				}
				m_dlg.lstExtraClasspath.setListData(extraClassPath);
			}

			m_dlg.btnExecute.addActionListener
				(
					new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{onExecute();}
					}
				);

			m_dlg.btnAdd.addActionListener
				(
					new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{onAdd();}
					}
				);

			m_dlg.btnEdit.addActionListener
				(
					new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{onEdit();}
					}
				);

			m_dlg.btnRemove.addActionListener
				(
					new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{onRemove();}
					}
				);

			m_dlg.btnCpAdd.addActionListener
				(
					new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{onCpAdd();}
					}
				);

			m_dlg.btnCpRemove.addActionListener
				(
					new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{onCpRemove();}
					}
				);

			m_dlg.btnGenerateTemplate.addActionListener
				(new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{onGenerateTemplate();}
					}
				);


		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	private void onGenerateTemplate()
	{
		new GenerateTemplateController(m_ownerFrame);
	}


	private void onCpRemove()
	{
		int[] selIndices = m_dlg.lstExtraClasspath.getSelectedIndices();

		if(0 == selIndices.length)
		{
			JOptionPane.showMessageDialog(m_ownerFrame, "Please select the classpath entry to delete");
			return;
		}

		Vector remainEntries = new Vector();

		for(int i=0; i < m_dlg.lstExtraClasspath.getModel().getSize(); ++i)
		{
			boolean found = false;
			for (int j = 0; j < selIndices.length; j++)
			{
				if(i == selIndices[j])
				{
					found = true;
					break;
				}
			}

			if(false == found)
			{
				remainEntries.add(m_dlg.lstExtraClasspath.getModel().getElementAt(i));
			}
		}

		m_dlg.lstExtraClasspath.setListData(remainEntries);
		saveScriptProps();
		m_admin.refreshExtraClassPath();
	}

	private void onCpAdd()
	{
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fc.setMultiSelectionEnabled(true);
		if(JFileChooser.APPROVE_OPTION == fc.showOpenDialog(m_ownerFrame))
		{

			Vector extraClassPath = new Vector();
			for (int i = 0; i < m_dlg.lstExtraClasspath.getModel().getSize(); i++)
			{
				extraClassPath.add(m_dlg.lstExtraClasspath.getModel().getElementAt(i));
			}

         File[] files = fc.getSelectedFiles();

			for (int i = 0; i < files.length; i++)
			{
				extraClassPath.add(files[i].getPath());
			}

			m_dlg.lstExtraClasspath.setListData(extraClassPath);
		}
		saveScriptProps();
		m_admin.refreshExtraClassPath();
	}

	private void onExecute()
	{

		if(-1 == m_dlg.tblScriptList.getSelectedRow())
		{
			JOptionPane.showMessageDialog(m_ownerFrame, "Please select the script to execute");
			return;
		}

		ScriptListTableModel tm = (ScriptListTableModel) m_dlg.tblScriptList.getModel();
		Script selScript = tm.getScripts()[m_dlg.tblScriptList.getSelectedRow()];
		m_admin.executeScript(m_ownerFrame, selScript ,m_targets);
	}

	private void onRemove()
	{
		if(-1 == m_dlg.tblScriptList.getSelectedRow())
		{
			JOptionPane.showMessageDialog(m_ownerFrame, "Please select the script to delete");
			return;
		}

		ScriptListTableModel tm = (ScriptListTableModel) m_dlg.tblScriptList.getModel();
		Script selScript = tm.getScripts()[m_dlg.tblScriptList.getSelectedRow()];


		int option = JOptionPane.showConfirmDialog(m_ownerFrame, "Do you want to remove script " + selScript.getName() + " from the list?");

		if(JOptionPane.YES_OPTION == option)
		{
			tm.remove(m_dlg.tblScriptList.getSelectedRow());
			saveScriptProps();
		}
	}

	private void onEdit()
	{
      if(-1 == m_dlg.tblScriptList.getSelectedRow())
		{
			JOptionPane.showMessageDialog(m_ownerFrame, "Please select the script to edit");
			return;
		}

		ScriptListTableModel tm = (ScriptListTableModel) m_dlg.tblScriptList.getModel();
		Script selScript = tm.getScripts()[m_dlg.tblScriptList.getSelectedRow()];

		ScriptPropertiesController propsCtrl = new ScriptPropertiesController(m_ownerFrame, selScript, m_admin.getPlugin());
		propsCtrl.setScriptPropsListener
		(
			new ScriptPropsAdapter()
			{
				public void scriptEdited(Script editedSript)
				{onScriptEdited();}
			}
		);

	}

	private void onScriptEdited()
	{
		ScriptListTableModel tm =  (ScriptListTableModel) m_dlg.tblScriptList.getModel();
		tm.refresh();
		saveScriptProps();
	}

	private void saveScriptProps()
	{
		try
		{
			ScriptListTableModel tm =  (ScriptListTableModel) m_dlg.tblScriptList.getModel();
			ClassPathEntry[] extraClassPath = new ClassPathEntry[m_dlg.lstExtraClasspath.getModel().getSize()];

			for (int i = 0; i < extraClassPath.length; i++)
			{
				extraClassPath[i] = new ClassPathEntry( (String) m_dlg.lstExtraClasspath.getModel().getElementAt(i) );
			}

			m_admin.writeScriptProps(new ScriptProps(tm.getScripts(), extraClassPath));
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	private void onAdd()
	{
		ScriptPropertiesController propsCtrl = new ScriptPropertiesController(m_ownerFrame, m_admin.getPlugin());
		propsCtrl.setScriptPropsListener
		(
			new ScriptPropsAdapter()
			{
				public void newScript(Script newScript)
				{onNewScript(newScript);}
			}
		);
	}

	private void onNewScript(Script newScript)
	{
		ScriptListTableModel tm =  (ScriptListTableModel) m_dlg.tblScriptList.getModel();
		tm.addScript(newScript);
		saveScriptProps();
	}

	public File getScriptPropertiesFile()
	{
		try
		{
			return new File(m_admin.getPlugin().getPluginUserSettingsFolder().getPath() + File.separator + UserScriptAdmin.SCRIPT_PROPERTIES_FILE);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}


}
