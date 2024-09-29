package net.sourceforge.squirrel_sql.client.session.defaultentry;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.PrioritizedCaretMouseListener;
import net.sourceforge.squirrel_sql.client.session.editorpaint.EditorPaintService;
import net.sourceforge.squirrel_sql.client.session.editorpaint.TextAreaPaintHandler;
import net.sourceforge.squirrel_sql.client.session.editorpaint.TextAreaPaintListener;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.gui.FontInfo;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

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

      SquirrelDefaultCaretWithPrioritizedMouseListener caret = new SquirrelDefaultCaretWithPrioritizedMouseListener();
      caret.setBlinkRate(getCaret().getBlinkRate());
      getCaret().deinstall(this);
      setCaret(caret);

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

   public void addTextAreaPaintListener(TextAreaPaintListener textAreaPaintListener)
   {
      _textAreaPaintHandler.addTextAreaPaintListener(textAreaPaintListener);
   }

   public TextAreaPaintHandler getTextAreaPaintHandler()
   {
      return _textAreaPaintHandler;
   }

   /**
    * This method is called, when text is pasted to the editor.
    * We replace non-breaking spaces by ordinary spaces because
    * non-breaking spaces keep reformatting from working.
    * See https://stackoverflow.com/questions/28295504/how-to-trim-no-break-space-in-java
    *
    * To reproduce the problem: Libre Office Writer allows to edit non-breaking spaces be ctrl+shift+space.
    */
   @Override
   public void replaceSelection(String text)
   {
      super.replaceSelection(StringUtilities.replaceNonBreakingSpacesBySpaces(text));
   }

   public void setPrioritizedCaretMouseListener(PrioritizedCaretMouseListener prioritizedCaretMouseListener)
   {
      ((SquirrelDefaultCaretWithPrioritizedMouseListener)getCaret()).setPrioritizedCaretMouseListener(prioritizedCaretMouseListener);
   }

   public void removeTextAreaPaintListener(TextAreaPaintListener textAreaPaintListener)
   {
      _textAreaPaintHandler.removeTextAreaPaintListener(textAreaPaintListener);
   }
}
