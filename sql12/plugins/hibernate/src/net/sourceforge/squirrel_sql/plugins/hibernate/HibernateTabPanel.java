package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.prefs.Preferences;

public class HibernateTabPanel extends JPanel
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(HibernateTabPanel.class);

   private static final String PERF_KEY_HQL_TAB_DIVIDER_LOCATION = "Squirrel.hibernateplugin.hqlTabDivLoc";
   private static final String PERF_KEY_LAST_SELECTED_TAB = "Squirrel.hibernateplugin.lastSelectedTab";


   JComboBox cboConfigurations;
   JToggleButton btnConnected;
   JButton btnOpenConfigs;

   private JSplitPane _splitHqlSql;
   private JPanel _toolbar;
   private int _curXOfToolbar;
   private JTabbedPane _tabObjectsHql;
   private HibernatePluginResources _resource;


   public HibernateTabPanel(JComponent mappedObjectComp, JComponent hqlTextComp, JComponent sqlTextComp, HibernatePluginResources resource)
   {
      _resource = resource;
      setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0),0,0 );
      _toolbar = createToolbar();
      add(_toolbar, gbc);

      _splitHqlSql = new JSplitPane(JSplitPane.VERTICAL_SPLIT, hqlTextComp, sqlTextComp);

      _tabObjectsHql = new JTabbedPane();

      // i18n[HibernateTabPanel.mappedObjects=Mapped objects]
      _tabObjectsHql.add(s_stringMgr.getString("HQLTabPanel.mappedObjects"), mappedObjectComp);

      // i18n[HibernateTabPanel.hql=HQL]
      _tabObjectsHql.add(s_stringMgr.getString("HQLTabPanel.hql"), _splitHqlSql);




      gbc = new GridBagConstraints(0,1,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,0,0,0),0,0 );
      add(_tabObjectsHql, gbc);


      _tabObjectsHql.setSelectedIndex(Preferences.userRoot().getInt(PERF_KEY_LAST_SELECTED_TAB, 0));

      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            _splitHqlSql.setDividerLocation(Preferences.userRoot().getDouble(PERF_KEY_HQL_TAB_DIVIDER_LOCATION, 0.5));
         }
      });

   }


   public void closing()
   {
      Preferences.userRoot().putDouble(PERF_KEY_HQL_TAB_DIVIDER_LOCATION, ((double) _splitHqlSql.getDividerLocation())/ ((double) _splitHqlSql.getHeight()) );
      Preferences.userRoot().putInt(PERF_KEY_LAST_SELECTED_TAB, _tabObjectsHql.getSelectedIndex());
   }


   private JPanel createToolbar()
   {
      JPanel ret = new JPanel();

      ret.setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      _curXOfToolbar = 0;

      // i18n[HibernateTabPanel.configuration=Configuration]
      JLabel lblCfg = new JLabel(s_stringMgr.getString("HQLTabPanel.configuration"));
      gbc = new GridBagConstraints(_curXOfToolbar++,0,1,1, 0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      ret.add(lblCfg, gbc);

      cboConfigurations = new JComboBox();
      gbc = new GridBagConstraints(_curXOfToolbar++,0,1,1, 1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,0,5,5), 0,0);
      ret.add(cboConfigurations, gbc);                      

      gbc = new GridBagConstraints(_curXOfToolbar++,0,1,1, 0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      btnConnected = new JToggleButton();
      //i18n[hibernate.HibernateTabPanel.connect=Connect/disconnect configuration selected configuration]
      btnConnected.setToolTipText(s_stringMgr.getString("hibernate.HQLTabPanel.connect"));
      ret.add(btnConnected, gbc);

      gbc = new GridBagConstraints(_curXOfToolbar++,0,1,1, 0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      btnOpenConfigs = new JButton(_resource.getIcon(HibernatePluginResources.IKeys.HIBERNATE_IMAGE));
      //i18n[hibernate.HibernateTabPanel.openConfigs=Open Hibernate configurations]
      btnOpenConfigs.setToolTipText(s_stringMgr.getString("hibernate.HibernateTabPanel.openConfigs"));
      ret.add(btnOpenConfigs, gbc);


      return ret;
   }


   public void addToToolbar(JComponent comp)
   {
      GridBagConstraints  gbc = new GridBagConstraints(_curXOfToolbar++,0,1,1, 0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      _toolbar.add(comp, gbc);

      _toolbar.validate();
   }
}
