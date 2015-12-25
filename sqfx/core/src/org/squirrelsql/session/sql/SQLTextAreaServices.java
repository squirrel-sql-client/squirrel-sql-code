package org.squirrelsql.session.sql;

import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.IndexRange;
import javafx.scene.input.KeyEvent;
import org.fxmisc.richtext.CodeArea;
import org.squirrelsql.AppState;
import org.squirrelsql.services.Utils;
import org.squirrelsql.session.CaretVicinityInfo;
import org.squirrelsql.session.SessionTabContext;
import org.squirrelsql.session.completion.joingenerator.JoinGeneratorProvider;
import org.squirrelsql.session.parser.ParserEventsListener;
import org.squirrelsql.session.parser.ParserEventsProcessor;
import org.squirrelsql.session.parser.kernel.ErrorInfo;
import org.squirrelsql.session.parser.kernel.TableAliasInfo;
import org.squirrelsql.session.schemainfo.SchemaCacheProperty;
import org.squirrelsql.session.sql.syntax.*;
import org.squirrelsql.workaround.CodeAreaRepaintWA;
import org.squirrelsql.workaround.FocusSqlTextAreaWA;

public class SQLTextAreaServices
{

   private final CodeArea _sqlTextArea;
   private final SQLSyntaxHighlighting _sqlSyntaxHighlighting;
   private final ParserEventsProcessor _parserEventsProcessor;
   private CurrentSqlMarker _currentSqlMarker;
   private LexAndParseResultListener _lexAndParseResultListener;

   private CaretPopup _caretPopup;


   public SQLTextAreaServices(SessionTabContext sessionTabContext)
   {
      _sqlTextArea = new CodeArea();

      SchemaCacheProperty schemaCacheValue = sessionTabContext.getSession().getSchemaCacheValue();

      _parserEventsProcessor = new ParserEventsProcessor(this, sessionTabContext.getSession());

      _parserEventsProcessor.addParserEventsListener(new ParserEventsListener()
      {
         @Override
         public void aliasesFound(TableAliasInfo[] aliasInfos)
         {
            onAliasesFound(aliasInfos);
         }

         @Override
         public void errorsFound(ErrorInfo[] errorInfos)
         {
            _sqlSyntaxHighlighting.setErrorInfos(errorInfos);
         }
      });

      _sqlSyntaxHighlighting = new SQLSyntaxHighlighting(_sqlTextArea, new SQLSyntaxHighlightTokenMatcher(schemaCacheValue), schemaCacheValue);

      new CtrlLeftRightHandler(_sqlTextArea);
      new DoubleClickHandler(_sqlTextArea);

      schemaCacheValue.addListener(() -> updateHighlighting());

      _caretPopup = new CaretPopup(_sqlTextArea);


      if (AppState.get().getSettingsManager().getSettings().isMarkCurrentSQL())
      {
         _currentSqlMarker = new CurrentSqlMarker(this);
      }
   }

   public void updateHighlighting()
   {
      _sqlSyntaxHighlighting.updateHighlighting();
      _parserEventsProcessor.triggerParser();
   }

   private void onAliasesFound(TableAliasInfo[] aliasInfos)
   {
      if(null != _lexAndParseResultListener)
      {
         _lexAndParseResultListener.aliasesFound(aliasInfos);
      }
   }

   public CodeArea getTextArea()
   {
      return _sqlTextArea;
   }

   public void setOnKeyPressed(EventHandler<KeyEvent> keyEventHandler)
   {
      _sqlTextArea.setOnKeyPressed(keyEventHandler);
   }

   public void setOnKeyTyped(EventHandler<KeyEvent> keyEventHandler)
   {
      _sqlTextArea.setOnKeyTyped(keyEventHandler);
   }

   public String getCurrentSql()
   {
      String sql = _sqlTextArea.getSelectedText();

      if(false == hasSelection())
      {
         CaretBounds caretBounds = getCurrentSqlCaretBounds();
         sql = _sqlTextArea.getText().substring(caretBounds.begin, caretBounds.end);
      }

      return sql;
   }

