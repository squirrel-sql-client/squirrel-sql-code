/*
 * Copyright (C) 2007 Rob Manning
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
package net.sourceforge.squirrel_sql.client.update;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import java.util.Set;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.plugin.IPluginManager;
import net.sourceforge.squirrel_sql.client.plugin.PluginInfo;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.EasyMockHelper;

public class UpdateUtilImplTest extends BaseSQuirreLJUnit4TestCase {

   UpdateUtilImpl underTest = null;
   
   IPluginManager mockPluginManager = null;
   EasyMockHelper mockHelper = new EasyMockHelper();
   
   @Before
   public void setUp() throws Exception {
      underTest = new UpdateUtilImpl();
      mockPluginManager = mockHelper.createMock(IPluginManager.class);
      underTest.setPluginManager(mockPluginManager);
   }

   @After
   public void tearDown() throws Exception {
      underTest = null;
   }

   @Test
   public void testGetInstalledPlugins() {
   	
   	PluginInfo[] pluginInfos = new PluginInfo[2];
   	PluginInfo mockPlugin1 = mockHelper.createMock(PluginInfo.class);
   	PluginInfo mockPlugin2 = mockHelper.createMock(PluginInfo.class);
   	EasyMock.expect(mockPlugin1.getInternalName()).andReturn("plugin1");
   	EasyMock.expect(mockPlugin2.getInternalName()).andReturn("plugin2");
   	pluginInfos[0] = mockPlugin1;
   	pluginInfos[1] = mockPlugin2;
   	
   	expect(mockPluginManager.getPluginInformation()).andReturn(pluginInfos);
   	
   	mockHelper.replayAll();
   	Set<String> installedPlugins = underTest.getInstalledPlugins();
   	mockHelper.verifyAll();
   	
   	assertEquals(2, installedPlugins.size());
   }
   

}
