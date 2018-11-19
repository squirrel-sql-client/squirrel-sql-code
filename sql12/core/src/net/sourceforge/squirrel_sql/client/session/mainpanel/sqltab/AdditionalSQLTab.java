package net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.titlefilepath.TitleFilePathHandler;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop.ButtonTabComponent;
import net.sourceforge.squirrel_sql.client.gui.titlefilepath.TitleFilePathHandlerUtil;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanelPosition;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.ImageIcon;
import java.awt.Component;
import java.io.File;

public class AdditionalSQLTab extends BaseSQLTab
{

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AdditionalSQLTab.class);
   private final int _tabNumber;
   private final ButtonTabComponent _tabComponent;

   private TitleFilePathHandler _titleFileHandler;
   private String _titleWithoutFile;


   public AdditionalSQLTab(ISession session)
   {
      super(session);

      AdditionalSQLTabCounter additionalSQLTabCounter = (AdditionalSQLTabCounter) session.getSessionLocal(AdditionalSQLTabCounter.class);

      if (null == additionalSQLTabCounter)
      {
         additionalSQLTabCounter = new AdditionalSQLTabCounter();
         session.putSessionLocal(AdditionalSQLTabCounter.class, additionalSQLTabCounter);
      }

      _tabNumber = additionalSQLTabCounter.nextNumber();

      _titleWithoutFile = s_stringMgr.getString("AdditionalSQLTab.title", _tabNumber);
      ImageIcon icon = Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.ADD_TAB);

      //_tabComponent = new ButtonTabComponent(getSession().getSessionSheet().getTabbedPane(), _titleWithoutFile, icon);
      _tabComponent = new ButtonTabComponent(_titleWithoutFile, icon);

      _tabComponent.getClosebutton().addActionListener(e -> onClose());
      _tabComponent.getToWindowButton().setVisible(false);

      _titleFileHandler = new TitleFilePathHandler(() -> setTitle(_titleWithoutFile));
   }

   @Override
   protected SQLPanel createSqlPanel()
   {
      return new SQLPanel(getSession(), SQLPanelPosition.ADDITIONAL_TAB_IN_SESSION_WINDOW);
   }

   private void setTitle(String title)
   {
      _titleWithoutFile = title;

      TitleFilePathHandlerUtil.setTitle(_titleWithoutFile, _titleFileHandler, _tabComponent);
   }

   @Override
   public Component getTabComponent()
   {
      return _tabComponent;
   }

   private void onClose()
   {
      getSession().getSessionSheet().removeMainTab(this);
   }


   public String getHint()
   {
      return s_stringMgr.getString("AdditionalSQLTab.tooltip", _tabNumber);
   }


   public void setSqlFile(File sqlFile)
   {
      _titleFileHandler.setSqlFile(sqlFile);
      setTitle(_titleWithoutFile);
   }

   public void setUnsavedEdits(boolean hasUnsavedEdits)
   {
      _titleFileHandler.setUnsavedEdits(hasUnsavedEdits);
   }
}
