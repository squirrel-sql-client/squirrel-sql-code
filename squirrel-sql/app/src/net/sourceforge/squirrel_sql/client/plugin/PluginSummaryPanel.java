package net.sourceforge.squirrel_sql.client.plugin;
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
import javax.swing.JPanel;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewer;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;

import net.sourceforge.squirrel_sql.client.plugin.PluginInfo;
import net.sourceforge.squirrel_sql.client.plugin.PluginInfoArrayDataSet;

public class PluginSummaryPanel extends JPanel {

    public PluginSummaryPanel(PluginInfo[] pluginInfo)
            throws DataSetException, IllegalArgumentException {
        super();
        if (pluginInfo == null) {
            throw new IllegalArgumentException("Null Pluginnfo[] passed");
        }

        DataSetViewerTablePanel table = new DataSetViewerTablePanel();
        DataSetViewer viewer = new DataSetViewer(table);
        viewer.show(new PluginInfoArrayDataSet(pluginInfo));

        add(table);
    }
}

