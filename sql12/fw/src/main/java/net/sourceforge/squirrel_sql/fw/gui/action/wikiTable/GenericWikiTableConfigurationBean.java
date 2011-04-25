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

import java.io.Serializable;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import org.apache.commons.lang.StringUtils;

/**
 * A generic implementation for {@link IWikiTableConfiguration}
 * This implementation is intended to be the base for all other provided configuration and can be easily saved to a file.
 * This configuration uses {@link GenericWikiTableTransformer} as the suitable transformer.
 * <p /><b>Warning:</b>
 * User specific configurations are saved with this bean to configuration files. Don't rename this class name, otherwise saved configurations
 * cannot be read from the file anymore.
 * @author Stefan Willinger
 * 
 */
public class GenericWikiTableConfigurationBean implements IWikiTableConfiguration, Serializable {

	private static final long serialVersionUID = 1L;


	private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(GenericWikiTableConfigurationBean.class);
	

	private String name;
	private String tableStartTag;
	private String headerStartTag;
	private String headerCell;
	private String headerEndTag;
	private String rowStartTag;
	private String dataCell;
	private String rowEndTag;
	private String tableEndTag;
	private String noWikiTag;
	
	private boolean enabled = true;
	
	/**
	 * Flag, if the configuration is initialized.
	 */
	private boolean initialized = false;

	/**
	 * Default Constructor.
	 * A configuration initialized with this constructor is always enabled.
	 */
	public GenericWikiTableConfigurationBean() {
		super();
		setDataCell(VALUE_PLACEHOLDER);
		setHeaderCell(VALUE_PLACEHOLDER);
		setNoWikiTag(VALUE_PLACEHOLDER);
		
		initialized = true;
	}

	/**
	 * Constructor for a full configured {@link GenericWikiTableConfigurationBean}.
	 * A configuration initialized with this constructor is always enabled.
	 */
	public GenericWikiTableConfigurationBean(String name, String tableStartTag, String headerStartTag, String headerCell,
			String headerEnd, String rowStartTag, String dataCell, String rowEndTag, String tableEndTag, String noWikiTag) {
		super();
		setName(name);
		setTableStartTag(tableStartTag);
		setHeaderStartTag(headerStartTag);
		setHeaderCell(headerCell);
		setHeaderEndTag(headerEnd);
		setRowStartTag(rowStartTag);
		setDataCell(dataCell);
		setRowEndTag(rowEndTag);
		setTableEndTag(tableEndTag);
		setNoWikiTag(noWikiTag);
		
		initialized = true;
	}

	/**
	 * Copy Constructor
	 * @param copy The source for the copy
	 */
	public GenericWikiTableConfigurationBean(IWikiTableConfiguration copy) {
		setName(copy.getName());
		setTableStartTag(copy.getTableStartTag());
		setHeaderStartTag(copy.getHeaderStartTag());
		setHeaderCell(copy.getHeaderCell());
		setHeaderEndTag(copy.getHeaderEndTag());
		setRowStartTag(copy.getRowStartTag());
		setDataCell(copy.getDataCell());
		setRowEndTag(copy.getRowEndTag());
		setTableEndTag(copy.getTableEndTag());
		setNoWikiTag(copy.getNoWikiTag());
		setEnabled(copy.isEnabled());
		
		initialized = true;
	}
	
	

