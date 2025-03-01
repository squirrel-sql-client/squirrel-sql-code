package net.sourceforge.squirrel_sql.client.session.action.syntax;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.session.ToolsPopupAccessor;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.action.syntax.rsyntax.SquirreLRSyntaxTextAreaUI;
import net.sourceforge.squirrel_sql.client.session.action.syntax.rsyntax.SquirrelRSyntaxTextArea;
import net.sourceforge.squirrel_sql.client.session.action.syntax.rsyntax.action.SquirrelCopyAsRtfAction;
import net.sourceforge.squirrel_sql.client.shortcut.ShortcutUtil;
import net.sourceforge.squirrel_sql.fw.resources.Resources;
import org.fife.ui.rtextarea.RTextAreaEditorKit;

import javax.swing.Action;
import java.util.HashMap;

public class ToolsPopupHandler
{

   void initToolsPopup(HashMap<String, Object> props, ISQLEntryPanel isqlEntryPanel)
   {
      // Note: SessionInternalFrame and SQLinternalFrame should never provide
      // a ToolsPopupAccessor. Their Tools Popup is configured in the SyntaxPlugin class
      // with standard Actions from ActionCollection.
      ToolsPopupAccessor tpa = (ToolsPopupAccessor) props.get((ToolsPopupAccessor.class.getName()));

      if(null == tpa)
      {
         return;
      }

      tpa.addToToolsPopup(ToolsPopupHandler_I18n.FIND , new FindAction(isqlEntryPanel));
      tpa.addToToolsPopup(ToolsPopupHandler_I18n.FIND_SELECTED , new FindSelectedAction(isqlEntryPanel));
      tpa.addToToolsPopup(ToolsPopupHandler_I18n.REPEAT_LAST_FIND , new RepeatLastFindAction(isqlEntryPanel));
      tpa.addToToolsPopup(ToolsPopupHandler_I18n.MARK_SELECTED , new MarkSelectedAction(isqlEntryPanel));
      tpa.addToToolsPopup(ToolsPopupHandler_I18n.REPLACE , new ReplaceAction(isqlEntryPanel));
      tpa.addToToolsPopup(ToolsPopupHandler_I18n.UNMARK , new UnmarkAction(isqlEntryPanel));
      tpa.addToToolsPopup(ToolsPopupHandler_I18n.GO_TO_LINE , new GoToLineAction(isqlEntryPanel));
      tpa.addToToolsPopup(ToolsPopupHandler_I18n.AUTO_CORR , new ConfigureAutoCorrectAction());
      tpa.addToToolsPopup(ToolsPopupHandler_I18n.DUP_LINE , new DuplicateLineAction(isqlEntryPanel));
      tpa.addToToolsPopup(ToolsPopupHandler_I18n.COMMENT , new CommentAction(isqlEntryPanel));
      tpa.addToToolsPopup(ToolsPopupHandler_I18n.UNCOMMENT , new UncommentAction(isqlEntryPanel));

      if (isqlEntryPanel.getTextComponent() instanceof SquirrelRSyntaxTextArea)
      {
         SquirrelRSyntaxTextArea rsEdit = (SquirrelRSyntaxTextArea) isqlEntryPanel.getTextComponent();

         Action toUpperAction = SquirreLRSyntaxTextAreaUI.getActionForName(rsEdit, RTextAreaEditorKit.rtaUpperSelectionCaseAction);
         toUpperAction.putValue(Resources.ACCELERATOR_STRING, ShortcutUtil.getKeystrokeString(SquirreLRSyntaxTextAreaUI.getToUpperCaseKeyStroke()));
         tpa.addToToolsPopup(ToolsPopupHandler_I18n.TO_UPPER_CASE, toUpperAction);

         Action toLowerAction = SquirreLRSyntaxTextAreaUI.getActionForName(rsEdit, RTextAreaEditorKit.rtaLowerSelectionCaseAction);
         toLowerAction.putValue(Resources.ACCELERATOR_STRING, ShortcutUtil.getKeystrokeString(SquirreLRSyntaxTextAreaUI.getToLowerCaseKeyStroke()));
         tpa.addToToolsPopup(ToolsPopupHandler_I18n.TO_LOWER_CASE, toLowerAction);
         
         tpa.addToToolsPopup(ToolsPopupHandler_I18n.COPY_AS_RTF, Main.getApplication().getActionCollection().get(SquirrelCopyAsRtfAction.class));
      }
   }


