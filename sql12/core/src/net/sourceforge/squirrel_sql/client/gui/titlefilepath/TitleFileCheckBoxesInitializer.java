package net.sourceforge.squirrel_sql.client.gui.titlefilepath;

import net.sourceforge.squirrel_sql.client.session.action.savedsession.SavedSessionUtil;
import net.sourceforge.squirrel_sql.fw.props.Props;

import javax.swing.JCheckBoxMenuItem;
import java.io.File;

public class TitleFileCheckBoxesInitializer
{
   private Boolean _currentStateIsInternallySaved = null;


   public void init(JCheckBoxMenuItem chkMnuShowFileName, JCheckBoxMenuItem chkMnuShowFilePath, File sqlFile)
   {
      if(   null != _currentStateIsInternallySaved
         && _currentStateIsInternallySaved == SavedSessionUtil.isInSavedSessionsDir(sqlFile))
      {
         // Checkboxes are already initialized according to the type of the current sqlFile.
         return;
      }

      _currentStateIsInternallySaved = SavedSessionUtil.isInSavedSessionsDir(sqlFile);

      if(_currentStateIsInternallySaved)
      {
         chkMnuShowFileName.setSelected(Props.getBoolean(TitleFilePathHandler.PREF_KEY_TITLE_FILE_PATH_HANDLER_SHOW_INTERNALLY_SAVED_SESSION_FILE_NAME, false));
         chkMnuShowFilePath.setSelected(Props.getBoolean(TitleFilePathHandler.PREF_KEY_TITLE_FILE_PATH_HANDLER_SHOW_INTERNALLY_SAVED_SESSION_PATH_NAME, false));
      }
      else
      {
         chkMnuShowFileName.setSelected(Props.getBoolean(TitleFilePathHandler.PREF_KEY_TITLE_FILE_PATH_HANDLER_SHOW_FILE_NAME, false));
         chkMnuShowFilePath.setSelected(Props.getBoolean(TitleFilePathHandler.PREF_KEY_TITLE_FILE_PATH_HANDLER_SHOW_PATH_NAME, false));
      }
   }
}
