package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.hibernate.server.ProjectionDisplayMode;

public class ProjectionDisplayModeRenderer
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ProjectionDisplayModeRenderer.class);


   public static String render(ProjectionDisplayMode value)
   {
      switch( value )
      {
         case DEFAULT_MODE:
            return s_stringMgr.getString("ProjectionDisplayModeRenderer.projection.display.default");
         case JSON_MODE:
            return s_stringMgr.getString("ProjectionDisplayModeRenderer.projection.display.json");
         case XML_MODE:
            return s_stringMgr.getString("ProjectionDisplayModeRenderer.projection.display.xml");
         case JSON_MODE_INC_TYPES:
            return s_stringMgr.getString("ProjectionDisplayModeRenderer.projection.display.json.inc.types");
         case XML_MODE_INC_TYPES:
            return s_stringMgr.getString("ProjectionDisplayModeRenderer.projection.display.xml.inc.types");
         default:
            throw new IllegalStateException("Unknown ProjectionDisplayMode" + value.name());
      }
   }
}
