package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import java.util.List;

@FunctionalInterface
public interface GutterItemsProviderListener
{
   void updateGutterItems(List<GutterItem> gutterItems);
}
