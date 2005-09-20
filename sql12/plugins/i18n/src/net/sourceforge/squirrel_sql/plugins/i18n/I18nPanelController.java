package net.sourceforge.squirrel_sql.plugins.i18n;

import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.awt.*;
import java.util.Locale;

public class I18nPanelController implements IGlobalPreferencesPanel
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(I18nPanelController.class);

   I18nPanel _panel;

   I18nPanelController()
   {
      _panel = new I18nPanel();

   }

   public void initialize(IApplication app)
   {
      Locale[] availableLocales = Locale.getAvailableLocales();
      for (int i = 0; i < availableLocales.length; i++)
      {
         _panel.cboLocales.addItem(availableLocales[i]);
      }

      _panel.cboLocales.setSelectedItem(Locale.getDefault());
   }

   public void applyChanges()
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public String getTitle()
   {
      return s_stringMgr.getString("I18n.title");
   }

   public String getHint()
   {
      return s_stringMgr.getString("I18n.hint");
   }

   public Component getPanelComponent()
   {
      return _panel;
   }

}
