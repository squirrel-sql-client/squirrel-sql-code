package net.sourceforge.squirrel_sql.client.gui.db;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.mainframe.action.CopyDriverAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.CreateDriverAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.DeleteDriverAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.InstallDefaultDriversAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ModifyDriverAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ModifyDriverCommand;
import net.sourceforge.squirrel_sql.client.mainframe.action.ShowDriverWebsiteAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ShowLoadedDriversOnlyAction;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.fw.gui.BasePopupMenu;
import net.sourceforge.squirrel_sql.fw.gui.ToolBar;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import java.awt.event.MouseEvent;

final class DriverUserInterfaceFactory implements IUserInterfaceFactory
{

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DriverUserInterfaceFactory.class);


   private IApplication _app;
   private DriversList _driversList;
   private ToolBar _tb;
   private BasePopupMenu _pm = new BasePopupMenu();
   private DriversListInternalFrame _tw;

   DriverUserInterfaceFactory(DriversList list)
   {
      if (list == null)
      {
         throw new IllegalArgumentException("DriversList == null");
      }

      _app = Main.getApplication();
//			_driversList = new DriversList(app);
      _driversList = list;

      final ActionCollection actions = _app.getActionCollection();
      addToPopup(actions.get(CreateDriverAction.class), _pm);
      _pm.addSeparator();
      addToPopup(actions.get(ModifyDriverAction.class), _pm);
      addToPopup(actions.get(CopyDriverAction.class), _pm);
      addToPopup(actions.get(ShowDriverWebsiteAction.class), _pm);
      _pm.addSeparator();
      addToPopup(actions.get(DeleteDriverAction.class), _pm);
      _pm.addSeparator();
   }

   private void addToPopup(Action action, BasePopupMenu popup)
   {
      _app.getResources().configureMenuItem(action, popup.add(action));
   }

   public ICommand getDoubleClickCommand(MouseEvent evt)
   {
      ICommand cmd = null;
      ISQLDriver driver = _driversList.getSelectedDriver();
      if (driver != null)
      {
         cmd = new ModifyDriverCommand(_app, driver);
      }
      return cmd;
   }


   void setDriversListInternalFrame(DriversListInternalFrame tw)
   {
      _tw = tw;
      propertiesChanged(null);
   }

   public void propertiesChanged(String propName)
   {
      if (propName == null ||
            propName.equals(SquirrelPreferences.IPropertyNames.SHOW_DRIVERS_TOOL_BAR))
      {
         boolean show = _app.getSquirrelPreferences().getShowDriversToolBar();
         if (show)
         {
            createToolBar();
         }
         else
         {
            _tb = null;
         }
         _tw.setToolBar(getToolBar());
      }
   }

   private void createToolBar()
   {
      _tb = new ToolBar();
      _tb.setUseRolloverButtons(true);
      _tb.setFloatable(false);

      if (_app.getDesktopStyle().isInternalFrameStyle())
      {
         final JLabel lbl = new JLabel(getWindowTitle(), SwingConstants.CENTER);
         lbl.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
         _tb.add(lbl, 0);
      }

      final ActionCollection actions = _app.getActionCollection();
      _tb.add(actions.get(CreateDriverAction.class));
      _tb.add(actions.get(ModifyDriverAction.class));
      _tb.add(actions.get(CopyDriverAction.class));
      _tb.add(actions.get(ShowDriverWebsiteAction.class));
      _tb.add(actions.get(DeleteDriverAction.class));
      _tb.addSeparator();
      _tb.add(actions.get(InstallDefaultDriversAction.class));
      _tb.addSeparator();
//			_tb.add(actions.get(ShowLoadedDriversOnlyAction.class));

      final Action act = actions.get(ShowLoadedDriversOnlyAction.class);
      final JToggleButton btn = new JToggleButton(act);
      final boolean show = _app.getSquirrelPreferences().getShowLoadedDriversOnly();
      btn.setSelected(show);
      btn.setText(null);
      _tb.add(btn);
   }

   public ToolBar getToolBar()
   {
      return _tb;
   }

   public BasePopupMenu getPopupMenu()
   {
      return _pm;
   }

   public IBaseList getList()
   {
      return _driversList;
   }

   public String getWindowTitle()
   {
      return s_stringMgr.getString("DriversListInternalFrame.windowtitle");
   }

   public DriversList getDriversList()
   {
      return _driversList;
   }
}
