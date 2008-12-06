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

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;

public class UpdateSummaryTableModelTest extends AbstractTableModelTest
{
	@Before
	public void setUp() throws Exception
	{
		List<ArtifactStatus> artifacts = new ArrayList<ArtifactStatus>();
		ArtifactStatus mockStatus = getArtifactStatus("testCoreArtifactName", "testArtifactType", true, true);
		ArtifactStatus mockStatus2 = getArtifactStatus("testPluginArtifactName", "testArtifactType", false, false);		
		artifacts.add(mockStatus);
		artifacts.add(mockStatus2);
		mockHelper.replayAll();
		classUnderTest = new UpdateSummaryTableModel(artifacts);
		editableColumns = new int[] { 3 };
	}

	/**
    * @param artifactName
	 * @param artifactType
	 * @param isInstalled
	 * @param isCoreArtifact
	 * @return
    */
   private ArtifactStatus getArtifactStatus(String artifactName, String artifactType, boolean isInstalled, boolean isCoreArtifact)
   {
	   ArtifactStatus mockStatus = mockHelper.createMock(ArtifactStatus.class);
		expect(mockStatus.getName()).andStubReturn(artifactName);
		expect(mockStatus.getType()).andStubReturn(artifactType);
		expect(mockStatus.isInstalled()).andStubReturn(isInstalled);
		expect(mockStatus.isCoreArtifact()).andStubReturn(isCoreArtifact);
		expect(mockStatus.getArtifactAction()).andStubReturn(ArtifactAction.INSTALL);
	   return mockStatus;
   }

}
