package net.sourceforge.squirrel_sql.fw.gui.action.colorrows;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class GotoColorCommand
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(GotoColorCommand.class);


   private DataSetViewerTable _table;

   public GotoColorCommand(DataSetViewerTable table)
   {
      _table = table;
   }

   public void execute()
   {
   }
}
