/*
 * MacOSPlugin.java
 *
 * Created on March 18, 2004, 11:17 AM
 */

package net.sourceforge.squirrel_sql.plugins.macosx;

import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;
import com.apple.eawt.Application;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.mainframe.action.AboutCommand;
import net.sourceforge.squirrel_sql.client.mainframe.action.GlobalPreferencesCommand;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.DefaultPlugin;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 *
 * @author  rowen
 */
public class MacOSPlugin extends DefaultPlugin {
    
    private final static ILogger s_log = LoggerController.createLogger(MacOSPlugin.class);

    public String getAuthor() {
        return("Neville Rowe");
    }
    
    public String getDescriptiveName() {
        return("Mac OS User Interface");
    }
    
    public String getInternalName() {
        return("macosx");
    }
    
    public String getVersion() {
        return("0.1");
    }

    public String getChangeLogFileName()
    {
            return "changes.txt";
    }

    public String getHelpFileName()
    {
            return "readme.txt";
    }

    public String getLicenceFileName()
    {
            return "licence.txt";
    }

    public synchronized void load(IApplication app) throws PluginException
    {
    	super.load(app);
    }

    public synchronized void initialize() throws PluginException
    {
        super.initialize();
        final IApplication app = getApplication();
        Application fApplication = Application.getApplication();
        fApplication.addApplicationListener(new com.apple.eawt.ApplicationAdapter() {
            public void handleAbout(ApplicationEvent e) {
                e.setHandled(true);
                new AboutCommand(app).execute();
            }
            public void handleOpenApplication(ApplicationEvent e) {
            }
            public void handleOpenFile(ApplicationEvent e) {
            }
            public void handlePreferences(ApplicationEvent e) {
                e.setHandled(true);
                new GlobalPreferencesCommand(app).execute();
            }
            public void handlePrintFile(ApplicationEvent e) {
            }
            public void handleQuit(ApplicationEvent e) {
                e.setHandled(true);
                app.getMainFrame().dispose();
            }
        });
        fApplication.addPreferencesMenuItem();
        fApplication.setEnabledPreferencesMenu(true);
    }
}
