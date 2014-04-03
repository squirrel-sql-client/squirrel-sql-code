package org.squirrelsql.aliases.dbconnector;

public interface ConnectFailureDecisionListener
{
   enum Decision
   {
      EDIT_ALIAS_REQUESTED,
      RELOGIN_REQUESTED
   }

   void decided(Decision decision);

}
