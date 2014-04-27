package org.squirrelsql.session.sql.syntax;

import org.fife.ui.rsyntaxtextarea.Token;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.StyleSpansBuilder;

import javax.swing.text.Segment;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class SQLSyntaxHighlighting
{
   public SQLSyntaxHighlighting(CodeArea sqlTextArea)
   {

      sqlTextArea.getStylesheets().add(getClass().getResource("sql-syntax.css").toExternalForm());

      sqlTextArea.textProperty().addListener((observable, oldText, newText) -> onTextPropertyChanged(newText, sqlTextArea));
   }

   private void onTextPropertyChanged(String sql, CodeArea codeArea)
   {
      try
      {
         ArrayList<String> lines = getLines(sql);

         ArrayList<TokenLine> tokenlines = new ArrayList<>();

         int initialTokenType = SquirrelTokenMakerBase.YYINITIAL;
         for (String line : lines)
         {
            TokenLine tokenLine = getTokenLine(line, initialTokenType);

            initialTokenType = tokenLine.getNextInitialTokenType();

            tokenlines.add(tokenLine);
         }


         StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();



         System.out.println("----------------------------------------------------------");

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
                  System.out.println("  EmptyLength=" + emptyLength);
               }

               int keywordLength = token.length();
               spansBuilder.add(Collections.singleton("keyword"), keywordLength);
               System.out.println("KeywordLength=" + keywordLength + "   " + token);

               lastTokenEnd = lineStart + token.getTextOffset() + keywordLength;
            }
            lineStart += tokenLine.getLineLength();
         }

         int endLength = sql.length() - lastTokenEnd;

         if (0 >= endLength)
         {
            spansBuilder.add(Collections.emptyList(), endLength);
         }

         codeArea.setStyleSpans(0, spansBuilder.create());

      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   private TokenLine getTokenLine(String line, int initialTokenType)
   {
      ArrayList<Token> tokens = new ArrayList<>();

      Token token = new SquirrelTokenMarker(new SQLSyntaxHighlightTokenMatcher()).getTokenList(new Segment(line.toCharArray(), 0, line.length()), initialTokenType, 0);

      while (null != token && 0 < token.length())
      {
         tokens.add(token);
         //System.out.println("token = " + token);

         token = token.getNextToken();
      }

      return new TokenLine(tokens, line, initialTokenType);
   }

   private ArrayList<String> getLines(String newText) throws IOException
   {
      ArrayList<String> lines;
      BufferedReader rdr = new BufferedReader(new StringReader(newText));
      lines = new ArrayList<String>();
      for (String line = rdr.readLine(); line != null; line = rdr.readLine())
      {
         lines.add(line);
      }
      rdr.close();
      return lines;
   }
}
