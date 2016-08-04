package org.squirrelsql.session.graph;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import org.squirrelsql.session.Session;
import org.squirrelsql.session.TableInfo;
import org.squirrelsql.session.graph.graphdesktop.Window;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TableWindowCtrl
{
   private Session _session;
   private final Window _window;
   private ColumnListCtrl _columnListCtrl;
   private TableInfo _tableInfo;
   private HashMap<String, PersistentFkProps> _persistentFkPropsByFkName = new HashMap<>();

   public TableWindowCtrl(Session session, TableInfo tableInfo, double x, double y, double width, double height, DrawLinesListener drawLinesListener)
   {
      _session = session;
      _tableInfo = tableInfo;

      _window = new Window(_tableInfo.getName());

      _columnListCtrl = new ColumnListCtrl(_session, _tableInfo, _window, () -> drawLinesListener.drawLines(TableWindowCtrl.this));

      Pane contentPane = new BorderPane(_columnListCtrl.getColumnListView());

      _window.setContentPane(contentPane);

      _window.setLayoutX(x);
      _window.setLayoutY(y);

      _window.setPrefSize(width, height);

      _window.setCtrl(this);

      _window.boundsInParentProperty().addListener((observable, oldValue, newValue) -> drawLinesListener.drawLines(TableWindowCtrl.this));

      _window.setOnClosedAction(e -> drawLinesListener.drawLines(TableWindowCtrl.this));
   }


   public Window getWindow()
   {
      return _window;
   }

   public List<LineSpec> getLineSpecs(TableWindowCtrl pkCtrl)
   {
      if(pkCtrl == this)
      {
         return new ArrayList<>();
      }

      ArrayList<LineSpec> ret = new ArrayList<>();

      TableWindowSide windowSide;

      if(pkCtrl.getMidX() < getMidX())
      {
         windowSide = TableWindowSide.RIGHT;
      }
      else
      {
         windowSide = TableWindowSide.LEFT;
      }

      List<FkSpec> fkSpecs = _columnListCtrl.getFkSpecsTo(pkCtrl._tableInfo, windowSide);

      for (FkSpec fkSpec : fkSpecs)
      {
         fkSpec.setPersistentFkProps(getPersistenFkProps(fkSpec.getFkName()));
      }

      if(0 < fkSpecs.size())
      {
         PkSpec pkSpec = pkCtrl._columnListCtrl.getPkSpec(windowSide);

         if (null != pkSpec)
         {
            for (FkSpec fkSpec : fkSpecs)
            {
               ret.add(new LineSpec(pkSpec, fkSpec));
            }
         }
      }

      return ret;
   }

   private PersistentFkProps getPersistenFkProps(String fkName)
   {
      PersistentFkProps ret = _persistentFkPropsByFkName.get(fkName);
      if(null == ret)
      {
         ret = new PersistentFkProps(fkName);
         _persistentFkPropsByFkName.put(fkName, ret);
      }

      return ret;
   }

   private double getMidX()
   {
      return _window.getBoundsInParent().getMinX() + (_window.getBoundsInParent().getMaxX() - _window.getBoundsInParent().getMinX()) / 2.0;
   }

   public TableInfo getTableInfo()
   {
      return _tableInfo;
   }
}
