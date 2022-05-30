package net.sourceforge.squirrel_sql.client.session.editorpaint.multicaret;

import javax.swing.JTextArea;
import java.awt.Graphics;

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
   }
}
