package net.sourceforge.squirrel_sql.fw.gui.tableselectiondiff;

import java.nio.file.Path;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.gui.action.copyasmarkdown.CopyAsMarkDown;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

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
      mnuCompare.addActionListener(e -> onCompare(diffTableProvider));
      ret.add(mnuCompare);

      return ret;
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

      String markdown = CopyAsMarkDown.createMarkdownForSelectedCells(diffTableProvider.getTable());

      if(StringUtilities.isEmpty(markdown, true))
      {
         return;
      }

      Main.getApplication().getTableSelectionDiffStore().storeSelectedForCompareMarkdown(markdown);
   }


   private static void onCompare(DiffTableProvider diffTableProvider)
   {
      Path leftMarkdownTempFile = Main.getApplication().getTableSelectionDiffStore().getSelectedMarkdownTempFile();

      if(null == leftMarkdownTempFile)
      {
         Main.getApplication().getMessageHandler().showErrorMessage(s_stringMgr.getString("TableSelectionDiff.no.selection.for.compare.err"));
         return;
      }


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



      String rightMarkdown = CopyAsMarkDown.createMarkdownForSelectedCells(diffTableProvider.getTable());
      Path rightMarkdownTempFile = TableSelectionDiffUtil.createRightTempFile(rightMarkdown);

      DbDiffPluginAccessor.showDiff(leftMarkdownTempFile, rightMarkdownTempFile);
   }

}
