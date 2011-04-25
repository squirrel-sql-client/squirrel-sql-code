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

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.hibernate.validator.AssertTrue;
import org.junit.Before;
import org.junit.Test;


/**
 * Test the behavior of the {@link WikiTableConfigurationFactory}
 * @author Stefan Willinger
 *
 */
public class WikiTableConfigurationFactoryTest extends BaseSQuirreLJUnit4TestCase {
	private IWikiTableConfigurationFactory classUnderTest = null;
	
	@Before
	public void setUp() {
		classUnderTest = new WikiTableConfigurationFactory();
	}
	
	/**
	 * Test, if the configs are in the right order.
	 */
	@Test
	public void testGetEmptyConfigurations() {
		List<IWikiTableConfiguration> configurations = classUnderTest.getConfigurations();
		assertNotNull(configurations);
		assertTrue(configurations.isEmpty());
	}
	
	/**
	 * Test, if the configs are in the right order.
	 */
	@Test
	public void testGetConfigurations() {
		
		IWikiTableConfiguration mockConfigA = mockHelper.createMock(IWikiTableConfiguration.class);
		expect(mockConfigA.getName()).andStubReturn("A");
		expect(mockConfigA.isReadOnly()).andStubReturn(true);
		expect(mockConfigA.clone()).andStubReturn(mockConfigA);
		
		IWikiTableConfiguration mockConfigB = mockHelper.createMock(IWikiTableConfiguration.class);
		expect(mockConfigB.getName()).andStubReturn("B");
		expect(mockConfigB.isReadOnly()).andStubReturn(true);
		expect(mockConfigB.clone()).andStubReturn(mockConfigB);
		mockHelper.replayAll();
		
		classUnderTest.addBuilInConfiguration(mockConfigA);
		classUnderTest.addBuilInConfiguration(mockConfigB);
		
		List<IWikiTableConfiguration> configurations = classUnderTest.getConfigurations();
		assertNotNull(configurations);
		assertEquals(2, configurations.size());
		
		// this is the expected order
		assertEquals("A", configurations.get(0).getName());
		assertEquals("B", configurations.get(1).getName());
	}
	
	/**
	 * Test, if the provided configs are really copies.
	 */
	@Test
	public void testGetConfigurationsAsCopy() {
		// Here, the usage of a easyMock object is not recommend.
		IWikiTableConfiguration mockConfig = new GenericWikiTableConfigurationBean("A", "", "", "%V", "", "", "%V", "", "", "%V"); 
			
		List<IWikiTableConfiguration> mocks = new ArrayList<IWikiTableConfiguration>();
		mocks.add(mockConfig);
		
		classUnderTest.replaceUserSpecificConfigurations(mocks);
		
		
		List<IWikiTableConfiguration> configs = classUnderTest.getConfigurations();
		
		List<IWikiTableConfiguration> anotherConfigs = classUnderTest.getConfigurations();
		
		assertNotSame(configs, anotherConfigs);
		assertNotSame(configs.get(0), anotherConfigs.get(0));
	}
	
	
	/**
	 * Test, if a read-only Configuration can be added as a user specific one.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testAddReadOnlyConfigurationAsUserSpecific() {
		IWikiTableConfiguration mockConfig = mockHelper.createMock(IWikiTableConfiguration.class);
		expect(mockConfig.getName()).andStubReturn("B");
		expect(mockConfig.isReadOnly()).andStubReturn(true);
		mockHelper.replayAll();
		
		List<IWikiTableConfiguration> userSpecific = new ArrayList<IWikiTableConfiguration>();
		userSpecific.add(mockConfig);
		classUnderTest.replaceUserSpecificConfigurations(userSpecific);
	}
	
	
	/**
	 * Test, if a user specific configuration can be added, if the name is not unique
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testUserSpecificWithNameConflict() {
		IWikiTableConfiguration mockConfigSystem = mockHelper.createMock(IWikiTableConfiguration.class);
		expect(mockConfigSystem.getName()).andStubReturn("B");
		expect(mockConfigSystem.isReadOnly()).andStubReturn(true);
		expect(mockConfigSystem.clone()).andStubReturn(mockConfigSystem);
		
		
		IWikiTableConfiguration mockConfigUser = mockHelper.createMock(IWikiTableConfiguration.class);
		expect(mockConfigUser.getName()).andStubReturn("B");
		expect(mockConfigUser.isReadOnly()).andStubReturn(false);
		expect(mockConfigUser.clone()).andStubReturn(mockConfigUser);
		
		mockHelper.replayAll();
		
		
		try {
			classUnderTest.addBuilInConfiguration(mockConfigSystem);
		} catch (IllegalArgumentException e) {
			fail("It must be possible, to add the build-in Config.");
		}
		
		// this must fail
		classUnderTest.addBuilInConfiguration(mockConfigUser);
		
		
		
		
	}
	
	/**
	 * Test, if a build-in configuration can be added, if the name is not unique
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testBuildInWithNameConflict() {
		IWikiTableConfiguration mockConfig = mockHelper.createMock(IWikiTableConfiguration.class);
		expect(mockConfig.getName()).andStubReturn("NewConfig");
		expect(mockConfig.isReadOnly()).andStubReturn(true);
		expect(mockConfig.clone()).andStubReturn(mockConfig);
		mockHelper.replayAll();

		classUnderTest.addBuilInConfiguration(mockConfig);
		classUnderTest.addBuilInConfiguration(mockConfig);
		// otherwise we got an exception
	}
	
	/**
	 * Test, if a user specific configuration can be added as a build-in one.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testUserSpecificAsBuildIn() {
		classUnderTest.addBuilInConfiguration(new GenericWikiTableConfigurationBean());
		// otherwise we got an exception
	}
	
	/**
	 * Test, if a read-only configuration can be added as a build-in one.
	 */
	@Test()
	public void testAddBuildInConfiguration() {
		IWikiTableConfiguration mockConfig = mockHelper.createMock(IWikiTableConfiguration.class);
		expect(mockConfig.getName()).andStubReturn("NewConfig");
		expect(mockConfig.isReadOnly()).andReturn(true);
		expect(mockConfig.clone()).andReturn(mockConfig);
		mockHelper.replayAll();
		
		classUnderTest.addBuilInConfiguration(mockConfig);
		// otherwise we got an exception
	}
	
