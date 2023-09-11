package net.sourceforge.squirrel_sql.client.session.defaultentry;

import net.sourceforge.squirrel_sql.client.session.PrioritizedCaretMouseListener;

import javax.swing.text.DefaultCaret;
import java.awt.event.MouseEvent;

public class SquirrelDefaultCaretWithPrioritizedMouseListener extends DefaultCaret
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
