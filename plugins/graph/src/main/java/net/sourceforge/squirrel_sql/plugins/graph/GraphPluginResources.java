package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;

public final class GraphPluginResources extends PluginResources
{
   public GraphPluginResources(IPlugin plugin)
   {
      super(GraphPluginResources.class.getName(), plugin);
   }

   public interface IKeys
   {
      String PRINT_IMAGE = "Print";
      String SAVE_IMAGES_TO_FILE = "SaveImagesToFile";
      String FILTER = "Filter";
      String FILTER_CHECKED = "FilterChecked";

      String AGG_FCT = "AggFct";
      String AGG_FCT_CHECKED = "AggFctChecked";
      String AGG_SUM = "AggSum";
      String AGG_MAX = "AggMax";
      String AGG_MIN = "AggMin";
      String AGG_COUNT = "AggCount";

      String JOIN_INNER = "Equal";
      String JOIN_LEFT = "EqualLeft";
      String JOIN_RIGHT = "EqualRight";
      String JOIN_NONE = "EqualCrossed";


      String TO_WINDOW = "ToWindow";

      String SHOW_MENU = "Showmenu";

      String HIDE_DOCK = "Hidedock";
      String HIDE_DOCK_SEL = "HidedockSel";
      String NEW_AND_FOLDER = "newandfolder";
      String NEW_OR_FOLDER = "neworfolder";
      String DELETE_FOLDER = "deletefolder";
   }
}
