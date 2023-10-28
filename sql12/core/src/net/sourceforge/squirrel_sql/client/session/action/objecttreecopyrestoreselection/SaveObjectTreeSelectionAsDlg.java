package net.sourceforge.squirrel_sql.client.session.action.objecttreecopyrestoreselection;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.*;
import java.awt.*;

public class SaveObjectTreeSelectionAsDlg extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SaveObjectTreeSelectionAsDlg.class);

   private JTextField _txtSelectionName = new JTextField();
   private JButton _btnOk;
   private JButton _btnCancel;

   private boolean _ok;


   public SaveObjectTreeSelectionAsDlg(Window parentFrame, String generatedName)
   {
      super(parentFrame, s_stringMgr.getString("SaveObjectTreeSelectionAsDlg.title"), ModalityType.APPLICATION_MODAL);

      layoutUI(generatedName);

      _btnOk.addActionListener(e -> onOk());
      _btnCancel.addActionListener(e -> close());

      getRootPane().setDefaultButton(_btnOk);

      GUIUtils.enableCloseByEscape(this);
      GUIUtils.initLocation(this, 450, 140);

      GUIUtils.forceFocus(_txtSelectionName);
      setVisible(true);
   }

   private void onOk()
   {

      if(StringUtilities.isEmpty(_txtSelectionName.getText(), true))
      {
         JOptionPane.showConfirmDialog(this, s_stringMgr.getString("SaveObjectTreeSelectionAsDlg.empty.name"));
         return;
      }
      _ok = true;

      close();
   }

   public boolean isOk()
   {
      return _ok;
   }

   public String getObjectTreeSelectionName()
   {
      return _txtSelectionName.getText().trim();
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

   private void layoutUI(String generatedName)
   {
      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10,10, 0,10), 0,0);
      getContentPane().add(new JLabel(s_stringMgr.getString("SaveObjectTreeSelectionAsDlg.label")), gbc);

      gbc = new GridBagConstraints(0,1,1,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,10, 0,10), 0,0);
      _txtSelectionName.setText(generatedName);
      _txtSelectionName.selectAll();
      getContentPane().add(_txtSelectionName, gbc);

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
      _btnOk = new JButton(s_stringMgr.getString("SaveObjectTreeSelectionAsDlg.ok"));
      ret.add(_btnOk, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5, 0,0), 0,0);
      _btnCancel = new JButton(s_stringMgr.getString("SaveObjectTreeSelectionAsDlg.cancel"));
      ret.add(_btnCancel, gbc);

      return ret;
   }
}
