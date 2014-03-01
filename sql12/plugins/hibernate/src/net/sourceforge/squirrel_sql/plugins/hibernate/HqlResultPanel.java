package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.fw.gui.IntegerField;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;

public class HqlResultPanel extends JPanel
{
   private static final StringManager s_stringMgr =  StringManagerFactory.getStringManager(HqlResultPanel.class);


   JCheckBox chkLimitObjectCount;
   IntegerField nbrLimitRows;
   JComboBox cboUseConnectionOf;


   public HqlResultPanel(JTabbedPane hqlResultTabbedPane, HibernatePluginResources resource)
   {

      setLayout(new BorderLayout());
      add(hqlResultTabbedPane);

      add(hqlResultTabbedPane, BorderLayout.CENTER);

      add(createBottomPanel(resource), BorderLayout.SOUTH);
   }

   private JPanel createBottomPanel(HibernatePluginResources resource)
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,0),0,0);
      ret.add(new JLabel(s_stringMgr.getString("HibernateSQLPanel.UseConnectionOf")), gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      cboUseConnectionOf = new JComboBox(UseConnectionOf.values());
      ret.add(cboUseConnectionOf, gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,25,5,0),0,0);
      chkLimitObjectCount = new JCheckBox(s_stringMgr.getString("HibernateSQLPanel.LimitRows_new"));
      ret.add(chkLimitObjectCount, gbc);

      gbc = new GridBagConstraints(3,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,3,5,5),0,0);
      nbrLimitRows = new IntegerField();
      ret.add(nbrLimitRows, gbc);
      nbrLimitRows.setColumns(8);


      gbc = new GridBagConstraints(4,0,1,1,1,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      ret.add(new JPanel(), gbc);

      return ret;

   }

   public static enum UseConnectionOf
   {
      OF_SESSION(s_stringMgr.getString("HibernateSQLPanel.UseConnectionOf.Session")),
      OF_HIBBERNAT_CONFIG(s_stringMgr.getString("HibernateSQLPanel.UseConnectionOf.HibernateConfig")),;
      private String _text;

      UseConnectionOf(String text)
      {
         _text = text;
      }


      @Override
      public String toString()
      {
         return _text;
      }

      public static UseConnectionOf getByOrdinal(int ordinal)
      {
         for (UseConnectionOf val : values())
         {
            if(val.ordinal() == ordinal)
            {
               return val;
            }
         }

         throw new IllegalArgumentException("Invalid ordinla " + ordinal);
      }
   }
}
