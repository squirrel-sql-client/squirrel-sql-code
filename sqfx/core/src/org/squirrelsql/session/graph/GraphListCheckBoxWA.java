package org.squirrelsql.session.graph;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import org.squirrelsql.Props;

/**
 * This workaround exists because inside a list in a Window the CheckBox did not size right.
 */
public class GraphListCheckBoxWA extends BorderPane
{

   private Props _props = new Props(getClass());
   private Image _imageChecked = _props.getImage("checked.png");
   private Image _imageUnchecked = _props.getImage("unchecked.png");

   private boolean _selected = false;
   private GraphListCheckBoxSelectionListener _graphListCheckBoxSelectionListener;
   private ColumnConfigurationListener _columnConfigurationListener;


   public GraphListCheckBoxWA(GraphListCheckBoxSelectionListener graphListCheckBoxSelectionListener, ColumnConfigurationListener columnConfigurationListener)
   {
      _graphListCheckBoxSelectionListener = graphListCheckBoxSelectionListener;
      _columnConfigurationListener = columnConfigurationListener;
      addEventHandler(MouseEvent.MOUSE_CLICKED, e -> onClicked());
      updateGraphics();


   }

   private void onClicked()
   {
      _selected = !_selected;
      updateGraphics();
      _graphListCheckBoxSelectionListener.selectionChanged(_selected);
      _columnConfigurationListener.requestLayout();
   }

   private void updateGraphics()
   {
      if(_selected)
      {
         setCenter(new ImageView(_imageChecked));
      }
      else
      {
         setCenter(new ImageView(_imageUnchecked));
      }
   }

   public boolean isSelected()
   {
      return _selected;
   }

   public void setSelected(boolean selected)
   {
      _selected = selected;
      updateGraphics();
   }
}
