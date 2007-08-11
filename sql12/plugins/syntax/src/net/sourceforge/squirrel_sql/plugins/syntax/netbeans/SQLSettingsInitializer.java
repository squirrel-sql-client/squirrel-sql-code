package net.sourceforge.squirrel_sql.plugins.syntax.netbeans;

import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPreferences;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPugin;
import org.netbeans.editor.*;
import org.netbeans.editor.ext.ExtKit;
import org.netbeans.editor.ext.ExtSettingsDefaults;
import org.netbeans.editor.ext.ExtSettingsNames;
import org.netbeans.editor.ext.java.JavaLayerTokenContext;
import org.netbeans.editor.ext.java.JavaSettingsDefaults;
import org.netbeans.editor.ext.java.JavaSettingsNames;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Map;


public class SQLSettingsInitializer extends Settings.AbstractInitializer
{

   /**
    * Name assigned to initializer
    */
   public static final String NAME = "sql-settings-initializer"; // NOI18N

   private Class sqlKitClass;
   private SyntaxPreferences _syntaxPreferences;

   private static int MENU_MASK = java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
   private SyntaxPugin _plugin;
   private Font _font;


   /**
    * Construct new java-settings-initializer.
    *
    * @param sqlKitClass the real kit class for which the settings are created.
    * @param syntaxPreferences
    * @param plugin
    */
   public SQLSettingsInitializer(Class sqlKitClass, SyntaxPreferences syntaxPreferences, Font font, SyntaxPugin plugin)
   {
      super(NAME);
      this.sqlKitClass = sqlKitClass;
      _syntaxPreferences = syntaxPreferences;
      _plugin = plugin;
      _font = font;
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

         new SQLSettingsDefaults.SQLTokenColoringInitializer(_syntaxPreferences, _font).updateSettingsMap(kitClass, settingsMap);
         new SQLSettingsDefaults.SQLLayerTokenColoringInitializer().updateSettingsMap(kitClass, settingsMap);

         SettingsUtil.updateListSetting(settingsMap, SettingsNames.KEY_BINDING_LIST, squirrelKeyBindings);

         settingsMap.put(JavaSettingsNames.FIND_HIGHLIGHT_SEARCH, Boolean.FALSE);
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
            new String[]{
               ExtSettingsNames.HIGHLIGHT_CARET_ROW_COLORING,
               ExtSettingsNames.HIGHLIGHT_MATCH_BRACE_COLORING,
            });

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


         settingsMap.put(SettingsNames.TEXT_LIMIT_LINE_VISIBLE, Boolean.valueOf(_syntaxPreferences.isTextLimitLineVisible()));

         settingsMap.put(SettingsNames.LINE_NUMBER_VISIBLE, Boolean.valueOf(true));

         settingsMap.put(SettingsNames.TEXT_LIMIT_WIDTH, Integer.valueOf(_syntaxPreferences.getTextLimitLineWidth()));


         settingsMap.put(SettingsNames.ABBREV_MAP, SQLSettingsDefaults.getAbbrevMap(_plugin));

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


         Coloring col = new Coloring(_font, SettingsDefaults.defaultForeColor, SettingsDefaults.defaultBackColor);

         SettingsUtil.setColoring(settingsMap,
                                  SettingsNames.DEFAULT_COLORING, col);
         


      }
   }




	public static final MultiKeyBinding[] squirrelKeyBindings =
      new MultiKeyBinding[]
      {
         new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_F, MENU_MASK),
            ExtKit.findAction),
         new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_FIND, 0),
            ExtKit.findAction),
         new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_H, MENU_MASK),
            ExtKit.replaceAction),
         new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_MASK),
            ExtKit.gotoAction),
         new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            ExtKit.escapeAction),

			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK),
				BaseKit.toUpperCaseAction),

			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK),
				BaseKit.toLowerCaseAction),


//         new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_OPEN_BRACKET, MENU_MASK),
//            ExtKit.matchBraceAction),
//         new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_OPEN_BRACKET, MENU_MASK | InputEvent.SHIFT_MASK),
//            ExtKit.selectionMatchBraceAction),
//         new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_F10, InputEvent.SHIFT_MASK),
//            ExtKit.showPopupMenuAction),

         new MultiKeyBinding(
            KeyStroke.getKeyStroke(KeyEvent.VK_F7, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK),
            BaseKit.toggleHighlightSearchAction),

//         new MultiKeyBinding(
//            KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_MASK),
//            SQLKit.duplicateLineAction),
      };

	   public static final String ACCELERATOR_STRING_TO_UPPER_CASE = "ctrl shift u";
	   public static final String ACCELERATOR_STRING_TO_LOWER_CASE = "ctrl shift l";



}
