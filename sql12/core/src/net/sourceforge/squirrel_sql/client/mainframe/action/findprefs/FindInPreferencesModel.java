package net.sourceforge.squirrel_sql.client.mainframe.action.findprefs;

import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import org.apache.commons.lang3.StringUtils;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FindInPreferencesModel
{
   private TreeMap<List<String>, List<PrefComponentInfo>> _componentInfoByPath;

   public FindInPreferencesModel(TreeMap<List<String>, List<PrefComponentInfo>> componentInfoByPath)
   {
      _componentInfoByPath = componentInfoByPath;
   }

   public DefaultMutableTreeNode createFilteredTreeNodes(String filterText)
   {
      DefaultMutableTreeNode root = new DefaultMutableTreeNode("");

      for (Map.Entry<List<String>, List<PrefComponentInfo>> entry : _componentInfoByPath.entrySet())
      {
         if(false == matches(entry.getKey(), filterText))
         {
            continue;
         }

         DefaultMutableTreeNode parent = root;
         for (String nodeName : entry.getKey())
         {
            boolean found = false;
            for (int i = 0; i < parent.getChildCount(); i++)
            {
               DefaultMutableTreeNode child = (DefaultMutableTreeNode) parent.getChildAt(i);
               if(nodeName.equals(child.getUserObject()))
               {
                  parent = child;
                  found = true;
                  break;
               }
            }
            if(false == found)
            {
               DefaultMutableTreeNode child = new DefaultMutableTreeNode(nodeName);
               parent.add(child);
               parent = child;
            }
         }
      }

      return root;
   }

   private boolean matches(List<String> path, String filterText)
   {
      if(StringUtilities.isEmpty(filterText, true))
      {
         return true;
      }

      filterText = filterText.trim();
      for (String pathEntry : path)
      {
         if(StringUtils.containsIgnoreCase(pathEntry, filterText))
         {
            return true;
         }
      }
      return false;
   }

   public List<String> treeNodeToComponentPath(DefaultMutableTreeNode node)
   {
      final Object[] pathIncludingRoot = node.getUserObjectPath();
      List<String> path = Stream.of(pathIncludingRoot).map(o -> (String)o).collect(Collectors.toList()).subList(1, pathIncludingRoot.length);
      return path;
   }

   public PrefComponentInfo treeNodeToComponentInfo(DefaultMutableTreeNode node)
   {
      final List<String> path = treeNodeToComponentPath(node);
      return _componentInfoByPath.get(treeNodeToComponentPath(node)).get(0);
   }
}
