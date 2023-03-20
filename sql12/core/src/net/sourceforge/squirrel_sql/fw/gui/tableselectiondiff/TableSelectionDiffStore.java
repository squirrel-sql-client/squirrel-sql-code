package net.sourceforge.squirrel_sql.fw.gui.tableselectiondiff;

import java.nio.file.Path;

public class TableSelectionDiffStore
{
   private Path _selectedMarkdownTempFile;

   public void storeSelectedForCompareMarkdown(String selectedMarkDown)
   {
      _selectedMarkdownTempFile = TableSelectionDiffUtil.createLeftTempFile(selectedMarkDown);
   }

   public Path getSelectedMarkdownTempFile()
   {
      return _selectedMarkdownTempFile;
   }
}
