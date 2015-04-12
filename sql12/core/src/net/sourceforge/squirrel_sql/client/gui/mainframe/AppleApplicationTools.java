package net.sourceforge.squirrel_sql.client.gui.mainframe;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.awt.*;

import static java.lang.Class.forName;

class AppleApplicationTools {

  private final ILogger log = LoggerController.createLogger(AppleApplicationTools.class);

  public boolean isAppleEnvironment()
  {
    try
    {
      forName("com.apple.eawt.Application");
      return true;
    }
    catch (ClassNotFoundException e)
    {
      return false;
    }
  }

  public void setDockIconImage(Image image)
  {
    try
    {
      Class applicationClass = forName("com.apple.eawt.Application");
      Object application = applicationClass.getMethod("getApplication").invoke(applicationClass);
      applicationClass.getMethod("setDockIconImage", Image.class).invoke(application, image);
    }
    catch (Exception e)
    {
      log.debug("Skipping to set application dock icon for Mac OS X, because didn't found 'com.apple.eawt.Application' class.", e);
    }
  }
}
