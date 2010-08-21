package net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop;

import javax.swing.*;
import java.awt.*;

public class DockPanel extends JPanel
{
   public DockPanel()
   {
      setLayout(new GridLayout(1, 1));
   }

   @Override
   public Dimension getMinimumSize()
   {
      return new Dimension(0, super.getMinimumSize().height);
   }

   @Override
   public Dimension getMaximumSize()
   {
      return new Dimension(100000, super.getMaximumSize().height);
   }
   
}
