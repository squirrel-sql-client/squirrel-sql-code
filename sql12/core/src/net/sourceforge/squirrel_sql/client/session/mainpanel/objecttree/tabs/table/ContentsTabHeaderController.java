package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table;

import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JComponent;
import javax.swing.JLabel;

public class ContentsTabHeaderController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ContentsTabHeaderController.class);

   private final JLabel _lblHeader;

   public ContentsTabHeaderController()
   {
      _lblHeader = new JLabel();
   }

   public JComponent getHeaderComponent()
   {
      return _lblHeader;
   }

   public void updateHeader(int nbrOfRowsRead, SessionProperties props)
   {
      GUIUtils.processOnSwingEventThread(() -> _updateHeader(nbrOfRowsRead, props));
   }

   private void _updateHeader(int nbrOfRowsRead, SessionProperties props)
   {
      if(false == props.getContentsLimitRows() || nbrOfRowsRead < props.getContentsNbrRowsToShow())
      {
         String rowsMsg = s_stringMgr.getString("ContentsTabHeaderController.rowsMessage", nbrOfRowsRead);
         _lblHeader.setText("<html><pre>" + rowsMsg + "</pre></html>");
      }
      else
      {
         String limitMsg = s_stringMgr.getString("ContentsTabHeaderController.limitMessage", nbrOfRowsRead);
         _lblHeader.setText("<html><pre>" + limitMsg + "</pre></html>");
      }
   }
}
