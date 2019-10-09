package net.sourceforge.squirrel_sql.plugins.syntax.rsyntax;

import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;

public class RSyntaxUtil
{
   /**
    * Couldn't find another way of triggering highlighting update on the positions
    * of current and former errors.
    *
    * Time will tell how stable this is.
    */
   static void forceHighlightUpdate(SquirrelRSyntaxTextArea squirrelRSyntaxTextArea)
   {
      RSyntaxDocument doc = (RSyntaxDocument) squirrelRSyntaxTextArea.getDocument();
      doc.setSyntaxStyle(doc.getSyntaxStyle());
   }
}
