package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import java.util.ArrayList;
import java.util.List;

public class ExcelTabOrFileNamesContainerJsonBean
{
   private List<NamedExcelTabOrFileNamesJsonBean> _namedExcelTabOrFileNamesJsonBeans = new ArrayList<>();

   public List<NamedExcelTabOrFileNamesJsonBean> getNamedExcelTabOrFileNamesJsonBeans()
   {
      return _namedExcelTabOrFileNamesJsonBeans;
   }

   public void setNamedExcelTabOrFileNamesJsonBeans(List<NamedExcelTabOrFileNamesJsonBean> namedExcelTabOrFileNamesJsonBeans)
   {
      _namedExcelTabOrFileNamesJsonBeans = namedExcelTabOrFileNamesJsonBeans;
   }
}