   public static void initToolsPopup(ActionCollection coll, ISQLPanelAPI sqlPanelAPI)
   {
      //sqlPanelAPI.addToToolsPopUp(ToolsPopupHandler_I18n.FIND, coll.get(FindAction.class));
      //sqlPanelAPI.addToToolsPopUp(ToolsPopupHandler_I18n.FIND_SELECTED , coll.get(FindSelectedAction.class));
      //sqlPanelAPI.addToToolsPopUp(ToolsPopupHandler_I18n.REPEAT_LAST_FIND , coll.get(RepeatLastFindAction.class));
      //sqlPanelAPI.addToToolsPopUp(ToolsPopupHandler_I18n.MARK_SELECTED , coll.get(MarkSelectedAction.class));
      //sqlPanelAPI.addToToolsPopUp(ToolsPopupHandler_I18n.REPLACE, coll.get(ReplaceAction.class));
      //sqlPanelAPI.addToToolsPopUp(ToolsPopupHandler_I18n.UNMARK, coll.get(UnmarkAction.class));
      //sqlPanelAPI.addToToolsPopUp(ToolsPopupHandler_I18n.GO_TO_LINE, coll.get(GoToLineAction.class));
      //sqlPanelAPI.addToToolsPopUp(ToolsPopupHandler_I18n.AUTO_CORR, coll.get(ConfigureAutoCorrectAction.class));
      //sqlPanelAPI.addToToolsPopUp(ToolsPopupHandler_I18n.DUP_LINE, coll.get(DuplicateLineAction.class));
      //sqlPanelAPI.addToToolsPopUp(ToolsPopupHandler_I18n.COMMENT, coll.get(CommentAction.class));
      //sqlPanelAPI.addToToolsPopUp(ToolsPopupHandler_I18n.UNCOMMENT, coll.get(UncommentAction.class));
      //
      //if (sqlPanelAPI.getSQLEntryPanel().getTextComponent() instanceof SquirrelRSyntaxTextArea)
      //{
      //   SquirrelRSyntaxTextArea rsEdit = (SquirrelRSyntaxTextArea) sqlPanelAPI.getSQLEntryPanel().getTextComponent();
      //
      //   Action toUpperAction = SquirreLRSyntaxTextAreaUI.getActionForName(rsEdit, RTextAreaEditorKit.rtaUpperSelectionCaseAction);
      //   toUpperAction.putValue(Resources.ACCELERATOR_STRING, ShortcutUtil.getKeystrokeString(SquirreLRSyntaxTextAreaUI.getToUpperCaseKeyStroke()));
      //   sqlPanelAPI.addToToolsPopUp(ToolsPopupHandler_I18n.TO_UPPER_CASE, toUpperAction);
      //
      //   Action toLowerAction = SquirreLRSyntaxTextAreaUI.getActionForName(rsEdit, RTextAreaEditorKit.rtaLowerSelectionCaseAction);
      //   toLowerAction.putValue(Resources.ACCELERATOR_STRING, ShortcutUtil.getKeystrokeString(SquirreLRSyntaxTextAreaUI.getToLowerCaseKeyStroke()));
      //   sqlPanelAPI.addToToolsPopUp(ToolsPopupHandler_I18n.TO_LOWER_CASE, toLowerAction);
      //
      //   sqlPanelAPI.addToToolsPopUp(ToolsPopupHandler_I18n.COPY_AS_RTF, Main.getApplication().getActionCollection().get(SquirrelCopyAsRtfAction.class));
      //}
   }
}
