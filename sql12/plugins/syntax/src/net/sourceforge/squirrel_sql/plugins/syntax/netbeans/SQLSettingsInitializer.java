package net.sourceforge.squirrel_sql.plugins.syntax.netbeans;

import org.netbeans.editor.*;
import org.netbeans.editor.ext.ExtSettingsNames;
import org.netbeans.editor.ext.ExtSettingsDefaults;
import org.netbeans.editor.ext.java.JavaLayerTokenContext;
import org.netbeans.editor.ext.java.JavaSettingsDefaults;
import org.netbeans.editor.ext.java.JavaSettingsNames;

import java.util.Map;

import net.sourceforge.squirrel_sql.plugins.syntax.netbeans.SQLSettingsDefaults;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPreferences;


public class SQLSettingsInitializer extends Settings.AbstractInitializer
{

   /**
    * Name assigned to initializer
    */
   public static final String NAME = "sql-settings-initializer"; // NOI18N

   private Class sqlKitClass;
   private SyntaxPreferences _syntaxPreferences;

   /**
    * Construct new java-settings-initializer.
    *
    * @param sqlKitClass the real kit class for which the settings are created.
    * @param syntaxPreferences
    */
   public SQLSettingsInitializer(Class sqlKitClass, SyntaxPreferences syntaxPreferences)
   {
      super(NAME);
      this.sqlKitClass = sqlKitClass;
      _syntaxPreferences = syntaxPreferences;
   }

   /**
    * Update map filled with the settings.
    *
    * @param kitClass    kit class for which the settings are being updated.
    *                    It is always non-null value.
    * @param settingsMap map holding [setting-name, setting-value] pairs.
    *                    The map can be empty if this is the first initializer
    *                    that updates it or if no previous initializers updated it.
    */
   public void updateSettingsMap(Class kitClass, Map settingsMap)
   {

      // Update java colorings
      if (kitClass == BaseKit.class)
      {

         new SQLSettingsDefaults.SQLTokenColoringInitializer(_syntaxPreferences).updateSettingsMap(kitClass, settingsMap);
         new SQLSettingsDefaults.SQLLayerTokenColoringInitializer().updateSettingsMap(kitClass, settingsMap);

      }

      if (kitClass == sqlKitClass)
      {

         SettingsUtil.updateListSetting(settingsMap, SettingsNames.KEY_BINDING_LIST,
            JavaSettingsDefaults.getJavaKeyBindings());

         SettingsUtil.updateListSetting(settingsMap, SettingsNames.TOKEN_CONTEXT_LIST,
            new TokenContext[]{
               SQLTokenContext.context,
               JavaLayerTokenContext.context
            });


         // List of the additional colorings
         SettingsUtil.updateListSetting(settingsMap, SettingsNames.COLORING_NAME_LIST,
                                        new String[] {
                                            ExtSettingsNames.HIGHLIGHT_CARET_ROW_COLORING,
                                            ExtSettingsNames.HIGHLIGHT_MATCH_BRACE_COLORING,
                                        }
                                       );

         // ExtCaret highlighting options
         settingsMap.put(ExtSettingsNames.HIGHLIGHT_CARET_ROW,
                         ExtSettingsDefaults.defaultHighlightCaretRow);
         settingsMap.put(ExtSettingsNames.HIGHLIGHT_MATCH_BRACE,
                         ExtSettingsDefaults.defaultHighlightMatchBrace);

         // ExtCaret highlighting colorings
         SettingsUtil.setColoring(settingsMap, ExtSettingsNames.HIGHLIGHT_CARET_ROW_COLORING,
                                  ExtSettingsDefaults.defaultHighlightCaretRowColoring);
         SettingsUtil.setColoring(settingsMap, ExtSettingsNames.HIGHLIGHT_MATCH_BRACE_COLORING,
                                  ExtSettingsDefaults.defaultHighlightMatchBraceColoring);



         settingsMap.put(SettingsNames.ABBREV_MAP, JavaSettingsDefaults.getJavaAbbrevMap());

         settingsMap.put(SettingsNames.MACRO_MAP, JavaSettingsDefaults.getJavaMacroMap());

         settingsMap.put(ExtSettingsNames.CARET_SIMPLE_MATCH_BRACE,
            JavaSettingsDefaults.defaultCaretSimpleMatchBrace);

         settingsMap.put(ExtSettingsNames.HIGHLIGHT_MATCH_BRACE,
            JavaSettingsDefaults.defaultHighlightMatchBrace);

         settingsMap.put(SettingsNames.IDENTIFIER_ACCEPTOR,
            JavaSettingsDefaults.defaultIdentifierAcceptor);

         settingsMap.put(SettingsNames.ABBREV_RESET_ACCEPTOR,
            JavaSettingsDefaults.defaultAbbrevResetAcceptor);

         settingsMap.put(SettingsNames.WORD_MATCH_MATCH_CASE,
            JavaSettingsDefaults.defaultWordMatchMatchCase);

         settingsMap.put(SettingsNames.WORD_MATCH_STATIC_WORDS,
            JavaSettingsDefaults.defaultWordMatchStaticWords);

         // Formatting settings
         settingsMap.put(JavaSettingsNames.JAVA_FORMAT_SPACE_BEFORE_PARENTHESIS,
            JavaSettingsDefaults.defaultJavaFormatSpaceBeforeParenthesis);

         settingsMap.put(JavaSettingsNames.JAVA_FORMAT_SPACE_AFTER_COMMA,
            JavaSettingsDefaults.defaultJavaFormatSpaceAfterComma);

         settingsMap.put(JavaSettingsNames.JAVA_FORMAT_NEWLINE_BEFORE_BRACE,
            JavaSettingsDefaults.defaultJavaFormatNewlineBeforeBrace);

         settingsMap.put(JavaSettingsNames.JAVA_FORMAT_LEADING_SPACE_IN_COMMENT,
            JavaSettingsDefaults.defaultJavaFormatLeadingSpaceInComment);

         settingsMap.put(JavaSettingsNames.JAVA_FORMAT_LEADING_STAR_IN_COMMENT,
            JavaSettingsDefaults.defaultJavaFormatLeadingStarInComment);

         settingsMap.put(JavaSettingsNames.INDENT_HOT_CHARS_ACCEPTOR,
            JavaSettingsDefaults.defaultIndentHotCharsAcceptor);

         settingsMap.put(ExtSettingsNames.REINDENT_WITH_TEXT_BEFORE,
            Boolean.FALSE);

         settingsMap.put(JavaSettingsNames.PAIR_CHARACTERS_COMPLETION,
            JavaSettingsDefaults.defaultPairCharactersCompletion);

         settingsMap.put(JavaSettingsNames.GOTO_CLASS_CASE_SENSITIVE,
            JavaSettingsDefaults.defaultGotoClassCaseSensitive);

         settingsMap.put(JavaSettingsNames.GOTO_CLASS_SHOW_INNER_CLASSES,
            JavaSettingsDefaults.defaultGotoClassShowInnerClasses);

         settingsMap.put(JavaSettingsNames.GOTO_CLASS_SHOW_LIBRARY_CLASSES,
            JavaSettingsDefaults.defaultGotoClassShowLibraryClasses);
      }

   }

}