	/**
	 * Test, if a read-only configuration can be added as a build-in one.
	 */
	@Test()
	public void testReplaceBuildInConfiguration() {
		IWikiTableConfiguration mockConfigAClone = mockHelper.createMock(IWikiTableConfiguration.class);
		expect(mockConfigAClone.getName()).andStubReturn("A");
		expect(mockConfigAClone.clone()).andReturn(mockConfigAClone);
		expect(mockConfigAClone.clone()).andReturn(mockConfigAClone);
		
		IWikiTableConfiguration mockConfigA = mockHelper.createMock(IWikiTableConfiguration.class);
		expect(mockConfigA.getName()).andStubReturn("A");
		expect(mockConfigA.isReadOnly()).andReturn(true);
		expect(mockConfigA.clone()).andReturn(mockConfigAClone);
		
		IWikiTableConfiguration mockConfigBClone = mockHelper.createMock(IWikiTableConfiguration.class);
		expect(mockConfigBClone.getName()).andStubReturn("B");
		expect(mockConfigBClone.clone()).andReturn(mockConfigBClone);
		
		IWikiTableConfiguration mockConfigB = mockHelper.createMock(IWikiTableConfiguration.class);
		expect(mockConfigB.getName()).andStubReturn("B");
		expect(mockConfigB.isReadOnly()).andReturn(true);
		expect(mockConfigB.clone()).andReturn(mockConfigBClone);
		expect(mockConfigB.clone()).andReturn(mockConfigBClone);
		mockHelper.replayAll();
		
		
		
		classUnderTest.addBuilInConfiguration(mockConfigA);
		
		List<IWikiTableConfiguration> buildInConfigurations = classUnderTest.getBuildInConfigurations();
		assertEquals(1, buildInConfigurations.size());
		
		buildInConfigurations.clear();
		assertEquals(1, classUnderTest.getBuildInConfigurations().size());
		buildInConfigurations.add(mockConfigB);
		classUnderTest.replaceBuilInConfiguration(buildInConfigurations);
		
		buildInConfigurations = classUnderTest.getBuildInConfigurations();
		assertEquals(1,buildInConfigurations.size());
		assertSame(mockConfigBClone, buildInConfigurations.get(0));
	}
	
	@Test
	public void testMerge() throws Exception {
		IWikiTableConfiguration mockConfigA = mockHelper.createMock(IWikiTableConfiguration.class);
		expect(mockConfigA.getName()).andStubReturn("A");
		
		
		IWikiTableConfiguration mockConfigBSecondary = mockHelper.createMock(IWikiTableConfiguration.class);
		expect(mockConfigBSecondary.getName()).andStubReturn("B");
		
		IWikiTableConfiguration mockconfigASecondary = mockHelper.createMock(IWikiTableConfiguration.class);
		expect(mockconfigASecondary.getName()).andStubReturn("A");
		
		mockHelper.replayAll();
		
		List<IWikiTableConfiguration> primary = new ArrayList<IWikiTableConfiguration>();
		primary.add(mockConfigA);
		
		List<IWikiTableConfiguration> secondary = new ArrayList<IWikiTableConfiguration>();
		secondary.add(mockconfigASecondary);
		secondary.add(mockConfigBSecondary);
		
		
		List<IWikiTableConfiguration> merged = WikiTableConfigurationFactory.merge(primary, secondary);
		
		assertEquals(1, primary.size());
		assertEquals(2, secondary.size());
		assertEquals(2, merged.size());
	}
	
}
