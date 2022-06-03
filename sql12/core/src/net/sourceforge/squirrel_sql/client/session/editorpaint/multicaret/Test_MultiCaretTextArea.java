package net.sourceforge.squirrel_sql.client.session.editorpaint.multicaret;

import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JTextArea;

public class Test_MultiCaretTextArea extends JTextArea
{

   private MultiCaretHandler _multiCaretHandler;

   @Override
   protected void paintComponent(Graphics g)
   {
      super.paintComponent(g);

      if(null != _multiCaretHandler)
      {
         _multiCaretHandler.onPaintComponent(g);
      }
   }

   public void setMultiCaretHandler(MultiCaretHandler multiCaretHandler)
   {
      _multiCaretHandler = multiCaretHandler;

      addKeyListener(new KeyAdapter() {
         @Override
         public void keyPressed(KeyEvent e)
         {
            onKeyPressed(e);
         }
      });

   }

   private void onKeyPressed(KeyEvent e)
   {
      if(isAddNextTrigger(e))
      {
         _multiCaretHandler.createNextCaret();
      }
      else if(isRemovePreviousTrigger(e))
      {
         _multiCaretHandler.removeLastCaret();
      }
   }

   private boolean isAddNextTrigger(KeyEvent e)
   {
      return e.getModifiersEx() == KeyEvent.ALT_DOWN_MASK && e.getKeyCode() == KeyEvent.VK_J;
   }

   private boolean isRemovePreviousTrigger(KeyEvent e)
   {
      return    (e.getModifiersEx() & KeyEvent.ALT_DOWN_MASK) == KeyEvent.ALT_DOWN_MASK
                && (e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) == KeyEvent.SHIFT_DOWN_MASK
                && e.getKeyCode() == KeyEvent.VK_J;
   }

}
