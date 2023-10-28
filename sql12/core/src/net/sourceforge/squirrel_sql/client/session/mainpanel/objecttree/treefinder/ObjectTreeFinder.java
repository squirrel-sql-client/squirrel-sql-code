package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.treefinder;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTree;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.client.session.schemainfo.FilterMatcher;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.tree.TreePath;

public class ObjectTreeFinder
{
   private static ILogger s_log = LoggerController.createLogger(ObjectTreeFinder.class);
   private final ObjectTree _objectTree;

   public ObjectTreeFinder(ObjectTree objectTree)
   {
      _objectTree = objectTree;
   }

   public ObjectTreeSearchResultFuture findPathToDbInfo(String catalog, String schema, FilterMatcher objectMatcher, ObjectTreeNode startNode, boolean useExpanders, ObjectTreeFinderGoToNextResultHandle goToNextResultHandle)
   {
      ObjectTreeSearchResultFutureIntern toFill = new ObjectTreeSearchResultFutureIntern(_objectTree.getSession());
      _getPathToDbInfo(catalog, schema, objectMatcher, startNode, useExpanders, toFill, goToNextResultHandle);
      return toFill;
   }

   private void _getPathToDbInfo(String catalog, String schema, FilterMatcher objectMatcher, ObjectTreeNode startNode, boolean useExpanders, ObjectTreeSearchResultFutureIntern toFill, ObjectTreeFinderGoToNextResultHandle goToNextResultHandle)
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
            toFill.addTask(getExpandDescr(startNode), () -> _objectTree.expandNode(startNode));
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

   private void recurseChildren(String catalog, String schema, FilterMatcher objectMatcher, ObjectTreeNode startNode, boolean useExpanders, ObjectTreeSearchResultFutureIntern toFill, ObjectTreeFinderGoToNextResultHandle goToNextResultHandle)
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
