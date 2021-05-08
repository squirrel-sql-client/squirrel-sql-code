package net.sourceforge.squirrel_sql.client.gui.db.aliasproperties;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetAdapter;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetEvent;
import net.sourceforge.squirrel_sql.client.preferences.PreferenceType;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class AliasPropertiesController
{
   private static HashMap<IIdentifier, AliasPropertiesController> _currentlyOpenInstancesByAliasID = new HashMap<>();

   private AliasPropertiesInternalFrame _frame;
   private ArrayList<IAliasPropertiesPanelController> _iAliasPropertiesPanelControllers = new ArrayList<>();
   private IApplication _app;
   private SQLAlias _alias;

   public static void showAliasProperties(IApplication app, SQLAlias selectedAlias)
   {
      AliasPropertiesController openProps = _currentlyOpenInstancesByAliasID.get(selectedAlias.getIdentifier());
      if(null == openProps)
      {
         _currentlyOpenInstancesByAliasID.put(selectedAlias.getIdentifier(), new AliasPropertiesController(app, selectedAlias));
      }
      else
      {
         openProps._frame.moveToFront();
      }

   }

   private AliasPropertiesController(IApplication app, SQLAlias selectedAlias)
   {
      _app = app;
      _alias = selectedAlias;
      _frame = new AliasPropertiesInternalFrame(_alias.getName(), app);

      _app.getMainFrame().addWidget(_frame);

      DialogWidget.centerWithinDesktop(_frame);

      _frame.setVisible(true);


      _frame.btnOk.addActionListener(e -> onOK());

      _frame.btnClose.addActionListener(e -> onClose());

      _frame.addWidgetListener(new WidgetAdapter(){
         @Override
         public void widgetClosed(WidgetEvent evt)
         {
            performClose();
         }
      });

      GUIUtils.enableCloseByEscape(_frame, dw -> _currentlyOpenInstancesByAliasID.remove(_alias.getIdentifier()));
      loadTabs();
   }

   private void loadTabs()
   {
      _iAliasPropertiesPanelControllers.add(new SchemaPropertiesController(_alias, _app));
      _iAliasPropertiesPanelControllers.add(new DriverPropertiesController(_alias, _app));
      _iAliasPropertiesPanelControllers.add(new ColorPropertiesController(_alias, _app));
      _iAliasPropertiesPanelControllers.add(new ConnectionPropertiesController(_alias, _app));
      
      IAliasPropertiesPanelController[] pluginAliasPropertiesPanelControllers =
         _app.getPluginManager().getAliasPropertiesPanelControllers(_alias);

      _iAliasPropertiesPanelControllers.addAll(Arrays.asList(pluginAliasPropertiesPanelControllers));


      for (int i = 0; i < _iAliasPropertiesPanelControllers.size(); i++)
      {
         IAliasPropertiesPanelController aliasPropertiesController = _iAliasPropertiesPanelControllers.get(i);

         int index = _frame.tabPane.getTabCount();
         _frame.tabPane.add(aliasPropertiesController.getTitle(), aliasPropertiesController.getPanelComponent());
         _frame.tabPane.setToolTipTextAt(index, aliasPropertiesController.getHint());

      }
   }

   private void performClose()
   {
      _currentlyOpenInstancesByAliasID.remove(_alias.getIdentifier());
      _frame.setVisible(false);
      _frame.dispose();
   }

   private void onOK()
   {
      for (int i = 0; i < _iAliasPropertiesPanelControllers.size(); i++)
      {
         IAliasPropertiesPanelController aliasPropertiesController = _iAliasPropertiesPanelControllers.get(i);
         aliasPropertiesController.applyChanges();
      }

      _app.savePreferences(PreferenceType.ALIAS_DEFINITIONS);
      performClose();
   }

   private void onClose()
   {
      performClose();
   }

}
