package net.sourceforge.squirrel_sql.client.session.mainpanel;

import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewer;
import net.sourceforge.squirrel_sql.fw.datasetviewer.RowColSelectedCountListener;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;

public class SelectRowColLabelController extends Component
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SelectRowColLabelController.class);


   private JLabel _lbl;

   public SelectRowColLabelController()
   {
      _lbl = new JLabel();
   }

   public JLabel getLabel()
   {
      return _lbl;
   }

   public void setDataSetViewer(IDataSetViewer dataSetViewer)
   {
      dataSetViewer.setRowColSelectedCountListener(new RowColSelectedCountListener(){
         @Override
         public void rowColSelectedCountChanged(int selectedRowCount, int selectedColumnCount)
         {
            onRowColSelectedCountChanged(selectedRowCount, selectedColumnCount);
         }
      });

   }

   private void onRowColSelectedCountChanged(int selectedRowCount, int selectedColumnCount)
   {
      s_stringMgr.getString("SelectRowColLabelController.RowColSelectedCountLabel", selectedRowCount, selectedColumnCount);
      _lbl.setText("Rows: " + selectedRowCount + ", Cols: " + selectedColumnCount);
   }
}
