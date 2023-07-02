/*
 * Copyright (C) 2011 Rob Manning
 * manningr@users.sourceforge.net
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

package net.sourceforge.squirrel_sql.client.session.action.dbdiff.gui;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.jmeld.JMeldConfigCtrl;
import net.sourceforge.squirrel_sql.client.gui.jmeld.JMeldUtil;
import net.sourceforge.squirrel_sql.client.session.action.dbdiff.JMeldPanelHandlerSaveCallback;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import org.jmeld.settings.EditorSettings;
import org.jmeld.settings.JMeldSettings;
import org.jmeld.ui.AbstractContentPanel;
import org.jmeld.ui.BufferDiffPanel;
import org.jmeld.ui.FilePanel;
import org.jmeld.ui.JMeldPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * A DiffPresentation that uses components from the JMeld project to render a comparison of the content of two
 * files in a JFrame.
 */
public class JMeldDiffPresentation extends AbstractSideBySideDiffPresentation
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(JMeldDiffPresentation.class);
	private boolean _useEmbedded;
	private JMeldPanel _meldPanel;
	private ConfigurableMeldPanel _configurableMeldPanel;

	public JMeldDiffPresentation()
	{
	}

	public JMeldDiffPresentation(boolean useEmbedded, JMeldPanelHandlerSaveCallback saveCallback)
	{
		_useEmbedded = useEmbedded;

		if(false == _useEmbedded)
		{
			return;
		}

		_configurableMeldPanel = createPanel(null);

	}

	/**
	 * @see AbstractSideBySideDiffPresentation#
	 *      executeDiff(java.lang.String, java.lang.String)
	 */
	public void executeDiff(String leftFilename, String rightFilename)
	{
		executeDiff(leftFilename, rightFilename, s_stringMgr.getString("JMeldDiffPresentation.table.diff"));
	}
	public void executeDiff(String leftFilename, String rightFilename, String diffDialogTitle)
	{
		executeDiff(leftFilename, rightFilename, diffDialogTitle, null);
	}

	public void executeDiff(String leftFilename, String rightFilename, String diffDialogTitle, JMeldPanelHandlerSaveCallback saveCallback)
	{
		if (false == _useEmbedded && null != diffDialogTitle)
		{
			JDialog diffDialog = new JDialog(Main.getApplication().getMainFrame(), diffDialogTitle);

			_configurableMeldPanel = createPanel(diffDialog);
			diffDialog.getContentPane().setLayout(new GridLayout(1,1));
			diffDialog.add(_configurableMeldPanel);

			JMeldSettings.getInstance().setDrawCurves(true);


			GUIUtils.enableCloseByEscape(diffDialog, w -> cleanMeldPanel());
			diffDialog.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e)
				{
					cleanMeldPanel();
				}

				@Override
				public void windowClosed(WindowEvent e)
				{
					cleanMeldPanel();
				}
			});

			GUIUtils.initLocation(diffDialog, 500, 400, JMeldDiffPresentation.class.getName());

			diffDialog.setVisible(true);
		}

		if (_useEmbedded)
		{
			JMeldUtil.cleanMeldPanel(_meldPanel);
		}

		final EditorSettings editorSettings = JMeldSettings.getInstance().getEditor();
		JMeldSettings.getInstance().getEditor().enableCustomFont(true);
		JMeldSettings.getInstance().getEditor().setFont(new Font(Font.MONOSPACED, Font.PLAIN, new JLabel().getFont().getSize()));
		editorSettings.setRightsideReadonly(null == saveCallback);
		editorSettings.setLeftsideReadonly(true);

		if(null != saveCallback)
		{
			GUIUtils.forceProperty(() -> 1 < JMeldPanel.getContentPanelList(_meldPanel.getTabbedPane()).size(), () -> prepareSaveButton(_meldPanel, saveCallback));
		}

		_meldPanel.openComparison(leftFilename, rightFilename);
	}

	private void prepareSaveButton(JMeldPanel meldPanel, JMeldPanelHandlerSaveCallback saveCallback)
	{
		JButton saveButton = getRightFilePanel(meldPanel).getSaveButton();
		saveButton.addActionListener(e -> onSaveToEditor(meldPanel, saveCallback));
		saveButton.setToolTipText(s_stringMgr.getString("JMeldDiffPresentation.write.changes.to.sql.editor"));
	}

	private FilePanel getRightFilePanel(JMeldPanel meldPanel)
	{
		for( AbstractContentPanel abstractContentPanel : JMeldPanel.getContentPanelList(meldPanel.getTabbedPane()) )
		{
			if(abstractContentPanel instanceof BufferDiffPanel)
			{
				return ((BufferDiffPanel) abstractContentPanel).getFilePanel(BufferDiffPanel.RIGHT);
			}
		}
		throw new IllegalStateException("Failed to return org.jmeld.ui.FilePanel");
	}

	private void onSaveToEditor(JMeldPanel meldPanel, JMeldPanelHandlerSaveCallback saveCallback)
	{
		String savedText = getRightFilePanel(meldPanel).getEditor().getText();
		saveCallback.rightSideSaved(savedText);
	}


	private ConfigurableMeldPanel createPanel(JDialog diffDialog)
	{
		_meldPanel = new NonExitingJMeldPanel(() -> close(diffDialog));
		_meldPanel.SHOW_TABBEDPANE_OPTION.disable();
		_meldPanel.SHOW_TOOLBAR_OPTION.disable();
		_meldPanel.SHOW_FILE_LABEL_OPTION.disable();

		return new ConfigurableMeldPanel(_meldPanel, new JMeldConfigCtrl(_meldPanel));
	}

	public ConfigurableMeldPanel getConfigurableMeldPanel()
	{
		return _configurableMeldPanel;
	}

	private void close(JDialog diffDialog)
	{
		cleanMeldPanel();

		if (null != diffDialog)
		{
			diffDialog.setVisible(false);
			diffDialog.dispose();
		}
	}

	public void cleanMeldPanel()
	{
		JMeldUtil.cleanMeldPanel(_meldPanel);
	}
}
