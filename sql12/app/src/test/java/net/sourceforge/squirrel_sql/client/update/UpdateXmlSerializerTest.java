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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ArtifactXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChannelXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ModuleXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ReleaseXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.UpdateXmlSerializer;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.UpdateXmlSerializerImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UpdateXmlSerializerTest extends BaseSQuirreLJUnit4TestCase {

    UpdateXmlSerializer serializerUnderTest = null;
    ArtifactXmlBean squirrel_jar = null;
    ArtifactXmlBean fw_jar = null;
    ChannelXmlBean channelBean = null;
    ReleaseXmlBean releaseBean = null;
    
    @Before
    public void setUp() throws Exception {
        serializerUnderTest = new UpdateXmlSerializerImpl();
        setupXmlBeans();
    }

    @After
    public void tearDown() throws Exception {
        serializerUnderTest = null;
        squirrel_jar = null;
        fw_jar = null;
    }
    
    @Test
    public void testWriteAndReadSuccessfully() throws Exception  {
        File f = File.createTempFile("Test-ReleaseXmlSerializerTest", ".xml");
        String filename = f.getAbsolutePath();
        assertTrue(f.canRead());
        assertTrue(f.canWrite());
        //System.out.println("Persisting to file : "+filename);
        serializerUnderTest.write(channelBean, filename);
        ChannelXmlBean newBean = serializerUnderTest.readChannelBean(filename);
        assertEquals(channelBean, newBean);
        
        assertEquals(1, newBean.getCurrentRelease().getModules().size());
        assertEquals(2, newBean.getCurrentRelease().getModules().iterator().next().getArtifacts().size());
        
        //printFile(filename);
        if (!f.delete()) {
            System.err.println("Failed to delete file");
        }
    }

    /* Helper methods */
    
    private void printFile(String filename) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filename));
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try { 
                    reader.close(); 
                } catch (IOException e) { 
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * This will produce the objects that create a Release XML document.
     */
    private void setupXmlBeans() {
        String channel = "snashot";
        String release = "Snapshot-20070922_1258";
        
        releaseBean = new ReleaseXmlBean("snapshot", release);
        
        releaseBean.addmodule(getCoreModule(release));
        
        channelBean = new ChannelXmlBean();
        channelBean.setName(channel);
        channelBean.setCurrentRelease(releaseBean);
    }
    
    private ModuleXmlBean getCoreModule(String release) {
        squirrel_jar = new ArtifactXmlBean("squirrel-sql.jar", "jar",
                release, 3048902, 322169161);
        fw_jar = new ArtifactXmlBean("fw.jar", "jar", release,
                1707852, 2069219788);
        ModuleXmlBean core = new ModuleXmlBean();
        core.addArtifact(squirrel_jar);
        core.addArtifact(fw_jar);     
        return core;
    }
}
