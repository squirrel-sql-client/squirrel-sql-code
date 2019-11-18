package net.sourceforge.squirrel_sql.client.session;

import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.gui.FontInfo;

import javax.swing.JTextArea;
import java.awt.Graphics;

class SquirrelDefaultTextArea extends JTextArea
{

   private MarkCurrentSqlHandler _markCurrentSqlHandler;

   SquirrelDefaultTextArea(ISession session)
   {
      SessionProperties props = session.getProperties();
      final FontInfo fi = props.getFontInfo();
      if (fi != null)
      {
         this.setFont(props.getFontInfo().createFont());
      }

      _markCurrentSqlHandler = new MarkCurrentSqlHandler(this, session);


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
      _markCurrentSqlHandler.paintMark(g);
   }

   public void setMarkCurrentSQLActive(boolean b)
   {
      _markCurrentSqlHandler.setActive(b);
   }
}
