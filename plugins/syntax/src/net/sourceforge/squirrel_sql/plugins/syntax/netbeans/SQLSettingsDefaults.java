/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package net.sourceforge.squirrel_sql.plugins.syntax.netbeans;

import org.netbeans.editor.*;
import org.netbeans.editor.ext.ExtSettingsDefaults;
import org.netbeans.editor.ext.java.JavaLayerTokenContext;

import java.awt.*;

import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPreferences;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxStyle;

/**
 * Default settings values for Java.
 *
 * @author Miloslav Metelka
 * @version 1.00
 */

public class SQLSettingsDefaults extends ExtSettingsDefaults
{

   // Formatting
   public static final Boolean defaultJavaFormatSpaceBeforeParenthesis = Boolean.FALSE;
   public static final Boolean defaultJavaFormatSpaceAfterComma = Boolean.TRUE;


   static class SQLTokenColoringInitializer
      extends SettingsUtil.TokenColoringInitializer
   {

      Font boldFont = SettingsDefaults.defaultFont.deriveFont(Font.BOLD);
      Font italicFont = SettingsDefaults.defaultFont.deriveFont(Font.ITALIC);
      Settings.Evaluator boldSubst = new SettingsUtil.FontStylePrintColoringEvaluator(Font.BOLD);
      Settings.Evaluator italicSubst = new SettingsUtil.FontStylePrintColoringEvaluator(Font.ITALIC);
      Settings.Evaluator lightGraySubst = new SettingsUtil.ForeColorPrintColoringEvaluator(Color.lightGray);

      Coloring commentColoring = new Coloring(null, new Color(115, 115, 115), null);

      Coloring numbersColoring = new Coloring(null, new Color(120, 0, 0), null);
      private SyntaxPreferences _syntaxPreferences;

      public SQLTokenColoringInitializer(SyntaxPreferences syntaxPreferences)
      {
         super(SQLTokenContext.context);
         _syntaxPreferences = syntaxPreferences;
      }

      public Object getTokenColoring(TokenContextPath tokenContextPath,
                                     TokenCategory tokenIDOrCategory, boolean printingSet)
      {
         if (!printingSet)
         {
            switch (tokenIDOrCategory.getNumericID())
            {
               case SQLTokenContext.WHITESPACE_ID:
               case SQLTokenContext.OPERATORS_ID:
               case SQLTokenContext.IDENTIFIER_ID:
                  return SettingsDefaults.emptyColoring;

               case SQLTokenContext.TABLE_ID:
                  return createColoringFromStyle(_syntaxPreferences.getTableStyle());


               case SQLTokenContext.COLUMN_ID:
                  return createColoringFromStyle(_syntaxPreferences.getColumnStyle());

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

      private Coloring createColoringFromStyle(SyntaxStyle tableStyle)
      {
         if(tableStyle.isBold())
         {
            return new Coloring(boldFont, Coloring.FONT_MODE_APPLY_STYLE, new Color(tableStyle.getTextRGB()), new Color(tableStyle.getBackgroundRGB()));
         }
         else if(tableStyle.isItalic())
         {
            return new Coloring(italicFont, Coloring.FONT_MODE_APPLY_STYLE, new Color(tableStyle.getTextRGB()), new Color(tableStyle.getBackgroundRGB()));
         }
         else
         {
            return new Coloring(null, Coloring.FONT_MODE_APPLY_STYLE, new Color(tableStyle.getTextRGB()), new Color(tableStyle.getBackgroundRGB()));
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

}
