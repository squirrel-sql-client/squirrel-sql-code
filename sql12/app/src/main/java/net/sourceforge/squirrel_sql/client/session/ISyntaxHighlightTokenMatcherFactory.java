package net.sourceforge.squirrel_sql.client.session;

import javax.swing.*;
import javax.swing.text.JTextComponent;

public interface ISyntaxHighlightTokenMatcherFactory
{
   public ISyntaxHighlightTokenMatcher getSyntaxHighlightTokenMatcher(ISession sess, JTextComponent editorPane);
}
