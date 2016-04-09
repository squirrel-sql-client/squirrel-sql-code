package org.squirrelsql.session.graph;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import org.squirrelsql.Props;
import org.squirrelsql.services.I18n;
import org.squirrelsql.session.Session;
import org.squirrelsql.session.objecttree.ObjectTreeFilterCtrl;
import org.squirrelsql.session.objecttree.ObjectTreeFilterCtrlMode;

public class GraphTabHeaderCtrl
{

   private final BorderPane _graphTabHeader;
   private final I18n _i18n = new I18n(getClass());
   private final Props _props = new Props(getClass());
   private GraphTableDndChannel _graphTableDndChannel;

   public GraphTabHeaderCtrl(GraphTableDndChannel graphTableDndChannel, Session session)
   {
      _graphTableDndChannel = graphTableDndChannel;

      _graphTabHeader = new BorderPane();

      _graphTabHeader.setCenter(new Label(_i18n.t("graph.new.graph.title")));

      Button btnAddTable = new Button();
      btnAddTable.setPadding(new Insets(3));
      btnAddTable.setGraphic(_props.getImageView("addTable.png"));
      btnAddTable.setTooltip(new Tooltip(_i18n.t("graph.add.table.button.tooltip")));
      BorderPane.setMargin(btnAddTable, new Insets(5));

      btnAddTable.setOnAction(e-> onAddTables(session));

      _graphTabHeader.setRight(btnAddTable);
   }

   private void onAddTables(Session session)
   {
      new ObjectTreeFilterCtrl(session, "", _graphTableDndChannel);
   }

   public Node getGraphTabHeader()
   {
      return _graphTabHeader;
   }
}
