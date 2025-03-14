package net.sourceforge.squirrel_sql.client.session.action.syntax.theme;

import net.sourceforge.squirrel_sql.client.session.action.syntax.ISyntaxConstants;
import net.sourceforge.squirrel_sql.client.session.action.syntax.SyntaxPreferences;

import java.awt.Color;

public class SyntaxThemeFactory
{

   public static final Color SYNTAX_DARK_THEME_BACKGROUND_COLOR = new Color(60, 63, 65);

   public static SyntaxTheme createDefaultLightTheme()
   {
      SyntaxTheme ret = new SyntaxTheme();

      ret.getColumnStyle().setName(ISyntaxConstants.IStyleNames.COLUMN);
      ret.getColumnStyle().setBackgroundRGB(Color.white.getRGB());
      ret.getColumnStyle().setTextRGB(new Color(102, 102, 0).getRGB());
      ret.getColumnStyle().setBold(false);
      ret.getColumnStyle().setItalic(false);

      ret.getCommentStyle().setName(ISyntaxConstants.IStyleNames.COMMENT);
      ret.getCommentStyle().setBackgroundRGB(Color.white.getRGB());
      ret.getCommentStyle().setTextRGB(Color.lightGray.darker().getRGB());
      ret.getCommentStyle().setBold(false);
      ret.getCommentStyle().setItalic(false);

      ret.getDataTypeStyle().setName(ISyntaxConstants.IStyleNames.DATA_TYPE);
      ret.getDataTypeStyle().setBackgroundRGB(Color.white.getRGB());
      ret.getDataTypeStyle().setTextRGB(Color.yellow.darker().getRGB());
      ret.getDataTypeStyle().setBold(false);
      ret.getDataTypeStyle().setItalic(false);

      ret.getErrorStyle().setName(ISyntaxConstants.IStyleNames.ERROR);
      ret.getErrorStyle().setBackgroundRGB(Color.white.getRGB());
      ret.getErrorStyle().setTextRGB(Color.red.getRGB());
      ret.getErrorStyle().setBold(false);
      ret.getErrorStyle().setItalic(false);

      ret.getFunctionStyle().setName(ISyntaxConstants.IStyleNames.FUNCTION);
      ret.getFunctionStyle().setBackgroundRGB(Color.white.getRGB());
      ret.getFunctionStyle().setTextRGB(Color.black.getRGB());
      ret.getFunctionStyle().setBold(false);
      ret.getFunctionStyle().setItalic(false);

      ret.getIdentifierStyle().setName(ISyntaxConstants.IStyleNames.IDENTIFIER);
      ret.getIdentifierStyle().setBackgroundRGB(Color.white.getRGB());
      ret.getIdentifierStyle().setTextRGB(Color.black.getRGB());
      ret.getIdentifierStyle().setBold(false);
      ret.getIdentifierStyle().setItalic(false);

      ret.getLiteralStyle().setName(ISyntaxConstants.IStyleNames.LITERAL);
      ret.getLiteralStyle().setBackgroundRGB(Color.white.getRGB());
      ret.getLiteralStyle().setTextRGB(new Color(176,48, 96).getRGB());
      ret.getLiteralStyle().setBold(false);
      ret.getLiteralStyle().setItalic(false);

      ret.getOperatorStyle().setName(ISyntaxConstants.IStyleNames.OPERATOR);
      ret.getOperatorStyle().setBackgroundRGB(Color.white.getRGB());
      ret.getOperatorStyle().setTextRGB(Color.black.getRGB());
      ret.getOperatorStyle().setBold(true);
      ret.getOperatorStyle().setItalic(false);

      ret.getReservedWordStyle().setName(ISyntaxConstants.IStyleNames.RESERVED_WORD);
      ret.getReservedWordStyle().setBackgroundRGB(Color.white.getRGB());
      ret.getReservedWordStyle().setTextRGB(Color.blue.getRGB());
      ret.getReservedWordStyle().setBold(false);
      ret.getReservedWordStyle().setItalic(false);

      ret.getSeparatorStyle().setName(ISyntaxConstants.IStyleNames.SEPARATOR);
      ret.getSeparatorStyle().setBackgroundRGB(Color.white.getRGB());
      ret.getSeparatorStyle().setTextRGB(new Color(0, 0, 128).getRGB()); // Navy.
      ret.getSeparatorStyle().setBold(false);
      ret.getSeparatorStyle().setItalic(false);

      ret.getTableStyle().setName(ISyntaxConstants.IStyleNames.TABLE);
      ret.getTableStyle().setBackgroundRGB(Color.white.getRGB());
      ret.getTableStyle().setTextRGB(new Color(0,133,0).getRGB());
      ret.getTableStyle().setBold(false);
      ret.getTableStyle().setItalic(false);

      ret.getWhiteSpaceStyle().setName(ISyntaxConstants.IStyleNames.WHITESPACE);
      ret.getWhiteSpaceStyle().setBackgroundRGB(Color.white.getRGB());
      ret.getWhiteSpaceStyle().setTextRGB(Color.black.getRGB());
      ret.getWhiteSpaceStyle().setBold(false);
      ret.getWhiteSpaceStyle().setItalic(false);

      ret.setCaretColorRGB(SyntaxPreferences.NO_COLOR);
      ret.setCurrentLineHighlightColorRGB(new Color(255,255,170).getRGB());

      return ret;
   }

