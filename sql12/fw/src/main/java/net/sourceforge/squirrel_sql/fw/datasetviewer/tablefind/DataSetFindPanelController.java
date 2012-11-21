package net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind;

import net.sourceforge.squirrel_sql.fw.datasetviewer.*;
import net.sourceforge.squirrel_sql.fw.util.*;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public class DataSetFindPanelController
{

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DataSetFindPanelController.class);

   private static final String PREF_KEY_DATASETFIND_TABLESEARCH_STRPREF = "SquirrelSQL.DataSetFind.tableSearch.StrPref_";

   private static final int MAX_HIST_LENGTH = 10;

   private DataSetFindPanel _dataSetFindPanel;

   private TableTraverser _tableTraverser = new TableTraverser();
   private DataSetViewerTablePanel _dataSetViewerTablePanel;
   private FindService _findService;

   private FindMarkColor _currentColor = new FindMarkColor(SquirrelConstants.TRACE_COLOR);
   private FindMarkColor _traceColor = new FindMarkColor(SquirrelConstants.TRACE_COLOR_CURRENT);
   private FindTrace _trace = new FindTrace();
   private String _currentSearchString = null;
   private IMessageHandler _messageHandler;

   private static enum FindMode
   {
      FORWARD, BACKWARD, HIGHLIGHT
   }

   public DataSetFindPanelController(IMessageHandler messageHandler, final DataSetFindPanelListener dataSetFindPanelListener)
   {
      _messageHandler = messageHandler;
      _dataSetFindPanel = new DataSetFindPanel();

      _dataSetFindPanel.btnDown.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onFind(FindMode.FORWARD);
         }
      });

      _dataSetFindPanel.btnUp.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onFind(FindMode.BACKWARD);
         }
      });

      _dataSetFindPanel.btnHighlightFindResult.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onFind(FindMode.HIGHLIGHT);
         }
      });

      _dataSetFindPanel.btnUnhighlightResult.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            clearFind();
         }
      });

      _dataSetFindPanel.btnShowRowsFoundInTable.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onShowRowsFoundInTable();
         }
      });




      _dataSetFindPanel.btnHideFindPanel.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            dataSetFindPanelListener.hideFindPanel();
         }
      });

      _dataSetFindPanel.chkCaseSensitive.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            clearFind();
         }
      });

      _dataSetFindPanel.cboMatchType.addItemListener(new ItemListener()
      {
         @Override
         public void itemStateChanged(ItemEvent e)
         {
            clearFind();
         }
      });

      for (int i = 0; i <  MAX_HIST_LENGTH; i++)
      {
         String item = Preferences.userRoot().get(PREF_KEY_DATASETFIND_TABLESEARCH_STRPREF + i, null);
         if (null != item)
         {
            _dataSetFindPanel.cboString.addItem(item);
         }
      }
      _dataSetFindPanel.cboString.getEditor().setItem(null);

      initKeyStrokes();
   }

   private void initKeyStrokes()
   {
      Action findNextAction = new AbstractAction("DataSetFind.FindNext")
      {
         public void actionPerformed(ActionEvent e)
         {
            onFind(true);
         }
      };

      Action findPrevAction = new AbstractAction("DataSetFind.FindPrev")
      {
         public void actionPerformed(ActionEvent e)
         {
            onFind(false);
         }
      };

      Action unhighlightAction = new AbstractAction("DataSetFind.Unhighlight")
      {
         public void actionPerformed(ActionEvent e)
         {
            _dataSetFindPanel.btnUnhighlightResult.doClick();
         }
      };

      EscapeAction escapeAction = new EscapeAction(_dataSetFindPanel.btnUnhighlightResult, _dataSetFindPanel.btnHideFindPanel);

      JComponent comp = (JComponent) _dataSetFindPanel.cboString.getEditor().getEditorComponent();
      comp.registerKeyboardAction(findNextAction, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), JComponent.WHEN_FOCUSED);
      comp.registerKeyboardAction(findNextAction, KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0, false), JComponent.WHEN_FOCUSED);

      comp.registerKeyboardAction(findPrevAction, KeyStroke.getKeyStroke(KeyEvent.VK_F3, InputEvent.SHIFT_DOWN_MASK, false), JComponent.WHEN_FOCUSED);

      comp.registerKeyboardAction(escapeAction, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false), JComponent.WHEN_FOCUSED);
   }

   private void onFind(boolean next)
   {
      if (next)
      {
         _dataSetFindPanel.btnDown.doClick();
      }
      else
      {
         _dataSetFindPanel.btnUp.doClick();
      }
   }

   private void onShowRowsFoundInTable()
   {
      Window parent = SwingUtilities.windowForComponent(_dataSetFindPanel);

      JDialog dlg = new JDialog(parent, s_stringMgr.getString("DataSetFindPanel.searchResult"));
      dlg.getContentPane().add(new JScrollPane(createSimpleTable().getComponent()));

      dlg.setLocation(_dataSetViewerTablePanel.getComponent().getLocationOnScreen());
      dlg.setSize(_findService.getVisibleSize());
      dlg.setVisible(true);
   }


   private DataSetViewerTablePanel createSimpleTable()
   {
      try
      {
         ensureFindService();

         List<Object[]> allRows = _findService.getRowsForIndexes(_trace.getRowsFound());
         ColumnDisplayDefinition[] columnDisplayDefinitions = _findService.getColumnDisplayDefinitions();

         SimpleDataSet ods = new SimpleDataSet(allRows, columnDisplayDefinitions);

         DataSetViewerTablePanel dsv = new DataSetViewerTablePanel();

         IDataModelImplementationDetails dataModelImplementationDetails = new IDataModelImplementationDetails()
         {
            @Override
            public String getStatementSeparator()
            {
               return ";";
            }
         };

         dsv.init(null, dataModelImplementationDetails);
         dsv.show(ods);
         return dsv;
      }
      catch (DataSetException e)
      {
         throw new RuntimeException(e);
      }
   }


   public void wasHidden()
   {
      clearFind();
   }


   private void clearFind()
   {
      _trace.clear();
      ensureFindService();
      _findService.repaintAll();
      _tableTraverser.reset();
   }

   private void onFind(FindMode findMode)
   {
      checkDataSetViewerPanel();

      ensureFindService();


      String searchString = "" + _dataSetFindPanel.cboString.getEditor().getItem();


      if(false == StringUtils.equals(searchString, _currentSearchString))
      {
         _trace.clear();
         _findService.repaintAll();
         _tableTraverser.reset();
      }

      _currentSearchString = searchString;

      if(null == _currentSearchString || false == _tableTraverser.hasRows() || StringUtilities.isEmpty(_currentSearchString))
      {
         return;
      }

      addToComboList(_currentSearchString);



      boolean matchFound = false;
      for(int i=0; i < _tableTraverser.getCellCount(); ++i)
      {
         if (FindMode.FORWARD == findMode || FindMode.HIGHLIGHT == findMode)
         {
            _tableTraverser.forward();
         }
         else
         {
            _tableTraverser.backward();
         }

         if(matches(_currentSearchString, _findService.getViewDataAsString(_tableTraverser.getRow(), _tableTraverser.getCol())))
         {
            matchFound = true;

            if (FindMode.HIGHLIGHT != findMode)
            {
               _findService.scrollToVisible(_tableTraverser.getRow(), _tableTraverser.getCol());
            }

            _findService.repaintCell(_tableTraverser.getRow(), _tableTraverser.getCol());

            if (null != _trace.getCurrent())
            {
               _findService.repaintCell(_trace.getCurrent().x, _trace.getCurrent().y);
            }
            _trace.add(_tableTraverser.getRow(), _tableTraverser.getCol());

            if (FindMode.HIGHLIGHT != findMode)
            {
               return;
            }
         }
      }

      if (false == matchFound)
      {
         _messageHandler.showMessage(s_stringMgr.getString("DataSetFindPanelController.noOccurenceFoundOf", _currentSearchString));
      }

   }

   private FindService ensureFindService()
   {
      if(null == _findService)
      {
         _findService = _dataSetViewerTablePanel.createFindService();

         _findService.setFindServiceCallBack(new FindServiceCallBack()
         {
            @Override
            public FindMarkColor getBackgroundColor(int viewRow, int viewColumn)
            {
               return onGetBackgroundColor(viewRow, viewColumn);
            }

            @Override
            public void tableCellStructureChanged()
            {
               clearFind();
            }
         });

         _tableTraverser.setFindService(_findService);

      }

      return _findService;
   }


   private void checkDataSetViewerPanel()
   {
      if(null == _dataSetViewerTablePanel)
      {
         throw new IllegalStateException("Find panel should not be visible when _dataSetViewerTablePanel is null");
      }
   }

   private boolean matches(String toMatchAgainst, String viewDataAsString)
   {
      DataSetFindPanel.MatchTypeCboItem sel = (DataSetFindPanel.MatchTypeCboItem) _dataSetFindPanel.cboMatchType.getSelectedItem();

      if(false == _dataSetFindPanel.chkCaseSensitive.isSelected())
      {
         if (DataSetFindPanel.MatchTypeCboItem.REG_EX != sel)
         {
            toMatchAgainst = toMatchAgainst.toLowerCase();
         }
         viewDataAsString = viewDataAsString.toLowerCase();
      }

      switch (sel)
      {
         case CONTAINS:
            return viewDataAsString.contains(toMatchAgainst);
         case STARTS_WITH:
            return viewDataAsString.startsWith(toMatchAgainst);
         case ENDS_WITH:
            return viewDataAsString.endsWith(toMatchAgainst);
         case REG_EX:
            return viewDataAsString.matches(toMatchAgainst);
      }

      throw new IllegalArgumentException("Unknown match type " + sel);
   }

   public DataSetFindPanel getPanel()
   {
      return _dataSetFindPanel;
   }


   public void setDataSetViewerTablePanel(DataSetViewerTablePanel dataSetViewerTablePanel)
   {
      _dataSetViewerTablePanel = dataSetViewerTablePanel;
      _findService = null;
      _trace.clear();
   }

   private FindMarkColor onGetBackgroundColor(int viewRow, int viewColumn)
   {
      String searchString = "" + _dataSetFindPanel.cboString.getEditor().getItem();
      if(null == searchString)
      {
         return null;
      }

      if(_trace.contains(viewRow, viewColumn))
      {
         if (_trace.isCurrent(viewRow, viewColumn))
         {
            return _currentColor;
         }
         else
         {
            return _traceColor;
         }
      }
      else
      {
         return null;
      }

   }

   private void addToComboList(String searchString)
   {
      for (int i = 0; i < _dataSetFindPanel.cboString.getItemCount(); i++)
      {
          if(searchString.equals(_dataSetFindPanel.cboString.getItemAt(i)))
          {
             _dataSetFindPanel.cboString.removeItemAt(i);
          }
      }
      ((DefaultComboBoxModel)_dataSetFindPanel.cboString.getModel()).insertElementAt(searchString, 0);


      ArrayList itemsToRemove = new ArrayList();
      for (int i = 0; i <  _dataSetFindPanel.cboString.getItemCount(); i++)
      {
         if (MAX_HIST_LENGTH > i)
         {
            Preferences.userRoot().put(PREF_KEY_DATASETFIND_TABLESEARCH_STRPREF + i, "" + _dataSetFindPanel.cboString.getItemAt(i));
         }
         else
         {
            itemsToRemove.add(_dataSetFindPanel.cboString.getItemAt(i));
         }
      }

      for (Object item : itemsToRemove)
      {
         _dataSetFindPanel.cboString.removeItem(item);
      }

      _dataSetFindPanel.cboString.setSelectedIndex(0);

   }

   public void focusTextField()
   {
      _dataSetFindPanel.cboString.getEditor().getEditorComponent().requestFocus();

   }



}
