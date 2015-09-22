package org.squirrelsql.session.sql.tablesearch;

import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Region;
import org.squirrelsql.Props;
import org.squirrelsql.services.FxmlHelper;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.Utils;
import org.squirrelsql.table.TableLoader;

public class TableSearchCtrl
{
   private final ToggleButton _btnSearch;
   private final Props _props = new Props(getClass());
   private final SearchResultHandler _searchResultHandler;

   private SearchPanelVisibleListener _searchPanelVisibleListener;
   private final Region _tableSearchPanelRegion;
   private final TableSearchPanel _tableSearchPanel;

   public TableSearchCtrl(TableLoader resultTableLoader)
   {
      _btnSearch = new ToggleButton();
      _btnSearch.setTooltip(new Tooltip(new I18n(getClass()).t("search.button.tooltip")));
      _btnSearch.setGraphic(_props.getImageView("search.png"));

      _btnSearch.setOnAction(e -> updateSearchVisible());


      FxmlHelper<TableSearchPanel> fxmlHelper = new FxmlHelper<>(TableSearchPanel.class);

      _tableSearchPanelRegion = fxmlHelper.getRegion();

      _tableSearchPanel = fxmlHelper.getView();

      configureButton(_tableSearchPanel.btnFindNext, "arrow_down.png", "button.find.next");
      configureButton(_tableSearchPanel.btnFindPrevious, "arrow_up.png", "button.find.previous");
      configureButton(_tableSearchPanel.btnHighlightAllMatches, "highlight.png", "button.highlight");
      configureButton(_tableSearchPanel.btnUnhighlightAll, "unhighlight.png", "button.unhighlight");
      configureButton(_tableSearchPanel.btnResultInOwnTable, "result_in_own_table.png", "button.result.in.own.table");

      _tableSearchPanel.cboSearchType.getItems().addAll(TableSearchType.values());
      _tableSearchPanel.cboSearchType.getSelectionModel().select(0);

      _tableSearchPanel.cboSearchString.setEditable(true);


      _searchResultHandler = new SearchResultHandler(resultTableLoader);


      _tableSearchPanel.btnFindNext.setOnAction(e -> onFind(true));
      _tableSearchPanel.btnFindPrevious.setOnAction(e -> onFind(false));

      _tableSearchPanel.btnHighlightAllMatches.setOnAction(e -> onHighLightAll());
      _tableSearchPanel.btnUnhighlightAll.setOnAction(e -> _searchResultHandler.unhighlightAll());

   }

   private void onHighLightAll()
   {
      String cboEditorText = _tableSearchPanel.cboSearchString.getEditor().getText();
      if(Utils.isEmptyString(cboEditorText))
      {
         return;
      }

      _searchResultHandler.highlightAll(cboEditorText, _tableSearchPanel.cboSearchType.getSelectionModel().getSelectedItem(), _tableSearchPanel.chkCaseSensitive.isSelected());
   }

   private void onFind(boolean forward)
   {
      String cboEditorText = _tableSearchPanel.cboSearchString.getEditor().getText();
      if(Utils.isEmptyString(cboEditorText))
      {
         return;
      }

      _tableSearchPanel.cboSearchString.getItems().remove(cboEditorText);
      _tableSearchPanel.cboSearchString.getItems().add(0, cboEditorText);
      _tableSearchPanel.cboSearchString.getSelectionModel().select(0);


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
}
