/*
 * Copyright (C) 2003 Gerd Wagner
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package net.sourceforge.squirrel_sql.plugins.codecompletion;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.completion.Completor;
import net.sourceforge.squirrel_sql.fw.completion.CompletorListener;
import net.sourceforge.squirrel_sql.fw.completion.CompletionInfo;
import net.sourceforge.squirrel_sql.fw.completion.CompletionCandidates;

import javax.swing.text.JTextComponent;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class CompleteCodeAction extends SquirrelAction
{
	private ISQLEntryPanel _sqlEntryPanel;
	private Completor _cc;
   private CodeCompletorModel _model;


   public CompleteCodeAction(IApplication app,
                             CodeCompletionPlugin plugin,
                             ISQLEntryPanel sqlEntryPanel,
                             ISession session,
                             CodeCompletionInfoCollection codeCompletionInfos)
	{
		super(app, plugin.getResources());
		_sqlEntryPanel = sqlEntryPanel;

      _model = new CodeCompletorModel(session, plugin, codeCompletionInfos, sqlEntryPanel.getIdentifier());
      _cc = new Completor((JTextComponent)_sqlEntryPanel.getTextComponent(), _model);
		_sqlEntryPanel.addSQLTokenListener(_model.getSQLTokenListener());

		_cc.addCodeCompletorListener
		(
			new CompletorListener()
			{
				public void completionSelected(CompletionInfo completion, int replaceBegin, int keyCode, int modifiers)
				{performCompletionSelected((CodeCompletionInfo) completion, replaceBegin, keyCode, modifiers);}
			}
		);
	}


	public void actionPerformed(ActionEvent evt)
	{
		_cc.show();
	}



	private void performCompletionSelected(CodeCompletionInfo completion, int replaceBegin, int keyCode, int modifiers)
	{

      if(KeyEvent.VK_SPACE == keyCode && modifiers == KeyEvent.CTRL_MASK)
      {
         // Code Completion has been done within Code Completion.
         // and relaunch completion popup.  

         CompletionCandidates completionCandidates = _model.getCompletionCandidates(_cc.getTextTillCarret());

         _sqlEntryPanel.setSelectionStart(replaceBegin);
         _sqlEntryPanel.setSelectionEnd(_sqlEntryPanel.getCaretPosition());
         _sqlEntryPanel.replaceSelection(completionCandidates.getAllCandidatesPrefix());

         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               _cc.show();
            }
         });
      }
      else if(KeyEvent.VK_TAB == keyCode)
		{
			_sqlEntryPanel.setSelectionStart(replaceBegin);
			_sqlEntryPanel.setSelectionEnd(getNextWhiteSpacePos(_sqlEntryPanel.getCaretPosition()));
			_sqlEntryPanel.replaceSelection(completion.getCompletionString());
         adoptCaret(completion);
		}
		else
		{
			_sqlEntryPanel.setSelectionStart(replaceBegin);
			_sqlEntryPanel.setSelectionEnd(_sqlEntryPanel.getCaretPosition());
			_sqlEntryPanel.replaceSelection(completion.getCompletionString());
         adoptCaret(completion);
		}

   }

   private void adoptCaret(CodeCompletionInfo completion)
   {
      if(0 < completion.getMoveCarretBackCount())
      {
         _sqlEntryPanel.setCaretPosition(_sqlEntryPanel.getCaretPosition()  - completion.getMoveCarretBackCount());
      }
   }

   private int getNextWhiteSpacePos(int startPos)
	{
		String text = _sqlEntryPanel.getText();

		int retPos = startPos;

		for(;retPos < text.length(); ++retPos)
		{
			if(Character.isWhitespace(text.charAt(retPos)))
			{
				return retPos;
			}
		}

		return retPos;
	}
}