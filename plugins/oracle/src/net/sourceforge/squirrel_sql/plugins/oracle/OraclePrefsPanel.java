package net.sourceforge.squirrel_sql.plugins.oracle;

import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;

public class OraclePrefsPanel extends JPanel
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(OraclePrefsPanel.class);

   JRadioButton radLoadAccessibleSchemasExceptSYS;
   JRadioButton radLoadAccessibleSchemasAndSYS;
   JRadioButton radLoadAllSchemas;


   public OraclePrefsPanel()
   {
      setLayout(new GridBagLayout());

      GridBagConstraints gbc;
      gbc = new GridBagConstraints(0,0,1,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);

      // i18n[OraclePrefsPanel.SYSSchemaDesc=Note: Loading the SYS Schema or even all Schemas may significantly slow down Session startup.]
      String desc = s_stringMgr.getString("OraclePrefsPanel.SYSSchemaDesc");
      add(new JLabel(desc), gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5),0,0);
      // i18n[OraclePrefsPanel.AccessibleButSys=Load accessible Schemas excluding SYS]
      radLoadAccessibleSchemasExceptSYS = new JRadioButton(s_stringMgr.getString("OraclePrefsPanel.AccessibleButSys"));
      add(radLoadAccessibleSchemasExceptSYS, gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5),0,0);
      // i18n[OraclePrefsPanel.AccessibleAndSys=Load accessible Schemas and SYS]
      radLoadAccessibleSchemasAndSYS = new JRadioButton(s_stringMgr.getString("OraclePrefsPanel.AccessibleAndSys"));
      add(radLoadAccessibleSchemasAndSYS, gbc);

      gbc = new GridBagConstraints(0,3,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5),0,0);
      // i18n[OraclePrefsPanel.All=Load all Schemas]
      radLoadAllSchemas= new JRadioButton(s_stringMgr.getString("OraclePrefsPanel.All"));
      add(radLoadAllSchemas, gbc);

      gbc = new GridBagConstraints(0,4,1,1,0,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,0,0,0),0,0);
      add(new JPanel(), gbc);

      ButtonGroup bg = new ButtonGroup();
      bg.add(radLoadAccessibleSchemasExceptSYS);
      bg.add(radLoadAccessibleSchemasAndSYS);
      bg.add(radLoadAllSchemas);

   }
}
