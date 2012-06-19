package net.sourceforge.squirrel_sql.client.preferences.codereformat;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.awt.*;

public class FormatSqlConfigPrefsTab implements IGlobalPreferencesPanel
{
   private static final StringManager s_stringMgr =
         StringManagerFactory.getStringManager(FormatSqlConfigPrefsTab.class);


   private FormatSqlController _formatSqlController;

   public FormatSqlConfigPrefsTab(IApplication app)
   {
      _formatSqlController = new FormatSqlController(app);
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
      _formatSqlController.applyChanges();
   }

   @Override
   public String getTitle()
   {
      return s_stringMgr.getString("codereformat.FormatSqlConfigPrefsTab.title");
   }

   @Override
   public String getHint()
   {
      return s_stringMgr.getString("codereformat.FormatSqlConfigPrefsTab.hint");
   }

   @Override
   public Component getPanelComponent()
   {
      return _formatSqlController.getPanel();
   }
}
