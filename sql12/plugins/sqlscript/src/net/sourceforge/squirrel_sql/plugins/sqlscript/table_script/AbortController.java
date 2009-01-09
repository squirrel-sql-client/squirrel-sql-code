package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class AbortController
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(AbortController.class);

   private JDialog _dlg;
   private boolean _stop;

   public AbortController(IApplication app)
   {
      _dlg = new JDialog(app.getMainFrame());
      _dlg.getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      _dlg.getContentPane().add(new JLabel(s_stringMgr.getString("AbortController.abortText")), gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      JButton btnAbort = new JButton(s_stringMgr.getString("AbortController.abort"));
      _dlg.getContentPane().add(btnAbort, gbc);

      _dlg.setResizable(false);
      _dlg.pack();
      _dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

      btnAbort.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            _stop = true;
            setVisible(false);
         }
      });
   }

   public boolean isStop()
   {
      return _stop;
   }

   public boolean isVisble()
   {
      return _dlg.isVisible();
   }

   public void setVisible(final boolean b)
   {
      GUIUtils.processOnSwingEventThread
         (
            new Runnable()
            {
               public void run()
               {
                  GUIUtils.centerWithinParent(_dlg);
                  _dlg.setVisible(b);
               }
            });
   }
}
