package net.sourceforge.squirrel_sql.client.gui.recentfiles;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.prefs.Preferences;

public class RecentFilesController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(RecentFilesDialog.class);
   private static final String PREF_KEY_RECENT_FILES_EXPANDED = "Squirrel.recentFiles.expanded";
   private static final String PREF_KEY_FAVOURITE_FILES_EXPANDED = "Squirrel.favouriteFiles.expanded";
   private static final String PREF_KEY_RECENT_ALIAS_FILES_EXPANDED = "Squirrel.recentAliasFiles.expanded";
   private static final String PREF_KEY_FAVOURITE_ALIAS_FILES_EXPANDED = "Squirrel.favouriteAliasFiles.expanded";

   private RecentFilesDialog _dialog;
   private IApplication _app;
   private DefaultMutableTreeNode _recentFilesNode;
   private DefaultMutableTreeNode _favouriteFilesNode;
   private DefaultMutableTreeNode _recentFilesForAliasNode;
   private DefaultMutableTreeNode _favouriteFilesForAliasNode;
   private File _fileToOpen;

   public RecentFilesController(IApplication app, ISQLAlias selectedAlias)
   {
      init(app, app.getMainFrame() , selectedAlias, true);
   }


   public RecentFilesController(ISQLPanelAPI panel)
   {
      Frame parent = GUIUtils.getOwningFrame(panel.getSQLEntryPanel().getTextComponent());
      init(panel.getSession().getApplication(), parent, panel.getSession().getAlias(), false);
   }


   private void init(IApplication app, final Frame parent, final ISQLAlias selectedAlias, boolean showAppendOption)
   {
      _app = app;
      _dialog = new RecentFilesDialog(parent, showAppendOption);

      _dialog.btnClose.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            _dialog.dispose();
         }
      });

      initAndLoadTree(selectedAlias);

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


      _dialog.btnFavourites.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onAddToFavourites(parent, null);
         }
      });

      _dialog.btnAliasFavourites.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onAddToFavourites(parent, selectedAlias);
         }
      });

      _dialog.btnRemoveSeleted.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onRemoveSelected(selectedAlias);
         }
      });

      _dialog.btnOpenFile.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onOpenFile();
         }
      });

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

   private void onOpenFile()
   {
      _fileToOpen = findFileToOpen(null);

      if(null == _fileToOpen)
      {
         JOptionPane.showMessageDialog(_dialog, s_stringMgr.getString("RecentFilesController.pleaseSelectFile"));
         return;
      }

      _dialog.dispose();

   }

   private void onWindowClosing()
   {
      JTree tre = _dialog.treFiles;
      tre.isCollapsed(new TreePath(_recentFilesNode.getPath()));
      Preferences.userRoot().putBoolean(PREF_KEY_RECENT_FILES_EXPANDED, tre.isExpanded(new TreePath(_recentFilesNode.getPath())));
      Preferences.userRoot().putBoolean(PREF_KEY_FAVOURITE_FILES_EXPANDED, tre.isExpanded(new TreePath(_favouriteFilesNode.getPath())));
      Preferences.userRoot().putBoolean(PREF_KEY_RECENT_ALIAS_FILES_EXPANDED, tre.isExpanded(new TreePath(_recentFilesForAliasNode.getPath())));
      Preferences.userRoot().putBoolean(PREF_KEY_FAVOURITE_ALIAS_FILES_EXPANDED, tre.isExpanded(new TreePath(_favouriteFilesForAliasNode.getPath())));
   }

   private void onRemoveSelected(ISQLAlias selectedAlias)
   {

      HashSet<DefaultMutableTreeNode> changedParents = new HashSet<DefaultMutableTreeNode>();
      TreePath[] paths = _dialog.treFiles.getSelectionPaths();

      DefaultTreeModel model = (DefaultTreeModel) _dialog.treFiles.getModel();
      for (TreePath path : paths)
      {
         DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) path.getLastPathComponent();
         if( dmtn.getUserObject() instanceof File )
         {
            model.removeNodeFromParent(dmtn);
            changedParents.add((DefaultMutableTreeNode) path.getParentPath().getLastPathComponent());
         }
      }

      for (DefaultMutableTreeNode changedParent : changedParents)
      {
         model.nodeStructureChanged(changedParent);
      }

      _app.getRecentFilesManager().setRecentFiles(getFileStringsFromNode(_recentFilesNode));
      _app.getRecentFilesManager().setFavouriteFiles(getFileStringsFromNode(_favouriteFilesNode));

      _app.getRecentFilesManager().setRecentFilesForAlias(selectedAlias, getFileStringsFromNode(_recentFilesForAliasNode));
      _app.getRecentFilesManager().setFavouriteFilesForAlias(selectedAlias, getFileStringsFromNode(_favouriteFilesForAliasNode));

   }

   private ArrayList<String> getFileStringsFromNode(DefaultMutableTreeNode parentNode)
   {
      ArrayList<String> files = new ArrayList<String>();
      for (int i = 0; i < parentNode.getChildCount(); i++)
      {
         File file = (File) ((DefaultMutableTreeNode) parentNode.getChildAt(i)).getUserObject();
         files.add(file.getAbsolutePath());
      }
      return files;
   }

   private void onAddToFavourites(Frame parent, ISQLAlias alias)
   {
      JFileChooser fc = new JFileChooser(_app.getSquirrelPreferences().getFilePreviousDir());
      fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

      int returnVal = fc.showOpenDialog(parent);
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

   private void initAndLoadTree(ISQLAlias selectedAlias)
   {
      DefaultMutableTreeNode root = new DefaultMutableTreeNode();

      _dialog.treFiles.setModel(new DefaultTreeModel(root));
      _dialog.treFiles.setRootVisible(false);

      _dialog.treFiles.setCellRenderer(new RecentFilesTreeCellRenderer(_app));


      _recentFilesNode = GUIUtils.createFolderNode(s_stringMgr.getString("RecentFilesController.recentFiles.global"));
      root.add(_recentFilesNode);
      addFileKidsToNode(_recentFilesNode, _app.getRecentFilesManager().getRecentFiles(), Preferences.userRoot().getBoolean(PREF_KEY_RECENT_FILES_EXPANDED, true));


      _favouriteFilesNode = GUIUtils.createFolderNode(s_stringMgr.getString("RecentFilesController.favouritFiles.global"));
      root.add(_favouriteFilesNode);
      addFileKidsToNode(_favouriteFilesNode, _app.getRecentFilesManager().getFavouriteFiles(), Preferences.userRoot().getBoolean(PREF_KEY_FAVOURITE_FILES_EXPANDED, false));


      _recentFilesForAliasNode = GUIUtils.createFolderNode(s_stringMgr.getString("RecentFilesController.recentFiles.alias", selectedAlias.getName()));
      root.add(_recentFilesForAliasNode);
      addFileKidsToNode(_recentFilesForAliasNode, _app.getRecentFilesManager().getRecentFilesForAlias(selectedAlias), Preferences.userRoot().getBoolean(PREF_KEY_RECENT_ALIAS_FILES_EXPANDED, false));


      _favouriteFilesForAliasNode = GUIUtils.createFolderNode(s_stringMgr.getString("RecentFilesController.favouritFiles.alias", selectedAlias.getName()));
      root.add(_favouriteFilesForAliasNode);
      addFileKidsToNode(_favouriteFilesForAliasNode, _app.getRecentFilesManager().getFavouriteFilesForAlias(selectedAlias), Preferences.userRoot().getBoolean(PREF_KEY_FAVOURITE_ALIAS_FILES_EXPANDED, false));



      _dialog.treFiles.addMouseListener(new MouseAdapter()
      {
         public void mouseClicked(MouseEvent evt)
         {
            onMouseClickedTree(evt);
         }
      });
   }

   private void onMouseClickedTree(MouseEvent evt)
   {
      _fileToOpen = findFileToOpen(evt);

      if(null != _fileToOpen)
      {
         _dialog.dispose();
      }
   }

   private File findFileToOpen(MouseEvent evt)
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

      if(false  == tn.getUserObject() instanceof File)
      {
         return null;
      }

      return (File)tn.getUserObject();
   }

   private void addFileKidsToNode(final DefaultMutableTreeNode parentNode, ArrayList<String> filePaths, final boolean expand)
   {
      for (String filePath : filePaths)
      {
         DefaultMutableTreeNode node = new DefaultMutableTreeNode(new File(filePath));
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
      return _fileToOpen;
   }
}
