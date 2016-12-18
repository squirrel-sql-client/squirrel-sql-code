package org.squirrelsql.session.graph;

public enum Operator
{
   EQUALS("="),
   LIKE("LIKE"),
   IN("IN"),
   IS_NULL("IS NULL", false),
   LESS("<"),
   LESS_EQUAL("<="),
   GREATER(">"),
   GREATER_EQUAL(">="),
   NOT_EQUAL("<>"),
   NOT_IN("NOT IN"),
   IS_NOT_NULL("IS NOT NULL", false);

   private String _opName;
   private final boolean _requiresValue;

   Operator(String opName)
   {
      this(opName, true);
   }


   Operator(String opName, boolean requiresValue)
   {
      _opName = opName;
      _requiresValue = requiresValue;
   }

   @Override
   public String toString()
   {
      return _opName;
   }

   public boolean requiresValue()
   {
      return _requiresValue;
   }
}
