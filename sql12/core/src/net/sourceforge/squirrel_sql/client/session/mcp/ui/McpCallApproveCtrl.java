package net.sourceforge.squirrel_sql.client.session.mcp.ui;

import java.awt.Frame;
import java.util.HashMap;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.syntax.rsyntax.RSyntaxSQLEntryAreaFactory;
import net.sourceforge.squirrel_sql.client.session.parser.IParserEventsProcessorFactory;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

public class McpCallApproveCtrl
{
   private McpCallApproveDlg _mcpCallApproveDlg;

   private final McpUiProps _mcpUiProps;
   private boolean _approved;

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

      _mcpCallApproveDlg.btnApprove.addActionListener(e -> onApprove(true));
      _mcpCallApproveDlg.btnDisapprove.addActionListener(e -> onApprove(false));

      GUIUtils.initLocation(_mcpCallApproveDlg, 500, 400);
      GUIUtils.enableCloseByEscape(_mcpCallApproveDlg);

      _mcpCallApproveDlg.setVisible(true);

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
}
