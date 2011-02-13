/*
 * Copyright (C) 2011 Rob Manning
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

package net.sourceforge.squirrel_sql.plugins.dbdiff.gui;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import net.sourceforge.squirrel_sql.plugins.dbdiff.SessionInfoProvider;
import net.sourceforge.squirrel_sql.plugins.dbdiff.prefs.DBDiffPreferenceBean;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DiffPresentationFactoryImplTest
{

	DiffPresentationFactoryImpl classUnderTest = null;

	@Mock
	private SessionInfoProvider mockSessionInfoProvider;

	@Mock
	private DBDiffPreferenceBean mockPreferenceBean;

	@Before
	public void setUp()
	{
		classUnderTest = new DiffPresentationFactoryImpl();
	}

	@After
	public void tearDown()
	{
		classUnderTest = null;
	}

	@Test
	public void testCreateDiffPresentation()
	{
		when(mockPreferenceBean.isUseTabularDiffPresenation()).thenReturn(true);
		when(mockPreferenceBean.isUseExternalGraphicalDiffTool()).thenReturn(false);

		IDiffPresentation result =
			classUnderTest.createDiffPresentation(mockSessionInfoProvider, mockPreferenceBean);

		assertTrue(result instanceof TabularDiffPresentation);

		when(mockPreferenceBean.isUseTabularDiffPresenation()).thenReturn(false);
		when(mockPreferenceBean.isUseExternalGraphicalDiffTool()).thenReturn(true);

		result = classUnderTest.createDiffPresentation(mockSessionInfoProvider, mockPreferenceBean);

		assertTrue(result instanceof ExternalToolSideBySideDiffPresentation);

		when(mockPreferenceBean.isUseTabularDiffPresenation()).thenReturn(false);
		when(mockPreferenceBean.isUseExternalGraphicalDiffTool()).thenReturn(false);

		result = classUnderTest.createDiffPresentation(mockSessionInfoProvider, mockPreferenceBean);

		assertTrue(result instanceof JMeldDiffPresentation);

	}

}
