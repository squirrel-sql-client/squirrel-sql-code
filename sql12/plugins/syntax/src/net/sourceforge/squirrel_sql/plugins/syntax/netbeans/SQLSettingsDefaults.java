package net.sourceforge.squirrel_sql.plugins.syntax.netbeans;

import java.awt.Color;
import java.awt.Font;
import java.util.Map;
import java.util.TreeMap;

import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPreferences;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPugin;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxStyle;

import org.netbeans.editor.Coloring;
import org.netbeans.editor.Settings;
import org.netbeans.editor.SettingsDefaults;
import org.netbeans.editor.SettingsUtil;
import org.netbeans.editor.TokenCategory;
import org.netbeans.editor.TokenContextPath;
import org.netbeans.editor.ext.ExtSettingsDefaults;
import org.netbeans.editor.ext.java.JavaLayerTokenContext;


public class SQLSettingsDefaults extends ExtSettingsDefaults
{
   // Formatting
   public static final Boolean defaultJavaFormatSpaceBeforeParenthesis = Boolean.FALSE;
   public static final Boolean defaultJavaFormatSpaceAfterComma = Boolean.TRUE;




   static class SQLTokenColoringInitializer
      extends SettingsUtil.TokenColoringInitializer
   {

      Font boldFont;
      Font italicFont;
      Font normalFont;

      Settings.Evaluator boldSubst = new SettingsUtil.FontStylePrintColoringEvaluator(Font.BOLD);
      Settings.Evaluator italicSubst = new SettingsUtil.FontStylePrintColoringEvaluator(Font.ITALIC);
      Settings.Evaluator lightGraySubst = new SettingsUtil.ForeColorPrintColoringEvaluator(Color.lightGray);

      Coloring commentColoring = new Coloring(null, new Color(115, 115, 115), null);

      Coloring numbersColoring = new Coloring(null, new Color(120, 0, 0), null);
      private SyntaxPreferences _syntaxPreferences;

      public SQLTokenColoringInitializer(SyntaxPreferences syntaxPreferences, Font font)
      {
         super(SQLTokenContext.context);
         _syntaxPreferences = syntaxPreferences;

         boldFont =  font.deriveFont(Font.BOLD);
         italicFont = font.deriveFont(Font.ITALIC);
         normalFont = font;
      }

      public Object getTokenColoring(TokenContextPath tokenContextPath,
                                     TokenCategory tokenIDOrCategory, boolean printingSet)
      {
         if (!printingSet)
         {
            switch (tokenIDOrCategory.getNumericID())
            {
               case SQLTokenContext.IDENTIFIER_ID:
						return createColoringFromStyle(_syntaxPreferences.getIdentifierStyle());

					case SQLTokenContext.WHITESPACE_ID:
						return createColoringFromStyle(_syntaxPreferences.getWhiteSpaceStyle());


					case SQLTokenContext.OPERATORS_ID:
						return createColoringFromStyle(_syntaxPreferences.getOperatorStyle());


					case SQLTokenContext.TABLE_ID:
                  return createColoringFromStyle(_syntaxPreferences.getTableStyle());


               case SQLTokenContext.COLUMN_ID:
                  return createColoringFromStyle(_syntaxPreferences.getColumnStyle());

					case SQLTokenContext.FUNCTION_ID:
						return createColoringFromStyle(_syntaxPreferences.getFunctionStyle());

					case SQLTokenContext.DATA_TYPE_ID:
						return createColoringFromStyle(_syntaxPreferences.getDataTypeStyle());

					case SQLTokenContext.STATEMENT_SEPARATOR_ID:
						return createColoringFromStyle(_syntaxPreferences.getSeparatorStyle());

					case SQLTokenContext.ERROR_ID:
                  return createColoringFromStyle(_syntaxPreferences.getErrorStyle());

               case SQLTokenContext.ERRORS_ID:
                  return createColoringFromStyle(_syntaxPreferences.getErrorStyle());

               case SQLTokenContext.KEYWORDS_ID:
                  return createColoringFromStyle(_syntaxPreferences.getReservedWordStyle());

               case SQLTokenContext.LINE_COMMENT_ID:
               case SQLTokenContext.BLOCK_COMMENT_ID:
                  return createColoringFromStyle(_syntaxPreferences.getCommentStyle());

               case SQLTokenContext.CHAR_LITERAL_ID:
                  return createColoringFromStyle(_syntaxPreferences.getLiteralStyle());

               case SQLTokenContext.STRING_LITERAL_ID:
                  return createColoringFromStyle(_syntaxPreferences.getLiteralStyle());

               case SQLTokenContext.NUMERIC_LITERALS_ID:
                  return createColoringFromStyle(_syntaxPreferences.getLiteralStyle());

//               case SQLTokenContext.ANNOTATION_ID: // JDK 1.5 annotations
//                  return new Coloring(null, new Color(0, 111, 0), null);

            }

         }
         else
         { // printing set
            switch (tokenIDOrCategory.getNumericID())
            {
               case SQLTokenContext.LINE_COMMENT_ID:
               case SQLTokenContext.BLOCK_COMMENT_ID:
                  return lightGraySubst; // print fore color will be gray

               default:
                  return SettingsUtil.defaultPrintColoringEvaluator;
            }

         }

         return null;

      }

      private Coloring createColoringFromStyle(SyntaxStyle style)
      {
         if(style.isBold())
         {
            return new Coloring(boldFont, Coloring.FONT_MODE_DEFAULT, new Color(style.getTextRGB()), new Color(style.getBackgroundRGB()));
         }
         else if(style.isItalic())
         {
            return new Coloring(italicFont, Coloring.FONT_MODE_DEFAULT, new Color(style.getTextRGB()), new Color(style.getBackgroundRGB()));
         }
         else
         {
            return new Coloring(normalFont, Coloring.FONT_MODE_DEFAULT, new Color(style.getTextRGB()), new Color(style.getBackgroundRGB()));
         }
      }

   }

