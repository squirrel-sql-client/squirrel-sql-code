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
package net.sourceforge.squirrel_sql.plugins.wikiTableConfiguration.configurations;

import net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.GenericWikiTableConfigurationBean;
import net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableConfiguration;

/**
 * A build in configuration for a MediaWiki table.
 * @author Stefan Willinger
 *
 */
public class MediaWikiTableConfiguration extends GenericWikiTableConfigurationBean {

	private static final long serialVersionUID = -6873210947889098473L;
	
	private static final String NAME = "Media Wiki";
	private static final String TABLE_START = "{| border=\"1\"" + NEW_LINE_PLACEHOLDER ;
	private static final String HEADER_START = "";
	private static final String HEADER_CELL = "! "+VALUE_PLACEHOLDER+" " + NEW_LINE_PLACEHOLDER ;
	private static final String HEADER_END = "";
	private static final String ROW_START ="|-"+ NEW_LINE_PLACEHOLDER ;
	private static final String DATA_CELL ="| "+VALUE_PLACEHOLDER +" "+ NEW_LINE_PLACEHOLDER ;
	private static final String ROW_END ="";
	private static final String TABLE_END ="|}";
	private static final String ESCAPE_SEQUENCE = "<nowiki>"+VALUE_PLACEHOLDER+"</nowiki>";
	
	
	public MediaWikiTableConfiguration() {
		super(NAME, TABLE_START, HEADER_START, HEADER_CELL, HEADER_END, ROW_START, DATA_CELL, ROW_END, TABLE_END, ESCAPE_SEQUENCE);
	}


	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.GenericWikiTableConfigurationBean#isReadOnly()
	 */
	@Override
	public boolean isReadOnly() {
		return true;
	}


	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.GenericWikiTableConfigurationBean#clone()
	 */
	@Override
	public IWikiTableConfiguration clone(){
		 MediaWikiTableConfiguration config = new MediaWikiTableConfiguration();
		 config.setEnabled(isEnabled());
		 return config;
	}
	
	
	
	

}
