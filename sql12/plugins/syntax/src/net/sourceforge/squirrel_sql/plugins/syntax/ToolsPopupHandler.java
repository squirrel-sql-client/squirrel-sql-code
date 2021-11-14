package net.sourceforge.squirrel_sql.plugins.syntax;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.session.ToolsPopupAccessor;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.shortcut.ShortcutUtil;
import net.sourceforge.squirrel_sql.fw.resources.Resources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.syntax.rsyntax.SquirreLRSyntaxTextAreaUI;
import net.sourceforge.squirrel_sql.plugins.syntax.rsyntax.SquirrelRSyntaxTextArea;
import net.sourceforge.squirrel_sql.plugins.syntax.rsyntax.action.SquirrelCopyAsRtfAction;
import org.fife.ui.rtextarea.RTextAreaEditorKit;

import javax.swing.Action;
import java.util.HashMap;

public class ToolsPopupHandler
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ToolsPopupHandler.class);

   interface I18n
   {
      String TO_UPPER_CASE = s_stringMgr.getString("SyntaxPlugin.touppercase");
      String TO_LOWER_CASE = s_stringMgr.getString("SyntaxPlugin.tolowercase");
      String FIND = s_stringMgr.getString("SyntaxPlugin.find");
      String FIND_SELECTED = s_stringMgr.getString("SyntaxPlugin.findselected");
      String REPEAT_LAST_FIND = s_stringMgr.getString("SyntaxPlugin.repeatLastFind");
      String MARK_SELECTED = s_stringMgr.getString("SyntaxPlugin.markSelected");
      String REPLACE = s_stringMgr.getString("SyntaxPlugin.replace");
      String UNMARK = s_stringMgr.getString("SyntaxPlugin.unmark");
      String GO_TO_LINE = s_stringMgr.getString("SyntaxPlugin.gotoline");
      String AUTO_CORR = s_stringMgr.getString("SyntaxPlugin.autocorr");
      String DUP_LINE = s_stringMgr.getString("SyntaxPlugin.duplicateline");
      String COMMENT = s_stringMgr.getString("SyntaxPlugin.comment");
      String UNCOMMENT = s_stringMgr.getString("SyntaxPlugin.uncomment");
      String COPY_AS_RTF = s_stringMgr.getString("SyntaxPlugin.copyasrtf");;
   }

   /** Logger for this class. */


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

      tpa.addToToolsPopup(I18n.FIND , new FindAction(app, rsrc, isqlEntryPanel));
      tpa.addToToolsPopup(I18n.FIND_SELECTED , new FindSelectedAction(app, rsrc, isqlEntryPanel));
      tpa.addToToolsPopup(I18n.REPEAT_LAST_FIND , new RepeatLastFindAction(app, rsrc, isqlEntryPanel));
      tpa.addToToolsPopup(I18n.MARK_SELECTED , new MarkSelectedAction(app, rsrc, isqlEntryPanel));
      tpa.addToToolsPopup(I18n.REPLACE , new ReplaceAction(app, rsrc, isqlEntryPanel));
      tpa.addToToolsPopup(I18n.UNMARK , new UnmarkAction(app, rsrc, isqlEntryPanel));
      tpa.addToToolsPopup(I18n.GO_TO_LINE , new GoToLineAction(app, rsrc, isqlEntryPanel));
      tpa.addToToolsPopup(I18n.AUTO_CORR , new ConfigureAutoCorrectAction(app, rsrc, _syntaxPugin));
      tpa.addToToolsPopup(I18n.DUP_LINE , new DuplicateLineAction(app, rsrc, isqlEntryPanel));
      tpa.addToToolsPopup(I18n.COMMENT , new CommentAction(app, rsrc, isqlEntryPanel));
      tpa.addToToolsPopup(I18n.UNCOMMENT , new UncommentAction(app, rsrc, isqlEntryPanel));

      if (isqlEntryPanel.getTextComponent() instanceof SquirrelRSyntaxTextArea)
      {
         SquirrelRSyntaxTextArea rsEdit = (SquirrelRSyntaxTextArea) isqlEntryPanel.getTextComponent();

         Action toUpperAction = SquirreLRSyntaxTextAreaUI.getActionForName(rsEdit, RTextAreaEditorKit.rtaUpperSelectionCaseAction);
         toUpperAction.putValue(Resources.ACCELERATOR_STRING, ShortcutUtil.getKeystrokeString(SquirreLRSyntaxTextAreaUI.getToUpperCaseKeyStroke()));
         tpa.addToToolsPopup(I18n.TO_UPPER_CASE, toUpperAction);

         Action toLowerAction = SquirreLRSyntaxTextAreaUI.getActionForName(rsEdit, RTextAreaEditorKit.rtaLowerSelectionCaseAction);
         toLowerAction.putValue(Resources.ACCELERATOR_STRING, ShortcutUtil.getKeystrokeString(SquirreLRSyntaxTextAreaUI.getToLowerCaseKeyStroke()));
         tpa.addToToolsPopup(I18n.TO_LOWER_CASE, toLowerAction);
         
         tpa.addToToolsPopup(I18n.COPY_AS_RTF, _syntaxPugin.getApplication().getActionCollection().get(SquirrelCopyAsRtfAction.class));
      }
   }


   void initToolsPopup(ActionCollection coll, ISQLPanelAPI sqlPanelAPI)
   {
      sqlPanelAPI.addToToolsPopUp(I18n.FIND, coll.get(FindAction.class));
      sqlPanelAPI.addToToolsPopUp(I18n.FIND_SELECTED , coll.get(FindSelectedAction.class));
      sqlPanelAPI.addToToolsPopUp(I18n.REPEAT_LAST_FIND , coll.get(RepeatLastFindAction.class));
      sqlPanelAPI.addToToolsPopUp(I18n.MARK_SELECTED , coll.get(MarkSelectedAction.class));
      sqlPanelAPI.addToToolsPopUp(I18n.REPLACE, coll.get(ReplaceAction.class));
      sqlPanelAPI.addToToolsPopUp(I18n.UNMARK, coll.get(UnmarkAction.class));
      sqlPanelAPI.addToToolsPopUp(I18n.GO_TO_LINE, coll.get(GoToLineAction.class));
      sqlPanelAPI.addToToolsPopUp(I18n.AUTO_CORR, coll.get(ConfigureAutoCorrectAction.class));
      sqlPanelAPI.addToToolsPopUp(I18n.DUP_LINE, coll.get(DuplicateLineAction.class));
      sqlPanelAPI.addToToolsPopUp(I18n.COMMENT, coll.get(CommentAction.class));
      sqlPanelAPI.addToToolsPopUp(I18n.UNCOMMENT, coll.get(UncommentAction.class));

      if (sqlPanelAPI.getSQLEntryPanel().getTextComponent() instanceof SquirrelRSyntaxTextArea)
      {
         SquirrelRSyntaxTextArea rsEdit = (SquirrelRSyntaxTextArea) sqlPanelAPI.getSQLEntryPanel().getTextComponent();

         Action toUpperAction = SquirreLRSyntaxTextAreaUI.getActionForName(rsEdit, RTextAreaEditorKit.rtaUpperSelectionCaseAction);
         toUpperAction.putValue(Resources.ACCELERATOR_STRING, ShortcutUtil.getKeystrokeString(SquirreLRSyntaxTextAreaUI.getToUpperCaseKeyStroke()));
         sqlPanelAPI.addToToolsPopUp(I18n.TO_UPPER_CASE, toUpperAction);

         Action toLowerAction = SquirreLRSyntaxTextAreaUI.getActionForName(rsEdit, RTextAreaEditorKit.rtaLowerSelectionCaseAction);
         toLowerAction.putValue(Resources.ACCELERATOR_STRING, ShortcutUtil.getKeystrokeString(SquirreLRSyntaxTextAreaUI.getToLowerCaseKeyStroke()));
         sqlPanelAPI.addToToolsPopUp(I18n.TO_LOWER_CASE, toLowerAction);

         sqlPanelAPI.addToToolsPopUp(I18n.COPY_AS_RTF, _syntaxPugin.getApplication().getActionCollection().get(SquirrelCopyAsRtfAction.class));
      }
   }
}
