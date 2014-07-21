package net.sourceforge.squirrel_sql.fw.gui.action.showreferences;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultMetaDataTable;
import net.sourceforge.squirrel_sql.fw.gui.action.InStatColumnInfo;

import java.util.ArrayList;

/**
 * Created by gerd on 21.07.14.
 */
public class RootTable
{
   private final ResultMetaDataTable _globalDbTable;
   private final ArrayList<InStatColumnInfo> _inStatColumnInfos;

   public RootTable(ResultMetaDataTable globalDbTable, ArrayList<InStatColumnInfo> inStatColumnInfos)
   {
      _globalDbTable = globalDbTable;
      _inStatColumnInfos = inStatColumnInfos;
   }

   public ResultMetaDataTable getGlobalDbTable()
   {
      return _globalDbTable;
   }

   public ArrayList<InStatColumnInfo> getInStatColumnInfos()
   {
      return _inStatColumnInfos;
   }

   @Override
   public String toString()
   {
      return _globalDbTable.getQualifiedName();
   }
}
