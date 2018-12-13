package net.sourceforge.squirrel_sql.client.session.mainpanel;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;

public class TabbedExcutionPanelUtil
{
   static int getIndexOfTab(IResultTab resultTab, JTabbedPane tabbedExecutionsPanel)
   {
      return getIndexOfTab((JComponent)resultTab, tabbedExecutionsPanel);
   }

   static int getIndexOfTab(JComponent tab, JTabbedPane tabbedExecutionsPanel)
   {
      if(null == tab)
      {
         return -1;
      }

      for (int i = 0; i < tabbedExecutionsPanel.getTabCount(); i++)
      {
         if (tab == tabbedExecutionsPanel.getComponentAt(i))
         {
            return i;
         }
      }
      return -1;
   }
}
