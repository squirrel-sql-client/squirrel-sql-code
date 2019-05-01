/*
 * Copyright (C) 2007 Rob Manning
 * manningr@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.client.session.mainpanel;

import java.awt.Component;
import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewer;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetMetaDataDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.TableState;
import net.sourceforge.squirrel_sql.fw.datasetviewer.coloring.markduplicates.MarkDuplicatesChooserController;
import net.sourceforge.squirrel_sql.fw.datasetviewer.coloring.markduplicates.MarkDuplicatesHandler;
import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;

import javax.swing.JComponent;

public interface IResultTab
{
   /**
    * Return the current SQL script.
    *
    * @return Current SQL script.
    */
   String getSqlString();

   /**
    * Return the current SQL script with control characters removed.
    *
    * @return Current SQL script.
    */
   String getViewableSqlString();

   void disposeTab();

   void returnToTabbedPane();

   /**
    * The tabbed pane of a single SQL result.
    * The tabs are "Results", "Meta data", "Info", ...
    * {@link #getCompleteResultTab()}
    */
   JComponent getTabbedPaneOfResultTabs();

   /**
    * The complete Result tab with top buttons and everything.
    * This is the component that is contained in the tabbed pane of all results.
    * See also {@link #getTabbedPaneOfResultTabs()}
    */
   JComponent getCompleteResultTab();


   void reRunSQL();

   TableState getResultSortableTableState();

   void toggleShowFindPanel();

   void findColumn();

   void markDuplicates(ActionEvent e);

   MarkDuplicatesChooserController getMarkDuplicatesChooserController();

   SQLResultExecuterPanelFacade getSQLResultExecuterPanelFacade();

   IDataSetViewer getSQLResultDataSetViewer();
}