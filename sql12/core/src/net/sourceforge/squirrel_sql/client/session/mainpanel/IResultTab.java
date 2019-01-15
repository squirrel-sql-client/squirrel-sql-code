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
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetMetaDataDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.TableState;
import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;

public interface IResultTab {

    /**
     * Show the results from the passed <TT>IDataSet</TT>.
     *
     * @param	rsds	<TT>ResultSetDataSet</TT> to show results for.
     * @param	mdds	<TT>ResultSetMetaDataDataSet</TT> for rsds.
     *
     * @throws	IllegalArgumentException
     * 			Thrown if <tt>null</tt> <tt>SQLExecutionInfo</tt> passed.
     *
     * @throws	DataSetException
     * 			Thrown if error occurred processing dataset.
     */
    void showResults(ResultSetDataSet rsds, ResultSetMetaDataDataSet mdds) throws DataSetException;

   /**
     * Return the current SQL script.
     *
     * @return	Current SQL script.
     */
    String getSqlString();

    /**
     * Return the current SQL script with control characters removed.
     *
     * @return	Current SQL script.
     */
    String getViewableSqlString();

    /**
     * Return the title for this tab.
     */
    String getTitle();

   void disposeTab();

    void returnToTabbedPane();

    Component getOutputComponent();

    void reRunSQL();

    /**
     * @see IHasIdentifier#getIdentifier()
     */
    IIdentifier getIdentifier();

    TableState getResultSortableTableState();

   void toggleShowFindPanel();

   void findColumn();

   void markDuplicates(ActionEvent e);

   boolean isMarkDuplicates();

}