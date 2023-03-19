package net.sourceforge.squirrel_sql.fw.gui.tableselectiondiff;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.gui.action.copyasmarkdown.CopyAsMarkDown;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

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
      String markdown = CopyAsMarkDown.createMarkdownForSelectedCells(diffTableProvider.getTable());

      if(StringUtilities.isEmpty(markdown, true))
      {
         return;
      }

      Main.getApplication().getTableSelectionDiffStore().storeMarkdown(markdown);
   }


   private static void onCompare(DiffTableProvider diffTableProvider)
   {
      String leftMarkdown = CopyAsMarkDown.createMarkdownForSelectedCells(diffTableProvider.getTable());
      String rightMarkdown = Main.getApplication().getTableSelectionDiffStore().retrieveAndClearMarkdown();

      DbDiffPluginAccessor.showDiff(leftMarkdown, rightMarkdown);
   }

}
