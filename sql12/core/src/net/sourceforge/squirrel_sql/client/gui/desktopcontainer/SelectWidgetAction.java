package net.sourceforge.squirrel_sql.client.gui.desktopcontainer;

import net.sourceforge.squirrel_sql.fw.gui.action.BaseAction;

import java.awt.event.ActionEvent;

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

      if (null != myTitle && myTitle.length() > MAX_TITLE_LENGTH)
      {
         myTitle = myTitle.substring(0, MAX_TITLE_LENGTH) + "...";
      }

      return myTitle;
   }

}
