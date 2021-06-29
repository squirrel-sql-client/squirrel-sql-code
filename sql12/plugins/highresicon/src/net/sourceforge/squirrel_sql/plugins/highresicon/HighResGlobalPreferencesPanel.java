package net.sourceforge.squirrel_sql.plugins.highresicon;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.awt.Component;

public class HighResGlobalPreferencesPanel implements IGlobalPreferencesPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(HighResGlobalPreferencesPanel.class);

   private final HighResPrefsCtrl _highResPrefsCtrl;

   public HighResGlobalPreferencesPanel(HighResolutionIconPlugin plugin)
   {
      _highResPrefsCtrl = new HighResPrefsCtrl(plugin);
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
      _highResPrefsCtrl.applyChanges();
   }

   @Override
   public String getTitle()
   {
      return s_stringMgr.getString("HighResPrefsPanel.highResolution");
   }

   @Override
   public String getHint()
   {
      return s_stringMgr.getString("HighResPrefsPanel.highResolution.hint");
   }

   @Override
   public Component getPanelComponent()
   {
      return _highResPrefsCtrl.getPanel();
   }
}
