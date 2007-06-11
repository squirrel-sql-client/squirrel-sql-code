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
package net.sourceforge.squirrel_sql.plugins.sqlbookmark;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.completion.CompletionInfo;
import net.sourceforge.squirrel_sql.fw.completion.Completor;
import net.sourceforge.squirrel_sql.fw.completion.CompletorListener;

import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;
import java.awt.*;

public class CompleteBookmarkAction extends SquirrelAction
{
   private ISQLEntryPanel _sqlEntryPanel;
   private Completor _cc;
   private ISession _session;
   private SQLBookmarkPlugin _plugin;


   public CompleteBookmarkAction(IApplication app, PluginResources rsrc, ISQLEntryPanel sqlEntryPanel, ISession session, SQLBookmarkPlugin plugin)
   {
      super(app, rsrc);
      _sqlEntryPanel = sqlEntryPanel;
      _session = session;
      _plugin = plugin;

      _cc = new Completor((JTextComponent)_sqlEntryPanel.getTextComponent(), plugin.getBookmarkManager(), new Color(204,255,255), true);

      _cc.addCodeCompletorListener
      (
         new CompletorListener()
         {
            public void completionSelected(CompletionInfo completion, int replaceBegin, int keyCode, int modifiers)
            {performCompletionSelected(completion);}
         }
      );
   }


   public void actionPerformed(ActionEvent evt)
   {
      _cc.show();
   }



   private void performCompletionSelected(CompletionInfo completion)
   {
      Bookmark bm = ((BookmarkCompletionInfo)completion).getBookmark();
      new RunBookmarkCommand(getApplication().getMainFrame(), _session, bm, _plugin, _sqlEntryPanel).execute();
	}
}