package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLResultExecuterPanel;

import javax.swing.*;
import java.awt.*;

public class HibernateSQLPanel extends JPanel
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(HibernateSQLPanel.class);

   JCheckBox _chkAppendSql;
   JButton _btnFormatSql;
   JCheckBox _chkAlwaysFormatSql;
   JButton _btnExecuteSql;
   JCheckBox _chkAlwaysExecuteSql;

   private JComponent _mainComp;
   private JPanel _mainCompHolder;


   public HibernateSQLPanel(JComponent mainComp)
   {
      setLayout(new BorderLayout());
      _mainCompHolder = new JPanel(new GridLayout(1,1));
      add(_mainCompHolder, BorderLayout.CENTER);

      setMainComponent(mainComp);

      add(createBottomPanel(), BorderLayout.SOUTH);
   }

   private JPanel createBottomPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      // i18n[HibernateSQLPanel.sql=SQL:]
      ret.add(new JLabel(s_stringMgr.getString("HibernateSQLPanel.sql")), gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      // i18n[HibernateSQLPanel.appendSql=Append]
      _chkAppendSql = new JCheckBox(s_stringMgr.getString("HibernateSQLPanel.appendSql"));
      ret.add(_chkAppendSql, gbc);


      gbc = new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      // i18n[HibernateSQLPanel.format=Format]
      _btnFormatSql = new JButton(s_stringMgr.getString("HibernateSQLPanel.format"));
      ret.add(_btnFormatSql, gbc);


      gbc = new GridBagConstraints(3,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      // i18n[HibernateSQLPanel.alwaysFormat=Always format]
      _chkAlwaysFormatSql = new JCheckBox(s_stringMgr.getString("HibernateSQLPanel.alwaysFormat"));
      ret.add(_chkAlwaysFormatSql, gbc);


      gbc = new GridBagConstraints(4,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      // i18n[HibernateSQLPanel.executeSql=Execute]
      _btnExecuteSql = new JButton(s_stringMgr.getString("HibernateSQLPanel.executeSql"));
      ret.add(_btnExecuteSql, gbc);

      gbc = new GridBagConstraints(5,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      // i18n[HibernateSQLPanel.alwaysExecute=Always execute SQL]
      _chkAlwaysExecuteSql = new JCheckBox(s_stringMgr.getString("HibernateSQLPanel.alwaysExecute"));
      ret.add(_chkAlwaysExecuteSql, gbc);


      gbc = new GridBagConstraints(6,0,1,1,1,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      ret.add(new JPanel(), gbc);


      return ret;

   }

   public void setMainComponent(JComponent mainComp)
   {
      if(_mainComp != mainComp)
      {
         _mainComp = mainComp;
         _mainCompHolder.removeAll();
         _mainCompHolder.add(_mainComp);
         _mainCompHolder.invalidate();
      }
   }
}
