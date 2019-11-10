package net.sourceforge.squirrel_sql.plugins.dataimport.gui;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Rectangle;
import java.awt.Window;

public class ImportTableDetailsCtrl
{
   private ImportTableDetailsDialog _importTableDetailsDialog;
   private final String _tableName;
   private final ISession _session;
   private final String[][] _previewData;
   private final boolean _headerIncluded;

   private boolean _isCreateTable;

   public ImportTableDetailsCtrl(Window owningWindow, ISession session, String[][] previewData, String tableName, boolean headerIncluded)
   {
      _importTableDetailsDialog = new ImportTableDetailsDialog(owningWindow);

      _tableName = tableName;
      _session = session;
      _previewData = previewData;
      _headerIncluded = headerIncluded;

      _importTableDetailsDialog.txtTableNamePattern.setText(ImportPropsDAO.getTableNamePattern());

      _importTableDetailsDialog.chkSuggestColumnTypes.setSelected(ImportPropsDAO.isSuggestColumnTypes());
      _importTableDetailsDialog.chkSuggestColumnTypes.addActionListener(e -> refreshSQL());

      _importTableDetailsDialog.txtVarcharLength.setInt(ImportPropsDAO.getVarCharLength());
      _importTableDetailsDialog.txtVarcharLength.getDocument().addDocumentListener(createRefreshSqlDocListener());


      _importTableDetailsDialog.txtNumericPrecision.setInt(ImportPropsDAO.getNumericPrecision());
      _importTableDetailsDialog.txtNumericPrecision.getDocument().addDocumentListener(createRefreshSqlDocListener());

      _importTableDetailsDialog.txtNumericScale.setInt(ImportPropsDAO.getNumericScale());
      _importTableDetailsDialog.txtNumericScale.getDocument().addDocumentListener(createRefreshSqlDocListener());

      refreshSQL();


      _importTableDetailsDialog.btnCreateTable.addActionListener(e -> onCreateTable());
      _importTableDetailsDialog.btnClose.addActionListener(e -> onClose());


      GUIUtils.initLocation(_importTableDetailsDialog, 400, 400);
      GUIUtils.enableCloseByEscape(_importTableDetailsDialog);
   }


   private DocumentListener createRefreshSqlDocListener()
   {
      return new DocumentListener()
         {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
               refreshSQL();
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
               refreshSQL();
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
               refreshSQL();
            }
         };
   }

   private void refreshSQL()
   {
      savePrefs();

      String createSql = TableCreateUtils.suggestCreateScript(_tableName, _session, _previewData, _headerIncluded);
      _importTableDetailsDialog.txtCreateTableSQL.setText(createSql);

      SwingUtilities.invokeLater(() -> _importTableDetailsDialog.txtCreateTableSQL.scrollRectToVisible(new Rectangle(0,0,1,1)));
   }

   public void showDialog()
   {
      _importTableDetailsDialog.setVisible(true);
   }


   private void onCreateTable()
   {
      _isCreateTable = true;
      onClose();
   }

   private void onClose()
   {
      savePrefs();
      close();
   }

   private void close()
   {
      _importTableDetailsDialog.setVisible(false);
      _importTableDetailsDialog.dispose();
   }


   private void savePrefs()
   {
      ImportPropsDAO.setTableNamePattern(_importTableDetailsDialog.txtTableNamePattern.getText());
      ImportPropsDAO.setSuggestTypes(_importTableDetailsDialog.chkSuggestColumnTypes.isSelected());

      ImportPropsDAO.setVarCharLength(_importTableDetailsDialog.txtVarcharLength.getInt());
      ImportPropsDAO.setNumericPrecision(_importTableDetailsDialog.txtNumericPrecision.getInt());
      ImportPropsDAO.setNumericScale(_importTableDetailsDialog.txtNumericScale.getInt());
   }


   public boolean isCreateTable()
   {
      return _isCreateTable;
   }

   public String getCreateSql()
   {
      return _importTableDetailsDialog.txtCreateTableSQL.getText();
   }



}
