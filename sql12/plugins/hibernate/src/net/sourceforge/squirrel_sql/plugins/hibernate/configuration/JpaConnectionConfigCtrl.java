package net.sourceforge.squirrel_sql.plugins.hibernate.configuration;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.plugins.hibernate.HibernatePlugin;
import net.sourceforge.squirrel_sql.plugins.hibernate.server.HibernateConfiguration;

import javax.swing.JOptionPane;
import java.awt.Component;

public class JpaConnectionConfigCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(JpaConnectionConfigCtrl.class);


   private JpaConnectionConfigPanel _panel;
   private HibernatePlugin _plugin;

   public JpaConnectionConfigCtrl(JpaConnectionConfigPanel jpaConnectionConfigPanel, HibernatePlugin plugin)
   {
      _panel = jpaConnectionConfigPanel;
      _plugin = plugin;
   }



   public void init(HibernateConfiguration cfg)
   {
      if (null == cfg)
      {
         return;
      }
      _panel.txtPersistenceUnitName.setText(cfg.getPersistenceUnitName());

   }
   
   public boolean checkValid(boolean silent)
   {
      String persistenceUnitName = _panel.txtPersistenceUnitName.getText();


      if (StringUtilities.isEmpty(persistenceUnitName, true))
      {
         if (false == silent)
         {
            // i18n[HibernateConfigController.noPersistenceUnitName=Missing Persitence-Unit name .\nChanges
            // cannot be applied.]
            JOptionPane.showMessageDialog(_plugin.getApplication().getMainFrame(), s_stringMgr.getString("HibernateController.noPersistenceUnitName"));
         }
         return false;
      }

      return true;

   }


   public void saveConfiguration(HibernateConfiguration cfg)
   {
      String persistenceUnitName = _panel.txtPersistenceUnitName.getText();
      cfg.setPersistenceUnitName(persistenceUnitName);
   }

   public boolean isMyPanel(Component selectedComponent)
   {
      return selectedComponent == _panel;
   }
}
