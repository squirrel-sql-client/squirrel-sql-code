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

import javax.swing.JTable;

/**
 * A {@link IWikiTableConfiguration} describes the structure of a table for a
 * specific WIKI engine. There are only mandatory 3 items of the configuration
 * <li>{@link #setHeaderCell(String)}</li> <li>{@link #setDataCell(String)}</li>
 * <li>{@link #setNoWikiTag(String)}</li> All other items maybe null.
 * <p>
 * When a {@link JTable} should be transformed into a WIKI-Table, then the
 * {@link IWikiTableTransformer} will process the configuration as followed:
 * <ul>
 * {@link #getTableStartTag()}
 * </ul>
 * <ul>
 * <ul>
 * {@link #getHeaderStartTag()}
 * </ul>
 * </ul>
 * <ul>
 * <ul>
 * <ul>
 * For each header value {@link #getHeaderCell()}
 * </ul>
 * </ul></ul>
 * <ul>
 * <ul>
 * {@link #getHeaderEndTag()}
 * </ul>
 * </ul>
 * <ul>
 * <ul>
 * For each selected row in the table {@link #getRowStartTag()}
 * </ul>
 * </ul>
 * <ul>
 * <ul>
 * <ul>
 * For each cell of a row {@link #getDataCell()}
 * </ul>
 * </ul></ul>
 * <ul>
 * <ul>
 * {@link #getRowEndTag()}
 * </ul>
 * </ul>
 * <ul>
 * {@link #getTableEndTag()}
 * </ul>
 * 
 * The configuration can, but must not be well formed. This configuration can
 * also be used for exports to HTML.
 * 
 * By using the special tag <code>noWikiTag</code>, a configuration can escape
 * the value with a WIKI specific tag.
 * <p>
 * A configuration can be enabled/disabled. This is the only information of a configuration, that is not protected by {@link #isReadOnly()}
 * <p>
 * A configuration for a WIKI table is intended to be saved into a file.
 * 
 * @author Stefan Willinger
 * 
 */
public interface IWikiTableConfiguration extends Cloneable, Serializable {

	/**
	 * If this string occurs in the configuration, it will be replaced by the
	 * current value. (header or data). This variable is mandatory for
	 * {@link #setHeaderCell(String)}, {@link #setDataCell(String)},
	 * {@link #setNoWikiTag(String)}. Other configuration items doesn't allow
	 * this.
	 */
	public static String VALUE_PLACEHOLDER = "%V";

	/**
	 * If this string occurs in any part of the configuration, it will be
	 * replaced by a new line character. More concrete, it will be replaced by
	 * the value of <code>System.getProperty("line.separator")</code>
	 * 
	 * @see Properties#
	 */
	public static String NEW_LINE_PLACEHOLDER = "%N";

	/**
	 * Tag, which indicates that a table begins.
	 * <p>
	 * Example:
	 * </p>
	 * <li>For HTML <code>&lt;TABLE&gt;</code> <li>For Mediawiki
	 * <code>{| %N</code>
	 * 
	 * @return Tag which should be used for the start of a table.
	 * @see #NEW_LINE_PLACEHOLDER
	 */
	String getTableStartTag();

	/**
	 * Tag, which indicates, that a header row start.
	 * <p>
	 * Example:
	 * </p>
	 * <li>For HTML <code>&lt;TR&gt;</code> <li>For Mediawiki is this not
	 * necessary.
	 * 
	 * @return Tag for starting a header row.
	 * @see #NEW_LINE_PLACEHOLDER
	 */
	String getHeaderStartTag();

	/**
	 * This is the value for a cell of the header. This tag must contain
	 * {@link #VALUE_PLACEHOLDER}.
	 * <p>
	 * Example:
	 * </p>
	 * <li>For HTML <code>&lt;TH&gt;%V&lt;/TH&gt;</code> <li>For Mediawiki
	 * <code>! %v %N</code>
	 * 
	 * @return Tag, which represents a cell of the header
	 * @see #VALUE_PLACEHOLDER
	 * @see #NEW_LINE_PLACEHOLDER
	 */
	String getHeaderCell();

	/**
	 * Tag, which indicates that a header row ends.
	 * <p>
	 * Example:
	 * </p>
	 * <li>For HTML <code>&lt;/TR&gt;</code> <li>For Mediawiki is this not
	 * necessary.
	 * 
	 * @return Tag for the end of a header row.
	 * @see #NEW_LINE_PLACEHOLDER
	 */
	String getHeaderEndTag();

	/**
	 * Tag, which indicates, that a table row start.
	 * <p>
	 * Example:
	 * </p>
	 * <li>For HTML <code>&lt;TR&gt;</code> <li>For Mediawiki <code>|- %N</code>
	 * 
	 * @return Tag for starting a table row.
	 * @see #NEW_LINE_PLACEHOLDER
	 */
	String getRowStartTag();

