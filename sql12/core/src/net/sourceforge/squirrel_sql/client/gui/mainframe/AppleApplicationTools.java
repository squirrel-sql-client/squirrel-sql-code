package net.sourceforge.squirrel_sql.client.gui.mainframe;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.awt.*;

class AppleApplicationTools {

  private final ILogger log = LoggerController.createLogger(AppleApplicationTools.class);

  public boolean isAppleEnvironment()
  {
    try
    {
      Class.forName("com.apple.eawt.Application");
      return true;
    }
    catch (ClassNotFoundException e)
    {
      log.debug("Skipping to set application dock icon for Mac OS X, because didn't found 'com.apple.eawt.Application' class.");
    }
    return false;
  }

  public void setDockIconImage(Image image)
  {
    com.apple.eawt.Application app = com.apple.eawt.Application.getApplication();
    app.setDockIconImage(image);
  }
}
