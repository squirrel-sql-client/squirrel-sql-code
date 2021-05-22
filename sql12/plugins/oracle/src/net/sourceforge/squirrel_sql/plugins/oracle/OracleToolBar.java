package net.sourceforge.squirrel_sql.plugins.oracle;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.ToolBar;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JCheckBox;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;

public class OracleToolBar extends ToolBar
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(OracleToolBar.class);

   private final ISession _session;
   private final OracleInternalFrame _oracleInternalFrame;

   public OracleToolBar(ISession session, OracleInternalFrame oracleInternalFrame)
   {
      _session = session;
      _oracleInternalFrame = oracleInternalFrame;
   }

   private JCheckBox _stayOnTop;

   protected void addStayOnTop(boolean stayOnTop)
   {
      _stayOnTop = new JCheckBox(s_stringMgr.getString("oracle.dboutputStayOnTop"), false);
      _stayOnTop.setSelected(stayOnTop);
      _stayOnTop.setVisible(_session.getApplication().getDesktopStyle().supportsLayers());

      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            onStayOnTopChanged(_stayOnTop.isSelected());
            _stayOnTop.addActionListener(e -> onStayOnTopChanged(_stayOnTop.isSelected()));
         }
      });


      add(_stayOnTop);
   }

   private void onStayOnTopChanged(boolean selected)
   {
      if (selected)
      {
         _oracleInternalFrame.setLayer(JLayeredPane.PALETTE_LAYER.intValue());
      }
      else
      {
         _oracleInternalFrame.setLayer(JLayeredPane.DEFAULT_LAYER.intValue());
      }

      // Needs to be done in both cases because if the window goes back to
      // the default layer it goes back behind all other windows too.
      _oracleInternalFrame.toFront();
   }

   public boolean isStayOnTop()
   {
      return _stayOnTop.isSelected();
   }
}
