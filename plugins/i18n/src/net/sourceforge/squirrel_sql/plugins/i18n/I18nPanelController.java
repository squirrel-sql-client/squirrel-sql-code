package net.sourceforge.squirrel_sql.plugins.i18n;

import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.awt.*;

public class I18nPanelController implements IGlobalPreferencesPanel
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(I18nPanelController.class);

   private I18nPanel _panel;
   private TranslatorsController _translatorsController;
   private DevelopersController _developersController;

   I18nPanelController(PluginResources resources)
   {
      _panel = new I18nPanel(resources);
      _translatorsController = new TranslatorsController(_panel.pnlTranslators);
      _developersController = new DevelopersController(_panel.pnlDevelopers);
   }


   public void initialize(IApplication app)
   {
      _translatorsController.initialize(app);
      _developersController.initialize(app);
   }

   public void uninitialize(IApplication app)
   {
      _translatorsController.uninitialize();
      _developersController.uninitialize();
   }

   public void applyChanges()
   {
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