	/**
	 * @see
	 * net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableConfiguration
	 * #getTableStartTag()
	 */
	public String getTableStartTag() {
		return tableStartTag;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableConfiguration#setTableStartTag(java.lang.String)
	 */
	@Override
	public void setTableStartTag(String tableStartTag) {
		checkReadOnly();
		this.tableStartTag = tableStartTag;
	}

	/**
	 * @see
	 * net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableConfiguration
	 * #getHeaderStartTag()
	 */
	public String getHeaderStartTag() {
		return headerStartTag;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableConfiguration#setHeaderStartTag(java.lang.String)
	 */
	@Override
	public void setHeaderStartTag(String headerStartTag) {
		checkReadOnly();
		this.headerStartTag = headerStartTag;
	}

	/**
	 * @see
	 * net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableConfiguration
	 * #getHeaderCell()
	 */
	public String getHeaderCell() {
		return headerCell;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableConfiguration#setHeaderCell(java.lang.String)
	 */
	@Override
	public void setHeaderCell(String headerCell) {
		checkReadOnly();
		if(StringUtils.contains(headerCell, VALUE_PLACEHOLDER) == false){
			throw new IllegalArgumentException(s_stringMgr.getString("GenericWikiTableConfigurationBean.headerCellErrorValueVariableMissing")); //$NON-NLS-1$
		}
		this.headerCell = headerCell;
	}

	/**
	 * @see
	 * net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableConfiguration
	 * #getHeaderEnd()
	 */
	public String getHeaderEndTag() {
		return headerEndTag;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableConfiguration#setHeaderEndTag(java.lang.String)
	 */
	@Override
	public void setHeaderEndTag(String headerEndTag) {
		checkReadOnly();
		this.headerEndTag = headerEndTag;
	}

	/**
	 * @see
	 * net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableConfiguration
	 * #getRowStartTag()
	 */
	public String getRowStartTag() {
		return rowStartTag;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableConfiguration#setRowStartTag(java.lang.String)
	 */
	@Override
	public void setRowStartTag(String rowStartTag) {
		checkReadOnly();
		this.rowStartTag = rowStartTag;
	}

	/**
	 * @see
	 * net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableConfiguration
	 * #getDataCell()
	 */
	public String getDataCell() {
		return dataCell;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableConfiguration#setDataCell(java.lang.String)
	 */
	@Override
	public void setDataCell(String dataCell) {
		checkReadOnly();
		if(StringUtils.contains(dataCell, VALUE_PLACEHOLDER) == false){
			throw new IllegalArgumentException(s_stringMgr.getString("GenericWikiTableConfigurationBean.dataCellErrorValueVariableMissing")); //$NON-NLS-1$
		}
		this.dataCell = dataCell;
	}

	/**
	 * @see
	 * net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableConfiguration
	 * #getRowEndTag()
	 */
	public String getRowEndTag() {
		return rowEndTag;
	}

	@Override
	public void setRowEndTag(String rowEndTag) {
		checkReadOnly();
		this.rowEndTag = rowEndTag;
	}

	/**
	 * @see
	 * net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableConfiguration
	 * #getTableEndTag()
	 */
	public String getTableEndTag() {
		return tableEndTag;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableConfiguration#setTableEndTag(java.lang.String)
	 */
	@Override
	public void setTableEndTag(String tableEndTag) {
		checkReadOnly();
		this.tableEndTag = tableEndTag;
	}

	

	/**
	 * Creates a instance of {@link GenericWikiTableTransformer}.
	 * @see
	 * net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableConfiguration
	 * #createTransformer()
	 * @see GenericWikiTableTransformer
	 */
	@Override
	public IWikiTableTransformer createTransformer() {
		return new GenericWikiTableTransformer(this);
	}


	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableConfiguration#getNoWikiTag()
	 */
	public String getNoWikiTag() {
		return noWikiTag;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableConfiguration#setNoWikiTag(java.lang.String)
	 */
	@Override
	public void setNoWikiTag(String escapeSequence) {
		checkReadOnly();
		if(StringUtils.contains(escapeSequence, VALUE_PLACEHOLDER) == false){
			throw new IllegalArgumentException(s_stringMgr.getString("GenericWikiTableConfigurationBean.noWikiErrorValueVariableMissing")); //$NON-NLS-1$
		}
		this.noWikiTag = escapeSequence;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableConfiguration#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableConfiguration#setName(java.lang.String)
	 * @throws IllegalArgumentException if the name is null or only contains blanks
	 */
	@Override
	public void setName(String name) {
		checkReadOnly();
		if(StringUtils.isBlank(name)){
			throw new IllegalArgumentException(s_stringMgr.getString("GenericWikiTableConfigurationBean.nameIsRequired")); //$NON-NLS-1$
		}
		this.name = name;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if(StringUtils.isBlank(this.name)){
			return s_stringMgr.getString("GenericWikiTableConfigurationBean.unnamed"); //$NON-NLS-1$
		}else{
			return this.name;
		}
	}

	/**
	 * A generic configuration is not read only.
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableConfiguration#isReadOnly()
	 */
	@Override
	public boolean isReadOnly() {
		return false;
	}

	/**
	 * @see java.lang.Object#clone()
	 * @see IWikiTableConfiguration#clone()
	 */
	@Override
	public  IWikiTableConfiguration clone() {
		return new GenericWikiTableConfigurationBean(this);
	}

	/**
	 * Checks, if changes are allowed, or if the configuration is read-only
	 * @throws IllegalArgumentException if the configuration is read-only
	 */
	private void checkReadOnly(){
		if(initialized && isReadOnly()){
			throw new IllegalArgumentException("A read-only configuration could not be changed!"); //$NON-NLS-1$
		}
	}

	/**
	 * Creates a copy of this configuration as a user-specific one. Whatever, this configuration is a build-in one.
	 * If the current configuration is a build-in one, then the resulting copy may not have the exactly same behavior, because a build-in configuration is
	 * always a sub-class of {@link GenericWikiTableConfigurationBean} 
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableConfiguration#copyAsUserSpecific()
	 */
	@Override
	public IWikiTableConfiguration copyAsUserSpecific() {
		return new GenericWikiTableConfigurationBean(this);
	}


	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableConfiguration#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableConfiguration#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GenericWikiTableConfigurationBean other = (GenericWikiTableConfigurationBean) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	
	
}
