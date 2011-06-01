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

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Stefan Willinger
 *
 */
public class CopyWikiTableActionFactoryTest extends BaseSQuirreLJUnit4TestCase{

	private CopyWikiTableActionFactory classUnderTest = null;
	private ITableActionCallback mockCallback = null;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		classUnderTest = new CopyWikiTableActionFactory();
		mockHelper.createMock(ITableActionCallback.class);
		mockHelper.replayAll();
	}

	/**
	 * Test if all build-in configurations are used for the menu-tree
	 */
	@Test
	public void testCreateMenueItem() {
		
		mockHelper.resetAll();
		IWikiTableConfigurationFactory mockWikiConfigFactory = mockHelper.createMock(IWikiTableConfigurationFactory.class);
		IWikiTableConfiguration mockConfigA = mockHelper.createMock(IWikiTableConfiguration.class);
		expect(mockConfigA.getName()).andStubReturn("MockConfigA");
		expect(mockConfigA.isEnabled()).andStubReturn(true);
		
		IWikiTableConfiguration mockConfigB = mockHelper.createMock(IWikiTableConfiguration.class);
		expect(mockConfigB.getName()).andStubReturn("MockConfigB");
		expect(mockConfigB.isEnabled()).andStubReturn(true);
		
		List<IWikiTableConfiguration> configList = new ArrayList<IWikiTableConfiguration>();
		configList.add(mockConfigA);
		configList.add(mockConfigB);
		
		expect(mockWikiConfigFactory.getConfigurations()).andReturn(configList);
		
		classUnderTest.setConfigurationFactory(mockWikiConfigFactory);
		mockHelper.replayAll();
		
		
		JMenuItem menuItem = classUnderTest.createMenueItem(mockCallback);
		assertNotNull(menuItem);
		assertEquals(JMenu.class, menuItem.getClass());
		JMenu menu = (JMenu) menuItem;
		assertEquals(2, menu.getItemCount());
	}
	
	/**
	 * If only one wiki-configuration exists, then the factory must create a simple JMenuItem instead of a Sub-Menu.
	 */
	@Test
	public void testSingleConfiguration() {
		mockHelper.resetAll();
		IWikiTableConfigurationFactory mockWikiConfigFactory = mockHelper.createMock(IWikiTableConfigurationFactory.class);
		IWikiTableConfiguration mockConfig = mockHelper.createMock(IWikiTableConfiguration.class);
		expect(mockConfig.getName()).andStubReturn("MockConfig");
		expect(mockConfig.isEnabled()).andStubReturn(true);
		
		List<IWikiTableConfiguration> configList = new ArrayList<IWikiTableConfiguration>();
		configList.add(mockConfig);
		expect(mockWikiConfigFactory.getConfigurations()).andReturn(configList);
		
		classUnderTest.setConfigurationFactory(mockWikiConfigFactory);
		mockHelper.replayAll();
		
		JMenuItem menuItem = classUnderTest.createMenueItem(mockCallback);
		assertNotNull(menuItem);
		assertEquals(JMenuItem.class, menuItem.getClass());
		assertEquals("Copy as WIKI table (MockConfig)", menuItem.getText());
	}	
	
	
	/**
	 * If no wiki-configuration exists, then the factory must create a disabled JMenueItem as hint for the missing configs.
	 */
	@Test
	public void testEmptyConfiguration() {
		mockHelper.resetAll();
		IWikiTableConfigurationFactory mockWikiConfigFactory = mockHelper.createMock(IWikiTableConfigurationFactory.class);
		List<IWikiTableConfiguration> configList = new ArrayList<IWikiTableConfiguration>();
		expect(mockWikiConfigFactory.getConfigurations()).andReturn(configList);
		
		classUnderTest.setConfigurationFactory(mockWikiConfigFactory);
		mockHelper.replayAll();
		
		JMenuItem menuItem = classUnderTest.createMenueItem(mockCallback);
		assertNotNull(menuItem);
		assertEquals(JMenuItem.class, menuItem.getClass());
		assertEquals("Copy as WIKI table", menuItem.getText());
		assertFalse(menuItem.isEnabled());
	}	
	
	/**
	 * Configurations, that are not enabled, are not used by the factory.
	 */
	@Test
	public void testDisabledConfigurations() {
		mockHelper.resetAll();
		IWikiTableConfigurationFactory mockWikiConfigFactory = mockHelper.createMock(IWikiTableConfigurationFactory.class);
		IWikiTableConfiguration mockConfig = mockHelper.createMock(IWikiTableConfiguration.class);
		expect(mockConfig.getName()).andStubReturn("MockConfig");
		expect(mockConfig.isEnabled()).andStubReturn(true);
		
		IWikiTableConfiguration mockConfigDisabled = mockHelper.createMock(IWikiTableConfiguration.class);
		expect(mockConfigDisabled.getName()).andStubReturn("MockConfigDisabled");
		expect(mockConfigDisabled.isEnabled()).andStubReturn(false);
		
		
		List<IWikiTableConfiguration> configList = new ArrayList<IWikiTableConfiguration>();
		configList.add(mockConfig);
		configList.add(mockConfigDisabled);
		
		expect(mockWikiConfigFactory.getConfigurations()).andReturn(configList);
		
		classUnderTest.setConfigurationFactory(mockWikiConfigFactory);
		mockHelper.replayAll();
		
		JMenuItem menuItem = classUnderTest.createMenueItem(mockCallback);
		assertNotNull(menuItem);
		// We expect only a menu item, because one config is disabled
		assertEquals(JMenuItem.class, menuItem.getClass());
	}	

}
