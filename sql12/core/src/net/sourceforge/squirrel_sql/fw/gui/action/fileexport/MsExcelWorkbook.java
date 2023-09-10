package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MsExcelWorkbook
{
   private final String _workbookFileName;

   private List<MsExcelSheet> _sheets = new ArrayList<>();

   public MsExcelWorkbook(String workbookFileName)
   {
      _workbookFileName = workbookFileName;
   }

   public void addSheet(String sheetName, String sheetSql)
   {
      _sheets.add(new MsExcelSheet(sheetName, sheetSql));
   }

   public boolean hasSheets()
   {
      return 0 < _sheets.size();
   }

   public String getWorkbookFileName()
   {
      return _workbookFileName;
   }

   public List<MsExcelSheet> getSheets()
   {
      return _sheets;
   }

   public File getWorkbookFile()
   {
      return new File(_workbookFileName);
   }

   public List<String> getSqlList()
   {
      return _sheets.stream().map(s -> s.getSheetSql()).collect(Collectors.toList());
   }
}
