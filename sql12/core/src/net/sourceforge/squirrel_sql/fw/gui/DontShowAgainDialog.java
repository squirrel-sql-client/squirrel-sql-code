package net.sourceforge.squirrel_sql.fw.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import net.sourceforge.squirrel_sql.fw.gui.buttontabcomponent.SmallToolTipInfoButton;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

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

      // auto-adjust height once dialog becomes visible
      addComponentListener(new ComponentAdapter()
      {
         @Override
         public void componentShown(ComponentEvent event)
         {
            setSize(new Dimension(getWidth(), getPreferredSize().height));
         }
      });
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
      //GUIUtils.centerWithinParent(this);
      GUIUtils.initLocation(this, defaultWidth, defaultHeight, "DontShowAgainDialog." + identifier);

      setVisible(true);

      _result.setDontShowAgain(chkDontShowAgain.isSelected());
      return _result;
   }

   private void createUI(String msg, String switchBackOnHowTo)
   {
      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;
      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10,10,0,20),0,0 );
      add(new JLabel(UIManager.getIcon("OptionPane.questionIcon")), gbc);

      gbc = new GridBagConstraints(1,0,1,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(10,5,0,10),0,0 );
      add(new MultipleLineLabel(msg), gbc);

      gbc = new GridBagConstraints(0,1,2,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,10,0,10),0,0 );
      add(createDontShowAgainPanel(switchBackOnHowTo), gbc);

      gbc = new GridBagConstraints(0,2,2,1,0,0,GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(10,10,10,10),0,0 );
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
