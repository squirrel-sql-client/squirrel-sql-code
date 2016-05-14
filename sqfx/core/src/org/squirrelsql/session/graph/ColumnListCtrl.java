package org.squirrelsql.session.graph;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import org.squirrelsql.services.CollectionUtil;
import org.squirrelsql.session.ColumnInfo;
import org.squirrelsql.session.Session;
import org.squirrelsql.session.TableInfo;
import org.squirrelsql.session.objecttree.TableDetailsReader;

import java.util.List;

public class ColumnListCtrl
{

   private final ListView<GraphColumn> _listView;

   public ColumnListCtrl(Session session, TableInfo tableInfo)
   {
      List<ColumnInfo> columns = session.getSchemaCacheValue().get().getColumns(tableInfo);

      PrimaryKeyInfo pkInfo = new PrimaryKeyInfo(TableDetailsReader.readPrimaryKey(session, tableInfo));
      ImportedKeysInfo impKeysInfo = new ImportedKeysInfo(TableDetailsReader.readImportedKeys(session, tableInfo));
      //ExportedKeysInfo expKeysInfo = new ExportedKeysInfo(TableDetailsReader.readExportedKeys(_session, tableInfo));

      _listView = new ListView<>(FXCollections.observableArrayList(CollectionUtil.transform(columns, c -> new GraphColumn(c, pkInfo, impKeysInfo))));

      _listView.setCellFactory(p -> new ColumnListCell());
   }

   public ListView<GraphColumn> getColumnList()
   {
      return _listView;
   }
}
