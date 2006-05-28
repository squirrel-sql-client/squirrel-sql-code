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

   JCheckBox chkLoadSysSchema;


   public OraclePrefsPanel()
   {
      setLayout(new GridBagLayout());

      GridBagConstraints gbc;
      gbc = new GridBagConstraints(0,0,1,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);

      // i18n[OraclePrefsPanel.SYSSchemaDesc=Note: Not loading the SYS Schema may significantly accelerate Session startup.]
      String desc = s_stringMgr.getString("OraclePrefsPanel.SYSSchemaDesc");
      add(new JLabel(desc), gbc);


      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5),0,0);
      // i18n[OraclePrefsPanel.SYSSchema=Load SYS Schema]
      chkLoadSysSchema = new JCheckBox(s_stringMgr.getString("OraclePrefsPanel.SYSSchema"));
      add(chkLoadSysSchema, gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,0,0,0),0,0);
      add(new JPanel(), gbc);


   }
}
