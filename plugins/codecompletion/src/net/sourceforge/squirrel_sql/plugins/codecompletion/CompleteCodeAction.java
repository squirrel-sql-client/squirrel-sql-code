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
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;

public class CompleteCodeAction extends SquirrelAction
{
   private ISQLEntryPanel _sqlEntryPanel;
   private CodeCompletor _cc;



	public CompleteCodeAction(IApplication app, PluginResources rsrc, ISQLEntryPanel sqlEntryPanel, ISession session, CodeCompletionInfoCollection codeCompletionInfos)
   {
      super(app, rsrc);
      _sqlEntryPanel = sqlEntryPanel;

		CodeCompletorModel model = new CodeCompletorModel(session, codeCompletionInfos);
      _cc = new CodeCompletor((JTextComponent)_sqlEntryPanel.getTextComponent(), model);
		_sqlEntryPanel.addSQLTokenListener(model.getSQLTokenListener());

      _cc.addCodeCompletorListener
      (
         new CodeCompletorListener()
         {
            public void completionSelected(CodeCompletionInfo completion, int replaceBegin)
            {performCompletionSelected(completion, replaceBegin);}
         }
      );
   }


   public void actionPerformed(ActionEvent evt)
   {
      _cc.show();
   }



   private void performCompletionSelected(CodeCompletionInfo completion, int replaceBegin)
   {

      _sqlEntryPanel.setSelectionStart(replaceBegin);
      _sqlEntryPanel.setSelectionEnd(_sqlEntryPanel.getCaretPosition());

      _sqlEntryPanel.replaceSelection(completion.getCompletionString());
	}
}