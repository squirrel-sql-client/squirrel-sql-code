package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.treefinder;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeExpanders;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.client.session.schemainfo.FilterMatcher;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.tree.TreePath;
import java.util.List;

public class ObjectTreeFinder
{
   private static ILogger logger = LoggerController.createLogger(ObjectTreeFinder.class);

   private ISession _session;
   private ObjectTreeExpanders _expanders;

   public ObjectTreeFinder(ISession session, ObjectTreeExpanders expanders)
   {
      _session = session;
      _expanders = expanders;
   }

   public ObjectTreeFinderResultFuture findPathToDbInfo(String catalog, String schema, FilterMatcher objectMatcher, ObjectTreeNode startNode, boolean useExpanders, ObjectTreeFinderGoToNextResultHandle goToNextResultHandle)
   {
      ObjectTreeFinderResultFutureIntern toFill = new ObjectTreeFinderResultFutureIntern(_session);
      _getPathToDbInfo(catalog, schema, objectMatcher, startNode, useExpanders, toFill, goToNextResultHandle);
      return toFill;
   }

   private void _getPathToDbInfo(String catalog, String schema, FilterMatcher objectMatcher, ObjectTreeNode startNode, boolean useExpanders, ObjectTreeFinderResultFutureIntern toFill, ObjectTreeFinderGoToNextResultHandle goToNextResultHandle)
   {
      if(dbObjectInfoEquals(catalog, schema, objectMatcher, startNode.getDatabaseObjectInfo()) && false == goToNextResultHandle.isAPreviousResult((startNode.getPath())))
      {
         final TreePath resultTreePath = new TreePath(startNode.getPath());
         goToNextResultHandle.addPreviousResult(resultTreePath);
         toFill.setTreePath(resultTreePath);
      }
      else
      {
         if(useExpanders &&  startNode.getAllowsChildren() && 0 == startNode.getChildCount() && startNode.hasNoChildrenFoundWithExpander() == false)
         {
            INodeExpander[] expanders = _expanders.getExpanders(startNode.getDatabaseObjectType());

            for (INodeExpander expander : expanders)
            {
               toFill.addTask(getExpandDescr(startNode), () -> doExpand(startNode, expander));
            }
         }

         toFill.addTask(getRecurseDescription(startNode), () -> recurseChildren(catalog, schema, objectMatcher, startNode, useExpanders, toFill, goToNextResultHandle));
         toFill.triggerExecution();
      }
   }

   private String getRecurseDescription(ObjectTreeNode startNode)
   {
      return "Checking " + new TreePath(startNode.getPath());
   }

   private String getExpandDescr(ObjectTreeNode startNode)
   {
      return "Loading child nodes of " + new TreePath(startNode.getPath());
   }

   private void recurseChildren(String catalog, String schema, FilterMatcher objectMatcher, ObjectTreeNode startNode, boolean useExpanders, ObjectTreeFinderResultFutureIntern toFill, ObjectTreeFinderGoToNextResultHandle goToNextResultHandle)
   {
      for(int i = 0; i < startNode.getChildCount(); ++i)
      {
         _getPathToDbInfo(catalog, schema, objectMatcher, (ObjectTreeNode) startNode.getChildAt(i), useExpanders, toFill, goToNextResultHandle);
         if(null != toFill.getTreePath())
         {
            return;
         }
      }
   }

   private void doExpand(ObjectTreeNode startNode, INodeExpander expander)
   {
      try
      {
         List<ObjectTreeNode> children = expander.createChildren(startNode.getSession(), startNode);

         if (children.isEmpty())
         {
            startNode.setNoChildrenFoundWithExpander(true);
         }
         else
         {
            for (int j = 0; j < children.size(); j++)
            {
               ObjectTreeNode newChild = children.get(j);
               if (0 == _expanders.getExpanders(newChild.getDatabaseObjectType()).length)
               {
                  newChild.setAllowsChildren(false);
               }
               else
               {
                  newChild.setAllowsChildren(true);
               }

               startNode.add(newChild);
            }
         }
      }
      catch (Exception e)
      {
         String msg = "Error loading object type " +  startNode.getDatabaseObjectType() +". Error: " + e +  ". See SQuirreL Logs for stacktrace.";
         Main.getApplication().getMessageHandler().showErrorMessage(msg);
         logger.error(msg, e);
      }
   }

   private  boolean dbObjectInfoEquals(String catalog, String schema, FilterMatcher objectMatcher, IDatabaseObjectInfo doi)
   {
      if(null != catalog)
      {
         if(false == catalog.equalsIgnoreCase(doi.getCatalogName()))
         {
            return false;
         }
      }

      if(null != schema)
      {
         if(false == schema.equalsIgnoreCase(doi.getSchemaName()))
         {
            return false;
         }
      }

      if(null != objectMatcher.getMetaDataMatchString())
      {
         if(   false == objectMatcher.matches(doi.getSimpleName())
            && false == objectMatcher.getMetaDataMatchString().equalsIgnoreCase(doi.getQualifiedName()))
         {
            return false;
         }
      }

      return true;
   }
}
