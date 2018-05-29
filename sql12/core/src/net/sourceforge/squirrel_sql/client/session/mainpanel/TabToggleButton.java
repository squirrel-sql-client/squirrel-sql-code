package net.sourceforge.squirrel_sql.client.session.mainpanel;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import java.awt.Insets;

public class TabToggleButton extends JToggleButton
{
   TabToggleButton(Action action)
   {
      super(action);
      setMargin(new Insets(0, 0, 0, 0));
      setBorderPainted(false);
      setText("");
   }

   TabToggleButton()
   {
      this((Action) null);
   }

   public TabToggleButton(ImageIcon icon)
   {
      this((Action) null);
      setIcon(icon);
   }
}
