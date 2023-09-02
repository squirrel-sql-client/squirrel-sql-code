package net.sourceforge.squirrel_sql.plugins.syntax.rsyntax;

import net.sourceforge.squirrel_sql.client.session.PrioritizedCaretMouseListener;
import org.fife.ui.rtextarea.ConfigurableCaret;

import java.awt.event.MouseEvent;

public class SquirrelRSyntaxCaretWithPrioritizedMouseListener extends ConfigurableCaret
{
   private PrioritizedCaretMouseListener _prioritizedCaretMouseListener;

   public void setPrioritizedCaretMouseListener(PrioritizedCaretMouseListener prioritizedCaretMouseListener)
   {
      _prioritizedCaretMouseListener = prioritizedCaretMouseListener;
   }

   @Override
   public void mouseClicked(MouseEvent e)
   {
      if(null != _prioritizedCaretMouseListener && _prioritizedCaretMouseListener.mouseClicked(e))
      {
         return;
      }

      super.mouseClicked(e);
   }

   @Override
   public void mousePressed(MouseEvent e)
   {
      if(null != _prioritizedCaretMouseListener && _prioritizedCaretMouseListener.mousePressed(e))
      {
         return;
      }

      super.mousePressed(e);
   }

   @Override
   public void mouseReleased(MouseEvent e)
   {
      if(null != _prioritizedCaretMouseListener && _prioritizedCaretMouseListener.mouseReleased(e))
      {
         return;
      }

      super.mouseReleased(e);
   }

}
