package net.sourceforge.squirrel_sql.client.session.mainpanel;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import javax.swing.*;

public class TabButton extends JButton
{
   TabButton(Action action)
   {
      super(action);
      GUIUtils.styleAsTabButton(this);
   }

   TabButton()
   {
      this((Action) null);
   }

   public TabButton(ImageIcon icon)
   {
      this((Action) null);
      setIcon(icon);
   }
}
