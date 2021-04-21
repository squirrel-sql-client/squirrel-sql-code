package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;

public class AbortController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AbortController.class);

   private JDialog _dlg;
   private volatile boolean _stop;

   public AbortController(Window owner)
   {
      _dlg = new JDialog(owner);
      _dlg.getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      _dlg.getContentPane().add(new JLabel(s_stringMgr.getString("AbortController.abortText")), gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      JButton btnAbort = new JButton(s_stringMgr.getString("AbortController.abort"));
      _dlg.getContentPane().add(btnAbort, gbc);

      _dlg.setResizable(false);
      _dlg.pack();
      _dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

      btnAbort.addActionListener(e -> onAbort());
   }

   private void onAbort()
   {
      _stop = true;
      close();
   }

   /**
    * @see net.sourceforge.squirrel_sql.plugins.sqlscript.table_script.IAbortController#isStop()
    */
   public boolean isStop()
   {
      return _stop;
   }

   /**
    * @see net.sourceforge.squirrel_sql.plugins.sqlscript.table_script.IAbortController#setVisible(boolean)
    */
   public void show()
   {
      GUIUtils.processOnSwingEventThread (() -> onShow());
   }

   private void onShow()
   {
      if(_dlg.isVisible())
      {
         return;
      }

      GUIUtils.centerWithinParent(_dlg);
      _dlg.setVisible(true);
   }

   public void close()
   {
      GUIUtils.processOnSwingEventThread (() -> onClose());
   }

   private void onClose()
   {
      _dlg.setVisible(false);
      _dlg.dispose();
   }
}
