package org.squirrelsql.session.graph;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollToEvent;
import javafx.scene.input.ScrollEvent;
import org.squirrelsql.services.CollectionUtil;
import org.squirrelsql.session.ColumnInfo;
import org.squirrelsql.session.Session;
import org.squirrelsql.session.TableInfo;
import org.squirrelsql.session.graph.graphdesktop.Window;
import org.squirrelsql.session.objecttree.TableDetailsReader;
import org.squirrelsql.workaround.ListViewScrollEventWA;

import java.util.*;

public class ColumnListCtrl
{

   private final ListView<GraphColumn> _listView;
   private final ColumnPositionHelper _columnPositionHelper;
   private Window _window;

   public ColumnListCtrl(Session session, TableInfo tableInfo, Window window, Runnable scrollListener)
   {
      _window = window;
      List<ColumnInfo> columns = session.getSchemaCacheValue().get().getColumns(tableInfo);

      PrimaryKeyInfo pkInfo = new PrimaryKeyInfo(TableDetailsReader.readPrimaryKey(session, tableInfo));
      ImportedKeysInfo impKeysInfo = new ImportedKeysInfo(TableDetailsReader.readImportedKeys(session, tableInfo));
      //ExportedKeysInfo expKeysInfo = new ExportedKeysInfo(TableDetailsReader.readExportedKeys(_session, tableInfo));

      _listView = new ListView<>(FXCollections.observableArrayList(CollectionUtil.transform(columns, c -> new GraphColumn(c, pkInfo, impKeysInfo))));

      _columnPositionHelper = new ColumnPositionHelper(_listView, _window);

      _listView.setCellFactory(p -> _columnPositionHelper.registerCell(new ColumnListCell()));

      new ListViewScrollEventWA(scrollListener, _listView);

   }

   public ListView<GraphColumn> getColumnListView()
   {
      return _listView;
   }

   public PkSpec getPkSpec(TableWindowSide windowSide)
   {

      ArrayList<Point2D> pkPoints = new ArrayList<>();

      for (GraphColumn graphColumn : _listView.getItems())
      {
         if(graphColumn.belongsToPk())
         {
            double pkPointX;
            if(TableWindowSide.LEFT == windowSide)
            {
               pkPointX = _window.getBoundsInParent().getMinX();
            }
            else
            {
               pkPointX = _window.getBoundsInParent().getMaxX();
            }

            double pkPointY = _columnPositionHelper.getMiddleYOfColumn(graphColumn);
            pkPoints.add(new Point2D(pkPointX, pkPointY));

         }
      }

      if (0 < pkPoints.size())
      {
         return new PkSpec(pkPoints, windowSide);
      }
      else
      {
         return null;
      }
   }


   public List<FkSpec> getFkSpecsTo(TableInfo toPkTable, TableWindowSide windowSide)
   {
      List<FkSpec> ret = new ArrayList<>();

      List<String> fkNames = getFkNamesBelongingToPk(toPkTable);

      for (String fkName : fkNames)
      {
         ArrayList<Point2D> fkPoints = new ArrayList<>();

         for (GraphColumn graphColumn : _listView.getItems())
         {
            if(graphColumn.belongsToFk(fkName))
            {
               double fkPointX;
               if(TableWindowSide.LEFT == windowSide)
               {
                  fkPointX = _window.getBoundsInParent().getMaxX();
               }
               else
               {
                  fkPointX = _window.getBoundsInParent().getMinX();
               }

               double pkPointY = _columnPositionHelper.getMiddleYOfColumn(graphColumn);
               fkPoints.add(new Point2D(fkPointX, pkPointY));
            }
         }

         if(0 < fkPoints.size())
         {
            ret.add(new FkSpec(fkName, fkPoints, windowSide));
         }
      }

      return ret;
   }

   private List<String> getFkNamesBelongingToPk(TableInfo toPkTable)
   {
      HashSet<String> ret = new HashSet<>();

      for (GraphColumn graphColumn : _listView.getItems())
      {
         String fkName = graphColumn.getFkNameTo(toPkTable);

         if(null != fkName)
         {
            ret.add(fkName);
         }
      }

      return Arrays.asList(ret.toArray(new String[0]));
   }
}
