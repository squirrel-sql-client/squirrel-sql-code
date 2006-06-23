package net.sourceforge.squirrel_sql.plugins.oracle;

import net.sourceforge.squirrel_sql.client.gui.db.aliasproperties.IAliasPropertiesPanelController;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class OracleAliasPrefsPanelController implements IAliasPropertiesPanelController
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(OracleAliasPrefsPanelController.class);

   private OracleAliasPrefsPanel _panel;
   private OracleAliasPrefs _prefs;

   OracleAliasPrefsPanelController(OracleAliasPrefs prefs)
   {
      _panel = new OracleAliasPrefsPanel();
      _prefs = prefs;

      _panel.radLoadAccessibleSchemasExceptSYS.setSelected(_prefs.isLoadAccessibleSchemasExceptSYS());
      _panel.radLoadAccessibleSchemasAndSYS.setSelected(_prefs.isLoadAccessibleSchemasAndSYS());
      _panel.radLoadAllSchemas.setSelected(_prefs.isLoadAllSchemas());


      _panel.btnApplyNow.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            applyChanges();
         }
      });
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
