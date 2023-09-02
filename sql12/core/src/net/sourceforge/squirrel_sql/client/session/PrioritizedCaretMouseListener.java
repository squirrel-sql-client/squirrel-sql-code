package net.sourceforge.squirrel_sql.client.session;

import java.awt.event.MouseEvent;

public interface PrioritizedCaretMouseListener
{

   /**
    * @return true if the event was consumed.
    */
   boolean mouseClicked(MouseEvent e);

   /**
    * @return true if the event was consumed.
    */
   boolean mousePressed(MouseEvent e);

   /**
    * @return true if the event was consumed.
    */
   boolean mouseReleased(MouseEvent e);
}
