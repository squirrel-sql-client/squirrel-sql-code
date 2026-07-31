package net.sourceforge.squirrel_sql.client.session.mcp.ui;

import java.awt.Frame;
import java.util.HashMap;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.syntax.rsyntax.RSyntaxSQLEntryAreaFactory;
import net.sourceforge.squirrel_sql.client.session.parser.IParserEventsProcessorFactory;
import net.sourceforge.squirrel_sql.client.util.codereformat.CodeReformator;
import net.sourceforge.squirrel_sql.client.util.codereformat.CodeReformatorConfigFactory;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.texteditdlg.TextEditController;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import org.apache.commons.lang3.StringUtils;

public class McpCallApproveCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(McpCallApproveCtrl.class);

   private McpCallApproveDlg _mcpCallApproveDlg;

   private final McpUiProps _mcpUiProps;
   private boolean _approved;
   private String _userToAiDisapproveResponse;
   private PreviousTextsAiDisapprovalMessages _previousTextsProvider = new PreviousTextsAiDisapprovalMessages();

   public McpCallApproveCtrl(String call, McpUiProps mcpUiProps, ISession session, Frame owningFrame)
   {
      _mcpUiProps = mcpUiProps;


      HashMap props = new HashMap<>();
      props.put(IParserEventsProcessorFactory.class.getName(), null);
      ISQLEntryPanel sqlEntryPanel = new RSyntaxSQLEntryAreaFactory().createSQLEntryPanel(session, props);
      sqlEntryPanel.getTextComponent().setEditable(false);


      _mcpCallApproveDlg = new McpCallApproveDlg(owningFrame, sqlEntryPanel);

      _mcpCallApproveDlg.sqlEntryPanel.setText(call);
      _mcpCallApproveDlg.sqlEntryPanel.setCaretPosition(0);

      _mcpCallApproveDlg.btnFormat.addActionListener(e -> onFormat(session));

      _mcpCallApproveDlg.btnEditAIResponseMessage.addActionListener(e -> onEditAIResponseMessage());
      _mcpCallApproveDlg.btnRun.addActionListener(e -> onFormat(session));

      _mcpCallApproveDlg.btnApprove.addActionListener(e -> onApprove(true));
      _mcpCallApproveDlg.btnDisapprove.addActionListener(e -> onApprove(false));

      GUIUtils.initLocation(_mcpCallApproveDlg, 500, 400);
      GUIUtils.enableCloseByEscape(_mcpCallApproveDlg);

      _mcpCallApproveDlg.setVisible(true);

   }

   private void onEditAIResponseMessage()
   {
      TextEditController textEditController =
            new TextEditController(_mcpCallApproveDlg,
                                   s_stringMgr.getString("McpCallApproveCtrl.edit.ai.disapproval.message.title"),
                                   s_stringMgr.getString("McpCallApproveCtrl.edit.ai.disapproval.message.description"),
                                   s_stringMgr.getString("McpCallApproveCtrl.edit.ai.disapproval.message.empty.title"),
                                   s_stringMgr.getString("McpCallApproveCtrl.edit.ai.disapproval.message.empty.text"),
                                   _previousTextsProvider
                                   );

      textEditController.setAllowEmptyText(true);

      String responseBuf = textEditController.getText();

      if( textEditController.isOk() )
      {
         _userToAiDisapproveResponse = responseBuf;
      }

      if(StringUtils.isNotBlank(_userToAiDisapproveResponse))
      {
         _mcpCallApproveDlg.btnEditAIResponseMessage.setIcon(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.EDIT_NOTE_CHECKED));
      }
      else
      {
         _mcpCallApproveDlg.btnEditAIResponseMessage.setIcon(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.EDIT_NOTE));
      }

      _previousTextsProvider.setCurrentApproveResponse(_userToAiDisapproveResponse);
   }

   private void onFormat(ISession session)
   {
      CodeReformator cr = new CodeReformator(CodeReformatorConfigFactory.createConfig(session));
      _mcpCallApproveDlg.sqlEntryPanel.setText(cr.reformat(_mcpCallApproveDlg.sqlEntryPanel.getText()));
      _mcpCallApproveDlg.sqlEntryPanel.setCaretPosition(0);
   }

   private void onApprove(boolean b)
   {
      _approved = b;
      _mcpCallApproveDlg.setVisible(false);
      _mcpCallApproveDlg.dispose();

   }

   public boolean isApproved()
   {
      return _approved;
   }

   public String getUserToAiDisapproveResponse()
   {
      return StringUtils.isBlank(_userToAiDisapproveResponse) ? null : _userToAiDisapproveResponse;
   }
}
