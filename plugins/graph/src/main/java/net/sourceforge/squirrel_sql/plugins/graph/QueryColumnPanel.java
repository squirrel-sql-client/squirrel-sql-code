package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

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
   private QueryColumnTextField txtColumn;
   private String _tableName;
   private ColumnInfo _columnInfo;
   private ISession _session;
   private GraphPluginResources _graphPluginResources;



   private JPopupMenu _popUp;

   QueryColumnPanel(final GraphPlugin graphPlugin, String tableName, ColumnInfo columnInfo, DndCallback dndCallback, ISession session)
   {
      super(new BorderLayout());
      _tableName = tableName;
      _columnInfo = columnInfo;
      _session = session;
      _graphPluginResources = new GraphPluginResources(graphPlugin);

      JPanel pnlButtons = new JPanel(new GridBagLayout());
      pnlButtons.setBackground(GraphTextAreaFactory.TEXTAREA_BG);

      GridBagConstraints gbc;

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
      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      pnlButtons.add(chkSelect, gbc);

      btnAggFct = new JButton();
      btnAggFct.setBackground(GraphTextAreaFactory.TEXTAREA_BG);
      btnAggFct.setBorder(BorderFactory.createEmptyBorder());
      //btnAggFct.setEnabled(false);
      gbc = new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
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


      _popUp = new JPopupMenu();
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

         _popUp.add(menuItem);
      }


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

      gbc = new GridBagConstraints(2,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,0,3), 0,0);
      pnlButtons.add(btnFilter, gbc);

      add(pnlButtons, BorderLayout.WEST);
      txtColumn = new QueryColumnTextField(_columnInfo.toString(), dndCallback, _session);
      txtColumn.setEditable(false);
      txtColumn.setBackground(GraphTextAreaFactory.TEXTAREA_BG);
      txtColumn.setBorder(BorderFactory.createEmptyBorder());

      add(txtColumn, BorderLayout.CENTER);
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

   private void onBtnAggFct(ActionEvent e)
   {
      _popUp.show(btnAggFct, 0, 0);
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

      new QueryFilterController(_tableName, _columnInfo, graphPlugin, _session, queryFilterListener);
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
