package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.util.JsonMarshalUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ExcelTabOrFileNamesDao
{
   public static void addOrReplaceSavedName(String name, List<String> excelTabOrFileNames)
   {
      List<NamedExcelTabOrFileNamesJsonBean> beans = readBeanList();

      for (NamedExcelTabOrFileNamesJsonBean bean : beans)
      {
         if(StringUtils.containsIgnoreCase(bean.getName(), name))
         {
            bean.setName(name);
            bean.setExcelTabOrFileNames(excelTabOrFileNames);
            writeBeanList(beans);
            return;
         }
      }

      final NamedExcelTabOrFileNamesJsonBean bean = new NamedExcelTabOrFileNamesJsonBean();
      bean.setName(name);
      bean.setExcelTabOrFileNames(excelTabOrFileNames);
      beans.add(0, bean);

      while (10 < beans.size())
      {
         beans.remove(beans.size() - 1);
      }

      writeBeanList(beans);
   }

   private static void writeBeanList(List<NamedExcelTabOrFileNamesJsonBean> beans)
   {

      ExcelTabOrFileNamesContainerJsonBean containerBean = new ExcelTabOrFileNamesContainerJsonBean();
      containerBean.setNamedExcelTabOrFileNamesJsonBeans(beans);

      final File file = new ApplicationFiles().getExcelTabOrFileNamesJsonBeanFile();
      JsonMarshalUtil.writeObjectToFile(file, containerBean);
   }

   private static List<NamedExcelTabOrFileNamesJsonBean> readBeanList()
   {
      ExcelTabOrFileNamesContainerJsonBean containerBean = new ExcelTabOrFileNamesContainerJsonBean();

      final File file = new ApplicationFiles().getExcelTabOrFileNamesJsonBeanFile();
      if(file.exists())
      {
         containerBean = JsonMarshalUtil.readObjectFromFile(file, ExcelTabOrFileNamesContainerJsonBean.class);
      }

      List<NamedExcelTabOrFileNamesJsonBean> beans =   containerBean.getNamedExcelTabOrFileNamesJsonBeans();
      return beans;
   }

   public static List<String> getSavedNames()
   {
      return readBeanList().stream().map(b -> b.getName()).collect(Collectors.toList());
   }

   public static List<String> getExcelTabOrFileNames(String savedName)
   {
      final Optional<NamedExcelTabOrFileNamesJsonBean> first
            = readBeanList().stream().filter(b -> StringUtils.containsIgnoreCase(b.getName(), savedName)).findFirst();

      if(first.isEmpty())
      {
         throw new IllegalStateException("Saved name \"" + savedName + "\" does not exist");
      }

      return first.get().getExcelTabOrFileNames();
   }
}
