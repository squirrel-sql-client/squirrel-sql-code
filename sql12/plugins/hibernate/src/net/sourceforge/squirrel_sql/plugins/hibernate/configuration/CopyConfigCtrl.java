package net.sourceforge.squirrel_sql.plugins.hibernate.configuration;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.plugins.hibernate.server.HibernateConfiguration;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import java.awt.Frame;

public class CopyConfigCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CopyConfigCtrl.class);

   private final CopyConfigDlg _dlg;
   private JComboBox _cboConfigs;
   private String _newName;

   public CopyConfigCtrl(Frame owningFrame, HibernateConfiguration selConfig, JComboBox cboConfigs)
   {
      _dlg = new CopyConfigDlg(owningFrame);
      _cboConfigs = cboConfigs;

      _dlg.txtNewName.setText(suggestCopyName(selConfig));

      _dlg.btnOk.addActionListener(e -> onOk());
      _dlg.btnCancel.addActionListener(e -> onCancel());

      GUIUtils.enableCloseByEscape(_dlg);
      GUIUtils.initLocation(_dlg, 300, 150);

      _dlg.setVisible(true);
   }

   private void onCancel()
   {
      close();
   }

   private void close()
   {
      _dlg.setVisible(false);
      _dlg.dispose();
   }

   private void onOk()
   {
      if(StringUtilities.isEmpty(_dlg.txtNewName.getText(), true))
      {
         JOptionPane.showMessageDialog(GUIUtils.getOwningFrame(_dlg), s_stringMgr.getString("CopyConfigCtrl.copy.config.name.must.not.be.empty"));
         return;
      }

      String newName = _dlg.txtNewName.getText().trim();

      for (int i = 0; i < _cboConfigs.getItemCount(); i++)
      {
         HibernateConfiguration config = (HibernateConfiguration) _cboConfigs.getItemAt(i);

         if(newName.equals(config.getName()))
         {
            JOptionPane.showMessageDialog(GUIUtils.getOwningFrame(_dlg), s_stringMgr.getString("CopyConfigCtrl.copy.config.name.exists"));
            return;
         }
      }

      _newName = newName;
      close();
   }

   private String suggestCopyName(HibernateConfiguration selConfig)
   {
      for(int postFix=1; ; ++postFix)
      {
         String newName = selConfig.getName() + "_" + postFix;

         boolean exists = false;
         for (int i = 0; i < _cboConfigs.getItemCount(); i++)
         {
            HibernateConfiguration config = (HibernateConfiguration) _cboConfigs.getItemAt(i);

            if(newName.equals(config.getName()))
            {
               exists = true;
               break;
            }
         }

         if(false == exists)
         {
            return newName;
         }
      }
   }

   public String getCopiedConfigName()
   {
      return _newName;
   }
}

