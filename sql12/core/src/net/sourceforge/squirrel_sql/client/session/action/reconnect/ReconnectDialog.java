package net.sourceforge.squirrel_sql.client.session.action.reconnect;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

public class ReconnectDialog extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ReconnectController.class);

   JButton btnYes;
   JButton btnNo;
   JButton btnCancel = new JButton(s_stringMgr.getString("ReconnectAction.cancel"));
   JButton btnToggleCollapsed;
   JTextField txtUrl = new JTextField();
   JTextField txtUser = new JTextField();
   JPasswordField txtPassword = new JPasswordField();

   private JPanel _pnlCollapseableHolder = new JPanel(new GridLayout(1,1));

   private JPanel _collapsedPanel;
   private JPanel _uncollapsedPanel;
   private ImageIcon _iconPlus = Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.PLUS);
   private ImageIcon _iconMinus = Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.MINUS);

   public ReconnectDialog()
   {
      super(Main.getApplication().getMainFrame(), s_stringMgr.getString("ReconnectAction.reconnect.title"), true);

      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(15,5,5,5),0,0);
      JLabel lblTitle = new JLabel(s_stringMgr.getString("ReconnectAction.confirmReconnect"));
      getContentPane().add(lblTitle, gbc);

      gbc = new GridBagConstraints(0,1,1,1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,5,5,5),0,0);
      getContentPane().add(createCollapsablePanel(), gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      getContentPane().add(createButtonPanel(), gbc);


      getRootPane().setDefaultButton(btnYes);
   }

   private JPanel createButtonPanel()
   {
      JPanel ret = new JPanel(new BorderLayout(5,5));

      btnYes = new JButton(s_stringMgr.getString("ReconnectAction.yes"));
      btnYes.setMnemonic(s_stringMgr.getString("ReconnectAction.yes.mnemonic").charAt(0));
      ret.add(btnYes, BorderLayout.WEST);

      btnNo = new JButton(s_stringMgr.getString("ReconnectAction.no"));
      btnNo.setMnemonic(s_stringMgr.getString("ReconnectAction.no.mnemonic").charAt(0));
      ret.add(btnNo, BorderLayout.CENTER);

      ret.add(btnCancel, BorderLayout.EAST);

      return ret;
   }

   private JPanel createCollapsablePanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      _collapsedPanel = new JPanel();
      _collapsedPanel.setBorder(BorderFactory.createEtchedBorder());
      _collapsedPanel.setPreferredSize(new Dimension(_collapsedPanel.getPreferredSize().width, 4));

      _uncollapsedPanel = createUncollapsedPanel();

      GridBagConstraints gbc;

      btnToggleCollapsed = new JButton(_iconPlus);
      GUIUtils.styleAsToolbarButton(btnToggleCollapsed);

      int top = btnToggleCollapsed.getPreferredSize().height / 2 - 1;
      gbc = new GridBagConstraints(0,0,1,1,1,0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(top,0,0,0),0,0);
      ret.add(_pnlCollapseableHolder, gbc);
      collapse();

      gbc = new GridBagConstraints(1,0,1,1,0,1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0),0,0);
      ret.add(btnToggleCollapsed, gbc);

      btnToggleCollapsed.addActionListener(e -> onToggleCollapsed());

      //ret.setBorder(BorderFactory.createLineBorder(Color.RED));
      return ret;
   }

   private JPanel createUncollapsedPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,2,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5),0,0);
      ret.add(new MultipleLineLabel(s_stringMgr.getString("ReconnectAction.connect.data.change.use")), gbc);


      gbc = new GridBagConstraints(0,1,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      ret.add(new JLabel(s_stringMgr.getString("ReconnectAction.url")), gbc);

      gbc = new GridBagConstraints(1,1,1,1,1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5),0,0);
      ret.add(txtUrl, gbc);


      gbc = new GridBagConstraints(0,2,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      ret.add(new JLabel(s_stringMgr.getString("ReconnectAction.user")), gbc);

      gbc = new GridBagConstraints(1,2,1,1,1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5),0,0);
      ret.add(txtUser, gbc);


      gbc = new GridBagConstraints(0,3,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      ret.add(new JLabel(s_stringMgr.getString("ReconnectAction.password")), gbc);

      gbc = new GridBagConstraints(1,3,1,1,1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5),0,0);
      ret.add(txtPassword, gbc);



      ret.setBorder(BorderFactory.createEtchedBorder());
      return ret;
   }

   private void onToggleCollapsed()
   {
      if(_iconMinus == btnToggleCollapsed.getIcon())
      {
         collapse();
      }
      else if(_iconPlus == btnToggleCollapsed.getIcon())
      {
         uncollapse();
      }
   }

   void uncollapse()
   {
      _pnlCollapseableHolder.removeAll();
      _pnlCollapseableHolder.add(_uncollapsedPanel);
      setSize(new Dimension(500, 300));

      btnToggleCollapsed.setIcon(_iconMinus);
   }

   void collapse()
   {
      _pnlCollapseableHolder.removeAll();
      _pnlCollapseableHolder.add(_collapsedPanel);
      _pnlCollapseableHolder.invalidate();
      setSize(new Dimension(500, 150));

      btnToggleCollapsed.setIcon(_iconPlus);

   }

   public boolean isCollapsed()
   {
      return btnToggleCollapsed.getIcon() == _iconPlus;
   }
}
