package net.sourceforge.squirrel_sql.plugins.syntax;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.ToolsPopupAccessor;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.fw.resources.Resources;
import net.sourceforge.squirrel_sql.plugins.syntax.rsyntax.SquirreLRSyntaxTextAreaUI;
import net.sourceforge.squirrel_sql.plugins.syntax.rsyntax.SquirrelRSyntaxTextArea;
import net.sourceforge.squirrel_sql.plugins.syntax.rsyntax.action.SquirrelCopyAsRtfAction;
import org.fife.ui.rtextarea.RTextAreaEditorKit;

import javax.swing.*;
import java.util.HashMap;

public class ToolsPopupHandler
{
   private SyntaxPlugin _syntaxPugin;

   ToolsPopupHandler(SyntaxPlugin syntaxPugin)
   {
      _syntaxPugin = syntaxPugin;
   }

   void initToolsPopup(HashMap<String, Object> props, ISQLEntryPanel isqlEntryPanel)
   {
      // Note: SessionInternalFrame and SQLinternalFrame should never provide
      // a ToolsPopupAccessor. Their Tools Popup ist configured in the SyntaxPlugin class
      // with standard Actions from ActionCollection.
      ToolsPopupAccessor tpa = (ToolsPopupAccessor) props.get((ToolsPopupAccessor.class.getName()));

      if(null == tpa)
      {
         return;
      }

      SyntaxPluginResources rsrc = _syntaxPugin.getResources();
      IApplication app = _syntaxPugin.getApplication();

      tpa.addToToolsPopup(SyntaxPlugin.i18n.FIND , new FindAction(app, rsrc, isqlEntryPanel));
      tpa.addToToolsPopup(SyntaxPlugin.i18n.FIND_SELECTED , new FindSelectedAction(app, rsrc, isqlEntryPanel));
      tpa.addToToolsPopup(SyntaxPlugin.i18n.REPEAT_LAST_FIND , new RepeatLastFindAction(app, rsrc, isqlEntryPanel));
      tpa.addToToolsPopup(SyntaxPlugin.i18n.MARK_SELECTED , new MarkSelectedAction(app, rsrc, isqlEntryPanel));
      tpa.addToToolsPopup(SyntaxPlugin.i18n.REPLACE , new ReplaceAction(app, rsrc, isqlEntryPanel));
      tpa.addToToolsPopup(SyntaxPlugin.i18n.UNMARK , new UnmarkAction(app, rsrc, isqlEntryPanel));
      tpa.addToToolsPopup(SyntaxPlugin.i18n.GO_TO_LINE , new GoToLineAction(app, rsrc, isqlEntryPanel));
      tpa.addToToolsPopup(SyntaxPlugin.i18n.AUTO_CORR , new ConfigureAutoCorrectAction(app, rsrc, _syntaxPugin));
      tpa.addToToolsPopup(SyntaxPlugin.i18n.DUP_LINE , new DuplicateLineAction(app, rsrc, isqlEntryPanel));
      tpa.addToToolsPopup(SyntaxPlugin.i18n.COMMENT , new CommentAction(app, rsrc, isqlEntryPanel));
      tpa.addToToolsPopup(SyntaxPlugin.i18n.UNCOMMENT , new UncommentAction(app,rsrc,isqlEntryPanel));

      if (isqlEntryPanel.getTextComponent() instanceof SquirrelRSyntaxTextArea)
      {
         SquirrelRSyntaxTextArea rsEdit = (SquirrelRSyntaxTextArea) isqlEntryPanel.getTextComponent();

         Action toUpperAction = SquirreLRSyntaxTextAreaUI.getActionForName(rsEdit, RTextAreaEditorKit.rtaUpperSelectionCaseAction);
         toUpperAction.putValue(Resources.ACCELERATOR_STRING, SquirreLRSyntaxTextAreaUI.RS_ACCELERATOR_STRING_TO_UPPER_CASE);
         tpa.addToToolsPopup(SyntaxPlugin.i18n.TO_UPPER_CASE, toUpperAction);

         Action toLowerAction = SquirreLRSyntaxTextAreaUI.getActionForName(rsEdit, RTextAreaEditorKit.rtaLowerSelectionCaseAction);
         toLowerAction.putValue(Resources.ACCELERATOR_STRING, SquirreLRSyntaxTextAreaUI.RS_ACCELERATOR_STRING_TO_LOWER_CASE);
         tpa.addToToolsPopup(SyntaxPlugin.i18n.TO_LOWER_CASE, toLowerAction);
         
         tpa.addToToolsPopup(SyntaxPlugin.i18n.COPY_AS_RTF,_syntaxPugin.getApplication().getActionCollection().get(SquirrelCopyAsRtfAction.class));
      }
   }


