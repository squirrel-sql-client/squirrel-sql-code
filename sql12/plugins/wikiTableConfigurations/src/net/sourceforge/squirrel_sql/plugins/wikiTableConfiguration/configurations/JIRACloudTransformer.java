package net.sourceforge.squirrel_sql.plugins.wikiTableConfiguration.configurations;

import javax.swing.JTable;
import net.sourceforge.squirrel_sql.fw.gui.action.copyasmarkdown.CopyAsMarkDown;
import net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableTransformer;

public class JIRACloudTransformer implements IWikiTableTransformer
{
   @Override
   public String transform(JTable table, boolean isExampleTableInConfig)
   {
      return CopyAsMarkDown.createMarkdownForSelectedCells(table, false);
   }
}
