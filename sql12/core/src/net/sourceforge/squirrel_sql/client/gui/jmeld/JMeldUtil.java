package net.sourceforge.squirrel_sql.client.gui.jmeld;

import org.jmeld.settings.JMeldSettings;
import org.jmeld.ui.AbstractContentPanel;
import org.jmeld.ui.BufferDiffPanel;
import org.jmeld.ui.JMeldPanel;

public class JMeldUtil
{
   public static void cleanMeldPanel(JMeldPanel meldPanel)
   {
      for (AbstractContentPanel abstractContentPanel : JMeldPanel.getContentPanelList(meldPanel.getTabbedPane()))
      {
         if(abstractContentPanel instanceof BufferDiffPanel)
         {
            JMeldSettings.getInstance().removeConfigurationListener((BufferDiffPanel) abstractContentPanel);
         }
      }
      meldPanel.getTabbedPane().removeAll();
   }
}
