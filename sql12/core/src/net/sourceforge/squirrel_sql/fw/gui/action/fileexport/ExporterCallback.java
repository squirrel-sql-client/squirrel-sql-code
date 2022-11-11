package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.fw.sql.ProgressAbortCallback;

@FunctionalInterface
public interface ExporterCallback
{
   ProgressAbortCallback createProgressController();
}
