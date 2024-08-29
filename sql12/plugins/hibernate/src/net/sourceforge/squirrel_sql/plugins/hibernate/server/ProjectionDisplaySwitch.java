package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.io.Serializable;

public class ProjectionDisplaySwitch implements Serializable
{
   private ProjectionDisplayMode projectionDisplayMode = ProjectionDisplayMode.DEFAULT_MODE;

   public ProjectionDisplayMode getProjectionDisplayMode()
   {
      return projectionDisplayMode;
   }

   public void setProjectionDisplayMode(ProjectionDisplayMode projectionDisplayMode)
   {
      this.projectionDisplayMode = projectionDisplayMode;
   }
}
