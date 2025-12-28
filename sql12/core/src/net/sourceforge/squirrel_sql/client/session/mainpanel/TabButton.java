package net.sourceforge.squirrel_sql.client.session.mainpanel;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

public class TabButton extends JButton
{
   public TabButton(Action action)
   {
      super(action);
      GUIUtils.styleAsTabButton(this);
   }

   public TabButton()
   {
      this((Action) null);
   }

   public TabButton(ImageIcon icon)
   {
      this((Action) null);
      setIcon(icon);
   }
}
