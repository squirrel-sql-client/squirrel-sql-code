package net.sourceforge.squirrel_sql.client.session.action.objecttreecopyrestoreselection;

import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.JsonMarshalUtil;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

public class ObjectTreeSelectionUtil
{
   private static final ILogger s_log = LoggerController.createLogger(ObjectTreeSelectionUtil.class);

   public static String selectionToJsonString(IObjectTreeAPI tree)
   {
      ObjectTreeSelection objectTreeSelection = selectionToObjectTreeSelection(tree);
      return JsonMarshalUtil.toJsonString(objectTreeSelection);
   }

   public static ObjectTreeSelection selectionToObjectTreeSelection(IObjectTreeAPI tree)
   {
      ObjectTreeSelection objectTreeSelection = new ObjectTreeSelection();

      for (ObjectTreeNode selectedNode : tree.getSelectedNodes())
      {
         ObjectTreePathSelection objectTreePathSelection = new ObjectTreePathSelection();
         objectTreeSelection.getObjectTreePathSelections().add(objectTreePathSelection);

         for (Object pathNodeObj : selectedNode.getPath())
         {
            if (false == pathNodeObj instanceof ObjectTreeNode)
            {
               s_log.error("ObjectTreeNode \"" + pathNodeObj + "\" is not an instance of ObjectTreeNode but of " + (null == pathNodeObj ? "<null>" : pathNodeObj.getClass().getName()));
               continue;
            }
            ObjectTreeNode objectTreeNode = (ObjectTreeNode) pathNodeObj;

            IDatabaseObjectInfo databaseObjectInfo = objectTreeNode.getDatabaseObjectInfo();
            objectTreePathSelection.getSimpleNamePath().add(databaseObjectInfo.getSimpleName());
         }
      }

      Collections.sort(objectTreeSelection.getObjectTreePathSelections(), (ps1, ps2) -> compareSelections(ps1, ps2));

      return objectTreeSelection;
   }

   private static int compareSelections(ObjectTreePathSelection ps1, ObjectTreePathSelection ps2)
   {
      for (int i = 0; i < Math.min(ps1.getSimpleNamePath().size(), ps2.getSimpleNamePath().size()); i++)
      {
         int res = ps1.getSimpleNamePath().get(i).compareTo(ps2.getSimpleNamePath().get(i));
         if(res != 0)
         {
            return res;
         }
      }
      return Integer.compare(ps1.getSimpleNamePath().size(), ps2.getSimpleNamePath().size());
   }


   public static void applySelection(IObjectTreeAPI tree, String objectTreeSelectionJsonString)
   {
      ObjectTreeSelection objectTreeSelection =
            JsonMarshalUtil.fromJsonString(objectTreeSelectionJsonString, ObjectTreeSelection.class);

      applySelection(tree, objectTreeSelection);
   }

   public static void applySelection(IObjectTreeAPI tree, ObjectTreeSelection objectTreeSelection)
   {
      ObjectTreeNode[] selectedNodes = tree.getSelectedNodes();

      if(0 == selectedNodes.length)
      {
         selectedNodes = new ObjectTreeNode[]{tree.getRootNode()};
      }

      tree.getObjectTree().clearSelection();

      for (ObjectTreeNode selNode : selectedNodes)
      {
         TreeNode parent = selNode.getParent();
         if(   null != parent
            && applySelectionToSiblings(selNode.getDatabaseObjectInfo().getDatabaseObjectType()))
         {
            for (int i = 0; i < parent.getChildCount(); i++)
            {
               TreeNode child = parent.getChildAt(i);
               if (false == child instanceof ObjectTreeNode)
               {
                  s_log.error("ObjectTreeNode \"" + child + "\" is not an instance of ObjectTreeNode but of " + (null == child ? "<null>" : child.getClass().getName()));
                  continue;
               }
               applyObjectTreeSelectionFromThisNodeOn(tree, (ObjectTreeNode) child, objectTreeSelection);
            }
         }

         applyObjectTreeSelectionFromThisNodeOn(tree, selNode, objectTreeSelection);

      }
   }

