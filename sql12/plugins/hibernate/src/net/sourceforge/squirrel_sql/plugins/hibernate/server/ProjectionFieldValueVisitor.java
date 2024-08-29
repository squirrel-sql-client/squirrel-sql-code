package net.sourceforge.squirrel_sql.plugins.hibernate.server;

@FunctionalInterface
public interface ProjectionFieldValueVisitor
{
   /**
    * @return true if visiting is to continue
    */
   boolean visit(ProjectionFieldValue projectionFieldValue);
}
