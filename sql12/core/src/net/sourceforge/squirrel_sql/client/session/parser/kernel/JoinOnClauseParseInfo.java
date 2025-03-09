package net.sourceforge.squirrel_sql.client.session.parser.kernel;

public interface JoinOnClauseParseInfo
{
   int getStatBegin();

   int getStatEnd();

   String getTableOrAliasName();
}
