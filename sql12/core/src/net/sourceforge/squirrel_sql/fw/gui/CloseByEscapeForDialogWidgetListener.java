package net.sourceforge.squirrel_sql.fw.gui;

import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;

@FunctionalInterface
public interface CloseByEscapeForDialogWidgetListener
{
   void willCloseByEscape(DialogWidget dialogWidget);
}
