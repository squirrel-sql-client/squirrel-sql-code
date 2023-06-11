package net.sourceforge.squirrel_sql.client.session.action.dbdiff.tableselectiondiff;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.dbdiff.DBDiffScriptFileManager;
import net.sourceforge.squirrel_sql.client.session.action.dbdiff.prefs.DBDiffPreferenceBean;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.JsonMarshalUtil;

import java.nio.file.Path;

public class DBDiffState
{
   private Path _selectedMarkdownTempFile;
   private ISession _sourceSession;
   private IDatabaseObjectInfo[] _selectedDatabaseObjects;
   private boolean _compareMenuEnabled;
   private ISession _destSession;
   private IDatabaseObjectInfo[] _destSelectedDatabaseObjects;
   private IDatabaseObjectInfo[] _sourceSelectedDatabaseObjects;
   private DBDiffPreferenceBean _dbDiffPreferenceBean;

   private DBDiffScriptFileManager _scriptFileManager = new DBDiffScriptFileManager();

   public void storeSelectedForCompareMarkdown(String selectedMarkDown)
   {
      _selectedMarkdownTempFile = TableSelectionDiffUtil.createLeftTempFile(selectedMarkDown);
   }

   public Path getSelectedMarkdownTempFile()
   {
      return _selectedMarkdownTempFile;
   }

   public void setSourceSession(ISession sourceSession)
   {
      _sourceSession = sourceSession;
   }

   public ISession getSourceSession()
   {
      return _sourceSession;
   }

//   public void setSelectedDatabaseObjects(IDatabaseObjectInfo[] selectedDatabaseObjects)
//   {
//      _selectedDatabaseObjects = selectedDatabaseObjects;
//   }
//
//   public IDatabaseObjectInfo[] getSelectedDatabaseObjects()
//   {
//      return _selectedDatabaseObjects;
//   }

   public void setCompareMenuEnabled(boolean compareMenuEnabled)
   {
      _compareMenuEnabled = compareMenuEnabled;
   }

   public boolean isCompareMenuEnabled()
   {
      return _compareMenuEnabled;
   }

   public ISession getDestSession()
   {
      return _destSession;
   }

   public void setDestSession(ISession destSession)
   {
      _destSession = destSession;
   }

   public void setDestSelectedDatabaseObjects(IDatabaseObjectInfo[] destSelectedDatabaseObjects)
   {
      _destSelectedDatabaseObjects = destSelectedDatabaseObjects;
   }

   public IDatabaseObjectInfo[] getDestSelectedDatabaseObjects()
   {
      return _destSelectedDatabaseObjects;
   }

   public void setSourceSelectedDatabaseObjects(IDatabaseObjectInfo[] sourceSelectedDatabaseObjects)
   {
      _sourceSelectedDatabaseObjects = sourceSelectedDatabaseObjects;
   }

   public IDatabaseObjectInfo[] getSourceSelectedDatabaseObjects()
   {
      return _sourceSelectedDatabaseObjects;
   }

   public DBDiffPreferenceBean getDBDiffPreferenceBean()
   {
      if (null == _dbDiffPreferenceBean)
      {
         _dbDiffPreferenceBean = JsonMarshalUtil.readObjectFromFileSave(new ApplicationFiles().getDBDiffPrefsJsonBeanFile(), DBDiffPreferenceBean.class, new DBDiffPreferenceBean());
      }
      return _dbDiffPreferenceBean;
   }

   public void writeDBDiffPreferenceBean(DBDiffPreferenceBean prefs)
   {
      _dbDiffPreferenceBean = prefs;
      JsonMarshalUtil.writeObjectToFile(new ApplicationFiles().getDBDiffPrefsJsonBeanFile(), prefs);
   }

   public DBDiffScriptFileManager getScriptFileManager()
   {
      return _scriptFileManager;
   }

   public void setScriptFileManager(DBDiffScriptFileManager scriptFileManager)
   {
      _scriptFileManager = scriptFileManager;
   }
}
