package net.sourceforge.squirrel_sql.client.preferences.shortcut;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.shortcut.Shortcut;
import net.sourceforge.squirrel_sql.client.shortcut.ShortcutUtil;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.JavabeanArrayDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.TableState;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;


public class ShortcutPrefsCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ShortcutPrefsCtrl.class);


   private static final String PREF_KEY_ACTION_NAME_COL_WIDTH = "ShortcutPrefsCtrl.column.actionName.width";
   private static final String PREF_KEY_VALID_SHORTCUT_COL_WIDTH = "ShortcutPrefsCtrl.column.validShortcut.width";
   private static final String PREF_KEY_DEFAULT_SHORTCUT_COL_WIDTH = "ShortcutPrefsCtrl.column.defaultShortcut.width";

   public static final String COL_HEADER_ACTION_NAME = s_stringMgr.getString("ShortcutPrefsCtrl.column.actionName");
   public static final String COL_HEADER_VALID_SHORTCUT = s_stringMgr.getString("ShortcutPrefsCtrl.column.validKeyStroke");
   public static final String COL_HEADER_DEFAULT_SHORTCUT = s_stringMgr.getString("ShortcutPrefsCtrl.column.defaultKeyStroke");

   private ShortcutPrefsPanel _shortcutPrefsPanel = new ShortcutPrefsPanel();
   private KeyStroke _currentKeyStroke;
   private List<Shortcut> _shortcuts;
   private JavabeanArrayDataSet _shortcutDataSet;


   public void applyChanges()
   {
      Props.putInt(PREF_KEY_ACTION_NAME_COL_WIDTH, _shortcutPrefsPanel.tblShortcuts.getColumnWidthForHeader(COL_HEADER_ACTION_NAME));
      Props.putInt(PREF_KEY_VALID_SHORTCUT_COL_WIDTH, _shortcutPrefsPanel.tblShortcuts.getColumnWidthForHeader(COL_HEADER_VALID_SHORTCUT));
      Props.putInt(PREF_KEY_DEFAULT_SHORTCUT_COL_WIDTH, _shortcutPrefsPanel.tblShortcuts.getColumnWidthForHeader(COL_HEADER_DEFAULT_SHORTCUT));


      Main.getApplication().getShortcutManager().save();

   }

   public JPanel getPanel()
   {

      _shortcuts = Main.getApplication().getShortcutManager().getShortcuts();

      _shortcutDataSet = new JavabeanArrayDataSet(Shortcut.class);

      _shortcutDataSet.setColHeader("actionName", COL_HEADER_ACTION_NAME);
      _shortcutDataSet.setColPos("actionName", 1);
      _shortcutDataSet.setAbsoluteWidht("actionName", Props.getInt(PREF_KEY_ACTION_NAME_COL_WIDTH, 200));

      _shortcutDataSet.setColHeader("validKeyStroke", COL_HEADER_VALID_SHORTCUT);
      _shortcutDataSet.setColPos("validKeyStroke", 2);
      _shortcutDataSet.setAbsoluteWidht("validKeyStroke", Props.getInt(PREF_KEY_VALID_SHORTCUT_COL_WIDTH, 200));

      _shortcutDataSet.setColHeader("defaultKeyStroke", COL_HEADER_DEFAULT_SHORTCUT);
      _shortcutDataSet.setColPos("defaultKeyStroke", 2);
      _shortcutDataSet.setAbsoluteWidht("defaultKeyStroke", Props.getInt(PREF_KEY_DEFAULT_SHORTCUT_COL_WIDTH, 200));

      _shortcutDataSet.setIgnoreProperty("userKeyStroke");

      displayShortcuts();

      _shortcutPrefsPanel.tblShortcuts.getTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      _shortcutPrefsPanel.tblShortcuts.getTable().getSelectionModel().setSelectionInterval(0,0);
      _shortcutPrefsPanel.tblShortcuts.addRowSelectionListener((nowSelectedIx, formerSelectedIx) -> onSelectedShortcutChanged());

      _shortcutPrefsPanel.txtShortcut.addKeyListener(new KeyAdapter() {

         @Override
         public void keyPressed(KeyEvent e)
         {
            onKeyPressed(e);
         }
      });

      _shortcutPrefsPanel.btnApply.addActionListener(e -> onApply());

      _shortcutPrefsPanel.btnRestoreDefault.addActionListener(e -> onRestore());

      _shortcutPrefsPanel.btnRestoreAll.addActionListener(e -> onRestoreAll());


      _shortcutPrefsPanel.tblShortcuts.getTable().getColoringService().setColoringCallback((row, column, isSelected) -> onGetCellColor(row, column, isSelected));

      onSelectedShortcutChanged();

      return _shortcutPrefsPanel;
   }

   private Color onGetCellColor(int row, int column, boolean isSelected)
   {
      int modelColumn = _shortcutPrefsPanel.tblShortcuts.getTable().getColumnModel().getColumn(column).getModelIndex();

      if(2 != modelColumn)
      {
         return null;
      }

      int modelRow = _shortcutPrefsPanel.tblShortcuts.getTable().getSortableTableModel().transformToModelRow(row);

      _shortcuts.get(modelRow);

      for (Shortcut shortcut : _shortcuts)
      {
         if(    shortcut != _shortcuts.get(modelRow)
             && null != shortcut.getValidKeyStroke() && null != _shortcuts.get(modelRow).getValidKeyStroke()
             && Utilities.equalsRespectNull(shortcut.getValidKeyStroke(), _shortcuts.get(modelRow).getValidKeyStroke()))
         {
            return new Color(255, 100, 100);
         }
      }

      if(_shortcuts.get(modelRow).hasUserKeyStroke())
      {
         return Color.green;
      }

      return null;
   }

   private void onSelectedShortcutChanged()
   {
      updateEnabled();


      if(0 == _shortcutPrefsPanel.tblShortcuts.getSelectedModelRows().length)
      {
         _shortcutPrefsPanel.txtSelectedShortcut.setText(s_stringMgr.getString("ShortcutPrefsPanel.txt.shortcut.formated", "<No selection>", "<No selection"));
         return;

      }


      Shortcut selectedShortcut = getSelectedShortcut();

      _shortcutPrefsPanel.txtSelectedShortcut.setText(s_stringMgr.getString("ShortcutPrefsPanel.txt.shortcut.formated", selectedShortcut.getActionName(), selectedShortcut.getDefaultKeyStroke()));
   }

   private void displayShortcuts()
   {
      try
      {
         final int[] selectedModelRows = _shortcutPrefsPanel.tblShortcuts.getSelectedModelRows();

         TableState tableState = _shortcutPrefsPanel.tblShortcuts.getResultSortableTableState();
         _shortcutDataSet.setJavaBeanList(_shortcuts);
         _shortcutPrefsPanel.tblShortcuts.show(_shortcutDataSet);
         _shortcutPrefsPanel.tblShortcuts.applyResultSortableTableState(tableState);

         Runnable runnable = () -> handleSelectAfterUpdateEvenWhenValidShortcutIsSorted(selectedModelRows);

         SwingUtilities.invokeLater(runnable);
      }
      catch (DataSetException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   private void handleSelectAfterUpdateEvenWhenValidShortcutIsSorted(int[] selectedModelRows)
   {
      if (selectedModelRows.length > 0)
      {
         int viewRow = _shortcutPrefsPanel.tblShortcuts.getTable().getSortableTableModel().transformToViewRow(selectedModelRows[0]);
         _shortcutPrefsPanel.tblShortcuts.getTable().getSelectionModel().setSelectionInterval(viewRow, viewRow);

         if(2 == _shortcutPrefsPanel.tblShortcuts.getTable().getSortableTableModel().getSortedColumn())
         {
            _shortcutPrefsPanel.tblShortcuts.getTable().scrollToVisible(viewRow, 1);
         }
      }
   }

   private void onApply()
   {
      Shortcut selectedShortcut = getSelectedShortcut();

      if(null == _currentKeyStroke)
      {
         throw new IllegalStateException("Should not happen. I.e. Apply button should be disabled when there is no _currentKeyStroke.");
      }

      selectedShortcut.setUserKeyStroke(_currentKeyStroke);

      displayShortcuts();

   }


   private void onRestore()
   {
      Shortcut selectedShortcut = getSelectedShortcut();

      selectedShortcut.restoreDefault();

      displayShortcuts();
   }

   private void onRestoreAll()
   {
      if(JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(_shortcutPrefsPanel, s_stringMgr.getString("ShortcutPrefsCtrl.restore.all.question")))
      {
         return;
      }

      for (Shortcut shortcut : _shortcuts)
      {
         shortcut.restoreDefault();
      }

      displayShortcuts();
   }


   private Shortcut getSelectedShortcut()
   {
      int[] seletedModelRows = _shortcutPrefsPanel.tblShortcuts.getSelectedModelRows();

      if(1 != seletedModelRows.length)
      {
         throw new IllegalStateException("Should not happen. I.e. Apply button should be disabled when no shortcut is selected.");
      }

      return _shortcuts.get(seletedModelRows[0]);
   }


   private void onKeyPressed(KeyEvent e)
   {
      _currentKeyStroke = KeyStroke.getKeyStrokeForEvent(e);
      String shortcutText = ShortcutUtil.getKeystrokeString(_currentKeyStroke);

      if(0 != e.getModifiers()
         && (
                  e.getKeyCode() == KeyEvent.VK_SHIFT
               || e.getKeyCode() == KeyEvent.VK_ALT
               || e.getKeyCode() == KeyEvent.VK_CONTROL
               || e.getKeyCode() == KeyEvent.VK_WINDOWS
               || e.getKeyCode() == KeyEvent.VK_META
            )
      )
      {
         // The text is up to now only a modifier. We await the user will push another key.
         shortcutText = null;
         _currentKeyStroke = null;
      }


      String finalShortcutText = shortcutText;
      SwingUtilities.invokeLater(() ->_shortcutPrefsPanel.txtShortcut.setText(finalShortcutText));

      updateEnabled();

      e.consume();

   }

   private void updateEnabled()
   {
      _shortcutPrefsPanel.btnApply.setEnabled(null != _currentKeyStroke && 1 == _shortcutPrefsPanel.tblShortcuts.getSeletedRows().length);

      _shortcutPrefsPanel.btnRestoreDefault.setEnabled(1 == _shortcutPrefsPanel.tblShortcuts.getSeletedRows().length);
   }

}
