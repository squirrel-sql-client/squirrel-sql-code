package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.MappedClassInfo;
import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.PropertyInfo;
import net.sourceforge.squirrel_sql.plugins.hibernate.server.ObjectSubstitute;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.ArrayList;
import java.util.Collection;

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
            parent.add(new DefaultMutableTreeNode(new PersistentCollectionResult(hpr, propertyInfo, allMappedClassInfos)));
         }
         else if (null != findMappedClassInfo(hpr.getTypeName(), allMappedClassInfos, true))
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
}
