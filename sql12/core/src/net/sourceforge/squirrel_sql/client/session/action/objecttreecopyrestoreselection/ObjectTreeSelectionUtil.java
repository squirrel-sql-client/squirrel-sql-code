package net.sourceforge.squirrel_sql.client.session.action.objecttreecopyrestoreselection;

import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.JsonMarshalUtil;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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

         objectTreePathSelection.setTypeName(selectedNode.getDatabaseObjectType().getName());
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
      HashMap<String, ObjectTreePathSelection> selectedNames_objectTreePathSelection = new HashMap<>();
      for (ObjectTreePathSelection objectTreePathSelection : objectTreeSelection.getObjectTreePathSelections())
      {
         List<String> simpleNamePath = objectTreePathSelection.getSimpleNamePath();

         if (!simpleNamePath.isEmpty())
         {
            selectedNames_objectTreePathSelection.put(simpleNamePath.get(simpleNamePath.size() - 1).toLowerCase(), objectTreePathSelection);
         }
      }

      applySelections(tree, node, selectedNames_objectTreePathSelection);
   }

   private static void applySelections(IObjectTreeAPI tree, ObjectTreeNode curNode, HashMap<String, ObjectTreePathSelection> selectedNames_objectTreePathSelection)
   {
      if(isNodeInSelection(curNode, selectedNames_objectTreePathSelection))
      {
         if (curNode.getParent() instanceof ObjectTreeNode)
         {
            tree.getObjectTree().expandPath(new TreePath(((ObjectTreeNode)curNode.getParent()).getPath()));
         }
         tree.getObjectTree().addSelectionPath(new TreePath(curNode.getPath()));
      }

      tree.getObjectTree().expandNode(curNode);

      for (int i = 0; i < curNode.getChildCount(); i++)
      {
         TreeNode child = curNode.getChildAt(i);
         if (false == child instanceof ObjectTreeNode)
         {
            s_log.error("ObjectTreeNode \"" + child + "\" is not an instance of ObjectTreeNode but of " + (null == child ? "<null>" : child.getClass().getName()));
            continue;
         }
         applySelections(tree, (ObjectTreeNode) child, selectedNames_objectTreePathSelection);
      }

   }

   private static boolean isNodeInSelection(ObjectTreeNode curNode, HashMap<String, ObjectTreePathSelection> selectedNames_objectTreePathSelection)
   {
      IDatabaseObjectInfo databaseObjectInfo = curNode.getDatabaseObjectInfo();

      return   selectedNames_objectTreePathSelection.containsKey(databaseObjectInfo.getSimpleName().toLowerCase())
            && databaseObjectInfo.getDatabaseObjectType().getName().equals(selectedNames_objectTreePathSelection.get(databaseObjectInfo.getSimpleName().toLowerCase()).getTypeName());
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
