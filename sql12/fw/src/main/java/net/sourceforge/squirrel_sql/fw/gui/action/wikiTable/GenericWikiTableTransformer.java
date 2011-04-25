/*
 * Copyright (C) 2011 Stefan Willinger
 * wis775@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.fw.gui.action.wikiTable;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.SquirrelTableCellRenderer;

import org.apache.commons.lang.StringUtils;

/**
 * A generic implementation for {@link IWikiTableTransformer}.
 * @author Stefan Willinger
 * 
 */
public class GenericWikiTableTransformer implements IWikiTableTransformer {

	public static String NEW_LINE = System.getProperty("line.separator"); //$NON-NLS-1$

	private IWikiTableConfiguration configuration = null;
	
	/**
	 * Default Constructor.
	 */
	public GenericWikiTableTransformer() {
		super();
	}

	/**
	 * Constructor for a full configured {@link GenericWikiTableTransformer}
	 */
	public GenericWikiTableTransformer(IWikiTableConfiguration configuration) {
		super();
		setConfiguration(configuration);
	}


	
	

	/**
	 * @see
	 * net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableTransformer
	 * #transform(javax.swing.JTable)
	 */
	@Override
	public String transform(JTable table) {
		int nbrSelRows = table.getSelectedRowCount();
		int nbrSelCols = table.getSelectedColumnCount();
		int[] selRows = table.getSelectedRows();
		int[] selCols = table.getSelectedColumns();

		if (selRows.length != 0 && selCols.length != 0) {
			StringBuilder buf = new StringBuilder(1024);
			// Start the table
			appendWithReplacement(buf, configuration.getTableStartTag());
			// Create the header
			appendWithReplacement(buf, configuration.getHeaderStartTag());
			for (int colIdx = 0; colIdx < nbrSelCols; ++colIdx) {
				appendWithReplacement(buf, configuration.getHeaderCell(), table.getColumnName(selCols[colIdx]));
			}
			appendWithReplacement(buf, configuration.getHeaderEndTag());
			// Now fill all the table rows
			for (int rowIdx = 0; rowIdx < nbrSelRows; ++rowIdx) {
				appendWithReplacement(buf, configuration.getRowStartTag());
				for (int colIdx = 0; colIdx < nbrSelCols; ++colIdx) {
					TableCellRenderer cellRenderer = table.getCellRenderer(selRows[rowIdx], selCols[colIdx]);
					Object cellObj = table.getValueAt(selRows[rowIdx], selCols[colIdx]);

					if (cellRenderer instanceof SquirrelTableCellRenderer) {
						cellObj = ((SquirrelTableCellRenderer) cellRenderer).renderValue(cellObj);
					}

					String value = null;
					if (cellObj == null) {
						value = ""; //$NON-NLS-1$
					} else {
						final String tmp = cellObj.toString();
						if (tmp.trim().equals("")) { //$NON-NLS-1$
							value = ""; //$NON-NLS-1$
						} else {
							value = tmp;
						}
					}
					appendWithReplacement(buf, configuration.getDataCell(), value);
				}
				appendWithReplacement(buf, configuration.getRowEndTag());
			}
			appendWithReplacement(buf, configuration.getTableEndTag());
			return buf.toString();
		}else{
			return null;
		}
		
	}

	

	/**
	 * Replaces some variables in the token and append's the resulting string to
	 * the buffer. The following place holder would be replaced: <li>
	 * NEW_LINE_PLACEHOLDER</li>
	 * 
	 * @param buffer Buffer, where to append
	 * @param token, where the replacement should be done.
	 * @see #NEW_LINE_PLACEHOLDER
	 * @see #appendWithReplacement(StringBuilder, String, String)
	 */
	public void appendWithReplacement(StringBuilder buff, String token) {
		appendWithReplacement(buff, token, null);
	}

	/**
	 * Replaces some variables in the token and append's the resulting string to
	 * the buffer. The following place holder would be replaced: 
	 * <li>NEW_LINE_PLACEHOLDER</li>
	 * <li>VALUE_PLACEHOLDER</li>
	 * If the configuration contains a <code>noWikiTag</code>, then the value will be embedded into this tag.
	 * @param buff Buffer, where to append
	 * @param token token, where the replacement should be done.
	 * @param value value, which should be replace the {@link IWikiTableConfiguration#VALUE_PLACEHOLDER}
	 * @see #NEW_LINE_PLACEHOLDER
	 * @see #VALUE_PLACEHOLDER
	 * @see IWikiTableConfiguration#getNoWikiTag() 
	 *  */
	public void appendWithReplacement(StringBuilder buff, String token, String value) {
		
		if(StringUtils.contains(token, IWikiTableConfiguration.VALUE_PLACEHOLDER) && value == null){
			value = ""; //$NON-NLS-1$
		}else if (!StringUtils.contains(token, IWikiTableConfiguration.VALUE_PLACEHOLDER) && value != null) {
			throw new IllegalStateException("there is no place holder for the value, but I should inject a value!"); //$NON-NLS-1$
		}
			
		if(StringUtils.isNotBlank(token)){
			// Escape the value;
			if(StringUtils.contains(token, IWikiTableConfiguration.VALUE_PLACEHOLDER)){
				value = escapeString(value);
			}
			
			value = replacePlaceHolder(token, value);
			
			buff.append(value);
		}
		
		
	}

	private String replacePlaceHolder(String token, String value) {
		// Replace the place holders in the token
		if(StringUtils.isNotBlank(token)){
			String newValue = token.replace(IWikiTableConfiguration.NEW_LINE_PLACEHOLDER, NEW_LINE);
			if(StringUtils.isNotEmpty(value)){
				newValue = newValue.replace(IWikiTableConfiguration.VALUE_PLACEHOLDER, value);
			}
			return newValue;
		}else{
			return value;
		}
	}

	/**
	 * Embeds the value within the specified <code>noWikiTag</code>
	 * @param valueString value to use
	 * @return value embedded in the <code>noWikiTag</code>
	 * @see IWikiTableConfiguration#getNoWikiTag()
	 */
	protected String escapeString(String valueString) {
		return replacePlaceHolder(configuration.getNoWikiTag(), valueString);
	}

	public IWikiTableConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(IWikiTableConfiguration configuration) {
		this.configuration = configuration;
	}

	

}
