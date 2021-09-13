package net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.DataModelImplementationDetails;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.SimpleDataSet;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.action.colorrows.ColorSelectedRowsCommand;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.SquirrelConstants;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import org.apache.commons.lang.StringUtils;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class DataSetFindPanelController
{

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DataSetFindPanelController.class);

   private static final String PREF_KEY_DATASETFIND_TABLESEARCH_STRPREF = "SquirrelSQL.DataSetFind.tableSearch.StrPref_";

   private static final int MAX_HIST_LENGTH = 10;

   private DataSetFindPanel _dataSetFindPanel;

   private TableTraverser _tableTraverser = new TableTraverser();
   private DataSetViewerTablePanel _dataSetViewerTablePanel;
   private FindService _findService;

   private Color _currentColor = SquirrelConstants.FIND_COLOR;
   private Color _traceColor = SquirrelConstants.FIND_COLOR_CURRENT;
   private FindTrace _trace = new FindTrace();
   private String _currentSearchString = null;

   private static enum FindMode
   {
      FORWARD, BACKWARD, HIGHLIGHT
   }

   public DataSetFindPanelController(final DataSetFindPanelListener dataSetFindPanelListener, final ISession session)
   {
      _dataSetFindPanel = new DataSetFindPanel();

      _dataSetFindPanel.btnDown.addActionListener(e -> onFind(FindMode.FORWARD));

      _dataSetFindPanel.btnUp.addActionListener(e -> onFind(FindMode.BACKWARD));

      _dataSetFindPanel.btnHighlightFindResult.addActionListener(e -> onFind(FindMode.HIGHLIGHT));

      _dataSetFindPanel.btnUnhighlightResult.addActionListener(e -> clearFind());

      _dataSetFindPanel.btnShowRowsFoundInTable.addActionListener(e -> onShowRowsFoundInTable(session));

      _dataSetFindPanel.btnChooseMatchesRowColor.addActionListener(e -> onChooseColorForRowsWithMatches());

      _dataSetFindPanel.btnHideFindPanel.addActionListener(e -> dataSetFindPanelListener.hideFindPanel());

      _dataSetFindPanel.chkCaseSensitive.addActionListener(e -> clearFind());

      _dataSetFindPanel.cboMatchType.addItemListener(e -> clearFind());

      for (int i = 0; i <  MAX_HIST_LENGTH; i++)
      {
         String item = Props.getString(PREF_KEY_DATASETFIND_TABLESEARCH_STRPREF + i, null);
         if (null != item)
         {
            _dataSetFindPanel.cboString.addItem(item);
         }
      }
      _dataSetFindPanel.cboString.getEditor().setItem(null);

      initKeyStrokes();
   }

   private void onChooseColorForRowsWithMatches()
   {
      if(0 == _trace.getRowsFound().size())
      {
         Main.getApplication().getMessageHandler().showMessage(s_stringMgr.getString("DataSetFindPanelController.noMatchesToColor"));
         return;
      }

      Color startColor = null;

      int rgb = ColorSelectedRowsCommand.getPreviousRowColorRgb();
      if(rgb != -1)
      {
         startColor = new Color(rgb);
      }

      Color newColor = JColorChooser.showDialog(GUIUtils.getOwningFrame(_dataSetViewerTablePanel.getTable()), s_stringMgr.getString("ColorSelectedRowsCommand.color.selected.rows"), startColor);

      if (null == newColor)
      {
         return;
      }

      ColorSelectedRowsCommand.setPreviousRowColorRgb(newColor);

      for (Integer row : _trace.getRowsFound())
      {
         _dataSetViewerTablePanel.getTable().getColoringService().getRowColorHandler().setColorForRow(row, newColor);
      }
      _dataSetViewerTablePanel.getTable().repaint();
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

   private void onShowRowsFoundInTable(ISession session)
   {
      Window parent = SwingUtilities.windowForComponent(_dataSetFindPanel);

      JDialog dlg = new JDialog(parent, s_stringMgr.getString("DataSetFindPanel.searchResult"));
      dlg.getContentPane().add(new JScrollPane(createSimpleTable(session).getComponent()));

      dlg.setLocation(_dataSetViewerTablePanel.getComponent().getLocationOnScreen());
      dlg.setSize(_findService.getVisibleSize());
      dlg.setVisible(true);

      GUIUtils.centerWithinParent(dlg);
   }


   private DataSetViewerTablePanel createSimpleTable(ISession session)
   {
      try
      {
         ensureFindService();

         List<Object[]> allRows = _findService.getRowsForIndexes(_trace.getRowsFound());
         ColumnDisplayDefinition[] columnDisplayDefinitions = _findService.getColumnDisplayDefinitions();

         SimpleDataSet ods = new SimpleDataSet(allRows, columnDisplayDefinitions);

         DataSetViewerTablePanel dsv = new DataSetViewerTablePanel();

         DataModelImplementationDetails dataModelImplementationDetails = new DataModelImplementationDetails();

         dsv.init(null, dataModelImplementationDetails, session);
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
         Main.getApplication().getMessageHandler().showMessage(s_stringMgr.getString("DataSetFindPanelController.noOccurenceFoundOf", _currentSearchString));
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
            public Color getBackgroundColor(int viewRow, int viewColumn)
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
         case EXACT:
            return viewDataAsString.equals(toMatchAgainst);
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
      reset();
   }

   public void reset()
   {
      _findService = null;
      _trace.clear();
   }

   private Color onGetBackgroundColor(int viewRow, int viewColumn)
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
            Props.putString(PREF_KEY_DATASETFIND_TABLESEARCH_STRPREF + i, "" + _dataSetFindPanel.cboString.getItemAt(i));
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
