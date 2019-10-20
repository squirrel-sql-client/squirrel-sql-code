package net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ObjectTreePosition;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreePanel;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.props.Props;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class SQLPanelSplitter extends JPanel
{
   private static final String PREF_OBJECT_TREE_SPLIT_DIVIDER_LOC = "SQLPanelSplitter.PREF_OBJECT_TREE_SPLIT_DIVIDER_LOC";

   private final JSplitPane _splitPane;
   private SQLPanel _sqlPanel;

   private ObjectTreePanel _objectTreePanel;

   private int _standardDividerSize;

   public SQLPanelSplitter(SQLPanel sqlPanel)
   {
      _sqlPanel = sqlPanel;

      setLayout(new GridLayout(1,1));

      _splitPane = new JSplitPane();
      add(_splitPane);

      _standardDividerSize = _splitPane.getDividerSize();

      _sqlPanel.setMinimumSize(new Dimension(0,0));
      _splitPane.setRightComponent(_sqlPanel);

      _showObjectTree(false, false);

      addComponentListener(new ComponentAdapter()
      {
         @Override
         public void componentResized(ComponentEvent e)
         {
            onComponentResized();
         }
      });
   }

   private void onComponentResized()
   {
      if(false == isSplit())
      {
         _splitPane.setDividerLocation(0);
      }
   }

   public void split(boolean b)
   {
      _showObjectTree(b, true);
   }

   private void _showObjectTree(boolean visible, boolean rememberWidthOnHide)
   {
      if(visible)
      {
         if(null == _objectTreePanel)
         {
            initObjectTree();
         }

         _splitPane.setDividerLocation(Props.getInt(PREF_OBJECT_TREE_SPLIT_DIVIDER_LOC, _sqlPanel.getWidth() / 2));
         _splitPane.setDividerSize(_standardDividerSize);
      }
      else
      {
         if (rememberWidthOnHide)
         {
            saveDividerLocation();
         }
         _splitPane.setDividerSize(0);
         _splitPane.setDividerLocation(0);
      }
   }

   private void initObjectTree()
   {
      _objectTreePanel = new ObjectTreePanel(_sqlPanel.getSession(), ObjectTreePosition.OBJECT_TREE_BESIDES_SQL_PANEL);
      _objectTreePanel.setMinimumSize(new Dimension(0,0));
      _splitPane.setLeftComponent(_objectTreePanel);
      SwingUtilities.invokeLater(() -> Main.getApplication().getPluginManager().objectTreeInSQLTabOpened(_objectTreePanel));
   }


   private void saveDividerLocation()
   {
      Props.putInt(PREF_OBJECT_TREE_SPLIT_DIVIDER_LOC, _splitPane.getDividerLocation());
   }

   public void sessionWindowClosing()
   {
      if(isSplit())
      {
         saveDividerLocation();
      }

      if (null != _objectTreePanel)
      {
         _objectTreePanel.sessionWindowClosing();
      }
   }

   public boolean isSplit()
   {
      return 0 < _splitPane.getDividerSize();
   }

   public IIdentifier getFindEntryPanelIdentifier()
   {
      if(null == _objectTreePanel)
      {
         return null;
      }

      return _objectTreePanel.getFindController().getFindEntryPanel().getIdentifier();
   }

   /**
    * @return null, when ObjectTreePanel is not initialized
    */
   public ObjectTreePanel getObjectTreePanel()
   {
      return _objectTreePanel;
   }
}
