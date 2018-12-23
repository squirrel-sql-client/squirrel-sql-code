package net.sourceforge.squirrel_sql.client.session.action.reconnect;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.JdbcConnectionData;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import org.jfree.util.ObjectUtilities;

import javax.swing.SwingUtilities;
import net.sourceforge.squirrel_sql.fw.props.Props;

public class ReconnectController
{
   public static final String PREF_KEY_RECONNECT_COLLAPSED = "Squirrel.reconnect.collapsed";

   private final ReconnectDialog _reconnectDialog = new ReconnectDialog();

   private final ReconnectInfo _reconnectInfo = new ReconnectInfo();

   public ReconnectController(ISession session)
   {
      JdbcConnectionData jdbcData = session.getJdbcData();

      _reconnectDialog.txtUrl.setText(jdbcData.getUrl());
      _reconnectDialog.txtUser.setText(jdbcData.getUser());
      _reconnectDialog.txtPassword.setText(jdbcData.getPassword());

      if(Props.getBoolean(PREF_KEY_RECONNECT_COLLAPSED, true))
      {
         _reconnectDialog.collapse();
      }
      else
      {
         _reconnectDialog.uncollapse();
         SwingUtilities.invokeLater(() -> prepareUrlTextField(_reconnectDialog));
      }

      _reconnectDialog.btnToggleCollapsed.addActionListener(e -> SwingUtilities.invokeLater(() -> prepareUrlTextField(_reconnectDialog)));

      _reconnectDialog.btnYes.addActionListener( e -> doReconnect(jdbcData));

      _reconnectDialog.btnNo.addActionListener( e -> close());
      _reconnectDialog.btnCancel.addActionListener( e -> close());

      GUIUtils.enableCloseByEscape(_reconnectDialog);
      GUIUtils.centerWithinParent(_reconnectDialog);

      _reconnectDialog.setVisible(true);

      Props.putBoolean(PREF_KEY_RECONNECT_COLLAPSED, _reconnectDialog.isCollapsed());
   }

   private void doReconnect(JdbcConnectionData jdbcData)
   {
      _reconnectInfo.setReconnectRequested(true);

      if(false == ObjectUtilities.equal(_reconnectDialog.txtUrl.getText(), jdbcData.getUrl()))
      {
         _reconnectInfo.setUrl(_reconnectDialog.txtUrl.getText());
      }

      if(false == ObjectUtilities.equal(_reconnectDialog.txtUser.getText(), jdbcData.getUser()))
      {
         _reconnectInfo.setUser(_reconnectDialog.txtUrl.getText());
      }

      if(false == ObjectUtilities.equal(new String(_reconnectDialog.txtPassword.getPassword()), jdbcData.getPassword()))
      {
         _reconnectInfo.setPassword(new String(_reconnectDialog.txtPassword.getPassword()));
      }

      close();
   }

   private void close()
   {
      _reconnectDialog.setVisible(false);
      _reconnectDialog.dispose();
   }

   private void prepareUrlTextField(ReconnectDialog reconnectDialog)
   {
      reconnectDialog.txtUrl.requestFocus();
      if (StringUtilities.isEmpty(reconnectDialog.txtUrl.getText()))
      {
         reconnectDialog.txtUrl.setCaretPosition(reconnectDialog.txtUrl.getText().length());
      }
   }

   public ReconnectInfo getReconnectInfo()
   {
      return _reconnectInfo;
   }
}