   public CaretBounds getCurrentSqlCaretBounds()
   {
      CaretBounds caretBounds = new CaretBounds();

      IndexRange selection = _sqlTextArea.getSelection();
      if(0 < selection.getLength())
      {
         caretBounds.begin = selection.getStart();
         caretBounds.end = selection.getEnd();
         return caretBounds;
      }

      int caretPosition = _sqlTextArea.getCaretPosition();


      ////////////////////////////////////////////////////////
      // Remove this to have the former, less strict
      // interpretation of current SQL.
      if(isLineAtCaretEmpty())
      {
         CaretBounds ret = new CaretBounds();
         ret.begin = caretPosition;
         ret.end = caretPosition;
         return ret;
      }
      //
      ///////////////////////////////////////////////////////


      String sqlTextAreaText = _sqlTextArea.getText();

      if(0 == sqlTextAreaText.length())
      {
         caretBounds.begin = -1;
         caretBounds.end = -1;
         return caretBounds;
      }

      caretBounds.begin = caretPosition;
      while(false == isStatementBegin(sqlTextAreaText, caretBounds.begin))
      {
         --caretBounds.begin;
      }

      caretBounds.end = caretPosition;
      while(false == isStatementEnd(sqlTextAreaText, caretBounds.end))
      {
         ++caretBounds.end;
      }
      return caretBounds;
   }

   private boolean isLineAtCaretEmpty()
   {
      int caretPosition = _sqlTextArea.getCaretPosition();
      String text = _sqlTextArea.getText();

      if(0 == text.length())
      {
         return false;
      }



      if(0 == caretPosition)
      {
         return SyntaxConstants.CODE_AREA_LINE_SEP_CAR == text.charAt(caretPosition);
      }

      if(text.length() <= caretPosition)
      {
         return text.endsWith(SyntaxConstants.CODE_AREA_LINE_SEP);
      }


      return '\n' == text.charAt(caretPosition) && '\n' == text.charAt(caretPosition - 1);
   }

   private boolean isStatementEnd(String sqlTextAreaText, int pos)
   {
      String textBehindPos = sqlTextAreaText.substring(pos);

      return sqlTextAreaText.length() == pos
             || Utils.isEmptyString(textBehindPos)
             || textBehindPos.startsWith(SyntaxConstants.SQL_SEPARATOR);
   }

   private boolean isStatementBegin(String sqlTextAreaText, int pos)
   {
      String textBeforePos = sqlTextAreaText.substring(0, pos);

      return 0 == pos
            || Utils.isEmptyString(textBeforePos)
            || textBeforePos.endsWith(SyntaxConstants.SQL_SEPARATOR);
   }

   public void replaceCurrentSql(String replacement)
   {
      replaceCurrentSql(replacement, false);
   }

   public void replaceCurrentSql(String replacement, boolean selectReplacement)
   {
      CaretBounds currentSqlCaretBounds = getCurrentSqlCaretBounds();

      _sqlTextArea.selectRange(currentSqlCaretBounds.begin, currentSqlCaretBounds.end);

      _sqlTextArea.replaceSelection(replacement);

      if(selectReplacement)
      {
         _sqlTextArea.selectRange(currentSqlCaretBounds.begin, currentSqlCaretBounds.begin + replacement.length());
      }
   }

   public boolean hasSelection()
   {
      String sql = _sqlTextArea.getSelectedText();

      return false == Utils.isEmptyString(sql);
   }


   public String getTokenTillCaret(char... stopsToIgnore)
   {
      return _getCaretVicinityInfo(c -> isTokenEndChar(c, stopsToIgnore)).getTokenTillCaret();
   }

   private boolean isTokenEndChar(char c, char... stopsToIgnore)
   {
      return WordBoundaryCheck.isInStopAtArrayOrWhiteSpace(c, stopsToIgnore);
   }

   public String getLineTillCaret()
   {
      return _getCaretVicinityInfo(c -> c == '\n').getTokenTillCaret();
   }

   private CaretVicinityInfo _getCaretVicinityInfo(TokenDelimiterCheck tokenDelimiterCheck)
   {
      int caretPosition = _sqlTextArea.getCaretPosition();
      String sqlTextAreaText = _sqlTextArea.getText();


      int begin = caretPosition-1;
      while(0 < begin && false == tokenDelimiterCheck.isTokenDelimiter(sqlTextAreaText.charAt(begin)))
      {
         --begin;
      }

      if(0 < begin)
      {
         begin +=1; // keep the whitespace
      }

      int end = caretPosition;
      while(sqlTextAreaText.length() - 1 >= end && false == tokenDelimiterCheck.isTokenDelimiter(sqlTextAreaText.charAt(end)))
      {
         ++end;
      }

      begin = Math.max(begin, 0);
      String tokenTillCaret = sqlTextAreaText.substring(begin, caretPosition).trim();
      String tokenAtCaret = sqlTextAreaText.substring(begin, end).trim();

      return new CaretVicinityInfo(tokenTillCaret, tokenAtCaret, begin, end, caretPosition);
   }


