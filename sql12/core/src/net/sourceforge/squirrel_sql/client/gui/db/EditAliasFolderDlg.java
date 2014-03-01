package net.sourceforge.squirrel_sql.client.gui.db;

import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class EditAliasFolderDlg extends JDialog
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(EditAliasFolderDlg.class);



   private JTextField _txtFolderName = new JTextField();

   private JButton _btnOK = new JButton(s_stringMgr.getString("EditAliasFolderDlg.OK"));
   private JButton _btnCancel = new JButton(s_stringMgr.getString("EditAliasFolderDlg.Cancel"));

   private String _folderName;


   public EditAliasFolderDlg(MainFrame mainFrame, String title, String text, String folderName)
   {
      super(mainFrame, title, true);
      createUI(text);
      _txtFolderName.setText(folderName);

      _btnOK.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onOK();
         }
      });

      getRootPane().setDefaultButton(_btnOK);

      _btnCancel.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onCancel();
         }
      });

      AbstractAction closeAction = new AbstractAction()
      {
         public void actionPerformed(ActionEvent actionEvent)
         {
            close();
         }
      };
      KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
      getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(escapeStroke, "CloseAction");
      getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, "CloseAction");
      getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(escapeStroke, "CloseAction");
      getRootPane().getActionMap().put("CloseAction", closeAction);


      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            _txtFolderName.requestFocus();
         }
      });

      setSize(400, 150);

   }

   private void onCancel()
   {
      close();
   }

   private void close()
   {
      setVisible(false);
      dispose();
   }

   private void onOK()
   {
      if(null == _txtFolderName.getText() || 0 == _txtFolderName.getText().trim().length())
      {
         JOptionPane.showConfirmDialog(this,s_stringMgr.getString("EditAliasFolderDlg.FolderNameEmpty"));
         return;
      }

      _folderName = _txtFolderName.getText();

      close();
   }


   private void createUI(String text)
   {
      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      getContentPane().add(new JLabel(text), gbc);

      gbc = new GridBagConstraints(0,1,1,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
      getContentPane().add(_txtFolderName, gbc);

      gbc = new GridBagConstraints(0,2,1,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      getContentPane().add(createButtonPanel(), gbc);
   }

   private JPanel createButtonPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      ret.add(_btnOK, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      ret.add(_btnCancel, gbc);

      return ret;
   }

   public String getFolderName()
   {
      return _folderName;
   }
}
