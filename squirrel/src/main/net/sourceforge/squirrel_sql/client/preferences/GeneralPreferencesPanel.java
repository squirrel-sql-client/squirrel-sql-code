package net.sourceforge.squirrel_sql.client.preferences;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.awt.BorderLayout;
import java.awt.Component;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.io.File;

import javax.swing.JCheckBox;
//import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
//import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import net.sourceforge.squirrel_sql.fw.gui.PropertyPanel;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;

class GeneralPreferencesPanel /*extends JPanel*/ implements IGlobalPreferencesPanel {
    /**
     * This interface defines locale specific strings. This should be
     * replaced with a property file.
     */
//  private interface i18n {
//      String LOOK_AND_FEEL = "Look and Feel:";
//      String SHOW_CONTENTS = "Show Window Contents While Dragging:";
//      String SHOW_TOOLTIPS = "Show Tooltips:";
//      String THEME_PACK = "Theme Pack:";
//      String LAF_WARNING = "Note: Changes to the Look and Feel or to the \nTheme Pack will require a restart of Squirrel-SQL.";
//      String TAB_HINT = "General";
//      String TAB_TITLE = "General";
//  }

    private MyPanel _myPanel = new MyPanel();

    private IApplication _app;

//  private JCheckBox _showContents = new JCheckBox();
//  private JCheckBox _showToolTips = new JCheckBox();
//  private LookAndFeelComboBox _lafCmb = new LookAndFeelComboBox();
//  private ThemePackComboBox _themePackCmb = new ThemePackComboBox();

//  private String _skinLafName;

    public GeneralPreferencesPanel(/*IApplication app, SquirrelPreferences prefs*/)
            /*throws IllegalArgumentException*/ {
        super();
//      if (app == null) {
//          throw new IllegalArgumentException("Null IApplication passed");
//      }
//      if (prefs == null) {
//          throw new IllegalArgumentException("Null SquirrelPreferences passed");
//      }

//      _app = app;
//      _prefs = prefs;

//      _skinLafName = _app.getLookAndFeelRegister().getSkinnableLookAndFeelName();

//      createUserInterface();
//      loadData();
    }

    public void initialize(IApplication app)
            throws IllegalArgumentException {
        if (app == null) {
            throw new IllegalArgumentException("Null IApplication passed");
        }

        _app = app;

//      createUserInterface();
        _myPanel.loadData(_app.getSquirrelPreferences());
//      loadData();
    }

    public Component getPanelComponent() {
        return _myPanel;
    }

    //public void setPreferences(SquirrelPreferences value) {
    //  _prefs = value;
    //}

//  private void loadData() {
//      _showContents.setSelected(_prefs.getShowContentsWhenDragging());
//      _showToolTips.setSelected(_prefs.getShowToolTips());
//      _themePackCmb.setSelectedItem(_prefs.getThemePack());
//      _themePackCmb.setEnabled(((String)_lafCmb.getSelectedItem()).equals(_skinLafName));
//  }

    public void applyChanges() {
        _myPanel.applyChanges(_app.getSquirrelPreferences());
//      _prefs.setShowContentsWhenDragging(_showContents.isSelected());
//      _prefs.setShowToolTips(_showToolTips.isSelected());
//      String className = _lafCmb.getSelectedLookAndFeel().getClassName();
//      _prefs.setLookAndFeelClassName(_lafCmb.getSelectedLookAndFeel().getClassName());
//      _prefs.setThemePack((String)_themePackCmb.getSelectedItem());
    }

    public String getTitle() {
        return MyPanel.i18n.TAB_TITLE;
    }

    public String getHint() {
        return MyPanel.i18n.TAB_HINT;
    }

//  private void createUserInterface() {
//      setLayout(new BorderLayout());