   void initToolsPopup(ActionCollection coll, ISQLPanelAPI sqlPanelAPI)
   {
      sqlPanelAPI.addToToolsPopUp(SyntaxPlugin.i18n.FIND, coll.get(FindAction.class));
      sqlPanelAPI.addToToolsPopUp(SyntaxPlugin.i18n.FIND_SELECTED , coll.get(FindSelectedAction.class));
      sqlPanelAPI.addToToolsPopUp(SyntaxPlugin.i18n.REPEAT_LAST_FIND , coll.get(RepeatLastFindAction.class));
      sqlPanelAPI.addToToolsPopUp(SyntaxPlugin.i18n.MARK_SELECTED , coll.get(MarkSelectedAction.class));
      sqlPanelAPI.addToToolsPopUp(SyntaxPlugin.i18n.REPLACE, coll.get(ReplaceAction.class));
      sqlPanelAPI.addToToolsPopUp(SyntaxPlugin.i18n.UNMARK, coll.get(UnmarkAction.class));
      sqlPanelAPI.addToToolsPopUp(SyntaxPlugin.i18n.GO_TO_LINE, coll.get(GoToLineAction.class));
      sqlPanelAPI.addToToolsPopUp(SyntaxPlugin.i18n.AUTO_CORR, coll.get(ConfigureAutoCorrectAction.class));
      sqlPanelAPI.addToToolsPopUp(SyntaxPlugin.i18n.DUP_LINE, coll.get(DuplicateLineAction.class));
      sqlPanelAPI.addToToolsPopUp(SyntaxPlugin.i18n.COMMENT, coll.get(CommentAction.class));
      sqlPanelAPI.addToToolsPopUp(SyntaxPlugin.i18n.UNCOMMENT, coll.get(UncommentAction.class));

      if (sqlPanelAPI.getSQLEntryPanel().getTextComponent() instanceof SquirrelRSyntaxTextArea)
      {
         SquirrelRSyntaxTextArea rsEdit = (SquirrelRSyntaxTextArea) sqlPanelAPI.getSQLEntryPanel().getTextComponent();

         Action toUpperAction = SquirreLRSyntaxTextAreaUI.getActionForName(rsEdit, RTextAreaEditorKit.rtaUpperSelectionCaseAction);
         toUpperAction.putValue(Resources.ACCELERATOR_STRING, SquirreLRSyntaxTextAreaUI.RS_ACCELERATOR_STRING_TO_UPPER_CASE);
         sqlPanelAPI.addToToolsPopUp(SyntaxPlugin.i18n.TO_UPPER_CASE, toUpperAction);

         Action toLowerAction = SquirreLRSyntaxTextAreaUI.getActionForName(rsEdit, RTextAreaEditorKit.rtaLowerSelectionCaseAction);
         toLowerAction.putValue(Resources.ACCELERATOR_STRING, SquirreLRSyntaxTextAreaUI.RS_ACCELERATOR_STRING_TO_LOWER_CASE);
         sqlPanelAPI.addToToolsPopUp(SyntaxPlugin.i18n.TO_LOWER_CASE, toLowerAction);

         sqlPanelAPI.addToToolsPopUp(SyntaxPlugin.i18n.COPY_AS_RTF,_syntaxPugin.getApplication().getActionCollection().get(SquirrelCopyAsRtfAction.class));
      }
   }
}
