package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

import java.util.ArrayList;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.MappedClassInfo;
import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.PropertyInfo;
import net.sourceforge.squirrel_sql.plugins.hibernate.server.ObjectSubstitute;

public class ViewObjectsUtil
{
   public static MappedClassInfo findMappedClassInfo(String className, ArrayList<MappedClassInfo> mappedClassInfos, boolean allowNotFound)
   {
      for (MappedClassInfo mappedClassInfo : mappedClassInfos)
      {
         if(mappedClassInfo.getClassName().equals(className))
         {
            return mappedClassInfo;
         }
      }

      if(allowNotFound)
      {
         return null;

      }

      throw new IllegalArgumentException("No mapping information found for class: " + className);
   }

   static void addSingleResultKids(DefaultMutableTreeNode parent, SingleResult singleResult, ArrayList<MappedClassInfo> allMappedClassInfos)
   {
      PropertyInfo[] propertyInfos = singleResult.getMappedClassInfo().getAttributes();

      for (PropertyInfo propertyInfo : propertyInfos)
      {
         String propertyName = propertyInfo.getHibernatePropertyInfo().getPropertyName();
         HibernatePropertyReader hpr = new HibernatePropertyReader(propertyName, singleResult.getObject());

         if (hpr.isPersistenCollection())
         {
            if(null == ViewObjectsUtil.findMappedClassInfo(hpr.getTypeName(), allMappedClassInfos, true))
            {
                  // Happens when hpr is a mapped basic type (e.g. Integer) collection
               parent.add(new DefaultMutableTreeNode(new PrimitiveCollection(hpr)));
            }
            else
            {
               parent.add(new DefaultMutableTreeNode(new PersistentCollectionResult(hpr, propertyInfo, allMappedClassInfos)));

            }
         }
         else if (isMappedType(allMappedClassInfos, hpr))
         {
            SingleResult buf = new SingleResult(propertyName, (ObjectSubstitute) hpr.getValue(), findMappedClassInfo(hpr.getTypeName(), allMappedClassInfos, false));
            parent.add(new DefaultMutableTreeNode(buf));
         }
         else
         {
            parent.add(new DefaultMutableTreeNode(new PrimitiveValue(hpr)));
         }
      }
   }

   private static boolean isMappedType(ArrayList<MappedClassInfo> allMappedClassInfos, HibernatePropertyReader hpr)
   {
      MappedClassInfo mappedClassInfo = findMappedClassInfo(hpr.getTypeName(), allMappedClassInfos, true);
      return null != mappedClassInfo && false == mappedClassInfo.isPlainValueArray();
   }

   static void nodeStructurChanged(DefaultMutableTreeNode node, JTree tree)
   {
      ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(node);
   }

   static void addPersistentCollectionKids(DefaultMutableTreeNode kidNode)
   {
      PersistentCollectionResult pcr = (PersistentCollectionResult) kidNode.getUserObject();
      ArrayList<SingleResult> singleResults = pcr.getKidResults();

      for (SingleResult singleResult : singleResults)
      {
         kidNode.add(new DefaultMutableTreeNode(singleResult));
      }
   }

   public static String getPrimitivePersistentCollectionString(HibernatePropertyReader hpr)
   {
      return "" + hpr.getPersistentCollection();
   }
}
