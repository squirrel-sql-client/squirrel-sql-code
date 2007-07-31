package net.sourceforge.squirrel_sql.plugins.userscript.kernel;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;

public class ScriptListController
{

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ScriptListController.class);

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
				// i18n[userscript.execOnTargets=Execute script on selected targets]
				title = s_stringMgr.getString("userscript.execOnTargets");
				// i18n[userscript.applicableToTarget=Scripts applicable to selected targets]
				applicableScriptsText = s_stringMgr.getString("userscript.applicableToTarget");
			}
			else
			{
				// i18n[userscript.execOn=Execute script on {0}]
				title = s_stringMgr.getString("userscript.execOn", m_targets.getAll()[0].getTargetInfo());
				// i18n[userscript.applicableTo=Scripts applicable to objects of type {0}]
				applicableScriptsText = s_stringMgr.getString("userscript.applicableTo", m_targets.getAll()[0].getTargetInfo());
			}


			m_dlg = new ScriptListDialog(ownerFrame, title, applicableScriptsText);
         GUIUtils.centerWithinParent(m_dlg);
			m_dlg.setVisible(true);

			ScriptProps props = m_admin.readScriptProps();
			if(null != props)
			{
				ScriptListTableModel tm = (ScriptListTableModel) m_dlg.tblScriptList.getModel();
				tm.setScripts(props.getScripts());

				Vector<String> extraClassPath = new Vector<String>();
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
			// i18n[userscript.selClasspathToDel=Please select the classpath entry to delete]
			JOptionPane.showMessageDialog(m_ownerFrame, s_stringMgr.getString("userscript.selClasspathToDel"));
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

			Vector<String> extraClassPath = new Vector<String>();
			for (int i = 0; i < m_dlg.lstExtraClasspath.getModel().getSize(); i++)
			{
				extraClassPath.add((String)m_dlg.lstExtraClasspath.getModel().getElementAt(i));
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

			// i18n[userscript.selScriptToExec=Please select the script to execute]
			JOptionPane.showMessageDialog(m_ownerFrame, s_stringMgr.getString("userscript.selScriptToExec"));
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
			// i18n[userscript.selScriptToDel=Please select the script to delete]
			JOptionPane.showMessageDialog(m_ownerFrame, s_stringMgr.getString("userscript.selScriptToDel"));
			return;
		}

		ScriptListTableModel tm = (ScriptListTableModel) m_dlg.tblScriptList.getModel();
		Script selScript = tm.getScripts()[m_dlg.tblScriptList.getSelectedRow()];


		// i18n[userscript.confirmRemove=Do you want to remove script {0} from the list?]
		int option = JOptionPane.showConfirmDialog(m_ownerFrame,  s_stringMgr.getString("userscript.confirmRemove", selScript.getName()));

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
			// i18n[userscript.selScriptToEdit=Please select the script to edit]
			JOptionPane.showMessageDialog(m_ownerFrame, s_stringMgr.getString("userscript.selScriptToEdit"));
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
