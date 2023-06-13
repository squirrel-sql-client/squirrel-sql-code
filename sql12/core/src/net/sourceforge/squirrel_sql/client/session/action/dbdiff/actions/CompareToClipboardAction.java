/*
 * Copyright (C) 2007 Rob Manning
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

package net.sourceforge.squirrel_sql.client.session.action.dbdiff.actions;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.action.ISQLPanelAction;
import net.sourceforge.squirrel_sql.client.session.action.dbdiff.tableselectiondiff.TableSelectionDiffUtil;
import net.sourceforge.squirrel_sql.fw.gui.ClipboardUtil;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.awt.event.ActionEvent;
import java.nio.file.Path;

public class CompareToClipboardAction extends SquirrelAction implements ISQLPanelAction
{

	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CompareToClipboardAction.class);

	private final static ILogger log = LoggerController.createLogger(CompareToClipboardAction.class);
	private ISQLPanelAPI _sqlPanelAPI;

	public CompareToClipboardAction()
	{
		super(Main.getApplication());
	}

	public void actionPerformed(ActionEvent evt)
	{
		String clipboardAsString = ClipboardUtil.getClipboardAsString();

		if(StringUtilities.isEmpty(clipboardAsString, true))
		{
			Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("CompareToClipboardAction.clipboard.empty.warn"));
			return;
		}

		Path leftClipboardTempFile = TableSelectionDiffUtil.createLeftTempFile(clipboardAsString);

		boolean isSelectedText = true;
		String rightEditorTextToCompare = _sqlPanelAPI.getSQLEntryPanel().getSelectedText();
		if(null == rightEditorTextToCompare)
		{
			isSelectedText = false;
			rightEditorTextToCompare = _sqlPanelAPI.getSQLEntryPanel().getText();

			if(StringUtilities.isEmpty(rightEditorTextToCompare, true))
			{
				Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("CompareToClipboardAction.editor.empty.warn"));
				return;
			}

		}

		Path rightEditorTextTempFile = TableSelectionDiffUtil.createRightTempFile(rightEditorTextToCompare);

		CompareToClipboardCtrl compareToClipboardCtrl = new CompareToClipboardCtrl(_sqlPanelAPI.getOwningFrame(), leftClipboardTempFile, rightEditorTextTempFile);


		String textToSave = compareToClipboardCtrl.getTextToSave();
		if(null != textToSave)
		{
			if(isSelectedText)
			{
				_sqlPanelAPI.getSQLEntryPanel().replaceSelection(textToSave);
			}
			else
			{
				_sqlPanelAPI.setEntireSQLScript(textToSave, false);
			}
		}



		//DBDIffService.showDiff(leftClipboardTempFile, rightEditorTextTempFile, s_stringMgr.getString("CompareToClipboardAction.clipboard.vs.editor"));

	}

	@Override
	public void setSQLPanel(ISQLPanelAPI sqlPanelAPI)
	{
		_sqlPanelAPI = sqlPanelAPI;
		setEnabled(null != _sqlPanelAPI);
	}
}
