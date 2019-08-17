package net.sourceforge.squirrel_sql.fw.datasetviewer;

import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class CellDataColumnDataPopupPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CellDataColumnDataPopupPanel.class);

   private final PopupEditableIOPanel ioPanel;
   private JDialog _parentFrame = null;
   private int _row;
   private int _col;
   private JTable _table;

   CellDataColumnDataPopupPanel(Object cellContents, ColumnDisplayDefinition colDef, boolean tableIsEditable)
   {
      super(new BorderLayout());

      if (tableIsEditable &&
            CellComponentFactory.isEditableInPopup(colDef, cellContents))
      {

         // data is editable in popup
         ioPanel = new PopupEditableIOPanel(colDef, cellContents, true);

         // Since data is editable, we need to add control panel
         // to manage user requests for DB update, file IO, etc.
         JPanel editingControls = createPopupEditingControls();
         add(editingControls, BorderLayout.SOUTH);
      }
      else
      {
         // data is not editable in popup
         ioPanel = new PopupEditableIOPanel(colDef, cellContents, false);
      }

      add(ioPanel, BorderLayout.CENTER);

   }

   /**
    * Set up user controls to stop editing and update DB.
    */
   private JPanel createPopupEditingControls()
   {

      JPanel panel = new JPanel(new BorderLayout());

      // create update/cancel controls using default layout
      JPanel updateControls = new JPanel();

      // set up Update button
      // i18n[cellDataPopUp.updateData=Update Data]
      JButton updateButton = new JButton(s_stringMgr.getString("cellDataPopUp.updateData"));
      updateButton.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent event)
         {

            // try to convert the text in the popup into a valid
            // instance of type of data object being held in the table cell
            StringBuffer messageBuffer = new StringBuffer();
            Object newValue = CellDataColumnDataPopupPanel.this.ioPanel.getObject(messageBuffer);
            if (messageBuffer.length() > 0)
            {
               // handle an error in conversion of text to object

               // i18n[cellDataPopUp.cannnotBGeConverted=The given text cannot be converted into the internal object.\n
               //Please change the data or cancel editing.\n
               //The conversion error was:\n{0}]
               String msg = s_stringMgr.getString("cellDataPopUp.cannnotBGeConverted", messageBuffer);

               JOptionPane.showMessageDialog(
                     CellDataColumnDataPopupPanel.this,
                     msg,
                     // i18n[cellDataPopUp.conversionError=Conversion Error]
                     s_stringMgr.getString("cellDataPopUp.conversionError"),
                     JOptionPane.ERROR_MESSAGE);

               CellDataColumnDataPopupPanel.this.ioPanel.requestFocus();

            }
            else
            {
               _table.setValueAt(newValue, _row, _col);
               CellDataColumnDataPopupPanel.this._parentFrame.setVisible(false);
               CellDataColumnDataPopupPanel.this._parentFrame.dispose();
            }
         }
      });

      // set up Cancel button
      // i18n[cellDataPopup.cancel=Cancel]
      JButton cancelButton = new JButton(s_stringMgr.getString("cellDataPopup.cancel"));
      cancelButton.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent event)
         {
            CellDataColumnDataPopupPanel.this._parentFrame.setVisible(false);
            CellDataColumnDataPopupPanel.this._parentFrame.dispose();
         }
      });

      // add buttons to button panel
      updateControls.add(updateButton);
      updateControls.add(cancelButton);

      // add button panel to main panel
      panel.add(updateControls, BorderLayout.SOUTH);

      return panel;
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
