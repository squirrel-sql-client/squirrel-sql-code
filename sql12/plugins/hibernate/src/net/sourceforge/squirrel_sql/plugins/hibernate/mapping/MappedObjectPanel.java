package net.sourceforge.squirrel_sql.plugins.hibernate.mapping;

import javax.swing.*;
import java.awt.*;
import java.util.prefs.Preferences;

public class MappedObjectPanel extends JPanel
{
   JTree objectTree;

   private JSplitPane _split;
   private static final String PERF_KEY_OBJ_TAB_DIV_LOC = "Squirrel.hibernateplugin.objTabDivLoc";

   public MappedObjectPanel(JComponent detailComp)
   {
      super(new GridLayout(1,1));

      objectTree = new JTree();

      _split = new JSplitPane();

      _split.setLeftComponent(new JScrollPane(objectTree));
      _split.setRightComponent(detailComp);

      add(_split);

      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            _split.setDividerLocation(Preferences.userRoot().getDouble(PERF_KEY_OBJ_TAB_DIV_LOC, 0.5));
         }
      });

   }

   public void closing()
   {
      Preferences.userRoot().putDouble(PERF_KEY_OBJ_TAB_DIV_LOC, ((double) _split.getDividerLocation())/ ((double) _split.getWidth()) );
   }
}
