package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;

public class SessionSaveDlg extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SessionSaveDlg.class);

   private JTextField _txtSavedSessionName = new JTextField();
   private JButton _btnOk;
   private JButton _btnCancel;

   private boolean _ok;


   public SessionSaveDlg(Window parentFrame, String savedSessionNameTemplate)
   {
      super(parentFrame, s_stringMgr.getString("SessionSaveDlg.title"), ModalityType.APPLICATION_MODAL);

      layoutUI(savedSessionNameTemplate);

      _btnOk.addActionListener(e -> onOk());
      _btnCancel.addActionListener(e -> close());

      getRootPane().setDefaultButton(_btnOk);

      GUIUtils.enableCloseByEscape(this);
      GUIUtils.initLocation(this, 450, 140);

      GUIUtils.forceFocus(_txtSavedSessionName);
      setVisible(true);
   }

   private void onOk()
   {

      if(StringUtilities.isEmpty(_txtSavedSessionName.getText(), true))
      {
         JOptionPane.showConfirmDialog(this, s_stringMgr.getString("SessionSaveDlg.empty.name"));
         return;
      }

      if( Main.getApplication().getSavedSessionsManager().doesNameExist(_txtSavedSessionName.getText().trim()))
      {
         JOptionPane.showConfirmDialog(this, s_stringMgr.getString("SessionSaveDlg.nonunique.name"));
         return;
      }

      _ok = true;

      close();
   }

   public boolean isOk()
   {
      return _ok;
   }

   public String getSavedSessionName()
   {
      return _txtSavedSessionName.getText().trim();
   }

   public void setOk(boolean ok)
   {
      _ok = ok;
   }

   private void close()
   {
      setVisible(false);
      dispose();
   }

   private void layoutUI(String savedSessionNameTemplate)
   {
      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10,10, 0,10), 0,0);
      getContentPane().add(new JLabel(s_stringMgr.getString("SessionSaveDlg.label")), gbc);

      gbc = new GridBagConstraints(0,1,1,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,10, 0,10), 0,0);
      _txtSavedSessionName.setText(savedSessionNameTemplate);
      getContentPane().add(_txtSavedSessionName, gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(15,10, 10,10), 0,0);
      getContentPane().add(createOkCancelPanel(), gbc);

      gbc = new GridBagConstraints(0,3,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.VERTICAL, new Insets(0,0, 0,0), 0,0);
      getContentPane().add(new JPanel(), gbc);

   }

   private JPanel createOkCancelPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0, 0,0), 0,0);
      _btnOk = new JButton(s_stringMgr.getString("SessionSaveDlg.ok"));
      ret.add(_btnOk, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5, 0,0), 0,0);
      _btnCancel = new JButton(s_stringMgr.getString("SessionSaveDlg.cancel"));
      ret.add(_btnCancel, gbc);

      return ret;
   }
}
