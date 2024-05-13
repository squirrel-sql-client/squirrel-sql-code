package net.sourceforge.squirrel_sql.client.session.mainpanel.rowcolandsum;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import net.sourceforge.squirrel_sql.fw.gui.ClipboardUtil;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class RowColLabelController extends Component
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(RowColLabelController.class);

   private JTextField _txtSelection;
   private JTextField _txtPosition;
   private JPanel _panel;
   private int _selectedRowCount;
   private int _selectedColumnCount;
   private int _selectedRow;
   private int _selectedColumn;

   public RowColLabelController()
   {
      _txtSelection = GUIUtils.styleTextFieldToCopyableLabel(new JTextField());
      _txtPosition = GUIUtils.styleTextFieldToCopyableLabel(new JTextField());

      _panel = new JPanel(new GridLayout(2,1));

      _panel.add(_txtSelection);
      _panel.add(_txtPosition);

      MouseAdapter mouseAdapter = new MouseAdapter()
      {
         @Override
         public void mousePressed(MouseEvent e)
         {
            onMousePressedTxtFields(_txtSelection, e);
         }

         @Override
         public void mouseReleased(MouseEvent e)
         {
            onMousePressedTxtFields(_txtSelection, e);
         }
      };

      _txtSelection.addMouseListener(mouseAdapter);
      _txtPosition.addMouseListener(mouseAdapter);

   }

   private void onMousePressedTxtFields(JTextField txtField, MouseEvent e)
   {
      if(false == e.isPopupTrigger())
      {
         return;
      }

      JPopupMenu popupMenu = new JPopupMenu();
      JMenuItem menuItem;

      menuItem = new JMenuItem(s_stringMgr.getString("RowColLabelController.copy.all"));
      menuItem.addActionListener(ae -> onCopyAll());
      popupMenu.add(menuItem);

      menuItem = new JMenuItem(s_stringMgr.getString("RowColLabelController.copy.selected.rows"));
      menuItem.addActionListener(ae -> onCopySelectedRowCount());
      popupMenu.add(menuItem);

      menuItem = new JMenuItem(s_stringMgr.getString("RowColLabelController.copy.selected.cols"));
      menuItem.addActionListener(ae -> onCopySelectedCols());
      popupMenu.add(menuItem);

      menuItem = new JMenuItem(s_stringMgr.getString("RowColLabelController.copy.pos.row"));
      menuItem.addActionListener(ae -> onCopyPosRow());
      popupMenu.add(menuItem);

      menuItem = new JMenuItem(s_stringMgr.getString("RowColLabelController.copy.pos.col"));
      menuItem.addActionListener(ae -> onCopyPosCol());
      popupMenu.add(menuItem);

      popupMenu.show(txtField, e.getX(), e.getY());
   }

   private void onCopyAll()
   {
      ClipboardUtil.copyToClip(getSelectionDisplayString(_selectedRowCount, _selectedColumnCount) + "\n" + getPosDisplayString(_selectedRow, _selectedColumn));
   }

   private void onCopyPosCol()
   {
      ClipboardUtil.copyToClip("" + (_selectedColumn + 1));
   }

   private void onCopyPosRow()
   {
      ClipboardUtil.copyToClip("" + (_selectedRow + 1));
   }

   private void onCopySelectedCols()
   {
      ClipboardUtil.copyToClip("" + (_selectedColumnCount));
   }

   private void onCopySelectedRowCount()
   {
      ClipboardUtil.copyToClip("" + (_selectedRowCount));
   }

   public JPanel getPanel()
   {
      return _panel;
   }

   void onRowColSelectionChanged(int selectedRowCount, int selectedColumnCount, int selectedRow, int selectedColumn)
   {
      this._selectedRowCount = selectedRowCount;
      this._selectedColumnCount = selectedColumnCount;
      this._selectedRow = selectedRow;
      this._selectedColumn = selectedColumn;

      _txtSelection.setText(getSelectionDisplayString(selectedRowCount, selectedColumnCount));

      _txtPosition.setText(getPosDisplayString(selectedRow, selectedColumn));
   }

   private static String getPosDisplayString(int selectedRow, int selectedColumn)
   {
      return s_stringMgr.getString("SelectRowColLabelController.RowColPositionLabel", selectedRow == -1 ? "" : (selectedRow + 1), selectedColumn == -1 ? "" : (selectedColumn + 1));
   }

   private static String getSelectionDisplayString(int selectedRowCount, int selectedColumnCount)
   {
      return s_stringMgr.getString("SelectRowColLabelController.RowColSelectedCountLabel", selectedRowCount, selectedColumnCount);
   }
}
