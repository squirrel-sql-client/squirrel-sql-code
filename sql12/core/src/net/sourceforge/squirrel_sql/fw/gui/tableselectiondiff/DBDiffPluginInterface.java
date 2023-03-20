package net.sourceforge.squirrel_sql.fw.gui.tableselectiondiff;

import java.nio.file.Path;

public interface DBDiffPluginInterface
{
   void showDiff(Path leftFile, Path rightFile);
}
