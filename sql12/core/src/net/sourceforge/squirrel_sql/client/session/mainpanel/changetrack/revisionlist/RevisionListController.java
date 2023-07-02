package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.revisionlist;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.ChangeTrackCloseDispatcher;
import net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.ChangeTrackCloseListener;
import net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.GitHandler;
import net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.revisionlist.diff.DiffToLocalCtrl;
import net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.revisionlist.diff.RevisionsDiffCtrl;
import net.sourceforge.squirrel_sql.fw.gui.ClipboardUtil;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;

public class RevisionListController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(RevisionListController.class);

   private static final String PREF_KEY_SPLIT_DIVIDER_LOCATION = "changetrack.RevisionListController.split.divider.location";
   private final DiffToLocalCtrl _diffToLocalCtrl;

   private final RevisionsDiffCtrl _revisionsDiffCtrl;


   private RevisionListDialog _dlg;
   private ChangeTrackCloseDispatcher _changeTrackCloseDispatcher;
   private RevisionListControllerChannel _revisionListControllerChannel;
   private File _file;
   private ChangeTrackCloseListener _changeTrackCloseListener;

   public RevisionListController(JComponent parentComp,
                                 ChangeTrackCloseDispatcher changeTrackCloseDispatcher,
                                 RevisionListControllerChannel revisionListControllerChannel,
                                 File file)
   {
      _file = file;

      _diffToLocalCtrl = new DiffToLocalCtrl(revisionListControllerChannel);
      _revisionsDiffCtrl = new RevisionsDiffCtrl();
      _dlg = new RevisionListDialog(parentComp,
                                    _file.getName(),
                                    GitHandler.getPathRelativeToRepo(file),
                                    GitHandler.getFilesRepositoryWorkTreePath(file),
                                    _diffToLocalCtrl.getDiffPanel(),
                                    _revisionsDiffCtrl.getDiffPanel());

      _changeTrackCloseDispatcher = changeTrackCloseDispatcher;
      _revisionListControllerChannel = revisionListControllerChannel;
      _changeTrackCloseListener = () -> onChangeTrackClosed();
      _changeTrackCloseDispatcher.addChangeTrackCloseListener(_changeTrackCloseListener);

      List<RevisionWrapper> revisions = GitHandler.getRevisions(file);

      _dlg.lstRevisions.setListData(revisions.toArray(new RevisionWrapper[0]));
      _dlg.lstRevisions.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

      if(0 < revisions.size())
      {
         _dlg.lstRevisions.setSelectedIndex(0);
         _dlg.lstRevisions.ensureIndexIsVisible(0);
      }

      _dlg.lstRevisions.addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent e)
         {
            maybeShowRevisionListPopup(e);
         }

         @Override
         public void mouseReleased(MouseEvent e)
         {
            maybeShowRevisionListPopup(e);
         }
      });

      _dlg.txtPreview.addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent e)
         {
            maybeShowPreviewPopup(e);
         }

         @Override
         public void mouseReleased(MouseEvent e)
         {
            maybeShowPreviewPopup(e);
         }
      });

      _dlg.tabbedPane.addChangeListener(e -> onTabChanged());

      GUIUtils.initLocation(_dlg, 500, 500);
      GUIUtils.enableCloseByEscape(_dlg, dialog -> onCloseRevisionList());

      initSplitDividerLocation();


      _dlg.lstRevisions.addListSelectionListener(e -> onListSelectionChanged(e));
      onListSelectionChanged(null);

      _dlg.addWindowListener(new WindowAdapter() {

         @Override
         public void windowClosing(WindowEvent e)
         {
            onCloseRevisionList();
         }

         @Override
         public void windowClosed(WindowEvent e)
         {
            onCloseRevisionList();
         }
      });

      _dlg.setVisible(true);

   }

   private void onTabChanged()
   {
      cleanUpMelds();

      if(  _dlg.tabbedPane.getSelectedComponent() == _diffToLocalCtrl.getDiffPanel()
         && 1 == _dlg.lstRevisions.getSelectedValuesList().size())
      {
         RevisionWrapper selectedWrapper = _dlg.lstRevisions.getSelectedValue();
         String fileContent = GitHandler.getVersionOfFile(_file, selectedWrapper.getRevCommitId(), selectedWrapper.getPreviousNamesOfFileRelativeToRepositoryRoot());

         _diffToLocalCtrl.setSelectedRevision(fileContent, selectedWrapper.getRevisionDateString());
      }
      else if(_dlg.tabbedPane.getSelectedComponent() == _revisionsDiffCtrl.getDiffPanel()
              && 1 < _dlg.lstRevisions.getSelectedValuesList().size())
      {
         RevisionWrapper leftWrapper = _dlg.lstRevisions.getSelectedValuesList().get(0);
         RevisionWrapper rightWrapper = _dlg.lstRevisions.getSelectedValuesList().get(1);

         String fileContentLeft = GitHandler.getVersionOfFile(_file, leftWrapper.getRevCommitId(), leftWrapper.getPreviousNamesOfFileRelativeToRepositoryRoot());
         String fileContentRight = GitHandler.getVersionOfFile(_file, rightWrapper.getRevCommitId(), rightWrapper.getPreviousNamesOfFileRelativeToRepositoryRoot());

         _revisionsDiffCtrl.setSelectedRevisions(fileContentLeft, leftWrapper.getRevisionDateString(), fileContentRight, rightWrapper.getRevisionDateString());
      }
   }

   private void onCloseRevisionList()
   {
      saveSplitLocation();
      cleanUpMelds();
   }

   private void cleanUpMelds()
   {
      _diffToLocalCtrl.cleanUpMelds();
      _revisionsDiffCtrl.cleanUpMelds();
   }

   private void maybeShowPreviewPopup(MouseEvent me)
   {
      if(false == me.isPopupTrigger())
      {
         return;
      }

      JPopupMenu popupMenu = new JPopupMenu();

      JMenuItem mnuPreviewCopy = new JMenuItem(s_stringMgr.getString("RevisionListController.preview.copy"));
      mnuPreviewCopy.addActionListener(e -> ClipboardUtil.copyToClip(_dlg.txtPreview.getSelectedText(), true));
      popupMenu.add(mnuPreviewCopy);

      JMenuItem mnuPreviewCopyAll = new JMenuItem(s_stringMgr.getString("RevisionListController.preview.copy.all"));
      mnuPreviewCopyAll.addActionListener(e -> ClipboardUtil.copyToClip(_dlg.txtPreview.getText(), true));
      popupMenu.add(mnuPreviewCopyAll);

      popupMenu.show(_dlg.txtPreview, me.getX(), me.getY());

   }

   private void onChangeTrackClosed()
   {
      _changeTrackCloseDispatcher.removeChangeTrackCloseListener(_changeTrackCloseListener);
      _dlg.setVisible(false);
      _dlg.dispose();
   }

   private void saveSplitLocation()
   {
      Main.getApplication().getPropsImpl().put(PREF_KEY_SPLIT_DIVIDER_LOCATION, _dlg.splitTreePreview.getDividerLocation());
   }

   private void onListSelectionChanged(ListSelectionEvent e)
   {
      if(null != e && e.getValueIsAdjusting())
      {
         return;
      }

      cleanUpMelds();

      _dlg.txtPreview.setText(null);
      _diffToLocalCtrl.setSelectedRevision(null, null);

      if(_dlg.lstRevisions.getSelectedValuesList().isEmpty())
      {
         _dlg.tabbedPane.setEnabledAt(0, true);
         _dlg.tabbedPane.setEnabledAt(1, true);
         _dlg.tabbedPane.setEnabledAt(2, false);
         if(2 == _dlg.tabbedPane.getSelectedIndex())
         {
            _dlg.tabbedPane.setSelectedIndex(0);
         }
      }
      else if(1 == _dlg.lstRevisions.getSelectedValuesList().size())
      {
         _dlg.tabbedPane.setEnabledAt(0, true);
         _dlg.tabbedPane.setEnabledAt(1, true);
         _dlg.tabbedPane.setEnabledAt(2, false);
         if(2 == _dlg.tabbedPane.getSelectedIndex())
         {
            _dlg.tabbedPane.setSelectedIndex(0);
         }

         RevisionWrapper selectedWrapper = _dlg.lstRevisions.getSelectedValuesList().get(0);
         String fileContent = GitHandler.getVersionOfFile(_file, selectedWrapper.getRevCommitId(), selectedWrapper.getPreviousNamesOfFileRelativeToRepositoryRoot());

         _dlg.txtPreview.setText(fileContent);

         if(_dlg.tabbedPane.getSelectedComponent() == _diffToLocalCtrl.getDiffPanel())
         {
            _diffToLocalCtrl.setSelectedRevision(fileContent, selectedWrapper.getRevisionDateString());
         }

         SwingUtilities.invokeLater(() -> _dlg.txtPreview.scrollRectToVisible(new Rectangle(0,0,1,1)));
      }
      else
      {
         _dlg.tabbedPane.setEnabledAt(0, false);
         _dlg.tabbedPane.setEnabledAt(1, false);
         _dlg.tabbedPane.setEnabledAt(2, true);
         _dlg.tabbedPane.setSelectedIndex(2);

         RevisionWrapper leftWrapper = _dlg.lstRevisions.getSelectedValuesList().get(0);
         RevisionWrapper rightWrapper = _dlg.lstRevisions.getSelectedValuesList().get(1);

         String fileContentLeft = GitHandler.getVersionOfFile(_file, leftWrapper.getRevCommitId(), leftWrapper.getPreviousNamesOfFileRelativeToRepositoryRoot());
         String fileContentRight = GitHandler.getVersionOfFile(_file, rightWrapper.getRevCommitId(), rightWrapper.getPreviousNamesOfFileRelativeToRepositoryRoot());

         _revisionsDiffCtrl.setSelectedRevisions(fileContentLeft, leftWrapper.getRevisionDateString(), fileContentRight, rightWrapper.getRevisionDateString());
      }
   }

   private void initSplitDividerLocation()
   {
      int preferredDividerLocation = Main.getApplication().getPropsImpl().getInt(PREF_KEY_SPLIT_DIVIDER_LOCATION, _dlg.getWidth() / 2);

      int dividerLocation = preferredDividerLocation;
      if (0 < _dlg.splitTreePreview.getWidth())
      {
         dividerLocation = Math.min(_dlg.splitTreePreview.getMaximumDividerLocation(), preferredDividerLocation);
      }

      _dlg.splitTreePreview.setDividerLocation(dividerLocation);
   }

   private void maybeShowRevisionListPopup(MouseEvent me)
   {
      if(false == me.isPopupTrigger())
      {
         return;
      }

      int ix = _dlg.lstRevisions.locationToIndex(me.getPoint());

      if(ix < 0)
      {
         return;
      }

      _dlg.lstRevisions.setSelectedIndex(ix);

      JPopupMenu popupMenu = new JPopupMenu();

      JMenu mnuCopy = new JMenu(s_stringMgr.getString("RevisionListController.copy"));

      RevisionWrapper selectedWrapper = _dlg.lstRevisions.getSelectedValue();
      mnuCopy.add(createCopyMenu("RevisionListController.copy.all", selectedWrapper.getDisplayString()));
      mnuCopy.add(createCopyMenu("RevisionListController.copy.date", selectedWrapper.getRevisionDateString()));
      mnuCopy.add(createCopyMenu("RevisionListController.copy.brancheslist", selectedWrapper.getBranchesListString()));
      mnuCopy.add(createCopyMenu("RevisionListController.copy.user", selectedWrapper.getCommitterName()));
      mnuCopy.add(createCopyMenu("RevisionListController.copy.revid", selectedWrapper.getRevisionIdString()));
      mnuCopy.add(createCopyMenu("RevisionListController.copy.commitMsg", selectedWrapper.getCommitMsg()));

      popupMenu.add(mnuCopy);


      JMenuItem mnuAsChangeTrackBase = new JMenuItem(s_stringMgr.getString("RevisionListController.as.change.track.base"));
      mnuAsChangeTrackBase.addActionListener(e -> _revisionListControllerChannel.replaceChangeTrackBase(_dlg.txtPreview.getText()));
      popupMenu.add(mnuAsChangeTrackBase);

      JMenuItem mnuAsEditorContent = new JMenuItem(s_stringMgr.getString("RevisionListController.as.editor.content"));
      mnuAsEditorContent.addActionListener(e -> _revisionListControllerChannel.replaceEditorContent(_dlg.txtPreview.getText()));
      popupMenu.add(mnuAsEditorContent);

      popupMenu.show(_dlg.lstRevisions, me.getX(), me.getY());
   }

   private JMenuItem createCopyMenu(String title, String toCopy)
   {
      JMenuItem ret = new JMenuItem(s_stringMgr.getString(title));

      ret.addActionListener(e -> ClipboardUtil.copyToClip(toCopy));

      return ret;
   }
}
