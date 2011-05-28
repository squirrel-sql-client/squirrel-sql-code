package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.graph.nondbconst.DndCallback;
import net.sourceforge.squirrel_sql.plugins.graph.querybuilder.QueryFilterController;
import net.sourceforge.squirrel_sql.plugins.graph.querybuilder.QueryFilterListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

class QueryColumnPanel extends JPanel
{
   private static final StringManager s_stringMgr =  StringManagerFactory.getStringManager(QueryColumnPanel.class);


   private JCheckBox chkSelect;
   private JButton btnAggFct;
   private JButton btnFilter;
   private JButton btnSorting;
   private QueryColumnTextField txtColumn;
   private String _tableName;
   private ColumnInfo _columnInfo;
   private ISession _session;
   private GraphPluginResources _graphPluginResources;



   private JPopupMenu _popUpAggregate;
   private JPopupMenu _popUpSorting;

   QueryColumnPanel(final GraphPlugin graphPlugin, String tableName, ColumnInfo columnInfo, DndCallback dndCallback, ISession session)
   {
      super(new BorderLayout());
      _tableName = tableName;
      _columnInfo = columnInfo;
      _session = session;
      _graphPluginResources = new GraphPluginResources(graphPlugin);
      setBorder(BorderFactory.createEmptyBorder());

      JPanel pnlButtons = new JPanel(new GridBagLayout());
      pnlButtons.setBackground(GraphTextAreaFactory.TEXTAREA_BG);

      GridBagConstraints gbc;

      int xPos = 0;

      chkSelect = new JCheckBox();
      chkSelect.setToolTipText(s_stringMgr.getString("QueryColumn.select"));
      chkSelect.setBackground(GraphTextAreaFactory.TEXTAREA_BG);
      chkSelect.setSelected(_columnInfo.getQueryData().isInSelectClause());
      chkSelect.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onChkSelectedChanged();
         }
      });
      gbc = new GridBagConstraints(xPos,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      pnlButtons.add(chkSelect, gbc);

      initAggregate(pnlButtons, ++xPos);

      initFilter(graphPlugin, pnlButtons, ++xPos);

      initSorting(pnlButtons, ++xPos);

      add(pnlButtons, BorderLayout.WEST);
      txtColumn = new QueryColumnTextField(_columnInfo.toString(), dndCallback, _session);
      txtColumn.setEditable(false);
      txtColumn.setBackground(GraphTextAreaFactory.TEXTAREA_BG);
      txtColumn.setBorder(BorderFactory.createEmptyBorder());

      add(txtColumn, BorderLayout.CENTER);
   }

   private void initFilter(final GraphPlugin graphPlugin, JPanel pnlButtons, int xPos)
   {
      GridBagConstraints gbc;
      btnFilter = new JButton();
      initFilterButtonIcon();
      btnFilter.setToolTipText(s_stringMgr.getString("QueryColumn.filterButton"));


      btnFilter.setBackground(GraphTextAreaFactory.TEXTAREA_BG);
      btnFilter.setBorder(BorderFactory.createEmptyBorder());
      btnFilter.setFocusable(false);
      btnFilter.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            showFilterDialog(graphPlugin);
         }
      });

      gbc = new GridBagConstraints(xPos,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,0,5), 0,0);
      pnlButtons.add(btnFilter, gbc);
   }

   private void initAggregate(JPanel pnlButtons, int xPos)
   {
      GridBagConstraints gbc;
      btnAggFct = new JButton();
      btnAggFct.setBackground(GraphTextAreaFactory.TEXTAREA_BG);
      btnAggFct.setBorder(BorderFactory.createEmptyBorder());
      //btnAggFct.setEnabled(false);
      gbc = new GridBagConstraints(xPos,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      pnlButtons.add(btnAggFct, gbc);
      btnAggFct.setEnabled(chkSelect.isSelected());
      btnAggFct.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onBtnAggFct(e);
         }
      });
      onAggSelected(_columnInfo.getQueryData().getAggregateFunction(), false);


      _popUpAggregate = new JPopupMenu();
      for (final AggregateFunctions af : AggregateFunctions.values())
      {
         JMenuItem menuItem = new JMenuItem(af.toString(), _graphPluginResources.getIcon(af.getImage()));
         menuItem.putClientProperty(AggregateFunctions.CLIENT_PROP_NAME, af);

         menuItem.addActionListener(new ActionListener()
         {
            @Override
            public void actionPerformed(ActionEvent e)
            {
               onAggSelected(af, true);
            }
         });

         _popUpAggregate.add(menuItem);
      }
   }

   private void initSorting(JPanel pnlButtons, int xPos)
   {
      GridBagConstraints gbc;
      btnSorting = new JButton();
      btnSorting.setBackground(GraphTextAreaFactory.TEXTAREA_BG);
      btnSorting.setBorder(BorderFactory.createEmptyBorder());
      gbc = new GridBagConstraints(xPos,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,3), 0,0);
      pnlButtons.add(btnSorting, gbc);
      btnSorting.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onBtnSorting(e);
         }
      });
      onSortingSelected(_columnInfo.getQueryData().getSorting(), false);


      _popUpSorting = new JPopupMenu();
      for (final Sorting sorting : Sorting.values())
      {
         JMenuItem menuItem = new JMenuItem(sorting.toString(), _graphPluginResources.getIcon(sorting.getImage()));
         menuItem.putClientProperty(Sorting.CLIENT_PROP_NAME, sorting);

         menuItem.addActionListener(new ActionListener()
         {
            @Override
            public void actionPerformed(ActionEvent e)
            {
               onSortingSelected(sorting, true);
            }
         });

         _popUpSorting.add(menuItem);
      }
   }

   private void onBtnSorting(ActionEvent e)
   {
      _popUpSorting.show(btnSorting, 0, 0);
   }


   private void onChkSelectedChanged()
   {
      _columnInfo.getQueryData().setInSelectClause(chkSelect.isSelected());
      btnAggFct.setEnabled(chkSelect.isSelected());

      if (false == chkSelect.isSelected())
      {
         onAggSelected(AggregateFunctions.NONE, false);
      }

      _columnInfo.getColumnInfoModelEventDispatcher().fireChanged(TableFramesModelChangeType.COLUMN_SELECT);

   }

   private void onAggSelected(AggregateFunctions af, boolean fireChanged)
   {
      btnAggFct.setIcon(_graphPluginResources.getIcon(af.getImage()));
      btnAggFct.setToolTipText(af.getToolTip());
      _columnInfo.getQueryData().setAggregateFunction(af);

      if (fireChanged)
      {
         _columnInfo.getColumnInfoModelEventDispatcher().fireChanged(TableFramesModelChangeType.COLUMN_SELECT);
      }
   }

   private void onSortingSelected(Sorting sorting, boolean fireChanged)
   {
      btnSorting.setIcon(_graphPluginResources.getIcon(sorting.getImage()));
      btnSorting.setToolTipText(sorting.getToolTip());
      _columnInfo.getQueryData().setSorting(sorting);

      if (fireChanged)
      {
         _columnInfo.getColumnInfoModelEventDispatcher().fireChanged(TableFramesModelChangeType.COLUMN_SELECT);
      }
   }

   private void onBtnAggFct(ActionEvent e)
   {
      _popUpAggregate.show(btnAggFct, 0, 0);
   }

   private void initFilterButtonIcon()
   {
      if (_columnInfo.getQueryData().isFiltered())
      {
         btnFilter.setIcon(_graphPluginResources.getIcon(GraphPluginResources.IKeys.FILTER_CHECKED));
      }
      else
      {
         btnFilter.setIcon(_graphPluginResources.getIcon(GraphPluginResources.IKeys.FILTER));
      }
   }

   private void showFilterDialog(GraphPlugin graphPlugin)
   {
      QueryFilterListener queryFilterListener = new QueryFilterListener()
      {
         @Override
         public void filterChanged()
         {
            initFilterButtonIcon();
         }
      };

      Window parent = SwingUtilities.windowForComponent(txtColumn);
      new QueryFilterController(parent,_tableName, _columnInfo, graphPlugin, _session, queryFilterListener);
   }

   int getMaxWidth(ColumnInfo[] allColumnInfos)
   {
      int maxSize = 0;
      FontMetrics fm = txtColumn.getFontMetrics(txtColumn.getFont());

      for (int i = 0; i < allColumnInfos.length; i++)
      {
         int buf = fm.stringWidth(allColumnInfos[i].toString());
         if(maxSize < buf)
         {
            maxSize = buf;
         }
      }

      return maxSize + chkSelect.getWidth() + btnFilter.getWidth();
   }

   public void addColumnMouseListener(MouseListener mouseListener)
   {
      txtColumn.addMouseListener(mouseListener);
   }
}
