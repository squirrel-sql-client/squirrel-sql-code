package net.sourceforge.squirrel_sql.client.gui.recentfiles;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.dnd.DropedFileExtractor;
import net.sourceforge.squirrel_sql.client.session.filemanager.FileHandler;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.TreeDnDHandler;
import net.sourceforge.squirrel_sql.fw.gui.TreeDnDHandlerCallback;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class RecentFilesController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(RecentFilesDialog.class);

   private static final ILogger s_log = LoggerController.createLogger(RecentFilesController.class);

   private static final String PREF_KEY_RECENT_FILES_EXPANDED = "Squirrel.recentFiles.expanded";
   private static final String PREF_KEY_FAVOURITE_FILES_EXPANDED = "Squirrel.favouriteFiles.expanded";
   private static final String PREF_KEY_RECENT_ALIAS_FILES_EXPANDED = "Squirrel.recentAliasFiles.expanded";
   private static final String PREF_KEY_FAVOURITE_ALIAS_FILES_EXPANDED = "Squirrel.favouriteAliasFiles.expanded";
   private static final String PREF_KEY_SHOW_PREVIEW = "Squirrel.recentFiles.showPreview";
   private static final String PREF_KEY_SPLIT_DIVIDER_LOCATION = "Squirrel.recentFiles.split.divider.location";

   private RecentFilesDialog _dialog;
   private IApplication _app;
   private Frame _parent;
   private ISQLAlias _selectedAlias;
   private DefaultMutableTreeNode _recentFilesNode;
   private DefaultMutableTreeNode _favouriteFilesNode;
   private DefaultMutableTreeNode _recentFilesForAliasNode;
   private DefaultMutableTreeNode _favouriteFilesForAliasNode;
   private RecentFileWrapper _fileToOpen;

   private int standardSplitDividerSize;

   public RecentFilesController(IApplication app, ISQLAlias selectedAlias)
   {
      init(app, app.getMainFrame() , selectedAlias, true);
   }


   public RecentFilesController(FileHandler fileHandler)
   {
      Frame parent = GUIUtils.getOwningFrame(fileHandler.getFileEditorAPI().getTextComponent());
      init(Main.getApplication(), parent, fileHandler.getFileEditorAPI().getSession().getAlias(), false);
   }


   private void init(IApplication app, final Frame parent, final ISQLAlias alias, boolean isCalledFromAliasView)
   {
      _app = app;
      _parent = parent;
      _selectedAlias = alias;
      _dialog = new RecentFilesDialog(_parent, isCalledFromAliasView, alias);

      _dialog.btnClose.addActionListener(e -> onCloseButton());

      initAndLoadTree();

      standardSplitDividerSize = _dialog.splitTreePreview.getDividerSize();
      _dialog.chkShowPreview.setSelected(Props.getBoolean(PREF_KEY_SHOW_PREVIEW, false));
      _dialog.chkShowPreview.addActionListener(e -> onShowPreview());
      onShowPreview();

      _dialog.splitTreePreview.addComponentListener(new ComponentAdapter() {
         @Override
         public void componentResized(ComponentEvent e)
         {
            onSplitPaneResized();
         }
      });

      _dialog.txtNumberRecentFiles.setInt(_app.getRecentFilesManager().getMaxRecentFiles());
      _dialog.txtNumberRecentFiles.getDocument().addDocumentListener(new DocumentListener()
      {
         @Override
         public void insertUpdate(DocumentEvent e)
         {
            updateRecentFilesCount();
         }

         @Override
         public void removeUpdate(DocumentEvent e)
         {
            updateRecentFilesCount();
         }

         @Override
         public void changedUpdate(DocumentEvent e)
         {
            updateRecentFilesCount();
         }
      });


      _dialog.btnFavourites.addActionListener(e -> onAddToFavourites(null));

      _dialog.btnAliasFavourites.addActionListener(e -> onAddToFavourites(alias));

      _dialog.btnRemoveSeleted.addActionListener(e -> onRemoveSelected());

      _dialog.btnOpenFile.addActionListener(e -> onOpenFile());

      _dialog.addWindowListener(new WindowAdapter()
      {
         @Override
         public void windowClosing(WindowEvent e)
         {
            onWindowClosing();
         }

         @Override
         public void windowClosed(WindowEvent e)
         {
            onWindowClosing();
         }
      });


      _dialog.setVisible(true);

   }

   private void onSplitPaneResized()
   {
      if(false == _dialog.chkShowPreview.isSelected())
      {
         hideRightSplit();
      }
   }

   private void onCloseButton()
   {
      writeUiTreeToModel();
      _dialog.dispose();
   }

   private void onOpenFile()
   {
      _fileToOpen = findFileToOpen(null);

      if(null == _fileToOpen)
      {
         return;
      }

      _dialog.dispose();

   }

   private void onWindowClosing()
   {
      JTree tre = _dialog.treFiles;
      tre.isCollapsed(new TreePath(_recentFilesNode.getPath()));
      Props.putBoolean(PREF_KEY_RECENT_FILES_EXPANDED, tre.isExpanded(new TreePath(_recentFilesNode.getPath())));
      Props.putBoolean(PREF_KEY_FAVOURITE_FILES_EXPANDED, tre.isExpanded(new TreePath(_favouriteFilesNode.getPath())));
      Props.putBoolean(PREF_KEY_RECENT_ALIAS_FILES_EXPANDED, tre.isExpanded(new TreePath(_recentFilesForAliasNode.getPath())));
      Props.putBoolean(PREF_KEY_FAVOURITE_ALIAS_FILES_EXPANDED, tre.isExpanded(new TreePath(_favouriteFilesForAliasNode.getPath())));

      Props.putBoolean(PREF_KEY_SHOW_PREVIEW, _dialog.chkShowPreview.isSelected());

      saveSpiltDividerLocation();
   }

   private void saveSpiltDividerLocation()
   {
      Props.putInt(PREF_KEY_SPLIT_DIVIDER_LOCATION, _dialog.splitTreePreview.getDividerLocation());
   }

   private void onRemoveSelected()
   {

      HashSet<DefaultMutableTreeNode> changedParents = new HashSet<DefaultMutableTreeNode>();
      TreePath[] paths = _dialog.treFiles.getSelectionPaths();

      DefaultTreeModel model = (DefaultTreeModel) _dialog.treFiles.getModel();
      for (TreePath path : paths)
      {
         DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) path.getLastPathComponent();
         if( dmtn.getUserObject() instanceof RecentFileWrapper )
         {
            model.removeNodeFromParent(dmtn);
            changedParents.add((DefaultMutableTreeNode) path.getParentPath().getLastPathComponent());
         }
      }

      for (DefaultMutableTreeNode changedParent : changedParents)
      {
         model.nodeStructureChanged(changedParent);
      }

      writeUiTreeToModel();

   }

   private void writeUiTreeToModel()
   {
      _app.getRecentFilesManager().setRecentFiles(getFileStringsFromNode(_recentFilesNode));
      _app.getRecentFilesManager().setFavouriteFiles(getFileStringsFromNode(_favouriteFilesNode));

      _app.getRecentFilesManager().setRecentFilesForAlias(_selectedAlias, getFileStringsFromNode(_recentFilesForAliasNode));
      _app.getRecentFilesManager().setFavouriteFilesForAlias(_selectedAlias, getFileStringsFromNode(_favouriteFilesForAliasNode));

      _app.getRecentFilesManager().setOpenAtStartupFile(_selectedAlias, getOpenAtStartupFile(_favouriteFilesForAliasNode));
   }

   private String getOpenAtStartupFile(DefaultMutableTreeNode parentNode)
   {
      for (int i = 0; i < parentNode.getChildCount(); i++)
      {
         RecentFileWrapper fileWrapper = (RecentFileWrapper) ((DefaultMutableTreeNode) parentNode.getChildAt(i)).getUserObject();
         if(fileWrapper.isOpenAtSessionStart())
         {
            return fileWrapper.getFile().getAbsolutePath();
         }
      }
      return null;
   }

   private ArrayList<String> getFileStringsFromNode(DefaultMutableTreeNode parentNode)
   {
      ArrayList<String> files = new ArrayList<String>();
      for (int i = 0; i < parentNode.getChildCount(); i++)
      {
         RecentFileWrapper fileWrapper = (RecentFileWrapper) ((DefaultMutableTreeNode) parentNode.getChildAt(i)).getUserObject();
         files.add(fileWrapper.getFile().getAbsolutePath());
      }
      return files;
   }

   private void onAddToFavourites(ISQLAlias alias)
   {
      JFileChooser fc = new JFileChooser(_app.getSquirrelPreferences().getFilePreviousDir());
      fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

      int returnVal = fc.showOpenDialog(_parent);
      if (returnVal != JFileChooser.APPROVE_OPTION)
      {
         return;
      }


      DefaultMutableTreeNode nodeToAddTo;
      ArrayList<String> listToAddTo;

      if (null == alias)
      {
         _app.getRecentFilesManager().adjustFavouriteFiles(fc.getSelectedFile());
         nodeToAddTo = _favouriteFilesNode;
         listToAddTo = _app.getRecentFilesManager().getFavouriteFiles();
      }
      else
      {
         _app.getRecentFilesManager().adjustFavouriteAliasFiles(alias, fc.getSelectedFile());
         nodeToAddTo = _favouriteFilesForAliasNode;
         listToAddTo = _app.getRecentFilesManager().getFavouriteFilesForAlias(alias);
      }

      nodeToAddTo.removeAllChildren();
      addFileKidsToNode(nodeToAddTo, listToAddTo, false);

      DefaultTreeModel dtm = (DefaultTreeModel) _dialog.treFiles.getModel();
      dtm.nodeStructureChanged(nodeToAddTo);
      _dialog.treFiles.expandPath(new TreePath(nodeToAddTo.getPath()));

      DefaultMutableTreeNode firstChild = (DefaultMutableTreeNode) nodeToAddTo.getFirstChild();
      _dialog.treFiles.scrollPathToVisible(new TreePath(firstChild.getPath()));

   }

   private void updateRecentFilesCount()
   {
      int maxRecentFiles = _dialog.txtNumberRecentFiles.getInt();
      if (0 < maxRecentFiles)
      {
         _app.getRecentFilesManager().setMaxRecentFiles(maxRecentFiles);
      }
   }

   private void initAndLoadTree()
   {
      DefaultMutableTreeNode root = new DefaultMutableTreeNode();


      _dialog.treFiles.setCellRenderer(new RecentFilesTreeCellRenderer(_app));


      _recentFilesNode = GUIUtils.createFolderNode(s_stringMgr.getString("RecentFilesController.recentFiles.global"));
      addFileKidsToNode(_recentFilesNode, _app.getRecentFilesManager().getRecentFiles(), Props.getBoolean(PREF_KEY_RECENT_FILES_EXPANDED, true));
      root.add(_recentFilesNode);


      _favouriteFilesNode = GUIUtils.createFolderNode(s_stringMgr.getString("RecentFilesController.favouritFiles.global"));
      addFileKidsToNode(_favouriteFilesNode, _app.getRecentFilesManager().getFavouriteFiles(), Props.getBoolean(PREF_KEY_FAVOURITE_FILES_EXPANDED, true));
      root.add(_favouriteFilesNode);


      _recentFilesForAliasNode = GUIUtils.createFolderNode(s_stringMgr.getString("RecentFilesController.recentFiles.alias", _selectedAlias.getName()));
      addFileKidsToNode(_recentFilesForAliasNode, _app.getRecentFilesManager().getRecentFilesForAlias(_selectedAlias), Props.getBoolean(PREF_KEY_RECENT_ALIAS_FILES_EXPANDED, true));
      root.add(_recentFilesForAliasNode);


      _favouriteFilesForAliasNode = GUIUtils.createFolderNode(s_stringMgr.getString("RecentFilesController.favouritFiles.alias", _selectedAlias.getName()));
      addFileKidsToNode(_favouriteFilesForAliasNode, _app.getRecentFilesManager().getFavouriteFilesForAlias(_selectedAlias), Props.getBoolean(PREF_KEY_FAVOURITE_ALIAS_FILES_EXPANDED, true));
      root.add(_favouriteFilesForAliasNode);


      String openAtStartupFileForAlias = _app.getRecentFilesManager().getOpenAtStartupFileForAlias(_selectedAlias);

      if(null != openAtStartupFileForAlias)
      {
         boolean found = false;
         for (int i = 0; i < _favouriteFilesForAliasNode.getChildCount(); i++)
         {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) _favouriteFilesForAliasNode.getChildAt(i);
            RecentFileWrapper fileWrapper = (RecentFileWrapper) child.getUserObject();

            if(openAtStartupFileForAlias.equals(fileWrapper.getFile().getAbsolutePath()))
            {
               fileWrapper.setOpenAtSessionStart(true);
               found = true;
               break;
            }
         }

         if(false == found)
         {
            throw new IllegalStateException("The open at Session start up file is not one of the Alias's favourite files.");
         }
      }


      _dialog.treFiles.addMouseListener(new MouseAdapter()
      {
         public void mouseClicked(MouseEvent evt)
         {
            onMouseClickedTree(evt);
         }

         @Override
         public void mousePressed(MouseEvent evt)
         {
            maybeShowTreePopup(evt);
         }

         @Override
         public void mouseReleased(MouseEvent evt)
         {
            maybeShowTreePopup(evt);
         }
      });

      _dialog.treFiles.addTreeSelectionListener(e -> onTreeSelectionChanged());


      _dialog.treFiles.setModel(new DefaultTreeModel(root));
      _dialog.treFiles.setRootVisible(false);

      initDnD();

   }

   private void onShowPreview()
   {
      if(_dialog.chkShowPreview.isSelected())
      {
         _dialog.splitTreePreview.setDividerSize(standardSplitDividerSize);

         int preferredDividerLocation = _app.getPropsImpl().getInt(PREF_KEY_SPLIT_DIVIDER_LOCATION, _dialog.getWidthPreference() / 2);

         int dividerLocation = preferredDividerLocation;
         if (0 < _dialog.splitTreePreview.getWidth())
         {
            dividerLocation = Math.min(_dialog.splitTreePreview.getMaximumDividerLocation(), preferredDividerLocation);
         }

         _dialog.splitTreePreview.setDividerLocation(dividerLocation);
      }
      else
      {
         saveSpiltDividerLocation();
         hideRightSplit();
      }

      onTreeSelectionChanged();
   }

   private void hideRightSplit()
   {
      _dialog.splitTreePreview.setDividerLocation(Integer.MAX_VALUE);
      _dialog.splitTreePreview.setDividerSize(0);
   }

   private void onTreeSelectionChanged()
   {
      _dialog.txtPreview.setText("");

      if(false == _dialog.chkShowPreview.isSelected())
      {
         return;
      }

      TreePath selectionPath = _dialog.treFiles.getSelectionPath();
      if(null == selectionPath)
      {
         return;
      }

      Object userObject = ((DefaultMutableTreeNode) selectionPath.getLastPathComponent()).getUserObject();

      if(false == userObject instanceof  RecentFileWrapper)
      {
         return;
      }

      RecentFileWrapper fileWrapper = (RecentFileWrapper) userObject;

      if(null == fileWrapper.getFile())
      {
         return;
      }

      String text;

      if(false == fileWrapper.getFile().exists())
      {
         text = s_stringMgr.getString("RecentFilesController.preview.doesNotExist", fileWrapper.getFile().getAbsolutePath());
      }
      else if(fileWrapper.getFile().isDirectory())
      {
         text = s_stringMgr.getString("RecentFilesController.preview.isADirectory", fileWrapper.getFile().getAbsolutePath());
      }
      else if(false == fileWrapper.getFile().canRead())
      {
         text = s_stringMgr.getString("RecentFilesController.preview.canNotRead", fileWrapper.getFile().getAbsolutePath());
      }
      else
      {
         try
         {
            text = String.join("\n", Files.readAllLines(fileWrapper.getFile().toPath()));
         }
         catch (Exception e)
         {
            String errMsg = s_stringMgr.getString("RecentFilesController.preview.errorReadingFile", fileWrapper.getFile().getAbsolutePath(), e.toString());
            Main.getApplication().getMessageHandler().showErrorMessage(errMsg);
            text = errMsg;
            s_log.error(errMsg, e);
         }
      }

      _dialog.txtPreview.setText(text);

      SwingUtilities.invokeLater(() -> _dialog.txtPreview.scrollRectToVisible(new Rectangle(0,0)));
   }

   private void initDnD()
   {
      TreeDnDHandlerCallback treeDnDHandlerCallback = new TreeDnDHandlerCallback()
      {
         @Override
         public boolean nodeAcceptsKids(DefaultMutableTreeNode selNode)
         {
            return onNodeAcceptsKids(selNode);
         }

         @Override
         public void dndExecuted()
         {
            onDndExecuted();
         }

         @Override
         public ArrayList<DefaultMutableTreeNode> createPasteTreeNodesFromExternalTransfer(DropTargetDropEvent dtde, TreePath targetPath)
         {
            return onCreatePasteTreeNodesFromExternalTransfer(dtde, targetPath);
         }
      };

      new TreeDnDHandler(_dialog.treFiles, treeDnDHandlerCallback, true);
   }

   private ArrayList<DefaultMutableTreeNode> onCreatePasteTreeNodesFromExternalTransfer(DropTargetDropEvent dtde, TreePath targetPath)
   {
      List<File> files = DropedFileExtractor.getFiles(dtde, _app);

      ArrayList<DefaultMutableTreeNode> ret = new ArrayList<DefaultMutableTreeNode>();

      DefaultMutableTreeNode parent = findParent(targetPath);
      for (File file : files)
      {
         if (false == parentContainsFile(parent, file))
         {
            ret.add(new DefaultMutableTreeNode(new RecentFileWrapper(file)));
         }
      }

      return ret;

   }

   private boolean parentContainsFile(DefaultMutableTreeNode parentNode, File fileToCheck)
   {
      if(null == parentNode)
      {
         return false;
      }

      for (int i = 0; i < parentNode.getChildCount(); i++)
      {
         RecentFileWrapper fileWrapper = (RecentFileWrapper) ((DefaultMutableTreeNode) parentNode.getChildAt(i)).getUserObject();

         if(fileWrapper.getFile().equals(fileToCheck))
         {
            return true;
         }
      }
      return false;
   }

   private DefaultMutableTreeNode findParent(TreePath targetPath)
   {
      if(((DefaultMutableTreeNode)targetPath.getLastPathComponent()).getUserObject() instanceof RecentFileWrapper)
      {
         targetPath = targetPath.getParentPath();
      }

      if(targetPath.getLastPathComponent() == _recentFilesNode)
      {
         return _recentFilesNode;
      }
      else if(targetPath.getLastPathComponent() == _recentFilesForAliasNode)
      {
         return _recentFilesForAliasNode;
      }
      else if(targetPath.getLastPathComponent() == _favouriteFilesNode)
      {
         return _favouriteFilesNode;
      }
      else if(targetPath.getLastPathComponent() == _favouriteFilesForAliasNode)
      {
         return _favouriteFilesForAliasNode;
      }
      else
      {
         return null;
      }

   }

   private void onDndExecuted()
   {
      writeUiTreeToModel();
   }

   private boolean onNodeAcceptsKids(DefaultMutableTreeNode selNode)
   {
      return _recentFilesNode == selNode ||
            _recentFilesForAliasNode == selNode ||
            _favouriteFilesNode == selNode ||
            _favouriteFilesForAliasNode == selNode;

   }


   private void onMouseClickedTree(MouseEvent evt)
   {
      _fileToOpen = findFileToOpen(evt);

      if(null != _fileToOpen)
      {
         _dialog.dispose();
      }
   }

   private void maybeShowTreePopup(MouseEvent evt)
   {
      if(false == evt.isPopupTrigger())
      {
         return;
      }

      TreePath clickedPath  = _dialog.treFiles.getPathForLocation(evt.getX(), evt.getY());

      if(null == clickedPath)
      {
         return;
      }

      _dialog.treFiles.setSelectionPath(clickedPath);

      DefaultMutableTreeNode node = (DefaultMutableTreeNode) clickedPath.getLastPathComponent();

      if( false == node.getUserObject() instanceof RecentFileWrapper )
      {
         return;
      }
      RecentFileWrapper fileWrapper = (RecentFileWrapper) node.getUserObject();

      JPopupMenu popUp = new JPopupMenu();

      popUp.add(createOpenInFileManagerItem(fileWrapper.getFile()));

      DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();

      if(fileWrapper.getFile().isFile() && parent == _favouriteFilesForAliasNode)
      {
         popUp.add(createOpenAtSessionStartupItem(node));
      }

      popUp.show(evt.getComponent(), evt.getX(), evt.getY());
   }

   private JMenuItem createOpenAtSessionStartupItem(DefaultMutableTreeNode node)
   {
      JMenuItem menuItem = new JMenuItem(s_stringMgr.getString("RecentFilesController.toggle.open.at.session.connect"));

      menuItem.addActionListener(e -> onOpenFileAtSessionConnect(node));

      return menuItem;
   }

   private void onOpenFileAtSessionConnect(DefaultMutableTreeNode node)
   {

      if(((RecentFileWrapper)node.getUserObject()).isOpenAtSessionStart())
      {
         ((RecentFileWrapper)node.getUserObject()).setOpenAtSessionStart(false);
         ((DefaultTreeModel)_dialog.treFiles.getModel()).nodeChanged(node);
         return;
      }

      TreeNode aliasFavouritesNode = node.getParent();

      for (int i = 0; i < aliasFavouritesNode.getChildCount(); i++)
      {
         DefaultMutableTreeNode child = (DefaultMutableTreeNode) aliasFavouritesNode.getChildAt(i);
         ((RecentFileWrapper)child.getUserObject()).setOpenAtSessionStart(false);
      }

      ((RecentFileWrapper)node.getUserObject()).setOpenAtSessionStart(true);

      ((DefaultTreeModel)_dialog.treFiles.getModel()).nodeStructureChanged(aliasFavouritesNode);
   }

   private JMenuItem createOpenInFileManagerItem(File file)
   {
      JMenuItem menuItem;

      if (file.isDirectory())
      {
         menuItem = new JMenuItem(s_stringMgr.getString("RecentFilesController.open.in.file.manager"));
      }
      else
      {
         menuItem = new JMenuItem(s_stringMgr.getString("RecentFilesController.open.parent.in.file.manager"));
      }

      menuItem.addActionListener(e -> onOpenInFileManager(file));
      return menuItem;
   }

   private void onOpenInFileManager(File file)
   {
      try
      {
         Desktop desktop = Desktop.getDesktop();
         if (file.isDirectory())
         {
            desktop.open(file);
         }
         else
         {
            desktop.open(file.getParentFile());
         }
      }
      catch (IOException e)
      {
         String msg = s_stringMgr.getString("RecentFilesController.failed.to open.file", e.getMessage());
         s_log.error(msg, e);
         Main.getApplication().getMessageHandler().showErrorMessage(msg);
      }
   }


   private RecentFileWrapper findFileToOpen(MouseEvent evt)
   {

      DefaultMutableTreeNode tn = getSelectedFileNode(evt);

      if (tn == null)
      {
         if (null == evt) // The open button was pushed
         {
            JOptionPane.showMessageDialog(_dialog, s_stringMgr.getString("RecentFilesController.pleaseSelectFile"));
         }
         return null;
      }

      RecentFileWrapper fileWrapper = (RecentFileWrapper) tn.getUserObject();


      if(false == fileWrapper.getFile().exists())
      {
         if(JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(_dialog, s_stringMgr.getString("RecentFilesController.fileDoesNotExist")))
         {
            DefaultTreeModel model = (DefaultTreeModel) _dialog.treFiles.getModel();
            TreeNode parent = tn.getParent();
            model.removeNodeFromParent(tn);
            model.nodeStructureChanged(parent);

            if(parent == _recentFilesNode)
            {
               _app.getRecentFilesManager().setRecentFiles(getFileStringsFromNode(_recentFilesNode));
            }
            else if(parent == _favouriteFilesNode)
            {
               _app.getRecentFilesManager().setFavouriteFiles(getFileStringsFromNode(_favouriteFilesNode));
            }
            else if(parent == _recentFilesForAliasNode)
            {
               _app.getRecentFilesManager().setRecentFilesForAlias(_selectedAlias, getFileStringsFromNode(_recentFilesForAliasNode));
            }
            else
            {
               _app.getRecentFilesManager().setFavouriteFilesForAlias(_selectedAlias, getFileStringsFromNode(_favouriteFilesForAliasNode));
            }
         }

         return null;
      }

      if(fileWrapper.getFile().isDirectory())
      {
         JFileChooser fc = new JFileChooser(fileWrapper.getFile());
         fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

         int returnVal = fc.showOpenDialog(_parent);
         if (returnVal != JFileChooser.APPROVE_OPTION)
         {
            return null;
         }

         fileWrapper = new RecentFileWrapper(fc.getSelectedFile());
      }


      if(false == fileWrapper.getFile().canRead())
      {
         JOptionPane.showMessageDialog(_dialog, s_stringMgr.getString("RecentFilesController.fileIsNotReadable"));
         return null;
      }

      return fileWrapper;

   }

   private DefaultMutableTreeNode getSelectedFileNode(MouseEvent evt)
   {
      TreePath path = _dialog.treFiles.getSelectionPath();

      if(null == path)
      {
         return null;
      }

      if (null != evt)
      {
         if (evt.getClickCount() < 2)
         {
            return null;
         }

         if (false == _dialog.treFiles.getPathBounds(path).contains(evt.getPoint()))
         {
            // If the mouse wasn't placed on the selected file we do nothing.
            return null;
         }
      }


      DefaultMutableTreeNode tn = (DefaultMutableTreeNode) path.getLastPathComponent();

      if(false  == tn.getUserObject() instanceof RecentFileWrapper)
      {
         return null;
      }
      return tn;
   }

   private void addFileKidsToNode(final DefaultMutableTreeNode parentNode, ArrayList<String> filePaths, final boolean expand)
   {
      for (String filePath : filePaths)
      {
         DefaultMutableTreeNode node = new DefaultMutableTreeNode(new RecentFileWrapper(new File(filePath)));
         parentNode.add(node);
      }

      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            if (expand)
            {
               _dialog.treFiles.expandPath(new TreePath(parentNode.getPath()));
            }
         }
      });

   }

   public File getFileToOpen()
   {
      if(null == _fileToOpen)
      {
         return null;
      }

      return _fileToOpen.getFile();
   }

   public boolean isAppend()
   {
      return _dialog.chkAppend.isSelected();
   }
}
