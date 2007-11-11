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

import java.util.HashSet;
import java.util.Set;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.plugin.PluginInfo;
import net.sourceforge.squirrel_sql.client.plugin.PluginManager;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChannelXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.XmlBeanUtilities;

/**
 * This class implements the business logic needed by the view 
 * (UpdateManagerDialog), to let the user to install new or updated software.
 * 
 * @author manningr
 */
public class UpdateControllerImpl implements UpdateController {

    public static final String DEFAULT_REPO_HOST = 
        "squirrel-sql.sourceforge.net";
    
    public static final String DEFAULT_REPO_PATH = 
        "releases";

    /**
     * The name of the release xml file that describes the installed version
     */
    public static final String RELEASE_XML_FILENAME = "release.xml";
    
    
    private IApplication _app = null;
    
    private UpdateUtil _util = new UpdateUtil(); 
    
    public UpdateControllerImpl(IApplication app) {
        _app = app;
    }
    
    public boolean isUpdateToDate() {
        boolean result = true;
        
        // 1. Find the local release.xml file
        String releaseFilename = _util.getLocalReleaseFile();
        
        // 2. Load the local release.xml file as a ChannelXmlBean.
        ChannelXmlBean installedBean = 
            _util.getLocalReleaseInfo(releaseFilename);
        
        // 3. Determine the channel that the user has (stable or snapshot)
        String channelName = installedBean.getName();
        
        StringBuilder releasePath = new StringBuilder(DEFAULT_REPO_PATH);
        releasePath.append("/");
        releasePath.append(channelName);
        
        // 4. Get the release.xml file as a ChannelXmlBean from the server
        ChannelXmlBean currentReleaseBean = 
            _util.downloadCurrentRelease(DEFAULT_REPO_HOST, 
                                         releasePath.toString(), 
                                         RELEASE_XML_FILENAME);
        
        // 5. Is it the same as the local copy, which was placed either by the
        // installer or the last update?
        return currentReleaseBean.equals(installedBean);
    }
    
    /**
     * Returns a set of plugins (internal names) of plugins that are currently
     * installed (regardless of whether or not they are enabled).
     * 
     * @return a set of plugin internal names
     */
    public Set<String> getInstalledPlugins() {
        Set<String> result = new HashSet<String>();
        PluginManager pmgr = _app.getPluginManager();
        PluginInfo[] infos = pmgr.getPluginInformation();
        for (PluginInfo info : infos) {
            result.add(info.getInternalName());
        }
        return result;
    }
    
    /**
     * Go get the files that need to be updated.
     * 
     * @return
     */
    public boolean pullDownUpdateFiles() {
        return true;
    }
    
}
