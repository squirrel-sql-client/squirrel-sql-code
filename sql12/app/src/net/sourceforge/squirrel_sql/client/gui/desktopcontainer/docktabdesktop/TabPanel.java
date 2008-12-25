package net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop;

import javax.swing.*;
import java.awt.*;

public class TabPanel extends JPanel
{
   private TabHandle _tabHandle;

   public TabPanel(TabHandle tabHandle)
   {
      _tabHandle = tabHandle;
      setLayout(new GridLayout(1,1));
      add(tabHandle.getWidget().getContentPane());
   }

   public TabHandle getTabHandle()
   {
      return _tabHandle;
   }
}
