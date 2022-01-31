package net.sourceforge.squirrel_sql.client.session.objecttreesearch;

import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionUtils;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.treefinder.ObjectTreeFinderGoToNextResultHandle;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.treefinder.ObjectTreeSearchResultFuture;
import net.sourceforge.squirrel_sql.client.session.schemainfo.FilterMatcher;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.JOptionPane;
import javax.swing.tree.TreePath;


/**
 * Helps to locate Objects in the Object tree of a Session main window or an ObjectTreeInternalFrame
 */
public class ObjectTreeSearch
{
   private static final ILogger s_log = LoggerController.createLogger(ObjectTreeSearch.class);

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ObjectTreeSearch.class);

   /**
    * View the Object at cursor in the Object Tree
    *
    * @param	evt		Event being executed.
    */
   public void viewObjectInObjectTree(String objectName, ISession session)
   {


      if(false == session.getActiveSessionWindow() instanceof SessionInternalFrame &&
         false == session.getActiveSessionWindow() instanceof ObjectTreeInternalFrame)
      {
         return;
      }


      IObjectTreeAPI objectTreeAPI = session.getObjectTreeAPIOfActiveSessionWindow();

      viewInObjectTree(objectName, objectTreeAPI);

   }

   public void viewInObjectTree(String objectName, IObjectTreeAPI objectTreeAPI)
   {
      ObjectTreeSearchPartitions partitions = getSearchPartitions(objectName, objectTreeAPI.getSession());
      if (partitions.size() == 0)
      {
         return;
      }

      _viewInObjectTree(partitions, objectTreeAPI, true, ObjectTreeFinderGoToNextResultHandle.DONT_GO_TO_NEXT_RESULT_HANDLE);
   }

   public void viewObjectInObjectTree(String objectName, IObjectTreeAPI objectTreeAPI, ObjectTreeFinderGoToNextResultHandle goToNextResultHandle)
   {
      ObjectTreeSearchPartitions partitions = getSearchPartitions(objectName, objectTreeAPI.getSession());
      if (partitions.size() == 0)
      {
         return;
      }

      _viewInObjectTree(partitions, objectTreeAPI, false, goToNextResultHandle);

   }

   private void _viewInObjectTree(ObjectTreeSearchPartitions partitions, IObjectTreeAPI objectTreeAPI, boolean selectMainObjectTreeIfFound, ObjectTreeFinderGoToNextResultHandle goToNextResultHandle)
   {
      if(false == partitions.hasNext())
      {
         return;
      }

      tryFindMatchForNextPartition(partitions, objectTreeAPI, selectMainObjectTreeIfFound, null, goToNextResultHandle);
   }

   private void tryFindMatchForNextPartition(ObjectTreeSearchPartitions partitions, IObjectTreeAPI objectTreeAPI, boolean selectMainObjectTreeIfFound, TreePath findResult, ObjectTreeFinderGoToNextResultHandle goToNextResultHandle)
   {
      if(null != findResult)
      {
         if (selectMainObjectTreeIfFound)
         {
            objectTreeAPI.getSession().selectMainTab(ISession.IMainPanelTabIndexes.OBJECT_TREE_TAB);
         }
      }
      else if(partitions.hasNext())
      {
         ObjectTreeSearchPartition partition;
         partition = partitions.next();
         ObjectTreeSearchResultFuture resultFuture = objectTreeAPI.selectInObjectTree(partition.getCatalog(), partition.getSchema(), new FilterMatcher(partition.getObject(), null), goToNextResultHandle);
         resultFuture.addFinishedListenerOrdered(tn -> tryFindMatchForNextPartition(partitions, objectTreeAPI, selectMainObjectTreeIfFound, tn, goToNextResultHandle));
      }
      else
      {
         if(goToNextResultHandle.hasPreviousResults())
         {
            String msg = s_stringMgr.getString("ObjectTreeSearch.message.no.more.objects.found",partitions.getSearchString());
            JOptionPane.showMessageDialog(SessionUtils.getOwningFrame(objectTreeAPI.getSession()), msg);
         }
         else
         {
            String msg = s_stringMgr.getString("ObjectTreeSearch.error.objectnotfound",partitions.getSearchString());
            JOptionPane.showMessageDialog(SessionUtils.getOwningFrame(objectTreeAPI.getSession()), msg);
         }
         goToNextResultHandle.reachedEmptyResult();
      }
   }

   private ObjectTreeSearchPartitions getSearchPartitions(String objectName, ISession session)
   {
      ObjectTreeSearchPartitions ret = new ObjectTreeSearchPartitions(objectName);

      String[] splits = objectName.split("\\.");

      if(splits.length >= 3)
      {
         ret.add(splits[0], splits[1], splits[2]);
         ret.add(null, removeQuotes(splits[1]), removeQuotes(splits[2]));
         ret.add(removeQuotes(splits[1]), null, removeQuotes(splits[2])); // For databases that support catalogs but not schemas
         ret.add(null, null, splits[2]);
      }
      else if(splits.length == 2)
      {
         ret.add(null, removeQuotes(splits[0]), removeQuotes(splits[1]));
         ret.add(removeQuotes(splits[0]), null, removeQuotes(splits[1])); // For databases that support catalogs but not schemas
         ret.add(null, null, removeQuotes(splits[0]));
      }
      else if(splits.length == 1)
      {

         try
         {
            String currentSchema = session.getSQLConnection().getSchema();
            if( false == StringUtilities.isEmpty(currentSchema, true) )
            {
               ret.addFirst(null, currentSchema, removeQuotes(splits[0]));
            }
         }
         catch (Throwable e)
         {
            // By now its not yet too common to support java.sql.Connection.getSchema().
            // That's why we don't issue a warning here.
            //s_log.warn("Failed to load current schema name: " + e);
         }

         ret.add(null, null, removeQuotes(splits[0]));

      }
      return ret;
   }

   private String removeQuotes(String objectName)
   {
      String ret = objectName.trim();


      while(ret.startsWith("\"") || ret.startsWith("/"))
      {
         ret = ret.substring(1);
      }

      while(ret.endsWith("\"") || ret.endsWith("/"))
      {
          ret = ret.substring(0,ret.length()-1);
      }
      
      return ret;
   }

   public void viewInObjectTree(TreePath treePath, IObjectTreeAPI objectTreeAPI)
   {
      objectTreeAPI.selectInObjectTree(treePath);
      objectTreeAPI.getSession().selectMainTab(ISession.IMainPanelTabIndexes.OBJECT_TREE_TAB);
   }
}
