package net.sourceforge.squirrel_sql.plugins.syntax.rsyntax;

import net.sourceforge.squirrel_sql.client.session.ISyntaxHighlightTokenMatcher;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLTokenListener;
import net.sourceforge.squirrel_sql.client.session.parser.IParserEventsProcessor;
import net.sourceforge.squirrel_sql.client.session.parser.ParserEventsAdapter;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.ErrorInfo;
import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfo;
import net.sourceforge.squirrel_sql.client.session.schemainfo.CaseInsensitiveString;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;

import javax.swing.*;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Arrays;

public class RSyntaxHighlightTokenMatcher implements ISyntaxHighlightTokenMatcher
{
   private ISession _sess;
   private SquirrelRSyntaxTextArea _squirrelRSyntaxTextArea;

   private CaseInsensitiveString _caseInsensitiveStringBuffer = new CaseInsensitiveString();

   private Hashtable<CaseInsensitiveString, String> _knownTables =
       new Hashtable<CaseInsensitiveString, String>();


   private Vector<SQLTokenListener> _sqlTokenListeners = new Vector<SQLTokenListener>();

   private Vector<ErrorInfo> _currentErrorInfos = new Vector<ErrorInfo>();


   public RSyntaxHighlightTokenMatcher(ISession sess, SquirrelRSyntaxTextArea squirrelRSyntaxTextArea, IParserEventsProcessor parserEventsProcessor)
   {
   }

   public RSyntaxHighlightTokenMatcher(ISession sess, SquirrelRSyntaxTextArea squirrelRSyntaxTextArea, final IIdentifier sqlEntryPanelIdentifier, final RSyntaxPropertiesWrapper rSyntaxPropertiesWrapper)
   {
      _sess = sess;
      _squirrelRSyntaxTextArea = squirrelRSyntaxTextArea;

      SwingUtilities.invokeLater(new Runnable()
      {
         // Invoke later because of initialization problems when getParserEventsProcessor() is called directly from the constructor.
         public void run()
         {
            initParsing(rSyntaxPropertiesWrapper, sqlEntryPanelIdentifier);
         }
      });
   }

   private void initParsing(RSyntaxPropertiesWrapper rSyntaxPropertiesWrapper, IIdentifier sqlEntryPanelIdentifier)
   {
      IParserEventsProcessor parserEventsProcessor = rSyntaxPropertiesWrapper.getParserEventsProcessor(sqlEntryPanelIdentifier, _sess);
      if (null != parserEventsProcessor)
      {
         parserEventsProcessor.addParserEventsListener(new ParserEventsAdapter()
         {
            @Override
            public void errorsFound(ErrorInfo[] errorInfos)
            {
               onErrorsFound(errorInfos);
            }
         });
      }
   }


   private void onErrorsFound(ErrorInfo[] errorInfos)
   {
      boolean errorsChanged = false;
      if(_currentErrorInfos.size() == errorInfos.length)
      {
         for (int i = 0; i < errorInfos.length; i++)
         {
            if(false == errorInfos[i].equals(_currentErrorInfos.get(i)))
            {
               errorsChanged = true;
               break;
            }
         }
      }
      else
      {
         errorsChanged = true;
      }

      if(errorsChanged)
      {
         _currentErrorInfos.clear();
         _currentErrorInfos.addAll(Arrays.asList(errorInfos));
         _squirrelRSyntaxTextArea.repaint();
      }
   }

   @Override
   public boolean isError(int offset, int len)
   {
      //System.out.println("Error: offset = " + offset + " len = " + len);
      for (int i = 0; i < _currentErrorInfos.size(); i++)
      {
         ErrorInfo errInf = _currentErrorInfos.elementAt(i);

         if(offset <= errInf.beginPos && errInf.endPos <= offset + len)
         {
            return true;
         }
      }
      return false;
   }

   public boolean isTable(char[] buffer, int offset, int len)
   {
      _caseInsensitiveStringBuffer.setCharBuffer(buffer, offset, len);

      // No new here, method is called very often

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
            _squirrelRSyntaxTextArea.repaint();
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