package net.sourceforge.squirrel_sql.plugins.syntax.rsyntax;

import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.gui.FontInfo;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Style;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.Token;

import javax.swing.text.StyleContext;
import java.awt.*;

import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPreferences;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxStyle;
import net.sourceforge.squirrel_sql.client.session.parser.IParserEventsProcessor;
import net.sourceforge.squirrel_sql.client.session.parser.ParserEventsListener;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.TableAliasInfo;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.ErrorInfo;

public class SquirrelSyntaxScheme extends SyntaxScheme
{
   public SquirrelSyntaxScheme()
   {
      super(true);
   }

   public void initSytles(SyntaxPreferences prefs, FontInfo fontInfo)
   {
      super.restoreDefaults();
      Style[] stylesBuf = new Style[SquirrelTokenMarker.getNumTokenTypes()];
      System.arraycopy(styles, 0, stylesBuf, 0, styles.length);

      StyleContext sc = StyleContext.getDefaultStyleContext();


      Font boldFont = sc.getFont(fontInfo.getFamily(), Font.BOLD, fontInfo.getSize());
      Font italicFont = sc.getFont(fontInfo.getFamily(), Font.ITALIC, fontInfo.getSize());


//      stylesBuf[SquirrelTokenMarker.TOKEN_IDENTIFIER_TABLE] = new Style(Color.green, null);
//      stylesBuf[SquirrelTokenMarker.TOKEN_IDENTIFIER_DATA_TYPE] = new Style(new Color(178,178,0), null, boldFont);
//      stylesBuf[SquirrelTokenMarker.TOKEN_IDENTIFIER_COLUMN] = new Style(new Color(102,102,0), null, boldFont);
//      stylesBuf[SquirrelTokenMarker.TOKEN_IDENTIFIER_FUNCTION] = new Style(Color.gray, null, italicFont);
//      stylesBuf[SquirrelTokenMarker.TOKEN_IDENTIFIER_STATEMENT_SEPARATOR] = new Style(new Color(0,0,128), null, italicFont);

      stylesBuf[Token.RESERVED_WORD] = createRSyntaxStyle(prefs.getReservedWordStyle(), boldFont, italicFont);
      stylesBuf[SquirrelTokenMarker.TOKEN_IDENTIFIER_TABLE] = createRSyntaxStyle(prefs.getTableStyle(), boldFont, italicFont);
      stylesBuf[SquirrelTokenMarker.TOKEN_IDENTIFIER_DATA_TYPE] = createRSyntaxStyle(prefs.getDataTypeStyle(), boldFont, italicFont);
      stylesBuf[SquirrelTokenMarker.TOKEN_IDENTIFIER_COLUMN] = createRSyntaxStyle(prefs.getColumnStyle(), boldFont, italicFont);
      stylesBuf[SquirrelTokenMarker.TOKEN_IDENTIFIER_FUNCTION] = createRSyntaxStyle(prefs.getFunctionStyle(), boldFont, italicFont);
      stylesBuf[SquirrelTokenMarker.TOKEN_IDENTIFIER_STATEMENT_SEPARATOR] = createRSyntaxStyle(prefs.getSeparatorStyle(), boldFont, italicFont);
      stylesBuf[Token.IDENTIFIER] = createRSyntaxStyle(prefs.getIdentifierStyle(), boldFont, italicFont);
      stylesBuf[Token.COMMENT_EOL] = createRSyntaxStyle(prefs.getCommentStyle(), boldFont, italicFont);
      stylesBuf[Token.COMMENT_MULTILINE] = createRSyntaxStyle(prefs.getCommentStyle(), boldFont, italicFont);
      stylesBuf[Token.COMMENT_DOCUMENTATION] = createRSyntaxStyle(prefs.getCommentStyle(), boldFont, italicFont);
      stylesBuf[Token.LITERAL_STRING_DOUBLE_QUOTE] = createRSyntaxStyle(prefs.getLiteralStyle(), boldFont, italicFont);
      stylesBuf[Token.OPERATOR] = createRSyntaxStyle(prefs.getOperatorStyle(), boldFont, italicFont);
      stylesBuf[Token.WHITESPACE] = createRSyntaxStyle(prefs.getWhiteSpaceStyle(), boldFont, italicFont);
//      stylesBuf[SquirrelTokenMarker.TOKEN_IDENTIFIER_ERROR] = createRSyntaxStyle(prefs.getErrorStyle(), boldFont, italicFont);
      stylesBuf[Token.ERROR_IDENTIFIER] = createRSyntaxStyle(prefs.getErrorStyle(), boldFont, italicFont);
      stylesBuf[Token.ERROR_NUMBER_FORMAT] = createRSyntaxStyle(prefs.getErrorStyle(), boldFont, italicFont);
      stylesBuf[Token.ERROR_STRING_DOUBLE] = createRSyntaxStyle(prefs.getErrorStyle(), boldFont, italicFont);

      styles = stylesBuf;
   }

   private Style createRSyntaxStyle(SyntaxStyle squirrelStyle, Font boldFont, Font italicFont)
   {
      Style style;

      if (squirrelStyle.isBold())
      {
         style = new Style(new Color(squirrelStyle.getTextRGB()), new Color(squirrelStyle.getBackgroundRGB()), boldFont);
      }
      else if (squirrelStyle.isItalic())
      {
         style = new Style(new Color(squirrelStyle.getTextRGB()), new Color(squirrelStyle.getBackgroundRGB()), italicFont);
      }
      else
      {
         style = new Style(new Color(squirrelStyle.getTextRGB()), new Color(squirrelStyle.getBackgroundRGB()), null);
      }

      return style;
   }




   public Object clone()
   {
      SyntaxScheme shcs = null;
      shcs = (SyntaxScheme) super.clone();

      shcs.styles = new Style[SquirrelTokenMarker.getNumTokenTypes()];
      for (int i = 0; i < SquirrelTokenMarker.getNumTokenTypes(); i++)
      {
         Style s = styles[i];
         if (s != null)
         {
            shcs.styles[i] = (Style) s.clone();
         }
      }
      return shcs;
   }

}
