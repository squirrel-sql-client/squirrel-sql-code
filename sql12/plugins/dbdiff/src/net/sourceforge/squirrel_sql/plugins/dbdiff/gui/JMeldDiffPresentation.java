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

package net.sourceforge.squirrel_sql.plugins.dbdiff.gui;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import org.jmeld.settings.EditorSettings;
import org.jmeld.settings.JMeldSettings;
import org.jmeld.ui.JMeldPanel;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

/**
 * A DiffPresentation that uses components from the JMeld project to render a comparison of the content of two
 * files in a JFrame.
 */
public class JMeldDiffPresentation extends AbstractSideBySideDiffPresentation
{
	public static final String PREF_IGNORE_WHITE_SPACES = "JMeldDiffPresentation.PREF_IGNORE_WHITE_SPACES";

	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(JMeldDiffPresentation.class);
	private JCheckBox _chkIgnoreWhiteSpaces;
	private JMeldPanel _meldPanel;

	/**
	 * @see net.sourceforge.squirrel_sql.plugins.dbdiff.gui.AbstractSideBySideDiffPresentation#
	 *      executeDiff(java.lang.String, java.lang.String)
	 */
	@Override
	public void executeDiff(String script1Filename, String script2Filename)
	{
		executeDiff(script1Filename, script2Filename, s_stringMgr.getString("JMeldDiffPresentation.table.diff"));
	}
	public void executeDiff(String script1Filename, String script2Filename, String diffDialogTitle)
	{
		final EditorSettings editorSettings = JMeldSettings.getInstance().getEditor();
		editorSettings.setRightsideReadonly(true);
		editorSettings.setLeftsideReadonly(true);

		JDialog diffDialog = new JDialog(Main.getApplication().getMainFrame(), diffDialogTitle);

		JPanel pnl = createPanel(diffDialog);
		diffDialog.getContentPane().setLayout(new GridLayout(1,1));
		diffDialog.add(pnl);

		_chkIgnoreWhiteSpaces.setSelected(Props.getBoolean(PREF_IGNORE_WHITE_SPACES, false));
		onIgnoreWhiteSpaces(_meldPanel);
		_chkIgnoreWhiteSpaces.addActionListener(e -> onIgnoreWhiteSpaces(_meldPanel));


		GUIUtils.enableCloseByEscape(diffDialog);
		GUIUtils.initLocation(diffDialog, 500, 400, JMeldDiffPresentation.class.getName());

		diffDialog.setVisible(true);

		JMeldSettings.getInstance().getEditor().enableCustomFont(true);
		JMeldSettings.getInstance().getEditor().setFont(new Font(Font.MONOSPACED, Font.PLAIN, new JLabel().getFont().getSize()));
		_meldPanel.openComparison(script1Filename, script2Filename);
	}

	private JPanel createPanel(JDialog diffDialog)
	{
		JPanel ret = new JPanel(new GridBagLayout());

		GridBagConstraints gbc;

		gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
		_chkIgnoreWhiteSpaces = new JCheckBox(s_stringMgr.getString("JMeldDiffPresentation.ignore.white.spaces"));
		ret.add(_chkIgnoreWhiteSpaces, gbc);

		_meldPanel = new NonExitingJMeldPanel(() -> close(diffDialog));
		_meldPanel.SHOW_TABBEDPANE_OPTION.disable();
		_meldPanel.SHOW_TOOLBAR_OPTION.disable();
		gbc = new GridBagConstraints(0,1,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0,0);
		ret.add(_meldPanel, gbc);

		return ret;
	}

	private void onIgnoreWhiteSpaces(JMeldPanel meldPanel)
	{
		JMeldSettings.getInstance().getEditor().setIgnoreBlankLines(_chkIgnoreWhiteSpaces.isSelected());
		JMeldSettings.getInstance().getEditor().setIgnoreWhitespaceAtBegin(_chkIgnoreWhiteSpaces.isSelected());
		JMeldSettings.getInstance().getEditor().setIgnoreWhitespaceAtEnd(_chkIgnoreWhiteSpaces.isSelected());
		JMeldSettings.getInstance().getEditor().setIgnoreWhitespaceInBetween(_chkIgnoreWhiteSpaces.isSelected());

		//meldPanel.doRefresh(new ActionEvent("SQuirreL", 0, "onIgnoreWhiteSpaces"));
		JMeldPanel.getContentPanelList(meldPanel.getTabbedPane()).forEach(p -> p.doRefresh());

		meldPanel.revalidate();

		Props.putBoolean(PREF_IGNORE_WHITE_SPACES, _chkIgnoreWhiteSpaces.isSelected());
	}

	private void close(JDialog diffDialog)
	{
		diffDialog.setVisible(false);
		diffDialog.dispose();
	}

}
