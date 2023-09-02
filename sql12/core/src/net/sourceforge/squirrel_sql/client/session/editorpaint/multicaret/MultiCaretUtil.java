package net.sourceforge.squirrel_sql.client.session.editorpaint.multicaret;

import java.awt.event.MouseEvent;

public class MultiCaretUtil
{
   public static boolean isAdditionalCaretMouseClickModifier(MouseEvent e)
   {
      return e.isAltDown() && e.isControlDown() && !e.isShiftDown();
   }
}
