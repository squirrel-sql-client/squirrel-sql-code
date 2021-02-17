package net.sourceforge.squirrel_sql.client.session.action.findcolums;

import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.awt.Window;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class FindColumnsScope
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(FindColumnsScope.class);

   private IObjectTreeAPI _objectTreeAPI;
   private ISession _session;

   private Window _owningWindow;
   private List<ITableInfo> _tableInfos;
   private String _findDialogTitle;

   public FindColumnsScope(List<ITableInfo> tablesInfos, ISession session, Window owningWindow, String findDialogTitle)
   {
      _owningWindow = owningWindow;
      _tableInfos = tablesInfos;
      _session = session;
      _findDialogTitle = findDialogTitle;
   }

   public FindColumnsScope(ISession session)
   {
      _session = session;
      _owningWindow = GUIUtils.getOwningWindow(_session.getSessionPanel());
      _findDialogTitle = s_stringMgr.getString("FindColumnsScope.dialog.title.unspecified");
   }

   public FindColumnsScope(IObjectTreeAPI objectTreeAPI)
   {
      _objectTreeAPI = objectTreeAPI;
      _session = objectTreeAPI.getSession();
      _owningWindow = GUIUtils.getOwningWindow(_objectTreeAPI.getObjectTree());
      _findDialogTitle = createDialogTitle();
   }

   private String createDialogTitle()
   {
      String ret;
      List<ITableInfo> selectedTables = _objectTreeAPI.getSelectedTables();

      if(0 < selectedTables.size())
      {
         ret = s_stringMgr.getString("FindColumnsScope.dialog.title.selected.tables");
      }
      else
      {
         final ObjectTreeNode[] selectedNodes = _objectTreeAPI.getSelectedNodes();

         HashSet<String> typeNames = new HashSet<>();
         ArrayList<String> nodeNames = new ArrayList<>();
         for (ObjectTreeNode selectedNode : selectedNodes)
         {
            if (   selectedNode.getDatabaseObjectType() == DatabaseObjectType.SCHEMA
                || selectedNode.getDatabaseObjectType() == DatabaseObjectType.CATALOG)
            {
               typeNames.add(selectedNode.getDatabaseObjectType().getName());
               nodeNames.add(selectedNode.getDatabaseObjectInfo().getSimpleName());
            }
         }

         if(1 == typeNames.size())
         {
            if (1 == nodeNames.size())
            {
               ret = s_stringMgr.getString("FindColumnsScope.dialog.title.selected.catalogOrSchema", typeNames.iterator().next(), nodeNames.iterator().next());
            }
            else
            {
               ret = s_stringMgr.getString("FindColumnsScope.dialog.title.selected.catalogsOrSchemas", typeNames.iterator().next());
            }
         }
         else if(1 == selectedNodes.length)
         {
            ret = s_stringMgr.getString("FindColumnsScope.dialog.title.selected.node", selectedNodes[0].getDatabaseObjectInfo().getSimpleName());
         }
         else
         {
            ret = s_stringMgr.getString("FindColumnsScope.dialog.title.selected.nodes");
         }
      }

      return ret;
   }


   public ISession getSession()
   {
      return _session;
   }

   public Window getOwningWindow()
   {
      return _owningWindow;
   }

   public String getDialogTitle()
   {
      return _findDialogTitle;
   }

   public ITableInfo[] getITableInfos()
   {
      if(null != _tableInfos)
      {
         return _tableInfos.toArray(new ITableInfo[0]);
      }

      if(null == _objectTreeAPI || containsDatabaseNode(_objectTreeAPI.getSelectedNodes()))
      {
         return _session.getSchemaInfo().getITableInfos();
      }

      ArrayList<ITableInfo> ret = new ArrayList<>();

      for (ObjectTreeNode selectedNode : _objectTreeAPI.getSelectedNodes())
      {
         if(   DatabaseObjectType.TABLE.equals(selectedNode.getDatabaseObjectType())
            || DatabaseObjectType.VIEW.equals(selectedNode.getDatabaseObjectType()) )
         {
            addUnique(ret, (ITableInfo) selectedNode.getDatabaseObjectInfo());
         }
         else if(DatabaseObjectType.SCHEMA.equals(selectedNode.getDatabaseObjectType()))
         {
            String schema = selectedNode.getDatabaseObjectInfo().getSimpleName();
            String catalog = null;

            final ObjectTreeNode parent = (ObjectTreeNode) selectedNode.getParent();
            if(DatabaseObjectType.CATALOG.equals(parent.getDatabaseObjectType()))
            {
               catalog = parent.getDatabaseObjectInfo().getSimpleName();
            }
            addAllUnique(ret, _session.getSchemaInfo().getITableInfos(catalog, schema));
         }
         else if(DatabaseObjectType.CATALOG.equals(selectedNode.getDatabaseObjectType()))
         {
            String catalog = selectedNode.getDatabaseObjectInfo().getSimpleName();
            addAllUnique(ret, _session.getSchemaInfo().getITableInfos(catalog, null));
         }
         else if(DatabaseObjectType.TABLE_TYPE_DBO.equals(selectedNode.getDatabaseObjectType()))
         {
            if (0 == selectedNode.getChildCount())
            {
               _objectTreeAPI.expandNode(selectedNode);
            }

            for (int i = 0; i < selectedNode.getChildCount(); i++)
            {
               final IDatabaseObjectInfo child = ((ObjectTreeNode) selectedNode.getChildAt(i)).getDatabaseObjectInfo();

               if(child instanceof ITableInfo)
               {
                  addUnique(ret, (ITableInfo) child);
               }
            }
            String catalog = selectedNode.getDatabaseObjectInfo().getSimpleName();
            addAllUnique(ret, _session.getSchemaInfo().getITableInfos(catalog, null));
         }
      }

      return ret.toArray(new ITableInfo[0]);

   }

   private void addAllUnique(ArrayList<ITableInfo> toFill, ITableInfo[] tableInfos)
   {
      for (ITableInfo tableInfo : tableInfos)
      {
         addUnique(toFill, tableInfo);
      }
   }

   private void addUnique(ArrayList<ITableInfo> toFill, ITableInfo databaseObjectInfo)
   {
      toFill.remove(databaseObjectInfo);
      toFill.add(databaseObjectInfo);
   }

   private boolean containsDatabaseNode(ObjectTreeNode[] selectedNodes)
   {
      return Arrays.stream(selectedNodes).anyMatch(selectedNode -> DatabaseObjectType.SESSION.equals(selectedNode.getDatabaseObjectType()));
   }
}