   private static void applyObjectTreeSelectionFromThisNodeOn(IObjectTreeAPI tree, ObjectTreeNode node, ObjectTreeSelection objectTreeSelection)
   {
      HashSet<String> selectedNames = new HashSet<>();
      for (ObjectTreePathSelection objectTreePathSelection : objectTreeSelection.getObjectTreePathSelections())
      {
         List<String> simpleNamePath = objectTreePathSelection.getSimpleNamePath();

         if (!simpleNamePath.isEmpty())
         {
            selectedNames.add(simpleNamePath.get(simpleNamePath.size() - 1));
         }
      }

      applySelections(tree, node, selectedNames);
   }

   private static void applySelections(IObjectTreeAPI tree, ObjectTreeNode curNode, HashSet<String> selectedNames)
   {
      if(selectedNames.contains(curNode.getDatabaseObjectInfo().getSimpleName().toLowerCase()))
      {
         if (curNode.getParent() instanceof ObjectTreeNode)
         {
            tree.getObjectTree().expandPath(new TreePath(((ObjectTreeNode)curNode.getParent()).getPath()));
         }
         tree.getObjectTree().addSelectionPath(new TreePath(curNode.getPath()));
      }

      //Stream.of(curNode.getExpanders()).forEach(exp -> createChildren(tree, curNode, exp));

      INodeExpander[] expanders = tree.getObjectTree().getObjectTreeModel().getExpanders(curNode.getDatabaseObjectType());
      Stream.of(expanders).forEach(exp -> initChildren(tree, curNode, exp));

      for (int i = 0; i < curNode.getChildCount(); i++)
      {
         TreeNode child = curNode.getChildAt(i);
         if (false == child instanceof ObjectTreeNode)
         {
            s_log.error("ObjectTreeNode \"" + child + "\" is not an instance of ObjectTreeNode but of " + (null == child ? "<null>" : child.getClass().getName()));
            continue;
         }
         applySelections(tree, (ObjectTreeNode) child, selectedNames);
      }

   }

   private static void initChildren(IObjectTreeAPI tree, ObjectTreeNode curNode, INodeExpander exp)
   {
      try
      {
         if(0 < curNode.getChildCount() || curNode.hasNoChildrenFoundWithExpander() || false == curNode.getAllowsChildren())
         {
            return;
         }

         List<ObjectTreeNode> children = exp.createChildren(tree.getSession(), curNode);
         if(children.isEmpty())
         {
            curNode.setNoChildrenFoundWithExpander(true);
         }
         else
         {
            for (int j = 0; j < children.size(); j++)
            {
               ObjectTreeNode newChild = children.get(j);
               if ( 0 == tree.getObjectTree().getObjectTreeModel().getExpanders(newChild.getDatabaseObjectType()).length)
               {
                  newChild.setAllowsChildren(false);
               }
               else
               {
                  newChild.setAllowsChildren(true);
               }
               curNode.add(newChild);
            }
         }
      }
      catch (SQLException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   private static boolean applySelectionToSiblings(DatabaseObjectType databaseObjectType)
   {
      return false == (
               DatabaseObjectType.SESSION.equals(databaseObjectType)
            || DatabaseObjectType.CATALOG.equals(databaseObjectType)
            || DatabaseObjectType.SCHEMA.equals(databaseObjectType)
            || databaseObjectType.isContainerNode());
   }

   public static String generateSelectionName(ObjectTreeSelection objectTreeSelection)
   {
      String name = String.join("/", objectTreeSelection.getObjectTreePathSelections().get(0).getSimpleNamePath());

      if(1  < objectTreeSelection.getObjectTreePathSelections().size())
      {
         name += " ...";
      }
      return name;
   }
}
