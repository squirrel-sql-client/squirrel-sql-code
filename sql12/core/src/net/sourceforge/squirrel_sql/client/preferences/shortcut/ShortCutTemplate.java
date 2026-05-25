package net.sourceforge.squirrel_sql.client.preferences.shortcut;

import javax.swing.text.DefaultEditorKit;
import net.sourceforge.squirrel_sql.client.session.action.DeleteCurrentLineAction;
import net.sourceforge.squirrel_sql.client.session.action.PasteFromHistoryAction;
import net.sourceforge.squirrel_sql.client.session.action.UndoAction;
import net.sourceforge.squirrel_sql.client.session.action.syntax.CommentAction;
import net.sourceforge.squirrel_sql.client.session.action.syntax.MarkSelectedAction;
import net.sourceforge.squirrel_sql.client.session.action.syntax.ReplaceAction;
import net.sourceforge.squirrel_sql.fw.resources.ResourceUtil;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaEditorKit;
import org.fife.ui.rtextarea.RTextAreaEditorKit;

/**
 * Remember:
 * 1. There's no need to set conflicting shortcuts to keyStroke null, because it's done automatically, see {@link ShortcutPrefsCtrl#onApplyShortcuts()}
 * 2. A Session must have been opened before a  shortcut-template can be applied, see first message box in {@link ShortcutPrefsCtrl#onApplyShortcuts()}
 * 3. SQuirreL must be restarted for shortcut changes to take effect.
 * <p>
 * All of those hints are given in or by the Global Preferences UI.
 *
 */
public enum ShortCutTemplate
{
   EMACS("Emacs",
         new TemplateKeyStroke(ResourceUtil.getActionName(DeleteCurrentLineAction.class), "ctrl K"),
         new TemplateKeyStroke(DefaultEditorKit.beginLineAction, "ctrl A"),
         new TemplateKeyStroke(DefaultEditorKit.endLineAction, "ctrl E"),
         new TemplateKeyStroke(RTextAreaEditorKit.rtaDeletePrevWordAction, "alt DELETE"),
         new TemplateKeyStroke(RTextAreaEditorKit.rtaUpperSelectionCaseAction, "alt U"),
         new TemplateKeyStroke(RTextAreaEditorKit.rtaLowerSelectionCaseAction, "alt L"),
         new TemplateKeyStroke(RSyntaxTextAreaEditorKit.rstaGoToMatchingBracketAction, "ctrl alt F"),
         new TemplateKeyStroke(ResourceUtil.getActionName(PasteFromHistoryAction.class), "ctrl Y"),
         new TemplateKeyStroke(ResourceUtil.getActionName(UndoAction.class), "ctrl pressed SLASH"),
         new TemplateKeyStroke(ResourceUtil.getActionName(CommentAction.class), "alt pressed SEMICOLON"),
         new TemplateKeyStroke(ResourceUtil.getActionName(ReplaceAction.class), "ctrl pressed H"),
         new TemplateKeyStroke(DefaultEditorKit.beginLineAction, "ctrl pressed A"),
         new TemplateKeyStroke(DefaultEditorKit.endLineAction, "ctrl pressed E"),
         new TemplateKeyStroke(DefaultEditorKit.copyAction, "alt pressed W"),
         new TemplateKeyStroke(ResourceUtil.getActionName(MarkSelectedAction.class), "ctrl pressed SPACE"),
         new TemplateKeyStroke(DefaultEditorKit.pasteAction, "ctrl pressed Y")
         );

   private final String _templateName;
   private final TemplateKeyStroke[] _templateKeyStrokes;

   ShortCutTemplate(String templateName, TemplateKeyStroke... templateKeyStrokes)
   {
      _templateName = templateName;
      _templateKeyStrokes = templateKeyStrokes;
   }

   public TemplateKeyStroke[] getTemplateKeyStrokes()
   {
      return _templateKeyStrokes;
   }

   @Override
   public String toString()
   {
      return _templateName;
   }
}
