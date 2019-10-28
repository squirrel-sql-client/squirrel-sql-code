package net.sourceforge.squirrel_sql.client.session.mainpanel.rowcolandsum;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class SumFunctionController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SumFunctionController.class);

   private JTextField _txtSum = GUIUtils.textFieldToCopyableLabel(new JTextField());
   private final JPanel _panel;
   private String _currentRenderedSum;
   private FunctionController _functionController;

   private DataSetViewerTable _currentTable;

   public SumFunctionController()
   {
      _panel = new JPanel(new GridLayout(2,1));
      _panel.add(_txtSum);

      JLabel lblMore = new JLabel(s_stringMgr.getString("SumFunctionController.more.html"));
      lblMore.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      _panel.add(lblMore);

      _txtSum.addMouseListener(new MouseAdapter() {

         @Override
         public void mousePressed(MouseEvent e)
         {
            onMousePressedOnSum(e);
         }

         @Override
         public void mouseReleased(MouseEvent e)
         {
            onMousePressedOnSum(e);
         }
      });

      lblMore.addMouseListener(new MouseAdapter()
      {
         @Override
         public void mouseClicked(MouseEvent e)
         {
            onMore();
         }
      });

   }

   private void onMore()
   {
      if (null == getFunctionController())
      {
         _functionController = new FunctionController(_panel);

         if(null != _currentTable)
         {
            _functionController.update(_currentTable);
         }

      }
   }

   private FunctionController getFunctionController()
   {
      if(null == _functionController || _functionController.isDisposed())
      {
         return null;
      }

      return _functionController;
   }

   private void onMousePressedOnSum(MouseEvent e)
   {
      if(false == e.isPopupTrigger())
      {
         return;
      }

      JPopupMenu popupMenu = new JPopupMenu();
      JMenuItem menuItem = new JMenuItem(s_stringMgr.getString("SumFunctionController.copy"));

      menuItem.addActionListener(ae -> onCopy());

      popupMenu.add(menuItem);

      popupMenu.show(_txtSum, e.getX(), e.getY());
   }

   private void onCopy()
   {
      if(StringUtilities.isEmpty(_txtSum.getSelectedText(), true))
      {
         if(null != _currentRenderedSum)
         {
            StringSelection ss = new StringSelection(_currentRenderedSum);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, ss);
         }
      }
      else
      {
         StringSelection ss = new StringSelection(_txtSum.getSelectedText());
         Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, ss);
      }
   }

   public JPanel getPanel()
   {
      return _panel;
   }


   void onSelectionChanged(DataSetViewerTable table)
   {
      _currentTable = table;

      ArrayList<SumAndColumn> sums;
      if(null != getFunctionController())
      {
         sums = getFunctionController().update(table);
      }
      else
      {
         sums = Calculator.calculateSums(table);
      }


      if(0 == sums.size())
      {
         _txtSum.setText(s_stringMgr.getString("SumFunctionController.no.sumable.row"));
      }
      else
      {
         SumAndColumn sumAndColumn = sums.get(0);

         String colName = sumAndColumn.getColumnDisplayDefinition().getColumnName();
         _currentRenderedSum = CellComponentFactory.getDataTypeObject(table, sumAndColumn.getColumnDisplayDefinition()).renderObject(sumAndColumn.getSum());

         _txtSum.setText("\u03A3(" + colName + ") = " + _currentRenderedSum);
      }

   }

}
