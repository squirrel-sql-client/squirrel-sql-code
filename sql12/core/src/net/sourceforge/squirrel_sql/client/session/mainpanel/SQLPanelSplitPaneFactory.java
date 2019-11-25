package net.sourceforge.squirrel_sql.client.session.mainpanel;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.props.Props;

import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.SplitPaneUI;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class SQLPanelSplitPaneFactory
{
   private static final String PREFS_KEY_SPLIT_DIVIDER_LOC = "squirrelSql_sqlPanel_divider_loc";
   private static final String PREFS_KEY_SPLIT_DIVIDER_LOC_HORIZONTAL = "squirrelSql_sqlPanel_divider_loc_horizontal";

   /**
    * Save the location of the divider depending on the orientation.
    *
    * @param splitPane
    * @see #PREFS_KEY_SPLIT_DIVIDER_LOC
    * @see #PREFS_KEY_SPLIT_DIVIDER_LOC_HORIZONTAL
    */
   static void saveOrientationDependingDividerLocation(JSplitPane splitPane)
   {
      int dividerLoc = splitPane.getDividerLocation();
      if (splitPane.getOrientation() == JSplitPane.VERTICAL_SPLIT)
      {
         Props.putInt(PREFS_KEY_SPLIT_DIVIDER_LOC, dividerLoc);
      }
      else
      {
         Props.putInt(PREFS_KEY_SPLIT_DIVIDER_LOC_HORIZONTAL, dividerLoc);
      }
   }

   /**
    * Create the split pane, restore the divider's location and register the needed listeners.
    * There will be a {@link PropertyChangeListener} for switching the layout horizontal/vertical
    * and a {@link MouseListener}, to restore the divider's location to the default values.
    *
    * @param splitPane
    * @param session
    * @return
    * @see #calculateDividerLocation(int, boolean)
    */
   static JSplitPane createSplitPane(ISession session)
   {
      final int spOrientation = session.getProperties().getSqlPanelOrientation();

      JSplitPane splitPane = new JSplitPane(spOrientation);

      int dividerLoc = calculateDividerLocation(spOrientation, false, splitPane);
      splitPane.setDividerLocation(dividerLoc);

      /*
       * Add a PropertyChangeListener for the SessionProperties for changing the orientation
       * of the split pane, if the user change the settings.
       */
      session.getProperties().addPropertyChangeListener(new PropertyChangeListener()
      {
         @Override
         public void propertyChange(PropertyChangeEvent evt)
         {
            if (SessionProperties.IPropertyNames.SQL_PANEL_ORIENTATION.equals(evt.getPropertyName()))
            {
               saveOrientationDependingDividerLocation(splitPane);
               splitPane.setOrientation((Integer) evt.getNewValue());
               splitPane.setDividerLocation(calculateDividerLocation(splitPane.getOrientation(), false, splitPane));
               splitPane.repaint();
            }
         }
      });


      /*
       * Add a mouse event listener to the divider, so that we can reset the divider location when a double click
       * occurs on the divider.
       */
      SplitPaneUI spUI = splitPane.getUI();
      if (spUI instanceof BasicSplitPaneUI)
      {
         BasicSplitPaneUI bspUI = (BasicSplitPaneUI) spUI;
         bspUI.getDivider().addMouseListener(new MouseAdapter()
         {
            @Override
            public void mouseClicked(MouseEvent e)
            {
               if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2)
               {
                  splitPane.setDividerLocation(calculateDividerLocation(splitPane.getOrientation(), true, splitPane));
               }
            }
         });

      }

      return splitPane;

   }

   /**
    * Calculates the divider location of the split pane, depending on a orientation.
    * The default values are defined as followed:
    * <li>Vertical: split panes height - 200</li>
    * <li>Horizontal: the half of split panes width</li>
    *
    * @param useDefault flag, if the default values should be used instead of the stored one.
    * @param splitPane
    * @return the divider's location depending on the orientation
    * @see #PREFS_KEY_SPLIT_DIVIDER_LOC
    * @see #PREFS_KEY_SPLIT_DIVIDER_LOC_HORIZONTAL
    */
   static int calculateDividerLocation(int orientation, boolean useDefault, JSplitPane splitPane)
   {
      int dividerLoc;

      final Dimension parentDim = splitPane.getSize();

      if (orientation == JSplitPane.VERTICAL_SPLIT)
      {
         int def = parentDim.height - 200;
         if (useDefault == false)
         {
            dividerLoc = Props.getInt(PREFS_KEY_SPLIT_DIVIDER_LOC, def);
         }
         else
         {
            dividerLoc = def;
         }
      }
      else
      {
         int def = parentDim.width / 2;
         if (useDefault == false)
         {
            dividerLoc = Props.getInt(PREFS_KEY_SPLIT_DIVIDER_LOC_HORIZONTAL, def);
         }
         else
         {
            dividerLoc = def;
         }
      }
      return dividerLoc;
   }
}
