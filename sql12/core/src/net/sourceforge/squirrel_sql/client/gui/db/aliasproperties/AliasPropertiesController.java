package net.sourceforge.squirrel_sql.client.gui.db.aliasproperties;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetAdapter;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetEvent;
import net.sourceforge.squirrel_sql.client.mainframe.action.findprefs.AliasPropertiesDialogFindInfo;
import net.sourceforge.squirrel_sql.client.mainframe.action.findprefs.PreferencesFindSupport;
import net.sourceforge.squirrel_sql.client.preferences.PreferenceType;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class AliasPropertiesController
{
   private static HashMap<IIdentifier, AliasPropertiesController> s_currentlyOpenInstancesByAliasID = new HashMap<>();

   private AliasPropertiesInternalFrame _frame;
   private ArrayList<IAliasPropertiesPanelController> _iAliasPropertiesPanelControllers = new ArrayList<>();
   private SQLAlias _alias;

   public static void showAliasProperties(SQLAlias selectedAlias)
   {
      AliasPropertiesController openProps = s_currentlyOpenInstancesByAliasID.get(selectedAlias.getIdentifier());
      if(null == openProps)
      {
         s_currentlyOpenInstancesByAliasID.put(selectedAlias.getIdentifier(), new AliasPropertiesController(selectedAlias));
      }
      else
      {
         openProps._frame.moveToFront();
      }
   }

   public static PreferencesFindSupport<AliasPropertiesDialogFindInfo> getPreferencesFindSupport()
   {
      return ofOpenDialog -> onCreateFindInfo(ofOpenDialog);
   }

   private static AliasPropertiesDialogFindInfo onCreateFindInfo(boolean ofOpenDialog)
   {
      if(Main.getApplication().getAliasesAndDriversManager().getAliasList().isEmpty())
      {
         return null;
      }

      SQLAlias firstAlias = (SQLAlias) Main.getApplication().getAliasesAndDriversManager().getAliasList().get(0);

      if(ofOpenDialog)
      {
         showAliasProperties(firstAlias);
      }

      AliasPropertiesController aliasPropertiesController = s_currentlyOpenInstancesByAliasID.get(firstAlias.getIdentifier());
      if(null == aliasPropertiesController)
      {
         aliasPropertiesController = new AliasPropertiesController(firstAlias, true);
      }

      return new AliasPropertiesDialogFindInfo(aliasPropertiesController._frame.getTitle(), aliasPropertiesController._frame.tabPane);
   }


   private AliasPropertiesController(SQLAlias alias)
   {
      this(alias, false);
   }

   private AliasPropertiesController(SQLAlias selectedAlias, boolean toUseByPreferencesFinderOnly)
   {
      _alias = selectedAlias;
      _frame = new AliasPropertiesInternalFrame(_alias.getName());

      Main.getApplication().getMainFrame().addWidget(_frame);

      DialogWidget.centerWithinDesktop(_frame);



      _frame.btnOk.addActionListener(e -> onOK());

      _frame.btnClose.addActionListener(e -> onClose());

      _frame.addWidgetListener(new WidgetAdapter(){
         @Override
         public void widgetClosed(WidgetEvent evt)
         {
            performClose();
         }
      });

      loadTabs();


      if(false == toUseByPreferencesFinderOnly)
      {
         GUIUtils.enableCloseByEscape(_frame, dw -> s_currentlyOpenInstancesByAliasID.remove(_alias.getIdentifier()));
         _frame.setVisible(true);
      }
   }


   private void loadTabs()
   {
      _iAliasPropertiesPanelControllers.add(new SchemaPropertiesController(_alias));
      _iAliasPropertiesPanelControllers.add(new DriverPropertiesController(_alias));
      _iAliasPropertiesPanelControllers.add(new ColorPropertiesController(_alias));
      _iAliasPropertiesPanelControllers.add(new ConnectionPropertiesController(_alias));
      
      IAliasPropertiesPanelController[] pluginAliasPropertiesPanelControllers =
            Main.getApplication().getPluginManager().getAliasPropertiesPanelControllers(_alias);

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
      s_currentlyOpenInstancesByAliasID.remove(_alias.getIdentifier());
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

      Main.getApplication().savePreferences(PreferenceType.ALIAS_DEFINITIONS);
      performClose();
   }

   private void onClose()
   {
      performClose();
   }

}
