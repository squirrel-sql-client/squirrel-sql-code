package net.sourceforge.squirrel_sql.client.gui.db;

import net.sourceforge.squirrel_sql.fw.gui.BasePopupMenu;
import net.sourceforge.squirrel_sql.fw.gui.ToolBar;

import java.awt.event.MouseEvent;

interface IUserInterfaceFactory<T extends IBaseList>
{
   ToolBar getToolBar();

   BasePopupMenu getPopupMenu();

   T getList();

   String getWindowTitle();

   void execDoubleClickCommand(MouseEvent evt);
}
