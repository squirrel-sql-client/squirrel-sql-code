package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.id.UidIdentifier;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SavedSessionMoreCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SavedSessionMoreCtrl.class);

   public static final String PREF_KEY_FIND_REMEMBER_LAST_SEARCH = "SavedSessionMoreCtrl.remember.last.search";

   public static final String PREF_KEY_FIND_LAST_SEARCH_STRING = "SavedSessionMoreCtrl.last.search.string";


   private final SavedSessionMoreDlg _dlg;
   private ISession _session;
   private SavedSessionMoreCtrlClosingListener _closingListener;
   private SavedSessionJsonBean _savedSessionToOpen;

   public SavedSessionMoreCtrl(ISession session,
                               SavedSessionMoreCtrlClosingListener closingListener,
                               boolean toUseByPreferencesFinderOnly)
   {
      _session = session;
      _closingListener = closingListener;

      Frame owningFrame;
      SavedSessionMoreDlgState state;
      if(null != _session)
      {
         owningFrame = GUIUtils.getOwningFrame(_session.getSessionPanel());
         state = SavedSessionMoreDlgState.CURRENT_SESSION_WARN_DISCARD_SQL_EDITORS;
         if(SavedSessionUtil.isSQLVirgin(_session))
         {
            state = SavedSessionMoreDlgState.CURRENT_SESSION;
         }
      }
      else
      {
         owningFrame = Main.getApplication().getMainFrame();
         state = SavedSessionMoreDlgState.CURRENT_SESSION_NONE;
      }

      _dlg = new SavedSessionMoreDlg(owningFrame, state);

      _dlg.txtToSearch.getDocument().addDocumentListener(new DocumentListener()
      {
         @Override
         public void insertUpdate(DocumentEvent e)
         {
            updateList();
         }

         @Override
         public void removeUpdate(DocumentEvent e)
         {
            updateList();
         }

         @Override
         public void changedUpdate(DocumentEvent e)
         {
            updateList();
         }
      });

      _dlg.chkRememberLastSearch.setSelected(Props.getBoolean(PREF_KEY_FIND_REMEMBER_LAST_SEARCH, false));

      if (_dlg.chkRememberLastSearch.isSelected())
      {
         String lastSearchString = Props.getString(PREF_KEY_FIND_LAST_SEARCH_STRING, null);
         _dlg.txtToSearch.setText(lastSearchString);

         if(null != lastSearchString)
         {
            _dlg.txtToSearch.selectAll();
         }
      }

      updateList();

      _dlg.lstSavedSessions.addMouseListener(new MouseAdapter()
      {
         @Override
         public void mouseClicked(MouseEvent e)
         {
            onListClicked(e);
         }

         @Override
         public void mousePressed(MouseEvent e)
         {
            maybeTriggerPopup(e);
         }

         @Override
         public void mouseReleased(MouseEvent e)
         {
            maybeTriggerPopup(e);
         }
      });


      final SavedSessionsManager savedSessionsManager = Main.getApplication().getSavedSessionsManager();

      _dlg.chkShowDefaultAliasMsg.setSelected(savedSessionsManager.isShowAliasChangeMsg());
      _dlg.chkShowDefaultAliasMsg.addActionListener(e -> savedSessionsManager.setShowAliasChangeMsg(_dlg.chkShowDefaultAliasMsg.isSelected()));

      _dlg.txtMaxNumberSavedSessions.setInt(savedSessionsManager.getMaxNumberSavedSessions());


      _dlg.btnOpenSelected.addActionListener(e -> onOpenSelected());
      _dlg.btnClose.addActionListener(e -> close());

      _dlg.btnDeleteSelected.addActionListener(e -> onDeleteSelected());


      _dlg.txtToSearch.addKeyListener(new KeyAdapter()
      {
         @Override
         public void keyPressed(KeyEvent e)
         {
            onKeyPressed(e);
         }
      });

      _dlg.addWindowListener(new WindowAdapter()
      {
         @Override
         public void windowClosing(WindowEvent e)
         {
            onClosing();
         }
      });

      if(false == toUseByPreferencesFinderOnly)
      {
         GUIUtils.enableCloseByEscape(_dlg, dialog -> onClosing());
         GUIUtils.initLocation(_dlg, 750, 750);

         GUIUtils.forceFocus(_dlg.txtToSearch);

         _dlg.setVisible(true);
      }
   }


   private void onKeyPressed(KeyEvent e)
   {
      if(e.getKeyCode() == KeyEvent.VK_UP)
      {
         int selIx = _dlg.lstSavedSessions.getSelectedIndex();

         if(0 < selIx)
         {
            _dlg.lstSavedSessions.setSelectedIndex(selIx - 1);
            _dlg.lstSavedSessions.ensureIndexIsVisible(selIx - 1);
         }
      }
      else if(e.getKeyCode() == KeyEvent.VK_DOWN)
      {
         int selIx = _dlg.lstSavedSessions.getSelectedIndex();

         if(_dlg.lstSavedSessions.getModel().getSize() - 1 > selIx)
         {
            _dlg.lstSavedSessions.setSelectedIndex(selIx + 1);
            _dlg.lstSavedSessions.ensureIndexIsVisible(selIx + 1);
         }
      }
   }

   private void updateList()
   {
      String filterText = _dlg.txtToSearch.getText();

      final List<SavedSessionJsonBean> savedSessions = Main.getApplication().getSavedSessionsManager().getSavedSessions();

      List<SavedSessionJsonBean> matchingSavedSessions = new ArrayList<>();
      for (SavedSessionJsonBean savedSession : savedSessions)
      {
         if(matches(savedSession, filterText))
         {
            matchingSavedSessions.add(savedSession);
         }
      }

      _dlg.lstSavedSessions.setListData(matchingSavedSessions.toArray(new SavedSessionJsonBean[0]));
      if(0 < _dlg.lstSavedSessions.getModel().getSize())
      {
         _dlg.lstSavedSessions.setSelectedIndex(0);
      }
   }

   private boolean matches(SavedSessionJsonBean savedSession, String filterText)
   {
      if(StringUtilities.isEmpty(filterText, true))
      {
         return true;
      }

      if(StringUtils.containsIgnoreCase(savedSession.getName(), filterText))
      {
         return true;
      }

      if(false == StringUtilities.isEmpty(savedSession.getDefaultAliasIdString(), true))
      {
         final SQLAlias alias = Main.getApplication().getAliasesAndDriversManager().getAlias(new UidIdentifier(savedSession.getDefaultAliasIdString()));

         if(null != alias )
         {
            if((null != alias.getName() && StringUtils.containsIgnoreCase(alias.getName(), filterText))
               || (null != alias.getUrl() && StringUtils.containsIgnoreCase(alias.getUrl(), filterText))
               || (null != alias.getUserName() && StringUtils.containsIgnoreCase(alias.getUserName(), filterText))
            )
            {
               return true;
            }
         }
      }

      for (SessionSqlJsonBean sessionSQL : savedSession.getSessionSQLs())
      {
         if(false == StringUtilities.isEmpty(sessionSQL.getInternalFileName(), true))
         {
            if(StringUtils.containsIgnoreCase(sessionSQL.getInternalFileName(), filterText))
            {
               return true;
            }
         }
         else if(false == StringUtilities.isEmpty(sessionSQL.getExternalFilePath(), true))
         {
            if(StringUtils.containsIgnoreCase(sessionSQL.getExternalFilePath(), filterText))
            {
               return true;
            }
         }
      }

      return false;
   }

   private void maybeTriggerPopup(MouseEvent me)
   {
      if(false == me.isPopupTrigger())
      {
         return;
      }

      JPopupMenu popupMenu = new JPopupMenu();
      JMenuItem menuItem;

      menuItem = new JMenuItem(s_stringMgr.getString("SavedSessionMoreCtrl.rename"));
      menuItem.addActionListener(e -> onRename());
      popupMenu.add(menuItem);

      menuItem = new JMenuItem(s_stringMgr.getString("SavedSessionMoreCtrl.print.saved.session.details.msg.panel"));
      menuItem.addActionListener(e -> onPrintDetails());
      popupMenu.add(menuItem);

      popupMenu.show(_dlg.lstSavedSessions, me.getX(), me.getY());
   }

   private void onPrintDetails()
   {
      final SavedSessionJsonBean selectedSavedSession = _dlg.lstSavedSessions.getSelectedValue();

      if(null == selectedSavedSession)
      {
         Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("SavedSessionMoreCtrl.no.section.to.print.details"));
      }

      SavedSessionUtil.printSavedSessionDetails(selectedSavedSession);
   }

   private void onRename()
   {
      final SavedSessionJsonBean selectedSavedSession = _dlg.lstSavedSessions.getSelectedValue();
      if(null == selectedSavedSession)
      {
         Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("SavedSessionMoreCtrl.no.session.to.rename"));
      }

      final SessionSaveDlg sessionSaveDlg = new SessionSaveDlg(_dlg, selectedSavedSession.getName());

      if(false == sessionSaveDlg.isOk())
      {
         return;
      }

      selectedSavedSession.setName(sessionSaveDlg.getSavedSessionName());
      Main.getApplication().getSavedSessionsManager().moveToTop(selectedSavedSession);

      Main.getApplication().getMainFrame().getMainFrameTitleHandler().updateMainFrameTitle();
   }

   private void onDeleteSelected()
   {
      final List<SavedSessionJsonBean> selectedValuesList = _dlg.lstSavedSessions.getSelectedValuesList();

      if(0 == selectedValuesList.size())
      {
         Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("SavedSessionMoreCtrl.no.session.selected.to.delete"));
         return;
      }

      final List<ISession> openSessions = Main.getApplication().getSavedSessionsManager().getOpenSessionsOfList(selectedValuesList);
      if(0 < openSessions.size())
      {
         if(JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(_dlg, s_stringMgr.getString("SavedSessionMoreCtrl.confirm.delete.including.used.in.open")))
         {
            return;
         }

         detachInternalFiles(openSessions);
      }
      else
      {
         if(JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(_dlg, s_stringMgr.getString("SavedSessionMoreCtrl.confirm.delete")))
         {
            return;
         }
      }

      Main.getApplication().getSavedSessionsManager().delete(selectedValuesList);

      _dlg.lstSavedSessions.setListData(Main.getApplication().getSavedSessionsManager().getSavedSessions().toArray(new SavedSessionJsonBean[0]));

      if(0 < _dlg.lstSavedSessions.getModel().getSize())
      {
         _dlg.lstSavedSessions.setSelectedIndex(1);
      }
   }

   private void detachInternalFiles(List<ISession> sessions)
   {
      for (ISession session : sessions)
      {
         List<SQLPanelSaveInfo> sqlPanelSaveInfos = SavedSessionUtil.getAllSQLPanelsOrderedAndTyped(session);

         for (SQLPanelSaveInfo sqlPanelSaveInfo : sqlPanelSaveInfos)
         {
            final File file = sqlPanelSaveInfo.getSqlPanel().getSQLPanelAPI().getFileHandler().getFile();

            if(SavedSessionUtil.isInSavedSessionsDir(file))
            {
               sqlPanelSaveInfo.getSqlPanel().getSQLPanelAPI().getFileHandler().fileDetach(true);
            }
         }
      }
   }

   private void onListClicked(MouseEvent e)
   {
      if(2 == e.getClickCount())
      {
         onOpenSelected();
      }
   }

   private void onOpenSelected()
   {
      _savedSessionToOpen = _dlg.lstSavedSessions.getSelectedValue();

      if(null == _savedSessionToOpen)
      {
         JOptionPane.showMessageDialog(_dlg, s_stringMgr.getString("SavedSessionMoreCtrl.no.saved.session.selected.to.open"));
         return;
      }
      close();
   }

   private void close()
   {
      onClosing();
      _dlg.setVisible(false);
      _dlg.dispose();
   }

   private boolean isOpenInNewSession()
   {
      if(null == _dlg.openInSessionPanel)
      {
         return true;
      }

      return _dlg.openInSessionPanel.isOpenInNewSession();
   }

   private void onClosing()
   {
      Main.getApplication().getSavedSessionsManager().setMaxNumberSavedSessions(_dlg.txtMaxNumberSavedSessions.getInt());

      Props.putBoolean(PREF_KEY_FIND_REMEMBER_LAST_SEARCH, _dlg.chkRememberLastSearch.isSelected());

      if (_dlg.chkRememberLastSearch.isSelected())
      {
         Props.putString(PREF_KEY_FIND_LAST_SEARCH_STRING, _dlg.txtToSearch.getText());
      }

      if(null != _closingListener)
      {
         _closingListener.closed(_savedSessionToOpen, isOpenInNewSession());
      }
   }

   public String getTitle()
   {
      return _dlg.getTitle();
   }

   public Container getContentPane()
   {
      return _dlg.getContentPane();
   }

   public void toFront()
   {
      _dlg.toFront();
   }
}
