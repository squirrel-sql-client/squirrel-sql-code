package net.sourceforge.squirrel_sql.fw.gui.action;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultMetaDataTable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShowReferencesUtil
{
   public static List<ResultMetaDataTable> findTables(JTable _table, ISession _session, boolean firstLineAllColumns)
   {
      return findTables(new TableCopyInStatementCommand(_table, _session).getInStatColumnInfos(firstLineAllColumns));
   }
   public static List<ResultMetaDataTable> findTables(ArrayList<InStatColumnInfo> inStatColumnInfos)
   {
      HashMap<String, ResultMetaDataTable> ret = new HashMap<>();
      for (InStatColumnInfo inStatColumnInfo : inStatColumnInfos)
      {
         ResultMetaDataTable buf = inStatColumnInfo.getColDef().getResultMetaDataTable();

         if (null != buf)
         {
            ret.put(buf.getQualifiedName().toLowerCase(), buf);
         }
      }

      return new ArrayList<>(ret.values());
   }
}
