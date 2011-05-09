package net.sourceforge.squirrel_sql.plugins.graph.querybuilder;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.graph.GraphPluginResources;
import net.sourceforge.squirrel_sql.plugins.graph.HideDockButtonHandler;

import javax.swing.*;
import java.awt.*;

public class GraphQueryWherePanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(GraphQueryWherePanel.class);

   JButton btnAddNewAndFolder;
   JButton btnAddNewOrFolder;
   JButton btnDeleteFolder;
   JSplitPane split;

   JTree treeWhere;


   public GraphQueryWherePanel(HideDockButtonHandler hideDockButtonHandler, JComponent editor, GraphPluginResources rsrc)
   {
      setLayout(new BorderLayout());
      add(createButtonPanel(hideDockButtonHandler, rsrc), BorderLayout.NORTH);

      split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

      treeWhere = new JTree();
      split.setLeftComponent(new JScrollPane(treeWhere));

      split.setRightComponent(createEditorPanel(editor));

      split.setOneTouchExpandable(true);
      add(split);
   }

   private JPanel createEditorPanel(JComponent editor)
   {
      JPanel ret = new JPanel(new BorderLayout(0,5));

      JLabel lbl = new JLabel(s_stringMgr.getString("graph.GraphQueryWherePanel.wherePreview"));
      ret.add(lbl, BorderLayout.NORTH);

      JPanel editorPanel = new JPanel(new GridLayout(1,1));
      editorPanel.setBorder(BorderFactory.createEtchedBorder());
      editorPanel.add(editor);
      ret.add(editorPanel);
      ret.setMinimumSize(new Dimension(25, ret.getMinimumSize().height));
      return ret;
   }

   private JPanel createButtonPanel(HideDockButtonHandler hideDockButtonHandler, GraphPluginResources rsrc)
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,10),0,0);
      ret.add(hideDockButtonHandler.getHideButton(), gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,0,5,5),0,0);
      btnAddNewAndFolder = new JButton(s_stringMgr.getString("graph.GraphQueryWherePanel.addNewAndFolder"));
      btnAddNewAndFolder.setIcon(rsrc.getIcon(GraphPluginResources.IKeys.NEW_AND_FOLDER));
      ret.add(btnAddNewAndFolder, gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,0,5,5),0,0);
      btnAddNewOrFolder = new JButton(s_stringMgr.getString("graph.GraphQueryWherePanel.addNewOrFolder"));
      btnAddNewOrFolder.setIcon(rsrc.getIcon(GraphPluginResources.IKeys.NEW_OR_FOLDER));
      ret.add(btnAddNewOrFolder, gbc);

      gbc = new GridBagConstraints(3,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,0,5,5),0,0);
      btnDeleteFolder = new JButton(s_stringMgr.getString("graph.GraphQueryWherePanel.deleteFolder"));
      btnDeleteFolder.setToolTipText(s_stringMgr.getString("graph.GraphQueryWherePanel.willMoveFiltersToUpper"));
      btnDeleteFolder.setIcon(rsrc.getIcon(GraphPluginResources.IKeys.DELETE_FOLDER));
      ret.add(btnDeleteFolder, gbc);

      gbc = new GridBagConstraints(4,0,1,1,1,1,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,0,5,5),0,0);
      JLabel lblFiltered = new JLabel(s_stringMgr.getString("graph.GraphQueryWherePanel.onlyFiltered"));
      lblFiltered.setForeground(Color.red);
      lblFiltered.setFont(lblFiltered.getFont().deriveFont(Font.BOLD));
      ret.add(lblFiltered, gbc);


      gbc = new GridBagConstraints(5,0,1,1,1,1,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,0,5,5),0,0);
      ret.add(new JPanel(), gbc);

      return ret;
   }

}
