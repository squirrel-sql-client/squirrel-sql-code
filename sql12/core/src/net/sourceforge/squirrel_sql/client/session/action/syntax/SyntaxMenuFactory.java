package net.sourceforge.squirrel_sql.client.session.action.syntax;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.SQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.action.syntax.rsyntax.SquirreLRSyntaxTextAreaUI;
import net.sourceforge.squirrel_sql.client.session.action.syntax.rsyntax.SquirrelRSyntaxTextArea;
import net.sourceforge.squirrel_sql.client.session.action.syntax.rsyntax.action.SquirrelCopyAsRtfAction;
import net.sourceforge.squirrel_sql.client.shortcut.ShortcutUtil;
import net.sourceforge.squirrel_sql.fw.resources.Resources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import org.fife.ui.rtextarea.RTextAreaEditorKit;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import java.util.ArrayList;
import java.util.List;

public class SyntaxMenuFactory
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SyntaxMenuFactory.class);


   public static void addMenuItemsToSQLPanelApi(SQLPanelAPI sqlPanelAPI)
   {
      IApplication app = Main.getApplication();
      JMenuItem mnuUnmark = sqlPanelAPI.addToSQLEntryAreaMenu(app.getActionCollection().get(UnmarkAction.class));
      app.getResources().configureMenuItem(app.getActionCollection().get(UnmarkAction.class), mnuUnmark);


      //////////////////////////////////////////////////////////////////////
      //
      JMenuItem mnuComment = sqlPanelAPI.addToSQLEntryAreaMenu(app.getActionCollection().get(CommentAction.class));
      app.getResources().configureMenuItem(app.getActionCollection().get(CommentAction.class), mnuComment);

      JMenuItem mnuCommentAltAccelerator = sqlPanelAPI.addToSQLEntryAreaMenu(app.getActionCollection().get(CommentActionAltAccelerator.class));
      app.getResources().configureMenuItem(app.getActionCollection().get(CommentActionAltAccelerator.class), mnuCommentAltAccelerator);
      mnuCommentAltAccelerator.setVisible(false);
      //
      //////////////////////////////////////////////////////////////////////


      /////////////////////////////////////////////////////////////////////
      //
      JMenuItem mnuUncomment = sqlPanelAPI.addToSQLEntryAreaMenu(app.getActionCollection().get(UncommentAction.class));
      app.getResources().configureMenuItem(app.getActionCollection().get(UncommentAction.class), mnuUncomment);

      JMenuItem mnuUncommentAltAccelerator = sqlPanelAPI.addToSQLEntryAreaMenu(app.getActionCollection().get(UncommentActionAltAccelerator.class));
      app.getResources().configureMenuItem(app.getActionCollection().get(UncommentActionAltAccelerator.class), mnuUncommentAltAccelerator);
      mnuUncommentAltAccelerator.setVisible(false);
      //
      /////////////////////////////////////////////////////////////////////


      if (sqlPanelAPI.getSQLEntryPanel().getTextComponent() instanceof SquirrelRSyntaxTextArea)
      {

         JMenuItem mnuCopyToRtf = sqlPanelAPI.addToSQLEntryAreaMenu(app.getActionCollection().get(SquirrelCopyAsRtfAction.class));
         app.getResources().configureMenuItem(app.getActionCollection().get(SquirrelCopyAsRtfAction.class), mnuCopyToRtf);

         SquirrelRSyntaxTextArea rsEdit = (SquirrelRSyntaxTextArea) sqlPanelAPI.getSQLEntryPanel().getTextComponent();

         configureRichTextAction(sqlPanelAPI, rsEdit, RTextAreaEditorKit.rtaUpperSelectionCaseAction, SquirreLRSyntaxTextAreaUI.getToUpperCaseKeyStroke(), s_stringMgr.getString("SyntaxPlugin.ToUpperShortDescription"));
         configureRichTextAction(sqlPanelAPI, rsEdit, RTextAreaEditorKit.rtaLowerSelectionCaseAction, SquirreLRSyntaxTextAreaUI.getToLowerCaseKeyStroke(), s_stringMgr.getString("SyntaxPlugin.ToLowerShortDescription"));

         configureRichTextAction(sqlPanelAPI, rsEdit, RTextAreaEditorKit.rtaLineUpAction, SquirreLRSyntaxTextAreaUI.getLineUpKeyStroke(), s_stringMgr.getString("SyntaxPlugin.LineUpShortDescription"));
         configureRichTextAction(sqlPanelAPI, rsEdit, RTextAreaEditorKit.rtaLineDownAction, SquirreLRSyntaxTextAreaUI.getLineDownKeyStroke(), s_stringMgr.getString("SyntaxPlugin.LineDownShortDescription"));

      }
   }

   private static void configureRichTextAction(ISQLPanelAPI sqlPanelAPI, SquirrelRSyntaxTextArea rsEdit, String rtaKey, KeyStroke acceleratorKeyStroke, String shortDescription)
   {
      Action action = SquirreLRSyntaxTextAreaUI.getActionForName(rsEdit, rtaKey);
      action.putValue(Resources.ACCELERATOR_STRING, ShortcutUtil.getKeystrokeString(acceleratorKeyStroke));

      action.putValue(Action.SHORT_DESCRIPTION, shortDescription);
      action.putValue(Action.MNEMONIC_KEY, 0);
      action.putValue(Action.ACCELERATOR_KEY, acceleratorKeyStroke);

      JMenuItem mnu = sqlPanelAPI.addToSQLEntryAreaMenu(action);
      mnu.setText((String) action.getValue(Action.SHORT_DESCRIPTION));
      Main.getApplication().getResources().configureMenuItem(action, mnu, true);
   }

   public static List<Action> getSQLInternalFrameToolbarActions()
   {
      List<Action> ret = new ArrayList<>();
      ret.add(Main.getApplication().getActionCollection().get(FindAction.class));
      ret.add(Main.getApplication().getActionCollection().get(ReplaceAction.class));

      return ret;
   }
}
