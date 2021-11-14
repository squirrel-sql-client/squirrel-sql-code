package net.sourceforge.squirrel_sql.plugins.syntax.theme;

import net.sourceforge.squirrel_sql.plugins.syntax.IConstants;

import java.awt.Color;

import static net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPreferences.NO_COLOR;

public class SyntaxThemeFactory
{
   public static SyntaxTheme createDefaultLightTheme()
   {
      SyntaxTheme ret = new SyntaxTheme();

      ret.getColumnStyle().setName(IConstants.IStyleNames.COLUMN);
      ret.getColumnStyle().setBackgroundRGB(Color.white.getRGB());
      ret.getColumnStyle().setTextRGB(new Color(102, 102, 0).getRGB());
      ret.getColumnStyle().setBold(false);
      ret.getColumnStyle().setItalic(false);

      ret.getCommentStyle().setName(IConstants.IStyleNames.COMMENT);
      ret.getCommentStyle().setBackgroundRGB(Color.white.getRGB());
      ret.getCommentStyle().setTextRGB(Color.lightGray.darker().getRGB());
      ret.getCommentStyle().setBold(false);
      ret.getCommentStyle().setItalic(false);

      ret.getDataTypeStyle().setName(IConstants.IStyleNames.DATA_TYPE);
      ret.getDataTypeStyle().setBackgroundRGB(Color.white.getRGB());
      ret.getDataTypeStyle().setTextRGB(Color.yellow.darker().getRGB());
      ret.getDataTypeStyle().setBold(false);
      ret.getDataTypeStyle().setItalic(false);

      ret.getErrorStyle().setName(IConstants.IStyleNames.ERROR);
      ret.getErrorStyle().setBackgroundRGB(Color.white.getRGB());
      ret.getErrorStyle().setTextRGB(Color.red.getRGB());
      ret.getErrorStyle().setBold(false);
      ret.getErrorStyle().setItalic(false);

      ret.getFunctionStyle().setName(IConstants.IStyleNames.FUNCTION);
      ret.getFunctionStyle().setBackgroundRGB(Color.white.getRGB());
      ret.getFunctionStyle().setTextRGB(Color.black.getRGB());
      ret.getFunctionStyle().setBold(false);
      ret.getFunctionStyle().setItalic(false);

      ret.getIdentifierStyle().setName(IConstants.IStyleNames.IDENTIFIER);
      ret.getIdentifierStyle().setBackgroundRGB(Color.white.getRGB());
      ret.getIdentifierStyle().setTextRGB(Color.black.getRGB());
      ret.getIdentifierStyle().setBold(false);
      ret.getIdentifierStyle().setItalic(false);

      ret.getLiteralStyle().setName(IConstants.IStyleNames.LITERAL);
      ret.getLiteralStyle().setBackgroundRGB(Color.white.getRGB());
      ret.getLiteralStyle().setTextRGB(new Color(176,48, 96).getRGB());
      ret.getLiteralStyle().setBold(false);
      ret.getLiteralStyle().setItalic(false);

      ret.getOperatorStyle().setName(IConstants.IStyleNames.OPERATOR);
      ret.getOperatorStyle().setBackgroundRGB(Color.white.getRGB());
      ret.getOperatorStyle().setTextRGB(Color.black.getRGB());
      ret.getOperatorStyle().setBold(true);
      ret.getOperatorStyle().setItalic(false);

      ret.getReservedWordStyle().setName(IConstants.IStyleNames.RESERVED_WORD);
      ret.getReservedWordStyle().setBackgroundRGB(Color.white.getRGB());
      ret.getReservedWordStyle().setTextRGB(Color.blue.getRGB());
      ret.getReservedWordStyle().setBold(false);
      ret.getReservedWordStyle().setItalic(false);

      ret.getSeparatorStyle().setName(IConstants.IStyleNames.SEPARATOR);
      ret.getSeparatorStyle().setBackgroundRGB(Color.white.getRGB());
      ret.getSeparatorStyle().setTextRGB(new Color(0, 0, 128).getRGB()); // Navy.
      ret.getSeparatorStyle().setBold(false);
      ret.getSeparatorStyle().setItalic(false);

      ret.getTableStyle().setName(IConstants.IStyleNames.TABLE);
      ret.getTableStyle().setBackgroundRGB(Color.white.getRGB());
      ret.getTableStyle().setTextRGB(new Color(0,133,0).getRGB());
      ret.getTableStyle().setBold(false);
      ret.getTableStyle().setItalic(false);

      ret.getWhiteSpaceStyle().setName(IConstants.IStyleNames.WHITESPACE);
      ret.getWhiteSpaceStyle().setBackgroundRGB(Color.white.getRGB());
      ret.getWhiteSpaceStyle().setTextRGB(Color.black.getRGB());
      ret.getWhiteSpaceStyle().setBold(false);
      ret.getWhiteSpaceStyle().setItalic(false);

      ret.setCaretColorRGB(NO_COLOR);
      ret.setCurrentLineHighlightColorRGB(new Color(255,255,170).getRGB());

      return ret;
   }

