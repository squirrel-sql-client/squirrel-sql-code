package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;


import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class ChangeTrackPrefsPanelController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ChangeTrackPrefsPanelController.class);


   private ChangeTrackPrefsPanel _changeTrackPrefsPanel;

   public ChangeTrackPrefsPanelController()
   {
      _changeTrackPrefsPanel = new ChangeTrackPrefsPanel();
   }

   public ChangeTrackPrefsPanel getPanel()
   {
      return _changeTrackPrefsPanel;
   }

   public void loadData(SquirrelPreferences prefs)
   {
   }

   public void applyChanges(SquirrelPreferences prefs)
   {

   }
}
