package org.squirrelsql.aliases.dbconnector;

import org.squirrelsql.session.DbConnectorResult;

public interface DbConnectorListener
{
   void finished(DbConnectorResult dbConnectorResult);
}