   public static SyntaxTheme createDarkTheme()
   {
      SyntaxTheme ret = new SyntaxTheme();

      ret.getColumnStyle().setName(IConstants.IStyleNames.COLUMN);
      ret.getColumnStyle().setBackgroundRGB(new Color(60,63,65).getRGB());
      ret.getColumnStyle().setTextRGB(Color.green.getRGB());
      ret.getColumnStyle().setBold(false);
      ret.getColumnStyle().setItalic(false);

      ret.getCommentStyle().setName(IConstants.IStyleNames.COMMENT);
      ret.getCommentStyle().setBackgroundRGB(new Color(60,63,65).getRGB());
      ret.getCommentStyle().setTextRGB(Color.lightGray.darker().getRGB());
      ret.getCommentStyle().setBold(false);
      ret.getCommentStyle().setItalic(false);

      ret.getDataTypeStyle().setName(IConstants.IStyleNames.DATA_TYPE);
      ret.getDataTypeStyle().setBackgroundRGB(new Color(60,63,65).getRGB());
      ret.getDataTypeStyle().setTextRGB(Color.yellow.darker().getRGB());
      ret.getDataTypeStyle().setBold(false);
      ret.getDataTypeStyle().setItalic(false);

      ret.getErrorStyle().setName(IConstants.IStyleNames.ERROR);
      ret.getErrorStyle().setBackgroundRGB(new Color(60,63,65).getRGB());
      ret.getErrorStyle().setTextRGB(Color.red.brighter().getRGB());
      ret.getErrorStyle().setBold(false);
      ret.getErrorStyle().setItalic(false);

      ret.getFunctionStyle().setName(IConstants.IStyleNames.FUNCTION);
      ret.getFunctionStyle().setBackgroundRGB(new Color(60,63,65).getRGB());
      ret.getFunctionStyle().setTextRGB(Color.white.getRGB());
      ret.getFunctionStyle().setBold(false);
      ret.getFunctionStyle().setItalic(false);

      ret.getIdentifierStyle().setName(IConstants.IStyleNames.IDENTIFIER);
      ret.getIdentifierStyle().setBackgroundRGB(new Color(60,63,65).getRGB());
      ret.getIdentifierStyle().setTextRGB(Color.white.getRGB());
      ret.getIdentifierStyle().setBold(false);
      ret.getIdentifierStyle().setItalic(false);

      ret.getLiteralStyle().setName(IConstants.IStyleNames.LITERAL);
      ret.getLiteralStyle().setBackgroundRGB(new Color(60,63,65).getRGB());
      ret.getLiteralStyle().setTextRGB(new Color(216,88, 136).getRGB());
      ret.getLiteralStyle().setBold(false);
      ret.getLiteralStyle().setItalic(false);

      ret.getOperatorStyle().setName(IConstants.IStyleNames.OPERATOR);
      ret.getOperatorStyle().setBackgroundRGB(new Color(60,63,65).getRGB());
      ret.getOperatorStyle().setTextRGB(Color.white.getRGB());
      ret.getOperatorStyle().setBold(true);
      ret.getOperatorStyle().setItalic(false);

      ret.getReservedWordStyle().setName(IConstants.IStyleNames.RESERVED_WORD);
      ret.getReservedWordStyle().setBackgroundRGB(new Color(60,63,65).getRGB());
      ret.getReservedWordStyle().setTextRGB(new Color(0, 208, 255).getRGB());
      ret.getReservedWordStyle().setBold(false);
      ret.getReservedWordStyle().setItalic(false);

      ret.getSeparatorStyle().setName(IConstants.IStyleNames.SEPARATOR);
      ret.getSeparatorStyle().setBackgroundRGB(new Color(60,63,65).getRGB());
      ret.getSeparatorStyle().setTextRGB(new Color(150, 150, 228).getRGB());
      ret.getSeparatorStyle().setBold(false);
      ret.getSeparatorStyle().setItalic(false);

      ret.getTableStyle().setName(IConstants.IStyleNames.TABLE);
      ret.getTableStyle().setBackgroundRGB(new Color(60,63,65).getRGB());
      ret.getTableStyle().setTextRGB(new Color(183, 231, 137).getRGB());
      ret.getTableStyle().setBold(false);
      ret.getTableStyle().setItalic(false);

      ret.getWhiteSpaceStyle().setName(IConstants.IStyleNames.WHITESPACE);
      ret.getWhiteSpaceStyle().setBackgroundRGB(new Color(60,63,65).getRGB());
      ret.getWhiteSpaceStyle().setTextRGB(Color.black.getRGB());
      ret.getWhiteSpaceStyle().setBold(false);
      ret.getWhiteSpaceStyle().setItalic(false);

      ret.setCaretColorRGB(Color.white.getRGB());
      ret.setCurrentLineHighlightColorRGB(Color.darkGray.getRGB());

      return ret;
   }
}
