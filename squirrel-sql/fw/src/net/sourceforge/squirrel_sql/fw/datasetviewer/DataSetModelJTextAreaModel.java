package net.sourceforge.squirrel_sql.fw.datasetviewer;
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
import java.awt.Component;

import javax.swing.JTextArea;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;
import javax.swing.text.Position;
import javax.swing.text.Segment;

/**
 * This is an adapter class that will turn a <TT>IDataSetModel</TT>
 * into a <TT>javax.swing.table.TableModel</TT>.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DataSetModelJTextAreaModel extends PlainDocument
		implements IDataSetModelConverter {
	/** <TT>IDataSetModel</TT> that this object is wrapped around. */
	private IDataSetModel _model;

	/** Listener for events in the <TT>IDataSetModel</TT>. */
	private MyDataModelListener _modelListener = new MyDataModelListener();

	/**
	 * Default ctor.
	 */
	public DataSetModelJTextAreaModel() {
		this(null);
	}

	/**
	 * Ctor specifying the <TT>IDataSetModel</TT> to display
	 * data from.
	 * 
	 * @param	model	<TT>IDataSetMoel</TT> containing the table data.
	 */
	public DataSetModelJTextAreaModel(IDataSetModel model) {
		super();
		setDataSetModel(model);
	}

	/**
	 * Set the <TT>IDataSetModel</TT> to display
	 * data from.
	 * 
	 * @param	model	<TT>IDataSetModel</TT> containing the table data.
	 */
	public synchronized void setDataSetModel(IDataSetModel model) {
		if (_model != null) {
			_model.removeListener(_modelListener);
		}
		_model = model;
		if (_model != null) {
			_model.addListener(_modelListener);
		}
	}
	/**
	 * Create the default component for this converter. In this
	 * case a <TT>JTextArea</TT>
	 * 
	 * @return	A new instance of a <TT>JTable</TT>.
	 */
	public Component createComponent() {
		return new JTextArea(this);
	}

	private synchronized void buildDocument() {
/*
		try {
			if (_model == null) {
				this.remove(0, getLength() - 1);
			} else {
				ColumnDisplayDefinition[] colDefs = _model.getColumnDefinitions();
		        if (_model._showHeadings) {
		            StringBuffer buf = new StringBuffer();
		            for (int i = 0; i < colDefs.length; ++i) {
		                if (i == 0) {
		                }
		                buf.append(format(colDefs[i].getLabel(), colDefs[i].getDisplayWidth(), ' '));
		            }
		            addLine(buf.toString());
		            buf = new StringBuffer();
		            for (int i = 0; i < colDefs.length; ++i) {
		                buf.append(format("", colDefs[i].getDisplayWidth(), '-'));
		            }
		            addLine(buf.toString());
		        }
				for (int i = 0, limit = _model.getRowCount(); i < limit;) {
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace(System.out);	//??ERROR
		}
*/
	}
	private class MyDataModelListener implements DataSetModelListener {
		/**
		 * @see DataSetModelListener#allRowsAdded(DataSetModelEvent)
		 */
		public void allRowsAdded(DataSetModelEvent evt) {
			buildDocument();
		}

		/**
		 * @see DataSetModelListener#moveToTop(DataSetModelEvent)
		 */
		public void moveToTop(DataSetModelEvent evt) {
		}
	}

}

