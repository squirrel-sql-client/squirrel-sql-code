package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs;

import javax.swing.text.JTextComponent;

import net.sourceforge.squirrel_sql.client.util.codereformat.CodeReformator;
import net.sourceforge.squirrel_sql.client.util.codereformat.CodeReformatorConfigFactory;
import net.sourceforge.squirrel_sql.client.util.codereformat.CommentSpec;
import net.sourceforge.squirrel_sql.client.util.codereformat.ICodeReformator;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class FormattedSourceTabCore
{
   private final static ILogger s_log = LoggerController.createLogger(FormattedSourceTabCore.class);

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(FormattedSourceTabCore.class);


   /**
    * does the work of formatting
    */
   private ICodeReformator _formatter = null;

   /**
    * whether or not to compress whitespace
    */
   private boolean _compressWhitespace = true;

   private CommentSpec[] _commentSpecs =
         new CommentSpec[]{new CommentSpec("/*", "*/"), new CommentSpec("--", "\n")};

   /**
    * The String to use to separate statements
    */
   private String _statementSeparator = null;


   /**
    * Whether or not to appendSeparator before reformatting
    */
   private boolean appendSeparator = true;


   void setupFormatter(String stmtSep, CommentSpec[] commentSpecs)
   {
      setupFormatter(null, stmtSep, commentSpecs);
   }

   void setupFormatter(ICodeReformator codeReformator, String stmtSep, CommentSpec[] commentSpecs)
   {
      if( commentSpecs != null )
      {
         this._commentSpecs = commentSpecs;
      }
      _statementSeparator = stmtSep;

      _formatter = codeReformator;
      if( null == _formatter )
      {
         _formatter = new CodeReformator(CodeReformatorConfigFactory.createConfig(stmtSep, this._commentSpecs));
      }
   }


   void setCompressWhitespace(boolean compressWhitespace)
   {
      _compressWhitespace = compressWhitespace;
   }

   boolean isCompressWhitespace()
   {
      return _compressWhitespace;
   }

   /**
    * We trap any IllegalStateException from the formatter here. If the SQL source code fails to format, log
    * it and show the original unformatted version.
    *
    * @param toFormat the SQL to format.
    * @return either formatted or original version of the specified SQL.
    */
   String format(String toFormat)
   {
      String result = toFormat;
      try
      {
         result = _formatter.reformat(toFormat);
      }
      catch(IllegalStateException e)
      {
         s_log.error("format: Formatting SQL failed: " + e.getMessage(), e);
      }
      return result;
   }


   void loadTextArea(JTextComponent textArea, String sourceCode)
   {
      textArea.setText("");

      if (appendSeparator && (_statementSeparator != null))
      {
         sourceCode += "\n";
         sourceCode += _statementSeparator;
      }
      String processedResult = sourceCode;
      if (_formatter != null && sourceCode.length() != 0)
      {
         textArea.setText(format(processedResult));
      }
      else
      {
         if (sourceCode.length() == 0)
         {
            sourceCode += s_stringMgr.getString("FormatterSourceTab.noSourceAvailable");
         }
         textArea.setText(sourceCode);
      }
      textArea.setCaretPosition(0);
   }

   boolean isAppendSeparator()
   {
      return appendSeparator;
   }
   void setAppendSeparator(boolean appendSeparator)
   {
      this.appendSeparator = appendSeparator;
   }

   public String getStatementSeparator()
   {
      return _statementSeparator;
   }
}
