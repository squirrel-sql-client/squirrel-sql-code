package net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.globalsearch.GlobalSearchType;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.gui.EditableComboBoxHandler;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.action.colorrows.ColorSelectionCommand;
import net.sourceforge.squirrel_sql.fw.gui.action.rowselectionwindow.RowsWindowFrame;
import net.sourceforge.squirrel_sql.fw.resources.LibraryResources;
import net.sourceforge.squirrel_sql.fw.util.SquirrelConstants;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import org.apache.commons.lang3.StringUtils;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.List;

public class DataSetFindPanelController
{

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DataSetFindPanelController.class);

   private static final String PREF_KEY_DATASETFIND_TABLESEARCH_STRPREF_PREFIX = "SquirrelSQL.DataSetFind.tableSearch.StrPref_";

   private final DataSetFindPanelListener _dataSetFindPanelListener;

   private DataSetFindPanel _dataSetFindPanel;
   private EditableComboBoxHandler _editableComboBoxHandler;

   private TableTraverser _tableTraverser = new TableTraverser();
   private DataSetViewerTablePanel _dataSetViewerTablePanel;
   private FindService _findService;

   private Color _currentColor = SquirrelConstants.FIND_COLOR;
   private Color _traceColor = SquirrelConstants.FIND_COLOR_CURRENT;
   private FindTrace _trace = new FindTrace();
   private String _currentSearchString = null;
   private ColsToSearchHolder _colsToSearchHolder = ColsToSearchHolder.UNFILTERED;
   private boolean _inExecutingGlobalSearch = false;

   private enum FindMode
   {
      FORWARD, BACKWARD, HIGHLIGHT
   }

   public DataSetFindPanelController(final ISession session, final DataSetFindPanelListener dataSetFindPanelListener)
   {
      _dataSetFindPanelListener = dataSetFindPanelListener;
      _dataSetFindPanel = new DataSetFindPanel();

      _dataSetFindPanel.btnDown.addActionListener(e -> onFind(FindMode.FORWARD));

      _dataSetFindPanel.btnUp.addActionListener(e -> onFind(FindMode.BACKWARD));

      _dataSetFindPanel.btnHighlightFindResult.addActionListener(e -> onFind(FindMode.HIGHLIGHT));

      _dataSetFindPanel.btnUnhighlightResult.addActionListener(e -> clearFind());

      _dataSetFindPanel.btnShowRowsFoundInTable.addActionListener(e -> onShowRowsFoundInTable(session));

      _dataSetFindPanel.btnColorMatchedCells.addActionListener(e -> onColorMatchedCells());

      _dataSetFindPanel.btnNarrowColsToSearch.addActionListener(e -> onNarrowColsToSearch());

      _dataSetFindPanel.btnHideFindPanel.addActionListener(e -> _dataSetFindPanelListener.hideFindPanel());

      _dataSetFindPanel.chkCaseSensitive.addActionListener(e -> clearFind());

      _dataSetFindPanel.cboMatchType.addItemListener(e -> clearFind());

      _editableComboBoxHandler = new EditableComboBoxHandler(_dataSetFindPanel.cboString, PREF_KEY_DATASETFIND_TABLESEARCH_STRPREF_PREFIX);

      initKeyStrokes();
   }

   private void onNarrowColsToSearch()
   {
      _colsToSearchHolder = new NarrowColsToSearchCtrl(_colsToSearchHolder, ensureFindService()).getColsToSearchFilter();

      _dataSetFindPanel.btnNarrowColsToSearch.setIcon(Main.getApplication().getResourcesFw().getIcon(LibraryResources.IImageNames.SELECT_COLUMN));
      if(_colsToSearchHolder.isNarrowed())
      {
         _dataSetFindPanel.btnNarrowColsToSearch.setIcon(Main.getApplication().getResourcesFw().getIcon(LibraryResources.IImageNames.SELECT_COLUMN_CHECKED));
      }
   }

   private void onColorMatchedCells()
   {
      if(0 == _trace.getCellsFound().size())
      {
         Main.getApplication().getMessageHandler().showMessage(s_stringMgr.getString("DataSetFindPanelController.noMatchesToColor"));
         return;
      }

      Color startColor = null;

      int rgb = ColorSelectionCommand.getPreviousColorRgb();
      if(rgb != -1)
      {
         startColor = new Color(rgb);
      }

      Color newColor = JColorChooser.showDialog(GUIUtils.getOwningFrame(_dataSetViewerTablePanel.getTable()), s_stringMgr.getString("DataSetFindPanel.colorMatchedCells"), startColor);

      if (null == newColor)
      {
         return;
      }

      ColorSelectionCommand.setPreviousRowColorRgb(newColor);

      for (Point cell : _trace.getCellsFound())
      {
         // A little y vs. y mismatch here :o)
         Point buf = new Point();
         buf.x = cell.y;
         buf.y = _dataSetViewerTablePanel.getTable().getSortableTableModel().transformToModelRow(cell.x);

         _dataSetViewerTablePanel.getTable().getColoringService().getUserColorHandler().setColorForCell(buf, newColor);
      }

      _dataSetFindPanel.btnUnhighlightResult.doClick(300);

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
      List<Object[]> allRows = ensureFindService().getRowsForViewIndexes(_trace.getRowsFound());
      ColumnDisplayDefinition[] columnDisplayDefinitions = ensureFindService().getColumnDisplayDefinitions();

      RowsWindowFrame rowsWindowFrame =
            new RowsWindowFrame(SwingUtilities.windowForComponent(_dataSetFindPanel), allRows, List.of(columnDisplayDefinitions), session);

      Main.getApplication().getRowsWindowFrameRegistry().add(rowsWindowFrame);
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


      String searchString = _editableComboBoxHandler.getItem();


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

      _editableComboBoxHandler.addOrReplaceCurrentItem(_currentSearchString);

      boolean matchFound = false;
      for(int i = 0; i < _tableTraverser.getCellCount(); ++i)
      {
         if (FindMode.FORWARD == findMode || FindMode.HIGHLIGHT == findMode)
         {
            _tableTraverser.forward();
         }
         else
         {
            _tableTraverser.backward();
         }

         if(   _colsToSearchHolder.isToSearch(_tableTraverser.getCol())
            && matches(_currentSearchString, _findService.getViewDataAsString(_tableTraverser.getRow(), _tableTraverser.getCol())))
         {
            matchFound = true;

            if (FindMode.HIGHLIGHT != findMode)
            {
               if(_inExecutingGlobalSearch)
               {
                  // Other trials to cope with table search panel being displayed after the result table was scrolled to the positon of a finding.
                  // The preferable way turned out to introduce the parameter adjustScrollPositonDueToTableSearchPanelBeingDisplayedLater.
                  //SwingUtilities.invokeLater(() -> _findService.scrollToVisible(_tableTraverser.getRow(), _tableTraverser.getCol(), true));
                  //GUIUtils.executeDelayed(() -> _findService.scrollToVisible(_tableTraverser.getRow(), _tableTraverser.getCol(), true), 300);

                  _findService.scrollToVisible(_tableTraverser.getRow(), _tableTraverser.getCol(), true, true);
               }
               else
               {
                  _findService.scrollToVisible(_tableTraverser.getRow(), _tableTraverser.getCol(), true, false);
               }
            }

            _findService.repaintCell(_tableTraverser.getRow(), _tableTraverser.getCol());

            if (null != _trace.getCurrent())
            {
               _findService.repaintCell(_trace.getCurrent().x, _trace.getCurrent().y);
            }
            _trace.add(_tableTraverser.getRow(), _tableTraverser.getCol());

            if (FindMode.HIGHLIGHT != findMode)
            {
               if(matchFound)
               {
                  _dataSetFindPanelListener.matchFound(_currentSearchString, getSelectedMatchType(), _dataSetFindPanel.chkCaseSensitive.isSelected());
               }
               return;
            }
         }
      }

      if (false == matchFound && false == _inExecutingGlobalSearch)
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
      DataSetSearchMatchType sel = getSelectedMatchType();

      if(false == _dataSetFindPanel.chkCaseSensitive.isSelected())
      {
         if (DataSetSearchMatchType.REG_EX != sel)
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

   private DataSetSearchMatchType getSelectedMatchType()
   {
      DataSetSearchMatchType sel = (DataSetSearchMatchType) _dataSetFindPanel.cboMatchType.getSelectedItem();
      return sel;
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
      String searchString = _editableComboBoxHandler.getItem();
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

   public void focusTextField()
   {
      _editableComboBoxHandler.focus();
   }

   public FirstSearchResult executeFindTillFirstResult(String textToSearch, GlobalSearchType globalSearchType)
   {
      try
      {
         _inExecutingGlobalSearch = true;

         _editableComboBoxHandler.addOrReplaceCurrentItem(textToSearch);
         _dataSetFindPanel.cboMatchType.setSelectedItem(DataSetSearchMatchType.ofGlobalSearchType(globalSearchType));
         _dataSetFindPanel.chkCaseSensitive.setSelected(globalSearchType == GlobalSearchType.CONTAINS); // As opposed to GlobalSearchType.CONTAINS_IGNORE_CASE
         _dataSetFindPanel.btnUnhighlightResult.doClick();
         _dataSetFindPanel.btnDown.doClick();

         if(-1 != _tableTraverser.getCol())
         {
            return new FirstSearchResult(_findService.getViewDataAsString(_tableTraverser.getRow(), _tableTraverser.getCol()), textToSearch, globalSearchType);
         }

         return FirstSearchResult.EMPTY;
      }
      finally
      {
         _inExecutingGlobalSearch = false;
      }
   }
}
