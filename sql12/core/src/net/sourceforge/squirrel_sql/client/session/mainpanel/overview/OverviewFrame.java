package net.sourceforge.squirrel_sql.client.session.mainpanel.overview;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetAdapter;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetEvent;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

public class OverviewFrame extends DialogWidget
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(OverviewFrame.class);

   private static final String PREF_KEY_OVERVIEW_FRAME_WIDTH = "Squirrel.overview.FrameWidth";
   private static final String PREF_KEY_OVERVIEW_FRAME_HIGHT = "Squirrel.overview.FrameHight";


   private JCheckBox _chkOnTop;

   public OverviewFrame(DataSetViewerTablePanel simpleTable, IApplication app, Window parent)
   {
      super(s_stringMgr.getString("OverviewFrame.title"), true, true, true, true, app, parent);

      setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

      final Container cont = getContentPane();
      cont.setLayout(new BorderLayout());

      if (app.getDesktopStyle().supportsLayers())
      {
         JPanel pnlButtons = new JPanel(new GridBagLayout());
         GridBagConstraints gbc;


         // i18n[resultFrame.stayOnTop=Stay on top]
         _chkOnTop = new JCheckBox(s_stringMgr.getString("OverviewFrame.stayOnTop"));
         gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,0,5), 0,0);
         pnlButtons.add(_chkOnTop, gbc);
         _chkOnTop.setSelected(true);


         _chkOnTop.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               onStayOnTopChanged();
            }
         });

         gbc = new GridBagConstraints(2,0,1,1,1,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,0,5), 0,0);
         pnlButtons.add(new JPanel(), gbc);
         cont.add(pnlButtons, BorderLayout.NORTH);
      }


      cont.add(new JScrollPane(simpleTable.getComponent()), BorderLayout.CENTER);


      int width = Preferences.userRoot().getInt(PREF_KEY_OVERVIEW_FRAME_WIDTH, 300);
      int hight = Preferences.userRoot().getInt(PREF_KEY_OVERVIEW_FRAME_HIGHT, 300);

      setSize(new Dimension(width, hight));


      addWidgetListener(new WidgetAdapter()
      {
         @Override
         public void widgetClosing(WidgetEvent evt)
         {
            onWidgetClosing();
         }
      });
   }

   private void onWidgetClosing()
   {
      Dimension size = getSize();

      Preferences.userRoot().putInt(PREF_KEY_OVERVIEW_FRAME_WIDTH, size.width);
      Preferences.userRoot().putInt(PREF_KEY_OVERVIEW_FRAME_HIGHT, size.height);
   }


   private void onStayOnTopChanged()
   {
      if(_chkOnTop.isSelected())
      {
         setLayer(JLayeredPane.PALETTE_LAYER.intValue());
      }
      else
      {
         setLayer(JLayeredPane.DEFAULT_LAYER.intValue());
      }

      // Needs to be done in both cases because if the window goes back to
      // the default layer it goes back behind all other windows too.
      toFront();
   }



}