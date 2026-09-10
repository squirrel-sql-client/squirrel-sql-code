package net.sourceforge.squirrel_sql.fw.gui;

import java.awt.Window;

@FunctionalInterface
public interface CloseByEscapeListener <WIN_TYPE extends Window>
{
   void willCloseByEscape(WIN_TYPE win);
}
