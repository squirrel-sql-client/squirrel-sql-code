package net.sourceforge.squirrel_sql.plugins.hibernate.server;

@FunctionalInterface
public interface TypedValueVisitor
{
   /**
    * @return true if visiting is to continue
    */
   boolean visit(TypedValue typedValue);
}
