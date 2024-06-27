package net.sourceforge.squirrel_sql.fw.datasetviewer.celldatapopup;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;

class CellDataColumnDataPopupPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CellDataColumnDataPopupPanel.class);

   private final PopupEditableIOPanel _ioPanel;
   private JDialog _parentFrame = null;
   private int _row;
   private int _col;
   private JTable _table;

   CellDataColumnDataPopupPanel(Object cellContents, ColumnDisplayDefinition colDef, boolean tableIsEditable)
   {
      super(new BorderLayout());

      if (tableIsEditable && CellComponentFactory.isEditableInPopup(colDef, cellContents))
      {

         // data is editable in popup
         _ioPanel = new PopupEditableIOPanel(colDef, cellContents, true);

         // Since data is editable, we need to add control panel
         // to manage user requests for DB update, file IO, etc.
         JPanel editingControls = createPopupEditingControls();
         add(editingControls, BorderLayout.SOUTH);
      }
      else
      {
         // data is not editable in popup
         _ioPanel = new PopupEditableIOPanel(colDef, cellContents, false);
      }
      add(_ioPanel, BorderLayout.CENTER);
   }

   /**
    * Set up user controls to stop editing and update DB.
    */
   private JPanel createPopupEditingControls()
   {

      JPanel panel = new JPanel(new BorderLayout());

      // create update/cancel controls using default layout
      JPanel updateControls = new JPanel();

      JButton updateButton = new JButton(s_stringMgr.getString("cellDataPopUp.updateData"));
      updateButton.addActionListener(event -> onUpdate());

      // set up Cancel button
      // i18n[cellDataPopup.cancel=Cancel]
      JButton cancelButton = new JButton(s_stringMgr.getString("cellDataPopup.cancel"));
      cancelButton.addActionListener(event -> onCancel());

      // add buttons to button panel
      updateControls.add(updateButton);
      updateControls.add(cancelButton);

      // add button panel to main panel
      panel.add(updateControls, BorderLayout.SOUTH);

      return panel;
   }

   private void onCancel()
   {
      closeFrame();
   }


   private void onUpdate()
   {
      // try to convert the text in the popup into a valid
      // instance of type of data object being held in the table cell
      StringBuffer messageBuffer = new StringBuffer();
      Object newValue = _ioPanel.getObject(messageBuffer);
      if (messageBuffer.length() > 0)
      {
         // handle an error in conversion of text to object
         JOptionPane.showMessageDialog(
               CellDataColumnDataPopupPanel.this,
               s_stringMgr.getString("cellDataPopUp.cannnotBGeConverted", messageBuffer),
               // i18n[cellDataPopUp.conversionError=Conversion Error]
               s_stringMgr.getString("cellDataPopUp.conversionError"),
               JOptionPane.ERROR_MESSAGE);

         _ioPanel.requestFocus();
      }
      else
      {
         _table.setValueAt(newValue, _row, _col);
         closeFrame();
      }
   }

   private void closeFrame()
   {
      _parentFrame.setVisible(false);
      _parentFrame.dispose();
   }


   /*
    * Save various information which is needed to do Update & Cancel.
    */
   public void setUserActionInfo(JDialog parent, int row, int col,
                                 JTable table)
   {
      _parentFrame = parent;
      _row = row;
      _col = col;
      _table = table;
   }


}
