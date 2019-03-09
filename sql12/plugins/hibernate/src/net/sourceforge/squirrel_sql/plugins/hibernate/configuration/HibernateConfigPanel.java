package net.sourceforge.squirrel_sql.plugins.hibernate.configuration;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.hibernate.HibernatePluginResources;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class HibernateConfigPanel extends JPanel
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(HibernateConfigPanel.class);

   JComboBox cboConfigs;
   JButton btnNewConfig;
   JButton btnRemoveConfig;

   JList lstClassPath;
   JButton btnClassPathAdd;
   JButton btnClassPathDirAdd;
   JButton btnClassPathRemove;
   JButton btnClassPathMoveUp;
   JButton btnClassPathMoveDown;
   JTextField txtConfigName;
   JButton btnApplyConfigChanges;

   JpaConnectionConfigPanel _jpaConnectionConfigPanel = new JpaConnectionConfigPanel();

   JRadioButton radCreateProcess;
   JButton btnProcessDetails;
   JRadioButton radInVM;


   public HibernateConfigPanel(HibernatePluginResources resources)
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
      add(createConfigDefPanel(resources), gbc);
   }

   private JPanel createConfigDefPanel(HibernatePluginResources resources)
   {
      JPanel ret = new JPanel(new GridBagLayout());
      // i18n[HibernateConfigPanel.ConfiguirationDef=Configuration definition]
      ret.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("HibernatePanel.ConfiguirationDef")));

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST,GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5),0,0);
      ret.add(createConfigNamePanel(), gbc);


      gbc = new GridBagConstraints(0,1,1,1,1,1, GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH, new Insets(0,5,5,5),0,0);
      ret.add(createClasspathPanel(resources), gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST,GridBagConstraints.HORIZONTAL, new Insets(10,0,5,0),0,0);
      ret.add(_jpaConnectionConfigPanel, gbc);

      gbc = new GridBagConstraints(0,3,1,1,0,0,GridBagConstraints.NORTHWEST,GridBagConstraints.HORIZONTAL, new Insets(10,5,5,5),0,0);
      ret.add(createProcessPanel(), gbc);

      gbc = new GridBagConstraints(0,4,1,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.NONE, new Insets(10,5,5,5),0,0);
      // i18n[HibernateConfigPanel.applyConfigChanges=Apply changes to this configuration]
      btnApplyConfigChanges = new JButton(s_stringMgr.getString("HibernatePanel.applyConfigChanges"));
      ret.add(btnApplyConfigChanges, gbc);

      return ret;
   }

   private JPanel createProcessPanel()
   {
      JPanel ret = new JPanel();

      ret.setBorder(BorderFactory.createEtchedBorder());
      ret.setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.WEST,GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      // i18n[HibernatePanel.howToAccess=To access Hibernate SQuirreL should:]
      ret.add(new JLabel(s_stringMgr.getString("HibernatePanel.howToAccess")), gbc);


      gbc = new GridBagConstraints(0,1,1,1,0,0, GridBagConstraints.WEST,GridBagConstraints.NONE, new Insets(5,20,0,5),0,0);
      // i18n[HibernateConfigPanel.inVM=Run Hibernate in SQUirreL's Java VM]
      radInVM = new JRadioButton(s_stringMgr.getString("HibernateConfigPanel.inVM"));
      ret.add(radInVM, gbc);

      gbc = new GridBagConstraints(1,1,1,1,0,0, GridBagConstraints.WEST,GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      // i18n[HibernateConfigPanel.createProcess=Launch a process that runs Hibernate (default)]
      radCreateProcess = new JRadioButton(s_stringMgr.getString("HibernateConfigPanel.createProcess"));
      ret.add(radCreateProcess, gbc);


      gbc = new GridBagConstraints(2,1,1,1,0,0, GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5),0,0);
      // i18n[HibernateConfigPanel.createProcessDetails=Details ...]
      btnProcessDetails = new JButton(s_stringMgr.getString("HibernateConfigPanel.createProcessDetails"));
      ret.add(btnProcessDetails, gbc);


      gbc = new GridBagConstraints(3,1,1,1,1,1, GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5),0,0);
      ret.add(new JPanel(), gbc);




      ButtonGroup btnGr = new ButtonGroup();
      btnGr.add(radCreateProcess);
      btnGr.add(radInVM);
      radCreateProcess.setSelected(true);
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


   private JPanel createClasspathPanel(final HibernatePluginResources resources)
   {
      // i18n[HibernateConfigPanel.newFactoryClasspathBorder=Additional classpath entries to create a SessionFactoryImpl]
      TitledBorder brd = BorderFactory.createTitledBorder(s_stringMgr.getString("HibernatePanel.classpath.of.hibernat.libs.and.entities"));
      JPanel ret = new JPanel(new GridBagLayout());
      ret.setBorder(brd);


      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,1,1, GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH, new Insets(0,5,5,5),0,0);

      lstClassPath = new JList(new ClassPathItemListModel());
      lstClassPath.setCellRenderer(new ClassPathListCellRenderer(resources));



      ret.add(new JScrollPane(lstClassPath), gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0, GridBagConstraints.SOUTHEAST,GridBagConstraints.NONE, new Insets(0,5,5,5),0,0);
      ret.add(createButtonClasspathPanel(resources), gbc);

      return ret;

   }

   private JPanel createButtonClasspathPanel(HibernatePluginResources resources)
   {
      JPanel ret = new JPanel(new GridBagLayout());


      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      // i18n[HibernateConfigPanel.classPathAdd=Add classpath entry]
      btnClassPathAdd = new JButton(s_stringMgr.getString("HibernatePanel.classPathAdd"));
      btnClassPathAdd.setIcon(resources.getIcon(HibernatePluginResources.IKeys.JAR_IMAGE));
      ret.add(btnClassPathAdd, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      btnClassPathDirAdd = new JButton(s_stringMgr.getString("HibernatePanel.classPathDirAdd"));
      btnClassPathDirAdd.setIcon(resources.getIcon(HibernatePluginResources.IKeys.JAR_DIRECTORY_IMAGE));
      ret.add(btnClassPathDirAdd, gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      // i18n[HibernateConfigPanel.classPathRemove=Remove selected entries]
      btnClassPathRemove = new JButton(s_stringMgr.getString("HibernatePanel.classPathRemove"));
      ret.add(btnClassPathRemove, gbc);

      gbc = new GridBagConstraints(3,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      btnClassPathMoveUp = new JButton(s_stringMgr.getString("HibernatePanel.moveUp"));
      ret.add(btnClassPathMoveUp, gbc);

      gbc = new GridBagConstraints(4,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      btnClassPathMoveDown = new JButton(s_stringMgr.getString("HibernatePanel.moveDown"));
      ret.add(btnClassPathMoveDown, gbc);

      return ret;
   }


}
