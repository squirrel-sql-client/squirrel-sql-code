package net.sourceforge.squirrel_sql.fw.gui;
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
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JComboBox;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

/**
 * This <TT>JComboBox</TT> will display all the Look and Feels
 * that have been registered with the <TT>UIManager</TT>.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class LookAndFeelComboBox extends JComboBox {
    /**
     * <TT>LookAndFeelInfo</TT> objects keyed by the
     * Look and Feel name.
     */
    private Map _lafsByName = new TreeMap();

    /**
     * <TT>LookAndFeelInfo</TT> objects keyed by the
     * Class name of the Look and Feel.
     */
    private Map _lafsByClassName = new TreeMap();

    /**
     * Default ctor. Select the currently active L & F after
     * building the combo box.
     */
    public LookAndFeelComboBox() {
        this(null);
    }

    public LookAndFeelComboBox(String selectedLafName) {
        super();
        generateLookAndFeelInfo();
        if (selectedLafName == null) {
            selectedLafName = UIManager.getLookAndFeel().getName();
        }
        setSelectedLookAndFeelName(selectedLafName);
    }

    public LookAndFeelInfo getSelectedLookAndFeel() {
        return (LookAndFeelInfo)_lafsByName.get((String)getSelectedItem());
    }

    public void setSelectedLookAndFeelName(String selectedLafName) {
        if (selectedLafName != null) {
            getModel().setSelectedItem(selectedLafName);
        }
    }

    public void setSelectedLookAndFeelClassName(String selectedLafClassName) {
        if (selectedLafClassName != null) {
            LookAndFeelInfo info = (LookAndFeelInfo)_lafsByClassName.get(selectedLafClassName);
            if (info != null) {
                setSelectedLookAndFeelName(info.getName());
            }
        }
    }

    /**
     * Fill combo with the names of all the Look and Feels in
     * alpabetical sequence.
     */
    private void generateLookAndFeelInfo() {
        // Put all available "Look and Feel" objects into collections
        // keyed by LAF name and by the class name.
        LookAndFeelInfo[] info = UIManager.getInstalledLookAndFeels();
        _lafsByName = new TreeMap();
        for (int i = 0; i < info.length; ++i) {
            _lafsByName.put(info[i].getName(), info[i]);
            _lafsByClassName.put(info[i].getClassName(), info[i]);
        }

        // Add the names of all LAF objects to control. By doing thru the Map
        // these will be sorted.
        for(Iterator it = _lafsByName.values().iterator(); it.hasNext();) {
            addItem(((LookAndFeelInfo)it.next()).getName());
        }
    }
}
