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

import java.awt.Font;
import javax.swing.JDialog;
import javax.swing.JLabel;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import org.jmeld.settings.EditorSettings;
import org.jmeld.settings.JMeldSettings;
import org.jmeld.ui.JMeldPanel;

/**
 * A DiffPresentation that uses components from the JMeld project to render a comparison of the content of two
 * files in a JFrame.
 */
public class JMeldDiffPresentation extends AbstractSideBySideDiffPresentation
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(JMeldDiffPresentation.class);

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

		final JMeldPanel meldPanel = new NonExitingJMeldPanel(() -> close(diffDialog));
		meldPanel.SHOW_TABBEDPANE_OPTION.disable();
		meldPanel.SHOW_TOOLBAR_OPTION.disable();

		diffDialog.add(meldPanel);

		GUIUtils.enableCloseByEscape(diffDialog);
		GUIUtils.initLocation(diffDialog, 500, 400, JMeldDiffPresentation.class.getName());

		diffDialog.setVisible(true);

		JMeldSettings.getInstance().getEditor().enableCustomFont(true);
		JMeldSettings.getInstance().getEditor().setFont(new Font(Font.MONOSPACED, Font.PLAIN, new JLabel().getFont().getSize()));
		meldPanel.openComparison(script1Filename, script2Filename);
	}

	private void close(JDialog diffDialog)
	{
		diffDialog.setVisible(false);
		diffDialog.dispose();
	}

}
