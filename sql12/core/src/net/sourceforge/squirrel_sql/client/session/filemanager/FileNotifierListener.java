package net.sourceforge.squirrel_sql.client.session.filemanager;

import java.io.File;

@FunctionalInterface
public interface FileNotifierListener
{
   void fileChanged(File file);
}
