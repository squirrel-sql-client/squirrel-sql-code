package net.sourceforge.squirrel_sql.client.session.action.findcolums;

import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.awt.Window;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class FindColumnsScope
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(FindColumnsScope.class);

   private IObjectTreeAPI _objectTreeAPI;
   private ISession _session;

   private Window _owningWindow;
   private List<ITableInfo> _tablesInGraph;
   private String _findDialogTitle;

   public FindColumnsScope(List<ITableInfo> tablesInGraph, ISession session, Window owningWindow, String findDialogTitle)
   {
      _owningWindow = owningWindow;
      _tablesInGraph = tablesInGraph;
      _session = session;
      _findDialogTitle = findDialogTitle;
   }

   public FindColumnsScope(IObjectTreeAPI objectTreeAPI, ISession session)
   {
      _objectTreeAPI = objectTreeAPI;
      _session = session;
      _owningWindow = GUIUtils.getOwningWindow(_session.getSessionPanel());
      _findDialogTitle = s_stringMgr.getString("FindColumnsScope.dialog.title.unspecified");

      if(null != _objectTreeAPI)
      {
         _session = _objectTreeAPI.getSession();
         _owningWindow = GUIUtils.getOwningWindow(_objectTreeAPI.getObjectTree());

         _findDialogTitle = createDialogTitle();
      }
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
}
