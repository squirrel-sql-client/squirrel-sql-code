package net.sourceforge.squirrel_sql.client.session.action.dbdiff.tableselectiondiff;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.action.dbdiff.DBDIffService;
import net.sourceforge.squirrel_sql.fw.gui.ClipboardUtil;
import net.sourceforge.squirrel_sql.fw.gui.action.copyasmarkdown.CopyAsMarkDown;
import net.sourceforge.squirrel_sql.fw.gui.action.copyasmarkdown.CopyAsMarkDownResult;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.nio.file.Path;

public class TableSelectionDiff
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(TableSelectionDiff.class);

   public static JMenu createMenu(DiffTableProvider diffTableProvider)
   {
      JMenu ret = new JMenu(s_stringMgr.getString("TableSelectionDiff.submenu.title"));

      JMenuItem mnuSelectForCompare = new JMenuItem(s_stringMgr.getString("TableSelectionDiff.select.for.compare"));
      mnuSelectForCompare.addActionListener(e -> onSelectForCompare(diffTableProvider));
      ret.add(mnuSelectForCompare);

      JMenuItem mnuCompare = new JMenuItem(s_stringMgr.getString("TableSelectionDiff.compare"));
      mnuCompare.addActionListener(e -> onCompare(diffTableProvider, false));
      ret.add(mnuCompare);


      JMenuItem mnuCompareToClip = new JMenuItem(s_stringMgr.getString("TableSelectionDiff.compare.to.clipboard"));
      mnuCompareToClip.addActionListener(e -> onCompareToClip(diffTableProvider, false));
      ret.add(mnuCompareToClip);

      ret.addSeparator();

      JMenuItem mnuCompareSingleColRaw = new JMenuItem(s_stringMgr.getString("TableSelectionDiff.compareSingleColRaw"));
      mnuCompareSingleColRaw.addActionListener(e -> onCompare(diffTableProvider, true));
      ret.add(mnuCompareSingleColRaw);

      JMenuItem mnuCompareSingleColRawToClip = new JMenuItem(s_stringMgr.getString("TableSelectionDiff.compareSingleColRaw.to.clipboard"));
      mnuCompareSingleColRawToClip.addActionListener(e -> onCompareToClip(diffTableProvider, true));
      ret.add(mnuCompareSingleColRawToClip);

      ret.addMenuListener(new MenuListener()
      {
         @Override
         public void menuSelected(MenuEvent e)
         {
            onParentMenuSelected(diffTableProvider, mnuCompareSingleColRaw, mnuCompareSingleColRawToClip);
         }

         @Override
         public void menuDeselected(MenuEvent e) {}

         @Override
         public void menuCanceled(MenuEvent e) {}
      });



      return ret;
   }

   private static void onParentMenuSelected(DiffTableProvider diffTableProvider, JMenuItem mnuCompareSingleColRaw, JMenuItem mnuCompareSingleColRawToClip)
   {
      mnuCompareSingleColRaw.setEnabled(true);
      mnuCompareSingleColRawToClip.setEnabled(true);

      if(   null == Main.getApplication().getDBDiffState().getSelectedTableCellsRawSingleColumnDataTempFile()
            || 1 != diffTableProvider.getTable().getSelectedColumns().length)
      {
         mnuCompareSingleColRaw.setEnabled(false);
      }

      if( 1 != diffTableProvider.getTable().getSelectedColumns().length)
      {
         mnuCompareSingleColRawToClip.setEnabled(false);
      }
   }

   private static void onSelectForCompare(DiffTableProvider diffTableProvider)
   {
      int nbrSelRows = diffTableProvider.getTable().getSelectedRowCount();
      int nbrSelCols = diffTableProvider.getTable().getSelectedColumnCount();

      if(0 == nbrSelRows || 0 == nbrSelCols)
      {
         Main.getApplication().getMessageHandler().showErrorMessage(s_stringMgr.getString("TableSelectionDiff.no.selection.err"));
         return;
      }

      if(1 == nbrSelRows && 1 == nbrSelCols)
      {
         Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("TableSelectionDiff.single.selection.warn"));
      }

      if(1 == nbrSelCols)
      {
         CopyAsMarkDownResult markdownForSelectedCells = CopyAsMarkDown.createMarkdownForSelectedCellsIncludingRawData(diffTableProvider.getTable());
         if(markdownForSelectedCells.isEmpty())
         {
            return;
         }
         String rawColumnString = markdownForSelectedCells.getRawColumnString(markdownForSelectedCells.getColNames()[0]);
         Main.getApplication().getDBDiffState().storeSelectedTableCellsForCompare(markdownForSelectedCells.getMarkDownString(), rawColumnString);
      }
      else
      {
         String markdown = CopyAsMarkDown.createMarkdownForSelectedCells(diffTableProvider.getTable());
         if(StringUtilities.isEmpty(markdown, true))
         {
            return;
         }
         Main.getApplication().getDBDiffState().storeSelectedTableCellsForMarkdownCompare(markdown);
      }
   }


   private static void onCompare(DiffTableProvider diffTableProvider, boolean singleColumnRaw)
   {
      Path leftMarkdownTempFile;
      if (singleColumnRaw)
      {
         leftMarkdownTempFile = Main.getApplication().getDBDiffState().getSelectedTableCellsRawSingleColumnDataTempFile();
      }
      else
      {
         leftMarkdownTempFile = Main.getApplication().getDBDiffState().getSelectedTableCellsMarkdownTempFile();
      }

      if(null == leftMarkdownTempFile)
      {
         Main.getApplication().getMessageHandler().showErrorMessage(s_stringMgr.getString("TableSelectionDiff.no.selection.for.compare.err"));
         return;
      }

      compareSelectedCellsToLeftTempFile(diffTableProvider, leftMarkdownTempFile, singleColumnRaw);
   }

   private static void compareSelectedCellsToLeftTempFile(DiffTableProvider diffTableProvider, Path leftMarkdownTempFile, boolean singleColumnRaw)
   {
      int nbrSelRows = diffTableProvider.getTable().getSelectedRowCount();
      int nbrSelCols = diffTableProvider.getTable().getSelectedColumnCount();

      if(0 == nbrSelRows || 0 == nbrSelCols)
      {
         Main.getApplication().getMessageHandler().showErrorMessage(s_stringMgr.getString("TableSelectionDiff.no.selection.err"));
         return;
      }


      Path rightMarkdownTempFile;
      if (singleColumnRaw)
      {
         CopyAsMarkDownResult copyAsMarkDownResult = CopyAsMarkDown.createMarkdownForSelectedCellsIncludingRawData(diffTableProvider.getTable());
         rightMarkdownTempFile = TableSelectionDiffUtil.createRightTempFile(copyAsMarkDownResult.getRawColumnString(copyAsMarkDownResult.getColNames()[0]));
      }
      else
      {
         String markdownForSelectedCells = CopyAsMarkDown.createMarkdownForSelectedCells(diffTableProvider.getTable());
         rightMarkdownTempFile = TableSelectionDiffUtil.createRightTempFile(markdownForSelectedCells);
      }

      String diffDialogTitle = s_stringMgr.getString("TableSelectionDiff.dialog.title");
      DBDIffService.showDiff(leftMarkdownTempFile, rightMarkdownTempFile, diffDialogTitle);
   }

   private static void onCompareToClip(DiffTableProvider diffTableProvider, boolean singleColumnRaw)
   {
      String clipboardAsString = ClipboardUtil.getClipboardAsString();

      if(StringUtilities.isEmpty(clipboardAsString, true))
      {
         Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("TableSelectionDiff.clipboard.empty.warn"));
         return;
      }

      Path leftClipboardTempFile = TableSelectionDiffUtil.createLeftTempFile(clipboardAsString);

      compareSelectedCellsToLeftTempFile(diffTableProvider, leftClipboardTempFile, singleColumnRaw);
   }
}
