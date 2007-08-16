package net.sourceforge.squirrel_sql.plugins.syntax.netbeans;

import net.sourceforge.squirrel_sql.client.session.ISyntaxHighlightTokenMatcher;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLTokenListener;
import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfo;
import net.sourceforge.squirrel_sql.client.session.schemainfo.CaseInsensitiveString;

import java.util.Hashtable;
import java.util.Vector;

public class SqlSyntaxHighlightTokenMatcher implements ISyntaxHighlightTokenMatcher
{
   private ISession _sess;
   private NetbeansSQLEditorPane _editorPane;

   private CaseInsensitiveString _caseInsensitiveStringBuffer = new CaseInsensitiveString();

   private Hashtable<CaseInsensitiveString, String> _knownTables = 
       new Hashtable<CaseInsensitiveString, String>();


   private Vector<SQLTokenListener> _sqlTokenListeners = 
       new Vector<SQLTokenListener>();


   public SqlSyntaxHighlightTokenMatcher(ISession sess, NetbeansSQLEditorPane editorPane)
   {

      _sess = sess;
      _editorPane = editorPane;
   }

   public boolean isTable(char[] buffer, int offset, int len)
   {
      _caseInsensitiveStringBuffer.setCharBuffer(buffer, offset, len);

      // No new here, method is called very often
      //if(_sess.getSchemaInfo().isTable(s))

      int tableExtRes = _sess.getSchemaInfo().isTableExt(_caseInsensitiveStringBuffer);
      if(SchemaInfo.TABLE_EXT_COLS_LOADED_BEFORE == tableExtRes || SchemaInfo.TABLE_EXT_COLS_LOADED_IN_THIS_CALL == tableExtRes)
      {

         // _knownTables is just a cache to prevent creating a new String each time
         String table = _knownTables.get(_caseInsensitiveStringBuffer);
         if(null == table)
         {
            table = new String(buffer, offset, len);
            _knownTables.put(new CaseInsensitiveString(table), table);
         }

         if(SchemaInfo.TABLE_EXT_COLS_LOADED_IN_THIS_CALL == tableExtRes)
         {
            _editorPane.repaint();
         }

         fireTableOrViewFound(table);

         return true;
      }
      return false;
   }

   private void fireTableOrViewFound(String tableOrViewName)
   {
      for (int i = 0; i < _sqlTokenListeners.size(); i++)
      {
         SQLTokenListener sqlTokenListener = _sqlTokenListeners.elementAt(i);
         sqlTokenListener.tableOrViewFound(tableOrViewName);
      }
   }

   public void addSQLTokenListener(SQLTokenListener tl)
   {
      removeSQLTokenListener(tl);
      _sqlTokenListeners.add(tl);
   }

   public boolean isFunction(char[] buffer, int offset, int len)
   {
      _caseInsensitiveStringBuffer.setCharBuffer(buffer, offset, len);


      if(_sess.getSchemaInfo().isFunction(_caseInsensitiveStringBuffer))
      {
         return true;
      }
      if(_sess.getSchemaInfo().isProcedure(_caseInsensitiveStringBuffer))
      {
         return true;
      }

      return false;
   }

   public boolean isDataType(char[] buffer, int offset, int len)
   {
      _caseInsensitiveStringBuffer.setCharBuffer(buffer, offset, len);

      if(_sess.getSchemaInfo().isDataType(_caseInsensitiveStringBuffer))
      {
         return true;
      }
      return false;
   }

   public boolean isStatementSeparator(char[] buffer, int offset, int len)
   {
      _caseInsensitiveStringBuffer.setCharBuffer(buffer, offset, len);

      String statSep = _sess.getQueryTokenizer().getSQLStatementSeparator();

      if(statSep.length() != len)
      {
         return false;
      }

      // no new here, method is called very often.
      for(int i=0; i < statSep.length(); ++i)
      {
         if(buffer[offset +i] != statSep.charAt(i))
         {
            return false;
         }
      }

      return true;
   }

   public boolean isColumn(char[] buffer, int offset, int len)
   {
      // No new here, method is called very often
      //String s = new String(buffer, offset, len);

      _caseInsensitiveStringBuffer.setCharBuffer(buffer, offset, len);

      if(_sess.getSchemaInfo().isColumn(_caseInsensitiveStringBuffer))
      {
         return true;
      }
      return false;
   }

   public boolean isKeyword(char[] buffer, int offset, int len)
   {
      // No new here, method is called very often
      // String s = new String(buffer, offset, len);

      _caseInsensitiveStringBuffer.setCharBuffer(buffer, offset, len);
      if(_sess.getSchemaInfo().isKeyword(_caseInsensitiveStringBuffer))
      {
         return true;
      }
      return false;

   }

   public void removeSQLTokenListener(SQLTokenListener tl)
   {
      _sqlTokenListeners.remove(tl);
   }


}
