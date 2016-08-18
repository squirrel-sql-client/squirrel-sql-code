package org.squirrelsql.session.graph;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import org.squirrelsql.Props;
import org.squirrelsql.services.I18n;

public class GraphTabHeaderCtrl
{

   private final BorderPane _graphTabHeader;
   private final I18n _i18n = new I18n(getClass());
   private final Props _props = new Props(getClass());
   private final Label _lblTabTitle = new Label();

   public GraphTabHeaderCtrl(GraphChannel graphChannel, String tabTitle)
   {

      _graphTabHeader = new BorderPane();

      _lblTabTitle.setText(tabTitle);
      _graphTabHeader.setCenter(_lblTabTitle);

      ToggleButton btnToggleShowToolbar = new ToggleButton();
      btnToggleShowToolbar.setPadding(new Insets(3));
      btnToggleShowToolbar.setGraphic(_props.getImageView("show-toolbar.png"));
      btnToggleShowToolbar.setTooltip(new Tooltip(_i18n.t("graph.toggle.show.toolbar")));
      btnToggleShowToolbar.setSelected(true);
      BorderPane.setMargin(btnToggleShowToolbar, new Insets(5));

      btnToggleShowToolbar.setOnAction(e-> onShowToolbar(graphChannel, btnToggleShowToolbar));

      graphChannel.setTabTitleListener((title) -> _lblTabTitle.setText(title));

      _graphTabHeader.setRight(btnToggleShowToolbar);
   }

   private void onShowToolbar(GraphChannel graphChannel, ToggleButton btnToggleShowToolbar)
   {
      graphChannel.showToolBar(btnToggleShowToolbar.isSelected());
      graphChannel.selectGraphTab();
   }

   public Node getGraphTabHeader()
   {
      return _graphTabHeader;
   }
}