        // Centre panel is the properties panel.
//      PropertyPanel pnl = new PropertyPanel();
//      _themePackCmb.setEnabled(false);
//      JLabel lbl = new JLabel(i18n.SHOW_CONTENTS, SwingConstants.RIGHT);
//      pnl.add(lbl, _showContents);
//      lbl = new JLabel(i18n.SHOW_TOOLTIPS, SwingConstants.RIGHT);
//      pnl.add(lbl, _showToolTips);
//      lbl = new JLabel(i18n.LOOK_AND_FEEL, SwingConstants.RIGHT);
//      pnl.add(lbl, _lafCmb);
//      lbl = new JLabel(i18n.THEME_PACK, SwingConstants.RIGHT);
//      pnl.add(lbl, _themePackCmb);
//      _lafCmb.addActionListener(new ActionListener() {
//          public void actionPerformed(ActionEvent evt) {
//              _themePackCmb.setEnabled(((String)_lafCmb.getSelectedItem()).equals(_skinLafName));
//          }
//      });
//      add(pnl, BorderLayout.CENTER);

        // Warning message in bottom panel.
//      JTextArea ta = new JTextArea(i18n.LAF_WARNING);
//      ta.setBackground(getBackground());
//      ta.setEditable(false);
//      ta.setFont(lbl.getFont());
//      add(ta, BorderLayout.SOUTH);
//  }

/*
    private class ThemePackComboBox extends JComboBox {
        ThemePackComboBox() {
            super();
            loadSkins();
        }

        private void loadSkins() {
            File skinsFolder = new File(ApplicationFiles.SQUIRREL_SKINS_FOLDER);
            if (skinsFolder.canRead()) {
                File[] files = skinsFolder.listFiles(new FileExtensionFilter("JAR files", new String[] {".jar", ".zip"}));
                for (int i = 0; i < files.length; ++i) {
                    addItem(files[i].getName());
                }
            }
        }
    }
*/
    private static final class MyPanel extends JPanel {
        /**
         * This interface defines locale specific strings. This should be
         * replaced with a property file.
         */
        interface i18n {
    //      String LOOK_AND_FEEL = "Look and Feel:";
            String SHOW_CONTENTS = "Show Window Contents While Dragging:";
            String SHOW_TOOLTIPS = "Show Tooltips:";
    //      String THEME_PACK = "Theme Pack:";
    //      String LAF_WARNING = "Note: Changes to the Look and Feel or to the \nTheme Pack will require a restart of Squirrel-SQL.";
            String TAB_HINT = "General";
            String TAB_TITLE = "General";
        }

        private JCheckBox _showContents = new JCheckBox();
        private JCheckBox _showToolTips = new JCheckBox();

        MyPanel() {
            super();
            createUserInterface();
        }

        void loadData(SquirrelPreferences prefs) {
            _showContents.setSelected(prefs.getShowContentsWhenDragging());
            _showToolTips.setSelected(prefs.getShowToolTips());
    //      _themePackCmb.setSelectedItem(_prefs.getThemePack());
    //      _themePackCmb.setEnabled(((String)_lafCmb.getSelectedItem()).equals(_skinLafName));
        }

        void applyChanges(SquirrelPreferences prefs) {
            prefs.setShowContentsWhenDragging(_showContents.isSelected());
            prefs.setShowToolTips(_showToolTips.isSelected());
    //      String className = _lafCmb.getSelectedLookAndFeel().getClassName();
    //      _prefs.setLookAndFeelClassName(_lafCmb.getSelectedLookAndFeel().getClassName());
    //      _prefs.setThemePack((String)_themePackCmb.getSelectedItem());
        }

        private void createUserInterface() {
            setLayout(new BorderLayout());
            PropertyPanel pnl = new PropertyPanel();
    //      _themePackCmb.setEnabled(false);
            JLabel lbl = new JLabel(i18n.SHOW_CONTENTS, SwingConstants.RIGHT);
            pnl.add(lbl, _showContents);
            lbl = new JLabel(i18n.SHOW_TOOLTIPS, SwingConstants.RIGHT);
            pnl.add(lbl, _showToolTips);
    //      lbl = new JLabel(i18n.LOOK_AND_FEEL, SwingConstants.RIGHT);
    //      pnl.add(lbl, _lafCmb);
    //      lbl = new JLabel(i18n.THEME_PACK, SwingConstants.RIGHT);
    //      pnl.add(lbl, _themePackCmb);
    //      _lafCmb.addActionListener(new ActionListener() {
    //          public void actionPerformed(ActionEvent evt) {
    //              _themePackCmb.setEnabled(((String)_lafCmb.getSelectedItem()).equals(_skinLafName));
    //          }
    //      });
            add(pnl, BorderLayout.CENTER);
        }
    }
}
