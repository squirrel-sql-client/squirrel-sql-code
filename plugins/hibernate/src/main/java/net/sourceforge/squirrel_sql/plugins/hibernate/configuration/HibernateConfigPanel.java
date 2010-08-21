package net.sourceforge.squirrel_sql.plugins.hibernate.configuration;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class HibernateConfigPanel extends JPanel
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(HibernateConfigPanel.class);

   JComboBox cboConfigs;
   JButton btnNewConfig;
   JButton btnRemoveConfig;
   JTextField txtFactoryProvider;
   JButton btnEditFactoryProviderInfo;
   JList lstClassPath;
   JButton btnClassPathAdd;
   JButton btnClassPathRemove;
   JButton btnClassPathMoveUp;
   JButton btnClassPathMoveDown;
   JTextField txtConfigName;
   JButton btnApplyConfigChanges;
   JRadioButton radConfiguration;
   JRadioButton radUserDefProvider;
   JRadioButton radJPA;
   JTextField txtPersistenceUnitName;


   public HibernateConfigPanel()
   {
      setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.WEST,GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      // i18n[HibernateConfigPanel.config=Configuration]
      JLabel lblConfig = new JLabel(s_stringMgr.getString("HibernatePanel.config"));
      add(lblConfig, gbc);

      gbc = new GridBagConstraints(1,0,1,1,1,0, GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5),0,0);
      cboConfigs = new JComboBox();
      add(cboConfigs, gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0, GridBagConstraints.WEST,GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      // i18n[HibernateConfigPanel.newConfig=New]
      btnNewConfig = new JButton(s_stringMgr.getString("HibernatePanel.newConfig"));
      add(btnNewConfig, gbc);

      gbc = new GridBagConstraints(3,0,1,1,0,0, GridBagConstraints.WEST,GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      // i18n[HibernateConfigPanel.removeConfig=Remove]
      btnRemoveConfig = new JButton(s_stringMgr.getString("HibernatePanel.removeConfig"));
      add(btnRemoveConfig, gbc);

      gbc = new GridBagConstraints(0,1,4,1,1,1, GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH, new Insets(15,10,10,10),0,0);
      add(createConfigDefPanel(), gbc);
   }

   private JPanel createConfigDefPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());
      // i18n[HibernateConfigPanel.ConfiguirationDef=Configuration definition]
      ret.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("HibernatePanel.ConfiguirationDef")));

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST,GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5),0,0);
      ret.add(createConfigNamePanel(), gbc);


      gbc = new GridBagConstraints(0,1,1,1,1,1, GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH, new Insets(0,5,5,5),0,0);
      ret.add(createClasspathPanel(), gbc);


      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST,GridBagConstraints.HORIZONTAL, new Insets(10,5,5,5),0,0);
      ret.add(createHowToCreateSessionFactoryPanel(), gbc);

      gbc = new GridBagConstraints(0,3,1,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.NONE, new Insets(10,5,5,5),0,0);
      // i18n[HibernateConfigPanel.applyConfigChanges=Apply changes to this configuration]
      btnApplyConfigChanges = new JButton(s_stringMgr.getString("HibernatePanel.applyConfigChanges"));
      ret.add(btnApplyConfigChanges, gbc);

      return ret;
   }

   private JPanel createConfigNamePanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.WEST,GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      // i18n[HibernateConfigPanel.configName=Configuration name]
      ret.add(new JLabel(s_stringMgr.getString("HibernatePanel.configName")), gbc);

      gbc = new GridBagConstraints(1,0,1,1,1,0, GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5),0,0);
      txtConfigName = new JTextField();
      ret.add(txtConfigName, gbc);

      return ret;
   }


   private JPanel createClasspathPanel()
   {
      // i18n[HibernateConfigPanel.newFactoryClasspathBorder=Additional classpath entries to create a SessionFactoryImpl]
      TitledBorder brd = BorderFactory.createTitledBorder(s_stringMgr.getString("HibernatePanel.newFactoryClasspathBorder"));
      JPanel ret = new JPanel(new GridBagLayout());
      ret.setBorder(brd);


      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,1,1, GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH, new Insets(0,5,5,5),0,0);
      lstClassPath = new JList();
      ret.add(new JScrollPane(lstClassPath), gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0, GridBagConstraints.SOUTHEAST,GridBagConstraints.NONE, new Insets(0,5,5,5),0,0);
      ret.add(createButtonClasspathPanel(), gbc);

      return ret;

   }

   private JPanel createButtonClasspathPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());


      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      // i18n[HibernateConfigPanel.classPathAdd=Add classpath entry]
      btnClassPathAdd = new JButton(s_stringMgr.getString("HibernatePanel.classPathAdd"));
      ret.add(btnClassPathAdd, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      // i18n[HibernateConfigPanel.classPathRemove=Remove selected entries]
      btnClassPathRemove = new JButton(s_stringMgr.getString("HibernatePanel.classPathRemove"));
      ret.add(btnClassPathRemove, gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      btnClassPathMoveUp = new JButton(s_stringMgr.getString("HibernatePanel.moveUp"));
      ret.add(btnClassPathMoveUp, gbc);

      gbc = new GridBagConstraints(3,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      btnClassPathMoveDown = new JButton(s_stringMgr.getString("HibernatePanel.moveDown"));
      ret.add(btnClassPathMoveDown, gbc);

      return ret;
   }

   private JPanel createHowToCreateSessionFactoryPanel()
   {
      JPanel ret = new JPanel();

      ret.setBorder(BorderFactory.createEtchedBorder());
      ret.setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.WEST,GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      // i18n[HibernateConfigPanel.toObtainSessionFact=To obtain a Hibernate SessionFactoryImpl instance SQuirreL should:]
      ret.add(new JLabel(s_stringMgr.getString("HibernatePanel.toObtainSessionFact")), gbc);


      gbc = new GridBagConstraints(0,1,1,1,0,0, GridBagConstraints.WEST,GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      // i18n[HibernateConfigPanel.toObtainSessionFactConfiguration=Call "new org.hibernate.cfg.Configuration().configure().buildSessionFactory();"]
      radConfiguration = new JRadioButton(s_stringMgr.getString("HibernatePanel.toObtainSessionFactConfiguration"));
      ret.add(radConfiguration, gbc);


      gbc = new GridBagConstraints(0,2,1,1,0,0, GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5),0,0);
      ret.add(createJPAPanel(), gbc);


      gbc = new GridBagConstraints(0,3,1,1,0,0, GridBagConstraints.WEST,GridBagConstraints.NONE, new Insets(5,5,0,5),0,0);
      // i18n[HibernateConfigPanel.toObtainSessionFactFactoryProvider=Invoke the user defined provider method below:]
      radUserDefProvider = new JRadioButton(s_stringMgr.getString("HibernatePanel.toObtainSessionFactFactoryProvider"));
      ret.add(radUserDefProvider, gbc);


      ButtonGroup btnGr = new ButtonGroup();
      btnGr.add(radConfiguration);
      btnGr.add(radUserDefProvider);
      btnGr.add(radJPA);
      radConfiguration.setSelected(true);

      gbc = new GridBagConstraints(0,4,1,1,1,0, GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL, new Insets(0,5,5,5),0,0);
      ret.add(createUserDefinedSessionFactoryPanel(), gbc);

      return ret;

   }

   private JPanel createJPAPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,0,5,5),0,0);
      // i18n[HibernateConfigPanel.toObtainSessionFactJPA=Call "javax.persistence.Persistence.createEntityManagerFactory("<persitence-unit name>");"]
      radJPA = new JRadioButton(s_stringMgr.getString("HibernatePanel.toObtainSessionFactJPA"));
      ret.add(radJPA, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      // i18n[HibernateConfigPanel.toObtainSessionFactPersUnit=persitence-unit name:]
      ret.add(new JLabel(s_stringMgr.getString("HibernatePanel.toObtainSessionFactPersUnit")), gbc);

      gbc = new GridBagConstraints(2,0,1,1,1,0,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,0,5,5),0,0);
      txtPersistenceUnitName = new JTextField();
      ret.add(txtPersistenceUnitName, gbc);

      return ret;
   }

   private JPanel createUserDefinedSessionFactoryPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;
      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.WEST,GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      // i18n[HibernateConfigPanel.FactoryProvider=SessionFactoryImpl provider]
      JLabel lblConfig = new JLabel(s_stringMgr.getString("HibernatePanel.FactoryProvider"));
      ret.add(lblConfig, gbc);

      gbc = new GridBagConstraints(1,0,1,1,1,0, GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5),0,0);
      txtFactoryProvider = new JTextField();
      txtFactoryProvider.setEditable(false);
      txtFactoryProvider.setBackground(Color.lightGray);

      ret.add(txtFactoryProvider, gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0, GridBagConstraints.WEST,GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      // i18n[HibernateConfigPanel.editFactoryProvider=Edit]
      btnEditFactoryProviderInfo = new JButton(s_stringMgr.getString("HibernatePanel.editFactoryProvider"));
      ret.add(btnEditFactoryProviderInfo, gbc);

      return ret;
   }
}