   static class SQLLayerTokenColoringInitializer
      extends SettingsUtil.TokenColoringInitializer
   {

      Font boldFont = SettingsDefaults.defaultFont.deriveFont(Font.BOLD);
      Settings.Evaluator italicSubst = new SettingsUtil.FontStylePrintColoringEvaluator(Font.ITALIC);

      public SQLLayerTokenColoringInitializer()
      {
         super(JavaLayerTokenContext.context);
      }

      public Object getTokenColoring(TokenContextPath tokenContextPath,
                                     TokenCategory tokenIDOrCategory, boolean printingSet)
      {
         if (!printingSet)
         {
            switch (tokenIDOrCategory.getNumericID())
            {
               case JavaLayerTokenContext.METHOD_ID:
                  return new Coloring(boldFont, Coloring.FONT_MODE_APPLY_STYLE,
                     null, null);

            }

         }
         else
         { // printing set
            switch (tokenIDOrCategory.getNumericID())
            {
               case JavaLayerTokenContext.METHOD_ID:
                  return italicSubst;

               default:
                  return SettingsUtil.defaultPrintColoringEvaluator;
            }

         }

         return null;
      }

   }

   public static Map<String, String> getAbbrevMap(SyntaxPugin plugin)
   {
      Map<String, String> javaAbbrevMap = new TreeMap<String, String>();

      // We do abrevs ourselfs in the DocumentListener in NetbeansSQLEntryPanel.
      // We don't use the Netbeans implementation mainly because it is case sensitive.
      // If we come across a performance problem one day we can still try to use this.

      //Hashtable autoCorrects = plugin.getAutoCorrectProviderImpl().getAutoCorrects();
      //javaAbbrevMap.putAll(autoCorrects);

      return javaAbbrevMap;



//      javaAbbrevMap.put("sout", "System.out.println(\"|\");"); // NOI18N
//      javaAbbrevMap.put("serr", "System.err.println(\"|\");"); // NOI18N
//
//      javaAbbrevMap.put("psf", "private static final "); // NOI18N
//      javaAbbrevMap.put("psfi", "private static final int "); // NOI18N
//      javaAbbrevMap.put("psfs", "private static final String "); // NOI18N
//      javaAbbrevMap.put("psfb", "private static final boolean "); // NOI18N
//      javaAbbrevMap.put("Psf", "public static final "); // NOI18N
//      javaAbbrevMap.put("Psfi", "public static final int "); // NOI18N
//      javaAbbrevMap.put("Psfs", "public static final String "); // NOI18N
//      javaAbbrevMap.put("Psfb", "public static final boolean "); // NOI18N
//
//      javaAbbrevMap.put("ab", "abstract "); // NOI18N
//      javaAbbrevMap.put("bo", "boolean "); // NOI18N
//      javaAbbrevMap.put("br", "break"); // NOI18N
//      javaAbbrevMap.put("ca", "catch ("); // NOI18N
//      javaAbbrevMap.put("cl", "class "); // NOI18N
//      javaAbbrevMap.put("cn", "continue"); // NOI18N
//      javaAbbrevMap.put("df", "default:"); // NOI18N
//      javaAbbrevMap.put("ex", "extends "); // NOI18N
//      javaAbbrevMap.put("fa", "false"); // NOI18N
//      javaAbbrevMap.put("fi", "final "); // NOI18N
//      javaAbbrevMap.put("fl", "float "); // NOI18N
//      javaAbbrevMap.put("fy", "finally "); // NOI18N
//      javaAbbrevMap.put("im", "implements "); // NOI18N
//      javaAbbrevMap.put("ir", "import "); // NOI18N
//      javaAbbrevMap.put("iof", "instanceof "); // NOI18N
//      javaAbbrevMap.put("ie", "interface "); // NOI18N
//      javaAbbrevMap.put("pr", "private "); // NOI18N
//      javaAbbrevMap.put("pe", "protected "); // NOI18N
//      javaAbbrevMap.put("pu", "public "); // NOI18N
//      javaAbbrevMap.put("re", "return "); // NOI18N
//      javaAbbrevMap.put("st", "static "); // NOI18N
//      javaAbbrevMap.put("sw", "switch ("); // NOI18N
//      javaAbbrevMap.put("sy", "synchronized "); // NOI18N
//      javaAbbrevMap.put("th", "throws "); // NOI18N
//      javaAbbrevMap.put("tw", "throw "); // NOI18N
//      javaAbbrevMap.put("twn", "throw new "); // NOI18N
//      javaAbbrevMap.put("wh", "while ("); // NOI18N
//
//      javaAbbrevMap.put("eq", "equals"); // NOI18N
//      javaAbbrevMap.put("le", "length"); // NOI18N
//
//      javaAbbrevMap.put("En", "Enumeration"); // NOI18N
//      javaAbbrevMap.put("Ex", "Exception"); // NOI18N
//      javaAbbrevMap.put("Ob", "Object"); // NOI18N
//      javaAbbrevMap.put("St", "String"); // NOI18N
//
//      javaAbbrevMap.put("pst", "printStackTrace();"); // NOI18N
//      javaAbbrevMap.put("tds", "Thread.dumpStack();"); // NOI18N
//
//      return javaAbbrevMap;
   }


}
