package net.sourceforge.squirrel_sql.client.action;

import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.ISessionWidget;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.IWidget;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.filemanager.FileHandler;
import net.sourceforge.squirrel_sql.client.session.filemanager.IFileEditorAPI;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreePanel;

public class ActionUpdateHelper
{
   private ISession _session;
   private ISQLPanelAPI _sqlPanelAPI;
   private IFileEditorAPI _fileEditorAPI;
   private ObjectTreePanel _objectTreePanel;
   private IMainPanelTab _selectedMainTab;

   public ActionUpdateHelper(IWidget frame)
   {

      final boolean isSQLFrame = (frame instanceof SQLInternalFrame);
      final boolean isTreeFrame = (frame instanceof ObjectTreeInternalFrame);
      final boolean isSessionInternalFrame = (frame instanceof SessionInternalFrame);

      if (frame instanceof ISessionWidget)
      {
         _session = ((ISessionWidget)frame).getSession();
      }

      if (isSQLFrame)
      {
         _sqlPanelAPI = ((SQLInternalFrame) frame).getSQLPanel().getSQLPanelAPI();
      }

      if (isTreeFrame)
      {
         _objectTreePanel = ((ObjectTreeInternalFrame) frame).getObjectTreePanel();
      }

      if(isSessionInternalFrame)
      {
         _selectedMainTab = _session.getSelectedMainTab();
      }


      if (isSessionInternalFrame)
      {
         SessionInternalFrame sif = (SessionInternalFrame) frame;
         if(sif.getSessionPanel().isAnSQLTabSelected())
         {
            _sqlPanelAPI = sif.getSessionPanel().getSelectedSQLPanel().getSQLPanelAPI();
         }
         else
         {
            _sqlPanelAPI = null;
         }

         if(sif.getSessionPanel().isObjectTreeTabSelected())
         {
            _objectTreePanel = ((SessionInternalFrame)frame).getSessionPanel().getObjectTreePanel();
         }
         else
         {
            _objectTreePanel = null;
         }

         _fileEditorAPI = sif.getActiveIFileEditorAPIOrNull();
      }

      if(null != _sqlPanelAPI && _sqlPanelAPI.getSQLPanelSplitter().isSplit())
      {
         _objectTreePanel = _sqlPanelAPI.getSQLPanelSplitter().getObjectTreePanel();
      }
   }

   public ISession getSession()
   {
      return _session;
   }

   public ISQLPanelAPI getSQLPanelAPI()
   {
      return _sqlPanelAPI;
   }

   public IObjectTreeAPI getObjectTreePanel()
   {
      return _objectTreePanel;
   }

   public IMainPanelTab getSelectedMainTab()
   {
      return _selectedMainTab;
   }

   public FileHandler getFileHandler()
   {
      if (null != _sqlPanelAPI)
      {
         return _sqlPanelAPI.getFileHandler();
      }
      else if(null != _fileEditorAPI)
      {
         return _fileEditorAPI.getFileHandler();
      }

      return null;
   }
}
