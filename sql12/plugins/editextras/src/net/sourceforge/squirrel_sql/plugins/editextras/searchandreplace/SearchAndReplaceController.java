package net.sourceforge.squirrel_sql.plugins.editextras.searchandreplace;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import net.sourceforge.squirrel_sql.plugins.editextras.EditExtrasPlugin;

import net.sourceforge.squirrel_sql.client.session.ISession;

public class SearchAndReplaceController
{
	private static final String PREFS_KEY_PREFIX_FIND = "Squirrel.jedit.searchandreplacedialog.find.entry";
	private static final String PREFS_KEY_PREFIX_REPLACE = "Squirrel.jedit.searchandreplacedialog.replace.entry";

	private SearchAndReplaceDlg _dlg;
	private ISession _session;
	private EditExtrasPlugin _plugin;
	private FindConfig _lastFingCfg;

	SearchAndReplaceController(ISession session, EditExtrasPlugin plugin, int modus)
	{
		_session = session;
		_plugin = plugin;

		_dlg = new SearchAndReplaceDlg(session.getApplication().getMainFrame(), "Find in " + _session.getSessionSheet().getTitle(), modus);
		_dlg.btnSearch.addActionListener
			(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{onSearch();}
				}
			);

		_dlg.btnClose.addActionListener
			(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{onClose();}
				}
			);

		_dlg.btnReplace.addActionListener
			(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{onReplace();}
				}
			);

		_dlg.btnReplaceAll.addActionListener
			(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{onReplaceAll();}
				}
			);

		_dlg.addWindowListener
		(
			new WindowAdapter()
			{
				public void windowClosing(WindowEvent e)
				{
					onClose();
				}
			}
		);


		AbstractAction closeAction = new AbstractAction()
		{
			public void actionPerformed(ActionEvent actionEvent)
			{
				onClose();
			}
		};
		KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		_dlg.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(escapeStroke, "CloseAction");
		_dlg.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, "CloseAction");
		_dlg.getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(escapeStroke, "CloseAction");
		_dlg.getRootPane().getActionMap().put("CloseAction", closeAction);

		AbstractAction findAction = new AbstractAction()
		{
			public void actionPerformed(ActionEvent actionEvent)
			{
				onSearch();
			}
		};
		KeyStroke findStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		_dlg.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(findStroke, "FindAction");
		_dlg.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(findStroke, "FindAction");
		_dlg.getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(findStroke, "FindAction");
		_dlg.getRootPane().getActionMap().put("FindAction", findAction);

		( (JComponent)_dlg.cboToFind.getEditor().getEditorComponent() ).getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(findStroke, "FindAction");
		( (JComponent)_dlg.cboToFind.getEditor().getEditorComponent() ).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(findStroke, "FindAction");
		( (JComponent)_dlg.cboToFind.getEditor().getEditorComponent() ).getInputMap(JComponent.WHEN_FOCUSED).put(findStroke, "FindAction");
		( (JComponent)_dlg.cboToFind.getEditor().getEditorComponent() ).getActionMap().put("FindAction", findAction);
	}

	private void onReplaceAll()
	{
		String toSearch = _dlg.cboToFind.getEditor().getItem().toString();
		String toReplaceBy = _dlg.cboToReplace.getEditor().getItem().toString();

		if(null == toSearch || "".equals(toSearch))
		{
			return;
		}

		SearchAndReplaceKernel kernel = _plugin.getSearchAndReplaceKernel(_session);

		onSearch();
		for(;;)
		{
			if(null != kernel.getSelectedText() && !"".equals(kernel.getSelectedText()))
			{
				kernel.replaceSelectionBy(toReplaceBy);
				if(0 == kernel.getNextFindStart())
				{
					break;
				}
			}
			else
			{
				break;
			}
			onSearch();
		}

		final String toReplaceByBuf = toReplaceBy;
		SwingUtilities.invokeLater
		(
				new Runnable()
				{
					public void run()
					{manageCboHistory(_dlg.cboToReplace, toReplaceByBuf);}
				}
		);

	}

	private void onReplace()
	{
		String toSearch = _dlg.cboToFind.getEditor().getItem().toString();
		String toReplaceBy = _dlg.cboToReplace.getEditor().getItem().toString();

		if(null == toSearch || "".equals(toSearch))
		{
			return;
		}

		SearchAndReplaceKernel kernel = _plugin.getSearchAndReplaceKernel(_session);

		if(!toSearch.equalsIgnoreCase(kernel.getSelectedText()))
		{
			onSearch();
		}
		else
		{
			kernel.replaceSelectionBy(toReplaceBy);
			SwingUtilities.invokeLater
			(
					new Runnable()
					{
						public void run()
						{onSearch();}
					}
			);
		}

		final String toReplaceByBuf = toReplaceBy;
		SwingUtilities.invokeLater
		(
				new Runnable()
				{
					public void run()
					{manageCboHistory(_dlg.cboToReplace, toReplaceByBuf);}
				}
		);
	}

	void show()
	{
		_plugin.getSearchAndReplaceKernel(_session).beginFromStart();

		_dlg.lblMessage.setText("");
		_dlg.chkMatchCase.setSelected(false);
		_dlg.chkMatchCase.setSelected(false);

		initCboFromPreferences(_dlg.cboToFind, PREFS_KEY_PREFIX_FIND);
		initCboFromPreferences(_dlg.cboToReplace, PREFS_KEY_PREFIX_REPLACE);
		_session.selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);
		GUIUtils.centerWithinParent(_dlg);
      _dlg.setVisible(true);
		_dlg.toFront();

		String selection = _plugin.getSearchAndReplaceKernel(_session).getSelectedText();
		if(null != selection && !"".equals(selection))
		{
			_dlg.cboToFind.getEditor().setItem(selection);
		}

		_dlg.cboToFind.getEditor().getEditorComponent().requestFocus();
	}

	private void initCboFromPreferences(JComboBox cbo, String prefsKeyPrefix)
	{
		cbo.removeAllItems();
		for (int i = 0;; i++)
		{
			String cboEntry = Preferences.userRoot().get(prefsKeyPrefix + i, null);
			if(null != cboEntry)
			{
            cbo.addItem(cboEntry);
			}
			else
			{
				break;
			}
		}
		cbo.getEditor().setItem(null);
		cbo.setSelectedIndex(-1);
	}

	private void onClose()
	{
		for (int i = 0; i < _dlg.cboToFind.getItemCount(); i++)
		{
			Preferences.userRoot().put(PREFS_KEY_PREFIX_FIND + i, _dlg.cboToFind.getItemAt(i).toString());
		}
		for (int i = 0; i < _dlg.cboToReplace.getItemCount(); i++)
		{
			Preferences.userRoot().put(PREFS_KEY_PREFIX_REPLACE + i, _dlg.cboToReplace.getItemAt(i).toString());
		}

		_dlg.setVisible(false);
	}

	private void onSearch()
	{
		_dlg.lblMessage.setText("");

		String toSearch = _dlg.cboToFind.getEditor().getItem().toString();

		if(null == toSearch)
		{
			return;
		}

		if("".equals(toSearch))
		{
			return;
		}

		SearchAndReplaceKernel kernel = _plugin.getSearchAndReplaceKernel(_session);

		FindConfig cfg = new FindConfig(toSearch, _dlg.chkWholeWord.isSelected(), _dlg.chkMatchCase.isSelected());
		if(!cfg.equals(kernel.getLastFindConfig()))
		{
			kernel.beginFromStart();
		}


		if( false == kernel.performFind(cfg) )
		{
			_dlg.lblMessage.setText("No matches found");
		}

		final String toSearchBuf = toSearch;
		SwingUtilities.invokeLater
		(
				new Runnable()
				{
					public void run()
					{manageCboHistory(_dlg.cboToFind, toSearchBuf);}
				}
		);
	}

	private void manageCboHistory(JComboBox cbo, String toAddToHistory)
	{
		int foundIndex = -1;
		for (int i = 0; i < cbo.getItemCount(); i++)
		{
			String buf = cbo.getItemAt(i).toString();
			if(buf.equals(toAddToHistory))
			{
				foundIndex = i;
				break;
			}
		}

		if(-1 == foundIndex)
		{
			cbo.insertItemAt(toAddToHistory, 0);
			while(5 < cbo.getItemCount())
			{
				cbo.removeItemAt(5);
			}
		}
		else
		{
			String buf = (String) cbo.getItemAt(foundIndex);
			cbo.removeItemAt(foundIndex);
			cbo.insertItemAt(buf, 0);
		}
		cbo.setSelectedIndex(-1);
		if(0 < cbo.getItemCount())
		{
			cbo.getEditor().setItem(cbo.getItemAt(0));
		}
		cbo.setPopupVisible(false);
	}
}
