package org.squirrelsql.session.sql.syntax;

import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.stage.Popup;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.MouseOverTextEvent;
import org.squirrelsql.session.parser.kernel.ErrorInfo;
import org.squirrelsql.session.schemainfo.SchemaCache;

import javax.swing.text.Segment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SQLSyntaxHighlighting
{

   private final ErrorToolTipHandler _errorToolTipHandler;
   private ISyntaxHighlightTokenMatcher _syntaxHighlightTokenMatcher;
   private CodeArea _sqlTextArea;
   private SchemaCache _schemaCache;
   private LexAndParseResultListener _lexAndParseResultListener;

   private ErrorInfosHandler _errorInfosHandler = new ErrorInfosHandler();

   public SQLSyntaxHighlighting(CodeArea sqlTextArea, ISyntaxHighlightTokenMatcher syntaxHighlightTokenMatcher, SchemaCache schemaCache)
   {
      _sqlTextArea = sqlTextArea;
      _schemaCache = schemaCache;

      _sqlTextArea.getStylesheets().add(getClass().getResource("sql-syntax.css").toExternalForm());

      _sqlTextArea.textProperty().addListener((observable, oldText, newText) -> onTextPropertyChanged(newText));
      _syntaxHighlightTokenMatcher = syntaxHighlightTokenMatcher;

      _errorToolTipHandler = new ErrorToolTipHandler(_sqlTextArea);
   }

   private void onTextPropertyChanged(String sql)
   {
      try
      {
         List<String> lines = getLines(sql);

         List<TokenLine> tokenlines = new ArrayList<>();

         int initialTokenType = SquirrelTokenMakerBase.YYINITIAL;
         for (String line : lines)
         {
            TokenLine tokenLine = getTokenLine(line, initialTokenType);

            initialTokenType = tokenLine.getNextInitialTokenType();

            tokenlines.add(tokenLine);
         }


         StyleSpansBuilderWrapper spansBuilder = new StyleSpansBuilderWrapper();


         TableNextToCursorHandler tableNextToCursorHandler = new TableNextToCursorHandler(_lexAndParseResultListener, _sqlTextArea.getCaretPosition(), _schemaCache);


         //System.out.println("----------------------------------------------------------");

         int lastTokenEnd = 0;
         int lineStart = 0;

         for (int i = 0; i < tokenlines.size(); i++)
         {
            TokenLine tokenLine =  tokenlines.get(i);

            for (Token token : tokenLine.getTokens())
            {

               int emptyLength = lineStart + token.getTextOffset() - lastTokenEnd;
               if (0 < emptyLength)
               {
                  spansBuilder.add(Collections.emptyList(), emptyLength);
                  //System.out.println("  EmptyLength=" + emptyLength);
               }

               int keywordLength = token.length();
               spansBuilder.add(Collections.singleton(getTokenStyle(lineStart, token)), keywordLength);
               //System.out.println("KeywordLength=" + keywordLength + "   " + token);

               tableNextToCursorHandler.checkToken(lineStart, token);

               lastTokenEnd = lineStart + token.getTextOffset() + keywordLength;
            }
            lineStart += tokenLine.getLineLength();
         }

         int endLength = sql.length() - lastTokenEnd;

         if (0 >= endLength)
         {
            spansBuilder.add(Collections.emptyList(), endLength);
         }

         if (spansBuilder.hasSpans())
         {
            _sqlTextArea.setStyleSpans(0, spansBuilder.create());
         }

         tableNextToCursorHandler.fireTableNextToCursor();

      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   private String getTokenStyle(int lineStart, Token token)
   {
      if(_errorInfosHandler.isError(lineStart, token))
      {
         return _errorInfosHandler.getErrorStyle();
      }

      return TokenToCssStyleMapper.getTokenStyle(token);
   }


   private TokenLine getTokenLine(String line, int initialTokenType)
   {
      List<Token> tokens = new ArrayList<>();

      Token token = new SquirrelTokenMarker(_syntaxHighlightTokenMatcher).getTokenList(new Segment(line.toCharArray(), 0, line.length()), initialTokenType, 0);

      while (null != token && 0 < token.length())
      {
         tokens.add(token);
         //System.out.println("token = " + token);

         token = token.getNextToken();
      }

      return new TokenLine(tokens, line, initialTokenType);
   }

   private List<String> getLines(String newText) throws IOException
   {
      List<String> lines = new ArrayList<>();
      BufferedReader rdr = new BufferedReader(new StringReader(newText));
      for (String line = rdr.readLine(); line != null; line = rdr.readLine())
      {
         lines.add(line);
      }
      rdr.close();
      return lines;
   }

   public void setLexAndParseResultListener(LexAndParseResultListener lexAndParseResultListener)
   {
      _lexAndParseResultListener = lexAndParseResultListener;
   }

   public void setErrorInfos(ErrorInfo[] errorInfos)
   {
      if(_errorInfosHandler.setErrorInfos(errorInfos))
      {
         onTextPropertyChanged(_sqlTextArea.getText());
         _errorToolTipHandler.setErrorInfos(errorInfos);
      }
   }
}
