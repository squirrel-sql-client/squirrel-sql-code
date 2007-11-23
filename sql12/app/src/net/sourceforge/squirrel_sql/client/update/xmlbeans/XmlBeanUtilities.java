package net.sourceforge.squirrel_sql.client.update.xmlbeans;

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

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.zip.CRC32;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.client.util.IOUtilities;


/**
 * Utility methods for working with XmlBeans. This will be used mostly for
 * building releases.
 * 
 * @author manningr
 */
public class XmlBeanUtilities {
    
    /**
     * This will create a ChannelXmlBean that describes a release as it is found
     * in the specified directory, using the specified releaseName and version.
     * @param channelName the name of the channel.
     * @param releaseName the name of the release.
     * @param version the version of the release.
     * @param directory the directory to use as the top-level of the release.
     * 
     * @return
     */
    public ChannelXmlBean buildChannelRelease(String channelName, 
                                       String releaseName, 
                                       String version, String directory) {
        ChannelXmlBean result = new ChannelXmlBean();
        result.setName(channelName);
        ReleaseXmlBean releaseBean = new ReleaseXmlBean(releaseName, version);
        releaseBean.setCreateTime(new Date());
        File dir = new File(directory);
        for (File f : dir.listFiles()) {
            System.out.println("Processing module directory: "+f);
            if (f.isDirectory()) {
                // f is a module
                ModuleXmlBean module = new ModuleXmlBean();
                module.setName(f.getName());
                for (File a : f.listFiles()) {
                    
                    String filename = a.getName();
                    System.out.println("Processing artifact file: "+filename);
                    String type = filename.substring(filename.indexOf(".")+1);
                    ArtifactXmlBean artifact = new ArtifactXmlBean();
                    artifact.setName(a.getName());
                    artifact.setType(type);
                    artifact.setVersion(version);
                    artifact.setSize(a.length());
                    artifact.setChecksum(getCheckSum(a));
                    module.addArtifact(artifact);
                }
                releaseBean.addmodule(module);
            }
        }
        result.setCurrentRelease(releaseBean);
        return result;
    }
    
    /**
     * Computes the CRC32 checksum for the specified file.  This doesn't appear
     * to be compatible with cksum.
     * 
     * @param f
     * @return
     */
    public long getCheckSum(File f) {
        CRC32 result = new CRC32();  
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            int b = 0;
            while ((b = fis.read()) != -1) {
                result.update(b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtilities.closeInputStream(fis);
        }
        return result.getValue();
    }
    
    /**
     * This is used by the build script to automate building the release.xml
     * file that describes what is available in a particular release.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        ApplicationArguments.initialize(new String[0]);
        if (args.length != 3) {
            printUsage();
        } else {
            File f = new File(args[2], "release.xml");
            String filename = f.getAbsolutePath();
            if (f.exists()) {
                System.err.println("File "+filename+" appears to already exist");
            } else {
                XmlBeanUtilities util = new XmlBeanUtilities();
                ChannelXmlBean channelBean = 
                    util.buildChannelRelease(args[0], args[0], args[1], args[2]);
                UpdateXmlSerializer serializer = new UpdateXmlSerializer();
                
                
                System.out.println("Writing channel release bean to "+filename);
                serializer.write(channelBean, filename);
            }
        }
        
    }
    
    private static void printUsage() {
        System.err.println("Usage: java XmlBeanUtilities <channel> <version> <directory>");
    }
}
