package net.sourceforge.squirrel_sql.client.session.editorpaint;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.editorpaint.multicaret.MultiCaretHandler;

import javax.swing.JTextArea;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class TextAreaPaintHandler
{
   private MarkCurrentSqlHandler _markCurrentSqlHandler;
   private MultiCaretHandler _multiCaretHandler;
   private List<TextAreaPaintListener> _textAreaPaintListeners = new ArrayList<>();


   public TextAreaPaintHandler(JTextArea editor, EditorPaintService paintService, ISession session)
   {
      _markCurrentSqlHandler = new MarkCurrentSqlHandler(editor, session);
      _multiCaretHandler = new MultiCaretHandler(editor, paintService);
   }

   public void addTextAreaPaintListener(TextAreaPaintListener textAreaPaintListener)
   {
      _textAreaPaintListeners.remove(textAreaPaintListener);
      _textAreaPaintListeners.add(textAreaPaintListener);
   }

   public void removeTextAreaPaintListener(TextAreaPaintListener textAreaPaintListener)
   {
      _textAreaPaintListeners.remove(textAreaPaintListener);
   }


   public MarkCurrentSqlHandler getMarkCurrentSqlHandler()
   {
      return _markCurrentSqlHandler;
   }

   public void onPaint(Graphics g)
   {
      _markCurrentSqlHandler.paintMark(g);

      for(TextAreaPaintListener listener : _textAreaPaintListeners.toArray(new TextAreaPaintListener[0]))
      {
         listener.paint();
      }
   }

   public void onPaintComponent(Graphics g)
   {
      _multiCaretHandler.onPaintComponent(g);
   }

   public MultiCaretHandler getMultiCaretHandler()
   {
      return _multiCaretHandler;
   }
}
