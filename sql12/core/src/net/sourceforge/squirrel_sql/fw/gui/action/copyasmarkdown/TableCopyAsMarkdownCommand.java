package net.sourceforge.squirrel_sql.fw.gui.action.copyasmarkdown;

import net.sourceforge.squirrel_sql.fw.gui.ClipboardUtil;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.JTable;

public class TableCopyAsMarkdownCommand
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(TableCopyAsMarkdownCommand.class);

   private final JTable _table;

   public TableCopyAsMarkdownCommand(JTable table)
   {
      _table = table;
   }

   public void execute()
   {
      String markdown = CopyAsMarkDown.createMarkdownForSelectedCells(_table);

      if(StringUtilities.isEmpty(markdown, true))
      {
         return;
      }

      ClipboardUtil.copyToClip(markdown);
   }
}