   public static SyntaxTheme createDarkTheme()
   {
      SyntaxTheme ret = new SyntaxTheme();

      ret.getColumnStyle().setName(ISyntaxConstants.IStyleNames.COLUMN);
      ret.getColumnStyle().setBackgroundRGB(SYNTAX_DARK_THEME_BACKGROUND_COLOR.getRGB());
      ret.getColumnStyle().setTextRGB(Color.green.getRGB());
      ret.getColumnStyle().setBold(false);
      ret.getColumnStyle().setItalic(false);

      ret.getCommentStyle().setName(ISyntaxConstants.IStyleNames.COMMENT);
      ret.getCommentStyle().setBackgroundRGB(SYNTAX_DARK_THEME_BACKGROUND_COLOR.getRGB());
      ret.getCommentStyle().setTextRGB(Color.lightGray.darker().getRGB());
      ret.getCommentStyle().setBold(false);
      ret.getCommentStyle().setItalic(false);

      ret.getDataTypeStyle().setName(ISyntaxConstants.IStyleNames.DATA_TYPE);
      ret.getDataTypeStyle().setBackgroundRGB(SYNTAX_DARK_THEME_BACKGROUND_COLOR.getRGB());
      ret.getDataTypeStyle().setTextRGB(Color.yellow.darker().getRGB());
      ret.getDataTypeStyle().setBold(false);
      ret.getDataTypeStyle().setItalic(false);

      ret.getErrorStyle().setName(ISyntaxConstants.IStyleNames.ERROR);
      ret.getErrorStyle().setBackgroundRGB(SYNTAX_DARK_THEME_BACKGROUND_COLOR.getRGB());
      ret.getErrorStyle().setTextRGB(Color.red.brighter().getRGB());
      ret.getErrorStyle().setBold(false);
      ret.getErrorStyle().setItalic(false);

      ret.getFunctionStyle().setName(ISyntaxConstants.IStyleNames.FUNCTION);
      ret.getFunctionStyle().setBackgroundRGB(SYNTAX_DARK_THEME_BACKGROUND_COLOR.getRGB());
      ret.getFunctionStyle().setTextRGB(Color.white.getRGB());
      ret.getFunctionStyle().setBold(false);
      ret.getFunctionStyle().setItalic(false);

      ret.getIdentifierStyle().setName(ISyntaxConstants.IStyleNames.IDENTIFIER);
      ret.getIdentifierStyle().setBackgroundRGB(SYNTAX_DARK_THEME_BACKGROUND_COLOR.getRGB());
      ret.getIdentifierStyle().setTextRGB(Color.white.getRGB());
      ret.getIdentifierStyle().setBold(false);
      ret.getIdentifierStyle().setItalic(false);

      ret.getLiteralStyle().setName(ISyntaxConstants.IStyleNames.LITERAL);
      ret.getLiteralStyle().setBackgroundRGB(SYNTAX_DARK_THEME_BACKGROUND_COLOR.getRGB());
      ret.getLiteralStyle().setTextRGB(new Color(216,88, 136).getRGB());
      ret.getLiteralStyle().setBold(false);
      ret.getLiteralStyle().setItalic(false);

      ret.getOperatorStyle().setName(ISyntaxConstants.IStyleNames.OPERATOR);
      ret.getOperatorStyle().setBackgroundRGB(SYNTAX_DARK_THEME_BACKGROUND_COLOR.getRGB());
      ret.getOperatorStyle().setTextRGB(Color.white.getRGB());
      ret.getOperatorStyle().setBold(true);
      ret.getOperatorStyle().setItalic(false);

      ret.getReservedWordStyle().setName(ISyntaxConstants.IStyleNames.RESERVED_WORD);
      ret.getReservedWordStyle().setBackgroundRGB(SYNTAX_DARK_THEME_BACKGROUND_COLOR.getRGB());
      ret.getReservedWordStyle().setTextRGB(new Color(0, 208, 255).getRGB());
      ret.getReservedWordStyle().setBold(false);
      ret.getReservedWordStyle().setItalic(false);

      ret.getSeparatorStyle().setName(ISyntaxConstants.IStyleNames.SEPARATOR);
      ret.getSeparatorStyle().setBackgroundRGB(SYNTAX_DARK_THEME_BACKGROUND_COLOR.getRGB());
      ret.getSeparatorStyle().setTextRGB(new Color(150, 150, 228).getRGB());
      ret.getSeparatorStyle().setBold(false);
      ret.getSeparatorStyle().setItalic(false);

      ret.getTableStyle().setName(ISyntaxConstants.IStyleNames.TABLE);
      ret.getTableStyle().setBackgroundRGB(SYNTAX_DARK_THEME_BACKGROUND_COLOR.getRGB());
      ret.getTableStyle().setTextRGB(new Color(183, 231, 137).getRGB());
      ret.getTableStyle().setBold(false);
      ret.getTableStyle().setItalic(false);

      ret.getWhiteSpaceStyle().setName(ISyntaxConstants.IStyleNames.WHITESPACE);
      ret.getWhiteSpaceStyle().setBackgroundRGB(SYNTAX_DARK_THEME_BACKGROUND_COLOR.getRGB());
      ret.getWhiteSpaceStyle().setTextRGB(Color.black.getRGB());
      ret.getWhiteSpaceStyle().setBold(false);
      ret.getWhiteSpaceStyle().setItalic(false);

      ret.setCaretColorRGB(Color.white.getRGB());
      ret.setCurrentLineHighlightColorRGB(Color.darkGray.getRGB());

      return ret;
   }
}
