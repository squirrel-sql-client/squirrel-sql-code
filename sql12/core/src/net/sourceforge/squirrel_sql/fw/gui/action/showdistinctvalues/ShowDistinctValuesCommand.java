package net.sourceforge.squirrel_sql.fw.gui.action.showdistinctvalues;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;

import java.awt.Window;

public class ShowDistinctValuesCommand
{
   private DataSetViewerTable _table;
   private Window _owningFrame;
   private ISession _session;

   public ShowDistinctValuesCommand(DataSetViewerTable table, Window owningWindow, ISession session)
   {
      _table = table;
      _owningFrame = owningWindow;
      _session = session;
   }

   public void execute()
   {
      new ShowDistinctValuesCtrl(_owningFrame, _table);
   }
}