   public void replaceTokenAtCaretBy(String replacement)
   {
      replaceTokenAtCaretBy(0, false, replacement);
   }

   public void replaceJoinGeneratorAtCaretBy(String replacement)
   {
      CaretVicinityInfo tci = _getCaretVicinityInfo( c -> c == JoinGeneratorProvider.GENERATOR_START);

      _sqlTextArea.replaceText(tci.getTokenBeginPos() - 1, tci.getCaretPosition(), replacement);
   }


   public void replaceTokenAtCaretBy(int offset, boolean removeSucceedingChars, String replacement)
   {
      TokenDelimiterCheck tokenDelimiterCheck = c -> isTokenEndChar(c);
      CaretVicinityInfo tci = _getCaretVicinityInfo(tokenDelimiterCheck);

      if (removeSucceedingChars)
      {
         _sqlTextArea.replaceText(tci.getTokenBeginPos() + offset, tci.getTokenEndPos(), replacement);
      }
      else
      {
         _sqlTextArea.replaceText(tci.getTokenBeginPos() + offset, tci.getCaretPosition(), replacement);
      }
   }


   public double getFontHight()
   {
      if( 0 > _sqlTextArea.getFont().getSize())
      {
         return 12;
      }
      else
      {
         return _sqlTextArea.getFont().getSize();
      }
   }

   public void requestFocus()
   {
      FocusSqlTextAreaWA.forceFocus(_sqlTextArea);
   }

   public double getStringWidth(String str)
   {
      FontMetrics fontMetrics = Toolkit.getToolkit().getFontLoader().getFontMetrics(_sqlTextArea.getFont());
      return fontMetrics.computeStringWidth(str);
   }

   public void setLexAndParseResultListener(LexAndParseResultListener lexAndParseResultListener)
   {
      _lexAndParseResultListener = lexAndParseResultListener;
      _sqlSyntaxHighlighting.setLexAndParseResultListener(lexAndParseResultListener);
   }

   public void close()
   {
      _parserEventsProcessor.endProcessing();
   }

   public void appendToEditor(String sql)
   {
      _sqlTextArea.appendText(sql);
      CodeAreaRepaintWA.avoidRepaintProblemsAfterTextModification(_sqlTextArea);

   }

   public javafx.scene.text.Font getFont()
   {
      return _sqlTextArea.getFont();
   }

   public void insertAtCarret(String s)
   {
      int caretPosition = _sqlTextArea.getCaretPosition();
      _sqlTextArea.replaceText(caretPosition,caretPosition,s);
      CodeAreaRepaintWA.avoidRepaintProblemsAfterTextModification(_sqlTextArea);
   }

   public CaretPopup getCaretPopup()
   {
      return _caretPopup;
   }

   public String getTokenAtCaret()
   {
      return _getCaretVicinityInfo(c -> isTokenEndChar(c)).getTokenAtCaret();
   }

   public void setText(String s)
   {
      _sqlTextArea.clear();
      _sqlTextArea.appendText(s);
   }

   public void clear()
   {
      _sqlTextArea.clear();
   }

   public void calculateTableNextToCaret()
   {
      _sqlSyntaxHighlighting.calculateTableNextToCaret();
   }

   public void replaceSelection(String replacement, boolean selectReplacement)
   {
      if(selectReplacement)
      {
         IndexRange selection = _sqlTextArea.getSelection();

         _sqlTextArea.replaceSelection(replacement);

         _sqlTextArea.selectRange(selection.getStart(), selection.getStart() + replacement.length());

      }
      else
      {
         _sqlTextArea.replaceSelection(replacement);
      }
   }

   public Node getTextAreaNode()
   {
      if (null != _currentSqlMarker)
      {
         return _currentSqlMarker.getTextAreaStackPane();
      }
      else
      {
         return _sqlTextArea;
      }
   }

}
