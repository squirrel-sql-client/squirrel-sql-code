package net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale;

import net.sourceforge.squirrel_sql.fw.gui.SortableTableModel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


/**
 * This class only exists because the regular right mouse menu handling does not work
 * for the buttons that a DataScalePanel consists of.
 *
 * The right mouse menu works for those Buttons only if a button was clicked and then the previous navigation button was used
 * to return to the table where the button was klicked. In this case the tabel's right mouse menu doesn't work anymore.
 * That is why we implement both ways. 
 *
 * Quite a bit comlicated just to bring up a right mouse menu, isn't it. 
 *
 *  
 */
public class DataScaleTablePopupHandler
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DataScaleTablePopupHandler.class);

   private DataScaleTable _dataScaleTable;
   private JButton _buttonPopupWasOpenedAt;
   private DataScale _dataScalePopupWasOpenedAt;

   DataScaleTablePopupHandler(final DataScaleTable dataScaleTable)
   {
      _dataScaleTable = dataScaleTable;
      
      attachListenersToTable(dataScaleTable);

      attachListenersToButtons(dataScaleTable);

   }

   private void attachListenersToButtons(DataScaleTable dataScaleTable)
   {
      final JPopupMenu popup = createPopupMenu();

      for (int i = 0; i < dataScaleTable.getDataScaleTableModel().getRowCount(); i++)
      {
         final DataScale dataScale = dataScaleTable.getDataScaleTableModel().getDataScaleAt(i);

         // This initialzing call is needed to make ths scale to create the buttons.
         dataScale.getPanel();

         JButton[] buttons = dataScale.getButtons();

         for (final JButton button : buttons)
         {
            button.addMouseListener(new MouseAdapter()
            {
               @Override
               public void mousePressed(MouseEvent evt)
               {
                  maybeShowPopUpOnButton(evt, dataScale, button, popup);
               }

               @Override
               public void mouseReleased(MouseEvent evt)
               {
                  maybeShowPopUpOnButton(evt, dataScale, button, popup);
               }
            });


         }
      }
   }

   private void maybeShowPopUpOnButton(MouseEvent evt, DataScale dataScale, JButton button, JPopupMenu popup)
   {
      if (evt.isPopupTrigger())
      {
         _dataScalePopupWasOpenedAt = dataScale;
         _buttonPopupWasOpenedAt = button;
         popup.show(evt.getComponent(), evt.getX(), evt.getY());
      }
   }

   private void attachListenersToTable(DataScaleTable dataScaleTable)
   {
      final JPopupMenu popup = createPopupMenu();

      dataScaleTable.addMouseListener(new MouseAdapter()
      {
         @Override
         public void mousePressed(MouseEvent evt)
         {
            maybeShowPopUpOnTable(evt, popup);
         }

         @Override
         public void mouseReleased(MouseEvent evt)
         {
            maybeShowPopUpOnTable(evt, popup);
         }
      });
   }

   private JPopupMenu createPopupMenu()
   {
      final JPopupMenu popup = new JPopupMenu();

      JMenuItem mnuInTable = new JMenuItem(s_stringMgr.getString("DataScaleTablePopupHandler.showInTable"));
      mnuInTable.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            _dataScalePopupWasOpenedAt.showInTableSelected(_buttonPopupWasOpenedAt);
         }
      });
      popup.add(mnuInTable);

      JMenuItem mnuInTableWin = new JMenuItem(s_stringMgr.getString("DataScaleTablePopupHandler.showTableInWin"));
      mnuInTableWin.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            _dataScalePopupWasOpenedAt.showInTableWin(_buttonPopupWasOpenedAt);
         }
      });
      popup.add(mnuInTableWin);

      return popup;
   }

   private void maybeShowPopUpOnTable(MouseEvent evt, JPopupMenu popup)
   {
      if (evt.isPopupTrigger())
      {
         Point point = new Point(evt.getX(), evt.getY());

         int colIx = _dataScaleTable.columnAtPoint(point);

         String headerValue = (String) _dataScaleTable.getColumnModel().getColumn(colIx).getHeaderValue();
         if(DataScaleTableModel.COL_NAME_DATA.equals(headerValue))
         {
            int rowIx = _dataScaleTable.rowAtPoint(point);

            DataScaleTableModel model = _dataScaleTable.getDataScaleTableModel();

            SortableTableModel sortableTableModel = (SortableTableModel) _dataScaleTable.getModel();
            int transformedRow = sortableTableModel.transfromToModelRow(rowIx);
            _dataScalePopupWasOpenedAt = model.getDataScaleAt(transformedRow);


            Rectangle rect = _dataScaleTable.getCellRect(rowIx, colIx, false);

//            System.out.println("cell rect x = " + rect.x);
//            System.out.println("cell rect y = " + rect.y);
//
//            System.out.println("point x = " + point.x);
//            System.out.println("point y = " + point.y);
//
            int xInScalePanel = point.x - rect.x;
            int yInScalePanel = point.y - rect.y;
//
//            System.out.println("point in panel x = " + xInScalePanel);
//            System.out.println("point in panel y = " + yInScalePanel);

            _buttonPopupWasOpenedAt = _dataScalePopupWasOpenedAt.getPanel().getButtonAt(xInScalePanel);

//            System.out.println("btn = " + _buttonPopupWasOpenedAt.getText());

            if (null != _dataScalePopupWasOpenedAt && null != _buttonPopupWasOpenedAt)
            {
               popup.show(evt.getComponent(), evt.getX(), evt.getY());
            }

         }
      }
   }
}
