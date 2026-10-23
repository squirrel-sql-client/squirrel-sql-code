package net.sourceforge.squirrel_sql.client.session.mcp.ui;

import java.awt.Frame;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JFrame;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SqlPanelExecutionFutureApprovalListener;
import net.sourceforge.squirrel_sql.client.session.action.syntax.rsyntax.RSyntaxSQLEntryAreaFactory;
import net.sourceforge.squirrel_sql.client.session.mainpanel.CancelPanelCtrl;
import net.sourceforge.squirrel_sql.client.session.mainpanel.ErrorPanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.ResultTab;
import net.sourceforge.squirrel_sql.client.session.mcp.server.McpApprovalCallPreviewBuilder;
import net.sourceforge.squirrel_sql.client.session.mcp.server.McpCall;
import net.sourceforge.squirrel_sql.client.session.mcp.server.McpCallExecutor;
import net.sourceforge.squirrel_sql.client.session.mcp.server.McpExecuteQueryResult;
import net.sourceforge.squirrel_sql.client.session.mcp.server.McpQueryExecuter;
import net.sourceforge.squirrel_sql.client.session.parser.IParserEventsProcessorFactory;
import net.sourceforge.squirrel_sql.client.util.codereformat.CodeReformator;
import net.sourceforge.squirrel_sql.client.util.codereformat.CodeReformatorConfigFactory;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice.ResultTableType;
import net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind.DataSetViewerFindHandler;
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

   public McpCallApproveCtrl(McpCallExecutor callExecutor, String call, McpUiProps mcpUiProps, ISession session, Frame owningFrame)
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
      _mcpCallApproveDlg.btnRun.addActionListener(e -> onExecuteCall(callExecutor));

      _mcpCallApproveDlg.btnApprove.addActionListener(e -> onApprove(true));
      _mcpCallApproveDlg.btnDisapprove.addActionListener(e -> onApprove(false));

      GUIUtils.enableCloseByEscape(_mcpCallApproveDlg);

      showDialog();
   }

   /**
    * The code in this method fixes the following bug:
    * SQuirreL-MCP: When SQuirreL's main frame was in background the AI-approve dialog
    *   wasn't displayed and SQuirreL became inaccessible.
    */
   private void showDialog()
   {
      Runnable showDialogRunnable = () -> {
         GUIUtils.initLocation(_mcpCallApproveDlg, 500, 400);
         _mcpCallApproveDlg.setAlwaysOnTop(true); // Makes it stay on top
         _mcpCallApproveDlg.setVisible(true);
         _mcpCallApproveDlg.setAlwaysOnTop(false); // Revert after showing
      };


      boolean isMinimized = (Main.getApplication().getMainFrame().getExtendedState() & Frame.ICONIFIED) == Frame.ICONIFIED;
      if(isMinimized)
      {
         Main.getApplication().getMainFrame().setExtendedState(Frame.NORMAL);
         GUIUtils.executeDelayed(showDialogRunnable, 300);
      }
      else
      {
         showDialogRunnable.run();
      }
   }

   private void onExecuteCall(McpCallExecutor callExecutor)
   {
      if(callExecutor.getMcpCall() == McpCall.executeQuery)
      {
         Main.getApplication().getThreadPool().addTask(() -> executeAndDisplayQuery(callExecutor));
      }
      else
      {
         DataSetViewerTablePanel table = callExecutor.getMcpCall().buildDataSetViewerTablePanelForApproval(callExecutor);

         displayDataSetViewerTablePanel(table);
      }
   }

   private void executeAndDisplayQuery(McpCallExecutor callExecutor)
   {
      AtomicBoolean isQueryErrorPanelBeingDisplayed = new AtomicBoolean(false);

      SqlPanelExecutionFutureApprovalListener sqlPanelExecutionFutureApprovalListener = new SqlPanelExecutionFutureApprovalListener()
      {
         @Override
         public void displayCancelPanelCtrl(CancelPanelCtrl cancelPanelCtrl)
         {
            _mcpCallApproveDlg.displaySqlQueryExecutionUI(cancelPanelCtrl.getPanel());
         }

         @Override
         public void displayErrorPanel(ErrorPanel errorPanel)
         {
            isQueryErrorPanelBeingDisplayed.set(true);
            _mcpCallApproveDlg.displaySqlQueryExecutionUI(errorPanel);
         }
      };

      McpExecuteQueryResult mcpExecuteQueryResult =
            McpQueryExecuter.executeQueryForApproval(callExecutor.getSql(), callExecutor.getMcpServerContext(), sqlPanelExecutionFutureApprovalListener);

      callExecutor.setCallResult(mcpExecuteQueryResult);

      GUIUtils.processOnSwingEventThread(() -> {
         if(mcpExecuteQueryResult.isUpdateMessage())
         {
            displayDataSetViewerTablePanel(McpApprovalCallPreviewBuilder.createDataSetViewerTablePanelOfString(McpCall.executeQuery, mcpExecuteQueryResult.getUpdateMessage()));
         }
         else if(mcpExecuteQueryResult.isErrorMessage())
         {
            if( false == isQueryErrorPanelBeingDisplayed.get() )
            {
               // We end up here e.g. when executing UPDATE/DELETE/INSERT was forbidden by the MCP.
               displayDataSetViewerTablePanel(McpApprovalCallPreviewBuilder.createDataSetViewerTablePanelOfString(McpCall.executeQuery, mcpExecuteQueryResult.getErrorMessage()));
            }
         }
         else
         {
            //mcpExecuteQueryResult.resultTab().setBorder(BorderFactory.createLineBorder(Color.red, 5));
            mcpExecuteQueryResult.resultTab().prepareMcpApproveDisplay();
            _mcpCallApproveDlg.displaySqlQueryExecutionUI(mcpExecuteQueryResult.resultTab());
         }
      });

   }

   private void displayDataSetViewerTablePanel(DataSetViewerTablePanel table)
   {
      DataSetViewerFindHandler dataSetViewerFindHandler = new DataSetViewerFindHandler(table, ResultTableType.ROWS_WINDOW, new JFrame());

      _mcpCallApproveDlg.btnFindInResult.setEnabled(true);
      _mcpCallApproveDlg.btnFindInResult.addActionListener(e -> dataSetViewerFindHandler.toggleShowFindPanel());

      _mcpCallApproveDlg.displaySqlQueryExecutionUI(dataSetViewerFindHandler.getComponent());
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

      if(_mcpCallApproveDlg.removeDisplayedSqlQueryExecutionUI() instanceof ResultTab resultTab)
      {
         resultTab.unprepareMcpApproveDisplay();
         resultTab.returnToTabbedPane();
      }
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
