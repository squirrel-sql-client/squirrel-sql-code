package net.sourceforge.squirrel_sql.plugins.graph;

public interface ConstraintViewListener
{
   void foldingPointMoved(ConstraintView source);
   void removeNonDbConstraint(ConstraintView constraintView);
}
