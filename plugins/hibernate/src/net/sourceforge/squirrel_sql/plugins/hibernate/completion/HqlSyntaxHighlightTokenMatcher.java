package net.sourceforge.squirrel_sql.plugins.hibernate.completion;

import net.sourceforge.squirrel_sql.client.session.ISyntaxHighlightTokenMatcher;
import net.sourceforge.squirrel_sql.client.session.SQLTokenListener;

import java.util.ArrayList;

public class HqlSyntaxHighlightTokenMatcher implements ISyntaxHighlightTokenMatcher
{
   private HQLCompletionInfoCollection _hqlCompletionInfoCollection;
   private ArrayList<SQLTokenListener> _listeners = new ArrayList<SQLTokenListener>();

   public HqlSyntaxHighlightTokenMatcher(HQLCompletionInfoCollection hqlCompletionInfoCollection)
   {
      _hqlCompletionInfoCollection = hqlCompletionInfoCollection;
   }


   @Override
   public boolean isError(int offset, int len)
   {
      return false;
   }

   public boolean isTable(char[] buffer, int offset, int len)
   {
      String classNameCandidate = getString(buffer, offset, len);
      boolean ret = _hqlCompletionInfoCollection.isMappeadClass(classNameCandidate);

      if(ret)
      {
         fireClassFound(classNameCandidate);
      }

      return ret;
   }

   public boolean isFunction(char[] buffer, int offset, int len)
   {
      return _hqlCompletionInfoCollection.isFunction(getString(buffer, offset, len));
   }

   public boolean isDataType(char[] buffer, int offset, int len)
   {
      return false;
   }

   public boolean isStatementSeparator(char[] buffer, int offset, int len)
   {
      return false;
   }

   public boolean isColumn(char[] buffer, int offset, int len)
   {
      return _hqlCompletionInfoCollection.isMappedAttribute(getString(buffer, offset, len));
   }

   public boolean isKeyword(char[] buffer, int offset, int len)
   {
      return _hqlCompletionInfoCollection.isKeyword(getString(buffer, offset, len));
   }

   public void addSQLTokenListener(SQLTokenListener tl)
   {
      _listeners.add(tl);
   }

   public void removeSQLTokenListener(SQLTokenListener tl)
   {
      _listeners.remove(tl);
   }


   private String getString(char[] buffer, int offset, int len)
   {
      return new String(buffer, offset, len);
   }


   private void fireClassFound(String className)
   {
      for (SQLTokenListener listener : _listeners)
      {
         listener.tableOrViewFound(className);
      }
   }
}
