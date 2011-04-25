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

import java.beans.BeanInfo;
import java.beans.Introspector;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for the BeanInfo of {@link MediaWikiTableConfigurationBeanInfo}
 * @author Stefan Willinger
 *
 */
public class MediaWikiTableConfigurationBeanInfoTest {

	/**
	 * Ensures, that only the property <code>enabled</code> is shown by the BeanInfo. All other properties are transient, because they are read only.
	 */
	@Test
	public void testBeanInfo() throws Exception {
		BeanInfo beanInfo = Introspector.getBeanInfo(MediaWikiTableConfiguration.class, Object.class);
		assertNotNull(beanInfo);
		assertEquals(1, beanInfo.getPropertyDescriptors().length);
		assertEquals("enabled", beanInfo.getPropertyDescriptors()[0].getName());
	}
}
