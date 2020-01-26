package net.sourceforge.squirrel_sql.client.preferences;
/*
 * Copyright (C) 2001-2004 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.client.plugin.PluginInfo;
import net.sourceforge.squirrel_sql.client.preferences.codereformat.FormatSqlConfigPrefsTab;
import net.sourceforge.squirrel_sql.client.preferences.shortcut.ShortcutPrefsTab;
import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static net.sourceforge.squirrel_sql.client.preferences.PreferenceType.DATATYPE_PREFERENCES;

/**
 * This sheet allows the user to maintain global preferences.
 * JASON: Rename to GlobalPreferencesInternalFrame
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
@SuppressWarnings("serial")
public class GlobalPreferencesSheet extends DialogWidget
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(GlobalPreferencesSheet.class);

   private final static ILogger s_log = LoggerController.createLogger(GlobalPreferencesSheet.class);

   /**
    * Singleton instance of this class.
    */
   private static GlobalPreferencesSheet s_instance;

   /**
    * List of all the panels (instances of
    * <TT>IGlobalPreferencesPanel</TT> objects in sheet.
    */
   private List<IGlobalPreferencesPanel> _panels = new ArrayList<>();

   private JTabbedPane _tabPane;


   /**
    * Sheet title.
    */
   private JLabel _titleLbl = new JLabel();

   public static final String PREF_KEY_GLOBAL_PREFS_SHEET_WIDTH = "Squirrel.globalPrefsSheetWidth";
   public static final String PREF_KEY_GLOBAL_PREFS_SHEET_HEIGHT = "Squirrel.globalPrefsSheetHeight";


   private static ArrayList<GlobalPreferencesActionListener> _listeners = new ArrayList<>();

   private GlobalPreferencesSheet()
   {
      super(s_stringMgr.getString("GlobalPreferencesSheet.title"), true, Main.getApplication());

      createGUI();

      for (Iterator<IGlobalPreferencesPanel> it = _panels.iterator(); it.hasNext(); )
      {
         IGlobalPreferencesPanel pnl = it.next();
         try
         {
            pnl.initialize(Main.getApplication());
         }
         catch (Throwable th)
         {
            final String msg = s_stringMgr.getString("GlobalPreferencesSheet.error.loading", pnl.getTitle());
            s_log.error(msg, th);
            Main.getApplication().showErrorDialog(msg, th);
         }
      }
      setSize(getDimension());

      Main.getApplication().getMainFrame().addWidget(this);
      DialogWidget.centerWithinDesktop(this);
      setVisible(true);

   }

   private Dimension getDimension()
   {
      return new Dimension(
            Props.getInt(PREF_KEY_GLOBAL_PREFS_SHEET_WIDTH, 650),
            Props.getInt(PREF_KEY_GLOBAL_PREFS_SHEET_HEIGHT, 600)
      );
   }


   /**
    * Registers a GlobalPreferencesActionListener to receive callbacks when
    * certain actions take place.
    *
    * @param listener the GlobalPreferencesActionListener to register.
    */
   public static void addGlobalPreferencesActionListener(GlobalPreferencesActionListener listener)
   {
      _listeners.add(listener);
   }


   /**
    * Unregisters a GlobalPreferencesActionListener to receive callbacks when
    * certain actions take place.
    *
    * @param listener the GlobalPreferencesActionListener to unregister.
    */
   public static void removeGlobalPreferencesActionListener(GlobalPreferencesActionListener listener)
   {
      _listeners.remove(listener);
   }


   /**
    * Show the Preferences dialog
    *
    * @throws IllegalArgumentException Thrown if a <TT>null</TT> <TT>IApplication</TT> object passed.
    */
   @SuppressWarnings("unchecked")
   public static void showSheet(Class componentClassOfTabToSelect)
   {
      if (s_instance == null)
      {
         s_instance = new GlobalPreferencesSheet();
      }
      else
      {
         s_instance.moveToFront();
      }

      if (null != componentClassOfTabToSelect)
      {
         s_instance.selectTab(componentClassOfTabToSelect);
      }
      for (GlobalPreferencesActionListener listener : _listeners)
      {
         listener.onDisplayGlobalPreferences();
      }
   }

   private void selectTab(Class componentClassOfTabToSelect)
   {
      for (int i = 0; i < _tabPane.getTabCount(); i++)
      {
         Component comp = _tabPane.getComponentAt(i);
         if (JScrollPane.class.equals(comp.getClass()))
         {
            comp = ((JScrollPane) comp).getViewport().getView();
         }

         if (componentClassOfTabToSelect.equals(comp.getClass()))
         {
            _tabPane.setSelectedIndex(i);
            return;
         }
      }
   }

   public void dispose()
   {
      Dimension size = getSize();
      Props.putInt(PREF_KEY_GLOBAL_PREFS_SHEET_WIDTH, size.width);
      Props.putInt(PREF_KEY_GLOBAL_PREFS_SHEET_HEIGHT, size.height);

      for (Iterator<IGlobalPreferencesPanel> it = _panels.iterator(); it.hasNext(); )
      {
         IGlobalPreferencesPanel pnl = it.next();
         pnl.uninitialize(Main.getApplication());
      }

      s_instance = null;
      super.dispose();
   }


   /**
    * Set title of this frame. Ensure that the title label
    * matches the frame title.
    *
    * @param   title   New title text.
    */
   public void setTitle(String title)
   {
      super.setTitle(title);
      _titleLbl.setText(title);
   }

   /**
    * Close this sheet.
    */
   private void performClose()
   {
      fireClose();
      dispose();
   }

   private void fireClose()
   {
      for (GlobalPreferencesActionListener listener : _listeners.toArray(new GlobalPreferencesActionListener[0]))
      {
         listener.onPerformClose();
      }
   }

   /**
    * OK button pressed so save changes.
    */
   private void performOk()
   {
      CursorChanger cursorChg = new CursorChanger(Main.getApplication().getMainFrame());
      cursorChg.show();
      try
      {
         final boolean isDebug = s_log.isDebugEnabled();
         long start = 0;
         for (Iterator<IGlobalPreferencesPanel> it = _panels.iterator(); it.hasNext(); )
         {
            if (isDebug)
            {
               start = System.currentTimeMillis();
            }
            IGlobalPreferencesPanel pnl = it.next();
            try
            {
               pnl.applyChanges();
            }
            catch (Throwable th)
            {
               final String msg = s_stringMgr.getString("GlobalPreferencesSheet.error.saving", pnl.getTitle());
               s_log.error(msg, th);
               Main.getApplication().showErrorDialog(msg, th);
            }
            if (isDebug)
            {
               s_log.debug("Panel " + pnl.getTitle()
                     + " applied changes in "
                     + (System.currentTimeMillis() - start) + "ms");
            }
         }
      }
      finally
      {
         Main.getApplication().savePreferences(DATATYPE_PREFERENCES);
         cursorChg.restore();
      }

      dispose();
      for (GlobalPreferencesActionListener listener : _listeners)
      {
         listener.onPerformOk();
      }
   }

   /**
    * Create user interface.
    */
   private void createGUI()
   {
      setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

      // This is a tool window.
      makeToolWindow(true);

      PrefrenceTabActvivationListener prefrenceTabActvivationListener = new PrefrenceTabActvivationListener()
      {
         @Override
         public void activateTabForClass(Class<?> tabClass)
         {
            selectTab(tabClass);
         }
      };


      // Add panels for core Squirrel functionality.
      _panels.add(new GeneralPreferencesPanel());
      _panels.add(new SQLPreferencesController());
      _panels.add(new ProxyPreferencesPanel());
      _panels.add(new DataTypePreferencesPanel());
      _panels.add(new WikiTablePreferencesTab());
      _panels.add(new FormatSqlConfigPrefsTab(Main.getApplication()));
      _panels.add(new ShortcutPrefsTab());

      // Go thru all loaded plugins asking for panels.
      PluginInfo[] plugins = Main.getApplication().getPluginManager().getPluginInformation();
      for (int plugIdx = 0; plugIdx < plugins.length; ++plugIdx)
      {
         PluginInfo pi = plugins[plugIdx];
         if (pi.isLoaded())
         {
            IGlobalPreferencesPanel[] pnls = pi.getPlugin().getGlobalPreferencePanels();
            if (pnls != null && pnls.length > 0)
            {
               for (int pnlIdx = 0; pnlIdx < pnls.length; ++pnlIdx)
               {
                  _panels.add(pnls[pnlIdx]);
               }
            }
         }
      }

      // Add all panels to the tabbed pane.
      _tabPane = UIFactory.getInstance().createTabbedPane();
      for (Iterator<IGlobalPreferencesPanel> it = _panels.iterator(); it.hasNext(); )
      {
         IGlobalPreferencesPanel pnl = it.next();
         String pnlTitle = pnl.getTitle();
         String hint = pnl.getHint();
         _tabPane.addTab(pnlTitle, null, pnl.getPanelComponent(), hint);
      }

      // This seems to be necessary to get background colours
      // correct. Without it labels added to the content pane
      // have a dark background while those added to a JPanel
      // in the content pane have a light background under
      // the java look and feel. Similar effects occur for other
      // look and feels.
      final JPanel contentPane = new JPanel();
      contentPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
      setContentPane(contentPane);

      GridBagConstraints gbc = new GridBagConstraints();
      contentPane.setLayout(new GridBagLayout());

      gbc.gridwidth = 1;
      gbc.fill = GridBagConstraints.BOTH;

      gbc.gridx = 0;
      gbc.gridy = 0;
      gbc.weightx = 1;
      contentPane.add(_titleLbl, gbc);

      ++gbc.gridy;
      gbc.weighty = 1;
      contentPane.add(_tabPane, gbc);

      ++gbc.gridy;
      gbc.weighty = 0;
      contentPane.add(createButtonsPanel(), gbc);

      GUIUtils.enableCloseByEscape(this, dw -> performClose());
   }

   /**
    * Create panel at bottom containing the buttons.
    */
   private JPanel createButtonsPanel()
   {
      JPanel pnl = new JPanel();

      JButton okBtn = new JButton(s_stringMgr.getString("GlobalPreferencesSheet.ok"));
      okBtn.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent evt)
         {
            performOk();
         }
      });
      JButton closeBtn = new JButton(s_stringMgr.getString("GlobalPreferencesSheet.close"));
      closeBtn.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent evt)
         {
            performClose();
         }
      });

      GUIUtils.setJButtonSizesTheSame(new JButton[]{okBtn, closeBtn});

      pnl.add(okBtn);
      pnl.add(closeBtn);

      getRootPane().setDefaultButton(okBtn);

      return pnl;
   }
}
