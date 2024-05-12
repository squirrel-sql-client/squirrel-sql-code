package net.sourceforge.squirrel_sql.fw.gui;

@FunctionalInterface
public interface ResizableTextEditDialogBeforeOkCloseCallback
{
   boolean allowCloseOnOk(ResizableTextEditDialog theResizableTextEditDialog, String editedText);
}
