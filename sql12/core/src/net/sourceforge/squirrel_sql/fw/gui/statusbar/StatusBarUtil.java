package net.sourceforge.squirrel_sql.fw.gui.statusbar;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;

public class StatusBarUtil
{
   public static Border createComponentBorder()
   {
      return BorderFactory.createCompoundBorder(new ThinLoweredBevelBorder(), BorderFactory.createEmptyBorder(0, 4, 0, 4));
   }

   public static void updateSubcomponentsFont(Container cont, Font font)
   {
      Component[] comps = cont.getComponents();
      for (int i = 0; i < comps.length; ++i)
      {
         comps[i].setFont(font);
         if (comps[i] instanceof Container)
         {
            updateSubcomponentsFont((Container)comps[i], font);
         }
      }
   }
}
