package net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.titlefilepath.TitleFilePathHandler;
import net.sourceforge.squirrel_sql.client.gui.titlefilepath.TitleFilePathHandlerUtil;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanelPosition;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.ResizableTextEditDialog;
import net.sourceforge.squirrel_sql.fw.gui.buttontabcomponent.ButtonTabComponent;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;

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

      session.addSimpleSessionListener(() -> onClose(false));

      AdditionalSQLTabCounter additionalSQLTabCounter = (AdditionalSQLTabCounter) session.getSessionLocal(AdditionalSQLTabCounter.class);

      if (null == additionalSQLTabCounter)
      {
         additionalSQLTabCounter = new AdditionalSQLTabCounter();
         session.putSessionLocal(AdditionalSQLTabCounter.class, additionalSQLTabCounter);
      }

      _tabNumber = additionalSQLTabCounter.nextNumber();

      _titleWithoutFile = s_stringMgr.getString("AdditionalSQLTab.title", _tabNumber);
      ImageIcon icon = Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.ADD_TAB);

      _titleFileHandler = new TitleFilePathHandler(() -> setTitle(_titleWithoutFile));

      //_tabComponent = new ButtonTabComponent(getSession().getSessionPanel().getTabbedPane(), _titleWithoutFile, icon);
      _tabComponent = new ButtonTabComponent(_titleWithoutFile, icon);

      _tabComponent.getClosebutton().addActionListener(e -> onClose(true));
      _tabComponent.getToWindowButton().setVisible(false);

   }

   @Override
   protected SQLPanel createSqlPanel()
   {
      return new SQLPanel(getSession(), SQLPanelPosition.ADDITIONAL_TAB_IN_SESSION_WINDOW, _titleFileHandler);
   }

   private void setTitle(String title)
   {
      _titleWithoutFile = title;

      TitleFilePathHandlerUtil.setTitle(_titleWithoutFile, _titleFileHandler, _tabComponent);
   }

   public void setTitleWithoutFile(String titleWithoutFile)
   {
      _titleWithoutFile = titleWithoutFile;
   }

   public String getTitleWithoutFile()
   {
      return _titleWithoutFile;
   }

   @Override
   public void mouseWheelClickedOnTabComponent()
   {
      _tabComponent.doClickClose();
   }

   @Override
   public void rightMouseClickedOnTabComponent(int clickPosX, int clickPosY)
   {
      JPopupMenu popupMenu = new JPopupMenu();
      JMenuItem mnuRename = new JMenuItem(s_stringMgr.getString("AdditionalSQLTab.tab.popup.rename"));
      mnuRename.addActionListener(e -> onRenameTab());
      popupMenu.add(mnuRename);

      popupMenu.show(_tabComponent, clickPosX, clickPosY);
   }

   private void onRenameTab()
   {
      ResizableTextEditDialog dlg =
            new ResizableTextEditDialog(GUIUtils.getOwningWindow(getTabComponent()),
                                        this.getClass().getName(),
                                        s_stringMgr.getString("AdditionalSQLTab.rename.dlg.title"),
                                        s_stringMgr.getString("AdditionalSQLTab.rename.dlg.label"),
                                        _titleWithoutFile);

      if(false == dlg.isOk())
      {
         return;
      }

      setTitle(dlg.getEditedText());
   }


   @Override
   public Component getTabComponent()
   {
      return _tabComponent;
   }

   private void onClose(boolean callConfirmClose)
   {
      close(callConfirmClose);
   }

   public void close(boolean callConfirmClose)
   {
      if(callConfirmClose && getSQLPanel().getSQLPanelAPI().confirmClose())
      {
         getSession().getSessionPanel().removeMainTab(this);
      }

      getSQLPanel().sessionWorksheetOrTabClosing();
   }


   public String getHint()
   {
      return s_stringMgr.getString("AdditionalSQLTab.tooltip", _tabNumber);
   }
}
