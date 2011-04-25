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

import net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.AbstractWikiTableExporterTest;
import net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.GenericWikiTableTransformer;

import org.junit.Before;

/**
 * Test the configuration for MediaWiki
 * @author Stefan Willinger
 *
 */
public class MediaWikiTableConfigurationBeanTest extends AbstractWikiTableExporterTest<MediaWikiTableConfiguration>{

	
	@Before
	public void setUp(){
		classUnderTest = new MediaWikiTableConfiguration();
		super.setUp();
	}
	
	
	@Override
	protected String createExpectedTextForFullSelection() {
		String expected = "{| border=\"1\""+GenericWikiTableTransformer.NEW_LINE +
		"! <nowiki>Country</nowiki> " +GenericWikiTableTransformer.NEW_LINE +
		"! <nowiki>Capital</nowiki> "+GenericWikiTableTransformer.NEW_LINE +
		"|-"+GenericWikiTableTransformer.NEW_LINE +
		"| <nowiki>Austria</nowiki> "+GenericWikiTableTransformer.NEW_LINE +
		"| <nowiki>Vienna</nowiki> "+GenericWikiTableTransformer.NEW_LINE +
		"|-"+GenericWikiTableTransformer.NEW_LINE +
		"| <nowiki>Italy</nowiki> "+GenericWikiTableTransformer.NEW_LINE +
		"| <nowiki>Rome</nowiki> "+GenericWikiTableTransformer.NEW_LINE +
		"|}";
		return expected;
	}
	

}
