package net.sourceforge.squirrel_sql.client.session;

import javax.swing.*;

public interface ISyntaxHighlightTokenMatcherFactory
{
   public ISyntaxHighlightTokenMatcher getSyntaxHighlightTokenMatcher(ISession sess, JEditorPane editorPane);
}
