package org.squirrelsql.session.graph;

public enum Operator
{
   EQUALS("="),
   LIKE("LIKE"),
   IN("IN"),
   IS_NULL("IS NULL"),
   LESS("<"),
   LESS_EQUAL("<="),
   GREATER(">"),
   GREATER_EQUAL(">="),
   NOT_EQUAL("<>"),
   NOT_IN("NOT IN"),
   IS_NOT_NULL("IS NOT NULL");

   private String _opName;

   Operator(String opName)
   {
      _opName = opName;
   }

   @Override
   public String toString()
   {
      return _opName;
   }
}
