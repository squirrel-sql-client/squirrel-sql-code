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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChannelXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.UpdateXmlSerializer;
import net.sourceforge.squirrel_sql.client.util.IOUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * Utility methods for the UpdateManager.
 * 
 * @author manningr
 */
public class UpdateUtil {

    /** Logger for this class. */
    private final static ILogger s_log = 
        LoggerController.createLogger(UpdateUtil.class);
    
    /** 
     * The protocol we expect the update site to be using. Only HTTP at the 
     * moment
     */
    public static final String HTTP_PROTOCOL_PREFIX = "http";
    
    /** 
     * where we expect to find release.xml, which describes what the 
     * user has installed previously. 
     */
    public static final String LOCAL_UPDATE_DIR_NAME = "update";
        
    /** 
     * the utility class that reads and writes release info from/to the  
     * release.xml file
     */
    private final UpdateXmlSerializer serializer = new UpdateXmlSerializer();
    
    /**
     * Downloads the current release available at the specified host and path.
     *  
     * @param host the host to open an HTTP connection to.
     * @param path the path on the host's webserver to the file.
     * @param fileToGet the file to get.
     * @return
     */
    public ChannelXmlBean downloadCurrentRelease(String host, 
                                                 String path, 
                                                 String fileToGet) {
        ChannelXmlBean result = null;
        BufferedInputStream is = null;
        String pathToFile = path + fileToGet;
        try {
            URL url = new URL(HTTP_PROTOCOL_PREFIX, host, pathToFile);
            is = new BufferedInputStream(url.openStream());
            result = serializer.read(is);
        } catch (Exception e) {
            s_log.error(
                "downloadCurrentRelease: Unexpected exception while " +
                "attempting to open an HTTP connection to host ("+host+") " +
                "to download a file ("+pathToFile+")");
        } finally {
            IOUtilities.closeInputStream(is);
        }
        return result;
    }
    
    /**
     * 
     * @param host
     * @param fileToGet
     * @param destDir
     */
    public boolean downloadHttpFile(String host, 
                                    String path, 
                                    String fileToGet, 
                                    String destDir) {
        boolean result = false;
        BufferedInputStream is = null;
        BufferedOutputStream os = null;
        try {
            URL url = new URL(HTTP_PROTOCOL_PREFIX, host, path + fileToGet);
            is = new BufferedInputStream(url.openStream());
            File localFile = new File(destDir, fileToGet);
            os = new BufferedOutputStream(new FileOutputStream(localFile));
            byte[] buffer = new byte[8192];
            int length = 0;
            while ((length = is.read(buffer)) != -1) {
                os.write(buffer, 0, length);
            }
            result = true;
        } catch (Exception e) {
            s_log.error("Exception encountered while attempting to " +
            		"download file "+fileToGet+" from host "+host+" and path "+
            		path+" to destDir "+destDir+": "+e.getMessage(), e);
        } finally {
            IOUtilities.closeInputStream(is);
            IOUtilities.closeOutpuStream(os); 
        }
        return result;
    }  
    
    /**
     * Returns an ChannelXmlBean that describes the locally installed release.
     * 
     * @param localReleaseFile the xml file to decode into an xmlbean.
     *  
     * @return a ChannelXmlBean
     */
    public ChannelXmlBean getLocalReleaseInfo(String localReleaseFile) {
        ChannelXmlBean result = null;
        if (s_log.isDebugEnabled()) {
            s_log.debug("Attempting to read local release file: "
                    + localReleaseFile);
        }
        try {
            result = serializer.read(localReleaseFile);
        } catch (IOException e) {
            s_log.error("Unable to read local release file: "+e.getMessage(), e);
        }
        return result;
    }
    
    /**
     * Returns the absolute path to the release xml file that describes what 
     * release the user currently has.
     * 
     * @return
     */
    public String getLocalReleaseFile() {
        String result = null;
        try {
            File f = new File(".");
            File[] files = f.listFiles();
            for (File file : files) {
                if ("update".equals(file.getName())) {
                    File[] updateFiles = file.listFiles();
                    for (File updateFile : updateFiles) {
                        if ("release.xml".equals(updateFile.getName())) {
                            result = updateFile.getAbsolutePath();
                        }
                    }
                }
            }
        } catch (Exception e) {
            s_log.error("getLocalReleaseFile: Exception encountered while " +
            		"attempting to find release.xml file");
        }
        return result;
    }
}
