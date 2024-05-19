package net.sourceforge.squirrel_sql.fw.gui;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.*;
import java.awt.*;

public class ResizableTextEditDialog extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ResizableTextEditDialog.class);

   private JTextField _txtEditedText = new JTextField();
   private JButton _btnOk;
   private JButton _btnCancel;

   private boolean _ok;


   public ResizableTextEditDialog(Window parentFrame,
                                  String originKey,
                                  String dialogTitle,
                                  String textFieldLabel,
                                  String initialEditText)
   {
      this(parentFrame,
           originKey,
           dialogTitle,
           textFieldLabel,
           initialEditText,
           null);
   }

   public ResizableTextEditDialog(Window parentFrame,
                                  String originKey,
                                  String dialogTitle,
                                  String textFieldLabel,
                                  String initialEditText,
                                  ResizableTextEditDialogBeforeOkCloseCallback okCloseCallback)
   {
      super(parentFrame, dialogTitle, ModalityType.APPLICATION_MODAL);

      layoutUI(initialEditText, textFieldLabel);

      _btnOk.addActionListener(e -> onOk(okCloseCallback));
      _btnCancel.addActionListener(e -> close());

      getRootPane().setDefaultButton(_btnOk);

      GUIUtils.enableCloseByEscape(this);
      GUIUtils.initLocation(this, 450, 140, originKey);

      GUIUtils.forceFocus(_txtEditedText);
      setVisible(true);
   }

   private void onOk(ResizableTextEditDialogBeforeOkCloseCallback okCloseCallback)
   {

      if(StringUtilities.isEmpty(_txtEditedText.getText(), true))
      {
         JOptionPane.showMessageDialog(this, s_stringMgr.getString("ResizableTextEditDialog.empty.name"));
         return;
      }

      if(null != okCloseCallback && false == okCloseCallback.allowCloseOnOk(this, _txtEditedText.getText().trim()))
      {
         return;
      }

      _ok = true;

      close();
   }

   public boolean isOk()
   {
      return _ok;
   }

   public String getEditedText()
   {
      return _txtEditedText.getText().trim();
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

   private void layoutUI(String savedSessionNameTemplate, String textFieldLabel)
   {
      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10,10, 0,10), 0,0);
      getContentPane().add(new JLabel(textFieldLabel), gbc);

      gbc = new GridBagConstraints(0,1,1,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,10, 0,10), 0,0);
      _txtEditedText.setText(savedSessionNameTemplate);
      getContentPane().add(_txtEditedText, gbc);

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
      _btnOk = new JButton(s_stringMgr.getString("ResizableTextEditDialog.ok"));
      ret.add(_btnOk, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5, 0,0), 0,0);
      _btnCancel = new JButton(s_stringMgr.getString("ResizableTextEditDialog.cancel"));
      ret.add(_btnCancel, gbc);

      return ret;
   }
}
