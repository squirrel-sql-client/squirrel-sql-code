package net.sourceforge.squirrel_sql.fw.gui;
/*
 * Copyright (C) 2001-2004 Colin Bell
 * colbell@users.sourceforge.net
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
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.action.BaseAction;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
import java.awt.Component;

/**
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ToolBar extends JToolBar
{
   /** Logger for this class. */
   private static ILogger s_log = LoggerController.createLogger(ToolBar.class);

   public ToolBar()
   {
      super();
   }

   public ToolBar(int orientation)
   {
      super(orientation);
   }

   public JButton add(Action action)
   {
      JButton btn = super.add(action);
      initialiseButton(action, btn);
      return btn;
   }

   public void remove(Action action)
   {
      for (Component component : super.getComponents())
      {
         if(false == component instanceof AbstractButton)
         {
            continue;
         }

         AbstractButton btn = (AbstractButton) component;

         if(action.equals(btn.getAction()))
         {
            remove(btn);
            invalidate();
            doLayout();
            repaint();
            return;
         }
      }
   }


   public JToggleButton addToggleAction(IToggleAction action)
   {
      return addToggleAction(action, null);
   }

   /**
    * @param session needed to prevent memory leaks.
    */
   public JToggleButton addToggleAction(IToggleAction action, ISession session)
   {
      JToggleButton tglBtn = new JToggleButton();
      tglBtn.setAction(action);
      super.add(tglBtn);
      action.getToggleComponentHolder().addToggleableComponent(tglBtn, session);
      initialiseButton(action, tglBtn);
      return tglBtn;
   }


   public AbstractButton add(Action action, AbstractButton btn)
   {
      btn.setAction(action);
      super.add(btn);
      initialiseButton(action, btn);
      return btn;
   }



   public void setUseRolloverButtons(boolean value)
   {
      putClientProperty("JToolBar.isRollover", value ? Boolean.TRUE : Boolean.FALSE);
   }

   protected void initialiseButton(Action action, AbstractButton btn)
   {
      if (btn != null)
      {
         btn.setRequestFocusEnabled(false);
         btn.setText("");
         String tt = null;
         if (action != null)
         {
            tt = (String) action.getValue(Action.SHORT_DESCRIPTION);
         }
         btn.setToolTipText(tt != null ? tt : "");
         if (action != null)
         {
            Icon icon = getIconFromAction(action, BaseAction.IBaseActionPropertyNames.ROLLOVER_ICON);
            if (icon != null)
            {
               btn.setRolloverIcon(icon);
               btn.setRolloverSelectedIcon(icon);
            }
            icon = getIconFromAction(action, BaseAction.IBaseActionPropertyNames.DISABLED_ICON);
            if (icon != null)
            {
               btn.setDisabledIcon(icon);
            }
         }
      }
   }

   /**
    * Retrieve an icon from the passed action for the specified key.
    *
    * @param	action		Action to retrieve icon from.
    * @param	key			Key that specified the icon.
    *
    * @return	The requested Icon or null.
    */
   protected Icon getIconFromAction(Action action, String key)
   {
      //Object obj = action.getValue(BaseAction.IBaseActionPropertyNames.ROLLOVER_ICON);
      Object obj = action.getValue(key);
      if (obj != null)
      {
         if (obj instanceof Icon)
         {
            return (Icon)obj;
         }
         StringBuffer msg = new StringBuffer();
         msg.append("Non icon object of type ").append(obj.getClass().getName())
            .append(" was stored in an Action of type ")
            .append(action.getClass().getName())
            .append(" using the key ").append(key).append(".");
         s_log.error(msg.toString());
      }
      return null;
   }
}
