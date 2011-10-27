package net.sourceforge.squirrel_sql.plugins.hibernate.configuration;

import net.sourceforge.squirrel_sql.plugins.hibernate.server.ClassPathItem;
import net.sourceforge.squirrel_sql.plugins.hibernate.server.ClassPathUtil;

import javax.swing.*;

public class ClassPathItemListModel extends DefaultListModel
{
   public String[] getClassPathArray()
   {
      ClassPathItem[] classPathItems = getItemArray();
      return ClassPathUtil.classPathAsStringArray(classPathItems);
   }

   private ClassPathItem[] getItemArray()
   {
      ClassPathItem[] ret = new ClassPathItem[getSize()];

      for (int i = 0; i < getSize(); i++)
      {
         ret[i] = (ClassPathItem) get(i);
      }

      return ret;
   }

   public ClassPathItem getClassPathItemAt(int i)
   {
      return (ClassPathItem) get(i);
   }

   public void addJar(String path)
   {
      _addEntry(path, false);
   }

   public void addJarDir(String path)
   {
      _addEntry(path, true);
   }

   private void _addEntry(String path, boolean jarDir)
   {
      ClassPathItem item = new ClassPathItem();
      item.setPath(path);
      item.setJarDir(jarDir);
      super.addElement(item);
   }


   @Override
   public void addElement(Object obj)
   {
      throw new UnsupportedOperationException("Use addJar() or addJarDir()");
   }

   public void addItem(ClassPathItem item)
   {
      super.addElement(item);
   }
}
