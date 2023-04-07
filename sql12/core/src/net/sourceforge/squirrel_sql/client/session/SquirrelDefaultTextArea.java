package net.sourceforge.squirrel_sql.client.session;

import net.sourceforge.squirrel_sql.client.session.editorpaint.EditorPaintService;
import net.sourceforge.squirrel_sql.client.session.editorpaint.TextAreaPaintHandler;
import net.sourceforge.squirrel_sql.client.session.editorpaint.TextAreaPaintListener;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.gui.FontInfo;

import javax.swing.JTextArea;
import java.awt.Graphics;

public class SquirrelDefaultTextArea extends JTextArea
{
   private TextAreaPaintHandler _textAreaPaintHandler;

   SquirrelDefaultTextArea(ISession session)
   {
      SessionProperties props = session.getProperties();
      final FontInfo fi = props.getFontInfo();
      if (fi != null)
      {
         this.setFont(props.getFontInfo().createFont());
      }

      _textAreaPaintHandler = new TextAreaPaintHandler(this, EditorPaintService.EMPTY, session);


      /////////////////////////////////////////////////////////////////////
      // To prevent the caret from being hidden by the current SQL mark
      putClientProperty("caretWidth", 3);
      //
      ////////////////////////////////////////////////////////////////////
   }

   @Override
   public void paint(Graphics g)
   {
      super.paint(g);
      _textAreaPaintHandler.onPaint(g);
   }

   @Override
   protected void paintComponent(Graphics g)
   {
      super.paintComponent(g);
      _textAreaPaintHandler.onPaintComponent(g);
   }

   public void setMarkCurrentSQLActive(boolean b)
   {
      _textAreaPaintHandler.getMarkCurrentSqlHandler().setActive(b);
   }

   public void setTextAreaPaintListener(TextAreaPaintListener textAreaPaintListener)
   {
      _textAreaPaintHandler.setTextAreaPaintListener(textAreaPaintListener);
   }

   public TextAreaPaintHandler getTextAreaPaintHandler()
   {
      return _textAreaPaintHandler;
   }
}
