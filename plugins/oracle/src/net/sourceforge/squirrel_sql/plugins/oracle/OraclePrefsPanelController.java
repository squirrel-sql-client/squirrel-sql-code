package net.sourceforge.squirrel_sql.plugins.oracle;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.awt.*;

public class OraclePrefsPanelController implements IGlobalPreferencesPanel
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(OraclePrefsPanelController.class);

   private OraclePrefsPanel _panel;
   private OracleGlobalPrefs _prefs;

   OraclePrefsPanelController(OracleGlobalPrefs prefs)
   {
      _panel = new OraclePrefsPanel();
      _prefs = prefs;
   }


   public void initialize(IApplication app)
   {
      _panel.radLoadAccessibleSchemasExceptSYS.setSelected(_prefs.isLoadAccessibleSchemasExceptSYS());
      _panel.radLoadAccessibleSchemasAndSYS.setSelected(_prefs.isLoadAccessibleSchemasAndSYS());
      _panel.radLoadAllSchemas.setSelected(_prefs.isLoadAllSchemas());
   }

   public void uninitialize(IApplication app)
   {
   }

   public void applyChanges()
   {
      _prefs.setLoadAccessibleSchemasExceptSYS(_panel.radLoadAccessibleSchemasExceptSYS.isSelected());
      _prefs.setLoadAccessibleSchemasAndSYS(_panel.radLoadAccessibleSchemasAndSYS.isSelected());
      _prefs.setLoadAllSchemas(_panel.radLoadAllSchemas.isSelected());
   }

   public String getTitle()
   {
      // i18n[OraclePrefsPanelController.title=Oracle]
      return s_stringMgr.getString("OraclePrefsPanelController.title");
   }

   public String getHint()
   {
      // i18n[OraclePrefsPanelController.hint=Oracle Plugin preferences]
      return s_stringMgr.getString("OraclePrefsPanelController.hint");
   }

   public Component getPanelComponent()
   {
      return _panel;
   }


}
