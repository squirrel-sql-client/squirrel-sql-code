package net.sourceforge.squirrel_sql.client.session.mainpanel.rowcolandsum;

import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewer;
import net.sourceforge.squirrel_sql.fw.datasetviewer.RowColSelectedCountListener;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.*;

public class RowColLabelController extends Component
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(RowColLabelController.class);

   private JLabel _lblSelection;
   private JLabel _lblPosition;
   private JPanel _panel;

   public RowColLabelController()
   {
      _lblSelection = new JLabel();
      _lblPosition = new JLabel();

      _panel = new JPanel(new GridLayout(2,1));

      _panel.add(_lblSelection);
      _panel.add(_lblPosition);

   }

   public JPanel getPanel()
   {
      return _panel;
   }

   void onRowColSelectionChanged(int selectedRowCount, int selectedColumnCount, int selectedRow, int selectedColumn)
   {
      _lblSelection.setText(s_stringMgr.getString("SelectRowColLabelController.RowColSelectedCountLabel", selectedRowCount, selectedColumnCount));

      _lblPosition.setText(s_stringMgr.getString("SelectRowColLabelController.RowColPositionLabel", selectedRow == -1 ? "" : (selectedRow + 1), selectedColumn == -1 ? "" : (selectedColumn + 1)));
   }
}
