package net.sourceforge.squirrel_sql.client.gui;

import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.ISessionWidget;

/**
 * Created by gerd on 06.03.16.
 */
public interface ISessionWidgetIndexHandler
{
   ISessionWidget getPreviousWidget(ISessionWidget sessionWindow);

   ISessionWidget getNextWidget(ISessionWidget sessionWindow);

   int size();

   ISessionWidget getLastSessionWidget();

   ISessionWidget getFirstSessionWidget();
}
