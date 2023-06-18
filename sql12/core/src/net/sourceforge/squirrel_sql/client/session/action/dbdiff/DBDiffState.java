package net.sourceforge.squirrel_sql.client.session.action.dbdiff;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.dbdiff.prefs.DBDiffPreferenceBean;
import net.sourceforge.squirrel_sql.client.session.action.dbdiff.tableselectiondiff.TableSelectionDiffUtil;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.JsonMarshalUtil;

import java.nio.file.Path;

public class DBDiffState
{
   private Path _selectedTableCellsMarkdownTempFile;
   private Path _selectedTableCellsRawSingleColumnDataTempFile;
   private ISession _sourceSession;
   private ISession _destSession;
   private IDatabaseObjectInfo[] _destSelectedDatabaseObjects;
   private IDatabaseObjectInfo[] _sourceSelectedDatabaseObjects;
   private DBDiffPreferenceBean _dbDiffPreferenceBean;

   private DBDiffScriptFileManager _scriptFileManager = new DBDiffScriptFileManager();

   public void storeSelectedTableCellsForMarkdownCompare(String selectedMarkDown)
   {
      storeSelectedTableCellsForCompare(selectedMarkDown, null);
   }

   public void storeSelectedTableCellsForCompare(String selectedMarkDown, String rawSingleColumnData)
   {
      _selectedTableCellsMarkdownTempFile = TableSelectionDiffUtil.createLeftTempFile(selectedMarkDown);

      _selectedTableCellsRawSingleColumnDataTempFile = null;
      if(null != rawSingleColumnData)
      {
         _selectedTableCellsRawSingleColumnDataTempFile = TableSelectionDiffUtil.createLeftTempFile(rawSingleColumnData);
      }
   }

   public Path getSelectedTableCellsMarkdownTempFile()
   {
      return _selectedTableCellsMarkdownTempFile;
   }

   public Path getSelectedTableCellsRawSingleColumnDataTempFile()
   {
      return _selectedTableCellsRawSingleColumnDataTempFile;
   }

   public void setSourceSession(ISession sourceSession)
   {
      _sourceSession = sourceSession;
   }

   public ISession getSourceSession()
   {
      return _sourceSession;
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
