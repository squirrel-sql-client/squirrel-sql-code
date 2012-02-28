package net.sourceforge.squirrel_sql.plugins.dbcopy.actions;

import com.jidesoft.swing.MultilineLabel;
import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EditPasteTableNameDlg extends JDialog
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(EditPasteTableNameDlg.class);



   private JTextField _txtFolderName = new JTextField();

   private JButton _btnOK = new JButton(s_stringMgr.getString("EditPasteTableNameDlg.OK"));
   private JButton _btnCancel = new JButton(s_stringMgr.getString("EditPasteTableNameDlg.Cancel"));

   private String _tableName;


   public EditPasteTableNameDlg(MainFrame mainFrame)
   {
      super(mainFrame, s_stringMgr.getString("EditPasteTableNameDlg.title"), true);
      createUI();

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


      GUIUtils.enableCloseByEscape(this);


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
         JOptionPane.showMessageDialog(this,s_stringMgr.getString("EditPasteTableNameDlg.TableNameEmpty"));
         return;
      }

      _tableName = _txtFolderName.getText();

      close();
   }


   private void createUI()
   {
      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
      getContentPane().add(new MultilineLabel(s_stringMgr.getString("EditPasteTableNameDlg.text")), gbc);

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

   public String getTableName()
   {
      return _tableName;
   }
}
