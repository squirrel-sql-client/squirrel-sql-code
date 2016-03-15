package org.squirrelsql.session.sql.tablesearch;

import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Region;
import org.squirrelsql.Props;
import org.squirrelsql.globalicons.GlobalIconNames;
import org.squirrelsql.services.*;
import org.squirrelsql.table.TableLoader;

public class TableSearchCtrl
{
   private final ToggleButton _btnSearch;
   private final Props _props = new Props(getClass());
   private final SearchResultHandler _searchResultHandler;
   private final EditableComboCtrl _editableComboCtrl;

   private SearchPanelVisibleListener _searchPanelVisibleListener;
   private final Region _tableSearchPanelRegion;
   private final TableSearchPanel _tableSearchPanel;
   private Pref _pref = new Pref(getClass());

   public TableSearchCtrl(TableLoader resultTableLoader)
   {
      _btnSearch = new ToggleButton();
      _btnSearch.setTooltip(new Tooltip(new I18n(getClass()).t("search.button.tooltip")));
      _btnSearch.setGraphic(_props.getImageView(GlobalIconNames.SEARCH));

      _btnSearch.setOnAction(e -> updateSearchVisible());


      FxmlHelper<TableSearchPanel> fxmlHelper = new FxmlHelper<>(TableSearchPanel.class);

      _tableSearchPanelRegion = fxmlHelper.getRegion();

      _tableSearchPanel = fxmlHelper.getView();

      configureButton(_tableSearchPanel.btnFindNext, GlobalIconNames.ARROW_DOWN, "button.find.next");
      configureButton(_tableSearchPanel.btnFindPrevious, GlobalIconNames.ARROW_UP, "button.find.previous");
      configureButton(_tableSearchPanel.btnHighlightAllMatches, "highlight.png", "button.highlight");
      configureButton(_tableSearchPanel.btnUnhighlightAll, "unhighlight.png", "button.unhighlight");
      configureButton(_tableSearchPanel.btnResultInOwnTable, "result_in_own_table.png", "button.result.in.own.table");

      _tableSearchPanel.cboSearchType.getItems().addAll(TableSearchType.values());
      _tableSearchPanel.cboSearchType.getSelectionModel().select(0);

      _editableComboCtrl = new EditableComboCtrl(_tableSearchPanel.cboSearchString, getClass().getName(), () -> onFind(true));

      _searchResultHandler = new SearchResultHandler(resultTableLoader);


      _tableSearchPanel.btnFindNext.setOnAction(e -> onFind(true));
      _tableSearchPanel.btnFindPrevious.setOnAction(e -> onFind(false));

      _tableSearchPanel.btnHighlightAllMatches.setOnAction(e -> onHighLightAll());
      _tableSearchPanel.btnUnhighlightAll.setOnAction(e -> _searchResultHandler.unhighlightAll());

      _tableSearchPanel.btnResultInOwnTable.setOnAction(e -> onSearchResultInOwnTable());

   }

   private void onSearchResultInOwnTable()
   {
      String cboEditorText = _editableComboCtrl.getText();
      if(Utils.isEmptyString(cboEditorText))
      {
         return;
      }

      _searchResultHandler.showSearchResultInOwnTable(cboEditorText, _tableSearchPanel.cboSearchType.getSelectionModel().getSelectedItem(), _tableSearchPanel.chkCaseSensitive.isSelected());
   }

   private void onHighLightAll()
   {
      String cboEditorText = _editableComboCtrl.getText();
      if(Utils.isEmptyString(cboEditorText))
      {
         return;
      }

      _searchResultHandler.highlightAll(cboEditorText, _tableSearchPanel.cboSearchType.getSelectionModel().getSelectedItem(), _tableSearchPanel.chkCaseSensitive.isSelected());
   }

   private void onFind(boolean forward)
   {
      String cboEditorText = _editableComboCtrl.getText();
      _editableComboCtrl.addCurrentTextToHistory();

      _searchResultHandler.find(forward, cboEditorText, _tableSearchPanel.cboSearchType.getSelectionModel().getSelectedItem(), _tableSearchPanel.chkCaseSensitive.isSelected());

   }

   private void configureButton(Button button, String iconName, String tooltipTextKey)
   {
      button.setGraphic(_props.getImageView(iconName));
      button.setTooltip(new Tooltip(new I18n(getClass()).t(tooltipTextKey)));
   }

   private void updateSearchVisible()
   {
      if(null != _searchPanelVisibleListener)
      {
         _searchPanelVisibleListener.showPanel(_tableSearchPanelRegion, _btnSearch.isSelected());
      }
   }

   public ToggleButton getSearchButton()
   {
      return _btnSearch;
   }

   public void setOnShowSearchPanel(SearchPanelVisibleListener searchPanelVisibleListener)
   {
      _searchPanelVisibleListener = searchPanelVisibleListener;
   }

   public void setActive(boolean b)
   {
      _searchResultHandler.setActive(b);
   }
}
