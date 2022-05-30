package net.sourceforge.squirrel_sql.client.session.editorpaint;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.editorpaint.multicaret.MultiCaretHandler;

import javax.swing.JTextArea;
import java.awt.Graphics;

public class TextAreaPaintHandler
{
   private MarkCurrentSqlHandler _markCurrentSqlHandler;
   private TextAreaPaintListener _textAreaPaintListener;

   private MultiCaretHandler _multiCaretHandler;


   public TextAreaPaintHandler(JTextArea editor, ISession session)
   {
      _markCurrentSqlHandler = new MarkCurrentSqlHandler(editor, session);
      _multiCaretHandler = new MultiCaretHandler(editor);
   }

   public void setTextAreaPaintListener(TextAreaPaintListener textAreaPaintListener)
   {
      _textAreaPaintListener = textAreaPaintListener;
   }

   public MarkCurrentSqlHandler getMarkCurrentSqlHandler()
   {
      return _markCurrentSqlHandler;
   }

   public void onPaint(Graphics g)
   {
      _markCurrentSqlHandler.paintMark(g);

      if(null != _textAreaPaintListener)
      {
         _textAreaPaintListener.paint();
      }
   }

   public void onPaintComponent(Graphics g)
   {
      _multiCaretHandler.onPaintComponent(g);
   }
}
