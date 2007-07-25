package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.client.session.ISyntaxHighlightTokenMatcher;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLTokenListener;

import javax.swing.*;

public class HqlSyntaxHighlightTokenMatcherProxy implements ISyntaxHighlightTokenMatcher
{
   private ISyntaxHighlightTokenMatcher _delegate;
   private JEditorPane _editorPane;

   public boolean isTable(char[] buffer, int offset, int len)
   {
      if(null == _delegate)
      {
         return false;
      }

      return _delegate.isTable(buffer, offset, len);
   }

   public boolean isFunction(char[] buffer, int offset, int len)
   {
      if(null == _delegate)
      {
         return false;
      }

      return _delegate.isFunction(buffer, offset, len);
   }

   public boolean isDataType(char[] buffer, int offset, int len)
   {
      if(null == _delegate)
      {
         return false;
      }

      return _delegate.isDataType(buffer, offset, len);
   }

   public boolean isStatementSeparator(char[] buffer, int offset, int len)
   {
      if(null == _delegate)
      {
         return false;
      }

      return _delegate.isStatementSeparator(buffer, offset, len);
   }

   public boolean isColumn(char[] buffer, int offset, int len)
   {
      if(null == _delegate)
      {
         return false;
      }

      return _delegate.isColumn(buffer, offset, len);
   }

   public boolean isKeyword(char[] buffer, int offset, int len)
   {
      if(null == _delegate)
      {
         return false;
      }

      return _delegate.isKeyword(buffer, offset, len);
   }

   public void removeSQLTokenListener(SQLTokenListener tl)
   {
   }

   public void addSQLTokenListener(SQLTokenListener tl)
   {
   }

   public void setDelegate(ISyntaxHighlightTokenMatcher delegate)
   {
      _delegate = delegate;
      if(null != _editorPane)
      {
         _editorPane.repaint();
      }
   }

   public void setEditorPane(JEditorPane editorPane)
   {
      _editorPane = editorPane;
      _editorPane.repaint();
   }
}
