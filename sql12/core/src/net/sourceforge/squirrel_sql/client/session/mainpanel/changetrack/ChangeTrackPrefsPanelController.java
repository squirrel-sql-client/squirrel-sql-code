package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;


import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JPanel;

public class ChangeTrackPrefsPanelController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ChangeTrackPrefsPanelController.class);


   private ChangeTrackPrefsPanel _changeTrackPrefsPanel;

   public ChangeTrackPrefsPanelController()
   {
      _changeTrackPrefsPanel = new ChangeTrackPrefsPanel();
   }

   public JPanel getPanel()
   {
      return _changeTrackPrefsPanel;
   }
}
