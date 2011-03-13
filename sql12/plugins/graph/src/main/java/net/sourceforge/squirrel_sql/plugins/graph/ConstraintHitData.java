package net.sourceforge.squirrel_sql.plugins.graph;

public class ConstraintHitData
{
   private ConstraintView _constraintView;
   private ConstraintHit _constraintHit;

   public ConstraintHitData(ConstraintView constraintView, ConstraintHit constraintHit)
   {
      _constraintView = constraintView;
      _constraintHit = constraintHit;
   }

   public ConstraintView getConstraintView()
   {
      return _constraintView;
   }

   public ConstraintHit getConstraintHit()
   {
      return _constraintHit;
   }
}
