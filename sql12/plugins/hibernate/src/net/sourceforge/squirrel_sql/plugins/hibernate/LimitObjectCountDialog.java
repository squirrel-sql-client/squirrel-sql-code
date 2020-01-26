package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class LimitObjectCountDialog extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(LimitObjectCountDialog.class);

   private JButton _btnCheck;
   private JButton _btnCheckAndRemember;
   private JButton _btnCancel;
   private boolean _check;
   private boolean _checkAndRemember;


   public LimitObjectCountDialog(MainFrame mainFrame)
   {
      super(mainFrame, s_stringMgr.getString("LimitObjectCountDialog.title"), true);

      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5),0,0);
      getContentPane().add(new MultipleLineLabel(s_stringMgr.getString("LimitObjectCountDialog.text")), gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5),0,0);
      getContentPane().add(createButtonPanel(), gbc);


      _btnCheck.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            _check = true;
            close();
         }
      });

      _btnCheckAndRemember.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            _check = true;
            _checkAndRemember = true;
            close();
         }
      });


      _btnCancel.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            close();
         }
      });


      setSize(350,200);

      GUIUtils.centerWithinParent(this);

      GUIUtils.enableCloseByEscape(this);

      setVisible(true);
   }

   private JPanel createButtonPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());
      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      _btnCheck = new JButton(s_stringMgr.getString("LimitObjectCountDialog.select"));
      ret.add(_btnCheck, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      _btnCheckAndRemember = new JButton(s_stringMgr.getString("LimitObjectCountDialog.selectAndRemember"));
      ret.add(_btnCheckAndRemember, gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      _btnCancel = new JButton(s_stringMgr.getString("LimitObjectCountDialog.cancel"));
      ret.add(_btnCancel, gbc);

//      gbc = new GridBagConstraints(3,0,1,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5),0,0);
//      ret.add(new JPanel(), gbc);

      return ret;

   }

   private void close()
   {
      setVisible(false);
      dispose();
   }

   public boolean check()
   {
      return _check;
   }

   public boolean checkAndRemember()
   {
      return _checkAndRemember;
   }
}
