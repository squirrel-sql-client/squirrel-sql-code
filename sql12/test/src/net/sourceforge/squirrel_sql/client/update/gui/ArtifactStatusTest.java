/*
 * Copyright (C) 2008 Rob Manning
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
package net.sourceforge.squirrel_sql.client.update.gui;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.update.UpdateUtil;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ArtifactXmlBean;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.EasyMockHelper;

import com.gargoylesoftware.base.testing.EqualsTester;

public class ArtifactStatusTest extends BaseSQuirreLJUnit4TestCase
{

	private ArtifactStatus classUnderTest = null;

	EasyMockHelper mockHelper = new EasyMockHelper();

	/* Common mock objects */

	ArtifactXmlBean mockCoreArtifactXmlBean = null;

	ArtifactXmlBean mockPluginArtifactXmlBean = null;

	ArtifactXmlBean mockTranslationArtifactXmlBean = null;

	// ArtifactXmlBean

	/* Data */
	private final String coreArtifactName = "aCoreArtifactName";

	private final boolean coreIsInstalled = true;

	private final long coreSize = 10000L;

	private final long coreChecksum = 10000L;

	private final String coreType = UpdateUtil.CORE_ARTIFACT_ID;

	private final String pluginArtifactName = "aPluginArtifactName";

	private final boolean pluginIsInstalled = false;

	private final long pluginSize = 20000L;

	private final long pluginChecksum = 20000L;

	private final String pluginType = UpdateUtil.PLUGIN_ARTIFACT_ID;

	private final String translationArtifactName = "aTranslationArtifactName";

	private final boolean translationIsInstalled = false;

	private final long translationSize = 30000L;

	private final long translationChecksum = 30000L;

	private final String translationType = UpdateUtil.TRANSLATION_ARTIFACT_ID;

	@Before
	public void setUp() throws Exception
	{
		mockCoreArtifactXmlBean = mockHelper.createMock(ArtifactXmlBean.class);
		expect(mockCoreArtifactXmlBean.getName()).andStubReturn(coreArtifactName);
		expect(mockCoreArtifactXmlBean.isInstalled()).andStubReturn(coreIsInstalled);
		expect(mockCoreArtifactXmlBean.getSize()).andStubReturn(coreSize);
		expect(mockCoreArtifactXmlBean.getChecksum()).andStubReturn(coreChecksum);
		expect(mockCoreArtifactXmlBean.getType()).andStubReturn(coreType);

		mockPluginArtifactXmlBean = mockHelper.createMock(ArtifactXmlBean.class);
		expect(mockPluginArtifactXmlBean.getName()).andStubReturn(pluginArtifactName);
		expect(mockPluginArtifactXmlBean.isInstalled()).andStubReturn(pluginIsInstalled);
		expect(mockPluginArtifactXmlBean.getSize()).andStubReturn(pluginSize);
		expect(mockPluginArtifactXmlBean.getChecksum()).andStubReturn(pluginChecksum);
		expect(mockPluginArtifactXmlBean.getType()).andStubReturn(pluginType);

		mockTranslationArtifactXmlBean = mockHelper.createMock(ArtifactXmlBean.class);
		expect(mockTranslationArtifactXmlBean.getName()).andStubReturn(translationArtifactName);
		expect(mockTranslationArtifactXmlBean.isInstalled()).andStubReturn(translationIsInstalled);
		expect(mockTranslationArtifactXmlBean.getSize()).andStubReturn(translationSize);
		expect(mockTranslationArtifactXmlBean.getChecksum()).andStubReturn(translationChecksum);
		expect(mockTranslationArtifactXmlBean.getType()).andStubReturn(translationType);
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Test
	public void testEqualsAndHashCode()
	{
		mockHelper.replayAll();
		final ArtifactStatus a = new ArtifactStatus(mockCoreArtifactXmlBean); // original object
		final ArtifactStatus b = new ArtifactStatus(mockCoreArtifactXmlBean); // another object that has the
																										// same values as the original
		final ArtifactStatus c = new ArtifactStatus(mockPluginArtifactXmlBean); // another object with different
																										// values
		final ArtifactStatus d = new ArtifactStatus(mockCoreArtifactXmlBean)
		{
			private static final long serialVersionUID = 1L;
		}; // a subclass of Foo with the same values as the original
		new EqualsTester(a, b, c, d);
		mockHelper.verifyAll();

		a.setName(null);
		b.setName(null);
		d.setName(null);
		new EqualsTester(a, b, c, d);

		// different only by type - same name
		a.setName(coreArtifactName);
		b.setName(coreArtifactName);
		c.setName(coreArtifactName);
		a.setType(null);
		b.setType(null);
		d.setType(null);
		new EqualsTester(a, b, c, d);

		a.setType(coreType);
		b.setType(coreType);
		c.setType(null);
		d.setType(coreType);
		new EqualsTester(a, b, c, d);

		a.setType(null);
		b.setType(null);
		c.setType(pluginType);
		d.setType(null);
		new EqualsTester(a, b, c, d);

	}

	@Test
	public void testArtifactStatus()
	{
		classUnderTest = new ArtifactStatus();
	}

	@Test
	public void testArtifactStatusArtifactXmlBean()
	{
		mockHelper.replayAll();
		classUnderTest = new ArtifactStatus(mockCoreArtifactXmlBean);
		mockHelper.verifyAll();

		assertEquals(coreArtifactName, classUnderTest.getName());
		assertEquals(coreIsInstalled, classUnderTest.isInstalled());
		assertEquals(this.coreSize, classUnderTest.getSize());
		assertEquals(coreChecksum, classUnderTest.getChecksum());
	}

	@Test
	public void testSetGetName()
	{
		mockHelper.replayAll();
		classUnderTest = new ArtifactStatus(mockCoreArtifactXmlBean);
		mockHelper.verifyAll();

		assertEquals(coreArtifactName, classUnderTest.getName());
		classUnderTest.setName(pluginArtifactName);
		assertEquals(pluginArtifactName, classUnderTest.getName());
	}

	@Test
	public void testSetGetType()
	{
		mockHelper.replayAll();
		classUnderTest = new ArtifactStatus(mockCoreArtifactXmlBean);
		mockHelper.verifyAll();

		assertEquals(UpdateUtil.CORE_ARTIFACT_ID, classUnderTest.getType());
		classUnderTest.setType(UpdateUtil.PLUGIN_ARTIFACT_ID);
		assertEquals(UpdateUtil.PLUGIN_ARTIFACT_ID, classUnderTest.getType());
	}

	@Test
	public void testIsCoreArtifact()
	{
		mockHelper.replayAll();
		classUnderTest = new ArtifactStatus(mockCoreArtifactXmlBean);
		mockHelper.verifyAll();

		assertTrue(classUnderTest.isCoreArtifact());
		assertFalse(classUnderTest.isPluginArtifact());
		assertFalse(classUnderTest.isTranslationArtifact());
	}

	@Test
	public void testIsPluginArtifact()
	{
		mockHelper.replayAll();
		classUnderTest = new ArtifactStatus(mockPluginArtifactXmlBean);
		mockHelper.verifyAll();

		assertTrue(classUnderTest.isPluginArtifact());
		assertFalse(classUnderTest.isCoreArtifact());
		assertFalse(classUnderTest.isTranslationArtifact());
	}

	@Test
	public void testIsTranslationArtifact()
	{
		mockHelper.replayAll();
		classUnderTest = new ArtifactStatus(mockTranslationArtifactXmlBean);
		mockHelper.verifyAll();

		assertTrue(classUnderTest.isTranslationArtifact());
		assertFalse(classUnderTest.isPluginArtifact());
		assertFalse(classUnderTest.isCoreArtifact());
	}

	@Test
	public void testSetGetInstalled()
	{
		mockHelper.replayAll();
		classUnderTest = new ArtifactStatus(mockCoreArtifactXmlBean);
		mockHelper.verifyAll();

		assertEquals(coreIsInstalled, classUnderTest.isInstalled());
		classUnderTest.setInstalled(false);
		assertFalse(classUnderTest.isInstalled());
	}

	@Test
	public void testSetGetArtifactAction()
	{
		mockHelper.replayAll();
		classUnderTest = new ArtifactStatus(mockCoreArtifactXmlBean);
		mockHelper.verifyAll();

		classUnderTest.setArtifactAction(ArtifactAction.INSTALL);
		assertEquals(ArtifactAction.INSTALL, classUnderTest.getArtifactAction());
	}

	@Test
	public void testToString()
	{
		mockHelper.replayAll();
		classUnderTest = new ArtifactStatus(mockCoreArtifactXmlBean);
		mockHelper.verifyAll();

		assertNotNull(classUnderTest.toString());
		assertTrue(classUnderTest.toString().length() > 0);
	}

	@Test
	public void testSetGetSize()
	{
		mockHelper.replayAll();
		classUnderTest = new ArtifactStatus(mockCoreArtifactXmlBean);
		mockHelper.verifyAll();

		assertEquals(coreSize, classUnderTest.getSize());
		classUnderTest.setSize(2);
		assertEquals(2, classUnderTest.getSize());
	}

	@Test
	public void testSetGetChecksum()
	{
		mockHelper.replayAll();
		classUnderTest = new ArtifactStatus(mockCoreArtifactXmlBean);
		mockHelper.verifyAll();

		assertEquals(coreChecksum, classUnderTest.getChecksum());
		classUnderTest.setChecksum(2);
		assertEquals(2, classUnderTest.getChecksum());
	}

	@Test
	public void testSetGetDisplayType()
	{
		mockHelper.replayAll();
		classUnderTest = new ArtifactStatus(mockCoreArtifactXmlBean);
		mockHelper.verifyAll();

		classUnderTest.setDisplayType(coreType);
		assertEquals(coreType, classUnderTest.getDisplayType());
	}

}
