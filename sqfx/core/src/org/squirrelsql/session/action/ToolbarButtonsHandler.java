package org.squirrelsql.session.action;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public class ToolbarButtonsHandler
{
   private final EventHandler<ActionEvent> _eventHandler;
   private final ImageView _icon;
   private Tooltip _tooltip;
   private boolean _selected;
   private boolean _disable;

   private Set<ButtonBase> _buttons = Collections.newSetFromMap(new WeakHashMap<>());

   public ToolbarButtonsHandler(EventHandler<ActionEvent> eventHandler, ImageView icon, Tooltip tooltip)
   {
      _eventHandler = eventHandler;
      _icon = icon;
      _tooltip = tooltip;
   }

   public void add(ButtonBase toolbarButton)
   {
      toolbarButton.setGraphic(new ImageView(_icon.getImage()));
      toolbarButton.setTooltip(_tooltip);
      toolbarButton.setOnAction(_eventHandler);
      _buttons.add(toolbarButton);

      updateUI(toolbarButton);
   }

   private void updateUI(ButtonBase toolbarButton)
   {
      if(toolbarButton instanceof ToggleButton)
      {
         ((ToggleButton)toolbarButton).setSelected(_selected);
      }

      toolbarButton.setDisable(_disable);
   }

   public void setSelected(boolean selected)
   {
      _selected = selected;
      _buttons.forEach(this::updateUI);
   }

   public void setDisable(boolean disable)
   {
      _disable = disable;
      _buttons.forEach(this::updateUI);
   }
}
