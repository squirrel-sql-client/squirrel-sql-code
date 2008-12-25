package net.sourceforge.squirrel_sql.client.gui.desktopcontainer;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import net.sourceforge.squirrel_sql.fw.gui.action.BaseAction;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.IWidget;

import javax.swing.*;

public class SelectWidgetAction extends BaseAction
{
   private static final int MAX_TITLE_LENGTH = 50;

   private IWidget _widget;

   public SelectWidgetAction(IWidget widget)
	{
      super(getTitle(widget.getTitle()));
      _widget = widget;
	}

	public void actionPerformed(ActionEvent evt)
	{
      new SelectWidgetCommand(_widget).execute();
	}


   private static String getTitle(String myTitle)
   {

      if (myTitle.length() > MAX_TITLE_LENGTH)
      {
         myTitle = myTitle.substring(0, MAX_TITLE_LENGTH) + "...";
      }

      return myTitle;
   }

}
