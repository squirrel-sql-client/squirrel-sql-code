package net.sourceforge.squirrel_sql.plugins.hibernate.configuration;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.DefaultListModel;

import net.sourceforge.squirrel_sql.plugins.hibernate.server.ClassPathItem;
import net.sourceforge.squirrel_sql.plugins.hibernate.server.ClassPathUtil;

public class ClassPathItemListModel extends DefaultListModel<ClassPathItem>
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
      ClassPathItem item = createClassPathItem(path, jarDir);
      super.addElement(item);
   }

   private ClassPathItem createClassPathItem(String path, boolean jarDir)
   {
      ClassPathItem item = new ClassPathItem();
      item.setPath(path);
      item.setJarDir(jarDir);
      return item;
   }


   @Override
   public void addElement(ClassPathItem obj)
   {
      throw new UnsupportedOperationException("Use addJar() or addJarDir()");
   }

   public void addItem(ClassPathItem item)
   {
      super.addElement(item);
   }

   public int[] replaceByJarDirs(List<ClassPathItem> toReplaceItems, File[] dirs)
   {
      List<ClassPathItem> newItems = Arrays.stream(dirs).map(f -> createClassPathItem(f.getPath(), true)).collect(Collectors.toList());

      return replaceItems(toReplaceItems, newItems);
   }

   public int[] replaceByJars(List<ClassPathItem> toReplaceItems, File[] entries)
   {
      List<ClassPathItem> newItems = Arrays.stream(entries).map(f -> createClassPathItem(f.getPath(), false)).collect(Collectors.toList());

      return replaceItems(toReplaceItems, newItems);
   }

   private int[] replaceItems(List<ClassPathItem> toReplaceItems, List<ClassPathItem> newItems)
   {
      int insertIndex = indexOf(toReplaceItems.get(0));

      toReplaceItems.forEach(i -> remove(indexOf(i)));

      for (int i = newItems.size() - 1; i > -1; i--)
      {
         ClassPathItem classPathItem = newItems.get(i);
         insertElementAt(classPathItem, insertIndex);
      }
      return indexesOf(newItems);
   }

   private int[] indexesOf(List<ClassPathItem> items)
   {
      return items.stream().mapToInt(i -> indexOf(i)).toArray();
   }
}
