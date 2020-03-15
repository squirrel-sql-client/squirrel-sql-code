package net.sourceforge.squirrel_sql.client.gui.db;

import java.io.Closeable;

public interface Java8CloseableFix extends Closeable
{
   @Override
   void close();
}
