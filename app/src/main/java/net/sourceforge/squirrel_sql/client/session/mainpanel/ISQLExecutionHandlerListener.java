package net.sourceforge.squirrel_sql.client.session.mainpanel;

import net.sourceforge.squirrel_sql.client.session.SQLExecutionInfo;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableTableModel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetMetaDataDataSet;

import java.util.ArrayList;

public interface ISQLExecutionHandlerListener
{
   void addResultsTab(SQLExecutionInfo info,
                      ResultSetDataSet rsds,
                      ResultSetMetaDataDataSet rsmdds,
                      IDataSetUpdateableTableModel model,
                      IResultTab resultTabToReplace);


   void removeCancelPanel(CancelPanelCtrl cancelPanelCtrl, IResultTab resultTabToReplace);

   void setCancelPanel(CancelPanelCtrl cancelPanelCtrl);

   void displayErrors(ArrayList<String> sqlExecErrorMsgs, String lastExecutedStatement);
}
