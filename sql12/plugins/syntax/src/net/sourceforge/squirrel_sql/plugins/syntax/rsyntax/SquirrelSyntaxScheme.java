package net.sourceforge.squirrel_sql.plugins.syntax.rsyntax;

import net.sourceforge.squirrel_sql.fw.gui.FontInfo;
import org.fife.ui.rsyntaxtextarea.Style;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.Token;

import javax.swing.text.StyleContext;
import java.awt.*;
import java.lang.reflect.Field;

import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPreferences;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxStyle;

public class SquirrelSyntaxScheme extends SyntaxScheme
{
   public SquirrelSyntaxScheme()
   {
      super(true);
   }

   public void initSytles(SyntaxPreferences prefs, FontInfo fontInfo)
   {
      super.restoreDefaults(fontInfo.createFont());
      Style[] stylesBuf = new Style[SquirrelTokenMarker.getNumTokenTypes()];
      System.arraycopy(super.getStyles(), 0, stylesBuf, 0, super.getStyles().length);

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

      super.setStyles(stylesBuf);
   }

   private Style createRSyntaxStyle(SyntaxStyle squirrelStyle, Font boldFont, Font italicFont)
   {
      Style style;

      ////////////////////////////////////////////////////////////
      // With RSyntax version 2.5.6 Marking find results
      // does not work anymore when Styles have background colors.
      // So we set all background colors to null.
      // The same is done for the default styles in org.fife.ui.rsyntaxtextarea.SyntaxScheme#restoreDefaults()
      //
      //Color bg = new Color(squirrelStyle.getBackgroundRGB());
      Color bg = null;
      //
      // Excerpts from mails:
      // Gerd Wagner to Robert Futrell on Jun 22, 2015:
      // 2. If one defines his own styles using SyntaxScheme.setStyles() the mark all finds feature does not work for Styles that set background colors.
      //
      // Robert Futrell to Gerd Wagner on Jun 23, 2015:
      // For the second issue, yes, that is a known problem I have to work on.
      // Custom background colors on tokens don't play nicely with Mark All or Mark Occurrences (I believe).
      // It's on the to-do list.
      //
      /////////////////////////////////////////////////////////////

      if (squirrelStyle.isBold())
      {
         style = new Style(new Color(squirrelStyle.getTextRGB()), bg, boldFont);
      }
      else if (squirrelStyle.isItalic())
      {
         style = new Style(new Color(squirrelStyle.getTextRGB()), bg, italicFont);
      }
      else
      {
         style = new Style(new Color(squirrelStyle.getTextRGB()), bg, null);
      }

      return style;
   }

}
