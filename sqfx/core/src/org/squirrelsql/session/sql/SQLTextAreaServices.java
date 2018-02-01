package org.squirrelsql.session.sql;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.IndexRange;
import javafx.scene.input.KeyEvent;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.squirrelsql.AppState;
import org.squirrelsql.services.Utils;
import org.squirrelsql.session.CaretVicinityInfo;
import org.squirrelsql.session.Session;
import org.squirrelsql.session.completion.joingenerator.JoinGeneratorProvider;
import org.squirrelsql.session.parser.ParserEventsListener;
import org.squirrelsql.session.parser.ParserEventsProcessor;
import org.squirrelsql.session.parser.kernel.ErrorInfo;
import org.squirrelsql.session.parser.kernel.TableAliasInfo;
import org.squirrelsql.session.schemainfo.SchemaCacheProperty;
import org.squirrelsql.session.sql.syntax.CaretPopup;
import org.squirrelsql.session.sql.syntax.LexAndParseResultListener;
import org.squirrelsql.session.sql.syntax.SQLSyntaxHighlightTokenMatcher;
import org.squirrelsql.session.sql.syntax.SQLSyntaxHighlighting;
import org.squirrelsql.session.sql.syntax.SyntaxConstants;
import org.squirrelsql.workaround.CodeAreaRepaintWA;
import org.squirrelsql.workaround.FocusNodeWA;
import org.squirrelsql.workaround.RichTextFxWA;
import org.squirrelsql.workaround.StringWidthWA;

public class SQLTextAreaServices
{

   private final VirtualizedScrollPane<CodeArea> _sqlTextAreaVirtualScroll;
   private final SQLSyntaxHighlighting _sqlSyntaxHighlighting;
   private final ParserEventsProcessor _parserEventsProcessor;
   private CurrentSqlMarker _currentSqlMarker;
   private LexAndParseResultListener _lexAndParseResultListener;

   private CaretPopup _caretPopup;


   public SQLTextAreaServices(Session session)
   {
      this(session, true);
   }

