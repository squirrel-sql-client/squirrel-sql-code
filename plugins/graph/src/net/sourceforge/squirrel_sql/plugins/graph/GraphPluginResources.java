package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;

public final class GraphPluginResources extends PluginResources
{
   GraphPluginResources(IPlugin plugin)
   {
      super(GraphPluginResources.class.getName(), plugin);
   }

   public interface IKeys
   {
      String PRINT_IMAGE = "Print";
      String SAVE_IMAGES_TO_FILE = "SaveImagesToFile";
   }
}
