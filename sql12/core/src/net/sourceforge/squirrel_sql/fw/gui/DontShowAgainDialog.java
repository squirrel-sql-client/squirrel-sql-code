package net.sourceforge.squirrel_sql.fw.gui;

import com.jidesoft.swing.MultilineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;

public class DontShowAgainDialog extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DontShowAgainDialog.class);

   private JCheckBox chkDontShowAgain = new JCheckBox(s_stringMgr.getString("DontShowAgainDialog.dontShowAgainMsg"));
   private JButton btnYes;
   private JButton btnNo;
   private JButton btnCancel = new JButton(s_stringMgr.getString("DontShowAgainDialog.cancel"));
   private DontShowAgainResult _result = new DontShowAgainResult();


   public DontShowAgainDialog(Window owner, String msg, String switchBackOnHowTo)
   {
      super(owner, ModalityType.APPLICATION_MODAL);

      createUI(msg, switchBackOnHowTo);

      btnYes.addActionListener( e -> onYes());
      btnNo.addActionListener( e -> onNo());
      btnCancel.addActionListener( e -> onCancel());

      getRootPane().setDefaultButton(btnYes);
   }

   private void onCancel()
   {
      _result.setCancel(true);
      close();
   }

   private void onNo()
   {
      _result.setNo(true);
      close();
   }

   private void onYes()
   {
      _result.setYes(true);
      close();
   }

   private void close()
   {
      setVisible(false);
      dispose();
   }

   public DontShowAgainResult showAndGetResult(String identifier, int defaultWidth, int defaultHeight)
   {
      GUIUtils.enableCloseByEscape(this);
      GUIUtils.initLocation(this, defaultWidth, defaultHeight, "DontShowAgainDialog." + identifier);

      setVisible(true);

      _result.setDontShowAgain(chkDontShowAgain.isSelected());
      return _result;
   }

   private void createUI(String msg, String switchBackOnHowTo)
   {
      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,0,5),0,0 );
      add(new MultilineLabel(msg), gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,0,5),0,0 );
      add(createDontShowAgainPanel(switchBackOnHowTo), gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(20,5,0,5),0,0 );
      add(createButtonsPanel(), gbc);

      gbc = new GridBagConstraints(0,3,1,1,1,1,GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0,0,0,0),0,0 );
      add(new JPanel(), gbc);

   }

   private JPanel createDontShowAgainPanel(String switchBackOnHowTo)
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0),0,0 );
      ret.add(chkDontShowAgain, gbc);

      gbc = new GridBagConstraints(1,1,1,1,0,0,GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0),0,0 );
      ret.add(new SmallToolTipInfoButton(switchBackOnHowTo).getButton(), gbc);

      return ret;
   }

   private JPanel createButtonsPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0),0,0 );
      btnYes = new JButton(s_stringMgr.getString("DontShowAgainDialog.yes"));
      btnYes.setMnemonic(s_stringMgr.getString("DontShowAgainDialog.yes.mnemonic").charAt(0));
      ret.add(btnYes, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,0,0),0,0 );
      btnNo = new JButton(s_stringMgr.getString("DontShowAgainDialog.no"));
      btnNo.setMnemonic(s_stringMgr.getString("DontShowAgainDialog.no.mnemonic").charAt(0));
      ret.add(btnNo, gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,0,0),0,0 );
      ret.add(btnCancel, gbc);

      return ret;
   }
}
