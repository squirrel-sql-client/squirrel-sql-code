package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import java.util.ArrayList;
import java.util.List;

public class NamedExcelTabOrFileNamesJsonBean
{
   private String _name;
   private List<String> _excelTabOrFileNames = new ArrayList<>();

   public String getName()
   {
      return _name;
   }

   public void setName(String name)
   {
      _name = name;
   }

   public void setExcelTabOrFileNames(List<String> excelTabOrFileNames)
   {
      _excelTabOrFileNames = excelTabOrFileNames;
   }

   public List<String> getExcelTabOrFileNames()
   {
      return _excelTabOrFileNames;
   }
}
