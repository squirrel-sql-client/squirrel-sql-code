package net.sourceforge.squirrel_sql.fw.gui.action.showdistinctvalues;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableModel;

import javax.swing.JFrame;

public class ShowDistinctValuesCommand
{
   private DataSetViewerTable _table;
   private JFrame _owningFrame;
   private ISession _session;

   public ShowDistinctValuesCommand(DataSetViewerTable table, IDataSetUpdateableModel updateableModel, JFrame owningFrame, ISession session)
   {
      _table = table;
      _owningFrame = owningFrame;
      _session = session;
   }

   public void execute()
   {
      new ShowDistinctValuesCtrl(_owningFrame, _table, _session);
   }
}
