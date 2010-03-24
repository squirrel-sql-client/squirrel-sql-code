package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.MappedClassInfo;
import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.PropertyInfo;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: gerd
 * Date: 22.02.2010
 * Time: 23:02:54
 * To change this template use File | Settings | File Templates.
 */
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

   static void addSingleResultKids(DefaultMutableTreeNode parent, SingleResult singleResult, Class persistenCollectionClass, ArrayList<MappedClassInfo> allMappedClassInfos)
   {
      PropertyInfo[] propertyInfos = singleResult.getMappedClassInfo().getAttributes();

      for (PropertyInfo propertyInfo : propertyInfos)
      {
         String propertyName = propertyInfo.getHibernatePropertyInfo().getPropertyName();
         HibernatePropertyReader hpr = new HibernatePropertyReader(propertyName, singleResult.getObject());

         Object value = hpr.getValue();
         if (null != value && persistenCollectionClass.isAssignableFrom(value.getClass()))
         {
            parent.add(new DefaultMutableTreeNode(new PersistentCollectionResult(value, propertyInfo, allMappedClassInfos)));
         }
         else if (null != findMappedClassInfo(hpr.getTypeName(), allMappedClassInfos, true))
         {
            SingleResult buf = new SingleResult(hpr.getValue(), findMappedClassInfo(hpr.getTypeName(), allMappedClassInfos, false));
            parent.add(new DefaultMutableTreeNode(buf));
         }
         else
         {
            parent.add(new DefaultMutableTreeNode(new PrimitiveValue(hpr, value)));
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
