package net.sourceforge.squirrel_sql.client.session.mainpanel;

import javax.swing.*;
import java.awt.*;

public class TabButton extends JButton
{
   TabButton(Action action)
   {
      super(action);
      setMargin(new Insets(0, 0, 0, 0));
      setBorderPainted(false);
      setText("");
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
