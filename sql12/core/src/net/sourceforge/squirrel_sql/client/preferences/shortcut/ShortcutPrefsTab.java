package net.sourceforge.squirrel_sql.client.preferences.shortcut;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.awt.Component;

public class ShortcutPrefsTab implements IGlobalPreferencesPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ShortcutPrefsTab.class);

   private final ShortcutPrefsCtrl _shortcutPrefsCtrl;


   public ShortcutPrefsTab()
   {
      _shortcutPrefsCtrl = new ShortcutPrefsCtrl();
   }

   @Override
   public void initialize(IApplication app)
   {
   }

   @Override
   public void uninitialize(IApplication app)
   {
   }

   @Override
   public void applyChanges()
   {
      _shortcutPrefsCtrl.applyChanges();
   }

   @Override
   public String getTitle()
   {
      return s_stringMgr.getString("keystroke.prefs.title");
   }

   @Override
   public String getHint()
   {
      return s_stringMgr.getString("keystroke.prefs.hint");
   }

   @Override
   public Component getPanelComponent()
   {
      return _shortcutPrefsCtrl.getPanel();
   }
}
