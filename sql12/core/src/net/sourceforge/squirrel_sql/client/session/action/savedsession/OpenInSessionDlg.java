package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class OpenInSessionDlg extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(OpenInSessionDlg.class);

   private OpenInSessionPanel _openInSessionPanel;

   private JButton _btnOk;
   private JButton _btnCancel;
   private boolean _isOk;

   public OpenInSessionDlg(JFrame owner, String savedSessionName, boolean warnDiscardExistingSqlEditors)
   {
      super(owner, s_stringMgr.getString("OpenInSessionDlg.title"), true);


      layoutUI(savedSessionName, warnDiscardExistingSqlEditors);

      _btnOk.addActionListener(e -> onOk());
      _btnCancel.addActionListener(e -> close());

      GUIUtils.initLocation(this, 450, 180);
      GUIUtils.enableCloseByEscape(this);

      setVisible(true);
   }

   private void onOk()
   {
      _isOk = true;
      close();
   }

   private void close()
   {
      setVisible(false);
      dispose();
   }


   private void layoutUI(String savedSessionName, boolean warnDiscardExistingSqlEditors)
   {
      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0);
      _openInSessionPanel = new OpenInSessionPanel(savedSessionName, warnDiscardExistingSqlEditors);
      getContentPane().add(_openInSessionPanel, gbc);

      gbc = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(10, 5, 5, 5), 0, 0);
      getContentPane().add(createButtonPanel(), gbc);
   }

   private JPanel createButtonPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0);
      _btnOk = new JButton(s_stringMgr.getString("OpenInSessionDlg.Ok"));
      ret.add(_btnOk, gbc);

      gbc = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0);
      _btnCancel = new JButton(s_stringMgr.getString("OpenInSessionDlg.Cancel"));
      ret.add(_btnCancel, gbc);

      GUIUtils.setJButtonSizesTheSame(_btnOk, _btnCancel);

      getRootPane().setDefaultButton(_btnOk);

      return ret;
   }

   public boolean isOk()
   {
      return _isOk;
   }

   public boolean isOpenInNewSession()
   {
      return _openInSessionPanel.isOpenInNewSession();
   }
}
