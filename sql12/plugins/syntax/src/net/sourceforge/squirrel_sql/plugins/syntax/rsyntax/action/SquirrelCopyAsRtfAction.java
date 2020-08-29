/*
 * Copyright (C) 2011 Stefan Willinger
 * wis775@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.plugins.syntax.rsyntax.action;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.action.ISQLPanelAction;
import net.sourceforge.squirrel_sql.fw.gui.ClipboardUtil;
import net.sourceforge.squirrel_sql.plugins.syntax.rsyntax.SquirrelRSyntaxTextArea;

import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.fw.resources.IResources;

/**
 * A Wrapper for {@link CopyAsRtfAction}.
 * This wrapper is the simplest way to customize the action properties like name and tooltip.
 *
 * @author Stefan Willinger
 */
public class SquirrelCopyAsRtfAction extends SquirrelAction implements ISQLPanelAction
{
   private ISQLPanelAPI _panel;


   /**
    * Construct a wrapper for {@link CopyAsRtfAction}
    *
    * @param rsrc The plugin resources.
    */
   public SquirrelCopyAsRtfAction(IResources rsrc)
   {
      super(Main.getApplication(), rsrc);
   }

   /**
    * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
    */
   @Override
   public void actionPerformed(ActionEvent e)
   {
      if(null == _panel)
      {
         return;
      }

      if(_panel.getSQLEntryPanel().getTextComponent() instanceof SquirrelRSyntaxTextArea)
      {
         SquirrelRSyntaxTextArea squirrelRSyntaxTextArea = (SquirrelRSyntaxTextArea) _panel.getSQLEntryPanel().getTextComponent();
         squirrelRSyntaxTextArea.copyAsStyledText();

         Main.getApplication().getPasteHistory().addToPasteHistory(ClipboardUtil.getClipboardAsString());
      }
   }


   @Override
   public void setSQLPanel(ISQLPanelAPI panel)
   {

      _panel = panel;
   }
}