	/**
	 * This is the value for a cell of the table row. This tag must contain
	 * {@link #VALUE_PLACEHOLDER}.
	 * <p>
	 * Example:
	 * </p>
	 * <li>For HTML <code>&lt;TD&gt;%V&lt;/TD&gt;</code> <li>For Mediawiki
	 * <code>| %V %N</code>
	 * 
	 * @return Tag, which represents a cell in a table row.
	 * @see #VALUE_PLACEHOLDER
	 * @see #NEW_LINE_PLACEHOLDER
	 */
	String getDataCell();

	/**
	 * Tag, which indicates that a table row ends.
	 * <p>
	 * Example:
	 * </p>
	 * <li>For HTML <code>&lt;/TR&gt;</code> <li>For Mediawiki is this not
	 * necessary.
	 * 
	 * @return Tag for the end of a table row.
	 * @see #NEW_LINE_PLACEHOLDER
	 */
	String getRowEndTag();

	/**
	 * Tag, which indicates that a table begins.
	 * <p>
	 * Example:
	 * </p>
	 * <li>For HTML <code>&lt;/TABLE&gt;</code> <li>For Mediawiki
	 * <code>|}</code>
	 * 
	 * @return Tag which should be used for the start of a table.
	 * @see #NEW_LINE_PLACEHOLDER
	 */
	String getTableEndTag();

	/**
	 * A special tag, which prevents the content for being interpreted as WIKI
	 * text. For Mediawiki, this would be
	 * <code>&lt;NOWIKI&gt;%V&lt;/NOWIKI&gt;</code>
	 * 
	 * @return Tag, which prevents the content to be interpreded as WIKI text
	 * @see #VALUE_PLACEHOLDER
	 * @see #NEW_LINE_PLACEHOLDER
	 */
	String getNoWikiTag();

	/**
	 * Creates for the current configuration a suitable transformer for processing the
	 * transforming operation.
	 * 
	 * @return A transformer to transform a JTable into the WIKI table.
	 */
	IWikiTableTransformer createTransformer();

	/**
	 * The name of the configuration. This must be unique.
	 * 
	 * @return
	 */
	String getName();

	/**
	 * Indicates, if the configuration is read only. There are several build-in
	 * configuration for different WIKI engines. These build-in configurations
	 * are read-only.
	 * 
	 * @return true, if the configuration is read-only
	 */
	boolean isReadOnly();

	/**
	 * Set the name of the configuration. The name must not be empty.
	 * 
	 * @see #getName()
	 */
	void setName(String name);

	/**
	 * @see #getNoWikiTag()
	 * @throws IllegalArgumentException
	 *             if the provided string does not contain a variable for the
	 *             value.
	 * @see #VALUE_PLACEHOLDER
	 */
	void setNoWikiTag(String noWikiTag);

	/**
	 * @see #getTableEndTag()
	 */
	void setTableEndTag(String tableEndTag);

	/**
	 * @see #getRowEndTag()
	 */
	void setRowEndTag(String rowEndTag);

	/**
	 * @see #getDataCell()
	 * @throws IllegalArgumentException
	 *             if the provided string does not contain a variable for the
	 *             value.
	 * @see #VALUE_PLACEHOLDER
	 */
	void setDataCell(String dataCell);

	/**
	 * @see #getRowStartTag()
	 */
	void setRowStartTag(String rowStartTag);

	/**
	 * @see #getHeaderEndTag()
	 */
	void setHeaderEndTag(String headerEndTag);

	/**
	 * @throws IllegalArgumentException
	 *             if the provided string does not contain a variable for the
	 *             value.
	 * @see #getHeaderCell()
	 * @see #VALUE_PLACEHOLDER
	 */
	void setHeaderCell(String headerCell);

	/**
	 * @see #getHeaderStartTag()
	 */
	void setHeaderStartTag(String headerStartTag);

	/**
	 * @see #getTableStartTag()
	 */
	void setTableStartTag(String tableStartTag);

	/**
	 * Creates a real copy of the current configuration. The configuration will
	 * be from the same type.
	 * 
	 * @return a copy of the configuration.
	 */
	IWikiTableConfiguration clone();

	/**
	 * Creates a user specific copy of the current configuration. If a build-in
	 * configuration are copied, the resulting configuration will be a user
	 * specific one. The engine of the resulting configuration will be the
	 * default one. Please note, that a build-in configuration may have a other
	 * engine. So the engine's behavior of this two configurations may differ.
	 * 
	 * @return a user specific copy of the configuration.
	 */
	IWikiTableConfiguration copyAsUserSpecific();
	
	
	/**
	 * @return true, if this configuration is enabled. Otherwise false.
	 */
	boolean isEnabled();
	
	/**
	 * Enable or disable this configuration
	 * @param enabled true, if this configuration should be enabled.
	 */
	void setEnabled(boolean enabled);

}
