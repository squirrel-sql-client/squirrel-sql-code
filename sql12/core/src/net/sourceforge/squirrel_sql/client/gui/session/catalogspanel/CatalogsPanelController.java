package net.sourceforge.squirrel_sql.client.gui.session.catalogspanel;

import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JComponent;
import javax.swing.tree.TreePath;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.treefinder.ObjectTreeFinderGoToNextResultHandle;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.treefinder.ObjectTreeSearchResultFuture;
import net.sourceforge.squirrel_sql.client.session.schemainfo.FilterMatcher;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import org.apache.commons.lang3.StringUtils;

public class CatalogsPanelController
{
   private static final ILogger s_log = LoggerController.createLogger(CatalogsPanelController.class);
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CatalogsPanelController.class);
   private final JComponent _parentComponent;


   private ISession _session;
   private CatalogsPanel _catalogsPanel;
   private PropertyChangeListener _connectionPropetryListener;
   private ActionListener _catalogsComboListener;

   public CatalogsPanelController(ISession session, JComponent parentComponent)
   {
      _session = session;
      _parentComponent = parentComponent;
      _catalogsPanel = new CatalogsPanel();
      updateAdditionalCatalogsIcon();


      _catalogsComboListener = e -> onCatalogSelected();
      _connectionPropetryListener = evt -> GUIUtils.processOnSwingEventThread(() -> onConnectionPropertyChanged(evt));

      _catalogsPanel.btnConfigureCatalogLoading.addActionListener(e -> onConfigureCatalogLoading());
      init();
   }

   private void updateAdditionalCatalogsIcon()
   {
      boolean hasAdditionalCatalogs = hasAdditionalUserChosenAndExistingCatalogs();
      _catalogsPanel.setHasAdditionalCatalogs(hasAdditionalCatalogs);
   }

   private void init()
   {
      try
      {
         _catalogsPanel.setVisible(false);
         //_catalogsPanel.removeAll();
         _catalogsPanel.catalogsCmb.removeActionListener(_catalogsComboListener);
         _session.getSQLConnection().removePropertyChangeListener(_connectionPropetryListener);

         final String[] catalogs = CatalogsPanelUtil.getCatalogsIfSupportedElseNull(_session);
         if(catalogs == null)
         {
            return;
         }

         final String selected = _session.getSQLConnection().getCatalog();


         setCatalogs(catalogs, selected);
         _catalogsPanel.catalogsCmb.addActionListener(_catalogsComboListener);


         _catalogsPanel.initSizeAndBackgroundAfterCatalogsComboFilled();
         _catalogsPanel.setVisible(true);
         _parentComponent.validate();

         _session.getSQLConnection().addPropertyChangeListener(_connectionPropetryListener);
      }
      catch (SQLException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   private void onConfigureCatalogLoading()
   {
      if(new AdditionalCatalogsController(_session).isOk())
      {
         refreshSchemaAndTree();
         updateAdditionalCatalogsIcon();
      }
   }

   private void onConnectionPropertyChanged(PropertyChangeEvent evt)
   {
      try
      {
         final String propName = evt.getPropertyName();
         if (propName == null || propName.equals(ISQLConnection.IPropertyNames.CATALOG))
         {
            final ISQLConnection conn = _session.getSQLConnection();
            if (!StringUtils.equals(conn.getCatalog(), getSelectedCatalog()))
            {
               setSelectedCatalog(conn.getCatalog());
            }
         }
      }
      catch (SQLException e)
      {
         s_log.error("Error processing Property ChangeEvent", e);
      }
   }

   public void onCatalogSelected()
   {
      String selectedCatalog = getSelectedCatalog();
      if (selectedCatalog != null)
      {
         try
         {
            //_session.getSQLConnection().setCatalog(selectedCatalog);
            _session.getConnectionPool().setSessionCatalog(selectedCatalog);
            refreshSchemaAndTree();
         }
         catch (Exception ex)
         {
            _session.showErrorMessage(ex);
            _refreshCatalogs();
         }
      }
   }

   private void refreshSchemaAndTree()
   {
      // Not needed here. Schema is reloaded in call api.refreshTree(true);
      //_session.getSchemaInfo().reloadAll();

      final String selectedCatalog = getSelectedCatalog();
      IObjectTreeAPI api = _session.getObjectTreeAPIOfActiveSessionWindow();
      api.refreshTree(true);

      String schema = null;
      if (DialectFactory.isMSSQLServer(_session.getSQLConnection().getSQLMetaData()))
      {
         // dbo is MSSQL's default schema.
         schema = "dbo";
      }

      ObjectTreeSearchResultFuture resultFuture =
            api.selectInObjectTree(selectedCatalog, schema, new FilterMatcher("TABLE", null), ObjectTreeFinderGoToNextResultHandle.DONT_GO_TO_NEXT_RESULT_HANDLE);
      resultFuture.addFinishedListenerOrdered(tn -> onFindFinished(tn, api));
   }

   private void onFindFinished(TreePath tn, IObjectTreeAPI api)
   {
      if (tn != null)
      {
         ObjectTreeNode[] nodes = api.getSelectedNodes();

         if (nodes.length > 0)
         {
            ObjectTreeNode tableNode = nodes[0];

            // send a tree expansion event to the object tree
            api.expandNode(tableNode);
         }
      }
   }

   public void setCatalogs(String[] catalogs, String selectedCatalog)
   {
      _catalogsPanel.catalogsCmb.removeAllItems();
      if (catalogs != null)
      {
         final Map<String, String> map = new TreeMap<String, String>();
         for (String catalog : catalogs)
         {
            if (!StringUtilities.isEmpty(catalog))
            {
               map.put(catalog, catalog);
            }
         }
         if (StringUtilities.isEmpty(selectedCatalog))
         {
            _catalogsPanel.catalogsCmb.addItem(NoCatalogPlaceHolder.s_instance);
         }
         for (String catalog : map.values())
         {
            _catalogsPanel.catalogsCmb.addItem(catalog);
         }
         if (!StringUtilities.isEmpty(selectedCatalog))
         {
            setSelectedCatalog(selectedCatalog);
         }
      }
      //_catalogsPanel.catalogsCmb.setMaximumSize(_catalogsPanel.catalogsCmb.getPreferredSize());
   }


   public void setSelectedCatalog(String selectedCatalog)
   {
      if (selectedCatalog != null)
      {
         _catalogsPanel.catalogsCmb.getModel().setSelectedItem(selectedCatalog);
      }
   }



   private void _refreshCatalogs()
   {
      init();
   }

   public String getSelectedCatalog()
   {
      if (   null == _catalogsPanel.catalogsCmb
            || false == _catalogsPanel.catalogsCmb.getSelectedItem() instanceof String) // Happens when instanceof SQLCatalogsComboBox.NoCatalogPlaceHolder. Perhaps NoCatalogPlaceHolder should be removed when it causes more trouble.
      {
         return null;
      }
      else
      {
         return (String) _catalogsPanel.catalogsCmb.getSelectedItem();
      }
   }

   public CatalogsPanel getPanel()
   {
      return _catalogsPanel;
   }

   public void refreshCatalogsPanel()
   {
      _refreshCatalogs();
      updateAdditionalCatalogsIcon();
   }

   private boolean hasAdditionalUserChosenAndExistingCatalogs()
   {
      String[] catalogs = CatalogsPanelUtil.getCatalogsIfSupportedElseNull(_session);

      if(null == catalogs)
      {
         return false;
      }

      List<String> lowerCaseCatalogsInDB = List.of(catalogs).stream().map(s -> StringUtils.toRootLowerCase(s)).toList();

      List<String> additionalUserChosenCatalogs = Main.getApplication().getCatalogLoadModelManager().getAliasCatalogLoadModelJsonBean(_session.getAlias()).getAdditionalUserChosenCatalogs();

      boolean ret = false;
      ArrayList<String> catalogsToRemove = new ArrayList<>();

      for(String additionalUserChosenCatalog : additionalUserChosenCatalogs)
      {
         if(lowerCaseCatalogsInDB.contains(StringUtils.toRootLowerCase(additionalUserChosenCatalog)))
         {
            ret = true;
         }
         else
         {
            catalogsToRemove.add(additionalUserChosenCatalog);
         }
      }

      Main.getApplication().getCatalogLoadModelManager().removeCatalogs(_session.getAlias(), catalogsToRemove);

      return ret;
   }

}
