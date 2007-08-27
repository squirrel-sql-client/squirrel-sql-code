package net.sourceforge.squirrel_sql.plugins.hibernate.mapping;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.prefs.Preferences;

public class MappedObjectPanel extends JPanel
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(MappedObjectPanel.class);


   JTree objectTree;
   JCheckBox chkShowQualified;

   private JSplitPane _split;
   private static final String PERF_KEY_OBJ_TAB_DIV_LOC = "Squirrel.hibernateplugin.objTabDivLoc";

   public MappedObjectPanel(JComponent detailComp)
   {
      super(new BorderLayout());

      add(createTopPanel(), BorderLayout.NORTH);


      objectTree = new JTree();

      _split = new JSplitPane();

      _split.setLeftComponent(new JScrollPane(objectTree));
      _split.setRightComponent(detailComp);

      add(_split, BorderLayout.CENTER);

      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            _split.setDividerLocation(Preferences.userRoot().getDouble(PERF_KEY_OBJ_TAB_DIV_LOC, 0.5));
         }
      });

   }

   private JPanel createTopPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      // i18n[MappedObjectPanel.QualifiedNames=Show qualified names]
      chkShowQualified = new JCheckBox(s_stringMgr.getString("MappedObjectPanel.QualifiedNames"));

      ret.add(chkShowQualified, gbc);

      // dist
      gbc = new GridBagConstraints(1,0,1,1,1,1,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
      ret.add(new JPanel(), gbc);

      return ret;
   }

   public void closing()
   {
      Preferences.userRoot().putDouble(PERF_KEY_OBJ_TAB_DIV_LOC, ((double) _split.getDividerLocation())/ ((double) _split.getWidth()) );
   }
}
