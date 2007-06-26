package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLResultExecuterPanel;
import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;

import javax.swing.*;
import java.awt.*;

public class HibernateSQLPanel extends JPanel
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(HibernateSQLPanel.class);

   JCheckBox _chkAppendSql;
   JButton _btnFormatSql;
   JCheckBox _chkAlwaysFormatSql;
   JCheckBox _chkAlwaysExecuteSql;

   JTabbedPane _tabResult_code;


   public HibernateSQLPanel(JComponent textComp, SQLResultExecuterPanel resultExecuterPanel)
   {

      setLayout(new BorderLayout());
      _tabResult_code = new JTabbedPane();//UIFactory.getInstance().createTabbedPane();

      // i18n[HibernateSQLPanel.code=SQL code]
      _tabResult_code.addTab(s_stringMgr.getString("HibernateSQLPanel.code"), textComp);

      // i18n[HibernateSQLPanel.result=SQL result]
      _tabResult_code.addTab(s_stringMgr.getString("HibernateSQLPanel.result"), resultExecuterPanel);


      add(_tabResult_code, BorderLayout.CENTER);

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
      // i18n[HibernateSQLPanel.Execute=Execute SQL]
      _chkAlwaysExecuteSql = new JCheckBox(s_stringMgr.getString("HibernateSQLPanel.Execute"));
      ret.add(_chkAlwaysExecuteSql, gbc);


      gbc = new GridBagConstraints(5,0,1,1,1,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      ret.add(new JPanel(), gbc);


      return ret;

   }
}