   public SQLTextAreaServices(Session session, boolean highlightErrors)
   {
      _sqlTextAreaVirtualScroll = new VirtualizedScrollPane<>(new CodeArea());

      SchemaCacheProperty schemaCacheValue = session.getSchemaCacheValue();

      _parserEventsProcessor = new ParserEventsProcessor(this, session);

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
            if (highlightErrors)
            {
               _sqlSyntaxHighlighting.setErrorInfos(errorInfos);
            }
         }
      });

      _sqlSyntaxHighlighting = new SQLSyntaxHighlighting(_sqlTextAreaVirtualScroll.getContent(), new SQLSyntaxHighlightTokenMatcher(schemaCacheValue), schemaCacheValue);

      new CtrlLeftRightHandler(_sqlTextAreaVirtualScroll.getContent());
      new DoubleClickHandler(_sqlTextAreaVirtualScroll.getContent());
      new TabHandler(this);

      schemaCacheValue.addListener(() -> updateHighlighting());

      _caretPopup = new CaretPopup(_sqlTextAreaVirtualScroll.getContent());


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
      return _sqlTextAreaVirtualScroll.getContent();
   }

   public VirtualizedScrollPane<CodeArea> getTextAreaVirtualScroll()
   {
      return _sqlTextAreaVirtualScroll;
   }


   public void setOnKeyPressed(EventHandler<KeyEvent> keyEventHandler)
   {
      _sqlTextAreaVirtualScroll.getContent().setOnKeyPressed(keyEventHandler);
   }

   public void setOnKeyTyped(EventHandler<KeyEvent> keyEventHandler)
   {
      _sqlTextAreaVirtualScroll.getContent().setOnKeyTyped(keyEventHandler);
   }

   public String getCurrentSql()
   {
      String sql = _sqlTextAreaVirtualScroll.getContent().getSelectedText();

      if(false == hasSelection())
      {
         CaretBounds caretBounds = getCurrentSqlCaretBounds();
         sql = _sqlTextAreaVirtualScroll.getContent().getText().substring(caretBounds.begin, caretBounds.end);
      }

      return sql;
   }

   public CaretBounds getCurrentSqlCaretBounds()
   {
      CaretBounds caretBounds = new CaretBounds();

      IndexRange selection = _sqlTextAreaVirtualScroll.getContent().getSelection();
      if(0 < selection.getLength())
      {
         caretBounds.begin = selection.getStart();
         caretBounds.end = selection.getEnd();
         return caretBounds;
      }

      int caretPosition = _sqlTextAreaVirtualScroll.getContent().getCaretPosition();


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


      String sqlTextAreaText = _sqlTextAreaVirtualScroll.getContent().getText();

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
      int caretPosition = _sqlTextAreaVirtualScroll.getContent().getCaretPosition();
      String text = _sqlTextAreaVirtualScroll.getContent().getText();

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

      _sqlTextAreaVirtualScroll.getContent().selectRange(currentSqlCaretBounds.begin, currentSqlCaretBounds.end);

      _sqlTextAreaVirtualScroll.getContent().replaceSelection(replacement);

      if(selectReplacement)
      {
         _sqlTextAreaVirtualScroll.getContent().selectRange(currentSqlCaretBounds.begin, currentSqlCaretBounds.begin + replacement.length());
      }
   }

   public void replaceLineAtCaret(String replacement)
   {
      CaretVicinityInfo cvi = _getCaretVicinityInfo(c -> c == '\n');

      _sqlTextAreaVirtualScroll.getContent().replaceText(cvi.getTokenBeginPos(), cvi.getTokenEndPos(), replacement);
      _sqlTextAreaVirtualScroll.getContent().moveTo(cvi.getTokenBeginPos());

   }


   public boolean hasSelection()
   {
      String sql = _sqlTextAreaVirtualScroll.getContent().getSelectedText();

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

   public String getLineAtCaret()
   {
      return _getCaretVicinityInfo(c -> c == '\n', false).getTokenAtCaret();
   }


   private CaretVicinityInfo _getCaretVicinityInfo(TokenDelimiterCheck tokenDelimiterCheck)
   {
      return _getCaretVicinityInfo(tokenDelimiterCheck, true);
   }

   private CaretVicinityInfo _getCaretVicinityInfo(TokenDelimiterCheck tokenDelimiterCheck, boolean trimmed)
   {
      int caretPosition = _sqlTextAreaVirtualScroll.getContent().getCaretPosition();
      String sqlTextAreaText = _sqlTextAreaVirtualScroll.getContent().getText();


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
      String tokenTillCaret = sqlTextAreaText.substring(begin, caretPosition);
      String tokenAtCaret = sqlTextAreaText.substring(begin, end);

      if(trimmed)
      {
         tokenTillCaret = tokenTillCaret.trim();
         tokenAtCaret = tokenAtCaret.trim();
      }

      return new CaretVicinityInfo(tokenTillCaret, tokenAtCaret, begin, end, caretPosition);
   }


   public void replaceTokenAtCaretBy(String replacement)
   {
      replaceTokenAtCaretBy(0, false, replacement);
   }

   public void replaceJoinGeneratorAtCaretBy(String replacement)
   {
      CaretVicinityInfo tci = _getCaretVicinityInfo( c -> c == JoinGeneratorProvider.GENERATOR_START);

      _sqlTextAreaVirtualScroll.getContent().replaceText(tci.getTokenBeginPos() - 1, tci.getCaretPosition(), replacement);
   }


   public void replaceTokenAtCaretBy(int offset, boolean removeSucceedingChars, String replacement)
   {
      TokenDelimiterCheck tokenDelimiterCheck = c -> isTokenEndChar(c);
      CaretVicinityInfo tci = _getCaretVicinityInfo(tokenDelimiterCheck);

      if (removeSucceedingChars)
      {
         _sqlTextAreaVirtualScroll.getContent().replaceText(tci.getTokenBeginPos() + offset, tci.getTokenEndPos(), replacement);
      }
      else
      {
         _sqlTextAreaVirtualScroll.getContent().replaceText(tci.getTokenBeginPos() + offset, tci.getCaretPosition(), replacement);
      }
   }


   public double getFontHight()
   {
      if( 0 > RichTextFxWA.getFont(_sqlTextAreaVirtualScroll.getContent()).getSize())
      {
         return 12;
      }
      else
      {
         return RichTextFxWA.getFont(_sqlTextAreaVirtualScroll.getContent()).getSize();
      }
   }

   public void requestFocus()
   {
      FocusNodeWA.forceFocus(_sqlTextAreaVirtualScroll.getContent());
   }

   public double getStringWidth(String str)
   {
      return StringWidthWA.computeTextWidth(RichTextFxWA.getFont(_sqlTextAreaVirtualScroll.getContent()), str);
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
      _sqlTextAreaVirtualScroll.getContent().appendText(sql);
      CodeAreaRepaintWA.avoidRepaintProblemsAfterTextModification(_sqlTextAreaVirtualScroll.getContent());

   }

   public javafx.scene.text.Font getFont()
   {
      return RichTextFxWA.getFont(_sqlTextAreaVirtualScroll.getContent());
   }

   public void insertAtCarret(String s)
   {
      int caretPosition = _sqlTextAreaVirtualScroll.getContent().getCaretPosition();
      _sqlTextAreaVirtualScroll.getContent().replaceText(caretPosition,caretPosition,s);
      CodeAreaRepaintWA.avoidRepaintProblemsAfterTextModification(_sqlTextAreaVirtualScroll.getContent());
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
      _sqlTextAreaVirtualScroll.getContent().clear();
      _sqlTextAreaVirtualScroll.getContent().appendText(s);
   }

   public void clear()
   {
      _sqlTextAreaVirtualScroll.getContent().clear();
   }

   public void calculateTableNextToCaret()
   {
      _sqlSyntaxHighlighting.calculateTableNextToCaret();
   }

   public void replaceSelection(String replacement, boolean selectReplacement)
   {
      if(selectReplacement)
      {
         IndexRange selection = _sqlTextAreaVirtualScroll.getContent().getSelection();

         _sqlTextAreaVirtualScroll.getContent().replaceSelection(replacement);

         _sqlTextAreaVirtualScroll.getContent().selectRange(selection.getStart(), selection.getStart() + replacement.length());

      }
      else
      {
         _sqlTextAreaVirtualScroll.getContent().replaceSelection(replacement);
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
         return _sqlTextAreaVirtualScroll;
      }
   }

   public void setEditable(boolean b)
   {
      _sqlTextAreaVirtualScroll.getContent().setEditable(b);
   }
}
