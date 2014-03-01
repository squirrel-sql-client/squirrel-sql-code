package net.sourceforge.squirrel_sql.plugins.dbcopy.actions;

import com.jidesoft.swing.MultilineLabel;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class EditPasteTableNameDlg extends JDialog
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(EditPasteTableNameDlg.class);



   private JTextField _txtTableName = new JTextField();

   private JButton _btnOK = new JButton(s_stringMgr.getString("EditPasteTableNameDlg.OK"));
   private JButton _btnCancel = new JButton(s_stringMgr.getString("EditPasteTableNameDlg.Cancel"));

   private String _tableName;
   private String _destTableName;


   public EditPasteTableNameDlg(Frame owner, String destTableName)
   {
      super(owner, s_stringMgr.getString("EditPasteTableNameDlg.title"), true);
      _destTableName = destTableName;
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
            _txtTableName.requestFocus();
         }
      });

      setSize(400, 200);

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
      if(null == _txtTableName.getText() || 0 == _txtTableName.getText().trim().length())
      {
         JOptionPane.showMessageDialog(this,s_stringMgr.getString("EditPasteTableNameDlg.TableNameEmpty"));
         return;
      }

      _tableName = _txtTableName.getText();

      close();
   }


   private void createUI()
   {
      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      int gridy = 0;

      gbc = new GridBagConstraints(0, gridy,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
      getContentPane().add(new MultilineLabel(s_stringMgr.getString("EditPasteTableNameDlg.text")), gbc);

      gbc = new GridBagConstraints(0,++gridy,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
      getContentPane().add(_txtTableName, gbc);

      if (null != _destTableName)
      {
         gbc = new GridBagConstraints(0,++gridy,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
         JLabel lblDestNameLink = new JLabel(s_stringMgr.getString("EditPasteTableNameDlg.htmlSetNameTo", _destTableName));
         lblDestNameLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

         lblDestNameLink.addMouseListener(new MouseAdapter()
         {
            @Override
            public void mouseClicked(MouseEvent e)
            {
               _txtTableName.setText(_destTableName);
            }
         });

         getContentPane().add(lblDestNameLink, gbc);
      }

      gbc = new GridBagConstraints(0,++gridy,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10,5,5,5), 0,0);
      getContentPane().add(new JPanel(), gbc);

      gbc = new GridBagConstraints(0,++gridy,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10,5,5,5), 0,0);
      getContentPane().add(createButtonPanel(), gbc);
   }

   private JPanel createButtonPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,0,5,5), 0,0);
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
